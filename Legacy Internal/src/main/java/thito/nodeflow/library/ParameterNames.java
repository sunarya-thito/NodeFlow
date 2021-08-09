/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package thito.nodeflow.library;

import org.objectweb.asm.Type;
import org.objectweb.asm.*;

import java.io.*;
import java.lang.reflect.*;
import java.net.*;
import java.util.ArrayList;
import java.util.*;

import static java.lang.reflect.Modifier.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static java.util.Objects.*;
import static java.util.stream.Collectors.*;

/**
 * ParameterNames contains utility methods for fetching the parameter names for a method or
 * constructor compiled into the class file using the {@code -parameters} flag to {@code javac} or
 * from the debug symbols in the class file.
 */
public final class ParameterNames
{
    private ParameterNames() { }

    /**
     * Tries to get meaningful parameter names of the method or constructor.  If the parameters were
     * compiled into the class file using the {@code -parameters} flag to {@code javac}, the parameters
     * are fetch via Java reflection.  Otherwise, the parameters are read from the debug symbols in the class
     * file.  If the method does not have debug symbols, empty result is returned.
     *
     * @param executable the method or constructor
     * @return the parameter names
     */
    public static Optional<List<String>> tryGetParameterNames(Executable executable)
    {
        requireNonNull(executable, "executable is null");

        if (executable.getParameterCount() == 0) {
            return Optional.of(emptyList());
        }

        List<Parameter> parameters = asList(executable.getParameters());
        if (parameters.stream().allMatch(Parameter::isNamePresent)) {
            return Optional.of(getParameterNames(parameters));
        }

        return getParameterNamesFromBytecode(executable);
    }

    /**
     * Gets the parameter names of the method or constructor.  If the parameters were compiled into
     * the class file using the {@code -parameters} flag to {@code javac}, the parameters are fetch
     * via Java reflection.  Otherwise, the parameters are read from the debug symbols in the class
     * file.  If the method does not have debug symbols, the default parameter names {@code argN}
     * are returned.
     *
     * @param executable the method or constructor
     * @return the parameter names
     */
    public static List<String> getParameterNames(Executable executable)
    {
        requireNonNull(executable, "executable is null");

        if (executable.getParameterCount() == 0) {
            return emptyList();
        }

        List<Parameter> parameters = asList(executable.getParameters());

        // if parameters were not compiled in the class file, try to get the parameters from the bytecode
        if (!parameters.stream().allMatch(Parameter::isNamePresent)) {
            Optional<List<String>> parameterNames = getParameterNamesFromBytecode(executable);
            if (parameterNames.isPresent()) {
                return parameterNames.get();
            }
        }

        return getParameterNames(parameters);
    }

    private static List<String> getParameterNames(List<Parameter> parameters)
    {
        // Parameter.getName() returns argN if the parameter names were not compiled into the class file
        return unmodifiableList(parameters.stream()
                .map(Parameter::getName)
                .collect(toList()));
    }

    /**
     * Gets the parameter names of the method or constructor from the debug symbols in the bytecode.
     * Note that, abstract methods and interface methods do not have bytecode, and therefore, there have
     * no debug symbols to read.
     *
     * @param executable the method or constructor
     * @return the parameter names if debug symbols were present
     */
    public static Optional<List<String>> getParameterNamesFromBytecode(Executable executable)
    {
        requireNonNull(executable, "executable is null");

        if (executable.getParameterCount() == 0) {
            return Optional.of(emptyList());
        }

        byte[] byteCode = loadBytecode(executable.getDeclaringClass());
        if (byteCode == null) {
            return Optional.empty();
        }

        try {
            ClassReader reader = new ClassReader(byteCode);
            ParameterNameClassVisitor visitor = new ParameterNameClassVisitor(executable);
            reader.accept(visitor, ClassReader.SKIP_FRAMES);

            return visitor.getParameterNames();
        }
        catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    private static byte[] loadBytecode(Class<?> declaringClass)
    {
        try {
            ClassLoader classLoader = declaringClass.getClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
                if (classLoader == null) {
                    // we can't get any class loaders
                    return null;
                }
            }
            URL classFile = classLoader.getResource(declaringClass.getName().replace('.', '/') + ".class");
            if (classFile != null) {
                ByteArrayOutputStream out = new ByteArrayOutputStream(4086);
                try (InputStream inputStream = new BufferedInputStream(classFile.openStream())) {
                    byte[] buffer = new byte[1024];
                    for (int length = inputStream.read(buffer); length >= 0; length = inputStream.read(buffer)) {
                        out.write(buffer, 0, length);
                    }
                }
                return out.toByteArray();
            }
        }
        catch (IOException e) {
        }
        return null;
    }

    private static class ParameterNameClassVisitor
            extends ClassVisitor
    {
        private final String methodName;
        private final List<Type> parameterTypes;
        private final ParameterNameMethodVisitor methodVisitor;
        private boolean methodFound;

        ParameterNameClassVisitor(Executable executable)
        {
            super(Opcodes.ASM9);

            methodName = executable instanceof Constructor ? "<init>" : executable.getName();

            parameterTypes = Arrays.stream(executable.getParameterTypes())
                    .map(Type::getType)
                    .collect(toList());

            this.methodVisitor = new ParameterNameMethodVisitor(isStatic(executable.getModifiers()), parameterTypes);
        }

        Optional<List<String>> getParameterNames()
        {
            return methodVisitor.getResult();
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions)
        {
            if (methodFound) {
                return null;
            }

            if (!name.equals(methodName) || !parameterTypes.equals(asList(Type.getArgumentTypes(desc)))) {
                return null;
            }

            methodFound = true;

            return methodVisitor;
        }
    }

    private static class ParameterNameMethodVisitor
            extends MethodVisitor
    {
        private final boolean isStatic;
        private final List<Type> parameterTypes;
        private final String[] slotNames;

        ParameterNameMethodVisitor(boolean isStatic, List<Type> parameterTypes)
        {
            super(Opcodes.ASM5);
            this.isStatic = isStatic;
            this.parameterTypes = parameterTypes;

            int parameterSlots = 0;
            if (!isStatic) {
                parameterSlots++;
            }
            for (Type parameterType : parameterTypes) {
                parameterSlots += parameterType.getSize();
            }
            slotNames = new String[parameterSlots];
        }

        @Override
        public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index)
        {
            if (index < slotNames.length) {
                slotNames[index] = name;
            }
        }

        Optional<List<String>> getResult()
        {
            int slot = 0;
            if (!isStatic) {
                slot++;
            }

            List<String> result = new ArrayList<>();
            for (Type parameterType : parameterTypes) {
                String slotName = slotNames[slot];
                if (slotName == null) {
                    // symbols not present or only partially defined
                    return Optional.empty();
                }
                result.add(slotName);

                slot += parameterType.getSize();
            }

            return Optional.of(unmodifiableList(result));
        }
    }
}
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.optimizer.ClassCompiler;

import java.io.File;
import java.io.FileOutputStream;

public class JSCompilerTest {
    public static void main(String[] args) throws Throwable {
        String javaScript = """
                let nothing = 10;
                nothing = 15;
                var something = true;
                something = "lmao";
                console.log("test");
                function test() {
                    something = false;
                }
                var set = function(testificate) {
                    nothing = testificate;
                };
                """;
        CompilerEnvirons compilerEnv = new CompilerEnvirons();
        compilerEnv.setLanguageVersion(Context.VERSION_ES6);
        ClassCompiler compiler = new ClassCompiler( compilerEnv );
        Object[] compiled = compiler.compileToClassFiles( javaScript, null, 1, "javascript.Test" );
        for( int j = 0; j != compiled.length; j += 2 ) {
            String className = (String)compiled[j];
            byte[] bytes = (byte[])compiled[(j + 1)];
            File file = new File( "testjs/"+className.replace( '.', '/' ) + ".class" );
            file.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream( file )) {
                fos.write( bytes );
            }
        }
;//        AstRoot root = JavaScriptReader.read(javaScript, "test.js");
//        System.out.println(root.getFirstChild().getNext().getClass());
    }
}

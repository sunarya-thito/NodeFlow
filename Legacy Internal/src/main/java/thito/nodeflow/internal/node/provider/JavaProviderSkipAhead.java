package thito.nodeflow.internal.node.provider;

import java.lang.reflect.*;
import java.util.*;

public class JavaProviderSkipAhead {
    public static void main(String[] args) {
        List<JavaProviderSkipAhead> skipAheads = collectSkipAhead(new HashSet<>(), new ArrayList<>(), A.class);
        skipAheads.forEach(x -> {
            x.members.forEach(y -> {
                System.out.println(x+" : "+y.getName());
            });
        });
    }
    public static class A {
        public B b;
        public C getC() {
            return null;
        }
    }

    public static class B {
        public void na(String m) {

        }
    }

    public static class C {
        public D getD() {
            return null;
        }
    }

    public static class D {
        public B b;
    }
    public static List<JavaProviderSkipAhead> collectSkipAhead(Set<Class<?>> checked, List<Member> tree, Class<?> clazz) {
        List<JavaProviderSkipAhead> list = new ArrayList<>();
        if (!checked.add(clazz)) return list;
        System.out.println("Scanning: "+clazz.getName()+" > "+tree);
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isPublic(field.getModifiers())) {
                List<Member> subTree = new ArrayList<>(tree);
                subTree.add(field);
                List<JavaProviderSkipAhead> continuity = collectSkipAhead(checked, subTree, field.getType());
                if (!continuity.isEmpty()) {
                    list.addAll(continuity);
                } else {
                    JavaProviderSkipAhead skipper = new JavaProviderSkipAhead(subTree);
                    list.add(skipper);
                }
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers())) {
                List<Member> subTree = new ArrayList<>(tree);
                subTree.add(method);
                if ((method.getParameterCount() == 0 && void.class != method.getReturnType()) &&
                        (!tree.isEmpty() || Modifier.isStatic(method.getModifiers()))) {
                    List<JavaProviderSkipAhead> continuity = collectSkipAhead(checked, subTree, method.getReturnType());
                    if (!continuity.isEmpty()) {
                        list.addAll(continuity);
                    } else {
                        JavaProviderSkipAhead skipper = new JavaProviderSkipAhead(subTree);
                        list.add(skipper);
                    }
                } else {
                    JavaProviderSkipAhead skipper = new JavaProviderSkipAhead(subTree);
                    list.add(skipper);
                }
            }
        }
        return list;
    }
    private List<Member> members;
    public JavaProviderSkipAhead(List<Member> members) {
        this.members = members;
    }
}

import thito.nodeflow.plugin.base.Name;

public class NF_Node {
    @Name("Test")
    public static MethodA MethodA(Object param1, Object param2) {
        MethodA methodA = new MethodA();
        return methodA;
    }

    public static class MethodA {

    }

}

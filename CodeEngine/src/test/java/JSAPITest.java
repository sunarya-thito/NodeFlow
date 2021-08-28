import thito.nodeflow.javascript.*;

public class JSAPITest {
    public static void main(String[] args) {
        Context context = new Context();
        Scope scope = new Scope() {
            @Override
            public void body() {
                let("test", function("nothing").body(new Scope() {
                    @Override
                    public void body() {
                        let("nothing", ref(90));
                    }
                }));
            }
        };
        System.out.println(scope.toSourceCode());
    }
}

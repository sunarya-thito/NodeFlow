package thito.nodeflow.javascript;

public class Function {
    private String[] args;

    public Reference body(Scope body) {
        return new Reference() {
            @Override
            public String toSourceCode() {
                return null;
            }
        };
    }

    public class FuncRef extends Reference {

        private Scope body;

        public FuncRef(Scope body) {
            this.body = body;
        }

        public Scope getBody() {
            return body;
        }

        public String[] getArgs() {
            return args;
        }

        @Override
        public String toSourceCode() {
            return null;
        }
    }
}

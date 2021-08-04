package thito.nodeflow.internal.node.provider;

import javafx.beans.property.*;
import javafx.scene.paint.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.NodeParameter;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.type.*;

import java.util.*;
import java.util.function.*;

public class ExecutionNodeParameter implements NodeParameterFactory {
    private String name;
    private LinkMode input, output;

    private boolean insertable;

    private NodeLinkShape inputShape, outputShape;

    public ExecutionNodeParameter(String name, LinkMode input, LinkMode output) {
        this.name = name;
        this.input = input;
        this.output = output;
    }

    public void setInputShape(NodeLinkShape inputShape) {
        this.inputShape = inputShape;
    }

    public void setOutputShape(NodeLinkShape outputShape) {
        this.outputShape = outputShape;
    }

    public NodeLinkShape getInputShape() {
        return inputShape;
    }

    public NodeLinkShape getOutputShape() {
        return outputShape;
    }

    public ExecutionNodeParameter insertable() {
        insertable = true;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    @Override
    public LinkMode getInputMode() {
        return input;
    }

    @Override
    public LinkMode getOutputMode() {
        return output;
    }

    @Override
    public NodeParameter createParameter(Node node, ComponentParameterState state) {
        return new ExecutionParameter(state, name, node, ModuleManagerImpl.getInstance().getEditorForContentType(ExecutionNodeParameter.class));
    }

    public class ExecutionParameterType implements NodeParameterType {
        private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);
        private Node node;

        public ExecutionParameterType(Node node) {
            this.node = node;
        }

        public Node getNode() {
            return node;
        }

        @Override
        public String name() {
            return "DEFAULT";
        }

        @Override
        public ObjectProperty<Color> inputColorProperty() {
            return color;
        }

        @Override
        public ObjectProperty<Color> outputColorProperty() {
            return color;
        }

        @Override
        public boolean isAssignableFrom(NodeParameterType other) {
            if (other instanceof ExecutionParameterType || other instanceof JavaParameterType.UnknownParameterType) {
                return true;
            }
            return false;
        }
    }

    public class ExecutionParameter extends NodeParameterImpl implements Predicate<thito.nodejfx.NodeParameter> {
        public ExecutionParameter(ComponentParameterState state, String name, Node node, ParameterEditor editor) {
            super(state, name, node, editor, new ExecutionParameterType(node), null);
            setInputMode(input);
            setOutputMode(output);
        }

        @Override
        public boolean test(thito.nodejfx.NodeParameter nodeParameter) {
            NodeImpl sourceRoot = getRoot(nodeParameter);
            NodeImpl targetRoot = getRoot(parameter);
            if (sourceRoot != null && targetRoot != null && sourceRoot != targetRoot) {
                // Execution must be from the same root
                // either its same implementation root or same node root
                return false;
            }
            return true;
        }

        private boolean hasNextInfiniteLoop(Set<thito.nodejfx.NodeParameter> used, thito.nodejfx.NodeParameter parameter) {
            if (!used.add(parameter)) return true;
            for (thito.nodejfx.NodeParameter output : parameter.getUnmodifiableOutputLinks()) {
                if (output == this.parameter) return true;
                if (hasNextInfiniteLoop(used, output)) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasPreviousInfiniteLoop(Set<thito.nodejfx.NodeParameter> used, thito.nodejfx.NodeParameter parameter) {
            if (!used.add(parameter)) return true;
            for (thito.nodejfx.NodeParameter input : parameter.getUnmodifiableInputLinks()) {
                if (input == this.parameter) return true;
                if (hasNextInfiniteLoop(used, input)) {
                    return true;
                }
            }
            return false;
        }

        private NodeImpl getRoot(thito.nodejfx.NodeParameter parameter) {
            for (thito.nodejfx.NodeParameter input : parameter.getUnmodifiableInputLinks()) {
                NodeImpl root = getRoot(input);
                if (root == null) {
                    return (NodeImpl) input.getNode().getUserData();
                }
            }
            return null;
        }

        private NodeImpl getPreviousImplementation(thito.nodejfx.NodeParameter parameter) {
            for (thito.nodejfx.NodeParameter input : parameter.getUnmodifiableInputLinks()) {
                NodeImpl node = (NodeImpl) input.getNode().getUserData();
                if (node.getState().getProvider() instanceof ImplementationNodeProvider) {
                    return node;
                }
                node = getPreviousImplementation(input);
                if (node != null) {
                    return node;
                }
            }
            return null;
        }

        @Override
        protected void updateType() {
            this.parameter.getInputType().set(type);
            this.parameter.getOutputType().set(type);
        }

        @Override
        protected void setupParameter(thito.nodejfx.NodeParameter parameter) {
            if (this.parameter != null) {
                this.parameter.getFilter().remove(this);
            }
            parameter.insertableProperty().set(insertable);
//            if (parameter instanceof SpecificParameter) {
//                ((SpecificParameter) parameter).getSubLabel().setTooltip(null);
//                Toolkit.install(parameter, ((SpecificParameter) parameter).getTooltip());
//            }
            super.setupParameter(parameter);
            parameter.getFilter().add(this);
            if (inputShape != null) {
                parameter.setInputShape(inputShape);
            }
            if (outputShape != null) {
                parameter.setOutputShape(outputShape);
            }
        }
    }
}

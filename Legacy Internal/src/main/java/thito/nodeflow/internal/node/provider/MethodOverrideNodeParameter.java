package thito.nodeflow.internal.node.provider;

import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.paint.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.NodeParameter;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.internal.node.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.*;

import java.lang.reflect.*;
import java.util.*;

public class MethodOverrideNodeParameter implements NodeParameterFactory {

    public static boolean isOverridable(int modifiers) {
        return !Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers));
    }

    public static boolean mustBeOverridden(int modifiers) {
        return Modifier.isAbstract(modifiers);
    }

    private Class<?> clazz;
    private Method method;
    private boolean input;

    public MethodOverrideNodeParameter(Class<?> clazz, Method method, boolean input) {
        this.clazz = clazz;
        this.method = method;
        this.input = input;
    }

    @Override
    public String getName() {
        return clazz.getName();
    }

    @Override
    public NodeParameter createParameter(Node node, ComponentParameterState state) {
        MethodOverrideParameter parameter = new MethodOverrideParameter(state, "Implementation", node, ModuleManagerImpl.getInstance().getEditorForContentType(ExecutionNodeParameter.class));
        GenericTypeStorage finalStorage = ((NodeImpl) node).getStorage();
        parameter.impl_getPeer().getUnmodifiableInputLinks().addListener((SetChangeListener<thito.nodejfx.NodeParameter>) change -> {
            if (change.wasAdded()) {
                NodeParameterImpl param = (NodeParameterImpl) change.getElementAdded().getUserData();
                Bindings.bindContent(finalStorage.getMap(), ((NodeImpl) param.getNode()).getStorage().getMap());
            }
            if (change.wasRemoved()) {
                NodeParameterImpl param = (NodeParameterImpl) change.getElementRemoved().getUserData();
                Bindings.unbindContent(finalStorage.getMap(), ((NodeImpl) param.getNode()).getStorage().getMap());
            }
        });
        return parameter;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    @Override
    public LinkMode getInputMode() {
        return input ? LinkMode.MULTIPLE : LinkMode.NONE;
    }

    @Override
    public LinkMode getOutputMode() {
        return input ? LinkMode.NONE : LinkMode.MULTIPLE;
    }

    public class MethodOverrideType implements NodeParameterType {
        private ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.WHITE);

        @Override
        public String name() {
            return "Implementation";
        }

        public Class<?> getType() {
            return clazz;
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
            if (other instanceof MethodOverrideType) {
                if (clazz.isAssignableFrom(((MethodOverrideType) other).getType()) || ((MethodOverrideType) other).getType().isAssignableFrom(clazz)) {
                    return true;
                }
            }
            return false;
        }
    }

    public class MethodOverrideParameter extends NodeParameterImpl {
        public MethodOverrideParameter(ComponentParameterState state, String name, Node node, ParameterEditor editor) {
            super(state, name, node, editor, new MethodOverrideType(), null);
            if (input) {
                setInputMode(LinkMode.MULTIPLE);
                setOutputMode(LinkMode.NONE);
            } else {
                setInputMode(LinkMode.NONE);
                setOutputMode(LinkMode.MULTIPLE);
            }
        }

        @Override
        protected void setupParameter(thito.nodejfx.NodeParameter parameter) {
            super.setupParameter(parameter);
            parameter.setOutputShape(NodeLinkShape.TRIANGLE_SHAPE);
            parameter.setInputShape(NodeLinkShape.TRIANGLE_SHAPE);
            if (Modifier.isAbstract(clazz.getModifiers()) || clazz.isInterface()) {
                parameter.getUnmodifiableOutputLinks().addListener((SetChangeListener<thito.nodejfx.NodeParameter>) change -> {
                    update(parameter, change.getSet());
                });
                update(parameter, parameter.getUnmodifiableOutputLinks());
            }
        }

        public Method getMethod() {
            return method;
        }

        public Class<?> getType() {
            return clazz;
        }

        private void update(thito.nodejfx.NodeParameter parameter, Set<? extends thito.nodejfx.NodeParameter> change) {
            int unimplemented = 0;
            int count = 0;
            if (!input) {
                for (Method method : clazz.getMethods()) {
                    if (mustBeOverridden(method.getModifiers())) {
                        boolean found = false;
                        for (thito.nodejfx.NodeParameter param : change) {
                            Object userdata = param.getUserData();
                            if (userdata instanceof MethodOverrideParameter) {
                                if (Objects.equals(((MethodOverrideParameter) userdata).getMethod(), method)) {
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (!found) {
                            unimplemented++;
                        }
                        count++;
                    }
                }
            }
            if (unimplemented > 0 && !input) {
                if (parameter instanceof LabelParameter) {
                    ((LabelParameter) parameter).getLabel().setTextFill(Color.RED);
                    ((LabelParameter) parameter).getLabel().setText("Implementation ("+(count - unimplemented)+"/"+count+")");
                }
            } else {
                if (parameter instanceof LabelParameter) {
                    ((LabelParameter) parameter).getLabel().setTextFill(Color.WHITE);
                    ((LabelParameter) parameter).getLabel().setText("Implementation");
                }
            }
        }
    }
}

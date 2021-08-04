package thito.nodeflow.internal.node.eventbus.command;

import javafx.beans.*;
import javafx.beans.binding.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.objectweb.asm.*;
import thito.nodeflow.api.*;
import thito.nodeflow.api.editor.node.Node;
import thito.nodeflow.api.editor.node.NodeParameter;
import thito.nodeflow.api.editor.node.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.node.*;
import thito.nodeflow.api.node.eventbus.command.*;
import thito.nodeflow.api.node.state.*;
import thito.nodeflow.api.task.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.Dialog;
import thito.nodeflow.api.ui.dialog.button.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.Toolkit;
import thito.nodeflow.internal.bundle.*;
import thito.nodeflow.internal.node.*;
import thito.nodeflow.internal.node.eventbus.command.parsers.*;
import thito.nodeflow.internal.node.provider.*;
import thito.nodeflow.internal.node.state.*;
import thito.nodeflow.internal.ui.*;
import thito.nodeflow.internal.ui.dialog.*;
import thito.nodeflow.library.ui.*;
import thito.nodejfx.*;
import thito.nodejfx.parameter.type.*;
import thito.reflectedbytecode.*;
import thito.reflectedbytecode.jvm.*;

import java.util.*;
import java.util.stream.*;

public class CommandProviderImpl extends AbstractNodeProvider implements CommandNodeProvider {
    private List<CommandVariable> variables = new ArrayList<>();
    private List<ArgumentParser> parsers = new ArrayList<>();
    private Class<?> senderType;
    public CommandProviderImpl(String id, String name, NodeProviderCategory category, Class<?> type) {
        super(id, name, category);
        this.senderType = type;
    }

    public Class<?> getSenderType() {
        return senderType;
    }

    @Override
    public CommandNodeCategoryImpl getCategory() {
        return (CommandNodeCategoryImpl) super.getCategory();
    }

    @Override
    public String getID() {
        return "nodeflow://command/"+ getCategory().getId()+"/"+senderType.getName()+"/"+parsers.stream().map(parser -> parser == null ? "null" : parser.getId()).collect(Collectors.joining(";"));
    }

    public void initializeParameters() {
        addParameter(new ExecutionNodeParameter("Execution", LinkMode.NONE, LinkMode.SINGLE));
        addParameter(new JavaNodeParameter("Executor", senderType, true, LinkMode.NONE, LinkMode.MULTIPLE));
        for (CommandVariable variable : variables) {
            addParameter(new JavaNodeParameter(variable.getDisplayName().getRawString(), variable.getType(), true, LinkMode.NONE, LinkMode.MULTIPLE));
        }
        addParameter(new ArgumentParameter(true, String.class, false, true, null));
        for (ArgumentParser parser : parsers) {
            if (parser == null) {
                addParameter(new ArgumentParameter(true, String.class, true, true, null));
            } else {
                addParameter(new ArgumentParameter(false, parser.getType(), true, true, null));
            }
        }
        ArgumentParameter parameter = new ArgumentParameter(false, String.class, false, true, "args");
        parameter.insertable = false;
        parameter.optional = true;
        addParameter(parameter);
    }

    public boolean isConstant(int arg) {
        return parsers.get(arg) == null;
    }

    public List<ArgumentParser> getParsers() {
        return parsers;
    }

    @Override
    public List<CommandVariable> getVariables() {
        return variables;
    }

    @Override
    public NodeCompileSession createCompileSession(Node node) {
        return null;
    }

    public List<String> getConstants(Node node, int arg) {
        NodeParameter parameter = node.getParameter(variables.size() + 3 + arg);
        List<String> collect;
        Object constant = parameter.getState().getConstantValue();
        if (constant instanceof List) {
            collect = ((List<?>) constant).stream().map(String::valueOf).collect(Collectors.toList());
        } else {
            collect = Collections.singletonList(String.valueOf(constant));
        }
        return collect;
    }

    @Override
    public CompileSession createNewSession() {
        return new CompileSession() {
            @Override
            protected void prepareStructure() {
                for (int i = 0; i < getNode().getParameters().size(); i++) {
                    getHandler().skipLocal(getNode().getParameter(i));
                }
                getHandler().prepareStructure(findOutputNode(getNode().getParameter(0)));
            }

            @Override
            protected void handleCompile() {
                for (int i = 0; i < variables.size(); i++) {
                    getHandler().setDirectReference(getNode().getParameter(i + 2), Code.getCode().getLocalFieldMap().getField(i + 3).get());
                }

                ILocalField command = Code.getCode().getLocalFieldMap().getField(1);
                ILocalField sender = Code.getCode().getLocalFieldMap().getField(0);
                ILocalField arguments = Code.getCode().getLocalFieldMap().getField(2);
                ILocalField ignoreError = Code.getCode().getLocalFieldMap().getField(3);
                ILocalField execute = Code.getCode().getLocalFieldMap().getField(4);

                getHandler().setDirectReference(getNode().getParameter(1), sender.get());

                for (int i = 0; i < parsers.size(); i++) {
                    ArgumentParser parser = parsers.get(i);
                    NodeParameter parameter = getNode().getParameter(variables.size() + 3 + i);
                    if (parser != null) {
                        getHandler().setDirectReference(parameter, arguments.get().arrayGet(i));
                    }
                }

                compileExecutor(sender, arguments, ignoreError, execute);
            }

            private void compileExecutor(ILocalField sender, ILocalField arguments, ILocalField ignoreError, ILocalField execute) {
                if (!sender.get().getType().getTypeName().equals(getSenderType().getTypeName())) {
                    Java.If(Java.InstanceOf(sender.get(), getSenderType())).isFalse().Then(() -> {
                        getCategory().getHandler().sendMessageToSender(sender.get(), Java.Class(String.class).staticMethod("format", String.class, Object[].class)
                                .invoke(getNode().getState().getExtras().getString("command-invalid-sender-message"), Java.NewArray(Object.class, 1).arrayInitialValues(getSenderType().getSimpleName())));
                        execute.get().write(boolean.class);
                        Code.getCode().getCodeVisitor().visitInsn(Opcodes.IRETURN);
                        Code.getCode().markReturn();
                    }).EndIf();
                }
                if (!getNode().getState().getExtras().getString("command-permission").isEmpty()) {
                    Java.If(getCategory().getHandler().hasPermission(sender.get(), getNode().getState().getExtras().getString("command-permission"))).isFalse().Then(() -> {
                        Java.If(ignoreError.get()).isFalse().Then(() -> {
                            getCategory().getHandler().sendMessageToSender(sender.get(), Java.Class(String.class).staticMethod("format", String.class, Object[].class)
                                    .invoke(getNode().getState().getExtras().getString("command-permission-message"),
                                            Java.NewArray(Object.class, 1).arrayInitialValues(getNode().getState().getExtras().getString("command-permission"))));
                        }).EndIf();
                        execute.get().write(boolean.class);
                        Code.getCode().getCodeVisitor().visitInsn(Opcodes.IRETURN);
                        Code.getCode().markReturn();
                    }).EndIf();
                }
                for (int i = 0; i < parsers.size(); i++) {
                    ArgumentParser parser = parsers.get(i);
                    if (parser != null) {
                        int finalI = i;
                        Java.Try(() -> {
                            Java.Try(() -> {
                                Java.Try(() -> {
                                    NodeParameter parameter = getNode().getParameter(variables.size() + 3 + finalI);
                                    ILocalField field = Code.getCode().getLocalFieldMap().createField(Java.Class(parser.getType()));
                                    field.set(parser.createHandler().parseArgument(Code.getCode().getLocalFieldMap().getField(2).get(),
                                            arguments.get().arrayGet(finalI)));
                                    getHandler().setDirectReference(parameter, field.get());
                                }).Catch(NullPointerException.class).Caught(error -> {
                                    Java.If(ignoreError.get()).isFalse().Then(() -> {
                                        getCategory().getHandler().sendMessageToSender(sender.get(), Java.Class(String.class).staticMethod("format", String.class, Object[].class)
                                                .invoke(getNode().getState().getExtras().getString("command-not-found-message"), Java.NewArray(Object.class, 1).arrayInitialValues(arguments.get().arrayGet(finalI))));
                                    }).EndIf();
                                    execute.get().write(boolean.class);
                                    Code.getCode().getCodeVisitor().visitInsn(Opcodes.IRETURN);
                                    Code.getCode().markReturn();
                                });
                            }).Catch(IllegalArgumentException.class).Caught(error -> {
                                Java.If(ignoreError.get()).isFalse().Then(() -> {
                                    getCategory().getHandler().sendMessageToSender(sender.get(), Java.Class(String.class).staticMethod("format", String.class, Object[].class)
                                            .invoke(getNode().getState().getExtras().getString("command-invalid-argument-message"), Java.NewArray(Object.class, 1).arrayInitialValues(arguments.get().arrayGet(finalI))));
                                }).EndIf();
                                execute.get().write(boolean.class);
                                Code.getCode().getCodeVisitor().visitInsn(Opcodes.IRETURN);
                                Code.getCode().markReturn();
                            });
                        }).Catch(UnsupportedOperationException.class).Caught(error -> {
                            Java.If(ignoreError.get()).isFalse().Then(() -> {
                                getCategory().getHandler().sendMessageToSender(sender.get(), Java.Class(String.class).staticMethod("format", String.class, Object[].class)
                                        .invoke(getNode().getState().getExtras().getString("command-invalid-sender-message"), Java.NewArray(Object.class, 1).arrayInitialValues(error.method("getMessage").invoke())));
                            }).EndIf();
                            execute.get().write(boolean.class);
                            Code.getCode().getCodeVisitor().visitInsn(Opcodes.IRETURN);
                            Code.getCode().markReturn();
                        });
                    }
                }
                Java.If(execute.get()).isTrue().Then(() -> {
                    if (isParameterUsed(getNode().getParameter(getNode().getParameters().size() - 1))) {
                        ILocalField joined = Code.getCode().getLocalFieldMap().createField(Java.Class(String.class));
                        Java.If(arguments.get().arrayLength()).isGreaterOrEqualsTo(parsers.size()).Then(() -> {
                            joined.set(Java.Class(String.class).staticMethod("join", CharSequence.class, CharSequence[].class)
                                    .invoke(" ", Java.Class(Arrays.class).staticMethod("copyOfRange", Object[].class, int.class, int.class)
                                            .invoke(arguments.get(), parsers.size(), arguments.get().arrayLength())));
                        }).Else(() -> {
                            joined.set("");
                        }).EndIf();
                        getHandler().setDirectReference(getNode().getParameter(getNode().getParameters().size() - 1), joined.get());
                    }
                    getHandler().compile(findOutputNode(getNode().getParameter(0)));
                }).EndIf();
                Code.getCode().getCodeVisitor().visitInsn(Opcodes.ICONST_1);
                Code.getCode().getCodeVisitor().visitInsn(Opcodes.IRETURN);
                Code.getCode().markReturn();
            }
        };
    }

    @Override
    public Node createComponent(NodeModule module) {
        Node node = super.createComponent(module);
        node.getState().getExtras().set("You don't have permission \"%s\" to do this!", "command-permission-message");
        node.getState().getExtras().set("Invalid command argument: %s", "command-invalid-argument-message");
        node.getState().getExtras().set("You must be a %s to be able to do this", "command-invalid-sender-message");
        node.getState().getExtras().set("Can't find %s", "command-not-found-message");
        node.getState().getExtras().set("My command description", "command-description");
        node.getState().getExtras().set("", "command-permission");
        return node;
    }

    @Override
    public Node fromState(NodeModule module, ComponentState state) {
        Node n = super.fromState(module, state);
        if (n instanceof CommandNodeImpl) {
            CommandNodeImpl node = (CommandNodeImpl) n;
            InvalidationListener invalidationListener = obs -> {
                node.getState().setProviderID(getID());
                for (NodeParameter parameter : node.getParameters()) {
                    thito.nodejfx.NodeParameter param = ((NodeParameterImpl) parameter).impl_getPeer();
                    param.getAddButton().setOnMouseClicked(event -> {
                        ContextMenu menu = new ContextMenu();
                        MenuItem itemArgument = new MenuItem("Argument");
                        itemArgument.setOnAction(e -> {
                            int index = node.getParameters().indexOf(parameter) + 1;
                            ArgumentParameter p;
                            parsers.add(index - (3 + variables.size()), null);
                            getParameters().add(index, p = new ArgumentParameter(true, String.class, true, true, null));
                            NodeParameter px;
                            node.getParameters().add(index, px = p.createParameter(node,
                                    new ComponentParameterStateImpl((StandardNodeModule) module, UUID.randomUUID(), null)));
                            ((StandardNodeModule) module).getSession().getUndoManager().storeAction(I18n.$("action-arguments-add").stringBinding(), () -> {
                                node.getParameters().remove(px);
                            }, () -> {
                                node.getParameters().add(index, px);
                            });
                            ((StandardNodeModule) module).attemptSave();
                        });
                        Menu itemInput = new Menu("Input");
                        for (ArgumentParser parser : CommandManagerImpl.getInstance().getParsers()) {
                            if (parser.getFacet() == null || parser.getFacet() == ((StandardNodeModule) module).getSession().getProject().getFacet()) {
                                MenuItem item = new MenuItem();
                                item.textProperty().bind(parser.getDisplayName().stringBinding());
                                item.setOnAction(e -> {
                                    int index = node.getParameters().indexOf(parameter) + 1;
                                    parsers.add(index - (3 + variables.size()), parser);
                                    ArgumentParameter p = new ArgumentParameter(false, parser.getType(), true, true, null);
                                    getParameters().add(index, p);
                                    NodeParameter px;
                                    node.getParameters().add(index, px = p.createParameter(node,
                                            new ComponentParameterStateImpl((StandardNodeModule) module, UUID.randomUUID(), null)));
                                    ((StandardNodeModule) module).getSession().getUndoManager().storeAction(I18n.$("action-arguments-add").stringBinding(), () -> {
                                        node.getParameters().remove(px);
                                    }, () -> {
                                        node.getParameters().add(index, px);
                                    });
                                    ((StandardNodeModule) module).attemptSave();
                                });
                                itemInput.getItems().add(item);
                            }
                        }
                        MenuItem enumItem = new MenuItem("Enum");
                        enumItem.setOnAction(e -> {
                            Task.runOnBackground("load-classes", () -> {
                                List<Class<?>> classes = ((BundleManagerImpl) NodeFlow.getApplication().getBundleManager()).collectClasses(test ->
                                        test.isEnum());
                                Task.runOnForeground("select-enum", () -> {
                                    Dialogs.openClassSelect(Toolkit.getWindow(node.impl_getPeer()), classes, result -> {
                                        EnumArgumentParser parser = new EnumArgumentParser(result);
                                        int index = node.getParameters().indexOf(parameter) + 1;
                                        parsers.add(index - (3 + variables.size()), parser);
                                        ArgumentParameter p = new ArgumentParameter(false, parser.getType(), true, true, null);
                                        getParameters().add(index, p);
                                        NodeParameter px;
                                        node.getParameters().add(index, px = p.createParameter(node,
                                                new ComponentParameterStateImpl((StandardNodeModule) module, UUID.randomUUID(), null)));
                                        ((StandardNodeModule) module).getSession().getUndoManager().storeAction(I18n.$("action-arguments-add").stringBinding(), () -> {
                                            node.getParameters().remove(px);
                                        }, () -> {
                                            node.getParameters().add(index, px);
                                        });
                                        ((StandardNodeModule) module).attemptSave();
                                    });
                                });
                            });
                        });
                        MenuItem constantItem = new MenuItem("Constant");
                        constantItem.setOnAction(e -> {
                            Task.runOnBackground("load-classes", () -> {
                                List<Class<?>> classes = ((BundleManagerImpl) NodeFlow.getApplication().getBundleManager()).collectClasses(test -> true);
                                Task.runOnForeground("select-enum", () -> {
                                    Dialogs.openClassSelect(Toolkit.getWindow(node.impl_getPeer()), classes, result -> {
                                        EnumArgumentParser parser = new EnumArgumentParser(result);
                                        int index = node.getParameters().indexOf(parameter) + 1;
                                        parsers.add(index - (3 + variables.size()), parser);
                                        ArgumentParameter p = new ArgumentParameter(false, parser.getType(), true, true, null);
                                        getParameters().add(index, p);
                                        NodeParameter px;
                                        node.getParameters().add(index, px = p.createParameter(node,
                                                new ComponentParameterStateImpl((StandardNodeModule) module, UUID.randomUUID(), null)));
                                        ((StandardNodeModule) module).getSession().getUndoManager().storeAction(I18n.$("action-arguments-add").stringBinding(), () -> {
                                            node.getParameters().remove(px);
                                        }, () -> {
                                            node.getParameters().add(index, px);
                                        });
                                        ((StandardNodeModule) module).attemptSave();
                                    });
                                });
                            });
                        });
                        itemInput.getItems().addAll(enumItem, constantItem);
                        menu.getItems().addAll(itemArgument, itemInput);
                        menu.show(param.getAddButton(), event.getScreenX(), event.getScreenY());
                    });
                }
                if (obs != null) {
                    ((ComponentStateImpl) node.getState()).attemptSave();
                }
            };
            invalidationListener.invalidated(null);
            node.getParameters().addListener(invalidationListener);
        }
        return n;
    }

    @Override
    protected NodeImpl createNode(NodeModule module, ComponentState state) {
        return new CommandNodeImpl(module, state, this);
    }

    public class CommandNodeImpl extends NodeImpl implements CommandNode, Tickable {

        public CommandNodeImpl(NodeModule module, ComponentState state, NodeProvider provider) {
            super(module, state, provider);
//            impl_getPeer().getTitleNode().getLabel().setMaxWidth(200);
            Tooltip tooltip = new Tooltip();
            tooltip.textProperty().bind(impl_getPeer().getTitleNode().getLabel().textProperty());
            impl_getPeer().getTitleNode().getLabel().setTooltip(tooltip);
            Ticker.register(this);
        }

        @Override
        public String getPermissionMessage() {
            return getState().getExtras().getString("command-permission-message");
        }

        @Override
        public String getInvalidArgumentMessage() {
            return getState().getExtras().getString("command-invalid-argument-message");
        }

        @Override
        public String getNotFoundMessage() {
            return getState().getExtras().getString("command-not-found-message");
        }

        @Override
        public String getInvalidSenderMessage() {
            return getState().getExtras().getString("command-invalid-sender-message");
        }

        @Override
        public String getPermission() {
            return getState().getExtras().getString("command-permission");
        }

        @Override
        public String getDescription() {
            return getState().getExtras().getString("command-description");
        }

        int tick;
        int index = 0;
        @Override
        public void tick() {
            if (tick % (16 * 5) == 0) {
                List<CommandEditor> list = new ArrayList<>(getParameters()).stream()
                        .filter(x -> x.impl_getPeer() instanceof CommandEditor && ((CommandEditor) x.impl_getPeer()).hasValue()).map(x -> (CommandEditor) x.impl_getPeer()).collect(Collectors.toList());
                if (!list.isEmpty() && list.get(0) instanceof ArgumentEditor) {
                    ArgumentEditor command = (ArgumentEditor) list.get(0);
                    impl_getPeer().titleProperty().set("On Command \n/"+command.arguments.get(index % command.arguments.size())+" "+
                            list.subList(1, list.size()).stream().map(String::valueOf).collect(Collectors.joining(" ")));
                }
                index++;
            }
            tick++;
        }

        public Node next() {
            return findOutputNode(getParameter(0));
        }

        @Override
        public List<ArgumentParser> getParsers() {
            return parsers;
        }

    }

    public class ArgumentParameter implements NodeParameterFactory {

        private boolean argument;
        private Class<?> outputType;
        private boolean removable;
        private boolean hasOutput;
        private boolean optional;
        private String defaultArgName;
        private boolean insertable = true;

        public ArgumentParameter(boolean argument, Class<?> outputType, boolean removable, boolean hasOutput, String defaultArgName) {
            this.argument = argument;
            this.outputType = outputType;
            this.removable = removable;
            this.hasOutput = hasOutput;
            this.defaultArgName = defaultArgName;
        }

        @Override
        public String getName() {
            return "Argument";
        }

        @Override
        public NodeParameter createParameter(Node node, ComponentParameterState state) {
            NodeParameterImpl parameter = new NodeParameterImpl(state, argument ? "Argument" : "Input", node, x -> argument ? new ArgumentEditor(this, x) : new InputEditor(this, x), new CompoundType().scan(outputType), outputType);
            parameter.setInputMode(LinkMode.NONE);
            parameter.setOutputMode(hasOutput ? LinkMode.MULTIPLE : LinkMode.NONE);
            if (removable) {
                parameter.impl_getPeer().removableProperty().set(true);
            }
            return parameter;
        }

        @Override
        public Class<?> getType() {
            return outputType;
        }

        @Override
        public LinkMode getInputMode() {
            return LinkMode.NONE;
        }

        @Override
        public LinkMode getOutputMode() {
            return LinkMode.MULTIPLE;
        }
    }

    public interface CommandEditor {
        boolean hasValue();
    }

    public class InputEditor extends thito.nodejfx.NodeParameter implements CommandEditor {

        private StringProperty property = new SimpleStringProperty();
        private boolean optional;
        public InputEditor(ArgumentParameter px, NodeParameter param) {
            this.optional = px.optional;
            Class<?> typeClass = px.outputType;
            setOutputShape(NodeLinkShape.CIRCLE_SHAPE);
            getOutputType().set(new CompoundType().scan(typeClass));
            getAllowOutput().set(true);
            getMultipleOutputAssigner().set(true);
            insertableProperty().set(px.insertable);
            HBox box = new HBox();
            Label label = new Label();
            label.textProperty().bind(property);
            label.getStyleClass().add("argument-editor-item");
            box.getChildren().add(label);
            if (param.getState().getConstantValue() != null) {
                property.set(String.valueOf(param.getState().getConstantValue()));
            } else {
                property.set(px.defaultArgName == null ? typeClass.getSimpleName() : px.defaultArgName);
                param.getState().setConstantValue(property.getValue());
            }
            property.addListener((obs, old, val) -> {
                param.getState().setConstantValue(val);
                ((ComponentParameterStateImpl) param.getState()).attemptSave();
            });
            box.getStyleClass().add("argument-editor-box");
            Label type = new Label(typeClass.getSimpleName());
            type.getStyleClass().add("argument-type-name");
            HBox container = new HBox(box);
            container.getChildren().add(type);
            container.getStyleClass().add("argument-container");
            getContainer().getChildren().add(container);
            box.setOnMouseClicked(event -> {
                FormContent.StringForm form = FormContent.StringForm.create(I18n.$("argument-name"), property.get(), false);
                DialogContent content = FormContent.createContent(I18n.$("edit-argument"), Pos.CENTER, new FormContent.Form[] {
                        form
                });
                TextDialogButton done = DialogButton.createTextButton(0, 0, I18n.$("done"), null, click -> {
                    property.set(form.getAnswer());
                    click.close();
                });
                TextDialogButton cancel = DialogButton.createTextButton(1, 0, I18n.$("button-cancel"), null, click -> {
                    click.close();
                });
                Dialog dialog = UIManagerImpl.getInstance().getDialogManager().createDialog(content, 0, cancel, done);
                dialog.open(Toolkit.getWindow(this));
            });
        }

        @Override
        public String toString() {
            return "<"+property.get()+">";
        }

        @Override
        public boolean hasValue() {
            return property.get() != null && (!optional || !outputLinks().isEmpty());
        }

        @Override
        public void removeParameter() {
            parsers.remove(getNode().getParameters().indexOf(this) - (3 + getVariables().size()));
            super.removeParameter();
        }
    }

    public class ArgumentEditor extends thito.nodejfx.NodeParameter implements CommandEditor {
        private ObservableList<String> arguments = FXCollections.observableArrayList();
        public ArgumentEditor(ArgumentParameter px, NodeParameter param) {
            boolean hasOutput = px.hasOutput;
            arguments.addListener((InvalidationListener) obs -> {
                ((NodeImpl) param.getNode()).impl_getPeer().titleProperty().set("On Command \n/"+param.getNode().getParameters().stream()
                        .filter(x -> x.impl_getPeer() instanceof CommandEditor && ((CommandEditor) x.impl_getPeer()).hasValue()).map(x -> x.impl_getPeer().toString()).collect(Collectors.joining(" ")));
            });
            setOutputShape(NodeLinkShape.CIRCLE_SHAPE);
            getOutputType().set(new CompoundType().scan(String.class));
            getAllowOutput().set(true);
            getMultipleOutputAssigner().set(true);
            insertableProperty().set(true);
            HBox box = new HBox();
            Label label = new Label();
            label.textProperty().bind(Bindings.createStringBinding(() -> arguments.isEmpty() ? "[?]" : arguments.size() == 1 ? arguments.get(0) : "<" + String.join("|", arguments) + ">", arguments));
            label.getStyleClass().add("argument-editor-item");
            box.getChildren().add(label);
            if (param.getState().getConstantValue() instanceof List) {
                arguments.addAll(((List<?>) param.getState().getConstantValue()).stream().map(String::valueOf).collect(Collectors.toList()));
            } else if (param.getState().getConstantValue() instanceof String) {
                arguments.add((String) param.getState().getConstantValue());
            }
            arguments.addListener((InvalidationListener) obs -> {
                param.getState().setConstantValue(arguments);
                ((ComponentParameterStateImpl) param.getState()).attemptSave();
            });
            box.getStyleClass().add("argument-editor-box");
            Label type = new Label("String");
            type.getStyleClass().add("argument-type-name");
            HBox container = new HBox(box);
            if (hasOutput) {
                container.getChildren().add(type);
            } else {
                type.setText("Command");
                container.getChildren().add(0, type);
            }
            container.getStyleClass().add("argument-container");
            getContainer().getChildren().add(container);
            box.setOnMouseClicked(event -> {
                FormContent.StringListForm form = FormContent.StringListForm.create(I18n.$("argument-aliases"), arguments, false);
                DialogContent content = FormContent.createContent(I18n.$("edit-argument"), Pos.CENTER, new FormContent.Form[] {
                       form
                });
                TextDialogButton done = DialogButton.createTextButton(0, 0, I18n.$("done"), null, click -> {
                    arguments.setAll(form.getAnswer());
                    click.close();
                });
                TextDialogButton cancel = DialogButton.createTextButton(1, 0, I18n.$("button-cancel"), null, click -> {
                    click.close();
                });
                Dialog dialog = UIManagerImpl.getInstance().getDialogManager().createDialog(content, 0, cancel, done);
                dialog.open(Toolkit.getWindow(this));
            });
        }

        @Override
        public String toString() {
            return arguments.size() == 1 ? arguments.get(0) : "<" + String.join("|", arguments) + ">";
        }

        @Override
        public boolean hasValue() {
            return !arguments.isEmpty();
        }

        @Override
        public void removeParameter() {
            parsers.remove(getNode().getParameters().indexOf(this) - (3 + getVariables().size()));
            super.removeParameter();
        }
    }

}

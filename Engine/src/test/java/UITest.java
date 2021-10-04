import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import thito.nodeflow.engine.node.*;
import thito.nodeflow.engine.node.handler.NodeCanvasHandler;
import thito.nodeflow.engine.node.handler.NodeHandler;
import thito.nodeflow.engine.node.handler.NodeParameterHandler;
import thito.nodeflow.engine.node.skin.*;
import thito.nodeflow.engine.node.state.HandlerState;

import java.util.Random;

public class UITest extends Application {

    public static void main(String[] args) {
    }

    private static Color safeColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(155) + 100, random.nextInt(155) + 100, random.nextInt(155) + 100);
    }

    public void start(Stage primaryStage) throws Exception {
        NodeCanvas canvas = new NodeCanvas(new NodeCanvasHandler() {

            @Override
            public NodeHandler createHandler(thito.nodeflow.engine.node.Node node, HandlerState state) {
                return new NodeHandler() {
                    int count = 0;
                    @Override
                    public NodeParameterHandler createParameterHandler(NodeParameter parameter, HandlerState state) {
                        return new NodeParameterHandler() {

                            private final Color color1 = safeColor();
                            private final Color color = safeColor();
                            private final StringProperty displayName = new SimpleStringProperty("PARAM "+count++);

                            @Override
                            public StringProperty displayNameProperty() {
                                return displayName;
                            }

                            @Override
                            public NodePort getInputPort() {
                                return new NodePort(true, color, PortShape.TRIANGLE);
                            }

                            @Override
                            public NodePort getOutputPort() {
                                return new NodePort(true, color1, PortShape.RHOMBUS);
                            }

                            @Override
                            public NodeParameter getParameter() {
                                return parameter;
                            }

                            @Override
                            public NodeParameterSkin createSkin() {
                                return new NodeParameterSkin(parameter);
                            }

                            @Override
                            public boolean acceptPairing(NodeParameter parameter, boolean asInput) {
                                return true;
                            }

                            @Override
                            public HandlerState saveState() {
                                return null;
                            }
                        };
                    }
                    @Override
                    public StringProperty displayNameProperty() {
                        return new SimpleStringProperty("Test");
                    }

                    @Override
                    public ObjectProperty<Image> iconProperty() {
                        return new SimpleObjectProperty<>();
                    }

                    @Override
                    public thito.nodeflow.engine.node.Node getNode() {
                        return node;
                    }

                    @Override
                    public NodeSkin createSkin() {
                        return new NodeSkin(node);
                    }

                    @Override
                    public HandlerState saveState() {
                        return null;
                    }
                };
            }

            @Override
            public NodeGroupSkin createGroupSkin(NodeGroup group) {
                return new NodeGroupSkin(group);
            }

            @Override
            public NodeCanvasSkin createCanvasSkin(NodeCanvas canvas) {
                return new NodeCanvasSkin(canvas);
            }
        });
        for (int i = 0; i < 5; i++) {
            thito.nodeflow.engine.node.Node node = new thito.nodeflow.engine.node.Node();
            canvas.getNodeList().add(node);
            for (int j = 0; j <= i; j++) {
                node.getParameters().add(new NodeParameter());
            }
        }
        BorderPane pane = new BorderPane(canvas.getSkin());
        Scene scene = new Scene(pane);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F5) {
                bulkRefresh(pane);
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
//        Field field = ScenicView.class.getDeclaredField("debug");
//        field.setAccessible(true);
//        field.set(null, false);
//        ScenicView.show(scene);
    }

    static void bulkRefresh(javafx.scene.Node node) {
        if (node instanceof Parent) {
            for (javafx.scene.Node child : ((Parent) node).getChildrenUnmodifiable()) {
                bulkRefresh(child);
            }
        }
        if (node instanceof Skin) {
            ((Skin) node).getStylesheets().clear();
            Platform.runLater(() -> {
                System.out.println("reload: "+node);
                ((Skin) node).getStylesheets().add("file:///C:/Users/Thito/IdeaProjects/NodeFlow%20Software/Engine/src/main/resources/"+node.getClass().getName().replace('.', '/')+".css");
            });
        }
    }
}

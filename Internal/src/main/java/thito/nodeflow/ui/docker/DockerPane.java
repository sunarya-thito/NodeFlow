package thito.nodeflow.ui.docker;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import thito.nodeflow.project.ProjectContext;
import thito.nodeflow.task.TaskThread;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class DockerPane extends StackPane {
    /*
    ----------------------------------
    |                                |
    |                                |
    ----------------------------------
    |       |                |       |
    |       |                |       |
    |       |                |       |
    |       |                |       |
    |       |                |       |
    ----------------------------------
    |                                |
    |                                |
    ----------------------------------
     */

    private DockerContext context;

    private ObjectProperty<Runnable> checkAutoClose = new SimpleObjectProperty<>();
    private SplitPane lHSplit, hSplit, vSplit;
    private DockerTabPane leftTabs, centerTabs, rightTabs, bottomTabs;

    private void setupDropHint(DockerTabPane dockerTabPane) {
        dockerTabPane.headerVisibleProperty().bind(Bindings.isNotEmpty(dockerTabPane.getTabList()).or(context.dragProperty().isNotNull()));
        dockerTabPane.checkAutoCloseProperty().bind(checkAutoClose);
    }

    private void refreshDividerPositions() {
        TaskThread.UI().schedule(() -> {
            lHSplit.setDividerPositions(1/4d);
            hSplit.setDividerPositions(4d/5d);
            vSplit.setDividerPositions(4d/5d);
        });
    }

    public DockerPane(DockerContext context) {
        this.context = context;

        hSplit = new SplitPane();
        hSplit.setDividerPositions(4d/5d);
        hSplit.getItems().addListener((InvalidationListener) obs -> {
            refreshDividerPositions();
        });
        lHSplit = new SplitPane();
        lHSplit.setDividerPositions(1/4d);
        lHSplit.getItems().addListener((InvalidationListener) obs -> {
            refreshDividerPositions();
        });

        leftTabs = new DockerTabPane(context);
        leftTabs.headerPositionProperty().set(DockerPosition.LEFT);
        setupDropHint(leftTabs);

        centerTabs = new DockerTabPane(context);
        centerTabs.headerPositionProperty().set(DockerPosition.TOP);
        setupDropHint(centerTabs);

        rightTabs = new DockerTabPane(context);
        rightTabs.headerPositionProperty().set(DockerPosition.RIGHT);
        setupDropHint(rightTabs);

        vSplit = new SplitPane();
        vSplit.getItems().addListener((InvalidationListener) obs -> {
            refreshDividerPositions();
        });
        vSplit.setOrientation(Orientation.VERTICAL);
        vSplit.getItems().add(hSplit);
        bottomTabs = new DockerTabPane(context);
        bottomTabs.headerPositionProperty().set(DockerPosition.BOTTOM);
        setupDropHint(bottomTabs);

        SplitPane.setResizableWithParent(leftTabs, false);
        SplitPane.setResizableWithParent(rightTabs, false);
        SplitPane.setResizableWithParent(bottomTabs, false);

        lHSplit.getItems().addListener((InvalidationListener) obs -> {
            if (lHSplit.getItems().isEmpty()) {
                hSplit.getItems().remove(lHSplit);
            } else if (!hSplit.getItems().contains(lHSplit)) {
                hSplit.getItems().add(0, lHSplit);
            }
            TaskThread.UI().schedule(() -> {
                hSplit.layout();
            });
        });
        hSplit.getItems().addListener((InvalidationListener) obs -> {
            if (hSplit.getItems().isEmpty()) {
                vSplit.getItems().remove(hSplit);
            } else if (!vSplit.getItems().contains(hSplit)) {
                vSplit.getItems().add(0, hSplit);
            }
            TaskThread.UI().schedule(() -> {
                vSplit.layout();
            });
        });
        leftTabs.headerVisibleProperty().addListener((obs, old, val) -> {
            if (val) {
                lHSplit.getItems().add(0, leftTabs);
            } else {
                lHSplit.getItems().remove(leftTabs);
            }
        });
        centerTabs.headerVisibleProperty().addListener((obs, old, val) -> {
            if (val) {
                lHSplit.getItems().add(centerTabs);
            } else {
                lHSplit.getItems().remove(centerTabs);
            }
        });
        rightTabs.headerVisibleProperty().addListener((obs, old, val) -> {
            if (val) {
                hSplit.getItems().add(rightTabs);
            } else {
                hSplit.getItems().remove(rightTabs);
            }
        });
        bottomTabs.headerVisibleProperty().addListener((obs, old, val) -> {
            if (val) {
                vSplit.getItems().add(bottomTabs);
            } else {
                vSplit.getItems().remove(bottomTabs);
            }
        });
        getChildren().addAll(vSplit);
    }

    public DockerTabPane getTabs(DockerPosition dockerPosition) {
        return switch (dockerPosition) {
            case LEFT -> leftTabs;
            case RIGHT -> rightTabs;
            case TOP -> centerTabs;
            case BOTTOM -> bottomTabs;
        };
    }

    public DockerPaneState saveState() {
        DockerPaneState state = new DockerPaneState();
        saveSectionState(state.center = new DockerPaneState.TabPaneState(), centerTabs);
        saveSectionState(state.left = new DockerPaneState.TabPaneState(), leftTabs);
        saveSectionState(state.right = new DockerPaneState.TabPaneState(), rightTabs);
        saveSectionState(state.bottom = new DockerPaneState.TabPaneState(), bottomTabs);
        state.dividerPositions = new double[3];
        double[] hSplitDividerPositions = hSplit.getDividerPositions();
        double[] lHSplitDividerPositions = lHSplit.getDividerPositions();
        double[] vSplitDividerPositions = vSplit.getDividerPositions();
        state.dividerPositions[0] = hSplitDividerPositions.length == 0 ? -1 : hSplitDividerPositions[0];
        state.dividerPositions[1] = lHSplitDividerPositions.length == 0 ? -1 : lHSplitDividerPositions[0];
        state.dividerPositions[2] = vSplitDividerPositions.length == 0 ? -1 : vSplitDividerPositions[0];
        return state;
    }

    public ObjectProperty<Runnable> checkAutoCloseProperty() {
        return checkAutoClose;
    }

    public void loadState(ProjectContext projectContext, DockerPaneState state) {
        loadSectionState(projectContext, state.center, centerTabs);
        loadSectionState(projectContext, state.left, leftTabs);
        loadSectionState(projectContext, state.right, rightTabs);
        loadSectionState(projectContext, state.bottom, bottomTabs);
        double hSplitDividerPosition = state.dividerPositions[0];
        double lHSplitDividerPosition = state.dividerPositions[1];
        double vSplitDividerPosition = state.dividerPositions[2];
        hSplit.setDividerPositions(hSplitDividerPosition);
        lHSplit.setDividerPositions(lHSplitDividerPosition);
        vSplit.setDividerPositions(vSplitDividerPosition);
    }

    private void loadSectionState(ProjectContext projectContext, DockerPaneState.TabPaneState state, DockerTabPane tabPane) {
        for (DockerPaneState.TabState tabState : state.tabs) {
            DockerTab dockerTab = loadTabState(projectContext, tabState);
            if (dockerTab != null) {
                tabPane.getTabList().add(dockerTab);
            }
        }
        if (state.selectedIndex >= 0 && state.selectedIndex < tabPane.getTabList().size()) {
            DockerTab dockerTab = tabPane.getTabList().get(state.selectedIndex);
            tabPane.focusedTabProperty().set(dockerTab);
        }
    }

    private DockerTab loadTabState(ProjectContext projectContext, DockerPaneState.TabState tabState) {
        DockNode dockNode = DockerManager.getManager().createDockNode(projectContext, tabState.componentState);
        if (dockNode != null) {
            return new DockerTab(getContext(), dockNode);
        }
        return null;
    }

    private void saveSectionState(DockerPaneState.TabPaneState state, DockerTabPane tabPane) {
        DockerTab[] tabs = tabPane.getTabList()
                .stream().filter(x -> x.contentProperty().get() instanceof DockNode).toArray(DockerTab[]::new);
        DockerTab focused = tabPane.focusedTabProperty().get();
        state.selectedIndex = -1;
        if (focused != null) {
            for (int i = 0; i < tabs.length; i++) if (tabs[i] == focused) {
                state.selectedIndex = i;
                break;
            }
        }
        state.tabs = Arrays.stream(tabs).map(this::saveTabState).toArray(DockerPaneState.TabState[]::new);
    }

    private DockerPaneState.TabState saveTabState(DockerTab tab) {
        Node node = tab.contentProperty().get();
        if (node instanceof DockNode) {
            DockerPaneState.TabState state = new DockerPaneState.TabState();
            state.componentState = ((DockNode) node).createState();
            return state;
        }
        return null;
    }

    public DockerTabPane getRightTabs() {
        return rightTabs;
    }

    public DockerTabPane getCenterTabs() {
        return centerTabs;
    }

    public DockerTabPane getLeftTabs() {
        return leftTabs;
    }

    public DockerTabPane getBottomTabs() {
        return bottomTabs;
    }

    public DockerContext getContext() {
        return context;
    }

    public class HintPane extends Pane {
        private BooleanProperty draggingOver = new SimpleBooleanProperty();
    }

}

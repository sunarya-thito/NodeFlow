package thito.nodeflow.internal.ui.dialog.content.form;

import com.jfoenix.controls.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.locale.*;
import thito.nodejfx.internal.*;

import java.util.*;
import java.util.stream.*;

public class PropertiesFormImpl extends AbstractFormImpl<List<String[]>> {
    private ObservableList<String[]> value = FXCollections.observableArrayList();
    private I18nItem[] columns;
    public PropertiesFormImpl(I18nItem question, I18nItem[] columns, List<String[]> initialValue, boolean optional) {
        super(question, initialValue, optional);
        this.columns = columns;
        value.addAll(initialValue);
    }

    private BooleanProperty disable = new SimpleBooleanProperty();
    @Override
    public BooleanProperty impl_disableProperty() {
        return disable;
    }

    @Override
    public Node createFieldPeer() {
        Node node = new Peer().borderPane;
        node.disableProperty().bind(disable);
        return node;
    }

    public class Peer {
        private BorderPane borderPane = new BorderPane();
        private GridPane gridPane = new GridPane();
        private ObservableList<Row> rows = FXCollections.observableArrayList();

        private String[] create() {
            String[] val = new String[columns.length];
            Arrays.fill(val, "");
            return val;
        }

        public Peer() {
            borderPane.setCenter(gridPane);
            CrossButton addButton = new CrossButton();
            FlowPane px = new FlowPane(addButton);
            px.getStyleClass().add("form-table-buttons");
            px.setOnMouseClicked(event -> {
                value.add(create());
            });
            borderPane.setBottom(px);
            borderPane.getStyleClass().add("form-table-root");
            gridPane.getStyleClass().add("form-table");
            double width = 90d / columns.length;
            for (int i = 0; i < columns.length; i++) {
                Label label = new Label();
                label.getStyleClass().add("form-table-column");
                label.textProperty().bind(columns[i].stringBinding());
                gridPane.add(label, i, 0);
                ColumnConstraints constraints = new ColumnConstraints();
                constraints.setPercentWidth(width);
                gridPane.getColumnConstraints().add(constraints);
            }

            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(10);
            gridPane.getColumnConstraints().add(constraints);

            rows.addAll(value.stream().map(Row::new).collect(Collectors.toList()));
            value.addListener(new ListChangeListener<String[]>() {
                @Override
                public void onChanged(Change<? extends String[]> change) {
                    impl_answerProperty().set(new ArrayList<>(value));
                    synchronized (rows) {
                        while (change.next()) {
                            if (change.wasPermutated()) {
                                List<Row> r = rows.subList(change.getFrom(), change.getTo());
                                int index = change.getFrom();
                                for (Row row : r) {
                                    row.remove(index);
                                    index++;
                                }
                                r.clear();
                                List<? extends String[]> r2 = change.getList().subList(change.getFrom(), change.getTo());
                                index = change.getFrom();
                                for (String[] rx : r2) {
                                    Row row = new Row(rx);
                                    row.add(index);
                                    rows.add(index, row);
                                    index++;
                                }
                            } else {
                                if (change.wasRemoved()) {
                                    List<Row> r = rows.subList(change.getFrom(), change.getFrom() + change.getRemovedSize());
                                    int index = change.getFrom();
                                    for (Row row : r) {
                                        row.remove(index);
                                        index++;
                                    }
                                    r.clear();
                                }
                                if (change.wasAdded()) {
                                    List<? extends String[]> r2 = change.getAddedSubList();
                                    int index = change.getFrom();
                                    for (String[] rx : r2) {
                                        Row row = new Row(rx);
                                        row.add(index);
                                        rows.add(index, row);
                                        index++;
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }

        public class Row {
            private JFXTextField[] components;
            private FlowPane fp = new FlowPane();
            private CrossButton removeButton = new CrossButton().diagonal();
            public Row(String[] value) {
                fp.getChildren().add(removeButton);
                fp.getStyleClass().add("form-table-row-buttons");
                fp.setOnMouseClicked(event -> {
                    remove(-1);
                });
                components = new JFXTextField[value.length];
                for (int i = 0; i < value.length; i++) {
                    components[i] = new JFXTextField(value[i]);
                    int finalI = i;
                    components[i].textProperty().addListener((obs, old, val) -> {
                        value[finalI] = val;
                        impl_answerProperty().set(new ArrayList<>(PropertiesFormImpl.this.value));
                    });
                    components[i].getStyleClass().add("form-table-row");
                }
            }

            public void add(int index) {
                for (int i = 0; i < components.length; i++) {
                    gridPane.add(components[i], i, index + 1);
                }
                gridPane.add(fp, components.length, index + 1);
            }

            public void remove(int index) {
                for (int i = 0; i < components.length; i++) {
                    gridPane.getChildren().remove(components[i]);
                }
                gridPane.getChildren().remove(fp);
            }
        }
    }

}

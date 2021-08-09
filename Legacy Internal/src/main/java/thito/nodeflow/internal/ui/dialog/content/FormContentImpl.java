package thito.nodeflow.internal.ui.dialog.content;

import javafx.beans.*;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import thito.nodeflow.api.locale.*;
import thito.nodeflow.api.ui.*;
import thito.nodeflow.api.ui.dialog.*;
import thito.nodeflow.api.ui.dialog.content.*;
import thito.nodeflow.internal.*;
import thito.nodeflow.internal.ui.dialog.content.form.*;
import thito.nodeflow.library.ui.layout.*;

import java.util.*;

public class FormContentImpl implements FormContent {

    private List<Form<?>> forms = new ArrayList<>();
    private I18nItem header;
    private Pos alignment;

    public FormContentImpl(I18nItem header, Pos alignment, Form<?>[] forms) {
        this.header = header;
        this.alignment = alignment;
        this.forms.addAll(Arrays.asList(forms));
    }

    @Override
    public Pane impl_createPeer(OpenedDialog dialog) {
        return new FormContentPeer();
    }

    @Override
    public Form<?>[] getForms() {
        return forms.toArray(new Form[0]);
    }

    @Override
    public I18nItem getHeader() {
        return header;
    }

    @Override
    public void setHeader(I18nItem title) {
        header = title;
    }

    @Override
    public Pos getHeaderAlignment() {
        return alignment;
    }

    @Override
    public void setHeaderAlignment(Pos pos) {
        alignment = pos;
    }

    public class FormPeer extends UIComponent {

        @Component("question")
        private final ObjectProperty<Label> question = new SimpleObjectProperty<>();

        @Component("viewport")
        private final ObjectProperty<StackPane> viewport = new SimpleObjectProperty<>();

        @Component("error")
        private final ObjectProperty<Label> error = new SimpleObjectProperty<>();

        private final ObjectProperty<Validator> active = new SimpleObjectProperty<>();

        private Form<?> form;
        private FormContentPeer peer;

        public FormPeer(Form<?> form, FormContentPeer peer) {
            this.form = form;
            this.peer = peer;
            setLayout(Layout.loadLayout("FormElementUI"));
        }

        @Override
        protected void onLayoutReady() {
            error.get().setMinHeight(Region.USE_PREF_SIZE);
            error.get().setMaxHeight(Region.USE_PREF_SIZE);
            error.get().managedProperty().bind(active.isNotNull());
            error.get().visibleProperty().bind(error.get().managedProperty());
            active.addListener((obs, old, val) -> {
                if (val != null) {
                    error.get().textProperty().bind(val.getMessage().stringBinding());
                }
            });
            question.get().textProperty().bind(form.getQuestion().stringBinding());
            Node node;
            viewport.get().getChildren().setAll(node = ((AbstractFormImpl) form).createFieldPeer());
            if (form instanceof StringForm) {
                ((ObservableList<Validator>) ((StringForm) form).getValidators()).addListener((InvalidationListener) observable -> {
                    for (Validator validator : ((StringForm) form).getValidators()) {
                        if (!validator.validate(node)) {
                            active.set(validator);
                            return;
                        }
                    }
                    active.set(null);
                });
            }
            form.impl_answerProperty().addListener(obs -> {
                if (form instanceof StringForm) {
                    for (Validator validator : ((StringForm) form).getValidators()) {
                        if (!validator.validate(node)) {
                            active.set(validator);
                            return;
                        }
                    }
                }
                active.set(null);
            });
        }

    }

    public class FormContentPeer extends UIComponent {

        @Component("header")
        private final ObjectProperty<Label> formHeader = new SimpleObjectProperty<>();
        @Component("formList")
        private final ObjectProperty<VBox> formList = new SimpleObjectProperty<>();

        public FormContentPeer() {
            setLayout(Layout.loadLayout("FormContentDialogUI"));
        }

        @Override
        protected void onLayoutReady() {
            formHeader.get().textProperty().bind(getHeader().stringBinding());
            formHeader.get().alignmentProperty().set(Toolkit.posToPos(getHeaderAlignment()));
            for (Form<?> form : forms) {
                formList.get().getChildren().add(new FormPeer(form, this));
            }
        }
    }
}

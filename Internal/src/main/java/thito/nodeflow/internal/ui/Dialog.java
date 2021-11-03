package thito.nodeflow.internal.ui;

import thito.nodeflow.internal.annotation.UIThread;
import thito.nodeflow.internal.language.I18n;
import thito.nodeflow.internal.task.TaskThread;

public class Dialog {

    public static Dialog create() {
        return new Dialog();
    }

    protected I18n title;
    protected I18n message;
    protected String icon;
    protected DialogButton[] buttons;
    protected Runnable fallback;

    public Dialog title(I18n title) {
        this.title = title;
        return this;
    }

    public Dialog message(I18n message) {
        this.message = message;
        return this;
    }

    public Dialog icon(String icon) {
        this.icon = icon;
        return this;
    }

    public Dialog info() {
        return icon("theme:Icons/Info.png");
    }

    public Dialog infoDanger() {
        return icon("theme:Icons/InfoDanger.png");
    }

    public Dialog infoWarning() {
        return icon("theme:Icons/InfoWarning.png");
    }

    public Dialog questionDanger() {
        return icon("theme:Icons/QuestionDanger.png");
    }

    public Dialog questionWarning() {
        return icon("theme:Icons/QuestionWarning.png");
    }

    public Dialog question() {
        return icon("theme:Icons/Question.png");
    }

    public Dialog fallback(Runnable r) {
        fallback = r;
        return this;
    }

    public Dialog buttons(DialogButton...buttons) {
        this.buttons = buttons;
        return this;
    }

    @UIThread
    public Handler show() {
        return new Handler();
    }

    public class Handler extends DialogWindow {
        private DialogSkin skin;
        public Handler() {
            if (title != null) {
                titleProperty().bind(title);
            }
            skin = new DialogSkin(Dialog.this, this);
            contentProperty().set(skin);
            show();
        }

        protected void dispose() {
            getStage().close();
        }

        @Override
        public void close() {
            super.close();
            if (fallback != null) {
                fallback.run();
            }
        }
    }

}

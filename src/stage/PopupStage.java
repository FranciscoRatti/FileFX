package stage;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

public class PopupStage extends Stage<Map.Entry<String, PopupStage.OPTION>> {
    private Label message;

    private Button close;
    private Button done;
    private TextField input;

    public enum TYPE {
        INFO,
        INPUT,
        ERROR,
        WARNING,
    }

    public enum OPTION {
        CLOSE,
        CANCEL,
        DONE,
    }

    private OPTION option;

    public PopupStage(TYPE type) {
        super(type == TYPE.INFO ? "Info" : type == TYPE.WARNING ? "Advertencia" : type == TYPE.ERROR ? "Error" : "Input");

        this.message = new Label();
        this.message.setId("Popup_message");
        setTitleId("Popup_title_" + type.toString().toLowerCase());

        VBox pane = new VBox(message);
        pane.setId("Popup_pane");

        HBox buttonsPane = new HBox();
        buttonsPane.setId("Popup_buttons_pane");

        switch (type) {
        case INPUT -> {
            input = new TextField();
            input.setId("Popup_input");
            input.setOnKeyPressed(e -> {if (e.getCode() == KeyCode.ENTER) {
                option = OPTION.DONE;
                close();
            }});

            close = new Button("Cancelar");
            close.setId("Popup_cancel");
            close.setOnAction(e -> {
                option = OPTION.CANCEL;
                close();
            });

            done = new Button("Aceptar");
            done.setId("Popup_done");
            done.setOnAction(e -> {
                option = OPTION.DONE;
                close();
            });

            input.requestFocus();
            buttonsPane.getChildren().addAll(done, close);
            pane.getChildren().addAll(input, buttonsPane);
        }
        case WARNING -> {
            close = new Button("Cancelar");
            close.setId("Popup_cancel");
            close.setOnAction(e -> {
                option = OPTION.CANCEL;
                close();
            });

            done = new Button("Aceptar");
            done.setId("Popup_done");
            done.setOnAction(e -> {
                option = OPTION.DONE;
                close();
            });

            close.requestFocus();
            buttonsPane.getChildren().addAll(done, close);
            pane.getChildren().addAll(buttonsPane);
        }
        default -> {
            close = new Button("Cerrar");
            close.setId("Popup_close");
            close.setOnAction(e -> {
                close();
            });

            close.requestFocus();
            pane.getChildren().add(close);
        }
        }

        close.setOnAction(e -> {
            close();
        });

        getChildren().add(pane);
    }

    public void afterShow() {
        if (input != null) {
            input.setText("");
        }
        option = OPTION.CANCEL;
    }
    public void afterClose() {
        var waitFuture = getCompletable();
        if (waitFuture != null && !waitFuture.isDone()) {
            waitFuture.complete(Map.entry(input == null ? "" : input.getText(), option));
        }

        if (input != null) {
            input.setText("");
        }
    }
    public void setMessage(String message) {
        this.message.setText(message);
    }

    public TextField getTextField() {return input;}
    public String getText() {
        return input.getText();
    }
    public OPTION getOption() {
        return option;
    }
}
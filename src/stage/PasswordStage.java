package stage;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.concurrent.CompletableFuture;

import static main.Lib.permissionsStage;

public class PasswordStage extends Stage {
    private final Label command;
    private final PasswordField password;
    private boolean done;

    public PasswordStage() {
        super("Contraseña");
        done = false;

        Label message = new Label("Ingrese la contraseña para ejecutar");
        message.setWrapText(true);
        message.setId("Password_message");

        command = new Label();
        command.setWrapText(true);
        command.setId("Password_command");

        password = new PasswordField();
        password.setId("Password_password");
        password.setOnKeyPressed(e -> {if (e.getCode() == KeyCode.ENTER) {
            done = true;
            close();
        }});

        Button accept = new Button("Aceptar");
        accept.setId("Password_button");
        accept.setOnAction(e -> {
            done = true;
            close();
        });

        Button cancel = new Button("Cancelar");
        cancel.setId("Password_button");
        cancel.setOnAction(e -> close());

        HBox buttonsPane = new HBox(accept, cancel);
        buttonsPane.setId("Password_buttons_pane");

        VBox pane = new VBox(message, command, password, buttonsPane);
        pane.setId("Password_pane");
        getChildren().add(pane);
    }

    public void afterShow() {
        if (permissionsStage.isShowing()) permissionsStage.changeTraversable(false);

        done = false;
        password.requestFocus();
        password.setText("");
    }

    public void afterClose() {
        if (permissionsStage.isShowing()) permissionsStage.changeTraversable(true);

        CompletableFuture<String> waitFuture = getCompletable();
        if (waitFuture != null && !waitFuture.isDone()) {
            if (done) waitFuture.complete(password.getText());
            else waitFuture.cancel(false);
        }

        done = false;
    }

    public void setCommand(String text) {command.setText(text);}
}
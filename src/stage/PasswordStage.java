package stage;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static main.Lib.THEME_PATH;

public class PasswordStage extends Stage {
    public final Label command;
    public final PasswordField password;
    private boolean reset;

    public PasswordStage() {
        setTitle("Contraseña");
        setAlwaysOnTop(true);
        setResizable(false);
        reset = true;

        Label message = new Label("Ingrese la contraseña para ejecutar");
        message.setWrapText(true);
        message.setId("Password_message");

        command = new Label();
        command.setWrapText(true);
        command.setId("Password_command");

        password = new PasswordField();
        password.setId("Password_password");
        password.setOnKeyPressed(e -> {if (e.getCode() == KeyCode.ENTER) close();});

        Button accept = new Button("Aceptar");
        accept.setId("Password_button");
        accept.setOnAction(e -> {
            reset = false;
            close();
        });

        Button cancel = new Button("Cancelar");
        cancel.setId("Password_button");
        cancel.setOnAction(e -> {
            reset = true;
            close();
        });

        HBox buttonsPane = new HBox(accept, cancel);
        buttonsPane.setId("Password_buttons_pane");

        VBox pane = new VBox(message, command, password, buttonsPane);
        pane.setId("Password_pane");

        Scene scene = new Scene(pane);
        scene.setOnKeyPressed(e -> {if (e.getCode() == KeyCode.ESCAPE) close();});
        scene.getStylesheets().add("file://"+THEME_PATH);
        setScene(scene);

        setOnCloseRequest(e -> {if (reset) password.setText("");});
        setOnShown(e -> {
            reset = true;
            password.requestFocus();
            password.setText("");
        });
    }
}
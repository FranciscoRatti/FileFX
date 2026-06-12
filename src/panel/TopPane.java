package panel;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import node.TopButton;

import java.io.File;

import main.Main;
import main.Lib;
import static panel.MainPane.*;
import static main.Main.*;
import static main.Lib.*;

public class TopPane extends HBox {
    private static TextField search;

    public TopPane() {
        super(10);
        setId("top_pane");

        update();
    }

    public void update() {
        printInfo("Actualizando panel superior");
        printInfo("Actualizando path a '"+Lib.BLUE+Main.path+Lib.RESET+"'");

        getChildren().clear();

        Button back = new TopButton("back", "Deshacer", e -> {
            back();
        });
        Button forward = new TopButton("forward", "Rehacer", e -> {
            forward();
        });
        Button parent = new TopButton("parent", "Ir arriba", e -> {
            parent();
        });

        search = new TextField(Main.path);
        search.setId("top_text_field");
        search.setPrefColumnCount(200);
        search.setOnKeyPressed(e -> {
            KeyCode key = e.getCode();

            if (key.equals(KeyCode.ENTER)) {
                String text = search.getText();
                if (!new File(text).exists()) {
                    printError("El archivo o directorio "+text+" no existe", null);
                    showAlert(new Alert(Alert.AlertType.ERROR), "El archivo o directorio "+text+" no existe", "ERROR");
                } else {
                    printInfo("Actualizando path a '"+Lib.BLUE+Main.path+Lib.RESET+"'");
                    path = text;
                    deselectAll();

                    updateCenter();
                    updateTop();
                    updateRight();
                }
            }
        });

        Button reload = new TopButton("reload", "Recargar", e -> {
            updateCenter();
            updateTop();
            updateRight();
        });

        getChildren().addAll(back, forward, parent, search, reload);
    }

    public static void focusSearch() {
        search.requestFocus();
    }
    public static boolean isSearchFocus() {
        if (search != null) return search.isFocused();
        else return false;
    }
}
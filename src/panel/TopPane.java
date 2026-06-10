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

public class TopPane extends HBox {
    private static Button back;
    private static Button forward;
    private static Button parent;
    private static TextField search;
    private static Button reload;

    public TopPane() {
        super(10);
        setId("top_pane");
    }

    public void update() {
        Lib.printInfo("Actualizando panel superior");
        Lib.printInfo("Actualizando path a '"+Lib.BLUE+Main.path+Lib.RESET+"'");

        getChildren().clear();

        back = new TopButton("back", "Deshacer", e -> {Lib.back();});
        forward = new TopButton("forward", "Rehacer", e -> {Lib.forward();});
        parent = new TopButton("parent", "Ir arriba", e -> {Lib.parent();});

        search = new TextField(Main.path);
        search.setId("top_text_field");
        search.setPrefColumnCount(200);
        search.setOnKeyPressed(e -> {
            KeyCode key = e.getCode();

            if (key.equals(KeyCode.ENTER)) {
                String text = search.getText();
                if (!new File(text).exists()) {
                    Lib.printError("El archivo o directorio "+text+" no existe", null);
                    Lib.showAlert(new Alert(Alert.AlertType.ERROR), "El archivo o directorio "+text+" no existe", "ERROR");
                } else {
                    Lib.printInfo("Actualizando path a '"+Lib.BLUE+Main.path+Lib.RESET+"'");
                    Main.path = text;
                    MainPane.deselectAll();
                    Lib.updateAll(true, true, true, false, true);
                }
            }
        });

        reload = new TopButton("reload", "Recargar", e -> {Lib.updateAll(true, true, true, true, true);});

        getChildren().addAll(back,forward, parent, search, reload);
    }

    public static void focusSearch() {
        search.requestFocus();
    }
    public static boolean isSearchFocus() {
        if (search != null) return search.isFocused();
        else return false;
    }
}
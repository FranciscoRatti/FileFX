package panel;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import main.FileFX;
import node.TopButton;

import java.io.File;

import main.Lib;
import static panel.MainPane.*;
import static main.FileFX.*;
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

        ObservableList<Node> children = getChildren();
        children.clear();

        search = new TextField(FileFX.path);
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
                    printInfo("Actualizando path a '"+Lib.BLUE+ FileFX.path+Lib.RESET+"'");
                    path = text;
                    deselectAll();

                    updateCenter();
                    updateTop();
                    updateRight();
                }
            }
        });

        String[] buttons = config.getProperty("top_buttons").split(",");
        for (String button : buttons) {

            switch (button) {
                case "back" -> children.add(new TopButton("back", "Deshacer", e -> back()));
                case "forward" -> children.add(new TopButton("forward", "Rehacer", e -> forward()));
                case "parent" -> children.add(new TopButton("parent", "Ir arriba", e -> parent()));
                case "search" -> children.add(search);
                case "reload" -> children.add(new TopButton("reload", "Recargar", e -> updateAll()));
            }
        }
    }

    public static void focusSearch() {search.requestFocus();}
    public static boolean isSearchFocus() {
        if (search != null) return search.isFocused();
        else return false;
    }
}
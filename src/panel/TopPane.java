package panel;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import node.TopButton;

import java.io.File;
import java.util.Optional;

import static main.FileFX.*;
import static main.Lib.*;

public class TopPane extends HBox {
    private static TopButton back;
    private static TopButton forward;
    private static TopButton parent;
    private static TextField search;
    private static TopButton clean;
    private static TopButton reload;

    public TopPane() {
        super(10);
        setId("top_pane");

        back = new TopButton("back"   , "Deshacer" , e -> back());
        forward = new TopButton("forward", "Rehacer"  , e -> forward());
        parent = new TopButton("parent" , "Ir arriba", e -> parent());

        search = new TextField();
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
                    printInfo("Actualizando path a '"+BLUE+path+RESET+"'");

                    updateCenter();
                    updateTop();
                    updateRight();
                }
            }
        });

        clean = new TopButton("clean", "Limpiar papelera", e -> restoreSelected());
        clean.setOnAction(e -> {
            Optional<ButtonType> result = showAlert(new Alert(Alert.AlertType.CONFIRMATION), "Todos los archivos de papelera\nseran eliminados permanentemente", "ADVERTENCIA");
            if (result.isPresent()) {
                ButtonBar.ButtonData option = result.get().getButtonData();
                if (option.equals(ButtonBar.ButtonData.OK_DONE)) {

                    // Eliminar
                    try {
                        printExecute("Limpiando la papelera");
                        new ProcessBuilder("rm", "-Rf", TRASH+"files", TRASH+"info").start().waitFor();
                        new ProcessBuilder("mkdir", TRASH+"files", TRASH+"info").start().waitFor();
                    } catch (Exception ex) {
                        printError("Error al eliminar archivo", ex);
                    }

                    path = TRASH+"files/";

                    updateCenter();
                    updateTop();
                    updateRight();
                }
            }
        });

        reload = new TopButton("reload" , "Recargar" , e -> updateAll());

        update();
    }

    public void update() {
        printInfo("Actualizando panel superior");

        ObservableList<Node> children = getChildren();
        children.clear();


        String textButtons = config.getProperty("top_buttons");
        String[] buttons = textButtons.substring(1, textButtons.length()-1).split(",");
        for (String button : buttons) {

            switch (button) {
                case "back"    -> children.add(back);
                case "forward" -> children.add(forward);
                case "parent"  -> children.add(parent);
                case "search"  -> {
                    children.add(search);
                    search.setText(path.startsWith(TRASH+"files") ?
                            "trash"+path.substring(HOME.length()+25) :
                            path.startsWith(HOME) ?
                            "~/"+path.substring(HOME.length()+1) :
                            path);
                }
                case "clean" -> {if (path.startsWith(TRASH+"files")) children.add(clean);}
                case "reload"  -> children.add(reload);
            }
        }
    }

    public static void focusSearch() {search.requestFocus();}
    public static boolean isSearchFocus() {
        if (search != null) return search.isFocused();
        else return false;
    }
}
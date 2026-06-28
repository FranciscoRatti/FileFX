package panel;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import node.TopNode;

import java.io.File;
import java.util.Optional;

import static main.FileFX.config;
import static main.FileFX.path;
import static main.Lib.*;
import static panel.CenterPane.centerNodes;

public class TopPane extends HBox {
    private static TopNode back;
    private static TopNode forward;
    private static TopNode parent;
    private static TextField search;
    private static TopNode clean;
    private static TopNode reload;

    public TopPane() {
        super(10);
        setId("top_pane");

        back = new TopNode("back"   , "Deshacer" , e -> back());
        forward = new TopNode("forward", "Rehacer"  , e -> forward());
        parent = new TopNode("parent" , "Ir arriba", e -> parent());

        search = new TextField();
        search.setId("top_text_field");
        search.setPrefColumnCount(200);
        search.setOnKeyPressed(e -> {
            KeyCode key = e.getCode();

            if (key.equals(KeyCode.ENTER)) {
                String text = search.getText();
                if (!text.endsWith("/")) text+="/";

                if (text.startsWith("~")) text = HOME+text.substring(1);
                else if (text.startsWith("trash")) text = TRASH+"files"+text.substring(5);

                if (!new File(text).exists()) {
                    printError("El archivo o directorio "+text+" no existe", null);
                    showAlert(new Alert(Alert.AlertType.ERROR), "El archivo o directorio "+text+" no existe", "ERROR");
                } else {
                    path = text;
                    printInfo("Actualizando path a '"+BLUE+path+RESET+"'");

                    updateCenter();
                    updateTop();
                    if (!centerNodes.isEmpty()) centerNodes.getFirst().setSelected(true);
                    updateRight();
                }
            }
        });

        clean = new TopNode("clean", "Limpiar papelera", e -> restoreSelected());
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

                    updateTop();
                    updateRight();
                    updateCenter();
                }
            }
        });

        reload = new TopNode("reload" , "Recargar" , e -> updateAll());

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
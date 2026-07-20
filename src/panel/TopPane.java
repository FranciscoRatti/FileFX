package panel;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import main.Lib;
import node.CenterNode;
import node.Button;

import java.io.File;
import java.util.Optional;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.CenterPane.centerNodes;
import static panel.CenterPane.selectFirst;

public class TopPane extends HBox {
    private static Button back = null;
    private static Button forward = null;
    private static Button parent = null;
    private static TextField search = null;
    private static Button clean = null;
    private static Button reload = null;

    public TopPane() {
        setId("TopPane");

        for (String[] button : TOP_BUTTONS) {
            switch (button[0]) {
                case "back" -> back = new Button(button[1], "Deshacer", "TopNode", e -> back());
                case "forward" -> forward = new Button(button[1], "Rehacer", "TopNode", e -> forward());
                case "parent" -> parent = new Button(button[1], "Ir arriba", "TopNode", e -> parent());
                case "search" -> {
                    search = new TextField();
                    search.setId("Top_search");
                    search.setPrefColumnCount(200);
                    search.setOnKeyPressed(e -> {
                        KeyCode key = e.getCode();

                        if (key.equals(KeyCode.ENTER)) {
                            String text = search.getText();
                            if (!text.endsWith("/")) text+="/";

                            if (text.startsWith("~")) text = HOME+text.substring(1);
                            else if (text.startsWith("trash")) text = Lib.TRASH+"files"+text.substring(5);

                            if (!new File(text).exists()) {
                                printError("El archivo o directorio "+text+" no existe", null);
                            } else {
                                path = text;
                                printInfo("Actualizando path a '"+BLUE+path+RESET+"'");

                                updateTop();
                                updateCenter();
                                if (!centerNodes.isEmpty()) {
                                    CenterNode first = centerNodes.getFirst();
                                    first.setSelected(true);
                                    first.requestFocus();
                                }
                                updateRight();
                            }
                        }
                    });
                }
                case "clean" -> {
                    clean = new Button(button[1], "Limpiar papelera", "TopNode", e -> restoreSelected());
                    clean.setOnAction(e -> {
                        Optional<ButtonType> result = showAlert(new Alert(Alert.AlertType.CONFIRMATION), "Todos los archivos de papelera\nseran eliminados permanentemente", "ADVERTENCIA");
                        if (result.isPresent()) {
                            ButtonBar.ButtonData option = result.get().getButtonData();
                            if (option.equals(ButtonBar.ButtonData.OK_DONE)) {

                                // Eliminar
                                try {
                                    printExecute("Limpiando la papelera");
                                    new ProcessBuilder("rm", "-Rf", Lib.TRASH+"files", Lib.TRASH+"info").start().waitFor();
                                    new ProcessBuilder("mkdir", Lib.TRASH+"files", Lib.TRASH+"info").start().waitFor();
                                } catch (Exception ex) {
                                    printError("Error al eliminar archivo", ex);
                                }

                                path = Lib.TRASH+"files/";

                                updateTop();
                                updateCenter();
                                selectFirst();
                                updateRight();
                            }
                        }
                    });
                }
                case "reload" -> reload = new Button(button[1], "Recargar", "TopNode", e -> updateAll());
            }
        }

        update();
    }

    public void update() {
        printInfo("Actualizando panel superior");

        ObservableList<Node> children = getChildren();
        children.clear();

        for (String[] button : TOP_BUTTONS) {
            switch (button[0]) {
                case "back"    -> children.add(back);
                case "forward" -> children.add(forward);
                case "parent"  -> children.add(parent);
                case "search"  -> {
                    children.add(search);
                    search.setText(
                            path.startsWith(Lib.TRASH+"files") ? "trash"+path.substring(HOME.length()+25) :
                            path.startsWith(HOME) ? "~"+path.substring(HOME.length()) :
                            path);
                }
                case "clean" -> {if (path.startsWith(Lib.TRASH+"files")) children.add(clean);}
                case "reload"  -> children.add(reload);
            }
        }
    }

    public static void focusSearch() {search.requestFocus();}
    public static boolean isSearchFocus() {
        return search.isFocused();
    }
}
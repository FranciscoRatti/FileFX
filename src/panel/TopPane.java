package panel;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import main.Lib;
import node.CenterNode;
import node.Button;

import java.io.File;
import java.util.Optional;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.MainPane.*;

public class TopPane extends HBox {
    private static Button back;
    private static Button forward;
    private static Button parent;
    private static TextField search;
    private static Button clean;
    private static Button reload;

    public enum BUTTONS {BACKWARD,FORWARD,PARENT,SEARCH,CLEAN,RELOAD}

    public TopPane() {
        setId("TopPane");

        for (String[] button : TOP_BUTTONS) {
            BUTTONS top_button = BUTTONS.valueOf(button[0]);
            switch (top_button) {
                case BACKWARD -> back = new Button(button[1], "Deshacer", "TopNode", e -> backward());
                case FORWARD -> forward = new Button(button[1], "Rehacer", "TopNode", e -> forward());
                case PARENT -> parent = new Button(button[1], "Ir arriba", "TopNode", e -> parent());
                case SEARCH -> {
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
                                if (!centerPane.items.isEmpty()) {
                                    CenterNode first = centerPane.items.getFirst();
                                    first.setSelected(true);
                                    first.requestFocus();
                                }
                                updateRight();
                            }
                        }
                    });
                }
                case CLEAN -> {
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
                                centerPane.selectFirst();
                                updateRight();
                            }
                        }
                    });
                }
                case RELOAD -> reload = new Button(button[1], "Recargar", "TopNode", e -> updateAll());
            }
        }

        update();
    }

    public void update() {
        printInfo("Actualizando panel superior");

        ObservableList<Node> children = getChildren();
        children.clear();

        for (String[] button : TOP_BUTTONS) {
            BUTTONS top_button = BUTTONS.valueOf(button[0]);
            switch (top_button) {
                case BACKWARD    -> children.add(back);
                case FORWARD -> children.add(forward);
                case PARENT  -> children.add(parent);
                case SEARCH  -> {
                    children.add(search);
                    search.setText(
                            path.startsWith(Lib.TRASH+"files") ? "trash"+path.substring(HOME.length()+25) :
                            path.startsWith(HOME) ? "~"+ (path.length() <= HOME.length() ? "/" : path.substring(HOME.length())) :
                            path);
                }
                case CLEAN -> {if (path.startsWith(Lib.TRASH+"files")) children.add(clean);}
                case RELOAD  -> children.add(reload);
            }
        }
    }

    public static void focusSearch() {search.requestFocus();}
    public static boolean isSearchFocus() {
        return search.isFocused();
    }
}
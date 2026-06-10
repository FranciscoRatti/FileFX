package scene;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import panel.CenterPane;

import static main.Lib.*;
import static main.Main.*;
import static panel.CenterPane.*;
import static panel.MainPane.*;
import main.Main;
import panel.RightPane;
import panel.TopPane;

public class Scene extends javafx.scene.Scene {
    public Scene() {
        super(mainPane, Integer.parseInt(Main.config.getProperty("width")), Integer.parseInt(Main.config.getProperty("height")));

        printInfo("Cargando hoja de estilos");
        getStylesheets().add("file://"+ABSOLUTE_PATH+"share/filefx/style.css");

        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCode keyCode = e.getCode();

            if (e.isControlDown()) {
                switch (keyCode) {
                    case X -> copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), true);
                    case C -> copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), false);
                    case V -> pasteFiles(getClipboardFiles());
                    case N -> CenterPane.showMenuCreate();

                    case DELETE -> removeFiles(parseFileLabelsToFiles(selectedItems));

                    case Z -> back();
                    case Y -> forward();
                    case L -> {if (!isAnyFocus()) TopPane.focusSearch();}

                    case T -> openShell();
                }
            } else if (e.isShiftDown()) {
                switch (keyCode) {
                    case UP -> mainPane.centerPane.changeSelectKey(true, -1);
                    case DOWN -> mainPane.centerPane.changeSelectKey(true, 1);
                    case PAGE_UP -> {if (!isAnyFocus()) mainPane.centerPane.changeSelectKey(true, -3);}
                    case PAGE_DOWN -> {if (!isAnyFocus()) mainPane.centerPane.changeSelectKey(true, 3);}
                }
            } else {
                switch (keyCode) {
                    case DELETE -> trashFiles(parseFileLabelsToFiles(selectedItems));
                    case CONTEXT_MENU -> CenterPane.showMenu(mainPane);
                    case ESCAPE -> {
                        if (TopPane.isSearchFocus()) updateAll(true, false, false, false, false);
                        else if (deselectAll()) updateAll(false, true, false, false, false);
                    }

                    case F5 -> updateAll(false, true, false, false, true);
                    case F4 -> RightPane.focusName();
                    case SPACE -> mainPane.changeShowRightPane();

                    case LEFT, BACK_SPACE -> {if (!isAnyFocus()) parent();}
                    case RIGHT, ENTER -> {if (!isAnyFocus()) mainPane.centerPane.openSelected();}

                    case UP -> {if (!isAnyFocus()) mainPane.centerPane.changeSelectKey(false, -1);}
                    case DOWN -> {if (!isAnyFocus()) mainPane.centerPane.changeSelectKey(false, 1);}
                    case PAGE_UP -> {if (!isAnyFocus()) mainPane.centerPane.changeSelectKey(false, -5);}
                    case PAGE_DOWN -> {if (!isAnyFocus()) mainPane.centerPane.changeSelectKey(false, 5);}
                }
            }
            if (!isAnyFocus()) e.consume();
        });

    }

    public static boolean isAnyFocus() {
        return  TopPane.isSearchFocus() || RightPane.isNameFocus();
    }
}
package scene;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import main.FileFX;
import panel.BottomPane;
import panel.CenterPane;
import panel.RightPane;
import panel.TopPane;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.CenterPane.*;
import static panel.MainPane.*;
import static panel.RightPane.changeShow;

public class Scene extends javafx.scene.Scene {
    public Scene() {
        super(mainPane, Double.parseDouble(dynamicValues.getProperty("width")), Double.parseDouble(dynamicValues.getProperty("height")));

        printInfo("Cargando hoja de estilos");
        getStylesheets().add("file://"+CONFIG_PATH+"theme.css");

        updateKeyBinding();
    }

    public static boolean isAnyFocus() {
        return  TopPane.isSearchFocus() || RightPane.isAnyFocus() || BottomPane.isFilterFocus();
    }

    private KeyCombination key;
    public void updateKeyBinding() {
        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            try {
                KeyCode keyCode = e.getCode();
                key =
                    e.isControlDown() ? new KeyCodeCombination(keyCode, KeyCombination.CONTROL_DOWN) :
                    e.isShiftDown() ? new KeyCodeCombination(keyCode, KeyCombination.SHIFT_DOWN) :
                    e.isAltDown() ? new KeyCodeCombination(keyCode, KeyCombination.ALT_DOWN) :
                    e.isMetaDown() ? new KeyCodeCombination(keyCode, KeyCombination.META_DOWN) :
                    e.isShortcutDown() ? new KeyCodeCombination(keyCode, KeyCombination.SHORTCUT_DOWN) :
                    new KeyCodeCombination(keyCode);
            } catch (IllegalArgumentException ignored) {return;}

            if (!isAnyFocus()) {
                e.consume();
                try {
                    if (setKeyBindAction(CUT, () -> copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), true))) return;
                    if (setKeyBindAction(COPY, () -> copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), false))) return;
                    if (setKeyBindAction(PASTE, () -> pasteFiles(getClipboardFiles()))) return;
                    if (setKeyBindAction(REMOVE, () -> removeFiles(parseFileLabelsToFiles(selectedItems)))) return;
                    if (setKeyBindAction(FileFX.TRASH, () -> trashFiles(parseFileLabelsToFiles(selectedItems)))) return;
                    if (setKeyBindAction(RENAME, () -> RightPane.focusName())) return;

                    if (setKeyBindAction(UP, () -> centerPane.moveCursor(false, -1))) return;
                    if (setKeyBindAction(OPEN, () -> CenterPane.openSelected())) return;
                    if (setKeyBindAction(DOWN, () -> centerPane.moveCursor(false, 1))) return;
                    if (setKeyBindAction(PARENT, () -> parent())) return;
                    if (setKeyBindAction(UP_STEP, () -> centerPane.moveCursor(false, -3))) return;
                    if (setKeyBindAction(DOWN_STEP, () -> centerPane.moveCursor(false, 3))) return;
                    if (setKeyBindAction(FIRST, () -> centerPane.moveCursor(false, -selectedItem.getIndex()))) return;
                    if (setKeyBindAction(LAST, () -> centerPane.moveCursor(false, centerNodes.size()-1 - selectedItem.getIndex()))) return;

                    if (setKeyBindAction(SELECT_UP, () -> centerPane.moveCursor(true, -1))) return;
                    if (setKeyBindAction(SELECT_DOWN, () -> centerPane.moveCursor(true, 1))) return;
                    if (setKeyBindAction(SELECT_UP_STEP, () -> centerPane.moveCursor(true, -5))) return;
                    if (setKeyBindAction(SELECT_DOWN_STEP, () -> centerPane.moveCursor(true, 5))) return;
                    if (setKeyBindAction(SELECT_FIRST, () -> centerPane.moveCursor(true, -selectedItem.getIndex()))) return;
                    if (setKeyBindAction(SELECT_LAST, () -> centerPane.moveCursor(true, centerNodes.size()-1 - selectedItem.getIndex()))) return;

                    if (setKeyBindAction(BACK, () -> back())) return;
                    if (setKeyBindAction(FORWARD, () -> forward())) return;

                    if (setKeyBindAction(OPEN_SHELL, () -> openShell())) return;
                    if (setKeyBindAction(SHOW_MENU, () -> CenterPane.showMenu(mainPane))) return;
                    if (setKeyBindAction(SHOW_MENU_CREATE, () -> CenterPane.showMenuCreate())) return;
                    if (setKeyBindAction(FOCUS_PATH, () -> Platform.runLater(() -> TopPane.focusSearch()))) return;
                    if (setKeyBindAction(FOCUS_FILTER, () -> Platform.runLater(() -> BottomPane.focusFilter()))) return;
                    if (setKeyBindAction(DESELECT_ALL, () ->{
                        deselectAll();
                        selectThis();
                        updateRight();
                    })) return;
                    if (setKeyBindAction(UPDATE_ALL, () -> updateAll())) return;
                    if (setKeyBindAction(CHANGE_SHOW_RIGHT_PANE, () -> changeShow())) return;
                    setKeyBindAction(CHANGE_SHOW_HIDDEN, () -> {
                        SHOW_HIDDEN = !SHOW_HIDDEN;
                        updateCenter();
                        selectFirst();
                    });

                } catch (IllegalArgumentException ignored) {}
            } else {
                try {
                    setKeyBindAction(DESELECT_ALL, () -> selectedItem.requestFocus());
                } catch (IllegalArgumentException ignored) {}
            }
        });
    }
    public boolean setKeyBindAction(KeyCombination[] keyCombinations, Runnable action) {
        for (KeyCombination keyCombination : keyCombinations) {
            if (keyCombination.equals(key)) {
                action.run();
                return true;
            }
        }
        return false;
    }
}
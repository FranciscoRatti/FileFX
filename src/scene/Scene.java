package scene;

import javafx.application.Platform;
import javafx.scene.input.*;
import main.FileFX;
import panel.*;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.CenterPane.*;
import static panel.MainPane.*;
import static panel.RightPane.*;

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
            key = getKeyCombination(e);
            if (key == null) return;

            if (!isAnyFocus()) {
                e.consume();
                try {
                    if (setKeyBindAction(CUT, () -> copyFilesToClipBoard(parseCenterNodesToFiles(centerPane.selectedItems), true))) return;
                    if (setKeyBindAction(COPY, () -> copyFilesToClipBoard(parseCenterNodesToFiles(centerPane.selectedItems), false))) return;
                    if (setKeyBindAction(PASTE, () -> pasteFiles(getClipboardFiles()))) return;
                    if (setKeyBindAction(REMOVE, () -> removeFiles(parseCenterNodesToFiles(centerPane.selectedItems)))) return;
                    if (setKeyBindAction(FileFX.TRASH, () -> trashFiles(parseCenterNodesToFiles(centerPane.selectedItems)))) return;
                    if (setKeyBindAction(RENAME, () -> RightPane.focusName())) return;

                    if (setKeyBindAction(UP, () -> centerPane.moveCursor(false, -1))) return;
                    if (setKeyBindAction(OPEN, () -> centerPane.openSelected())) return;
                    if (setKeyBindAction(DOWN, () -> centerPane.moveCursor(false, 1))) return;
                    if (setKeyBindAction(PARENT, () -> parent())) return;
                    if (setKeyBindAction(UP_STEP, () -> centerPane.moveCursor(false, -3))) return;
                    if (setKeyBindAction(DOWN_STEP, () -> centerPane.moveCursor(false, 3))) return;
                    if (setKeyBindAction(FIRST, () -> centerPane.moveCursor(false, -centerPane.selectedItem.getIndex()))) return;
                    if (setKeyBindAction(LAST, () -> centerPane.moveCursor(false, centerPane.centerNodes.size()-1 - centerPane.selectedItem.getIndex()))) return;

                    if (setKeyBindAction(SELECT_UP, () -> centerPane.moveCursor(true, -1))) return;
                    if (setKeyBindAction(SELECT_DOWN, () -> centerPane.moveCursor(true, 1))) return;
                    if (setKeyBindAction(SELECT_UP_STEP, () -> centerPane.moveCursor(true, -5))) return;
                    if (setKeyBindAction(SELECT_DOWN_STEP, () -> centerPane.moveCursor(true, 5))) return;
                    if (setKeyBindAction(SELECT_FIRST, () -> centerPane.moveCursor(true, -centerPane.selectedItem.getIndex()))) return;
                    if (setKeyBindAction(SELECT_LAST, () -> centerPane.moveCursor(true, centerPane.centerNodes.size()-1 - centerPane.selectedItem.getIndex()))) return;

                    if (setKeyBindAction(BACK, () -> back())) return;
                    if (setKeyBindAction(FORWARD, () -> forward())) return;

                    if (setKeyBindAction(OPEN_SHELL, () -> openShell())) return;
                    if (setKeyBindAction(SHOW_MENU, () -> centerPane.showMenu(mainPane))) return;
                    if (setKeyBindAction(SHOW_MENU_CREATE, () -> centerPane.showMenuCreate())) return;
                    if (setKeyBindAction(FOCUS_PATH, () -> Platform.runLater(() -> TopPane.focusSearch()))) return;
                    if (setKeyBindAction(FOCUS_FILTER, () -> Platform.runLater(() -> BottomPane.focusFilter()))) return;
                    if (setKeyBindAction(FOCUS_INSIDE, () -> Platform.runLater(() -> RightPane.focusInside()))) return;
                    if (setKeyBindAction(SAVE_INSIDE, () -> RightPane.saveInside())) return;
                    if (setKeyBindAction(DESELECT_ALL, () ->{
                        centerPane.deselectAll();
                        centerPane.selectThis();
                        updateRight();
                    })) return;
                    if (setKeyBindAction(UPDATE_ALL, () -> updateAll())) return;
                    if (setKeyBindAction(CHANGE_SHOW_RIGHT_PANE, () -> changeShow())) return;
                    setKeyBindAction(CHANGE_SHOW_HIDDEN, () -> {
                        SHOW_HIDDEN = !SHOW_HIDDEN;
                        updateCenter();
                        centerPane.selectFirst();
                    });

                } catch (IllegalArgumentException ignored) {}
            } else {
                try {
                    setKeyBindAction(DESELECT_ALL, () -> centerPane.selectedItem.requestFocus());
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

    public static KeyCombination getKeyCombination(KeyEvent e) {
        try {
            KeyCode keyCode = e.getCode();
            return e.isControlDown() ? new KeyCodeCombination(keyCode, KeyCombination.CONTROL_DOWN) :
                    e.isShiftDown() ? new KeyCodeCombination(keyCode, KeyCombination.SHIFT_DOWN) :
                    e.isAltDown() ? new KeyCodeCombination(keyCode, KeyCombination.ALT_DOWN) :
                    e.isMetaDown() ? new KeyCodeCombination(keyCode, KeyCombination.META_DOWN) :
                    e.isShortcutDown() ? new KeyCodeCombination(keyCode, KeyCombination.SHORTCUT_DOWN) :
                    new KeyCodeCombination(keyCode);
        } catch (IllegalArgumentException ignored) {return null;}
    }
}
package scene;

import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
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

    public void updateKeyBinding() {
        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            KeyCombination key;
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
                    setKeyBindAction(cut, key, () -> copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), true));
                    setKeyBindAction(copy, key, () -> copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), false));
                    setKeyBindAction(paste, key, () -> pasteFiles(getClipboardFiles()));
                    setKeyBindAction(remove, key, () -> removeFiles(parseFileLabelsToFiles(selectedItems)));
                    setKeyBindAction(trash, key, () -> trashFiles(parseFileLabelsToFiles(selectedItems)));
                    setKeyBindAction(rename, key, () -> RightPane.focusName());

                    setKeyBindAction(up, key, () -> centerPane.moveCursor(false, -1));
                    setKeyBindAction(open, key, () -> CenterPane.openSelected());
                    setKeyBindAction(down, key, () -> centerPane.moveCursor(false, 1));
                    setKeyBindAction(parent, key, () -> parent());
                    setKeyBindAction(up_step, key, () -> centerPane.moveCursor(false, -3));
                    setKeyBindAction(down_step, key, () -> centerPane.moveCursor(false, 3));

                    setKeyBindAction(select_up, key, () -> centerPane.moveCursor(true, -1));
                    setKeyBindAction(select_down, key, () -> centerPane.moveCursor(true, 1));
                    setKeyBindAction(select_up_step, key, () -> centerPane.moveCursor(true, -5));
                    setKeyBindAction(select_down_step, key, () -> centerPane.moveCursor(true, 5));

                    setKeyBindAction(back, key, () -> back());
                    setKeyBindAction(forward, key, () -> forward());

                    setKeyBindAction(open_shell, key, () -> openShell());
                    setKeyBindAction(show_menu, key, () -> CenterPane.showMenu(mainPane));
                    setKeyBindAction(show_menu_create, key, () -> CenterPane.showMenuCreate());
                    setKeyBindAction(focus_path, key, () -> Platform.runLater(() -> TopPane.focusSearch()));
                    setKeyBindAction(focus_filter, key, () -> Platform.runLater(() -> BottomPane.focusFilter()));

                    setKeyBindAction(deselect_all, key, () -> {
                        deselectAll();
                        selectThis();
                        updateRight();
                    });

                    setKeyBindAction(update_all, key, () -> updateAll());
                    setKeyBindAction(change_show_right_pane, key, () -> changeShow());
                } catch (IllegalArgumentException ignored) {}
            } else {
                try {
                    setKeyBindAction(deselect_all, key, () -> selectedItem.requestFocus());
                } catch (IllegalArgumentException ignored) {}
            }
        });
    }
    public void setKeyBindAction(KeyCombination[] keyCombinations, KeyCombination actualKey, Runnable action) {
        for (KeyCombination keyCombination : keyCombinations) {
            if (keyCombination.equals(actualKey)) {
                action.run();
            }
        }
    }
}
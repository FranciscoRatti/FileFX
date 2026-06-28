package scene;

import javafx.application.Platform;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import panel.CenterPane;
import panel.RightPane;
import panel.TopPane;

import static main.FileFX.*;
import static main.FileFX.back;
import static main.FileFX.forward;
import static main.FileFX.parent;
import static main.Lib.*;
import static main.Lib.back;
import static main.Lib.forward;
import static main.Lib.parent;
import static panel.MainPane.*;

public class Scene extends javafx.scene.Scene {
    public Scene() {
        super(mainPane, Double.parseDouble(dynamicValues.getProperty("width")), Double.parseDouble(dynamicValues.getProperty("height")));

        printInfo("Cargando hoja de estilos");
        getStylesheets().add("file://"+CONFIG_PATH+"theme.css");

        updateKeyBinding();
    }

    public static boolean isAnyFocus() {
        return  TopPane.isSearchFocus() || RightPane.isAnyFocus();
    }

    public void updateKeyBinding() {
        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (!isAnyFocus()) {
                e.consume();
                try {
                    KeyCombination key =
                                    e.isControlDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.CONTROL_DOWN) :
                                    e.isShiftDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.SHIFT_DOWN) :
                                    e.isAltDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.ALT_DOWN) :
                                    e.isMetaDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.META_DOWN) :
                                    e.isShortcutDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.SHORTCUT_DOWN) :
                                    new KeyCodeCombination(e.getCode());

                    setKeyBindAction(cut, key, () -> copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), true));
                    setKeyBindAction(copy, key, () -> copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), false));
                    setKeyBindAction(paste, key, () -> pasteFiles(getClipboardFiles()));
                    setKeyBindAction(remove, key, () -> removeFiles(parseFileLabelsToFiles(selectedItems)));
                    setKeyBindAction(trash, key, () -> trashFiles(parseFileLabelsToFiles(selectedItems)));
                    setKeyBindAction(rename, key, () -> RightPane.focusName());

                    setKeyBindAction(up, key, () -> centerPane.changeSelectKey(false, -1));
                    setKeyBindAction(open, key, () -> CenterPane.openSelected());
                    setKeyBindAction(down, key, () -> centerPane.changeSelectKey(false, 1));
                    setKeyBindAction(parent, key, () -> parent());
                    setKeyBindAction(up_step, key, () -> centerPane.changeSelectKey(false, -3));
                    setKeyBindAction(down_step, key, () -> centerPane.changeSelectKey(false, 3));

                    setKeyBindAction(select_up, key, () -> centerPane.changeSelectKey(true, -1));
                    setKeyBindAction(select_down, key, () -> centerPane.changeSelectKey(true, 1));
                    setKeyBindAction(select_up_step, key, () -> centerPane.changeSelectKey(true, -5));
                    setKeyBindAction(select_down_step, key, () -> centerPane.changeSelectKey(true, 5));

                    setKeyBindAction(back, key, () -> back());
                    setKeyBindAction(forward, key, () -> forward());

                    setKeyBindAction(open_shell, key, () -> openShell());
                    setKeyBindAction(show_menu, key, () -> CenterPane.showMenu(mainPane));
                    setKeyBindAction(show_menu_create, key, () -> CenterPane.showMenuCreate());
                    setKeyBindAction(focus_path, key, () -> Platform.runLater(() -> TopPane.focusSearch()));

                    setKeyBindAction(deselect_all, key, () -> {
                        if (TopPane.isSearchFocus()) updateTop();
                        else if (RightPane.isAnyFocus()) updateRight();
                        else {
                            deselectAll();
                            selectThis();
                            updateRight();
                        }
                    });

                    setKeyBindAction(update_all, key, () -> updateAll());
                    setKeyBindAction(change_show_right_pane, key, () -> mainPane.changeShowRightPane());
                } catch (IllegalArgumentException ignored) {}
        }});
    }
    public void setKeyBindAction(KeyCombination[] keyCombinations, KeyCombination actualKey, Runnable action) {
        for (KeyCombination keyCombination : keyCombinations) {
            if (keyCombination.equals(actualKey)) {
                action.run();
            }
        }
    }
}
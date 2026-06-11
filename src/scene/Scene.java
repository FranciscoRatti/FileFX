package scene;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import panel.CenterPane;

import static main.Lib.*;
import static main.Main.*;
import static panel.MainPane.*;
import main.*;
import panel.*;

public class Scene extends javafx.scene.Scene {
    public Scene() {
        super(mainPane, Integer.parseInt(Main.config.getProperty("width")), Integer.parseInt(Main.config.getProperty("height")));

        printInfo("Cargando hoja de estilos");
        getStylesheets().add("file://"+ABSOLUTE_PATH+"share/filefx/style.css");

        updateKeyBinding();
    }

    public void updateKeyBinding() {
        addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            try {
                if (!isAnyFocus()) e.consume();

                KeyCombination key =
                        e.isControlDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.CONTROL_DOWN) :
                                e.isShiftDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.SHIFT_DOWN) :
                                e.isAltDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.ALT_DOWN) :
                                e.isMetaDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.META_DOWN) :
                                e.isShortcutDown() ? new KeyCodeCombination(e.getCode(), KeyCombination.SHORTCUT_DOWN) :
                                new KeyCodeCombination(e.getCode());

                if (key.equals(cut)) copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), true);
                if (key.equals(copy)) copyFilesToClipBoard(parseFileLabelsToFiles(selectedItems), false);
                if (key.equals(paste)) pasteFiles(getClipboardFiles());
                if (key.equals(remove)) removeFiles(parseFileLabelsToFiles(selectedItems));
                if (key.equals(trash)) trashFiles(parseFileLabelsToFiles(selectedItems));
                if (key.equals(rename)) RightPane.focusName();

                if (key.equals(up)) centerPane.changeSelectKey(false, -1);
                if (key.equals(open)) if (!isAnyFocus()) centerPane.openSelected();
                if (key.equals(down)) centerPane.changeSelectKey(false, 1);
                if (key.equals(parent)) if (!isAnyFocus()) parent();
                if (key.equals(up_step)) if (!isAnyFocus()) centerPane.changeSelectKey(false, -3);
                if (key.equals(down_step)) if (!isAnyFocus()) centerPane.changeSelectKey(false, 3);

                if (key.equals(select_up)) if (!isAnyFocus()) centerPane.changeSelectKey(true, -1);
                if (key.equals(select_down)) if (!isAnyFocus()) centerPane.changeSelectKey(true, 1);
                if (key.equals(select_up_step)) if (!isAnyFocus()) centerPane.changeSelectKey(true, -5);
                if (key.equals(select_down_step)) if (!isAnyFocus()) centerPane.changeSelectKey(true, 5);

                if (key.equals(back)) back();
                if (key.equals(forward)) forward();

                if (key.equals(open_shell)) openShell();
                if (key.equals(show_menu)) CenterPane.showMenu(mainPane);
                if (key.equals(show_menu_create)) CenterPane.showMenuCreate();
                if (key.equals(focus_path)) if (!isAnyFocus()) TopPane.focusSearch();

                if (key.equals(deselect_all)) {
                    if (TopPane.isSearchFocus()) updateAll(true, false, false, false, false);
                    else if (deselectAll()) updateAll(false, true, false, false, false);
                }
                if (key.equals(update_all)) updateAll(false, true, false, false, true);
                if (key.equals(change_show_right_pane)) mainPane.changeShowRightPane();
            } catch (IllegalArgumentException ignored) {}
        });
    }

    public static boolean isAnyFocus() {
        return  TopPane.isSearchFocus() || RightPane.isNameFocus();
    }
}
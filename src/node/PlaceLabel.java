package node;

import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import javafx.util.Duration;
import main.Main;
import main.Lib;

import javax.naming.Context;

public class PlaceLabel extends Label {
    private String path;
    public PlaceLabel(String name, String icon, String path) {
        super(name, new ImageView("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/left/"+icon+".png"));
        this.path=path;

        setId("left_place_label");
        setMaxWidth(Double.MAX_VALUE);

        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                Lib.forwardBuffer.clear();
                Lib.backBuffer.add(path);
                Main.path = path;
                Lib.updateAll(true, true, true, false, true);
            }
        });

        setOnMouseEntered(e -> {
            PlaceMenu.hide();
        });
    }

    public static class PlaceMenu extends Label {
        private static ContextMenu menu;
        public PlaceMenu(String name, String icon, PlaceLabel... labels) {
            super(name, new ImageView("file://" + Lib.ABSOLUTE_PATH + "share/filefx/icons/left/" + icon + ".png"));
            setId("left_place_label");
            setMaxWidth(Double.MAX_VALUE);

            menu = new ContextMenu();
            menu.setAutoHide(true);
            ObservableList<MenuItem> observableList = menu.getItems();

            for (PlaceLabel label : labels) {
                MenuItem item = new MenuItem(label.getText(), label.getGraphic());
                item.setOnAction(e -> {
                    Lib.forwardBuffer.clear();
                    Lib.backBuffer.add(Main.path);
                    Main.path = label.path;
                    Lib.updateAll(true, true, true, false, true);
                });
                observableList.add(item);
            }

            setOnMouseEntered(e -> {
                if (!menu.isShowing()) menu.show(this, Side.RIGHT, 0, 0);
            });
        }

        public static void hide() {
            if (menu != null && menu.isShowing()) {
                menu.hide();
            }
        }
    }
}

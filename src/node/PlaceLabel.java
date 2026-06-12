package node;

import javafx.collections.ObservableList;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import main.Main;
import main.Lib;
import static main.Main.*;
import static main.Lib.*;

public class PlaceLabel extends Label {
    private String path;
    public PlaceLabel(String name, String icon, String path) {
        super(name, new ImageView("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/left/"+icon+".png"));
        this.path=path;

        setId("left_place_label");
        setMaxWidth(Double.MAX_VALUE);

        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                forwardBuffer.clear();
                backBuffer.add(path);
                Main.path = path;

                updateCenter();
                updateTop();
                updateRight();
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
                    forwardBuffer.clear();
                    backBuffer.add(Main.path);
                    Main.path = label.path;

                    updateCenter();
                    updateTop();
                    updateRight();
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

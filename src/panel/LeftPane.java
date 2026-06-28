package panel;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import main.Lib;
import node.LeftNode;

import static main.FileFX.config;
import static main.Lib.printInfo;

public class LeftPane extends VBox {
    private VBox placesBox;
    private VBox devicesBox;

    public LeftPane() {
        super(0);
        setId("left_pane");

        update();
    }

    public void update() {
        printInfo("Actualizando panel izquierdo");
        ObservableList<Node> children = getChildren();
        children.clear();

        if (Boolean.parseBoolean(config.getProperty("show_places"))) {
            placesBox = new VBox();
            ObservableList<Node> placesChildren = placesBox.getChildren();

            Label title = new Label("Lugares");
            title.setId("left_label_title");
            placesChildren.add(title);

            String[] places = config.getProperty("places").split(",");
            for (String place : places) {
                String[] values = place.substring(1, place.length()-1).split(";");
                placesChildren.add(new LeftNode(
                        values[0], values[1],
                        values[2].charAt(0) == '~' ? Lib.HOME+values[2].substring(1) : values[2]
                        ));
            }

            placesBox.getChildren().add(new node.Separator(10, Orientation.HORIZONTAL));
            children.add(placesBox);
        }

        if (Boolean.parseBoolean(config.getProperty("show_devices"))) {
            devicesBox = new VBox();

            Label title = new Label("Dispositivos");
            title.setId("left_label_title");

            Label rootDirectory = new LeftNode("Raiz", "\uEF81", "/");

            devicesBox.getChildren().addAll(title, rootDirectory, new node.Separator(20, Orientation.HORIZONTAL));
            children.add(devicesBox);
        }
    }
}

package panel;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import main.Lib;
import node.LeftNode;

import static main.FileFX.*;
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

        if (SHOW_PLACES) {
            placesBox = new VBox();
            ObservableList<Node> placesChildren = placesBox.getChildren();

            Label title = new Label("Lugares");
            title.setId("left_label_title");
            placesChildren.add(title);

            for (String[] place : PLACES) {
                placesChildren.add(new LeftNode(
                        place[0], place[1],
                        place[2].charAt(0) == '~' ? Lib.HOME+place[2].substring(1) : place[2]
                        ));
            }

            placesBox.getChildren().add(new node.Separator(10, Orientation.HORIZONTAL));
            children.add(placesBox);
        }

        if (SHOW_DEVICES) {
            devicesBox = new VBox();

            Label title = new Label("Dispositivos");
            title.setId("left_label_title");

            Label rootDirectory = new LeftNode("Raiz", "\uEF81", "/");

            devicesBox.getChildren().addAll(title, rootDirectory, new node.Separator(20, Orientation.HORIZONTAL));
            children.add(devicesBox);
        }
    }
}

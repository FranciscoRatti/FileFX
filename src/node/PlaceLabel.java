package node;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;

import main.*;

import static main.FileFX.*;
import static main.Lib.*;

public class PlaceLabel extends Label {
    public PlaceLabel(String name, String icon, String path) {
        super(name);

        Label label = new Label(icon);
        label.setFont(nerdFont);
        label.setId("left_place_icon");
        setGraphic(label);

        setId("left_place_label");
        setMaxWidth(Double.MAX_VALUE);

        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                printInfo("Yendo a '"+BLUE+path+RESET+"'");

                forwardBuffer.clear();
                backBuffer.add(path);
                FileFX.path = path;

                updateCenter();
                updateTop();
                updateRight();
            }
        });
    }
}

package node;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

import main.*;
import static main.Lib.*;

public class PlaceLabel extends Label {
    public PlaceLabel(String name, String icon, String path) {
        super(name, new ImageView("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/left/"+icon+".png"));

        setId("left_place_label");
        setMaxWidth(Double.MAX_VALUE);

        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
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

package node;

import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import main.FileFX;

import static main.FileFX.nerdFont;
import static main.Lib.*;
import static panel.MainPane.selectThis;

public class LeftNode extends Label {
    public LeftNode(String name, String icon, String path) {
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
                backBuffer.add(FileFX.path);
                FileFX.path = path;

                updateCenter();
                selectThis();
                updateTop();
                updateRight();
            }
        });
    }
}

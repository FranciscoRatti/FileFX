package node;

import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import main.FileFX;

import static main.FileFX.nerdFont;
import static main.Lib.*;
import static panel.MainPane.selectThis;

public class LeftNode extends Label {
    private final Label label;
    public LeftNode(String name, String icon, String path) {
        super(name);

        label = new Label(icon);
        label.setFont(nerdFont);
        label.setId("left_place_icon");
        setGraphic(label);

        setId("left_place_label");
        setMaxWidth(Double.MAX_VALUE);

        if (path != null) {
            setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    printExecute("Yendo a '"+BLUE+path+RESET+"'");

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

    public void setIcon(String icon) {
        label.setText(icon);
    }
    public LeftNode setColor(Color color) {
        String css = "-fx-text-fill: rgb("+color.getRed()*255+","+color.getGreen()*255+","+color.getBlue()*255+");";
        setStyle(css);
        label.setStyle(css);
        return this;
    }
}
package node;

import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import main.FileFX;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.MainPane.*;

public class LeftNode extends Label {
    private final Label label;
    public LeftNode(String name, String icon, String path) {
        super(name);

        label = new Label(icon);
        label.setFont(nerdFont);
        label.setId("LeftNode_icon");
        setGraphic(label);

        if (path != null) {
            setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    printExecute("Yendo a '"+BLUE+path+RESET+"'");

                    forwardBuffer.clear();
                    backBuffer.add(FileFX.path);
                    FileFX.path = path;

                    updateTop();
                    updateCenter();
                    centerPane.selectFirst();
                    updateRight();
                }
            });
        }

        setId("LeftNode_label");
        setMaxWidth(Double.MAX_VALUE);
        setTooltip(new Tooltip(path));
    }

    public void setIcon(String icon) {
        label.setText(icon);
    }
    public void setColor(Color color) {
        String css = "-fx-text-fill: rgb("+color.getRed()*255+","+color.getGreen()*255+","+color.getBlue()*255+");";
        setStyle(css);
        label.setStyle(css);
    }
}
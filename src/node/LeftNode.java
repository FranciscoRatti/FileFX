package node;

import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import main.FileFX;

import static main.FileFX.nerdFont;
import static main.Lib.*;
import static panel.MainPane.centerPane;
import static panel.MainPane.isAnyShowing;

public class LeftNode extends Label {
    private final Label iconLabel;
    private final String path;
    public LeftNode(String name, String icon, String path) {
        super(name);
        this.path=path;

        iconLabel = new Label(icon);
        iconLabel.setFont(nerdFont);
        iconLabel.setId("LeftNode_icon");
        setGraphic(iconLabel);

        if (path != null) {
            setOnMouseClicked(e -> {
                if (isAnyShowing()) return;
                if (e.getButton().equals(MouseButton.PRIMARY)) open();
            });
        }

        setId("LeftNode_label");
        setMaxWidth(Double.MAX_VALUE);
        setTooltip(new Tooltip(path));
    }

    public void open() {
        printExecute("Yendo a '"+BLUE+path+RESET+"'");

        forwardBuffer.clear();
        backBuffer.add(FileFX.path);
        FileFX.path = this.path;

        updateTop();
        updateCenter();
        centerPane.selectFirst();
        updateRight();
    }

    public void setIcon(String icon) {
        iconLabel.setText(icon);
    }
    public String getIcon() {return iconLabel.getText();}
    public void setIconId(String id) {
        iconLabel.setId(id);}

    public void setColor(Color color) {
        String css = "-fx-text-fill: rgb("+color.getRed()*255+","+color.getGreen()*255+","+color.getBlue()*255+");";
        setStyle(css);
        iconLabel.setStyle(css);
    }
}
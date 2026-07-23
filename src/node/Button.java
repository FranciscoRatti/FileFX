package node;

import javafx.event.*;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Text;

import static main.FileFX.nerdFont;

public class Button extends javafx.scene.control.Button {
    private final Text label;

    public Button(String icon, String tooltip, String id, EventHandler<ActionEvent> event) {
        label = new Text(icon);
        label.setFont(nerdFont);
        setGraphic(label);

        setTooltip(new Tooltip(tooltip));
        setEventHandler(ActionEvent.ACTION, event);

        changeId(id);
    }

    public void changeId(String id) {
        label.setId(id+"_icon");
        setId(id+"_button");
    }
    public void addSuffixId(String suffix) {
        label.setId(label.getId()+suffix);
        setId(getId()+suffix);
    }
}

package node;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import static main.FileFX.nerdFont;
import static main.Lib.ABSOLUTE_PATH;

public class TopNode extends Button {
    public TopNode(String icon, String tooltip, EventHandler<ActionEvent> event) {
        Text label = new Text(icon);
        label.setFont(nerdFont);
        label.setId("top_label");
        setGraphic(label);

        setTooltip(new Tooltip(tooltip));
        setId("top_button");
        setEventHandler(ActionEvent.ACTION, event);
    }
}

package node;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

import static main.Lib.ABSOLUTE_PATH;

public class TopNode extends Button {
    public TopNode(String icon, String tooltip, EventHandler<ActionEvent> event) {
        super(null, new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/top/"+icon+".png"));
        setTooltip(new Tooltip(tooltip));
        setId("top_button");
        setEventHandler(ActionEvent.ACTION, event);
    }
}

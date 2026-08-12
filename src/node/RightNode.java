package node;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RightNode extends HBox {
    public TextField value;

    public RightNode(String titleText, boolean editable) {
        setAlignment(Pos.CENTER_LEFT);
        setMaxWidth(Double.MAX_VALUE);

        Label title = new Label(titleText);
        title.setId("RightNode_title");
        title.setMinWidth(Region.USE_PREF_SIZE);

        value = new TextField();
        value.setId("RightNode_value");
        value.setEditable(editable);
        value.setMaxWidth(Double.MAX_VALUE);
        setHgrow(value, Priority.ALWAYS);

        getChildren().addAll(title, value);
    }
}
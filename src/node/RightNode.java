package node;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class RightNode extends HBox {
    public final TextField value;

    public RightNode(String titleText, boolean editable) {
        setAlignment(Pos.CENTER_LEFT);
        setMaxWidth(Double.MAX_VALUE);

        Label title = new Label(titleText);
        title.setId("RightNode_title");
        title.setMinWidth(Region.USE_PREF_SIZE);
        getChildren().add(title);

        this.value = new TextField();
        this.value.setId("RightNode_value");
        this.value.setEditable(editable);
        this.value.setMaxWidth(Double.MAX_VALUE);
        setHgrow(this.value, Priority.ALWAYS);

        getChildren().add(this.value);
    }
}

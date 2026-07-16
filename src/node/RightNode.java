package node;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class RightNode extends HBox {
    public Label title;
    public TextField value;

    public RightNode(String title, String value, boolean editable) {
        setAlignment(Pos.CENTER_LEFT);
        setMaxWidth(Double.MAX_VALUE);

        this.title = new Label(title);
        this.title.setId("RightNode_title");
        this.title.setMinWidth(Region.USE_PREF_SIZE);
        getChildren().add(this.title);

        this.value = new TextField(value);
        this.value.setId("RightNode_value");
        this.value.setEditable(editable);
        this.value.setMaxWidth(Double.MAX_VALUE);
        setHgrow(this.value, Priority.ALWAYS);

        getChildren().add(this.value);
    }
}

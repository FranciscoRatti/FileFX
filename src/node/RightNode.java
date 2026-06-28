package node;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class RightNode extends HBox {
    public Label label;
    public TextField textField;

    public RightNode(String title, String text, boolean editable) {
        super(10);
        setAlignment(Pos.CENTER_LEFT);
        setMaxWidth(Double.MAX_VALUE);
        label = new Label(title);
        label.setId("right_label");
        label.setMinWidth(Region.USE_PREF_SIZE);
        getChildren().add(label);

        textField = new TextField(text);
        textField.setId("right_component");
        textField.setEditable(editable);
        textField.setMaxWidth(Double.MAX_VALUE);
        setHgrow(textField, Priority.ALWAYS);

        getChildren().add(textField);
    }
}

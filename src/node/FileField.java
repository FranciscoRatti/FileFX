package node;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FileField extends HBox {
    public Label label;
    public TextField textField;

    public FileField(String title, String text, boolean editable) {
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

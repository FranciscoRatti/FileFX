package node;

import javafx.application.Platform;
import javafx.scene.control.*;

public class PartitionNode {
    public Label title;
    public TextField value;

    public PartitionNode(String title, String value, boolean editable) {
        this.title = new Label(title);
        this.title.setId("partition_title");

        this.value = new TextField(value);
        this.value.setId("partition_value");
        this.value.setEditable(editable);
        this.value.setMaxWidth(Double.MAX_VALUE);
    }
}
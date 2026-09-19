package node;

import javafx.scene.control.Label;
import javafx.scene.input.KeyCombination;

public class GotoNode extends Label {
    public GotoNode(String path, KeyCombination key) {
        super(" > "+path);
        setId("GotoNode_path");

        Label keyLabel = new Label(key.getDisplayText());
        keyLabel.setId("GotoNode_key");
        setGraphic(keyLabel);
    }
}

package stage;

import entity.PartitionProperties;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import node.PartitionNode;

import static main.Lib.CONFIG_PATH;

public class PartitionStage extends Stage {
    public PartitionStage(PartitionProperties properties) {
        setTitle(properties.labelText);

        VBox titlesPane, valuesPane;

        PartitionNode nameNode       = new PartitionNode("Nombre :", properties.name, false);
        PartitionNode uuidNode       = new PartitionNode("ID :", properties.uuid, false);
        PartitionNode labelNode      = new PartitionNode("Etiqueta :", properties.label, false);
        PartitionNode typeNode       = new PartitionNode("Tipo :", properties.type.toString(), false);
        PartitionNode sizeNode       = new PartitionNode("Tamaño :", properties.size, false);

        if (properties.type == PartitionProperties.TYPE.PART) {
            PartitionNode fsavailNode    = new PartitionNode("Libre :", properties.fsavail, false);
            PartitionNode fsuseNode      = new PartitionNode("Uso :", properties.fsuse, false);
            PartitionNode mountpointNode = new PartitionNode("Montaje :", properties.mountpoint, false);
            PartitionNode fstypeNode     = new PartitionNode("FS :", properties.fstype, false);
            PartitionNode rmNode         = new PartitionNode("Removible :", Boolean.toString(!properties.rm.equals("0")), false);

            String valueText = fsuseNode.value.getText();
            if (!valueText.isEmpty()) {
                int value = Integer.parseInt(fsuseNode.value.getText().substring(0, 2));
                if (value < 20) fsuseNode.value.setStyle("-fx-text-fill: lime;");
                else if (value < 40) fsuseNode.value.setStyle("-fx-text-fill: yellow;");
                else if (value < 60) fsuseNode.value.setStyle("-fx-text-fill: orange;");
                else if (value < 80) fsuseNode.value.setStyle("-fx-text-fill: red;");
                else fsuseNode.value.setStyle("-fx-text-fill: drakred;");
            }

            titlesPane = new VBox(
                    nameNode.title, uuidNode.title, labelNode.title, typeNode.title, sizeNode.title,
                    fsavailNode.title, fsuseNode.title, mountpointNode.title, fstypeNode.title, rmNode.title
            );

            valuesPane = new VBox(
                    nameNode.value, uuidNode.value, labelNode.value, typeNode.value, sizeNode.value,
                    fsavailNode.value, fsuseNode.value, mountpointNode.value, fstypeNode.value, rmNode.value
            );
        } else {
            PartitionNode modelNode = new PartitionNode("Modelo :", properties.model, false);

            titlesPane = new VBox(
                    nameNode.title, uuidNode.title, labelNode.title, modelNode.title, typeNode.title, sizeNode.title
            );

            valuesPane = new VBox(
                    nameNode.value, uuidNode.value, labelNode.value, modelNode.value, typeNode.value, sizeNode.value
            );
        }

        titlesPane.setId("PartitionColumn");
        titlesPane.setAlignment(Pos.CENTER_RIGHT);
        valuesPane.setId("PartitionColumn");
        valuesPane.setAlignment(Pos.CENTER_RIGHT);

        HBox mainPane = new HBox(titlesPane, valuesPane);
        mainPane.setId("PartitionPane");

        Scene scene = new Scene(mainPane);
        scene.getStylesheets().add("file://"+CONFIG_PATH+"theme.css");
        setScene(scene);
    }
}

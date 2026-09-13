package panel;

import entity.PartitionProperties;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.Lib;
import node.*;
import stage.PartitionStage;

import java.io.InputStream;
import java.util.ArrayList;

import static main.FileFX.*;
import static main.Lib.*;

public class LeftPane extends VBox {
    private VBox devicesBox;
    private Label devicesTitle = new Label("Dispositivos");
    public final ArrayList<PartitionStage> partitionStages;

    public LeftPane() {
        setMaxWidth(LEFT_WIDTH);
        partitionStages = new ArrayList<>();
        setId("LeftPane");

        if (SHOW_PLACES) {
            VBox placesBox = new VBox();
            ObservableList<Node> placesChildren = placesBox.getChildren();

            Label title = new Label("Lugares");
            title.setId("Left_title");
            placesChildren.add(title);

            for (String[] place : PLACES) {
                placesChildren.add(new LeftNode(
                        place[0], place[1],
                        place[2].charAt(0) == '~' ? Lib.HOME+place[2].substring(1) : place[2]
                ));
            }

            placesBox.getChildren().add(new node.Separator(20, Orientation.HORIZONTAL));
            getChildren().add(placesBox);
        }

        if (SHOW_DEVICES) {
            devicesBox = new VBox();
            devicesTitle = new Label("Dispositivos");
            devicesTitle.setId("Left_title");
            update();
            getChildren().add(devicesBox);
        }
    }

    public void update() {
        if (SHOW_DEVICES) {
            printInfo("Actualizando panel izquierdo");
            ObservableList<Node> devicesChildren = devicesBox.getChildren();
            devicesChildren.clear();

            devicesChildren.add(devicesTitle);
            partitionStages.clear();

            // Tomar discos y particiones
            try {
                Process process = new ProcessBuilder("lsblk", "-f", "-P", "-o", "NAME,FSTYPE,LABEL,UUID,FSAVAIL,FSUSE%,MOUNTPOINT,SIZE,RM,TYPE,MODEL").start();
                process.waitFor();
                try (InputStream in = process.getInputStream()) {
                    String[] lines = new String(in.readAllBytes()).split("\n");
                    for (String line : lines) {

                        // Tomar meta datos
                        String[] properties = line.substring(0, line.length()-1).split("\" ");
                        PartitionProperties part = new PartitionProperties(properties);

                        if (part.type == PartitionProperties.TYPE.DISK || part.type == PartitionProperties.TYPE.PART) {
                            PartitionStage stage = new PartitionStage(part);
                            partitionStages.add(stage);

                            LeftNode node = new LeftNode(
                                    part.labelText,
                                    part.icon,
                                    part.type == PartitionProperties.TYPE.PART ? !part.mountpoint.isEmpty() ? part.mountpoint : null : null
                            );
                            node.setColor(Color.valueOf((String) colorsMime.getOrDefault(part.type.toString().toLowerCase(), "white")));
                            node.setOnMouseReleased(e -> {
                                if (e.getButton() == MouseButton.SECONDARY) stage.show();
                            });

                            // Si es disco
                            if (part.type == PartitionProperties.TYPE.DISK) {
                                node.getTooltip().setText(part.labelText);

                                devicesChildren.add(node);
                            }

                            // Si es particion
                            else {

                                // Si esta montado
                                if (!part.mountpoint.isEmpty()) {

                                    // Si se puede desmontar
                                    if (part.rm.charAt(0) == '1') {
                                        devicesChildren.add(
                                                new HBox(
                                                        node,
                                                        new Button(UNMOUNT_ICON, "Desmontar", "LeftNode_unmount", e -> {
                                                            try {
                                                                printExecute("Expulsando '" + YELLOW + part.name + RESET + "'");
                                                                new ProcessBuilder("udisksctl", "unmount", "-b", "/dev/" + part.name, "&&", "udisksctl", "power-off", "-b", "/dev/" + part.name)
                                                                        .start().waitFor();
                                                                updateLeft();
                                                            } catch (Exception ex) {
                                                                printErrorAndShow("Error al expulsar '" + part.name + "'", ex);
                                                            }
                                                        })
                                                )
                                        );
                                    }

                                    // Si no se puede desmontar
                                    else {
                                        node.getTooltip().setText(part.mountpoint);
                                        devicesChildren.add(node);
                                    }
                                }

                                // Si no esta montado
                                else if (SHOW_UNMOUNTED) devicesChildren.add(node);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                printErrorAndShow("Error al cargar particiones", e);
            }

            devicesChildren.add(new node.Separator(20, Orientation.HORIZONTAL));
        }
    }

    public boolean isAnyShowing() {
        for (PartitionStage stage : partitionStages)
            if (stage.isShowing()) return true;

        return false;
    }
}
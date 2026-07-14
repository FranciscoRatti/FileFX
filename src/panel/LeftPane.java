package panel;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.Lib;
import node.Button;
import node.LeftNode;

import java.io.InputStream;

import static main.FileFX.*;
import static main.Lib.*;

public class LeftPane extends VBox {
    public LeftPane() {
        super(0);
        setId("left_pane");
        setMaxWidth(LEFT_WIDTH);

        update();
    }

    public void update() {
        printInfo("Actualizando panel izquierdo");
        ObservableList<Node> children = getChildren();
        children.clear();

         if (SHOW_PLACES) {
            VBox placesBox = new VBox();
            ObservableList<Node> placesChildren = placesBox.getChildren();

            Label title = new Label("Lugares");
            title.setId("left_label_title");
            placesChildren.add(title);

            for (String[] place : PLACES) {
                placesChildren.add(new LeftNode(
                        place[0], place[1],
                        place[2].charAt(0) == '~' ? Lib.HOME+place[2].substring(1) : place[2]
                        ));
            }

            placesBox.getChildren().add(new node.Separator(20, Orientation.HORIZONTAL));
            children.add(placesBox);
        }

        if (SHOW_DEVICES) {
            VBox devicesBox = new VBox();
            ObservableList<Node> devicesChildren = devicesBox.getChildren();

            Label title = new Label("Dispositivos");
            title.setId("left_label_title");
            devicesChildren.add(title);

            // Tomar discos y particiones
            try {
                Process process = new ProcessBuilder("lsblk", "-f", "-P", "-o", "NAME,FSTYPE,LABEL,UUID,FSAVAIL,FSUSE%,MOUNTPOINT,SIZE,RM,TYPE,MODEL").start();
                process.waitFor();
                try (InputStream in = process.getInputStream()) {
                    String[] lines = new String(in.readAllBytes()).split("\n");
                    for (String line : lines) {

                        // Tomar meta datos
                        String[] properties = line.substring(0, line.length()-1).split("\" ");
                        String name = properties[0].split("=")[1].substring(1);
                        String fstype = properties[1].split("=")[1].substring(1);
                        String label = properties[2].split("=")[1].substring(1);
                        String uuid = properties[3].split("=")[1].substring(1);
                        String fsavail = properties[4].split("=")[1].substring(1);
                        String fsuse = properties[5].split("=")[1].substring(1);
                        String mountpoint = properties[6].split("=")[1].substring(1);
                        String size = properties[7].split("=")[1].substring(1);
                        String rm = properties[8].split("=")[1].substring(1);
                        String type = properties[9].split("=")[1].substring(1);
                        String model = properties[10].split("=")[1].substring(1);
                        String labelName = label.equals("") ? model.equals("") ? name : model : label;

                        // Si es disco
                        if (type.equals("disk")) {
                            devicesChildren.add(new LeftNode(labelName, iconsMyme.getProperty("disc"), null)
                                    .setColor(Color.valueOf(colorsMyme.getProperty("disc"))));

                        // Si es particion
                        } else if (type.equals("part")){
                            String icon = iconsMyme.getProperty("partition");
                            if (!mountpoint.equals("")) {
                                for (String[] partitionIcon : PARTITION_ICONS) {
                                    if (partitionIcon[0].equals(mountpoint)) {
                                        icon = partitionIcon[1];
                                        labelName = partitionIcon[2];
                                    }
                                }

                                if (rm.charAt(0) == '1') {
                                    devicesChildren.add(new HBox(
                                            new LeftNode(labelName, " "+icon, mountpoint)
                                            .setColor(Color.valueOf(colorsMyme.getProperty("partition"))),
                                            new Button(UNMOUNT_ICON, "Desmontar", "left_unmount", e -> {
                                                try {
                                                    printExecute("Expulsando '"+YELLOW+name+RESET+"'");
                                                    new ProcessBuilder("udisksctl", "unmount", "-b", "/dev/"+name, "&&", "udisksctl", "power-off", "-b", "/dev/"+name)
                                                            .start().waitFor();
                                                    updateLeft();
                                                } catch (Exception ex) {
                                                    printError("Error expulsando '"+name+"'", ex);
                                                }
                                            })));
                                } else {
                                    devicesChildren.add(
                                            new LeftNode(labelName, " "+icon, mountpoint)
                                            .setColor(Color.valueOf(colorsMyme.getProperty("partition")))
                                    );
                                }
                            } else if (SHOW_UNMOUNTED) {
                                devicesChildren.add(new LeftNode(labelName, " "+icon, null)
                                        .setColor(Color.valueOf(colorsMyme.getProperty("partition"))));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                printError("Error al cargar particiones", e);
            }

            devicesChildren.add(new node.Separator(20, Orientation.HORIZONTAL));
            children.add(devicesBox);
        }
    }
}
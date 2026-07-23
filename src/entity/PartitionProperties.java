package entity;

import static main.FileFX.*;

public class PartitionProperties {
    public PartitionProperties(String[] properties) {
        name = properties[0].split("=")[1].substring(1);
        fstype = properties[1].split("=")[1].substring(1);
        label = properties[2].split("=")[1].substring(1);
        uuid = properties[3].split("=")[1].substring(1);
        fsavail = properties[4].split("=")[1].substring(1);
        fsuse = properties[5].split("=")[1].substring(1);
        mountpoint = properties[6].split("=")[1].substring(1);
        size = properties[7].split("=")[1].substring(1);
        rm = properties[8].split("=")[1].substring(1);
        type = TYPE.valueOf(properties[9].split("=")[1].substring(1).toUpperCase());
        model = properties[10].split("=")[1].substring(1);

        labelText = label.isEmpty() ? model.isEmpty() ? name : model : label;
        icon = iconsMime.getProperty(type.toString().toLowerCase());

        // Si es particion
        if (type == TYPE.PART) {
            if (!mountpoint.isEmpty()) {
                for (String[] partitionIcon : PARTITION_ICONS) {
                    if (partitionIcon[0].equals(mountpoint)) {
                        labelText = partitionIcon[1];
                    }
                }
            }
            icon = " "+icon;
        }
    }

    public enum TYPE {
        DISK, PART
    }

    public final String name;
    public final String fstype;
    public final String label;
    public final String uuid;
    public final String fsavail;
    public final String fsuse;
    public final String mountpoint;
    public final String size;
    public final String rm;
    public final TYPE type;
    public final String model;

    public String labelText;
    public String icon;
}
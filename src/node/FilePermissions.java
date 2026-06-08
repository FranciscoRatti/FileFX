package node;

import entity.FileProperties;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;

public class FilePermissions extends VBox {
    public static int permissionsOctal = 000;

    public FilePermissions(File file) {
        char[] ownerPermissions = FileProperties.getOwnerPermissions();
        char[] groupPermissions = FileProperties.getGroupPermissions();
        char[] otherPermissions = FileProperties.getOtherPermissions();
        permissionsOctal = toOctal(ownerPermissions)*100 + toOctal(groupPermissions)*10 + toOctal(otherPermissions);

        FileField titleLabel = new FileField("Permisos :", new String(ownerPermissions)+new String(groupPermissions)+new String(otherPermissions));

        /*
        CheckBoxPermission[] owner = new CheckBoxPermission[]{
                new CheckBoxPermission(ownerPermissions[0], 400),
                new CheckBoxPermission(ownerPermissions[1], 200),
                new CheckBoxPermission(ownerPermissions[2], 100)
        };

        CheckBoxPermission[] group = new CheckBoxPermission[]{
                new CheckBoxPermission(groupPermissions[0], 40),
                new CheckBoxPermission(groupPermissions[1], 20),
                new CheckBoxPermission(groupPermissions[2], 10)
        };

        CheckBoxPermission[] other = new CheckBoxPermission[]{
                new CheckBoxPermission(otherPermissions[0], 4),
                new CheckBoxPermission(otherPermissions[1], 2),
                new CheckBoxPermission(otherPermissions[2], 1)
        };

        HBox checkPane = new HBox(new Separator((int) 20, Orientation.VERTICAL),
                owner[0], owner[1], owner[2],
                group[0], group[1], group[2],
                other[0], other[1], other[2]
        );

        checkPane.setAlignment(Pos.CENTER_LEFT);
        */
        getChildren().addAll(titleLabel);
    }

    private int toOctal(char[] permissions) {
        char r = permissions[0];
        char w = permissions[1];
        char x = permissions[2];
        int total = 0;

        if (r == 'r') total+=4;
        if (w == 'w') total+=2;
        if (x == 'x') total+=1;

        return total;
    }

    /*
    private static class CheckBoxPermission extends  CheckBox{
        public CheckBoxPermission(char character, int value) {
            setId("permissions_check_box");
            setSelected(character != '-');

            setOnAction(e -> {
                if (isSelected()) {FilePermissions.permissionsOctal += value;}
                else {FilePermissions.permissionsOctal -= value;}
            });
        }
    }
    */
}
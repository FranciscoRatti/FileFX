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

        FileField titleLabel = new FileField("Permisos :", new String(ownerPermissions)+new String(groupPermissions)+new String(otherPermissions), false);

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
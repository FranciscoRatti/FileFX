package panel;

import entity.FileProperties;
import javafx.geometry.Orientation;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;

import main.Main;
import main.Lib;
import static main.Main.*;
import static main.Lib.*;
import static panel.MainPane.*;
import node.*;

public class RightPane extends ScrollPane {
    private static ImageView miniatura;
    private static FileField nameNode;
    private static FileField sizeNode;
    private static FilePermissions permissionsNode;
    private static FileField ownerNode;
    private static FileField groupNode;

    private VBox pane;

    public RightPane() {

        pane = new VBox();
        pane.setId("right_pane");
        pane.setPrefWidth(Integer.parseInt(config.getProperty("right_pref_width")));

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setId("right_scroll_pane");
        setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        setFitToWidth(true);
        setContent(pane);
    }

    public void update() {
        Lib.printInfo("Actualizando panel derecho");
        pane.getChildren().clear();
        int paneWidth = (int) pane.getWidth();

        Button close = new Button("x");
        close.setId("right_close_button");
        close.setOnAction(e -> {
            Main.mainPane.changeShowRightPane(false);
        });

        if (MainPane.selectedItem != null) {
            String extensionText = selectedItem.getExtension();

            // Miniatura
            Image image;

            if (selectedFile.isDirectory()) {
                image = new Image("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/right/directory.png");
            } else {
                if (
                        extensionText.equals("bmp") ||
                        extensionText.equals("gif") ||
                        extensionText.equals("jpeg") ||
                        extensionText.equals("jpg") ||
                        extensionText.equals("png")
                ) {
                    image = new Image("file://"+selectedFile.getAbsolutePath());
                } else image = new Image("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/right/file.png");
            }

            pane.applyCss();

            miniatura = new ImageView(image);
            miniatura.setPreserveRatio(true);
            miniatura.setSmooth(true);

            int imageWidth = (int) image.getWidth();
            int imageHeight = (int) image.getHeight();
            if (imageWidth < imageHeight) miniatura.setFitHeight(paneWidth-5);
            else miniatura.setFitWidth(paneWidth-5);

            StackPane miniaturaPane = new StackPane(miniatura);

            // Propiedades
            nameNode = new FileField("Nombre :", selectedItem.getName(), true);
            nameNode.textField.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    Lib.renameFile(selectedFile, nameNode.textField.getText());
                }
            });

            // Tamaño
            long size = FileProperties.getSize();
            String sizeText = String.valueOf(size);
            int sizeTextLenght = sizeText.length();
            String unit;
            if (size >= 1000) {
                 unit = "KB";
                 sizeText = sizeText.substring(0, sizeTextLenght-3)+","+sizeText.substring(sizeTextLenght-3, sizeTextLenght-1);
            } else if (size >= 1000000) {
                unit = "MB";
                sizeText = sizeText.substring(0, sizeText.length()-6)+","+sizeText.substring(sizeTextLenght-6, sizeTextLenght-4);
            } else if (size >= 1000000000) {
                unit = "GB";
                sizeText = sizeText.substring(0, sizeText.length()-9)+","+sizeText.substring(sizeTextLenght-9, sizeTextLenght-7);
            } else if (size >= 1000000000000L) {
                unit = "TB";
                sizeText = sizeText.substring(0, sizeText.length()-12)+","+sizeText.substring(sizeTextLenght-12, sizeTextLenght-10);
            } else unit = "B";

            sizeNode = new FileField("Tamaño :", sizeText+" "+unit, false);

            // Permisos
            permissionsNode = new FilePermissions(selectedFile);

            // Usuario y grupo
            ownerNode = new FileField("Usuario :", FileProperties.getOwner(), true);
            groupNode = new FileField("Grupo   :", FileProperties.getGroup(), true);

            pane.getChildren().addAll(
                    close,
                    miniaturaPane,
                    new Separator(10, Orientation.HORIZONTAL),
                    nameNode,
                    sizeNode,
                    new Separator(20, Orientation.HORIZONTAL),
                    permissionsNode,
                    new Separator(20, Orientation.HORIZONTAL),
                    ownerNode,
                    groupNode
            );
        } else {
            miniatura = new ImageView("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/right/void.png");

            StackPane miniaturaPane = new StackPane(miniatura);
            miniaturaPane.setMinHeight(paneWidth);

            pane.getChildren().addAll(close, miniaturaPane);
        }
    }

    public static void focusName() {
        nameNode.textField.requestFocus();
    }

    public static boolean isNameFocus() {
        if (nameNode != null) return nameNode.textField.isFocused();
        else return false;
    }
}
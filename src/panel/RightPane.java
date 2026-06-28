package panel;

import entity.FileProperties;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import main.Lib;
import node.RightNode;

import java.time.LocalDateTime;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.MainPane.selectedItem;

public class RightPane extends ScrollPane {
    private static RightNode nameNode;
    private static RightNode sizeNode;
    private static RightNode dateTimeNode;
    private static RightNode tipeNode;
    private static RightNode permissionsNode;
    private static RightNode ownerNode;
    private static RightNode groupNode;

    private VBox pane;
    private double paneWidth;

    public RightPane() {
        paneWidth = RIGHT_WIDTH;

        pane = new VBox();
        pane.setId("right_pane");
        pane.setPrefWidth(paneWidth);

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setId("right_scroll_pane");
        setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        setFitToWidth(true);
        setContent(pane);

        update();
    }

    public void update() {
        printInfo("Actualizando panel derecho");
        ObservableList<Node> children = pane.getChildren();
        children.clear();

        Button close = new Button("x");
        close.setId("right_close_button");
        close.setOnAction(e -> {
            mainPane.changeShowRightPane(false);
        });
        children.add(close);

        ImageView miniatura;
        if (selectedItem != null) {
            FileProperties propertie = selectedItem.getPropertie();
            String extensionText = selectedItem.getExtension();

            // Miniatura
            if (SHOW_MINIATURA &&
                    !propertie.isDirectory() && extensionText != null &&
                    (
                            extensionText.equals("bmp") ||
                            extensionText.equals("gif") ||
                            extensionText.equals("jpeg") ||
                            extensionText.equals("jpg") ||
                            extensionText.equals("png")
            )) {
                Image image = new Image("file://"+propertie.getAbsolutePath());
                miniatura = new ImageView(image);
                miniatura.setPreserveRatio(true);

                int imageWidth = (int) image.getWidth();
                int imageHeight = (int) image.getHeight();
                if (imageWidth < imageHeight) miniatura.setFitHeight(paneWidth-5);
                else miniatura.setFitWidth(paneWidth-5);

                StackPane miniaturaPane = new StackPane(miniatura);
                miniaturaPane.setMinSize(paneWidth, paneWidth);

                children.addAll(miniaturaPane);
            } else {
                Text label = new Text(selectedItem.getIcon());
                label.setFont(nerdFont);
                label.setId("right_miniatura");

                if (FILL_MINIATURA_LIKE_ICON)
                    label.setFill(selectedItem.getColor());
                else
                    label.setFill(UNKNOW_COLOR);

                StackPane miniaturaPane = new StackPane(label);
                miniaturaPane.setMinSize(paneWidth, paneWidth);

                children.add(miniaturaPane);
            }

            // Propiedades
            nameNode = new RightNode("Nombre :", selectedItem.getName(), !path.startsWith(TRASH+"files"));
            nameNode.textField.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    renameFile(propertie, nameNode.textField.getText());
                }
            });

            // Tamaño
            long size = propertie.getSize();
            String sizeText = String.valueOf(size);
            int sizeTextLenght = sizeText.length();

            if (size >= 1000)
                 sizeText = sizeText.substring(0, sizeTextLenght-3)+","+sizeText.substring(sizeTextLenght-3, sizeTextLenght-1)+" KB";
            else if (size >= 1000000)
                sizeText = sizeText.substring(0, sizeText.length()-6)+","+sizeText.substring(sizeTextLenght-6, sizeTextLenght-4)+" MB";
            else if (size >= 1000000000)
                sizeText = sizeText.substring(0, sizeText.length()-9)+","+sizeText.substring(sizeTextLenght-9, sizeTextLenght-7)+" GB";
            else if (size >= 1000000000000L)
                sizeText = sizeText.substring(0, sizeText.length()-12)+","+sizeText.substring(sizeTextLenght-12, sizeTextLenght-10)+" TB";
            else sizeText += " BI";

            sizeNode = new RightNode("Tamaño :", sizeText, false);

            // Fecha
            LocalDateTime dateTime = propertie.getDateTime();
            LocalDateTime now = LocalDateTime.now();
            dateTimeNode = new RightNode("Fecha :",
                    dateTime.isAfter(now.minusDays(1)) ? dateTime.getHour()+":"+dateTime.getMinute() :
                    dateTime.isAfter(now.minusYears(1)) ? dateTime.getDayOfMonth()+"/"+dateTime.getMonthValue() :
                    dateTime.getDayOfMonth()+"/"+dateTime.getMonthValue()+"/"+dateTime.getYear(),
                    false);

            // Tipo mime
            tipeNode = new RightNode("Tipo :", propertie.getMimeType(), false);

            // Permisos
            permissionsNode = new RightNode("Permisos :",
                    new String(propertie.getOwnerPermissions())+
                    new String(propertie.getGroupPermissions())+
                    new String(propertie.getOtherPermissions()),
                    false);

            // Usuario y grupo
            ownerNode = new RightNode("Usuario :", propertie.getOwner(), true);
            groupNode = new RightNode("Grupo   :", propertie.getGroup(), true);

            children.addAll(
                    new node.Separator(10, Orientation.HORIZONTAL),
                    nameNode,
                    sizeNode,
                    dateTimeNode,
                    tipeNode,
                    new node.Separator(20, Orientation.HORIZONTAL),
                    permissionsNode,
                    ownerNode,
                    groupNode
            );
        } else {
            miniatura = new ImageView("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/right/void.png");

            StackPane miniaturaPane = new StackPane(miniatura);
            miniaturaPane.setMinHeight(paneWidth);

            children.add(miniaturaPane);
        }
    }

    public static void focusName() {
        nameNode.textField.requestFocus();
    }

    public static boolean isAnyFocus() {
        return  (nameNode != null && nameNode.textField.isFocused()) ||
                (sizeNode != null && sizeNode.textField.isFocused()) ||
                (dateTimeNode != null && dateTimeNode.textField.isFocused()) ||
                (tipeNode != null && tipeNode.textField.isFocused()) ||
                (permissionsNode != null && permissionsNode.textField.isFocused()) ||
                (ownerNode != null && ownerNode.textField.isFocused()) ||
                (groupNode != null && groupNode.textField.isFocused());
    }
}
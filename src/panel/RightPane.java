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
import node.RightNode;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.MainPane.*;

public class RightPane extends ScrollPane {
    private static RightNode nameNode;
    private static RightNode permissionsNode;
    private static RightNode ownerNode;
    private static RightNode groupNode;
    private static RightNode sizeNode;
    private static RightNode modifiedDateTimeNode;
    private static RightNode createDateTimeNode;
    private static RightNode typeNode;

    private final VBox pane;
    private double paneWidth;

    private static boolean isRightPaneShow;

    public RightPane() {
        paneWidth = RIGHT_WIDTH;

        pane = new VBox();
        pane.setId("RightPane_pane");
        pane.setPrefWidth(paneWidth);

        update();

        setHbarPolicy(ScrollBarPolicy.NEVER);
        setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        setFitToWidth(true);
        setContent(pane);
        setId("RightPane");
    }

    public void update() {
        printInfo("Actualizando panel derecho");
        ObservableList<Node> children = pane.getChildren();
        children.clear();

        Button close = new Button("x");
        close.setId("Right_close");
        close.setOnAction(e -> {
            changeShow(false);
        });
        children.add(close);

        if (selectedItem != null) {
            FileProperties propertie = selectedItem.getPropertie();
            String extensionText = selectedItem.getExtension();

            // Miniatura
            if (SHOW_MINIATURA &&
                !propertie.isDirectory() && extensionText != null && (
                    extensionText.equals("bmp") ||
                    extensionText.equals("gif") ||
                    extensionText.equals("jpeg") ||
                    extensionText.equals("jpg") ||
                    extensionText.equals("png")
                )
            ) {
                Image image = new Image("file://" + propertie.getAbsolutePath());
                ImageView miniatura = new ImageView(image);
                miniatura.setPreserveRatio(true);

                int imageWidth = (int) image.getWidth();
                int imageHeight = (int) image.getHeight();
                if (imageWidth < imageHeight) miniatura.setFitHeight(paneWidth - 5);
                else miniatura.setFitWidth(paneWidth - 5);

                StackPane miniaturaPane = new StackPane(miniatura);
                miniaturaPane.setMinSize(paneWidth, paneWidth);

                children.addAll(miniaturaPane);
            } else {
                Text label = new Text(selectedItem.getIcon());
                label.setFont(nerdFont);
                label.setId("Right_miniatura");

                if (FILL_MINIATURA_LIKE_ICON) label.setFill(selectedItem.getColor());
                else label.setFill(UNKNOW_COLOR);

                StackPane miniaturaPane = new StackPane(label);
                miniaturaPane.setMinSize(paneWidth, paneWidth);

                children.add(miniaturaPane);
            }

            // Propiedades
            nameNode = new RightNode("Nombre :", selectedItem.getName(), !path.startsWith(TRASH + "files"));
            nameNode.value.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    renameFile(propertie, nameNode.value.getText());
                }
            });

            // Tamaño
            sizeNode = new RightNode("Tamaño :", propertie.getSizeString(), false);

            // Fecha
            createDateTimeNode = new RightNode("Creado :", propertie.getCreationString(), false);
            modifiedDateTimeNode = new RightNode("Modificado :", propertie.getModifiedString(), false);

            // Tipo mime
            typeNode = new RightNode("Tipo :", propertie.getMimeType(), false);

            // Permisos
            permissionsNode = new RightNode("Permisos :",
                    new String(propertie.getOwnerPermissions()) +
                    new String(propertie.getGroupPermissions()) +
                    new String(propertie.getOtherPermissions()),
                    false);

            // Usuario y grupo
            ownerNode = new RightNode("Usuario :", propertie.getOwner(), true);
            groupNode = new RightNode("Grupo   :", propertie.getGroup(), true);

            children.addAll(
                    new node.Separator(10, Orientation.HORIZONTAL),
                    nameNode,
                    sizeNode,
                    createDateTimeNode,
                    modifiedDateTimeNode,
                    typeNode,
                    new node.Separator(20, Orientation.HORIZONTAL),
                    permissionsNode,
                    ownerNode,
                    groupNode
            );
        }
    }

    public static void focusName() {
        nameNode.value.requestFocus();
        nameNode.value.selectRange(0, selectedItem.getName().length()-selectedItem.getExtension().length()-1);
    }
    public static void changeShow(boolean isRightPaneShow) {
        RightPane.isRightPaneShow=isRightPaneShow;
        if (isRightPaneShow) {
            if (rightPane == null) rightPane = new RightPane();
            mainPane.setRight(rightPane);
        } else {
            mainPane.setRight(null);
        }
    }
    public static void changeShow() {
        changeShow(!isRightPaneShow);
    }
    public static boolean isAnyFocus() {
        return  (nameNode != null && nameNode.value.isFocused()) ||
                (sizeNode != null && sizeNode.value.isFocused()) ||
                (modifiedDateTimeNode != null && modifiedDateTimeNode.value.isFocused()) ||
                (createDateTimeNode != null && createDateTimeNode.value.isFocused()) ||
                (typeNode != null && typeNode.value.isFocused()) ||
                (permissionsNode != null && permissionsNode.value.isFocused()) ||
                (ownerNode != null && ownerNode.value.isFocused()) ||
                (groupNode != null && groupNode.value.isFocused());
    }
}
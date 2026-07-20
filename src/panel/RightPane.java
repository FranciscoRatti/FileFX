package panel;

import entity.FileProperties;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import main.Lib;
import node.CenterNode;
import node.RightNode;

import java.io.*;
import java.util.ArrayList;

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
            FileProperties properties = selectedItem.getFileProperties();
            String extensionText = selectedItem.getExtension();

            // Miniatura
            StackPane miniaturaPane = new StackPane();
            if (SHOW_MINIATURA &&
                !properties.isDirectory && extensionText != null && (
                    extensionText.equals("bmp") ||
                    extensionText.equals("gif") ||
                    extensionText.equals("jpeg") ||
                    extensionText.equals("jpg") ||
                    extensionText.equals("png")
                )
            ) {
                Image image = new Image("file://" + properties.getAbsolutePath());
                ImageView miniatura = new ImageView(image);
                miniatura.setPreserveRatio(true);

                int imageWidth = (int) image.getWidth();
                int imageHeight = (int) image.getHeight();
                if (imageWidth < imageHeight) miniatura.setFitHeight(paneWidth - 3);
                else miniatura.setFitWidth(paneWidth - 3);

                miniaturaPane.getChildren().add(miniatura);

            } else if (
                    (SHOW_INSIDE_DIRECTORIES && properties.isDirectory) ||
                    (SHOW_INSIDE_FILES && properties.getMimeType().startsWith("text/"))
            ) {
                VBox insideBox = new VBox();
                ObservableList<Node> childrenInside = insideBox.getChildren();

                ScrollPane insidePane = new ScrollPane(insideBox);
                insidePane.setId("Right_miniatura_pane");
                insidePane.setHbarPolicy(ScrollBarPolicy.NEVER);
                insidePane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
                insidePane.setFitToWidth(true);

                // Si es directorio
                if (properties.isDirectory) {
                    ArrayList<CenterNode> insideNodes;
                    File[] content = properties.listFiles();

                    if (content.length > 0) {
                        insideNodes = new ArrayList<>();

                        ArrayList<CenterNode> filesList = new ArrayList<>();
                        ArrayList<CenterNode> directoriesList = new ArrayList<>();

                        int size;
                        for (size = 0; size < content.length && size < 50; size++) {
                            File file = content[size];
                            CenterNode node = new CenterNode(file, false);

                            if (file.isDirectory()) directoriesList.add(node);
                            else filesList.add(node);
                        }

                        if (IS_DIRECTORY_FIRST) insideNodes.addAll(directoriesList);
                        insideNodes.addAll(filesList);
                        if (!IS_DIRECTORY_FIRST) insideNodes.addAll(directoriesList);

                        if (size == 50) {
                            CenterNode node = new CenterNode("[...]");
                            insideNodes.add(node);
                        }

                        for (int i = 0; i < insideNodes.size(); i++) {
                            CenterNode node = insideNodes.get(i);
                            node.setIndex(i);
                            childrenInside.add(node);
                        }

                        miniaturaPane.getChildren().add(insidePane);
                    } else {
                        Text insideVoid = new Text("Vacio");
                        insideVoid.setId("Right_miniatura_void");
                        miniaturaPane.getChildren().add(insideVoid);
                    }

                // Si es archivo
                } else {
                    try (BufferedReader reader = new BufferedReader(new FileReader(selectedItem.getFile()))) {
                        String lineText;
                        int index = 0;
                        while ((lineText = reader.readLine()) != null && index < 200) {
                            Label line = new Label(lineText);
                            line.setMaxWidth(Double.MAX_VALUE);
                            line.setId(index % 2 == 0 ? "Right_miniatura_lineB1" : "Right_miniatura_lineB2");
                            childrenInside.add(line);
                            index++;
                        }

                        if (index == 200) {
                            Label line = new Label("[...]");
                            line.setMaxWidth(Double.MAX_VALUE);
                            line.setId("Right_miniatura_lineB1");
                            childrenInside.add(line);
                        }

                        miniaturaPane.getChildren().add(insidePane);
                    } catch (Exception e) {
                        printError("Error al leer interior del archivo "+selectedItem.getFile().getAbsolutePath(), e);
                    }
                }
            } else {
                Text label = new Text(selectedItem.getIcon());
                label.setFont(nerdFont);
                label.setId("Right_miniatura");

                if (FILL_MINIATURA_LIKE_ICON) label.setFill(selectedItem.getColor());
                else label.setFill(UNKNOW_COLOR);

                miniaturaPane.getChildren().add(label);
            }

            miniaturaPane.setMinSize(paneWidth, paneWidth);
            miniaturaPane.setMaxSize(paneWidth, paneWidth);
            children.add(miniaturaPane);

            // Propiedades
            nameNode = new RightNode("Nombre :", selectedItem.getName(), !path.startsWith(Lib.TRASH + "files"));
            nameNode.value.setOnKeyPressed(e -> {
                if (e.getCode().equals(KeyCode.ENTER)) {
                    renameFile(properties, nameNode.value.getText());
                }
            });

            // Tamaño
            sizeNode = new RightNode("Tamaño :", properties.getSizeString(), false);

            // Fecha
            createDateTimeNode = new RightNode("Creado :", properties.getCreationString(), false);
            modifiedDateTimeNode = new RightNode("Modificado :", properties.getModifiedString(), false);

            // Tipo mime
            typeNode = new RightNode("Tipo :", properties.getMimeType(), false);

            // Permisos
            permissionsNode = new RightNode("Permisos :",
                    new String(properties.getOwnerPermissions()) +
                    new String(properties.getGroupPermissions()) +
                    new String(properties.getOtherPermissions()),
                    false);

            // Usuario y grupo
            ownerNode = new RightNode("Usuario :", properties.getOwner(), true);
            groupNode = new RightNode("Grupo   :", properties.getGroup(), true);

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
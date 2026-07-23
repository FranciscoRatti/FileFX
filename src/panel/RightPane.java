package panel;

import entity.FileProperties;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import main.Lib;
import node.CenterNode;
import node.RightNode;
import scene.Scene;

import java.io.*;
import java.util.ArrayList;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.MainPane.*;

public class RightPane extends ScrollPane {
    private static StackPane miniaturaPane;
    private static Text iconLabel;
    private static ScrollPane insidePane;
    private static TextArea textNode;

    private static RightNode nameNode;
    private static RightNode permissionsNode;
    private static RightNode ownerNode;
    private static RightNode groupNode;
    private static RightNode sizeNode;
    private static RightNode modifiedDateTimeNode;
    private static RightNode createDateTimeNode;
    private static RightNode typeNode;

    public static double paneWidth;
    public static boolean isRightPaneShow;

    public RightPane() {
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        setFitToWidth(true);
        setId("RightPane");
        
        paneWidth = RIGHT_WIDTH;
        VBox pane = new VBox();
        pane.setId("RightPane_pane");
        pane.setPrefWidth(paneWidth);
        setContent(pane);
        ObservableList<Node> children = pane.getChildren();

        Button close = new Button("x");
        close.setId("Right_close");
        close.setOnAction(e -> changeShow(false));

        // Miniatura
        miniaturaPane = new StackPane();
        miniaturaPane.setMinSize(paneWidth, paneWidth);
        miniaturaPane.setMaxSize(paneWidth, paneWidth);

        iconLabel = new Text();
        iconLabel.setFont(nerdFont);
        iconLabel.setId("Right_miniatura");

        insidePane = new ScrollPane();
        insidePane.setId("Right_miniatura_pane");
        insidePane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        insidePane.setHbarPolicy(ScrollBarPolicy.NEVER);
        insidePane.setFitToWidth(true);
        insidePane.setFitToHeight(true);
        insidePane.setMinSize(paneWidth, paneWidth);
        insidePane.setMaxSize(paneWidth, paneWidth);

        update();

        // Propiedades
        nameNode = new RightNode("Nombre :", !path.startsWith(Lib.TRASH+"files"));
        nameNode.value.setOnKeyPressed(e -> {
            if (centerPane.selectedItem != null && e.getCode().equals(KeyCode.ENTER)) {
                renameFile(centerPane.selectedItem.getFile(), nameNode.value.getText());
            }
        });
        sizeNode = new RightNode("Tamaño :", false);
        createDateTimeNode = new RightNode("Creado :", false);
        modifiedDateTimeNode = new RightNode("Modificado :", false);
        typeNode = new RightNode("Tipo :", false);
        permissionsNode = new RightNode("Permisos :", false);
        ownerNode = new RightNode("Usuario :", true);
        groupNode = new RightNode("Grupo   :", true);

        children.addAll(
                close,
                miniaturaPane,
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

    public void update() {
        printInfo("Actualizando panel derecho");

        if (centerPane.selectedItem != null) {
            FileProperties properties = centerPane.selectedItem.getFileProperties();

            // Miniatura
            String extensionText = centerPane.selectedItem.getExtension();
            miniaturaPane.getChildren().clear();
            insidePane.setContent(null);
            textNode = null;

            // Si es imagen
            if (SHOW_MINIATURA && !properties.isDirectory && extensionText != null && (
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

            // Si es directorio
            } else if (SHOW_INSIDE_DIRECTORIES && properties.isDirectory) {
                VBox insideBox = new VBox();
                ObservableList<Node> childrenBox = insideBox.getChildren();

                ArrayList<CenterNode> insideNodes;
                File[] content = properties.listFiles();

                if (content.length > 0) {
                    insideNodes = new ArrayList<>();

                    ArrayList<CenterNode> filesList = new ArrayList<>();
                    ArrayList<CenterNode> directoriesList = new ArrayList<>();

                    int size;
                    for (size = 0; size < content.length && size < 25; size++) {
                        File file = content[size];
                        CenterNode node = new CenterNode(file, false);

                        if (file.isDirectory()) directoriesList.add(node);
                        else filesList.add(node);
                    }

                    if (IS_DIRECTORY_FIRST) insideNodes.addAll(directoriesList);
                    insideNodes.addAll(filesList);
                    if (!IS_DIRECTORY_FIRST) insideNodes.addAll(directoriesList);

                    if (size == 25) {
                        CenterNode node = new CenterNode("[...]");
                        insideNodes.add(node);
                    }

                    for (int i = 0; i < insideNodes.size(); i++) {
                        CenterNode node = insideNodes.get(i);
                        node.setIndex(i);
                        childrenBox.add(node);
                    }

                    insidePane.setContent(insideBox);
                    miniaturaPane.getChildren().add(insidePane);
                } else {
                    iconLabel.setText(centerPane.selectedItem.getIcon());
                    if (FILL_MINIATURA_LIKE_ICON) iconLabel.setFill(centerPane.selectedItem.getColor());
                    else iconLabel.setFill(UNKNOW_COLOR);
                    miniaturaPane.getChildren().add(iconLabel);
                }

            // Si es archivo
            } else if (SHOW_INSIDE_FILES && properties.getMimeType().startsWith("text")) {
                try (BufferedReader reader = new BufferedReader(new FileReader(centerPane.selectedItem.getFile()))) {
                    String lineText;
                    StringBuilder result = new StringBuilder();
                    while ((lineText = reader.readLine()) != null) {
                        result.append(lineText).append("\n");
                    }
                    result.deleteCharAt(result.length()-1);

                    textNode = new TextArea(result.toString());
                    textNode.setId("Right_miniatura_text");
                    textNode.setOnKeyPressed(e -> {
                        KeyCombination key = Scene.getKeyCombination(e);
                        if (key != null) for (KeyCombination keyCombination : SAVE_INSIDE) {
                            if (key.equals(keyCombination)) {
                                saveInside();
                                updateRight();
                                break;
                            }
                        }
                    });

                    insidePane.setContent(textNode);
                    miniaturaPane.getChildren().add(insidePane);
                } catch (Exception e) {
                    printError("Error al leer interior del archivo "+centerPane.selectedItem.getFile().getAbsolutePath(), e);
                }

            // Si es especial
            } else {
                iconLabel.setText(centerPane.selectedItem.getIcon());
                if (FILL_MINIATURA_LIKE_ICON) iconLabel.setFill(centerPane.selectedItem.getColor());
                else iconLabel.setFill(UNKNOW_COLOR);
                miniaturaPane.getChildren().add(iconLabel);
            }

            // Propiedades
            nameNode.value.setText(centerPane.selectedItem.getName());
            sizeNode.value.setText(properties.getSizeString());
            createDateTimeNode.value.setText(properties.getCreationString());
            modifiedDateTimeNode.value.setText(properties.getModifiedString());
            typeNode.value.setText(properties.getMimeType());
            permissionsNode.value.setText(
                    new String(properties.getOwnerPermissions()) +
                    new String(properties.getGroupPermissions()) +
                    new String(properties.getOtherPermissions())
            );
            ownerNode.value.setText(properties.getOwner());
            groupNode.value.setText(properties.getGroup());
        }
    }

    public static void saveInside() {
        if (textNode != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(centerPane.selectedItem.getFile()))) {
                printExecute("Guardando cambios en '"+YELLOW+centerPane.selectedItem.getName()+RESET+"'");
                String[] lines = textNode.getText().split("\n");
                writer.write(lines[0]);
                for (int i = 1; i < lines.length; i++) {
                    writer.newLine();
                    writer.write(lines[i]);
                }
            } catch (Exception ex) {
                printError("Error al guardar cambios en '"+centerPane.selectedItem.getName()+"'", ex);
            }
        }
    }

    public static void focusInside() {
        if (textNode != null) textNode.requestFocus();
    }
    public static void focusName() {
        nameNode.value.requestFocus();
        String extension = centerPane.selectedItem.getExtension();
        if (extension == null) nameNode.value.selectAll();
        else nameNode.value.selectRange(0, centerPane.selectedItem.getName().length()-centerPane.selectedItem.getExtension().length()-1);
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
        return  (textNode != null && textNode.isFocused()) ||
                (nameNode != null && nameNode.value.isFocused()) ||
                (sizeNode != null && sizeNode.value.isFocused()) ||
                (modifiedDateTimeNode != null && modifiedDateTimeNode.value.isFocused()) ||
                (createDateTimeNode != null && createDateTimeNode.value.isFocused()) ||
                (typeNode != null && typeNode.value.isFocused()) ||
                (permissionsNode != null && permissionsNode.value.isFocused()) ||
                (ownerNode != null && ownerNode.value.isFocused()) ||
                (groupNode != null && groupNode.value.isFocused());
    }
}
package panel;

import entity.FileProperties;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private static TextArea textNode;
    private static RightNode nameNode;
    private static RightNode permissionsNode;
    private static RightNode ownerNode;
    private static RightNode groupNode;
    private static RightNode sizeNode;
    private static RightNode modifiedDateTimeNode;
    private static RightNode createDateTimeNode;
    private static RightNode typeNode;

    private final VBox pane;
    private final double paneWidth;

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
        close.setOnAction(e -> changeShow(false));
        children.add(close);

        if (selectedItem != null) {
            FileProperties properties = selectedItem.getFileProperties();
            String extensionText = selectedItem.getExtension();

            // Miniatura
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

                children.add(createMiniatura(miniatura));

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
                        childrenBox.add(node);
                    }

                    children.add(createInside(insideBox));
                } else {
                    Text insideVoid = new Text("Vacio");
                    insideVoid.setId("Right_miniatura_void");

                    children.add(createMiniatura(insideVoid));
                }

            // Si es archivo
            } else if (SHOW_INSIDE_FILES && properties.getMimeType().startsWith("text")) {
                try (BufferedReader reader = new BufferedReader(new FileReader(selectedItem.getFile()))) {
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

                    children.add(createInside(textNode));
                } catch (Exception e) {
                    printError("Error al leer interior del archivo "+selectedItem.getFile().getAbsolutePath(), e);
                }

            // Si es especial
            } else {
                Text label = new Text(selectedItem.getIcon());
                label.setFont(nerdFont);
                label.setId("Right_miniatura");

                if (FILL_MINIATURA_LIKE_ICON) label.setFill(selectedItem.getColor());
                else label.setFill(UNKNOW_COLOR);

                children.add(createMiniatura(label));
            }

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

    public StackPane createMiniatura(Node node) {
        StackPane miniaturaPane = new StackPane(node);
        miniaturaPane.setMinSize(paneWidth, paneWidth);
        miniaturaPane.setMaxSize(paneWidth, paneWidth);
        return miniaturaPane;
    }
    public ScrollPane createInside(Node node) {
        ScrollPane insidePane = new ScrollPane(node);
        insidePane.setId("Right_miniatura_pane");
        insidePane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        insidePane.setHbarPolicy(ScrollBarPolicy.NEVER);
        insidePane.setFitToWidth(true);
        insidePane.setFitToHeight(true);
        insidePane.setMinSize(paneWidth, paneWidth);
        insidePane.setMaxSize(paneWidth, paneWidth);
        return insidePane;
    }
    public static void saveInside() {
        if (textNode != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedItem.getFile()))) {
                printExecute("Guardando cambios en '"+YELLOW+selectedItem.getName()+RESET+"'");
                String[] lines = textNode.getText().split("\n");
                writer.write(lines[0]);
                for (int i = 1; i < lines.length; i++) {
                    writer.newLine();
                    writer.write(lines[i]);
                }
            } catch (Exception ex) {
                printError("Error al guardar cambios en '"+selectedItem.getName()+"'", ex);
            }
        }
    }

    public static void focusInside() {
        if (textNode != null) textNode.requestFocus();
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
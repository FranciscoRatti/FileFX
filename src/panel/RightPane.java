package panel;

import entity.FileProperties;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
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
    private static StackPane miniaturaPane;
    private static Image image;
    private static ImageView miniatura;
    private static ScrollPane insidePane;
    private static Text iconLabel;
    private static TextArea textNode;

    private static RightNode nameNode;
    private static RightNode sizeNode;
    private static RightNode modifiedDateTimeNode;
    private static RightNode createDateTimeNode;
    private static RightNode typeNode;
    private static RightNode permissionsNode;
    private static RightNode ownerNode;
    private static RightNode groupNode;

    public static boolean isRightPaneShow;

    public RightPane() {
        setHbarPolicy(ScrollBarPolicy.NEVER);
        setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        setFitToWidth(true);
        setId("RightPane");
        setMaxWidth(RIGHT_WIDTH);

        VBox pane = new VBox();
        pane.setId("RightPane_pane");
        pane.setMaxWidth(RIGHT_WIDTH);
        ObservableList<Node> children = pane.getChildren();

        setContent(pane);

        Button close = new Button("x");
        close.setId("Right_close");
        close.setFocusTraversable(false);
        close.setOnAction(e -> changeShow(false));

        // Miniatura
        miniaturaPane = new StackPane();
        miniaturaPane.setMinSize(RIGHT_WIDTH, RIGHT_WIDTH);
        miniaturaPane.setMaxSize(RIGHT_WIDTH, RIGHT_WIDTH);

        miniatura = new ImageView();
        miniatura.setPreserveRatio(true);

        iconLabel = new Text();
        iconLabel.setFont(nerdFont);
        iconLabel.setId("Right_miniatura");

        insidePane = new ScrollPane();
        insidePane.setId("Right_miniatura_pane");
        insidePane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        insidePane.setHbarPolicy(ScrollBarPolicy.NEVER);
        insidePane.setFitToWidth(true);
        insidePane.setFitToHeight(true);
        insidePane.setMinSize(RIGHT_WIDTH, RIGHT_WIDTH);
        insidePane.setMaxSize(RIGHT_WIDTH, RIGHT_WIDTH);

        // Propiedades
        nameNode = new RightNode("Nombre :", !path.startsWith(Lib.TRASH_PATH +"files"));
        nameNode.value.focusedProperty().addListener((obs, before, now) -> {
            if (now) addFocused();
            else minusFocused();
        });
        nameNode.value.setOnKeyPressed(e -> {
            if (!centerPane.selectedItems.isEmpty() && e.getCode().equals(KeyCode.ENTER)) {
                renameFile(centerPane.selectionModel.getSelectedItem().getFileProperties(), nameNode.value.getText());
            }
        });

        sizeNode = new RightNode("Tamaño :", false);
        sizeNode.value.focusedProperty().addListener((obs, before, now) -> {
            if (now) addFocused();
            else minusFocused();
        });

        createDateTimeNode = new RightNode("Creado :", false);
        createDateTimeNode.value.focusedProperty().addListener((obs, before, now) -> {
            if (now) addFocused();
            else minusFocused();
        });

        modifiedDateTimeNode = new RightNode("Modificado :", false);
        modifiedDateTimeNode.value.focusedProperty().addListener((obs, before, now) -> {
            if (now) addFocused();
            else minusFocused();
        });

        typeNode = new RightNode("Tipo :", false);
        typeNode.value.focusedProperty().addListener((obs, before, now) -> {
            if (now) addFocused();
            else minusFocused();
        });

        permissionsNode = new RightNode("Permisos :", false);
        permissionsNode.value.focusedProperty().addListener((obs, before, now) -> {
            if (now) addFocused();
            else minusFocused();
        });
        permissionsNode.value.setOnMouseClicked(e -> permissionsStage.show());

        ownerNode = new RightNode("Usuario :", false);
        ownerNode.value.focusedProperty().addListener((obs, before, now) -> {
            if (now) addFocused();
            else minusFocused();
        });

        groupNode = new RightNode("Grupo   :", false);
        groupNode.value.focusedProperty().addListener((obs, before, now) -> {
            if (now) addFocused();
            else minusFocused();
        });

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

        if (centerPane.selectionModel.getSelectedItem() != null) {
            FileProperties properties = centerPane.selectionModel.getSelectedItem().getFileProperties();

            // Miniatura
            String extensionText = centerPane.selectionModel.getSelectedItem().getExtension();
            miniaturaPane.getChildren().clear();
            image = null;
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
                image = new Image("file://" + properties.getAbsolutePath());
                miniatura.setImage(image);

                if (image.getWidth() < image.getHeight()) miniatura.setFitHeight(RIGHT_WIDTH - 3);
                else miniatura.setFitWidth(RIGHT_WIDTH - 3);

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
                        CenterNode node = new CenterNode(new FileProperties(file), false);

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
                    iconLabel.setText(centerPane.selectionModel.getSelectedItem().getIcon());
                    if (FILL_MINIATURA_LIKE_ICON) iconLabel.setFill(centerPane.selectionModel.getSelectedItem().getColor());
                    else iconLabel.setFill(UNKNOW_COLOR);
                    miniaturaPane.getChildren().add(iconLabel);
                }

            // Si es archivo
            } else if (SHOW_INSIDE_FILES && properties.getMimeType().startsWith("text")) {
                try (BufferedReader reader = new BufferedReader(new FileReader(centerPane.selectionModel.getSelectedItem().getFileProperties()))) {
                    String lineText;
                    StringBuilder result = new StringBuilder();
                    while ((lineText = reader.readLine()) != null) {
                        result.append(lineText).append("\n");
                    }
                    result.deleteCharAt(result.length()-1);

                    textNode = new TextArea(result.toString());
                    textNode.setId("Right_miniatura_text");
                    textNode.setFocusTraversable(false);
                    textNode.focusedProperty().addListener((obs, before, now) -> {
                        if (now) addFocused();
                        else minusFocused();
                    });
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
                    printErrorAndShow("Error al leer interior del archivo "+centerPane.selectionModel.getSelectedItem().getFileProperties().getAbsolutePath(), e);
                }

            // Si es especial
            } else {
                iconLabel.setText(centerPane.selectionModel.getSelectedItem().getIcon());
                if (FILL_MINIATURA_LIKE_ICON) iconLabel.setFill(centerPane.selectionModel.getSelectedItem().getColor());
                else iconLabel.setFill(UNKNOW_COLOR);
                miniaturaPane.getChildren().add(iconLabel);
            }

            // Propiedades
            nameNode.value.setText(centerPane.selectionModel.getSelectedItem().getName());
            sizeNode.value.setText(properties.getSizeString());
            createDateTimeNode.value.setText(properties.getCreationString());
            modifiedDateTimeNode.value.setText(properties.getModifiedString());
            typeNode.value.setText(properties.getMimeType());
            permissionsNode.value.setText(properties.getPermissionsString());
            ownerNode.value.setText(properties.getOwner());
            groupNode.value.setText(properties.getGroup());
        }
    }

    public void setSize() {
        setMaxWidth(RIGHT_WIDTH);

        miniaturaPane.setMinSize(RIGHT_WIDTH, RIGHT_WIDTH);
        miniaturaPane.setMaxSize(RIGHT_WIDTH, RIGHT_WIDTH);
        insidePane.setMinSize(RIGHT_WIDTH, RIGHT_WIDTH);
        insidePane.setMaxSize(RIGHT_WIDTH, RIGHT_WIDTH);

        if (image != null) {
            if (image.getWidth() < image.getHeight()) miniatura.setFitHeight(RIGHT_WIDTH - 3);
            else miniatura.setFitWidth(RIGHT_WIDTH - 3);
        }
    }

    public static void saveInside() {
        if (textNode != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(centerPane.selectionModel.getSelectedItem().getFileProperties()))) {
                printExecute("Guardando cambios en '"+YELLOW+centerPane.selectionModel.getSelectedItem().getName()+RESET+"'");
                String[] lines = textNode.getText().split("\n");
                writer.write(lines[0]);
                for (int i = 1; i < lines.length; i++) {
                    writer.newLine();
                    writer.write(lines[i]);
                }
            } catch (Exception ex) {
                printErrorAndShow("Error al guardar cambios en '"+centerPane.selectionModel.getSelectedItem().getName()+"'", ex);
            }
        }
    }

    public static void focusInside() {
        if (textNode != null) textNode.requestFocus();
    }
    public static void focusName() {
        CenterNode selectedItem = centerPane.getSelectionModel().getSelectedItem();

        nameNode.value.requestFocus();
        String extension = selectedItem.getExtension();
        if (extension == null) nameNode.value.selectAll();
        else nameNode.value.selectRange(0, selectedItem.getName().length()-selectedItem.getExtension().length()-1);
    }

    public static void changeShow(boolean isRightPaneShow) {
        RightPane.isRightPaneShow=isRightPaneShow;
        if (isRightPaneShow) {
            mainPane.borderPane.setRight(rightPane);
        } else {
            mainPane.borderPane.setRight(null);
        }
    }
    public static void changeShow() {
        changeShow(!isRightPaneShow);
    }
}
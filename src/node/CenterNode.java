package node;

import entity.FileProperties;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.Lib;
import scene.Scene;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static main.FileFX.*;
import static main.Lib.RESET;
import static main.Lib.YELLOW;
import static main.Lib.printErrorAndShow;
import static main.Lib.printExecute;
import static main.Lib.printInfo;
import static main.Lib.updateCenter;
import static main.Lib.updateRight;
import static panel.MainPane.*;

public class CenterNode extends HBox {
    public static boolean dropInternally = false;

    private FileProperties fileProperties;
    private final boolean isDirectory;
    private final String name;
    private String extension;
    private String icon;
    private Color color;
    private double[] colorRGB;
    private int index;

    public Label nameLabel, iconLabel;
    public ArrayList<Label> columns;

    public CenterNode(FileProperties file, boolean selectable) {

        // Propiedades
        fileProperties = file;

        isDirectory = file.isDirectory();
        name = file.getName();
        extension = name.contains(".") && !isDirectory ? name.substring(name.lastIndexOf('.')+1) : null;

        String colorText;
        if (file.canRead()) {
            icon = iconsExtension.getProperty("."+extension);
            if (icon == null) icon = iconsMime.getProperty(fileProperties.getMimeType());
            if (icon == null) icon = iconsMime.getProperty("unknow");

            colorText = colorsExtension.getProperty("."+extension);
            if (colorText == null) colorText = colorsMime.getProperty(fileProperties.getMimeType());
            if (colorText == null) colorText = colorsMime.getProperty("unknow");
        } else {
            icon = iconsMime.getProperty("lock");
            colorText = colorsMime.getProperty("lock");
        }

        // Nodo
        nameLabel = new Label(name);
        nameLabel.setId("CenterNode_name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        setHgrow(nameLabel, Priority.ALWAYS);
        getChildren().add(nameLabel);

        // Icono
        setIcon(icon, Color.valueOf(colorText));
        colorRGB = new double[]{color.getRed()*255, color.getGreen()*255, color.getBlue()*255};
        setColor(nameLabel);

        // Eventos
        if (!selectable) return;

        // MouseClicked
        setOnMouseClicked(e -> {
            if (Scene.isAnyShowing()) return;

            MouseButton button = e.getButton();
            int clickCount = e.getClickCount();

            if (button.equals(MouseButton.PRIMARY)) {
                if (clickCount == 2) centerPane.openSelected();
            }
        });

        // Drag Origen
        setOnDragDetected(e -> {
            Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);

            ClipboardContent clipboard = new ClipboardContent();
            clipboard.putFiles(centerPane.selectedItems.stream().map(n -> (File) n.getFileProperties()).toList());

            dragboard.setContent(clipboard);
            e.consume();
        });

        setOnDragDone(e -> {
            System.out.println(dropInternally);

            if (dropInternally) {
                dropInternally = false;
            } else if (e.isAccepted()) {
                updateCenter();
                centerPane.selectFirst();
                updateRight();
            }
        });

        // Drag Destino
        setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                e.consume();
            }
        });

        setOnDragDropped(e -> {
            CenterNode.dropInternally = true;

            List<File> files = e.getDragboard().getFiles();
            String destination = isDirectory ? fileProperties.getAbsolutePath() : path;
            String[] paths = files.stream()
                    .map(f -> f.getAbsolutePath())
                    .filter(s -> !s.equals(destination))
                    .toArray(String[]::new);

            String[] comando = new String[paths.length+2];
            comando[0] = "mv";
            System.arraycopy(paths, 0, comando, 1, paths.length);
            comando[paths.length+1] = destination;

            try {
                printExecute("Moviendo archivos a '"+YELLOW+destination+RESET+"'");
                new ProcessBuilder(comando).start().waitFor();
            } catch (Exception ex) {
                printErrorAndShow("Error al mover los archivos a "+fileProperties.getAbsolutePath(), ex);
            }

            updateCenter();
            if (isDirectory) {
                if ((fileProperties.getAbsolutePath() + "/").equals(path))
                    centerPane.select(new ArrayList<>(files.stream().map(f -> f.getName()).toList()));
                else
                    centerPane.select(fileProperties.getName());
            } else
                centerPane.select(new ArrayList<>(files.stream().map(f -> f.getName()).toList()));
            updateRight();

            e.setDropCompleted(true);
            e.consume();
        });

        setOnDragEntered(e -> setFocused(true));
        setOnDragExited(e -> setFocused(false));
    }

    public CenterNode(String text) {
        isDirectory = false;
        name = text;

        // Color
        color = Color.valueOf(colorsMime.getProperty("unknow"));
        colorRGB = new double[]{color.getRed()*255, color.getGreen()*255, color.getBlue()*255};

        // Nodo
        nameLabel = new Label(name);
        nameLabel.setId("CenterNode_name");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        setHgrow(nameLabel, Priority.ALWAYS);
        getChildren().add(nameLabel);

        // Icono
        setColor(nameLabel);
    }

    public String getName() {return name;}
    public String getIcon() {return icon;}
    public Color getColor() {return color;}
    public FileProperties getFileProperties() {return fileProperties;}
    public String getExtension() {return extension;}
    public int getIndex() {return index;}

    public void setSelected(boolean selected) {
        String id = getId();
        if (selected) {
            printInfo("Se selecciono '" + Lib.BLUE + name + Lib.RESET + "'");
            centerPane.selectionModel.select(getIndex());

            setId((id.charAt(15) == '1') ? "CenterNode_boxB1-focus" : "CenterNode_boxB2-focus");
            nameLabel.setId("CenterNode_name-focus");
            iconLabel.setId("CenterNode_icon-focus");
            if (!columns.isEmpty())
                for (Label column : columns) column.setId("CenterNode_column-focus");

            nameLabel.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");
            iconLabel.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");
            if (!columns.isEmpty())
                for (Label column : columns) column.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");
        } else {
            centerPane.selectionModel.clearSelection(getIndex());

            setId((id.charAt(15) == '1') ? "CenterNode_boxB1" : "CenterNode_boxB2");
            nameLabel.setId("CenterNode_name");
            iconLabel.setId("CenterNode_icon");
            if (!columns.isEmpty())
                for (Label column : columns) column.setId("CenterNode_column");

            setColor(nameLabel);
            iconLabel.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
            if (!columns.isEmpty())
                for (Label column : columns) setColor(column);
        }
    }
    public void setIcon(String icon, Color color) {
        this.color = color;
        colorRGB = new double[]{color.getRed()*255, color.getGreen()*255, color.getBlue()*255};

        this.icon =icon;
        iconLabel = new Label(icon);
        iconLabel.setId("CenterNode_icon");
        iconLabel.setFont(nerdFont);
        iconLabel.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
        nameLabel.setGraphic(iconLabel);
    }
    public void setColor(Label label) {
        if (isDirectory) {
            if (FILL_TEXT_DIR_LIKE_ICON)
                label.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
            else
                label.setStyle("-fx-text-fill: rgb("+UNKNOW_COLOR_RGB[0]+","+UNKNOW_COLOR_RGB[1]+","+UNKNOW_COLOR_RGB[2]+");");
        } else {
            if (FILL_TEXT_FILE_LIKE_ICON)
                label.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
            else
                label.setStyle("-fx-text-fill: rgb("+UNKNOW_COLOR_RGB[0]+","+UNKNOW_COLOR_RGB[1]+","+UNKNOW_COLOR_RGB[2]+");");
        }
    }
    public void setIndex(int index) {
        this.index = index;
        setId((index % 2 == 0) ? "CenterNode_boxB1" : "CenterNode_boxB2");
    }

    public void addColumns() {
        columns = new ArrayList<>();
        if (COLUMNS != null) {
            for (Lib.COLUMNS column : COLUMNS) {
                switch (column) {
                    case Lib.COLUMNS.PERMISSIONS -> createColumn(
                            new String(fileProperties.getOwnerPermissions()) +
                                    new String(fileProperties.getGroupPermissions()) +
                                    new String(fileProperties.getOtherPermissions())
                    );
                    case Lib.COLUMNS.OWNER -> createColumn(fileProperties.getOwner());
                    case Lib.COLUMNS.GROUP -> createColumn(fileProperties.getGroup());
                    case Lib.COLUMNS.SIZE -> {
                        String sizeString = fileProperties.getSizeString();
                        int length = sizeString.length();
                        createColumn(
                                length == 1 ? "        "+sizeString :
                                        length == 2 ? "       "+sizeString :
                                        length == 3 ? "      "+sizeString :
                                        length == 4 ? "     "+sizeString :
                                        length == 5 ? "    "+sizeString :
                                        length == 6 ? "   "+sizeString :
                                        length == 7 ? "  "+sizeString :
                                        length == 8 ? " "+sizeString :
                                        sizeString
                        );
                    }
                    case Lib.COLUMNS.MODIFIED -> createColumn(fileProperties.getModifiedString());
                    case Lib.COLUMNS.CREATED -> createColumn(fileProperties.getCreationString());
                    case Lib.COLUMNS.TYPE -> createColumn(fileProperties.getMimeType());
                }
            }
        }
    }

    public void createColumn(String value) {
        Label column = new Label(value);
        column.setId("CenterNode_column");
        setColor(column);
        getChildren().add(column);
        columns.add(column);
    }
}
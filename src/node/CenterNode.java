package node;

import entity.FileProperties;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.Lib;

import java.io.File;
import java.util.ArrayList;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.MainPane.*;

public class CenterNode extends HBox {
    private FileProperties properties;
    private Boolean selected;
    private File file;
    private final boolean isDirectory;
    private final String fileName;
    private String extension;
    private String iconText;
    private Color color;
    private double[] colorRGB;
    private int index;

    private Label icon;
    public Label name;
    public ArrayList<Label> columns;

    public CenterNode(File file, boolean selectable) {

        // Propiedades
        properties = new FileProperties(file);
        selected = false;

        this.file = file;
        isDirectory = file.isDirectory();
        fileName = file.getName();
        extension = fileName.contains(".") && !isDirectory ? fileName.substring(fileName.lastIndexOf('.')+1) : null;

        String colorText;
        if (file.canRead()) {
            iconText = iconsExtension.getProperty("."+extension);
            if (iconText == null) iconText = iconsMime.getProperty(properties.getMimeType());
            if (iconText == null) iconText = iconsMime.getProperty("unknow");

            colorText = colorsExtension.getProperty("."+extension);
            if (colorText == null) colorText = colorsMime.getProperty(properties.getMimeType());
            if (colorText == null) colorText = colorsMime.getProperty("unknow");
        } else {
            iconText = iconsMime.getProperty("lock");
            colorText = colorsMime.getProperty("lock");
        }

        // Nodo
        name = new Label(fileName);
        name.setId("CenterNode_name");
        name.setMaxWidth(Double.MAX_VALUE);
        setHgrow(name, Priority.ALWAYS);
        getChildren().add(name);

        // Icono
        setIcon(iconText, Color.valueOf(colorText));
        colorRGB = new double[]{color.getRed()*255, color.getGreen()*255, color.getBlue()*255};
        setColor(name);

        // Evento
        if (selectable) setOnMouseReleased(e -> {
            MouseButton button = e.getButton();
            int clickCount = e.getClickCount();

            if (button.equals(MouseButton.PRIMARY)) {

                // Seleccionar
                if (clickCount == 1) {
                    if (!e.isControlDown() && !e.isShiftDown()) {
                        centerPane.deselectAll();

                    } else if (e.isShiftDown()) {
                        if (centerPane.selectedItem != null) {
                            boolean beSelected = false;
                            CenterNode lastSelectedItem = centerPane.selectedItem;

                            for (CenterNode centerNode : centerPane.centerNodes) {
                                if (beSelected) {
                                    if (centerNode.equals(this) || centerNode.equals(lastSelectedItem)) {
                                        break;
                                    } else {
                                        centerNode.setSelected(true);
                                    }
                                } else if (centerNode.equals(this) || centerNode.equals(lastSelectedItem)) {
                                    beSelected = true;
                                }
                            }
                        }
                    }

                    if (!selected) {
                        setSelected(true);
                    } else {
                        setSelected(false);
                        centerPane.selectedItems.remove(this);
                        if (centerPane.selectedItem == this) {
                            if (centerPane.selectedItems.isEmpty()) centerPane.selectedItem = null;
                            else centerPane.selectedItem = centerPane.selectedItems.getFirst();
                        }
                        if (centerPane.selectedItems.isEmpty()) centerPane.selectThis();
                    }
                    updateRight();
                } else if (clickCount == 2) {
                    centerPane.openSelected();
                }
            }
        });
    }

    public CenterNode(String text) {
        selected = false;
        isDirectory = false;
        fileName = text;

        // Color
        color = Color.valueOf(colorsMime.getProperty("unknow"));
        colorRGB = new double[]{color.getRed()*255, color.getGreen()*255, color.getBlue()*255};

        // Nodo
        name = new Label(fileName);
        name.setId("CenterNode_name");
        name.setMaxWidth(Double.MAX_VALUE);
        setHgrow(name, Priority.ALWAYS);
        getChildren().add(name);

        // Icono
        setColor(name);
    }

    public File getFile() {return file;}
    public String getName() {return fileName;}
    public String getIcon() {return iconText;}
    public Color getColor() {return color;}
    public FileProperties getFileProperties() {return properties;}
    public String getExtension() {return extension;}
    public int getIndex() {return index;}

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            String id = getId();
            if (selected) {
                printInfo("Se selecciono '" + Lib.BLUE + fileName + Lib.RESET + "'");
                setId((id.charAt(15) == '1') ? "CenterNode_boxB1-focus" : "CenterNode_boxB2-focus");
                name.setId("CenterNode_name-focus");
                icon.setId("CenterNode_icon-focus");
                if (!columns.isEmpty()) for (Label column : columns)
                    column.setId("CenterNode_column-focus");

                name.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");
                icon.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");
                if (!columns.isEmpty()) for (Label column : columns)
                        column.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");

                centerPane.selectedItem = this;
                centerPane.selectedItems.add(this);
            } else {
                setId((id.charAt(15) == '1') ? "CenterNode_boxB1" : "CenterNode_boxB2");
                name.setId("CenterNode_name");
                icon.setId("CenterNode_icon");
                if (!columns.isEmpty()) for (Label column : columns)
                    column.setId("CenterNode_column");

                setColor(name);
                icon.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
                if (!columns.isEmpty()) for (Label column : columns)
                    setColor(column);
            }
        }
    }
    public void setIcon(String icon, Color color) {
        this.color = color;
        colorRGB = new double[]{color.getRed()*255, color.getGreen()*255, color.getBlue()*255};

        this.iconText =icon;
        this.icon = new Label(icon);
        this.icon.setId("CenterNode_icon");
        this.icon.setFont(nerdFont);
        this.icon.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
        name.setGraphic(this.icon);
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
                            new String(properties.getOwnerPermissions()) +
                                    new String(properties.getGroupPermissions()) +
                                    new String(properties.getOtherPermissions())
                    );
                    case Lib.COLUMNS.OWNER -> createColumn(properties.getOwner());
                    case Lib.COLUMNS.GROUP -> createColumn(properties.getGroup());
                    case Lib.COLUMNS.SIZE -> {
                        String sizeString = properties.getSizeString();
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
                    case Lib.COLUMNS.MODIFIED -> createColumn(properties.getModifiedString());
                    case Lib.COLUMNS.CREATED -> createColumn(properties.getCreationString());
                    case Lib.COLUMNS.TYPE -> createColumn(properties.getMimeType());
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
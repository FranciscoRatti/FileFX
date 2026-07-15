package node;

import entity.FileProperties;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import main.Lib;
import panel.MainPane;

import java.io.File;
import java.util.ArrayList;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.CenterPane.*;
import static panel.MainPane.*;

public class CenterNode extends HBox {
    private final FileProperties propertie;
    private Boolean selected;
    private final File file;
    private final boolean isDirectory;
    private final String fileName;
    private String extension = "";
    private String icon;
    private Label iconLabel;
    private Color color;
    private double[] colorRGB;
    private int index;
    
    public Label label;
    public ArrayList<Label> columns;

    public CenterNode(File file) {

        // Propiedades
        propertie = new FileProperties(file);
        selected = false;

        this.file = file;
        isDirectory = file.isDirectory();
        fileName = file.getName();
        extension = fileName.contains(".") && !isDirectory ? fileName.substring(fileName.lastIndexOf('.')+1) : null;

        String colorText;
        if (file.canRead()) {
            icon = iconsExtension.getProperty("."+extension);
            if (icon == null) icon = iconsMyme.getProperty(propertie.getMimeType());
            if (icon == null) icon = iconsMyme.getProperty("unknow");

            colorText = colorsExtension.getProperty("."+extension);
            if (colorText == null) colorText = colorsMyme.getProperty(propertie.getMimeType());
            if (colorText == null) colorText = colorsMyme.getProperty("unknow");
        } else {
            icon = iconsMyme.getProperty("lock");
            colorText = colorsMyme.getProperty("lock");
        }

        color = Color.valueOf(colorText);
        colorRGB = new double[]{color.getRed()*255, color.getGreen()*255, color.getBlue()*255};

        // Nodo
        label = new Label(fileName);
        label.setId("center_label");
        label.setMaxWidth(Double.MAX_VALUE);
        setHgrow(label, Priority.ALWAYS);
        getChildren().add(label);

        columns = new ArrayList<>();

        setIcon(icon, color);
        setColor(label);

        setOnMouseReleased(e -> {
            MouseButton button = e.getButton();
            int clickCount = e.getClickCount();

            if (button.equals(MouseButton.PRIMARY)) {

                // Seleccionar
                if (clickCount == 1) {
                    if (!e.isControlDown() && !e.isShiftDown()) {
                        deselectAll();

                    } else if (e.isShiftDown()) {
                        if (selectedItem != null) {
                            boolean beSelected = false;
                            CenterNode lastSelectedItem = selectedItem;

                            for (CenterNode centerNode : centerNodes) {
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

                    setSelected(true);

                    updateRight();
                } else if (clickCount == 2) {
                    openSelected();
                }
            }
        });
    }

    public File getFile() {return file;}
    public String getName() {return fileName;}
    public String getIcon() {return icon;}
    public Color getColor() {return color;}
    public FileProperties getPropertie() {return propertie;}
    public String getExtension() {return extension;}
    public int getIndex() {return index;}

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            String id = getId();
            if (selected) {
                printInfo("Se selecciono '" + Lib.BLUE + fileName + Lib.RESET + "'");
                setId((id.charAt(id.length() - 1) == '1') ? "center_box_focus_b1" : "center_box_focus_b2");
                label.setId("center_label_focus");
                iconLabel.setId("center_icon_focus");
                if (!columns.isEmpty()) for (Label column : columns)
                    column.setId("center_column_focus");

                label.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");
                iconLabel.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");
                if (!columns.isEmpty()) for (Label column : columns)
                        column.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");

                MainPane.selectedItem = this;
                MainPane.selectedItems.add(this);
            } else {
                setId((id.charAt(id.length() - 1) == '1') ? "center_box_b1" : "center_box_b2");
                label.setId("center_label");
                iconLabel.setId("center_icon");
                if (!columns.isEmpty()) for (Label column : columns)
                    column.setId("center_column");

                setColor(label);
                iconLabel.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
                if (!columns.isEmpty()) for (Label column : columns)
                    setColor(column);
            }
        }
    }
    public void setIcon(String icon, Color color) {
        this.color = color;
        colorRGB = new double[]{color.getRed()*255, color.getGreen()*255, color.getBlue()*255};

        this.icon=icon;
        iconLabel = new Label(icon);
        iconLabel.setId("center_icon");
        iconLabel.setFont(nerdFont);
        iconLabel.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
        label.setGraphic(iconLabel);
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
      setId((index % 2 == 0) ? "center_box_b1" : "center_box_b2");
    }

    public void createColumn(String value) {
        Label column = new Label(value);
        column.setId("center_column");
        setColor(column);
        getChildren().add(column);
        columns.add(column);
    }
}
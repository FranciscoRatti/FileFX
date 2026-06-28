package node;

import entity.FileProperties;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import main.Lib;
import panel.MainPane;

import java.io.File;

import static main.FileFX.*;
import static main.Lib.printInfo;
import static main.Lib.updateRight;
import static panel.CenterPane.centerNodes;
import static panel.CenterPane.openSelected;
import static panel.MainPane.deselectAll;
import static panel.MainPane.selectedItem;

public class CenterNode extends Label {
    private FileProperties propertie;
    private Boolean selected;
    private File file;
    private boolean isDirectory;
    private String fileName;
    private String extension = "";
    private String icon;
    private Label iconLabel;
    private Color color;
    private double[] colorRGB;
    private int index;

    public CenterNode(File file) {
        setMaxWidth(Double.MAX_VALUE);

        propertie = new FileProperties(file);
        selected = false;

        this.file = file;
        isDirectory = file.isDirectory();
        fileName = file.getName();
        setText(fileName);
        extension =  fileName.contains(".") && !isDirectory ? fileName.substring(fileName.lastIndexOf('.')+1) : null;

        String colorText;
        if (file.canRead()) {
            icon = iconsExtension.getProperty("." + extension);
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

        setIcon(icon, color);
        setColor();

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
                setId((id.charAt(id.length() - 1) == '1') ? "center_label_focus_b1" : "center_label_focus_b2");

                MainPane.selectedItem = this;
                MainPane.selectedItems.add(this);

                iconLabel.setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");
                setStyle("-fx-text-fill: rgb("+FOCUS_COLOR.getRed()*255+","+FOCUS_COLOR.getGreen()*255+","+FOCUS_COLOR.getBlue()*255+");");
            } else {
                setId((id.charAt(id.length() - 1) == '1') ? "center_label_b1" : "center_label_b2");

                iconLabel.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
                setColor();
            }
        }
    }
    public void setIcon(String icon, Color color) {
        this.color = color;
        colorRGB = new double[]{color.getRed()*255, color.getGreen()*255, color.getBlue()*255};

        this.icon=icon;
        iconLabel = new Label(icon);
        iconLabel.setFont(nerdFont);
        iconLabel.setId("center_icon");
        iconLabel.setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
        setGraphic(iconLabel);
    }
    public void setColor() {
        if (isDirectory) {
            if (Boolean.parseBoolean(config.getProperty("fill_text_dir_like_icon")))
                setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
            else
                setStyle("-fx-text-fill: rgb("+UNKNOW_COLOR_RGB[0]+","+UNKNOW_COLOR_RGB[1]+","+UNKNOW_COLOR_RGB[2]+");");
        } else {
            if (Boolean.parseBoolean(config.getProperty("fill_text_file_like_icon")))
                setStyle("-fx-text-fill: rgb("+colorRGB[0]+","+colorRGB[1]+","+colorRGB[2]+");");
            else
                setStyle("-fx-text-fill: rgb("+UNKNOW_COLOR_RGB[0]+","+UNKNOW_COLOR_RGB[1]+","+UNKNOW_COLOR_RGB[2]+");");
        }
    }
    public void setIndex(int index) {
      this.index = index;
      setId((index % 2 == 0) ? "center_label_b1" : "center_label_b2");
    }
}
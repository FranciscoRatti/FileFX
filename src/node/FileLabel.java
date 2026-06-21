package node;

import entity.FileProperties;
import javafx.scene.control.Label;
import panel.MainPane;

import java.io.File;

import main.Lib;
import static main.Lib.*;
import static main.FileFX.*;

public class FileLabel extends Label {
    private File file;
    private String fileName;
    private String icon;
    private FileProperties propertie;
    private Boolean selected;
    private String extension = "";
    private int index;

    public FileLabel(File file) {
        this.file = file;
        fileName = file.getName();
        setText(fileName);
        extension =  fileName.contains(".") && !file.isDirectory() ? fileName.substring(fileName.lastIndexOf('.')+1) : null;

        propertie = new FileProperties(file);
        selected = false;
        setMaxWidth(Double.MAX_VALUE);

        icon = null;
        if (icon != null) icon = iconsExtension.getProperty("."+extension);
        if (icon == null) icon = iconsMyme.getProperty(propertie.getMimeType());
        if (icon == null) icon = iconsMyme.getProperty("unknow");

        Label label = new Label(icon);
        label.setFont(nerdFont);
        label.setId("center_icon");

        setGraphic(label);
    }

    public File getFile() {
      return file;
    }
    public String getName() {
      return fileName;
    }
    public String getIcon() {return icon;}
    public FileProperties getPropertie() {
      return propertie;
    }
    public String getExtension() {
      return extension;
    }
    public int getIndex() {
        return index;
    }

    public void setSelected(boolean selected) {
        if (this.selected != selected) {
            this.selected = selected;
            String id = getId();
            if (selected) {
                printInfo("Se selecciono '" + Lib.BLUE + fileName + Lib.RESET + "'");
                setId((id.charAt(id.length() - 1) == '1') ? "center_label_focus_b1" : "center_label_focus_b2");

                MainPane.selectedItem = this;
                MainPane.selectedItems.add(this);
            } else {
                setId((id.charAt(id.length() - 1) == '1') ? "center_label_b1" : "center_label_b2");
            }
        }
    }
    public void setIndex(int index) {
      this.index = index;
      setId((index % 2 == 0) ? "center_label_b1" : "center_label_b2");
    }
}

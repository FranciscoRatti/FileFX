package node;

import entity.FileProperties;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import panel.CenterPane;
import panel.MainPane;

import java.io.File;
import java.io.IOException;

import main.Main;
import main.Lib;

public class FileLabel extends Label {
    private File file;
    private String fileName;
    private Boolean selected;
    private String extension = "";
    private int index;
    
    public FileLabel(File file) {
        this.file=file;
        fileName = file.getName();
        selected = false;
        setText(fileName);

        setMaxWidth(Double.MAX_VALUE);
        
        // Si es directorio
        if (file.isDirectory()) {
            setGraphic(new ImageView("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/center/directory/dir.png"));
            
        // Si es archivo
        } else {
            int i = fileName.lastIndexOf('.');
            if (i < fileName.length()-1) {
                extension = fileName.substring(i + 1);
            }

            ImageView icon;

            if (extension.equals("")) setGraphic(icon = new ImageView("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/center/file/not_format.png"));
            else if (Boolean.parseBoolean(Main.config.getProperty("show_miniatura")) && (
                    extension.equals("bmp") ||
                    extension.equals("gif") ||
                    extension.equals("jpeg") ||
                    extension.equals("jpg") ||
                    extension.equals("png")
            )) {
                setGraphic(icon = new ImageView("file://"+file.getAbsolutePath()));
            } else if (new File(Lib.ABSOLUTE_PATH+"share/filefx/icons/center/file/"+extension+".png").exists()) {
                setGraphic(icon = new ImageView("file://" + Lib.ABSOLUTE_PATH + "share/filefx/icons/center/file/" + extension + ".png"));
            } else setGraphic(icon = new ImageView("file://"+Lib.ABSOLUTE_PATH+"share/filefx/icons/center/file/unknow.png"));

            icon.setFitHeight(20);
            icon.setPreserveRatio(true);
            icon.setSmooth(true);
        }
    }

    public void setName(String fileName) {this.fileName=fileName;}
    public String getName() {return fileName;}

    public void setSelected(boolean selected) {
        this.selected = selected;
        String id = getId();
        if (selected) {
            Lib.printInfo("Se selecciono '"+Lib.BLUE+fileName+Lib.RESET+"'");
            setId((id.charAt(id.length() - 1) == '1') ? "center_label_focus_b1" : "center_label_focus_b2");

            MainPane.selectedItem = this;
            MainPane.selectedItems.add(this);
            MainPane.selectedFile = new FileProperties(file);
        } else {
            setId((id.charAt(id.length() - 1) == '1') ? "center_label_b1" : "center_label_b2");
        }
    }
    public boolean isSelected() {return selected;}

    public void setIndex(int index) {
        this.index=index;
        setId((index%2==0) ? "center_label_b1" : "center_label_b2");
    }
    public int getIndex() {return index;}

    public File getFile() {return file;}
    public String getExtension() {return extension;}
}
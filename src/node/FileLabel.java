package node;

import entity.FileProperties;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import panel.MainPane;

import java.io.File;
import java.util.Enumeration;

import main.Main;
import main.Lib;

import static main.Lib.ABSOLUTE_PATH;
import static main.Main.iconsBinding;

public class FileLabel extends Label {
  private File file;
  private String fileName;
  private FileProperties propertie;
  private Boolean selected;
  private String extension = "";
  private int index;

  public FileLabel(File file) {
    this.file = file;
    fileName = file.getName();
    setText(fileName);
    propertie = new FileProperties(file);
    selected = false;
    setMaxWidth(Double.MAX_VALUE);

    String mimeType = propertie.getMimeType();
    String icon;
    if (mimeType.contains("no read permission"))
      icon = iconsBinding.getProperty("lock");
    else
      icon = mimeType == null ? null : iconsBinding.getProperty(mimeType);

    Label label = new Label();
    label.setFont(Font.loadFont("file://" + ABSOLUTE_PATH + "share/filefx/0xProtoNerdFontMono-Regular.ttf", 16));
    label.setPadding(new Insets(0, 5, 0, 5));

    if (icon != null)
      label.setText(icon);
    else
      label.setText(iconsBinding.getProperty("unknow"));

    setGraphic(label);
  }

  public File getFile() {
    return file;
  }

  public String getName() {
    return fileName;
  }

  public FileProperties getPropertie() {
    return propertie;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
    String id = getId();
    if (selected) {
      Lib.printInfo("Se selecciono '" + Lib.BLUE + fileName + Lib.RESET + "'");
      setId((id.charAt(id.length() - 1) == '1') ? "center_label_focus_b1" : "center_label_focus_b2");

      MainPane.selectedItem = this;
      MainPane.selectedItems.add(this);
    } else {
      setId((id.charAt(id.length() - 1) == '1') ? "center_label_b1" : "center_label_b2");
    }
  }

  public boolean isSelected() {
    return selected;
  }

  public String getExtension() {
    return extension;
  }

  public void setIndex(int index) {
    this.index = index;
    setId((index % 2 == 0) ? "center_label_b1" : "center_label_b2");
  }

  public int getIndex() {
    return index;
  }
}

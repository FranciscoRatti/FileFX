package panel;

import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import node.PlaceLabel;

import main.Main;
import main.Lib;

public class LeftPane extends VBox {
    private boolean showHome;
    private boolean showDevices;
    private VBox home;
    private VBox devices;

    public LeftPane() {
        super(0);

        showHome = Boolean.parseBoolean(Main.config.getProperty("show_places"));
        showDevices = Boolean.parseBoolean(Main.config.getProperty("show_devices"));
        setId("left_pane");

        update();
    }

    public void update() {
        Lib.printInfo("Actualizando panel izquierdo");
        ObservableList<Node> childrens = getChildren();
        childrens.clear();

        if (showHome) {
            if (home == null) {
                home = new VBox();

                Label title = new Label("Lugares");
                title.setId("left_label_title");

                PlaceLabel homeDirectory = new PlaceLabel("Home", "home", Lib.HOME);
                PlaceLabel desktopDirectory = new PlaceLabel("Escritorio", "desktop", Lib.HOME + "/Desktop");
                PlaceLabel downloadDirectory = new PlaceLabel("Descargas", "download", Lib.HOME + "/Downloads");
                PlaceLabel documentsDirectory = new PlaceLabel("Documentos", "documents", Lib.HOME + "/Documents");
                PlaceLabel.PlaceMenu mediaDirectory = new PlaceLabel.PlaceMenu("Media      ▶", "media",
                        new PlaceLabel("Imagenes", "image", Lib.HOME + "/Images"),
                        new PlaceLabel("Videos", "video", Lib.HOME + "/Videos"),
                        new PlaceLabel("Plantillas", "template", Lib.HOME + "/Templates")
                );

                home.getChildren().addAll(title, homeDirectory, desktopDirectory, downloadDirectory, documentsDirectory, mediaDirectory,
                        new node.Separator(10, Orientation.HORIZONTAL));
            }
            childrens.add(home);
        }

        if (showDevices) {
            if (devices == null) {
                devices = new VBox();

                Label title = new Label("Dispositivos");
                title.setId("left_label_title");

                Label rootDirectory = new PlaceLabel("Raiz", "root", "/");

                devices.getChildren().addAll(title, rootDirectory,
                        new node.Separator(20, Orientation.HORIZONTAL));
            }
            childrens.add(devices);
        }
    }

    public void setShowHome(boolean showHome) {
        this.showHome=showHome;
        update();
    }
    public void setShowDevices(boolean showDevices) {
        this.showDevices=showDevices;
        update();
    }
}

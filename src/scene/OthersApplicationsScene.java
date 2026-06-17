package scene;

import entity.DesktopApplication;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import main.Main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static main.Lib.*;
import static main.Main.desktopApplications;
import static main.Main.isApplicationsSucceded;
import static panel.MainPane.selectedItem;
import static panel.MainPane.selectedItems;

public class OthersApplicationsScene extends Scene {
    public static VBox pane;
    public OthersApplicationsScene() {
        super(new ScrollPane(pane = new VBox(2)), 200, 400);
        getStylesheets().add("file://"+CONFIG_PATH+"theme.css");

        // Cargar applicaciones
        ArrayList<File> desktopFiles = new ArrayList<>();

        String env = System.getenv("XDG_DATA_HOME");
        if (env != null) {
            File[] files = new File(env+"/applications").listFiles();
            if (files != null) desktopFiles.addAll(Arrays.asList(files));
        }

        env = System.getenv("XDG_DATA_DIRS");
        if (env != null) {
            for (String dir : env.split(":")) {
                File[] files = new File(dir+"/applications").listFiles();
                if (files != null) desktopFiles.addAll(Arrays.asList(files));
            }
        }

        // Hilo
        Task<Void> task = new Task<Void>() {
            protected Void call() throws Exception {
                desktopApplications = new ArrayList<>();

                for (File file : desktopFiles) {
                    if (file.isDirectory()) {
                        for (File childrenFile : file.listFiles()) {
                            DesktopApplication app = new DesktopApplication(childrenFile);
                            if (app.hasParameter() && app.isDisplay() && app.getMimeTypes() != null) {
                                desktopApplications.add(app);
                            }
                        }
                    } else {
                        DesktopApplication app = new DesktopApplication(file);
                        if (app.hasParameter() && app.isDisplay() && app.getMimeTypes() != null) {
                            desktopApplications.add(app);
                        }
                    }
                }

                desktopApplications.sort(Comparator.comparing(DesktopApplication::getName, String.CASE_INSENSITIVE_ORDER));

                // Crear botones
                ObservableList<Node> childrens = pane.getChildren();
                for (DesktopApplication app : desktopApplications) {
                    ImageView icon = new ImageView(app.getIcon());
                    icon.setPreserveRatio(true);
                    icon.setFitHeight(24);

                    Button button = new Button(app.getName(), icon);
                    button.setId("other_button");
                    button.setOnAction(e -> {
                        if (selectedItems != null && !selectedItems.isEmpty()) {
                            Main.othersApplicationsStage.close();
                            app.openWith(selectedItem);
                        }
                    });
                    childrens.add(button);
                }
                isApplicationsSucceded = true;
                printOk("Applicaciones para abrir con cargadas con exito");
                return null;
            }
        };
        new Thread(task).start();
    }
}

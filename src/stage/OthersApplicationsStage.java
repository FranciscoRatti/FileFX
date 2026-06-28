package stage;

import entity.DesktopApplication;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.FileFX;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import static main.FileFX.desktopApplications;
import static main.Lib.CONFIG_PATH;
import static main.Lib.printOk;
import static panel.MainPane.selectedItem;
import static panel.MainPane.selectedItems;

public class OthersApplicationsStage extends Stage {
    public static VBox pane;
    public static Scene scene;

    public OthersApplicationsStage() {
        setTitle("Abrir con");

        pane = new VBox();
        pane.setId("other_pane");

        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        StackPane mainPane = new StackPane(scrollPane);
        mainPane.setId("main_pane");

        scene = new Scene(mainPane);
        scene.getStylesheets().add("file://"+CONFIG_PATH+"theme.css");
        setScene(scene);

        // Hilo
        Task<Void> task = new Task<Void>() {
            protected Void call() throws Exception {

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
                    button.setMaxWidth(Double.MAX_VALUE);
                    button.setId("other_button");
                    button.setOnAction(e -> {
                        if (selectedItems != null && !selectedItems.isEmpty()) {
                            FileFX.othersApplicationsStage.close();
                            app.openWith(selectedItem);
                        }
                    });
                    childrens.add(button);
                }

                printOk("Applicaciones para abrir con cargadas con exito");
                return null;
            }
        };
        new Thread(task).start();

        setOnShown(e -> {
            Platform.runLater(() -> {
                double width = pane.getWidth()+17.0;
                setMaxWidth(width);
                setWidth(width);

                setHeight(500);
                setMaxHeight(pane.getHeight());

                centerOnScreen();
            });
        });
    }
}

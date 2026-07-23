package stage;

import entity.DesktopApplication;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import main.FileFX;

import java.io.File;
import java.util.*;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.MainPane.*;

public class OthersApplicationsStage extends Stage {
    public OthersApplicationsStage() {
        setTitle("Abrir con");

        VBox pane = new VBox();
        pane.setId("OtherPane");

        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        StackPane mainPane = new StackPane(scrollPane);
        mainPane.setId("MainPane");

        Scene scene = new Scene(mainPane);
        scene.getStylesheets().add("file://"+CONFIG_PATH+"theme.css");
        setScene(scene);

        // Hilo
        Task<Void> task = new Task<>() {
            protected Void call() {
                lock.lock();

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
                        for (File childrenFile : Objects.requireNonNull(file.listFiles())) {
                            DesktopApplication app = new DesktopApplication(childrenFile);
                            if (app.hasParameter() && app.getMimeTypes() != null) {
                                desktopApplications.add(app);
                            }
                        }
                    } else {
                        DesktopApplication app = new DesktopApplication(file);
                        if (app.hasParameter() && app.getMimeTypes() != null) {
                            desktopApplications.add(app);
                        }
                    }
                }

                desktopApplications.sort(Comparator.comparing(DesktopApplication::getName, String.CASE_INSENSITIVE_ORDER));

                // Crear botones
                ObservableList<Node> children = pane.getChildren();
                for (DesktopApplication app : desktopApplications) {
                    ImageView icon = new ImageView(app.getIcon());
                    icon.setPreserveRatio(true);
                    icon.setFitHeight(24);

                    Button button = new Button(app.getName(), icon);
                    button.setMaxWidth(Double.MAX_VALUE);
                    button.setId("OtherNode");
                    button.setOnAction(e -> {
                        if (centerPane.selectedItems != null && !centerPane.selectedItems.isEmpty()) {
                            FileFX.othersApplicationsStage.close();
                            app.openWith(centerPane.selectedItem);
                        }
                    });
                    children.add(button);
                }

                printOk("Applicaciones para abrir con cargadas con exito");
                lock.unlock();
                return null;
            }
        };
        new Thread(task).start();

        setOnShown(e -> Platform.runLater(() -> {
            double width = pane.getWidth()+17.0;
            setMaxWidth(width);
            setWidth(width);

            setHeight(500);
            setMaxHeight(pane.getHeight());

            Platform.runLater(() -> centerOnScreen());
        }));
    }
}

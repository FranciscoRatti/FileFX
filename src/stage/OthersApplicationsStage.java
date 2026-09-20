package stage;

import entity.DesktopApplication;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import static main.Lib.*;
import static panel.MainPane.centerPane;

public class OthersApplicationsStage extends Stage {
    public final ArrayList<DesktopApplication> desktopApplications;
    private final ArrayList<Button> desktopButtons;

    public OthersApplicationsStage() {
        super("Abrir con");

        VBox pane = new VBox();
        pane.setId("OtherStage_pane");

        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

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
        desktopButtons = new ArrayList<>();

        // Hilo
        Task<Void> task = new Task<>() {
            protected Void call() {
                lock.lock();

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
                        if (!centerPane.selectedItems.isEmpty()) {
                            othersApplicationsStage.close();
                            app.openWith(centerPane.selectionModel.getSelectedItem());
                        }
                    });
                    desktopButtons.add(button);
                }
                children.addAll(desktopButtons);

                printOk("Applicaciones para abrir con cargadas con exito");
                lock.unlock();
                return null;
            }
        };
        new Thread(task).start();

        getChildren().add(scrollPane);
        StackPane.setMargin(this, new Insets(12, 0, 10, 0));
    }

    public void afterShow() {
        desktopButtons.getFirst().requestFocus();
    }
    public void afterClose() {}
}
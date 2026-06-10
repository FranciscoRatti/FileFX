package main;

import entity.DesktopApplication;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import panel.MainPane;
import scene.OthersApplicationsScene;
import scene.Scene;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

import static main.Lib.*;

public class Main extends javafx.application.Application {
    public static Properties config;
    public static boolean DISABLE_MENU_ITEM;
    public static ArrayList<DesktopApplication> desktopApplications;
    public static Stage othersApplicationsStage;

    public static MainPane mainPane;
    public static Scene normalScene;
    public static Stage stage;

    public static String path = "";

    public static void main(String[] args) {
        if (args.length > 0) path = args[0];
        launch(args);
    }

    @Override
    public void start(Stage s) {
        printInfo("Cargando archivo de propiedades");
        try (FileInputStream fileInputStream = new FileInputStream(ABSOLUTE_PATH+"share/filefx/config.properties")) {
        //try (FileInputStream fileInputStream = new FileInputStream(HOME+"/.config/filefx/config.properties")) {
            config = new Properties();
            config.load(fileInputStream);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de propiedades", e);
            System.exit(0);
        }

        DISABLE_MENU_ITEM = Boolean.parseBoolean(config.getProperty("disable_menu_item"));

        printInfo("Cargando path inicial");
        if (path.equals("")) {
            String initDirectory = config.getProperty("init_directory");
            if (initDirectory.charAt(0) == '~') {
                path = HOME+(initDirectory.substring(1));
            } else {
                path = initDirectory;
            }
        }
        printInfo("Path inicial: '"+BLUE+path+RESET+"'");

        printInfo("Cargando portapapeles");
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        printInfo("Cargando panel principal");
        mainPane = new MainPane();
        updateAll();

        printInfo("Cargando escena principal");
        normalScene = new Scene();

        printInfo("Cargando escenario principal");
        stage=s;
        stage.getIcons().add(new Image("file://"+ABSOLUTE_PATH+"share/filefx/icons/icon.png"));

        stage.setScene(normalScene);
        printInfo("Mostrando escenario");
        stage.showAndWait();
        printOk("Aplicacion iniciada con exito");

        Platform.runLater(() -> {
            printInfo("Cargando applicaciones para abrir con");
            othersApplicationsStage = new Stage();
            othersApplicationsStage.setScene(new OthersApplicationsScene());
        });
    }
}

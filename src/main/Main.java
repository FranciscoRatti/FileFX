package main;

import javafx.scene.image.Image;
import javafx.stage.Stage;
import panel.MainPane;
import scene.Scene;

import java.awt.*;
import java.io.*;
import java.util.Properties;

import static main.Lib.*;

public class Main extends javafx.application.Application {
    public static Properties config;
    public static boolean DISABLE_MENU_ITEM;

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
            String[] directories = config.getProperty("init_directory").split("/");
            for (String value: directories) {
                if (value != null) {
                    if (value.charAt(0) == '$') {
                        path += System.getenv(value.substring(1));
                    } else {
                        path += "/"+value;
                    }
                }
            }
        }
        printInfo("Path inicial: '"+BLUE+path+RESET+"'");

        printInfo("Cargando portapapeles");
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        printInfo("Cargando scene.normal.panel principal");
        mainPane = new MainPane();
        updateAll();

        printInfo("Cargando escena principal");
        normalScene = new Scene();

        printInfo("Cargando escenario principal");
        stage=s;
        stage.getIcons().add(new Image("file://"+ABSOLUTE_PATH+"share/filefx/icons/icon.png"));

        stage.setScene(normalScene);
        printInfo("Mostrando escenario");
        stage.show();
        printOk("Aplicacion iniciada con exito");
    }
}

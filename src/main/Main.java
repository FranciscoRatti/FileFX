package main;

import entity.DesktopApplication;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import panel.MainPane;
import scene.OthersApplicationsScene;
import scene.Scene;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Properties;

import static main.Lib.*;

public class Main extends javafx.application.Application {
    public static Properties config;
    public static Properties keyBinding;

    public static ArrayList<DesktopApplication> desktopApplications;
    public static Stage othersApplicationsStage;

    public static MainPane mainPane;
    public static Scene scene;
    public static Stage stage;

    public static String path = "";

    public static void main(String[] args) {
        if (args.length > 0) path = args[0];
        launch(args);
    }

    @Override
    public void start(Stage s) {
        printInfo("Cargando archivo de configuracion");
        try (FileInputStream fileInputStream = new FileInputStream(ABSOLUTE_PATH+"share/filefx/config.properties")) {
        //try (FileInputStream fileInputStream = new FileInputStream(HOME+"/.config/filefx/config.properties")) {
            config = new Properties();
            config.load(fileInputStream);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de configuracion", e);
            System.exit(0);
        }

        if (path.equals("")) {
            String initDirectory = config.getProperty("init_path");
            if (initDirectory.charAt(0) == '~') {
                path = HOME+(initDirectory.substring(1));
            } else {
                path = initDirectory;
            }
        }
        printInfo("Path inicial: '"+BLUE+path+RESET+"'");


        printInfo("Cargando archivo de combinaciones de teclado");
        try (FileInputStream fileInputStream = new FileInputStream(ABSOLUTE_PATH+"share/filefx/key_binding.properties")) {
        //try (FileInputStream fileInputStream = new FileInputStream(HOME+"/.config/filefx/key_binding.properties")) {
            keyBinding = new Properties();
            keyBinding.load(fileInputStream);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de combinaciones de teclado", e);
            System.exit(0);
        }

        printInfo("Cargando combinaciones de tecla");
        updateKeyBinding();

        printInfo("Cargando portapapeles");
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        printInfo("Cargando panel principal");
        mainPane = new MainPane();
        updateAll();

        printInfo("Cargando escena principal");
        scene = new Scene();

        printInfo("Cargando escenario principal");
        stage=s;
        stage.getIcons().add(new Image("file://"+ABSOLUTE_PATH+"share/filefx/icons/icon.png"));

        stage.setScene(scene);
        printInfo("Mostrando escenario");
        stage.show();
        printOk("Aplicacion iniciada con exito");

        Platform.runLater(() -> {
            printInfo("Cargando applicaciones para abrir con");
            othersApplicationsStage = new Stage();
            othersApplicationsStage.setScene(new OthersApplicationsScene());
        });
    }

    public static void updateKeyBinding() {
        cut = KeyCodeCombination.valueOf(keyBinding.getProperty("cut"));
        copy = KeyCodeCombination.valueOf(keyBinding.getProperty("copy"));
        paste = KeyCodeCombination.valueOf(keyBinding.getProperty("paste"));
        remove = KeyCodeCombination.valueOf(keyBinding.getProperty("remove"));
        trash = KeyCodeCombination.valueOf(keyBinding.getProperty("trash"));
        rename = KeyCodeCombination.valueOf(keyBinding.getProperty("rename"));

        up = KeyCodeCombination.valueOf(keyBinding.getProperty("up"));
        open = KeyCodeCombination.valueOf(keyBinding.getProperty("open"));
        down = KeyCodeCombination.valueOf(keyBinding.getProperty("down"));
        parent = KeyCodeCombination.valueOf(keyBinding.getProperty("parent"));
        up_step = KeyCodeCombination.valueOf(keyBinding.getProperty("up_step"));
        down_step = KeyCodeCombination.valueOf(keyBinding.getProperty("down_step"));

        select_up = KeyCodeCombination.valueOf(keyBinding.getProperty("select_up"));
        select_down = KeyCodeCombination.valueOf(keyBinding.getProperty("select_down"));
        select_up_step = KeyCodeCombination.valueOf(keyBinding.getProperty("select_up_step"));
        select_down_step = KeyCodeCombination.valueOf(keyBinding.getProperty("select_down_step"));

        back = KeyCodeCombination.valueOf(keyBinding.getProperty("back"));
        forward = KeyCodeCombination.valueOf(keyBinding.getProperty("forward"));

        open_shell = KeyCodeCombination.valueOf(keyBinding.getProperty("open_shell"));
        show_menu = KeyCodeCombination.valueOf(keyBinding.getProperty("show_menu"));
        show_menu_create = KeyCodeCombination.valueOf(keyBinding.getProperty("show_menu_create"));
        focus_path = KeyCodeCombination.valueOf(keyBinding.getProperty("focus_path"));

        deselect_all = KeyCodeCombination.valueOf(keyBinding.getProperty("deselect_all"));
        update_all = KeyCodeCombination.valueOf(keyBinding.getProperty("update_all"));
        change_show_right_pane = KeyCodeCombination.valueOf(keyBinding.getProperty("change_show_right_pane"));
    }

    // Combinaciones de tecla
    public static KeyCombination cut;
    public static KeyCombination copy;
    public static KeyCombination paste;
    public static KeyCombination remove;
    public static KeyCombination trash;
    public static KeyCombination rename;

    public static KeyCombination up;
    public static KeyCombination open;
    public static KeyCombination down;
    public static KeyCombination parent;
    public static KeyCombination up_step;
    public static KeyCombination down_step;

    public static KeyCombination select_up;
    public static KeyCombination select_down;
    public static KeyCombination select_up_step;
    public static KeyCombination select_down_step;

    public static KeyCombination back;
    public static KeyCombination forward;

    public static KeyCombination open_shell;
    public static KeyCombination show_menu;
    public static KeyCombination show_menu_create;
    public static KeyCombination focus_path;

    public static KeyCombination deselect_all;
    public static KeyCombination update_all;
    public static KeyCombination change_show_right_pane;
}

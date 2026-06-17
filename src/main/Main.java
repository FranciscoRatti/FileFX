package main;

import entity.DesktopApplication;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import panel.MainPane;
import scene.OthersApplicationsScene;
import scene.Scene;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

import static main.Lib.*;

public class Main extends javafx.application.Application {
    public static Properties config;
    public static Properties keyBinding;
    public static Properties dynamicValues;
    public static Properties iconsBinding;
    public static boolean isApplicationsSucceded = false;

    public static ArrayList<DesktopApplication> desktopApplications;
    public static Stage othersApplicationsStage;
    public static String path = "";

    public static MainPane mainPane;
    public static Scene scene;
    public static Stage stage;

    public static void main(String[] args) {
        if (args.length > 1) {
            path = args[0];
            for (String arg : args) {
                System.out.println("Argumento : '"+arg+"'");
            }
        }
        launch(args);
    }

    @Override
    public void start(Stage s) {
        printInfo("Cargando archivo de configuracion");
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH+"config.properties")) {
            config = new Properties();
            config.load(fileInputStream);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de configuracion", e);
            System.exit(0);
        }

        printInfo("Cargando archivo de combinaciones de teclado");
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH+"key_binding.properties")) {
            keyBinding = new Properties();
            keyBinding.load(fileInputStream);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de combinaciones de teclado", e);
            System.exit(0);
        }

        printInfo("Cargando archivo de valores dinamicos");
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH+"dynamic_values.properties")) {
            dynamicValues = new Properties();
            dynamicValues.load(fileInputStream);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de combinaciones de teclado", e);
            System.exit(0);
        }

        printInfo("Verificando que existan valores dinamicos");
        if (dynamicValues.getProperty("width") == null) {
            dynamicValues.put("width", "1200");
            printError("no existe propiedad width", null);
        }
        if (dynamicValues.getProperty("height") == null) {
            dynamicValues.put("height", "700");
            printError("no existe propiedad height", null);
        }
        if (dynamicValues.getProperty("init_path") == null) {
            dynamicValues.put("init_path", HOME);
            printError("no existe propiedad init_path", null);
        }
        if (dynamicValues.getProperty("init_selection") == null) {
            dynamicValues.put("init_selection", "");
            printError("no existe propiedad init_selection", null);
        }

        String initPath = dynamicValues.getProperty("init_path");
        if (path.equals("") && initPath != null) {
            if (initPath.charAt(0) == '~') {
                path = HOME+(initPath.substring(1));
            } else {
                path = initPath;
            }
        }
        printInfo("Path inicial: '"+BLUE+path+RESET+"'");

        printInfo("Cargando combinaciones de tecla");
        updateKeyBinding();

        printInfo("Cargando archivo de iconos");
        try (Reader reader = new InputStreamReader(new FileInputStream(CONFIG_PATH+"icons_binding.properties"), StandardCharsets.UTF_8)) {
            iconsBinding = new Properties();
            iconsBinding.load(reader);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de iconos", e);
            System.exit(0);
        }

        printInfo("Cargando portapapeles");
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        printInfo("Cargando panel principal");
        mainPane = new MainPane();

        printInfo("Cargando escena principal");
        scene = new Scene();

        printInfo("Cargando escenario principal");
        stage=s;
        stage.getIcons().add(new Image("file://"+ABSOLUTE_PATH+"share/filefx/icons/icon.png"));
        stage.setOnCloseRequest(e -> {
            boolean isSaveBounds = Boolean.parseBoolean(config.getProperty("save_bounds"));
            boolean isSavePath = Boolean.parseBoolean(config.getProperty("save_path"));
            boolean isSaveSelection = Boolean.parseBoolean(config.getProperty("save_selection"));

            if (isSaveBounds || isSavePath || isSaveSelection) {
                printInfo("Actualizando valores dinamicos:");
                try (FileOutputStream output = new FileOutputStream(CONFIG_PATH+"dynamic_values.properties")) {
                    String width = String.valueOf(stage.getWidth());
                    String height = String.valueOf(stage.getHeight());
                    String selection = MainPane.selectedItem == null ? "" : MainPane.selectedItem.getName();

                    printInfo("   height="+height);
                    printInfo("   width="+width);
                    printInfo("   init_path="+path);
                    printInfo("   init_selection="+selection);
                    
                    if (isSaveBounds) {
                        dynamicValues.replace("width", width);
                        dynamicValues.replace("height", height);
                    }
                    if (isSavePath) dynamicValues.replace("init_path", path);
                    if (isSaveSelection) dynamicValues.replace("init_selection", selection);
                    dynamicValues.store(output, "");
                } catch (IOException ex) {
                    printError("Error al actualizar datos en dynamic_values.properties", ex);
                }
            }

            printOk("Aplicacion finalizada");
        });

        stage.setScene(scene);
        printInfo("Mostrando escenario");
        stage.show();
        Platform.runLater(() -> {
            stage.setWidth(Double.parseDouble(dynamicValues.getProperty("width")));
            stage.setHeight(Double.parseDouble(dynamicValues.getProperty("height")));
            updateRight();
        });
        printOk("Aplicacion iniciada con exito");

        printInfo("Cargando applicaciones para abrir con");
        othersApplicationsStage = new Stage();
        othersApplicationsStage.setScene(new OthersApplicationsScene());
        isApplicationsSucceded = true;
    }

    public static void updateKeyBinding() {
        cut = getKeyCombination("cut");
        copy = getKeyCombination("copy");
        paste = getKeyCombination("paste");
        remove = getKeyCombination("remove");
        trash = getKeyCombination("trash");
        rename = getKeyCombination("rename");

        up = getKeyCombination("up");
        open = getKeyCombination("open");
        down = getKeyCombination("down");
        parent = getKeyCombination("parent");
        up_step = getKeyCombination("up_step");
        down_step = getKeyCombination("down_step");

        select_up = getKeyCombination("select_up");
        select_down = getKeyCombination("select_down");
        select_up_step = getKeyCombination("select_up_step");
        select_down_step = getKeyCombination("select_down_step");

        back = getKeyCombination("back");
        forward = getKeyCombination("forward");

        open_shell = getKeyCombination("open_shell");
        show_menu = getKeyCombination("show_menu");
        show_menu_create = getKeyCombination("show_menu_create");
        focus_path = getKeyCombination("focus_path");

        deselect_all = getKeyCombination("deselect_all");
        update_all = getKeyCombination("update_all");
        change_show_right_pane = getKeyCombination("change_show_right_pane");
    }

    public static KeyCombination[] getKeyCombination(String keyName) {
        String property = keyBinding.getProperty(keyName);
        if (property != null) {
            String[] texts = property.split(",");
            KeyCombination[] keys = new KeyCombination[texts.length];

            for (int i = 0; i < texts.length; i++) {
                keys[i] = KeyCodeCombination.valueOf(texts[i]);
            }

            return keys;
        } else return null;
    }

    // Combinaciones de tecla
    public static KeyCombination[] cut;
    public static KeyCombination[] copy;
    public static KeyCombination[] paste;
    public static KeyCombination[] remove;
    public static KeyCombination[] trash;
    public static KeyCombination[] rename;

    public static KeyCombination[] up;
    public static KeyCombination[] open;
    public static KeyCombination[] down;
    public static KeyCombination[] parent;
    public static KeyCombination[] up_step;
    public static KeyCombination[] down_step;

    public static KeyCombination[] select_up;
    public static KeyCombination[] select_down;
    public static KeyCombination[] select_up_step;
    public static KeyCombination[] select_down_step;

    public static KeyCombination[] back;
    public static KeyCombination[] forward;

    public static KeyCombination[] open_shell;
    public static KeyCombination[] show_menu;
    public static KeyCombination[] show_menu_create;
    public static KeyCombination[] focus_path;

    public static KeyCombination[] deselect_all;
    public static KeyCombination[] update_all;
    public static KeyCombination[] change_show_right_pane;
}

package main;

import entity.DesktopApplication;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import panel.MainPane;
import scene.Scene;
import stage.OthersApplicationsStage;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static main.Lib.*;

public class FileFX extends javafx.application.Application {
    public static Properties config;
    public static Properties keyBinding;
    public static Properties dynamicValues;
    public static Properties iconsMyme;
    public static Properties iconsExtension;
    public static Properties colorsMyme;
    public static Properties colorsExtension;

    public static Font nerdFont;
    public static String path = "";
    public static final Lock lock = new ReentrantLock();

    public static ArrayList<DesktopApplication> desktopApplications;
    public static Stage othersApplicationsStage;

    public static MainPane mainPane;
    public static Scene scene;
    public static Stage stage;

    public static void main(String[] args) {
        if (args.length > 0) path = args[0];
        launch(args);
    }

    public void start(Stage s) {
        nerdFont = Font.loadFont("file://" + ABSOLUTE_PATH + "share/filefx/0xProtoNerdFontMono-Regular.ttf", 16);

        printInfo("Cargando archivo de configuracion");
        try (Reader reader = new InputStreamReader(new FileInputStream(CONFIG_PATH+"config.properties"), StandardCharsets.UTF_8)) {
            config = new Properties();
            config.load(reader);

            TERMINAL = config.getProperty("terminal");
            SAVE_BOUNDS = Boolean.parseBoolean(config.getProperty("save_bounds"));
            SAVE_PATH = Boolean.parseBoolean(config.getProperty("save_path"));
            SAVE_SELECTION = Boolean.parseBoolean(config.getProperty("save_selection"));
            TOP_BUTTONS = splitTwoTimes(config.getProperty("top_buttons"));
            RIGHT_WIDTH = Double.parseDouble(config.getProperty("right_width"));
            SHOW_RIGHT_PANE = Boolean.parseBoolean(config.getProperty("show_right_pane"));
            SHOW_MINIATURA = Boolean.parseBoolean(config.getProperty("show_miniatura"));
            SHOW_PLACES = Boolean.parseBoolean(config.getProperty("show_places"));
            PLACES = splitTwoTimes(config.getProperty("places"));
            SHOW_DEVICES = Boolean.parseBoolean(config.getProperty("show_devices"));
            IS_DIRECTORY_FIRST = Boolean.parseBoolean(config.getProperty("is_directory_first"));
            SHOW_HIDDEN = Boolean.parseBoolean(config.getProperty("show_hidden"));
            SHOW_THIS = Boolean.parseBoolean(config.getProperty("show_this"));
            SHOW_PARENT = Boolean.parseBoolean(config.getProperty("show_parent"));
            FILL_TEXT_FILE_LIKE_ICON = Boolean.parseBoolean(config.getProperty("fill_text_file_like_icon"));
            FILL_TEXT_DIR_LIKE_ICON = Boolean.parseBoolean(config.getProperty("fill_text_dir_like_icon"));
            FILL_MINIATURA_LIKE_ICON = Boolean.parseBoolean(config.getProperty("fill_miniatura_like_icon"));
            CONTEXT_MENU_ICONS = split(config.getProperty("context_menu_icons"));
            CHECK_CLIPBOARD_PASTE = Boolean.parseBoolean(config.getProperty("check_clipboard_paste"));

        } catch (IOException e) {
            printError("No se pudo leer el archivo de "+RED+"configuracion"+RESET, e);
            System.exit(0);
        }

        printInfo("Cargando archivo de combinaciones de teclado");
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH+"key_binding.properties")) {
            keyBinding = new Properties();
            keyBinding.load(fileInputStream);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de "+RED+"combinaciones de teclado"+RESET, e);
            System.exit(0);
        }

        printInfo("Cargando archivo de valores dinamicos");
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH+"dynamic_values.properties")) {
            dynamicValues = new Properties();
            dynamicValues.load(fileInputStream);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de "+RED+"combinaciones de teclado"+RESET, e);
            System.exit(0);
        }

        printInfo("Verificando que existan valores dinamicos");
        dynamicValues.putIfAbsent("width", "1200");
        dynamicValues.putIfAbsent("height", "700");
        dynamicValues.putIfAbsent("init_path", HOME);
        dynamicValues.putIfAbsent("init_selection", "");

        String initPath = dynamicValues.getProperty("init_path");
        if (path.isEmpty() && initPath != null) {
            if (initPath.charAt(0) == '~') {
                path = HOME+initPath.substring(1);
            } else {
                path = initPath;
            }
        }
        printInfo("Path inicial: '"+BLUE+path+RESET+"'");

        printInfo("Cargando combinaciones de teclado");
        updateKeyBinding();

        printInfo("Cargando archivo de iconos");
        try (Reader reader = new InputStreamReader(new FileInputStream(CONFIG_PATH+"icons_binding.properties"), StandardCharsets.UTF_8)) {
            Properties iconsBinding = new Properties();
            iconsBinding.load(reader);

            iconsMyme = new Properties();
            iconsExtension = new Properties();

            iconsBinding.forEach((arg0, arg1) -> {
                String k = (String) arg0;
                String v = (String) arg1;

                if (k.startsWith(".")) iconsExtension.put(k, v);
                else iconsMyme.put(k, v);
            });
        } catch (IOException e) {
            printError("No se pudo leer el archivo de iconos", e);
            System.exit(0);
        }

        printInfo("Cargando archivo de colores");
        try (FileInputStream input = new FileInputStream(CONFIG_PATH+"colors_binding.properties")) {
            Properties colorsBinding = new Properties();
            colorsBinding.load(input);

            colorsMyme = new Properties();
            colorsExtension = new Properties();

            colorsBinding.forEach((arg0, arg1) -> {
                String k = (String) arg0;
                String v = (String) arg1;

                if (k.startsWith(".")) colorsExtension.put(k, v);
                else {
                    colorsMyme.put(k, v);
                    if (k.equals("focus")) {
                        FOCUS_COLOR = Color.valueOf(v);
                        FOCUS_COLOR_RGB = new double[]{FOCUS_COLOR.getRed() * 255, FOCUS_COLOR.getGreen() * 255, FOCUS_COLOR.getBlue() * 255};
                    } else if (k.equals("unknow")) {
                        UNKNOW_COLOR = Color.valueOf(v);
                        UNKNOW_COLOR_RGB = new double[]{UNKNOW_COLOR.getRed() * 255, UNKNOW_COLOR.getGreen() * 255, UNKNOW_COLOR.getBlue() * 255};
                    }
                }
            });
        } catch (IOException e) {
            printError("No se puedo leer archivo de colores", e);
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
        stage.getIcons().add(new Image("file://"+ABSOLUTE_PATH+"share/filefx/icon.png"));
        stage.setTitle("Explorador de archivos");
        stage.setOnCloseRequest(e -> {
            if (SAVE_BOUNDS || SAVE_PATH || SAVE_SELECTION) {
                printInfo("Actualizando valores dinamicos:");
                try (FileOutputStream output = new FileOutputStream(CONFIG_PATH+"dynamic_values.properties")) {
                    String width = String.valueOf(stage.getWidth());
                    String height = String.valueOf(stage.getHeight());
                    String selection = MainPane.selectedItem == null ? "" : MainPane.selectedItem.getName();

                    printInfo("   height="+height);
                    printInfo("   width="+width);
                    printInfo("   init_path="+path);
                    printInfo("   init_selection="+selection);
                    
                    if (SAVE_BOUNDS) {
                        dynamicValues.replace("width", width);
                        dynamicValues.replace("height", height);
                    }
                    if (SAVE_PATH) dynamicValues.replace("init_path", path);
                    if (SAVE_SELECTION) dynamicValues.replace("init_selection", selection);
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
        othersApplicationsStage = new OthersApplicationsStage();
    }

    private String[] split(String text) {
        return text.substring(1, text.length()-1).split(",");
    }
    private String[][] splitTwoTimes(String text) {
        String[] split = text.substring(1, text.length()-1).split(",");
        String[][] result = new String[split.length][];
        for (int i = 0; i < split.length; i++) {
            result[i] = split[i].substring(1, split[i].length()-1).split(";");
        }
        return result;
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

    // Configuracion
    public static String TERMINAL;
    public static boolean SAVE_BOUNDS;
    public static boolean SAVE_PATH;
    public static boolean SAVE_SELECTION;
    public static String[][] TOP_BUTTONS;
    public static double RIGHT_WIDTH;
    public static boolean SHOW_RIGHT_PANE;
    public static boolean SHOW_MINIATURA;
    public static boolean SHOW_PLACES;
    public static String[][] PLACES;
    public static boolean SHOW_DEVICES;
    public static boolean IS_DIRECTORY_FIRST;
    public static boolean SHOW_HIDDEN;
    public static boolean SHOW_THIS;
    public static boolean SHOW_PARENT;
    public static boolean FILL_TEXT_FILE_LIKE_ICON;
    public static boolean FILL_TEXT_DIR_LIKE_ICON;
    public static boolean FILL_MINIATURA_LIKE_ICON;
    public static String[] CONTEXT_MENU_ICONS;
    public static boolean CHECK_CLIPBOARD_PASTE;

    public static Color FOCUS_COLOR;
    public static double[] FOCUS_COLOR_RGB;
    public static Color UNKNOW_COLOR;
    public static double[] UNKNOW_COLOR_RGB;
}
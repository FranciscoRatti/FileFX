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

import static main.Lib.*;
import static panel.RightPane.changeShow;

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
            FILL_MINIATURA_LIKE_ICON = Boolean.parseBoolean(config.getProperty("fill_miniatura_like_icon"));
            SHOW_INSIDE_DIRECTORIES = Boolean.parseBoolean(config.getProperty("show_inside_directories"));
            SHOW_INSIDE_FILES = Boolean.parseBoolean(config.getProperty("show_inside_files"));

            BOTTOM_BUTTONS = split(config.getProperty("bottom_buttons"));
            ORDER_ICONS = split(config.getProperty("order_icons"));

            LEFT_WIDTH = Double.parseDouble(config.getProperty("left_width"));
            SHOW_PLACES = Boolean.parseBoolean(config.getProperty("show_places"));
            PLACES = splitTwoTimes(config.getProperty("places"));
            SHOW_DEVICES = Boolean.parseBoolean(config.getProperty("show_devices"));
            PARTITION_ICONS = splitTwoTimes(config.getProperty("partition_icons"));
            SHOW_UNMOUNTED = Boolean.parseBoolean(config.getProperty("show_unmounted"));
            UNMOUNT_ICON = config.getProperty("unmount_icon");

            IS_DIRECTORY_FIRST = Boolean.parseBoolean(config.getProperty("is_directory_first"));
            SHOW_HIDDEN = Boolean.parseBoolean(config.getProperty("show_hidden"));
            SHOW_THIS = Boolean.parseBoolean(config.getProperty("show_this"));
            SHOW_PARENT = Boolean.parseBoolean(config.getProperty("show_parent"));
            FILL_TEXT_FILE_LIKE_ICON = Boolean.parseBoolean(config.getProperty("fill_text_file_like_icon"));
            FILL_TEXT_DIR_LIKE_ICON = Boolean.parseBoolean(config.getProperty("fill_text_dir_like_icon"));
            DEFAULT_ORDER = ORDER.valueOf(config.getProperty("default_order"));
            CUSTOM_ORDER = splitTwoTimes(config.getProperty("custom_order"));

            String[] columnsText = split(config.getProperty("columns"));
            if (columnsText != null) {
                COLUMNS = new COLUMNS[columnsText.length];
                for (int i = 0; i < columnsText.length; i++) COLUMNS[i] = Lib.COLUMNS.valueOf(columnsText[i].toUpperCase());
            }

            CONTEXT_MENU_ICONS = split(config.getProperty("context_menu_icons"));
            CHECK_CLIPBOARD_PASTE = Boolean.parseBoolean(config.getProperty("check_clipboard_paste"));

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

        printInfo("Cargando archivo de valores iniciales");
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH+"init_values.properties")) {
            dynamicValues = new Properties();
            dynamicValues.load(fileInputStream);
        } catch (IOException e) {
            printError("No se pudo leer el archivo de valores iniciales", e);
            System.exit(0);
        }

        printInfo("Verificando que existan valores iniciales");
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
        changeShow(SHOW_RIGHT_PANE);

        printInfo("Cargando escena principal");
        scene = new Scene();

        printInfo("Cargando escenario principal");
        stage=s;
        stage.getIcons().add(new Image("file://"+ABSOLUTE_PATH+"share/filefx/icon.png"));
        stage.setTitle("Explorador de archivos");
        stage.setOnCloseRequest(e -> {
            printExecute("Cerrando ventana");
            if (SAVE_BOUNDS || SAVE_PATH || SAVE_SELECTION) {
                printInfo("Actualizando valores dinamicos:");
                try (FileOutputStream output = new FileOutputStream(CONFIG_PATH+"init_values.properties")) {
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
                    printError("Error al actualizar datos en init_values.properties", ex);
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
        if (text.equals("[]")) return null;
        else return text.substring(1, text.length()-1).split(",");
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
        CUT = getKeyCombination("cut");
        COPY = getKeyCombination("copy");
        PASTE = getKeyCombination("paste");
        REMOVE = getKeyCombination("remove");
        TRASH = getKeyCombination("trash");
        RENAME = getKeyCombination("rename");

        UP = getKeyCombination("up");
        OPEN = getKeyCombination("open");
        DOWN = getKeyCombination("down");
        PARENT = getKeyCombination("parent");
        UP_STEP = getKeyCombination("up_step");
        DOWN_STEP = getKeyCombination("down_step");
        FIRST = getKeyCombination("first");
        LAST = getKeyCombination("last");

        SELECT_UP = getKeyCombination("select_up");
        SELECT_DOWN = getKeyCombination("select_down");
        SELECT_UP_STEP = getKeyCombination("select_up_step");
        SELECT_DOWN_STEP = getKeyCombination("select_down_step");
        SELECT_FIRST = getKeyCombination("select_first");
        SELECT_LAST = getKeyCombination("select_last");

        BACK = getKeyCombination("back");
        FORWARD = getKeyCombination("forward");

        OPEN_SHELL = getKeyCombination("open_shell");
        SHOW_MENU = getKeyCombination("show_menu");
        SHOW_MENU_CREATE = getKeyCombination("show_menu_create");
        FOCUS_PATH = getKeyCombination("focus_path");
        FOCUS_FILTER = getKeyCombination("focus_filter");
        FOCUS_INSIDE = getKeyCombination("focus_inside");
        SAVE_INSIDE = getKeyCombination("save_inside");
        DESELECT_ALL = getKeyCombination("deselect_all");
        UPDATE_ALL = getKeyCombination("update_all");
        CHANGE_SHOW_RIGHT_PANE = getKeyCombination("change_show_right_pane");
        CHANGE_SHOW_HIDDEN = getKeyCombination("change_show_hidden");
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
    public static KeyCombination[] CUT;
    public static KeyCombination[] COPY;
    public static KeyCombination[] PASTE;
    public static KeyCombination[] REMOVE;
    public static KeyCombination[] TRASH;
    public static KeyCombination[] RENAME;

    public static KeyCombination[] UP;
    public static KeyCombination[] OPEN;
    public static KeyCombination[] DOWN;
    public static KeyCombination[] PARENT;
    public static KeyCombination[] UP_STEP;
    public static KeyCombination[] DOWN_STEP;
    public static KeyCombination[] FIRST;
    public static KeyCombination[] LAST;

    public static KeyCombination[] SELECT_UP;
    public static KeyCombination[] SELECT_DOWN;
    public static KeyCombination[] SELECT_UP_STEP;
    public static KeyCombination[] SELECT_DOWN_STEP;
    public static KeyCombination[] SELECT_FIRST;
    public static KeyCombination[] SELECT_LAST;

    public static KeyCombination[] BACK;
    public static KeyCombination[] FORWARD;

    public static KeyCombination[] OPEN_SHELL;
    public static KeyCombination[] SHOW_MENU;
    public static KeyCombination[] SHOW_MENU_CREATE;
    public static KeyCombination[] FOCUS_PATH;
    public static KeyCombination[] FOCUS_FILTER;
    public static KeyCombination[] FOCUS_INSIDE;
    public static KeyCombination[] SAVE_INSIDE;
    public static KeyCombination[] DESELECT_ALL;
    public static KeyCombination[] UPDATE_ALL;
    public static KeyCombination[] CHANGE_SHOW_RIGHT_PANE;
    public static KeyCombination[] CHANGE_SHOW_HIDDEN;

    // Configuracion
    public static String TERMINAL;
    public static boolean SAVE_BOUNDS;
    public static boolean SAVE_PATH;
    public static boolean SAVE_SELECTION;

    public static String[][] TOP_BUTTONS;

    public static double RIGHT_WIDTH;
    public static boolean SHOW_RIGHT_PANE;
    public static boolean SHOW_MINIATURA;
    public static boolean FILL_MINIATURA_LIKE_ICON;
    public static boolean SHOW_INSIDE_DIRECTORIES;
    public static boolean SHOW_INSIDE_FILES;

    public static String[] BOTTOM_BUTTONS;
    public static String[] ORDER_ICONS;

    public static double LEFT_WIDTH;
    public static boolean SHOW_PLACES;
    public static String[][] PLACES;
    public static boolean SHOW_DEVICES;
    public static String[][] PARTITION_ICONS;
    public static boolean SHOW_UNMOUNTED;
    public static String UNMOUNT_ICON;

    public static boolean IS_DIRECTORY_FIRST;
    public static boolean SHOW_HIDDEN;
    public static boolean SHOW_THIS;
    public static boolean SHOW_PARENT;
    public static boolean FILL_TEXT_FILE_LIKE_ICON;
    public static boolean FILL_TEXT_DIR_LIKE_ICON;
    public static ORDER DEFAULT_ORDER;
    public static String[][] CUSTOM_ORDER;
    public static COLUMNS[] COLUMNS;

    public static String[] CONTEXT_MENU_ICONS;
    public static boolean CHECK_CLIPBOARD_PASTE;

    // Colores
    public static Color FOCUS_COLOR;
    public static double[] FOCUS_COLOR_RGB;
    public static Color UNKNOW_COLOR;
    public static double[] UNKNOW_COLOR_RGB;
}
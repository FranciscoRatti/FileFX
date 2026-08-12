package main;

import entity.DesktopApplication;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import panel.MainPane;
import scene.Scene;
import stage.OthersApplicationsStage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

import static main.Lib.*;
import static panel.MainPane.*;
import static panel.RightPane.*;

public class FileFX extends javafx.application.Application {
    public static Properties config;
    public static Properties keyBinding;
    public static Properties initValues;
    public static Properties iconsMime;
    public static Properties iconsExtension;
    public static Properties colorsMime;
    public static Properties colorsExtension;

    public static Font nerdFont;
    public static String path = "";

    public static ArrayList<DesktopApplication> desktopApplications;
    public static OthersApplicationsStage othersApplicationsStage;

    public static MainPane mainPane;
    public static Scene scene;
    public static Stage stage;

    public static void main(String[] args) {
        boolean disableAutoUpdate = false;
        if (args.length > 0) {
            if (args[0].equals("--disable-auto-update")) {
                disableAutoUpdate = true;
                if (args.length > 1) {
                    path = args[1];
                    if (path.charAt(path.length()-1) != '/') path += "/";
                }
            } else {
                path = args[0];
                if (path.charAt(path.length()-1) != '/') path += "/";
            }
        }

        if (!disableAutoUpdate) {
            Task<Void> task = new Task<>() {
                protected Void call() throws Exception {
                    checkUpdate();
                    return null;
                }
            };
            new Thread(task).start();
        }

        launch(args);
    }

    public void start(Stage s) {
        nerdFont = Font.loadFont("file://" + ABSOLUTE_PATH + "0xProtoNerdFontMono-Regular.ttf", 16);

        printInfo("Cargando archivo de valores iniciales");
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH+"init_values.properties")) {
            initValues = new Properties();
            initValues.load(fileInputStream);

            initValues.putIfAbsent("width", "950");
            initValues.putIfAbsent("height", "525");
            initValues.putIfAbsent("init_path", HOME);
            initValues.putIfAbsent("init_selection", "");
            initValues.putIfAbsent("right_width", "200.0");
            initValues.putIfAbsent("left_width", "130.0");

            RIGHT_WIDTH = Double.parseDouble( initValues.getProperty("right_width"));
            LEFT_WIDTH = Double.parseDouble(initValues.getProperty("left_width"));
        } catch (IOException e) {
            printError("No se pudo leer el archivo de valores iniciales", e);
            System.exit(0);
        }

        printInfo("Cargando archivo de configuracion");
        try (Reader reader = new InputStreamReader(new FileInputStream(CONFIG_PATH+"config.properties"), StandardCharsets.UTF_8)) {
            config = new Properties();
            config.load(reader);

            TERMINAL = (String) config.getOrDefault("terminal", "xterm");
            SAVE_BOUNDS = Boolean.parseBoolean((String) config.getOrDefault("save_bounds", "false"));
            SAVE_PATH = Boolean.parseBoolean((String) config.getOrDefault("save_path", "false"));
            SAVE_SELECTION = Boolean.parseBoolean((String) config.getOrDefault("save_selection", "false"));
            TEMPLATES_DIR = (String) config.getOrDefault("templates_dir", HOME+"/Templates");
            if (TEMPLATES_DIR.charAt(0) == '~') TEMPLATES_DIR = HOME + TEMPLATES_DIR.substring(1);

            TOP_BUTTONS = splitTwoTimes((String) config.getOrDefault("top_buttons", "[{back;\uF177},{forward;\uF178},{parent;\uDB81\uDE45},{search},{clean;\uDB80\uDCE2},{reload;\uF2F1}]"));

            SAVE_RIGHT_WIDTH = Boolean.parseBoolean((String) config.getOrDefault("save_right_width", "false"));
            SHOW_RIGHT_PANE = Boolean.parseBoolean((String) config.getOrDefault("show_right_pane", "true"));
            SHOW_MINIATURA = Boolean.parseBoolean((String) config.getOrDefault("show_miniatura", "false"));
            FILL_MINIATURA_LIKE_ICON = Boolean.parseBoolean((String) config.getOrDefault("fill_miniatura_like_icon", "true"));
            SHOW_INSIDE_DIRECTORIES = Boolean.parseBoolean((String) config.getOrDefault("show_inside_directories", "false"));
            SHOW_INSIDE_FILES = Boolean.parseBoolean((String) config.getOrDefault("show_inside_files", "false"));

            BOTTOM_BUTTONS = split((String) config.getOrDefault("bottom_buttons", "[order,filter]"));
            ORDER_ICONS = split((String) config.getOrDefault("order_icons", "[\uEB69,\uF073,\uDB83\uDC8E,\uEBB9]"));

            SAVE_LEFT_WIDTH = Boolean.parseBoolean((String) config.getOrDefault("save_left_width", "false"));
            SHOW_PLACES = Boolean.parseBoolean((String) config.getOrDefault("show_places", "true"));
            PLACES = splitTwoTimes((String) config.getOrDefault("places", "[{Home;\uF46D;~/},{Descargas;\uF019;~/Downloads/},{Documentos;\uDB85\uDD17;~/Documents/},{Imagenes;\uF03E;~/Images/},{Papelera;\uF014;~/.local/share/Trash/files/},{Config;\uF013;~/.config/filefx/}]"));
            SHOW_DEVICES = Boolean.parseBoolean((String) config.getOrDefault("show_devices", "true"));
            PARTITION_ICONS = splitTwoTimes((String) config.getOrDefault("partition_icons", "[{/;Raiz},{/boot/efi;Boot}]"));
            SHOW_UNMOUNTED = Boolean.parseBoolean((String) config.getOrDefault("show_unmounted", "false"));
            UNMOUNT_ICON = (String) config.getOrDefault("unmount_icon", "\uDB81\uDEA6");

            IS_DIRECTORY_FIRST = Boolean.parseBoolean((String) config.getOrDefault("is_directory_first", "true"));
            SHOW_HIDDEN = Boolean.parseBoolean((String) config.getOrDefault("show_hidden", "true"));
            SHOW_THIS = Boolean.parseBoolean((String) config.getOrDefault("show_this", "true"));
            SHOW_PARENT = Boolean.parseBoolean((String) config.getOrDefault("show_parent", "true"));
            FILL_TEXT_FILE_LIKE_ICON = Boolean.parseBoolean((String) config.getOrDefault("fill_text_file_like_icon", "false"));
            FILL_TEXT_DIR_LIKE_ICON = Boolean.parseBoolean((String) config.getOrDefault("fill_text_dir_like_icon", "true"));
            DEFAULT_ORDER = ORDER.valueOf((String) config.getOrDefault("default_order", "NAME"));
            CUSTOM_ORDER = splitTwoTimes((String) config.getOrDefault("custom_order", "[{~/Downloads/;DATE},{~/Images/;DATE},{~/Videos/;DATE},{trash/;DATE}]"));

            String[] columnsText = split((String) config.getOrDefault("columns", "[size]"));
            COLUMNS = new COLUMNS[columnsText.length];
            for (int i = 0; i < columnsText.length; i++) COLUMNS[i] = Lib.COLUMNS.valueOf(columnsText[i].toUpperCase());

            String[] contextMenuItemsText = split((String) config.getOrDefault("context_menu_items", "BACKWARD,FORWARD,OPEN,OPEN_WITH,CREATE_FILE,CREATE_DIR,CREATE_LINK,RENAME,COPY,CUT,PASTE,RESTORE,TRASH,REMOVE,EXTRACT,COMPRESS,SHELL"));
            CONTEXT_MENU_ITEMS = new ITEMS[contextMenuItemsText.length];
            for (int i = 0; i < contextMenuItemsText.length; i++) CONTEXT_MENU_ITEMS[i] = Lib.ITEMS.valueOf(contextMenuItemsText[i].toUpperCase());
            CONTEXT_MENU_ICONS = split((String) config.getOrDefault("context_menu_icons", "[\uDB83\uDDCF,\uDB83\uDDCF,\uEA7F,\uEA80,\uF0C1,\uDB81\uDE0E,\uF0C5,\uDB80\uDD90,\uDB80\uDD92,\uF1B8,\uF48E,\uF52F,\uDB80\uDFD6,\uDB80\uDFD7,\uF489]"));
            CHECK_CLIPBOARD_PASTE = Boolean.parseBoolean((String) config.getOrDefault("check_clipboard_paste", "true"));

        } catch (IOException e) {
            printError("No se pudo leer el archivo de configuracion", e);
            System.exit(0);
        }

        printInfo("Cargando archivo de combinaciones de teclado");
        try (FileInputStream fileInputStream = new FileInputStream(CONFIG_PATH+"key_binding.properties")) {
            keyBinding = new Properties();
            keyBinding.load(fileInputStream);

            CUT = getKeyCombination("cut", "ctrl+x");
            COPY = getKeyCombination("copy", "ctrl+c");
            PASTE = getKeyCombination("paste", "ctrl+v");
            REMOVE = getKeyCombination("remove", "ctrl+delete");
            TRASH = getKeyCombination("trash", "delete");
            RENAME = getKeyCombination("rename", "f4");

            UP = getKeyCombination("up", "up");
            OPEN = getKeyCombination("open", "enter,right");
            DOWN = getKeyCombination("down", "down");
            PARENT = getKeyCombination("parent", "backspace,left");
            UP_STEP = getKeyCombination("up_step", "page up");
            DOWN_STEP = getKeyCombination("down_step", "page down");
            FIRST = getKeyCombination("first", "home");
            LAST = getKeyCombination("last", "end");

            SELECT_UP = getKeyCombination("select_up", "shift+up");
            SELECT_DOWN = getKeyCombination("select_down", "shift+down");
            SELECT_UP_STEP = getKeyCombination("select_up_step", "shift+page up");
            SELECT_DOWN_STEP = getKeyCombination("select_down_step", "shift+page down");
            SELECT_FIRST = getKeyCombination("select_first", "shift+home");
            SELECT_LAST = getKeyCombination("select_last", "shift+end");
            DESELECT_ALL = getKeyCombination("deselect_all", "esc");

            BACKWARD = getKeyCombination("back", "ctrl+z");
            FORWARD = getKeyCombination("forward", "ctrl+y");

            SHOW_MENU = getKeyCombination("show_menu", "context menu,ctrl+space");
            SHOW_MENU_CREATE = getKeyCombination("show_menu_create", "n");
            CHANGE_SHOW_RIGHT_PANE = getKeyCombination("change_show_right_pane", "space");
            CHANGE_SHOW_HIDDEN = getKeyCombination("change_show_hidden", "h");
            CHANGE_PERMISSIONS = getKeyCombination("change_permissions", "p");
            UPDATE_ALL = getKeyCombination("update_all", "f5");

            FOCUS_PATH = getKeyCombination("focus_path", "s");
            FOCUS_FILTER = getKeyCombination("focus_filter", "f");
            FOCUS_INSIDE = getKeyCombination("focus_inside", "i");
            SAVE_INSIDE = getKeyCombination("save_inside", "ctrl+s");

            OPEN_SHELL = getKeyCombination("open_shell", "ctrl+t");
        } catch (IOException e) {
            printError("No se pudo leer el archivo de combinaciones de teclado", e);
            System.exit(0);
        }

        String initPath = initValues.getProperty("init_path");
        if (path.isEmpty()) {
            if (initPath.charAt(0) == '~') {
                path = HOME+initPath.substring(1);
            } else {
                path = initPath;
            }
        }
        printInfo("Path inicial: '"+BLUE+path+RESET+"'");

        printInfo("Cargando archivo de iconos");
        try (Reader reader = new InputStreamReader(new FileInputStream(CONFIG_PATH+"icons_binding.properties"), StandardCharsets.UTF_8)) {
            Properties iconsBinding = new Properties();
            iconsBinding.load(reader);

            iconsMime = new Properties();
            iconsExtension = new Properties();

            iconsBinding.forEach((arg0, arg1) -> {
                String k = (String) arg0;
                String v = (String) arg1;

                if (k.startsWith(".")) iconsExtension.put(k, v);
                else iconsMime.put(k, v);
            });
        } catch (IOException e) {
            printError("No se pudo leer el archivo de iconos", e);
            System.exit(0);
        }

        printInfo("Cargando archivo de colores");
        try (FileInputStream input = new FileInputStream(CONFIG_PATH+"colors_binding.properties")) {
            Properties colorsBinding = new Properties();
            colorsBinding.load(input);

            colorsMime = new Properties();
            colorsExtension = new Properties();

            colorsBinding.forEach((arg0, arg1) -> {
                String k = (String) arg0;
                String v = (String) arg1;

                if (k.startsWith(".")) colorsExtension.put(k, v);
                else {
                    colorsMime.put(k, v);
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

        printInfo("Cargando panel principal");
        mainPane = new MainPane();
        changeShow(SHOW_RIGHT_PANE);

        printInfo("Cargando escena principal");
        scene = new Scene();

        printInfo("Cargando escenario principal");
        stage=s;
        stage.getIcons().add(new Image("file://"+ABSOLUTE_PATH+"icon.png"));
        stage.setTitle("Explorador de archivos");

        stage.setScene(scene);
        printInfo("Mostrando escenario");
        stage.show();
        Platform.runLater(() -> {
            stage.setWidth(Double.parseDouble(initValues.getProperty("width")));
            stage.setHeight(Double.parseDouble(initValues.getProperty("height")));
            updateRight();
        });
        printOk("Aplicacion iniciada con exito");

        printInfo("Cargando applicaciones para abrir con");
        othersApplicationsStage = new OthersApplicationsStage();

        stage.setOnCloseRequest(e -> {
            printExecute("Cerrando ventana");
            if (SAVE_BOUNDS || SAVE_PATH || SAVE_SELECTION || SAVE_RIGHT_WIDTH || SAVE_LEFT_WIDTH) {
                printInfo("Actualizando valores dinamicos:");
                try (FileOutputStream output = new FileOutputStream(CONFIG_PATH+"init_values.properties")) {
                    String width = String.valueOf(stage.getWidth());
                    String height = String.valueOf(stage.getHeight());
                    String selection = centerPane.selectionModel.getSelectedItem() == null ? "" : centerPane.selectionModel.getSelectedItem().getName();

                    if (SAVE_BOUNDS) {
                        printInfo("   height="+height);
                        printInfo("   width="+width);
                        initValues.replace("width", width);
                        initValues.replace("height", height);
                    }
                    if (SAVE_PATH) {
                        printInfo("   init_path="+path);
                        initValues.replace("init_path", path);
                    }
                    if (SAVE_SELECTION) {
                        printInfo("   init_selection="+selection);
                        initValues.replace("init_selection", selection);
                    }
                    if (SAVE_RIGHT_WIDTH) {
                        printInfo("   right_width="+RIGHT_WIDTH);
                        initValues.replace("right_width", String.valueOf(RIGHT_WIDTH));
                    }
                    if (SAVE_LEFT_WIDTH) {
                        printInfo("   left_width="+LEFT_WIDTH);
                        initValues.replace("left_width", String.valueOf(LEFT_WIDTH));
                    }

                    initValues.store(output, "");
                } catch (IOException ex) {
                    printError("Error al actualizar datos en init_values.properties", ex);
                }
            }

            System.exit(0);
            printOk("Aplicacion finalizada");
        });
    }

    private static void checkUpdate() {
        Properties metadata = new Properties();

        try (FileInputStream input = new FileInputStream("/var/lib/filefx/metadata.properties")) {
            metadata.load(input);
            LocalDate lastCheck = LocalDate.parse(metadata.getProperty("last_check"));
            LocalDate now = LocalDate.now();

            // Si hace mas de dos dias que no se chequea
            if (lastCheck.isBefore(now.minusDays(1))) {

                // Actualizar
                ProcessBuilder pb = new ProcessBuilder(LIB_PATH+"update.sh", String.valueOf(ProcessHandle.current().pid()));
                pb.redirectErrorStream(true);
                Process process = pb.start();

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }

                // Guardar last_check
                try (FileOutputStream output = new FileOutputStream("/var/lib/filefx/metadata.properties")) {
                    metadata.put("last_check", now.toString());
                    metadata.store(output, "");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public static KeyCombination[] getKeyCombination(String keyName, String fallBack) {
        String property = keyBinding.getProperty(keyName);
        if (property == null) property = fallBack;

        String[] texts = property.split(",");
        KeyCombination[] keys = new KeyCombination[texts.length];

        for (int i = 0; i < texts.length; i++) {
            keys[i] = KeyCodeCombination.valueOf(texts[i]);
        }

        return keys;
    }

    // Valores iniciales
    public static double RIGHT_WIDTH;
    public static double LEFT_WIDTH;

    // Configuracion
    public static String TERMINAL;
    public static boolean SAVE_BOUNDS;
    public static boolean SAVE_PATH;
    public static boolean SAVE_SELECTION;
    public static String TEMPLATES_DIR;

    public static String[][] TOP_BUTTONS;

    public static boolean SAVE_RIGHT_WIDTH;
    public static boolean SHOW_RIGHT_PANE;
    public static boolean SHOW_MINIATURA;
    public static boolean FILL_MINIATURA_LIKE_ICON;
    public static boolean SHOW_INSIDE_DIRECTORIES;
    public static boolean SHOW_INSIDE_FILES;

    public static String[] BOTTOM_BUTTONS;
    public static String[] ORDER_ICONS;

    public static boolean SAVE_LEFT_WIDTH;
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

    public static ITEMS[] CONTEXT_MENU_ITEMS;
    public static String[] CONTEXT_MENU_ICONS;
    public static boolean CHECK_CLIPBOARD_PASTE;

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
    public static KeyCombination[] DESELECT_ALL;

    public static KeyCombination[] BACKWARD;
    public static KeyCombination[] FORWARD;

    public static KeyCombination[] SHOW_MENU;
    public static KeyCombination[] SHOW_MENU_CREATE;
    public static KeyCombination[] CHANGE_SHOW_RIGHT_PANE;
    public static KeyCombination[] CHANGE_SHOW_HIDDEN;
    public static KeyCombination[] CHANGE_PERMISSIONS;
    public static KeyCombination[] UPDATE_ALL;

    public static KeyCombination[] FOCUS_PATH;
    public static KeyCombination[] FOCUS_FILTER;
    public static KeyCombination[] FOCUS_INSIDE;
    public static KeyCombination[] SAVE_INSIDE;

    public static KeyCombination[] OPEN_SHELL;

    // Colores
    public static Color FOCUS_COLOR;
    public static double[] FOCUS_COLOR_RGB;
    public static Color UNKNOW_COLOR;
    public static double[] UNKNOW_COLOR_RGB;
}
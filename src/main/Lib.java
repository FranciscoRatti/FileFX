package main;

import entity.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import node.FileLabel;
import panel.*;

import java.awt.datatransfer.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import static main.Main.*;
import static panel.MainPane.*;
import static panel.CenterPane.*;

public class Lib {

// CONSTANTES ----------------------------------------------------------------------------------------------------------

    public static boolean isCut = false;
    public static final String HOME = System.getenv("HOME");
    //public static final String ABSOLUTE_PATH = "/usr/";
    public static final String ABSOLUTE_PATH = HOME+"/Documents/Programacion/Proyectos/FileFX/";

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    public static LinkedList<String> backBuffer = new LinkedList<>();
    public static LinkedList<String> forwardBuffer = new LinkedList<>();
    public static Clipboard clipboard;
    private static File[] clipboardFiles;

// METODOS -------------------------------------------------------------------------------------------------------------

    // Crear componentes
    public static <R> Optional<R> showAlert(Dialog<R> dialog, String message, String title) {
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait();
    }
    public static ContextMenu createContextMenu(
            boolean open, boolean openWith, boolean createFile, boolean createDirectory, boolean rename, boolean copy, boolean cut, boolean paste, boolean trash, boolean remove, boolean shell
    ) {
        ContextMenu contextMenu = new ContextMenu();
        ObservableList<MenuItem> contextMenuItems = contextMenu.getItems();
        MenuItem pasteItem;

        if (open) contextMenuItems.add(createNewOpenItem());
        if (openWith) contextMenuItems.add(createNewOpenWithItem());

        if (createFile) contextMenuItems.add(createNewFileItem());
        if (createDirectory) contextMenuItems.add(createNewDirectoryItem());
        if (rename) contextMenuItems.add(createRenameItem());

        if (copy) contextMenuItems.addAll(new SeparatorMenuItem(), createCopyItem());
        if (cut) contextMenuItems.add(createCutItem());
        if (paste) {contextMenuItems.add(pasteItem = createPasteItem());}
        else pasteItem = null;

        if (trash) contextMenuItems.addAll(new SeparatorMenuItem(), createTrashItem());
        if (remove) contextMenuItems.add(createRemoveItem());

        if (shell) contextMenuItems.addAll(new SeparatorMenuItem(), createOpenShellItem());

        if (Boolean.parseBoolean(config.getProperty("check_clipboard_paste"))) {
            contextMenu.setOnShowing(e -> {
                if (pasteItem != null) {
                    clipboardFiles = getClipboardFiles();
                    if (clipboard != null && clipboardFiles != null) {
                        boolean setDisable = false;
                        for (File file : clipboardFiles) {
                            if (!file.exists()) setDisable = true;
                        }
                        pasteItem.setDisable(setDisable);
                    } else {
                        pasteItem.setDisable(true);
                    }
                }
            });
        }

        return contextMenu;
    }

    private static MenuItem createNewOpenItem() {
        MenuItem item = new MenuItem("Abrir");
        item.setOnAction(e -> {
            if (selectedItem != null) {
                centerPane.openSelected();
            }
        });
        return item;
    }
    private static Menu createNewOpenWithItem() {
        Menu menu = new Menu("Abrir con");
        ObservableList<MenuItem> childrens = menu.getItems();

        Platform.runLater(() -> {
            menu.getParentPopup().setOnShowing(e -> {
                childrens.clear();

                String mimeType = FileProperties.getMimeType();

                for (DesktopApplication app : desktopApplications) {
                    boolean isMimeTypeEqual = false;

                    for (String mimeTypeApp : app.getMimeTypes()) {
                        if (mimeType.equals(mimeTypeApp)) {
                            isMimeTypeEqual = true;
                            break;
                        }
                    }

                    if (isMimeTypeEqual) {
                        ImageView icon = new ImageView(app.getIcon());
                        icon.setPreserveRatio(true);
                        icon.setFitHeight(20);

                        MenuItem item = new MenuItem(app.getName(), icon);
                        item.setOnAction(ev -> {
                            app.openWith(selectedItem);
                        });
                        childrens.add(item);
                    }
                }

                MenuItem others = new MenuItem("Otra...");
                others.setOnAction(ev -> {
                    othersApplicationsStage.showAndWait();
                });
                childrens.add(others);
            });
        });

        return menu;
    }
    private static Menu createNewFileItem() {
        MenuItem item = new MenuItem("Sin formato");
        item.setOnAction(e -> {
            Optional<String> result = showAlert(new TextInputDialog(), "Ingrese nombre del archivo", null);
            if (result.isPresent()) {
                File newFile;
                String fileName = "sin_nombre";
                String input = result.get();

                if (!input.isEmpty()) fileName = input;

                newFile = new File(path+"/"+fileName);
                if (selectedItems.size() == 1) {
                    File selectedFile = selectedItems.getFirst().getFile();
                    if (selectedFile.isDirectory())
                        newFile = new File(selectedFile.getAbsolutePath() + "/" + fileName);
                }
                createNewFile(newFile);
            }
        });

        return new Menu("Crear archivo", new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/context_menu/new_file.png"), item);
    }
    private static MenuItem createNewDirectoryItem() {
        MenuItem item = new MenuItem("Crear carpeta", new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/context_menu/new_dir.png"));
        item.setOnAction(e -> {
            Optional<String> result = showAlert(new TextInputDialog(), "Ingrese nombre de la carpeta", null);
            if (result.isPresent()) {
                File newDirectory;
                String directoryName = "sin_nombre";
                String input = result.get();

                if (!input.isEmpty()) directoryName = input;

                newDirectory = new File(path+"/"+directoryName);
                if (selectedItems.size() == 1) {
                    File selectedFile = selectedItems.getFirst().getFile();
                    if (selectedFile.isDirectory())
                        newDirectory = new File(selectedFile.getAbsolutePath() + "/" + directoryName);
                }
                createNewDirectory(newDirectory);
            }
        });
        return item;
    }
    private static MenuItem createRenameItem() {
        MenuItem item = new MenuItem("Renombrar", new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/context_menu/rename.png"));
        item.setAccelerator(rename);
        item.setOnAction(e -> {
            RightPane.focusName();
        });
        return item;
    }
    private static MenuItem createCopyItem() {
        MenuItem item = new MenuItem("Copiar", new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/context_menu/copy.png"));
        item.setAccelerator(copy);
        item.setOnAction(e -> {
            copyFilesToClipBoard(MainPane.parseFileLabelsToFiles(selectedItems), false);
        });
        return item;
    }
    private static MenuItem createCutItem() {
        MenuItem item = new MenuItem("Cortar", new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/context_menu/cut.png"));
        item.setAccelerator(cut);
        item.setOnAction(e -> {
            copyFilesToClipBoard(MainPane.parseFileLabelsToFiles(selectedItems), true);
        });
        return item;
    }
    private static MenuItem createPasteItem() {
        clipboardFiles = Boolean.parseBoolean(config.getProperty("check_clipboard_paste")) ?
                getClipboardFiles() : null;

        MenuItem item = new MenuItem("Pegar", new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/context_menu/paste.png"));
        item.setAccelerator(paste);
        item.setOnAction(e -> {
            if (clipboardFiles == null) clipboardFiles = getClipboardFiles();
            pasteFiles(clipboardFiles);
        });
        return item;
    }
    private static MenuItem createTrashItem() {
        MenuItem item = new MenuItem("Enviar a papelera", new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/context_menu/trash.png"));
        item.setAccelerator(trash);
        item.setOnAction(e -> {
            trashFiles(MainPane.parseFileLabelsToFiles(selectedItems));
        });
        return item;
    }
    private static MenuItem createRemoveItem() {
        MenuItem item = new MenuItem("Eliminar", new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/context_menu/remove.png"));
        item.setAccelerator(remove);
        item.setOnAction(e -> {
            removeFiles(MainPane.parseFileLabelsToFiles(selectedItems));
        });
        return item;
    }
    private static MenuItem createOpenShellItem() {
        MenuItem item = new MenuItem("Abrir una terminal  ", new ImageView("file://"+ABSOLUTE_PATH+"share/filefx/icons/context_menu/shell.png"));
        item.setAccelerator(open_shell);
        item.setOnAction(e -> {
            openShell();
        });
        return item;
    }
    
    public static void updateAll(boolean topPane, boolean rightPane, boolean bottomPane, boolean leftPane, boolean centerPane) {
        if (topPane) MainPane.topPane.update();
        if (rightPane) MainPane.rightPane.update();
        if (bottomPane) ;
        if (leftPane) MainPane.leftPane.update();
        if (centerPane) MainPane.centerPane.update();
    }
    public static void updateAll() {updateAll(true, true, true, true, true);}

    // Imprimir informacion
    public static void printInfo(String message) {
        System.out.println("[" + BLUE + "INFO" + RESET + "]     "+message);
    }
    public static void printError(String message, Exception e) {
        System.out.println("[" + RED + "ERROR" + RESET + "]    "+message);

        if (e != null) {
            System.out.println("[" + RED + "ERROR" + RESET + "]     "+RED+e.getMessage()+RESET);
            for (StackTraceElement s: e.getStackTrace()) {
                System.out.println("[" + RED + "ERROR" + RESET + "]"+RED+"        ."+s+RESET);
            }

            Throwable cause = e.getCause();
            if (cause != null) {
                System.out.println("[" + RED + "ERROR" + RESET + "]     "+RED+cause.getMessage()+RESET);
                for (StackTraceElement s: cause.getStackTrace()) {
                    System.out.println("[" + RED + "ERROR" + RESET + "]"+RED+"        ."+s+RESET);
                }
            }
        }
    }
    public static void printOk(String message) {
        System.out.println("[" + GREEN + " OK " + RESET + "]     "+message);
    }
    public static void printExecute(String message) {
        System.out.println("[" + YELLOW + "EXEC" + RESET + "]     "+message);
    }

    // Acciones
    public static void back() {
        if (!backBuffer.isEmpty()) {
            printExecute("Retrocediendo");
            forwardBuffer.add(path);
            path = backBuffer.removeLast();
            selectedItem=null;
            selectedItems.clear();
            selectedFile=null;

            updateAll(true, true, false, false, true);
        }
    }
    public static void forward() {
        if (!forwardBuffer.isEmpty()) {
            printExecute("Volviendo");
            backBuffer.add(path);
            path = forwardBuffer.removeLast();
            selectedItem=null;
            selectedItems.clear();
            selectedFile=null;

            updateAll(true, true, false, false, true);
        }
    }
    public static void parent() {
        if (!path.equals("/")) {
            printExecute("Yendo al parent");
            forwardBuffer.clear();
            backBuffer.add(path);
            selectedItem=null;
            selectedItems.clear();
            selectedFile=null;

            String oldPath = path.substring(0, path.length()-1);

            int length = path.length();
            if (length > 1) {
                do {
                    path = path.substring(0, length-1);
                    length = path.length();
                } while(!path.endsWith("/"));
            }

            updateAll(true, true, false, true, true);

            Platform.runLater(() -> {
                for (FileLabel label : fileLabels) {
                    if (label.getFile().getAbsolutePath().equals(oldPath)) {
                        label.setSelected(true);
                        centerPane.setSelectedOnCenter();
                        break;
                    }
                }
            });
        }
    }

    public static void createNewFile(File file) {
        try {
            printExecute("Creando nuevo archivo '"+YELLOW+file+RESET+"'");
            if (!file.createNewFile()) printError("No se pudo crear el archivo "+file, null);
        } catch (Exception ex) {
            printError("No se pudo crear el archivo "+file, ex);
        }
        centerPane.update();
    }
    public static void createNewDirectory(File directory) {
        try {
            printExecute("Creando nuevo directorio '"+YELLOW+directory+RESET+"'");
            if (!directory.mkdir()) printError("No se pudo crear el directorio "+directory, null);
        } catch (Exception ex) {
            printError("No se pudo crear el directorio "+directory, ex);
        }
        centerPane.update();
    }

    public static void renameFile(File file, String newName) {
        if (file != null && newName != null) {
            try {
                String absolutePath = file.getAbsolutePath();
                String newAbsolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/") + 1) + newName;

                printExecute("Renombrando archivo '" + BLUE + absolutePath + RESET + "' a '" + BLUE + newAbsolutePath + RESET + "'");

                ProcessBuilder pb = new ProcessBuilder("mv", file.getAbsolutePath(), newAbsolutePath);
                pb.start();

                selectedItems.clear();
                selectedItem = null;
                updateAll(false, true, false, false, true);
            } catch (Exception e) {
                printError("Error al renombrar '" + file.getAbsolutePath() + "'", e);
            }
        }
    }

    public static void copyToClipBoard(String text, boolean isCut) {
        if (text != null) {
            Lib.isCut = isCut;

            printInfo("Copiando al portapapeles '" + BLUE + text + RESET + "'");
            StringSelection selection = new StringSelection(text);
            clipboard.setContents(selection, null);
        }
    }
    public static void copyFilesToClipBoard(File[] files, boolean isCut) {
        if (files != null) {
            StringBuilder selection = new StringBuilder();
            for (File f: files) {
                selection.append(f.getAbsolutePath()).append(",");
            }
            selection.deleteCharAt(selection.length()-1);
            copyToClipBoard(selection.toString(), isCut);
        }
    }
    public static void pasteFiles(File[] files) {
        if (files != null) {
            printExecute("Pegando portapapeles");

            for (File file : files) {
                if (file.exists()) {
                    String absolutePath = file.getAbsolutePath();
                    String name = file.getName();
                    String destination = path + "/" + name;

                    if (absolutePath.equals(destination)) destination = path + "/(copia) " + name;

                    String operation;
                    ProcessBuilder pb;
                    try {
                        if (isCut) {
                            pb = new ProcessBuilder("mv", absolutePath, destination);
                            operation = "Cortando ";
                        } else {
                            pb = new ProcessBuilder("cp", "-R", absolutePath, destination);
                            operation = "Copiando ";
                        }

                        printExecute(operation + (((file.isDirectory()) ? "directorio" : "archivo") + " '" + YELLOW + absolutePath + RESET + "' a '" + YELLOW + destination + RESET));
                        pb.start().waitFor();
                    } catch (Exception e) {
                        printError("No se pudo pegar '" + file.getAbsolutePath() + "'", e);
                    }
                } else {
                    printError("El archivo '" + file.getAbsolutePath() + "' no existe", null);
                    break;
                }
            }

            updateAll(false, false, false, false, true);
        }
    }
    public static File[] getClipboardFiles() {
        File[] files = null;

        if (clipboard != null && clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            try {
                printExecute("Leyendo porpapeles");
                String clipboardData = (String) clipboard.getData(DataFlavor.stringFlavor);
                String[] filesPath = clipboardData.split(",");

                files = new File[filesPath.length];
                for (int i = 0; i < filesPath.length; i++) {
                    files[i] = new File(filesPath[i]);
                }
            } catch (Exception e) {
                printError("Error al leer porpapeles", e);
            }
        }

        return files;
    }

    public static void trashFiles(File[] files) {
        if (files != null) {
            createTrashInfo(files);
            for (File file : files) {
                try {
                    ProcessBuilder cp;
                    ProcessBuilder rm;
                    if (file.isDirectory()) {
                        printExecute("Moviendo directorio '" + YELLOW + file.getAbsolutePath() + RESET + "' a la papelera");
                        cp = new ProcessBuilder("cp", "-R", file.getAbsolutePath(), HOME + "/.local/share/Trash/files");
                        rm = new ProcessBuilder("rm", "-Rf", file.getAbsolutePath());
                    } else {
                        printExecute("Moviendo archivo '" + YELLOW + file.getAbsolutePath() + RESET + "' a la papelera");
                        cp = new ProcessBuilder("cp", file.getAbsolutePath(), HOME + "/.local/share/Trash/files");
                        rm = new ProcessBuilder("rm", "-f", file.getAbsolutePath());
                    }
                    cp.start();
                    rm.start();
                } catch (Exception e) {
                    printError("Error al enviar a la papelera el archivo " + file.getAbsolutePath(), e);
                    break;
                }
            }

            selectedItems.clear();
            selectedItem = null;
            updateAll(false, true, false, false, true);
        }
    }
    private static void createTrashInfo(File[] files) {
        if (files != null) {
            for (File file: files) {
                if (file.isDirectory()) {
                    createTrashInfo(file.listFiles());
                } else {
                    File trashInfo = new File(HOME+"/.local/share/Trash/info/"+file.getName()+".trashinfo");
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(trashInfo))) {
                        printExecute("Creando nuevo archivo '"+YELLOW+trashInfo.getAbsolutePath()+RESET+"'");
                        trashInfo.createNewFile();
                        writer.write("[Trash Info]"); writer.newLine();
                        writer.write("Path="+file.getAbsolutePath()); writer.newLine();
                        writer.write("DeletionDate="+ LocalDateTime.now().toString());
                    } catch (IOException e) {
                        printError("Error al crear archivo el '"+file.getAbsolutePath()+".trashinfo'", e);
                        break;
                    }
                }
            }
        }
    }
    public static void removeFiles(File[] files) {
        if (files != null) {
            Optional<ButtonType> result;
            String message = "";
            if (files.length == 1) {
                String type = files[0].isDirectory() ? "directorio '" : "archivo '";
                message = "El " + type + files[0].getAbsolutePath() + "'\nsera eliminado permanentemente";
            } else if (files.length > 1)
                message = "Los multiples archivos y/o directorios\nseran eliminados permanentemente";

            if (!message.isEmpty()) {
                result = showAlert(new Alert(Alert.AlertType.CONFIRMATION), message, "ADVERTENCIA");
                if (result.isPresent()) {
                    ButtonBar.ButtonData option = result.get().getButtonData();
                    if (option.equals(ButtonBar.ButtonData.OK_DONE)) {
                        for (File file : files) {
                            try {
                                ProcessBuilder rm;
                                if (file.isDirectory()) {
                                    printExecute("Eliminando directorio '" + YELLOW + file.getAbsolutePath() + RESET + "'");
                                    rm = new ProcessBuilder("rm", "-Rf", file.getAbsolutePath());
                                } else {
                                    printExecute("Eliminando archivo '" + YELLOW + file.getAbsolutePath() + RESET + "'");
                                    rm = new ProcessBuilder("rm", "-f", file.getAbsolutePath());
                                }
                                rm.start();
                            } catch (Exception e) {
                                printError("Error al eliminar el archivo " + file.getAbsolutePath(), e);
                                break;
                            }
                        }
                    }
                }
            }

            selectedItems.clear();
            selectedItem = null;
            updateAll(false, true, false, false, true);
        }
    }

    public static void openShell() {
        try {
            String shellPath = path;
            if (selectedItem != null) {
                File dir = selectedItem.getFile();
                if (dir.isDirectory()) shellPath = dir.getAbsolutePath();
            }

            ProcessBuilder pb = new ProcessBuilder("x-terminal-emulator").directory(new File(shellPath));
            pb.start();
        } catch (IOException ex) {
            printError("Error al ejecutar el comando 'x-terminal-emulator'", ex);
        }
    }
}
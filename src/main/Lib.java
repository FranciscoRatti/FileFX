package main;

import entity.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import node.CenterNode;
import panel.RightPane;

import java.awt.datatransfer.*;
import java.io.*;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.*;

import static main.FileFX.*;
import static panel.CenterPane.*;
import static panel.MainPane.*;

public class Lib {

    // CONSTANTES ----------------------------------------------------------------------------------------------------------

    public static boolean isCut = false;
    public static final String HOME = System.getenv("HOME");
    public static final String TRASH = HOME+"/.local/share/Trash/";
    public static final String ABSOLUTE_PATH = "/usr/";
    //public static final String ABSOLUTE_PATH = HOME+"/Documents/Programacion/Proyectos/FileFX/";
    public static final String CONFIG_PATH = HOME + "/.config/filefx/";
    //public static final String CONFIG_PATH = ABSOLUTE_PATH+"share/filefx/";

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    public enum ORDER {NAME, DATE, SIZE, MIME}
    public enum COLUMNS {PERMISSIONS, OWNER, GROUP, SIZE, MODIFIED, CREATED, TYPE}

    public static final LinkedList<String> backBuffer = new LinkedList<>();
    public static final LinkedList<String> forwardBuffer = new LinkedList<>();
    public static Clipboard clipboard;
    private static File[] clipboardFiles;
    public static final Lock lock = new ReentrantLock();

    // METODOS -------------------------------------------------------------------------------------------------------------

    // Crear componentes
    public static <R> Optional<R> showAlert(Dialog<R> dialog, String message, String title) {
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(message);
        return dialog.showAndWait();
    }

    public static ContextMenu createContextMenu(
              int open, int openWith,
              int createFile, int createDirectory,
              int rename, int copy, int cut, int paste,
              int restore, int trash, int remove,
              int shell)
    {
        ContextMenu contextMenu = new ContextMenu();
        ObservableList<MenuItem> contextMenuItems = contextMenu.getItems();
        MenuItem pasteItem;

        String[] icons = new String[CONTEXT_MENU_ICONS.length];
        System.arraycopy(CONTEXT_MENU_ICONS, 0, icons, 0, CONTEXT_MENU_ICONS.length);

        if (open == 1) contextMenuItems.add(createNewOpenItem(icons[0]));
        if (openWith == 1) contextMenuItems.add(createNewOpenWithItem(icons[1]));

        if (createFile == 1) contextMenuItems.add(createNewFileItem(icons[2]));
        if (createDirectory == 1) contextMenuItems.add(createNewDirectoryItem(icons[3]));
        if (rename == 1) contextMenuItems.add(createRenameItem(icons[4]));

        if (copy == 1) contextMenuItems.addAll(new SeparatorMenuItem(), createCopyItem(icons[5]));
        if (cut == 1) contextMenuItems.add(createCutItem(icons[6]));
        if (paste == 1) contextMenuItems.add(pasteItem = createPasteItem(icons[7]));
        else pasteItem = null;

        if (restore == 1) contextMenuItems.addAll(new SeparatorMenuItem(), createRestoreItem(icons[8]));
        if (trash == 1) contextMenuItems.addAll(new SeparatorMenuItem(), createTrashItem(icons[9]));
        if (remove == 1) contextMenuItems.add(createRemoveItem(icons[10]));

        if (shell == 1) contextMenuItems.addAll(new SeparatorMenuItem(), createOpenShellItem(icons[11]));

        if (CHECK_CLIPBOARD_PASTE) {
            contextMenu.setOnShowing(e -> {
                if (pasteItem != null) {
                    clipboardFiles = getClipboardFiles();
                    if (clipboard != null && clipboardFiles != null) {
                        boolean setDisable = false;
                        for (File file : clipboardFiles) {
                            if (!file.exists())
                                setDisable = true;
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

    private static MenuItem createNewOpenItem(String icon) {
        MenuItem item = new MenuItem("Abrir", createIconItem(icon));
        item.setOnAction(e -> {
            if (centerPane.selectedItem != null) {
                centerPane.openSelected();
            }
        });
        return item;
    }
    private static Menu createNewOpenWithItem(String icon) {
        Menu menu = new Menu("Abrir con", createIconItem(icon));
        ObservableList<MenuItem> childrens = menu.getItems();

        Platform.runLater(() -> menu.getParentPopup().setOnShowing(e -> {
            lock.lock();
            childrens.clear();

            String mimeType = centerPane.selectedItem.getFileProperties().getMimeType();
            for (DesktopApplication app : desktopApplications) {
                boolean isMimeTypeEqual = false;

                for (String mimeTypeApp : app.getMimeTypes()) {
                    if (mimeType.equals(mimeTypeApp)) {
                        isMimeTypeEqual = true;
                        break;
                    }
                }

                if (isMimeTypeEqual) {
                ImageView imageIcon = new ImageView(app.getIcon());
                imageIcon.setPreserveRatio(true);
                imageIcon.setFitHeight(20);

                MenuItem item = new MenuItem(app.getName(), imageIcon);
                item.setOnAction(ev -> {
                    app.openWith(centerPane.selectedItem);
                });
                childrens.add(item);
              }
            }

            MenuItem others = new MenuItem("Otra...");
            others.setOnAction(ev ->
                    othersApplicationsStage.showAndWait()
            );
            childrens.add(others);
            lock.unlock();
        }));

        return menu;
    }
    private static Menu createNewFileItem(String icon) {
        MenuItem item = new MenuItem("Sin formato");
        item.setOnAction(e -> {
            Optional<String> result = showAlert(new TextInputDialog(), "Ingrese nombre del archivo", null);
            if (result.isPresent()) {
            File newFile;
            String fileName = "sin_nombre";
            String input = result.get();

            if (!input.isEmpty()) fileName = input;

            newFile = new File(path + "/" + fileName);
            if (centerPane.selectedItems.size() == 1) {
                File selectedFile = centerPane.selectedItems.getFirst().getFile();
                if (selectedFile.isDirectory()) newFile = new File(selectedFile.getAbsolutePath() + "/" + fileName);
            }
            createNewFile(newFile);
          }
        });

        return new Menu("Crear archivo", createIconItem(icon), item);
    }
    private static MenuItem createNewDirectoryItem(String icon) {
      MenuItem item = new MenuItem("Crear carpeta", createIconItem(icon));
      item.setOnAction(e -> {
          Optional<String> result = showAlert(new TextInputDialog(), "Ingrese nombre de la carpeta", null);
          if (result.isPresent()) {
          File newDirectory;
          String directoryName = "sin_nombre";
          String input = result.get();

          if (!input.isEmpty()) directoryName = input;

          newDirectory = new File(path + "/" + directoryName);
          if (centerPane.selectedItems.size() == 1) {
              File selectedFile = centerPane.selectedItems.getFirst().getFile();
              if (selectedFile.isDirectory()) newDirectory = new File(selectedFile.getAbsolutePath() + "/" + directoryName);
          }
          createNewDirectory(newDirectory);
        }
      });
      return item;
    }
    private static MenuItem createRenameItem(String icon) {
        MenuItem item = new MenuItem("Renombrar", createIconItem(icon));
        if (RENAME != null) item.setAccelerator(RENAME[0]);
        item.setOnAction(e -> {
            RightPane.focusName();
        });
        return item;
    }
    private static MenuItem createCopyItem(String icon) {
        MenuItem item = new MenuItem("Copiar", createIconItem(icon));
        if (COPY != null) item.setAccelerator(COPY[0]);
        item.setOnAction(e -> {
            copyFilesToClipBoard(parseCenterNodesToFiles(centerPane.selectedItems), false);
        });
        return item;
    }
    private static MenuItem createCutItem(String icon) {
        MenuItem item = new MenuItem("Cortar", createIconItem(icon));
        if (CUT != null) item.setAccelerator(CUT[0]);
        item.setOnAction(e -> {
            copyFilesToClipBoard(parseCenterNodesToFiles(centerPane.selectedItems), true);
        });
        return item;
    }
    private static MenuItem createPasteItem(String icon) {
        clipboardFiles = CHECK_CLIPBOARD_PASTE ? getClipboardFiles() : clipboardFiles;

        MenuItem item = new MenuItem("Pegar", createIconItem(icon));
        if (PASTE != null) item.setAccelerator(PASTE[0]);
        item.setOnAction(e -> {
            if (clipboardFiles == null) clipboardFiles = getClipboardFiles();
            pasteFiles(clipboardFiles);
        });
        return item;
    }
    private static MenuItem createRestoreItem(String icon) {
        MenuItem item = new MenuItem("Restaurar", createIconItem(icon));
        item.setOnAction(e -> {
            restoreSelected();
        });
        return item;
    }
    private static MenuItem createTrashItem(String icon) {
        MenuItem item = new MenuItem("Enviar a papelera", createIconItem(icon));
        if (FileFX.TRASH != null) item.setAccelerator(FileFX.TRASH[0]);
        item.setOnAction(e -> {
            trashFiles(parseCenterNodesToFiles(centerPane.selectedItems));
        });
        return item;
    }
    private static MenuItem createRemoveItem(String icon) {
        MenuItem item = new MenuItem("Eliminar", createIconItem(icon));
        if (REMOVE != null) item.setAccelerator(REMOVE[0]);
        item.setOnAction(e -> {
            removeFiles(parseCenterNodesToFiles(centerPane.selectedItems));
        });
        return item;
    }
    private static MenuItem createOpenShellItem(String icon) {
      MenuItem item = new MenuItem("Abrir una terminal ", createIconItem(icon));
      if (OPEN_SHELL != null) item.setAccelerator(OPEN_SHELL[0]);
      item.setOnAction(e -> {
          openShell();
      });
      return item;
    }
    private static Label createIconItem(String text) {
        Label icon = new Label(text);
        icon.setFont(nerdFont);
        icon.setId("ContexMenu_icon");
        return icon;
    }

    public static void updateTop() {
        topPane.update();
    }
    public static void updateRight() {
        rightPane.update();
    }
    public static void updateLeft() {
        leftPane.update();
    }
    public static void updateCenter() {
        centerPane.update();
    }
    public static void updateAll() {
        updateTop();
        updateLeft();
        updateCenter();
        centerPane.selectFirst();
        updateRight();
    }

    // Imprimir informacion
    public static void printInfo(String message) {System.out.println("[" + BLUE + "INFO" + RESET + "]     " + message);}
    public static void printError(String message, Exception e) {
        showAlert(new Alert(Alert.AlertType.ERROR), message, "ERROR");

        System.out.println("[" + RED + "ERROR" + RESET + "]    " + message);

        if (e != null) {
            System.out.println("[" + RED + "ERROR" + RESET + "]     " + RED + e.getMessage() + RESET);
            for (StackTraceElement s : e.getStackTrace()) {
              System.out.println("[" + RED + "ERROR" + RESET + "]" + RED + "        ." + s + RESET);
            }

            Throwable cause = e.getCause();
            if (cause != null) {
                System.out.println("[" + RED + "ERROR" + RESET + "]     " + RED + cause.getMessage() + RESET);
                for (StackTraceElement s : cause.getStackTrace()) System.out.println("[" + RED + "ERROR" + RESET + "]" + RED + "        ." + s + RESET);
            }
        }
    }
    public static void printOk(String message) {System.out.println("[" + GREEN + " OK " + RESET + "]     " + GREEN + message + RESET);}
    public static void printExecute(String message) {System.out.println("[" + YELLOW + "EXEC" + RESET + "]     " + message);}

    // Acciones
    public static void back() {
        if (!backBuffer.isEmpty()) {
            printExecute("Retrocediendo");
            forwardBuffer.add(path);
            path = backBuffer.removeLast();
            centerPane.filter = null;

            updateTop();
            updateCenter();
            centerPane.selectFirst();
            updateRight();
        }
    }
    public static void forward() {
        if (!forwardBuffer.isEmpty()) {
            printExecute("Volviendo");
            backBuffer.add(path);
            path = forwardBuffer.removeLast();
            centerPane.filter = null;

            updateTop();
            updateCenter();
            centerPane.selectFirst();
            updateRight();
        }
    }
    public static void parent() {
        if (!path.equals("/")) {
            printExecute("Yendo al parent");
            forwardBuffer.clear();
            backBuffer.add(path);
            centerPane.filter = null;

            String oldPath = path.substring(0, path.length()-1);
            path = Path.of(path).getParent().toString();
            if (!path.equals("/")) path += "/";

            updateCenter();
            updateTop();

            boolean flag = false;
            for (CenterNode label : centerPane.centerNodes) {
                if (label.getFile().getAbsolutePath().equals(oldPath)) {
                    label.setSelected(true);
                    centerPane.setSelectedOnCenter();
                    flag = true;
                    break;
                }
            }

            if (!flag) centerPane.selectFirst();

            updateRight();
        }
    }

    public static void createNewFile(File file) {
        if (!path.startsWith(TRASH+"files")) {
            try {
                printExecute("Creando nuevo archivo '"+YELLOW+file.getAbsolutePath()+RESET+"'");
                if (!file.createNewFile()) printError("No se pudo crear el archivo "+file.getAbsolutePath(), null);
            } catch (Exception ex) {
                printError("No se pudo crear el archivo " + file, ex);
            }

            updateCenter();
            for (CenterNode node : centerPane.centerNodes)
                if (node.getName().equals(file.getName()))
                    node.setSelected(true);
        }
    }
    public static void createNewDirectory(File directory) {
        if (!path.startsWith(TRASH+"files")) {
            try {
                printExecute("Creando nuevo directorio '" + YELLOW + directory + RESET + "'");
                if (!directory.mkdir()) printError("No se pudo crear el directorio " + directory, null);
            } catch (Exception ex) {
                printError("No se pudo crear el directorio " + directory, ex);
            }
            updateCenter();
        }
    }
    public static void renameFile(File file, String newName) {
        if (file != null && newName != null) {
            try {
                String absolutePath = file.getAbsolutePath();
                String newAbsolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/") + 1) + newName;

                printExecute("Renombrando archivo '" + BLUE + absolutePath + RESET + "' a '" + BLUE + newAbsolutePath + RESET + "'");

                ProcessBuilder pb = new ProcessBuilder("mv", file.getAbsolutePath(), newAbsolutePath);
                pb.start().waitFor();

                centerPane.selectedItems.clear();
                centerPane.selectedItem = null;

                updateRight();
                updateCenter();
                for (CenterNode centerNode : centerPane.centerNodes) {
                    if (centerNode.getName().equals(newName)) {
                        centerNode.setSelected(true);
                        centerNode.requestFocus();
                        break;
                    }
                }
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
            for (File f : files) {
                selection.append(f.getAbsolutePath()).append(",");
            }
            selection.deleteCharAt(selection.length() - 1);
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

            updateCenter();
            centerPane.selectFirst();
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
                for (int i = 0; i < filesPath.length; i++) files[i] = new File(filesPath[i]);
            } catch (Exception e) {
                printError("Error al leer porpapeles", e);
            }
        }

        return files;
    }

    public static void restoreFiles(File[] files) {
        for (File file : files) {
            printExecute("Restaurando archivo '"+YELLOW+file.getAbsolutePath().substring(TRASH.length()+6)+RESET+"'");

            File[] childrens = null;
            boolean isDirectory = file.isDirectory();
            if (isDirectory) childrens = file.listFiles();

            FileProperties properties = new FileProperties(file);
            String trashPath = properties.getTrashPath();
            File trashInfo = properties.getTrashInfo();

            if (trashPath != null && file.exists()) {
                try {

                    // Si es directorio
                    if (isDirectory) {
                        new ProcessBuilder("mkdir", "-p", trashPath)
                                .start().waitFor();

                    // Si es archivo
                    } else {
                        new ProcessBuilder("mkdir", "-p", Path.of(trashPath).getParent().toString())
                                .start().waitFor();
                        new ProcessBuilder("mv", file.getAbsolutePath(), trashPath)
                                .start().waitFor();
                        file.delete();
                    }

                    trashInfo.delete();
                } catch (Exception e) {
                    printError("No se pudo mover el archivo '"+file.getName()+"'", e);
                }
            } else {
                printError("No se encontro archivo trash info de '"+file.getName()+"'", null);
                continue;
            }

            if (childrens != null) restoreFiles(childrens);
            if (isDirectory) file.delete();
        }
    }
    public static void restoreSelected() {
        if (centerPane.selectedItems != null && !centerPane.selectedItems.isEmpty()) {
            File[] files = new File[centerPane.selectedItems.size()];
            for (int i = 0; i < files.length; i++) {
                files[i] = centerPane.selectedItems.get(i).getFile();
            }

            restoreFiles(files);

            updateCenter();
            updateRight();
        }
    }
    public static void trashFiles(File[] files) {
        if (files != null && !path.startsWith(TRASH+"files")) {
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

            centerPane.selectedItems.clear();
            centerPane.selectedItem = null;

            updateRight();
            updateCenter();
        }
        else {removeFiles(files);}
    }
    private static void createTrashInfo(File[] files) {
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) createTrashInfo(file.listFiles());

                File trashInfo = new File(HOME + "/.local/share/Trash/info/" + file.getName() + ".trashinfo");
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(trashInfo))) {
                    printExecute("Creando nuevo archivo '" + YELLOW + trashInfo.getAbsolutePath() + RESET + "'");
                    trashInfo.createNewFile();
                    writer.write("[Trash Info]");
                    writer.newLine();
                    writer.write("Path=" + (file.isDirectory() ? file.getAbsolutePath()+"/" : file.getAbsolutePath()) );
                    writer.newLine();
                    writer.write("DeletionDate=" + LocalDateTime.now());
                } catch (IOException e) {
                    printError("Error al crear archivo el '" + file.getAbsolutePath() + ".trashinfo'", e);
                    break;
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
            } else if (files.length > 1) message = "Los archivos y/o directorios\nseran eliminados permanentemente";

            if (files.length > 0) {
                result = showAlert(new Alert(Alert.AlertType.CONFIRMATION), message, "ADVERTENCIA");
                if (result.isPresent()) {
                    ButtonBar.ButtonData option = result.get().getButtonData();
                    if (option.equals(ButtonBar.ButtonData.OK_DONE)) {
                        for (File file : files) {
                            if (!(file.getAbsolutePath()+"/").equals(path)) {
                                try {
                                    ProcessBuilder pb;
                                    if (file.isDirectory()) {
                                        printExecute("Eliminando directorio '" + YELLOW + file.getAbsolutePath() + RESET + "'");
                                        pb = new ProcessBuilder("rm", "-Rf", file.getAbsolutePath());
                                    } else {
                                        printExecute("Eliminando archivo '" + YELLOW + file.getAbsolutePath() + RESET + "'");
                                        pb = new ProcessBuilder("rm", "-f", file.getAbsolutePath());
                                    }
                                    pb.start().waitFor();
                                } catch (Exception e) {
                                    printError("Error al eliminar el archivo " + file.getAbsolutePath(), e);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            centerPane.selectedItems.clear();
            centerPane.selectedItem = null;

            updateRight();
            updateCenter();
        }
    }

    public static void openShell() {
        try {
            String shellPath = path;
            if (centerPane.selectedItem != null) {
                File dir = centerPane.selectedItem.getFile();
                if (dir.isDirectory()) shellPath = dir.getAbsolutePath();
            }

            ProcessBuilder pb = new ProcessBuilder(TERMINAL).directory(new File(shellPath));
            pb.start();
        } catch (IOException ex) {
            printError("Error al abrir la terminal '"+TERMINAL+"'", ex);
        }
    }
}
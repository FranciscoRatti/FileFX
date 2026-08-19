package main;

import entity.*;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import node.CenterNode;
import panel.RightPane;
import stage.OthersApplicationsStage;
import stage.PasswordStage;
import stage.PermissionsStage;

import java.io.*;
import java.nio.file.Files;
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
  public static final String USER = System.getenv("USER");

  public static final String TRASH = HOME + "/.local/share/Trash/";
  public static final String ABSOLUTE_PATH = "/usr/share/filefx/";
  //public static final String ABSOLUTE_PATH = HOME+"/Documents/Programacion/Proyectos/FileFX/resources/";
  public static final String CONFIG_PATH = HOME + "/.config/filefx/";
  //public static final String CONFIG_PATH = ABSOLUTE_PATH;
  public static final String LIB_PATH = "/usr/lib/filefx/";
  public static String THEME_PATH = CONFIG_PATH+"theme.css";

  public static final String RESET = "\u001B[0m";
  public static final String RED = "\u001B[31m";
  public static final String GREEN = "\u001B[32m";
  public static final String YELLOW = "\u001B[33m";
  public static final String BLUE = "\u001B[34m";

  public enum ORDER {NAME, DATE, SIZE, MIME}
  public enum COLUMNS {PERMISSIONS, OWNER, GROUP, SIZE, MODIFIED, CREATED, TYPE}
  public enum ITEMS {
    BACKWARD, FORWARD, OPEN, OPEN_WITH, CREATE_FILE, CREATE_DIR, CREATE_LINK, RENAME, PERMISSIONS,
    COPY, CUT, PASTE, RESTORE, TRASH, REMOVE, EXTRACT, COMPRESS, SHELL, ADMIN, SEPARATOR
  }

  public static final LinkedList<String> backBuffer = new LinkedList<>();
  public static final LinkedList<String> forwardBuffer = new LinkedList<>();
  public static final Lock lock = new ReentrantLock();

  public static OthersApplicationsStage othersApplicationsStage;
  public static PermissionsStage permissionsStage;
  public static PasswordStage passwordStage;

  // METODOS -------------------------------------------------------------------------------------------------------------

  // Crear componentes
  public static <R> Optional<R> showAlert(Dialog<R> dialog, String message, String title) {
    dialog.setTitle(title);
    dialog.setHeaderText(null);
    dialog.setContentText(message);
    return dialog.showAndWait();
  }

  public static ContextMenu createContextMenu(
        int backward, int forward,
        int open, int openWith, int createFile, int createDir, int createLink,
        int rename, int permissions,
        int copy, int cut, int paste,
        int restore, int trash, int remove,
        int extract, int compress, int shell, int admin) {
    ContextMenu contextMenu = new ContextMenu();
    contextMenu.setAutoHide(true);
    ObservableList<MenuItem> contextMenuItems = contextMenu.getItems();
    MenuItem pasteItem = null;
    MenuItem extractHereItem = null;

    for (int i = 0; i < CONTEXT_MENU_ITEMS.length; i++) {
      ITEMS item = CONTEXT_MENU_ITEMS[i];
      switch (item) {
        case SEPARATOR ->   contextMenuItems.add(new SeparatorMenuItem());
        case BACKWARD ->    {if (backward == 1)    contextMenuItems.add(createNewBackwardItem(CONTEXT_MENU_ICONS[i]));}
        case FORWARD ->     {if (forward == 1)     contextMenuItems.add(createNewForwardItem(CONTEXT_MENU_ICONS[i]));}
        case OPEN ->        {if (open == 1)        contextMenuItems.add(createNewOpenItem(CONTEXT_MENU_ICONS[i]));}
        case OPEN_WITH ->   {if (openWith == 1)    contextMenuItems.add(createNewOpenWithItem(CONTEXT_MENU_ICONS[i]));}
        case CREATE_FILE -> {if (createFile == 1)  contextMenuItems.add(createNewFileItem(CONTEXT_MENU_ICONS[i]));}
        case CREATE_DIR ->  {if (createDir == 1)   contextMenuItems.add(createNewDirectoryItem(CONTEXT_MENU_ICONS[i]));}
        case CREATE_LINK -> {if (createLink == 1)  contextMenuItems.add(createNewLinkItem(CONTEXT_MENU_ICONS[i]));}
        case RENAME ->      {if (rename == 1)      contextMenuItems.add(createRenameItem(CONTEXT_MENU_ICONS[i]));}
        case PERMISSIONS -> {if (permissions == 1) contextMenuItems.add(createPermissionsItem(CONTEXT_MENU_ICONS[i]));}
        case COPY ->        {if (copy == 1)        contextMenuItems.add(createCopyItem(CONTEXT_MENU_ICONS[i]));}
        case CUT ->         {if (cut == 1)         contextMenuItems.add(createCutItem(CONTEXT_MENU_ICONS[i]));}
        case PASTE ->       {if (paste == 1)       contextMenuItems.add(pasteItem = createPasteItem(CONTEXT_MENU_ICONS[i]));}
        case RESTORE ->     {if (restore == 1)     contextMenuItems.add(createRestoreItem(CONTEXT_MENU_ICONS[i]));}
        case TRASH ->       {if (trash == 1)       contextMenuItems.add(createTrashItem(CONTEXT_MENU_ICONS[i]));}
        case REMOVE ->      {if (remove == 1)      contextMenuItems.add(createRemoveItem(CONTEXT_MENU_ICONS[i]));}
        case EXTRACT ->     {if (extract == 1)     contextMenuItems.add(extractHereItem = createExtractHereItem(CONTEXT_MENU_ICONS[i]));}
        case COMPRESS ->    {if (compress == 1)    contextMenuItems.add(createCompressItem(CONTEXT_MENU_ICONS[i]));}
        case SHELL ->       {if (shell == 1)       contextMenuItems.add(createOpenShellItem(CONTEXT_MENU_ICONS[i]));}
        case ADMIN ->       {if (admin == 1)       contextMenuItems.add(createAdminItem(CONTEXT_MENU_ICONS[i]));}
      }
    }

    MenuItem finalPasteItem = pasteItem;
    MenuItem finalExtractHereItem = extractHereItem;
    contextMenu.setOnShown(e -> {
      if (CHECK_CLIPBOARD_PASTE) {
        if (finalPasteItem != null) {
          File[] clipboardFiles = getClipboardFiles();
          if (clipboardFiles != null) {
            boolean setDisable = false;
            for (File file : clipboardFiles) {
              if (!file.exists()) setDisable = true;
            }
            finalPasteItem.setDisable(setDisable);
          } else {
            finalPasteItem.setDisable(true);
          }
        }
      }

      if (finalExtractHereItem != null) {
        CenterNode selectedItem = centerPane.selectionModel.getSelectedItem();
        String extension = selectedItem.getExtension();
        String mimeType = selectedItem.getFileProperties().getMimeType();
        finalExtractHereItem.setDisable((extension == null || (!extension.equals("zip") &&
            !extension.equals("tar") &&
            !extension.equals("gz") &&
            !extension.equals("bz2") &&
            !extension.equals("xz") &&
            !extension.equals("zstp") &&
            !extension.equals("7z") &&
            !extension.equals("rar"))) && (!mimeType.equals("application/zip") &&
                !mimeType.equals("application/gzip") &&
                !mimeType.equals("application/x-tar") &&
                !mimeType.equals("application/x-bzip2") &&
                !mimeType.equals("application/x-xz") &&
                !mimeType.equals("application/x-zstd") &&
                !mimeType.equals("application/x-7z-compressed") &&
                !mimeType.equals("application/x-rar-compressed")));
      }
    });

    return contextMenu;
  }

  private static MenuItem createNewBackwardItem(String icon) {
    MenuItem item = new MenuItem("Deshacer", createIconItem(icon));
    item.setAccelerator(BACKWARD[0]);
    item.setOnAction(e -> backward());
    return item;
  }
  private static MenuItem createNewForwardItem(String icon) {
    MenuItem item = new MenuItem("Rehacer", createIconItem(icon));
    item.setAccelerator(FORWARD[0]);
    item.setOnAction(e -> forward());
    return item;
  }
  private static MenuItem createNewOpenItem(String icon) {
    MenuItem item = new MenuItem("Abrir", createIconItem(icon));
    item.setAccelerator(OPEN[0]);
    item.setOnAction(e -> centerPane.openSelected());
    return item;
  }
  private static Menu createNewOpenWithItem(String icon) {
    Menu menu = new Menu("Abrir con", createIconItem(icon));
    ObservableList<MenuItem> childrens = menu.getItems();

    Platform.runLater(() -> menu.getParentPopup().setOnShowing(e -> {
      childrens.clear();

      String mimeType = centerPane.selectionModel.getSelectedItem().getFileProperties().getMimeType();
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
          item.setOnAction(ev -> app.openWith(centerPane.selectionModel.getSelectedItem()));
          childrens.add(item);
        }
      }

      MenuItem others = new MenuItem("Otra...");
      others.setOnAction(ev -> {
        lock.lock();
        othersApplicationsStage.showAndWait();
        lock.unlock();
      });
      childrens.add(others);
    }));

    return menu;
  }
  private static Menu createNewFileItem(String icon) {
    File[] templates = new File(TEMPLATES_DIR).listFiles();
    MenuItem[] templatesItems = new MenuItem[templates == null ? 0 : templates.length];
    for (int i = 0; i < templatesItems.length; i++) {
      templatesItems[i] = createNewTemplateFileItem(new FileProperties(templates[i]));
    }

    MenuItem withoutFormatItem = new MenuItem("Sin formato", createIconItem((String) iconsMime.getOrDefault("inode/x-empty", "")));
    withoutFormatItem.setOnAction(e -> {
      Optional<String> result = showAlert(new TextInputDialog(), "Ingrese nombre del archivo", null);
      if (result.isPresent()) {
        File newFile;
        String fileName = "sin_nombre";
        String input = result.get();

        if (!input.isEmpty())
          fileName = input;

        newFile = new File(path + "/" + fileName);
        if (centerPane.selectedItems.size() == 1) {
          File selectedFile = centerPane.selectedItems.getFirst().getFileProperties();
          if (selectedFile.isDirectory())
            newFile = new File(selectedFile.getAbsolutePath() + "/" + fileName);
        }
        createNewFile(newFile);
      }
    });

    Menu menu = new Menu("Crear archivo", createIconItem(icon), templatesItems);
    menu.getItems().add(withoutFormatItem);
    return menu;
  }
  private static MenuItem createNewTemplateFileItem(FileProperties template) {
    String name = template.getName();
    String extension = name.contains(".") && !template.isDirectory ? name.substring(name.lastIndexOf('.')+1) : null;

    String icon;
    if (template.canRead()) {
      icon = iconsExtension.getProperty("."+extension);
      if (icon == null) icon = iconsMime.getProperty(template.getMimeType());
      if (icon == null) icon = iconsMime.getProperty("unknow");
    } else {
      icon = iconsMime.getProperty("lock");
    }

    MenuItem item = new MenuItem(template.getName(), createIconItem(icon));
    item.setOnAction(e -> {
      Optional<String> result = showAlert(new TextInputDialog(), "Ingrese nombre del archivo", null);

      if (result.isPresent()) {
        String fileName = "sin_nombre";
        String input = result.get();
        if (!input.isEmpty()) fileName = input;

        File newFile = new File(path + "/" + fileName);

        File selectedFile = centerPane.selectionModel.getSelectedItem().getFileProperties();
        if (selectedFile.isDirectory())
          newFile = new File(selectedFile.getAbsolutePath() + "/" + fileName);

        try {
          printExecute("Creando nuevo archivo a partir de una plantilla '" + YELLOW + template.getAbsolutePath() + RESET + "'");
          new ProcessBuilder("cp", template.getAbsolutePath(), newFile.getAbsolutePath())
                  .start().waitFor();

          updateCenter();
          centerPane.select(template.getName());
          updateRight();
        } catch (Exception ex) {
          printError("Error al crear archivo '"+result.get()+"'", ex);
        }
      }
    });
    return item;
  }
  private static MenuItem createNewDirectoryItem(String icon) {
    MenuItem item = new MenuItem("Crear carpeta", createIconItem(icon));
    item.setOnAction(e -> {
      Optional<String> result = showAlert(new TextInputDialog(), "Ingrese nombre de la carpeta", null);
      if (result.isPresent()) {
        File newDirectory;
        String directoryName = "sin_nombre";
        String input = result.get();

        if (!input.isEmpty())
          directoryName = input;

        newDirectory = new File(path + "/" + directoryName);
        if (centerPane.selectedItems.size() == 1) {
          File selectedFile = centerPane.selectedItems.getFirst().getFileProperties();
          if (selectedFile.isDirectory())
            newDirectory = new File(selectedFile.getAbsolutePath() + "/" + directoryName);
        }
        createNewDirectory(newDirectory);
      }
    });
    return item;
  }
  private static MenuItem createNewLinkItem(String icon) {
    MenuItem item = new MenuItem("Crear enlace", createIconItem(icon));
    item.setOnAction(e -> createLink(centerPane.selectionModel.getSelectedItem().getFileProperties()));
    return item;
  }
  private static MenuItem createPermissionsItem(String icon) {
    MenuItem item = new MenuItem("Permisos", createIconItem(icon));
    item.setAccelerator(CHANGE_PERMISSIONS[0]);
    item.setOnAction(e -> showPermissionsStage());
    return item;
  }
  private static MenuItem createRenameItem(String icon) {
    MenuItem item = new MenuItem("Renombrar", createIconItem(icon));
    item.setAccelerator(RENAME[0]);
    item.setOnAction(e -> RightPane.focusName());
    return item;
  }
  private static MenuItem createCopyItem(String icon) {
    MenuItem item = new MenuItem("Copiar", createIconItem(icon));
    item.setAccelerator(COPY[0]);
    item.setOnAction(e -> copyFilesToClipBoard(parseCenterNodesToFiles(centerPane.selectedItems), false));
    return item;
  }
  private static MenuItem createCutItem(String icon) {
    MenuItem item = new MenuItem("Cortar", createIconItem(icon));
    item.setAccelerator(CUT[0]);
    item.setOnAction(e -> copyFilesToClipBoard(parseCenterNodesToFiles(centerPane.selectedItems), true));
    return item;
  }
  private static MenuItem createPasteItem(String icon) {
    MenuItem item = new MenuItem("Pegar", createIconItem(icon));
    item.setAccelerator(PASTE[0]);
    item.setOnAction(e -> pasteFiles(getClipboardFiles()));
    return item;
  }
  private static MenuItem createRestoreItem(String icon) {
    MenuItem item = new MenuItem("Restaurar", createIconItem(icon));
    item.setOnAction(e -> restoreSelected());
    return item;
  }
  private static MenuItem createTrashItem(String icon) {
    MenuItem item = new MenuItem("Enviar a papelera", createIconItem(icon));
    item.setAccelerator(FileFX.TRASH[0]);
    item.setOnAction(e -> trashFiles(parseCenterNodesToFiles(centerPane.selectedItems)));
    return item;
  }
  private static MenuItem createRemoveItem(String icon) {
    MenuItem item = new MenuItem("Eliminar", createIconItem(icon));
    item.setAccelerator(REMOVE[0]);
    item.setOnAction(e -> removeFiles(parseCenterNodesToFiles(centerPane.selectedItems)));
    return item;
  }
  private static MenuItem createExtractHereItem(String icon) {
    MenuItem item = new MenuItem("Extraer aqui", createIconItem(icon));
    item.setOnAction(e -> extractHere(centerPane.selectionModel.getSelectedItem().getFileProperties()));
    return item;
  }
  private static MenuItem createCompressItem(String icon) {
    MenuItem item = new MenuItem("Comprimir", createIconItem(icon));
    item.setOnAction(e -> {
      if (!centerPane.selectedItems.isEmpty()) {
        compress(parseCenterNodesToFiles(centerPane.selectedItems));
      }
    });
    return item;
  }
  private static MenuItem createOpenShellItem(String icon) {
    MenuItem item = new MenuItem("Abrir una terminal ", createIconItem(icon));
    item.setAccelerator(OPEN_SHELL[0]);
    item.setOnAction(e -> openShell());
    return item;
  }
  private static MenuItem createAdminItem(String icon) {
    MenuItem item = new MenuItem("Abrir como administrador ", createIconItem(icon));
    item.setOnAction(e -> openWithAdmin());
    return item;
  }
  private static Label createIconItem(String text) {
    Label icon = new Label(text);
    icon.setFont(nerdFont);
    icon.setId("ContextMenu_icon");
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
  public static void printInfo(String message) {
    System.out.println("[" + BLUE + "INFO" + RESET + "]     " + message);
  }
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
        for (StackTraceElement s : cause.getStackTrace())
          System.out.println("[" + RED + "ERROR" + RESET + "]" + RED + "        ." + s + RESET);
      }
    }
  }
  public static void printOk(String message) {
    System.out.println("[" + GREEN + " OK " + RESET + "]     " + GREEN + message + RESET);
  }
  public static void printExecute(String message) {
    System.out.println("[" + YELLOW + "EXEC" + RESET + "]     " + message);
  }
  public static String showPasswordStage(String comand) {
    passwordStage.command.setText(comand);
    passwordStage.showAndWait();
    return passwordStage.password.getText();
  }

  // Acciones
  public static void backward() {
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

      String oldPath = path.substring(0, path.length() - 1);
      path = Path.of(path).getParent().toString();
      if (!path.equals("/"))
        path += "/";

      updateCenter();
      updateTop();

      boolean flag = false;
      for (CenterNode label : centerPane.items) {
        if (label.getFileProperties().getAbsolutePath().equals(oldPath)) {
          centerPane.selectionModel.select(label.getIndex());
          centerPane.setSelectedOnCenter();
          flag = true;
          break;
        }
      }

      if (!flag)
        centerPane.selectFirst();

      updateRight();
    }
  }

  public static void createNewFile(File file) {
    if (!path.startsWith(TRASH + "files")) {
      try {
        printExecute("Creando nuevo archivo '" + YELLOW + file.getAbsolutePath() + RESET + "'");
        if (!file.createNewFile())
          printError("No se pudo crear el archivo " + file.getAbsolutePath(), null);
      } catch (Exception ex) {
        printError("No se pudo crear el archivo " + file, ex);
      }

      updateCenter();
      centerPane.select(file.getName());
      updateRight();
    }
  }
  public static void createNewDirectory(File directory) {
    if (!path.startsWith(TRASH + "files")) {
      try {
        printExecute("Creando nuevo directorio '" + YELLOW + directory + RESET + "'");
        if (!directory.mkdir())
          printError("No se pudo crear el directorio " + directory, null);
      } catch (Exception ex) {
        printError("No se pudo crear el directorio " + directory, ex);
      }

      updateCenter();
      centerPane.select(directory.getName());
      updateRight();
    }
  }
  public static void createLink(File file) {
    try {
      Optional<String> option = showAlert(new TextInputDialog(), "Nombre del enlace", "Crear enlace");
      if (option.isPresent()) {
        printExecute("Creando enlace simbolico de '" + YELLOW + file.getName() + "'");
        Files.createSymbolicLink(Path.of(path + option.get()), file.toPath());

        updateCenter();
        centerPane.selectionModel.clearSelection();
        if (!centerPane.select(option.get()))
          centerPane.selectFirst();
        updateRight();
      }
    } catch (Exception e) {
      printError("Error al crear enlace simbolico de '" + file.getName() + "'", e);
    }
  }

  public static void renameFile(File file, String newName) {
    if (file != null && newName != null) {
      try {
        String absolutePath = file.getAbsolutePath();
        String newAbsolutePath = absolutePath.substring(0, absolutePath.lastIndexOf("/") + 1) + newName;

        printExecute(
            "Renombrando archivo '" + BLUE + absolutePath + RESET + "' a '" + BLUE + newAbsolutePath + RESET + "'");

        ProcessBuilder pb = new ProcessBuilder("mv", file.getAbsolutePath(), newAbsolutePath);
        pb.start().waitFor();

        centerPane.selectionModel.clearSelection();
        updateCenter();
        for (CenterNode centerNode : centerPane.items) {
          if (centerNode.getName().equals(newName)) {
            centerNode.setSelected(true);
            centerNode.requestFocus();
            break;
          }
        }
        updateRight();
      } catch (Exception e) {
        printError("Error al renombrar '" + file.getAbsolutePath() + "'", e);
      }
    }
  }
  public static int changePermission(String value) {
    CenterNode selectedItem = centerPane.selectionModel.getSelectedItem();
    if (selectedItem == null) return 1;
    FileProperties properties = selectedItem.getFileProperties();

    try {
      printExecute("Cambiando permisos de '"+YELLOW+properties.getOctetPermissions()+RESET+"' a '"+YELLOW+value+RESET+"'");
      if (properties.getOwner().equals(USER)) {
        return new ProcessBuilder("chmod", String.valueOf(value), properties.getAbsolutePath())
                .start()
                .waitFor();
      } else {
        String password = showPasswordStage("sudo filefx");
        if (password.isEmpty()) return 1;
        password += "\n";

        Process process = new ProcessBuilder("sudo", "-k", "-S", "chmod", String.valueOf(value), properties.getAbsolutePath())
                .start();

        try (OutputStream output = process.getOutputStream()) {
          output.write(password.getBytes());
          output.flush();
        }

        int exitCode = process.waitFor();
        if (exitCode != 0)
          printError("Contraseña incorrecta", null);
        return exitCode;
      }
    } catch (Exception e) {
      printError("Error al cambiar permisos de '"+selectedItem.getName()+"'", e);
      return 1;
    }
  }
  public static void showPermissionsStage() {
    permissionsStage.update();
    permissionsStage.showAndWait();
  }

  public static void copyToClipBoard(String text) {
    if (text != null) {
      printExecute("Copiando al portapapeles '" + BLUE + text + RESET + "'");

      try {
        Process process = new ProcessBuilder("xclip", "-selection", "c").start();
        try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
          writer.write(text);
        }
      } catch (Exception e) {
        printError("Error al pegar en el portapeles", e);
      }
    }
  }
  public static void copyFilesToClipBoard(File[] files, boolean isCut) {
    if (files != null) {
      Lib.isCut = isCut;
      StringBuilder selection = new StringBuilder();
      for (File f : files) {
        selection.append(f.getAbsolutePath()).append(",");
      }
      selection.deleteCharAt(selection.length() - 1);
      copyToClipBoard(selection.toString());
    }
  }

  public static void pasteFiles(File[] files) {
    if (files != null) {
      printExecute("Pegando portapapeles");

      for (File file : files) {
        if (file.exists()) {
          String absolutePath = file.getAbsolutePath();
          String name = file.getName();
          String destination = path + name;

          if (absolutePath.equals(destination))
            destination = path + "(copia) " + name;

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

            printExecute(operation + (((file.isDirectory()) ? "directorio" : "archivo") + " '" + YELLOW + absolutePath
                + RESET + "' a '" + YELLOW + destination + RESET));
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
    try {
      printExecute("Leyendo porpapeles");

      String clipboardData = getClipboard();
      if (clipboardData == null)
        return null;

      String[] filesPath = clipboardData.split(",");

      File[] files = new File[filesPath.length];

      for (int i = 0; i < filesPath.length; i++)
        files[i] = new File(filesPath[i]);
      return files;
    } catch (Exception e) {
      printError("Error al leer porpapeles", e);
      return null;
    }
  }
  public static String getClipboard() {
    try {
      Process process = new ProcessBuilder("xclip", "-o", "-selection", "c").start();
      try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
        return input.readLine();
      }
    } catch (Exception e) {
      printError("Error al leer portapeles", e);
      return null;
    }
  }

  public static void restoreFiles(File[] files) {
    for (File file : files) {
      printExecute(
          "Restaurando archivo '" + YELLOW + file.getAbsolutePath().substring(TRASH.length() + 6) + RESET + "'");

      File[] childrens = null;
      boolean isDirectory = file.isDirectory();
      if (isDirectory)
        childrens = file.listFiles();

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
          printError("No se pudo mover el archivo '" + file.getName() + "'", e);
        }
      } else {
        printError("No se encontro archivo trash info de '" + file.getName() + "'", null);
        continue;
      }

      if (childrens != null)
        restoreFiles(childrens);
      if (isDirectory)
        file.delete();
    }
  }
  public static void restoreSelected() {
    if (centerPane.selectedItems != null && !centerPane.selectedItems.isEmpty()) {
      File[] files = new File[centerPane.selectedItems.size()];
      for (int i = 0; i < files.length; i++) {
        files[i] = centerPane.selectedItems.get(i).getFileProperties();
      }

      restoreFiles(files);

      updateCenter();
      updateRight();
    }
  }

  public static void trashFiles(File[] files) {
    if (files != null && !path.startsWith(TRASH + "files")) {
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

      centerPane.selectionModel.clearSelection();
      updateCenter();
      centerPane.selectFirst();
      updateRight();
    } else {
      removeFiles(files);
    }
  }
  private static void createTrashInfo(File[] files) {
    if (files != null) {
      for (File file : files) {
        if (file.isDirectory())
          createTrashInfo(file.listFiles());

        File trashInfo = new File(HOME + "/.local/share/Trash/info/" + file.getName() + ".trashinfo");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(trashInfo))) {
          printExecute("Creando nuevo archivo '" + YELLOW + trashInfo.getAbsolutePath() + RESET + "'");
          trashInfo.createNewFile();
          writer.write("[Trash Info]");
          writer.newLine();
          writer.write("Path=" + (file.isDirectory() ? file.getAbsolutePath() + "/" : file.getAbsolutePath()));
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
      } else if (files.length > 1)
        message = "Los archivos y/o directorios\nseran eliminados permanentemente";

      if (files.length > 0) {
        result = showAlert(new Alert(Alert.AlertType.CONFIRMATION), message, "ADVERTENCIA");
        if (result.isPresent()) {
          ButtonBar.ButtonData option = result.get().getButtonData();
          if (option.equals(ButtonBar.ButtonData.OK_DONE)) {
            for (File file : files) {
              if (!(file.getAbsolutePath() + "/").equals(path)) {
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

      centerPane.selectionModel.clearSelection();
      updateCenter();
      centerPane.selectFirst();
      updateRight();
    }
  }

  public static void extractHere(File file) {
    try {
      printExecute("Descomprimiendo '" + YELLOW + file.getName() + RESET + "'");
      String absolutePath = file.getAbsolutePath();
      File output = new File(absolutePath.substring(0,
          absolutePath.length() - (file.getName().length() - file.getName().lastIndexOf("."))));
      output.mkdir();

      new ProcessBuilder("tar", "-xzf", file.getName(), "-C", output.getName()).directory(new File(path)).start()
          .waitFor();

      updateCenter();
      centerPane.selectFirst();
      updateRight();
    } catch (Exception e) {
      printError("Error al descomprimir archivo '" + file.getName() + "'", e);
    }
  }
  public static void compress(File[] files) {
    String[] paths = new String[files.length];
    for (int i = 0; i < files.length; i++) {
      paths[i] = files[i].getName();
    }

    try {
      List<String> command = new ArrayList<>();
      command.add("tar");
      command.add("-czf");

      Optional<String> option = showAlert(new TextInputDialog(), "Nombre del archivo comprimido:",
          "Comprimir archivos");
      String name;

      if (option.isPresent())
        name = option.get();
      else
        return;

      command.add(path + name);

      command.addAll(Arrays.asList(paths));

      printExecute("Creando archivo comprimido '" + YELLOW + name + RESET + "'");
      new ProcessBuilder(command).directory(new File(path)).start().waitFor();

      updateCenter();
      if (!centerPane.select(name))
        centerPane.selectFirst();
      updateRight();
    } catch (Exception e) {
      printError("Error al compirmir archivos '" + Arrays.toString(paths) + "'", e);
    }
  }

  public static void openShell() {
    try {
      String shellPath = path;
      File dir = centerPane.selectionModel.getSelectedItem().getFileProperties();
      if (dir.isDirectory())
        shellPath = dir.getAbsolutePath();

      ProcessBuilder pb = new ProcessBuilder(TERMINAL).directory(new File(shellPath));
      pb.start();
    } catch (IOException ex) {
      printError("Error al abrir la terminal '" + TERMINAL + "'", ex);
    }
  }
  public static void openWithAdmin() {
    String password = showPasswordStage("sudo filefx");
    if (password.isEmpty()) return;
    password += "\n";

    try {
      Process process = new ProcessBuilder("sudo", "-k", "-S", "filefx").start();

      try (OutputStream output = process.getOutputStream()) {
        output.write(password.getBytes());
        output.flush();
      }

      if (process.waitFor() != 0)
        printError("Contraseña incorrecta", null);
    } catch (Exception e) {
      printError("Error al abrir como administrador", e);
    }
  }
}
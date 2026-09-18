package panel;

import entity.FileProperties;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import main.Lib;
import node.CenterNode;

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.MainPane.centerPane;
import static scene.Scene.addShowing;

public class CenterPane extends ListView<CenterNode> {
    public String filter = null;

    private final ContextMenu menuFile;
    private final ContextMenu menuDirectory;
    private final ContextMenu menuCreate;
    private final ContextMenu menuTrash;

    private final Comparator<FileProperties> compareByName = Comparator.comparing(FileProperties::getName, String.CASE_INSENSITIVE_ORDER);
    private final Comparator<FileProperties> compareByDate = Comparator.comparing(n -> n.getModifiedDateTime());
    private final Comparator<FileProperties> compareBySize = Comparator.comparing(n -> n.getSize());
    private final Comparator<FileProperties> compareByMime = Comparator.comparing(n -> n.getMimeType(), String.CASE_INSENSITIVE_ORDER);

    public final MultipleSelectionModel<CenterNode> selectionModel;
    public final ObservableList<CenterNode> items, selectedItems;
    public double itemHeight;

    public CenterPane() {
        setId("CenterPane");
        setFocusTraversable(false);
        items = getItems();

        selectionModel = getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.MULTIPLE);

        selectedItems = selectionModel.getSelectedItems();
        selectedItems.addListener((javafx.collections.ListChangeListener<CenterNode>) change -> {
            while (change.next()) {
                for (CenterNode item : change.getAddedSubList()) item.setSelected(true);
                for (CenterNode item : change.getRemoved()) item.setSelected(false);
            }
        });

        if (!new File(path).exists()) {
            printErrorAndShow("El directorio inicial '"+path+"' no existe", null);
            path = HOME+"/";
        }

        update();

        Platform.runLater(() -> {
            itemHeight = items.getFirst().getHeight();

            String initSelect = initValues.getProperty("init_selection");
            if (initSelect != null) {
                for (CenterNode label : getItems()) {
                    if (initSelect.equals(label.getName())) {
                        label.setSelected(true);
                        setSelectedOnCenter();
                        break;
                    }
                }
            }
            if (getSelectionModel().getSelectedItem() == null) {
                selectThis();
            }
        });

        menuFile      = createContextMenu(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1);
        menuDirectory = createContextMenu(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1);
        menuCreate    = createContextMenu(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        menuTrash     = createContextMenu(1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 0, 0, 1, 1);

        // Acciones generales
        setOnMouseClicked(e -> {
            MouseButton button = e.getButton();

            if (button.equals(MouseButton.MIDDLE)) parent();
            else if (button.equals(MouseButton.BACK)) backward();
            else if (button.equals(MouseButton.FORWARD)) forward();
            else if (button.equals(MouseButton.SECONDARY)) showMenu(mainPane, e.getScreenX(), e.getScreenY());
            else if (button.equals(MouseButton.PRIMARY)) {
                if (isAnyShowing()) hideAll();
                updateRight();
                if (permissionsStage.isShowing()) permissionsStage.update();
            }

            e.consume();
        });

        setOnDragOver(e -> {
            if (e.getDragboard().hasFiles()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                e.consume();
            }
        });

        setOnDragDropped(e -> {
            CenterNode.dropInternally = true;

            List<File> files = e.getDragboard().getFiles();
            String[] paths = files.stream()
                    .map(f -> f.getAbsolutePath())
                    .filter(s -> !s.equals(path))
                    .toArray(String[]::new);

            String[] comando = new String[paths.length+2];
            comando[0] = "mv";
            System.arraycopy(paths, 0, comando, 1, paths.length);
            comando[paths.length+1] = path;

            try {
                printExecute("Moviendo archivos a '"+YELLOW+path+RESET+"'");
                new ProcessBuilder(comando).start().waitFor();
            } catch (Exception ex) {
                printErrorAndShow("Error al mover los archivos a "+path, ex);
            }

            updateCenter();
            centerPane.select(new ArrayList<>(files.stream().map(f -> f.getName()).toList()));
            updateRight();
        });
    }

    public void update() {
        printInfo("Actualizando panel central");

        // Reiniciando
        items.clear();

        // Tomar contenido
        File directory = new File(path);
        File[] content;
        try {
            content = directory.listFiles();
        } catch (Exception ex) {
            printErrorAndShow("No existe '"+path+"'", ex);
            return;
        }

        LinkedList<CenterNode> filesList = new LinkedList<>();
        LinkedList<CenterNode> directoriesList = new LinkedList<>();

        // Crear nodos
        if (content != null) {

            Stream<File> streamFile = Arrays.stream(content);
            if (!SHOW_HIDDEN) streamFile = streamFile.filter(file -> !file.getName().startsWith("."));
            if (filter != null) streamFile = streamFile.filter(file -> file.getName().contains(filter));

            Stream<FileProperties> streamFileProperties = streamFile.map(file -> new FileProperties(file));

            ORDER order = DEFAULT_ORDER;
            for (String[] customOrder : CUSTOM_ORDER) {
                if (path.equals(
                        customOrder[0].charAt(0) == '~' ? HOME+(customOrder[0].substring(1)) :
                                customOrder[0].startsWith("trash") ? Lib.TRASH+"files"+(customOrder[0].substring(5)) :
                                        customOrder[0])) {
                    order = ORDER.valueOf(customOrder[1]);
                    break;
                }
            }

            switch (order) {
                case DATE -> streamFileProperties=streamFileProperties.sorted(compareByDate.reversed());
                case SIZE -> streamFileProperties=streamFileProperties.sorted(compareBySize);
                case MIME -> streamFileProperties=streamFileProperties.sorted(compareByMime);
                default -> streamFileProperties=streamFileProperties.sorted(compareByName);
            }

            streamFileProperties.forEach(file -> {
                CenterNode centerNode = new CenterNode(file, true);
                if (file.isDirectory()) directoriesList.add(centerNode);
                else filesList.add(centerNode);
            });
        }

        if (SHOW_PARENT) {
            File parent = directory.getParentFile();
            if (parent != null) {
                CenterNode parentNode = new CenterNode(new FileProperties(parent), true);
                parentNode.nameLabel.setText("..");
                parentNode.setIcon(iconsMime.getProperty("parent"), Color.valueOf(colorsMime.getProperty("parent")));
                directoriesList.addFirst(parentNode);
            }
        }

        if (SHOW_THIS) {
            CenterNode thisNode = new CenterNode(new FileProperties(directory), true);
            thisNode.nameLabel.setText(".");
            thisNode.setIcon(iconsMime.getProperty("this"), Color.valueOf(colorsMime.getProperty("this")));
            directoriesList.addFirst(thisNode);
        }

        if (IS_DIRECTORY_FIRST) items.addAll(directoriesList);
        items.addAll(filesList);
        if (!IS_DIRECTORY_FIRST) items.addAll(directoriesList);

        // Añadir nodos
        if (!items.isEmpty()) {
            for (int i = 0; i < items.size(); i++) {
                CenterNode node = items.get(i);
                node.setIndex(i);
                node.addColumns();
            }
        }

        refresh();
    }

    public void moveCursor(boolean isShiftPressed, int step) {
        boolean isLast = false;
        boolean isFirst = false;

        // Seleccionar

        int size = items.size();
        if (size == 0) return;
        int currentIndex = selectionModel.getSelectedIndex();
        int targetIndex = currentIndex + step;

        if (targetIndex >= size-1) {
            if (currentIndex == size-1) {
                targetIndex = 0;
                isFirst = true;
            } else {
                targetIndex = size-1;
                isLast = true;
            }
        } else if (targetIndex <= 0) {
            if (currentIndex == 0) {
                targetIndex = size-1;
                isLast = true;
            } else {
                targetIndex = 0;
                isFirst = true;
            }
        }

        if (isShiftPressed) {
            selectionModel.selectRange(targetIndex, currentIndex);
        } else {
            selectionModel.clearSelection();
            selectionModel.select(targetIndex);
        }

        updateRight();

        // Hacer scroll

        if (isLast) scrollTo(size-1);
        else if (isFirst) scrollTo(0);
        else setSelectedOnCenter();
    }

    public void setSelectedOnCenter() {
        if (items.isEmpty()) return;
        int visibleCount = (int) Math.floor(getHeight() / itemHeight);
        scrollTo(selectionModel.getSelectedIndex() - (visibleCount / 2));
    }

    public void showMenu(Node anchor, double x, double y) {
        printInfo("Mostrando menu");

        hideAll();
        addShowing();

        if (path.startsWith(Lib.TRASH+"files")) {
            menuTrash.show(anchor, x, y);
        } else if (selectionModel.getSelectedItem().getFileProperties().isDirectory()) {
            menuDirectory.show(anchor, x, y);
        } else menuFile.show(anchor, x, y);
    }
    public void showMenu() {
        printInfo("Mostrando menu");

        hideAll();
        addShowing();

        if (path.startsWith(Lib.TRASH+"files")) {
            menuTrash.show(Window.getWindows().getFirst());
        } else if (selectedItems.getFirst().getFileProperties().isDirectory()) {
            menuDirectory.show(Window.getWindows().getFirst());
        } else menuFile.show(Window.getWindows().getFirst());
    }
    public void showMenuCreate() {
        printInfo("Mostrando menu");

        hideAll();
        addShowing();

        menuCreate.show(Window.getWindows().getFirst());
    }

    public void hideAll() {
        menuFile.hide();
        menuDirectory.hide();
        menuCreate.hide();
        menuTrash.hide();
    }
    public boolean isAnyShowing() {
        return menuFile.isShowing() || menuDirectory.isShowing() || menuCreate.isShowing() || menuTrash.isShowing();
    }

    public void openSelected() {
        if (selectionModel.getSelectedItem() != null && !selectionModel.getSelectedItem().nameLabel.getText().equals(".")) {
            File file = selectionModel.getSelectedItem().getFileProperties();
            String absolutePath = file.getAbsolutePath();

            // Si es directorio
            if (file.isDirectory()) {
                filter = null;

                forwardBuffer.clear();
                backBuffer.add(path);
                path=absolutePath+"/";

                printInfo("Entrando a '"+BLUE+path+RESET+"'");

                updateTop();
                updateCenter();
                selectFirst();
                updateRight();

            // Si es archivo
            } else {
                try {
                    printExecute("Abriendo '"+Lib.YELLOW+absolutePath+Lib.RESET+"'");
                    ProcessBuilder pb = new ProcessBuilder("open", absolutePath);
                    pb.start();
                } catch (IOException ex) {
                    Lib.printErrorAndShow("No se puede abrir el archivo "+absolutePath, ex);
                }
            }
        }
    }

    public void selectThis() {
        if (SHOW_THIS) {
            items.getFirst().setSelected(true);
            scrollTo(0);
        } else {
            CenterNode thisNode = new CenterNode(new FileProperties(new File(path)), true);
            thisNode.setIcon(iconsMime.getProperty("this"), Color.valueOf(colorsMime.getProperty("this")));
            getSelectionModel().select(thisNode);
        }
    }
    public void selectFirst() {
        if (!items.isEmpty()) {
            int size = items.size();

            if ((SHOW_THIS && SHOW_PARENT) && size > 2) selectionModel.select(2);
            else if ((SHOW_THIS ^ SHOW_PARENT) && size > 1) selectionModel.select(1);
            else if (!SHOW_THIS && !SHOW_PARENT) selectionModel.selectFirst();
            else selectThis();

            scrollTo(0);
        } else {
            selectThis();
        }
    }
    public void select(String name) {
        for (CenterNode node : items) {
            if (node.getName().equals(name)) {
                node.setSelected(true);
                return;
            }
        }
    }
    public void select(ArrayList<String> names) {
        int size;
        for (CenterNode item : items) {
            size = names.size();
            if (size == 0) break;

            String itemName = item.getName();
            for (int i = 0; i < size; i++) {
                if (itemName.equals(names.get(i))) {
                    item.setSelected(true);
                    names.remove(i);
                    break;
                }
            }
        }
    }

    public static File[] parseCenterNodesToFiles(ObservableList<CenterNode> centerNodeList) {
        if (!centerNodeList.isEmpty()) {
            File[] listFiles = new File[centerNodeList.size()];
            for (int i = 0; i < centerNodeList.size(); i++) {
                CenterNode centerNode = centerNodeList.get(i);
                listFiles[i] = centerNode.getFileProperties();
            }
            return listFiles;
        } else {
            return null;
        }
    }
}

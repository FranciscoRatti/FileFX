package panel;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import main.Lib;
import node.CenterNode;

import java.io.*;
import java.util.*;

import static main.FileFX.*;
import static main.Lib.*;

public class CenterPane extends ListView<CenterNode> {
    public String filter = null;

    private final ContextMenu menuFile;
    private final ContextMenu menuDirectory;
    private final ContextMenu menuMultiple;
    private final ContextMenu menuCreate;
    private final ContextMenu menuTrash;

    private final Comparator<CenterNode> compareByName = Comparator.comparing(CenterNode::getName, String.CASE_INSENSITIVE_ORDER);
    private final Comparator<CenterNode> compareByDate = Comparator.comparing(n -> n.getFileProperties().getModifiedDateTime());
    private final Comparator<CenterNode> compareBySize = Comparator.comparing(n -> n.getFileProperties().getSize());
    private final Comparator<CenterNode> compareByMime = Comparator.comparing(n -> n.getFileProperties().getMimeType(), String.CASE_INSENSITIVE_ORDER);

    public final MultipleSelectionModel<CenterNode> selectionModel;
    public final ObservableList<CenterNode> items, selectedItems;

    public CenterPane() {
        setId("CenterPane");
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
            printError("El directorio inicial '"+path+"' no existe", null);
            path = HOME+"/";
        }

        update();

        Platform.runLater(() -> {
            String initSelect = dynamicValues.getProperty("init_selection");
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

        menuFile      = createContextMenu(1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1);
        menuDirectory = createContextMenu(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1);
        menuMultiple  = createContextMenu(1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1);
        menuCreate    = createContextMenu(0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        menuTrash     = createContextMenu(1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1);

        // Acciones generales
        setOnMouseClicked(e -> {
            MouseButton button = e.getButton();
            EventTarget target = e.getTarget();

            if (button.equals(MouseButton.MIDDLE)) parent();
            else if (button.equals(MouseButton.BACK)) backward();
            else if (button.equals(MouseButton.FORWARD)) forward();
            else if (button.equals(MouseButton.SECONDARY)) showMenu(mainPane, e.getScreenX(), e.getScreenY());
            else if (button.equals(MouseButton.PRIMARY)) {
                if (isAnyShow()) hideAll();
                updateRight();
            }

            e.consume();
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
            printError("No existe '"+path+"'", ex);
            return;
        }

        ArrayList<CenterNode> filesList = new ArrayList<>();
        ArrayList<CenterNode> directoriesList = new ArrayList<>();

        // Crear nodos
        if (content != null) {

            for (File file : content) {
                boolean isHidden = file.getName().startsWith(".");
                if (!SHOW_HIDDEN && isHidden) continue;

                if (filter != null) {
                    if (file.getName().contains(filter)) {
                        CenterNode centerNode = new CenterNode(file, true);
                        if (file.isDirectory()) directoriesList.add(centerNode);
                        else filesList.add(centerNode);
                    }
                } else {
                    CenterNode centerNode = new CenterNode(file, true);
                    if (file.isDirectory()) directoriesList.add(centerNode);
                    else filesList.add(centerNode);
                }
            }

            // Ordenar
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
                case DATE -> {
                    filesList.sort(compareByDate.reversed());
                    directoriesList.sort(compareByDate.reversed());
                }
                case SIZE -> {
                    filesList.sort(compareBySize);
                    directoriesList.sort(compareBySize);
                }
                case MIME -> {
                    filesList.sort(compareByMime);
                    directoriesList.sort(compareByMime);
                }
                default -> {
                    filesList.sort(compareByName);
                    directoriesList.sort(compareByName);
                }
            }
        }

        if (SHOW_PARENT) {
            File parent = directory.getParentFile();
            if (parent != null) {
                CenterNode parentNode = new CenterNode(parent, true);
                parentNode.nameLabel.setText("..");
                parentNode.setIcon(iconsMime.getProperty("parent"), Color.valueOf(colorsMime.getProperty("parent")));
                directoriesList.addFirst(parentNode);
            }
        }

        if (SHOW_THIS) {
            CenterNode thisNode = new CenterNode(directory, true);
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

        double cellHeight = items.getFirst().prefHeight(-1);
        int visibleCount = (int) Math.floor(getHeight() / cellHeight);
        scrollTo(selectionModel.getSelectedIndex() - (visibleCount / 2));
    }

    public void showMenu(Node anchor, double x, double y) {
        printInfo("Mostrando menu");

        hideAll();

        if (path.startsWith(Lib.TRASH+"files")) {
            menuTrash.show(anchor, x, y);
        } else if (selectedItems.size() == 1) {
            if (selectedItems.getFirst().getFileProperties().isDirectory()) menuDirectory.show(anchor, x, y);
            else menuFile.show(anchor, x, y);
        } else {
            menuMultiple.show(anchor, x, y);
        }
    }
    public void showMenu() {
        printInfo("Mostrando menu");

        hideAll();
        if (path.startsWith(Lib.TRASH+"files")) {
            menuTrash.show(Window.getWindows().getFirst());
        } else if (selectedItems.size() == 1) {
            if (selectedItems.getFirst().getFileProperties().isDirectory()) menuDirectory.show(Window.getWindows().getFirst());
            else menuFile.show(Window.getWindows().getFirst());
        } else {
            menuMultiple.show(Window.getWindows().getFirst());
        }
    }
    public void showMenuCreate() {
        hideAll();
        printInfo("Mostrando menu");
        menuCreate.show(Window.getWindows().getFirst());
    }

    public void hideAll() {
        menuFile.hide();
        menuDirectory.hide();
        menuMultiple.hide();
        menuCreate.hide();
        menuTrash.hide();
    }
    public boolean isAnyShow() {
        return  menuFile.isShowing() || menuDirectory.isShowing() ||
                menuMultiple.isShowing() || menuCreate.isShowing() || menuTrash.isShowing();
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
                    Lib.printError("No se puede abrir el archivo "+absolutePath, ex);
                }
            }
        }
    }

    public void selectThis() {
        if (SHOW_THIS) {
            items.getFirst().setSelected(true);
            scrollTo(0);
        } else {
            CenterNode thisNode = new CenterNode(new File(path), true);
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
    public boolean select(String name) {
        for (CenterNode node : items) {
            if (node.getName().equals(name)) {
                node.setSelected(true);
                return true;
            }
        }
        return false;
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

package panel;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import main.Lib;
import node.CenterNode;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.locks.*;

import static main.FileFX.*;
import static main.Lib.*;
import static panel.MainPane.*;

public class CenterPane extends ScrollPane {
    public ArrayList<CenterNode> centerNodes;
    public ArrayList<CenterNode> selectedItems;
    public CenterNode selectedItem;
    public String filter = null;
    private final VBox pane;

    private final ContextMenu menu;
    private final ContextMenu menuFile;
    private final ContextMenu menuDirectory;
    private final ContextMenu menuMultiple;
    private final ContextMenu menuCreate;
    private final ContextMenu menuTrash;

    private final Comparator<CenterNode> compareByName = Comparator.comparing(CenterNode::getName, String.CASE_INSENSITIVE_ORDER);
    private final Comparator<CenterNode> compareByDate = Comparator.comparing(n -> n.getFileProperties().getModifiedDateTime());
    private final Comparator<CenterNode> compareBySize = Comparator.comparing(n -> n.getFileProperties().getSize());
    private final Comparator<CenterNode> compareByMime = Comparator.comparing(n -> n.getFileProperties().getMimeType(), String.CASE_INSENSITIVE_ORDER);

    private final Lock lock = new ReentrantLock();

    public CenterPane() {
        setId("CenterPane");
        setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        setFitToWidth(true);

        if (!new File(path).exists()) {
            printError("El directorio inicial '"+path+"' no existe", null);
            path = HOME+"/";
        }
        centerNodes = new ArrayList<>();
        selectedItems = new ArrayList<>();
        pane = new VBox();
        setContent(pane);
        update();

        Platform.runLater(() -> {
            String initSelect = dynamicValues.getProperty("init_selection");
            if (initSelect != null) {
                for (CenterNode label : centerNodes) {
                    if (initSelect.equals(label.getName())) {
                        label.setSelected(true);
                        setSelectedOnCenter();
                        break;
                    }
                }
            }
            if (selectedItem == null) {
                selectThis();
            }
        });

        menu          = createContextMenu(0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        menuFile      = createContextMenu(1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1);
        menuDirectory = createContextMenu(1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1);
        menuMultiple  = createContextMenu(1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1);
        menuCreate    = createContextMenu(0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        menuTrash     = createContextMenu(1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1);

        // Acciones generales
        setOnMouseClicked(e -> {
            MouseButton button = e.getButton();
            EventTarget target = e.getTarget();

            if (button.equals(MouseButton.MIDDLE)) {
                parent();
            } else if (button.equals(MouseButton.BACK)) {
                back();
            } else if (button.equals(MouseButton.FORWARD)) {
                forward();
            } else if (button.equals(MouseButton.PRIMARY)) {
                boolean isChildren = false;
                Node n = (Node) target;
                while (n != null) {
                    if (n == pane) isChildren = true;
                    n = n.getParent();
                }

                if (!isChildren) {
                    if (isAnyShow()) hideAll();
                    else {
                        deselectAll();
                        selectThis();
                        updateRight();
                    }
                } else {
                    if (isAnyShow()) hideAll();
                }
            } else if (button.equals(MouseButton.SECONDARY)) {
                showMenu(this);
            }

            e.consume();
        });
    }

    public void update() {
        printInfo("Actualizando panel central");

        // Reiniciando
        centerNodes.clear();
        ObservableList<Node> children = pane.getChildren();
        children.clear();

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
                parentNode.name.setText("..");
                parentNode.setIcon(iconsMime.getProperty("parent"), Color.valueOf(colorsMime.getProperty("parent")));
                directoriesList.addFirst(parentNode);
            }
        }

        if (SHOW_THIS) {
            CenterNode thisNode = new CenterNode(directory, true);
            thisNode.name.setText(".");
            thisNode.setIcon(iconsMime.getProperty("this"), Color.valueOf(colorsMime.getProperty("this")));
            directoriesList.addFirst(thisNode);
        }

        if (IS_DIRECTORY_FIRST) centerNodes.addAll(directoriesList);
        centerNodes.addAll(filesList);
        if (!IS_DIRECTORY_FIRST) centerNodes.addAll(directoriesList);

        // Añadir nodos
        if (!centerNodes.isEmpty()) {
            for (int i = 0; i < centerNodes.size(); i++) {
                CenterNode node = centerNodes.get(i);
                node.setIndex(i);
                node.addColumns();
                children.add(node);
            }
        }
    }

    public void showMenu(Node anchor) {
        hideAll();
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

        printInfo("Mostrando menu");
        if (selectedItems.isEmpty()) {
            menu.show(anchor, mouseLocation.x, mouseLocation.y);
        } else if (path.startsWith(Lib.TRASH+"files")) {
            menuTrash.show(anchor, mouseLocation.x, mouseLocation.y);
        } else if (selectedItems.size() == 1) {
            if (selectedItems.getFirst().getFile().isDirectory()) menuDirectory.show(anchor, mouseLocation.x, mouseLocation.y);
            else menuFile.show(anchor, mouseLocation.x, mouseLocation.y);
        } else {
            menuMultiple.show(anchor, mouseLocation.x, mouseLocation.y);
        }
    }
    public void showMenuCreate() {
        hideAll();
        printInfo("Mostrando menu");
        menuCreate.show(Window.getWindows().getFirst());
    }

    public void hideAll() {
        menu.hide();
        menuFile.hide();
        menuDirectory.hide();
        menuMultiple.hide();
        menuCreate.hide();
        menuTrash.hide();
    }
    public boolean isAnyShow() {
        return  menu.isShowing() || menuFile.isShowing() || menuDirectory.isShowing() ||
                menuMultiple.isShowing() || menuCreate.isShowing() || menuTrash.isShowing();
    }

    public void moveCursor(boolean isShiftDown, int step) {
        if (step == 0) return;

        if (!selectedItems.isEmpty()) {

            // Seleccion -----------------------------------------------------------------------------------------------

            int selectedItemIndex = selectedItem.getIndex();
            CenterNode labelStepSelected = null;

            // Si el seleccionado es el primero
            if (selectedItemIndex == 0 && step < 0)
                labelStepSelected = centerNodes.getLast();

            // Si el seleccionado es el ultimo
            else if (selectedItemIndex == centerNodes.size()-1 && step > 0)
                labelStepSelected = centerNodes.getFirst();

            // Si el seleccionado esta en un indice menor a los pasos
            else if (selectedItemIndex < -step && step < 0)
                labelStepSelected = centerNodes.getFirst();

            // Si el seleccionado esta en un indice mayor a los pasos
            else if (centerNodes.size()-1-selectedItemIndex < step && step > 0)
                labelStepSelected = centerNodes.getLast();

            else
                for (CenterNode label : centerNodes)
                    if (label.getIndex() == selectedItemIndex+step) {
                        labelStepSelected = label; break;
                    }

            if (labelStepSelected != null) {

                // Si no se presiono shift
                if (!isShiftDown) {
                    deselectAll();

                // Si se presiono shift
                } else {
                    boolean beSelected = false;
                    CenterNode lastSelectedItem = selectedItem;

                    for (CenterNode centerNode : centerNodes) {
                        boolean flag = centerNode.equals(labelStepSelected) || centerNode.equals(lastSelectedItem);
                        if (beSelected) {
                            if (flag) {
                                break;
                            } else {
                                centerNode.setSelected(true);
                            }
                        } else if (flag) {
                            beSelected = true;
                        }
                    }
                }

                labelStepSelected.setSelected(true);
            }
        } else if (!centerNodes.isEmpty()) {
            if ((step < 0)) centerNodes.getLast().setSelected(true);
            else centerNodes.getFirst().setSelected(true);
        }
        updateRight();

        // Scroll ------------------------------------------------------------------------------------------------------

        double contentHeight = pane.getBoundsInLocal().getHeight();
        double viewportHeight = getViewportBounds().getHeight();
        Bounds labelBounds = selectedItem.getBoundsInParent();
        double scrollRange = contentHeight - viewportHeight;

        if (scrollRange > 0) {
            if (selectedItem == centerNodes.getFirst()) {
                setVvalue(0);
            } else if (selectedItem == centerNodes.getLast()) {
                setVvalue(1);

            } else {
                double visibleTop = getVvalue() * scrollRange;
                double visibleBottom = visibleTop + viewportHeight;

                double topThreshold = visibleTop + (viewportHeight / 6.0);
                double bottomThreshold = visibleBottom - (viewportHeight / 6.0);

                double labelsToScroll = Math.abs(step);
                double scrollByPixels = labelBounds.getHeight() * labelsToScroll;

                if (step > 0) {
                    if (labelBounds.getMaxY() > bottomThreshold) {
                        double targetVisibleTop = visibleTop + scrollByPixels;
                        double newV = targetVisibleTop / scrollRange;
                        setVvalue(newV);
                    }
                } else {
                    if (labelBounds.getMinY() < topThreshold) {
                        double targetVisibleTop = visibleTop - scrollByPixels;
                        double newV = targetVisibleTop / scrollRange;
                        setVvalue(newV);
                    }
                }
            }
        }
    }
    public void setSelectedOnCenter() {
        if (selectedItem != null) {
            Platform.runLater(() -> {
                double contentHeight = pane.getBoundsInLocal().getHeight();
                double viewportHeight = getViewportBounds().getHeight();
                double scrollRange = contentHeight - viewportHeight;

                if (scrollRange > 0) {
                    setVvalue(
                            (selectedItem.getBoundsInParent().getCenterY() - (viewportHeight / 2.0)) / scrollRange);
                }
            });
        }
    }
    public void openSelected() {
        if (selectedItem != null && !selectedItem.name.getText().equals(".")) {
            File file = selectedItem.getFile();
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
                Platform.runLater(() -> {
                    selectFirst();
                    updateRight();
                });

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

    public void deselectAll() {
        if (!selectedItems.isEmpty() && selectedItem != null) {
            selectedItem = null;
            for (CenterNode centerNode : selectedItems) centerNode.setSelected(false);
            selectedItems.clear();

            printInfo("Se deselecciono todo");
        }
    }
    public void selectThis() {
        if (SHOW_THIS) {
            lock.lock();
            centerNodes.getFirst().setSelected(true);
            centerPane.setVvalue(0);
            lock.unlock();
        } else {
            selectedItem = new CenterNode(new File(path), true);
            selectedItem.setIcon(iconsMime.getProperty("this"), Color.valueOf(colorsMime.getProperty("this")));
            selectedItems.add(selectedItem);
        }
    }
    public void selectFirst() {
        if (!centerNodes.isEmpty()) {
            lock.lock();
            int length = centerNodes.size();
            if (SHOW_THIS && SHOW_PARENT && length > 2)
                centerNodes.get(2).setSelected(true);
            else if ((SHOW_THIS || SHOW_PARENT) && length > 1)
                centerNodes.get(1).setSelected(true);
            else
                centerNodes.getFirst().setSelected(true);
            centerPane.setVvalue(0);
            lock.unlock();
        } else {
            selectThis();
        }
    }

    public static File[] parseCenterNodesToFiles(ArrayList<CenterNode> centerNodeList) {
        if (!centerNodeList.isEmpty()) {
            File[] listFiles = new File[centerNodeList.size()];
            for (int i = 0; i < centerNodeList.size(); i++) {
                CenterNode centerNode = centerNodeList.get(i);
                listFiles[i] = centerNode.getFile();
            }
            return listFiles;
        } else {
            return null;
        }
    }
}

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
import main.FileFX;
import main.Lib;
import node.CenterNode;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import static main.FileFX.*;
import static main.Lib.*;
import static main.Lib.back;
import static main.Lib.forward;
import static main.Lib.parent;
import static panel.MainPane.*;

public class CenterPane extends ScrollPane {
    public static ArrayList<CenterNode> centerNodes;

    private static ContextMenu menu;
    private static ContextMenu menuFile;
    private static ContextMenu menuDirectory;
    private static ContextMenu menuMultiple;
    private static ContextMenu menuCreate;
    private static ContextMenu menuTrash;

    private VBox pane;

    public CenterPane() {
        if (!new File(path).exists()) {
            printError("El directorio inicial '"+path+"' no existe", null);
            path = HOME;
        }
        centerNodes = new ArrayList<>();
        selectedItems = new ArrayList<>();

        menu          = createContextMenu(0, 0, 1, 1, 0, 0, 0, 1, 0, 0, 0, 1);
        menuFile      = createContextMenu(1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 1);
        menuDirectory = createContextMenu(1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1);
        menuMultiple  = createContextMenu(1, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 1);
        menuCreate    = createContextMenu(0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0);
        menuTrash     = createContextMenu(1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1);

        pane = new VBox();

        setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        setFitToWidth(true);
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

        // Acciones generales
        setOnMouseReleased(e -> {
            MouseButton button = e.getButton();
            EventTarget target = e.getTarget();

            if (button.equals(MouseButton.MIDDLE)) {
                parent();
            } else if (button.equals(MouseButton.BACK)) {
                back();
            } else if (button.equals(MouseButton.FORWARD)) {
                forward();
            } else if (button.equals(MouseButton.PRIMARY)) {
                if (!isChildrenOf(pane, (Node) target)) {
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
        selectedItems.clear();
        selectedItem = null;
        ObservableList<Node> childrensList = pane.getChildren();
        childrensList.clear();

        // Tomar contenido
        File directory = new File(path);
        File[] content;
        try {
            content = directory.listFiles();
        } catch (Exception e) {
            printError("No existe '"+path+"'", e);
            return;
        }
        ArrayList<CenterNode> filesList = new ArrayList<>();
        ArrayList<CenterNode> directoriesList = new ArrayList<>();

        // Crear nodos
        if (content != null) {
            for (File file : content) {
                boolean isHidden = file.getName().startsWith(".");
                if (!SHOW_HIDDEN && isHidden) continue;

                CenterNode centerNode = new CenterNode(file);
                if (file.isDirectory()) directoriesList.add(centerNode);
                else filesList.add(centerNode);
            }

            filesList.sort(Comparator.comparing(CenterNode::getName, String.CASE_INSENSITIVE_ORDER));
            directoriesList.sort(Comparator.comparing(CenterNode::getName, String.CASE_INSENSITIVE_ORDER));

            if (SHOW_PARENT) {
                File parent = directory.getParentFile();
                if (parent != null) {
                    CenterNode parentNode = new CenterNode(parent);
                    parentNode.setText("..");
                    parentNode.setIcon(iconsMyme.getProperty("parent"), Color.valueOf(colorsMyme.getProperty("parent")));
                    directoriesList.addFirst(parentNode);
                }
            }

            if (SHOW_THIS) {
                CenterNode thisNode = new CenterNode(directory);
                thisNode.setText(".");
                thisNode.setIcon(iconsMyme.getProperty("this"), Color.valueOf(colorsMyme.getProperty("this")));
                directoriesList.addFirst(thisNode);
            }

            // Añadiendo nodos
            if (IS_DIRECTORY_FIRST) centerNodes.addAll(directoriesList);
            centerNodes.addAll(filesList);
            if (!IS_DIRECTORY_FIRST) centerNodes.addAll(directoriesList);
        }

        // Definiendo ids
        for (int i = 0; i < centerNodes.size(); i++) {
            centerNodes.get(i).setIndex(i);
        }

        childrensList.addAll(centerNodes);
    }

    public static void showMenu(Node anchor) {
        hideAll();
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

        printInfo("Mostrando menu");
        if (selectedItems.isEmpty()) {
            menu.show(anchor, mouseLocation.x, mouseLocation.y);
        } else if (path.startsWith(TRASH+"files")) {
            menuTrash.show(anchor, mouseLocation.x, mouseLocation.y);
        } else if (selectedItems.size() == 1) {
            if (selectedItems.getFirst().getFile().isDirectory()) menuDirectory.show(anchor, mouseLocation.x, mouseLocation.y);
            else menuFile.show(anchor, mouseLocation.x, mouseLocation.y);
        } else {
            menuMultiple.show(anchor, mouseLocation.x, mouseLocation.y);
        }
    }
    public static void showMenuCreate() {
        hideAll();
        printInfo("Mostrando menu");
        menuCreate.show(Window.getWindows().getFirst());
    }
    public static void hideAll() {
        menu.hide();
        menuFile.hide();
        menuDirectory.hide();
        menuMultiple.hide();
        menuCreate.hide();
        menuTrash.hide();
    }
    public static boolean isAnyShow() {
        return  menu.isShowing() || menuFile.isShowing() || menuDirectory.isShowing() ||
                menuMultiple.isShowing() || menuCreate.isShowing() || menuTrash.isShowing();
    }

    public void changeSelectKey(boolean isShiftDown, int step) {
        if (!selectedItems.isEmpty()) {

            // Seleccion

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
                        if (beSelected) {
                            if (centerNode.equals(labelStepSelected) || centerNode.equals(lastSelectedItem)) {
                                break;
                            } else {
                                centerNode.setSelected(true);
                            }
                        } else if (centerNode.equals(labelStepSelected) || centerNode.equals(lastSelectedItem)) {
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

        // Scroll

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
                } else if (step < 0) {
                    if (labelBounds.getMinY() < topThreshold) {
                        double targetVisibleTop = visibleTop - scrollByPixels;
                        double newV = targetVisibleTop / scrollRange;
                        setVvalue(newV);
                    }
                }
            }
        }
    }

    public static void openSelected() {
        if (selectedItem != null && !selectedItem.getText().equals(".")) {
            File file = selectedItem.getFile();
            String absolutePath = file.getAbsolutePath();

            // Si es directorio
            if (file.isDirectory()) {
                forwardBuffer.clear();
                backBuffer.add(FileFX.path);
                path=absolutePath+"/";

                printInfo("Entrando a '"+BLUE+path+RESET+"'");

                updateCenter();
                updateTop();
                if (!centerNodes.isEmpty()) centerNodes.getFirst().setSelected(true);
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
}

package panel;

import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import static main.Main.*;
import static main.Lib.*;
import static panel.MainPane.*;

import javafx.stage.Window;
import main.Lib;
import main.Main;
import node.FileLabel;
import node.PlaceLabel;

public class CenterPane extends ScrollPane {
    public static ArrayList<FileLabel> fileLabels;

    private static ContextMenu menu;
    private static ContextMenu menuFile;
    private static ContextMenu menuDirectory;
    private static ContextMenu menuMultiple;
    private static ContextMenu menuCreate;

    private VBox pane;

    public CenterPane() {
        if (!new File(path).exists()) {
            printError("El directorio inicial '"+path+"' no existe", new FileNotFoundException("The initial directory '"+path+"' not exist"));
            path = "/";
        }
        fileLabels = new ArrayList<>();
        MainPane.selectedItems = new ArrayList<>();

        menu =          createContextMenu(true , true , false, false, false, true , false, false, true );
        menuFile =      createContextMenu(false, false, true , true , true , true , true , true , true );
        menuDirectory = createContextMenu(true , true , true , true , true , true , true , true , true );
        menuMultiple =  createContextMenu(false, false, false, true , true , true , true , true , true );
        menuCreate =    createContextMenu(true , true , false, false, false, false, false, false, false);

        pane = new VBox();

        setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        setFitToWidth(true);
        setContent(pane);

        update();

        // Acciones generales
        setOnMouseReleased(e -> {
            MouseButton button = e.getButton();
            EventTarget target = e.getTarget();
            boolean isShiftDown = e.isShiftDown();
            boolean isControlDown = e.isControlDown();

            if (button.equals(MouseButton.MIDDLE)) {
                parent();
            } else if (button.equals(MouseButton.BACK)) {
                back();
            } else if (button.equals(MouseButton.FORWARD)) {
                forward();
            } else if (button.equals(MouseButton.PRIMARY)) {
                if (target instanceof FileLabel) {
                    if (isAnyShow()) hideAll();
                    changeSelectMouse(isShiftDown, isControlDown, e,(FileLabel) target);
                } else {
                    if (isAnyShow()) hideAll();
                    else deselectAll();
                }
            } else if (button.equals(MouseButton.SECONDARY)) {
                showMenu(this);
            }

            e.consume();
        });

        setOnMouseEntered(e -> {
            PlaceLabel.PlaceMenu.hide();
        });
    }

    public void update() {
        printInfo("Actualizando panel central");

        // Reiniciando todo
        fileLabels.clear();
        MainPane.selectedItems.clear();
        MainPane.selectedItem = null;
        MainPane.selectedFile = null;
        ObservableList<Node> childrensList = pane.getChildren();
        childrensList.clear();

        // Creando nodos
        File[] content;
        try {
            content = new File(path).listFiles();
        } catch (Exception e) {
            printError("No existe "+path, e);
            content = new File("/").listFiles();
        }
        ArrayList<FileLabel> filesList = new ArrayList<>();
        ArrayList<FileLabel> directoriesList = new ArrayList<>();

        if (content != null) {
            boolean showHidden = Boolean.parseBoolean(config.getProperty("show_hidden"));

            for (File file : content) {
                boolean isHidden = file.getName().startsWith(".");

                if (!showHidden && isHidden) continue;

                if (file.isDirectory()) {
                    directoriesList.add(new FileLabel(file));
                } else {
                    filesList.add(new FileLabel(file));
                }
            }
        }

        filesList.sort(Comparator.comparing(FileLabel::getName, String.CASE_INSENSITIVE_ORDER));
        directoriesList.sort(Comparator.comparing(FileLabel::getName, String.CASE_INSENSITIVE_ORDER));

        // Añadiendo nodos
        if (Boolean.parseBoolean(config.getProperty("is_directory_first"))) {
            fileLabels.addAll(directoriesList);}
        fileLabels.addAll(filesList);
        if (!Boolean.parseBoolean(config.getProperty("is_directory_first"))) {
            fileLabels.addAll(directoriesList);}

        // Definiendo ids
        for (int i = 0; i < fileLabels.size(); i++) {
            fileLabels.get(i).setIndex(i);
        }

        childrensList.addAll(fileLabels);
    }

    public static void showMenu(Node anchor) {
        hideAll();

        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();

        if (MainPane.selectedItems.isEmpty()) {
            menu.show(anchor, mouseLocation.x, mouseLocation.y);
        } else if (MainPane.selectedItems.size() == 1) {
            if (MainPane.selectedItems.getFirst().getFile().isDirectory()) menuDirectory.show(anchor, mouseLocation.x, mouseLocation.y);
            else menuFile.show(anchor, mouseLocation.x, mouseLocation.y);
        } else {
            menuMultiple.show(anchor, mouseLocation.x, mouseLocation.y);
        }
    }
    public static void showMenuCreate() {
        hideAll();
        menuCreate.show(Window.getWindows().getFirst());
    }
    public static void hideAll() {
        if (menu.isShowing()) menu.hide();
        if (menuFile.isShowing()) menuFile.hide();
        if (menuDirectory.isShowing()) menuDirectory.hide();
        if (menuMultiple.isShowing()) menuMultiple.hide();
        if (menuCreate.isShowing()) menuCreate.hide();
    }
    public static boolean isAnyShow() {
        return  menu.isShowing() ||
                menuFile.isShowing() ||
                menuDirectory.isShowing() ||
                menuMultiple.isShowing() ||
                menuCreate.isShowing();
    }

    public void changeSelectMouse(boolean isShitDown, boolean isControlDown, MouseEvent event, FileLabel label) {
        MouseButton button = event.getButton();
        int clickCount = event.getClickCount();

        if (button.equals(MouseButton.PRIMARY)) {

            // Seleccionar
            if (clickCount == 1) {
                if (!event.isControlDown() && !event.isShiftDown()) {
                    MainPane.selectedItems.clear();
                    for (FileLabel fileLabel : CenterPane.fileLabels) {
                        fileLabel.setSelected(false);
                    }
                } else if (event.isShiftDown()) {
                    if (MainPane.selectedItem != null) {
                        boolean beSelected = false;
                        FileLabel lastSelectedItem = MainPane.selectedItem;

                        for (FileLabel fileLabel : CenterPane.fileLabels) {
                            if (beSelected) {
                                if (fileLabel.equals(label) || fileLabel.equals(lastSelectedItem)) {
                                    break;
                                } else {
                                    fileLabel.setSelected(true);
                                }
                            } else if (fileLabel.equals(label) || fileLabel.equals(lastSelectedItem)) {
                                beSelected = true;
                            }
                        }
                    }
                }

                label.setSelected(true);

                Lib.updateAll(false, true , false ,false, false);
            } else if (clickCount == 2) {
                openSelected();
            }
        }
    }
    public void changeSelectKey(boolean isShiftDown, int step) {
        if (selectedItem != null) {

            // Seleccion

            int selectedItemIndex = selectedItem.getIndex();
            FileLabel labelStepSelected = null;

            // Si el seleccionado es el primero
            if (selectedItemIndex == 0 && step < 0) {
                labelStepSelected = fileLabels.getLast();

            // Si el seleccionado es el ultimo
            } else if (selectedItemIndex == fileLabels.size()-1 && step > 0) {
                labelStepSelected = fileLabels.getFirst();

            // Si el seleccionado esta en un indice menor a los pasos
            } else if (selectedItemIndex < -step && step < 0) {
                labelStepSelected = fileLabels.getFirst();

            // Si el seleccionado esta en un indice mayor a los pasos
            } else if (fileLabels.size()-1-selectedItemIndex < step && step > 0) {
                labelStepSelected = fileLabels.getLast();

            } else {
                for (FileLabel label : fileLabels) {
                    if (label.getIndex() == selectedItemIndex+step) {labelStepSelected = label; break;}
                }
            }

            if (labelStepSelected != null) {

                // Si no se presiono shift
                if (!isShiftDown) {
                    deselectAll();

                // Si se presiono shift
                } else if (step > 1 || step < -1) {
                    boolean beSelected = false;
                    FileLabel lastSelectedItem = MainPane.selectedItem;

                    for (FileLabel fileLabel : fileLabels) {
                        if (beSelected) {
                            if (fileLabel.equals(labelStepSelected) || fileLabel.equals(lastSelectedItem)) {
                                break;
                            } else {
                                fileLabel.setSelected(true);
                            }
                        } else if (fileLabel.equals(labelStepSelected) || fileLabel.equals(lastSelectedItem)) {
                            beSelected = true;
                        }
                    }
                }

                labelStepSelected.setSelected(true);
                updateAll(false, true, false, false, false);
            }
        } else if (!fileLabels.isEmpty()) {
            if ((step < 0)) fileLabels.getLast().setSelected(true);
            else fileLabels.getFirst().setSelected(true);
            updateAll(false, true, false, false, false);
        }


        // Scroll

        double contentHeight = pane.getBoundsInLocal().getHeight();
        double viewportHeight = getViewportBounds().getHeight();
        Bounds labelBounds = selectedItem.getBoundsInParent();
        double scrollRange = contentHeight - viewportHeight;

        if (scrollRange > 0) {
            if (selectedItem == fileLabels.getFirst()) {
                setVvalue(0);
            } else if (selectedItem == fileLabels.getLast()) {
                setVvalue(1);

            } else {
                double visibleTop = getVvalue() * scrollRange;
                double visibleBottom = visibleTop + viewportHeight;

                double topThreshold = visibleTop + (viewportHeight / 6.0);
                double bottomThreshold = visibleBottom - (viewportHeight / 6.0);

                double labelsToScroll = Math.max(1, Math.abs(step));
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
        if (selectedItem != null) {
            File file = selectedItem.getFile();
            String fileName = file.getName();

            // Si es directorio
            if (file.isDirectory()) {
                Lib.forwardBuffer.clear();
                Lib.backBuffer.add(Main.path);
                Main.path+="/"+fileName;

                MainPane.selectedItems.clear();
                MainPane.selectedItem = null;
                MainPane.selectedFile = null;

                Lib.printInfo("Actualizando path a '"+Lib.BLUE+Main.path+Lib.RESET+"'");

                Main.mainPane.centerPane.update();
                topPane.update();

            // Si es archivo
            } else {
                try {
                    Lib.printExecute("Abriendo '"+Lib.YELLOW+Main.path+"/"+fileName+Lib.RESET+"'");
                    ProcessBuilder pb = new ProcessBuilder("open", Main.path+"/"+fileName);
                    pb.start();
                } catch (IOException ex) {
                    Lib.printError("No se puede abrir el archivo "+Main.path+"/"+fileName, ex);
                }
            }


            Lib.updateAll(false, true , false ,false, false);
        }
    }
}

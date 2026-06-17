package panel;

import entity.FileProperties;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.util.ArrayList;

import main.Main;
import node.FileLabel;

import static main.Lib.printInfo;

public class MainPane extends BorderPane {
    public static CenterPane centerPane;
    public static TopPane topPane;
    public static RightPane rightPane;
    public static LeftPane leftPane;

    private boolean isRightPaneShow;

    public static ArrayList<FileLabel> selectedItems;
    public static FileLabel selectedItem;

    public MainPane() {
        super();
        centerPane = new CenterPane();
        setCenter(centerPane);
        topPane = new TopPane();
        setTop(topPane);
        rightPane = new RightPane();
        setRight(rightPane);
        changeShowRightPane(Boolean.parseBoolean(Main.config.getProperty("show_right_pane")));
        leftPane = new LeftPane();
        setLeft(leftPane);

        setId("main_pane");
    }

    public void changeShowRightPane(boolean isRightPaneShow) {
        this.isRightPaneShow=isRightPaneShow;
        if (isRightPaneShow) {
            if (rightPane == null) rightPane = new RightPane();
            setRight(rightPane);
        } else {
            setRight(null);
        }
    }
    public void changeShowRightPane() {
        changeShowRightPane(!isRightPaneShow);
    }

    public static void deselectAll() {
        if (!selectedItems.isEmpty() || selectedItem != null) {
            selectedItems.clear();
            selectedItem = null;

            for (FileLabel fileLabel : CenterPane.fileLabels) fileLabel.setSelected(false);

            printInfo("Se deselcciono todo");
        }
    }

    public static File[] parseFileLabelsToFiles(ArrayList<FileLabel> fileLabelList) {
        if (!fileLabelList.isEmpty()) {
            File[] listFiles = new File[fileLabelList.size()];
            for (int i = 0; i < fileLabelList.size(); i++) {
                FileLabel fileLabel = fileLabelList.get(i);
                listFiles[i] = fileLabel.getFile();
            }
            return listFiles;
        } else {
            return null;
        }
    }
}
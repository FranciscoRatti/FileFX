package panel;

import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import node.CenterNode;

import java.io.File;
import java.util.ArrayList;

import static main.FileFX.*;
import static main.Lib.printInfo;

public class MainPane extends BorderPane {
    public static TopPane topPane;
    public static RightPane rightPane;
    public static BottomPane bottomPane;
    public static LeftPane leftPane;
    public static CenterPane centerPane;

    private boolean isRightPaneShow;

    public static ArrayList<CenterNode> selectedItems;
    public static CenterNode selectedItem;
    public static String filter = null;

    public MainPane() {
        super();
        centerPane = new CenterPane();
        setCenter(centerPane);
        topPane = new TopPane();
        setTop(topPane);
        rightPane = new RightPane();
        setRight(rightPane);
        bottomPane = new BottomPane();
        setBottom(bottomPane);
        leftPane = new LeftPane();
        setLeft(leftPane);

        changeShowRightPane(SHOW_RIGHT_PANE);
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
        if (!selectedItems.isEmpty() && selectedItem != null) {
            selectedItem = null;
            for (CenterNode centerNode : selectedItems) centerNode.setSelected(false);
            selectedItems.clear();

            printInfo("Se deselecciono todo");
        }
    }
    public static void selectThis() {
        if (SHOW_THIS) {
            CenterPane.centerNodes.getFirst().setSelected(true);
            centerPane.setSelectedOnCenter();
        } else {
            selectedItem = new CenterNode(new File(path));
            selectedItem.setIcon(iconsMyme.getProperty("this"), Color.valueOf(colorsMyme.getProperty("this")));
            selectedItems.add(selectedItem);
        }
    }

    public static File[] parseFileLabelsToFiles(ArrayList<CenterNode> centerNodeList) {
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
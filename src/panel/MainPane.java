package panel;

import javafx.scene.layout.BorderPane;
import node.CenterNode;

import java.io.File;
import java.util.ArrayList;

public class MainPane extends BorderPane {
    public static TopPane topPane;
    public static RightPane rightPane;
    public static BottomPane bottomPane;
    public static LeftPane leftPane;
    public static CenterPane centerPane;

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

        setId("MainPane");
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
package panel;

import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import main.FileFX;
import node.Separator;
import stage.PartitionStage;

import static main.FileFX.LEFT_WIDTH;
import static main.FileFX.RIGHT_WIDTH;
import static main.Lib.othersApplicationsStage;
import static main.Lib.permissionsStage;

public class MainPane extends StackPane {
    public final BorderPane borderPane;
    public static TopPane topPane;
    public static RightPane rightPane;
    public static BottomPane bottomPane;
    public static LeftPane leftPane;
    public static CenterPane centerPane;

    public MainPane() {
        setId("MainPane");

        Separator leftBorder = new Separator(Double.MAX_VALUE, Orientation.HORIZONTAL);
        leftBorder.setId("LeftPane_border");
        leftBorder.setOnMouseEntered(e -> setCursor(Cursor.H_RESIZE));
        leftBorder.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        leftBorder.setOnMouseDragged(e -> {
            LEFT_WIDTH = e.getSceneX();
            leftPane.setMaxWidth(LEFT_WIDTH);
        });

        Separator rightBorder = new Separator(Double.MAX_VALUE, Orientation.HORIZONTAL);
        rightBorder.setId("RightPane_border");
        rightBorder.setOnMouseEntered(e -> setCursor(Cursor.H_RESIZE));
        rightBorder.setOnMouseExited(e -> setCursor(Cursor.DEFAULT));
        rightBorder.setOnMouseDragged(e -> {
            RIGHT_WIDTH = FileFX.scene.getWidth() - e.getSceneX();
            rightPane.setSize();
        });

        centerPane = new CenterPane();
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        topPane = new TopPane();
        rightPane = new RightPane();
        bottomPane = new BottomPane();
        leftPane = new LeftPane();

        borderPane = new BorderPane(
                new HBox(centerPane, rightBorder),
                topPane,
                rightPane,
                bottomPane,
                new HBox(leftPane, leftBorder)
        );
        borderPane.setId("MainPane");
        getChildren().add(borderPane);
    }

    private static int focused = 0;
    public static void addFocused() {focused++;}
    public static void minusFocused() {if (focused > 0) focused--;}
    public static boolean isAnyFocus() {return focused != 0;}

    private static int showing = 0;
    public static void addShowing() {showing++;}
    public static void minusShowing() {if (showing > 0) showing--;}
    public static boolean isAnyShowing() {return showing != 0;}
    public static void closeAll() {
        if (othersApplicationsStage.isShowing()) othersApplicationsStage.close();
        if (permissionsStage.isShowing()) permissionsStage.close();
        for (PartitionStage partitionStage : leftPane.partitionStages)
            if (partitionStage.isShowing()) partitionStage.close();
    }
}
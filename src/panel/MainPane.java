package panel;

import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import main.FileFX;
import main.Lib;
import node.Separator;

import static main.FileFX.LEFT_WIDTH;
import static main.FileFX.RIGHT_WIDTH;
import static panel.RightPane.*;

public class MainPane extends BorderPane {
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
            rightPane.setMaxWidth(RIGHT_WIDTH);
            RightPane.setSize();
        });

        centerPane = new CenterPane();
        HBox.setHgrow(centerPane, Priority.ALWAYS);
        setCenter(new HBox(centerPane, rightBorder));
        topPane = new TopPane();
        setTop(topPane);
        rightPane = new RightPane();
        setRight(rightPane);
        bottomPane = new BottomPane();
        setBottom(bottomPane);
        leftPane = new LeftPane();
        setLeft(new HBox(leftPane, leftBorder));
    }
}
package panel;

import javafx.scene.layout.BorderPane;

public class MainPane extends BorderPane {
    public static TopPane topPane;
    public static RightPane rightPane;
    public static BottomPane bottomPane;
    public static LeftPane leftPane;
    public static CenterPane centerPane;

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
}
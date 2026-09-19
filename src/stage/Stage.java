package stage;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import panel.MainPane;

import static main.FileFX.mainPane;
import static panel.MainPane.*;

public abstract class Stage extends StackPane {
    private final Label titleLabel;

    public Stage(String title) {
        setId("Stage_pane");
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        titleLabel = new Label(title);
        titleLabel.setId("Stage_title");
        StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        getChildren().add(titleLabel);
    }

    private boolean isShowing = false;

    public final void show() {
        centerPane.hideAll();
        MainPane.closeAll();

        isShowing = true;
        addShowing();
        mainPane.getChildren().add(this);

        afterShow();
        titleLabel.toFront();
    }
    public abstract void afterShow();

    public abstract void beforeClose();
    public final void close() {
        beforeClose();

        isShowing = false;
        minusShowing();
        mainPane.getChildren().remove(this);
    }

    public final boolean isShowing() {return isShowing;}
}
package stage;

import javafx.scene.layout.StackPane;
import panel.MainPane;

import static main.FileFX.mainPane;
import static panel.MainPane.*;

public abstract class Stage extends StackPane {
    private boolean isShowing = false;

    public final void show() {
        centerPane.hideAll();
        MainPane.closeAll();

        isShowing = true;
        addShowing();
        mainPane.getChildren().add(this);

        afterShow();
    }
    public abstract void afterShow();

    public final void close() {
        isShowing = false;
        minusShowing();
        mainPane.getChildren().remove(this);
    }

    public final boolean isShowing() {return isShowing;}
}
package stage;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import panel.MainPane;

import java.util.concurrent.CompletableFuture;

import static main.FileFX.mainPane;
import static panel.MainPane.*;

public abstract class Stage extends StackPane {
    private final Label titleLabel;
    private boolean isShowing = false;
    private CompletableFuture<String> waitFuture;

    public Stage(String title) {
        setId("Stage_pane");
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        titleLabel = new Label(title);
        titleLabel.setId("Stage_title");
        StackPane.setAlignment(titleLabel, Pos.TOP_CENTER);
        getChildren().add(titleLabel);
    }

    public final CompletableFuture<String> showAndWait() {
        show(false);
        waitFuture = new CompletableFuture<>();
        return waitFuture;
    }
    protected final CompletableFuture<String> getCompletable() {return waitFuture;}

    private void show(boolean closeAll) {
        centerPane.hideAll();
        if (closeAll) MainPane.closeAll();

        isShowing = true;
        addShowing();
        mainPane.getChildren().add(this);

        afterShow();
        titleLabel.toFront();
    }
    public final void show() {show(true);}
    public abstract void afterShow();

    public final void close() {
        isShowing = false;
        minusShowing();
        mainPane.getChildren().remove(this);

        afterClose();
    }
    public abstract void afterClose();

    public final boolean isShowing() {return isShowing;}
}
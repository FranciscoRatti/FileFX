package stage;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import main.FileFX;
import node.GotoNode;
import node.LeftNode;
import panel.LeftPane;
import scene.Scene;

import static main.FileFX.*;
import static main.Lib.*;
import static main.Lib.backBuffer;
import static main.Lib.forwardBuffer;
import static main.Lib.updateCenter;
import static main.Lib.updateRight;
import static main.Lib.updateTop;
import static panel.MainPane.centerPane;
import static panel.MainPane.leftPane;

public class GotoStage extends Stage {
    private final String[] placesIcons;

    public GotoStage() {
        super("Go To");

        if (SHOW_PLACES) {
            placesIcons = new String[leftPane.placesNodes.length];
            for (int i = 0; i < leftPane.placesNodes.length; i++) {
                placesIcons[i] = leftPane.placesNodes[i].getIcon();
            }
        } else {
            placesIcons = null;
        }

        if (GOTO_PLACES == null) setVisible(false);
        else {
            FlowPane pane = new FlowPane();
            ObservableList<Node> childrenPane = pane.getChildren();
            pane.setId("GotoStage_pane");
            getChildren().add(pane);

            KeyCombination[] gotoKeys = new KeyCombination[GOTO_PLACES.length];

            for (int i = 0; i < GOTO_PLACES.length; i++) {
                gotoKeys[i] = KeyCodeCombination.valueOf(GOTO_PLACES[i][1]);
                childrenPane.add(new GotoNode(GOTO_PLACES[i][0], gotoKeys[i]));
            }

            setOnKeyPressed(e -> {
                KeyCombination key = Scene.getKeyCombination(e);
                if (key == null) return;

                for (int i = 0; i < gotoKeys.length; i++) {
                    if (key.equals(gotoKeys[i])) {
                        forwardBuffer.clear();
                        backBuffer.add(FileFX.path);
                        path = stringToPath(GOTO_PLACES[i][0]);

                        printExecute("Yendo a '"+BLUE+path+RESET+"'");

                        updateTop();
                        updateCenter();
                        centerPane.selectFirst();
                        updateRight();

                        close();
                        return;
                    }
                }

                for (int i = 0; i < leftPane.placesNodes.length; i++) {
                    if (key.equals(KeyCombination.valueOf(String.valueOf(i+1)))) {
                        leftPane.placesNodes[i].open();

                        close();
                        return;
                    }
                }
            });
        }
    }

    public void afterShow() {
        requestFocus();

        if (!SHOW_PLACES) return;
        for (int i = 0; i < leftPane.placesNodes.length; i++) {
            leftPane.placesNodes[i].setIcon(String.valueOf(i+1));
            leftPane.placesNodes[i].setIconId("GotoNode_key");
        }
    }

    public void beforeClose() {
        if (!SHOW_PLACES) return;
        for (int i = 0; i < leftPane.placesNodes.length; i++) {
            leftPane.placesNodes[i].setIcon(placesIcons[i]);
            leftPane.placesNodes[i].setIconId("LeftNode_icon");
        }
    }
}
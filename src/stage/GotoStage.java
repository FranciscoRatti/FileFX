package stage;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.FlowPane;
import main.FileFX;
import node.GotoNode;
import scene.Scene;

import static main.FileFX.*;
import static main.Lib.*;
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

    public void afterClose() {
        if (!SHOW_PLACES) return;
        for (int i = 0; i < leftPane.placesNodes.length; i++) {
            leftPane.placesNodes[i].setIcon(placesIcons[i]);
            leftPane.placesNodes[i].setIconId("LeftNode_icon");
        }
    }
}
package panel;

import javafx.application.Platform;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import node.CenterNode;

import java.util.ArrayList;
import java.util.List;

import static main.Lib.*;
import static panel.CenterPane.centerNodes;
import static panel.MainPane.*;

public class BottomPane extends HBox {
    private static TextField filter;

    public BottomPane() {
        filter = new TextField("");
        filter.setId("bottom_text_field");
        filter.setPromptText("Filtro");
        filter.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(filter, Priority.ALWAYS);
        filter.setOnKeyPressed(e -> {
            KeyCode key = e.getCode();

            if (key.equals(KeyCode.ENTER)) {
                MainPane.filter = filter.getText();
                if (MainPane.filter.isEmpty()) {
                    MainPane.filter = null;
                }

                ArrayList<CenterNode> preSelectedList = new ArrayList<>(List.copyOf(selectedItems));
                updateCenter();
                deselectAll();
                for (CenterNode centerNode : centerNodes) {
                    for (CenterNode preSelected : preSelectedList) {
                        if (preSelected.getName().equals(centerNode.getName())) {
                            preSelectedList.remove(preSelected);
                            centerNode.setSelected(true);
                            break;
                        }
                    }
                    if (preSelectedList.isEmpty()) break;
                }
                if (selectedItem == null && selectedItems.isEmpty()) selectThis();
                Platform.runLater(() -> centerPane.setSelectedOnCenter());
                updateRight();
            }
        });

        getChildren().add(filter);

        setId("bottom_pane");
    }

    public static void focusFilter() {filter.requestFocus();}
    public static boolean isFilterFocus() {return filter.isFocused();}
}

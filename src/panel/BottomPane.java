package panel;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import node.Button;
import node.CenterNode;

import java.util.ArrayList;
import java.util.List;

import static main.Lib.*;
import static main.FileFX.*;
import static panel.CenterPane.*;
import static panel.MainPane.*;

public class BottomPane extends HBox {
    private static TextField filter;
    private Button[] orderButtons;

    public BottomPane() {
        ObservableList<Node> children = getChildren();

        for (String button : BOTTOM_BUTTONS) {
            if (button.equals("order")) {
                orderButtons = new Button[]{
                        new Button(ORDER_ICONS[0], "Nombre", "bottom", e -> changeOrder(ORDER.NAME)),
                        new Button(ORDER_ICONS[1], "Fecha", "bottom", e -> changeOrder(ORDER.DATE)),
                        new Button(ORDER_ICONS[2], "Tamaño", "bottom", e -> changeOrder(ORDER.SIZE)),
                        new Button(ORDER_ICONS[3], "Tipo", "bottom", e -> changeOrder(ORDER.MIME))
                };
                selectOrder();
                children.add(new HBox(2, orderButtons));

            } else if (button.equals("filter")) {
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
                        if (!centerNodes.isEmpty()) centerNodes.getFirst().requestFocus();
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
                        if (selectedItem == null && selectedItems.isEmpty()) selectFirst();
                        Platform.runLater(() -> centerPane.setSelectedOnCenter());
                        updateRight();
                    }
                });

                children.add(filter);
            }
        }

        setId("bottom_pane");
    }

    public static void focusFilter() {filter.requestFocus();}
    public static boolean isFilterFocus() {return filter.isFocused();}

    public void changeOrder(ORDER order) {
        switch (order) {
            case DATE -> {
                DEFAULT_ORDER = ORDER.DATE;
                selectOrder();
                updateCenter();
            }
            case SIZE -> {
                DEFAULT_ORDER = ORDER.SIZE;
                selectOrder();
                updateCenter();
            }
            case MIME -> {
                DEFAULT_ORDER = ORDER.MIME;
                selectOrder();
                updateCenter();
            }
            default -> {
                DEFAULT_ORDER = ORDER.NAME;
                selectOrder();
                updateCenter();
            }
        }
    }
    public void selectOrder() {
        switch (DEFAULT_ORDER) {
            case DATE -> {
                orderButtons[0].changeId("bottom");
                orderButtons[1].changeId("bottom_selected");
                orderButtons[2].changeId("bottom");
                orderButtons[3].changeId("bottom");
            }
            case SIZE -> {
                orderButtons[0].changeId("bottom");
                orderButtons[1].changeId("bottom");
                orderButtons[2].changeId("bottom_selected");
                orderButtons[3].changeId("bottom");
            }
            case MIME -> {
                orderButtons[0].changeId("bottom");
                orderButtons[1].changeId("bottom");
                orderButtons[2].changeId("bottom");
                orderButtons[3].changeId("bottom_selected");
            }
            default -> {
                orderButtons[0].changeId("bottom_selected");
                orderButtons[1].changeId("bottom");
                orderButtons[2].changeId("bottom");
                orderButtons[3].changeId("bottom");
            }
        }
    }
}

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
                        new Button(ORDER_ICONS[0], "Nombre", "BottomNode", e -> changeOrder(ORDER.NAME)),
                        new Button(ORDER_ICONS[1], "Fecha", "BottomNode", e -> changeOrder(ORDER.DATE)),
                        new Button(ORDER_ICONS[2], "Tamaño", "BottomNode", e -> changeOrder(ORDER.SIZE)),
                        new Button(ORDER_ICONS[3], "Tipo", "BottomNode", e -> changeOrder(ORDER.MIME))
                };
                selectOrder();
                children.add(new HBox(2, orderButtons));

            } else if (button.equals("filter")) {
                filter = new TextField("");
                filter.setId("Bottom_textfield");
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

        setId("BottomPane");
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
                orderButtons[0].changeId("BottomNode");
                orderButtons[1].addSuffixId("-selected");
                orderButtons[2].changeId("BottomNode");
                orderButtons[3].changeId("BottomNode");
            }
            case SIZE -> {
                orderButtons[0].changeId("BottomNode");
                orderButtons[1].changeId("BottomNode");
                orderButtons[2].addSuffixId("-selected");
                orderButtons[3].changeId("BottomNode");
            }
            case MIME -> {
                orderButtons[0].changeId("BottomNode");
                orderButtons[1].changeId("BottomNode");
                orderButtons[2].changeId("BottomNode");
                orderButtons[3].addSuffixId("-selected");
            }
            default -> {
                orderButtons[0].addSuffixId("-selected");
                orderButtons[1].changeId("BottomNode");
                orderButtons[2].changeId("BottomNode");
                orderButtons[3].changeId("BottomNode");
            }
        }
    }
}

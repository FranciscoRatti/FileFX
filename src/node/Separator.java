package node;

import javafx.geometry.Orientation;
import javafx.scene.layout.Region;

public class Separator extends Region {
    public Separator(int size, Orientation orientation) {
        if (orientation.equals(Orientation.VERTICAL)) setPrefWidth(size);
        else setPrefHeight(size);
    }
}

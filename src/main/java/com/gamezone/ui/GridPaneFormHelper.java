package com.gamezone.ui;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class GridPaneFormHelper {

    private final GridPane grid = new GridPane();
    private int rowIndex = 0;

    public GridPaneFormHelper() {
        grid.setHgap(10);
        grid.setVgap(10);
    }

    public void addRow(String labelText, Node control) {
        grid.add(new Label(labelText), 0, rowIndex);
        grid.add(control, 1, rowIndex);
        rowIndex++;
    }

    public GridPane getGrid() {
        return grid;
    }
}

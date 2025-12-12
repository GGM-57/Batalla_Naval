package com.example.batalla_naval.util;

import javafx.geometry.Insets;
import javafx.scene.layout.*;

public final class TableroUIFactory {
    private TableroUIFactory() {}

    public static Pane[][] construirTablero(GridPane grid, int tam, int cell) {

        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        grid.setHgap(0);
        grid.setVgap(0);
        grid.setPadding(Insets.EMPTY);

        grid.setPrefSize(cell * tam, cell * tam);
        grid.setMinSize(cell * tam, cell * tam);
        grid.setMaxSize(cell * tam, cell * tam);

        for (int i = 0; i < tam; i++) {
            ColumnConstraints cc = new ColumnConstraints(cell);
            cc.setMinWidth(cell);
            cc.setPrefWidth(cell);
            cc.setMaxWidth(cell);
            grid.getColumnConstraints().add(cc);

            RowConstraints rc = new RowConstraints(cell);
            rc.setMinHeight(cell);
            rc.setPrefHeight(cell);
            rc.setMaxHeight(cell);
            grid.getRowConstraints().add(rc);
        }

        Pane[][] celdas = new Pane[tam][tam];

        for (int fila = 0; fila < tam; fila++) {
            for (int col = 0; col < tam; col++) {
                Pane celda = new Pane();
                celda.getStyleClass().add("celda");
                celda.setPrefSize(cell, cell);
                celda.setMinSize(cell, cell);
                celda.setMaxSize(cell, cell);

                grid.add(celda, col, fila);
                celdas[fila][col] = celda;
            }
        }

        return celdas;
    }
}

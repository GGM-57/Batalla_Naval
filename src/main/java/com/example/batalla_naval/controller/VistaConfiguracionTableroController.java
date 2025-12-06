package com.example.batalla_naval.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class VistaConfiguracionTableroController {
    public Button btnIniciarBatalla;
    public Label informationLabel;
    public Label fragataCount;
    @FXML
    private GridPane playerBoard;

    @FXML
    private void initialize() {
        //agegar logica


        /*codigo que conecta con el VistaConfiguracionTablero.fxml y
        VistaConfiguracionTableroEstilos.css para la creacion e
        independencia de las celdas dentro del gridpane*/
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                Pane cell = new Pane();
                cell.getStyleClass().add("cell");
                cell.setPrefSize(45, 45);
                playerBoard.add(cell, col, row);
            }
        }
    }
}


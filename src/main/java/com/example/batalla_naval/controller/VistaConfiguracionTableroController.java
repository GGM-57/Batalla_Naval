package com.example.batalla_naval.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class VistaConfiguracionTableroController {
    public Button btnIniciarBatalla;
    public Label informationLabel;
    public Label fragataCount;
    public ImageView imageFragatas;
    public ImageView imageDestructores;

    @FXML
    private GridPane playerBoard;

    @FXML
    private void initialize() {
        //agegar logica


        /*llamar el metodo que permite movimiento de navios*/
        enableDrag(imageFragatas);
        setupBoardDragEvents();

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

    /*logica para el movimiento de imagenes en la vista 2 de configuracion del tablero
     * se conecta con VistaConfiguracionTablero.fxml*/

    /*metodo que permite tomar la imagen del panel izquiero*/
    private void enableDrag(ImageView imageView) {
        imageView.setOnDragDetected(event -> {
            Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putImage(imageView.getImage());
            db.setContent(content);

            event.consume();
        });
    }
    /*metodo que permite ubicar la imegen en el player board (gridpane)*/
    private void setupBoardDragEvents() {
        playerBoard.setOnDragOver(event -> {
            if (event.getGestureSource() != playerBoard &&
                    event.getDragboard().hasImage()) {

                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        playerBoard.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (db.hasImage()) {
                ImageView newShip = new ImageView(db.getImage());
                newShip.setFitWidth(45);
                newShip.setPreserveRatio(true);

                // Calculamos la celda donde cayó
                int col = (int) (event.getX() / (playerBoard.getWidth() / 10));
                int row = (int) (event.getY() / (playerBoard.getHeight() / 10));

                playerBoard.add(newShip, col, row);

                event.setDropCompleted(true);
            } else {
                event.setDropCompleted(false);
            }

            event.consume();
        });
    }
}


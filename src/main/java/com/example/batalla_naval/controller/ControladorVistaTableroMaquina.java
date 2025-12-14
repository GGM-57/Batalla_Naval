package com.example.batalla_naval.controller;
import com.example.batalla_naval.model.Barco;
import com.example.batalla_naval.model.Celda;
import com.example.batalla_naval.model.Tablero;
import com.example.batalla_naval.util.SoundEffects;
import com.example.batalla_naval.util.TableroUIFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import com.example.batalla_naval.model.Orientacion;

import java.util.List;

public class ControladorVistaTableroMaquina {


    private static final int CELL = 45;
    private static final int TAM = 10;


    @FXML
    private GridPane gridTableroMaquina;
    @FXML  private Button btnHecho;


    private Pane[][] celdasTablero;

    @FXML
    public void initialize() {

        btnHecho.setOnMouseEntered(e->{
            SoundEffects.playHover();
        });
        celdasTablero = TableroUIFactory.construirTablero(gridTableroMaquina, TAM, CELL);


        String base = "-fx-background-color: #111827; -fx-border-color: #1f2933; -fx-border-width: 1;";
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                celdasTablero[f][c].setStyle(base);
            }
        }
    }


    @FXML
    private void onVolverConfiguracion(ActionEvent event) {
        SoundEffects.playNegativeClick();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Método para recibir los datos desde el ControladorJuego.
     * @param tablero El objeto Tablero de la máquina
     * @param flota La lista de Barcos de la máquina
     */
    public void cargarDatosYMostrar(Tablero tablero, List<Barco> flota) {


        pintarBarcosMaquina(flota);
        pintarDisparos(tablero);
    }

    private void pintarBarcosMaquina(List<Barco> flota) {

        for (Barco barco : flota) {
            int filaInicio = barco.getFila();
            int colInicio  = barco.getColumna();

            javafx.scene.Group formaBarco = barco.clonarForma();
            formaBarco.setOnMouseClicked(null);

            if (barco.getOrientacion() == Orientacion.VERTICAL) {
                javafx.geometry.Bounds b = formaBarco.getBoundsInLocal();
                double pivotX = b.getMinX() + b.getWidth() / 2.0;
                double pivotY = b.getMinY() + b.getHeight() / 2.0;
                formaBarco.getTransforms().add(new javafx.scene.transform.Rotate(90, pivotX, pivotY));
            }

            gridTableroMaquina.add(formaBarco, colInicio, filaInicio);

            GridPane.setHalignment(formaBarco, HPos.CENTER);
            GridPane.setValignment(formaBarco, VPos.CENTER);

            if (barco.getOrientacion() == Orientacion.HORIZONTAL) {
                GridPane.setColumnSpan(formaBarco, barco.getTamanio());
                GridPane.setRowSpan(formaBarco, 1);
            } else {
                GridPane.setRowSpan(formaBarco, barco.getTamanio());
                GridPane.setColumnSpan(formaBarco, 1);
            }

            if (barco.estaHundido()) {
                formaBarco.setOpacity(0.5);
            }
        }
    }



    private void pintarDisparos(Tablero tablero) {
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                Celda celda = tablero.getCelda(f, c);
                Pane p = celdasTablero[f][c];

                if (celda.estaGolpeada()) {
                    if (celda.tieneBarco()) {
                        p.setStyle("-fx-background-color: #f97316; -fx-border-color: #1f2933; -fx-border-width: 1;");
                    } else {
                        p.setStyle("-fx-background-color: #020617; -fx-border-color: #1f2933; -fx-border-width: 1;");
                    }
                }
            }
        }
    }
}

package com.example.batalla_naval.controller;
import com.example.batalla_naval.model.Barco;
import com.example.batalla_naval.model.Celda;
import com.example.batalla_naval.model.Tablero;
import com.example.batalla_naval.util.TableroUIFactory;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import java.util.List;

public class ControladorVistaTableroMaquina {


    private static final int CELL = 45;
    private static final int TAM = 10;


    @FXML
    private GridPane gridTableroMaquina;


    private Pane[][] celdasTablero;

    @FXML
    public void initialize() {

        celdasTablero = TableroUIFactory.construirTablero(gridTableroMaquina, TAM, CELL);


        String base = "-fx-background-color: #111827; -fx-border-color: #1f2933; -fx-border-width: 1;";
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                celdasTablero[f][c].setStyle(base);
            }
        }
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
            int colInicio = barco.getColumna();

            // Clona la forma del barco para evitar mover el original de la vista principal

            javafx.scene.Group formaBarco = barco.clonarForma();


            formaBarco.setOnMouseClicked(null);

            // Posicionar el barco clonado en el GridPane
            gridTableroMaquina.add(formaBarco, colInicio, filaInicio);

            // Centrarlo y aplicar el ColumnSpan
            GridPane.setHalignment(formaBarco, HPos.CENTER);
            GridPane.setValignment(formaBarco, VPos.CENTER);
            GridPane.setColumnSpan(formaBarco, barco.getTamaño());


            if (barco.estaHundido()) {

                formaBarco.setOpacity(0.5);
            }
        }
    }

    private void pintarDisparos(Tablero tablero) {
        // Recorrer el tablero y pintar el estado de cada celda
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                Celda celda = tablero.getCelda(f, c);
                Pane p = celdasTablero[f][c];

                if (celda.estaGolpeada()) {
                    if (celda.tieneBarco()) {
                        // TOCADO o HUNDIDO
                        p.setStyle("-fx-background-color: #f97316; -fx-border-color: #1f2933; -fx-border-width: 1;");
                    } else {
                        // AGUA
                        p.setStyle("-fx-background-color: #020617; -fx-border-color: #1f2933; -fx-border-width: 1;");
                    }
                }
            }
        }
    }
}

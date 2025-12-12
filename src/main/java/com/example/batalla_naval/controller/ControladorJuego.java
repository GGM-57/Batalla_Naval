package com.example.batalla_naval.controller;

import com.example.batalla_naval.model.Barco;
import com.example.batalla_naval.model.Celda;
import com.example.batalla_naval.model.Coordenada;
import com.example.batalla_naval.model.Orientacion;
import com.example.batalla_naval.model.ResultadoDisparo;
import com.example.batalla_naval.model.Tablero;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.util.*;

public class ControladorJuego {

    private static final int TAM = 10;
    private static final int CELL = 45;

    @FXML
    private GridPane gridJugador;
    @FXML
    private GridPane gridMaquina;

    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblTurno;
    @FXML
    private Label lblEstado;

    @FXML
    private Button btnVolverMenu;
    @FXML
    private Button btnRendirse;

    private Tablero tableroJugador;
    private Tablero tableroMaquina;

    private Pane[][] celdasJugador = new Pane[TAM][TAM];
    private Pane[][] celdasMaquina = new Pane[TAM][TAM];

    private final List<Barco> flotaJugador = new ArrayList<>();
    private final List<Barco> flotaMaquina = new ArrayList<>();

    private final boolean[][] disparosMaquina = new boolean[TAM][TAM];

    private final Random random = new Random();

    private boolean turnoJugador = true;
    private boolean juegoTerminado = false;

    // ------------------------------------------------------------------
    // Este método lo llama VistaConfiguracionTableroController
    // cuando se pulsa "¡A LUCHAR!"
    // ------------------------------------------------------------------
    public void initData(Tablero tableroJugador) {
        this.tableroJugador = tableroJugador;

        // Construir grillas visuales
        configurarGrid(gridJugador);
        configurarGrid(gridMaquina);
        crearCeldasVisuales();

        // Dibujar barcos del jugador
        extraerFlotaJugadorDesdeTablero();
        pintarBarcosJugador();

        // Crear tablero de la máquina y su flota
        tableroMaquina = new Tablero(TAM, TAM);
        colocarFlotaMaquinaAleatoria();

        lblTurno.setText("Turno del jugador");
        lblEstado.setText("Haz clic en el tablero de la máquina para disparar.");
    }

    // No usamos initialize() para lógica, porque necesitamos primero el tableroJugador
    @FXML
    private void initialize() {
        // Volver al menú
        btnVolverMenu.setOnAction(this::volverAlMenu);

        // Rendirse
        btnRendirse.setOnAction(e -> {
            if (!juegoTerminado) {
                juegoTerminado = true;
                lblTurno.setText("Juego terminado");
                lblEstado.setText("Te has rendido. La máquina gana.");
                deshabilitarClicksMaquina();
            }
        });
    }


    // ------------------------------------------------------------------
    // Construcción de grillas
    // ------------------------------------------------------------------

    private void configurarGrid(GridPane grid) {
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

        for (int i = 0; i < TAM; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPrefWidth(CELL);
            cc.setMinWidth(CELL);
            cc.setMaxWidth(CELL);
            grid.getColumnConstraints().add(cc);

            RowConstraints rc = new RowConstraints();
            rc.setPrefHeight(CELL);
            rc.setMinHeight(CELL);
            rc.setMaxHeight(CELL);
            grid.getRowConstraints().add(rc);
        }
    }

    private void crearCeldasVisuales() {
        // Celdas del jugador (solo visual, sin eventos)
        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                Pane p = new Pane();
                p.getStyleClass().add("celda-batalla");
                p.setPrefSize(CELL, CELL);
                p.setMinSize(CELL, CELL);
                p.setMaxSize(CELL, CELL);

                gridJugador.add(p, col, fila);
                celdasJugador[fila][col] = p;
            }
        }

        // Celdas de la máquina (reciben disparos del jugador)
        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                final int f = fila;
                final int c = col;

                Pane p = new Pane();
                p.getStyleClass().add("celda-batalla");
                p.setPrefSize(CELL, CELL);
                p.setMinSize(CELL, CELL);
                p.setMaxSize(CELL, CELL);

                p.setOnMouseClicked(e -> manejarDisparoJugador(f, c));

                gridMaquina.add(p, col, fila);
                celdasMaquina[fila][col] = p;
            }
        }
    }

    // ------------------------------------------------------------------
    // Flota del jugador (se extrae del tablero que viene de la config)
    // ------------------------------------------------------------------

    private void extraerFlotaJugadorDesdeTablero() {
        Set<Barco> set = new HashSet<>();

        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                Celda celda = tableroJugador.getCelda(fila, col);
                if (celda.tieneBarco()) {
                    set.add(celda.getBarco());
                }
            }
        }
        flotaJugador.clear();
        flotaJugador.addAll(set);
    }

    private void pintarBarcosJugador() {
        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                Celda celda = tableroJugador.getCelda(fila, col);
                if (celda.tieneBarco()) {
                    Pane p = celdasJugador[fila][col];
                    if (!p.getStyleClass().contains("celda-barco-jugador")) {
                        p.getStyleClass().add("celda-barco-jugador");
                    }
                }
            }
        }
    }

    // ------------------------------------------------------------------
    // Flota de la máquina (aleatoria)
    // ------------------------------------------------------------------

    private void colocarFlotaMaquinaAleatoria() {
        colocarBarcosDeTipoMaquina("Fragata", 1, 4);
        colocarBarcosDeTipoMaquina("Destructor", 2, 3);
        colocarBarcosDeTipoMaquina("Submarino", 3, 2);
        colocarBarcosDeTipoMaquina("Portaaviones", 4, 1);
    }

    private void colocarBarcosDeTipoMaquina(String tipo, int tamaño, int cantidad) {
        int colocados = 0;

        while (colocados < cantidad) {
            Barco barco = new Barco(tipo, tamaño);

            int fila = random.nextInt(TAM);
            int col = random.nextInt(TAM);

            // Por ahora mantenemos orientación horizontal
            Orientacion orientacion = Orientacion.HORIZONTAL;
            Coordenada inicio = new Coordenada(fila, col);

            boolean ok = tableroMaquina.ubicarBarco(barco, inicio, orientacion);
            if (ok) {
                flotaMaquina.add(barco);
                colocados++;
            }
        }
    }

    // ------------------------------------------------------------------
    // Turno del jugador: disparo sobre gridMaquina
    // ------------------------------------------------------------------

    private void manejarDisparoJugador(int fila, int col) {
        if (!turnoJugador || juegoTerminado) {
            return;
        }

        Coordenada objetivo = new Coordenada(fila, col);
        ResultadoDisparo resultado = tableroMaquina.recibirDisparo(objetivo);

        actualizarCeldaMaquina(fila, col, resultado);

        switch (resultado) {
            case AGUA -> lblEstado.setText("Disparaste a (" + fila + ", " + col + "): AGUA.");
            case TOCADO -> lblEstado.setText("Disparaste a (" + fila + ", " + col + "): TOCADO.");
            case HUNDIDO -> lblEstado.setText("¡Hundiste un barco enemigo!");
        }

        if (tableroMaquina.todosBarcosHundidos(flotaMaquina)) {
            juegoTerminado = true;
            lblTurno.setText("Juego terminado");
            lblEstado.setText("¡Has ganado! Hundiste toda la flota enemiga.");
            deshabilitarClicksMaquina();
            return;
        }

        // Pasar turno a la máquina
        turnoJugador = false;
        lblTurno.setText("Turno de la máquina");

        // Turno de la máquina (sencillo)
        turnoMaquina();
    }

    private void actualizarCeldaMaquina(int fila, int col, ResultadoDisparo resultado) {
        Pane p = celdasMaquina[fila][col];

        p.getStyleClass().removeAll("celda-agua", "celda-tocado", "celda-hundido");

        switch (resultado) {
            case AGUA -> {
                if (!p.getStyleClass().contains("celda-agua")) {
                    p.getStyleClass().add("celda-agua");
                }
            }
            case TOCADO -> {
                if (!p.getStyleClass().contains("celda-tocado")) {
                    p.getStyleClass().add("celda-tocado");
                }
            }
            case HUNDIDO -> {
                if (!p.getStyleClass().contains("celda-hundido")) {
                    p.getStyleClass().add("celda-hundido");
                }
            }
        }
    }

    private void deshabilitarClicksMaquina() {
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                celdasMaquina[f][c].setOnMouseClicked(null);
            }
        }
    }

    // ------------------------------------------------------------------
    // Turno de la máquina (IA muy simple: disparo aleatorio)
    // ------------------------------------------------------------------

    private void turnoMaquina() {
        if (juegoTerminado) return;

        int fila, col;

        // Buscar casilla donde todavía no haya disparado
        do {
            fila = random.nextInt(TAM);
            col = random.nextInt(TAM);
        } while (disparosMaquina[fila][col]);

        disparosMaquina[fila][col] = true;

        Coordenada objetivo = new Coordenada(fila, col);
        ResultadoDisparo resultado = tableroJugador.recibirDisparo(objetivo);

        actualizarCeldaJugador(fila, col, resultado);

        switch (resultado) {
            case AGUA -> lblEstado.setText("La máquina disparó a (" + fila + ", " + col + "): AGUA.");
            case TOCADO -> lblEstado.setText("La máquina te ha TOCADO en (" + fila + ", " + col + ").");
            case HUNDIDO -> lblEstado.setText("La máquina hundió uno de tus barcos.");
        }

        if (tableroJugador.todosBarcosHundidos(flotaJugador)) {
            juegoTerminado = true;
            lblTurno.setText("Juego terminado");
            lblEstado.setText("La máquina ha hundido toda tu flota. Has perdido.");
            deshabilitarClicksMaquina();
            return;
        }

        // Regresa el turno al jugador
        turnoJugador = true;
        lblTurno.setText("Turno del jugador");
    }

    private void actualizarCeldaJugador(int fila, int col, ResultadoDisparo resultado) {
        Pane p = celdasJugador[fila][col];

        p.getStyleClass().removeAll("celda-agua", "celda-tocado", "celda-hundido");

        switch (resultado) {
            case AGUA -> {
                if (!p.getStyleClass().contains("celda-agua")) {
                    p.getStyleClass().add("celda-agua");
                }
            }
            case TOCADO -> {
                if (!p.getStyleClass().contains("celda-tocado")) {
                    p.getStyleClass().add("celda-tocado");
                }
            }
            case HUNDIDO -> {
                if (!p.getStyleClass().contains("celda-hundido")) {
                    p.getStyleClass().add("celda-hundido");
                }
            }
        }
    }
    private void volverAlMenu(ActionEvent event) {
        try {
            // Ajusta el nombre del FXML si tu pantalla inicial se llama distinto
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/batalla_naval/VistaInicio.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            lblEstado.setText("Error al volver al menú.");
        }
    }

}

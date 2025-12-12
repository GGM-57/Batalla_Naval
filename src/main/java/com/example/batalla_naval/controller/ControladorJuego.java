package com.example.batalla_naval.controller;

import com.example.batalla_naval.model.Barco;
import com.example.batalla_naval.model.Celda;
import com.example.batalla_naval.model.Coordenada;
import com.example.batalla_naval.model.Orientacion;
import com.example.batalla_naval.model.ResultadoDisparo;
import com.example.batalla_naval.model.Tablero;
import com.example.batalla_naval.util.MusicTrack;
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
import com.example.batalla_naval.util.SoundEffects;
import com.example.batalla_naval.util.MusicManager;



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
    private final boolean[][] disparosJugador = new boolean[TAM][TAM];


    private final Random random = new Random();
    private boolean musicaBatallaIniciada = false;

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
        MusicManager.playLoop(MusicTrack.BATALLA, 0.35);


        btnVolverMenu.setOnMouseEntered(e->{
            SoundEffects.playHover();
        });

        btnVolverMenu.setOnAction(e -> {
            System.out.println(">>> Sonido del botón presionado");
            SoundEffects.playClick();

            SoundEffects.playClick();
            volverAlMenu(e);
        });

        btnRendirse.setOnMouseEntered(e->{
            SoundEffects.playHover();

        });


        // CLICK Volver al menú
        btnVolverMenu.setOnAction(e -> {
            SoundEffects.playClick();
            volverAlMenu(e);
        });

        // CLICK Rendirse
        btnRendirse.setOnAction(e -> {

            SoundEffects.playNegativeClick();

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
        // Celdas del jugador
        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                Pane p = new Pane();
                p.setPrefSize(CELL, CELL);
                p.setMinSize(CELL, CELL);
                p.setMaxSize(CELL, CELL);

                // estilo base
                p.setStyle("-fx-background-color: #111827; -fx-border-color: #1f2933; -fx-border-width: 1;");

                gridJugador.add(p, col, fila);
                celdasJugador[fila][col] = p;
            }
        }

        // Celdas de la máquina
        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                final int f = fila;
                final int c = col;

                Pane p = new Pane();
                p.setPrefSize(CELL, CELL);
                p.setMinSize(CELL, CELL);
                p.setMaxSize(CELL, CELL);

                // estilo base
                p.setStyle("-fx-background-color: #111827; -fx-border-color: #1f2933; -fx-border-width: 1;");

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
        System.out.println("Barcos del jugador en batalla: " + flotaJugador.size());
    }

    private void pintarBarcosJugador() {

        // Opcional: dejar todas las celdas con estilo base oscuro (sin azul)
        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                Pane p = celdasJugador[fila][col];
                p.setStyle("-fx-background-color: #111827; -fx-border-color: #1f2933; -fx-border-width: 1;");
            }
        }

        // Poner cada barco como una sola figura en el GridPane
        for (Barco barco : flotaJugador) {

            int filaInicio = barco.getFila();
            int colInicio  = barco.getColumna();

            // Dibujo original del barco (el mismo de la pantalla de configuración)
            javafx.scene.Group forma = barco.getForma();

            // Importante: quitar interacción (no queremos rotarlo en batalla)
            forma.setOnMouseClicked(null);

            // Añadir al grid del jugador en la celda inicial
            gridJugador.add(forma, colInicio, filaInicio);

            // Hacer que ocupe varias celdas según su orientación y tamaño
            if (barco.esVertical()) {
                GridPane.setRowSpan(forma, barco.getTamaño());
            } else {
                GridPane.setColumnSpan(forma, barco.getTamaño());
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

        // ⬇️ NUEVO: si ya disparaste aquí, no hagas nada
        if (disparosJugador[fila][col]) {
            lblEstado.setText("Ya disparaste a (" + fila + ", " + col + "). Elige otra casilla.");
            return;
        }
        disparosJugador[fila][col] = true;

        Coordenada objetivo = new Coordenada(fila, col);
        ResultadoDisparo resultado = tableroMaquina.recibirDisparo(objetivo);

        actualizarCeldaMaquina(fila, col, resultado);

        switch (resultado) {
            case AGUA ->{
                lblEstado.setText("Disparaste a (" + fila + ", " + col + "): AGUA.");
                //SoundEffects.misilFallado();
            }
            case TOCADO ->{
                lblEstado.setText("Disparaste a (" + fila + ", " + col + "): TOCADO.");
                SoundEffects.playExplosion1();
            }
            case HUNDIDO ->{
                lblEstado.setText("¡Hundiste un barco enemigo!");
                SoundEffects.playExplosion2();
            }

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

        turnoMaquina();
    }


    private void actualizarCeldaMaquina(int fila, int col, ResultadoDisparo resultado) {
        Pane p = celdasMaquina[fila][col];

        switch (resultado) {
            case AGUA -> p.setStyle(
                    "-fx-background-color: #020617; -fx-border-color: #1f2933; -fx-border-width: 1;");
            case TOCADO -> p.setStyle(
                    "-fx-background-color: #f97316; -fx-border-color: #1f2933; -fx-border-width: 1;");
            case HUNDIDO -> {
                p.setStyle(
                        "-fx-background-color: #b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");
                // PINTAR TODO EL BARCO HUNDIDO EN ROJO
                marcarBarcoHundido(tableroMaquina, celdasMaquina, fila, col);
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
            case AGUA -> {
                lblEstado.setText("La máquina disparó a (" + fila + ", " + col + "): AGUA.");
                SoundEffects.misilFallado();
            }
            case TOCADO -> {
                lblEstado.setText("La máquina te ha TOCADO en (" + fila + ", " + col + ").");
                SoundEffects.stopAguaSalpicada();
                SoundEffects.playExplosion1();
            }
            case HUNDIDO -> {
                lblEstado.setText("La máquina hundió uno de tus barcos.");
                SoundEffects.stopAguaSalpicada();
                SoundEffects.playExplosion2();
                activarMusicaBatalla();
            }
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

        switch (resultado) {
            case AGUA -> p.setStyle(
                    "-fx-background-color: #020617; -fx-border-color: #1f2933; -fx-border-width: 1;");
            case TOCADO -> p.setStyle(
                    "-fx-background-color: #f97316; -fx-border-color: #1f2933; -fx-border-width: 1;");
            case HUNDIDO -> {
                p.setStyle(
                        "-fx-background-color: #b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");
              /*marcar el barco undido*/
                marcarBarcoHundido(tableroJugador, celdasJugador, fila, col);
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
    private void marcarBarcoHundido(Tablero tablero, Pane[][] celdas, int fila, int col) {
        Celda celdaImpacto = tablero.getCelda(fila, col);
        if (!celdaImpacto.tieneBarco()) return;

        Barco barcoHundido = celdaImpacto.getBarco();

        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                Celda celda = tablero.getCelda(f, c);
                if (celda.tieneBarco() && celda.getBarco() == barcoHundido) {
                    Pane p = celdas[f][c];
                    p.setStyle(
                            "-fx-background-color: #b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");
                }
            }
        }
    }
    private void activarMusicaBatalla() {
        if (!musicaBatallaIniciada) {
            musicaBatallaIniciada = true;
            // Cambia este método por el que tengas para el soundtrack de batalla
            MusicManager.playLoop(MusicTrack.PROBLEMAS, 0.35);
        }
    }


}

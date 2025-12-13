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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
import com.example.batalla_naval.util.TableroUIFactory;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import com.example.batalla_naval.model.SesionJuego;
import java.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.PauseTransition;




public class ControladorJuego {

    private static final int CELL = 45;
    private static final int TAM = 10;

    @FXML private GridPane gridJugador;
    @FXML private GridPane gridMaquina;
    @FXML private Label lblTitulo;
    @FXML private Label lblTurno;
    @FXML private Label lblEstado;
    @FXML private Button btnVolverMenu;
    @FXML private Button btnRendirse;



    @FXML
    private Label lblTableroJugador;


    @FXML
    private Label lblCronometro;

    private Timeline cronometro;
    private int segundos = 0;

    private Tablero tableroJugador;
    private Tablero tableroMaquina;

    private Pane[][] celdasJugador;
    private Pane[][] celdasMaquina;

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
        celdasJugador = TableroUIFactory.construirTablero(gridJugador, TAM, CELL);
        celdasMaquina = TableroUIFactory.construirTablero(gridMaquina, TAM, CELL);

        // Asegurar que el grid no tenga separaciones
        gridJugador.setHgap(0);
        gridJugador.setVgap(0);
        gridMaquina.setHgap(0);
        gridMaquina.setVgap(0);

// Forzar el mismo estilo base en ambos tableros (evita que el enemigo se vea "más oscuro")
        String base = "-fx-background-color: #111827; -fx-border-color: #1f2933; -fx-border-width: 1;";
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                celdasJugador[f][c].setStyle(base);
                celdasMaquina[f][c].setStyle(base);
            }
        }

        /*codigo para dar click en las celdas del enemigo*/
        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                final int f = fila;
                final int c = col;
                celdasMaquina[fila][col].setOnMouseClicked(e -> manejarDisparoJugador(f, c));
            }
        }



        // Dibujar barcos del jugador
        extraerFlotaJugadorDesdeTablero();
        pintarBarcosJugador();

        // Crear tablero de la máquina y su flota
        tableroMaquina = new Tablero(TAM, TAM);
        colocarFlotaMaquinaAleatoria();

        lblTurno.setText("Turno del jugador");
        lblEstado.setText("Haz clic en el tablero de la máquina para disparar.");

        iniciarCronometro();
        System.out.println("CRONO: iniciado");
    }

    // No usamos initialize() para lógica, porque necesitamos primero el tableroJugador
    @FXML
    private void initialize() {
        String nombre = SesionJuego.getNombreJugador();
        lblTurno.setText("Turno de " + nombre);
        if (lblTableroJugador != null) {
            lblTableroJugador.setText("Tablero de " + nombre);
        } else {
            System.out.println("ERROR: lblTableroJugador no está inyectado (fx:id no coincide en VistaBatalla.fxml)");
        }


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
                detenerCronometro();
                lblTurno.setText("Juego terminado");
                lblEstado.setText("Te has rendido. La máquina gana.");
                deshabilitarClicksMaquina();
            }
        });

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
// Resetear offsets (muy importante para que no quede corrido)
            forma.setTranslateX(0);
            forma.setTranslateY(0);
            forma.setLayoutX(0);
            forma.setLayoutY(0);

// Añadir al grid del jugador
            gridJugador.add(forma, colInicio, filaInicio);

// Centrar dentro de la celda (o del área que ocupa)
            GridPane.setHalignment(forma, HPos.CENTER);
            GridPane.setValignment(forma, VPos.CENTER);

            // Hacer que ocupe varias celdas según su orientación y tamaño
            GridPane.setColumnSpan(forma, barco.getTamaño());

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
            detenerCronometro();
            lblTurno.setText("Juego terminado");
            lblEstado.setText("¡Has ganado! Hundiste toda la flota enemiga.");
            deshabilitarClicksMaquina();
            return;
        }

        // Pasar turno a la máquina
        turnoJugador = false;
        lblTurno.setText("Turno de la máquina");

        simularPensandoMaquina();
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

// ✅ Copias finales para usar en lambda
        final int filaFinal = fila;
        final int colFinal = col;
        final ResultadoDisparo resultadoFinal = resultado;

// ✅ Sonido del proyectil primero
        SoundEffects.proyectilLanzado();

// ✅ Esperar y luego aplicar el daño + sonido de impacto
        reproducirImpactoConRetraso(() -> {

            // 1) Pintar el daño DESPUÉS del tiempo
            actualizarCeldaJugador(filaFinal, colFinal, resultadoFinal);

            // 2) Texto + sonidos de impacto
            switch (resultadoFinal) {
                case AGUA -> {
                    lblEstado.setText("La máquina disparó a (" + filaFinal + ", " + colFinal + "): AGUA.");
                    SoundEffects.misilFallado();
                }
                case TOCADO -> {
                    lblEstado.setText("La máquina te ha TOCADO en (" + filaFinal + ", " + colFinal + ").");
                    SoundEffects.playExplosion1();
                }
                case HUNDIDO -> {
                    lblEstado.setText("La máquina hundió uno de tus barcos.");
                    SoundEffects.playExplosion2();
                    activarMusicaBatalla();
                }
            }

            // 3) Verificar fin del juego (después de aplicar daño)
            if (tableroJugador.todosBarcosHundidos(flotaJugador)) {
                juegoTerminado = true;
                lblTurno.setText("Juego terminado");
                lblEstado.setText("La máquina ha hundido toda tu flota. Has perdido.");
                deshabilitarClicksMaquina();
                return;
            }

            // 4) Regresar turno al jugador
            turnoJugador = true;
            lblTurno.setText("Turno del jugador");
        });

        turnoJugador = false;
        lblTurno.setText("Turno de la máquina");


        if (tableroJugador.todosBarcosHundidos(flotaJugador)) {
            juegoTerminado = true;
            detenerCronometro();
            lblTurno.setText("Juego terminado");
            lblEstado.setText("La máquina ha hundido toda tu flota. Has perdido.");
            deshabilitarClicksMaquina();
            return;
        }

        // Regresa el turno al jugador
        turnoJugador = true;
        lblTurno.setText("Turno del jugador");
    }
    private void simularPensandoMaquina() {
        if (juegoTerminado) return;

        lblEstado.setText("La máquina está pensando...");
        turnoMaquina();

    }


    private void actualizarCeldaJugador(int fila, int col, ResultadoDisparo resultado) {
        Pane p = celdasJugador[fila][col];

        switch (resultado) {
            case AGUA -> p.setStyle(
                    "-fx-background-color: #020617; -fx-border-color: #1f2933; -fx-border-width: 1;");
            case TOCADO -> p.setStyle(
                    "-fx-background-color: #f97316; -fx-border-color: #1f2933; -fx-border-width: 1;");
            case HUNDIDO -> {

//                p.setStyle(
//                        "-fx-background-color: #b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");
//                mostrarAnimacionHundimiento(p); /*insertar animacion de explosion*/
              /*marcar el barco undido*/
                marcarBarcoHundido(tableroJugador, celdasJugador, fila, col);
            }
        }
    }

    //metodo para animacion de explosion en barco hundido
    private void mostrarAnimacionHundimiento(Pane celda) {
        try {
            celda.setStyle("-fx-background-color: transparent; -fx-border-color: #1f2933; -fx-border-width: 1;");
            celda.getChildren().clear();

            String rutaGif = "/com/example/batalla_naval/images/explosion1.gif"; // Cambia esta ruta
            Image gifImage = new Image(getClass().getResource(rutaGif).toExternalForm());


            ImageView gifView = new ImageView(gifImage);

            gifView.fitWidthProperty().bind(celda.widthProperty());
            gifView.fitHeightProperty().bind(celda.heightProperty());
            gifView.setPreserveRatio(false); // Estirar el GIF para cubrir


//            celda.getChildren().clear();
            celda.getChildren().add(gifView);

        } catch (Exception e) {
            System.err.println("Error al cargar o mostrar el GIF de hundimiento: " + e.getMessage());
            // En caso de error (ej. GIF no encontrado), ponemos el color rojo como fallback.
            celda.setStyle("-fx-background-color: #b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");
        }
    }

    private void volverAlMenu(ActionEvent event) {
        detenerCronometro();

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

        if (tablero == tableroJugador) {
            Node formaBarco = barcoHundido.getForma();

            // Verificamos si la forma existe y si su padre es gridJugador
            if (formaBarco != null && formaBarco.getParent() instanceof GridPane gridPadre) {
                gridPadre.getChildren().remove(formaBarco);
            }

        }
        for (int f = 0; f < TAM; f++) {
            for (int c = 0; c < TAM; c++) {
                Celda celda = tablero.getCelda(f, c);
                if (celda.tieneBarco() && celda.getBarco() == barcoHundido) {
                    Pane p = celdas[f][c];
                    mostrarAnimacionHundimiento(p);
//                    p.setStyle(
//                            "-fx-background-color: #b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");
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
    private void iniciarCronometro() {
        detenerCronometro(); // evita duplicados

        segundos = 0;
        if (lblCronometro != null) {
            lblCronometro.setText("00:00");
        }

        cronometro = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            segundos++;
            int min = segundos / 60;
            int seg = segundos % 60;
            if (lblCronometro != null) {
                lblCronometro.setText(String.format("%02d:%02d", min, seg));
            }
        }));

        cronometro.setCycleCount(Timeline.INDEFINITE);
        cronometro.play();
    }

    private void detenerCronometro() {
        if (cronometro != null) {
            cronometro.stop();
            cronometro = null;
        }
    }



    private void reproducirImpactoConRetraso(Runnable sonidoExplosion) {
        SoundEffects.proyectilLanzado(); // 1. sonido del misil

        PauseTransition pausa = new PauseTransition(Duration.seconds(1.5));
        pausa.setOnFinished(e -> sonidoExplosion.run());
        pausa.play();
    }


}

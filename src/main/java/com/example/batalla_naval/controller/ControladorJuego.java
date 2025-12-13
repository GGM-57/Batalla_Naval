package com.example.batalla_naval.controller;

import com.example.batalla_naval.model.*;
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

import java.util.*;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.animation.PauseTransition;




public class ControladorJuego {

    private static final int CELL= 45;
    private static final int TAM= 10;
    /*  tiempo de espera de la maquina*/
    private static final double DELAY_MAQUINA_SEG= 0.5;
    public Button btnAccionMaquina; /*boton para mostrar tablero maquina*/

    @FXML private GridPane gridJugador;
    @FXML private GridPane gridMaquina;
    @FXML private Label lblTitulo;
    @FXML private Label lblTurno;
    @FXML private Label lblEstado;
    @FXML private Button btnVolverMenu;
    @FXML private Button btnRendirse;
    /*variable de estado para determinar cuando inicia el juego y habilitar boton de revelar tablero*/
    @FXML boolean JuegoIniciado =false;



    @FXML
    private Label lblTableroJugador;


    @FXML
    private Label lblCronometro;

    private Timeline cronometro;
    private int segundos= 0;
    private ControlIA iaMaquina;

    private Tablero tableroJugador;
    private Tablero tableroMaquina;

    private Pane[][] celdasJugador;
    private Pane[][] celdasMaquina;

    private final List<Barco> flotaJugador= new ArrayList<>();
    private final List<Barco> flotaMaquina= new ArrayList<>();

    private final boolean[][] disparosJugador= new boolean[TAM][TAM];
    private static final String STYLE_HALO_SEGURO ="-fx-background-color: #0b1b2a; -fx-border-color: #1f2933; -fx-border-width: 1;";


    private final Random random= new Random();
    private boolean musicaBatallaIniciada= false;

    private boolean turnoJugador= true;
    private boolean juegoTerminado= false;





    public void initData(Tablero tableroJugador) {
        this.tableroJugador= tableroJugador;

        /*inicializacion de celdas*/
        celdasJugador= TableroUIFactory.construirTablero(gridJugador, TAM, CELL);
        celdasMaquina= TableroUIFactory.construirTablero(gridMaquina, TAM, CELL);

        /*inicializacion de IA*/
        iaMaquina= new ControlIA(TAM);

        gridJugador.setHgap(0);
        gridJugador.setVgap(0);
        gridMaquina.setHgap(0);
        gridMaquina.setVgap(0);


        String base= "-fx-background-color: #111827; -fx-border-color: #1f2933; -fx-border-width: 1;";
        for (int f= 0; f < TAM; f++) {
            for (int c= 0; c < TAM; c++) {
                celdasJugador[f][c].setStyle(base);
                celdasMaquina[f][c].setStyle(base);
            }
        }

        /*habilitacion del boton de mostrar tablero maquina*/
        if (btnAccionMaquina != null) {
            btnAccionMaquina.setDisable(false);
        }

        /*codigo para dar click en las celdas del enemigo*/
        for (int fila= 0; fila < TAM; fila++) {
            for (int col= 0; col < TAM; col++) {
                final int f= fila;
                final int c= col;
                celdasMaquina[fila][col].setOnMouseClicked(e -> {
                   /*deshabiliatar el boton de revelar tablero del oponente*/
                    if (btnAccionMaquina != null) {
                    btnAccionMaquina.setDisable(true);
                    }

                    manejarDisparoJugador(f, c);
                });
            }
        }

        extraerFlotaJugadorDesdeTablero();
        pintarBarcosJugador();


        tableroMaquina= new Tablero(TAM, TAM);
        colocarFlotaMaquinaAleatoria();

        lblTurno.setText("Turno del jugador");
        lblEstado.setText("Haz clic en el tablero de la máquina para disparar.");

//        iniciarCronometro();
//        System.out.println("CRONO: iniciado");
    }


    @FXML
    private void initialize() {
        String nombre= SesionJuego.getNombreJugador();
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



        btnVolverMenu.setOnAction(e -> {
            SoundEffects.playClick();
            volverAlMenu(e);
        });


        btnRendirse.setOnAction(e -> {

            SoundEffects.playNegativeClick();

            if (!juegoTerminado) {
                juegoTerminado= true;
                detenerCronometro();
                lblTurno.setText("Juego terminado");
                lblEstado.setText("Te has rendido. La máquina gana.");
                deshabilitarClicksMaquina();
            }
        });

    }








    private void extraerFlotaJugadorDesdeTablero() {
        Set<Barco> set= new HashSet<>();

        for (int fila= 0; fila < TAM; fila++) {
            for (int col= 0; col < TAM; col++) {
                Celda celda= tableroJugador.getCelda(fila, col);
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


        for (int fila= 0; fila < TAM; fila++) {
            for (int col= 0; col < TAM; col++) {
                Pane p= celdasJugador[fila][col];
                p.setStyle("-fx-background-color: #111827; -fx-border-color: #1f2933; -fx-border-width: 1;");
            }
        }


        for (Barco barco : flotaJugador) {

            int filaInicio= barco.getFila();
            int colInicio = barco.getColumna();


            javafx.scene.Group forma= barco.getForma();


            forma.setOnMouseClicked(null);



            forma.setTranslateX(0);
            forma.setTranslateY(0);
            forma.setLayoutX(0);
            forma.setLayoutY(0);


            gridJugador.add(forma, colInicio, filaInicio);


            GridPane.setHalignment(forma, HPos.CENTER);
            GridPane.setValignment(forma, VPos.CENTER);


            GridPane.setColumnSpan(forma, barco.getTamaño());

        }
    }







    private void colocarFlotaMaquinaAleatoria() {
        colocarBarcosDeTipoMaquina("Fragata", 1, 4);
        colocarBarcosDeTipoMaquina("Destructor", 2, 3);
        colocarBarcosDeTipoMaquina("Submarino", 3, 2);
        colocarBarcosDeTipoMaquina("Portaaviones", 4, 1);
    }

    private void colocarBarcosDeTipoMaquina(String tipo, int tamaño, int cantidad) {
        int colocados= 0;

        while (colocados < cantidad) {
            Barco barco= new Barco(tipo, tamaño);

            int fila= random.nextInt(TAM);
            int col= random.nextInt(TAM);


            Orientacion orientacion= Orientacion.HORIZONTAL;
            Coordenada inicio= new Coordenada(fila, col);

            boolean ok= tableroMaquina.ubicarBarco(barco, inicio, orientacion);
            if (ok) {
                flotaMaquina.add(barco);
                colocados++;
            }
        }
    }





    private void manejarDisparoJugador(int fila, int col) {
        if (!turnoJugador || juegoTerminado) {
            return;
        }

        if (!JuegoIniciado) {
            JuegoIniciado = true;
            iniciarCronometro(); /*Inicia el juego y el tiempo*/
            /*Deshabilita el botón de revelar tablero oponente después del primer disparo*/

            if (btnAccionMaquina != null) {
                btnAccionMaquina.setDisable(true);
            }
        }

        if (disparosJugador[fila][col]) {
            lblEstado.setText("Ya disparaste a (" + fila + ", " + col + "). Elige otra casilla.");
            return;
        }
        disparosJugador[fila][col]= true;

        Coordenada objetivo= new Coordenada(fila, col);
        ResultadoDisparo resultado= tableroMaquina.recibirDisparo(objetivo);

        actualizarCeldaMaquina(fila, col, resultado);

        switch (resultado) {
            case AGUA ->{
                lblEstado.setText("Disparaste a (" + fila + ", " + col + "): AGUA.");

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
            juegoTerminado= true;
            detenerCronometro();
            deshabilitarClicksMaquina();

            String tiempo= (lblCronometro != null) ? lblCronometro.getText() : "00:00";
            String nombre= SesionJuego.getNombreJugador();
            String resumen= "¡" + nombre + " ganaste! Tiempo: " + tiempo;

            NavegadorEscenas.irAVictoria(gridJugador, resumen);
            return;
        }



        turnoJugador= false;
        lblTurno.setText("Turno de la máquina");

        simularPensandoMaquina();
    }


    private void actualizarCeldaMaquina(int fila, int col, ResultadoDisparo resultado) {
        Pane p= celdasMaquina[fila][col];

        switch (resultado) {
            case AGUA -> {
                p.setStyle("-fx-background-color: #020617; -fx-border-color: #1f2933; -fx-border-width: 1;");
            }
            case TOCADO -> {
                marcarBarcoTocado(tableroMaquina, celdasMaquina, fila, col);
            }

            case HUNDIDO -> {
                p.setStyle("-fx-background-color: #b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");
                marcarBarcoHundido(tableroMaquina, celdasMaquina, fila, col);
            }
        }
    }



    private void deshabilitarClicksMaquina() {
        for (int f= 0; f < TAM; f++) {
            for (int c= 0; c < TAM; c++) {
                celdasMaquina[f][c].setOnMouseClicked(null);
            }
        }
    }





    private void turnoMaquina() {
        if (juegoTerminado) return;

        Coordenada objetivo= iaMaquina.elegirDisparo();
        int fila= objetivo.getFila();
        int col = objetivo.getColumna();
        ResultadoDisparo resultado= tableroJugador.recibirDisparo(objetivo);

        iaMaquina.informarResultado(tableroJugador, objetivo, resultado);


        final int filaFinal= fila;
        final int colFinal= col;
        final ResultadoDisparo resultadoFinal= resultado;


        SoundEffects.proyectilLanzado();


        reproducirImpactoConRetraso(() -> {


            actualizarCeldaJugador(filaFinal, colFinal, resultadoFinal);


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


            if (tableroJugador.todosBarcosHundidos(flotaJugador)) {
                juegoTerminado= true;
                lblTurno.setText("Juego terminado");
                lblEstado.setText("La máquina ha hundido toda tu flota. Has perdido.");
                deshabilitarClicksMaquina();
                return;
            }


            turnoJugador= true;
            lblTurno.setText("Turno del jugador");
        });

        turnoJugador= false;
        lblTurno.setText("Turno de la máquina");


        if (tableroJugador.todosBarcosHundidos(flotaJugador)) {
            juegoTerminado= true;
            detenerCronometro();
            lblTurno.setText("Juego terminado");
            lblEstado.setText("La máquina ha hundido toda tu flota. Has perdido.");
            deshabilitarClicksMaquina();
            return;
        }


        turnoJugador= true;
        lblTurno.setText("Turno del jugador");
    }
    private void simularPensandoMaquina() {
        if (juegoTerminado) return;

        lblEstado.setText("La máquina está pensando...");

        PauseTransition pause= new PauseTransition(Duration.seconds(DELAY_MAQUINA_SEG));
        pause.setOnFinished(e -> {
            if (!juegoTerminado) {
                turnoMaquina();
            }
        });
        pause.play();
    }


    private void actualizarCeldaJugador(int fila, int col, ResultadoDisparo resultado) {
        Pane p= celdasJugador[fila][col];

        switch (resultado) {
            case AGUA -> {
                    p.setStyle("-fx-background-color: #020617; -fx-border-color: #1f2933; -fx-border-width: 1;");
            }
            case TOCADO -> {
                p.setStyle("-fx-background-color:#f97316; -fx-border-color: #1f2933; -fx-border-width: 1;");
                marcarBarcoTocado(tableroJugador, celdasJugador, fila, col);

            }
            case HUNDIDO -> {
                p.setStyle("-fx-background-color:#b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");
                marcarBarcoHundido(tableroJugador, celdasJugador, fila, col);
            }
        }
    }


    private void mostrarAnimacionHundimiento(Pane celda) {
        try {
            celda.getChildren().clear();
            celda.setStyle("-fx-background-color:#b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");

            String rutaGif= "/com/example/batalla_naval/images/explosion1.gif";
            Image gifImage= new Image(getClass().getResource(rutaGif).toExternalForm());


            ImageView gifView= new ImageView(gifImage);

            gifView.fitWidthProperty().bind(celda.widthProperty());
            gifView.fitHeightProperty().bind(celda.heightProperty());
            gifView.setPreserveRatio(false);

            celda.getChildren().add(gifView);

        } catch (Exception e) {
            System.err.println("Error al cargar o mostrar el GIF de hundimiento: " + e.getMessage());
            celda.setStyle("-fx-background-color: #b91c1c; -fx-border-color: #1f2933; -fx-border-width: 1;");
        }
    }

    private void mostrarAnimacionTocado(Pane celda) {
        try {
            celda.setStyle("-fx-background-color:#f97316; -fx-border-color: #1f2933; -fx-border-width: 1;");

            String rutaGif = "/com/example/batalla_naval/images/fuego1.gif";
            Image gifImage = new Image(getClass().getResource(rutaGif).toExternalForm());

            ImageView gifView = new ImageView(gifImage);

            gifView.fitWidthProperty().bind(celda.widthProperty());
            gifView.fitHeightProperty().bind(celda.heightProperty());
            gifView.setPreserveRatio(false);

            celda.getChildren().add(gifView);

        } catch (Exception e) {
            System.err.println("Error al cargar o mostrar el GIF de fuego: " + e.getMessage());
            celda.setStyle("-fx-background-color: #f97316; -fx-border-color: #1f2933; -fx-border-width: 1;");
        }
    }

    private void volverAlMenu(ActionEvent event) {
        detenerCronometro();

        try {

            FXMLLoader loader= new FXMLLoader(
                    getClass().getResource("/com/example/batalla_naval/VistaInicio.fxml")
            );
            Parent root= loader.load();

            Stage stage= (Stage) ((Node) event.getSource())
                    .getScene().getWindow();
            Scene scene= new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            lblEstado.setText("Error al volver al menú.");
        }
    }
    private void marcarBarcoHundido(Tablero tablero, Pane[][] celdas, int fila, int col) {
        Celda celdaImpacto= tablero.getCelda(fila, col);
        if (!celdaImpacto.tieneBarco()) return;

        Barco barcoHundido= celdaImpacto.getBarco();

        if (tablero==tableroJugador) {
            Node formaBarco= barcoHundido.getForma();


            if (formaBarco != null && formaBarco.getParent() instanceof GridPane gridPadre) {
                gridPadre.getChildren().remove(formaBarco);
            }

        }
        for (int f= 0; f < TAM; f++) {
            for (int c= 0; c < TAM; c++) {
                Celda celda= tablero.getCelda(f, c);
                if (celda.tieneBarco() && celda.getBarco()==barcoHundido) {
                    Pane p= celdas[f][c];
                    mostrarAnimacionHundimiento(p);


                }
            }
        }
        marcarHaloHundido(tablero, celdas, barcoHundido);

    }

    private void marcarBarcoTocado(Tablero tablero, Pane[][] celdas, int fila, int col) {
        Pane p = celdas[fila][col];

        p.setStyle("-fx-background-color: #f97316; -fx-border-color: #1f2933; -fx-border-width: 1;");
        /*inserta animacion de fuego*/
        mostrarAnimacionTocado(p);
    }

    private void marcarHaloHundido(Tablero tablero, Pane[][] celdas, Barco barcoHundido) {
        for (int f= 0; f < TAM; f++) {
            for (int c= 0; c < TAM; c++) {
                Celda celda= tablero.getCelda(f, c);
                if (celda.tieneBarco() && celda.getBarco()==barcoHundido) {

                    for (int df= -1; df <= 1; df++) {
                        for (int dc= -1; dc <= 1; dc++) {
                            int ff= f + df;
                            int cc= c + dc;
                            if (ff < 0 || ff >= TAM || cc < 0 || cc >= TAM) continue;


                            if (!tablero.getCelda(ff, cc).tieneBarco()) {
                                Pane p= celdas[ff][cc];


                                String s= p.getStyle();
                                if (s != null && (s.contains("#f97316") || s.contains("#b91c1c"))) continue;

                                p.setStyle(STYLE_HALO_SEGURO);
                            }
                        }
                    }
                }
            }
        }
    }



    private void activarMusicaBatalla() {
        if (!musicaBatallaIniciada) {
            musicaBatallaIniciada= true;

            MusicManager.playLoop(MusicTrack.PROBLEMAS, 0.35);
        }
    }
    private void iniciarCronometro() {
        detenerCronometro();

        segundos= 0;
        if (lblCronometro != null) {
            lblCronometro.setText("00:00");
        }

        cronometro= new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            segundos++;
            int min= segundos / 60;
            int seg= segundos % 60;
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
            cronometro= null;
        }
    }



    private void reproducirImpactoConRetraso(Runnable sonidoExplosion) {
        SoundEffects.proyectilLanzado();

        PauseTransition pausa= new PauseTransition(Duration.seconds(1.5));
        pausa.setOnFinished(e -> sonidoExplosion.run());
        pausa.play();
    }
    private void mostrarPantallaVictoria() {
        try {

            String tiempo= (lblCronometro != null) ? lblCronometro.getText() : "00:00";
            String nombre= SesionJuego.getNombreJugador();

            FXMLLoader loader= new FXMLLoader(
                    getClass().getResource("/com/example/batalla_naval/VistaVictoria.fxml")
            );
            Parent root= loader.load();


            ControladorVictoria ctrl= loader.getController();
            ctrl.setResumen("¡" + nombre + " ganaste! Tiempo: " + tiempo);


            Stage stage= (Stage) gridJugador.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblEstado.setText("Error al cargar pantalla de victoria.");
        }
    }

/*metodo para mostrar el tablero de la maquina en segundo plano. Se conecta con VistaTableroMaquina.FXML
 y con el ControladorVistaTableroMaquina*/
    public void handleAccionMaquina(ActionEvent actionEvent) {
        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/batalla_naval/VistaTableroMaquina.fxml")
            );
            Parent root = loader.load();

            ControladorVistaTableroMaquina ctrlMaquina = loader.getController();

            ctrlMaquina.cargarDatosYMostrar(this.tableroMaquina, this.flotaMaquina);

            //Crear el nuevo Stage (Ventana)
            Stage stage = new Stage();
            stage.setTitle("Tablero de la Máquina");
            stage.setScene(new Scene(root));


            // Mostrar la ventana SIN bloquear la principal
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblEstado.setText("Error al cargar la vista del tablero de la máquina.");
        }
    }
}

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
import javafx.stage.Modality;
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
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.transform.Rotate;
import javafx.geometry.Bounds;





public class ControladorJuego {

    private static final int CELL= 45;
    private static final int TAM= 10;
    /*  tiempo de espera de la maquina*/
    private static final double DELAY_MAQUINA_SEG= 0.5;
    public Button btnVstaBarcosEnemigos; /*boton para mostrar tablero maquina*/

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




    /* Inicializa la escena de juego con el tablero del jugador: construye ambos GridPane, configura estilos base, crea la IA,
     registra los clics sobre el tablero enemigo, extrae/pinta la flota del jugador y genera/ubica aleatoriamente la flota de
     la máquina. También deja listo el texto de estado/turno.  */
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
        if (btnVstaBarcosEnemigos != null) {
            btnVstaBarcosEnemigos.setDisable(false);
        }

        /*codigo para dar click en las celdas del enemigo*/
        for (int fila= 0; fila < TAM; fila++) {
            for (int col= 0; col < TAM; col++) {
                final int f= fila;
                final int c= col;
                celdasMaquina[fila][col].setOnMouseClicked(e -> {
                   /*deshabiliatar el boton de revelar tablero del oponente*/
                    if (btnVstaBarcosEnemigos != null) {
                    btnVstaBarcosEnemigos.setDisable(true);
                    }

                    manejarDisparoJugador(f, c);
                });
            }
        }
        /* Recorre todas las celdas del tablero del jugador para detectar barcos colocados y construir
        la lista flotaJugador sin duplicados (usa un Set). Esto sirve para saber qué barcos existen realmente
         en el tablero durante la partida.  */
        extraerFlotaJugadorDesdeTablero();
        pintarBarcosJugador();


        tableroMaquina= new Tablero(TAM, TAM);
        colocarFlotaMaquinaAleatoria();

        lblTurno.setText("Turno del jugador");
        lblEstado.setText("Haz clic en el tablero de la máquina para disparar.");



    }


    @FXML
    private void initialize() {
        btnVstaBarcosEnemigos.setOnMouseEntered(e->{SoundEffects.playHover();});
        String nombre= SesionJuego.getNombreJugador();
        lblTurno.setText("Turno de " + nombre);
        if (lblTableroJugador != null) {
            lblTableroJugador.setText("Tablero de " + nombre);
        } else {
            System.out.println("ERROR:");
        }


        MusicManager.playLoop(MusicTrack.BATALLA, 0.35);


        btnVolverMenu.setOnMouseEntered(e->{
            SoundEffects.playHover();
        });

        btnVolverMenu.setOnAction(e -> {

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
    }




    /* Limpia el estilo visual del grid del jugador y luego dibuja cada barco de la flota como un
     contenedor (StackPane) agregado al GridPane, respetando orientación y tamaño mediante spans.
      También guarda el contenedor en el objeto Barco para poder retirarlo después. */
    private void pintarBarcosJugador() {


        for (int fila = 0; fila < TAM; fila++) {
            for (int col = 0; col < TAM; col++) {
                Pane p = celdasJugador[fila][col];
                p.setStyle("-fx-background-color: #111827; -fx-border-color: #1f2933; -fx-border-width: 1;");
            }
        }

        for (Barco barco : flotaJugador) {

            int filaInicio = barco.getFila();
            int colInicio  = barco.getColumna();

            Orientacion orient = barco.getOrientacion();


            StackPane contenedor = crearContenedorBarco(barco, orient);


            barco.setContenedorEnGrid(contenedor);


            gridJugador.add(contenedor, colInicio, filaInicio);


            if (orient == Orientacion.HORIZONTAL) {
                GridPane.setColumnSpan(contenedor, barco.getTamanio());
                GridPane.setRowSpan(contenedor, 1);
            } else {
                GridPane.setRowSpan(contenedor, barco.getTamanio());
                GridPane.setColumnSpan(contenedor, 1);
            }


            GridPane.setHalignment(contenedor, HPos.CENTER);
            GridPane.setValignment(contenedor, VPos.CENTER);
        }
    }






    /* Reinicia el tablero y la flota de la máquina y luego coloca sus barcos con cantidades/tamaños
     definidos (fragata, destructor, submarino, portaaviones) usando ubicaciones y orientaciones aleatorias válidas.  */

    private void colocarFlotaMaquinaAleatoria() {
        flotaMaquina.clear();
        tableroMaquina = new Tablero(TAM, TAM);

        colocarBarcosDeTipoMaquina("Fragata", 1, 4);
        colocarBarcosDeTipoMaquina("Destructor", 2, 3);
        colocarBarcosDeTipoMaquina("Submarino", 3, 2);
        colocarBarcosDeTipoMaquina("Portaaviones", 4, 1);
    }

    /* Coloca repetidamente barcos de un tipo específico en el tablero de la máquina
    hasta completar la cantidad requerida. Para cada barco, elige fila/columna/orientación aleatoria
     y valida la ubicación con ubicarBarco; si es válida lo agrega a la flota.  */

    private void colocarBarcosDeTipoMaquina(String tipo, int tamaño, int cantidad) {
        int colocados = 0;

        while (colocados < cantidad) {
            Barco barco = new Barco(tipo, tamaño);

            int fila = random.nextInt(TAM);
            int col  = random.nextInt(TAM);


            Orientacion orientacion = random.nextBoolean()
                    ? Orientacion.HORIZONTAL
                    : Orientacion.VERTICAL;

            Coordenada inicio = new Coordenada(fila, col);

            boolean ok = tableroMaquina.ubicarBarco(barco, inicio, orientacion);

            if (ok) {

                barco.setOrientacion(orientacion);

                flotaMaquina.add(barco);
                colocados++;

            }
        }
    }






    /* Gestiona el disparo del jugador sobre el tablero enemigo: valida turno/fin del juego,
     arranca cronómetro en el primer disparo, evita disparos repetidos, aplica el disparo al modelo
      (recibirDisparo), actualiza UI y sonidos según el resultado y verifica victoria. Si fue agua,
       pasa turno a la máquina con una pausa simulando “pensar”. CO */
    private void manejarDisparoJugador(int fila, int col) {

        if (!turnoJugador || juegoTerminado) {
            return;
        }

        if (!JuegoIniciado) {
            JuegoIniciado = true;
            iniciarCronometro(); /*Inicia el juego y el tiempo*/
            /*Deshabilita el botón de revelar tablero oponente después del primer disparo*/

            if (btnVstaBarcosEnemigos != null) {
                btnVstaBarcosEnemigos.setDisable(true);
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



        if (resultado== ResultadoDisparo.AGUA) {
            turnoJugador= false;
            lblTurno.setText("Turno de la máquina");
            simularPensandoMaquina();
        } else {
            turnoJugador= true;
            lblTurno.setText("Tu turno (continúas)");
        }

    }
    /* Actualiza la representación visual de una celda en el tablero de la máquina después de un disparo:
     pinta agua, o dispara rutinas para marcar “tocado” o “hundido” (incluyendo animaciones/halo de seguridad). */

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

                marcarBarcoHundido(tableroMaquina, celdasMaquina, fila, col);
            }
        }
    }


    /* Desactiva la interacción del usuario sobre el tablero enemigo removiendo los handlers de clic
    en todas las celdas. Se usa cuando el juego termina para impedir más disparos. */
    private void deshabilitarClicksMaquina() {
        for (int f= 0; f < TAM; f++) {
            for (int c= 0; c < TAM; c++) {
                celdasMaquina[f][c].setOnMouseClicked(null);
            }
        }
    }



    /* Ejecuta el turno de la IA: elige una coordenada con iaMaquina, dispara al tablero del jugador,
    informa el resultado a la IA, reproduce sonidos con retraso para simular impacto, actualiza el tablero
    del jugador y verifica derrota. Si el resultado fue agua devuelve turno al jugador; si no, la máquina
    continúa tras una pausa.*/

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
                juegoTerminado = true;
                detenerCronometro(); /* Detener el tiempo, ya que el juego terminó*/
                lblTurno.setText("Juego terminado");
                lblEstado.setText("¡DERROTA! La máquina ha hundido toda tu flota.");
                deshabilitarClicksMaquina();

                /* Lógica para ir a la pantalla de Derrota:*/
                NavegadorEscenas.irAVista(gridJugador, "/com/example/batalla_naval/VistaDerrota.fxml");

                return;
            }



            if (resultadoFinal== ResultadoDisparo.AGUA) {
                turnoJugador= true;
                lblTurno.setText("Turno del jugador");
            } else {

                turnoJugador= false;
                lblTurno.setText("Turno de la máquina");
                simularPensandoMaquina();
            }


        });



        turnoJugador= true;
        lblTurno.setText("Turno del jugador");
    }
    /* Genera una espera corta (PauseTransition) para simular que la máquina
    “piensa” antes de disparar. Tras el delay, si el juego sigue activo, ejecuta turnoMaquina(). */
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
    /* Crea un StackPane con tamaño exacto para representar gráficamente un barco según su orientación,
     resetea transforms/posiciones del nodo visual del barco y lo rota 90° si es vertical. Devuelve el
      contenedor listo para insertarse en el GridPane. */
    private StackPane crearContenedorBarco(Barco barco, Orientacion ori) {

        int t = barco.getTamanio();

        double w = (ori == Orientacion.HORIZONTAL) ? (t * CELL) : CELL;
        double h = (ori == Orientacion.HORIZONTAL) ? CELL : (t * CELL);

        StackPane box = new StackPane();
        box.setPrefSize(w, h);
        box.setMinSize(w, h);
        box.setMaxSize(w, h);
        box.setAlignment(Pos.CENTER);

        var node = barco.getForma();
        if (node == null) return box;

        node.setOnMouseClicked(null);

        node.getTransforms().clear();
        node.setRotate(0);
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setLayoutX(0);
        node.setLayoutY(0);

        if (ori == Orientacion.VERTICAL) {
            Bounds b = node.getBoundsInLocal();
            double pivotX = b.getMinX() + b.getWidth() / 2.0;
            double pivotY = b.getMinY() + b.getHeight() / 2.0;
            node.getTransforms().add(new Rotate(90, pivotX, pivotY));
        }

        box.getChildren().add(node);
        return box;
    }




    /* Actualiza la UI del tablero del jugador en la celda impactada por la máquina: pinta agua o activa rutinas
     de “tocado/hundido” para mostrar animaciones y marcar halo alrededor si corresponde. */
    private void actualizarCeldaJugador(int fila, int col, ResultadoDisparo resultado) {
        Pane p= celdasJugador[fila][col];

        switch (resultado) {
            case AGUA -> {
                    p.setStyle("-fx-background-color: #020617; -fx-border-color: #1f2933; -fx-border-width: 1;");
            }
            case TOCADO -> {

                marcarBarcoTocado(tableroJugador, celdasJugador, fila, col);

            }
            case HUNDIDO -> {

                marcarBarcoHundido(tableroJugador, celdasJugador, fila, col);
            }
        }
    }

    /* Reemplaza el contenido visual de una celda por un GIF de explosión/hundimiento, ajustándolo al tamaño
    de la celda. Si falla la carga del recurso, aplica un estilo de respaldo (color rojo) y registra el error. */
    private void mostrarAnimacionHundimiento(Pane celda) {
        try {
            celda.getChildren().clear();


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
    /* Muestra una animación (GIF de fuego) en la celda donde hubo impacto “tocado”,
    ajustando el GIF al tamaño del Pane. Si falla, aplica un estilo alterno (naranja) y registra el error. */
    private void mostrarAnimacionTocado(Pane celda) {
        try {


            String rutaGif = "/com/example/batalla_naval/images/fuego4.gif";
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
    /* Detiene el cronómetro y navega a VistaInicio.fxml cargando el FXML y
    reemplazando la Scene del Stage actual. Si ocurre un error de carga, informa en lblEstado. */
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

    /* A partir de una celda impactada, obtiene el barco asociado y marca todas sus celdas como hundidas (animación).
     Si el barco es del jugador, además elimina su contenedor del GridPane para que no quede dibujado encima. Finalmente
     aplica el “halo seguro” alrededor del barco hundido. */
    private void marcarBarcoHundido(Tablero tablero, Pane[][] celdas, int fila, int col) {
        Celda celdaImpacto= tablero.getCelda(fila, col);
        if (!celdaImpacto.tieneBarco()) return;

        Barco barcoHundido= celdaImpacto.getBarco();

        if (tablero == tableroJugador) {
            Node cont = barcoHundido.getContenedorEnGrid();
            if (cont != null && cont.getParent() instanceof GridPane gridPadre) {
                gridPadre.getChildren().remove(cont);
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
    /* Marca visualmente una celda como “tocado” mostrando la animación de fuego en esa posición. */
    private void marcarBarcoTocado(Tablero tablero, Pane[][] celdas, int fila, int col) {
        Pane p = celdas[fila][col];



        mostrarAnimacionTocado(p);
    }
    /* Recorre las celdas del barco hundido y pinta un halo (celdas vecinas 8-direcciones) como “seguras” cuando no contienen barco,
     evitando sobrescribir estilos de celdas ya marcadas como impacto. */
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


    /* Cambia o activa música adicional de “batalla/problemas” una
     sola vez (flag musicaBatallaIniciada) para intensificar el ambiente cuando la máquina hunde un barco. */
    private void activarMusicaBatalla() {
        if (!musicaBatallaIniciada) {
            musicaBatallaIniciada= true;

            MusicManager.playLoop(MusicTrack.PROBLEMAS, 0.35);
        }
    }

    /* Reinicia el contador de tiempo a cero y arranca un Timeline que incrementa los segundos cada 1s,
    actualizando lblCronometro en formato mm:ss. */
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


    /* Simula el tiempo de viaje del proyectil: reproduce el sonido de lanzamiento y, tras una pausa fija (1.5s),
     ejecuta la acción recibida (normalmente reproducir explosión/impacto). */
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
 y con el ControladorVistaTableroMaquina
    Abre una ventana modal (Stage) con VistaTableroMaquina.fxml para “reconocimiento” del enemigo.
     Carga el FXML, entrega al controlador el tablero y la flota de la máquina, configura owner/modality y
      muestra con showAndWait() para bloquear la ventana principal mientras esté abierta. CO */
    public void onVstaBarcosEnemigos(ActionEvent actionEvent) {
        SoundEffects.playClick();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/batalla_naval/VistaTableroMaquina.fxml")
            );
            Parent root = loader.load();

            ControladorVistaTableroMaquina ctrl = loader.getController();
            ctrl.cargarDatosYMostrar(this.tableroMaquina, this.flotaMaquina);

            Stage owner = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            Stage stage = new Stage();
            stage.initOwner(owner);
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Reconocimiento del Enemigo");

            stage.setScene(new Scene(root, 1080, 720));
            stage.setResizable(false);
            stage.centerOnScreen();

            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            lblEstado.setText("Error al cargar la vista del tablero de la máquina.");
        }
    }



}

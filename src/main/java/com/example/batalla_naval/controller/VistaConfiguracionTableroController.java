package com.example.batalla_naval.controller;

import com.example.batalla_naval.model.Barco;
import com.example.batalla_naval.model.Tablero;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import com.example.batalla_naval.util.SoundEffects;
import javafx.util.Duration;
import com.example.batalla_naval.model.Coordenada;
import com.example.batalla_naval.model.Orientacion;




public class VistaConfiguracionTableroController {
    public Button btnIniciarBatalla;
    public Label informationLabel;
    public Label fragataCount;
    private static final int CELL = 45;

    // Matriz de panes que representan cada celda del GridPane
    private Pane[][] celdasGraficas = new Pane[10][10];

    // Lista de celdas que están actualmente en "preview"
    private final java.util.List<Pane> celdasPreview = new java.util.ArrayList<>();



    @FXML private Pane paneFragatas;
    @FXML private Pane paneDestructores;
    @FXML private Pane paneSubmarinos;
    @FXML private Pane panePortaaviones;
    private Tablero tableroJugador = new Tablero(10, 10);

    @FXML private GridPane tableroJugadorGrid;


    @FXML
    /*metodo que se ejecuta automáticamente al cargar el controlador*/
    private void initialize() {
        btnIniciarBatalla.setOnMouseEntered(e->{
            SoundEffects.playHover();
        });
        btnIniciarBatalla.setOnAction(e->{
            SoundEffects.playClick();
        });
        tableroJugadorGrid.setPrefSize(CELL * 10, CELL * 10);
        tableroJugadorGrid.setMinSize(CELL * 10, CELL * 10);
        tableroJugadorGrid.setMaxSize(CELL * 10, CELL * 10);

        // fijar columnas y filas a tamaño CELL
        for (int i = 0; i < 10; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setMinWidth(CELL);
            cc.setPrefWidth(CELL);
            cc.setMaxWidth(CELL);
            tableroJugadorGrid.getColumnConstraints().add(cc);

            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(CELL);
            rc.setPrefHeight(CELL);
            rc.setMaxHeight(CELL);
            tableroJugadorGrid.getRowConstraints().add(rc);
        }


        System.out.println("Inicializando tablero vista 2");


        /*crear navios*/
        crearBarcosPanelIzquierdo();

        configurarEventosArrastre();

        /*codigo que conecta con el VistaConfiguracionTablero.fxml y
        VistaConfiguracionTableroEstilos.css para la creacion e
        independencia de las celdas dentro del gridpane*/
        for (int fila=0; fila<10; fila++) {
            for (int columna=0; columna<10; columna++) { /*recorre filas y columnas*/

                Pane celda = new Pane();
                celda.getStyleClass().add("celda");

                celda.setPrefSize(CELL, CELL);
                celda.setMinSize(CELL, CELL);
                celda.setMaxSize(CELL, CELL);
                /*cada celda 45 porque el tablero esta fijado en 450*/

                tableroJugadorGrid.add(celda, columna, fila);
                celdasGraficas[fila][columna] = celda;
                /*la organizacion del tablero de juego:
                * tablero -> es un grid pane
                * celda -> es un pane (espacio en blanco donde se puede meter elementos)*/
            }
        }
    }

    /*metodo de creacion de navios en el panel izquierdo*/



    /*logica para el movimiento de imagenes en la vista 2 de configuracion del tablero
     * se conecta con VistaConfiguracionTablero.fxml*/
    private void crearBarcosPanelIzquierdo() {

        Barco fragata = new Barco("Fragata", 1);
        //paneFragatas.getChildren().clear();
        paneFragatas.getChildren().add(fragata.getForma());
        paneFragatas.setUserData(fragata); /*guarda el barco en el pane para poder moverlo*/
        habilitarDrag(paneFragatas);

        Barco destructor = new Barco("Destructor", 2);
        //paneDestructores.getChildren().clear();
        paneDestructores.getChildren().add(destructor.getForma());
        paneDestructores.setUserData(destructor);
        habilitarDrag(paneDestructores);

        Barco submarino = new Barco("Submarino", 3);
        //paneSubmarinos.getChildren().clear();
        paneSubmarinos.getChildren().add(submarino.getForma());
        paneSubmarinos.setUserData(submarino);
        habilitarDrag(paneSubmarinos);

        Barco portaaviones = new Barco("Portaaviones", 4);
        //panePortaaviones.getChildren().clear();
        panePortaaviones.getChildren().add(portaaviones.getForma());
        panePortaaviones.setUserData(portaaviones);
        habilitarDrag(panePortaaviones);
    }

    /*____________________________________METODOS PARA TOMAR, ARRASTRAR Y SOLTAR EN EL TABLERO___________________________________*/
    /*metodo que permite tomar/arrastar (drag) la imagen del panel izquiero al tabler/gridpane*/

    private void habilitarDrag(Pane paneNavio) {


        paneNavio.setOnMouseEntered(e -> {
            SoundEffects.playHover();

            ScaleTransition st =new ScaleTransition(Duration.millis(150), paneNavio);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        paneNavio.setOnMouseExited(e -> {
            ScaleTransition st= new ScaleTransition(Duration.millis(150), paneNavio);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        paneNavio.setOnMousePressed(e -> {
            SoundEffects.playClick();
            ScaleTransition st= new ScaleTransition(Duration.millis(150), paneNavio);
            st.setToX(1.2);
            st.setToY(1.2);
            st.play();
        });
        paneNavio.setOnMouseReleased(e -> {
            SoundEffects.playNegativeClick();
            ScaleTransition st= new ScaleTransition(Duration.millis(150), paneNavio);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

        });




        paneNavio.setOnDragDetected(event -> {


            // Recuperar el Navio almacenado en el pane
            Barco barco = (Barco) paneNavio.getUserData();
            if (barco == null) {
                System.out.println("⚠ ERROR: paneNavio no tiene un Navio asignado en setUserData()");
                return;
            }

            // Crear dragboard
            Dragboard dragboard = paneNavio.startDragAndDrop(TransferMode.MOVE);

            // Contenido que se enviará
            ClipboardContent content = new ClipboardContent();

            // Enviar tipo de barco
            content.putString(barco.getTipo());

            // Tomar snapshot de la figura real del navio
            WritableImage snapshot = barco.getForma().snapshot(null, null);
            content.putImage(snapshot);

            // Guardarlo en el dragboard
            dragboard.setContent(content);

            event.consume();
        });


    }


    /*metodo que permite ubicar la imegen en el player board (gridpane)
    * eventos que usa el metodo:
    * onDragOver → cuando el barco está siendo arrastrado por encima del tablero
    * onDragDropped → cuando el barco se suelta sobre el tablero*/

    private void configurarEventosArrastre() {

        /*--------------------------- DRAG OVER --------------------------------*/
        tableroJugadorGrid.setOnDragOver(event -> {

            limpiarPreview();  // limpiar cualquier resaltado anterior

            Dragboard dragboard = event.getDragboard();

            if (event.getGestureSource() != tableroJugadorGrid && dragboard.hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);

                // --- NUEVO: calcular preview ---

                String tipo = dragboard.getString();
                if (tipo != null) {

                    int tamanio = Barco.tamañoPorTipo(tipo);

                    int columna = (int) (event.getX() / CELL);
                    int fila    = (int) (event.getY() / CELL);

                    // Orientación (por ahora manejamos horizontal)
                    Orientacion orientacion = Orientacion.HORIZONTAL;

                    // Revisar si sería una posición válida en el modelo
                    Barco barcoTemporal = new Barco(tipo, tamanio);
                    Coordenada inicio = new Coordenada(fila, columna);
                    boolean valido = tableroJugador.puedeUbicarBarco(barcoTemporal, inicio, orientacion);

                    // Pintar preview
                    marcarPreview(fila, columna, tamanio, orientacion, valido);
                }
            } else {
                // si no hay imagen, quitar preview
                limpiarPreview();
            }

            event.consume();
        });


        /*--------------------------- DRAG DROPPED -----------------------------*/
        tableroJugadorGrid.setOnDragDropped(event -> {

            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasImage()) {

                String tipo = dragboard.getString();
                int tamaño = Barco.tamañoPorTipo(tipo);

                // Crear barco real
                Barco barco = new Barco(tipo, tamaño);

                // Calcular fila/columna donde se soltó
                int columna = (int) (event.getX() / 45);
                int fila    = (int) (event.getY() / 45);

                // Validar que la casilla está dentro del tablero
                if (fila < 0 || fila >= tableroJugador.getFilas()
                        || columna < 0 || columna >= tableroJugador.getColumnas()) {

                    informationLabel.setText("⚠ No se puede colocar fuera del tablero.");
                    event.setDropCompleted(false);
                    event.consume();
                    return;
                }


                // Coordenada inicial en el tablero
                // Coordenada inicial en el tablero
                Coordenada inicio = new Coordenada(fila, columna);

// Por ahora, tratamos siempre el barco como HORIZONTAL,
// porque su forma gráfica está dibujada a lo largo de las columnas.
                Orientacion orientacion = Orientacion.HORIZONTAL;

// Intentar colocarlo en el modelo
                boolean colocado = tableroJugador.ubicarBarco(barco, inicio, orientacion);



                if (colocado) {
                    SoundEffects.playPosicionarBarco();
                    informationLabel.setText("✔ Barco colocado en (" + fila + ", " + columna + ")");

                    barco.getForma().setTranslateX(0);
                    barco.getForma().setTranslateY(0);
                    tableroJugadorGrid.add(barco.getForma(), columna, fila);

                    GridPane.setColumnSpan(barco.getForma(), barco.getTamanio());

                    event.setDropCompleted(true);
                } else {
                    informationLabel.setText("⚠ No se puede colocar el barco ahí.");
                    event.setDropCompleted(false);
                }



            } else {
                event.setDropCompleted(false);
            }

            event.consume();
        });
        tableroJugadorGrid.setOnDragExited(event -> limpiarPreview());


    }


    private void limpiarPreview() {
        for (Pane p : celdasPreview) {
            p.getStyleClass().remove("celda-preview");
            p.getStyleClass().remove("celda-preview-invalid");
        }
        celdasPreview.clear();
    }

    private void marcarPreview(int filaInicio,
                               int columnaInicio,
                               int tamanio,
                               Orientacion orientacion,
                               boolean valido) {

        limpiarPreview();

        for (int i = 0; i < tamanio; i++) {
            int f = (orientacion == Orientacion.HORIZONTAL) ? filaInicio : filaInicio + i;
            int c = (orientacion == Orientacion.HORIZONTAL) ? columnaInicio + i : columnaInicio;

            if (f < 0 || f >= tableroJugador.getFilas()
                    || c < 0 || c >= tableroJugador.getColumnas()) {
                // se sale del tablero, no se marca
                continue;
            }

            Pane celda = celdasGraficas[f][c];
            if (celda == null) continue;

            String styleClass = valido ? "celda-preview" : "celda-preview-invalid";
            if (!celda.getStyleClass().contains(styleClass)) {
                celda.getStyleClass().add(styleClass);
            }
            celdasPreview.add(celda);
        }
    }


}


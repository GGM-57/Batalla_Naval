package com.example.batalla_naval.controller;

import com.example.batalla_naval.model.Navio;
import com.example.batalla_naval.model.Tablero;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import java.net.URL;
import java.util.ResourceBundle;
import com.example.batalla_naval.util.SoundEffects;
import javafx.util.Duration;


public class VistaConfiguracionTableroController {
    public Button btnIniciarBatalla;
    public Label informationLabel;
    public Label fragataCount;
    private static final int CELL = 45;
    private boolean dragging = false;

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

        /*llamar el metodo que permite movimiento de navios*/
//        habilitarDrag(paneFragatas); /*metodo para arrastrar*/
//        habilitarDrag(paneDestructores);
//        habilitarDrag(paneSubmarinos);
//        habilitarDrag(panePortaaviones);
        /*metodo para manejar lo arrastrado y soltado en el tablero*/
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

        Navio fragata = new Navio("Fragata", 1);
        //paneFragatas.getChildren().clear();
        paneFragatas.getChildren().add(fragata.getForma());
        paneFragatas.setUserData(fragata); /*guarda el barco en el pane para poder moverlo*/
        habilitarDrag(paneFragatas);

        Navio destructor = new Navio("Destructor", 2);
        //paneDestructores.getChildren().clear();
        paneDestructores.getChildren().add(destructor.getForma());
        paneDestructores.setUserData(destructor);
        habilitarDrag(paneDestructores);

        Navio submarino = new Navio("Submarino", 3);
        //paneSubmarinos.getChildren().clear();
        paneSubmarinos.getChildren().add(submarino.getForma());
        paneSubmarinos.setUserData(submarino);
        habilitarDrag(paneSubmarinos);

        Navio portaaviones = new Navio("Portaaviones", 4);
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
            Navio navio = (Navio) paneNavio.getUserData();
            if (navio == null) {
                System.out.println("⚠ ERROR: paneNavio no tiene un Navio asignado en setUserData()");
                return;
            }

            // Crear dragboard
            Dragboard dragboard = paneNavio.startDragAndDrop(TransferMode.MOVE);

            // Contenido que se enviará
            ClipboardContent content = new ClipboardContent();

            // Enviar tipo de barco
            content.putString(navio.getTipo());

            // Tomar snapshot de la figura real del navio
            WritableImage snapshot = navio.getForma().snapshot(null, null);
            content.putImage(snapshot);

            // Guardarlo en el dragboard
            dragboard.setContent(content);

            event.consume();
        });


//
//        paneNavio.setOnDragDetected(event -> { /*cuando se arrastra la imagen se dectata el evento*/
//            //System.out.println("se esta haciendo drag");
//
//
//            //Navio navio = new Navio(/*"Fragata", 1*/);
//
//            Dragboard contenedorArrastre =paneNavio.startDragAndDrop(TransferMode.MOVE);
//            /*Dragboard es un contenedor temporal donde se aloja lo que se está arrastrando*/
//            /*obtener el barco Group*/
//            Navio navio = (Navio) paneNavio.getUserData(); /*recupera el barco original*/
//
//            ClipboardContent content = new ClipboardContent();
//            content.putString(navio.getTipo()); /*enviar el tipo de barco*/
//
//            WritableImage snapshot = navio.getForma().snapshot(null, null);
//            content.putImage(snapshot);
//
//            contenedorArrastre.setContent(content);
//
//            event.consume();
//
//
//
//
//
//
//            //System.out.println("drag- se metió pane al dragboard");
//            if (!paneNavio.getChildren().isEmpty()) {
//                Group barco = (Group) paneNavio.getChildren().get(0);
//
//                // Crear snapshot del barco
//                WritableImage snapshot = barco.snapshot(null, null);
//
//
//                ClipboardContent contenido = new ClipboardContent();
//                /*clipboardcontent : cajita que se pone dentro del dragboard para guardar la info a mover*/
//                contenido.putImage(snapshot);/*pone la imagen(contenido) en el clipboard*/
//                //System.out.println("se puso el contenido en el clipboard");
//                contenedorArrastre.setContent(contenido);/*guarda el contenido en el drag*/
//            }
//            event.consume();
////            //System.out.println("terminó evento de drag");
//        });
    }


    /*metodo que permite ubicar la imegen en el player board (gridpane)
    * eventos que usa el metodo:
    * onDragOver → cuando el barco está siendo arrastrado por encima del tablero
    * onDragDropped → cuando el barco se suelta sobre el tablero*/

    private void configurarEventosArrastre() {

        /*--------------------------- DRAG OVER --------------------------------*/
        tableroJugadorGrid.setOnDragOver(event -> {

            Dragboard dragboard = event.getDragboard();

            // Solo aceptamos si viene una imagen (snapshot del barco)
            if (event.getGestureSource() != tableroJugadorGrid &&
                    dragboard.hasImage()) {

                event.acceptTransferModes(TransferMode.MOVE);
            }

            event.consume();
        });

        /*--------------------------- DRAG DROPPED -----------------------------*/
        tableroJugadorGrid.setOnDragDropped(event -> {

            Dragboard dragboard = event.getDragboard();

            if (dragboard.hasImage()) {

                String tipo = dragboard.getString();
                int tamaño = Navio.tamañoPorTipo(tipo);

                // Crear navio real
                Navio navio = new Navio(tipo, tamaño);

                // Calcular fila/columna donde se soltó
                int columna=(int) (event.getX() /45);
                int fila=(int) (event.getY() /45);

                // Intentar colocarlo en el modelo
                boolean colocado = tableroJugador.colocarBarco(navio, fila, columna);

                if (colocado) {
                    SoundEffects.playPositiveClick();
                    System.out.println("Barco colocado en fila " + fila + ", columna " + columna);
                    informationLabel.setText("✔ Barco colocado en (" + fila + ", " + columna + ")");

                    // Agregar visualmente al grid
                    navio.getForma().setTranslateX(0);
                    navio.getForma().setTranslateY(0);

                    navio.getForma().setTranslateX(0);
                    navio.getForma().setTranslateY(0);
                    tableroJugadorGrid.add(navio.getForma(), columna, fila);
                    GridPane.setColumnSpan(navio.getForma(), tamaño);



                    event.setDropCompleted(true);

                } else {
                    System.out.println("No se puede colocar en (" + fila + ", " + columna + ")");
                    informationLabel.setText("⚠ No se puede colocar el barco ahí.");
                    event.setDropCompleted(false);
                }

            } else {
                event.setDropCompleted(false);
            }

            event.consume();
        });
//        System.out.println("se entró a cofigurarEventosArrastre");
//
//        tableroJugadorGrid.setOnDragOver(event -> {
//            /*detecta si algo está siendo arrastrado sobre el tablero
//            * que no sea el mismo tablero
//            * que sea una imagen (hay que cambiar esto por figuras)
//            * ahí sí permite el movimiento*/
//
//
//            //System.out.println("se esta arrastrando algo al grid");
//
//            if (event.getGestureSource() != tableroJugadorGrid && /*asegura que lo que arrastra no es el propio*/
//                    event.getDragboard().hasImage()) { /*comprueba que el Dragboard contiene una imagen.*/
//
//                event.acceptTransferModes(TransferMode.MOVE);
//            }
//            event.consume();
//        });
//
//
//        tableroJugadorGrid.setOnDragDropped(event -> {
//            /*handler que se dispara si el usuario soltó el mouse sobre el tableroJugadorGrid,
//            crea un nuevo imageView con la imagen del barco arrastrado*/
//            Dragboard contenedorArrastre = event.getDragboard(); /*obtiene el contenido que traía el drag*/
//
//            if (contenedorArrastre.hasImage()) { /*Comprueba que efectivamente hay una imagen en el Dragboard*/
//
//                String tipo = contenedorArrastre.getString();
//                int tamaño = Navio.tamañoPorTipo(tipo);
//                /*crea un navio con el tipo y tamaño respectivo*/
//                Navio navio = new Navio (tipo, tamaño);
//
//
//
//                /* calcula aproximada la casilla donde cayó el barco*/
//                int columna = (int) (event.getX() / (tableroJugadorGrid.getWidth() / 10));
//                int fila = (int) (event.getY() / (tableroJugadorGrid.getHeight() / 10));
//
//
//
//                //System.out.println("navio instanciado en setOnDragDropped de configurarEventosArrastre");
//
//                /*intenta colocarlo en el modelo*/
//                boolean colocado = tableroJugador.colocarBarco(navio, fila, columna);
//
//                if (colocado) {
//                    informationLabel.setText("Barco colocado correctamente en fila "+fila+ " ,columna " + columna);
//                    System.out.println("Barco colocado correctamente en fila "+fila+ " ,columna " + columna);
//
//                    /*agregar la forma del navio al tablero*/
//                    tableroJugadorGrid.add(navio.getForma(), columna, fila);
//                    /*ImageView nuevoNavio = new ImageView(contenedorArrastre.getImage());*/
//
//                    /*nuevoNavio.setFitWidth(45);
//                    nuevoNavio.setPreserveRatio(true);
//
//                    tableroJugadorGrid.add(nuevoNavio, columna, fila);*/
//
//                    event.setDropCompleted(true);
//
//                } else {
//                    System.out.println("No se puede colocar el barco en " +fila+ "," +columna);
//                    informationLabel.setText("⚠ No se puede colocar el barco allí.");
//                    event.setDropCompleted(false);
//                }
//
//            } else {
//                event.setDropCompleted(false);
//            }
//
//            event.consume();
//        });

    }

}


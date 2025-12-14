package com.example.batalla_naval.controller;
import com.example.batalla_naval.util.TableroUIFactory;
import com.example.batalla_naval.model.Barco;
import com.example.batalla_naval.model.Tablero;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import com.example.batalla_naval.util.SoundEffects;
import com.example.batalla_naval.util.MusicManager;
import com.example.batalla_naval.model.SesionJuego;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.example.batalla_naval.model.Coordenada;
import com.example.batalla_naval.model.Orientacion;
import javafx.event.ActionEvent;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
import javafx.scene.transform.Rotate;
import javafx.geometry.Bounds;


import java.io.IOException;

public class VistaConfiguracionTableroController {
    public Button btnIniciarBatalla;
    public Label informationLabel;
    @FXML private Label lblTituloTablero;


    @FXML private Label fragataCount;
    @FXML private Label destructorCount;
    @FXML private Label submarinoCount;
    @FXML private Label portaavionesCount;

    private static final int CELL = 45;

    /*límites y contadores de barcos*/
    private static final int MAX_FRAGATAS = 4;
    private static final int MAX_DESTRUCTORES = 3;
    private static final int MAX_SUBMARINOS = 2;
    private static final int MAX_PORTAAVIONES = 1;
    private Orientacion orientacionActual = Orientacion.HORIZONTAL;


    private int fragatasColocadas = 0;
    private int destructoresColocados = 0;
    private int submarinosColocados = 0;
    private int portaavionesColocados = 0;


    private Pane[][] celdasGraficas;


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
        String nombre = SesionJuego.getNombreJugador();
        lblTituloTablero.setText("Tablero de " + nombre);
        btnIniciarBatalla.setOnMouseEntered(e->{
            SoundEffects.playHover();
        });
        btnIniciarBatalla.setOnAction(e -> {
            SoundEffects.playClick();
            MusicManager.stopMenuMusic();
            try {
                irALucha(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });


        fragataCount.setText("x" + MAX_FRAGATAS);
        destructorCount.setText("x" + MAX_DESTRUCTORES);
        submarinoCount.setText("x" + MAX_SUBMARINOS);
        portaavionesCount.setText("x" + MAX_PORTAAVIONES);

        btnIniciarBatalla.setDisable(true);






        /*crear navios*/
        crearBarcosPanelIzquierdo();

        configurarEventosArrastre();
        celdasGraficas = TableroUIFactory.construirTablero(tableroJugadorGrid, 10, CELL);
        System.out.println("Inicializando tablero vista 2");


    }

    /*metodo de creacion de navios en el panel izquierdo*/



    /*logica para el movimiento de imagenes en la vista 2 de configuracion del tablero
     * se conecta con VistaConfiguracionTablero.fxml*/
    private void crearBarcosPanelIzquierdo() {

        Barco fragata = new Barco("Fragata", 1);

        paneFragatas.getChildren().add(fragata.getForma());
        paneFragatas.setUserData(fragata); /*guarda el barco en el pane para poder moverlo*/
        habilitarDrag(paneFragatas);

        Barco destructor = new Barco("Destructor", 2);

        paneDestructores.getChildren().add(destructor.getForma());
        paneDestructores.setUserData(destructor);
        habilitarDrag(paneDestructores);

        Barco submarino = new Barco("Submarino", 3);

        paneSubmarinos.getChildren().add(submarino.getForma());
        paneSubmarinos.setUserData(submarino);
        habilitarDrag(paneSubmarinos);

        Barco portaaviones = new Barco("Portaaviones", 4);

        panePortaaviones.getChildren().add(portaaviones.getForma());
        panePortaaviones.setUserData(portaaviones);
        habilitarDrag(panePortaaviones);
    }

    /*____________________________________METODOS PARA TOMAR, ARRASTRAR Y SOLTAR EN EL TABLERO___________________________________*/
    /*metodo que permite tomar/arrastar (drag) la imagen del panel izquiero al tabler/gridpane*/

    private void habilitarDrag(Pane paneNavio) {

        Label orientLabel = new Label("H");
        orientLabel.setStyle(
                "-fx-background-color: rgba(0,0,0,0.7);" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 10px;" +
                        "-fx-padding: 2 4 2 4;" +
                        "-fx-background-radius: 4;"
        );


        orientLabel.setTranslateX(5);
        orientLabel.setTranslateY(5);

        paneNavio.getChildren().add(orientLabel);


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
        paneNavio.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                orientacionActual = (orientacionActual == Orientacion.HORIZONTAL)
                        ? Orientacion.VERTICAL
                        : Orientacion.HORIZONTAL;

                orientLabel.setText(
                        orientacionActual == Orientacion.HORIZONTAL ? "H" : "V"
                );

                informationLabel.setText(
                        "Orientación: " + orientacionActual + " (clic derecho para cambiar)"
                );
            }
        });



        Tooltip tip = new Tooltip("Clic derecho para rotar (H / V)");
        tip.setShowDelay(Duration.ZERO);
        tip.setHideDelay(Duration.seconds(0.1));
        tip.setShowDuration(Duration.seconds(4));
        Tooltip.install(paneNavio, tip);





        paneNavio.setOnDragDetected(event -> {



            Barco barco = (Barco) paneNavio.getUserData();
            if (barco==null) {
                System.out.println("⚠ ERROR: paneNavio no tiene un Navio asignado en setUserData()");
                return;
            }


            Dragboard dragboard = paneNavio.startDragAndDrop(TransferMode.MOVE);


            ClipboardContent content = new ClipboardContent();


            content.putString(barco.getTipo());


            WritableImage snapshot = barco.getForma().snapshot(null, null);
            content.putImage(snapshot);


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

            limpiarPreview();

            Dragboard dragboard = event.getDragboard();

            if (event.getGestureSource() != tableroJugadorGrid && dragboard.hasImage()) {
                event.acceptTransferModes(TransferMode.MOVE);



                String tipo = dragboard.getString();
                if (tipo != null) {

                    int tamanio = Barco.tamañoPorTipo(tipo);

                    int columna = (int) (event.getX()/CELL);
                    int fila    = (int) (event.getY()/CELL);


                    Orientacion orientacion = orientacionActual;


                    Barco barcoTemporal = new Barco(tipo, tamanio);
                    Coordenada inicio = new Coordenada(fila, columna);
                    boolean valido = tableroJugador.puedeUbicarBarco(barcoTemporal, inicio, orientacion);


                    marcarPreview(fila, columna, tamanio, orientacion, valido);
                }
            } else {

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


                switch (tipo) {
                    case "Fragata" -> {
                        if (fragatasColocadas >= MAX_FRAGATAS) {
                            informationLabel.setText("Ya colocaste todas las Fragatas.");
                            event.setDropCompleted(false);
                            event.consume();
                            return;
                        }
                    }
                    case "Destructor" -> {
                        if (destructoresColocados >= MAX_DESTRUCTORES) {
                            informationLabel.setText("Ya colocaste todos los Destructores.");
                            event.setDropCompleted(false);
                            event.consume();
                            return;
                        }
                    }
                    case "Submarino" -> {
                        if (submarinosColocados >= MAX_SUBMARINOS) {
                            informationLabel.setText("Ya colocaste todos los Submarinos.");
                            event.setDropCompleted(false);
                            event.consume();
                            return;
                        }
                    }
                    case "Portaaviones" -> {
                        if (portaavionesColocados >= MAX_PORTAAVIONES) {
                            informationLabel.setText("Ya colocaste el Portaaviones.");
                            event.setDropCompleted(false);
                            event.consume();
                            return;
                        }
                    }
                }


                Barco barco = new Barco(tipo, tamaño);


                int columna = (int) (event.getX()/CELL);
                int fila    = (int) (event.getY()/CELL);



                if (fila < 0 || fila >= tableroJugador.getFilas()
                        || columna < 0 || columna >= tableroJugador.getColumnas()) {

                    informationLabel.setText("⚠ No se puede colocar fuera del tablero.");
                    event.setDropCompleted(false);
                    event.consume();
                    return;
                }




                Coordenada inicio = new Coordenada(fila, columna);



                Orientacion orientacion = orientacionActual;



                boolean colocado = tableroJugador.ubicarBarco(barco, inicio, orientacion);



                if (colocado) {
                    SoundEffects.playPosicionarBarco();
                    informationLabel.setText("✔ Barco colocado en (" + fila + ", " + columna + ")");


                    StackPane contenedor = crearContenedorBarco(barco, orientacion);


                    tableroJugadorGrid.add(contenedor, columna, fila);


                    if (orientacion == Orientacion.HORIZONTAL) {
                        GridPane.setColumnSpan(contenedor, barco.getTamanio());
                        GridPane.setRowSpan(contenedor, 1);
                    } else {
                        GridPane.setRowSpan(contenedor, barco.getTamanio());
                        GridPane.setColumnSpan(contenedor, 1);
                    }



                    event.setDropCompleted(true);

                    /*actualizar contadores */
                    switch (tipo) {
                        case "Fragata" -> {
                            fragatasColocadas++;
                            fragataCount.setText("x" + (MAX_FRAGATAS - fragatasColocadas));
                        }
                        case "Destructor" -> {
                            destructoresColocados++;
                            destructorCount.setText("x" + (MAX_DESTRUCTORES - destructoresColocados));
                        }
                        case "Submarino" -> {
                            submarinosColocados++;
                            submarinoCount.setText("x" + (MAX_SUBMARINOS - submarinosColocados));
                        }
                        case "Portaaviones" -> {
                            portaavionesColocados++;
                            portaavionesCount.setText("x" + (MAX_PORTAAVIONES - portaavionesColocados));
                        }
                    }


                    int totalNecesario = MAX_FRAGATAS + MAX_DESTRUCTORES + MAX_SUBMARINOS + MAX_PORTAAVIONES;
                    int totalColocado = fragatasColocadas + destructoresColocados +
                            submarinosColocados + portaavionesColocados;

                    if (totalColocado==totalNecesario) {
                        btnIniciarBatalla.setDisable(false);
                        informationLabel.setText("Todos los barcos colocados. ¡Listo para luchar!");
                    }
                } else {
                    informationLabel.setText("No se puede colocar el barco ahí.");
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
            int f = (orientacion==Orientacion.HORIZONTAL) ? filaInicio : filaInicio + i;
            int c = (orientacion==Orientacion.HORIZONTAL) ? columnaInicio + i : columnaInicio;

            if (f < 0 || f >= tableroJugador.getFilas()
                    || c < 0 || c >= tableroJugador.getColumnas()) {

                continue;
            }

            Pane celda = celdasGraficas[f][c];
            if (celda==null) continue;

            String styleClass = valido ? "celda-preview" : "celda-preview-invalid";
            if (!celda.getStyleClass().contains(styleClass)) {
                celda.getStyleClass().add(styleClass);
            }
            celdasPreview.add(celda);
        }
    }
    private void irALucha(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource(
                "/com/example/batalla_naval/VistaBatalla.fxml"
        ));

        Parent root = loader.load();

        ControladorJuego controladorJuego = loader.getController();


        controladorJuego.initData(tableroJugador);


        Stage stage = (Stage) btnIniciarBatalla.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private StackPane crearContenedorBarco(Barco barco, Orientacion orientacion) {


        int t = barco.getTamanio();
        double w = (orientacion == Orientacion.HORIZONTAL) ? (t * CELL) : CELL;
        double h = (orientacion == Orientacion.HORIZONTAL) ? CELL : (t * CELL);

        StackPane box = new StackPane();
        box.setPrefSize(w, h);
        box.setMinSize(w, h);
        box.setMaxSize(w, h);
        box.setAlignment(Pos.CENTER);


        var node = barco.getForma();


        node.getTransforms().clear();
        node.setRotate(0);
        node.setTranslateX(0);
        node.setTranslateY(0);

        if (orientacion == Orientacion.VERTICAL) {
            Bounds b = node.getBoundsInLocal();
            double pivotX = b.getMinX() + b.getWidth() / 2.0;
            double pivotY = b.getMinY() + b.getHeight() / 2.0;
            node.getTransforms().add(new Rotate(90, pivotX, pivotY));
        }

        box.getChildren().add(node);
        return box;
    }



}


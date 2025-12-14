package com.example.batalla_naval.controller;

import com.example.batalla_naval.util.MusicManager;
import com.example.batalla_naval.util.MusicTrack;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node; // NECESARIO para los botones
import javafx.event.ActionEvent; // NECESARIO para los botones
import java.net.URL;
import java.util.ResourceBundle;

public class ControladorDerrota implements Initializable {

    // En el FXML, el contenedor principal es un VBox, que es un Node.
    // Necesitamos inyectar un elemento para que NavegadorEscenas sepa a qué ventana volver.
    // Usaremos el VBox principal para esto.

    @FXML private Node rootContainer; // Asume que el VBox principal del FXML tiene fx:id="rootContainer"

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Detenemos la música de batalla y ponemos la de derrota.
        MusicManager.stop(MusicTrack.BATALLA);
        MusicManager.stop(MusicTrack.PROBLEMAS); // Si es que estaba sonando

        // Usamos playLoop por si quieres que la música de derrota se repita mientras el usuario decide.
        // Si prefieres que suene una sola vez: MusicManager.playOnce(MusicTrack.DERROTA, 0.4);
        MusicManager.playLoop(MusicTrack.DERROTA, 0.4);
    }

    @FXML
    private void volverAIntentar(ActionEvent event) {
        MusicManager.stop(MusicTrack.DERROTA);
        // Volver a la pantalla de configuración del tablero.
        // Usamos el método irAVista que tienes en NavegadorEscenas.java
        NavegadorEscenas.irAVista((Node)event.getSource(), "/com/example/batalla_naval/VistaConfiguracionTablero.fxml");
    }

    @FXML
    private void irAlMenu(ActionEvent event) {
        MusicManager.stop(MusicTrack.DERROTA);
        // Volver al menú principal.
        NavegadorEscenas.irAVista((Node)event.getSource(), "/com/example/batalla_naval/VistaInicio.fxml");
    }
}
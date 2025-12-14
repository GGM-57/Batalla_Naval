package com.example.batalla_naval.controller;

import com.example.batalla_naval.util.MusicManager;
import com.example.batalla_naval.util.MusicTrack;
import com.example.batalla_naval.util.SoundEffects;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class ControladorDerrota implements Initializable {


    @FXML private Node rootContainer;
    @FXML private Button btnVolverIntentar;
    @FXML private Button btnMenuPrincipal;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        MusicManager.stop(MusicTrack.BATALLA);
        MusicManager.stop(MusicTrack.PROBLEMAS);
        MusicManager.playLoop(MusicTrack.DERROTA, 0.4);
        btnMenuPrincipal.setOnMouseEntered(e->{
            SoundEffects.playHover();
        });
        btnVolverIntentar.setOnMouseEntered(e->{
            SoundEffects.playHover();
        });
    }

    @FXML
    private void volverAIntentar(ActionEvent event) {
        MusicManager.stop(MusicTrack.DERROTA);
        MusicManager.playMenuMusic();
        SoundEffects.playClick();
        NavegadorEscenas.irAVista((Node)event.getSource(), "/com/example/batalla_naval/VistaConfiguracionTablero.fxml");
    }

    @FXML
    private void irAlMenu(ActionEvent event) {
        MusicManager.stop(MusicTrack.DERROTA);
        SoundEffects.playNegativeClick();
        NavegadorEscenas.irAVista((Node)event.getSource(), "/com/example/batalla_naval/VistaInicio.fxml");
    }
}
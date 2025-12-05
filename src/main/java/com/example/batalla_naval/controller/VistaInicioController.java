package com.example.batalla_naval.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class VistaInicioController {
    @FXML
    private Button jugarButton;

    @FXML
    private void initialize() {
        //agregar logica
    }

    @FXML
    private void onJugarClick(ActionEvent event) throws IOException {
        System.out.println("Jugando");
        cambiarVentana(event, "/com/example/batalla_naval/VistaConfiguracionTablero.fxml");
    }

    private void cambiarVentana(ActionEvent event, String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

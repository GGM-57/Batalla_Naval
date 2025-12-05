package com.example.batalla_naval.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;

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

    /* logica del boton de instrucciones
    * */
    @FXML
    private void onInstruccionesClick(ActionEvent event){
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Instrucciones del juego");
        alert.setHeaderText(null);
        alert.setContentText("instrucciones");
        ButtonType okButton=new ButtonType("más que claro!!", ButtonBar.ButtonData.OK_DONE); /* boton de aceptar*/
        alert.getButtonTypes().setAll(okButton);
        alert.showAndWait();
    }


    private void cambiarVentana(ActionEvent event, String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

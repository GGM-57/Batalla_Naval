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
        alert.setContentText(
                """
                        INSTRUCCIONES, COMANDOS Y PASOS PARA JUGAR:
                       \s
                        1.El juego se juega por turnos
                       \s
                        2. Antes de iniciar, se ponen los barcos en el tablero
                       \s
                        3. Click izquierdo = colocar barco
                       \s
                        4. Click derecho = girar barco
                       \s
                        5. Tipos de barco:
                           • Fragata: 1 casilla
                           • Destructor: 2 casillas
                           • Submarino: 3 casillas
                           • Portaaviones: 4 casillas
                       \s
                        6. No se pueden superponer barcos uno encima del otro
                       \s
                        7. Cuando empiece la batalla, selecciona las casillas del enemigo
                       \s
                        8. Gana quien hunda todos los barcos del oponente"""
        );
        ButtonType okButton=new ButtonType("más que claro!!", ButtonBar.ButtonData.OK_DONE); /* boton de aceptar*/
        alert.getButtonTypes().setAll(okButton);
        alert.showAndWait();
    }


    private void cambiarVentana(ActionEvent event, String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        System.out.println("cambio de ventana 1 correcto");

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

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
import com.example.batalla_naval.util.MusicManager;
import com.example.batalla_naval.util.SoundEffects;
import com.example.batalla_naval.model.SesionJuego;
import javafx.scene.control.TextField;

import com.example.batalla_naval.persistence.GamePersistence;
import com.example.batalla_naval.persistence.GameState;


import java.io.IOException;
import java.util.Optional;


public class VistaInicioController {
    @FXML
    private Button jugarButton;
    @FXML
    private Button instruccionesButton;
    @FXML
    private TextField usernameField;
    @FXML
    private Button continuarButton;


    @FXML
    private void initialize() {
        usernameField.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.trim().isEmpty()) {
                usernameField.setStyle("");
            }});
        System.out.println("el juego corre perfectamente");

        MusicManager.playMenuMusic();
        jugarButton.setOnMouseEntered(e -> SoundEffects.playHover());
        instruccionesButton.setOnMouseEntered(e->SoundEffects.playHover());


        jugarButton.setOnAction(e -> {
            SoundEffects.playClick();
            try {
                onJugarClick(e);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        instruccionesButton.setOnAction(e ->{
            SoundEffects.playClick();
            onInstruccionesClick(e);
        });
        usernameField.setOnAction(e -> {
            SoundEffects.playClick();
            try {
                onJugarClick(new ActionEvent(usernameField, null));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        if (continuarButton != null) {
            continuarButton.setOnMouseEntered(e -> SoundEffects.playHover());
            continuarButton.setDisable(!GamePersistence.existeGuardado());
        }


    }

    @FXML
    private void onJugarClick(ActionEvent event) throws IOException {
        String nombre = usernameField.getText().trim();

        if (nombre.isEmpty()) {
            usernameField.setStyle(
                    "-fx-border-color: #ef4444;" + "-fx-border-width: 2;" + "-fx-background-color: #1f2937;" +
                            "-fx-text-fill: white;"
            );

            usernameField.requestFocus();
            SoundEffects.stoplayClick();
            SoundEffects.playNegativeClick();
            return;
        }
        usernameField.setStyle("");

        SesionJuego.setNombreJugador(nombre);
        cambiarVentana(event, "/com/example/batalla_naval/VistaConfiguracionTablero.fxml");
    }
    @FXML
    private void onContinuarClick(ActionEvent event) throws IOException {
        if (!GamePersistence.existeGuardado()) {
            return;
        }

        GameState gs = GamePersistence.cargar();
        if (gs == null) {
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/batalla_naval/VistaBatalla.fxml"));
        Parent root = loader.load();

        ControladorJuego ctrl = loader.getController();
        ctrl.initFromState(gs);

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
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
        ButtonType okButton = new ButtonType("¡más que claro!", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(okButton);

        Optional<ButtonType> resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == okButton) {
            SoundEffects.playNegativeClick();
        }
    }


    private void cambiarVentana(ActionEvent event, String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}

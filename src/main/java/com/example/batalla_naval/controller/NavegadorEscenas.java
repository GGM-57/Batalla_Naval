package com.example.batalla_naval.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavegadorEscenas {

    /**
     * Carga cualquier vista FXML y reemplaza la escena actual.
     * @param sourceNode un nodo que YA está en la escena actual (ej: gridJugador, un botón, etc.)
     * @param rutaFxml ruta absoluta dentro de resources. Ej: "/com/example/batalla_naval/VistaInicio.fxml"
     */
    public static void irAVista(Node sourceNode, String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(NavegadorEscenas.class.getResource(rutaFxml));
            Parent root = loader.load();

            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la pantalla de victoria y le inyecta el resumen.
     */
    public static void irAVictoria(Node sourceNode, String resumen) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    NavegadorEscenas.class.getResource("/com/example/batalla_naval/VistaVictoria.fxml")
            );
            Parent root = loader.load();

            ControladorVictoria controller = loader.getController();
            controller.setResumen(resumen);

            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

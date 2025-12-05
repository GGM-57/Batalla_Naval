package com.example.batalla_naval;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
       // System.out.println(App.class.getResource("/com/example/batalla_naval/login.fxml"));
        URL fxmlUrl = App.class.getResource("/com/example/batalla_naval/VistaInicio.fxml");

        FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Batalla Naval");
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}

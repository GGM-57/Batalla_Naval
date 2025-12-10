package com.example.batalla_naval.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class MusicManager {

    private static MediaPlayer menuPlayer;

    // Reproduce la música del menú (en bucle)
    public static void playMenuMusic() {
        // Si ya está sonando, no repetir
        if (menuPlayer != null && menuPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        // Cargar el recurso desde /src/main/resources/audio/menu_theme.mp3
        URL recurso = MusicManager.class.getResource(
                "/com/example/batalla_naval/audios/ElementalStars.mp3"
        );

        if (recurso == null) {
            System.err.println("No se encontró /com/example/batalla_naval/audios/ElementalStars.mp3 ");
            return;
        }

        Media media = new Media(recurso.toExternalForm());
        menuPlayer = new MediaPlayer(media);

        // Repetir indefinidamente
        menuPlayer.setCycleCount(MediaPlayer.INDEFINITE);

        // Volumen: ajusta a gusto (0.0 – 1.0)
        menuPlayer.setVolume(0.3);

        // Reproducir
        menuPlayer.play();
    }

    // Detener la música del menú
    public static void stopMenuMusic() {
        if (menuPlayer != null) {
            menuPlayer.stop();
        }
    }
}

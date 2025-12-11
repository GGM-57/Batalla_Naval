package com.example.batalla_naval.util;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class SoundEffects {

    private static AudioClip hoverSound;
    private static AudioClip clickSound;

    static {
        // Cargar sonidos solo una vez
        hoverSound = loadClip("/com/example/batalla_naval/audios/soundEffects/hoverEffect.mp3");
        clickSound = loadClip("/com/example/batalla_naval/audios/soundEffects/menuPositive.mp3");
    }

    private static AudioClip loadClip(String path) {
        URL recurso = SoundEffects.class.getResource(path);
        if (recurso == null) {
            System.err.println("No se encontró el sonido: " + path);
            return null;
        }
        return new AudioClip(recurso.toExternalForm());
    }

    public static void playHover() {
        if (hoverSound != null) {
            hoverSound.play(0.4); // volumen entre 0.0 y 1.0
        }
    }

    public static void playClick() {
        if (clickSound != null) {
            clickSound.play(0.6);
        }
    }
}

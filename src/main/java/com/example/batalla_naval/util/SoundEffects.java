package com.example.batalla_naval.util;

import javafx.scene.media.AudioClip;
import java.net.URL;

public class SoundEffects {

    private static AudioClip hoverSound;
    private static AudioClip clickSound;
    private static AudioClip clickNegativeSound;
    private static AudioClip posicionarBarcoSound;
    private static AudioClip explosion1;
    private static AudioClip explosion2;


    static {
        // Cargar sonidos solo una vez
        hoverSound = loadClip("/com/example/batalla_naval/audios/soundEffects/hoverEffect.mp3");
        clickSound = loadClip("/com/example/batalla_naval/audios/soundEffects/menuPositive.mp3");
        clickNegativeSound = loadClip("/com/example/batalla_naval/audios/soundEffects/menuNegative.mp3");
        posicionarBarcoSound=loadClip("/com/example/batalla_naval/audios/soundEffects/posicionar.mp3");
        explosion1=loadClip("/com/example/batalla_naval/audios/soundEffects/explosion.mp3");
        explosion2=loadClip("/com/example/batalla_naval/audios/soundEffects/explosion2.mp3");
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
        if (hoverSound != null){
            hoverSound.play(0.9);
        }
    }

    public static void playClick() {
        if (clickSound != null){
            clickSound.play(0.6);
        }
    }
    public static void playNegativeClick(){
        if(clickSound !=null){
            clickNegativeSound.play(0.6);
        }
    }public static void playPosicionarBarco(){
        if(clickSound !=null){
            posicionarBarcoSound.play(0.6);
        }
    }
    public static void playExplosion1(){
        if(clickSound !=null){
            explosion1.play(0.6);
        }
    } public static void playExplosion2(){
        if(clickSound !=null){
            explosion2.play(0.9);
        }
    }
}

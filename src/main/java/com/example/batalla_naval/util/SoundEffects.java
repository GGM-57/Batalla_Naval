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
    private static AudioClip aguaSalpicada;
    private static AudioClip proyectil;
    private static AudioClip fuegoAmigo;



    static {
        // Cargar sonidos solo una vez
        hoverSound = loadClip("/com/example/batalla_naval/audios/soundEffects/hoverEffect.mp3");
        clickSound = loadClip("/com/example/batalla_naval/audios/soundEffects/menuPositive.mp3");
        clickNegativeSound = loadClip("/com/example/batalla_naval/audios/soundEffects/menuNegative.mp3");
        posicionarBarcoSound=loadClip("/com/example/batalla_naval/audios/soundEffects/posicionar.mp3");
        explosion1=loadClip("/com/example/batalla_naval/audios/soundEffects/explosion.mp3");
        explosion2=loadClip("/com/example/batalla_naval/audios/soundEffects/explosion2.mp3");
        aguaSalpicada=loadClip("/com/example/batalla_naval/audios/soundEffects/salpicadoraDeAgua.mp3");
        proyectil=loadClip("/com/example/batalla_naval/audios/soundEffects/proyectil.mp3");
        fuegoAmigo=loadClip("/com/example/batalla_naval/audios/soundEffects/fuegoamigo.mp3");
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
            clickSound.play(0.9);
        }
    }
    public static void stoplayClick() {
        if (clickSound != null){
            clickSound.stop();
        }
    }

    public static void playNegativeClick(){
        if(clickNegativeSound !=null){
            clickNegativeSound.play(0.9);
        }
    }

    public static void playPosicionarBarco(){
        if(posicionarBarcoSound !=null){
            posicionarBarcoSound.play(0.9);
        }
    }

    public static void playExplosion1(){
        if(explosion1 !=null){
            explosion1.play(0.9);
        }
    }

    public static void playExplosion2(){
        if(explosion2 !=null){
            explosion2.play(0.9);
        }
    }

    public static void misilFallado(){
        if(aguaSalpicada !=null){
            aguaSalpicada.play(0.9);
        }
    }

    public static void proyectilLanzado() {
        if (proyectil != null) {
            proyectil.play(0.9);
        }
    }
    public static void playFuegoAmigo() {
        if (fuegoAmigo != null) {
            fuegoAmigo.play(0.9);
        }
    }

}



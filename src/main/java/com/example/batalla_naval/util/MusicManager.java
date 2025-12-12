package com.example.batalla_naval.util;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class MusicManager {


    private static final Map<MusicTrack, MediaPlayer> players =
            new EnumMap<>(MusicTrack.class);

    // Ruta de cada soundtrack
    private static final Map<MusicTrack, String> RUTAS = Map.of(
            MusicTrack.MENU,    "/com/example/batalla_naval/audios/soundtracks/ElementalStars.mp3",
            MusicTrack.BATALLA, "/com/example/batalla_naval/audios/soundtracks/pazAntesdelaGuerra.mp3",
            MusicTrack.VICTORIA,"/com/example/batalla_naval/audios/soundtracks/Victory.mp3",
            MusicTrack.DERROTA, "/com/example/batalla_naval/audios/soundtracks/derrota.mp3",
            MusicTrack.PROBLEMAS, "/com/example/batalla_naval/audios/soundtracks/themeProblemas.mp3"
    );

    // Para saber qué pista está en loop actualmente (opcional)
    private static MusicTrack currentLoop = null;

    // =============== API genérica ===============

    // Reproduce en loop una pista
    public static void playLoop(MusicTrack track, double volume) {
        // Si ya está sonando esa misma pista, no la reiniciamos
        MediaPlayer mp = players.get(track);
        if (mp != null && mp.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        // Detener la que estaba en loop antes (si quieres que solo haya una de fondo)
        if (currentLoop != null && currentLoop != track) {
            stop(currentLoop);
        }

        // Crear el MediaPlayer si aún no existe
        if (mp == null) {
            String ruta = RUTAS.get(track);
            if (ruta == null) {
                System.err.println("No se ha configurado ruta para " + track);
                return;
            }

            URL recurso = MusicManager.class.getResource(ruta);
            if (recurso == null) {
                System.err.println("No se encontró el recurso de audio: " + ruta);
                return;
            }

            Media media = new Media(recurso.toExternalForm());
            mp = new MediaPlayer(media);
            players.put(track, mp);
        }

        mp.stop();
        mp.setCycleCount(MediaPlayer.INDEFINITE);
        mp.setVolume(volume);
        mp.play();

        currentLoop = track;
    }

    // Reproduce una pista una sola vez (sin loop)
    public static void playOnce(MusicTrack track, double volume) {
        MediaPlayer mp = players.get(track);

        if (mp == null) {
            String ruta = RUTAS.get(track);
            if (ruta == null) {
                System.err.println("No se ha configurado ruta para " + track);
                return;
            }

            URL recurso = MusicManager.class.getResource(ruta);
            if (recurso == null) {
                System.err.println("No se encontró el recurso de audio: " + ruta);
                return;
            }

            Media media = new Media(recurso.toExternalForm());
            mp = new MediaPlayer(media);
            players.put(track, mp);
        }

        mp.stop();
        mp.setCycleCount(1);
        mp.setVolume(volume);
        mp.play();
    }

    // Detener una pista específica
    public static void stop(MusicTrack track) {
        MediaPlayer mp = players.get(track);
        if (mp != null) {
            mp.stop();
        }
        if (currentLoop == track) {
            currentLoop = null;
        }
    }

    // Detener todas las pistas
    public static void stopAll() {
        for (MediaPlayer mp : players.values()) {
            if (mp != null) {
                mp.stop();
            }
        }
        currentLoop = null;
    }

    // =============== Métodos antiguos (compatibilidad) ===============

    // Reproduce la música del menú (en bucle)
    public static void playMenuMusic() {
        playLoop(MusicTrack.MENU, 0.3);
    }

    // Detener la música del menú
    public static void stopMenuMusic() {
        stop(MusicTrack.MENU);
    }
}

package com.example.batalla_naval.persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class PlayerStatsIO {

    private static final String FILE_NAME = "player_stats.txt";

    private PlayerStatsIO() {}

    private static Path filePath() {
        String home = System.getProperty("user.home");
        return Path.of(home, FILE_NAME);
    }

    public static final class Stats {
        public String nickname;
        public int hundidosJugador;
        public int hundidosMaquina;
    }

    public static boolean existeStats() {
        return Files.exists(filePath());
    }

    public static void borrarStats() {
        try {
            Files.deleteIfExists(filePath());
        } catch (IOException ignored) { }
    }

    public static void guardarStats(String nickname, int hundidosJugador, int hundidosMaquina) {
        Path p = filePath();

        try (BufferedWriter bw = Files.newBufferedWriter(p)) {
            bw.write(nickname == null ? "" : nickname);
            bw.newLine();
            bw.write(Integer.toString(hundidosJugador));
            bw.newLine();
            bw.write(Integer.toString(hundidosMaquina));
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stats cargarStats() {
        Path p = filePath();
        if (!Files.exists(p)) return null;

        try (BufferedReader br = Files.newBufferedReader(p)) {
            Stats s = new Stats();
            s.nickname = br.readLine();

            String a = br.readLine();
            String b = br.readLine();

            s.hundidosJugador = (a == null) ? 0 : Integer.parseInt(a.trim());
            s.hundidosMaquina = (b == null) ? 0 : Integer.parseInt(b.trim());

            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

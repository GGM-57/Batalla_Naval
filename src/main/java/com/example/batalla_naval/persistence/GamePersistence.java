package com.example.batalla_naval.persistence;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GamePersistence {

    private static final String FILE_NAME = "batalla_naval.save";

    private GamePersistence() {}

    private static Path filePath() {
        String home = System.getProperty("user.home");
        return Path.of(home, FILE_NAME);
    }

    public static boolean existeGuardado() {
        return Files.exists(filePath());
    }

    public static void borrar() {
        try {
            Files.deleteIfExists(filePath());
        } catch (IOException ignored) { }
    }

    public static void guardar(GameState state) {
        if (state == null) return;

        Path p = filePath();

        try (ObjectOutputStream out = new ObjectOutputStream(
                new BufferedOutputStream(Files.newOutputStream(p))
        )) {
            out.writeObject(state);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameState cargar() {
        Path p = filePath();
        if (!Files.exists(p)) return null;

        try (ObjectInputStream in = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(p))
        )) {
            Object obj = in.readObject();
            if (obj instanceof GameState gs) {
                return gs;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

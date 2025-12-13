package com.example.batalla_naval.controller;

import com.example.batalla_naval.util.MusicManager;
import com.example.batalla_naval.util.MusicTrack;
import com.example.batalla_naval.util.SoundEffects;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ControladorVictoria {

    @FXML private Canvas canvasConfetti;
    @FXML private Label lblResumen;

    private final Random random = new Random();
    private final List<Particula> particulas = new ArrayList<>();
    private AnimationTimer timer;


    /** Para que ControladorJuego le pase un texto bonito */
    public void setResumen(String texto) {
        if (lblResumen != null) lblResumen.setText(texto);
    }

    @FXML
    private void initialize() {

        try {
            MusicManager.playLoop(MusicTrack.VICTORIA, 0.50);
        } catch (Exception ignored) { }
        try {
            SoundEffects.playClick();
        } catch (Exception ignored) { }

        iniciarConfetti();
    }

    private void iniciarConfetti() {
        GraphicsContext gc = canvasConfetti.getGraphicsContext2D();


        gc.setFill(Color.rgb(0, 0, 0, 1.0));
        gc.fillRect(0, 0, canvasConfetti.getWidth(), canvasConfetti.getHeight());


        for (int i = 0; i < 260; i++) {
            particulas.add(Particula.random(random, canvasConfetti.getWidth(), canvasConfetti.getHeight(), true));
        }

        timer = new AnimationTimer() {
            long last = 0;

            @Override
            public void handle(long now) {
                if (last==0) last = now;
                double dt = (now - last) / 1_000_000_000.0;
                last = now;


                gc.setFill(Color.rgb(0, 0, 0, 0.10));
                gc.fillRect(0, 0, canvasConfetti.getWidth(), canvasConfetti.getHeight());


                if (particulas.size() < 420 && random.nextDouble() < 0.35) {
                    particulas.add(Particula.random(random, canvasConfetti.getWidth(), canvasConfetti.getHeight(), false));
                }

                for (int i = particulas.size() - 1; i >= 0; i--) {
                    Particula p = particulas.get(i);
                    p.update(dt);

                    gc.save();
                    gc.translate(p.x, p.y);
                    gc.rotate(p.rot);
                    gc.setFill(p.color);
                    gc.fillRoundRect(-p.w / 2, -p.h / 2, p.w, p.h, 4, 4);
                    gc.restore();

                    if (p.y > canvasConfetti.getHeight() + 60 || p.alpha <= 0.02) {
                        particulas.remove(i);
                    }
                }
            }
        };

        timer.start();
    }

    @FXML
    private void onReintentar() {
        SoundEffects.playClick();
        detener();

        NavegadorEscenas.irAVista(canvasConfetti, "/com/example/batalla_naval/VistaConfiguracionTablero.fxml");

    }

    @FXML
    private void onMenu() {
        SoundEffects.playClick();
        detener();
        NavegadorEscenas.irAVista(canvasConfetti, "/com/example/batalla_naval/VistaInicio.fxml");

    }

    @FXML
    private void onSalir() {
        SoundEffects.playNegativeClick();
        detener();
        Stage stage = (Stage) canvasConfetti.getScene().getWindow();
        stage.close();
    }

    private void detener() {
        if (timer != null) timer.stop();
    }



    private static class Particula {
        double x, y, vx, vy;
        double w, h;
        double rot, vrot;
        double alpha;
        Color color;

        void update(double dt) {
            vy += 520 * dt;
            vx *= Math.pow(0.92, dt * 60);
            vy *= Math.pow(0.985, dt * 60);

            x += vx * dt;
            y += vy * dt;

            rot += vrot * dt;

            alpha -= 0.22 * dt;
            if (alpha < 0) alpha = 0;

            color = Color.color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        }

        static Particula random(Random r, double W, double H, boolean burst) {
            Particula p = new Particula();

            p.x = r.nextDouble() * W;
            p.y = burst ? (H * 0.25 + r.nextDouble() * H * 0.20) : -20;

            double speed = burst ? (340 + r.nextDouble() * 520) : (180 + r.nextDouble() * 420);
            double angle = burst ? Math.toRadians(220 + r.nextDouble() * 100) : Math.toRadians(80 + r.nextDouble() * 20);


            p.vx = Math.cos(angle) * speed * (r.nextBoolean() ? 1 : -1);
            p.vy = Math.sin(angle) * speed;

            p.w = 6 + r.nextDouble() * 10;
            p.h = 10 + r.nextDouble() * 18;

            p.rot = r.nextDouble() * 360;
            p.vrot = (r.nextDouble() * 260 + 120) * (r.nextBoolean() ? 1 : -1);

            p.alpha = 1.0;

            Color[] colors = new Color[] {
                    Color.web("#FF4D4D"),
                    Color.web("#FFD24D"),
                    Color.web("#4DFF88"),
                    Color.web("#4DC3FF"),
                    Color.web("#B84DFF"),
                    Color.web("#FFFFFF")
            };
            p.color = colors[r.nextInt(colors.length)];

            return p;
        }
    }
}

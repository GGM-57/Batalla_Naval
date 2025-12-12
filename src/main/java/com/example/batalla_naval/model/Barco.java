package com.example.batalla_naval.model;

import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Barco {

    // ================== LÓGICA DEL JUEGO (MODELO) ==================
    private String tipo;          // nombre del barco (Fragata, etc.)
    private int tamaño;           // 1, 2, 3, 4
    private final List<Coordenada> posiciones = new ArrayList<>();
    private int golpes = 0;

    private boolean vertical = true;
    private int fila;
    private int columna;
    private boolean posicionado = false;   // ¿ya está en el tablero?

    // ================== PARTE GRÁFICA (VISTA) ======================
    private Group forma;          // grupo de figuras que dibuja el barco

    public Barco(String tipo, int tamaño) {
        this.tamaño = tamaño;
        this.tipo = tipo;

        // Crear la figura visual según el tipo
        this.forma = crearForma();

        // Limitar área visible del barco para evitar offsets raros
        forma.layoutBoundsProperty().addListener((obs, oldB, newB) -> {
            Rectangle clip = new Rectangle(
                    newB.getMinX(),
                    newB.getMinY(),
                    newB.getWidth(),
                    newB.getHeight()
            );
            forma.setClip(clip);
        });

        normalizarForma(this.forma);
        habilitarRotacion();
    }

    // ================== GETTERS / SETTERS DE LÓGICA ==================

    public String getTipo() {
        return tipo;
    }

    public int getTamaño() {
        return tamaño;
    }

    // Versión sin ñ para usar desde Tablero
    public int getTamanio() {
        return tamaño;
    }

    public boolean esVertical() {
        return vertical;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }

    public void setPosicion(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.posicionado = true;
    }

    public boolean esPosicionado() {
        return posicionado;
    }

    public List<Coordenada> getPosiciones() {
        return posiciones;
    }

    // ===== Métodos que pide el diagrama: agregarCoordenada / golpes =====

    public void agregarCoordenada(Coordenada coord) {
        posiciones.add(coord);
    }

    public void registrarGolpe() {
        golpes++;
    }

    public boolean estaHundido() {
        return golpes >= tamaño;
    }

    // ================== PARTE GRÁFICA ===============================

    public Group getForma() {
        return forma;
    }

    /** Decide la forma a dibujar según el tipo */
    private Group crearForma() {
        switch (tipo) {
            case "Fragata":
                return crearFragata();
            case "Destructor":
                return crearDestructor();
            case "Submarino":
                return crearSubmarino();
            case "Portaaviones":
                return crearPortaaviones();
            default:
                return crearGenerico();
        }
    }

    /**
     * Ajusta los hijos del grupo para que la esquina superior izquierda del conjunto
     * quede en (0,0).
     */
    private void normalizarForma(Group g) {
        if (g == null || g.getChildren().isEmpty()) return;

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;

        for (Node n : g.getChildren()) {
            Bounds b = n.getBoundsInLocal();
            double childMinX = b.getMinX() + n.getTranslateX();
            double childMinY = b.getMinY() + n.getTranslateY();
            if (childMinX < minX) minX = childMinX;
            if (childMinY < minY) minY = childMinY;
        }

        if (minX == Double.MAX_VALUE || minY == Double.MAX_VALUE) return;

        for (Node n : g.getChildren()) {
            n.setTranslateX(n.getTranslateX() - minX);
            n.setTranslateY(n.getTranslateY() - minY);
        }
    }

    // ================== DIBUJO DE CADA TIPO ==========================

    private Group crearGenerico() {
        Group g = new Group();
        for (int i = 0; i < tamaño; i++) {
            Rectangle r = new Rectangle(25, 25);
            r.setFill(Color.LIGHTBLUE);
            r.setStroke(Color.BLACK);
            r.setStrokeWidth(2);
            r.setTranslateX(i * 45);
            g.getChildren().add(r);
        }
        return g;
    }

    /* Fragata */
    private Group crearFragata() {
        Group g = new Group();

        Rectangle cuerpo = new Rectangle(40, 18);
        cuerpo.setArcWidth(10);
        cuerpo.setArcHeight(10);
        cuerpo.setFill(Color.web("#334155"));
        cuerpo.setStroke(Color.BLACK);
        cuerpo.setStrokeWidth(1.8);
        g.getChildren().add(cuerpo);

        Polygon proa = new Polygon();
        proa.getPoints().addAll(
                40.0, 0.0,
                40.0, 18.0,
                54.0, 9.0
        );
        proa.setFill(Color.web("#475569"));
        proa.setStroke(Color.BLACK);
        proa.setStrokeWidth(1.5);
        g.getChildren().add(proa);

        javafx.scene.shape.Line lineaDecorativa = new javafx.scene.shape.Line();
        lineaDecorativa.setStartX(0);
        lineaDecorativa.setStartY(12);
        lineaDecorativa.setEndX(40);
        lineaDecorativa.setEndY(12);
        lineaDecorativa.setStroke(Color.web("#93C5FD"));
        lineaDecorativa.setStrokeWidth(2);
        g.getChildren().add(lineaDecorativa);

        Rectangle cabina = new Rectangle(14, 10);
        cabina.setFill(Color.web("#E5E7EB"));
        cabina.setStroke(Color.BLACK);
        cabina.setStrokeWidth(1.2);
        cabina.setTranslateX(6);
        cabina.setTranslateY(3);
        g.getChildren().add(cabina);

        Rectangle ventana = new Rectangle(6, 5);
        ventana.setFill(Color.web("#60A5FA"));
        ventana.setStroke(Color.BLACK);
        ventana.setStrokeWidth(1);
        ventana.setTranslateX(9);
        ventana.setTranslateY(5);
        g.getChildren().add(ventana);

        return g;
    }

    /* Destructor (2 celdas) */
    private Group crearDestructor() {
        Group g = new Group();

        // Destructor ocupa 2 celdas
        int t = this.tamaño; // debería ser 2
        double cellStep = 45;
        double anchoSegmento = 40;
        double alto = 18;

        // 40 + 45*(t-1) => para t=2: 85
        double anchoTotal = (t * cellStep) - (cellStep - anchoSegmento);

        // ===== CASCO (UNA SOLA PIEZA) =====
        Rectangle casco = new Rectangle(anchoTotal, alto);
        casco.setArcWidth(18);
        casco.setArcHeight(18);
        casco.setFill(Color.web("#0f7a3a"));
        casco.setStroke(Color.web("#063e1f"));
        casco.setStrokeWidth(1.6);
        g.getChildren().add(casco);

        // Proa más clara para dar volumen
        Rectangle proa = new Rectangle(14, alto);
        proa.setArcWidth(18);
        proa.setArcHeight(18);
        proa.setFill(Color.web("#18a34a"));
        proa.setStroke(Color.TRANSPARENT);
        proa.setTranslateX(anchoTotal - 14);
        g.getChildren().add(proa);

        // Cola (aleta) sin sobresalir
        Rectangle cola = new Rectangle(7, 10);
        cola.setArcWidth(10);
        cola.setArcHeight(10);
        cola.setFill(Color.web("#064e3b"));
        cola.setStroke(Color.web("#052e16"));
        cola.setStrokeWidth(1.0);
        cola.setTranslateX(-3.5);
        cola.setTranslateY(4);
        g.getChildren().add(cola);

        // ===== FRANJA CENTRAL (deck) =====
        Rectangle franja = new Rectangle(anchoTotal - 18, 4);
        franja.setArcWidth(10);
        franja.setArcHeight(10);
        franja.setFill(Color.web("#53c18a"));
        franja.setStroke(Color.TRANSPARENT);
        franja.setTranslateX(9);
        franja.setTranslateY(7);
        g.getChildren().add(franja);

        // ===== SUPERSTRUCTURA (dentro del casco) =====
        Rectangle superestructura = new Rectangle(20, 10);
        superestructura.setArcWidth(8);
        superestructura.setArcHeight(8);
        superestructura.setFill(Color.web("#0b5c2e"));
        superestructura.setStroke(Color.web("#052e16"));
        superestructura.setStrokeWidth(1.1);
        superestructura.setTranslateX(anchoTotal * 0.38);
        superestructura.setTranslateY(4);
        g.getChildren().add(superestructura);

        // Ventanitas (3) sobre la superestructura
        for (int v = 0; v < 3; v++) {
            Rectangle win = new Rectangle(4, 3);
            win.setArcWidth(3);
            win.setArcHeight(3);
            win.setFill(Color.web("#a7f3d0"));
            win.setStroke(Color.web("#0f172a"));
            win.setStrokeWidth(0.6);
            win.setTranslateX(anchoTotal * 0.38 + 3 + v * 6);
            win.setTranslateY(7);
            g.getChildren().add(win);
        }

        // ===== CAÑÓN (dentro del casco, hacia la proa) =====
        Rectangle baseCanon = new Rectangle(10, 6);
        baseCanon.setArcWidth(6);
        baseCanon.setArcHeight(6);
        baseCanon.setFill(Color.web("#111827"));
        baseCanon.setStroke(Color.web("#000000"));
        baseCanon.setStrokeWidth(1.0);
        baseCanon.setTranslateX(anchoTotal - 30);
        baseCanon.setTranslateY(6);
        g.getChildren().add(baseCanon);

        Rectangle tuboCanon = new Rectangle(14, 2);
        tuboCanon.setArcWidth(4);
        tuboCanon.setArcHeight(4);
        tuboCanon.setFill(Color.web("#111827"));
        tuboCanon.setStroke(Color.web("#000000"));
        tuboCanon.setStrokeWidth(0.8);
        tuboCanon.setTranslateX(anchoTotal - 18);
        tuboCanon.setTranslateY(8);
        g.getChildren().add(tuboCanon);

        // Luz frontal pequeña
        Rectangle luz = new Rectangle(3, 3);
        luz.setArcWidth(4);
        luz.setArcHeight(4);
        luz.setFill(Color.web("#facc15"));
        luz.setStroke(Color.web("#111827"));
        luz.setStrokeWidth(0.6);
        luz.setTranslateX(anchoTotal - 10);
        luz.setTranslateY(7.5);
        g.getChildren().add(luz);

        return g;
    }


    /* Submarino (3 celdas) */
    private Group crearSubmarino() {
        Group g = new Group();

        // Medidas base (alineadas a tu grilla)
        double cellStep = 45;     // distancia entre celdas
        double alto = 18;         // alto del submarino
        double anchoSegmento = 40; // ancho visual por celda

        double anchoTotal = (tamaño * cellStep) - (cellStep - anchoSegmento); // 40 + 45*(tamaño-1)

        // ===== CUERPO PRINCIPAL (UNA SOLA PIEZA) =====
        Rectangle cuerpo = new Rectangle(anchoTotal, alto);
        cuerpo.setArcWidth(22);
        cuerpo.setArcHeight(22);
        cuerpo.setFill(Color.web("#6b7280"));
        cuerpo.setStroke(Color.web("#374151"));
        cuerpo.setStrokeWidth(1.6);
        g.getChildren().add(cuerpo);

        // Proa (nariz) más clara
        Rectangle proa = new Rectangle(14, alto);
        proa.setArcWidth(22);
        proa.setArcHeight(22);
        proa.setFill(Color.web("#7b8794"));
        proa.setStroke(Color.TRANSPARENT);
        proa.setTranslateX(anchoTotal - 14);
        g.getChildren().add(proa);

        // Cola (aleta) sin sobresalir del alto
        Rectangle cola = new Rectangle(8, 10);
        cola.setArcWidth(10);
        cola.setArcHeight(10);
        cola.setFill(Color.web("#4b5563"));
        cola.setStroke(Color.web("#111827"));
        cola.setStrokeWidth(1.0);
        cola.setTranslateX(-4);
        cola.setTranslateY(4);
        g.getChildren().add(cola);

        // ===== DETALLES SUPERIORES =====
        // Franja superior (deck)
        Rectangle franja = new Rectangle(anchoTotal - 18, 4);
        franja.setArcWidth(10);
        franja.setArcHeight(10);
        franja.setFill(Color.web("#9ca3af"));
        franja.setStroke(Color.TRANSPARENT);
        franja.setTranslateX(9);
        franja.setTranslateY(7);
        g.getChildren().add(franja);

        // Escotilla central (centrada)
        double centroX = anchoTotal / 2.0;
        Rectangle escotilla = new Rectangle(14, 9);
        escotilla.setArcWidth(8);
        escotilla.setArcHeight(8);
        escotilla.setFill(Color.web("#4b5563"));
        escotilla.setStroke(Color.web("#111827"));
        escotilla.setStrokeWidth(1.0);
        escotilla.setTranslateX(centroX - 7);
        escotilla.setTranslateY(4.5);
        g.getChildren().add(escotilla);

        // Ventanas (3) distribuidas proporcionalmente
        for (int k = 1; k <= 3; k++) {
            double px = (anchoTotal * (k / 4.0)) - 2.5; // 1/4, 2/4, 3/4
            Rectangle ventana = new Rectangle(5, 5);
            ventana.setArcWidth(6);
            ventana.setArcHeight(6);
            ventana.setFill(Color.web("#dbeafe"));
            ventana.setStroke(Color.web("#0f172a"));
            ventana.setStrokeWidth(0.8);
            ventana.setTranslateX(px);
            ventana.setTranslateY(6.5);
            g.getChildren().add(ventana);
        }

        // Luz frontal
        Rectangle luz = new Rectangle(3, 3);
        luz.setArcWidth(4);
        luz.setArcHeight(4);
        luz.setFill(Color.web("#facc15"));
        luz.setStroke(Color.web("#111827"));
        luz.setStrokeWidth(0.6);
        luz.setTranslateX(anchoTotal - 10);
        luz.setTranslateY(7.5);
        g.getChildren().add(luz);

        return g;
    }



    /* Portaaviones (4 celdas) */
    private Group crearPortaaviones() {
        Group g = new Group();

        // Usa el tamaño real del barco (en tu caso 4)
        int t = this.tamaño; // o int t = 4;

        // Medidas base alineadas a tu grilla
        double cellStep = 45;
        double anchoSegmento = 40;
        double alto = 20;

        // 40 + 45*(t-1)  => para t=4: 175
        double anchoTotal = (t * cellStep) - (cellStep - anchoSegmento);

        // ===== CASCO (UNA SOLA PIEZA) =====
        Rectangle casco = new Rectangle(anchoTotal, alto);
        casco.setArcWidth(18);
        casco.setArcHeight(18);
        casco.setFill(Color.web("#4b5563"));          // gris naval
        casco.setStroke(Color.web("#111827"));
        casco.setStrokeWidth(1.8);
        g.getChildren().add(casco);

        // Proa más clara para dar volumen
        Rectangle proa = new Rectangle(18, alto);
        proa.setArcWidth(18);
        proa.setArcHeight(18);
        proa.setFill(Color.web("#64748b"));
        proa.setStroke(Color.TRANSPARENT);
        proa.setTranslateX(anchoTotal - 18);
        g.getChildren().add(proa);

        // ===== CUBIERTA (DECK) =====
        Rectangle cubierta = new Rectangle(anchoTotal - 6, alto - 6);
        cubierta.setArcWidth(14);
        cubierta.setArcHeight(14);
        cubierta.setFill(Color.web("#1f2937"));       // cubierta oscura
        cubierta.setStroke(Color.web("#0b1220"));
        cubierta.setStrokeWidth(1.2);
        cubierta.setTranslateX(3);
        cubierta.setTranslateY(3);
        g.getChildren().add(cubierta);

        // Línea central de la cubierta (amarilla)
        Rectangle lineaCentral = new Rectangle(anchoTotal - 22, 3);
        lineaCentral.setArcWidth(6);
        lineaCentral.setArcHeight(6);
        lineaCentral.setFill(Color.web("#facc15"));
        lineaCentral.setTranslateX(10);
        lineaCentral.setTranslateY(9);
        g.getChildren().add(lineaCentral);

        // Marcas blancas pequeñas sobre la pista
        for (int k = 0; k < t; k++) {
            Rectangle marca = new Rectangle(10, 2);
            marca.setFill(Color.web("#e5e7eb"));
            marca.setTranslateX(15 + k * 40);
            marca.setTranslateY(6);
            g.getChildren().add(marca);
        }

        // Pista oblicua (diagonal simple con Polygon)
        javafx.scene.shape.Polygon pistaOblicua = new javafx.scene.shape.Polygon(
                anchoTotal * 0.55,  3.0,
                anchoTotal - 6.0,   3.0,
                anchoTotal - 18.0,  17.0,
                anchoTotal * 0.55,  17.0
        );
        pistaOblicua.setFill(Color.web("#111827"));
        pistaOblicua.setStroke(Color.web("#0b1220"));
        pistaOblicua.setStrokeWidth(1.0);
        g.getChildren().add(pistaOblicua);

        // ===== ELEVADORES (rectángulos claros) =====
        Rectangle elev1 = new Rectangle(16, 6);
        elev1.setArcWidth(6);
        elev1.setArcHeight(6);
        elev1.setFill(Color.web("#374151"));
        elev1.setStroke(Color.web("#0b1220"));
        elev1.setStrokeWidth(1.0);
        elev1.setTranslateX(anchoTotal * 0.18);
        elev1.setTranslateY(12);
        g.getChildren().add(elev1);

        Rectangle elev2 = new Rectangle(16, 6);
        elev2.setArcWidth(6);
        elev2.setArcHeight(6);
        elev2.setFill(Color.web("#374151"));
        elev2.setStroke(Color.web("#0b1220"));
        elev2.setStrokeWidth(1.0);
        elev2.setTranslateX(anchoTotal * 0.40);
        elev2.setTranslateY(4);
        g.getChildren().add(elev2);

        // ===== ISLA / SUPERSTRUCTURA (DENTRO DE LA CUBIERTA) =====
        // Base isla
        Rectangle isla = new Rectangle(22, 12);
        isla.setArcWidth(8);
        isla.setArcHeight(8);
        isla.setFill(Color.web("#334155"));
        isla.setStroke(Color.web("#0f172a"));
        isla.setStrokeWidth(1.2);
        isla.setTranslateX(anchoTotal * 0.62);
        isla.setTranslateY(5);
        g.getChildren().add(isla);

        // Nivel superior isla
        Rectangle islaTop = new Rectangle(14, 6);
        islaTop.setArcWidth(6);
        islaTop.setArcHeight(6);
        islaTop.setFill(Color.web("#475569"));
        islaTop.setStroke(Color.web("#0f172a"));
        islaTop.setStrokeWidth(1.0);
        islaTop.setTranslateX(anchoTotal * 0.62 + 4);
        islaTop.setTranslateY(3);
        g.getChildren().add(islaTop);

        // Ventanitas isla
        for (int v = 0; v < 3; v++) {
            Rectangle win = new Rectangle(4, 3);
            win.setArcWidth(3);
            win.setArcHeight(3);
            win.setFill(Color.web("#93c5fd"));
            win.setStroke(Color.web("#0f172a"));
            win.setStrokeWidth(0.6);
            win.setTranslateX(anchoTotal * 0.62 + 4 + v * 6);
            win.setTranslateY(8);
            g.getChildren().add(win);
        }

        // Radar/antena (SIN sobresalir)
        Rectangle radar = new Rectangle(10, 2);
        radar.setArcWidth(4);
        radar.setArcHeight(4);
        radar.setFill(Color.web("#9ca3af"));
        radar.setStroke(Color.web("#0f172a"));
        radar.setStrokeWidth(0.8);
        radar.setTranslateX(anchoTotal * 0.62 + 6);
        radar.setTranslateY(2);
        g.getChildren().add(radar);

        return g;
    }

    // ================== MAPEO TIPO → TAMAÑO =========================

    public static int tamañoPorTipo(String tipo) {
        return switch (tipo) {
            case "Fragata" -> 1;
            case "Destructor" -> 2;
            case "Submarino" -> 3;
            case "Portaaviones" -> 4;
            default -> 1;
        };
    }

    // ================== ROTACIÓN ====================================

    public void rotarNavio() {
        this.vertical = !this.vertical;

        // Rotamos solo los rectángulos que forman el “cuerpo” principal
        for (Node n : forma.getChildren()) {
            if (n instanceof Rectangle parte) {
                // Detectamos si este rectángulo forma parte del cuerpo (ancho 40, alto ~18–20)
                if (parte.getWidth() == 40 && (parte.getHeight() == 18 || parte.getHeight() == 20)) {
                    int index = (int) (parte.getTranslateX() / 45); // aproximado
                    if (vertical) {
                        parte.setTranslateX(0);
                        parte.setTranslateY(index * 45);
                    } else {
                        parte.setTranslateX(index * 45);
                        parte.setTranslateY(0);
                    }
                }
            }
        }
    }

    public void habilitarRotacion() {
        forma.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                rotarNavio();
            }
        });
    }
}

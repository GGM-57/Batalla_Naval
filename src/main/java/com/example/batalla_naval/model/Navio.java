package com.example.batalla_naval.model;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;


public class Navio {
    private String tipo;
    private int tamaño;       /*1, 2, 3, 4*/
    private boolean vertical = true;
    private int fila;
    private int columna;
    private boolean posicionado = false;  /*ya está en el tablero?*/
    private Group forma; /*el navio dibujado=grupo de figuras*/

    public Navio(String tipo, int tamaño) {
        //System.out.println("constructor navio ejecutando");
        this.tamaño=tamaño;
        this.tipo=tipo;
       // this.vertical=true;
        this.forma=crearForma(); /* en esta parte del constructor se crea la figura visual segun el tipo*/
        //System.out.println("se creó forma en clase navio");
        habilitarRotacion();
       // System.out.println("se hablito rotacion en clase navio");
    }
/*
    public void rotar() {
        this.vertical = !this.vertical;
    }*/

    public int getTamaño() {
        return tamaño;
    }

    public String getTipo() {
        return tipo;
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
        this.fila=fila;
        this.columna=columna;
        this.posicionado= true;
    }

    public boolean EsPosicionado() {
        return posicionado;
    }

    /*__________________________________DIBUJAR NAVIOS_____________________________________________*/

    /*getter para que se pueda obtener la figura en el controlador*/
    public Group getForma() {
        return forma;
    }

    /*Mertodo donde se decide la forma a dibujar*/
    private Group crearForma(){
        Group navioForma=new Group();

        switch (tipo){
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

    /*metodos de distintas formas para cada navio*/
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

//        int celda=30;/*cada seccion del barco mide 30px*/
//        int lado=30; /*rectangulo */
//
//        for (int i=0; i<tamaño; i++) {
//            Rectangle parte=new Rectangle(20, 30);
//            parte.setFill(Color.DARKBLUE);
//            parte.setStroke(Color.BLACK);
//            parte.setStrokeWidth(2);
//
//            if (vertical) {
//                parte.setTranslateY(i * celda);
//            } else {
//                parte.setTranslateX(i * celda);
//            }
//
//            /*Distribuir en horizontal por defecto
//            parte.setTranslateX(i * 45);*/
//
//            navioForma.getChildren().add(parte);
//        }
//        System.out.println("se creó la figura en la clase navio");
//        return navioForma;

    private Group crearFragata() {
        Group g = new Group();

        // Cuerpo principal (casco)
        Rectangle cuerpo = new Rectangle(40, 18);
        cuerpo.setArcWidth(10);
        cuerpo.setArcHeight(10);
        cuerpo.setFill(Color.web("#334155")); // Gris-azulado elegante
        cuerpo.setStroke(Color.BLACK);
        cuerpo.setStrokeWidth(1.8);
        g.getChildren().add(cuerpo);

        // Proa (punta del barco)
        Polygon proa = new Polygon();
        proa.getPoints().addAll(
                40.0, 0.0,   // punta derecha superior
                40.0, 18.0,  // punta derecha inferior
                54.0, 9.0    // punta extendida
        );
        proa.setFill(Color.web("#475569")); // Tone más claro para contraste
        proa.setStroke(Color.BLACK);
        proa.setStrokeWidth(1.5);
        g.getChildren().add(proa);

        // Línea decorativa horizontal (detalle visual)
        javafx.scene.shape.Line lineaDecorativa = new javafx.scene.shape.Line();
        lineaDecorativa.setStartX(0);
        lineaDecorativa.setStartY(12);
        lineaDecorativa.setEndX(40);
        lineaDecorativa.setEndY(12);
        lineaDecorativa.setStroke(Color.web("#93C5FD")); // Azul luminoso
        lineaDecorativa.setStrokeWidth(2);
        g.getChildren().add(lineaDecorativa);

        // Cabina
        Rectangle cabina = new Rectangle(14, 10);
        cabina.setFill(Color.web("#E5E7EB")); // gris claro
        cabina.setStroke(Color.BLACK);
        cabina.setStrokeWidth(1.2);
        cabina.setTranslateX(6);
        cabina.setTranslateY(3);
        g.getChildren().add(cabina);

        // Ventanita de la cabina
        Rectangle ventana = new Rectangle(6, 5);
        ventana.setFill(Color.web("#60A5FA")); // azul brillante
        ventana.setStroke(Color.BLACK);
        ventana.setStrokeWidth(1);
        ventana.setTranslateX(9);
        ventana.setTranslateY(5);
        g.getChildren().add(ventana);

        return g;
    }




    private Group crearDestructor() {
            Group g = new Group();

            for (int i = 0; i < tamaño; i++) {
                Rectangle r = new Rectangle(20, 20);
                r.setArcWidth(20);
                r.setArcHeight(20);
                r.setFill(Color.FORESTGREEN);
                r.setStroke(Color.BLACK);
                r.setStrokeWidth(2);
                r.setTranslateX(i * 45);
                g.getChildren().add(r);
            }

            return g;
        }

        private Group crearSubmarino() {
            Group g = new Group();

            for (int i = 0; i < tamaño; i++) {
                Rectangle r = new Rectangle(25, 25);
                r.setArcHeight(45);  // borde totalmente redondeado
                r.setArcWidth(45);
                r.setFill(Color.GRAY);
                r.setStroke(Color.BLACK);
                r.setStrokeWidth(2);
                r.setTranslateX(i * 45);
                g.getChildren().add(r);
            }

            return g;
        }

        private Group crearPortaaviones() {
            Group g = new Group();

            // Cuerpo principal
            for (int i = 0; i < tamaño; i++) {
                Rectangle r = new Rectangle(30, 30);
                r.setFill(Color.DARKRED);
                r.setStroke(Color.BLACK);
                r.setStrokeWidth(2);
                r.setTranslateX(i * 45);
                g.getChildren().add(r);
            }

            // Cabina en la parte superior del primer bloque
//            Rectangle cabina = new Rectangle(30, 20);
//            cabina.setTranslateX(8);
//            cabina.setTranslateY(-20);
//            cabina.setFill(Color.BLACK);
//            g.getChildren().add(cabina);

            return g;
        }



    /*metodo para mapear el tañamo y el tipo de navio y así poder soltarlo en el tablero*/
    public static int tamañoPorTipo(String tipo) {
        return switch (tipo) {
            case "Fragata" -> 1;
            case "Destructor" -> 2;
            case "Submarino" -> 3;
            case "Portaaviones" -> 4;
            default -> 1;
        };
    }


    /*metodos para rotar el navio con el mouse*/
    public void rotarNavio() {
        this.vertical = !this.vertical;

        for (int i = 0; i < forma.getChildren().size(); i++) {
            Rectangle parte = (Rectangle) forma.getChildren().get(i);

            if (vertical) {
                parte.setTranslateX(0);
                parte.setTranslateY(i * 45);
            } else {
                parte.setTranslateX(i * 45);
                parte.setTranslateY(0);
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



package com.example.batalla_naval.model;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.geometry.Bounds;
import javafx.scene.Node;


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
        this.forma = crearForma();
        normalizarForma(this.forma);

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
    /**
     * Ajusta los hijos del grupo para que la esquina superior izquierda del conjunto
     * quede en (0,0). Esto evita offsets visuales al insertar el Group en el GridPane.
     */

    private void normalizarForma(Group g) {
        if (g == null || g.getChildren().isEmpty()) return;

        double minX=Double.MAX_VALUE;
        double minY=Double.MAX_VALUE;

        for (Node n:g.getChildren()) {
            Bounds b=n.getBoundsInLocal();
            double childMinX=b.getMinX()+ n.getTranslateX();
            double childMinY =b.getMinY() +n.getTranslateY();
            if (childMinX<minX) minX=childMinX;
            if (childMinY<minY) minY=childMinY;
        }

        if (minX==Double.MAX_VALUE || minY==Double.MAX_VALUE) return;

        for (Node n :g.getChildren()){
            n.setTranslateX(n.getTranslateX()-minX);
            n.setTranslateY(n.getTranslateY()-minY);
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

    /* canva del barco fagata que tiene un cuadrado de tamano*/
    private Group crearFragata() {
        Group g=new Group();

        Rectangle cuerpo=new Rectangle(40, 18);
        cuerpo.setArcWidth(10);
        cuerpo.setArcHeight(10);
        cuerpo.setFill(Color.web("#334155"));
        cuerpo.setStroke(Color.BLACK);
        cuerpo.setStrokeWidth(1.8);
        g.getChildren().add(cuerpo);

        Polygon proa=new Polygon();
        proa.getPoints().addAll(
                40.0, 0.0,
                40.0, 18.0,
                54.0, 9.0
        );
        proa.setFill(Color.web("#475569"));
        proa.setStroke(Color.BLACK);
        proa.setStrokeWidth(1.5);
        g.getChildren().add(proa);

        javafx.scene.shape.Line lineaDecorativa=new javafx.scene.shape.Line();
        lineaDecorativa.setStartX(0);
        lineaDecorativa.setStartY(12);
        lineaDecorativa.setEndX(40);
        lineaDecorativa.setEndY(12);
        lineaDecorativa.setStroke(Color.web("#93C5FD"));
        lineaDecorativa.setStrokeWidth(2);
        g.getChildren().add(lineaDecorativa);

        Rectangle cabina=new Rectangle(14, 10);
        cabina.setFill(Color.web("#E5E7EB"));
        cabina.setStroke(Color.BLACK);
        cabina.setStrokeWidth(1.2);
        cabina.setTranslateX(6);
        cabina.setTranslateY(3);
        g.getChildren().add(cabina);

        Rectangle ventana=new Rectangle(6, 5);
        ventana.setFill(Color.web("#60A5FA"));
        ventana.setStroke(Color.BLACK);
        ventana.setStrokeWidth(1);
        ventana.setTranslateX(9);
        ventana.setTranslateY(5);
        g.getChildren().add(ventana);

        return g;
    }



    /* canva del barco destructr que tiene dos cuadrados de tamano*/
    private Group crearDestructor() {
        Group g=new Group();

        for (int i=0;i <tamaño;i++){
            Rectangle bloque=new Rectangle(40, 18);
            bloque.setArcWidth(8);
            bloque.setArcHeight(8);
            bloque.setFill(Color.web("#0f7a3a"));
            bloque.setStroke(Color.web("#063e1f"));
            bloque.setStrokeWidth(1.6);
            bloque.setTranslateX(i*45);
            g.getChildren().add(bloque);

            Rectangle franja=new Rectangle(36, 4);
            franja.setFill(Color.web("#53c18a"));
            franja.setStroke(Color.TRANSPARENT);
            franja.setTranslateX(i * 45 + 2);
            franja.setTranslateY(8);
            g.getChildren().add(franja);

            Rectangle ventana1=new Rectangle(6, 4);
            ventana1.setFill(Color.web("#a7f3d0"));
            ventana1.setStroke(Color.BLACK);
            ventana1.setTranslateX(i*45+10);
            ventana1.setTranslateY(6);
            g.getChildren().add(ventana1);

            Rectangle ventana2=new Rectangle(6, 4);
            ventana2.setFill(Color.web("#a7f3d0"));
            ventana2.setStroke(Color.BLACK);
            ventana2.setTranslateX(i * 45 + 24);
            ventana2.setTranslateY(6);
            g.getChildren().add(ventana2);
        }

        Rectangle torreta=new Rectangle(14, 12);
        torreta.setFill(Color.web("#111827")); // casi negro
        torreta.setStroke(Color.web("#000000"));
        torreta.setStrokeWidth(1.4);
        torreta.setTranslateX(8);   // dentro del primer bloque
        torreta.setTranslateY(-12); // encima del bloque
        g.getChildren().add(torreta);

        Rectangle canon=new Rectangle(18, 4);
        canon.setFill(Color.web("#0b5c2e"));
        canon.setStroke(Color.BLACK);
        canon.setTranslateX(40);
        canon.setTranslateY(-6);
        g.getChildren().add(canon);
        return g;
    }

    /* canva del submarino que tiene tres cuadrados de tamano*/
    private Group crearSubmarino(){
        Group g=new Group();

        for (int i=0;i<tamaño;i++) {

            Rectangle bloque=new Rectangle(40, 18);
            bloque.setArcWidth(10);
            bloque.setArcHeight(10);
            bloque.setFill(Color.web("#6b7280"));
            bloque.setStroke(Color.web("#374151"));
            bloque.setStrokeWidth(1.6);
            bloque.setTranslateX(i*45);
            g.getChildren().add(bloque);

            Rectangle franja=new Rectangle(36, 3);
            franja.setFill(Color.web("#9ca3af"));
            franja.setStroke(Color.TRANSPARENT);
            franja.setTranslateX(i*45+2);
            franja.setTranslateY(10);
            g.getChildren().add(franja);

            Rectangle ventana=new Rectangle(6, 6);
            ventana.setFill(Color.web("#dbeafe"));
            ventana.setStroke(Color.BLACK);
            ventana.setStrokeWidth(0.8);
            ventana.setTranslateX(i*45+16);
            ventana.setTranslateY(4);
            g.getChildren().add(ventana);
        }

        Rectangle torre=new Rectangle(10, 20);
        torre.setFill(Color.web("#4b5563")); // gris aún más oscuro
        torre.setStroke(Color.BLACK);
        torre.setStrokeWidth(1.4);
        torre.setTranslateX(45+14); // sobre el bloque central
        torre.setTranslateY(-18);
        g.getChildren().add(torre);

        Rectangle periscopioBase=new Rectangle(4, 14);
        periscopioBase.setFill(Color.web("#374151"));
        periscopioBase.setStroke(Color.BLACK);
        periscopioBase.setTranslateX(45+19);
        periscopioBase.setTranslateY(-30);
        g.getChildren().add(periscopioBase);

        Rectangle periscopioCabeza=new Rectangle(10, 4);
        periscopioCabeza.setFill(Color.web("#374151"));
        periscopioCabeza.setStroke(Color.BLACK);
        periscopioCabeza.setTranslateX(45+16);
        periscopioCabeza.setTranslateY(-34);
        g.getChildren().add(periscopioCabeza);
        return g;
    }

    /* canva del portaaviones que tiene cuatro cuadrados de tamano*/
    private Group crearPortaaviones(){
        Group g=new Group();
        int tamaño=4;

        for (int i=0;i<tamaño; i++){

            Rectangle bloque=new Rectangle(40, 20);
            bloque.setArcWidth(8);
            bloque.setArcHeight(8);
            bloque.setFill(Color.web("#6b7280"));
            bloque.setStroke(Color.web("#111827"));
            bloque.setStrokeWidth(1.7);
            bloque.setTranslateX(i * 45);
            g.getChildren().add(bloque);

            Rectangle franja=new Rectangle(36, 4);
            franja.setFill(Color.web("#9ca3af"));
            franja.setTranslateX(i*45+2);
            franja.setTranslateY(12);
            g.getChildren().add(franja);
        }

        Rectangle pista=new Rectangle( (40*tamaño)+(5 * (tamaño - 1)), 6);
        pista.setFill(Color.web("#facc15"));
        pista.setStroke(Color.BLACK);
        pista.setStrokeWidth(1.2);
        pista.setTranslateY(6);
        g.getChildren().add(pista);

        for (int i=0; i <tamaño; i++) {
            Rectangle linea =new Rectangle(8, 2);
            linea.setFill(Color.WHITE);
            linea.setTranslateX(i *45+16);
            linea.setTranslateY(8);
            g.getChildren().add(linea);
        }

        Rectangle torreBase=new Rectangle(20, 26);
        torreBase.setFill(Color.web("#4b5563"));
        torreBase.setStroke(Color.BLACK);
        torreBase.setStrokeWidth(1.6);
        torreBase.setTranslateX(45 *2 +10);
        torreBase.setTranslateY(-26);
        g.getChildren().add(torreBase);

        for (int y=0; y<3; y++){
            Rectangle ventana=new Rectangle(6, 6);
            ventana.setFill(Color.web("#dbeafe"));
            ventana.setStroke(Color.BLACK);
            ventana.setStrokeWidth(0.8);
            ventana.setTranslateX(45*2+14);
            ventana.setTranslateY(-22+(y*8));
            g.getChildren().add(ventana);
        }

        // Radar superior
        Rectangle radar =new Rectangle(14, 4);
        radar.setFill(Color.web("#9ca3af"));
        radar.setStroke(Color.BLACK);
        radar.setTranslateX(45 * 2 + 13);
        radar.setTranslateY(-30);
        g.getChildren().add(radar);

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



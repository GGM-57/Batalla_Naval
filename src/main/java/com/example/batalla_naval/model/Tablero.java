package com.example.batalla_naval.model;

public class Tablero {
    private int filas;
    private int columnas;
    private int[][] tablero; /*lee 0=vacio y 1=ocupado*/
    private Object navio;

    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.tablero = new int[filas][columnas];
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    /*metodo para verificar si un navio puede ser colocado ahí, sin que se
    * salga del tablero o sobreponga a otro*/
    public boolean puedeColocarse(Navio navio, int fila, int columna){
        int tamaño= navio.getTamaño();
        boolean vertical= navio.esVertical();

        if(vertical){
            /*Verifica límite vertical*/
            if(fila+tamaño > filas)
                return false; /*el navio se sale del tablero*/
            /*Verifica colisiones verticales*/
            for(int i=0; i<tamaño; i++){ /*bucle que recorre cada celda del tablero*/
                if(tablero[fila+i][columna]==1)
                    return false;/*coalision de navios*/
            }
        } else {
            /*Verifica límite horizontal*/
            if (columna + tamaño > columnas)
                return false;
            /*Verifica colisiones horizontales*/
            for (int i = 0; i < tamaño; i++) {
                if (tablero[fila][columna + i] == 1)
                    return false;
            }
        }

        return true;
    }

    /*metodo que coloca el navio en el tablero si es valido
    * retorna true si el navio se coloco correctamente*/
    public boolean colocarBarco(Navio navio, int fila, int columna) {
        if (!puedeColocarse(navio, fila, columna)) {
            return false;
            /*Llama a puedeColocarse(...) para validar la colocación.
            Si no puede colocarse (puedeColocarse devuelve false), el método
            retorna false y no modifica el tablero.*/
        }

        int tamaño=navio.getTamaño();

        if (navio.esVertical()) {
            for (int i=0; i<tamaño; i++) {
                tablero[fila+i][columna]=1;
            }
        } else {
            for (int i=0; i<tamaño; i++) {
                tablero[fila][columna+i]=1;
            }
        }

        navio.setPosicion(fila, columna);
        return true;
    }

    /*impresion del tablero en consola para revisiones*/
    public void imprimirTablero() {
        for (int i=0; i<filas; i++) {
            for (int j=0; j < columnas; j++) {
                System.out.print(tablero[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}

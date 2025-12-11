package com.example.batalla_naval.model;

import java.util.List;

public class Tablero {

    private final int filas;
    private final int columnas;
    private final Celda[][] grilla;

    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.grilla = new Celda[filas][columnas];

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                grilla[f][c] = new Celda(f, c);
            }
        }
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public Celda getCelda(int fila, int columna) {
        return grilla[fila][columna];
    }

    // Validar si se puede ubicar el barco
    public boolean puedeUbicarBarco(Barco barco, Coordenada inicio, Orientacion orientacion) {
        int fila = inicio.getFila();
        int columna = inicio.getColumna();
        int tamanio = barco.getTamanio();

        if (orientacion == Orientacion.VERTICAL) {
            if (fila + tamanio > filas) return false;
            for (int i = 0; i < tamanio; i++) {
                if (grilla[fila + i][columna].tieneBarco()) return false;
            }
        } else { // HORIZONTAL
            if (columna + tamanio > columnas) return false;
            for (int i = 0; i < tamanio; i++) {
                if (grilla[fila][columna + i].tieneBarco()) return false;
            }
        }
        return true;
    }

    // Colocar barco si es válido
    public boolean ubicarBarco(Barco barco, Coordenada inicio, Orientacion orientacion) {
        if (!puedeUbicarBarco(barco, inicio, orientacion)) {
            return false;
        }

        int fila = inicio.getFila();
        int columna = inicio.getColumna();
        int tamanio = barco.getTamanio();

        for (int i = 0; i < tamanio; i++) {
            int f = (orientacion == Orientacion.VERTICAL) ? fila + i : fila;
            int c = (orientacion == Orientacion.HORIZONTAL) ? columna + i : columna;

            grilla[f][c].setBarco(barco);
            barco.agregarCoordenada(new Coordenada(f, c));
        }

        barco.setPosicion(fila, columna);
        return true;
    }

    // Disparo a una coordenada
    public ResultadoDisparo recibirDisparo(Coordenada coord) {
        Celda celda = grilla[coord.getFila()][coord.getColumna()];

        if (celda.estaGolpeada()) {
            return ResultadoDisparo.AGUA;
        }

        celda.marcarGolpe();

        if (!celda.tieneBarco()) {
            return ResultadoDisparo.AGUA;
        }

        Barco barco = celda.getBarco();
        barco.registrarGolpe();

        if (barco.estaHundido()) {
            return ResultadoDisparo.HUNDIDO;
        } else {
            return ResultadoDisparo.TOCADO;
        }
    }

    public boolean todosBarcosHundidos(List<Barco> barcos) {
        for (Barco b : barcos) {
            if (!b.estaHundido()) return false;
        }
        return true;
    }

    // Solo para debug
    public void imprimirTableroDebug() {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                System.out.print(grilla[i][j].tieneBarco() ? "1 " : "0 ");
            }
            System.out.println();
        }
        System.out.println();
    }
}

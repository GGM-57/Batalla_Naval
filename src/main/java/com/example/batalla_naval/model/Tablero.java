package com.example.batalla_naval.model;

import java.util.List;
import java.io.Serializable;


public class Tablero implements Serializable {
    private static final long serialVersionUID = 1L;


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
    private boolean dentro(int f, int c) {
        return f >= 0 && f < filas && c >= 0 && c < columnas;
    }


    public boolean puedeUbicarBarco(Barco barco, Coordenada inicio, Orientacion orientacion) {
        int fila = inicio.getFila();
        int columna = inicio.getColumna();
        int tamanio = barco.getTamanio();

        for (int i = 0; i < tamanio; i++) {
            int f = (orientacion == Orientacion.VERTICAL) ? fila + i : fila;
            int c = (orientacion == Orientacion.HORIZONTAL) ? columna + i : columna;

            if (!dentro(f, c)) return false;
            if (grilla[f][c].tieneBarco()) return false;
        }

        for (int i = 0; i < tamanio; i++) {
            int f = (orientacion == Orientacion.VERTICAL) ? fila + i : fila;
            int c = (orientacion == Orientacion.HORIZONTAL) ? columna + i : columna;

            for (int df = -1; df <= 1; df++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int ff = f + df;
                    int cc = c + dc;

                    if (!dentro(ff, cc)) continue;

                    if (grilla[ff][cc].tieneBarco()) return false;
                }
            }
        }

        return true;
    }


    public boolean ubicarBarco(Barco barco, Coordenada inicio, Orientacion orientacion) {
        if (!puedeUbicarBarco(barco, inicio, orientacion)) {
            return false;
        }

        int fila = inicio.getFila();
        int columna = inicio.getColumna();
        int tamanio = barco.getTamanio();

        barco.getPosiciones().clear();


        for (int i = 0; i < tamanio; i++) {
            int f = (orientacion == Orientacion.VERTICAL) ? fila + i : fila;
            int c = (orientacion == Orientacion.HORIZONTAL) ? columna + i : columna;

            grilla[f][c].setBarco(barco);
            barco.agregarCoordenada(new Coordenada(f, c));
        }

        barco.setPosicion(fila, columna);
        barco.setOrientacion(orientacion);

        return true;
    }

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

    public Celda[][] getGrilla() {
        return grilla;
    }


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

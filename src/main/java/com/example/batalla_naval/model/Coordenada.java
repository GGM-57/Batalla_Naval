package com.example.batalla_naval.model;
import java.io.Serializable;

public class Coordenada implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int fila;
    private final int columna;

    public Coordenada(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}

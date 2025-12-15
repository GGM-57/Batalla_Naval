package com.example.batalla_naval.model;

import java.io.Serializable;

public class Celda implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int fila;
    private final int columna;
    private Barco barco;
    private boolean golpeada;

    public Celda(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.golpeada = false;
    }

    public boolean tieneBarco() {
        return barco != null;
    }

    public boolean estaGolpeada() {
        return golpeada;
    }

    public void marcarGolpe() {
        this.golpeada = true;
    }

    public Barco getBarco() {
        return barco;
    }

    public void setBarco(Barco barco) {
        this.barco = barco;
    }

    // opcional (no afecta a nadie)
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
}

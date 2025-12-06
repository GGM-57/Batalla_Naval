package com.example.batalla_naval.model;

public class Navio {
    private int tamaño;       // 1, 2, 3, 4
    private boolean vertical = true;
    private int fila;
    private int columna;
    private boolean posicionado = false;  /*ya está en el tablero?*/

    public Navio(int tamaño) {
        this.tamaño = tamaño;
    }

    public void rotar() {
        this.vertical = !this.vertical;
    }

    public int getTamaño() {
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

    public boolean EsPosicionado() {
        return posicionado;
    }

}

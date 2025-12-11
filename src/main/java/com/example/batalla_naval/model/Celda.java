package com.example.batalla_naval.model;

public class Celda {

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
}

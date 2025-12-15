package com.example.batalla_naval.persistence;

import com.example.batalla_naval.model.Barco;
import com.example.batalla_naval.model.ControlIA;
import com.example.batalla_naval.model.Tablero;

import java.io.Serializable;
import java.util.List;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    public Tablero tableroJugador;
    public Tablero tableroMaquina;

    public List<Barco> flotaJugador;
    public List<Barco> flotaMaquina;

    public ControlIA ia;

    public boolean turnoJugador;
    public boolean juegoTerminado;
    public boolean juegoIniciado;

    public int segundos;
    public boolean[][] disparosJugador;

}

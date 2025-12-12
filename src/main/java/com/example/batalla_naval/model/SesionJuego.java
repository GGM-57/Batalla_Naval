package com.example.batalla_naval.model;

public final class SesionJuego{
    private static String nombreJugador="Jugador";

    private SesionJuego(){}

    public static void setNombreJugador(String nombre){
        if (nombre==null||nombre.trim().isEmpty()){
            nombreJugador="Jugador";
        } else{
            nombreJugador=nombre.trim();
        }
    }

    public static String getNombreJugador() {
        return nombreJugador;
    }
}

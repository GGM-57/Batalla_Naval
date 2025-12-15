package com.example.batalla_naval.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Random;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;


public class ControlIA implements Serializable {
    private static final long serialVersionUID = 1L;


    private enum Modo { HUNT, TARGET }
    private enum Dir { NONE, H, V }

    private final int tam;
    private final boolean[][] disparos;
    private transient Random random = new Random();


    private final Deque<Coordenada> colaVecinos=new ArrayDeque<>();


    private final List<Coordenada> hits=new ArrayList<>();

    private Modo modo=Modo.HUNT;
    private Dir dir=Dir.NONE;


    private boolean probarExtremoA=true;

    public ControlIA(int tam) {
        this.tam=tam;
        this.disparos=new boolean[tam][tam];
    }


    public Coordenada elegirDisparo() {
        if (modo==Modo.TARGET) {

            if (dir==Dir.NONE) {
                while (!colaVecinos.isEmpty()) {
                    Coordenada c=colaVecinos.removeFirst();
                    if (esValidaYNoDisparada(c)) {
                        marcarDisparo(c);
                        return c;
                    }
                }

                resetTarget();
            } else {

                Coordenada c=siguienteEnLinea();
                if (c != null) {
                    marcarDisparo(c);
                    return c;
                }

                resetTarget();
            }
        }


        Coordenada c=elegirHuntConParidad();
        marcarDisparo(c);
        return c;
    }

    public void informarResultado(Tablero tablero, Coordenada tiro, ResultadoDisparo resultado){

        if (resultado==ResultadoDisparo.AGUA) {

            if (modo==Modo.TARGET&& dir != Dir.NONE) {
                probarExtremoA=!probarExtremoA;
            }
            return;
        }

        if (resultado==ResultadoDisparo.TOCADO) {
            if (modo==Modo.HUNT) {

                modo=Modo.TARGET;
                dir=Dir.NONE;
                hits.clear();
                colaVecinos.clear();
                probarExtremoA=true;

                hits.add(tiro);
                encolarVecinos(tiro);
                return;
            }


            hits.add(tiro);


            if (dir==Dir.NONE&& hits.size() >= 2) {
                Dir nueva=deducirDireccion();
                if (nueva != Dir.NONE) {
                    dir=nueva;
                    colaVecinos.clear();
                    probarExtremoA=true;
                }
            }

            return;
        }

        if (resultado==ResultadoDisparo.HUNDIDO) {
            bloquearHaloDeBarcoHundido(tablero, tiro);
            resetTarget();
        }
    }


    private void encolarVecinos(Coordenada hit) {
        int f=hit.getFila();
        int c=hit.getColumna();

        agregarSiValido(f - 1, c);
        agregarSiValido(f + 1, c);
        agregarSiValido(f, c - 1);
        agregarSiValido(f, c + 1);
    }

    private Dir deducirDireccion() {

        Coordenada a=hits.get(0);
        Coordenada b=hits.get(1);

        if (a.getFila()==b.getFila()) return Dir.H;
        if (a.getColumna()==b.getColumna()) return Dir.V;
        return Dir.NONE;
    }

    private Coordenada siguienteEnLinea() {
        if (hits.isEmpty()) return null;

        if (dir==Dir.H) {
            hits.sort(Comparator.comparingInt(Coordenada::getColumna));
                Coordenada min=hits.get(0);
                Coordenada max=hits.get(hits.size() - 1);

                Coordenada extremoA=new Coordenada(min.getFila(), min.getColumna() - 1);
                Coordenada extremoB=new Coordenada(max.getFila(), max.getColumna() + 1);

            return elegirEntreExtremos(extremoA, extremoB);

        } else if (dir==Dir.V) {
            hits.sort(Comparator.comparingInt(Coordenada::getFila));
                Coordenada min=hits.get(0);
                Coordenada max=hits.get(hits.size() - 1);

            Coordenada extremoA=new Coordenada(min.getFila() - 1, min.getColumna());
            Coordenada extremoB=new Coordenada(max.getFila() + 1, max.getColumna());

            return elegirEntreExtremos(extremoA, extremoB);
        }

        return null;
    }

    private Coordenada elegirEntreExtremos(Coordenada a, Coordenada b) {
        if (probarExtremoA) {
            if (esValidaYNoDisparada(a)) return a;
            probarExtremoA=false;
            if (esValidaYNoDisparada(b)) return b;
        } else {
            if (esValidaYNoDisparada(b)) return b;
            probarExtremoA=true;
            if (esValidaYNoDisparada(a)) return a;
        }
        return null;
    }

    private void resetTarget() {
        modo=Modo.HUNT;
        dir=Dir.NONE;
        hits.clear();
        colaVecinos.clear();
        probarExtremoA=true;
    }


    private Coordenada elegirHuntConParidad() {
        List<Coordenada> candidatos=new ArrayList<>();
        for (int f=0; f < tam; f++) {
            for (int c=0; c < tam; c++) {
                if (!disparos[f][c]&& ((f + c) % 2==0)) {
                    candidatos.add(new Coordenada(f, c));
                }
            }
        }
        if (!candidatos.isEmpty()) {
            return candidatos.get(random.nextInt(candidatos.size()));
        }


        List<Coordenada> libres=new ArrayList<>();
        for (int f=0; f < tam; f++) {
            for (int c=0; c < tam; c++) {
                if (!disparos[f][c]) libres.add(new Coordenada(f, c));
            }
        }
        return libres.get(random.nextInt(libres.size()));
    }


    private boolean dentro(int f, int c) {
        return f >=0&& f < tam && c >= 0&& c < tam;
    }

    private boolean esValidaYNoDisparada(Coordenada c) {
        int f=c.getFila();
        int col=c.getColumna();
        return dentro(f, col)  && !disparos[f][col];
    }

    private void marcarDisparo(Coordenada c) {
        disparos[c.getFila()][c.getColumna()]=true;
    }

    private void agregarSiValido(int f, int c) {
        if (!dentro(f, c)) return;
        if (disparos[f][c]) return;
        colaVecinos.addLast(new Coordenada(f, c));
    }

    private void bloquearHaloDeBarcoHundido(Tablero tablero, Coordenada tiro) {
        Celda celda = tablero.getCelda(tiro.getFila(), tiro.getColumna());
        if (!celda.tieneBarco()) return;

        Barco barco = celda.getBarco();


        for (int f = 0; f < tablero.getFilas(); f++) {
            for (int c = 0; c < tablero.getColumnas(); c++) {
                Celda x = tablero.getCelda(f, c);
                if (x.tieneBarco() && x.getBarco() == barco) {


                    for (int df = -1; df<= 1; df++) {
                        for (int dc = -1; dc<= 1; dc++) {
                             int ff = f + df;
                            int cc = c + dc;
                              if (ff < 0||ff >= tam||cc < 0||cc>= tam) continue;


                            disparos[ff][cc] = true;
                        }
                    }
                }
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.random = new Random();
    }


}

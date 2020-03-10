package de.ostfalia.prog.ss20.felder;

import de.ostfalia.prog.ss20.figuren.Schlumpf;

import java.util.List;

public class Feld {
    List<Feld> nachbarListe;
    List<Schlumpf> figurListe;
    int nummer;

    public Feld(int nummer, List<Feld> nachbarListe, List<Schlumpf> figurListe) {
        this.nummer=nummer;
        this.nachbarListe = nachbarListe;
        this.figurListe = figurListe;
    }



    //wenn >1 figur auf feld, rufe NPC.wirken auf
}

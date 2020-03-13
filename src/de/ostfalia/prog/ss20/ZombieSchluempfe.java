package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.enums.Richtung;
import de.ostfalia.prog.ss20.felder.Feld;
import de.ostfalia.prog.ss20.figuren.Figur;
import de.ostfalia.prog.ss20.figuren.Schlumpf;
import de.ostfalia.prog.ss20.interfaces.IZombieSchluempfe;

import java.util.ArrayList;
import java.util.List;

public class ZombieSchluempfe implements IZombieSchluempfe {

    private List<Feld> feldListe = new ArrayList<>();
    private List<Spieler> spielerListe = new ArrayList<>();

    public void initialisieren(){
        for(int x = 36; x > 0; x++) {
            if(x == 1){

            }
            feldListe.add(new Feld());
        }
    }

    public List<Spieler> getSpielerListe() {
        return spielerListe;
    }

    public static int wuerfeln() {
        return (int) (Math.random() * 6) + 1;
    }


    public ZombieSchluempfe(Farbe... farben) {
        //je nachdem wie viele Farben, so viele Spieler


        for (Farbe farbe : farben) {
            spielerListe.add(new Spieler(farbe));
        }

    }


    public ZombieSchluempfe(String conf, Farbe... farben) {
        /*
        conf aufspalten
        figuren erstellen
        bzzt erstellen
        doc erstellen
         */
    }


    @Override
    public boolean bewegeFigur(String figurName, int augenzahl, Richtung richtung) {
        return false;
    }

    @Override
    public boolean bewegeFigur(String figurName, int augenzahl) {
        return false;
    }

    @Override
    public int getFeldnummer(String figurName) {
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().equals(figurName)) {
                    return schlumpf.getAktuellesFeld();
                }
            }
        }
        return -1; // TODO: 10.03.2020 muss abgefangen werden
    }

    @Override
    public boolean istZombie(String figurName) {
        return false;
    }

    @Override
    public Farbe getFarbeAmZug() {
        return null;
    }

    @Override
    public Farbe gewinner() {
        return null;
    }
}

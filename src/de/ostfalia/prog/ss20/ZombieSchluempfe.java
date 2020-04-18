package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.enums.Richtung;
import de.ostfalia.prog.ss20.felder.*;
import de.ostfalia.prog.ss20.figuren.Doc;
import de.ostfalia.prog.ss20.figuren.Figur;
import de.ostfalia.prog.ss20.figuren.Fliege;
import de.ostfalia.prog.ss20.figuren.Schlumpf;
import de.ostfalia.prog.ss20.interfaces.IZombieSchluempfe;

import java.util.ArrayList;
import java.util.List;

public class ZombieSchluempfe implements IZombieSchluempfe {

    private List<Feld> feldListe = new ArrayList<>();
    private List<Spieler> spielerListe = new ArrayList<>();
    Fliege fliege;
    Doc doc;

    public void initialisieren() {


        List<Feld> felder = new ArrayList<>();
        List<Feld> nachbarFelder = new ArrayList<>();

        for (int x = 36; x >= 0; x--) {
            // Felder erstellen (Standard-Konstruktor, ohne Werte, außer Feld-Nummer) (Aufpassen, welche Felder wichtig sind)
            // Nach Erstellung: Hinzufügen der NAchbarfelder (tw. HardCode, tw. Vereinfachen)
            // Start-Feld hinzufügen

            switch (x) {
                case 0:
                    //Startfeld
                    felder.add(new Startfeld(x));
                    break;
                case 11:
                    //Tuberose
                    felder.add(new Spezialfeld(x));
                    break;
                case 16:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 17:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 25:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 26:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 27:
                    //Fluss
                    felder.add(new Spezialfeld(x));
                    break;
                case 24:
                    //Pilz
                    felder.add(new Spezialfeld(x));
                    break;
                case 29:
                    //Docs Labor
                    felder.add(new Spezialfeld(x));
                    break;
                case 36:
                    //Dorf
                    felder.add(new Zielfeld(x));
                    break;
                default:
                    felder.add(new Normalfeld(x));
                    break;
            }
        }

        for (Feld feld : felder) {
            switch (feld.getNummer()) {
                case 3:
                    for (Feld feld2 : felder) {
                        if (feld2.getNummer() == 4 || feld2.getNummer() == 8) {
                            feld.getNachbarListe().add(feld2);
                        }
                    }
                    feld.setIstAbzweigung(true);
                    break;
                case 7:
                    for (Feld feld2 : felder) {
                        if (feld2.getNummer() == 15) {
                            feld.getNachbarListe().add(feld2);
                        }
                    }
                    break;
                case 14:
                    for (Feld feld2 : felder) {
                        if (feld2.getNummer() == 15) {
                            feld.getNachbarListe().add(feld2);
                        }
                    }
                    break;
                case 31:
                    for (Feld feld2 : felder) {
                        if (feld2.getNummer() == 32 || feld2.getNummer() == 36) {
                            feld.getNachbarListe().add(feld2);
                        }
                    }
                    feld.setIstAbzweigung(true);
                    break;
                case 35:
                    for (Feld feld2 : felder) {
                        if (feld2.getNummer() == 1) {
                            feld.getNachbarListe().add(feld2);
                        }
                    }
                    break;
                default:
                    for (Feld feld2 : felder) {
                        if (feld2.getNummer() == feld.getNummer() + 1) {
                            feld.getNachbarListe().add(feld2);
                        }
                    }
                    break;
            }
        }

        for (Feld feld : felder) {
            //System.out.print("\nFeld " + feld.getNummer() + " : ");
            for (Feld nachbar : feld.getNachbarListe()) {
                //System.out.print(nachbar.getNummer() + " ");
            }
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

        doc = new Doc("Doc", 29);
        fliege = new Fliege("Fliege", 20);

        initialisieren();
    }


    public ZombieSchluempfe(String conf, Farbe... farben) {
        for (Farbe farbe : farben) {
            spielerListe.add(new Spieler(farbe));
        }

        doc = new Doc("Doc", 29);
        fliege = new Fliege("Fliege", 20);

        //conf aufspalten:
        String[] confs = conf.split(", ");
        for(String config : confs){
            String configName = config.substring(0,config.indexOf(":"));
            int feld = Integer.parseInt(config.substring(config.indexOf(":") + 1));

            for(Spieler spieler : spielerListe){
                for(Schlumpf schlumpf : spieler.getSchlumpfListe()){
                    if(schlumpf.getName().contentEquals(configName)) {
                        schlumpf.setAktuellesFeld(feld);
                        //System.out.println(schlumpf.getName() + ": " + schlumpf.getAktuellesFeld());
                    }
                }
            }
        }
        initialisieren();
    }


    @Override
    public boolean bewegeFigur(String figurName, int augenzahl, Richtung richtung) {

        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().equals(figurName)) {


                    return true;
                }
            }
        }
        // spieler rausfinden über figurname(ROT/ BLAU/...)
        //spieler.getschlupflist
        //Figur finden

        //feld addieren  je nach richtung + setzen
        return false;
    }

    @Override
    public boolean bewegeFigur(String figurName, int augenzahl) {
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().equals(figurName)) {
                    for (int i = 1; i <= augenzahl; i++) {
                        schlumpf.setAktuellesFeld(schlumpf.getAktuellesFeld() + i); //zieht feld für feld

                        //pro feld statusveränderungen anpassen:
                        if (schlumpf.getAktuellesFeld() == fliege.getFliegeAktuellesFeld()) {
                            schlumpf.setIstZombie(true);
                        }
                        if (schlumpf.getAktuellesFeld() == doc.getAktuellesFeld()) {
                            schlumpf.setIstZombie(false);
                        }
                    }
                    return true;
                }
            }
        }
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
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().equals(figurName)) {
                    return schlumpf.isIstZombie();
                }
            }
        }
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

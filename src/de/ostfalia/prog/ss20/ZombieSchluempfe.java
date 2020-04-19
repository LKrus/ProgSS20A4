package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.enums.Richtung;
import de.ostfalia.prog.ss20.felder.*;
import de.ostfalia.prog.ss20.figuren.Doc;
import de.ostfalia.prog.ss20.figuren.Fliege;
import de.ostfalia.prog.ss20.figuren.Schlumpf;
import de.ostfalia.prog.ss20.interfaces.IZombieSchluempfe;

import java.util.ArrayList;
import java.util.List;

public class ZombieSchluempfe implements IZombieSchluempfe {

    private List<Feld> feldListe = new ArrayList<>();
    private List<Spieler> spielerListe = new ArrayList<>();
    private Spieler spielerAmZug;
    Fliege fliege;
    Doc doc;
    Zielfeld zielfeld;

    public void initialisieren() {
        List<Feld> nachbarFelder = new ArrayList<>();
        zielfeld = new Zielfeld(36);
        List<Feld> felder = felderGenerieren();

        //Startspieler bestimmen
        spielerAmZug = spielerListe.get(0);
    }

    public List<Feld> felderGenerieren(){
        List<Feld> felder = new ArrayList<>();
        for (int x = 36; x >= 0; x--) {
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
        return felder;
    }

    public void zugBeenden(){
        int newIndex = spielerListe.indexOf(spielerAmZug) + 1;
        if(newIndex >= spielerListe.size()){
            newIndex = 0;
        }
        spielerAmZug = spielerListe.get(newIndex);
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
        for (String config : confs) {
            String configName = config.substring(0, config.indexOf(":"));
            int feld;
            boolean istZombie;
            if(config.contains(":Z")){
                feld = Integer.parseInt(config.substring(config.indexOf(":") + 1, config.indexOf(":Z")));
                istZombie = true;
            } else {
                feld = Integer.parseInt(config.substring(config.indexOf(":") + 1));
                istZombie = false;
            }

            for (Spieler spieler : spielerListe) {
                for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                    if (schlumpf.getName().contentEquals(configName)) {
                        schlumpf.setAktuellesFeld(feld);
                        schlumpf.setIstZombie(istZombie);
                    }
                }
            }
            if (configName.contentEquals("Bzz")) {
                fliege.setAktuellesFeld(feld);
            } else if (configName.contentEquals("Doc")) {
                doc.setAktuellesFeld(feld);
            }
        }
        initialisieren();
    }


    @Override
    public boolean bewegeFigur(String figurName, int augenzahl, Richtung richtung) {
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().equals(figurName)) {
                    for (int i = 1; i <= augenzahl; i++) {

                        //überprüfen ob abzweigung
                        if (schlumpf.getAktuellesFeld() == 3 || schlumpf.getAktuellesFeld() == 31) {
                            if (richtung == Richtung.WEITER) {
                                schlumpf.setAktuellesFeld(schlumpf.getAktuellesFeld() + i);
                            } else if (schlumpf.getAktuellesFeld() == 3) {
                                schlumpf.setAktuellesFeld(8);
                            } else if (schlumpf.getAktuellesFeld() == 31) {
                                schlumpf.setAktuellesFeld(36);
                            }

                        } else if (schlumpf.getAktuellesFeld() == 7) {
                            schlumpf.setAktuellesFeld(15);
                        } else if (schlumpf.getAktuellesFeld() == 35) {
                            schlumpf.setAktuellesFeld(1);
                        } else {
                            schlumpf.setAktuellesFeld(schlumpf.getAktuellesFeld() + i); //zieht feld für feld
                        }

                        //wenn schlumpf im ziel ist, ist zug beendet:
                        if (schlumpf.getAktuellesFeld() >= 36) { // >= statt == falls irgendwas schief läuft
                            zielfeld.addToZielListe(schlumpf);
                            return true;
                        }

                        //pro feld statusveränderungen anpassen:
                        if (schlumpf.getAktuellesFeld() == fliege.getAktuellesFeld()) {
                            schlumpf.setIstZombie(true);
                            System.out.println(schlumpf.getName()+" ist Zombie");
                        }
                        if (schlumpf.getAktuellesFeld() == doc.getAktuellesFeld()) {
                            schlumpf.setIstZombie(false);
                            System.out.println(schlumpf.getName()+" ist kein Zombie");
                        }
                    }
                    zugBeenden();
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public boolean bewegeFigur(String figurName, int augenzahl) {
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().equals(figurName)) {
                    for (int i = 1; i <= augenzahl; i++) {
                        /*
                            Bei keiner Eingabe soll der Schlumpf gerade weiterlaufen.
                         */
                        if (schlumpf.getAktuellesFeld() == 35) {
                            schlumpf.setAktuellesFeld(1);
                        } else if (schlumpf.getAktuellesFeld() == 7) {
                            schlumpf.setAktuellesFeld(15);
                        } else {
                            schlumpf.setAktuellesFeld(schlumpf.getAktuellesFeld() + 1); //zieht feld für feld
                        }

                        //wenn schlumpf im ziel ist, ist zug beendet:
                        if (schlumpf.getAktuellesFeld() >= 36) { // >= statt == falls irgendwas schief läuft
                            zielfeld.addToZielListe(schlumpf);
                            return true;
                        }

                        //pro feld statusveränderungen anpassen:
                        if (schlumpf.getAktuellesFeld() == fliege.getAktuellesFeld()) {
                            schlumpf.setIstZombie(true);
                            System.out.println(schlumpf.getName()+" ist Zombie");
                        }
                        if (schlumpf.getAktuellesFeld() == doc.getAktuellesFeld()) {
                            schlumpf.setIstZombie(false);
                            System.out.println(schlumpf.getName()+" ist kein Zombie");
                        }
                    }
                    zugBeenden();
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
        if (figurName.contentEquals("Bzz")) {
            return fliege.getAktuellesFeld();
        } else if (figurName.contentEquals("Doc")) {
            return doc.getAktuellesFeld();
        }
        return -1;
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
        if (figurName.contentEquals("Bzz")) {
            return fliege.getIstZombie();
        } else if (figurName.contentEquals("Doc")) {
            return doc.getIstZombie();
        }
        return false;
    }

    @Override
    public Farbe getFarbeAmZug() {
        if(spielerAmZug != null){
            return spielerAmZug.getFarbe();
        }
        return null;
    }

    @Override
    public Farbe gewinner() {
        int counter=0;
        for (Spieler spieler: spielerListe) { //für jeden spieler die liste durchsuchen
            for (Schlumpf schlumpf:spieler.getSchlumpfListe()) {
                if (schlumpf.getAktuellesFeld()==36){
                    counter++;
                }
            }
            if (counter==4){ //spieler hat 4 schlümpfe im ziel, hat gewonnen
                return spieler.getSpielerFarbe();
            }
            counter=0;
        }
        return null;
    }

    @Override
    public String toString(){
        String spielStatus = "Spielstatus: ";
        for(Spieler spieler : spielerListe){
            for(Schlumpf schlumpf : spieler.getSchlumpfListe()){
                spielStatus = spielStatus.concat(schlumpf.getName() + ":" + schlumpf.getAktuellesFeld());
                if(schlumpf.isIstZombie()){
                    spielStatus = spielStatus.concat(":Z");
                }
                spielStatus = spielStatus.concat(", ");
            }
        }
        //Bzz
        spielStatus = spielStatus.concat("Bzz:" + fliege.getAktuellesFeld());
        if(fliege.getIstZombie()){
            spielStatus = spielStatus.concat(":Z");
        }
        spielStatus = spielStatus.concat(", ");

        //Doc
        spielStatus = spielStatus.concat("Doc:" + doc.getAktuellesFeld());
        if(doc.getIstZombie()) {
            spielStatus = spielStatus.concat(":Z");
        }
        return spielStatus;
    }
}

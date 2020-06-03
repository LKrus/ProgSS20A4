package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.enums.Richtung;
import de.ostfalia.prog.ss20.exceptions.FalscheSpielerzahlException;
import de.ostfalia.prog.ss20.exceptions.UngueltigePositionException;
import de.ostfalia.prog.ss20.exceptions.WiederholteFarbenException;
import de.ostfalia.prog.ss20.felder.Feld;
import de.ostfalia.prog.ss20.felder.Normalfeld;
import de.ostfalia.prog.ss20.felder.Spezialfeld;
import de.ostfalia.prog.ss20.felder.Startfeld;
import de.ostfalia.prog.ss20.felder.Zielfeld;
import de.ostfalia.prog.ss20.figuren.Doc;
import de.ostfalia.prog.ss20.figuren.Fliege;
import de.ostfalia.prog.ss20.figuren.Schlumpf;
import de.ostfalia.prog.ss20.figuren.Schlumpfine;
import de.ostfalia.prog.ss20.interfaces.IZombieSchluempfe;

import java.util.ArrayList;
import java.util.List;

public class ZombieSchluempfe implements IZombieSchluempfe {

    private List<Feld> feldListe = new ArrayList<>();
    private List<Spieler> spielerListe = new ArrayList<>();
    private List<Schlumpf> zombieSchluempfe = new ArrayList<>();
    private Spieler spielerAmZug;
    private int[] flussFeldNummern = {16, 17, 25, 26, 27};
    Fliege fliege;
    Doc doc;
    Zielfeld zielfeld;
    boolean istBlauImSpiel = false, istRotImSpiel = false, istGelbImSpiel = false, istGruenImSpiel = false;
    Schlumpfine schlumpfine;
    int gelbFiguren = 0;
    int rotFiguren = 0;
    int blauFiguren = 0;
    int gruenFiguren = 0;


    public void initialisieren() {
        zielfeld = new Zielfeld(36);
        feldListe = felderGenerieren();

        //Startspieler bestimmen
        spielerAmZug = spielerListe.get(0);
    }

    public List<Feld> felderGenerieren() {
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

    public void zugBeenden() {
        int newIndex = spielerListe.indexOf(spielerAmZug) + 1;
        if (newIndex >= spielerListe.size()) {
            newIndex = 0;
        }
        spielerAmZug = spielerListe.get(newIndex);
    }

    public static int wuerfeln() {
        return (int) (Math.random() * 7) + 1;
    }

    public ZombieSchluempfe(Farbe... farben) throws FalscheSpielerzahlException, WiederholteFarbenException{
        //je nachdem wie viele Farben, so viele Spieler
        spielerHinzufuegen(farben);

        doc = new Doc("Doc", 29);
        fliege = new Fliege("Bzz", 20);
        schlumpfine = new Schlumpfine("Schlumpfine", 1);

        testeValidePositionierung();

        initialisieren();
    }

    private void testeValidePositionierung() {
        if (doc.getAktuellesFeld() == fliege.getAktuellesFeld()) {
            throw new UngueltigePositionException("Fliege und Doc können nicht auf dem selben Feld beginnen.");
        }else if (fliege.getAktuellesFeld() == 11) {
            throw new UngueltigePositionException("Fliege kann nicht auf der Tuberose(Feld 11) landen.");
        }else if (fliege.getAktuellesFeld() == 0 || fliege.getAktuellesFeld() == 36) {
            throw new UngueltigePositionException("Fliege kann weder auf dem Start- noch auf dem Zielfeld des Spiels starten.");
        }else if (fliege.getAktuellesFeld() < 0 || fliege.getAktuellesFeld() > 36) {
            throw new UngueltigePositionException("Fliege nicht auf Spielbrett.");
        }else if (doc.getAktuellesFeld() < 0 || doc.getAktuellesFeld() > 36) {
            throw new UngueltigePositionException("Oberschlumpf nicht auf Spielbrett.");
        }

        testeValidePositionSchluempfe();
    }

    private void testeValidePositionSchluempfe(){
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                for (int n : flussFeldNummern) {
                    if (schlumpf.getAktuellesFeld() == n) {
                        throw new UngueltigePositionException("Schlumpf auf Flussfeld.");
                    }
                }
                if (schlumpf.getAktuellesFeld() < 0 || schlumpf.getAktuellesFeld() > 36) {
                    throw new UngueltigePositionException("Schlumpf nicht auf Spielbrett.");
                }
            }
        }
    }

    public ZombieSchluempfe(String conf, Farbe... farben) throws WiederholteFarbenException, FalscheSpielerzahlException{
        spielerHinzufuegen(farben);


        doc = new Doc("Doc", 29);
        fliege = new Fliege("Bzz", 20);
        schlumpfine = new Schlumpfine("Schlumpfine", 1);

        //conf aufspalten:
        configHandling(conf.split(", "));

        testeValidePositionierung();

        initialisieren();
    }

    private void configHandling(String[] confs) throws WiederholteFarbenException{
        blauFiguren = 0;
        rotFiguren = 0;
        gelbFiguren = 0;
        gruenFiguren = 0;
        for (String config : confs) {
            String configName = config.substring(0, config.indexOf(":"));
            int feld;
            boolean istZombie;
            if (config.contains(":Z")) {
                feld = Integer.parseInt(config.substring(config.indexOf(":") + 1, config.indexOf(":Z")));
                istZombie = true;
            } else {
                feld = Integer.parseInt(config.substring(config.indexOf(":") + 1));
                istZombie = false;
            }
            if(configName.contains("GELB") ||
                    configName.contains("ROT") ||
                    configName.contains("GRUEN") ||
                    configName.contains("BLAU")) {
                schlumpfConfigHandling(configName, feld, istZombie);
            }
            else if (configName.contentEquals("Bzz")) {
                fliege.setAktuellesFeld(feld);
            } else if (configName.contentEquals("Doc")) {
                doc.setAktuellesFeld(feld);
            } else if (configName.contentEquals("Schlumpfine")){
                schlumpfine.setAktuellesFeld(feld);
            }
            checkWiederholteFarben(configName);
        }
    }

    private void checkWiederholteFarben(String configName) throws WiederholteFarbenException{
        if(configName.contains("GELB")){
            gelbFiguren++;
        } else if(configName.contains("ROT")){
            rotFiguren++;
        } else if(configName.contains("BLAU")){
            blauFiguren++;
        } else if(configName.contains("GRUEN")){
            gruenFiguren++;
        }
        if(gelbFiguren > 4 || rotFiguren > 4 || blauFiguren > 4 || gruenFiguren > 4){
            throw new WiederholteFarbenException("Mehr als 4 FIguren einer Farbe.");
        }
    }

    private void schlumpfConfigHandling(String configName, int feld, boolean istZombie) {
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().contentEquals(configName)) {
                    schlumpf.setAktuellesFeld(feld);
                    schlumpf.setIstZombie(istZombie);
                    if (istZombie) {
                        zombieSchluempfe.add(schlumpf);
                    }
                }
            }
        }
    }

    @Override
    public boolean bewegeFigur(String figurName, int augenzahl, Richtung richtung) {

        if (gewinner() == null) {
            if (figurName.equals(fliege.getName())) {
                fliegeBewegung(augenzahl, richtung);
                zugBeenden();
                return true;
            } else if (figurName.equals(schlumpfine.getName())) {
                schlumpfineBewegung(augenzahl, richtung);
                zugBeenden();
                return true;
            } else {
                return bewegeFigurSchlumpf(figurName, augenzahl, richtung);
            }
        }
        return false;
    }

    private boolean bewegeFigurSchlumpf(String figurName, int augenzahl, Richtung richtung){
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getName().equals(figurName)) {
                    int ursprungsFeld = schlumpf.getAktuellesFeld();
                    boolean ursprungsObIstZombie = schlumpf.isIstZombie();

                    if (schlumpf.getAktuellesFeld() == 36) {
//                        System.out.println("Schlumpf " + figurName + " ist bereits im Dorf und kann nicht mehr bewegt werden.");
                        throw new UngueltigePositionException("Schlumpf " + figurName + " ist bereits im Dorf und kann nicht mehr bewegt werden.");
                    }
                    for (int i = 1; i <= augenzahl; i++) {
                        //überprüfen ob abzweigung
                        schlumpfZiehtFeld(schlumpf, richtung);

                        //wenn schlumpf im ziel ist, ist zug beendet:
                        if (!schlumpf.isIstZombie() && schlumpf.getAktuellesFeld() == 36) {
                            zielfeld.addToZielListe(schlumpf);
                            System.out.println(figurName + " ist nun im Ziel.");
                            zugBeenden();
                            return true;
                        }
                        //zombieschlumpf darf nicht ins ziel
                        else if (schlumpf.isIstZombie() && schlumpf.getAktuellesFeld() == 36) {
                            schlumpf.setAktuellesFeld(ursprungsFeld);
                            schlumpf.setIstZombie(ursprungsObIstZombie);
                            throw new UngueltigePositionException("Ein Zombieschlumpf darf nicht ins Dorf.");
                        }

                        //gucken ob letztes feld flussfeld ist
                        if (i == augenzahl && (schlumpf.getAktuellesFeld() == 16 || schlumpf.getAktuellesFeld() == 17
                                || schlumpf.getAktuellesFeld() == 25 || schlumpf.getAktuellesFeld() == 26
                                || schlumpf.getAktuellesFeld() == 27)) {
                            schlumpf.setAktuellesFeld(ursprungsFeld);
                            schlumpf.setIstZombie(ursprungsObIstZombie);
                            throw new UngueltigePositionException("Der Schlumpf kann nicht auf einem Flussfeld stehen bleiben.");
                        }
                        //wenn letztes feld pilzfeld ist
                        if (i == augenzahl && schlumpf.getAktuellesFeld() == 24 && schlumpf.isIstZombie()) {
                            System.out.println("Schlumpf " + figurName + " wird vom Pilzfeld geheilt. Er ist nun kein Zombie mehr.");
                            schlumpf.setIstZombie(false);
                            zombieSchluempfe.remove(schlumpf);
                            schlumpf.setAktuellesFeld(0);
                            System.out.println(figurName + " ist nun auf Feld " + schlumpf.getAktuellesFeld() + ".");
                            zugBeenden();
                            return true;
                        }

                        //pro feld statusveränderungen anpassen:
                        statusVeraenderungen(schlumpf, figurName);
                    }
                    zugBeenden();
                    return true;
                }
            }
        }
        return false;
    }

    private void schlumpfZiehtFeld(Schlumpf schlumpf, Richtung richtung) {
        if (schlumpf.getAktuellesFeld() == 3 || schlumpf.getAktuellesFeld() == 31) {
            if (richtung == Richtung.WEITER) {
                schlumpf.setAktuellesFeld(schlumpf.getAktuellesFeld() + 1);
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
            schlumpf.setAktuellesFeld(schlumpf.getAktuellesFeld() + 1); //zieht feld für feld
        }
    }

    private void statusVeraenderungen(Schlumpf schlumpf, String figurName) {
        if (schlumpf.getAktuellesFeld() == 11 && schlumpf.isIstZombie()) {
            schlumpf.setIstZombie(false);
            zombieSchluempfe.remove(schlumpf);
            System.out.println("Das Blütenstaubfeld heilt " + figurName + ". Er ist nun kein Zombie mehr.");
        }
        if (schlumpf.getAktuellesFeld() == doc.getAktuellesFeld() && schlumpf.isIstZombie()) {
            schlumpf.setIstZombie(false);
            zombieSchluempfe.remove(schlumpf);
            System.out.println("Doc heilt Schlumpf " + figurName + ". Er ist nun kein Zombie mehr.");
        }
        if (schlumpf.getAktuellesFeld() == fliege.getAktuellesFeld()
                && !schlumpf.isIstZombie()
                && schlumpf.getAktuellesFeld() != 24) {
            schlumpf.setIstZombie(true);
            zombieSchluempfe.add(schlumpf);
            System.out.println("Die Fliege beißt Schlumpf " + figurName + ". Er ist nun ein Zombie.");
        }
        if (schlumpf.getAktuellesFeld() == schlumpfine.getAktuellesFeld()
                && schlumpf.isIstZombie()
                && schlumpf.getAktuellesFeld() != 24) {
            schlumpf.setIstZombie(false);
            zombieSchluempfe.remove(schlumpf);
            System.out.println("Die Schlumpfine heilt Schlumpf " + figurName + ". Er ist nun kein Zombie mehr.");
        }
        for (Schlumpf schlumpf2 : zombieSchluempfe) {
            if (schlumpf.getAktuellesFeld() == schlumpf2.getAktuellesFeld()) {
                schlumpf.setIstZombie(true);
            }
        }
        System.out.println(figurName + " ist nun auf Feld " + schlumpf.getAktuellesFeld() + ".");
    }

    public void fliegeBewegung(int augenzahl, Richtung richtung) {
        int ursprungsfeld = fliege.getAktuellesFeld();
        //überprüfen ob Abzweigung
        for (int i = 1; i <= augenzahl; i++) {
            //überprüfen ob abzweigung
            if (fliege.getAktuellesFeld() == 3||fliege.getAktuellesFeld()==31) {
                if (richtung == Richtung.WEITER) {
                    fliege.setAktuellesFeld(fliege.getAktuellesFeld() + 1);
                } else if (fliege.getAktuellesFeld()==3){
                    fliege.setAktuellesFeld(8);
                }else if (fliege.getAktuellesFeld()==31){
                    fliege.setAktuellesFeld(ursprungsfeld);
                    throw new UngueltigePositionException("Die Fliege darf nicht ins Dorf.");
                }

            } else if (fliege.getAktuellesFeld() == 7) {
                fliege.setAktuellesFeld(15);
            } else if (fliege.getAktuellesFeld() == 35) {
                fliege.setAktuellesFeld(1);
            } else {
                fliege.setAktuellesFeld(fliege.getAktuellesFeld() + 1); //zieht feld für feld
            }
        }

        fliegeUngueltigeEndposition(ursprungsfeld);
        System.out.println(fliege.getName() + " ist nun auf Feld " + fliege.getAktuellesFeld() + ".");
    }

    private void fliegeUngueltigeEndposition(int ursprungsfeld) {
        if (fliege.getAktuellesFeld() == doc.getAktuellesFeld()) {
            fliege.setAktuellesFeld(ursprungsfeld);
            throw new UngueltigePositionException("Bzz bleibt nicht auf Docs Labor.");
        }else if(fliege.getAktuellesFeld()==11){
            fliege.setAktuellesFeld(ursprungsfeld);
            throw new UngueltigePositionException("Fliege bleibt nicht auf Blütenstaubfeld.");
        }else if (fliege.getAktuellesFeld()==schlumpfine.getAktuellesFeld()){
            fliege.setAktuellesFeld(ursprungsfeld);
            throw new UngueltigePositionException("Fliege bleibt nicht auf Feld der Schlumpfine.");
        }else {

            // ggf statusveränderungen anpassen wenn auf feld ein zombie ist:
            if (fliege.getAktuellesFeld() != 24) { //auf pilzfeld hat fliege keine wirkung
                for (Spieler spieler : spielerListe) {
                    for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                        if (fliege.getAktuellesFeld() == schlumpf.getAktuellesFeld() && !schlumpf.isIstZombie()) {
                            schlumpf.setIstZombie(true);
                            zombieSchluempfe.add(schlumpf);
                            System.out.println("Die Fliege beißt Schlumpf " + schlumpf.getName() + ". Er ist nun ein Zombie.");
                        }
                    }
                }
            }
        }
    }


    public void schlumpfineBewegung(int augenzahl, Richtung richtung) {
        //bewegt sich feld für feld und heilt ggf

        int ursprungsFeld=schlumpfine.getAktuellesFeld();

        //überprüfen ob Abzweigung
        for (int i = 1; i <= augenzahl; i++) {
            //überprüfen ob abzweigung
            if (schlumpfine.getAktuellesFeld() == 3 || schlumpfine.getAktuellesFeld() == 31) {
                if (richtung == Richtung.WEITER) {
                    schlumpfine.setAktuellesFeld(schlumpfine.getAktuellesFeld() + 1);
                } else if (schlumpfine.getAktuellesFeld() == 3) {
                    schlumpfine.setAktuellesFeld(8);
                } else if (schlumpfine.getAktuellesFeld() == 31) {
                    schlumpfine.setAktuellesFeld(36);
                }

            } else if (schlumpfine.getAktuellesFeld() == 7) {
                schlumpfine.setAktuellesFeld(15);
            } else if (schlumpfine.getAktuellesFeld() == 35) {
                schlumpfine.setAktuellesFeld(1);
            } else if (schlumpfine.getAktuellesFeld() == 36) {
                schlumpfine.setAktuellesFeld(1);
            } else {
                schlumpfine.setAktuellesFeld(schlumpfine.getAktuellesFeld() + 1); //zieht feld für feld
            }

            if (schlumpfine.getAktuellesFeld()==fliege.getAktuellesFeld()){
                schlumpfine.setAktuellesFeld(ursprungsFeld);
                throw new UngueltigePositionException("Schlumpfine kann Feld der Fliege nicht betreten.");
            }else {

                // ggf statusveränderungen anpassen wenn auf feld ein zombie ist:
                if (schlumpfine.getAktuellesFeld() != 24) { //auf PilzFeld hat Schlumpfine keine Wirkung
                    for (Spieler spieler : spielerListe) {
                        for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                            if (schlumpfine.getAktuellesFeld() == schlumpf.getAktuellesFeld() && schlumpf.isIstZombie()) {
                                schlumpf.setIstZombie(false);
                                zombieSchluempfe.remove(schlumpf);
                                System.out.println("Die Schlumpfine heilt Schlumpf " + schlumpf.getName() + ". " +
                                        "Er ist nun kein Zombie mehr.");
                            }
                        }
                    }
                }
            }
            System.out.println("Schlumpfine ist nun auf Feld " + schlumpfine.getAktuellesFeld() + ".");
        }
    }


    @Override
    public boolean bewegeFigur(String figurName, int augenzahl) {
        return bewegeFigur(figurName, augenzahl, Richtung.WEITER);
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
        } else if (figurName.contentEquals("Schlumpfine")){
            return schlumpfine.getAktuellesFeld();
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
        if (spielerAmZug != null) {
            return spielerAmZug.getFarbe();
        }
        return null;
    }

    @Override
    public Farbe gewinner() {
        int counter = 0;
        for (Spieler spieler : spielerListe) { //für jeden spieler die liste durchsuchen
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                if (schlumpf.getAktuellesFeld() == 36) {
                    counter++;
                }
            }
            if (counter == 4) { //spieler hat 4 schlümpfe im ziel, hat gewonnen
                return spieler.getSpielerFarbe();
            }
            counter = 0;
        }
        return null;
    }

    @Override
    public String toString() {
        String spielStatus = "Spielstatus: ";
        for (Spieler spieler : spielerListe) {
            for (Schlumpf schlumpf : spieler.getSchlumpfListe()) {
                spielStatus = spielStatus.concat(schlumpf.getName() + ":" + schlumpf.getAktuellesFeld());
                if (schlumpf.isIstZombie()) {
                    spielStatus = spielStatus.concat(":Z");
                }
                spielStatus = spielStatus.concat(", ");
            }
        }
        //Bzz
        spielStatus = spielStatus.concat("Bzz:" + fliege.getAktuellesFeld());
        if (fliege.getIstZombie()) {
            spielStatus = spielStatus.concat(":Z");
        }
        spielStatus = spielStatus.concat(", ");

        //Doc
        spielStatus = spielStatus.concat("Doc:" + doc.getAktuellesFeld());
        if (doc.getIstZombie()) {
            spielStatus = spielStatus.concat(":Z");
        }
        spielStatus = spielStatus.concat("Schlumpfine:" + schlumpfine.getAktuellesFeld());
        if(schlumpfine.getIstZombie()){
            spielStatus = spielStatus.concat(":Z");
        }
        return spielStatus;
    }

    public List<Spieler> getSpielerListe() {
        return spielerListe;
    }

    private void spielerHinzufuegen(Farbe... farben) throws FalscheSpielerzahlException, WiederholteFarbenException {
        int playerCounter = 0;
        boolean istWiederholteFarbe = false;
        for (Farbe farbe : farben) {
            spielerListe.add(new Spieler(farbe));
            switch (farbe) {
                case GRUEN:
                    if (istGruenImSpiel) {
                        istWiederholteFarbe = true;
                    }
                    istGruenImSpiel = true;
                    break;
                case ROT:
                    if (istRotImSpiel) {
                        istWiederholteFarbe = true;
                    }
                    istRotImSpiel = true;
                    break;
                case BLAU:
                    if (istBlauImSpiel) {
                        istWiederholteFarbe = true;
                    }
                    istBlauImSpiel = true;
                    break;
                case GELB:
                    if (istGelbImSpiel) {
                        istWiederholteFarbe = true;
                    }
                    istGelbImSpiel = true;
                    break;
                default:
                    System.err.println("ERROR 002: UNKNOWN COLOUR IN CONSTRUCTOR");
                    break;
            }
            playerCounter++;
        }
        if (playerCounter == 0) {
            throw new FalscheSpielerzahlException("Not enough players. Minimum is 1.");
        } else if (playerCounter > 4) {
            throw new FalscheSpielerzahlException("Too many players. Maximum is 4.");
        }
        if(istWiederholteFarbe){
            throw new WiederholteFarbenException("Eine Farbe wurde mehrfach genannt.");
        }
    }

    public static IZombieSchluempfe laden(String dateiName){

        return null;
    }

}
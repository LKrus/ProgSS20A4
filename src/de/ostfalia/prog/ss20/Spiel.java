package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.felder.Spezialfeld;

import static de.ostfalia.prog.ss20.enums.Farbe.*;

/**
 * Controller
 */
public class Spiel {

    public static void main(String[] args) {

        /*
        abfragen wie viele spieler
        (conf einlesen)
        spielbrett erstellen
        speiler erstellen
        figuren setzen

        spiel beginnt:

        gelb würfelt
        gelb sucht figur aus
        (neues feld wird errechnet)
        figur zieht feld für feld: nachgucken ob effekt
        wenn abzweiung, fragen wo hin
        figur zieht feld für feld: nachgucken ob effekt
        figur.setAktuellesFeld
       gucken ob gewinnbedingung erfüllt

       nächster spieler
        ...

         */


        ZombieSchluempfe zombieSchluempfe = new ZombieSchluempfe(BLAU, ROT);
        System.out.println(zombieSchluempfe.getFarbeAmZug());
        zombieSchluempfe.bewegeFigur("BLAU-A", 3);
        System.out.println(zombieSchluempfe.getFarbeAmZug());
//        System.out.println(zombieSchluempfe.getSpielerListe().get(0).name);

    }


}

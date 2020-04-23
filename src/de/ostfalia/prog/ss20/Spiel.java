package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.enums.Richtung;

import java.util.Scanner;

import static de.ostfalia.prog.ss20.enums.Farbe.*;

/**
 * Controller
 */
public class Spiel {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        ZombieSchluempfe zombieSchluempfe = new ZombieSchluempfe(GELB, ROT);

        Farbe farbeGewonnen = zombieSchluempfe.gewinner(); //hier kommt grad null zurück

        while (null != farbeGewonnen) {

            // TODO: 23.04.2020 spieler am zug festlegen (gelb)
            System.out.println("Es ist Spieler __ dran.");
            //evtl hier auflistung, wo seine figuren grad stehen?
            int augenzahl = zombieSchluempfe.wuerfeln(); //6 = fliege
            if (augenzahl == 6) {
                while (augenzahl == 6) {
                    augenzahl = zombieSchluempfe.wuerfeln();
                }
                System.out.println("Augenzahl der Fliege: " + augenzahl);

                //nachrechnen ob abbiegung zwischen figur und figur+augenzahl da ist
                if (zombieSchluempfe.fliege.getAktuellesFeld() <= 3 && zombieSchluempfe.fliege.getAktuellesFeld() + augenzahl > 3) {
                    System.out.println("In welche Richtung soll gezogen werden? WEITER oder ABZWEIGEN?");
                    String richtungString = scanner.next();
                    
                    //geht das kürzer?
                    if (richtungString.toUpperCase().equals(Richtung.WEITER)) {
                        zombieSchluempfe.bewegeFigur("Fliege", augenzahl, Richtung.WEITER);
                    } else if (richtungString.toUpperCase().equals(Richtung.ABZWEIGEN)) {
                        zombieSchluempfe.bewegeFigur("Fliege", augenzahl, Richtung.ABZWEIGEN);
                    }
                } else {
                    zombieSchluempfe.bewegeFigur("Fliege", augenzahl);
                }

            } else {
                System.out.println("Augenzahl: " + augenzahl);

                System.out.println("Welche Figur soll gezogen werden?");
                String figurName = scanner.next(); //fehlerhafte eingabe

                //nachrechnen ob abbiegung zwischen figur und figur+augenzahl da ist

                zombieSchluempfe.bewegeFigur(figurName, augenzahl);
            }

            //spieler am zug +1
        }
        System.out.println(farbeGewonnen + " hat gewonnen");


//        ZombieSchluempfe zombieSchluempfe = new ZombieSchluempfe(BLAU, ROT);
//        System.out.println(zombieSchluempfe.toString());
//        zombieSchluempfe.bewegeFigur("BLAU-A", 3);
//        System.out.println(zombieSchluempfe.toString());
////        System.out.println(zombieSchluempfe.getSpielerListe().get(0).name);

    }
}

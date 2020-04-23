package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;

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
                //fliege zieht, aber anders, fliegt über zwischenfelder hinweg und landet auf endfeld
            }else{
                System.out.println("Augenzahl: " + augenzahl);

                System.out.println("Welche Figur soll gezogen werden?");
                String figurName = scanner.next(); //fehlerhafte eingabe abfangen
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

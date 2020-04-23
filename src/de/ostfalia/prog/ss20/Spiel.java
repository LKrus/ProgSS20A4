package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;

import java.util.Scanner;

import static de.ostfalia.prog.ss20.enums.Farbe.*;

/**
 * Controller
 */
public class Spiel {

    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);

        ZombieSchluempfe zombieSchluempfe = new ZombieSchluempfe(GELB, ROT);

        Farbe farbeGewonnen = zombieSchluempfe.gewinner();

        while (null!=farbeGewonnen) {

            //spieler am zug festlegen (gelb)

            int augenzahl = zombieSchluempfe.wuerfeln();
            System.out.println("Augenzahl: "+augenzahl);

            System.out.println("Welche Figur soll gezogen werden?");
            String figurName = scanner.next();
            //nachrechnen ob abbiegung zwischen figur und figur+augenzahl da ist
            zombieSchluempfe.bewegeFigur(figurName, augenzahl);

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

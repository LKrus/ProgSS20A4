package de.ostfalia.prog.ss20;

import de.ostfalia.prog.ss20.enums.Farbe;
import de.ostfalia.prog.ss20.felder.Spezialfeld;

import java.util.Scanner;

import static de.ostfalia.prog.ss20.enums.Farbe.*;

/**
 * Controller
 */
public class Spiel {

    public static void main(String[] args) {
        Scanner scanner=new Scanner(System.in);

        String input=scanner.nextLine();

        //string aufspalten nach conf + farben
        String conf="";

        ZombieSchluempfe zombieSchluempfe = new ZombieSchluempfe(conf,GELB, ROT);

        //spieler am zug festlegen (gelb)

        int augenzahl = zombieSchluempfe.wuerfeln();

        //abfragen welche figur gezogen werden soll:
        String figurName = scanner.next();
        //nachrechnen ob abbiegung zwischen figur und figur+augenzahl da ist
        zombieSchluempfe.bewegeFigur(figurName,augenzahl);

        Farbe farbeGewonnen = zombieSchluempfe.gewinner();
        if (null!=farbeGewonnen){
            System.out.println(farbeGewonnen+"hat gewonnen");
        }else {
            //spieler am zug +1
            //alles wiederholen;
        }

//        ZombieSchluempfe zombieSchluempfe = new ZombieSchluempfe(BLAU, ROT);
//        System.out.println(zombieSchluempfe.toString());
//        zombieSchluempfe.bewegeFigur("BLAU-A", 3);
//        System.out.println(zombieSchluempfe.toString());
////        System.out.println(zombieSchluempfe.getSpielerListe().get(0).name);

    }


}

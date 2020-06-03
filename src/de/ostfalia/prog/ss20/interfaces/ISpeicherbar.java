package de.ostfalia.prog.ss20.interfaces;

import java.io.IOException;

/**
 * Erweitert das Spiel ZombieSchluempfe, so dass eine Spielstellung gespeichert und geladen werden kann
 *
 * @author M. Gruendel, D. Dick und L. Blote
 * @since SS 2020
 */
public interface ISpeicherbar {

    /**
     * Die Methode speichert in eine Datei den momentanen Spielzustand, so dass
     * nach dem Laden das Weiterspielen möglich ist.
     *
     * Methode wird erst ab der 3. Aufgabe implementiert.
     *
     * @param dateiName
     *            Name der Datei bzw. den kompletten Pfad, wo die Datei
     *            gespeichert wird
     */
    public void speichern(String dateiName) throws IOException;

    /**
     * Die Methode
     * "public static IZombieSchluempfe ladenSpiel (String dateiName) throws IOException"
     * liest eine Datei und konfiguriert das Spiel, wie es dort gespeichert ist,
     * so dass nach dem Laden das Weiterspielen möglich ist.
     *
     * Das heißt, die Figuren werden auf die Position gebracht wie sie
     * gespeichert wurden und der Spieler, der als nächstes spielen darf ist
     * auch bekannt.
     *
     * Methode wird erst ab der 3. Aufgabe implementiert.
     *
     * @param dateiName
     *            Der Name der Datei, wo die gewünschte Spielkonfiguration
     *            gespeichert ist.
     * @return eine Instanz der Klasse, die IZombieSchluempfe implementiert
     */


    public static IZombieSchluempfe laden(String dateiName) throws Exception {
        throw new NoSuchMethodException("Methode laden(String) muss überschrieben werden.");
    }
}
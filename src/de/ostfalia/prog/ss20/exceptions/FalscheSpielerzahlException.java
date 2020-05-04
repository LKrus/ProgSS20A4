package de.ostfalia.prog.ss20.exceptions;

public class FalscheSpielerzahlException extends Exception {

    public FalscheSpielerzahlException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "Die Spieleranzahl liegt nicht im Bereich 1 bis 4 (beide inklusive).";
    }
}

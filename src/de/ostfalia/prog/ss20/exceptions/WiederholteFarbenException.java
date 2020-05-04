package de.ostfalia.prog.ss20.exceptions;

public class WiederholteFarbenException extends Exception {

    public WiederholteFarbenException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "Mindestens eine der Farben wurde doppelt erzeugt, oder einer Farbe wurden mehr als ihrer 4 Standardfiguren übergeben.";
    }
}

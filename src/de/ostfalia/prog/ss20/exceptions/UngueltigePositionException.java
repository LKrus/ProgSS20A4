package de.ostfalia.prog.ss20.exceptions;

public class UngueltigePositionException extends RuntimeException {

    public UngueltigePositionException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "Eine ungültige Position für eine der Figuren wurde übergeben.";
    }
}

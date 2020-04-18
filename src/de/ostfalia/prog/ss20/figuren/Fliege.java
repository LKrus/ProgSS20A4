package de.ostfalia.prog.ss20.figuren;

public class Fliege extends NPC {
    private int aktuellesFeld;

    public Fliege(String name, int aktuellesFeld) {
        super(name, aktuellesFeld);
        this.aktuellesFeld=aktuellesFeld;
    }

    public int getFliegeAktuellesFeld(){
        return aktuellesFeld;
    }

    @Override
    public void wirken(){

    }
}

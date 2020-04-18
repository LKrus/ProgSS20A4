package de.ostfalia.prog.ss20.figuren;

public class Doc extends NPC {
    private int aktuellesFeld;

    public Doc(String name, int aktuellesFeld) {
        super(name, aktuellesFeld);
        this.aktuellesFeld=aktuellesFeld;
    }

    public int getAktuellesFeld(){
        return aktuellesFeld;
    }

    @Override
    public void wirken(){

    }
}

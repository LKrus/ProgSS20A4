package de.ostfalia.prog.ss20.figuren;

public class Doc extends NPC {
    private int aktuellesFeld;
    private boolean istZombie = false;

    public Doc(String name, int aktuellesFeld) {
        super(name, aktuellesFeld);
        this.aktuellesFeld=aktuellesFeld;
    }

    public int getAktuellesFeld(){
        return aktuellesFeld;
    }

    public boolean getIstZombie(){
        return istZombie;
    }

    @Override
    public void wirken(){

    }
}

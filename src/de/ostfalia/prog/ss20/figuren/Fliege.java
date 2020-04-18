package de.ostfalia.prog.ss20.figuren;

public class Fliege extends NPC {
    private int aktuellesFeld;
    private boolean istZombie = true;

    public Fliege(String name, int aktuellesFeld) {
        super(name, aktuellesFeld);
        this.aktuellesFeld=aktuellesFeld;
    }

    public int getAktuellesFeld(){
        return aktuellesFeld;
    }

    public void setAktuellesFeld(int feld){
        aktuellesFeld = feld;
    }

    public boolean getFliegeIstZombie(){
        return istZombie;
    }

    @Override
    public void wirken(){

    }
}

package tutosandroidfrance.com.IntellibagExample;

/**
 * Created by florentchampigny on 24/02/15.
 */
public class Infos {
    private int color;
    private String categorie;
    private int valeur;

    public Infos(int color, String categorie, int valeur) {
        this.color = color;
        this.categorie = categorie;
        this.valeur = valeur;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String pseudo) {
        this.categorie = categorie;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(String text) {
        this.valeur = valeur;
    }
}

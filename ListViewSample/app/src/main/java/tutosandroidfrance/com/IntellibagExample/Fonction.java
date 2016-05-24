package tutosandroidfrance.com.IntellibagExample;

/**
 * Created by florentchampigny on 24/02/15.
 */
public class Fonction {
    private String img;
    private String categorie;
    private int valeur;

    public Fonction(String img, String categorie, int valeur) {
        this.img = img;
        this.categorie = categorie;
        this.valeur = valeur;
    }

    public String getImg() {
        return img;
    }

    public void setImg(int color) {
        this.img = img;
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

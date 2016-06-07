package com.example.kevin.intellibag;


public class Fonction {
    private String img;
    private String categorie;
    private String valeur;

    public Fonction(String img, String categorie, String valeur) {
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

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String text) {
        this.valeur = valeur;
    }
}

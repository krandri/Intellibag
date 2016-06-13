package com.example.kevin.intellibag;

public class Capteur {

    private int id;
    private String nom;
    private String date;
    private int valeur;

    public Capteur(){}

    public Capteur(String nom, String date, int valeur){
        this.nom = nom;
        this.date = date;
        this.valeur = valeur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public String toString(){
        return "ID : "+id+"\nNom : "+nom+"\nDate : "+date+"\nValeur : "+valeur;
    }
}
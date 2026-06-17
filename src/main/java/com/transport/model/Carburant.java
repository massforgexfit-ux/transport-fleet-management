package com.transport.model;

import java.io.Serializable;

/**
 * Classe représentant la gestion du carburant
 */
public class Carburant implements Serializable {
    private int idCarburant;
    private String type; // essence, diesel, électrique, etc.
    private double quantite; // en litres ou kWh
    private double prix; // prix par unité
    private Vehicle vehiculeAffecte;

    public Carburant(int idCarburant, String type, double quantite, double prix) {
        this.idCarburant = idCarburant;
        this.type = type;
        this.quantite = quantite;
        this.prix = prix;
    }

    // Getters et Setters
    public int getIdCarburant() {
        return idCarburant;
    }

    public void setIdCarburant(int idCarburant) {
        this.idCarburant = idCarburant;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getQuantite() {
        return quantite;
    }

    public void setQuantite(double quantite) {
        this.quantite = quantite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public Vehicle getVehiculeAffecte() {
        return vehiculeAffecte;
    }

    public void setVehiculeAffecte(Vehicle vehiculeAffecte) {
        this.vehiculeAffecte = vehiculeAffecte;
    }

    // Méthodes métier
    public void ajouterConsommation() {
        System.out.println("Consommation de carburant enregistrée.");
    }

    public double calculerCoutTotal() {
        return quantite * prix;
    }

    @Override
    public String toString() {
        return "Carburant{" +
                "idCarburant=" + idCarburant +
                ", type='" + type + '\'' +
                ", quantite=" + quantite +
                ", prix=" + prix +
                ", coutTotal=" + calculerCoutTotal() +
                '}';
    }
}
package com.transport.model;

/**
 * Classe représentant une voiture (spécialisation de Vehicle)
 */
public class Voiture extends Vehicle {
    private int nombrePlaces;
    private String typeCarburant;

    public Voiture(int idVehicule, String immatriculation, String marque, 
                   String modele, int annee, double kilometrage, String statut,
                   int nombrePlaces, String typeCarburant) {
        super(idVehicule, immatriculation, marque, modele, annee, kilometrage, statut);
        this.nombrePlaces = nombrePlaces;
        this.typeCarburant = typeCarburant;
    }

    // Getters et Setters
    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public String getTypeCarburant() {
        return typeCarburant;
    }

    public void setTypeCarburant(String typeCarburant) {
        this.typeCarburant = typeCarburant;
    }

    // Méthodes métier
    public void reserver() {
        System.out.println("Voiture " + immatriculation + " réservée.");
        this.statut = "réservée";
    }

    public void liberer() {
        System.out.println("Voiture " + immatriculation + " libérée.");
        this.statut = "disponible";
    }

    @Override
    public void demarrer() {
        System.out.println("Voiture " + immatriculation + " en cours de démarrage...");
        this.statut = "en route";
    }

    @Override
    public void arreter() {
        System.out.println("Voiture " + immatriculation + " arrêtée.");
        this.statut = "arrêtée";
    }

    @Override
    public String toString() {
        return "Voiture{" +
                "idVehicule=" + idVehicule +
                ", immatriculation='" + immatriculation + '\'' +
                ", nombrePlaces=" + nombrePlaces +
                ", typeCarburant='" + typeCarburant + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
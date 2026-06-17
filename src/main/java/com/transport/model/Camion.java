package com.transport.model;

/**
 * Classe représentant un camion (spécialisation de Vehicle)
 */
public class Camion extends Vehicle {
    private double capaciteCharge;
    private String typeMarchandise;

    public Camion(int idVehicule, String immatriculation, String marque, 
                  String modele, int annee, double kilometrage, String statut,
                  double capaciteCharge, String typeMarchandise) {
        super(idVehicule, immatriculation, marque, modele, annee, kilometrage, statut);
        this.capaciteCharge = capaciteCharge;
        this.typeMarchandise = typeMarchandise;
    }

    // Getters et Setters
    public double getCapaciteCharge() {
        return capaciteCharge;
    }

    public void setCapaciteCharge(double capaciteCharge) {
        this.capaciteCharge = capaciteCharge;
    }

    public String getTypeMarchandise() {
        return typeMarchandise;
    }

    public void setTypeMarchandise(String typeMarchandise) {
        this.typeMarchandise = typeMarchandise;
    }

    // Méthodes métier
    public void chargerMarchandise(double poids) {
        if (poids <= capaciteCharge) {
            System.out.println("Marchandise chargée: " + poids + " tonnes");
        } else {
            System.out.println("Capacité dépassée! Limite: " + capaciteCharge);
        }
    }

    public void dechargerMarchandise() {
        System.out.println("Marchandise déchargée du camion " + immatriculation);
    }

    @Override
    public void demarrer() {
        System.out.println("Camion " + immatriculation + " en cours de démarrage...");
        this.statut = "en route";
    }

    @Override
    public void arreter() {
        System.out.println("Camion " + immatriculation + " arrêté.");
        this.statut = "arrêté";
    }

    @Override
    public String toString() {
        return "Camion{" +
                "idVehicule=" + idVehicule +
                ", immatriculation='" + immatriculation + '\'' +
                ", capaciteCharge=" + capaciteCharge +
                ", typeMarchandise='" + typeMarchandise + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
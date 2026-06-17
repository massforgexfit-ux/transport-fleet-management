package com.transport.model;

/**
 * Classe représentant un bus (spécialisation de Vehicle)
 */
public class Bus extends Vehicle {
    private int capacitePassagers;
    private String typeBus; // urbain, routier, etc.

    public Bus(int idVehicule, String immatriculation, String marque, 
               String modele, int annee, double kilometrage, String statut,
               int capacitePassagers, String typeBus) {
        super(idVehicule, immatriculation, marque, modele, annee, kilometrage, statut);
        this.capacitePassagers = capacitePassagers;
        this.typeBus = typeBus;
    }

    // Getters et Setters
    public int getCapacitePassagers() {
        return capacitePassagers;
    }

    public void setCapacitePassagers(int capacitePassagers) {
        this.capacitePassagers = capacitePassagers;
    }

    public String getTypeBus() {
        return typeBus;
    }

    public void setTypeBus(String typeBus) {
        this.typeBus = typeBus;
    }

    // Méthodes métier
    public void embarquerPassagers(int nombre) {
        if (nombre <= capacitePassagers) {
            System.out.println(nombre + " passagers embarqués.");
        } else {
            System.out.println("Capacité dépassée! Limite: " + capacitePassagers);
        }
    }

    public void debarquerPassagers(int nombre) {
        System.out.println(nombre + " passagers débarqués.");
    }

    @Override
    public void demarrer() {
        System.out.println("Bus " + immatriculation + " en cours de démarrage...");
        this.statut = "en route";
    }

    @Override
    public void arreter() {
        System.out.println("Bus " + immatriculation + " arrêté.");
        this.statut = "arrêté";
    }

    @Override
    public String toString() {
        return "Bus{" +
                "idVehicule=" + idVehicule +
                ", immatriculation='" + immatriculation + '\'' +
                ", capacitePassagers=" + capacitePassagers +
                ", typeBus='" + typeBus + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
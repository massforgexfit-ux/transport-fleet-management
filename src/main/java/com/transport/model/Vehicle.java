package com.transport.model;

import java.io.Serializable;

/**
 * Classe abstraite représentant un véhicule générique
 */
public abstract class Vehicle implements Serializable {
    protected int idVehicule;
    protected String immatriculation;
    protected String marque;
    protected String modele;
    protected int annee;
    protected double kilometrage;
    protected String statut; // actif, maintenance, hors service
    protected TypeVehicule type;

    public Vehicle(int idVehicule, String immatriculation, String marque, 
                   String modele, int annee, double kilometrage, String statut) {
        this.idVehicule = idVehicule;
        this.immatriculation = immatriculation;
        this.marque = marque;
        this.modele = modele;
        this.annee = annee;
        this.kilometrage = kilometrage;
        this.statut = statut;
    }

    // Getters et Setters
    public int getIdVehicule() {
        return idVehicule;
    }

    public void setIdVehicule(int idVehicule) {
        this.idVehicule = idVehicule;
    }

    public String getImmatriculation() {
        return immatriculation;
    }

    public void setImmatriculation(String immatriculation) {
        this.immatriculation = immatriculation;
    }

    public String getMarque() {
        return marque;
    }

    public void setMarque(String marque) {
        this.marque = marque;
    }

    public String getModele() {
        return modele;
    }

    public void setModele(String modele) {
        this.modele = modele;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public double getKilometrage() {
        return kilometrage;
    }

    public void setKilometrage(double kilometrage) {
        this.kilometrage = kilometrage;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public TypeVehicule getType() {
        return type;
    }

    public void setType(TypeVehicule type) {
        this.type = type;
    }

    // Méthodes abstraites
    public abstract void demarrer();

    public abstract void arreter();

    @Override
    public String toString() {
        return "Vehicle{" +
                "idVehicule=" + idVehicule +
                ", immatriculation='" + immatriculation + '\'' +
                ", marque='" + marque + '\'' +
                ", modele='" + modele + '\'' +
                ", annee=" + annee +
                ", kilometrage=" + kilometrage +
                ", statut='" + statut + '\'' +
                '}';
    }
}
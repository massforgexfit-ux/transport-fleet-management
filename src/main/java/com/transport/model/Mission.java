package com.transport.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Classe représentant une mission de transport
 */
public class Mission implements Serializable {
    private int idMission;
    private LocalDate dateDepart;
    private LocalDate dateRetour;
    private String destination;
    private String statut; // programmée, en cours, terminée, annulée
    private Vehicle vehiculeAffecte;
    private Chauffeur chauffeurAffecte;
    private Carburant carburant;

    public Mission(int idMission, LocalDate dateDepart, LocalDate dateRetour, String destination) {
        this.idMission = idMission;
        this.dateDepart = dateDepart;
        this.dateRetour = dateRetour;
        this.destination = destination;
        this.statut = "programmée";
    }

    // Getters et Setters
    public int getIdMission() {
        return idMission;
    }

    public void setIdMission(int idMission) {
        this.idMission = idMission;
    }

    public LocalDate getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDate dateDepart) {
        this.dateDepart = dateDepart;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Vehicle getVehiculeAffecte() {
        return vehiculeAffecte;
    }

    public void setVehiculeAffecte(Vehicle vehiculeAffecte) {
        this.vehiculeAffecte = vehiculeAffecte;
    }

    public Chauffeur getChauffeurAffecte() {
        return chauffeurAffecte;
    }

    public void setChauffeurAffecte(Chauffeur chauffeurAffecte) {
        this.chauffeurAffecte = chauffeurAffecte;
    }

    public Carburant getCarburant() {
        return carburant;
    }

    public void setCarburant(Carburant carburant) {
        this.carburant = carburant;
    }

    // Méthodes métier
    public void affecterVehicule(Vehicle vehicle) {
        if (vehicle != null) {
            this.vehiculeAffecte = vehicle;
            System.out.println("Véhicule " + vehicle.getImmatriculation() + " affecté à la mission.");
        }
    }

    public void affecterChauffeur(Chauffeur chauffeur) {
        if (chauffeur != null) {
            this.chauffeurAffecte = chauffeur;
            System.out.println("Chauffeur " + chauffeur.getPrenom() + " affecté à la mission.");
        }
    }

    public void demarrerMission() {
        if (vehiculeAffecte != null && chauffeurAffecte != null) {
            this.statut = "en cours";
            vehiculeAffecte.demarrer();
            System.out.println("Mission " + idMission + " démarrée vers " + destination);
        }
    }

    public void terminerMission() {
        this.statut = "terminée";
        if (vehiculeAffecte != null) {
            vehiculeAffecte.arreter();
        }
        System.out.println("Mission " + idMission + " terminée.");
    }

    @Override
    public String toString() {
        return "Mission{" +
                "idMission=" + idMission +
                ", dateDepart=" + dateDepart +
                ", dateRetour=" + dateRetour +
                ", destination='" + destination + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }
}
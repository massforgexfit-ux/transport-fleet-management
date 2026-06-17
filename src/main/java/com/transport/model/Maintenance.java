package com.transport.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Classe représentant une maintenance de véhicule
 */
public class Maintenance implements Serializable {
    private int idMaintenance;
    private String typeMaintenance; // révision, réparation, contrôle technique
    private double cout;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Vehicle vehiculeAffecte;
    private List<String> historique;

    public Maintenance(int idMaintenance, String typeMaintenance, double cout, LocalDate dateDebut) {
        this.idMaintenance = idMaintenance;
        this.typeMaintenance = typeMaintenance;
        this.cout = cout;
        this.dateDebut = dateDebut;
        this.historique = new ArrayList<>();
        historique.add("Maintenance créée le " + LocalDate.now());
    }

    // Getters et Setters
    public int getIdMaintenance() {
        return idMaintenance;
    }

    public void setIdMaintenance(int idMaintenance) {
        this.idMaintenance = idMaintenance;
    }

    public String getTypeMaintenance() {
        return typeMaintenance;
    }

    public void setTypeMaintenance(String typeMaintenance) {
        this.typeMaintenance = typeMaintenance;
    }

    public double getCout() {
        return cout;
    }

    public void setCout(double cout) {
        this.cout = cout;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public Vehicle getVehiculeAffecte() {
        return vehiculeAffecte;
    }

    public void setVehiculeAffecte(Vehicle vehiculeAffecte) {
        this.vehiculeAffecte = vehiculeAffecte;
    }

    public List<String> getHistorique() {
        return new ArrayList<>(historique);
    }

    // Méthodes métier
    public void planifierMaintenance() {
        historique.add("Maintenance planifiée pour " + typeMaintenance);
        System.out.println("Maintenance planifiée pour le " + dateDebut);
    }

    public void enregistrerCout() {
        historique.add("Coût enregistré: " + cout + " €");
        System.out.println("Coût de maintenance: " + cout + " €");
    }

    public void afficherHistorique() {
        System.out.println("Historique de maintenance:");
        for (String h : historique) {
            System.out.println("  - " + h);
        }
    }

    @Override
    public String toString() {
        return "Maintenance{" +
                "idMaintenance=" + idMaintenance +
                ", typeMaintenance='" + typeMaintenance + '\'' +
                ", cout=" + cout +
                ", dateDebut=" + dateDebut +
                '}';
    }
}
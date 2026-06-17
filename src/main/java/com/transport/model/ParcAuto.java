package com.transport.model;

import java.util.*;

/**
 * Classe représentant un parc automobile d'une entreprise de transport
 */
public class ParcAuto {
    private int idParc;
    private String nom;
    private String localisation;
    private List<Vehicle> vehicules;

    public ParcAuto(int idParc, String nom, String localisation) {
        this.idParc = idParc;
        this.nom = nom;
        this.localisation = localisation;
        this.vehicules = new ArrayList<>();
    }

    // Getters et Setters
    public int getIdParc() {
        return idParc;
    }

    public void setIdParc(int idParc) {
        this.idParc = idParc;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public List<Vehicle> getVehicules() {
        return vehicules;
    }

    // Méthodes métier
    public void ajouterVehicule(Vehicle vehicle) {
        if (vehicle != null) {
            vehicules.add(vehicle);
        }
    }

    public void supprimerVehicule(Vehicle vehicle) {
        vehicules.remove(vehicle);
    }

    public List<Vehicle> listerVehicules() {
        return new ArrayList<>(vehicules);
    }

    public Vehicle rechercherVehicule(int idVehicule) {
        return vehicules.stream()
                .filter(v -> v.getIdVehicule() == idVehicule)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "ParcAuto{" +
                "idParc=" + idParc +
                ", nom='" + nom + '\'' +
                ", localisation='" + localisation + '\'' +
                ", nombreVehicules=" + vehicules.size() +
                '}';
    }
}
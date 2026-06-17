package com.transport.model;

import java.util.*;

/**
 * Classe représentant un chauffeur (spécialisation d'Utilisateur)
 */
public class Chauffeur extends Utilisateur {
    private String telephone;
    private String permis;
    private String typePermis; // A, B, C, E, etc.
    private boolean disponibilite;
    private List<Mission> missions;

    public Chauffeur(int idUtilisateur, String nom, String prenom, 
                     String email, String motDePasse, String telephone,
                     String permis, String typePermis) {
        super(idUtilisateur, nom, prenom, email, motDePasse);
        this.telephone = telephone;
        this.permis = permis;
        this.typePermis = typePermis;
        this.disponibilite = true;
        this.missions = new ArrayList<>();
    }

    // Getters et Setters
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getPermis() {
        return permis;
    }

    public void setPermis(String permis) {
        this.permis = permis;
    }

    public String getTypePermis() {
        return typePermis;
    }

    public void setTypePermis(String typePermis) {
        this.typePermis = typePermis;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public List<Mission> getMissions() {
        return missions;
    }

    // Méthodes métier
    public void accepterMission(Mission mission) {
        if (disponibilite) {
            missions.add(mission);
            mission.setStatut("acceptée");
            this.disponibilite = false;
            System.out.println("Mission acceptée par " + prenom + " " + nom);
        } else {
            System.out.println(prenom + " " + nom + " n'est pas disponible.");
        }
    }

    public void refuserMission(Mission mission) {
        mission.setStatut("refusée");
        System.out.println("Mission refusée par " + prenom + " " + nom);
    }

    public void consulterMissions() {
        System.out.println("Missions du chauffeur " + prenom + " " + nom + ":");
        for (Mission m : missions) {
            System.out.println("  - " + m);
        }
    }

    public void realiser() {
        System.out.println("Mission en cours de réalisation par " + prenom + " " + nom);
    }

    @Override
    public String toString() {
        return "Chauffeur{" +
                "idUtilisateur=" + idUtilisateur +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", telephone='" + telephone + '\'' +
                ", typePermis='" + typePermis + '\'' +
                ", disponibilite=" + disponibilite +
                ", missions=" + missions.size() +
                '}';
    }
}
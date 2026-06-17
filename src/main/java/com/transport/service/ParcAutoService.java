package com.transport.service;

import com.transport.model.*;
import java.util.*;

/**
 * Service de gestion du parc automobile
 */
public class ParcAutoService {
    private List<ParcAuto> parcs;
    private List<Vehicle> vehicules;
    private List<Chauffeur> chauffeurs;
    private List<Mission> missions;
    private List<Maintenance> maintenances;

    public ParcAutoService() {
        this.parcs = new ArrayList<>();
        this.vehicules = new ArrayList<>();
        this.chauffeurs = new ArrayList<>();
        this.missions = new ArrayList<>();
        this.maintenances = new ArrayList<>();
    }

    // Méthodes pour ParcAuto
    public void ajouterParc(ParcAuto parc) {
        parcs.add(parc);
        System.out.println("Parc " + parc.getNom() + " ajouté.");
    }

    public ParcAuto rechercherParc(int idParc) {
        return parcs.stream()
                .filter(p -> p.getIdParc() == idParc)
                .findFirst()
                .orElse(null);
    }

    // Méthodes pour Véhicules
    public void ajouterVehicule(int idParc, Vehicle vehicle) {
        ParcAuto parc = rechercherParc(idParc);
        if (parc != null) {
            parc.ajouterVehicule(vehicle);
            vehicules.add(vehicle);
            System.out.println("Véhicule " + vehicle.getImmatriculation() + " ajouté.");
        }
    }

    public Vehicle rechercherVehicule(int idVehicule) {
        return vehicules.stream()
                .filter(v -> v.getIdVehicule() == idVehicule)
                .findFirst()
                .orElse(null);
    }

    public List<Vehicle> listerVehiculesParc(int idParc) {
        ParcAuto parc = rechercherParc(idParc);
        return parc != null ? parc.listerVehicules() : new ArrayList<>();
    }

    // Méthodes pour Chauffeurs
    public void ajouterChauffeur(Chauffeur chauffeur) {
        chauffeurs.add(chauffeur);
        System.out.println("Chauffeur " + chauffeur.getPrenom() + " " + chauffeur.getNom() + " ajouté.");
    }

    public Chauffeur rechercherChauffeur(int idChauffeur) {
        return chauffeurs.stream()
                .filter(c -> c.getIdUtilisateur() == idChauffeur)
                .findFirst()
                .orElse(null);
    }

    public List<Chauffeur> listerChauffeursDisponibles() {
        return chauffeurs.stream()
                .filter(Chauffeur::isDisponibilite)
                .toList();
    }

    // Méthodes pour Missions
    public void creerMission(Mission mission) {
        missions.add(mission);
        System.out.println("Mission " + mission.getIdMission() + " créée.");
    }

    public Mission rechercherMission(int idMission) {
        return missions.stream()
                .filter(m -> m.getIdMission() == idMission)
                .findFirst()
                .orElse(null);
    }

    public void affecterVehiculeEtChauffeur(int idMission, int idVehicule, int idChauffeur) {
        Mission mission = rechercherMission(idMission);
        Vehicle vehicule = rechercherVehicule(idVehicule);
        Chauffeur chauffeur = rechercherChauffeur(idChauffeur);

        if (mission != null && vehicule != null && chauffeur != null) {
            mission.affecterVehicule(vehicule);
            mission.affecterChauffeur(chauffeur);
            chauffeur.accepterMission(mission);
            System.out.println("Affectation réussie.");
        }
    }

    // Méthodes pour Maintenance
    public void planifierMaintenance(Vehicle vehicle, Maintenance maintenance) {
        if (vehicle != null) {
            maintenance.setVehiculeAffecte(vehicle);
            maintenance.planifierMaintenance();
            maintenances.add(maintenance);
            vehicle.setStatut("maintenance");
        }
    }

    // Statistiques
    public void afficherStatistiques() {
        System.out.println("===== STATISTIQUES =====" );
        System.out.println("Nombre de parcs: " + parcs.size());
        System.out.println("Nombre de véhicules: " + vehicules.size());
        System.out.println("Nombre de chauffeurs: " + chauffeurs.size());
        System.out.println("Nombre de missions: " + missions.size());
        System.out.println("Nombre de maintenances: " + maintenances.size());
    }
}
package com.transport;

import com.transport.model.*;
import com.transport.service.ParcAutoService;
import java.time.LocalDate;

/**
 * Classe principale - Démonstration du système de gestion de parc automobile
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== SYSTÈME DE GESTION DE PARC AUTOMOBILE ===");
        System.out.println();

        // Initialisation du service
        ParcAutoService service = new ParcAutoService();

        // 1. Création d'un parc automobile
        ParcAuto parc1 = new ParcAuto(1, "Parc Principal", "Paris");
        service.ajouterParc(parc1);
        System.out.println();

        // 2. Ajout de véhicules
        System.out.println("--- Ajout de véhicules ---");
        Camion camion1 = new Camion(1, "AB-123-CD", "Renault", "T-High", 2022, 5000, "actif", 25.0, "Marchandises générales");
        Voiture voiture1 = new Voiture(2, "EF-456-GH", "Peugeot", "3008", 2021, 8000, "actif", 5, "Diesel");
        Bus bus1 = new Bus(3, "IJ-789-KL", "Mercedes", "Citaro", 2020, 12000, "actif", 50, "Urbain");

        service.ajouterVehicule(1, camion1);
        service.ajouterVehicule(1, voiture1);
        service.ajouterVehicule(1, bus1);
        System.out.println();

        // 3. Ajout de chauffeurs
        System.out.println("--- Ajout de chauffeurs ---");
        Chauffeur chauffeur1 = new Chauffeur(1, "Dupont", "Jean", "jean.dupont@transport.fr", "pass123", "06-12-34-56-78", "12345678", "C");
        Chauffeur chauffeur2 = new Chauffeur(2, "Martin", "Marie", "marie.martin@transport.fr", "pass456", "06-98-76-54-32", "87654321", "D");

        service.ajouterChauffeur(chauffeur1);
        service.ajouterChauffeur(chauffeur2);
        System.out.println();

        // 4. Création de missions
        System.out.println("--- Création de missions ---");
        Mission mission1 = new Mission(1, LocalDate.now(), LocalDate.now().plusDays(2), "Lyon");
        Mission mission2 = new Mission(2, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), "Marseille");

        service.creerMission(mission1);
        service.creerMission(mission2);
        System.out.println();

        // 5. Affectation de véhicules et chauffeurs aux missions
        System.out.println("--- Affectation aux missions ---");
        service.affecterVehiculeEtChauffeur(1, 1, 1); // Camion + Chauffeur1
        service.affecterVehiculeEtChauffeur(2, 3, 2); // Bus + Chauffeur2
        System.out.println();

        // 6. Démarrage des missions
        System.out.println("--- Démarrage des missions ---");
        mission1.demarrerMission();
        System.out.println();

        // 7. Gestion de la maintenance
        System.out.println("--- Maintenance des véhicules ---");
        Maintenance maintenance1 = new Maintenance(1, "Révision", 500.0, LocalDate.now());
        service.planifierMaintenance(voiture1, maintenance1);
        maintenance1.enregistrerCout();
        System.out.println();

        // 8. Gestion du carburant
        System.out.println("--- Gestion du carburant ---");
        Carburant carburant1 = new Carburant(1, "Diesel", 100.0, 1.50);
        System.out.println("Coût total du carburant: " + carburant1.calculerCoutTotal() + " €");
        System.out.println();

        // 9. Affichage des statistiques
        service.afficherStatistiques();
        System.out.println();

        // 10. Affichage des informations
        System.out.println("--- Informations détaillées ---");
        System.out.println(parc1);
        System.out.println(camion1);
        System.out.println(voiture1);
        System.out.println(bus1);
        System.out.println(chauffeur1);
        System.out.println(mission1);
    }
}
# Transport Fleet Management System
## Système de Gestion de Parc Automobile d'une Entreprise de Transport

### 📋 Description

Ce projet est une application complète en Java pour la gestion d'un parc automobile d'une entreprise de transport. Il implémente tous les éléments définis dans le diagramme de classe UML fourni.

### 🏗️ Architecture du Projet

```
src/main/java/com/transport/
├── Main.java                           # Classe principale - démonstration
├── model/                              # Modèles de données
│   ├── ParcAuto.java                   # Gestion du parc
│   ├── Vehicle.java                    # Classe abstraite de base
│   ├── Camion.java                     # Type: Camion
│   ├── Voiture.java                    # Type: Voiture
│   ├── Bus.java                        # Type: Bus
│   ├── TypeVehicule.java               # Énumération des types
│   ├── Utilisateur.java                # Classe abstraite utilisateur
│   ├── Chauffeur.java                  # Type: Chauffeur
│   ├── Mission.java                    # Gestion des missions
│   ├── Maintenance.java                # Gestion de la maintenance
│   └── Carburant.java                  # Gestion du carburant
└── service/                            # Services métier
    └── ParcAutoService.java            # Service principal
```

### ✨ Fonctionnalités

#### 1. **Gestion des Véhicules**
- Ajout, suppression et recherche de véhicules
- Trois types de véhicules: Camion, Voiture, Bus
- Suivi du statut (actif, maintenance, hors service)
- Gestion du kilométrage et des informations techniques

#### 2. **Gestion des Chauffeurs**
- Création et gestion des profils chauffeurs
- Suivi des permis et disponibilités
- Assignation aux missions
- Acceptation/Refus de missions

#### 3. **Gestion des Missions**
- Création de missions avec dates et destinations
- Affectation de véhicules et chauffeurs
- Suivi du statut (programmée, en cours, terminée)
- Démarrage et termination des missions

#### 4. **Gestion de la Maintenance**
- Planification des maintenances
- Enregistrement des coûts
- Historique des interventions
- Suivi du statut des véhicules

#### 5. **Gestion du Carburant**
- Suivi de la consommation
- Calcul des coûts
- Support de multiples types de carburant

### 🚀 Installation et Utilisation

#### Prérequis
- Java 11 ou supérieur
- Maven 3.6+

#### Étapes d'installation

```bash
# Cloner le projet
git clone https://github.com/massforgexfit-ux/transport-fleet-management.git
cd transport-fleet-management

# Compiler le projet
mvn clean compile

# Exécuter l'application
mvn exec:java -Dexec.mainClass="com.transport.Main"
```

### 📊 Diagramme de Classe UML

Le projet implémente exactement le diagramme fourni avec:
- Hiérarchie de classe Vehicle (abstraite)
- Hiérarchie de classe Utilisateur (abstraite)
- Relations d'association et de composition
- Tous les attributs et méthodes spécifiés

### 📝 Exemple d'Utilisation

```java
// Initialisation
ParcAutoService service = new ParcAutoService();
ParcAuto parc = new ParcAuto(1, "Parc Principal", "Paris");

// Ajout d'un camion
Camion camion = new Camion(1, "AB-123-CD", "Renault", "T-High", 
                           2022, 5000, "actif", 25.0, "Marchandises");
service.ajouterVehicule(1, camion);

// Création d'une mission
Mission mission = new Mission(1, LocalDate.now(), 
                              LocalDate.now().plusDays(2), "Lyon");
service.creerMission(mission);

// Affectation
service.affecterVehiculeEtChauffeur(1, 1, 1);
mission.demarrerMission();
```

### 🔧 Structure des Classes Principales

#### ParcAuto
- Gère une collection de véhicules
- Méthodes: ajouterVehicule(), listerVehicules(), rechercherVehicule()

#### Vehicle (abstraite)
- Classe de base pour tous les véhicules
- Attributs communs: immatriculation, marque, modèle, statut
- Méthodes abstraites: demarrer(), arreter()

#### Chauffeur extends Utilisateur
- Manage de missions
- Gestion de disponibilité
- Acceptation/Refus de missions

#### Mission
- Affectation de véhicule et chauffeur
- Gestion du statut et des dates
- Démarrage et termination

#### Maintenance
- Planification et suivi
- Historique des interventions
- Calcul des coûts

### 📄 Rapport du Projet

Un rapport détaillé en PDF est disponible à la racine du projet avec:
- Architecture générale du système
- Explication du diagramme UML
- Choix de conception justifiés
- Guide d'utilisation complet
- Code examples

### 🎯 Points d'Extension

Le projet peut être étendu avec:
- **Persistence**: Intégration avec une base de données SQL
- **API REST**: Création d'une API pour l'intégration
- **Interface GUI**: Développement d'une interface Swing ou JavaFX
- **Notifications**: Système d'alertes pour maintenances/missions
- **Rapports**: Génération de rapports PDF/Excel

### 📚 Technologies Utilisées

- **Langage**: Java 11
- **Build Tool**: Maven
- **Sérialisation**: Java Serialization
- **JSON**: Gson (optionnel pour future intégration)

### 👨‍💼 Auteur

**massforgexfit-ux**
- Projet académique - Gestion de parc automobile
- 2026

### 📞 Support

Pour toute question ou problème, veuillez ouvrir une issue sur GitHub.

### 📝 Licence

Ce projet est sous licence MIT.

---

**Bon développement! 🚀**

# Gestion PDR - Maintenance Conditionnement

Application Java 17 Swing/JDBC/MySQL pour l'amélioration de la consommation des pièces de rechange au service Maintenance Conditionnement.

## Données analysées

Les fichiers CSV reçus contiennent :

- un sommaire d'interventions ;
- un catalogue principal de 1 018 pièces (`sara2`) ;
- des feuilles de stock requis par intervention.

La table `PieceRechange` est construite à partir des colonnes du catalogue :

- `Code article`
- `Description de la pièce et outil`
- `Unité`
- `Groupe d'articles`
- `Qté consommée (historique)`
- `Sous-ensemble (réf. plan)`
- `Réf. constructeur`
- `Pièce d'usure`

Deux colonnes de gestion sont ajoutées pour les fonctions de stock : `stock_actuel` et `stock_minimum`.

## Technologies

- Java 17
- Swing
- JDBC
- MySQL
- Apache POI
- Maven
- Architecture MVC + DAO

## Fonctionnalités

- Gestion des pièces : importer, ajouter, modifier, supprimer, rechercher, afficher, consulter le stock.
- Import automatique depuis Excel `.xls/.xlsx` avec Apache POI.
- Import CSV compatible avec les fichiers fournis.
- Gestion des entrées : augmentation automatique du stock.
- Gestion des sorties : diminution automatique du stock et blocage si la sortie dépasse le stock disponible.
- CRUD complet Machines.
- CRUD complet Techniciens.
- Alertes automatiques si `stock_actuel <= stock_minimum`.

## Tables MySQL

Uniquement les tables demandées sont créées :

- `PieceRechange`
- `EntreeStock`
- `SortieStock`
- `Machine`
- `Technicien`
- `AlerteStock`

Le script SQL est disponible dans `database/schema.sql`.

## Configuration MySQL

Par défaut, l'application utilise :

```text
URL      : jdbc:mysql://localhost:3306/pdr_maintenance?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
User     : root
Password :
```

Vous pouvez modifier ces valeurs avec des variables d'environnement ou propriétés Java :

```bash
export PDR_DB_URL="jdbc:mysql://localhost:3306/pdr_maintenance?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export PDR_DB_USER="root"
export PDR_DB_PASSWORD="votre_mot_de_passe"
```

Créer la base avant le lancement :

```sql
CREATE DATABASE IF NOT EXISTS pdr_maintenance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Les tables sont créées automatiquement au démarrage.

## Compilation et exécution

```bash
mvn clean compile
mvn exec:java
```

Classe principale :

```text
com.pdr.App
```

## Utilisation Eclipse

1. Importer le projet comme `Existing Maven Project`.
2. Configurer Java 17.
3. Vérifier la connexion MySQL.
4. Lancer `com.pdr.App`.
5. Menu `Pièces` > `Importer Excel/CSV` pour charger le catalogue.

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

# Version un seul fichier pour VS Code

Le code regroupé est dans :

```text
single-file/PdrApplication.java
```

## Prérequis

- Java 17
- MySQL
- Les JAR suivants dans un dossier `lib/` :
  - MySQL Connector/J
  - Apache POI
  - Apache POI OOXML
  - dépendances transitives Apache POI

Si vous utilisez Maven, la version complète du projet est plus simple à compiler :

```bash
mvn clean compile
mvn exec:java
```

## Base MySQL

Créer la base :

```sql
CREATE DATABASE IF NOT EXISTS pdr_maintenance CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Les tables sont créées automatiquement au démarrage.

## Compilation manuelle exemple

Depuis la racine du projet :

```bash
javac -cp "lib/*" single-file/PdrApplication.java
java -cp "lib/*:single-file" PdrApplication
```

Sous Windows :

```bat
javac -cp "lib/*" single-file\PdrApplication.java
java -cp "lib/*;single-file" PdrApplication
```

## Configuration de connexion

Par défaut :

```text
URL      : jdbc:mysql://localhost:3306/pdr_maintenance?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
User     : root
Password :
```

Variables possibles :

```bash
export PDR_DB_URL="jdbc:mysql://localhost:3306/pdr_maintenance?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
export PDR_DB_USER="root"
export PDR_DB_PASSWORD="votre_mot_de_passe"
```

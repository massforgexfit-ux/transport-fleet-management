package com.transport.model;

/**
 * Énumération des types de véhicules disponibles
 */
public class TypeVehicule {
    private int idType;
    private String libelle;

    public TypeVehicule(int idType, String libelle) {
        this.idType = idType;
        this.libelle = libelle;
    }

    public int getIdType() {
        return idType;
    }

    public void setIdType(int idType) {
        this.idType = idType;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public void ajouterType() {
        // Logique pour ajouter un type
    }

    public void modifierType() {
        // Logique pour modifier un type
    }

    public void supprimerType() {
        // Logique pour supprimer un type
    }

    @Override
    public String toString() {
        return "TypeVehicule{" +
                "idType=" + idType +
                ", libelle='" + libelle + '\'' +
                '}';
    }
}
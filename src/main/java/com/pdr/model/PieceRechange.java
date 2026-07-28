package com.pdr.model;

public class PieceRechange {
    private long id;
    private String codeArticle;
    private String descriptionPiece;
    private String unite;
    private String groupeArticles;
    private int quantiteConsommeeHistorique;
    private String sousEnsemble;
    private String referenceConstructeur;
    private boolean pieceUsure;
    private int stockActuel;
    private int stockMinimum;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCodeArticle() {
        return codeArticle;
    }

    public void setCodeArticle(String codeArticle) {
        this.codeArticle = codeArticle;
    }

    public String getDescriptionPiece() {
        return descriptionPiece;
    }

    public void setDescriptionPiece(String descriptionPiece) {
        this.descriptionPiece = descriptionPiece;
    }

    public String getUnite() {
        return unite;
    }

    public void setUnite(String unite) {
        this.unite = unite;
    }

    public String getGroupeArticles() {
        return groupeArticles;
    }

    public void setGroupeArticles(String groupeArticles) {
        this.groupeArticles = groupeArticles;
    }

    public int getQuantiteConsommeeHistorique() {
        return quantiteConsommeeHistorique;
    }

    public void setQuantiteConsommeeHistorique(int quantiteConsommeeHistorique) {
        this.quantiteConsommeeHistorique = quantiteConsommeeHistorique;
    }

    public String getSousEnsemble() {
        return sousEnsemble;
    }

    public void setSousEnsemble(String sousEnsemble) {
        this.sousEnsemble = sousEnsemble;
    }

    public String getReferenceConstructeur() {
        return referenceConstructeur;
    }

    public void setReferenceConstructeur(String referenceConstructeur) {
        this.referenceConstructeur = referenceConstructeur;
    }

    public boolean isPieceUsure() {
        return pieceUsure;
    }

    public void setPieceUsure(boolean pieceUsure) {
        this.pieceUsure = pieceUsure;
    }

    public int getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(int stockActuel) {
        this.stockActuel = stockActuel;
    }

    public int getStockMinimum() {
        return stockMinimum;
    }

    public void setStockMinimum(int stockMinimum) {
        this.stockMinimum = stockMinimum;
    }

    @Override
    public String toString() {
        return codeArticle + " - " + descriptionPiece;
    }
}

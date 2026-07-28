package com.pdr.model;

import java.time.LocalDateTime;

public class AlerteStock {
    private long id;
    private long pieceId;
    private String codeArticle;
    private String message;
    private LocalDateTime dateAlerte;
    private boolean resolue;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPieceId() {
        return pieceId;
    }

    public void setPieceId(long pieceId) {
        this.pieceId = pieceId;
    }

    public String getCodeArticle() {
        return codeArticle;
    }

    public void setCodeArticle(String codeArticle) {
        this.codeArticle = codeArticle;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDateAlerte() {
        return dateAlerte;
    }

    public void setDateAlerte(LocalDateTime dateAlerte) {
        this.dateAlerte = dateAlerte;
    }

    public boolean isResolue() {
        return resolue;
    }

    public void setResolue(boolean resolue) {
        this.resolue = resolue;
    }
}

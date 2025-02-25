package com.example.demo7.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Competence {
//    private final StringProperty id_Categorie;
    private final StringProperty nomCompetence;
    private final StringProperty codeCompetence;

    public Competence(String nom_Competence, String code_Competence) {
        this.nomCompetence = new SimpleStringProperty(nom_Competence);
        this.codeCompetence = new SimpleStringProperty(code_Competence);
    }

    //Getters propriétés
    public StringProperty getCodeCompetenceProperty() { return codeCompetence; }
    public StringProperty getNomProperty() { return nomCompetence; }

    // Getters classiques
    public String getCodeCompetence() { return codeCompetence.get(); }
    public String getNomCompetence() { return nomCompetence.get(); }

    //Setters
    public void setCodeCompetence(String code) { this.codeCompetence.set(code); }
    public void setNomCompetence(String nom) { this.nomCompetence.set(nom); }

}

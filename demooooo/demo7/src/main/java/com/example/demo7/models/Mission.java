package com.example.demo7.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Mission {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty description;
    private final ObjectProperty<LocalDate> dateDebut;
    private final IntegerProperty duree;
    private final StringProperty statut;

    public Mission(int id, String nom, String description, LocalDate dateDebut, int duree, String statut) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
        this.dateDebut = new SimpleObjectProperty<>(dateDebut);
        this.duree = new SimpleIntegerProperty(duree);
        this.statut = new SimpleStringProperty(statut);
    }


    // Getters pour les propriétés
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public ObjectProperty dateDebutProperty() { return dateDebut; }
    public IntegerProperty dureeProperty() { return duree; }
    public StringProperty statutProperty() { return statut; }

    // Getters classiques
    public int getId() { return id.get(); }
    public String getNom() { return nom.get(); }
    public String getDescription() { return description.get(); }
    public LocalDate getDateDebut() { return dateDebut.get(); }
    public int getDuree() { return duree.get(); }
    public String getStatut() { return statut.get(); }

    // Setters
    public void setId(int id) { this.id.set(id); }
    public void setNom(String nom) { this.nom.set(nom); }
    public void setDescription(String description) { this.description.set(description); }
    public void setDateDebut(LocalDate dateDebut) { this.dateDebut.set(dateDebut); }
    public void setDuree(int duree) { this.duree.set(duree); }
    public void setStatut(String statut) { this.statut.set(statut); }

    @Override
    public String toString() {
        return nom.get();  // Permet d'afficher directement le nom dans les ComboBox
    }
}

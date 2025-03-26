package com.example.demo7.models;

import javafx.beans.property.*;

public class Personnel {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nom;
    private final SimpleStringProperty prenom;
    private final SimpleStringProperty email;
    private final SimpleStringProperty role;

    // Constructeur
    public Personnel(int id, String nom, String prenom, String email, String role) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.email = new SimpleStringProperty(email);
        this.role = new SimpleStringProperty(role);
    }

    // Getters pour récupérer les valeurs
    public int getId() { return id.get(); }
    public String getNom() { return nom.get(); }
    public String getPrenom() { return prenom.get(); }
    public String getEmail() { return email.get(); }
    public String getRole() { return role.get(); }

    // Propriétés pour le TableView
    public IntegerProperty idProperty() { return id; }
    public StringProperty nomProperty() { return nom; }
    public StringProperty prenomProperty() { return prenom; }
    public StringProperty emailProperty() { return email; }
    public StringProperty roleProperty() { return role; }

    // Setter si nécessaire
    public void setRole(String newRole) { this.role.set(newRole); }

    @Override
    public String toString() {
        return nom.get() + " " + prenom.get();
    }

}

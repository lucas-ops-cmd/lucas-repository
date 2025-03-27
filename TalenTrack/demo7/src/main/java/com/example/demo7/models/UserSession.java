package com.example.demo7.models;

import com.example.demo7.database.DatabaseConnection;
import com.example.demo7.controllers.ConnexionController;


public class UserSession {
    private static UserSession instance;
    private int idPersonne;


    private UserSession(String email) {
        this.idPersonne = DatabaseConnection.getIdPersonnel(email);
    }

    public static void startSession(String email) {
        if (instance == null) {
            instance = new UserSession(email);
        }
    }

    public static UserSession getInstance() {
        return instance;
    }

    public int getIdPersonnel() {
        return idPersonne;
    }

    public static void clearSession() {
        instance = null;
    }
}

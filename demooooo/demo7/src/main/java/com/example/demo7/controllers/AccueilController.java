package com.example.demo7.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.IOException;
import java.util.Objects;

public class AccueilController {

    @FXML
    private Button btnConnexion;

    @FXML
    private void onConnexionClick() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/com/example/demo7/connexion-view.fxml")));
            Parent root = loader.load();

            Stage stage = (Stage) btnConnexion.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la page de connexion : " + e.getMessage());
        }
    }
}

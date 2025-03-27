package com.example.demo7.controllers;

import com.example.demo7.database.DatabaseConnection;
import com.example.demo7.models.Mission;
import com.example.demo7.models.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;

/**
 * Contrôleur pour la gestion de l'interface Employé.
 * Permet aux employés de consulter leurs missions.
 */
public class EmployeController {

    /**
     * Table des missions attribuées à l'employé.
     */
    @FXML private TableView<?> tableMissions;

    /**
     * Colonnes de la table des missions.
     */
    @FXML private TableView<Mission> tableMissions;
    @FXML private TableColumn<Mission, Integer> colMissionId;
    @FXML private TableColumn<Mission, String> colMissionNom;
    @FXML private TableColumn<Mission, Date> colDateDebut;
    @FXML private TableColumn<Mission, Integer> colDuree;

    private ObservableList<Mission> missionList = FXCollections.observableArrayList();


    /**
     * Bouton permettant de retourner à l'accueil.
     */
    @FXML private Button btnRetourAccueil;

    /**
     * Initialise le contrôleur et affiche un message indiquant que l'interface Employé est chargée.
     */
    @FXML
    private void initialize(){
        // Configuration des colonnes missions
        colMissionId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colMissionNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colDateDebut.setCellValueFactory(cellData -> cellData.getValue().dateDebutProperty());
        colDuree.setCellValueFactory(cellData -> cellData.getValue().dureeProperty().asObject());

        loadMissionData();

        System.out.println("Interface Employé chargée !");
    }
    private void loadMissionData() {
        int idPersonne= UserSession.getInstance().getIdPersonnel();
        missionList.setAll(DatabaseConnection.getMissionsPers(idPersonne));
        tableMissions.setItems(missionList);
        tableMissions.refresh();
    }

    /**
     * Gère l'événement de retour à l'accueil.
     */


    @FXML
    private void handleRetourAccueil() {
        System.out.println("Retour à l'accueil");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo7/accueil-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRetourAccueil.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
            UserSession.clearSession();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour à l'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }


}

package com.example.demo7.controllers;

import com.example.demo7.database.DatabaseConnection;
import com.example.demo7.models.Mission;
import com.example.demo7.models.Personnel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

public class AdminController {


    @FXML private TableView<Personnel> tableUsers;
    @FXML private TableColumn<Personnel, Integer> colUserId;
    @FXML private TableColumn<Personnel, String> colUserNom;
    @FXML private TableColumn<Personnel, String> colUserPrenom;
    @FXML private TableColumn<Personnel, String> colUserEmail;
    @FXML private TableColumn<Personnel, String> colUserRole;
    @FXML private Button btnPromoteChef;
    @FXML private Button btnPromoteAdmin;
    @FXML private Button btnDowngradeToEmployee;
    @FXML private Button btnDeleteUser;
    @FXML private Button btnAddUser;


    @FXML private TableView<Mission> tableMissions;
    @FXML private TableColumn<Mission, Integer> colMissionId;
    @FXML private TableColumn<Mission, String> colMissionNom;
    @FXML private TableColumn<Mission, String> colMissionDesc;
    @FXML private Button btnAddMission;
    @FXML private Button btnUpdateMission;
    @FXML private Button btnDeleteMission;

    @FXML private ComboBox<Mission> comboMissions;
    @FXML private ComboBox<Personnel> comboUsers;
    @FXML private Button btnAffecterMission;
    @FXML private Button btnRetirerMission;

    @FXML private Button btnRetourAccueil;

    @FXML private TextField txtMissionName;
    @FXML private DatePicker dpMissionStartDate;
    @FXML private TextField txtMissionDuration;
    @FXML private TableColumn<Mission, String> colMissionPersonnel;
    @FXML private TableColumn<Mission, String> colMissionStatut;
    @FXML private ComboBox<String> comboMissionStatus; // Ajout du ComboBox pour le statut

    private ObservableList<Personnel> personnelList = FXCollections.observableArrayList();
    private ObservableList<Mission> missionList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Configuration des colonnes utilisateurs
        colUserId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colUserNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colUserPrenom.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        colUserEmail.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        colUserRole.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        // Configuration des colonnes missions
        colMissionId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colMissionNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colMissionDesc.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());

        // Initialisation des valeurs de statut de mission
        if (comboMissionStatus != null) {
            comboMissionStatus.setItems(FXCollections.observableArrayList(
                    "Préparation", "Planifiée", "En cours", "Terminée"
            ));
        } else {
            System.err.println("❌ Erreur : comboMissionStatus est NULL !");
        }

        loadPersonnelData();
        loadMissionData();

        comboUsers.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Personnel personnel) {
                return personnel == null ? "" : personnel.getNom() + " " + personnel.getPrenom();
            }

            @Override
            public Personnel fromString(String s) {
                return null; // Non utilisé
            }
        });

        colMissionPersonnel.setCellValueFactory(cellData -> {
            String personnels = DatabaseConnection.getPersonnelAffecte(cellData.getValue().getId());
            return new SimpleStringProperty(personnels);
        });
        colMissionStatut.setCellValueFactory(cellData -> cellData.getValue().statutProperty());

    }

    private void loadPersonnelData() {
        personnelList.setAll(DatabaseConnection.getPersonnels());
        tableUsers.setItems(personnelList);
        comboUsers.setItems(personnelList);
    }

    private void loadMissionData() {
        missionList.setAll(DatabaseConnection.getMissions());
        tableMissions.setItems(missionList);
        comboMissions.setItems(missionList);
    }

    @FXML
    private void handlePromoteChef() {
        Personnel selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.updateRole(selected.getId(), "Chef de projet");
            selected.setRole("Chef de projet");
            tableUsers.refresh();
        }
    }

    @FXML
    private void handlePromoteAdmin() {
        Personnel selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.updateRole(selected.getId(), "Admin");
            selected.setRole("Admin");
            tableUsers.refresh();
        }
    }

    @FXML
    private void handleDowngradeToEmployee() {
        Personnel selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.updateRole(selected.getId(), "Personnel");
            selected.setRole("Personnel");
            tableUsers.refresh();
        }
    }

    @FXML
    private void handleDeleteUser() {
        Personnel selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.deleteUser(selected.getId());
            personnelList.remove(selected);
        }
    }


    @FXML
    private void handleAddMission() {
        String nom = txtMissionName.getText();
        LocalDate dateDebut = dpMissionStartDate.getValue();
        int duree;

        try {
            duree = Integer.parseInt(txtMissionDuration.getText());
        } catch (NumberFormatException e) {
            System.err.println("❌ Erreur : La durée doit être un nombre valide !");
            return;
        }

        if (nom.isEmpty() || dateDebut == null || duree <= 0) {
            System.out.println("❌ Veuillez remplir tous les champs !");
            return;
        }

        DatabaseConnection.addMission(nom, "Description automatique", dateDebut, duree);
        loadMissionData();
    }

    @FXML
    private void handleUpdateMission() {
        Mission selected = tableMissions.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int duree;
            try {
                duree = Integer.parseInt(txtMissionDuration.getText());
            } catch (NumberFormatException e) {
                System.err.println("❌ Erreur : La durée doit être un nombre valide !");
                return;
            }
            String statut = comboMissionStatus.getValue();

            DatabaseConnection.updateMission(selected.getId(), duree, statut);
            loadMissionData();
        }
    }

    @FXML
    private void handleDeleteMission() {
        Mission selected = tableMissions.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.deleteMission(selected.getId());
            missionList.remove(selected);
        }
    }

    @FXML
    private void handleAffecterMission() {
        Personnel personnel = comboUsers.getSelectionModel().getSelectedItem();
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();
        if (personnel != null && mission != null) {
            DatabaseConnection.assignPersonnelToMission(personnel.getId(), mission.getId());
            DatabaseConnection.updateMissionStatus(mission.getId(), "Planifiée");
            System.out.println("Affectation réussie !");
        }
        loadMissionData();
    }


    @FXML
    private void handleRetirerMission() {
        Personnel personnel = comboUsers.getSelectionModel().getSelectedItem();
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();
        if (personnel != null && mission != null) {
            DatabaseConnection.removePersonnelFromMission(personnel.getId(), mission.getId());
            System.out.println("Désaffectation réussie !");
        }
        loadMissionData();
    }

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
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour à l'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }


}

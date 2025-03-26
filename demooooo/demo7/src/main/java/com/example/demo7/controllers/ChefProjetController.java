package com.example.demo7.controllers;

import com.example.demo7.database.DatabaseConnection;
import com.example.demo7.models.Mission;
import com.example.demo7.models.PersonneSugereeComp;
import com.example.demo7.models.Personnel;
import com.example.demo7.models.UserSession;
import javafx.beans.property.SimpleStringProperty;
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

public class ChefProjetController {

    @FXML private TableView<Mission> tableMissions;
    @FXML private TableColumn<Mission, Integer> colMissionId;
    @FXML private TableColumn<Mission, String> colMissionNom;
    @FXML private TableColumn<Mission, Date> colDateDebut;
    @FXML private TableColumn<?, ?> colMissionDesc;
    @FXML private TableColumn<Mission, String> colMissionPersonnel;
    @FXML private TableColumn<Mission, String> colMissionStatut;
    @FXML private TableColumn<Mission, Integer> colDuree;
    @FXML private TableColumn<Mission, String> colMissionCompetences;

    @FXML private ComboBox<PersonneSugereeComp> comboPersoSugere;
    @FXML private ComboBox<Mission> comboMissions;
    @FXML private ComboBox<Personnel> comboUsers;
    @FXML private ComboBox<String> comboCompetences;

    @FXML private TextField txtNbrPerson;

    @FXML private TableView<String[]> tableCompetences;
    @FXML private TableColumn<String[], String> colCompetence;
    @FXML private TableColumn<String[], String> colNbrPerson;

    @FXML private Button btnRetourAccueil;

    private ObservableList<Personnel> personnelSugereList = FXCollections.observableArrayList();
    private ObservableList<Personnel> personnelList = FXCollections.observableArrayList();
    private ObservableList<Mission> missionList = FXCollections.observableArrayList();
    private ObservableList<String[]> competencesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configuration des colonnes missions
        colMissionId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colMissionNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colDateDebut.setCellValueFactory(cellData -> cellData.getValue().dateDebutProperty());
        colDuree.setCellValueFactory(cellData -> cellData.getValue().dureeProperty().asObject());
        //Afficher les compétances des missions
        colMissionCompetences.setCellValueFactory(cellData -> {
            String competence = DatabaseConnection.getCompetenceMission(cellData.getValue().getId());
            return new SimpleStringProperty(competence);
        });

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


        // Configuration du form ajouter competence pour une mission
        colCompetence.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[0]));
        colNbrPerson.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[1]));

        System.out.println("Interface Chef de Projet chargée !");
    }

    private void loadMissionData() {
        int idPersonne=UserSession.getInstance().getIdPersonnel();
        missionList.setAll(DatabaseConnection.getMissionsPers(idPersonne));
        tableMissions.setItems(missionList);
        comboMissions.setItems(missionList);
        tableMissions.refresh();
    }

    private void loadPersonnelData() {
        personnelList.setAll(DatabaseConnection.getPersonnels());
        comboUsers.setItems(personnelList);
    }

    @FXML
    private void handleAffecterMission() {
        Personnel personnelSug = null;
        if (comboPersoSugere.getSelectionModel().getSelectedItem() != null) {
            personnelSug = comboPersoSugere.getSelectionModel().getSelectedItem().getPersonnel();
        }
        Personnel personnel = comboUsers.getSelectionModel().getSelectedItem();
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();
        if (personnel != null && mission != null) {
            DatabaseConnection.assignPersonnelToMission(personnel.getId(), mission.getId());
            if (DatabaseConnection.validateMissionStatus(mission.getId())) {
                DatabaseConnection.updateMissionStatus(mission.getId(), "Planifiée");
            }
            System.out.println("Affectation réussie !");

        }else if (personnelSug != null && mission != null) {
            DatabaseConnection.assignPersonnelToMission(personnelSug.getId(), mission.getId());
            System.out.println("Affectation réussie !");

        }
        loadMissionData();
    }

    @FXML
    private void handleRetirerMission() {
        Personnel personnelSug = null;
        if (comboPersoSugere.getSelectionModel().getSelectedItem() != null) {
            personnelSug = comboPersoSugere.getSelectionModel().getSelectedItem().getPersonnel();
        }
        Personnel personnel = comboUsers.getSelectionModel().getSelectedItem();
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();
        if (personnel != null && mission != null) {
            DatabaseConnection.removePersonnelFromMission(personnel.getId(), mission.getId());
            System.out.println("Désaffectation réussie !");
        }else if (personnelSug != null && mission != null) {
            DatabaseConnection.removePersonnelFromMission(personnelSug.getId(), mission.getId());
            System.out.println("Désaffectation réussie !");

        }
        loadMissionData();
    }

    @FXML
    private void handleSugererPersonnel() {
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();
        personnelSugereList.setAll(DatabaseConnection.getPersoCompetenceMission(mission.getId()));
        comboPersoSugere.setItems(DatabaseConnection.getListPersonneSugereeComp(mission.getId(),personnelSugereList));

    }

    @FXML
    private void loadCompetences() {
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();
        ObservableList<String> competencesList = DatabaseConnection.getCompetences(mission.getId());
        comboCompetences.setItems(competencesList);
    }

    @FXML
    private void loadMissionCompetences() {
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();
        competencesList.setAll(DatabaseConnection.getMissionCompetences(mission.getId()));
        tableCompetences.setItems(competencesList);
        loadCompetences();
    }

    @FXML
    private void handleAddCompetence() {
        String selectedCompetence = comboCompetences.getValue();
        String nbrPersonText = txtNbrPerson.getText();
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();

        if (selectedCompetence == null || nbrPersonText.isEmpty() ) {
            System.out.println("❌ Please fill in all fields!");
            return;
        }

        int nbrPerRequis;
        try {
            nbrPerRequis = Integer.parseInt(nbrPersonText);
        } catch (NumberFormatException e) {
            System.out.println("❌ The required number of people must be a valid number!");
            return;
        }
        if (nbrPerRequis <= 0) {
            System.out.println("❌ The required number of people must be greater than 0!");
        }

        DatabaseConnection.addCompetenceToMission(selectedCompetence, nbrPerRequis, mission.getId());
        if (DatabaseConnection.validateMissionStatus(mission.getId())) {
            DatabaseConnection.updateMissionStatus(mission.getId(), "Planifié");
        }
        loadCompetences();
        loadMissionCompetences();
        loadMissionData();

    }

    @FXML
    private void handleDeleteCompetence() {
        String[] selectedRow = tableCompetences.getSelectionModel().getSelectedItem();
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();
        if (mission == null) {
            System.out.println("❌ Please select a mission first!");
            return;
        }

        if (selectedRow == null) {
            System.out.println("❌ Please select a competence to remove!");
            return;
        }
        String selectedCompetence = selectedRow[0];
        DatabaseConnection.removeCompetenceFromMission(mission.getId(), selectedCompetence);

        loadCompetences();
        loadMissionCompetences();
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
            UserSession.clearSession();
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour à l'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
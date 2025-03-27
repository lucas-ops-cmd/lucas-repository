package com.example.demo7.controllers;

import com.example.demo7.database.DatabaseConnection;
import com.example.demo7.models.Competence;
import com.example.demo7.models.Mission;
import com.example.demo7.models.PersonneSugereeComp;
import com.example.demo7.models.Personnel;
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
import java.time.LocalDate;
import java.util.Date;

/**
 * Contrôleur pour la vue d'administration.
 * Cette classe gère les interactions utilisateur avec la vue d'administration,
 * y compris la gestion des utilisateurs, des missions et des compétences.
 */
public class AdminController {



    // TableView et TableColumn pour les utilisateurs
    @FXML private TableView<Personnel> tableUsers;
    @FXML private TableColumn<Personnel, Integer> colUserId;
    @FXML private TableColumn<Personnel, String> colUserNom;
    @FXML private TableColumn<Personnel, String> colUserPrenom;
    @FXML private TableColumn<Personnel, String> colUserEmail;
    @FXML private TableColumn<Personnel, String> colUserRole;

    // Boutons pour la gestion des utilisateurs
    @FXML private Button btnPromoteChef;
    @FXML private Button btnPromoteAdmin;
    @FXML private Button btnDowngradeToEmployee;
    @FXML private Button btnDeleteUser;
    @FXML private Button btnAddEmployee;

    // Liste observable des personnels suggérés
    private ObservableList<Personnel> personnelSugereList = FXCollections.observableArrayList();
    @FXML private ComboBox<PersonneSugereeComp> comboPersoSugere;

    // TableView et TableColumn pour les missions
    @FXML private TableView<Mission> tableMissions;
    @FXML private TableColumn<Mission, Integer> colMissionId;
    @FXML private TableColumn<Mission, String> colMissionNom;
    @FXML private TableColumn<Mission, Date> colDateDebut;
    @FXML private TableColumn<Mission, String> colMissionPersonnel;
    @FXML private TableColumn<Mission, String> colMissionStatut;
    @FXML private TableColumn<Mission, Integer> colDuree;
    @FXML private TableColumn<Mission, String> colMissionCompetences;

    // TableView et TableColumn pour les formations
    @FXML private TableView<Mission> tableFormations;
    @FXML private TableColumn<Mission, Integer> colFormationId;
    @FXML private TableColumn<Mission, String> colFormationNom;
    @FXML private TableColumn<Mission, Date> colDateDebutFor;
    @FXML private TableColumn<Mission, String> colFormationPersonnel;
    @FXML private TableColumn<Mission, String> colFormationStatut;
    @FXML private TableColumn<Mission, Integer> colDureeFor;
    @FXML private TableColumn<Mission, String> colFormationCompetences;

    // Boutons pour la gestion des missions
    @FXML private Button btnAddMission;
    @FXML private Button btnUpdateMission;
    @FXML private Button btnDeleteMission;

    // ComboBox pour sélectionner des missions et des utilisateurs
    @FXML private ComboBox<Mission> comboMissions;
    @FXML private ComboBox<Personnel> comboUsers;

    // Boutons pour affecter et retirer des missions
    @FXML private Button btnAffecterMission;
    @FXML private Button btnRetirerMission;

    // Bouton pour retourner à l'accueil
    @FXML private Button btnRetourAccueil;

    // Champs de texte pour la gestion des missions
    @FXML private TextField txtMissionName;
    @FXML private DatePicker dpMissionStartDate;
    @FXML private TextField txtMissionDuration;
    @FXML private TextField txtMissionNbrTotalRequis;

    // Champs de texte pour la gestion des compétences
    @FXML private TextField txtNbrPerson;
    @FXML private ComboBox<String> comboCompetences;

    // TableView et TableColumn pour les compétences
    @FXML private TableView<String[]> tableCompetences;
    @FXML private TableColumn<String[], String> colCompetence;
    @FXML private TableColumn<String[], String> colNbrPerson;

    // Champs de texte pour la gestion des formation
    @FXML private TextField txtFormationName;
    @FXML private DatePicker dpFormationStartDate;
    @FXML private TextField txtFormationDuration;
    @FXML private TextField txtFormationNbrTotalRequis;
    @FXML public TextField txtNbrPersonFor;

    // Buttons pour la gestion des formations
    @FXML public Button btnAddFormation;
    @FXML public Button btnUpdateFormation;
    @FXML public Button btnDeleteFormation;

    //ComboBox pour selectionner formation, competence et user
    @FXML public ComboBox<Mission> comboFormations;
    @FXML public ComboBox<String> comboCompetencesFormation;
    @FXML private ComboBox<Personnel> comboUsersFor;

    // TableView et TableColumn pour les compétences formation
    @FXML private TableView<String[]> tableCompetencesFormation;
    @FXML private TableColumn<String[], String> colCompetenceFormation;
    @FXML private TableColumn<String[], String> colNbrPersonFormation;

    // Listes observables pour les personnels, missions et compétences
    private ObservableList<Personnel> personnelList = FXCollections.observableArrayList();
    private ObservableList<Mission> missionList = FXCollections.observableArrayList();
    private ObservableList<String[]> competencesList = FXCollections.observableArrayList();
    private ObservableList<Mission> formationList = FXCollections.observableArrayList();
    private ObservableList<String[]> competencesFormationList = FXCollections.observableArrayList();
    private ObservableList<Personnel> personnelFormationList = FXCollections.observableArrayList();
    /**
     * Méthode d'initialisation appelée après le chargement du fichier FXML.
     * Cette méthode configure les colonnes des TableView et charge les données initiales.
     */
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
        colDateDebut.setCellValueFactory(cellData -> cellData.getValue().dateDebutProperty());
        colDuree.setCellValueFactory(cellData -> cellData.getValue().dureeProperty().asObject());


        // Configuration de la colonne des compétences des missions
        colMissionCompetences.setCellValueFactory(cellData -> {
            String competence = DatabaseConnection.getCompetenceMission(cellData.getValue().getId());
            return new SimpleStringProperty(competence);
        });

        // Configuration de la colonne des compétences des missions
        colMissionCompetences.setCellValueFactory(cellData -> {
            String competence = DatabaseConnection.getCompetenceMission(cellData.getValue().getId());
            return new SimpleStringProperty(competence);
        });

        // Configuration des colonnes formations
        colFormationId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        colFormationNom.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        colDateDebutFor.setCellValueFactory(cellData -> cellData.getValue().dateDebutProperty());
        colDureeFor.setCellValueFactory(cellData -> cellData.getValue().dureeProperty().asObject());
        colFormationStatut.setCellValueFactory(cellData -> cellData.getValue().statutProperty());
        // Configuration de la colonne des compétences des formations
        colFormationCompetences.setCellValueFactory(cellData -> {
            String competence = DatabaseConnection.getCompetenceMission(cellData.getValue().getId());
            return new SimpleStringProperty(competence);
        });

        loadPersonnelData();
        loadMissionData();
        loadFormationData();
//        handleAssignCompetences();

        // Configuration du ComboBox pour les utilisateurs
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

        // Configuration de la colonne du personnel affecté à une mission
        colMissionPersonnel.setCellValueFactory(cellData -> {
            String personnels = DatabaseConnection.getPersonnelAffecte(cellData.getValue().getId());
            return new SimpleStringProperty(personnels);
        });
        colMissionStatut.setCellValueFactory(cellData -> cellData.getValue().statutProperty());

        // Configuration des colonnes pour les compétences missions
        colCompetence.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[1]));
        colNbrPerson.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[2]));

        // Configuration des colonnes pour les compétences formations
        colCompetenceFormation.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[1]));
        colNbrPersonFormation.setCellValueFactory(param -> new SimpleStringProperty(param.getValue()[2]));

        // Configuration de la colonne du personnel affecté à une formation
        colFormationPersonnel.setCellValueFactory(cellData -> {
            String personnels = DatabaseConnection.getPersonnelAffecte(cellData.getValue().getId());
            return new SimpleStringProperty(personnels);
        });

        // Configuration du ComboBox pour les utilisateurs formation
        comboUsersFor.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Personnel personnel) {
                return personnel == null ? "" : personnel.getNom() + " " + personnel.getPrenom();
            }

            @Override
            public Personnel fromString(String s) {
                return null; // Non utilisé
            }
        });
    }

    /**
     * Méthode pour charger les données des personnels.
     */
    private void loadPersonnelData() {
        personnelList.setAll(DatabaseConnection.getPersonnels());
        tableUsers.setItems(personnelList);
        comboUsers.setItems(personnelList);
        comboUsersFor.setItems(personnelList);
        tableUsers.refresh();

    }

    /**
     * Méthode pour charger les données des missions.
     */
    private void loadMissionData() {
        missionList.setAll(DatabaseConnection.getMissions("Mission"));
        tableMissions.setItems(missionList);
        comboMissions.setItems(missionList);
        tableMissions.refresh();
//        handleAssignCompetences();

    }

    /**
     * Méthode pour charger les compétences d'une mission et d'une formation sélectionnée.
     */
    @FXML
    private void loadCompetences() {
        if (comboMissions.getSelectionModel().getSelectedItem() != null) {
            Mission mission = comboMissions.getSelectionModel().getSelectedItem();
            ObservableList<String> competencesList = DatabaseConnection.getCompetences(mission.getId());
            comboCompetences.setItems(competencesList);
        } else if (comboFormations.getSelectionModel().getSelectedItem() != null) {
            // Compétences Formation
            Mission formation = comboFormations.getSelectionModel().getSelectedItem();
            ObservableList<String> competencesFormationList = DatabaseConnection.getCompetences(formation.getId());
            comboCompetencesFormation.setItems(competencesFormationList);
        }

    }

    /**
     * Méthode pour charger les compétences d'une mission et d'une formation avec le nombre de personnes requises.
     */
    @FXML
    private void loadMissionCompetences() {
        if (comboMissions.getSelectionModel().getSelectedItem() != null) {
            Mission mission = comboMissions.getSelectionModel().getSelectedItem();
            competencesList.setAll(DatabaseConnection.getMissionCompetences(mission.getId()));
            tableCompetences.setItems(competencesList);
        }

        if (comboFormations.getSelectionModel().getSelectedItem() != null) {
            Mission formation = comboFormations.getSelectionModel().getSelectedItem();
            competencesFormationList.setAll(DatabaseConnection.getMissionCompetences(formation.getId()));
            tableCompetencesFormation.setItems(competencesFormationList);
        }

        loadCompetences();
    }

    /**
     * Méthode pour charger les données des formations.
     */
    private void loadFormationData() {
        formationList.setAll(DatabaseConnection.getMissions("Formation"));
        tableFormations.setItems(formationList);
        comboFormations.setItems(formationList);
        tableFormations.refresh();
//        handleAssignCompetences();
    }
    /**
     * Méthode pour promouvoir un utilisateur au rôle de chef de projet.
     */
    @FXML
    private void handlePromoteChef() {
        Personnel selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.updateRole(selected.getId(), "Chef de projet");
            selected.setRole("Chef de projet");
            tableUsers.refresh();
        }
    }

    /**
     * Méthode pour promouvoir un utilisateur au rôle d'administrateur.
     */
    @FXML
    private void handlePromoteAdmin() {
        Personnel selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.updateRole(selected.getId(), "Admin");
            selected.setRole("Admin");
            tableUsers.refresh();
        }
    }

    /**
     * Méthode pour rétrograder un utilisateur au rôle d'employé.
     */
    @FXML
    private void handleDowngradeToEmployee() {
        Personnel selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.updateRole(selected.getId(), "Personnel");
            selected.setRole("Personnel");
            tableUsers.refresh();
        }
    }

    /**
     * Méthode pour supprimer un utilisateur.
     */
    @FXML
    private void handleDeleteUser() {
        Personnel selected = tableUsers.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.deleteUser(selected.getId());
            personnelList.remove(selected);
        }
    }

    /**
     * Méthode pour suggérer du personnel pour une mission.
     */
    @FXML
    private void handleSugererPersonnel() {
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();
        personnelSugereList.setAll(DatabaseConnection.getPersoCompetenceMission(mission.getId()));
        comboPersoSugere.setItems(DatabaseConnection.getListPersonneSugereeComp(mission.getId(), personnelSugereList));
    }

    /**
     * Méthode pour ouvrir la fenêtre d'ajout d'un employé.
     */
    @FXML
    private void handleAddEmployee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/demo7/add-employee-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));

            stage.setTitle("Add Employee");
            Stage mainStage = (Stage) btnAddEmployee.getScene().getWindow();
            stage.setX(mainStage.getX() + mainStage.getWidth() / 2 - stage.getWidth() / 2);
            stage.setY(mainStage.getY() + mainStage.getHeight() / 2 - stage.getHeight() / 2);
            stage.show();
            loadPersonnelData();
        } catch (IOException e) {
            System.err.println("❌ Error: Failed to open the modal.");
            e.printStackTrace();
        }
    }

    /**
     * Méthode pour ajouter une nouvelle mission.
     */
    @FXML
    private void handleAddMission() {
        String nom = txtMissionName.getText();
        LocalDate dateDebut = dpMissionStartDate.getValue();
        int duree;
        int nbrTotalRequis;

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

        try {
            nbrTotalRequis = Integer.parseInt(txtMissionNbrTotalRequis.getText());
        } catch (NumberFormatException e) {
            System.err.println("❌ Erreur : Le Nombre total requis doit être un nombre valide !");
            return;
        }

        DatabaseConnection.addMission(nom, "Description automatique", dateDebut, duree, nbrTotalRequis, "Mission");
        loadMissionData();
    }

    /**
     * Méthode pour ajouter une compétence à une mission.
     */
    @FXML
    private void handleAddCompetence() {
        String selectedCompetence = comboCompetences.getValue();
        String nbrPersonText = txtNbrPerson.getText();
        Mission mission = comboMissions.getSelectionModel().getSelectedItem();

        if (selectedCompetence == null || nbrPersonText.isEmpty()) {
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

    /**
     * Méthode pour supprimer une compétence d'une mission.
     */
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

    /**
     * Méthode pour mettre à jour une mission.
     */
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

            DatabaseConnection.updateMission(selected.getId(), duree, selected.getStatut());
            loadMissionData();
        }
    }

    /**
     * Méthode pour supprimer une mission.
     */
    @FXML
    private void handleDeleteMission() {
        Mission selected = tableMissions.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.deleteMission(selected.getId());
            missionList.remove(selected);
        }
    }

    /**
     * Méthode pour affecter une mission à un utilisateur.
     */
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
        } else if (personnelSug != null && mission != null) {
            DatabaseConnection.assignPersonnelToMission(personnelSug.getId(), mission.getId());
            System.out.println("Affectation réussie !");
        }
        loadMissionData();
    }

    /**
     * Méthode pour retirer une mission d'un utilisateur.
     */
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
        } else if (personnelSug != null && mission != null) {
            DatabaseConnection.removePersonnelFromMission(personnelSug.getId(), mission.getId());
            System.out.println("Désaffectation réussie !");
        }
        loadMissionData();
    }
    /**
     * Méthode pour ajouter une compétence à une formation.
     */
    @FXML
    private void handleAddFormation() {
        String nom = txtFormationName.getText();
        LocalDate dateDebut = dpFormationStartDate.getValue();
        int duree;
        int nbrTotalRequis;

        try {
            duree = Integer.parseInt(txtFormationDuration.getText());
        } catch (NumberFormatException e) {
            System.err.println("❌ Erreur : La durée doit être un nombre valide !");
            return;
        }

        if (nom.isEmpty() || dateDebut == null || duree <= 0) {
            System.out.println("❌ Veuillez remplir tous les champs !");
            return;
        }

        try {
            nbrTotalRequis = Integer.parseInt(txtFormationNbrTotalRequis.getText());
        } catch (NumberFormatException e) {
            System.err.println("❌ Erreur : Le Nombre total requis doit être un nombre valide !");
            return;
        }

        DatabaseConnection.addMission(nom, "Description automatique", dateDebut, duree, nbrTotalRequis, "Formation");
        loadFormationData();
    }

    /**
     * Méthode pour mettre à jour une formation.
     */
    @FXML
    private void handleUpdateFormation() {
        Mission selected = tableFormations.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int duree;
            try {
                duree = Integer.parseInt(txtFormationDuration.getText());
            } catch (NumberFormatException e) {
                System.err.println("❌ Erreur : La durée doit être un nombre valide !");
                return;
            }

            DatabaseConnection.updateMission(selected.getId(), duree, selected.getStatut());
            loadFormationData();
        }
    }

    /**
     * Méthode pour supprimer une formation.
     */
    @FXML
    private void handleDeleteFormation() {
        Mission selected = tableFormations.getSelectionModel().getSelectedItem();
        if (selected != null) {
            DatabaseConnection.deleteMission(selected.getId());
            formationList.remove(selected);
        }
    }

    /**
     * Méthode pour ajouter une compétence à une formation.
     */
    @FXML
    private void handleAddCompetenceFormation() {
        String selectedCompetence = comboCompetencesFormation.getValue();
        String nbrPersonText = txtNbrPersonFor.getText();
        Mission formation = comboFormations.getSelectionModel().getSelectedItem();

        if (selectedCompetence == null || nbrPersonText.isEmpty()) {
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

        DatabaseConnection.addCompetenceToMission(selectedCompetence, nbrPerRequis, formation.getId());
        if (DatabaseConnection.validateMissionStatus(formation.getId())) {
            DatabaseConnection.updateMissionStatus(formation.getId(), "Planifié");
        }

        loadCompetences();
        loadMissionCompetences();
        loadMissionData();

    }

    /**
     * Méthode pour supprimer une compétence d'une formation.
     */
    @FXML
    private void handleDeleteCompetenceFormation() {
        String[] selectedRow = tableCompetencesFormation.getSelectionModel().getSelectedItem();
        Mission formation = comboFormations.getSelectionModel().getSelectedItem();
        if (formation == null) {
            System.out.println("❌ Please select a training first!");
            return;
        }

        if (selectedRow == null) {
            System.out.println("❌ Please select a competence to remove!");
            return;
        }
        String selectedCompetence = selectedRow[0];
        DatabaseConnection.removeCompetenceFromMission(formation.getId(), selectedCompetence);

        loadCompetences();
        loadMissionCompetences();
        loadFormationData();
    }

    /**
     * Méthode pour affecter une formation à un utilisateur.
     */
    @FXML
    private void handleAffecterFormation() {
        Personnel personnel = comboUsersFor.getSelectionModel().getSelectedItem();
        Mission formation = comboFormations.getSelectionModel().getSelectedItem();
        if (personnel != null && formation != null) {
            DatabaseConnection.assignPersonnelToMission(personnel.getId(), formation.getId());
            if (DatabaseConnection.validateMissionStatus(formation.getId())) {
                DatabaseConnection.updateMissionStatus(formation.getId(), "Planifiée");
            }
            System.out.println("Affectation réussie !");
        }
        loadFormationData();
    }

    /**
     * Méthode pour retirer une formation à un utilisateur.
     **/

    @FXML
    private void handleRetirerFormation() {
        Personnel personnel = comboUsersFor.getSelectionModel().getSelectedItem();
        Mission formation = comboMissions.getSelectionModel().getSelectedItem();
        if (personnel != null && formation != null) {
            DatabaseConnection.removePersonnelFromMission(personnel.getId(), formation.getId());
            System.out.println("Désaffectation réussie !");
        }
        loadFormationData();
    }

//    @FXML
//    private void handleAssignCompetences() {
//
//        System.out.println("✅ Mise à jour des missions terminées...");
//        DatabaseConnection.assignCompetencesMission();
//        System.out.println("✅ Ajout des compétences aux employés...");
//        DatabaseConnection.assignCompetencesMission();
//    }


    /**
     * Méthode pour retourner à l'accueil.
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
        } catch (IOException e) {
            System.err.println("❌ Erreur lors du retour à l'accueil : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
package com.example.demo7.database;

import com.example.demo7.models.Mission;
import com.example.demo7.models.PersonneSugereeComp;
import com.example.demo7.models.Personnel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de gestion de la connexion à la base de données.
 * Cette classe fournit des méthodes pour interagir avec la base de données,
 * y compris la récupération, la mise à jour et la suppression des données.
 */
public class DatabaseConnection {

    /**
     * URL de connexion à la base de données.
     */
    private static final String URL = "jdbc:mysql://109.176.199.97:3306/TalenTrack";

    /**
     * Nom d'utilisateur pour la connexion à la base de données.
     */
    private static final String USER = "lucas";

    /**
     * Mot de passe pour la connexion à la base de données.
     */
    private static final String PASSWORD = "lucas";

    /**
     * Connexion à la base de données.
     */
    private static Connection connection;

    /**
     * Méthode pour obtenir une connexion à la base de données.
     * Si la connexion n'existe pas, elle est créée.
     *
     * @return La connexion à la base de données.
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion réussie à la base de données !");
            } catch (SQLException e) {
                System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
            }
        }
        return connection;
    }

    /**
     * Méthode pour vérifier les informations de connexion d'un utilisateur et retourner son rôle.
     *
     * @param email      L'email de l'utilisateur.
     * @param motDePasse Le mot de passe de l'utilisateur.
     * @return Le rôle de l'utilisateur si les informations sont valides, sinon null.
     */
    public static String verifierUtilisateurAvecRole(String email, String motDePasse) {
        String sql = "SELECT Nom_Role FROM personnel WHERE Email = ? AND Mot_De_Passe = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, motDePasse);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("Nom_Role");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de l'utilisateur : " + e.getMessage());
        }
        return null;
    }

    /**
     * Méthode pour récupérer la liste des employés.
     *
     * @return Une liste des employés.
     */
    public static List<Personnel> getPersonnels() {
        List<Personnel> personnelList = new ArrayList<>();
        String sql = "SELECT ID_Personnel, Nom, Prenom, Email, Nom_Role FROM personnel";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                personnelList.add(new Personnel(
                        rs.getInt("ID_Personnel"),
                        rs.getString("Nom"),
                        rs.getString("Prenom"),
                        rs.getString("Email"),
                        rs.getString("Nom_Role")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du personnel : " + e.getMessage());
        }
        return personnelList;
    }

    /**
     * Méthode pour récupérer la liste des missions.
     *
     * @return Une liste des missions.
     */
    public static List<Mission> getMissions(String type) {
        updateMissionsStatus(); // Met à jour le statut des missions
        List<Mission> missions = new ArrayList<>();
        String sql = "SELECT ID_Mission, Nom, Description, Date_Debut, Duree, Statut, Type FROM mission WHERE Type = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)){
            stmt.setString(1, type);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    missions.add(new Mission(
                            rs.getInt("ID_Mission"),
                            rs.getString("Nom"),
                            rs.getString("Description"),
                            rs.getDate("Date_Debut").toLocalDate(),  // Conversion SQL -> LocalDate
                            rs.getInt("Duree"),
                            rs.getString("Statut"),
                            rs.getString("Type")
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des missions : " + e.getMessage());
        }
        return missions;
    }

    /**
     * Méthode pour mettre à jour une mission (changer la durée et le statut).
     *
     * @param missionId L'ID de la mission à mettre à jour.
     * @param duree     La nouvelle durée de la mission.
     * @param statut    Le nouveau statut de la mission.
     */
    public static void updateMission(int missionId, int duree, String statut) {
        String sql = "UPDATE mission SET Duree = ?, Statut = ? WHERE ID_Mission = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, duree);
            stmt.setString(2, statut);
            stmt.setInt(3, missionId);
            stmt.executeUpdate();
            System.out.println("✅ Mission mise à jour avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la mission : " + e.getMessage());
        }
    }

    /**
     * Méthode pour rétrograder un utilisateur au rôle d'employé.
     *
     * @param personnelId L'ID du personnel à rétrograder.
     */
    public static void downgradeToEmployee(int personnelId) {
        String sql = "UPDATE personnel SET Nom_Role = 'Personnel' WHERE ID_Personnel = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, personnelId);
            stmt.executeUpdate();
            System.out.println("✅ Utilisateur rétrogradé en employé !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du changement de rôle : " + e.getMessage());
        }
    }

    /**
     * Méthode pour supprimer un utilisateur.
     *
     * @param personnelId L'ID du personnel à supprimer.
     */
    public static void deleteUser(int personnelId) {
        String sql = "DELETE FROM personnel WHERE ID_Personnel = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, personnelId);
            stmt.executeUpdate();
            System.out.println("✅ Utilisateur supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
        }
    }

    /**
     * Méthode pour ajouter un nouvel employé.
     *
     * @param nom        Le nom de l'employé.
     * @param prenom     Le prénom de l'employé.
     * @param email      L'email de l'employé.
     * @param motDePasse Le mot de passe de l'employé.
     */
    public static void addEmployee(String nom, String prenom, String email, String motDePasse) {
        String sql = "INSERT INTO personnel (Nom, Prenom, Date_Entree_Ent, Mot_De_Passe, Nom_Role, Email) VALUES (?, ?, CURRENT_DATE, ?, 'Personnel', ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, motDePasse);
            stmt.setString(4, email);

            stmt.executeUpdate();
            System.out.println("✅ Personnel ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du personnel : " + e.getMessage());
        }
    }

    /**
     * Méthode pour mettre à jour le rôle d'un employé.
     *
     * @param personnelId L'ID du personnel à mettre à jour.
     * @param newRole     Le nouveau rôle du personnel.
     */
    public static void updateRole(int personnelId, String newRole) {
        String sql = "UPDATE personnel SET Nom_Role = ? WHERE ID_Personnel = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, newRole);
            stmt.setInt(2, personnelId);
            stmt.executeUpdate();
            System.out.println("✅ Rôle mis à jour avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour du rôle : " + e.getMessage());
        }
    }

    /**
     * Méthode pour ajouter une nouvelle mission.
     *
     * @param nom           Le nom de la mission.
     * @param description   La description de la mission.
     * @param dateDebut     La date de début de la mission.
     * @param duree         La durée de la mission.
     * @param nbrTotalRequis Le nombre total de personnes requises pour la mission.
     */
    public static void addMission(String nom, String description, LocalDate dateDebut, int duree, int nbrTotalRequis, String type) {
        String sql = "INSERT INTO mission (Nom, Date_Debut, Duree, Statut, Nombre_Total_Requis, Type) VALUES (?, ?, ?, 'Préparation', ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setDate(2, Date.valueOf(dateDebut));  // Conversion LocalDate → SQL Date
            stmt.setInt(3, duree);
            stmt.setInt(4, nbrTotalRequis);
            stmt.setString(5, type);
            stmt.executeUpdate();

            System.out.println("✅ Mission: " + nom + " ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la mission : " + e.getMessage());
        }
    }

    /**
     * Méthode pour récupérer les compétences non encore assignées à une mission.
     *
     * @param missionId L'ID de la mission.
     * @return Une liste observable des compétences non assignées.
     */
    public static ObservableList<String> getCompetences(int missionId) {
        String sql = "SELECT c.Code_Competence, c.Nom_Competence " +
                "FROM competence c " +
                "WHERE c.Code_Competence NOT IN (" +
                "    SELECT n.Code_Competence FROM necessiter n WHERE n.ID_Mission = ?" +
                ")";

        ObservableList<String> competences = FXCollections.observableArrayList();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, missionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                competences.add(rs.getString("Nom_Competence"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching unassigned competences: " + e.getMessage());
        }

        return competences;
    }

    /**
     * Méthode pour récupérer les compétences d'une mission avec le nombre de personnes requises.
     *
     * @param missionId L'ID de la mission.
     * @return Une liste observable des compétences de la mission.
     */
    public static ObservableList<String[]> getMissionCompetences(int missionId) {
        String sql = "SELECT c.Code_Competence, c.Nom_Competence, n.Nombre_Requis " +
                "FROM necessiter n " +
                "JOIN competence c ON n.Code_Competence = c.Code_Competence " +
                "WHERE n.ID_Mission = ? GROUP BY c.Code_Competence, c.Nom_Competence, n.Nombre_Requis";
        ObservableList<String[]> competences = FXCollections.observableArrayList();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, missionId); // Set the mission ID as parameter
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] competenceData = new String[3];
                competenceData[0] = rs.getString("Code_Competence");
                competenceData[1] = rs.getString("Nom_Competence");
                competenceData[2] = String.valueOf(rs.getInt("Nombre_Requis"));
                competences.add(competenceData);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching competences for mission: " + e.getMessage());
        }

        return competences;
    }

    /**
     * Méthode pour ajouter une compétence à une mission.
     *
     * @param competenceCode Le code de la compétence à ajouter.
     * @param nbrPerRequis   Le nombre de personnes requises pour cette compétence.
     * @param missionId      L'ID de la mission.
     */
    public static void addCompetenceToMission(String nomCompetence, int nbrPerRequis, int missionId) {
        String getCodeSql = "SELECT Code_Competence FROM competence WHERE Nom_Competence = ?";
        String insertSql = "INSERT INTO necessiter (Code_Competence, ID_Mission, Nombre_Requis) VALUES (?, ?, ?)";

        Connection conn = getConnection(); // Ne pas fermer cette connexion ici !

        try (
                PreparedStatement getCodeStmt = conn.prepareStatement(getCodeSql)
        ) {
            getCodeStmt.setString(1, nomCompetence);
            ResultSet rs = getCodeStmt.executeQuery();

            if (rs.next()) {
                String competenceCode = rs.getString("Code_Competence");

                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, competenceCode);
                    insertStmt.setInt(2, missionId);
                    insertStmt.setInt(3, nbrPerRequis);
                    insertStmt.executeUpdate();
                    System.out.println("✅ Competence added to mission successfully!");
                }
            } else {
                System.err.println("❌ No competence found with name: " + nomCompetence);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error adding competence to mission: " + e.getMessage());
        }
    }


    /**
     * Méthode pour supprimer une compétence d'une mission.
     *
     * @param missionId      L'ID de la mission.
     * @param competenceCode Le code de la compétence à supprimer.
     */
    public static void removeCompetenceFromMission(int missionId, String competenceCode) {
        String sql = "DELETE FROM necessiter WHERE Code_Competence = ? AND ID_Mission = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, competenceCode);
            stmt.setInt(2, missionId);
            stmt.executeUpdate();
            System.out.println("✅ Compétence: " + competenceCode + " retiré de la mission: " + missionId + " !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du retrait de la compétence " + competenceCode + "de la mission : " + missionId + e.getMessage());
        }
    }

    /**
     * Méthode pour récupérer le personnel affecté à une mission.
     *
     * @param missionId L'ID de la mission.
     * @return Une chaîne de caractères représentant les noms des personnels affectés.
     */
    public static String getPersonnelAffecte(int missionId) {
        String sql = "SELECT p.Nom, p.Prenom FROM personnel p " +
                "JOIN affecter a ON p.ID_Personnel = a.ID_Personnel " +
                "WHERE a.ID_Mission = ?";
        StringBuilder personnelListe = new StringBuilder();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, missionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (personnelListe.length() > 0) personnelListe.append(", ");
                personnelListe.append(rs.getString("Nom")).append(" ").append(rs.getString("Prenom"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des personnels affectés : " + e.getMessage());
        }

        return personnelListe.toString();
    }

    /**
     * Méthode pour supprimer une mission.
     *
     * @param missionId L'ID de la mission à supprimer.
     */
    public static void deleteMission(int missionId) {
        String sql = "DELETE FROM mission WHERE ID_Mission = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, missionId);
            stmt.executeUpdate();
            System.out.println("✅ Mission supprimée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la mission : " + e.getMessage());
        }
    }

    /**
     * Méthode pour affecter un employé à une mission.
     *
     * @param personnelId L'ID du personnel à affecter.
     * @param missionId   L'ID de la mission.
     */
    public static void assignPersonnelToMission(int personnelId, int missionId) {
        String sql = "INSERT INTO affecter (ID_Personnel, ID_Mission, Date_Affectation) VALUES (?, ?, CURDATE())";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, personnelId);
            stmt.setInt(2, missionId);
            stmt.executeUpdate();
            System.out.println("✅ Employé affecté à la mission !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'affectation de l'employé : " + e.getMessage());
        }
    }

    /**
     * Méthode pour retirer un employé d'une mission.
     *
     * @param personnelId L'ID du personnel à retirer.
     * @param missionId   L'ID de la mission.
     */
    public static void removePersonnelFromMission(int personnelId, int missionId) {
        String sql = "DELETE FROM affecter WHERE ID_Personnel = ? AND ID_Mission = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, personnelId);
            stmt.setInt(2, missionId);
            stmt.executeUpdate();
            System.out.println("✅ Employé retiré de la mission !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du retrait de l'employé de la mission : " + e.getMessage());
        }
    }

    /**
     * Méthode pour valider le statut d'une mission.
     *
     * @param missionId L'ID de la mission.
     * @return true si le statut de la mission est valide, sinon false.
     */
    public static Boolean validateMissionStatus(int missionId) {
        String sql = "SELECT n.ID_Mission FROM necessiter n "
                + "LEFT JOIN affecter a ON n.ID_Mission = a.ID_Mission "
                + "LEFT JOIN posseder p ON a.ID_Personnel = p.ID_Personnel AND n.Code_Competence = p.Code_Competence "
                + "GROUP BY n.ID_Mission "
                + "HAVING COUNT(DISTINCT a.ID_Personnel) >= (SELECT SUM(n2.Nombre_Requis) FROM necessiter n2 WHERE n2.ID_Mission = n.ID_Mission) "
                + "AND COUNT(DISTINCT a.ID_Personnel) = (SELECT Nombre_Total_Requis FROM mission WHERE mission.ID_Mission = n.ID_Mission)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                int missionIdValid = resultSet.getInt("ID_Mission");
                System.out.println("Mission " + missionIdValid + " is now validated and set to 'En Planifiée'.");
                return true;
            } else {
                System.out.println("❌ Mission not validated yet.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error validating mission status: " + e.getMessage());
        }
        return false;
    }

    /**
     * Méthode pour mettre à jour le statut d'une mission.
     *
     * @param missionId L'ID de la mission.
     * @param status    Le nouveau statut de la mission.
     */
    public static void updateMissionStatus(int missionId, String status) {
        String sql = "UPDATE mission SET Statut = ? WHERE ID_Mission = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, missionId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Mission mise à jour en statut '" + status + "' !");
            } else {
                System.out.println("❌ Aucune mission trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour de la mission : " + e.getMessage());
        }
    }

    /**
     * Méthode pour mettre à jour le statut des missions en cours.
     */
    public static void updateMissionsStatus() {
        String sql = "UPDATE mission SET Statut = 'En Cours' WHERE Statut = 'Planifiée' AND Date_Debut = CURDATE()";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ " + rowsUpdated + " Missions mises à jour en statut 'En Cours'.");
            } else {
                System.out.println("❌ Aucune mission à mettre à jour.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour des missions : " + e.getMessage());
        }
    }

    /**
     * Méthode pour récupérer la liste des personnels avec leurs compétences pour une mission.
     *
     * @param missionId L'ID de la mission.
     * @return Une liste des personnels avec leurs compétences pour la mission.
     */
    public static List<Personnel> getPersoCompetenceMission(int missionId) {
        List<Personnel> personnelList = new ArrayList<>();
        String sql = "SELECT p.ID_Personnel, Nom, Prenom, Email, Nom_Role FROM personnel p " +
                "JOIN posseder po ON p.ID_Personnel = po.ID_Personnel " +
                "JOIN necessiter n ON po.Code_Competence = n.Code_Competence " +
                "WHERE n.ID_Mission = ? AND Nombre_Requis>0";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, missionId); // Définition du paramètre

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Personnel personnel = new Personnel(
                            rs.getInt("ID_Personnel"),
                            rs.getString("Nom"),
                            rs.getString("Prenom"),
                            rs.getString("Email"),
                            rs.getString("Nom_Role")
                    );
                    personnelList.add(personnel);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération du personnel : " + e.getMessage());
        }
        return personnelList;
    }

    /**
     * Méthode pour récupérer les compétences d'un personnel pour une mission.
     *
     * @param missionId L'ID de la mission.
     * @param personnel Le personnel dont on veut obtenir les compétences.
     * @return Les compétences du personnel pour la mission.
     */
    public static String getCompetencePersoMission(int missionId, Personnel personnel) {
        String competences = "";
        String sql = "SELECT c.Nom_Competence FROM posseder po " +
                "JOIN personnel p ON p.ID_Personnel = po.ID_Personnel " +
                "JOIN necessiter n ON po.Code_Competence = n.Code_Competence " +
                "JOIN competence c ON c.Code_Competence = po.Code_Competence " +
                "WHERE n.ID_Mission = ? AND p.ID_Personnel = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, missionId); // Paramètre pour l'ID de la mission
            stmt.setInt(2, personnel.getId()); // Paramètre pour l'ID du personnel

            // Exécution de la requête
            try (ResultSet rs = stmt.executeQuery()) {
                // Parcours des résultats de la requête
                while (rs.next()) {
                    // Ajoutez le nom de la compétence à la liste
                    competences += " - " + rs.getString("Nom_Competence");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Affiche l'erreur si une exception est levée
        }
        // Retourne la liste des compétences
        return competences;
    }

    //Afficher les competences d'une mission
    public static String getCompetenceMission(int missionId) {
        String sql = "SELECT c.Nom_competence " +
                "FROM necessiter n " +
                "JOIN competence c ON n.Code_Competence = c.Code_Competence " +
                "WHERE n.ID_Mission = ? GROUP BY c.Nom_competence";
        StringBuilder competencesList = new StringBuilder();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, missionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                if (competencesList.length() > 0) competencesList.append(", ");
                competencesList.append(rs.getString("Nom_competence"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des personnels affectés : " + e.getMessage());
        }

        return competencesList.toString();
    }

    /**
     * Méthode pour récupérer la liste des personnes suggérées avec leurs compétences pour une mission.
     *
     * @param missionId  L'ID de la mission.
     * @param personnels La liste des personnels à suggérer.
     * @return Une liste observable des personnes suggérées avec leurs compétences.
     */
    public static ObservableList<PersonneSugereeComp> getListPersonneSugereeComp(int missionId, List<Personnel> personnels) {
        ObservableList<PersonneSugereeComp> personneSugereeList = FXCollections.observableArrayList();
        for (Personnel personnel : personnels) {
            personneSugereeList.add(new PersonneSugereeComp(personnel, getCompetencePersoMission(missionId, personnel)));
        }
        return personneSugereeList;
    }



}
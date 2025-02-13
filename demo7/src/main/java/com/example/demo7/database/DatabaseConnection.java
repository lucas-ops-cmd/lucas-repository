package com.example.demo7.database;

import com.example.demo7.models.Mission;
import com.example.demo7.models.Personnel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://109.176.199.97:3306/TalenTrack";
    private static final String USER = "lucas";
    private static final String PASSWORD = "lucas";


    private static Connection connection;

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

    // Vérifier l'utilisateur et récupérer son rôle
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

    // 🔹 Récupérer la liste des employés
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



    public static List<Mission> getMissions() {
        updateMissionsStatus(); // Changer le statut des missions
        List<Mission> missions = new ArrayList<>();
        String sql = "SELECT ID_Mission, Nom, Description, Date_Debut, Duree, Statut FROM mission";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                missions.add(new Mission(
                        rs.getInt("ID_Mission"),
                        rs.getString("Nom"),
                        rs.getString("Description"),
                        rs.getDate("Date_Debut").toLocalDate(),  // Conversion SQL -> LocalDate
                        rs.getInt("Duree"),
                        rs.getString("Statut")
                ));
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des missions : " + e.getMessage());
        }
        return missions;
    }




    // Modifier une mission (changer durée et statut)
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

    // 🔹 Supprimer un utilisateur
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

    // Ajouter personnel
    public static void addEmployee(String nom, String prenom, String email, String motDePasse) {

        String sql = "INSERT INTO personnel (Nom, Prenom, Date_Entree_Ent, Mot_De_Passe, Nom_Role, Email) VALUES (?, ?, CURRENT_DATE, ?, 'Personnel', ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);

            stmt.setString(3, motDePasse);
            stmt.setString(4, email);  //

            // Exécuter la requête
            stmt.executeUpdate();
            System.out.println("✅ Personnel ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout du personnel : " + e.getMessage());
        }
    }


    // 🔹 Modifier le rôle d'un employé
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

    public static void addMission(String nom, String description, LocalDate dateDebut, int duree, int nbrTotalRequis) {
        String sql = "INSERT INTO mission (Nom, Date_Debut, Duree, Statut, Nombre_Total_Requis) VALUES (?, ?, ?, 'Préparation', ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setDate(2, Date.valueOf(dateDebut));  // Conversion LocalDate → SQL Date
            stmt.setInt(3, duree);
            stmt.setInt(4, nbrTotalRequis);
            stmt.executeUpdate();


            System.out.println("✅ Mission: " + nom + " ajoutée avec succès !");


        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la mission : " + e.getMessage());

        }
    }

    // Afficher les competences non déjà choisis dans la combobox
    public static ObservableList<String> getCompetences(int missionId) {
        String sql = "SELECT c.Code_Competence " +
                "FROM competence c " +
                "WHERE c.Code_Competence NOT IN (" +
                "    SELECT n.Code_Competence FROM necessiter n WHERE n.ID_Mission = ?" +
                ")";

        ObservableList<String> competences = FXCollections.observableArrayList();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, missionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                competences.add(rs.getString("Code_Competence"));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching unassigned competences: " + e.getMessage());
        }

        return competences;
    }

    //Afficher les competence de la mission choisis
    public static ObservableList<String[]> getMissionCompetences(int missionId) {
        String sql = "SELECT c.Code_Competence, n.Nombre_Requis " +
                "FROM necessiter n " +
                "JOIN competence c ON n.Code_Competence = c.Code_Competence " +
                "WHERE n.ID_Mission = ? GROUP BY c.Code_Competence, n.Nombre_Requis";
        ObservableList<String[]> competences = FXCollections.observableArrayList();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, missionId); // Set the mission ID as parameter
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String[] competenceData = new String[2];
                competenceData[0] = rs.getString("Code_Competence");
                competenceData[1] = String.valueOf(rs.getInt("Nombre_Requis"));
                competences.add(competenceData);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error fetching competences for mission: " + e.getMessage());
        }

        return competences;
    }


    // Ajouter competences à mission
    public static void addCompetenceToMission(String competenceCode, int nbrPerRequis, int missionId) {
        String sql = "INSERT INTO necessiter (Code_Competence, ID_Mission, Nombre_Requis) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, competenceCode);
            stmt.setInt(2, missionId);
            stmt.setInt(3, nbrPerRequis);
            stmt.executeUpdate();
            System.out.println("✅ Competence added to mission successfully!");
        } catch (SQLException e) {
            System.err.println("❌ Error adding competence to mission: " + e.getMessage());
        }
    }

    // Supprimer competence de mission
    public static void removeCompetenceFromMission(int missionId, String competenceCode) {
        String sql = "DELETE FROM necessiter WHERE Code_Competence = ? AND ID_Mission = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, competenceCode);
            stmt.setInt(2, missionId);
            stmt.executeUpdate();
            System.out.println("✅ Compétence: " + competenceCode + " retiré de la mission: "+ missionId +" !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors du retrait de la compétence " + competenceCode + "de la mission : "+ missionId + e.getMessage());
        }
    }





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


    // 🔹 Supprimer une mission
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

    // 🔹 Affecter un employé à une mission
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

    // 🔹 Retirer un employé d'une mission
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

    // Methode pour valider une mission
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

    // Methode pour changer le statut d'une mission
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

    //  Méthode pour changer le statut en cours des missions
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



}

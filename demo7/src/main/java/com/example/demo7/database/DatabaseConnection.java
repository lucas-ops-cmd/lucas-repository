package com.example.demo7.database;

import com.example.demo7.models.Mission;
import com.example.demo7.models.Personnel;

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
        updateMissionsStatus();
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

    public static void addUser(String nom, String prenom, String email, String role) {
        String sql = "INSERT INTO personnel (nom, prenom, email, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setString(2, prenom);
            stmt.setString(3, email);
            stmt.setString(4, role);
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

    public static void addMission(String nom, String description, LocalDate dateDebut, int duree) {
        String sql = "INSERT INTO mission (Nom, Date_Debut, Duree, Statut) VALUES (?, ?, ?, 'Préparation')";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, nom);
            stmt.setDate(2, Date.valueOf(dateDebut));  // Conversion LocalDate → SQL Date
            stmt.setInt(3, duree);
            stmt.executeUpdate();
            System.out.println("✅ Mission ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la mission : " + e.getMessage());
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
    // Redefinition méthode pour changer le statut en cours

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

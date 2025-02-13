package com.example.demo7.controllers;

import com.example.demo7.database.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class AddEmployeeController {

    @FXML private TextField txtEmployeeNom;
    @FXML private TextField txtEmployeePrenom;
    @FXML private TextField txtEmployeeEmail;
    @FXML private PasswordField txtEmployeeMotDePasse;
    @FXML private Button btnAddEmployee;
    @FXML private Button btnCancel;


    @FXML
    private void handleAddEmployee() {
        String nom = txtEmployeeNom.getText();
        String prenom = txtEmployeePrenom.getText();
        String email = txtEmployeeEmail.getText();
        String motDePasse = txtEmployeeMotDePasse.getText();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || motDePasse.isEmpty()) {
            System.out.println("All fields must be filled!");
            return;
        }

        DatabaseConnection.addEmployee(nom, prenom, email, motDePasse);
        closeModal();
    }

    @FXML
    private void handleCancel() {
        closeModal();
    }

    private void closeModal() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}

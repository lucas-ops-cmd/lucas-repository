package com.example.demo7.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test pour voir si le fichier est bien trouvé
            URL fxmlLocation = getClass().getResource("/com/example/demo7/accueil-view.fxml");
            if (fxmlLocation == null) {
                throw new IOException("❌ ERREUR : Le fichier accueil-view.fxml est introuvable !");
            }

            System.out.println("✅ Fichier FXML trouvé : " + fxmlLocation);

            FXMLLoader loader = new FXMLLoader(fxmlLocation);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Accueil");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}

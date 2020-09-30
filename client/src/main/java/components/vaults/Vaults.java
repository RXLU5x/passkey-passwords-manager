package components.vaults;

import components.Alert;
import components.home.Home;
import http.HttpsClient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import localDB.Database;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class Vaults extends BorderPane implements Initializable
{
    @FXML
    private VaultsSidebar sidebar;

    public Vaults() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vaults/Vaults.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        VBox vaults = sidebar.getVaults();
        VBox account = sidebar.getAccount();
        VBox signOut = sidebar.getSignOut();

        vaults.setOnMouseClicked(event ->
            {
                ObservableList<String> vaultsStyleClass = vaults.getStyleClass();
                ObservableList<String> accountStyleClass = account.getStyleClass();

                if(vaultsStyleClass.contains("inactive")) {
                    vaultsStyleClass.remove("inactive");
                    accountStyleClass.setAll("inactive");

                    this.setCenter(new VaultsOperations());
                }
            }
        );

        account.setOnMouseClicked(event ->
            {
                ObservableList<String> accountStyleClass = account.getStyleClass();
                ObservableList<String> vaultsStyleClass = vaults.getStyleClass();

                if(accountStyleClass.contains("inactive")) {
                    accountStyleClass.remove("inactive");
                    vaultsStyleClass.setAll("inactive");

                    this.setCenter(new Account());
                }
            }
        );

        signOut.setOnMouseClicked(event ->
            {
                URI uri = URI.create("https://localhost:8080/api/users/signout");

                try {
                    HttpResponse<String> response = HttpsClient
                        .getInstance()
                        .sendRequest(uri, "POST");

                    if (response.statusCode() < 200 || response.statusCode() >= 300)
                        throw new Exception(response.body());

                    Database.getInstance(null).deleteAll();

                    Platform.exit();
                } catch (Exception e) {
                    Alert.showAlert(e.getMessage());
                }
            }
        );

        VaultsOperations vaultsOperations = new VaultsOperations();
        this.setCenter(vaultsOperations);

        vaultsOperations.syncAllVaults();
    }
}

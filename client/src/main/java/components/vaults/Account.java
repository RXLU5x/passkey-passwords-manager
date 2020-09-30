package components.vaults;

import components.Alert;
import components.TextField;
import components.home.Home;
import crypto.Crypto;
import crypto.KeyCrypto;
import dto.user.UserDTO;
import http.HttpsClient;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import localDB.Database;
import models.user.User;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class Account extends VBox implements Initializable
{
    @FXML
    private TextField username;

    @FXML
    private Button delete;

    public Account() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vaults/Account.fxml"));
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
        delete.setOnMouseClicked(event -> handleDeleteClicked());
    }

    private void handleDeleteClicked() {
        String userN = username.getFieldText();
        ObservableList<String> userNStyleClass = username.getStyleClass();

        boolean invalid = false;

        if (userN.isBlank()) {
            invalid = true;
            if (!userNStyleClass.contains("field-error"))
                userNStyleClass.add("field-error");
        } else
            userNStyleClass.remove("field-error");

        try {
            if(invalid)
                throw new Exception("You need to fill in all the fields!");

            URI uri = URI.create("https://localhost:8080/api/users/delete");

            Crypto crypto = Crypto.getInstance();
            KeyCrypto keyCrypto = crypto.getDerivedKeys();
            byte[] authenticationKey = keyCrypto.getAuthenticationKey();

            User user = new User(null, null, null, username.getFieldText(), authenticationKey);
            UserDTO userDTO = user.toDTO();

            HttpResponse<String> response = HttpsClient
                .getInstance()
                .sendRequest(uri, "POST", userDTO.toJSON());

            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new Exception(response.body());

            Database database = Database.getInstance(null);
            database.deleteAll();

            Platform.exit();
        } catch (Exception e) {
            Alert.showAlert(e.getMessage());
        }
    }
}

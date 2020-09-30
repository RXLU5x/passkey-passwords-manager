package components.home;

import components.Alert;
import components.PasswordField;
import components.TextField;
import components.vaults.Vaults;
import crypto.Crypto;
import crypto.KeyCrypto;
import dto.user.UserDTO;
import http.HttpsClient;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import models.user.User;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SignUp extends VBox implements Initializable
{
    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField email;

    @FXML
    private TextField username;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button signUpButton;

    public SignUp() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home/SignUp.fxml"));
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
        this.signUpButton.setOnMouseClicked(event -> handleButtonClicked());
    }

    private void handleButtonClicked()
    {
        String firstN = firstName.getFieldText();
        ObservableList<String> firstNStyleClass = firstName.getStyleClass();

        String lastN = lastName.getFieldText();
        ObservableList<String> lastNStyleClass = lastName.getStyleClass();

        String em = email.getFieldText();
        ObservableList<String> emailStyleClass = email.getStyleClass();

        String userN = username.getFieldText();
        ObservableList<String> userNStyleClass = username.getStyleClass();

        String password = passwordField.getFieldText();
        ObservableList<String> passStyleClass = passwordField.getStyleClass();

        int numberOfInvalidFields = 0;

        ArrayList<ObservableList<String>> toAdd = new ArrayList<>();
        ArrayList<ObservableList<String>> toRem = new ArrayList<>();

        if (firstN.isBlank()) {
            numberOfInvalidFields++;
            if (!firstNStyleClass.contains("field-error"))
                toAdd.add(firstNStyleClass);
        } else {
            if(firstNStyleClass.contains("field-error"))
                toRem.add(firstNStyleClass);
        }

        if (lastN.isBlank()) {
            numberOfInvalidFields++;
            if (!lastNStyleClass.contains("field-error"))
                toAdd.add(lastNStyleClass);
        } else {
            if(lastNStyleClass.contains("field-error"))
                toRem.add(lastNStyleClass);
        }

        if (em.isBlank()) {
            numberOfInvalidFields++;
            if (!emailStyleClass.contains("field-error"))
                toAdd.add(emailStyleClass);
        } else {
            if(emailStyleClass.contains("field-error"))
                toRem.add(emailStyleClass);
        }

        if (userN.isBlank()) {
            numberOfInvalidFields++;
            if (!userNStyleClass.contains("field-error"))
                toAdd.add(userNStyleClass);
        } else {
            if(userNStyleClass.contains("field-error"))
                toRem.add(userNStyleClass);
        }

        if (password.isBlank()) {
            numberOfInvalidFields++;
            if (!passStyleClass.contains("field-error"))
                toAdd.add(passStyleClass);
        } else {
            if(passStyleClass.contains("field-error"))
                toRem.add(passStyleClass);
        }

        try {
            if (numberOfInvalidFields > 0) {
                toAdd.forEach(styleClass -> styleClass.add("field-error"));
                toRem.forEach(styleClass -> styleClass.remove("field-error"));

                throw new Exception("You need to fill in all the fields!");
            }

            Crypto crypto = Crypto.getInstance();
            crypto.deriveKeys(password.toCharArray());

            KeyCrypto keyCrypto = crypto.getDerivedKeys();
            byte[] authenticationKey = keyCrypto.getAuthenticationKey();

            User user = new User(firstN, lastN, em, userN, authenticationKey);
            UserDTO userDTO = user.toDTO();

            URI uri = URI.create("https://localhost:8080/api/users/signup");

            HttpResponse<String> response = HttpsClient
                .getInstance()
                .sendRequest(uri, "POST", userDTO.toJSON());

            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new Exception(response.body());

            Scene scene = this.getScene();
            Vaults vaults = new Vaults();
            scene.setRoot(vaults);
        } catch (Exception e) {
            Alert.showAlert(e.getMessage());
        }
    }
}

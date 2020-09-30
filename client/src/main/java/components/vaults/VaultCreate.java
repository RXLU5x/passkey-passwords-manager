package components.vaults;

import components.Alert;
import components.PasswordField;
import components.TextField;
import crypto.Crypto;
import dao.VaultDao;
import dto.vault.VaultDTO;
import http.HttpsClient;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import localDB.Database;
import models.vault.Vault;
import models.vault.VaultBody;
import models.vault.VaultHeaderInfo;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class VaultCreate extends VBox implements Initializable
{
    @FXML
    Button back;

    @FXML
    TextField title;

    @FXML
    TextField username;

    @FXML
    PasswordField passwordField;

    @FXML
    TextField email;

    @FXML
    TextField address;

    @FXML
    ChoiceBox<String> encSchemes;

    @FXML
    ChoiceBox<String> authSchemes;

    @FXML
    Button submit;

    public VaultCreate() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vaults/VaultCreate.fxml"));
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
        submit.setOnMouseClicked(event -> handleSubmitClicked());
        back.setOnMouseClicked(event -> handleBackClicked());

        encSchemes.getItems().setAll(Crypto.getInstance().getCipherList());
        authSchemes.getItems().setAll(Crypto.getInstance().getMACList());

        encSchemes.setValue("AES-256-GCM");
        authSchemes.setDisable(true);

        encSchemes.setOnAction(event -> handleActionComboBox());
    }

    private void handleActionComboBox() {
        String encScheme = encSchemes.getValue();

        if(encScheme.equals("AES-256-GCM") || encScheme.equals("Camellia-256-GCM") || encScheme.equals("ChaCha20-256-Poly1305")) {
            authSchemes.setDisable(true);
            authSchemes.setValue(null);
        } else
            authSchemes.setDisable(false);
    }

    private void handleBackClicked() {
        VaultsOperations root = new VaultsOperations();
        ((BorderPane) this.getScene().getRoot()).setCenter(root);
    }

    @FXML
    private void handleSubmitClicked()
    {
        String titl = title.getFieldText();
        ObservableList<String> titlStyleClass = title.getStyleClass();

        String userN = username.getFieldText();
        ObservableList<String> userNStyleClass = username.getStyleClass();

        String password = passwordField.getFieldText();
        ObservableList<String> passStyleClass = passwordField.getStyleClass();

        String em = email.getFieldText();
        ObservableList<String> emStyleClass = email.getStyleClass();

        String add = address.getFieldText();
        ObservableList<String> addStyleClass = address.getStyleClass();

        int numberOfInvalidFields = 0;

        ArrayList<ObservableList<String>> toAdd = new ArrayList<>();
        ArrayList<ObservableList<String>> toRem = new ArrayList<>();

        if (titl.isBlank()) {
            numberOfInvalidFields++;
            if (!titlStyleClass.contains("field-error"))
                toAdd.add(titlStyleClass);
        } else {
            if(titlStyleClass.contains("field-error"))
                toRem.add(titlStyleClass);
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

        if (em.isBlank()) {
            numberOfInvalidFields++;
            if (!emStyleClass.contains("field-error"))
                toAdd.add(emStyleClass);
        } else {
            if(emStyleClass.contains("field-error"))
                toRem.add(emStyleClass);
        }

        if (add.isBlank()) {
            numberOfInvalidFields++;
            if (!addStyleClass.contains("field-error"))
                toAdd.add(addStyleClass);
        } else {
            if(addStyleClass.contains("field-error"))
                toRem.add(addStyleClass);
        }

        try {
            if (numberOfInvalidFields > 0) {
                toAdd.forEach(styleClass -> styleClass.add("field-error"));
                toRem.forEach(styleClass -> styleClass.remove("field-error"));

                throw new Exception("You need to fill in all the fields!");
            }

            VaultHeaderInfo vaultHeaderInfo = new VaultHeaderInfo(titl, add);
            VaultBody vaultBody = new VaultBody(userN, em, password, new ArrayList<>());

            Vault vault = Vault.generateInstance(encSchemes.getValue(), authSchemes.getValue(), vaultHeaderInfo, vaultBody);
            VaultDTO vaultDTO = vault.toDTO();

            URI uri = URI.create("https://localhost:8080/api/vaults/create");

            HttpResponse<String> response = HttpsClient
                .getInstance()
                .sendRequest(uri, "PUT", vaultDTO.toJSON());

            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new Exception(response.body());

            VaultDao vDao = VaultDao.fromJSON(response.body());
            Database.getInstance(null).put(vDao.getId(), vDao.getTimeTag(), vaultDTO.toJSON());

            VaultsOperations root = new VaultsOperations();
            ((BorderPane) this.getScene().getRoot()).setCenter(root);
        } catch (Exception e) {
            Alert.showAlert(e.getMessage());
        }
    }
}

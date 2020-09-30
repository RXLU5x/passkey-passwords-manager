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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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

public class VaultInfo extends VBox implements Initializable
{
    @FXML
    private Button back;

    @FXML
    private Button sync;

    @FXML
    private Button delete;

    @FXML
    private Button edit;

    @FXML
    private ImageView image;

    @FXML
    private Text vaultName;

    @FXML
    private TextField title;

    @FXML
    private TextField username;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField email;

    @FXML
    private TextField address;

    @FXML
    private ChoiceBox<String> encSchemes;

    @FXML
    private ChoiceBox<String> authSchemes;

    private String id;
    private long timeTag;

    public VaultInfo() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vaults/VaultInfo.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public VaultInfo(String id, long timeTag) {
        this();

        this.id = id;
        this.timeTag = timeTag;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        back.setOnMouseClicked(event -> handleBackClicked());
        sync.setOnMouseClicked(event -> handleSyncClicked());
        delete.setOnMouseClicked(event -> handleDeleteClicked());
        edit.setOnMouseClicked(event -> handleEditClicked());

        encSchemes.getItems().setAll(Crypto.getInstance().getCipherList());
        authSchemes.getItems().setAll(Crypto.getInstance().getMACList());

        encSchemes.setValue("AES-256-GCM");
        authSchemes.setDisable(true);

        encSchemes.setOnAction(event -> handleActionComboBox());
    }

    private void handleBackClicked() {
        VaultsOperations root = new VaultsOperations();
        ((BorderPane) this.getScene().getRoot()).setCenter(root);
    }

    private void handleActionComboBox() {
        String encScheme = encSchemes.getValue();

        if(encScheme.equals("AES-256-GCM") || encScheme.equals("Camellia-256-GCM") || encScheme.equals("ChaCha20-256-Poly1305")) {
            authSchemes.setDisable(true);
            authSchemes.setValue(null);
        } else
            authSchemes.setDisable(false);
    }

    private void handleSyncClicked()
    {
        try {
            URI uri = URI.create("https://localhost:8080/api/vaults?id=" + id);

            HttpResponse<String> response = HttpsClient
                .getInstance()
                .sendRequest(uri, "GET");

            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new Exception(response.body());

            VaultDao record = VaultDao.fromJSON(response.body());

            Database database = Database.getInstance(null);
            database.put(id, record.getTimeTag(), record.getVault().toJSON());

            VaultsOperations root = new VaultsOperations();
            ((BorderPane) this.getScene().getRoot()).setCenter(root);
        } catch (Exception e) {
            Alert.showAlert(e.getMessage());
        }
    }

    private void handleDeleteClicked()
    {
        try {
            URI uri = URI.create("https://localhost:8080/api/vaults/delete?id=" + id);

            HttpResponse<String> response = HttpsClient
                .getInstance()
                .sendRequest(uri, "DELETE");

            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new Exception(response.body());

            Database database = Database.getInstance(null);
            database.delete(id);

            VaultsOperations root = new VaultsOperations();
            ((BorderPane) this.getScene().getRoot()).setCenter(root);
        } catch (Exception e) {
            Alert.showAlert(e.getMessage());
        }
    }

    private void handleEditClicked()
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

            VaultDao vaultDao = new VaultDao(id, timeTag, vaultDTO);

            URI uri = URI.create("https://localhost:8080/api/vaults?id=" + id);

            HttpResponse<String> response = HttpsClient
                .getInstance()
                .sendRequest(uri, "POST", vaultDao.toJSON());

            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new Exception(response.body());

            handleSyncClicked();
        } catch (Exception e) {
            Alert.showAlert(e.getMessage());
        }
    }

    public void setTitle(String title) {
        this.vaultName.setText(title);
        this.title.setupFieldText(title);
    }

    public void setUsername(String username) {
        this.username.setupFieldText(username);;
    }

    public void setPassword(String password) {
        this.passwordField.setFieldText(password);
    }

    public void setEmail(String email) {
        this.email.setupFieldText(email);
    }

    public void setAddress(String address) {
        this.address.setupFieldText(address);
        this.image.setImage(new Image("https://www.google.com/s2/favicons?sz=32&domain_url=" + address));
    }

    public void setEncScheme(String encScheme) {
        this.encSchemes.setValue(encScheme);
    }

    public void setAuthScheme(String authScheme) {
        this.authSchemes.setValue(authScheme);
    }
}
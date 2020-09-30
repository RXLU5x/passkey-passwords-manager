package components.vaults;

import components.Alert;
import components.Vault;
import dao.CheckDao;
import dao.VaultDao;
import dto.vault.VaultDTO;
import http.HttpsClient;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import localDB.Database;
import localDB.DbRecordDAO;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VaultsOperations extends BorderPane implements Initializable
{
    @FXML
    private BorderPane content;

    @FXML
    private VBox createVault;

    @FXML
    private VBox syncVaults;

    @FXML
    private VBox vaults;

    public VaultsOperations() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vaults/VaultsOperations.fxml"));
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
        Database database = Database.getInstance(null);

        List<DbRecordDAO> list = database.getAll();

        try {
            for (DbRecordDAO dao : list) {
                VaultDTO vaultDTO = VaultDTO.fromJSON(dao.getVault());
                models.vault.Vault vaultModel = vaultDTO.toModel();
                vaultModel.decryptBody();

                Vault vault = new Vault();
                vault.setTitle(vaultModel.getVaultHeader().getVaultHeaderInfo().getTitle());
                vault.setImageAddress(vaultModel.getVaultHeader().getVaultHeaderInfo().getUrl());
                vault.setUsername(vaultModel.getVaultBody().getUsername());
                vault.setOnMouseClicked(event ->
                    {
                        VaultInfo vaultInfo = new VaultInfo(dao.getId(), dao.getTimeTag());
                        vaultInfo.setTitle(vaultModel.getVaultHeader().getVaultHeaderInfo().getTitle());
                        vaultInfo.setAddress(vaultModel.getVaultHeader().getVaultHeaderInfo().getUrl());
                        vaultInfo.setEncScheme(vaultModel.getVaultHeader().getVaultHeaderCrypto().getAlgo());
                        vaultInfo.setAuthScheme(vaultModel.getVaultHeader().getVaultHeaderCrypto().getMac());
                        vaultInfo.setEmail(vaultModel.getVaultBody().getEmail());
                        vaultInfo.setUsername(vaultModel.getVaultBody().getUsername());
                        vaultInfo.setPassword(vaultModel.getVaultBody().getPassword());

                        content.setCenter(vaultInfo);
                    }
                );

                this.vaults.getChildren().add(vault);
            }

            createVault.setOnMouseClicked(event -> content.setCenter(new VaultCreate()));
            syncVaults.setOnMouseClicked(event -> syncIfVaults());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncAllVaults() {
        try {
            CheckDao dao = new CheckDao("All", null);

            URI uri = URI.create("https://localhost:8080/api/vaults/check");

            HttpResponse<String> response = HttpsClient
                .getInstance()
                .sendRequest(uri, "POST", dao.toJson());

            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new Exception(response.body());

            VaultDao[] vaultsArray = CheckDao.fromJson(response.body()).getVaults();

            Database database = Database.getInstance(null);

            for (VaultDao s : vaultsArray)
            {
                if(s.isDelete())
                    database.delete(s.getId());
                else
                    database.put(s.getId(), s.getTimeTag(), s.getVault().toJSON());
            }

            VaultsOperations root = new VaultsOperations();
            ((BorderPane) this.getParent()).setCenter(root);
        } catch (Exception e){
            Alert.showAlert(e.getMessage());
        }
    }

    public void syncIfVaults() {
        try {
            Database database = Database.getInstance(null);

            List<DbRecordDAO> list = database.getAll();
            ArrayList<VaultDao> vaults = new ArrayList<>();

            try {
                for (DbRecordDAO dao : list) {
                    VaultDTO vaultDTO = VaultDTO.fromJSON(dao.getVault());
                    VaultDao vaultDao = new VaultDao(dao.getId(), dao.getTimeTag(), vaultDTO);
                    vaults.add(vaultDao);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            CheckDao dao = new CheckDao("If", vaults.toArray(new VaultDao[0]));

            URI uri = URI.create("https://localhost:8080/api/vaults/check");

            HttpResponse<String> response = HttpsClient
                .getInstance()
                .sendRequest(uri, "POST", dao.toJson());

            if (response.statusCode() < 200 || response.statusCode() >= 300)
                throw new Exception(response.body());

            VaultDao[] vaultsArray = CheckDao.fromJson(response.body()).getVaults();

            for (VaultDao s : vaultsArray) {
                if(s.isDelete())
                    database.delete(s.getId());
                else
                    database.put(s.getId(), s.getTimeTag(), s.getVault().toJSON());
            }

            VaultsOperations root = new VaultsOperations();
            ((BorderPane) this.getParent()).setCenter(root);
        } catch (Exception e){
            Alert.showAlert(e.getMessage());
        }
    }
}

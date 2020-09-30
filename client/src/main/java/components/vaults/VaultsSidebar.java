package components.vaults;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class VaultsSidebar extends VBox
{
    @FXML
    private VBox vaults;

    @FXML
    private VBox account;

    @FXML
    private VBox signOut;

    public VaultsSidebar() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vaults/VaultsSidebar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public VBox getVaults() {
        return vaults;
    }

    public VBox getAccount() {
        return account;
    }

    public VBox getSignOut() {
        return signOut;
    }
}

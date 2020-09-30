package components;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.io.IOException;

public class Vault extends HBox
{
    @FXML
    private ImageView imageView;

    @FXML
    private Text title;

    @FXML
    private Text username;

    public Vault() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/vaults/Vault.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setUsername(String username) {
        this.username.setText(username);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setImageAddress(String address) {
        this.imageView.setImage(new Image("https://www.google.com/s2/favicons?sz=32&domain_url=" + address));
    }
}

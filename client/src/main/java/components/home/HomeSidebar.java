package components.home;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HomeSidebar extends VBox
{
    @FXML
    private VBox signUp;

    @FXML
    private VBox signIn;

    public HomeSidebar() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home/HomeSidebar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public VBox getSignUp() {
        return signUp;
    }

    public VBox getSignIn() {
        return signIn;
    }
}

package components.home;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Home extends BorderPane implements Initializable
{
    @FXML
    private HomeSidebar sidebar;

    public Home() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/home/Home.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        VBox signIn = sidebar.getSignIn();
        VBox signUp = sidebar.getSignUp();

        signIn.setOnMouseClicked(event ->
            {
                ObservableList<String> signInStyleClass = signIn.getStyleClass();
                ObservableList<String> signUpStyleClass = signUp.getStyleClass();

                if(signInStyleClass.contains("inactive")) {
                    signInStyleClass.remove("inactive");
                    signUpStyleClass.setAll("inactive");

                    this.setCenter(new SignIn());
                }
            }
        );

        signUp.setOnMouseClicked(event ->
            {
                ObservableList<String> signInStyleClass = signIn.getStyleClass();
                ObservableList<String> signUpStyleClass = signUp.getStyleClass();

                if(signUpStyleClass.contains("inactive")) {
                    signUpStyleClass.remove("inactive");
                    signInStyleClass.setAll("inactive");

                    this.setCenter(new SignUp());
                }
            }
        );
    }
}

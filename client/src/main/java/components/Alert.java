package components;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Alert extends VBox
{
    @FXML
    private Text message;

    @FXML
    private Button okButton;

    public Alert() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Alert.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void setMessage(String message) {
        this.message.setText(message);
    }

    public void setupOkButtonClickHandler(EventHandler<MouseEvent> handler) {
        this.okButton.setOnMouseClicked(handler);
    }

    public static void showAlert(String message) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);

        Alert alert = new Alert();
        alert.setMessage(message);
        alert.setupOkButtonClickHandler(event -> popup.close());

        popup.setScene(new Scene(alert));
        popup.setResizable(false);
        popup.setTitle("Oops!");
        popup.show();
    }
}

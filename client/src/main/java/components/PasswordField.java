package components;

import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PasswordField extends VBox implements Initializable
{
    @FXML
    private Button eyeButton;

    @FXML
    private FontIcon eyeButtonIcon;

    @FXML
    private Text header;

    @FXML
    private HBox password;

    @FXML
    private javafx.scene.control.PasswordField passwordField;

    private final TextField textField;
    private final String size;

    private String colour;

    public PasswordField(@NamedArg("size") String size) {
        this.textField = new TextField();
        this.size = size;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/PasswordField.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public PasswordField(
        @NamedArg("size") String size,
        @NamedArg("colour") String colour
    ) {
        this(size);
        this.colour = colour;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        eyeButton.setOnMousePressed(t -> handleButtonMousePressed());
        eyeButton.setOnMouseReleased(t -> handleButtonMouseReleased());

        if(colour != null)
            header.setFill(Paint.valueOf(colour));

        if(size != null)
            header.setFont(Font.font(Double.parseDouble(size)));
    }

    public String getFieldText() {
        return passwordField.getText();
    }

    public void setFieldText(String text) {
        passwordField.setText(text);
    }

    public void setBorderRed() {
        if(!passwordField.getStyleClass().contains("field-error"))
            passwordField.getStyleClass().add("field-error");
    }

    public void setBorderBlue() {
        passwordField.getStyleClass().remove("field-error");
    }

    private void handleButtonMousePressed() {
        double width = eyeButton.getWidth();
        double height = eyeButton.getHeight();

        eyeButtonIcon.setIconLiteral("fas-eye-slash");

        eyeButton.setMaxSize(width, height);
        eyeButton.setMinSize(width, height);
        eyeButton.setPrefSize(width, height);

        textField.setMaxSize(passwordField.getWidth(), passwordField.getHeight());
        textField.setMinSize(passwordField.getWidth(), passwordField.getHeight());
        textField.setPrefSize(passwordField.getWidth(), passwordField.getHeight());

        textField.setText(passwordField.getText());

        StringSelection stringSelection = new StringSelection(passwordField.getText());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);

        password.getChildren().set(0, textField);
    }

    private void handleButtonMouseReleased() {
        eyeButtonIcon.setIconLiteral("fas-eye");

        password.getChildren().set(0, passwordField);
    }
}

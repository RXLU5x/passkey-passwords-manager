package components;

import javafx.beans.NamedArg;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class TextField extends VBox implements Initializable
{
    @FXML
    private Text title;

    @FXML
    private TextFlow textFlow;

    @FXML
    private javafx.scene.control.TextField field;

    private final String text;

    private String colour;
    private String size;

    public TextField(@NamedArg("text") String text) {
        this.text = text;
        this.colour = null;
        this.size = null;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/TextField.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public TextField(
        @NamedArg("text") String text,
        @NamedArg("colour") String colour,
        @NamedArg("size") String size
    ) {
        this(text);
        this.colour = colour;
        this.size = size;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.title.setText(text);

        if(size != null)
            this.title.setFont(Font.font(Double.parseDouble(size)));

        if(colour != null)
            this.title.setFill(Paint.valueOf(colour));

        this.textFlow.managedProperty().bind(textFlow.visibleProperty());
    }

    public void setupFieldText(String text) {
        this.field.setText(text);
    }

    public String getFieldText() {
        return this.field.getText();
    }
}

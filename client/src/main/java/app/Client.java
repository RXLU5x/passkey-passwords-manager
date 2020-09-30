package app;

import components.home.Home;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Client extends Application
{
    public static void centerOnScreen(Stage stage) {
        Rectangle2D primScreenBounds = Screen.getPrimary().getBounds();

        stage.setX((primScreenBounds.getWidth() - stage.getWidth()) / 2.0);
        stage.setY((primScreenBounds.getHeight() - stage.getHeight()) / 2.0);
    }

    @Override
    public void start(Stage stage) {
        Home home = new Home();
        //Vaults home = new Vaults();
        Scene scene = new Scene(home);

        stage.setTitle("Passkey");
        stage.setScene(scene);

        stage.show();

        centerOnScreen(stage);
    }
}

package pharmancyApp;

import javafx.scene.image.Image;
import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class Main extends Application {

    private static void logout() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/logout";
        try {
            Response response = HTTPMethods.get(url);
            if (response != null) {
                int responseCode = response.getRespondCode();
                if (responseCode >= 200 && responseCode <= 299) {
                    Authentication.clearToken();
                    Authentication.setLogin(false);
                    if (Settings.DEBUG)
                        System.out.println("INFO:Logged out");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/scenes/LoginScene.fxml")));
        primaryStage.setTitle("Σύνδεση στο σύστημα");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
        primaryStage.getIcons().add(new Image("assets/images/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(t -> {
            if (Authentication.isLoggedIn()) {
                logout();
            }
            Platform.exit();
            System.exit(0);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}

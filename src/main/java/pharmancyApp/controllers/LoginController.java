package pharmancyApp.controllers;

import REST.Authentication;
import REST.HTTPMethods;
import REST.Response;
import animatefx.animation.FadeInRight;
import animatefx.util.ParallelAnimationFX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.UserDomain;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.Utils.AlertDialogs;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;


public class LoginController implements Initializable {
    @FXML
    private TextField usernameFld;
    @FXML
    private TextField passwordFld;
    @FXML
    private AnchorPane rightContainer;
    @FXML
    private Label appInfo;

    @FXML
    private void showDashboard(String domain, ActionEvent event) {
        switch (Objects.requireNonNull(UserDomain.getDomainFromString(domain))) {
            case PHARMACIST:
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhDashboardScene.fxml")));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setTitle("Πίνακας ελέγχου - Ιδιότητα:Φαρμακοποιός");
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;
            case SUPPLIER:
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpDashboardScene.fxml")));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setTitle("Πίνακας ελέγχου - Ιδιότητα:Προμηθευτής");
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;
            case CUSTOMER:
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/CU/CuDashboardScene.fxml")));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setTitle("Καλώς ήρθατε!");
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();

                }
                break;

        }
    }

    @FXML
    private void login(ActionEvent event) {
        String username = usernameFld.getText().trim();
        String password = passwordFld.getText().trim();
        if (username.isEmpty() && password.isEmpty()) {
            String headerText = "Ελλιπή στοιχεία";
            String contentText = "Για να συνδεθείτε απαιτείτε username και password.";
            AlertDialogs.error(headerText, null, contentText);
        } else {
            String jsonString = "{\"username\": \"" + username + "\", \"password\":\"" + password + "\"}";
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/login";

            try {
                Response response = HTTPMethods.post(jsonString, url);
                if (response != null) {
                    int respondCode = response.getRespondCode();
                    JSONObject jsonResponse = new JSONObject(response.getResponse());
                    if (respondCode >= 200 && respondCode <= 299) {
                        Authentication.setToken(jsonResponse.getString("token"));
                        Authentication.setLogin(true);
                        String domain = jsonResponse.getString("domain");
                        showDashboard(domain, event);

                    } else {
                        String headerText = "Αδυναμία συνδεσης";
                        AlertDialogs.error(headerText, jsonResponse, null);
                    }
                }else{
                    String headerText = "Αδυναμία συνδεσης";
                    String contentText="Η επικοινωνία με τον εξυπηρετητή απέτυχε";
                    AlertDialogs.error(headerText, null, contentText);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        appInfo.setText(Settings.buildVersion + " - " + Settings.buildDate);
        new ParallelAnimationFX(new FadeInRight(rightContainer)).play();

    }
}

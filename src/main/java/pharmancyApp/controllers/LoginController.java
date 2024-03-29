package pharmancyApp.controllers;

import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
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
import pharmancyApp.models.UserDomain;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;
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

    private boolean checkUserCredentials(String username, String password) {
        String validationError = null;
        boolean isValid = true;

        if (password.isEmpty()) {
            validationError = "Παρακαλώ συμπληρώστε κωδικό πρόσβασης (Password)";
            isValid = false;
        }

        if (username.isEmpty()) {
            validationError = "Παρακαλώ συμπληρώστε όνομα χρήστη (Username)";
            isValid = false;
        }

        if (!isValid) {
            AlertDialogs.alertPlainText(Alert.AlertType.ERROR,"Σφάλμα","Ελλιπή στοιχεία",validationError);
        }

        return isValid;
    }


    @FXML
    private void login(ActionEvent event) {
        String username = usernameFld.getText().trim();
        String password = passwordFld.getText().trim();
        if (checkUserCredentials(username, password)) {
            String jsonString = "{\"username\": \"" + username + "\", \"password\":\"" + password + "\"}";
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/login";

            try {
                Response response = HTTPMethods.post(jsonString, url);
                if (response != null) {
                    int respondCode = response.getRespondCode();
                    if (respondCode >= 200 && respondCode <= 299) {
                        JSONObject jsonResponse = new JSONObject(response.getResponse());
                        Authentication.setToken(jsonResponse.getString("token"));
                        Authentication.setLogin(true);
                        String domain = jsonResponse.getString("domain");
                        showDashboard(domain, event);

                    } else {
                        JSONObject responseObj = new JSONObject(response.getResponse());
                        String headerText = "Αδυναμια συνδεσης";
                        AlertDialogs.alertJSONResponse(Alert.AlertType.ERROR,"Σφάλμα",headerText,responseObj);
                    }
                } else {
                    String headerText = "Αδυναμία συνδεσης";
                    String contentText = "Η επικοινωνία με τον εξυπηρετητή απέτυχε";
                    AlertDialogs.alertPlainText(Alert.AlertType.ERROR, "Σφάλμα", headerText,contentText);
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

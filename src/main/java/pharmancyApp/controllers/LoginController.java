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
import models.EmployeeDomain;
import org.json.JSONObject;
import pharmancyApp.Settings;
import java.net.URL;
import java.util.Map;
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
    private void showDashboard(String domain, ActionEvent event) {
        switch (Objects.requireNonNull(EmployeeDomain.getDomainFromString(domain))) {
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
            Alert alert = new Alert(Alert.AlertType.ERROR);
            ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
            alert.setResizable(false);
            alert.setWidth(200);
            alert.setHeight(300);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Ελειπής στοιχεία - Είσαι καθυστερημένος");
            alert.setContentText("Για να συνδεθείτε απαιτείτε username και password.");
            alert.showAndWait();
            if (alert.getResult().equals(okBtn)) {
                alert.close();
            }
        } else {
            String jsonString = "{\"username\": \"" + username + "\", \"password\":\"" + password + "\"}";
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/login";

            try {
                Response response = HTTPMethods.post(jsonString, url);
                int respondCode = response.getRespondCode();
                JSONObject jsonResponse = new JSONObject(response.getResponse());
                if (respondCode >= 200 && respondCode <= 299) {
                    Authentication.setToken(jsonResponse.getString("token"));
                    Authentication.setLogin(true);
                    String domain = jsonResponse.getString("domain");
                    showDashboard(domain, event);

                } else {
                    StringBuilder errorMessage = new StringBuilder();
                    Map<String, Object> i = jsonResponse.toMap();
                    for (Map.Entry<String, Object> entry : i.entrySet()) {
                        errorMessage.append(entry.getValue().toString()).append("\n");
                        System.out.println(entry.getKey() + "/" + entry.getValue());

                    }
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
                    alert.setResizable(false);
                    alert.setWidth(200);
                    alert.setHeight(300);
                    alert.setTitle("Σφάλμα");
                    alert.setHeaderText("Αδυναμία συνδεσης");
                    alert.setContentText(errorMessage.toString());
                    alert.showAndWait();
                    if (alert.getResult().equals(okBtn)) {
                        alert.close();
                    }
                }
            } catch (Exception e) {
                e.fillInStackTrace();

            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new ParallelAnimationFX(
                new FadeInRight(rightContainer)).play();

    }
}

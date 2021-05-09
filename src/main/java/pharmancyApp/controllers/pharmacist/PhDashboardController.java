package pharmancyApp.controllers.pharmacist;

import REST.Authentication;
import REST.HTTPMethods;
import REST.Response;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Employee;
import models.Location;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.controllers.UpdateLocationDetailsController;
import pharmancyApp.controllers.UpdateUserDetailsController;

import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class PhDashboardController implements Initializable {

    @FXML
    private Label usernameLbl;
    @FXML
    private Label domainLbl;
    @FXML
    private Label idLbl;
    @FXML
    private Label firstnameLbl;
    @FXML
    private Label lastnameLbl;
    @FXML
    private Label emailLbl;
    @FXML
    private Label streetLbl;
    @FXML
    private Label streetNumLbl;
    @FXML
    private Label cityLbl;
    @FXML
    private Label postalCodeLbl;

    Employee employee;
    Location location;


    private void bindLabels(Employee employee, Location location) {
        idLbl.textProperty().bind(employee.idProperty().asString());
        usernameLbl.textProperty().bind(employee.usernameProperty());
        emailLbl.textProperty().bind(employee.emailProperty().get().isEmpty() ? new SimpleStringProperty("(κενό)") : employee.emailProperty());
        firstnameLbl.textProperty().bind(employee.firstnameProperty().get().isEmpty() ? new SimpleStringProperty("(κενό)") : employee.firstnameProperty());
        lastnameLbl.textProperty().bind(employee.lastnameProperty().get().isEmpty() ? new SimpleStringProperty("(κενό)") : employee.lastnameProperty());
        domainLbl.textProperty().bind(switch (employee.domainProperty().get()) {
            case "PH" -> new SimpleStringProperty("Φαρμακοποιός");
            case "SP" -> new SimpleStringProperty("Προμηθευτής");
            default -> new SimpleStringProperty("(κενό)");
        });
        streetLbl.textProperty().bind(location.streetProperty().get().isEmpty()? new SimpleStringProperty("(κενό)") : location.streetProperty());
        streetNumLbl.textProperty().bind(location.streetNumProperty().asString().get().isEmpty()? new SimpleStringProperty("(κενό)") : location.streetNumProperty().asString());
        cityLbl.textProperty().bind(location.cityProperty().get().isEmpty()? new SimpleStringProperty("(κενό)") : location.cityProperty());
        postalCodeLbl.textProperty().bind(location.postalCodeProperty().asString().get().isEmpty()? new SimpleStringProperty("(κενό)") : location.postalCodeProperty().asString());
    }

    private void initDashboard() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/dashboard";
        try {
            Response response = HTTPMethods.get(url);
            int respondCode = response.getRespondCode();
            JSONObject jsonResponse = new JSONObject(response.getResponse());
            if (respondCode >= 200 && respondCode <= 299) {
                JSONObject userJson = jsonResponse.getJSONObject("user");
                int uId = userJson.getInt("id");
                String username = userJson.getString("username");
                String email = userJson.getString("email");
                String domain = userJson.getJSONObject("employee").getString("domain");
                String firstname = userJson.getString("first_name");
                String lastname = userJson.getString("last_name");
                employee = new Employee(uId, username, email, firstname, lastname, domain);
                JSONObject locationJson = jsonResponse.getJSONObject("location");
                int pId = locationJson.getInt("id");
                String street = locationJson.getString("street");
                int streetNum = locationJson.getInt("street_num");
                String city = locationJson.getString("city");
                int postalCode = locationJson.getInt("postal_code");
                location = new Location(pId, street, streetNum, city, postalCode);
                bindLabels(employee, location);
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
                alert.setHeaderText("Αδυναμια συνδεσης");
                alert.setContentText(errorMessage.toString());
                alert.showAndWait();
                if (alert.getResult().equals(okBtn)) {
                    alert.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void updateUser() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/UpdateUserScene.fxml")));
            Parent root = loader.load();
            UpdateUserDetailsController updateUserDetailsController = loader.getController();
            updateUserDetailsController.setEmployee(employee);
            updateUserDetailsController.setFields();
            Stage stage = new Stage();
            stage.setTitle("Σύνδεση στο σύστημα");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void updatePharmancyDetails() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhUpdatePharmancyScene.fxml")));
            Parent root = loader.load();
            UpdateLocationDetailsController updateLocationDetailsController = loader.getController();
            updateLocationDetailsController.setEmployee(employee);
            updateLocationDetailsController.setPharmancy(location);
            updateLocationDetailsController.setFields();
            Stage stage = new Stage();
            stage.setTitle("Σύνδεση στο σύστημα");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private boolean checkIfPharmacistDetailsAreEmpty() {
        return employee.usernameProperty().get().isEmpty() && employee.lastnameProperty().get().isEmpty() && employee.firstnameProperty().get().isEmpty() && employee.emailProperty().get().isEmpty();
    }


    private boolean checkIfLocationDetailsAreEmpty() {
        return location.streetProperty().get().isEmpty() && location.streetNumProperty().get() == 0 && location.cityProperty().get().isEmpty() && location.postalCodeProperty().get() == 0;
    }

    @FXML
    private void makeOrder() {
        if(checkIfPharmacistDetailsAreEmpty() && checkIfLocationDetailsAreEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhMakeOrderScene.fxml")));
                Parent root = loader.load();
                PhMakeOrderController phMakeOrderController = loader.getController();
                phMakeOrderController.setEmployee(employee);
                phMakeOrderController.setPharmancy(location);
                phMakeOrderController.fetchMedicines();
                Stage stage = new Stage();
                stage.setTitle("Δημιουργία παραγγελίας");
                Scene scene = new Scene(root);
                scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
                stage.setScene(scene);
                stage.setResizable(false);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();

            }
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR);
            ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
            alert.setTitle("Σφάλμα");
            alert.setHeaderText("Αδυναμια δημιουργία παραγγελίας");
            alert.setContentText("Βεβαιωθείτε ότι έχετε συμπληρώσει όλα τα στοιχεία χρήστη και όλα τα στοιχεία τοποθεσίας.");
            alert.showAndWait();
            if (alert.getResult().equals(okBtn)) {
                alert.close();
            }
        }
    }

    @FXML
    public void getOrdersList() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhOrdersListScene.fxml")));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Δημιουργία παραγγελίας");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void getMedicinesList() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhMedicinesListScene.fxml")));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Δημιουργία παραγγελίας");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void showAvailableMedicines() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhAvailableMedicinesListScene.fxml")));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Δημιουργία παραγγελίας");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void showCustomersOrders(){
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhCustomersOrdersListScene.fxml")));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Δημιουργία παραγγελίας");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void logout(ActionEvent event) {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/logout";
        try {
            Response response = HTTPMethods.get(url);
            int respondCode = response.getRespondCode();
            if (respondCode >= 200 && respondCode <= 299) {
                Authentication.clearToken();
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/LoginScene.fxml")));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setTitle("Σύνδεση στο σύστημα");
                    Scene scene = new Scene(root);
                    scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
                    stage.setScene(scene);
                    stage.setResizable(false);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initDashboard();

    }
}

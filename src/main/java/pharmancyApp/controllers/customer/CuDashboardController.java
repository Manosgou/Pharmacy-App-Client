package pharmancyApp.controllers.customer;

import REST.Authentication;
import REST.HTTPMethods;
import REST.Response;
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

public class CuDashboardController implements Initializable {

    @FXML
    private Label usernameLbl;
    @FXML
    private Label emailLbl;
    @FXML
    private Label firstnameLbl;
    @FXML
    private Label lastnameLbl;
    @FXML
    private Label streetLbl;
    @FXML
    private Label streetNumLbl;
    @FXML
    private Label cityLbl;
    @FXML
    private Label postalCodeLbl;
    @FXML
    private Label orderMedicineLbl;
    @FXML
    private Label orderQuantityLbl;
    @FXML
    private Label orderTotalPriceLbl;


    Employee employee;
    Location location;

    private void bindLabels(Employee employee, Location location) {
        usernameLbl.textProperty().bind(employee.usernameProperty());
        if (employee.emailProperty().get().isEmpty()) {
            employee.emailProperty().set("(κενό)");
        }
        emailLbl.textProperty().bind(employee.emailProperty());

        if (employee.firstnameProperty().get().isEmpty()) {
            employee.firstnameProperty().set("(κενό)");
        }
        firstnameLbl.textProperty().bind(employee.firstnameProperty());

        if (employee.lastnameProperty().get().isEmpty()) {
            employee.lastnameProperty().set("(κενό)");
        }
        lastnameLbl.textProperty().bind(employee.lastnameProperty());

        if (location.streetProperty().get().isEmpty()) {
            location.streetProperty().set("(κενό)");
        }
        streetLbl.textProperty().bind(location.streetProperty());
        streetNumLbl.textProperty().bind(location.streetNumProperty().asString());

        if (location.cityProperty().get().isEmpty()) {
            location.cityProperty().set("(κενό)");
        }
        cityLbl.textProperty().bind(location.cityProperty());
        postalCodeLbl.textProperty().bind(location.postalCodeProperty().asString());
    }


    private void setLastOrderLabels(String orderMedicine, int orderQuantity, float orderPrice){
        orderMedicineLbl.setText(orderMedicine==null ?"(κενό)":orderMedicine);
        orderQuantityLbl.setText(Integer.toString(orderQuantity));
        orderTotalPriceLbl.setText(Float.toString(orderPrice));
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
                JSONObject pharmancyJson = jsonResponse.getJSONObject("location");
                int pId = pharmancyJson.getInt("id");
                String street = pharmancyJson.getString("street");
                int streetNum = pharmancyJson.getInt("street_num");
                String city = pharmancyJson.getString("city");
                int postalCode = pharmancyJson.getInt("postal_code");
                location = new Location(pId, street, streetNum, city, postalCode);
                if (!(jsonResponse.isNull("last_order"))) {
                    JSONObject lastOrder = jsonResponse.getJSONObject("last_order");
                    String orderMedicine = lastOrder.getString("medicine");
                    int orderQuantity = lastOrder.getInt("quantity");
                    float orderPrice = lastOrder.getFloat("total_price");
                    setLastOrderLabels(orderMedicine,orderQuantity,orderPrice);
                }
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
    private void updateLocationDetails() {
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


    @FXML
    private void updateUserDetails() {
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
    private void showOrders() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/CU/CuOrdersListScene.fxml")));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Προβολή παραγγελιών");
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
    private void showMedicinesList() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/CU/CuMedicinesListScene.fxml")));
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

    private boolean checkIfPharmacistDetailsAreEmpty() {
        return employee.usernameProperty().get().trim().isEmpty() && employee.lastnameProperty().get().trim().isEmpty() && employee.firstnameProperty().get().trim().isEmpty() && employee.emailProperty().get().trim().isEmpty();
    }


    private boolean checkIfLocationDetailsAreEmpty() {
        return location.streetProperty().get().isEmpty() && location.streetNumProperty().get() == 0 && location.cityProperty().get().isEmpty() && location.postalCodeProperty().get() == 0;
    }

    @FXML
    private void makeOrder() {


        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/CU/CuMakeOrderScene.fxml")));
            Parent root = loader.load();
            CuMakeOrderController cuMakeOrderController = loader.getController();
            cuMakeOrderController.setEmployee(employee);
            cuMakeOrderController.setPharmancy(location);
            cuMakeOrderController.fetchMedicines();
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

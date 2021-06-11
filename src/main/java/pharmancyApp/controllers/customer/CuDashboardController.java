package pharmancyApp.controllers.customer;

import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
import animatefx.animation.*;
import animatefx.util.ParallelAnimationFX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import pharmancyApp.models.User;
import pharmancyApp.models.Location;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;
import pharmancyApp.controllers.UpdateLocationDetailsController;
import pharmancyApp.controllers.UpdateUserDetailsController;

import java.net.URL;
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
    @FXML
    private AnchorPane logOutContainer;
    @FXML
    private AnchorPane userInformationContainer;
    @FXML
    private HBox centerBtnBar;
    @FXML
    private HBox lastOrderContainer;


    User user;
    Location location;

    private void bindLabels(User user, Location location) {
        usernameLbl.textProperty().bind(user.usernameProperty());
        if (user.emailProperty().get().isEmpty()) {
            user.emailProperty().set("(κενό)");
        }
        emailLbl.textProperty().bind(user.emailProperty());

        if (user.firstnameProperty().get().isEmpty()) {
            user.firstnameProperty().set("(κενό)");
        }
        firstnameLbl.textProperty().bind(user.firstnameProperty());

        if (user.lastnameProperty().get().isEmpty()) {
            user.lastnameProperty().set("(κενό)");
        }
        lastnameLbl.textProperty().bind(user.lastnameProperty());

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


    private void setLastOrderLabels(String orderMedicine, int orderQuantity, float orderPrice) {
        orderMedicineLbl.setText(orderMedicine == null ? "(κενό)" : orderMedicine);
        orderQuantityLbl.setText(Integer.toString(orderQuantity));
        orderTotalPriceLbl.setText(Float.toString(orderPrice));
    }

    private void initDashboard() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/dashboard";
        try {
            Response response = HTTPMethods.get(url);
            if (response != null) {
                int respondCode = response.getRespondCode();
                if (respondCode >= 200 && respondCode <= 299) {
                    JSONObject jsonResponse = new JSONObject(response.getResponse());
                    JSONObject userProfileJson = jsonResponse.getJSONObject("user_profile");
                    JSONObject userDetails = userProfileJson.getJSONObject("user");
                    int uId = userDetails.getInt("id");
                    String username = userDetails.getString("username");
                    String email = userDetails.getString("email");
                    String domain = userProfileJson.getString("domain");
                    String firstname = userDetails.getString("first_name");
                    String lastname = userDetails.getString("last_name");
                    user = new User(uId, username, email, firstname, lastname, domain);
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
                        setLastOrderLabels(orderMedicine, orderQuantity, orderPrice);
                    }
                    bindLabels(user, location);
                } else {
                    JSONObject jsonResponse = new JSONObject(response.getResponse());
                    String headerText = "Αδυναμια συνδεσης";
                    AlertDialogs.error(headerText, jsonResponse, null);
                    if (respondCode == 401) {
                        Authentication.setLogin(false);
                    }
                }
            } else {
                String headerText = "Αδυναμία συνδεσης";
                String contentText = "Η επικοινωνία με τον εξυπηρετητή απέτυχε";
                AlertDialogs.error(headerText, null, contentText);
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
            updateLocationDetailsController.setEmployee(user);
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
            updateUserDetailsController.setEmployee(user);
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
            CuOrdersListController cuOrdersListController =loader.getController();
            cuOrdersListController.setUser(user);
            cuOrdersListController.setLocation(location);
            cuOrdersListController.fetchOrders();
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

    private boolean checkIfUserDetailsAreEmpty() {
        String username = user.usernameProperty().get().trim();
        String lastname = user.lastnameProperty().get().trim();
        String firstname = user.firstnameProperty().get().trim();
        String email = user.emailProperty().get().trim();

        String errorMessage = null;
        boolean isValid = true;

        if (username.isEmpty() || username.equals("(κενό)")) {
            errorMessage = "Παρακαλώ συμπληρώστε το username";
            isValid = false;
        }
        if (lastname.isEmpty() || lastname.equals("(κενό)")) {
            errorMessage = "Παρακαλώ συμπληρώστε το επίθετο";
            isValid = false;
        }
        if (firstname.isEmpty() || firstname.equals("(κενό)")) {
            errorMessage = "Παρακαλώ συμπληρώστε το όνομα";
            isValid = false;
        }
        if (email.isEmpty() || email.equals("(κενό)")) {
            errorMessage = "Παρακαλώ συμπληρώστε το email";
            isValid = false;
        }

        if (!isValid) {
            String headerText = "Ελλιπή στοιχεία";
            AlertDialogs.error(headerText, null, errorMessage);
        }


        return isValid;
    }


    private boolean checkIfLocationDetailsAreEmpty() {
        String street = location.streetProperty().get().trim();
        String city = location.cityProperty().get().trim();

        String errorMessage = null;
        boolean isValid = true;

        if (street.isEmpty() || street.equals("(κενό)")) {
            errorMessage = "Παρακαλώ συμπληρώστε το όδο";
            isValid = false;
        }

        if (city.isEmpty() || city.equals("(κενό)")) {
            errorMessage = "Παρακαλώ συμπληρώστε την πόλη";
            isValid = false;
        }

        if (!isValid) {
            String headerText = "Ελλιπή στοιχεία";
            AlertDialogs.error(headerText, null, errorMessage);
        }

        return isValid;
    }

    @FXML
    private void makeOrder() {
        if (checkIfUserDetailsAreEmpty() && checkIfLocationDetailsAreEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/CU/CuMakeOrderScene.fxml")));
                Parent root = loader.load();
                CuMakeOrderController cuMakeOrderController = loader.getController();
                cuMakeOrderController.setEmployee(user);
                cuMakeOrderController.setPharmancy(location);
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
        } else {
            System.out.println("Noooo");
        }


    }

    @FXML
    private void loginPage(ActionEvent event){
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

    @FXML
    private void logout(ActionEvent event) {
        if (Authentication.isLoggedIn()) {
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/logout";
            try {
                Response response = HTTPMethods.get(url);
                if (response != null) {
                    int respondCode = response.getRespondCode();
                    if (respondCode >= 200 && respondCode <= 299) {
                        Authentication.clearToken();
                        Authentication.setLogin(false);
                        loginPage(event);

                    } else {
                        JSONObject responseObj = new JSONObject(response.getResponse());
                        String headerText = "Αδυναμια συνδεσης";
                        AlertDialogs.error(headerText, responseObj, null);
                        if (respondCode == 401) {
                            Authentication.setLogin(false);
                            logout(event);
                        }
                    }
                } else {
                    String headerText = "Αδυναμία συνδεσης";
                    String contentText = "Η επικοινωνία με τον εξυπηρετητή απέτυχε";
                    AlertDialogs.error(headerText, null, contentText);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Authentication.clearToken();
            loginPage(event);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        centerBtnBar.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/CuDashboard.css")).toExternalForm());
        new ParallelAnimationFX(
                new FadeInUp(logOutContainer),
                new FadeInLeft(userInformationContainer),
                new ZoomIn(centerBtnBar),
                new SlideInUp(lastOrderContainer)).play();
        initDashboard();
    }
}

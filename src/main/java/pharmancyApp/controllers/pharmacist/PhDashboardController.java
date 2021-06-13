package pharmancyApp.controllers.pharmacist;

import javafx.scene.control.Alert;
import javafx.stage.Window;
import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
import com.jfoenix.controls.JFXListView;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import pharmancyApp.models.User;
import pharmancyApp.models.Location;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;
import pharmancyApp.controllers.UpdateLocationDetailsController;
import pharmancyApp.controllers.UpdateUserDetailsController;
import java.net.URL;
import java.util.*;

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
    @FXML
    private PieChart medicinesPieChart;
    @FXML
    private JFXListView<String> deficitMedicinesLst;
    @FXML
    private BarChart<Integer, String> ordersBarChart;

    User user;
    Location location;


    private void bindLabels(User user, Location location) {
        idLbl.textProperty().bind(user.idProperty().asString());
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
        domainLbl.setText("Φαρμακοποιός" + " " + "(" + user.domainProperty().get() + ")");
        if (location.streetProperty().get().isEmpty()) {
            location.streetProperty().set("(κενό)");
        }
        streetLbl.textProperty().bind(location.streetProperty().get().isEmpty() ? new SimpleStringProperty("(κενό)") : location.streetProperty());
        streetNumLbl.textProperty().bind(location.streetNumProperty().asString().get().isEmpty() ? new SimpleStringProperty("(κενό)") : location.streetNumProperty().asString());
        if (location.cityProperty().get().isEmpty()) {
            location.cityProperty().set("(κενό)");
        }
        cityLbl.textProperty().bind(location.cityProperty().get().isEmpty() ? new SimpleStringProperty("(κενό)") : location.cityProperty());
        postalCodeLbl.textProperty().bind(location.postalCodeProperty().asString().get().isEmpty() ? new SimpleStringProperty("(κενό)") : location.postalCodeProperty().asString());
    }

    private void getOrdersBarChart(JSONObject object) {
        int ordersOnProcess = object.getInt("orders_on_process");
        int ordersOnDeliver = object.getInt("orders_on_deliver");
        int deliveredOrders = object.getInt("orders_delivered");
        XYChart.Series data = new XYChart.Series();
        data.getData().add(new XYChart.Data("Παραγγελίες σε επεξεργασία", ordersOnProcess));
        data.getData().add(new XYChart.Data("Παραγγελίες σε παράδοση", ordersOnDeliver));
        data.getData().add(new XYChart.Data("Παραγγελίες", deliveredOrders));
        ordersBarChart.getData().add(data);
    }


    private void getPieChart(JSONArray jsonArray) {
        ObservableList<PieChart.Data> pieChartValuesList = FXCollections.observableArrayList();
        PieChart.Data pieChartData = null;
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            String[] keys = JSONObject.getNames(obj);
            for (String key : keys) {
                int value = obj.getInt(key);
                pieChartData = new PieChart.Data(key, value);
            }
            pieChartValuesList.add(pieChartData);
        }
        medicinesPieChart.setData(pieChartValuesList);
    }

    private void getDeficitMedicines(JSONArray jsonArray) {
        ObservableList<String> deficitMedicines = FXCollections.observableArrayList();
        for (int i = 0; i < jsonArray.length(); i++) {
            String deficitMedicine = jsonArray.getString(i);
            deficitMedicines.add(deficitMedicine);
        }
        deficitMedicinesLst.setItems(deficitMedicines);
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
                    JSONArray deficitMedicines = jsonResponse.getJSONArray("deficit_medicines");
                    getDeficitMedicines(deficitMedicines);
                    JSONArray medicinesQuantity = jsonResponse.getJSONArray("medicines_quantity");
                    getPieChart(medicinesQuantity);
                    JSONObject orders = jsonResponse.getJSONObject("orders");
                    getOrdersBarChart(orders);
                    JSONObject locationJson = jsonResponse.getJSONObject("location");
                    int pId = locationJson.getInt("id");
                    String street = locationJson.getString("street");
                    int streetNum = locationJson.getInt("street_num");
                    String city = locationJson.getString("city");
                    int postalCode = locationJson.getInt("postal_code");
                    location = new Location(pId, street, streetNum, city, postalCode);
                    bindLabels(user, location);
                } else {

                    JSONObject responseObj = new JSONObject(response.getResponse());
                    String headerText = "Αδυναμια συνδεσης";
                    AlertDialogs.alertJSONResponse(Alert.AlertType.ERROR,"Σφάλμα",headerText,responseObj);
                    if (respondCode == 401) {
                        Authentication.setLogin(false);
                    }
                }
            } else {
                String headerText = "Αδυναμία συνδεσης";
                String contentText = "Η επικοινωνία με τον εξυπηρετητή απέτυχε";
                AlertDialogs.alertPlainText(Alert.AlertType.ERROR,"Σφάλμα",headerText,contentText);
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
    private void updatePharmancyDetails() {
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

    private boolean checkIfUserDetailsAreEmpty() {
        String username = user.usernameProperty().get().trim();
        String lastname = user.lastnameProperty().get().trim();
        String firstname = user.firstnameProperty().get().trim();
        String email = user.emailProperty().get().trim();

        String errorMessage = null;
        boolean isValid = true;

        if (username.isEmpty()) {
            errorMessage = "Παρακαλώ συμπληρώστε το username";
            isValid = false;
        }
        if (lastname.isEmpty()) {
            errorMessage = "Παρακαλώ συμπληρώστε το επίθετο";
            isValid = false;
        }
        if (firstname.isEmpty()) {
            errorMessage = "Παρακαλώ συμπληρώστε το όνομα";
            isValid = false;
        }
        if (email.isEmpty()) {
            errorMessage = "Παρακαλώ συμπληρώστε το email";
            isValid = false;
        }

        if (!isValid) {
            String headerText = "Ελλιπή στοιχεία";
            AlertDialogs.alertPlainText(Alert.AlertType.ERROR,"Σφάλμα",headerText,errorMessage);
        }


        return isValid;
    }


    private boolean checkIfLocationDetailsAreEmpty() {
        String street = location.streetProperty().get().trim();
        String city = location.cityProperty().get().trim();

        String errorMessage = null;
        boolean isValid = true;

        if (street.isEmpty()) {
            errorMessage = "Παρακαλώ συμπληρώστε το όδο";
            isValid = false;
        }

        if (city.isEmpty()) {
            errorMessage = "Παρακαλώ συμπληρώστε την πόλη";
            isValid = false;
        }

        if (!isValid) {
            String headerText = "Ελλιπή στοιχεία";
            AlertDialogs.alertPlainText(Alert.AlertType.ERROR,"Σφάλμα",headerText,errorMessage);
        }

        return isValid;
    }

    @FXML
    private void makeOrder() {
        if (checkIfUserDetailsAreEmpty() && checkIfLocationDetailsAreEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhMakeOrderScene.fxml")));
                Parent root = loader.load();
                PhMakeOrderController phMakeOrderController = loader.getController();
                phMakeOrderController.setEmployee(user);
                phMakeOrderController.setPharmancy(location);
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
    }

    @FXML
    public void getOrdersList() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhOrdersListScene.fxml")));
            Parent root = loader.load();
            PhOrdersListController phOrdersListController = loader.getController();
            phOrdersListController.setUser(user);
            phOrdersListController.setLocation(location);
            phOrdersListController.fetchOrders();
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
    private void getMedicinesForSaleList() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhMedicinesForSaleListScene.fxml")));
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
    private void showCustomersOrders() {
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
    private void loginPage(ActionEvent event) {
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

    private void closeWindows(ActionEvent event){
        Window currentWindow = ((Node) event.getSource()).getScene().getWindow();
        ArrayList<Window> windows = new ArrayList<>(Window.getWindows());
        windows.remove(currentWindow);
        windows.forEach(w->((Stage)w).close());
        loginPage(event);
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
                        closeWindows(event);

                    } else {
                        JSONObject responseObj = new JSONObject(response.getResponse());
                        String headerText = "Αδυναμια συνδεσης";
                        AlertDialogs.alertJSONResponse(Alert.AlertType.ERROR,"Σφάλμα",headerText,responseObj);
                        if (respondCode == 401) {
                            Authentication.setLogin(false);
                            logout(event);
                        }
                    }

                } else {
                    String headerText = "Αδυναμία συνδεσης";
                    String contentText = "Η επικοινωνία με τον εξυπηρετητή απέτυχε";
                    AlertDialogs.alertPlainText(Alert.AlertType.ERROR,"Σφάλμα",headerText,contentText);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Authentication.clearToken();
            closeWindows(event);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initDashboard();

    }
}

package pharmancyApp.controllers.supplier;

import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
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
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;
import pharmancyApp.controllers.UpdateUserDetailsController;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SpDashboardController implements Initializable {
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
    private Label orderFullNameLbl;
    @FXML
    private Label orderMedicineLbl;
    @FXML
    private Label orderQuantityLbl;
    @FXML
    private Label orderTotalPriceLbl;
    @FXML
    private Label medicineNameLbl;
    @FXML
    private Label medicineCategoryLbl;
    @FXML
    private Label medicineQuantityLbl;
    @FXML
    private Label medicinePriceLbl;
    @FXML
    private BarChart<String, Integer> barChart;
    @FXML
    private PieChart pieChart;


    private User user;


    private void bindEmployeeLabels(User user) {
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
        domainLbl.setText("Προμηθευτής" + " " + "(" + user.domainProperty().get() + ")");
    }


    private void getBarChart(int totalOrders, int totalMedicines, int totalCategories) {
        XYChart.Series data = new XYChart.Series();
        data.getData().add(new XYChart.Data("Παραγγελίες", totalOrders));
        data.getData().add(new XYChart.Data("Φάρμακα", totalMedicines));
        data.getData().add(new XYChart.Data("Κατηγορίες Φαρμάκων", totalCategories));
        barChart.getData().add(data);

    }

    private void setLastActionsLabels(String customerName, String orderMedicine, int orderQuantity, float orderPrice, String medicineName, String medicineCategory, int medicineQuantity, float medicinePrice) {
        orderFullNameLbl.setText(customerName == null ? "(κενό)" : customerName);
        orderMedicineLbl.setText(orderMedicine == null ? "(κενό)" : orderMedicine);
        orderQuantityLbl.setText(Integer.toString(orderQuantity));
        orderTotalPriceLbl.setText(Float.toString(orderPrice));
        medicineNameLbl.setText(medicineName == null ? "(κενό)" : medicineName);
        medicineCategoryLbl.setText(medicineCategory == null ? "(κενό)" : medicineCategory);
        medicineQuantityLbl.setText(Integer.toString(medicineQuantity));
        medicinePriceLbl.setText(Float.toString(medicinePrice));
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
        pieChart.setData(pieChartValuesList);
    }

    private void initDashboard() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/dashboard";
        try {
            Response response = HTTPMethods.get(url);
            if (response != null) {
                int respondCode = response.getRespondCode();
                JSONObject jsonResponse = new JSONObject(response.getResponse());
                if (respondCode >= 200 && respondCode <= 299) {
                    JSONObject userProfileJson = jsonResponse.getJSONObject("user_profile");
                    JSONObject userDetails = userProfileJson.getJSONObject("user");
                    int id = userDetails.getInt("id");
                    String username = userDetails.getString("username");
                    String email = userDetails.getString("email");
                    String domain = userProfileJson.getString("domain");
                    String firstname = userDetails.getString("first_name");
                    String lastname = userDetails.getString("last_name");
                    this.user = new User(id, username, email, firstname, lastname, domain);
                    String customerName = null;
                    String orderMedicine = null;
                    int orderQuantity = 0;
                    float orderPrice = 0.0f;
                    if (!(jsonResponse.isNull("last_order"))) {
                        JSONObject lastOrder = jsonResponse.getJSONObject("last_order");
                        customerName = lastOrder.getString("full_name");
                        orderMedicine = lastOrder.getString("medicine");
                        orderQuantity = lastOrder.getInt("quantity");
                        orderPrice = lastOrder.getFloat("total_price");
                    }
                    String medicineName = null;
                    String medicineCategory = null;
                    int medicineQuantity = 0;
                    float medicinePrice = 0.0f;
                    if (!(jsonResponse.isNull("last_medicine"))) {
                        JSONObject lastMedicine = jsonResponse.getJSONObject("last_medicine");
                        medicineName = lastMedicine.getString("name");
                        medicineCategory = lastMedicine.getString("category");
                        medicineQuantity = lastMedicine.getInt("quantity");
                        medicinePrice = lastMedicine.getFloat("price");
                    }
                    int totalOrders = jsonResponse.isNull("total_orders") ? 0 : jsonResponse.getInt("total_orders");
                    int totalMedicines = jsonResponse.isNull("total_medicines") ? 0 : jsonResponse.getInt("total_medicines");
                    int totalCategories = jsonResponse.isNull("total_categories") ? 0 : jsonResponse.getInt("total_categories");
                    JSONArray medicinesQuantity = jsonResponse.getJSONArray("medicines_quantity");
                    getPieChart(medicinesQuantity);
                    getBarChart(totalOrders, totalMedicines, totalCategories);
                    bindEmployeeLabels(this.user);
                    setLastActionsLabels(customerName, orderMedicine, orderQuantity, orderPrice, medicineName, medicineCategory, medicineQuantity, medicinePrice);
                } else {

                    JSONObject responseObj = new JSONObject(response.getResponse());
                    String headerText = "Αδυναμια συνδεσης";
                    AlertDialogs.error(headerText, responseObj, null);
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
    private void addNewMedicine() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpMedicineForm.fxml")));
            Parent root = loader.load();
            SpMedicineFormController spMedicineFormController = loader.getController();
            spMedicineFormController.fetchCategories();
            spMedicineFormController.init();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Εισαγωγή νέου φαρμάκου");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void addNewCategory() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpCategoryFormScene.fxml")));
            Parent root = loader.load();
            SpCategoryFormController spCategoryFormController = loader.getController();
            spCategoryFormController.init();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Εισαγωγή νέου φαρμάκου");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void showAllCategories() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpCategoriesListScene.fxml")));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Προβολή όλων των κατηγοριών");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void showAllMedicines() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpMedicinesListScene.fxml")));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Λίστα φαρμάκων");
            stage.setResizable(false);
            stage.show();
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Ενημέρωση στοιχείων χρήστη");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    @FXML
    private void showOrders() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpOrdersListScene.fxml")));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Προβολή όλων των κατηγοριών");
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
        initDashboard();

    }
}

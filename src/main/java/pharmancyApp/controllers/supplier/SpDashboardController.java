package pharmancyApp.controllers.supplier;

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
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Employee;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.controllers.UpdateUserDetailsController;

import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class SpDashboardController implements Initializable {
    @FXML
    private Label usernameLbl;
    @FXML
    private Label passwordLbl;
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


    Employee employee;


    private void bindEmployeeLabels(Employee employee) {
        idLbl.textProperty().bind(employee.idProperty().asString());
        usernameLbl.textProperty().bind(employee.usernameProperty());
        emailLbl.textProperty().bind(employee.emailProperty());
        firstnameLbl.textProperty().bind(employee.firstnameProperty());
        lastnameLbl.textProperty().bind(employee.lastnameProperty());
        passwordLbl.textProperty().bind(switch (employee.domainProperty().get()) {
            case "PH" -> new SimpleStringProperty("Φαρμακοποιός");
            case "SP" -> new SimpleStringProperty("Προμηθευτής");
            case "CU" -> new SimpleStringProperty("Πελάτης");
            default -> new SimpleStringProperty("(κενό)");
        });
    }


    private void getBarChart(int totalOrders,int totalMedicines,int totalCategories){
        XYChart.Series data = new XYChart.Series();
        data.getData().add(new XYChart.Data("Παραγγελίες", totalOrders));
        data.getData().add(new XYChart.Data("Φάρμακα"  , totalMedicines));
        data.getData().add(new XYChart.Data("Κατηγορίες Φαρμάκων"  , totalCategories));
        barChart.getData().add(data);

    }

    private void lastActionsLabels(String customerName, String orderMedicine, int orderQuantity, float orderPrice, String medicineName, String medicineCategory, int medicineQuantity, float medicinePrice) {
        orderFullNameLbl.setText(customerName);
        orderMedicineLbl.setText(orderMedicine);
        orderQuantityLbl.setText(Integer.toString(orderQuantity));
        orderTotalPriceLbl.setText(Float.toString(orderPrice));
        medicineNameLbl.setText(medicineName);
        medicineCategoryLbl.setText(medicineCategory);
        medicineQuantityLbl.setText(Integer.toString(medicineQuantity));
        medicinePriceLbl.setText(Float.toString(medicinePrice));
    }

    private void initDashboard() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/dashboard";
        try {
            Response response = HTTPMethods.get(url);
            int respondCode = response.getRespondCode();
            JSONObject jsonResponse = new JSONObject(response.getResponse());
            if (respondCode >= 200 && respondCode <= 299) {
                JSONObject user = jsonResponse.getJSONObject("user");
                int id = user.getInt("id");
                String username = user.getString("username");
                String email = user.getString("email");
                String domain = user.getJSONObject("employee").getString("domain");
                String firstname = user.getString("first_name");
                String lastname = user.getString("last_name");
                employee = new Employee(id, username, email, firstname, lastname, domain);
                JSONObject lastOrder = jsonResponse.getJSONObject("last_order");
                String customerName = lastOrder.getString("full_name");
                String orderMedicine = lastOrder.getString("medicine");
                int orderQuantity = lastOrder.getInt("quantity");
                float orderPrice = lastOrder.getFloat("total_price");
                JSONObject lastMedicine = jsonResponse.getJSONObject("last_medicine");
                String medicineName = lastMedicine.getString("name");
                String medicineCategory = lastMedicine.getString("category");
                int medicineQuantity = lastMedicine.getInt("quantity");
                float medicinePrice = lastMedicine.getFloat("price");
                int totalOrders = jsonResponse.getInt("total_orders");
                int totalMedicines = jsonResponse.getInt("total_medicines");
                int totalCategories = jsonResponse.getInt("total_categories");
                getBarChart(totalOrders,totalMedicines,totalCategories);
                bindEmployeeLabels(employee);
                lastActionsLabels(customerName, orderMedicine, orderQuantity, orderPrice, medicineName, medicineCategory, medicineQuantity, medicinePrice);
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
    private void addNewMedicine() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpMedicineForm.fxml")));
            Parent root = loader.load();
            SpMedicineFormController spMedicineFormController = loader.getController();
            spMedicineFormController.fetchCategories();
            spMedicineFormController.init();
            Stage stage = new Stage();
            stage.setTitle("Εισαγωγή νέου φαρμάκου");
            stage.setScene(new Scene(root));
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
            stage.setTitle("Εισαγωγή νέου φαρμάκου");
            stage.setScene(new Scene(root));
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
            SpCategoriesListController spCategoriesListController = loader.getController();
            spCategoriesListController.fetchCategories();
            Stage stage = new Stage();
            stage.setTitle("Εισαγωγή νέου φαρμάκου");
            stage.setScene(new Scene(root));
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
            SpMediciniesListController spMediciniesListController = loader.getController();
            spMediciniesListController.fetchMedicines();
            Stage stage = new Stage();
            stage.setTitle("Λίστα φαρμάκων");
            stage.setScene(new Scene(root));
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
            updateUserDetailsController.setEmployee(employee);
            updateUserDetailsController.setFields();
            Stage stage = new Stage();
            stage.setTitle("Σύνδεση στο σύστημα");
            stage.setScene(new Scene(root));
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
            stage.setTitle("Σύνδεση στο σύστημα");
            stage.setScene(new Scene(root));
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
            int responseCode = response.getRespondCode();
            if (responseCode == 200) {
                Authentication.clearToken();
                try {
                    FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/LoginScene.fxml")));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setTitle("Σύνδεση στο σύστημα");
                    stage.setScene(new Scene(root));
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

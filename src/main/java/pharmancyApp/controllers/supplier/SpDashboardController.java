package pharmancyApp.controllers.supplier;

import REST.Authentication;
import REST.HTTPMethods;
import REST.Response;
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Employee;
import org.json.JSONArray;
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


    Employee employee;


    private void bindEmployeeLabels(Employee employee) {
        idLbl.textProperty().bind(employee.idProperty().asString());
        usernameLbl.textProperty().bind(employee.usernameProperty());
        if(employee.emailProperty().get().isEmpty()){
            employee.emailProperty().set("(κενό)");
        }
        emailLbl.textProperty().bind(employee.emailProperty());

        if(employee.firstnameProperty().get().isEmpty()){
            employee.firstnameProperty().set("(κενό)");
        }
        firstnameLbl.textProperty().bind(employee.firstnameProperty());

        if(employee.lastnameProperty().get().isEmpty()){
            employee.lastnameProperty().set("(κενό)");
        }
        lastnameLbl.textProperty().bind(employee.lastnameProperty());
        domainLbl.setText("Προμηθευτής"+" "+"("+employee.domainProperty().get()+")");
    }


    private void getBarChart(int totalOrders, int totalMedicines, int totalCategories) {
        XYChart.Series data = new XYChart.Series();
        data.getData().add(new XYChart.Data("Παραγγελίες", totalOrders));
        data.getData().add(new XYChart.Data("Φάρμακα", totalMedicines));
        data.getData().add(new XYChart.Data("Κατηγορίες Φαρμάκων", totalCategories));
        barChart.getData().add(data);

    }

    private void lastActionsLabels(String customerName, String orderMedicine, int orderQuantity, float orderPrice, String medicineName, String medicineCategory, int medicineQuantity, float medicinePrice) {
        orderFullNameLbl.setText(customerName ==null ?"(κενό)":customerName);
        orderMedicineLbl.setText(orderMedicine==null ?"(κενό)":orderMedicine);
        orderQuantityLbl.setText(Integer.toString(orderQuantity));
        orderTotalPriceLbl.setText(Float.toString(orderPrice));
        medicineNameLbl.setText(medicineName==null ?"(κενό)":medicineName);
        medicineCategoryLbl.setText(medicineCategory==null ?"(κενό)":medicineCategory);
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
                String customerName = null;
                String orderMedicine = null;
                int orderQuantity =0;
                float orderPrice = 0.0f;
                if (!(jsonResponse.isNull("last_order"))) {
                    JSONObject lastOrder = jsonResponse.getJSONObject("last_order");
                    customerName = lastOrder.getString("full_name");
                    orderMedicine = lastOrder.getString("medicine");
                    orderQuantity = lastOrder.getInt("quantity");
                    orderPrice = lastOrder.getFloat("total_price");
                }
                String medicineName = null;
                String medicineCategory =null;
                int medicineQuantity =0;
                float medicinePrice =0.0f;
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
                Authentication.setLogin(false);
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

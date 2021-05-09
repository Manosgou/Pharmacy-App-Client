package pharmancyApp.controllers.customer;

import REST.HTTPMethods;
import REST.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Medicine;
import models.MedicineCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Settings;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class CuMedicinesListController implements Initializable {
    @FXML
    private TableView<Medicine> medicinesTable;
    @FXML
    private TableColumn<Medicine, String> medicineNameCol;
    @FXML
    private TableColumn<Medicine, String> medicineCategoryCol;
    @FXML
    private TableColumn<Medicine, Integer> medicineQuantityCol;
    @FXML
    private TableColumn<Medicine, Float> medicinePriceCol;

    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();


    private void getMedicinesTable(){
        medicineNameCol.setCellValueFactory(item -> item.getValue().nameProperty());
        medicineCategoryCol.setCellValueFactory(item -> item.getValue().getMedicineCategory().nameProperty());
        medicineQuantityCol.setCellValueFactory(item -> item.getValue().quantityProperty().asObject());
        medicinePriceCol.setCellValueFactory(item -> item.getValue().priceProperty().asObject());
        medicinesTable.setItems(medicines);
    }

    private void fetchMedicines(){
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/customer/get/medicines";
        try {
            Response response = HTTPMethods.get(url);
            int respondCode = response.getRespondCode();
            JSONArray jsonArray = new JSONArray(response.getResponse());
            if (respondCode >= 200 && respondCode <= 299) {
                MedicineCategory medicineCategory;
                Medicine medicine;
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    int quantity = jsonObject.getInt("quantity");
                    float price = jsonObject.getFloat("price");
                    JSONObject categoryObj = jsonObject.getJSONObject("category");
                    int  categoryId = categoryObj.getInt("id");
                    String categoryName = categoryObj.getString("name");
                    medicineCategory =  new MedicineCategory(categoryId,categoryName);
                    medicine = new Medicine(id, name, quantity, price,medicineCategory);
                    medicines.add(medicine);


                }
                getMedicinesTable();
            } else {
                StringBuilder errorMessage = new StringBuilder();
                JSONObject responseObj = new JSONObject(response);
                Map<String, Object> i = responseObj.toMap();
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fetchMedicines();
    }
}

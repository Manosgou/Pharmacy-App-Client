package pharmancyApp.controllers.pharmacist;

import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import pharmancyApp.models.Medicine;
import pharmancyApp.models.MedicineCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;

import java.net.URL;
import java.util.ResourceBundle;

public class PhAvailableMedicinesTableController implements Initializable {

    @FXML
    private TableView<Medicine> medicinesTable;

    @FXML
    private TableColumn<Medicine, String> medicineNameCol;

    @FXML
    private TableColumn<Medicine, String> medicineCategoryNameCol;

    @FXML
    private TableColumn<Medicine, Integer> medicineQuantityCol;

    @FXML
    private TableColumn<Medicine, Float> medicinePriceCol;

    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();

    @FXML
    private void getMedicinesTable() {
        medicineNameCol.setCellValueFactory(item -> item.getValue().nameProperty());
        medicineCategoryNameCol.setCellValueFactory(item -> item.getValue().getMedicineCategory().nameProperty());
        medicineQuantityCol.setCellValueFactory(item -> item.getValue().quantityProperty().asObject());
        medicinePriceCol.setCellValueFactory(item -> item.getValue().priceProperty().asObject());
        medicinesTable.setItems(medicines);
    }

    private void fetchMedicines() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/get/medicines";
        try {
            Response response = HTTPMethods.get(url);
            if (response != null) {
                int respondCode = response.getRespondCode();

                if (respondCode >= 200 && respondCode <= 299) {
                    JSONArray jsonArray = new JSONArray(response.getResponse());
                    Medicine medicine;
                    MedicineCategory medicineCategory;
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        int quantity = jsonObject.getInt("quantity");
                        float price = jsonObject.getFloat("price");
                        JSONObject categoryObj = jsonObject.getJSONObject("category");
                        int categoryId = categoryObj.getInt("id");
                        String categoryName = categoryObj.getString("name");
                        medicineCategory = new MedicineCategory(categoryId, categoryName);
                        medicine = new Medicine(id, name, quantity, price, medicineCategory);
                        medicines.addAll(medicine);

                    }

                    getMedicinesTable();


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
    private void refreshTable() {
        medicines.clear();
        fetchMedicines();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        medicinesTable.setPlaceholder(new Label("Δεν υπάρχουν διαθέσιμα φάρμακα"));
        fetchMedicines();
    }
}

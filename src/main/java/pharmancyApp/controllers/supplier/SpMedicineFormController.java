package pharmancyApp.controllers.supplier;

import REST.HTTPMethods;
import REST.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Medicine;
import models.MedicineCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Settings;
import java.util.Map;


public class SpMedicineFormController{

    @FXML
    private Label medHeaderLbl;
    @FXML
    private TextField medicineNamFld;

    @FXML
    private TextField medicineQuaFld;

    @FXML
    private TextField medicinePriFld;
    @FXML
    private Button sumbitMedBtn;

    @FXML
    private ComboBox<MedicineCategory> medCatComboBox;

    private Medicine medicine;
    private ObservableList<MedicineCategory> categories = FXCollections.observableArrayList();
    private boolean isUpdate = false;

    public void setUpdate(boolean update) {
        this.isUpdate = update;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    @FXML
    private void saveMedicine(ActionEvent ev) {
        String medicineName = medicineNamFld.getText();
        int medicineQuantity = Integer.parseInt(medicineQuaFld.getText());
        float medicinePrice = Float.parseFloat(medicinePriFld.getText());
        MedicineCategory category = medCatComboBox.getValue();

        String jsonString = "{\"name\":\"" + medicineName + "\",\"quantity\":\"" + medicineQuantity + "\",\"price\":\"" + medicinePrice + "\",\"category\":{\"id\":\"" + category.getId() + "\",\"name\":\"" + category.getName() + "\"}}";
        System.out.println(jsonString);
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/create/medicine";
        try {
            Response response = HTTPMethods.post(jsonString, url);
            int respondCode = response.getRespondCode();
            if (respondCode >= 200 && respondCode <= 299) {
                final Node source = (Node) ev.getSource();
                final Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            } else {
                StringBuilder errorMessage = new StringBuilder();
                JSONObject jsonResponse = new JSONObject(response.toString());
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

    public void fetchCategories() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/get/categories";
        try {
            Response response = HTTPMethods.get(url);
            int respondCode = response.getRespondCode();
            JSONArray jsonArray = new JSONArray(response.getResponse());
            if (respondCode >= 200 && respondCode <= 299) {
                MedicineCategory medicineCategory;
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    medicineCategory = new MedicineCategory(id, name);
                    categories.add(medicineCategory);


                }
                medCatComboBox.setItems(categories);
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

    @FXML
    private void updateMedicine(ActionEvent event) {
        String medicineName = medicineNamFld.getText();
        int medicineQuantity = Integer.parseInt(medicineQuaFld.getText());
        float medicinePrice = Float.parseFloat(medicinePriFld.getText());
        MedicineCategory category = medCatComboBox.getValue();

        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/update/medicine/" + medicine.getId();
        String jsonString = "{\"name\":\"" + medicineName + "\",\"quantity\":\"" + medicineQuantity + "\",\"price\":\"" + medicinePrice + "\",\"category\":{\"id\":\"" + category.getId() + "\",\"name\":\"" + category.getName() + "\"}}";
        try {
            Response response = HTTPMethods.put(jsonString, url);
            int respondCode = response.getRespondCode();
            JSONObject jsonResponse = new JSONObject(response.getResponse());
            if (respondCode >= 200 && respondCode <= 299) {
                final Node source = (Node) event.getSource();
                final Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
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
            e.printStackTrace();
        }


    }

    public void init() {
        if (isUpdate) {
            sumbitMedBtn.setOnAction(this::updateMedicine);
            sumbitMedBtn.setText("Ενημέρωση");
            medHeaderLbl.setText("Ενημέρωση φαρμάκου");
            medicineNamFld.setText(medicine.getName());
            medicineQuaFld.setText(Integer.toString(medicine.getQuantity()));
            medicinePriFld.setText(Float.toString(medicine.getPrice()));
            for (MedicineCategory c : categories) {
                if (c.getId() == medicine.getMedicineCategory().getId()) {
                    medCatComboBox.setValue(c);
                }
            }

        } else {
            sumbitMedBtn.setOnAction(this::saveMedicine);
            sumbitMedBtn.setText("Αποθήκευση");
            medHeaderLbl.setText("Εισαγωγή νέου φαρμάκου");
        }
    }


}

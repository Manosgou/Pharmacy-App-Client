package pharmancyApp.controllers.supplier;

import REST.HTTPMethods;
import REST.Response;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.MedicineCategory;
import org.json.JSONObject;
import javafx.event.ActionEvent;
import pharmancyApp.Settings;

import java.util.Map;

public class SpCategoryFormController {
    @FXML
    private Label catHeaderLbl;
    @FXML
    private TextField categoryNameFld;
    @FXML
    private Button submitCatBtn;
    @FXML
    private ImageView catHeaderImage;
    private MedicineCategory medicineCategory;
    private boolean isUpdate = false;


    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public void setMedicineCategory(MedicineCategory medicineCategory) {
        this.medicineCategory = medicineCategory;
    }

    public void updateCategory(ActionEvent event) {
        String catgoryName = categoryNameFld.getText();
        String jsonString = "{\"name\":\"" + catgoryName + "\"}";
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/update/category/" + medicineCategory.getId();
        try {
            Response response = HTTPMethods.put(jsonString, url);
            int respondCode = response.getRespondCode();
            JSONObject jsonResponse = new JSONObject(response.getResponse());
            if (respondCode >=200 && respondCode<=299) {
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

    public void createCategory(ActionEvent event) {
        String categoryName = categoryNameFld.getText();
        String jsonString = "{\"name\":\"" + categoryName + "\"}";
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/create/category";
        try {
            Response response = HTTPMethods.post(jsonString, url);
            int respondCode = response.getRespondCode();
            JSONObject jsonResponse = new JSONObject(response.getResponse());
            if (respondCode >=200 && respondCode<=299) {
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
            categoryNameFld.setText(medicineCategory.getName());
            submitCatBtn.setOnAction(this::updateCategory);
            catHeaderLbl.setText("Ενημέρωση κατηγορίας");
            submitCatBtn.setText("Ενημέρωση κατηγορίας");
            catHeaderImage.setImage(new Image("assets/icons/udpate.png"));


        } else {
            submitCatBtn.setOnAction(this::createCategory);
            catHeaderLbl.setText("Νέα κατηγορίας");
            submitCatBtn.setText("Αποθήκευση κατηγορίας");
            catHeaderImage.setImage(new Image("assets/icons/new.png"));
        }
    }
}

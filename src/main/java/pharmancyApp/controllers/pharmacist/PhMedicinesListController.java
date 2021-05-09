package pharmancyApp.controllers.pharmacist;

import REST.HTTPMethods;
import REST.Response;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Medicine;
import models.MedicineCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Colors;
import pharmancyApp.Settings;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class PhMedicinesListController implements Initializable {
    @FXML
    private TableView<Medicine> medicinesTable;
    @FXML
    private TableColumn<Medicine, String> medicineNameCol;
    @FXML
    private TableColumn<Medicine, String> medCategoryCol;
    @FXML
    private TableColumn<Medicine, Integer> medQuantityCol;
    @FXML
    private TableColumn<Medicine, Float> medPriceCol;
    @FXML
    private TableColumn<Medicine, String> medicineOptionsCol;

    private Medicine medicine;
    private ObservableList<Medicine> medicines = FXCollections.observableArrayList();
    private void getMedicinesTable(){
        medicineNameCol.setCellValueFactory(item -> item.getValue().nameProperty());
        medCategoryCol.setCellValueFactory(item -> item.getValue().getMedicineCategory().nameProperty());
        medQuantityCol.setCellValueFactory(item -> item.getValue().quantityProperty().asObject());
        medPriceCol.setCellValueFactory(item -> item.getValue().priceProperty().asObject());
        Callback<TableColumn<Medicine, String>, TableCell<Medicine, String>> cellFoctory = (TableColumn<Medicine, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);

                } else {

                    JFXButton updateMedicinePrice = new JFXButton("Ενημέρωση τιμής");
                    updateMedicinePrice.setStyle("-fx-background-color:" + Colors.YELLOW);
                    updateMedicinePrice.setTextFill(Paint.valueOf(Colors.WHITE));


                    updateMedicinePrice.setOnMouseClicked((MouseEvent event) -> {
                        medicine = getTableView().getItems().get(getIndex());
                        try {
                            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhMedicinePriceForm.fxml")));
                            Parent root = loader.load();
                            PhMedicinePriceFormController phMedicinePriceFormController =loader.getController();
                            phMedicinePriceFormController.setUpdate(true);
                            phMedicinePriceFormController.setMedicine(medicine);
                            phMedicinePriceFormController.init();
                            Stage stage = new Stage();
                            stage.setTitle("Ενημέρωση τιμής φαρμάκου");
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
                            stage.setScene(scene);
                            stage.setResizable(false);
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }



                    });



                    HBox actionsBtns = new HBox(updateMedicinePrice);
                    actionsBtns.setAlignment(Pos.CENTER);
                    actionsBtns.setSpacing(5);

                    setGraphic(actionsBtns);

                }
                setText(null);
            }

        };
        medicineOptionsCol.setCellFactory(cellFoctory);
        medicinesTable.setItems(medicines);
    }


    private void fetchMedicines(){
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/get/medicines";
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

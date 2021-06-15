package pharmancyApp.controllers.pharmacist;

import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import pharmancyApp.models.Medicine;
import pharmancyApp.models.MedicineCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.utils.Colors;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class PhMedicinesForSaleTableController implements Initializable {
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
    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();

    private void getMedicinesTable() {
        medicineNameCol.setCellValueFactory(item -> item.getValue().nameProperty());
        medCategoryCol.setCellValueFactory(item -> item.getValue().getMedicineCategory().nameProperty());
        medQuantityCol.setCellValueFactory(item -> item.getValue().quantityProperty().asObject());
        medPriceCol.setCellValueFactory(item -> item.getValue().priceProperty().asObject());
        Callback<TableColumn<Medicine, String>, TableCell<Medicine, String>> cellFactory = (TableColumn<Medicine, String> param) -> new TableCell<>() {
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
                            PhMedicinePriceFormController phMedicinePriceFormController = loader.getController();
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
        medicineOptionsCol.setCellFactory(cellFactory);
        medicinesTable.setItems(medicines);
    }


    private void fetchMedicines() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/medicines";
        try {
            Response response = HTTPMethods.get(url);
            if (response != null) {
                int respondCode = response.getRespondCode();
                if (respondCode >= 200 && respondCode <= 299) {
                    JSONArray jsonArray = new JSONArray(response.getResponse());
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
                        medicines.add(medicine);


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

        medicinesTable.setPlaceholder(new Label("Δεν υπάρχουν φάρμακα για πώληση"));
        fetchMedicines();
    }
}

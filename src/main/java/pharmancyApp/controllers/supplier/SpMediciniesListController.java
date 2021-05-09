package pharmancyApp.controllers.supplier;

import REST.HTTPMethods;
import REST.Response;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class SpMediciniesListController {
    @FXML
    private TableView<Medicine> medicinesTable;

    @FXML
    private TableColumn<Medicine, String> medicineNameCol;

    @FXML
    private TableColumn<Medicine, String> medCategoryCol;

    @FXML
    private TableColumn<Medicine, String> medicineOptionsCol;

    private Medicine medicine;
    private ObservableList<Medicine> medicines = FXCollections.observableArrayList();


    @FXML
    private void getMedicinesTable() {
        medicineNameCol.setCellValueFactory(item -> item.getValue().nameProperty());
        medCategoryCol.setCellValueFactory(item -> item.getValue().getMedicineCategory().nameProperty());
        Callback<TableColumn<Medicine, String>, TableCell<Medicine, String>> cellFoctory = (TableColumn<Medicine, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);

                } else {

                    JFXButton viewMedicine = new JFXButton("Προβολη");
                    viewMedicine.setStyle("-fx-background-color:" + Colors.VIEW);
                    viewMedicine.setTextFill(Paint.valueOf(Colors.WHITE));

                    JFXButton editMedicine = new JFXButton("Επεξεργασια");
                    editMedicine.setStyle("-fx-background-color:" + Colors.YELLOW);
                    editMedicine.setTextFill(Paint.valueOf(Colors.WHITE));


                    JFXButton deleteMedicine = new JFXButton("Διαγραφη");
                    deleteMedicine.setStyle("-fx-background-color:" + Colors.DELETE);
                    deleteMedicine.setTextFill(Paint.valueOf(Colors.WHITE));

                    viewMedicine.setOnMouseClicked((MouseEvent event) -> {

                        medicine = getTableView().getItems().get(getIndex());
                        try {
                            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpMedicineDetailsScene.fxml")));
                            Parent root = loader.load();
                            SpMedicineDetailsController spMedicineDetailsController = loader.getController();
                            spMedicineDetailsController.setMedicine(medicine);
                            spMedicineDetailsController.init();
                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stage.setTitle("Hello World");
                            stage.setScene(new Scene(root));
                            stage.setResizable(false);
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }


                    });


                    editMedicine.setOnMouseClicked((MouseEvent event) -> {
                        medicine = getTableView().getItems().get(getIndex());
                        try {
                            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpMedicineForm.fxml")));
                            Parent root = loader.load();
                            SpMedicineFormController spMedicineFormController = loader.getController();
                            spMedicineFormController.fetchCategories();
                            spMedicineFormController.setUpdate(true);
                            spMedicineFormController.setMedicine(medicine);
                            spMedicineFormController.init();
                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stage.setTitle("Hello World");
                            stage.setScene(new Scene(root));
                            stage.setResizable(false);
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    });

                    deleteMedicine.setOnMouseClicked((MouseEvent event) -> {
                        medicine = getTableView().getItems().get(getIndex());
                        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/delete/medicine/" + medicine.getId();
                        Alert alert;
                        ButtonType delete = new ButtonType("Διαγραφή", ButtonBar.ButtonData.OK_DONE);
                        ButtonType cancel = new ButtonType("Ακύρωση", ButtonBar.ButtonData.CANCEL_CLOSE);
                        alert = new Alert(Alert.AlertType.WARNING,
                                "Είστε σίγουροι ότι θέλετε να διαγαψετε το φάρμακο με όνομα " + medicine.getName() + ".\n(Η ενέργεια αυτή είναι μη αναστρέψιμη)",

                                delete,
                                cancel);

                        alert.setTitle("Διαγραφή πελάτη");
                        alert.setHeaderText("Προειδοποίηση!");
                        alert.getDialogPane().setMinHeight(200);
                        alert.getDialogPane().setMinWidth(500);
                        alert.setResizable(false);
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.orElse(cancel) == delete) {
                            try {
                                Response response = HTTPMethods.delete(url);
                                int respondCode = response.getRespondCode();
                                if (respondCode > 200 && respondCode < 299) {
                                    medicines.removeIf(m -> m.getId() == medicine.getId());
                                } else {
                                    StringBuilder errorMessage = new StringBuilder();
                                    JSONObject responseObj = new JSONObject(response);
                                    Map<String, Object> i = responseObj.toMap();
                                    for (Map.Entry<String, Object> entry : i.entrySet()) {
                                        errorMessage.append(entry.getValue().toString()).append("\n");
                                        System.out.println(entry.getKey() + "/" + entry.getValue());

                                    }
                                    alert = new Alert(Alert.AlertType.ERROR);
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
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    });

                    HBox actionsBtns = new HBox(viewMedicine, editMedicine, deleteMedicine);
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

    public void fetchMedicines() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/get/medicines";
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
                    int categoryId = categoryObj.getInt("id");
                    String categoryName = categoryObj.getString("name");
                    medicineCategory = new MedicineCategory(categoryId, categoryName);
                    medicine = new Medicine(id, name, quantity, price, medicineCategory);
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

    public void init() {

    }

}

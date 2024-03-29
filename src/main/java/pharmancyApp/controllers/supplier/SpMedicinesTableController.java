package pharmancyApp.controllers.supplier;

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
import javafx.scene.Node;
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
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;


public class SpMedicinesTableController implements Initializable {
    @FXML
    private TableView<Medicine> medicinesTable;

    @FXML
    private TableColumn<Medicine, String> medicineNameCol;

    @FXML
    private TableColumn<Medicine, String> medCategoryCol;

    @FXML
    private TableColumn<Medicine, String> medicineOptionsCol;

    private Medicine medicine;
    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();


    @FXML
    private void getMedicinesTable() {
        medicineNameCol.setCellValueFactory(item -> item.getValue().nameProperty());
        medCategoryCol.setCellValueFactory(item -> item.getValue().getMedicineCategory().nameProperty());
        Callback<TableColumn<Medicine, String>, TableCell<Medicine, String>> cellFactory = (TableColumn<Medicine, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);

                } else {

                    JFXButton viewMedicine = new JFXButton("Προβολη");
                    viewMedicine.setStyle("-fx-background-color:" + Colors.BLUE);
                    viewMedicine.setTextFill(Paint.valueOf(Colors.WHITE));

                    JFXButton editMedicine = new JFXButton("Επεξεργασια");
                    editMedicine.setStyle("-fx-background-color:" + Colors.YELLOW);
                    editMedicine.setTextFill(Paint.valueOf(Colors.WHITE));


                    JFXButton deleteMedicine = new JFXButton("Διαγραφη");
                    deleteMedicine.setStyle("-fx-background-color:" + Colors.RED);
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
                            stage.setTitle("Προβολή φαρμάκου: "+medicine.getName());
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
                            stage.setScene(scene);
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
                            stage.setTitle("Επεξεργασία φαρμάκου");
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
                            stage.setScene(scene);
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
                                if (response != null) {
                                    int respondCode = response.getRespondCode();
                                    if (respondCode > 200 && respondCode < 299) {
                                        medicines.removeIf(m -> m.getId() == medicine.getId());
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
        medicineOptionsCol.setCellFactory(cellFactory);
        medicinesTable.setItems(medicines);
    }

    private void fetchMedicines() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/get/medicines";
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

        medicinesTable.setPlaceholder(new Label("Δεν υπάρχουν φάρμακα"));
        fetchMedicines();
    }
}

package pharmancyApp.controllers.supplier;

import REST.Authentication;
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
import models.MedicineCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Colors;
import pharmancyApp.Settings;
import pharmancyApp.Utils.AlertDialogs;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;


public class SpCategoriesListController implements Initializable {
    @FXML
    private TableView<MedicineCategory> categoriesTable;

    @FXML
    private TableColumn<MedicineCategory, String> categoryNameCol;

    @FXML
    private TableColumn<MedicineCategory, String> categoryOptionsCol;

    private MedicineCategory medicineCategory;
    private final ObservableList<MedicineCategory> categories = FXCollections.observableArrayList();


    public void fetchCategories() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/get/categories";
        try {
            Response response = HTTPMethods.get(url);

            if (response != null) {
                int respondCode = response.getRespondCode();

                if (respondCode >= 200 && respondCode <= 299) {
                    JSONArray jsonArray = new JSONArray(response.getResponse());
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        this.medicineCategory = new MedicineCategory(id, name);
                        this.categories.addAll(this.medicineCategory);

                    }
                    getCategoriesTable();
                } else {

                    JSONObject responseObj = new JSONObject(response.getResponse());
                    String headerText = "Αδυναμια συνδεσης";
                    AlertDialogs.error(headerText, responseObj, null);
                    if (respondCode == 401) {
                        Authentication.setLogin(false);
                    }
                }
            } else {
                String headerText = "Αδυναμία συνδεσης";
                String contentText = "Η επικοινωνία με τον εξυπηρετητή απέτυχε";
                AlertDialogs.error(headerText, null, contentText);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void getCategoriesTable() {
        categoryNameCol.setCellValueFactory(item -> item.getValue().nameProperty());
        Callback<TableColumn<MedicineCategory, String>, TableCell<MedicineCategory, String>> cellFoctory = (TableColumn<MedicineCategory, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);

                } else {

                    JFXButton editCategory = new JFXButton("Επεξεργασια");
                    editCategory.setStyle("-fx-background-color:" + Colors.YELLOW);
                    editCategory.setTextFill(Paint.valueOf(Colors.WHITE));

                    JFXButton deleteCategory = new JFXButton("Διαγραφη");
                    deleteCategory.setStyle("-fx-background-color:" + Colors.RED);
                    deleteCategory.setTextFill(Paint.valueOf(Colors.WHITE));


                    editCategory.setOnMouseClicked((MouseEvent event) -> {
                        medicineCategory = getTableView().getItems().get(getIndex());
                        try {
                            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpCategoryFormScene.fxml")));
                            Parent root = loader.load();
                            SpCategoryFormController spCategoryFormController = loader.getController();
                            spCategoryFormController.setUpdate(true);
                            spCategoryFormController.setMedicineCategory(medicineCategory);
                            spCategoryFormController.init();
                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            Scene scene = new Scene(root);
                            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styling/FlatBee.css")).toExternalForm());
                            stage.setScene(scene);
                            stage.setTitle("Επεξεργασία κατηγορίας.");
                            stage.setResizable(false);
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    });

                    deleteCategory.setOnMouseClicked((MouseEvent event) -> {
                        medicineCategory = getTableView().getItems().get(getIndex());
                        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/delete/category/" + medicineCategory.getId();
                        try {
                            Response response = HTTPMethods.delete(url);
                            if (response != null) {
                                int respondCode = response.getRespondCode();
                                if (respondCode > 200 && respondCode < 299) {
                                    ButtonType delete = new ButtonType("Διαγραφή", ButtonBar.ButtonData.OK_DONE);
                                    ButtonType cancel = new ButtonType("Ακύρωση", ButtonBar.ButtonData.CANCEL_CLOSE);
                                    Alert alert = new Alert(Alert.AlertType.WARNING,
                                            "Είστε σίγουροι ότι θέλετε να διαγαψετε την κατηγορία με όνομα " + medicineCategory.getName() + ".\n(Η ενέργεια αυτή είναι μη αναστρέψιμη)",

                                            delete,
                                            cancel);

                                    alert.setTitle("Διαγραφή πελάτη");
                                    alert.setHeaderText("Προειδοποίηση!");
                                    alert.getDialogPane().setMinHeight(200);
                                    alert.getDialogPane().setMinWidth(500);
                                    alert.setResizable(false);
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.orElse(cancel) == delete) {
                                        categories.removeIf(c -> c.getId() == medicineCategory.getId());
                                    }

                                } else {

                                    JSONObject responseObj = new JSONObject(response.getResponse());
                                    String headerText = "Αδυναμια συνδεσης";
                                    AlertDialogs.error(headerText, responseObj, null);
                                    if (respondCode == 401) {
                                        Authentication.setLogin(false);
                                    }
                                }
                            } else {
                                String headerText = "Αδυναμία συνδεσης";
                                String contentText = "Η επικοινωνία με τον εξυπηρετητή απέτυχε";
                                AlertDialogs.error(headerText, null, contentText);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    HBox actionsBtns = new HBox(editCategory, deleteCategory);
                    actionsBtns.setAlignment(Pos.CENTER);
                    actionsBtns.setSpacing(5);

                    setGraphic(actionsBtns);

                }
                setText(null);
            }

        };
        categoryOptionsCol.setCellFactory(cellFoctory);
        categoriesTable.setItems(this.categories);
    }

    @FXML
    private void refreshTable() {
        categories.clear();
        fetchCategories();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fetchCategories();
    }
}

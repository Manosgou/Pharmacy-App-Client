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
import models.MedicineCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Colors;
import pharmancyApp.Settings;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


public class SpCategoriesListController {
    @FXML
    private TableView<MedicineCategory> categoriesTable;

    @FXML
    private TableColumn<MedicineCategory, String> categoryNameCol;

    @FXML
    private TableColumn<MedicineCategory, String> categoryOptionsCol;

    private MedicineCategory medicineCategory;
    private ObservableList<MedicineCategory> categories = FXCollections.observableArrayList();


    public void fetchCategories(){
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/get/categories";
        try{
            Response response = HTTPMethods.get(url);
            int respondCode = response.getRespondCode();
            JSONArray jsonArray = new JSONArray(response.getResponse());
            if(respondCode >=200 && respondCode<=299){
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    this.medicineCategory = new MedicineCategory(id,name);
                    this.categories.addAll(this.medicineCategory);

                }
                getCategoriesTable();
            }else{
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

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    private void getCategoriesTable(){
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
                    deleteCategory.setStyle("-fx-background-color:" + Colors.DELETE);
                    deleteCategory.setTextFill(Paint.valueOf(Colors.WHITE));


                    editCategory.setOnMouseClicked((MouseEvent event) -> {
                        medicineCategory = getTableView().getItems().get(getIndex());
                        try {
                            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpCategoryFormScene.fxml")));
                            Parent root = loader.load();
                            SpCategoryFormController spCategoryFormController =loader.getController();
                            spCategoryFormController.setUpdate(true);
                            spCategoryFormController.setMedicineCategory(medicineCategory);
                            spCategoryFormController.init();
                            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            stage.setTitle("Hello World");
                            stage.setScene(new Scene(root));
                            stage.setResizable(false);
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    });

                    deleteCategory.setOnMouseClicked((MouseEvent event) -> {
                        medicineCategory = getTableView().getItems().get(getIndex());
                        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/delete/category/"+medicineCategory.getId();
                        try {
                            Response response= HTTPMethods.delete(url);
                            int respondCode = response.getRespondCode();
                            if(respondCode >200 && respondCode<299){
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
                                    categories.removeIf(c->c.getId()==medicineCategory.getId());
                                }

                            }else {
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
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

                    HBox actionsBtns = new HBox(editCategory,deleteCategory);
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

}

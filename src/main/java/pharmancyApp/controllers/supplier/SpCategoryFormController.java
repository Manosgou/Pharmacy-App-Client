package pharmancyApp.controllers.supplier;

import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import pharmancyApp.models.MedicineCategory;
import org.json.JSONObject;
import javafx.event.ActionEvent;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;
import pharmancyApp.utils.TextFieldFilters;
import java.net.URL;
import java.util.ResourceBundle;


public class SpCategoryFormController implements Initializable {
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

    private boolean categoryFormValidation(String categoryName) {
        String validationError = null;
        boolean inputIsValid = true;
        if (categoryName.isEmpty()) {
            validationError = "Παρακαλώ εισάγετε όνομα κατηγορίας";
            inputIsValid = false;

        }
        if (!inputIsValid) {
            String headerText = "Ελλιπή στοιχεία";
            AlertDialogs.error(headerText, null, validationError);
        }

        return inputIsValid;
    }

    public void updateCategory(ActionEvent event) {
        String catgoryName = categoryNameFld.getText();
        if (categoryFormValidation(catgoryName)) {
            String jsonString = "{\"name\":\"" + catgoryName + "\"}";
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/update/category/" + medicineCategory.getId();
            try {
                Response response = HTTPMethods.put(jsonString, url);
                if (response != null) {
                    int respondCode = response.getRespondCode();
                    if (respondCode >= 200 && respondCode <= 299) {
                        final Node source = (Node) event.getSource();
                        final Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
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
    }

    public void createCategory(ActionEvent event) {
        String categoryName = categoryNameFld.getText();
        if (categoryFormValidation(categoryName)) {
            String jsonString = "{\"name\":\"" + categoryName + "\"}";
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/create/category";
            try {
                Response response = HTTPMethods.post(jsonString, url);
                if (response != null) {
                    int respondCode = response.getRespondCode();
                    JSONObject jsonResponse = new JSONObject(response.getResponse());
                    if (respondCode >= 200 && respondCode <= 299) {
                        final Node source = (Node) event.getSource();
                        final Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
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
    }


    public void init() {
        if (isUpdate) {
            categoryNameFld.setText(medicineCategory.getName());
            submitCatBtn.setOnAction(this::updateCategory);
            catHeaderLbl.setText("Ενημέρωση κατηγορίας");
            submitCatBtn.setText("Ενημέρωση κατηγορίας");
            catHeaderImage.setImage(new Image("assets/icons/update.png"));


        } else {
            submitCatBtn.setOnAction(this::createCategory);
            catHeaderLbl.setText("Νέα κατηγορίας");
            submitCatBtn.setText("Αποθήκευση κατηγορίας");
            catHeaderImage.setImage(new Image("assets/icons/new.png"));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categoryNameFld.setTextFormatter(new TextFormatter<String>(TextFieldFilters.stringFilter));
    }
}

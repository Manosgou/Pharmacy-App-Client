package pharmancyApp.controllers.supplier;

import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.converter.FloatStringConverter;
import javafx.util.converter.IntegerStringConverter;
import pharmancyApp.models.Medicine;
import pharmancyApp.models.MedicineCategory;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;
import pharmancyApp.utils.TextFieldFilters;
import java.net.URL;
import java.util.ResourceBundle;


public class SpMedicineFormController implements Initializable {

    @FXML
    private Label medHeaderLbl;
    @FXML
    private TextField medicineNamFld;
    @FXML
    private TextField medicineQuaFld;
    @FXML
    private TextField medicinePriFld;
    @FXML
    private Button submitMedBtn;
    @FXML
    private ImageView medHeaderImage;
    @FXML
    private ComboBox<MedicineCategory> medCatComboBox;


    private Medicine medicine;
    private final ObservableList<MedicineCategory> categories = FXCollections.observableArrayList();
    private boolean isUpdate = false;

    public void setUpdate(boolean update) {
        this.isUpdate = update;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    private boolean medicineFormValidation(String medicineName, MedicineCategory category) {
        String validationError = null;
        boolean inputIsValid = true;
        if (category == null) {
            validationError = "Παρακαλώ επιλέξτε κατηγορία φαρμάκου";
            inputIsValid = false;
        }

        if (medicineName.isEmpty()) {
            validationError = "Παρακαλώ συμπληρώστε το όνομα φαρμάκου";
            inputIsValid = false;
        }

        if (!inputIsValid) {
            String headerText = "Ελλιπή στοιχεία";
            AlertDialogs.alertPlainText(Alert.AlertType.ERROR,"Σφάλμα",headerText,validationError);
        }

        return inputIsValid;
    }

    @FXML
    private void saveMedicine(ActionEvent ev) {
        String medicineName = medicineNamFld.getText();
        int medicineQuantity = Integer.parseInt(medicineQuaFld.getText());
        float medicinePrice = Float.parseFloat(medicinePriFld.getText());
        MedicineCategory category = medCatComboBox.getValue();
        if (medicineFormValidation(medicineName, category)) {
            String jsonString = "{\"name\":\"" + medicineName + "\",\"quantity\":\"" + medicineQuantity + "\",\"price\":\"" + medicinePrice + "\",\"category\":{\"id\":\"" + category.getId() + "\",\"name\":\"" + category.getName() + "\"}}";
            System.out.println(jsonString);
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/create/medicine";
            try {
                Response response = HTTPMethods.post(jsonString, url);
                if (response != null) {
                    int respondCode = response.getRespondCode();
                    if (respondCode >= 200 && respondCode <= 299) {
                        final Node source = (Node) ev.getSource();
                        final Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
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
    }

    public void fetchCategories() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/get/categories";
        try {
            Response response = HTTPMethods.get(url);
            if (response != null) {
                int respondCode = response.getRespondCode();

                if (respondCode >= 200 && respondCode <= 299) {
                    MedicineCategory medicineCategory;
                    JSONArray jsonArray = new JSONArray(response.getResponse());
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        medicineCategory = new MedicineCategory(id, name);
                        categories.add(medicineCategory);


                    }
                    medCatComboBox.setItems(categories);
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
    private void updateMedicine(ActionEvent event) {
        String medicineName = medicineNamFld.getText();
        int medicineQuantity = Integer.parseInt(medicineQuaFld.getText());
        float medicinePrice = Float.parseFloat(medicinePriFld.getText());
        MedicineCategory category = medCatComboBox.getValue();
        if (medicineFormValidation(medicineName, category)) {
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/update/medicine/" + medicine.getId();
            String jsonString = "{\"name\":\"" + medicineName + "\",\"quantity\":\"" + medicineQuantity + "\",\"price\":\"" + medicinePrice + "\",\"category\":{\"id\":\"" + category.getId() + "\",\"name\":\"" + category.getName() + "\"}}";
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
    }

    public void init() {
        if (isUpdate) {
            submitMedBtn.setOnAction(this::updateMedicine);
            submitMedBtn.setText("Ενημέρωση");
            medHeaderLbl.setText("Ενημέρωση φαρμάκου");
            medHeaderImage.setImage(new Image("assets/icons/update.png"));
            medicineNamFld.setText(medicine.getName());
            medicineQuaFld.setText(Integer.toString(medicine.getQuantity()));
            medicinePriFld.setText(Float.toString(medicine.getPrice()));
            for (MedicineCategory c : categories) {
                if (c.getId() == medicine.getMedicineCategory().getId()) {
                    medCatComboBox.setValue(c);
                }
            }

        } else {
            submitMedBtn.setOnAction(this::saveMedicine);
            submitMedBtn.setText("Αποθήκευση");
            medHeaderLbl.setText("Εισαγωγή νέου φαρμάκου");
            medHeaderImage.setImage(new Image("assets/icons/pharmacy_b&w.png"));
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        medicineNamFld.setTextFormatter(new TextFormatter<String>(TextFieldFilters.stringFilter));
        medicineQuaFld.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(), 1, TextFieldFilters.integerFilter));
        medicinePriFld.setTextFormatter(new TextFormatter<>(new FloatStringConverter(), 1.00f, TextFieldFilters.floatFilter));
    }
}

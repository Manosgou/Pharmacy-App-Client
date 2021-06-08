package pharmancyApp.controllers.pharmacist;

import REST.Authentication;
import REST.HTTPMethods;
import REST.Response;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.converter.FloatStringConverter;
import models.Medicine;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.Utils.AlertDialogs;
import pharmancyApp.Utils.TextFieldFilters;
import java.net.URL;
import java.util.ResourceBundle;


public class PhMedicinePriceFormController implements Initializable {
    @FXML
    private Label headerTitle;
    @FXML
    private TextField medicinePriceFld;
    @FXML
    private ImageView headerImage;
    @FXML
    private JFXButton submit;

    private int orderId;
    private Medicine medicine;
    private boolean isUpdate = false;

    public void setOrderId(int id) {
        this.orderId = id;
    }

    public void setUpdate(Boolean update) {
        this.isUpdate = update;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
    }

    @FXML
    private void savePrice(ActionEvent event) {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/make/medicine-available/" + orderId;
        String jsonString = "{\"price\":\"" + medicinePriceFld.getText() + "\"}";
        try {
            Response response = HTTPMethods.post(jsonString, url);
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

    private void updatePrice(ActionEvent event) {
        String price = medicinePriceFld.getText().trim();
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/update/medicine/" + medicine.getId() + "/price";
        String jsonString = "{\"price\":\"" + price + "\"}";
        try {
            Response response = HTTPMethods.put(jsonString, url);
            if (response != null) {
                int respondCode = response.getRespondCode();
                if (respondCode >= 200 && respondCode <= 299) {
                    medicine.priceProperty().setValue(Float.valueOf(price));
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

    public void init() {
        if (isUpdate) {
            headerTitle.setText("Ενημέρωση τιμής");
            headerImage.setImage(new Image("assets/icons/update.png"));
            submit.setText("Ενημέρωση");
            medicinePriceFld.setText(Float.toString(medicine.getPrice()));
            submit.setOnAction(this::updatePrice);
        } else {
            headerTitle.setText("Τιμή φαρμάκου");
            headerImage.setImage(new Image("assets/icons/euro.png"));
            submit.setText("Αποθήκευση");
            submit.setOnAction(this::savePrice);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        medicinePriceFld.setTextFormatter(new TextFormatter<Float>(new FloatStringConverter(), 1.00f, TextFieldFilters.floatFilter));
    }
}

package pharmancyApp.controllers;

import REST.HTTPMethods;
import REST.Response;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import models.User;
import models.Location;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.Utils.AlertDialogs;
import pharmancyApp.Utils.TextFieldFilters;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;


public class UpdateLocationDetailsController implements Initializable {

    @FXML
    private JFXTextField streetFld;

    @FXML
    private JFXTextField streetNumFld;

    @FXML
    private JFXTextField cityFld;

    @FXML
    private JFXTextField postalCodeFld;

    private User user;
    private Location location;


    public void setEmployee(User user) {
        this.user = user;
    }

    public void setPharmancy(Location location) {
        this.location = location;
    }

    public void setFields() {
        streetFld.setText(location.getStreet());
        streetNumFld.setText(Integer.toString(location.getStreetNum()));
        cityFld.setText(location.getCity());
        postalCodeFld.setText(Integer.toString(location.getPostalCode()));

    }

    private void updatePharmancy(String street, int streeNum, String city, int postalCode) {
        location.streetProperty().set(street);
        location.streetNumProperty().set(streeNum);
        location.cityProperty().set(city);
        location.postalCodeProperty().set(postalCode);
    }

    private boolean textFieldsValidation(String street, String city) {
        String validationError = null;
        boolean inputIsValid = true;

        if (street.isEmpty()) {
            validationError = "Παρακαλώ συμπληρώστε το πεδίο Διεύνθυνση";
            inputIsValid = false;
        }
        if (city.isEmpty()) {
            validationError = "Παρακαλώ συμπληρώστε το πεδίο πόλη";
            inputIsValid = false;
        }
        if (!inputIsValid) {
            String headerText = "Ελλιπή στοιχεία";
            AlertDialogs.error(headerText, null, validationError);
        }


        return inputIsValid;
    }


    @FXML
    private void updatePharmancyInformation(ActionEvent event) {
        String street = streetFld.getText().trim();
        int streetNum = streetNumFld.getText().trim().isEmpty() ? 0 : Integer.parseInt(streetNumFld.getText().trim());
        String city = cityFld.getText().trim();
        int postalCode = postalCodeFld.getText().trim().isEmpty() ? 0 : Integer.parseInt(postalCodeFld.getText().trim());

        if (textFieldsValidation(street, city)) {
            String jsonString = "{\"street\":\"" + street + "\",\"street_num\":\"" + streetNum + "\",\"city\":\"" + city + "\",\"postal_code\":\"" + postalCode + "\"}";
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/location/update/" + user.getId();

            try {
                Response response = HTTPMethods.put(jsonString, url);
                if (response != null) {
                    int respondCode = response.getRespondCode();
                    JSONObject jsonResponse = new JSONObject(response.getResponse());
                    if (respondCode >= 200 && respondCode <= 299) {
                        updatePharmancy(street, streetNum, city, postalCode);
                        final Node source = (Node) event.getSource();
                        final Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                    } else {
                        String headerText = "Ελλιπή στοιχεία";
                        AlertDialogs.error(headerText, jsonResponse, null);
                    }
                }else{
                    String headerText = "Αδυναμία συνδεσης";
                    String contentText="Η επικοινωνία με τον εξυπηρετητή απέτυχε";
                    AlertDialogs.error(headerText, null, contentText);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        streetFld.setTextFormatter(new TextFormatter<String>(TextFieldFilters.stringFilter));
        cityFld.setTextFormatter(new TextFormatter<String>(TextFieldFilters.stringFilter));
        streetNumFld.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 1, TextFieldFilters.integerFilter));
        postalCodeFld.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 1, TextFieldFilters.integerFilter));
    }
}


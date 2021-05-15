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
import models.Employee;
import models.Location;
import org.json.JSONObject;
import pharmancyApp.Settings;
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

    private Employee employee;
    private Location location;


    public void setEmployee(Employee employee) {
        this.employee = employee;
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

    @FXML
    private void updatePharmancyInformation(ActionEvent event) {
        String street = streetFld.getText().trim();
        int streetNum = streetNumFld.getText().trim().isEmpty() ? 0 : Integer.parseInt(streetNumFld.getText().trim());
        String city = cityFld.getText().trim();
        int postalCode = postalCodeFld.getText().trim().isEmpty() ? 0 : Integer.parseInt(postalCodeFld.getText().trim());
        String jsonString = "{\"street\":\"" + street + "\",\"street_num\":\"" + streetNum + "\",\"city\":\"" + city + "\",\"postal_code\":\"" + postalCode + "\"}";
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/location/update/" + employee.getId();

        try {
            Response response = HTTPMethods.put(jsonString, url);
            int respondCode = response.getRespondCode();
            JSONObject jsonResponse = new JSONObject(response.getResponse());
            if (respondCode >= 200 && respondCode <= 299) {
                updatePharmancy(street, streetNum, city, postalCode);
                final Node source = (Node) event.getSource();
                final Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            } else {
                StringBuilder errorMessage = new StringBuilder();
                Map<String, Object> i = jsonResponse.toMap();
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
                alert.setHeaderText("Είσαι καθυστερημένος");
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
        streetNumFld.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 1, TextFieldFilters.integerFilter));
        postalCodeFld.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 1, TextFieldFilters.integerFilter));
    }
}


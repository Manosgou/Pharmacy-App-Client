package pharmancyApp.controllers;

import REST.HTTPMethods;
import REST.Response;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.User;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.Utils.AlertDialogs;
import pharmancyApp.Utils.TextFieldFilters;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateUserDetailsController implements Initializable {
    @FXML
    private TextField usernameFld;
    @FXML
    private TextField emailFld;
    @FXML
    private TextField lastnameFld;
    @FXML
    private TextField firstnameFld;

    private User user;


    public void setEmployee(User user) {
        this.user = user;
    }

    public void setFields() {
        usernameFld.setText(user.getUsername());
        emailFld.setText(user.getEmail());
        lastnameFld.setText(user.getLastname());
        firstnameFld.setText(user.getFirstname());
    }

    private void updateEmployee(String username, String email, String lastname, String firstname) {
        user.usernameProperty().set(username);
        user.emailProperty().set(email);
        user.lastnameProperty().set(lastname);
        user.firstnameProperty().set(firstname);
    }


    private boolean textFieldsValidation(String username, String email, String lastname, String firstname) {
        String validationError = null;
        boolean inputIsValid = true;


        if (firstname.isEmpty()) {
            validationError = "Παρακαλώ συμπληρώστε το πεδίο όνομα";
            inputIsValid = false;
        }
        if (lastname.isEmpty()) {
            validationError = "Παρακαλώ συμπληρώστε το πεδίο επίθετο";
            inputIsValid = false;
        }
        if (email.isEmpty()) {
            validationError = "Παρακαλώ συμπληρώστε το πεδίο email";
            inputIsValid = false;
        }
        if (username.isEmpty()) {
            validationError = "Παρακαλώ συμπληρώστε το πεδίο username";
            inputIsValid = false;
        }


        if (!inputIsValid) {
            String headerText = "Ελλιπή στοιχεία";
            AlertDialogs.error(headerText, null, validationError);
        }


        return inputIsValid;
    }

    @FXML
    private void updateUserInformation(ActionEvent event) {
        String username = usernameFld.getText().trim();
        String email = emailFld.getText().trim();
        String lastname = lastnameFld.getText().trim();
        String firstname = firstnameFld.getText().trim();

        if (textFieldsValidation(username, email, lastname, firstname)) {
            String jsonString = "{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"last_name\":\"" + lastname + "\",\"first_name\":\"" + firstname + "\"}";
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/user/update/" + user.getId();
            try {
                Response response = HTTPMethods.put(jsonString, url);
                if (response != null) {


                    int respondCode = response.getRespondCode();
                    JSONObject jsonResponse = new JSONObject(response.getResponse());
                    if (respondCode >= 200 && respondCode <= 299) {
                        updateEmployee(username, email, lastname, firstname);
                        final Node source = (Node) event.getSource();
                        final Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                    } else {
                        String headerText = "Ελλιπή στοιχεία";
                        AlertDialogs.error(headerText, jsonResponse, null);
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lastnameFld.setTextFormatter(new TextFormatter<String>(TextFieldFilters.stringFilter));
        firstnameFld.setTextFormatter(new TextFormatter<String>(TextFieldFilters.stringFilter));
    }
}

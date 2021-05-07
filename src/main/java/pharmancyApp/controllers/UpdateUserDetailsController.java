package pharmancyApp.controllers;

import REST.HTTPMethods;
import REST.Response;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Employee;
import org.json.JSONObject;
import pharmancyApp.Settings;

import java.util.Map;

public class UpdateUserDetailsController {
    @FXML
    private TextField usernameFld;
    @FXML
    private TextField emailFld;
    @FXML
    private TextField lastnameFld;
    @FXML
    private TextField firstnameFld;

    private Employee employee;


    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public void setFields() {
        usernameFld.setText(employee.getUsername());
        emailFld.setText(employee.getEmail());
        lastnameFld.setText(employee.getLastname());
        firstnameFld.setText(employee.getFirstname());
    }

    private void updateEmployee(String username,String email,String lastname,String firstname){
        employee.usernameProperty().set(username);
        employee.emailProperty().set(email);
        employee.lastnameProperty().set(lastname);
        employee.firstnameProperty().set(firstname);
    }

    @FXML
    private void updateUserInformation(ActionEvent event) {
        String username =usernameFld.getText().trim();
        String email = emailFld.getText().trim();
        String lastname = lastnameFld.getText().trim();
        String firstname = firstnameFld.getText().trim();


        String jsonString = "{\"username\":\"" + username + "\",\"email\":\"" + email + "\",\"last_name\":\"" + lastname + "\",\"first_name\":\"" + firstname + "\"}";
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/user/update/" + employee.getId();
        try {
            Response response = HTTPMethods.put(jsonString, url);
            int respondCode = response.getRespondCode();
            JSONObject jsonResponse = new JSONObject(response.getResponse());
            if (respondCode >=200 && respondCode<=299) {
                updateEmployee(username,email,lastname,firstname);
                final Node source = (Node) event.getSource();
                final Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            }else{
                StringBuilder errorMessage = new StringBuilder();
                Map<String, Object> i=jsonResponse.toMap();
                for(Map.Entry<String, Object> entry  :i.entrySet()){
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


}

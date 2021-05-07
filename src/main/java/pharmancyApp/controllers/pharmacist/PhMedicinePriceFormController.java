package pharmancyApp.controllers.pharmacist;

import REST.HTTPMethods;
import REST.Response;
import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Medicine;
import org.json.JSONObject;
import pharmancyApp.Settings;

import java.util.Map;

public class PhMedicinePriceFormController {
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
    private boolean isUpdate =false;

    public void setOrderId(int id){
        this.orderId =id;
    }

    public void setUpdate(Boolean update){
        this.isUpdate =update;
    }

    public void setMedicine(Medicine medicine){
        this.medicine =medicine;
    }

    @FXML
    private void savePrice(ActionEvent event){
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/make/medicine-available/"+orderId;
        String jsonString = "{\"price\":\""+medicinePriceFld.getText()+"\"}";
        try {
            Response response = HTTPMethods.post(jsonString, url);
            int respondCode = response.getRespondCode();
            JSONObject jsonResponse = new JSONObject(response.getResponse());
            if (respondCode >=200 && respondCode<=299) {
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
                alert.setHeaderText("Αδυναμία συνδεσης");
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

    private void updatePrice(ActionEvent event){
        String price = medicinePriceFld.getText().trim();
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/update/medicine/"+medicine.getId()+"/price";
        String jsonString = "{\"price\":\""+price+"\"}";
        try {
            Response response = HTTPMethods.put(jsonString, url);
            int respondCode = response.getRespondCode();
            JSONObject jsonResponse = new JSONObject(response.getResponse());
            if (respondCode >=200 && respondCode<=299) {
                medicine.priceProperty().setValue(Float.valueOf(price));
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
                alert.setHeaderText("Αδυναμία συνδεσης");
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
    public void init(){
        if(isUpdate){
            headerTitle.setText("Ενημέρωση τιμής");
            headerImage.setImage(new Image("./assets/icons/udpate.png"));
            submit.setText("Ενημέρωση");
            medicinePriceFld.setText(Float.toString(medicine.getPrice()));
            submit.setOnAction(this::updatePrice);
        }else{
            headerTitle.setText("Τιμή φαρμάκου");
            headerImage.setImage(new Image("./assets/icons/euro.png"));
            submit.setText("Αποθήκευση");
            submit.setOnAction(this::savePrice);
        }
    }


}
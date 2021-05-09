package pharmancyApp.controllers.pharmacist;

import REST.HTTPMethods;
import REST.Response;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Order;
import models.OrderStatus;
import org.json.JSONObject;
import pharmancyApp.Settings;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class PhCustomerOrderDetailsController implements Initializable {
    @FXML
    private Label userNameLbl;

    @FXML
    private Label firstNameLbl;

    @FXML
    private Label lastNameLbl;

    @FXML
    private Label emailLbl;

    @FXML
    private Label domainLbl;

    @FXML
    private Label medNameLbl;

    @FXML
    private Label medCatLbl;

    @FXML
    private Label streetLbl;

    @FXML
    private Label streetNumLbl;

    @FXML
    private Label cityLbl;

    @FXML
    private Label postalCodeLbl;

    @FXML
    private Label quantityLbl;

    @FXML
    private Label totalPriceLbl;

    @FXML
    private ComboBox<OrderStatus> orderStatusComboBox;

    @FXML
    private Label orderDateLbl;

    private Order order;
    ObservableList<OrderStatus> orderStatuses = FXCollections.observableArrayList(new OrderStatus("OP", "Σε επεξεργασία"), new OrderStatus("OD", "Σε παράδοση"), new OrderStatus("DE", "Παραδόθηκε"));

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setFields() {
        userNameLbl.setText(order.getEmployee().getUsername());
        firstNameLbl.setText(order.getEmployee().getFirstname());
        lastNameLbl.setText(order.getEmployee().getLastname());
        emailLbl.setText(order.getEmployee().getEmail());
        domainLbl.setText(order.getEmployee().getDomain());
        medNameLbl.setText(order.getMedicine().getName());
        medCatLbl.setText(order.getMedicine().getMedicineCategory().toString());
        streetLbl.setText(order.getLocation().getStreet());
        streetNumLbl.setText(Integer.toString(order.getLocation().getStreetNum()));
        cityLbl.setText(order.getLocation().getCity());
        postalCodeLbl.setText(Integer.toString(order.getLocation().getPostalCode()));
        quantityLbl.setText(Integer.toString(order.getQuantity()));
        totalPriceLbl.setText(Float.toString(order.getTotalPrice()));
        for (OrderStatus os : orderStatuses) {
            if (os.getStatudId().equals(order.getOrderStatus().getStatudId())) {
                orderStatusComboBox.setValue(os);

            }
        }
        orderDateLbl.setText(order.getOrderDateFormatedProperty().get());


    }

    @FXML
    private void updateOrderStatus(ActionEvent event){
        OrderStatus orderStatus = orderStatusComboBox.getValue();
        String jsonString = "{\"order_status\":\""+orderStatus.getStatudId()+"\"}";
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/update/customer/order-status/"+order.getId();
        try {
            Response response = HTTPMethods.put(jsonString, url);
            int respondCode = response.getRespondCode();
            if (respondCode >= 200 && respondCode <= 299) {
                order.getOrderStatus().statusProperty().bind(orderStatus.statusProperty());
                final Node source = (Node) event.getSource();
                final Stage stage = (Stage) source.getScene().getWindow();
                stage.close();
            } else {
                StringBuilder errorMessage = new StringBuilder();
                JSONObject jsonResponse = new JSONObject(response.toString());
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
                alert.setHeaderText("Αδυναμια συνδεσης");
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
        orderStatusComboBox.setItems(orderStatuses);
    }
}

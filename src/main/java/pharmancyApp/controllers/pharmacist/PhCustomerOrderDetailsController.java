package pharmancyApp.controllers.pharmacist;

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
import javafx.stage.Stage;
import pharmancyApp.models.Order;
import pharmancyApp.models.OrderStatus;
import org.json.JSONObject;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;

import java.net.URL;
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
        userNameLbl.setText(order.getUser().getUsername());
        firstNameLbl.setText(order.getUser().getFirstname());
        lastNameLbl.setText(order.getUser().getLastname());
        emailLbl.setText(order.getUser().getEmail());
        domainLbl.setText(order.getUser().getDomain());
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
    private void updateOrderStatus(ActionEvent event) {
        OrderStatus orderStatus = orderStatusComboBox.getValue();
        String jsonString = "{\"order_status\":\"" + orderStatus.getStatudId() + "\"}";
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/update/customer/order-status/" + order.getId();
        try {
            Response response = HTTPMethods.put(jsonString, url);
            if (response != null) {
                int respondCode = response.getRespondCode();
                if (respondCode >= 200 && respondCode <= 299) {
                    order.getOrderStatus().statusProperty().bind(orderStatus.statusProperty());
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

            }
            String headerText = "Αδυναμία συνδεσης";
            String contentText = "Η επικοινωνία με τον εξυπηρετητή απέτυχε";
            AlertDialogs.alertPlainText(Alert.AlertType.ERROR,"Σφάλμα",headerText,contentText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        orderStatusComboBox.setItems(orderStatuses);
    }
}

package pharmancyApp.controllers.customer;

import REST.HTTPMethods;
import REST.Response;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import models.Medicine;
import models.MedicineCategory;
import models.Order;
import models.OrderStatus;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Colors;
import pharmancyApp.Settings;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class CuOrdersListController implements Initializable {
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, String> medicineNameCol;
    @FXML
    private TableColumn<Order, String> medCategoryCol;
    @FXML
    private TableColumn<Order, Integer> orderQuantityCol;
    @FXML
    private TableColumn<Order, Float> orderTotalPriceCol;
    @FXML
    private TableColumn<Order, String> orderStatusCol;
    @FXML
    private TableColumn<Order, String> orderDateTimeCol;
    @FXML
    private TableColumn<Order, String> orderOptionsCol;

    private final ObservableList<Order> orders = FXCollections.observableArrayList();


    @FXML
    private void getOrdersTable() {
        medicineNameCol.setCellValueFactory(item -> item.getValue().getMedicine().nameProperty());
        medCategoryCol.setCellValueFactory(item -> item.getValue().getMedicine().getMedicineCategory().nameProperty());
        orderQuantityCol.setCellValueFactory(item -> item.getValue().quantityProperty().asObject());
        orderTotalPriceCol.setCellValueFactory(item -> item.getValue().totalPriceProperty().asObject());
        orderStatusCol.setCellValueFactory(item -> item.getValue().getOrderStatus().statusProperty());
        orderDateTimeCol.setCellValueFactory(item -> item.getValue().getOrderDateFormatedProperty());
        Callback<TableColumn<Order, String>, TableCell<Order, String>> cellFoctory = (TableColumn<Order, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);

                } else {

                    JFXButton showReceipt = new JFXButton("Προβολή απόδειξης");
                    showReceipt.setStyle("-fx-background-color:" + Colors.VIEW);
                    showReceipt.setTextFill(Paint.valueOf(Colors.WHITE));


                    showReceipt.setOnMouseClicked((MouseEvent event) -> {

                    });


                    HBox actionsBtns = new HBox(showReceipt);
                    actionsBtns.setAlignment(Pos.CENTER);
                    actionsBtns.setSpacing(5);

                    setGraphic(actionsBtns);

                }
                setText(null);
            }

        };
        orderOptionsCol.setCellFactory(cellFoctory);
        ordersTable.setItems(orders);
    }

    public void fetchOrders() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/customer/get/orders";

        try {
            Response response = HTTPMethods.get(url);
            int respondCode = response.getRespondCode();
            JSONArray jsonArray = new JSONArray(response.getResponse());
            if (respondCode >= 200 && respondCode <= 299) {
                Medicine medicine;
                MedicineCategory medicineCategory;
                OrderStatus orderStatus;
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    System.out.println(jsonObject);
                    int id = jsonObject.getInt("id");
                    String medicineJson = jsonObject.getJSONObject("medicine").toString();
                    JSONObject medicineObj = new JSONObject(medicineJson);
                    int medicineId = medicineObj.getInt("id");
                    String medicineName = medicineObj.getString("name");
                    int medicineQuantity = medicineObj.getInt("quantity");
                    float medicinePrice = medicineObj.getFloat("price");
                    JSONObject medicineCategoryObj = medicineObj.getJSONObject("category");
                    int medicineCategoryId = medicineCategoryObj.getInt("id");
                    String medicineCategoryName = medicineCategoryObj.getString("name");
                    medicineCategory = new MedicineCategory(medicineCategoryId, medicineCategoryName);
                    int quantity = jsonObject.getInt("quantity");
                    float price = jsonObject.getFloat("total_price");
                    String orderStatusId = jsonObject.getString("order_status");
                    String ordStatus = switch (jsonObject.getString("order_status")) {
                        case "OP" -> "Σε επεξεργασία";
                        case "OD" -> "Σε παράδοση";
                        case "DE" -> "Παραδόθηκε";
                        default -> "Καμία ενέργεια";
                    };
                    String orderTimeDate = jsonObject.getString("date_ordered");
                    orderStatus = new OrderStatus(orderStatusId, ordStatus);
                    medicine = new Medicine(medicineId, medicineName, medicineQuantity, medicinePrice, medicineCategory);
                    Order order = new Order(id, null, medicine, quantity, price, orderStatus, null, orderTimeDate);
                    orders.add(order);


                }
                getOrdersTable();
            } else {
                StringBuilder errorMessage = new StringBuilder();
                JSONObject responseObj = new JSONObject(response);
                Map<String, Object> i = responseObj.toMap();
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
        fetchOrders();
    }
}

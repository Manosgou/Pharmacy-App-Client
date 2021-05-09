package pharmancyApp.controllers.supplier;

import REST.HTTPMethods;
import REST.Response;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Colors;
import pharmancyApp.Settings;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class SpOrdersListController implements Initializable {
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, String> buyersFirstNameCol;
    @FXML
    private TableColumn<Order, String> buyersLastNameCol;
    @FXML
    private TableColumn<Order, String> buyersDomainCol;
    @FXML
    private TableColumn<Order, String> medicineNameCol;
    @FXML
    private TableColumn<Order, Integer> orderQuantityCol;
    @FXML
    private TableColumn<Order, Float> orderTotalPriceCol;
    @FXML
    private TableColumn<Order, String> orderStatusCol;
    @FXML
    private TableColumn<Order, String> ordersOptionCol;

    private Order order;
    private final ObservableList<Order> orders = FXCollections.observableArrayList();


    @FXML
    private void getOrdersTable() {
        buyersFirstNameCol.setCellValueFactory(item -> item.getValue().getEmployee().firstnameProperty());
        buyersLastNameCol.setCellValueFactory(item -> item.getValue().getEmployee().lastnameProperty());
        buyersDomainCol.setCellValueFactory(item -> item.getValue().getEmployee().domainProperty());
        medicineNameCol.setCellValueFactory(item -> item.getValue().getMedicine().nameProperty());
        orderQuantityCol.setCellValueFactory(item -> item.getValue().getMedicine().quantityProperty().asObject());
        orderTotalPriceCol.setCellValueFactory(item -> item.getValue().totalPriceProperty().asObject());
        orderStatusCol.setCellValueFactory(item -> item.getValue().getOrderStatus().statusProperty());
        Callback<TableColumn<Order, String>, TableCell<Order, String>> cellFoctory = (TableColumn<Order, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);

                } else {

                    JFXButton viewOrder = new JFXButton("Προβολη");
                    viewOrder.setStyle("-fx-background-color:" + Colors.VIEW);
                    viewOrder.setTextFill(Paint.valueOf(Colors.WHITE));

                    JFXButton deleteOrder = new JFXButton("Διαγραφή");
                    deleteOrder.setStyle("-fx-background-color:" + Colors.DELETE);
                    deleteOrder.setTextFill(Paint.valueOf(Colors.WHITE));

                    viewOrder.setOnMouseClicked((MouseEvent event) -> {
                        order = getTableView().getItems().get(getIndex());
                        try {
                            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/SP/SpOrderDetailsScene.fxml")));
                            Parent root = loader.load();
                            SpOrderDetailsController spOrderDetailsController = loader.getController();
                            spOrderDetailsController.setOrder(order);
                            spOrderDetailsController.setFields();
                            Stage stage = new Stage();
                            stage.setTitle("Σύνδεση στο σύστημα");
                            stage.setScene(new Scene(root));
                            stage.setResizable(false);
                            stage.show();
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                    });


                    deleteOrder.setOnMouseClicked((MouseEvent event) -> {
                        order = getTableView().getItems().get(getIndex());
                        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/delete/order/" + order.getId();
                        Alert alert;

                        ButtonType delete = new ButtonType("Διαγραφή", ButtonBar.ButtonData.OK_DONE);
                        ButtonType cancel = new ButtonType("Ακύρωση", ButtonBar.ButtonData.CANCEL_CLOSE);
                        alert = new Alert(Alert.AlertType.WARNING,
                                "Είστε σίγουροι ότι θέλετε να διαγαψετε την παραγγελία του πελάτη " + order.getEmployee().getLastname() + " " + order.getEmployee().getFirstname() + ".\n(Η ενέργεια αυτή είναι μη αναστρέψιμη)",

                                delete,
                                cancel);

                        alert.setTitle("Διαγραφή πελάτη");
                        alert.setHeaderText("Προειδοποίηση!");
                        alert.getDialogPane().setMinHeight(200);
                        alert.getDialogPane().setMinWidth(500);
                        alert.setResizable(false);
                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.orElse(cancel) == delete) {
                            try {
                                Response response = HTTPMethods.delete(url);
                                int respondCode = response.getRespondCode();
                                if (respondCode > 200 && respondCode < 299) {
                                    orders.removeIf(m -> m.getId() == order.getId());
                                } else {
                                    StringBuilder errorMessage = new StringBuilder();
                                    JSONObject responseObj = new JSONObject(response);
                                    Map<String, Object> i = responseObj.toMap();
                                    for (Map.Entry<String, Object> entry : i.entrySet()) {
                                        errorMessage.append(entry.getValue().toString()).append("\n");
                                        System.out.println(entry.getKey() + "/" + entry.getValue());

                                    }
                                    alert = new Alert(Alert.AlertType.ERROR);
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
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }


                    });


                    HBox actionsBtns = new HBox(viewOrder, deleteOrder);
                    actionsBtns.setAlignment(Pos.CENTER);
                    actionsBtns.setSpacing(5);

                    setGraphic(actionsBtns);

                }
                setText(null);
            }

        };
        ordersOptionCol.setCellFactory(cellFoctory);
        ordersTable.setItems(orders);
    }


    public void fetchOrders() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/supplier/get/orders";

        try {
            Response response = HTTPMethods.get(url);
            int respondCode = response.getRespondCode();
            JSONArray jsonArray = new JSONArray(response.getResponse());
            if (respondCode >= 200 && respondCode <= 299) {
                Employee employee;
                Medicine medicine;
                MedicineCategory medicineCategory;
                Location location;
                OrderStatus orderStatus;
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    JSONObject employeeObj = new JSONObject(jsonObject.getJSONObject("employee").toString());
                    JSONObject userObj = new JSONObject(employeeObj.getJSONObject("user").toString());
                    int employeeId = userObj.getInt("id");
                    String employeeUsername = userObj.getString("username");
                    String employeeEmail = userObj.getString("email");
                    String employeeLastName = userObj.getString("last_name");
                    String employeeFirstName = userObj.getString("first_name");
                    String employeeDomain = switch (employeeObj.getString("domain")) {
                        case "PH" -> "Φαρμακοποιός";
                        case "SP" -> "Προμηθευτής";
                        case "CU" -> "Πελάτης";
                        default -> "(κενό)";
                    };
                    employee = new Employee(employeeId, employeeUsername, employeeEmail, employeeFirstName, employeeLastName, employeeDomain);
                    JSONObject medicineObj = new JSONObject(jsonObject.getJSONObject("medicine").toString());
                    int medicineId = medicineObj.getInt("id");
                    String medicineName = medicineObj.getString("name");
                    int medicineQuantity = medicineObj.getInt("quantity");
                    float medicinePrice = medicineObj.getFloat("price");
                    JSONObject medicineCategoryObj = medicineObj.getJSONObject("category");
                    int medicineCategoryId = medicineCategoryObj.getInt("id");
                    String medicineCategoryName = medicineCategoryObj.getString("name");
                    medicineCategory = new MedicineCategory(medicineCategoryId, medicineCategoryName);
                    JSONObject locationObj = new JSONObject(jsonObject.getJSONObject("location").toString());
                    int locationId = locationObj.getInt("id");
                    String locationStreet = locationObj.getString("street");
                    int locationStreetNum = locationObj.getInt("street_num");
                    String locationCity = locationObj.getString("city");
                    int lcoationPostalCode = locationObj.getInt("postal_code");
                    location = new Location(locationId, locationStreet, locationStreetNum, locationCity, lcoationPostalCode);
                    int quantity = jsonObject.getInt("quantity");
                    float price = jsonObject.getFloat("total_price");
                    String orderStatusId = jsonObject.getString("order_status");
                    String ordStatus = switch (jsonObject.getString("order_status")) {
                        case "OP" -> "Σε επεξεργασία";
                        case "OD" -> "Σε παράδοση";
                        case "DE" -> "Παραδόθηκε";
                        default -> "Καμία ενέργεια";
                    };
                    String orderDateTime = jsonObject.getString("date_ordered");
                    orderStatus = new OrderStatus(orderStatusId, ordStatus);
                    medicine = new Medicine(medicineId, medicineName, medicineQuantity, medicinePrice, medicineCategory);
                    Order order = new Order(id, employee, medicine, quantity, price, orderStatus, location, orderDateTime);
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

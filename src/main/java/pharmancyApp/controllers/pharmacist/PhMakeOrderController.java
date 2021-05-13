package pharmancyApp.controllers.pharmacist;

import REST.HTTPMethods;
import REST.Response;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Colors;
import pharmancyApp.Settings;

import java.util.*;

public class PhMakeOrderController {

    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private TableView<Medicine> medicineTable;
    @FXML
    private TableColumn<Medicine, String> medicineNameCol;
    @FXML
    private TableColumn<Medicine, Integer> medicineQuantityCol;
    @FXML
    private TableColumn<Medicine, Float> medicinePriceCol;
    @FXML
    private TableColumn<Medicine, String> actionsCol;
    @FXML
    private TableColumn<CartItem, String> medicineCartCol;
    @FXML
    private TableColumn<CartItem, String> cartActionsCol;
    @FXML
    private TableColumn<CartItem, Integer> quantityCartCol;
    @FXML
    private TableColumn<CartItem, Float> cartTotalCol;

    private Employee employee;
    private Medicine medicine;
    private CartItem cartItem;
    private Location location;
    private ObservableList<CartItem> cart = FXCollections.observableArrayList();
    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();


    public void setPharmancy(Location location) {
        this.location = location;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }


    @FXML
    private void getMedicinesTable() {
        medicineNameCol.setCellValueFactory(item -> item.getValue().nameProperty());
        medicineQuantityCol.setCellValueFactory(item -> item.getValue().quantityProperty().asObject());
        medicinePriceCol.setCellValueFactory(item -> item.getValue().priceProperty().asObject());
        Callback<TableColumn<Medicine, String>, TableCell<Medicine, String>> cellFoctory = (TableColumn<Medicine, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);

                } else {

                    JFXButton addToCart = new JFXButton("Προσθήκη στο καλάθι");
                    addToCart.setStyle("-fx-background-color:" + Colors.ADD);
                    addToCart.setTextFill(Paint.valueOf(Colors.WHITE));

                    TextField quantityFld = new TextField();
                    quantityFld.setMaxWidth(50);
                    quantityFld.setText("1");


                    addToCart.setOnMouseClicked((MouseEvent event) -> {
                        int quantity = Integer.parseInt(quantityFld.getText());
                        medicine = getTableView().getItems().get(getIndex());
                        if (quantity > 0) {
                            if (!(quantity > medicine.getQuantity())) {
                                if (cart.stream().noneMatch(m -> m.getMedicine().equals(medicine))) {
                                    cartItem = new CartItem(new Random().nextInt(), medicine, Integer.parseInt(quantityFld.getText()), medicine.getPrice());
                                    cart.add(cartItem);
                                    quantityFld.setText("1");
                                } else {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
                                    alert.setResizable(false);
                                    alert.setWidth(200);
                                    alert.setHeight(300);
                                    alert.setTitle("Σφάλμα");
                                    alert.setHeaderText("Είσαι καθυστερημένος");
                                    alert.setContentText("Το προιόν έχει ήδη προστεθεί στο καλάθι");
                                    alert.showAndWait();
                                    if (alert.getResult().equals(okBtn)) {
                                        alert.close();
                                    }
                                }
                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
                                alert.setResizable(false);
                                alert.setWidth(200);
                                alert.setHeight(300);
                                alert.setTitle("Σφάλμα");
                                alert.setHeaderText("Είσαι καθυστερημένος");
                                alert.setContentText("Η ποσότητα που επιλέξατε είναι μεγαλύτερη από αύτη που σας παρέχει ο προμηθευτής");
                                alert.showAndWait();
                                if (alert.getResult().equals(okBtn)) {
                                    alert.close();
                                }
                            }

                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
                            alert.setResizable(false);
                            alert.setWidth(200);
                            alert.setHeight(300);
                            alert.setTitle("Σφάλμα");
                            alert.setHeaderText("Είσαι καθυστερημένος");
                            alert.setContentText("Το πεδίο της ποσότητας δεν μπορεί να είναι αρνητικό ή μηδέν.");
                            alert.showAndWait();
                            if (alert.getResult().equals(okBtn)) {
                                alert.close();
                            }
                        }

                    });
                    Label quantityLbl = new Label("Ποσότητα:");
                    HBox quantity = new HBox(quantityLbl, quantityFld);
                    quantity.setSpacing(10);
                    quantity.setAlignment(Pos.CENTER);
                    VBox actionsBtns = new VBox(addToCart, quantity);
                    actionsBtns.setAlignment(Pos.CENTER);
                    actionsBtns.setSpacing(3);
                    actionsBtns.setAlignment(Pos.CENTER);

                    setGraphic(actionsBtns);

                }
                setText(null);
            }

        };
        actionsCol.setCellFactory(cellFoctory);
        medicineTable.setItems(this.medicines);
    }

    @FXML
    private void getCartTable() {
        medicineCartCol.setCellValueFactory(item -> item.getValue().getMedicine().nameProperty());
        quantityCartCol.setCellValueFactory(item -> item.getValue().quantityProperty().asObject());
        cartTotalCol.setCellValueFactory(item -> item.getValue().priceProperty().asObject());
        Callback<TableColumn<CartItem, String>, TableCell<CartItem, String>> cellFoctory = (TableColumn<CartItem, String> param) -> new TableCell<>() {
            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);

                } else {

                    JFXButton deleteItem = new JFXButton("Διαγραφή");
                    deleteItem.setStyle("-fx-background-color:" + Colors.DELETE);
                    deleteItem.setTextFill(Paint.valueOf(Colors.WHITE));


                    deleteItem.setOnMouseClicked((MouseEvent event) -> {
                        cartItem = getTableView().getItems().get(getIndex());
                        cart.remove(cartItem);
                    });

                    HBox actionsBtns = new HBox(deleteItem);
                    actionsBtns.setAlignment(Pos.CENTER);
                    actionsBtns.setSpacing(5);

                    setGraphic(actionsBtns);

                }
                setText(null);
            }

        };
        cartActionsCol.setCellFactory(cellFoctory);
        cartTable.setItems(this.cart);
    }

    public void fetchMedicines() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/make/order";
        try {
            Response response = HTTPMethods.get(url);
            int respondCode = response.getRespondCode();
            JSONArray jsonArray = new JSONArray(response.getResponse());
            if (respondCode >= 200 && respondCode <= 299) {

                MedicineCategory medicineCategory;
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    int quantity = jsonObject.getInt("quantity");
                    float price = jsonObject.getFloat("price");
                    JSONObject categoryObj = jsonObject.getJSONObject("category");
                    int categoryId = categoryObj.getInt("id");
                    String categoryName = categoryObj.getString("name");
                    medicineCategory = new MedicineCategory(categoryId, categoryName);
                    this.medicine = new Medicine(id, name, quantity, price, medicineCategory);
                    this.medicines.addAll(this.medicine);

                }

                getMedicinesTable();
                getCartTable();


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

    private String cartToJson() {
        StringBuilder jsonString = new StringBuilder();

        for (int i = 0; i < cart.size(); i++) {
            jsonString.append("{\"employee\":\"").append(employee.getId()).append("\",\"medicine\":\"").append(cart.get(i).getMedicine().getId()).append("\",\"quantity\":\"").append(cart.get(i).getQuantity()).append("\",\"total_price\":\"").append(cart.get(i).getPrice()).append(cart.get(i).getQuantity()).append("\",\"location\":\"").append(location.getId()).append("\"}");
            if (!(i == cart.size() - 1)) {
                jsonString.append(",");
            }

        }


        return String.format("[%s]", jsonString);
    }

    private String checkCart(){
        if(!cart.isEmpty()){
            return cartToJson();
        }
        Alert alert = new Alert(Alert.AlertType.ERROR);
        ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
        alert.setResizable(false);
        alert.setWidth(200);
        alert.setHeight(300);
        alert.setTitle("Σφάλμα");
        alert.setHeaderText("H παραγγελία δεν μπορεί να πραγματοποιηθεί.");
        alert.setContentText("Το καλάθι δεν μπορεί να είναι κενό.");
        alert.showAndWait();
        if (alert.getResult().equals(okBtn)) {
            alert.close();
        }
        return null;
    }

    @FXML
    private void submitOrder(ActionEvent event) {
        String jsonString = checkCart();
        if(jsonString !=null) {
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/make/order";
            try {
                Response response = HTTPMethods.post(jsonString, url);
                int respondCode = response.getRespondCode();
                if (respondCode >= 200 && respondCode <= 299) {

                    final Node source = (Node) event.getSource();
                    final Stage stage = (Stage) source.getScene().getWindow();
                    stage.close();

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
    }


}

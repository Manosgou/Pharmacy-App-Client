package pharmancyApp.controllers.customer;

import REST.Authentication;
import REST.HTTPMethods;
import REST.Response;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.Colors;
import pharmancyApp.Settings;
import pharmancyApp.Utils.AlertDialogs;
import pharmancyApp.Utils.TextFieldFilters;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class CuMakeOrderController implements Initializable {
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

    private User user;
    private Medicine medicine;
    private CartItem cartItem;
    private Location location;
    private final ObservableList<CartItem> cart = FXCollections.observableArrayList();
    private final ObservableList<Medicine> medicines = FXCollections.observableArrayList();

    public void setPharmancy(Location location) {
        this.location = location;
    }

    public void setEmployee(User user) {
        this.user = user;
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
                    addToCart.setStyle("-fx-background-color:" + Colors.GREEN);
                    addToCart.setTextFill(Paint.valueOf(Colors.WHITE));

                    TextField quantityFld = new TextField();
                    quantityFld.setMaxWidth(50);
                    quantityFld.setTextFormatter(new TextFormatter<Integer>(new IntegerStringConverter(), 1, TextFieldFilters.integerFilter));


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
                                    String headerText = "";
                                    String contentText = "Το προιόν έχει ήδη προστεθεί στο καλάθι.";
                                    AlertDialogs.error(headerText, null, contentText);
                                }
                            } else {
                                String headerText = "Λανθασμενη ποσοτητα";
                                String contentText = "Η ποσότητα που επιλέξατε είναι μεγαλύτερη από αύτη που σας παρέχει ο προμηθευτής";
                                AlertDialogs.error(headerText, null, contentText);
                            }

                        } else {
                            String headerText = "Λανθασμενη ποσοτητα";
                            String contentText = "Το πεδίο της ποσότητας δεν μπορεί να είναι αρνητικό ή μηδέν.";
                            AlertDialogs.error(headerText, null, contentText);

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
                    deleteItem.setStyle("-fx-background-color:" + Colors.RED);
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

    private void fetchMedicines() {
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/customer/make/order";
        try {
            Response response = HTTPMethods.get(url);
            if (response != null) {


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
                    JSONObject responseObj = new JSONObject(response.getResponse());
                    String headerText = "Αδυναμια συνδεσης";
                    AlertDialogs.error(headerText, responseObj, null);
                    if (respondCode == 401) {
                        Authentication.setLogin(false);
                    }
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

    private String cartToJson() {
        StringBuilder jsonString = new StringBuilder();

        for (int i = 0; i < cart.size(); i++) {
            jsonString.append("{\"user_profile\":\"").append(user.getId()).append("\",\"medicine\":\"").append(cart.get(i).getMedicine().getId()).append("\",\"quantity\":\"").append(cart.get(i).getQuantity()).append("\",\"total_price\":\"").append(cart.get(i).getPrice()).append(cart.get(i).getQuantity()).append("\",\"location\":\"").append(location.getId()).append("\"}");
            if (!(i == cart.size() - 1)) {
                jsonString.append(",");
            }

        }


        return String.format("[%s]", jsonString);
    }

    private String checkCart() {
        if (!cart.isEmpty()) {
            return cartToJson();
        }
        String headerText = "H παραγγελία δεν μπορεί να πραγματοποιηθεί.";
        String contentText = "Το καλάθι δεν μπορεί να είναι κενό.";
        AlertDialogs.error(headerText, null, contentText);
        return null;
    }


    @FXML
    void submitOrder(ActionEvent event) {
        String jsonString = checkCart();
        if (jsonString != null) {
            String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/customer/make/order";
            try {
                Response response = HTTPMethods.post(jsonString, url);
                if (response != null) {
                    int respondCode = response.getRespondCode();
                    if (respondCode >= 200 && respondCode <= 299) {
                        final Node source = (Node) event.getSource();
                        final Stage stage = (Stage) source.getScene().getWindow();
                        stage.close();
                    } else {
                        JSONObject responseObj = new JSONObject(response.getResponse());
                        String headerText = "Αδυναμια συνδεσης";
                        AlertDialogs.error(headerText, responseObj, null);
                        if (respondCode == 401) {
                            Authentication.setLogin(false);
                        }
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
        fetchMedicines();
    }
}

package pharmancyApp.controllers.pharmacist;

import pharmancyApp.rest.Authentication;
import pharmancyApp.rest.HTTPMethods;
import pharmancyApp.rest.Response;
import com.itextpdf.html2pdf.HtmlConverter;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import pharmancyApp.models.*;
import org.json.JSONArray;
import org.json.JSONObject;
import pharmancyApp.utils.Colors;
import pharmancyApp.Settings;
import pharmancyApp.utils.AlertDialogs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;


public class PhOrdersListController{

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

    private Order order;
    private final ObservableList<Order> orders = FXCollections.observableArrayList();

    private User user;
    private Location location;

    public void setUser(User user) {
        this.user = user;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

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

                    JFXButton makeItAvailable = new JFXButton("Προσθήκη στο κατάστημα");
                    makeItAvailable.setStyle("-fx-background-color:" + Colors.PRIMARY);
                    makeItAvailable.setTextFill(Paint.valueOf(Colors.WHITE));

                    JFXButton saveReceipt = new JFXButton("Αποθήκευση απόδειξης");
                    saveReceipt.setStyle("-fx-background-color:" + Colors.BLUE);
                    saveReceipt.setTextFill(Paint.valueOf(Colors.WHITE));





                    makeItAvailable.setOnMouseClicked((MouseEvent event) -> {
                        order = getTableView().getItems().get(getIndex());
                        if (order.getOrderStatus().getStatudId().equals("DE")) {
                            try {
                                FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/scenes/PH/PhMedicinePriceForm.fxml")));
                                Parent root = loader.load();
                                PhMedicinePriceFormController phMedicinePriceFormController = loader.getController();
                                phMedicinePriceFormController.setOrderId(order.getId());
                                phMedicinePriceFormController.init();
                                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                stage.setTitle("Hello World");
                                stage.setScene(new Scene(root));
                                stage.setResizable(false);
                                stage.show();
                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                        } else {
                            String headerText = "Αδυναμια συνδεσης";
                            String contentText = "Για να διαθέσετε το φάρμακο " + order.getMedicine().getName() + " προς πωληση,πρεπει η κατασταση παραγγελιιας να ειναι η τελικη";
                            AlertDialogs.error(headerText, null, contentText);
                        }


                    });


                    saveReceipt.setOnMouseClicked((MouseEvent event) -> {
                        order = getTableView().getItems().get(getIndex());
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Αποθήκευση ως");
                        File dest = fileChooser.showSaveDialog(new Stage());
                        if (dest != null) {
                            String filePath = dest.getAbsolutePath();
                            if(!filePath.endsWith(".pdf")) {
                                try {
                                    saveReceipt(order, new File(filePath + ".pdf"));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });

                    HBox actionsBtns = new HBox(makeItAvailable,saveReceipt);
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
        String url = (Settings.DEBUG ? "http://127.0.0.1:8000/" : "https://pharmacyapp-api.herokuapp.com/") + "api/v1/pharmacist/get/orders";

        try {
            Response response = HTTPMethods.get(url);
            if (response != null) {
                int respondCode = response.getRespondCode();
                if (respondCode >= 200 && respondCode <= 299) {
                    JSONArray jsonArray = new JSONArray(response.getResponse());
                    Medicine medicine;
                    MedicineCategory medicineCategory;
                    OrderStatus orderStatus;
                    Order order;
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
                        float price = Math.round(jsonObject.getFloat("total_price"));
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
                        order = new Order(id, user, medicine, quantity, price, orderStatus, location, orderTimeDate);
                        orders.add(order);


                    }
                    getOrdersTable();
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


    private void saveReceipt(Order order, File file) throws IOException {
        String htmlReceipt = "<html lang=\"en\">\n" +
                "  <head>\n" +
                "    <style>\n" +
                "      body {\n" +
                "        font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;\n" +
                "        text-align: center;\n" +
                "        color: #777;\n" +
                "      }\n" +
                "      body h1 {\n" +
                "        font-weight: 300;\n" +
                "        margin-bottom: 0px;\n" +
                "        padding-bottom: 0px;\n" +
                "        color: #FF4233;\n" +
                "      }\n" +
                "      body h3 {\n" +
                "        font-weight: 300;\n" +
                "        margin-top: 10px;\n" +
                "        margin-bottom: 20px;\n" +
                "        font-style: italic;\n" +
                "        color: #555;\n" +
                "      }\n" +
                "      body h4 {\n" +
                "        color: #FF4233;\n" +
                "      }\n" +
                "      .invoice-box {\n" +
                "        max-width: 800px;\n" +
                "        margin: auto;\n" +
                "        padding: 30px;\n" +
                "        border: 1px solid #eee;\n" +
                "        box-shadow: 0 0 10px rgba(0, 0, 0, 0.15);\n" +
                "        font-size: 16px;\n" +
                "        line-height: 24px;\n" +
                "        font-family: 'Helvetica Neue', 'Helvetica', Helvetica, Arial, sans-serif;\n" +
                "        color: #555;\n" +
                "      }\n" +
                "      .invoice-box table {\n" +
                "        width: 100%;\n" +
                "        line-height: inherit;\n" +
                "        text-align: left;\n" +
                "        border-collapse: collapse;\n" +
                "      }\n" +
                "      .invoice-box table td {\n" +
                "        padding: 5px;\n" +
                "        vertical-align: top;\n" +
                "      }\n" +
                "      .invoice-box table tr td:nth-child(2) {\n" +
                "        text-align: right;\n" +
                "      }\n" +
                "      .invoice-box table tr.top table td {\n" +
                "        padding-bottom: 20px;\n" +
                "      }\n" +
                "      .invoice-box table tr.top table td.title {\n" +
                "        font-size: 45px;\n" +
                "        line-height: 45px;\n" +
                "        color: #333;\n" +
                "      }\n" +
                "      .invoice-box table tr.information table td {\n" +
                "        padding-bottom: 40px;\n" +
                "      }\n" +
                "      .invoice-box table tr.heading td {\n" +
                "        background: #eee;\n" +
                "        border-bottom: 1px solid #ddd;\n" +
                "        font-weight: bold;\n" +
                "      }\n" +
                "      .invoice-box table tr.details td {\n" +
                "        padding-bottom: 20px;\n" +
                "      }\n" +
                "      .invoice-box table tr.item td {\n" +
                "        border-bottom: 1px solid #eee;\n" +
                "      }\n" +
                "      .invoice-box table tr.item.last td {\n" +
                "        border-bottom: none;\n" +
                "      }\n" +
                "      .invoice-box table tr.total td:nth-child(2) {\n" +
                "        border-top: 2px solid #eee;\n" +
                "        font-weight: bold;\n" +
                "      }\n" +
                "      @media only screen and (max-width: 600px) {\n" +
                "        .invoice-box table tr.top table td {\n" +
                "          width: 100%;\n" +
                "          display: block;\n" +
                "          text-align: center;\n" +
                "        }\n" +
                "        .invoice-box table tr.information table td {\n" +
                "          width: 100%;\n" +
                "          display: block;\n" +
                "          text-align: center;\n" +
                "        }\n" +
                "      }\n" +
                "    </style>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <h1>Σας ευχαριστούμε για την προτίμηση!</h1>\n" +
                "    <h3>Απόδειξη αγοράς.</h3>\n" +
                "    <div class=\"invoice-box\">\n" +
                "      <table>\n" +
                "        <tr class=\"top\">\n" +
                "          <td colspan=\"2\">\n" +
                "            <table>\n" +
                "              <tr>\n" +
                "                <td class=\"title\">\n" +
                "                <h4>Pharma[Co]</h4>"+
                "                </td>\n" +
                "                <td>\n" +
                "                  Αρ. απόδειξης #: "+order.getId()+"<br />\n" +
                "                  Ημ. παραγγελίας: "+order.getOrderDateFormatedProperty().get()+"<br />\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr class=\"information\">\n" +
                "          <td colspan=\"2\">\n" +
                "            <table>\n" +
                "              <tr>\n" +
                "                <td>\n" +
                "                  <b>Στοιχεία τοποθεσίας</b>.<br />\n" +
                "                  "+order.getLocation().getStreet()+" "+order.getLocation().getStreetNum()+"<br />\n" +
                "                  "+order.getLocation().getCity()+", Τ.Κ "+order.getLocation().getPostalCode()+"\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                  <b>Στοιχεία πελάτη</b>.<br />\n" +
                "                  "+order.getUser().getLastname()+" "+order.getUser().getFirstname()+"<br />\n" +
                "                  "+order.getUser().getEmail()+"\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr class=\"heading\">\n" +
                "          <td>Κατάσταση παραγγελίας</td>\n" +
                "          <td>Κωδικός κατάστασης</td>\n" +
                "        </tr>\n" +
                "        <tr class=\"details\">\n" +
                "          <td>"+order.getOrderStatus().getStatus()+"</td>\n" +
                "          <td><i>"+order.getOrderStatus().getStatudId()+"</i></td>\n" +
                "        </tr>\n" +
                "        <tr class=\"heading\">\n" +
                "          <td>Όνομα φαρμάκου</td>\n" +
                "          <td>Τιμή φαρμάκου * Ποσότητα</td>\n" +
                "        </tr>\n" +
                "        <tr class=\"item\">\n" +
                "          <td>"+order.getMedicine().getName()+" - "+order.getMedicine().getMedicineCategory().getName()+"</td>\n" +
                "          <td>"+order.getMedicine().getPrice()+" € * "+order.getQuantity()+"</td>\n" +
                "        </tr>\n" +
                "        <tr class=\"total\">\n" +
                "          <td></td>\n" +
                "          <td>Σύνολο: "+order.getTotalPrice()+" €</td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "    </div>\n" +
                "  </body>\n" +
                "</html>";
        HtmlConverter.convertToPdf(htmlReceipt, new FileOutputStream(file));

    }


    @FXML
    private void refreshTable() {
        orders.clear();
        fetchOrders();
    }
}

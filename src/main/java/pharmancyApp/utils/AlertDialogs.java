package pharmancyApp.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.json.JSONObject;
import java.util.Map;

public class AlertDialogs {

    public static void alertJSONResponse(Alert.AlertType alertType, String title, String headerText, JSONObject jsonResponse) {
        Alert alert = new Alert(alertType);
        ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
        alert.setResizable(false);
        alert.setWidth(200);
        alert.setHeight(300);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        StringBuilder errorMessage = new StringBuilder();
        Map<String, Object> i = jsonResponse.toMap();
        for (Map.Entry<String, Object> entry : i.entrySet()) {
            errorMessage.append(entry.getValue().toString()).append("\n");
            System.out.println(entry.getKey() + "/" + entry.getValue());

        }
        alert.setContentText(errorMessage.toString().replaceAll("[\\[\\]\"]", ""));
        alert.showAndWait();
        if (alert.getResult().equals(okBtn)) {
            alert.close();
        }
    }



    public static void alertPlainText(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
        alert.setResizable(false);
        alert.setWidth(200);
        alert.setHeight(300);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
        if (alert.getResult().equals(okBtn)) {
            alert.close();
        }
    }
}

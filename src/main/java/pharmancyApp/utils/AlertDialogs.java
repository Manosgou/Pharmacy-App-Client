package pharmancyApp.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import java.util.Map;

public class AlertDialogs {

    public static void error(String headerText,@Nullable JSONObject jsonResponse,@Nullable String contentText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        ButtonType okBtn = new ButtonType("Εντάξει", ButtonBar.ButtonData.OK_DONE);
        alert.setResizable(false);
        alert.setWidth(200);
        alert.setHeight(300);
        alert.setTitle("Σφάλμα");
        alert.setHeaderText(headerText);
        StringBuilder errorMessage = new StringBuilder();
        if(jsonResponse !=null) {
            Map<String, Object> i = jsonResponse.toMap();
            for (Map.Entry<String, Object> entry : i.entrySet()) {
                errorMessage.append(entry.getValue().toString()).append("\n");
                System.out.println(entry.getKey() + "/" + entry.getValue());

            }
            alert.setContentText(errorMessage.toString().replaceAll("[\\[\\]\"]", ""));
        }
        if(contentText !=null){
            alert.setContentText(contentText);
        }
        alert.showAndWait();
        if (alert.getResult().equals(okBtn)) {
            alert.close();
        }
    }
}

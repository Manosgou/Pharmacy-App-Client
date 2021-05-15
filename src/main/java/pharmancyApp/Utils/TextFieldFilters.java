package pharmancyApp.Utils;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public class TextFieldFilters {
    public static UnaryOperator<TextFormatter.Change> integerFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("^[0-9]+$")) {
            return change;
        }
        return null;
    };

    public static UnaryOperator<TextFormatter.Change> floatFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("^[1-9]+\\.?[1-9]*$")) {
            return change;
        }
        return null;
    };

}

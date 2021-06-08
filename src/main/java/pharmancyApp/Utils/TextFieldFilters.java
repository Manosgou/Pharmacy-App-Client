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
        if (newText.matches("^[0-9]+\\.?[0-9]*$")) {
            return change;
        }
        return null;
    };

    public static UnaryOperator<TextFormatter.Change> stringFilter = change -> {
        String newText = change.getControlNewText();
        if (newText.matches("^[\\u0370-\\u03ff\\u1f00-\\u1fffA-za-z-. ]*$")) {
            return change;
        }
        return null;
    };


}

package pharmancyApp.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Location {
    private IntegerProperty id;
    private StringProperty street;
    private IntegerProperty streetNum;
    private StringProperty city;
    private IntegerProperty postalCode;

    public Location(int id, String street, int streetNum, String city, int postalCode){
        this.id = new SimpleIntegerProperty(id);
        this.street = new SimpleStringProperty(street);
        this.streetNum = new SimpleIntegerProperty(streetNum);
        this.city = new SimpleStringProperty(city);
        this.postalCode = new SimpleIntegerProperty(postalCode);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getStreet() {
        return street.get();
    }

    public StringProperty streetProperty() {
        return street;
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public int getStreetNum() {
        return streetNum.get();
    }

    public IntegerProperty streetNumProperty() {
        return streetNum;
    }

    public void setStreetNum(int streetNum) {
        this.streetNum.set(streetNum);
    }

    public String getCity() {
        return city.get();
    }

    public StringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    public int getPostalCode() {
        return postalCode.get();
    }

    public IntegerProperty postalCodeProperty() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode.set(postalCode);
    }
}

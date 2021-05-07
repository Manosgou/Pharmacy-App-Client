package models;

import javafx.beans.property.*;

public class Medicine {
    private IntegerProperty id;
    private StringProperty name;
    private IntegerProperty quantity;
    private FloatProperty price;
    private MedicineCategory medicineCategory;

    public Medicine(int id,String name,int quantity,float price,MedicineCategory medicineCategory){
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.quantity =new SimpleIntegerProperty(quantity);
        this.price = new SimpleFloatProperty(price);
        this.medicineCategory = medicineCategory;

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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }

    public float getPrice() {
        return price.get();
    }

    public FloatProperty priceProperty() {
        return price;
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    public MedicineCategory getMedicineCategory() {
        return medicineCategory;
    }

    public void setMedicineCategory(MedicineCategory medicineCategory) {
        this.medicineCategory = medicineCategory;
    }
}

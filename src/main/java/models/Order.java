package models;

import javafx.beans.property.*;
import org.jetbrains.annotations.Nullable;

import java.lang.constant.Constable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Order {
    private IntegerProperty id;
    private Employee employee;
    private Medicine medicine;
    private IntegerProperty quantity;
    private FloatProperty totalPrice;
    private OrderStatus orderStatus;
    private Location location;
    private StringProperty orderDateTime;

    public Order(int id, @Nullable Employee employee, Medicine medicine, int quantity, float totalPrice, OrderStatus orderStatus, @Nullable Location location,String orderDateTime) {
        this.id = new SimpleIntegerProperty(id);
        this.employee = employee;
        this.medicine = medicine;
        this.quantity = new SimpleIntegerProperty(quantity);
        this.totalPrice = new SimpleFloatProperty(totalPrice);
        this.orderStatus = orderStatus;
        this.location = location;
        this.orderDateTime= new SimpleStringProperty(orderDateTime);
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

    public Medicine getMedicine() {
        return medicine;
    }

    public void setMedicine(Medicine medicine) {
        this.medicine = medicine;
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

    public float getTotalPrice() {
        return totalPrice.get();
    }

    public FloatProperty totalPriceProperty() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice.set(totalPrice);
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getOrderDateTime() {
        return orderDateTime.get();
    }

    public StringProperty orderDateTimeProperty() {
        return orderDateTime;
    }

    public void setOrderDateTime(String orderDateTime) {
        this.orderDateTime.set(orderDateTime);
    }

    public StringProperty getOrderDateFormatedProperty(){
        String dateTime = null;
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(this.orderDateTime.get());
            dateTime = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new SimpleStringProperty(dateTime);
    }
}

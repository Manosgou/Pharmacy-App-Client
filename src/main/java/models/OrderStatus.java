package models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class OrderStatus {
    private StringProperty statudId;
    private StringProperty status;

    public OrderStatus(String statudId, String status) {
        this.statudId = new SimpleStringProperty(statudId);
        this.status = new SimpleStringProperty(status);
    }

    public String getStatudId() {
        return statudId.get();
    }

    public StringProperty statudIdProperty() {
        return statudId;
    }

    public void setStatudId(String statudId) {
        this.statudId.set(statudId);
    }

    public String getStatus() {
        return status.get();
    }

    public StringProperty statusProperty() {
        return status;
    }

    public void setStatus(String status) {
        this.status.set(status);
    }
    @Override
    public String toString() {
        return this.getStatus();
    }
}

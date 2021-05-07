package pharmancyApp.controllers.supplier;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import models.Medicine;

public class SpMedicineDetailsController {
    @FXML
    private Label medicineNameLbl;

    @FXML
    private Label medicineQuaLbl;

    @FXML
    private Label medicinePriceLbl;

    @FXML
    private Label medicineCatLbl;

    private Medicine medicine;

    public void setMedicine(Medicine medicine){
        this.medicine =medicine;

    }


    public void init(){
        medicineNameLbl.setText(medicine.getName());
        medicineQuaLbl.setText(Integer.toString(medicine.getQuantity()));
        medicinePriceLbl.setText(medicine.getPrice() +" €");
        medicineCatLbl.setText(medicine.getMedicineCategory().getName());
    }


}

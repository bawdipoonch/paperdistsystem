package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class EditHawkerController implements Initializable {

private Hawker hawkerRow;
	
	//Edit Customer Fields FXML
	@FXML private TextField editNameTF;
	@FXML private TextField editHawkerCodeTF;
	@FXML private TextField editMobileNumTF;
	@FXML private TextField editAgencyNameTF;
	@FXML private TextField editOldHouseNumTF;
	@FXML private TextField editNewHouseNumTF;
	@FXML private TextField editAddrLine1;
	@FXML private TextField editAddrLine2;
	@FXML private TextField editLocalityTF;
	@FXML private TextField editCityTF;
	@FXML private TextField editFeeTF;
	@FXML private CheckBox editActiveCheck;
	@FXML private ComboBox<String> editStateLOV;
	@FXML private TextField editProfile1TF;
	@FXML private TextField editProfile2TF;
	@FXML private TextField editProfile3TF;
	@FXML private TextField initialsTF;

	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

//		setupBindings();
		
	}
	
	public void setHawkerToEdit(Hawker hawker){
		this.hawkerRow = hawker;
	}
	
	public void setupBindings(){
		editStateLOV.getItems().addAll("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", 
				"Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha",
				"Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal");
		editStateLOV.getSelectionModel().select(hawkerRow.getState());
		
		
		editNameTF.setText(hawkerRow.getName());
		editHawkerCodeTF.setText(""+hawkerRow.getHawkerCode());
		editMobileNumTF.setText(hawkerRow.getMobileNum());
		editOldHouseNumTF.setText(hawkerRow.getOldHouseNum());
		editNewHouseNumTF.setText(hawkerRow.getNewHouseNum());
		editAddrLine1.setText(hawkerRow.getAddrLine1());
		editAddrLine2.setText(hawkerRow.getAddrLine2());
		editLocalityTF.setText(hawkerRow.getLocality());
		editCityTF.setText(hawkerRow.getCity());
		editAgencyNameTF.setText(hawkerRow.getAgencyName());
		editFeeTF.setText(""+hawkerRow.getFee());
		editActiveCheck.setSelected(hawkerRow.getActiveFlag());
		editProfile1TF.setText(hawkerRow.getProfile1());
		editProfile2TF.setText(hawkerRow.getProfile2());
		editProfile3TF.setText(hawkerRow.getProfile3());
		initialsTF.setText(hawkerRow.getInitials());
	}
	
	public Hawker returnUpdatedHawker(){
		Hawker edittedHawker = new Hawker(hawkerRow);
        edittedHawker.setName(editNameTF.getText());
        edittedHawker.setMobileNum(editMobileNumTF.getText());
        edittedHawker.setHawkerCode(editHawkerCodeTF.getText());
        edittedHawker.setOldHouseNum(editOldHouseNumTF.getText());
        edittedHawker.setNewHouseNum(editNewHouseNumTF.getText());
        edittedHawker.setAddrLine1(editAddrLine1.getText());
        edittedHawker.setAddrLine2(editAddrLine2.getText());
        edittedHawker.setLocality(editLocalityTF.getText());
        edittedHawker.setCity(editCityTF.getText());
        edittedHawker.setState(editStateLOV.getSelectionModel().getSelectedItem());
        edittedHawker.setFee(Double.parseDouble(editFeeTF.getText()));
        edittedHawker.setActiveFlag(editActiveCheck.isSelected());
        edittedHawker.setAgencyName(editAgencyNameTF.getText());
        edittedHawker.setProfile1(editProfile1TF.getText());
        edittedHawker.setProfile2(editProfile2TF.getText());
        edittedHawker.setProfile3(editProfile3TF.getText());
        edittedHawker.setInitials(initialsTF.getText());
        edittedHawker.updateHawkerRecord();
    	return edittedHawker;
	}

}

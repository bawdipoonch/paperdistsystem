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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class EditCustomerController implements Initializable {

	private Customer custRow;
	
	//Edit Customer Fields FXML
	@FXML private TextField editNameTF;
	@FXML private TextField editCustomerCodeTF;
	@FXML private TextField editMobileNumTF;
	@FXML private TextField editHouseSeqTF;
	@FXML private TextField editOldHouseNumTF;
	@FXML private TextField editNewHouseNumTF;
	@FXML private TextField editAddrLine1;
	@FXML private TextField editAddrLine2;
	@FXML private TextField editLocalityTF;
	@FXML private TextField editCityTF;
	@FXML private TextField editProfile1TF;
	@FXML private TextField editProfile2TF;
	@FXML private TextField editProfile3TF;
	@FXML private TextField initialsTF;
	@FXML private ComboBox<String> editHawkerCodeLOV;
	@FXML private ComboBox<String> editLineNumLOV;
	@FXML private ComboBox<String> editStateLOV;

	private ObservableList<String> hawkerCodeData = FXCollections.observableArrayList();
	private ObservableList<String> hawkerLineNumData = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

//		setupBindings();
		
	}
	
	public void setCustomerToEdit(Customer customer){
		this.custRow = customer;
	}
	
	public void setupBindings(){
		editStateLOV.getItems().addAll("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", 
				"Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha",
				"Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal");
		editStateLOV.getSelectionModel().select(custRow.getState());
		populateHawkerCodes();
		editHawkerCodeLOV.getItems().addAll(hawkerCodeData);
		
		editHawkerCodeLOV.getSelectionModel().select(custRow.getHawkerCode());
		editLineNumLOV.getItems().clear();
		populateLineNumbersForHawkerCode(custRow.getHawkerCode());
		editLineNumLOV.getItems().addAll(hawkerLineNumData);
		editLineNumLOV.getSelectionModel().select(""+custRow.getLineNum());
		if(HawkerLoginController.loggedInHawker==null)
			editHawkerCodeLOV.setDisable(false);
		editHawkerCodeLOV.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				editLineNumLOV.getItems().clear();
				populateLineNumbersForHawkerCode(newValue);
				editLineNumLOV.getItems().addAll(hawkerLineNumData);
				editLineNumLOV.getSelectionModel().clearSelection();
			}
		});
		editNameTF.setText(custRow.getName());
		editCustomerCodeTF.setText(""+custRow.getCustomerCode());
		editMobileNumTF.setText(custRow.getMobileNum());
		editHouseSeqTF.setText(""+custRow.getHouseSeq());
		editOldHouseNumTF.setText(custRow.getOldHouseNum());
		editNewHouseNumTF.setText(custRow.getNewHouseNum());
		editAddrLine1.setText(custRow.getAddrLine1());
		editAddrLine2.setText(custRow.getAddrLine2());
		editLocalityTF.setText(custRow.getLocality());
		editCityTF.setText(custRow.getCity());
		editProfile1TF.setText(custRow.getProfile1());
		editProfile2TF.setText(custRow.getProfile2());
		editProfile3TF.setText(custRow.getProfile3());
		initialsTF.setText(custRow.getInitials());
	}
	
	private void populateLineNumbersForHawkerCode(String hawkerCode) {
		// TODO Auto-generated method stub
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			hawkerLineNumData.clear();
			PreparedStatement stmt = con.prepareStatement("select distinct line_num from line_info where hawker_id = ?");
			stmt.setLong(1, hawkerIdForCode(hawkerCode));
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				hawkerLineNumData.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private long hawkerIdForCode(String hawkerCode) {
		// TODO Auto-generated method stub
		long hawkerId=-1;
		Connection con = Main.dbConnection;
		try {
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			PreparedStatement hawkerIdStatement = null;
			String hawkerIdQuery = "select hawker_id from hawker_info where hawker_code = ?";
			hawkerIdStatement = con.prepareStatement(hawkerIdQuery);
			hawkerIdStatement.setString(1, hawkerCode);
			ResultSet hawkerIdRs = hawkerIdStatement.executeQuery();
			
			if(hawkerIdRs.next()){
				hawkerId=hawkerIdRs.getLong(1);
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hawkerId;
	}
	
	
	private void populateHawkerCodes() {
		// TODO Auto-generated method stub
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			hawkerCodeData.clear();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select distinct hawker_code from hawker_info");
			while(rs.next()){
				hawkerCodeData.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Customer returnUpdatedCustomer(){
		
		Customer edittedCustomer = new Customer(custRow);
		edittedCustomer.setCustomerCode(Long.parseLong(editCustomerCodeTF.getText()));
        edittedCustomer.setName(editNameTF.getText());
        edittedCustomer.setMobileNum(editMobileNumTF.getText());
        edittedCustomer.setHawkerCode(editHawkerCodeLOV.getSelectionModel().getSelectedItem());
        edittedCustomer.setLineNum(Integer.parseInt(editLineNumLOV.getSelectionModel().getSelectedItem()));
        edittedCustomer.setHouseSeq(Integer.parseInt(editHouseSeqTF.getText()));
        edittedCustomer.setOldHouseNum(editOldHouseNumTF.getText());
        edittedCustomer.setNewHouseNum(editNewHouseNumTF.getText());
        edittedCustomer.setAddrLine1(editAddrLine1.getText());
        edittedCustomer.setAddrLine2(editAddrLine2.getText());
        edittedCustomer.setLocality(editLocalityTF.getText());
        edittedCustomer.setCity(editCityTF.getText());
        edittedCustomer.setState(editStateLOV.getSelectionModel().getSelectedItem());
        edittedCustomer.setProfile1(editProfile1TF.getText());
        edittedCustomer.setProfile2(editProfile2TF.getText());
        edittedCustomer.setProfile3(editProfile3TF.getText());
        edittedCustomer.setInitials(initialsTF.getText());
        edittedCustomer.updateCustomerRecord();
    	return edittedCustomer;
	}
	

}
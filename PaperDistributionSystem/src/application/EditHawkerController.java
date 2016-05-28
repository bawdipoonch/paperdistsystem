package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class EditHawkerController implements Initializable {

	private Hawker hawkerRow;
	@FXML
	public GridPane gridPane;

	// Edit Customer Fields FXML
	@FXML
	private TextField editNameTF;
	@FXML
	private TextField editHawkerCodeTF;
	@FXML
	private TextField editMobileNumTF;
	@FXML
	private TextField editAgencyNameTF;
	@FXML
	private TextField editOldHouseNumTF;
	@FXML
	private TextField editNewHouseNumTF;
	@FXML
	private TextField editBldgStreetTF;
	@FXML
	private TextField editAddrLine1;
	@FXML
	private TextField editAddrLine2;
	@FXML
	private TextField editLocalityTF;
	@FXML
	private TextField editCityTF;
	@FXML
	private TextField editFeeTF;
	@FXML
	private CheckBox editActiveCheck;
	@FXML
	private ComboBox<String> editStateLOV;
	@FXML
	private ComboBox<String> editProfile1TF;
	@FXML
	private ComboBox<String> editProfile2TF;
	@FXML
	private TextField editProfile3TF;
	@FXML
	private TextField initialsTF;
	@FXML
	private ComboBox<String> editEmploymentLOV;
	@FXML
	private ComboBox<String> editPointNameLOV;
	@FXML
	private TextField editCommentsTF;
	@FXML
	private TextField editBankAcNo;
	@FXML
	private TextField editBankName;
	@FXML
	private TextField editIfscCode;

	private ObservableList<String> employmentData = FXCollections.observableArrayList();
	private ObservableList<String> profileValues = FXCollections.observableArrayList();
	private ObservableList<String> pointNameValues = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// setupBindings();

	}

	public void setHawkerToEdit(Hawker hawker) {
		this.hawkerRow = hawker;
	}

	public void setupBindings() {
		editStateLOV.getItems().addAll("Tamil Nadu", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
				"Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand",
				"Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
				"Odisha", "Punjab", "Rajasthan", "Sikkim", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand",
				"West Bengal");
		editStateLOV.getSelectionModel().select(hawkerRow.getState());

		populateEmploymentValues();
		populateProfileValues();
		populatePointNames();
		editNameTF.setText(hawkerRow.getName());
		editHawkerCodeTF.setText(hawkerRow.getHawkerCode());
		editMobileNumTF.setText(hawkerRow.getMobileNum());
		editOldHouseNumTF.setText(hawkerRow.getOldHouseNum());
		editNewHouseNumTF.setText(hawkerRow.getNewHouseNum());
		editAddrLine1.setText(hawkerRow.getAddrLine1());
		editAddrLine2.setText(hawkerRow.getAddrLine2());
		editLocalityTF.setText(hawkerRow.getLocality());
		editCityTF.setText(hawkerRow.getCity());
		editAgencyNameTF.setText(hawkerRow.getAgencyName());
		editFeeTF.setText("" + hawkerRow.getFee());
		editActiveCheck.setSelected(hawkerRow.getActiveFlag());
		editProfile3TF.setText(hawkerRow.getProfile3());
		initialsTF.setText(hawkerRow.getInitials());
		initialsTF.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (newValue.length() > 3)
					initialsTF.setText(oldValue);
			}
		});

		editProfile1TF.getItems().addAll(profileValues);
		editProfile2TF.getItems().addAll(profileValues);
		editProfile1TF.getSelectionModel().select(hawkerRow.getProfile1());
		editProfile2TF.getSelectionModel().select(hawkerRow.getProfile2());
		editProfile3TF.setText(hawkerRow.getProfile3());
		editEmploymentLOV.getItems().addAll(employmentData);
		editEmploymentLOV.getSelectionModel().select(hawkerRow.getEmployment());
		editCommentsTF.setText(hawkerRow.getComments());
		editPointNameLOV.getItems().addAll(pointNameValues);
		editPointNameLOV.getSelectionModel().select(hawkerRow.getPointName());
		editNameTF.requestFocus();
		editBankAcNo.setText(hawkerRow.getBankAcNo());
		editBankName.setText(hawkerRow.getBankName());
		editIfscCode.setText(hawkerRow.getIfscCode());
		// editMobileNumTF.textProperty().addListener(new
		// ChangeListener<String>() {
		//
		// @Override
		// public void changed(ObservableValue<? extends String> observable,
		// String oldValue, String newValue) {
		//
		// if (newValue.length() > 10){
		// editMobileNumTF.setText(oldValue);
		//
		// Notifications.create().title("Invalid mobile number")
		// .text("Mobile number should only contain 10 DIGITS")
		// .hideAfter(Duration.seconds(5)).showError();
		// }
		// try {
		// Integer.parseInt(newValue);
		// } catch (NumberFormatException e) {
		// editMobileNumTF.setText(oldValue);
		//
		// Notifications.create().title("Invalid mobile number")
		// .text("Mobile number should only contain 10 DIGITS")
		// .hideAfter(Duration.seconds(5)).showError();
		// Main._logger.debug("Error :",e); e.printStackTrace();
		// }
		// }
		// });
	}

	public boolean isValid() {
		boolean validate = true;
		if (hawkerCodeExists(editHawkerCodeTF.getText())) {
			Notifications.create().title("Hawker already exists")
					.text("Hawker with same Hawker Code alraedy exists. Please choose different hawker code.")
					.hideAfter(Duration.seconds(5)).showError();
			validate = false;
		}
		if (mobileNumExists(editMobileNumTF.getText())) {
			Notifications.create().title("Mobile already exists")
					.text("Hawker with same Mobile Number alraedy exists. Please enter a different value.")
					.hideAfter(Duration.seconds(5)).showError();
			validate = false;
		}
		if (editNameTF.getText() == null) {
			validate = false;
			Notifications.create().hideAfter(Duration.seconds(5)).title("Hawker not selected")
					.text("Please select a hawker before adding the the customer").showError();
			try {
				Double.parseDouble(editFeeTF.getText());
			} catch (NumberFormatException e) {
				validate = false;
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid fee")
						.text("Fee per subscription should not be empty and must be numeric only").showError();
				Main._logger.debug("Error :",e);
				e.printStackTrace();
			}
		}
		if (editProfile3TF.getText() != null && checkExistingProfileValue(editProfile3TF.getText())) {
			validate = false;
			Notifications.create().hideAfter(Duration.seconds(5)).title("Profile 3 already exists")
					.text("Value for Profile 3 already exists, please select this in Profile 1 or Profile 2 field.")
					.showError();
		}

		if (editMobileNumTF.getText().length() != 10) {
			Notifications.create().title("Invalid mobile number").text("Mobile number should only contain 10 DIGITS")
					.hideAfter(Duration.seconds(5)).showError();
			validate = false;
		}
		if (editPointNameLOV.getSelectionModel().getSelectedItem() == null) {
			Notifications.create().title("Invalid point name").text("Point Name must be selected to create hawker")
					.hideAfter(Duration.seconds(5)).showError();
			validate = false;
		}
		try {
			Integer.parseInt(editMobileNumTF.getText());
		} catch (NumberFormatException e) {
			Notifications.create().title("Invalid mobile number").text("Mobile number should only contain 10 DIGITS")
					.hideAfter(Duration.seconds(5)).showError();
			validate = false;
			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return validate;
	}

	private boolean hawkerCodeExists(String hawkerCode) {

		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select count(*) from hawker_info where lower(hawker_code) = ? and hawker_id<>?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, hawkerCode.toLowerCase());
			stmt.setLong(1, hawkerRow.getHawkerId());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) > 0)
					return true;
			}
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return false;
	}

	private boolean checkExistingProfileValue(String profileValue) {
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			PreparedStatement stmt = con
					.prepareStatement("select value from lov_lookup where code = 'PROFILE_VALUES' AND lower(VALUE)=?");
			stmt.setString(1, profileValue.toLowerCase());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return false;
	}

	public Hawker returnUpdatedHawker() {
		if (isValid()) {
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
			edittedHawker.setProfile1(editProfile1TF.getSelectionModel().getSelectedItem());
			edittedHawker.setProfile2(editProfile2TF.getSelectionModel().getSelectedItem());
			edittedHawker.setProfile3(editProfile3TF.getText());
			edittedHawker.setInitials(initialsTF.getText());
			edittedHawker.setPointName(editPointNameLOV.getSelectionModel().getSelectedItem());
			edittedHawker.setEmployment(editEmploymentLOV.getSelectionModel().getSelectedItem());
			edittedHawker.setComments(editCommentsTF.getText());
			edittedHawker.setBankAcNo(editBankAcNo.getText());
			edittedHawker.setBankName(editBankName.getText());
			edittedHawker.setIfscCode(editIfscCode.getText());
			edittedHawker.updateHawkerRecord();
			return edittedHawker;
		} else
			return null;
	}

	private void populateProfileValues() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					if (!con.isValid(0)) {
						con = Main.reconnect();
					}
					profileValues.clear();
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery(
							"select value, code, seq, lov_lookup_id from lov_lookup where code='PROFILE_VALUES' order by seq");
					while (rs.next()) {
						profileValues.add(rs.getString(1));
					}

				} catch (SQLException e) {

					Main._logger.debug("Error :",e);
					e.printStackTrace();
				} catch (Exception e) {

					Main._logger.debug("Error :",e);
					e.printStackTrace();
				}
				return null;
			}

		};
		new Thread(task).start();
	}

	private void populateEmploymentValues() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					if (!con.isValid(0)) {
						con = Main.reconnect();
					}
					employmentData.clear();
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery(
							"select value, code, seq, lov_lookup_id from lov_lookup where code='EMPLOYMENT_STATUS' order by seq");
					while (rs.next()) {
						employmentData.add(rs.getString(1));
					}

				} catch (SQLException e) {

					Main._logger.debug("Error :",e);
					e.printStackTrace();
				} catch (Exception e) {

					Main._logger.debug("Error :",e);
					e.printStackTrace();
				}
				return null;
			}

		};
		new Thread(task).start();
	}

	public void populatePointNames() {
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			pointNameValues.clear();
			PreparedStatement stmt = con.prepareStatement("select distinct name from point_name order by name");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				pointNameValues.add(rs.getString(1));
			}
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}

	}

	private boolean mobileNumExists(String mobileNum) {
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select hawker_code,name from hawker_info where mobile_num=? and hawker_code <> ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, mobileNum);
			stmt.setString(2, editHawkerCodeTF.getText());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return false;
	}

	public void releaseVariables() {

		pointNameValues = null;
		employmentData = null;
		profileValues = null;
	}
}

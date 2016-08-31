package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

public class HawkerProfileController implements Initializable{
	private Hawker hawkerRow = HawkerLoginController.loggedInHawker;

	@FXML
    private Label adminAgencyName;

    @FXML
    private Label adminMobileLabel;

    @FXML
    private Label adminAddrLabel;

    @FXML
    private GridPane gridPane;

    @FXML
    private Label editPointNameLOV;

    @FXML
    private Label editNameTF;

    @FXML
    private Label initialsTF;

    @FXML
    private Label editHawkerCodeTF;

    @FXML
    private Label editMobileNumTF;

    @FXML
    private Label editAgencyNameTF;

    @FXML
    private Label editFeeTF;

    @FXML
    private Label editOldHouseNumTF;

    @FXML
    private Label editNewHouseNumTF;

    @FXML
    private Label editBldgStreetTF;

    @FXML
    private CheckBox editActiveCheck;

    @FXML
    private Label editAddrLine1;

    @FXML
    private Label editAddrLine2;

    @FXML
    private Label editLocalityTF;

    @FXML
    private Label editCityTF;

    @FXML
    private Label editStateLOV;

    @FXML
    private Label editProfile1TF;

    @FXML
    private Label editProfile2TF;

    @FXML
    private Label editProfile3TF;

    @FXML
    private Label editEmploymentLOV;

    @FXML
    private Label editCommentsTF;

    @FXML
    private Label editBankAcNo;

    @FXML
    private Label editBankName;

    @FXML
    private Label editIfscCode;
    
    private ObservableList<String> employmentData = FXCollections.observableArrayList();
	private ObservableList<String> profileValues = FXCollections.observableArrayList();
	private ObservableList<String> pointNameValues = FXCollections.observableArrayList();
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setupBindings();
		
	}


	private void setupBindings() {
//		populateEmploymentValues();
//		populateProfileValues();
//		populatePointNames();
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
		if(HawkerLoginController.loggedInHawker!=null)
			editFeeTF.setDisable(true);
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

		editProfile1TF.setText(hawkerRow.getProfile1());
		editProfile2TF.setText(hawkerRow.getProfile2());
		editProfile3TF.setText(hawkerRow.getProfile3());
		editEmploymentLOV.setText(hawkerRow.getEmployment());
		editCommentsTF.setText(hawkerRow.getComments());
		editPointNameLOV.setText(hawkerRow.getPointName());
		editNameTF.requestFocus();
		editBankAcNo.setText(hawkerRow.getBankAcNo());
		editBankName.setText(hawkerRow.getBankName());
		editIfscCode.setText(hawkerRow.getIfscCode());
		
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
					profileValues = FXCollections.observableArrayList();
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery(
							"select value, code, seq, lov_lookup_id from lov_lookup where code='PROFILE_VALUES' order by seq");
					while (rs.next()) {
						if (profileValues != null && !profileValues.contains(rs.getString(1)))
							profileValues.add(rs.getString(1));
					}

				} catch (SQLException e) {

					Main._logger.debug("Error :", e);
					e.printStackTrace();
				} catch (Exception e) {

					Main._logger.debug("Error :", e);
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
					employmentData = FXCollections.observableArrayList();
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery(
							"select value, code, seq, lov_lookup_id from lov_lookup where code='EMPLOYMENT_STATUS' order by seq");
					while (rs.next()) {
						if (employmentData != null && !employmentData.contains(rs.getString(1)))
							employmentData.add(rs.getString(1));
					}

				} catch (SQLException e) {

					Main._logger.debug("Error :", e);
					e.printStackTrace();
				} catch (Exception e) {

					Main._logger.debug("Error :", e);
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
			pointNameValues = FXCollections.observableArrayList();
			PreparedStatement stmt = con.prepareStatement("select distinct name from point_name order by name");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (pointNameValues != null && !pointNameValues.contains(rs.getString(1)))
					pointNameValues.add(rs.getString(1));
			}
		} catch (SQLException e) {

			Main._logger.debug("Error :", e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :", e);
			e.printStackTrace();
		}

	}
	
	@FXML
	private void changePasswordClicked(ActionEvent evt) {
		TextInputDialog changePwdDialog = new TextInputDialog();
		changePwdDialog.setTitle("Change password");
		changePwdDialog.setHeaderText("Please enter the new password. \nPassword must be atleast 5 characters long.");
		// changePwdDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK,
		// ButtonType.CANCEL);
		final Button btOk = (Button) changePwdDialog.getDialogPane().lookupButton(ButtonType.OK);
		btOk.addEventFilter(ActionEvent.ACTION, event -> {
			if (changePwdDialog.getEditor().getText().isEmpty() || changePwdDialog.getEditor().getText().length() < 5) {
				Notifications.create().title("Empty password")
						.text("Password cannot be left empty and must be more than 5 characters. Try again.")
						.hideAfter(Duration.seconds(5)).showError();
				event.consume();
			}
		});
		Optional<String> result = changePwdDialog.showAndWait();
		if (result.isPresent()) {
			// changeHawkerPwd(hawkerRow,result.get());
			HawkerLoginController.loggedInHawker.setPassword(result.get());
			HawkerLoginController.loggedInHawker.updateHawkerRecord();
			Notifications.create().title("Password updated").text("Password was successfully updated")
					.hideAfter(Duration.seconds(5)).showInformation();
		}
	}

	@FXML
	private void changeMobileClicked(ActionEvent evt) {
		Notifications.create().title("Please login again")
		.text("If you update Hawker profile, then please logout and login again to see changes.")
		.hideAfter(Duration.seconds(5)).showInformation();
		AHawkerInfoTabController.showEditHawkerDialog(HawkerLoginController.loggedInHawker);

		
	}

	private void populateAdminHeaders() {

		HashMap<String, String> adminMap = Main.getAdminDetails();
		adminAgencyName.setText(adminMap.get("name"));
		adminMobileLabel.setText(adminMap.get("mobile"));
		adminAddrLabel.setText(adminMap.get("addr"));

	}
	
	public void reloadData(){
		setupBindings();
		populateAdminHeaders();
	}

	public void releaseVariables() {

		pointNameValues = null;
		employmentData = null;
		profileValues = null;
	}
}

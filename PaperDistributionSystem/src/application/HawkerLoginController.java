package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.controlsfx.control.Notifications;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HawkerLoginController implements Initializable {

	@FXML
	private Button loginButton;
	@FXML
	private Button adminLoginButton;
	@FXML
	private TextField mobileNum;
	@FXML
	private TextField password;
	@FXML
	Button registerButton;
	// Stage stage;
	Parent root;

	public static Hawker loggedInHawker;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		/*
		 * AmazonRDSClient rdsClient = new AmazonRDSClient(new
		 * BasicAWSCredentials(ACCESS_KEY, SECRET)); DescribeDBInstancesResult
		 * dbinstancesresult = rdsClient.describeDBInstances(); List<DBInstance>
		 * dbInstanceList = dbinstancesresult.getDBInstances(); if
		 * (!dbInstanceList.isEmpty()) { DBInstance dbInstance =
		 * dbInstanceList.get(0); Endpoint endPoint = dbInstance.getEndpoint();
		 * 
		 * 
		 * }
		 */

		password.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				if (event.getCode() == KeyCode.ENTER) {
					loginClicked(new ActionEvent());
				}
			}
		});

		mobileNum.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				if (event.getCode() == KeyCode.ENTER) {
					loginClicked(new ActionEvent());
				}
			}
		});
		adminLoginButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				if (event.getCode() == KeyCode.ENTER) {
					adminLoginClicked(new ActionEvent());
				}
			}
		});
		loginButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				if (event.getCode() == KeyCode.ENTER) {
					loginClicked(new ActionEvent());
				}
			}
		});

		registerButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {

				if (event.getCode() == KeyCode.ENTER) {
					addHawkerExtraClicked(new ActionEvent());
				}
			}
		});

	}

	@FXML
	public void loginClicked(ActionEvent event) {

		System.out.println("Hawker Login button clicked");

		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}

			PreparedStatement existsStmt = con
					.prepareStatement("select hawker_code from hawker_info where mobile_num = ? and password=?");
			existsStmt.setString(1, mobileNum.getText());
			existsStmt.setString(2, password.getText());
			if (existsStmt.executeQuery().next()) {
				PreparedStatement stmt = con.prepareStatement(
						"select hawker_id,name,hawker_code, mobile_num, agency_name, active_flag, fee, old_house_num, new_house_num, addr_line1, addr_line2, locality, city, state,customer_access, billing_access, line_info_access, line_dist_access, paused_cust_access, product_access, reports_access,profile1,profile2,profile3,initials,password, employment, comments, point_name, building_street,bank_ac_no,bank_name,ifsc_code,stop_history_access  from hawker_info where mobile_num = ?");
				stmt.setString(1, mobileNum.getText());
				ResultSet rs = stmt.executeQuery();
				if (rs.next()) {
					if (rs.getString(6).equalsIgnoreCase("Y")) {
						loggedInHawker = new Hawker(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
								rs.getString(5), rs.getString(6).equalsIgnoreCase("Y"), rs.getDouble(7),
								rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),
								rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16),
								rs.getString(17), rs.getString(18), rs.getString(19), rs.getString(20),
								rs.getString(21), rs.getString(22), rs.getString(23), rs.getString(24),
								rs.getString(25), rs.getString(26), rs.getString(27), rs.getString(28),
								rs.getString(29), rs.getString(30), rs.getString(31), rs.getString(32),
								rs.getString(33), rs.getString(34));
						Notifications.create().hideAfter(Duration.seconds(5)).title("Logged in")
								.text("Login successful").showInformation();
						// stage = (Stage) loginButton.getScene().getWindow();
						// load up OTHER FXML document
						root = FXMLLoader.load(getClass().getResource("HawkerHome.fxml"));
						Scene scene = new Scene(root);
						Main.primaryStage.setScene(scene);
						Main.primaryStage.setMaximized(true);
						Main.primaryStage.show();
					} else {
						Notifications.create().hideAfter(Duration.seconds(5)).title("Inactive Hawker")
								.text("Hawker with given mobile number is not activated yet. Please contact administrator.")
								.showError();
					}
				}
			} else {
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid login details")
						.text("Invalid mobile number or password").showError();
			}

		} catch (SQLException e) {

			Main._logger.debug(e.getStackTrace());
			e.printStackTrace();
		} catch (IOException e) {

			Main._logger.debug(e.getStackTrace());
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug(e.getStackTrace());
			e.printStackTrace();
		}

	}

	@FXML
	public void adminLoginClicked(ActionEvent event) {
		try {
			// get reference to the button's stage
			// stage = (Stage) adminLoginButton.getScene().getWindow();
			// load up OTHER FXML document
			root = FXMLLoader.load(getClass().getResource("AdminLogin.fxml"));
			/*
			 * else{ stage=(Stage) btn2.getScene().getWindow(); root =
			 * FXMLLoader.load(getClass().getResource("FXMLDocument.fxml")); }
			 */
			// create a new scene with root and set the stage
			Scene scene = new Scene(root);
			Main.primaryStage.setScene(scene);
			Main.primaryStage.setMaximized(true);
			Main.primaryStage.show();
		} catch (Exception e) {

			Main._logger.debug(e.getStackTrace());
			e.printStackTrace();
		}
	}

	@FXML
	private void addHawkerExtraClicked(ActionEvent event) {
		try {

			Dialog<String> addHawkerDialog = new Dialog<String>();
			addHawkerDialog.setTitle("Add new hawker");
			addHawkerDialog.setHeaderText("Add new Hawker data below.");

			// Set the button types.
			ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
			addHawkerDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CLOSE);
			Button saveButton = (Button) addHawkerDialog.getDialogPane().lookupButton(saveButtonType);
			FXMLLoader addHawkerLoader = new FXMLLoader(getClass().getResource("AddHawkersExtraScreen.fxml"));
			Parent addHawkerGrid = (Parent) addHawkerLoader.load();
			AddHawkerExtraScreenController addHwkController = addHawkerLoader
					.<AddHawkerExtraScreenController> getController();

			saveButton.addEventFilter(ActionEvent.ACTION, btnEvent -> {
				if (addHwkController.isValid()) {
					addHwkController.addHawker();
					Notifications.create().hideAfter(Duration.seconds(5)).title("Hawker created")
							.text("Hawker created successfully. You can now try to login").showInformation();
				} else {
					btnEvent.consume();
				}
			});

			/*
			 * saveButton.addEventFilter(ActionEvent.ACTION, btnEvent -> { if
			 * (addHwkController.isValid()) { addHwkController.addHawker();
			 * Notifications.create().hideAfter(Duration.seconds(5)).title(
			 * "Hawker created") .text(
			 * "Hawker created successfully. You can now try to login"
			 * ).showInformation(); } else { btnEvent.consume(); }
			 * 
			 * });
			 */
			addHawkerDialog.getDialogPane().setContent(addHawkerGrid);
			addHwkController.setupBindings();

			addHawkerDialog.setResultConverter(dialogButton -> {
				if (dialogButton != saveButtonType) {
					return null;
				}
				return null;
			});

			Optional<String> updatedHawker = addHawkerDialog.showAndWait();
			// refreshHawkerTable();

			updatedHawker.ifPresent(new Consumer<String>() {

				@Override
				public void accept(String t) {

					// addHawkerDialog.showAndWait();
				}
			});

		} catch (IOException e) {

			Main._logger.debug(e.getStackTrace());
			e.printStackTrace();
		}
	}

}

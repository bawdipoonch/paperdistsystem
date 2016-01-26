package application;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.Endpoint;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.*;

public class HawkerLoginController implements Initializable {

	@FXML
	private Button loginButton;
	@FXML
	private Button adminLoginButton;
	@FXML
	private TextField mobileNum;
	@FXML private TextField password;

	Stage stage;
	Parent root;

    public static Hawker loggedInHawker;
	

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		/*AmazonRDSClient rdsClient = new AmazonRDSClient(new BasicAWSCredentials(ACCESS_KEY, SECRET));
		DescribeDBInstancesResult dbinstancesresult = rdsClient.describeDBInstances();
		List<DBInstance> dbInstanceList = dbinstancesresult.getDBInstances();
		if (!dbInstanceList.isEmpty()) {
			DBInstance dbInstance = dbInstanceList.get(0);
			Endpoint endPoint = dbInstance.getEndpoint();
			

		}*/
		
		

	}

	@FXML
	public void loginClicked(ActionEvent event) throws IOException {

		System.out.println("Hawker Login button clicked");
		
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			
			PreparedStatement existsStmt = con.prepareStatement("select hawker_code from hawker_info where mobile_num = ? and password=?");
			existsStmt.setString(1, mobileNum.getText());
			existsStmt.setString(2, password.getText());
			if(existsStmt.executeQuery().next()){
				PreparedStatement stmt = con.prepareStatement("select hawker_id,name,hawker_code, mobile_num, agency_name, active_flag, fee, old_house_num, new_house_num, addr_line1, addr_line2, locality, city, state,customer_access, billing_access, line_info_access, line_dist_access, paused_cust_access, product_access, reports_access,profile1,profile2,profile3,initials  from hawker_info where mobile_num = ?");
				stmt.setString(1, mobileNum.getText());
				ResultSet rs = stmt.executeQuery();
				if(rs.next()){
					if (rs.getString(6).equalsIgnoreCase("Y")) {
						loggedInHawker = new Hawker(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
								rs.getString(5), rs.getString(6).equalsIgnoreCase("Y"), rs.getDouble(7),
								rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),
								rs.getString(13), rs.getString(14), rs.getString(15), rs.getString(16),
								rs.getString(17), rs.getString(18), rs.getString(19), rs.getString(20),
								rs.getString(21), rs.getString(22), rs.getString(23), rs.getString(24),rs.getString(25));
						Notifications.create().hideAfter(Duration.seconds(5)).title("Logged in").text("Login successful").showInformation();
						stage = (Stage) loginButton.getScene().getWindow();
						// load up OTHER FXML document
						root = FXMLLoader.load(getClass().getResource("HawkerHome.fxml"));
						Scene scene = new Scene(root);
						stage.setScene(scene);
						stage.show();
					}
					else
					{
						Notifications.create().hideAfter(Duration.seconds(5)).title("Inactive Hawker").text("Hawker with given mobile number is not activated yet. Please contact administrator.").showError();
					}
				}
			}
			else {
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid login details").text("Invalid mobile number or password").showError();
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		

	}

	@FXML
	public void adminLoginClicked(ActionEvent event) throws IOException {
		if (event.getSource() == adminLoginButton) {
			// get reference to the button's stage
			stage = (Stage) adminLoginButton.getScene().getWindow();
			// load up OTHER FXML document
			root = FXMLLoader.load(getClass().getResource("AdminLogin.fxml"));
		}
		/*
		 * else{ stage=(Stage) btn2.getScene().getWindow(); root =
		 * FXMLLoader.load(getClass().getResource("FXMLDocument.fxml")); }
		 */
		// create a new scene with root and set the stage
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

}

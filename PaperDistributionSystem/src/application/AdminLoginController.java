package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminLoginController implements Initializable {
	
	@FXML
	private Button adminLoginButton;
	@FXML
	private TextField adminUsername;
	@FXML
	private PasswordField adminPassword;
	@FXML private Button backButton;
	
	Stage stage; 
    Parent root;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		adminUsername.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getCode()==KeyCode.ENTER){
					loginClicked(new ActionEvent());
				}
			}
		});
		
		adminPassword.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				// TODO Auto-generated method stub
				if(event.getCode()==KeyCode.ENTER){
					loginClicked(new ActionEvent());
				}
			}
		});
	}
	
	@FXML
	private void loginClicked(ActionEvent event) {
		System.out.println("Admin Login button clicked");
		
try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			
			PreparedStatement existsStmt = con.prepareStatement("select username,password from admin_login where username = ? and password=?");
			existsStmt.setString(1, adminUsername.getText());
			existsStmt.setString(2, adminPassword.getText());
			if(existsStmt.executeQuery().next()){
				stage=(Stage) adminLoginButton.getScene().getWindow();
		        //load up OTHER FXML document
				root = FXMLLoader.load(getClass().getResource("AdminHome.fxml"));
				
				Scene scene = new Scene(root);
			      stage.setScene(scene);
			      stage.show();
			}
			else {
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid login details").text("Invalid Administrator username or password").showError();
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

	@FXML
	private void backButtonClicked(ActionEvent event) throws IOException {

		stage=(Stage) adminLoginButton.getScene().getWindow();
        //load up OTHER FXML document
		root = FXMLLoader.load(getClass().getResource("HawkerLogin.fxml"));
		
		Scene scene = new Scene(root);
	      stage.setScene(scene);
	      stage.show();
	}
	
}

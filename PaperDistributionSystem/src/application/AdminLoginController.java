package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

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

	}
	
	@FXML
	private void loginClicked(ActionEvent event) throws IOException {
		System.out.println("Admin Login button clicked");
		
		stage=(Stage) adminLoginButton.getScene().getWindow();
        //load up OTHER FXML document
		root = FXMLLoader.load(getClass().getResource("AdminHome.fxml"));
		
		Scene scene = new Scene(root);
	      stage.setScene(scene);
	      stage.show();
		
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

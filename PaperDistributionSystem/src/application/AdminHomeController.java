package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class AdminHomeController implements Initializable {

	@FXML private Button logoutButton;	
	Stage stage; 
    Parent root;
    @FXML private TabPane tabPane;


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

		tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				// TODO Auto-generated method stub
				System.out.println("Tab changed from "+oldValue.getText()+" to "+newValue.getText());
				
			}
		});
	}
	
	@FXML
	private void logoutClicked(ActionEvent event) throws IOException {
		System.out.println("Logout clicked");
		stage=(Stage) logoutButton.getScene().getWindow();
        //load up OTHER FXML document
		root = FXMLLoader.load(getClass().getResource("HawkerLogin.fxml"));
		
		Scene scene = new Scene(root);
	      stage.setScene(scene);
	      stage.show();
	}
	
	@FXML
	private void customersTabSelected(ActionEvent event) throws IOException {
		
	}
	
	@FXML
	private void lineInfoTabSelected(ActionEvent event) throws IOException {
		
	}
	
	@FXML
	private void pausedCustTabSelected(ActionEvent event) throws IOException {
		
	}
	
	@FXML
	private void hawkerInfoTabSelected(ActionEvent event) throws IOException {
		
	}

}

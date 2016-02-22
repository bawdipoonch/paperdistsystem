package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminHomeController implements Initializable {

	@FXML private Button logoutButton;	
	Stage stage; 
    Parent root;
    @FXML private TabPane tabPane;
	@FXML private Button changePwdButton;
    
    @FXML private Tab customersTab;
    @FXML private Tab lineInfoTab;
    @FXML private Tab pausedCustTab;
    @FXML private Tab hawkerTab;
    @FXML private Tab lineDistTab;
    @FXML private Tab additionalItemsTab;
    @FXML private Tab productsTab;

    private ACustomerInfoTabController customerTabController;
    private AHawkerInfoTabController hawkerTabController;
    private ALineInfoTabController lineInfoTabController;
    private APausedCustomerTabController pausedCustTabController;
    private ALineDistributorTabController lineDistTabController;
    private AdditionalItemsController additionalItemsTabController;
    private AProductsTabController productsTabController;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

		loadTabs();
		
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
	
	
	private void loadTabs(){
		tabPane.getTabs().clear();
		try {
			FXMLLoader customerTabLoader = new FXMLLoader(getClass().getResource("A-CustomersTab.fxml"));
			Parent custroot = (Parent)customerTabLoader.load();
			customerTabController = customerTabLoader.<ACustomerInfoTabController>getController();
			customersTab = new Tab();
			customersTab.setText("Customers");
			customersTab.setContent(custroot);
			customersTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					customerTabController.reloadData();
				}
			});
			
			hawkerTab = new Tab();
			FXMLLoader hawkerTabLoader = new FXMLLoader(getClass().getResource("A-HawkerInfoTab.fxml"));
			Parent hawkerroot = (Parent)hawkerTabLoader.load();
			hawkerTabController = hawkerTabLoader.<AHawkerInfoTabController>getController();
			hawkerTab.setText("Hawkers");
			hawkerTab.setContent(hawkerroot);
			hawkerTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					hawkerTabController.reloadData();
				}
			});
			
			lineInfoTab = new Tab();
			FXMLLoader lineInfoTabLoader = new FXMLLoader(getClass().getResource("A-LineInfoTab.fxml"));
			Parent lineinforoot = (Parent)lineInfoTabLoader.load();
			lineInfoTabController = lineInfoTabLoader.<ALineInfoTabController>getController();
			lineInfoTab.setText("Line Information");
			lineInfoTab.setContent(lineinforoot);
			
			lineInfoTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					lineInfoTabController.reloadData();
				}
			});
			
			lineDistTab = new Tab();
			FXMLLoader lineDistTabLoader = new FXMLLoader(getClass().getResource("A-LineDistributorTab.fxml"));
			Parent linedistroot = (Parent)lineDistTabLoader.load();
			lineDistTabController = lineDistTabLoader.<ALineDistributorTabController>getController();
			lineDistTab.setText("Line Distribution Boy");
			lineDistTab.setContent(linedistroot);
			lineDistTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					lineDistTabController.reloadData();
				}
			});
			
			additionalItemsTab = new Tab();
			FXMLLoader additionalItemsTabLoader = new FXMLLoader(getClass().getResource("AdditionalItems.fxml"));
			Parent additionalItemsRoot = (Parent)additionalItemsTabLoader.load();
			additionalItemsTabController = additionalItemsTabLoader.<AdditionalItemsController>getController();
			additionalItemsTab.setText("Additional Items");
			additionalItemsTab.setContent(additionalItemsRoot);
			additionalItemsTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					additionalItemsTabController.reloadData();
				}
			});
			
			productsTab = new Tab();
			FXMLLoader productsTabLoader = new FXMLLoader(getClass().getResource("AProductsTab.fxml"));
			Parent productsRoot = (Parent)productsTabLoader.load();
			productsTabController = productsTabLoader.<AProductsTabController>getController();
			productsTab.setText("Products");
			productsTab.setContent(productsRoot);
			productsTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					productsTabController.reloadData();
				}
			});
			
			pausedCustTab = new Tab();
			FXMLLoader pausedCustTabLoader = new FXMLLoader(getClass().getResource("A-PausedCustomersTab.fxml"));
			Parent pausedcustroot = (Parent)pausedCustTabLoader.load();
			pausedCustTabController = pausedCustTabLoader.<APausedCustomerTabController>getController();
			pausedCustTab.setText("Paused Customers");
			pausedCustTab.setContent(pausedcustroot);
			pausedCustTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					// TODO Auto-generated method stub
					pausedCustTabController.reloadData();
				}
			});
			
			tabPane.getTabs().addAll(hawkerTab,customersTab, lineInfoTab,lineDistTab, productsTab, additionalItemsTab, pausedCustTab);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FXML private void refreshClicked(ActionEvent event){
//		ACustomerInfoTabController
		customerTabController.reloadData();
		hawkerTabController.reloadData();
		lineInfoTabController.reloadData();
		lineDistTabController.reloadData();
//		pausedCustTabController.reloadData();
	}
	@FXML private void changePasswordClicked(ActionEvent evt){
		TextInputDialog changePwdDialog = new TextInputDialog();
		changePwdDialog.setTitle("Change password");
		changePwdDialog.setHeaderText("Please enter the new password. \nPassword must be atleast 5 characters long.");
//		changePwdDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		final Button btOk = (Button) changePwdDialog.getDialogPane().lookupButton(ButtonType.OK);
		 btOk.addEventFilter(ActionEvent.ACTION, event -> {
			 if (changePwdDialog.getEditor().getText().isEmpty() || changePwdDialog.getEditor().getText().length()<5) {
		         Notifications.create().title("Empty password").text("Password cannot be left empty and must be more than 5 characters. Try again.").hideAfter(Duration.seconds(5)).showError();
		    	 event.consume();
		     }
		 });
		Optional<String> result = changePwdDialog.showAndWait();
		 if (result.isPresent()) {
			 try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					String deleteString = "update admin_login set password =? where username='admin'";
					PreparedStatement deleteStmt = con.prepareStatement(deleteString);
					deleteStmt.setString(1, changePwdDialog.getEditor().getText());

					deleteStmt.executeUpdate();
					con.commit();

					Notifications.create().title("Password updated").text("Password was successfully updated. Please login again!").hideAfter(Duration.seconds(5)).showInformation();
					logoutClicked(new ActionEvent());
				} catch (SQLException e) {

					e.printStackTrace();
					Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed")
							.text("Delete request of hawker bill has failed").showError();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
		 }
	}
	

}

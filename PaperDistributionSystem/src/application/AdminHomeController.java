package application;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
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
    
    @FXML private Tab customersTab;
    @FXML private Tab lineInfoTab;
    @FXML private Tab pausedCustTab;
    @FXML private Tab hawkerTab;

    private ACustomerInfoTabController customerTabController;
    private AHawkerInfoTabController hawkerTabController;
    private ALineInfoTabController lineInfoTabController;
    private APausedCustomerTabController pausedCustTabController;
    

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub

		loadTabs();
		
		
		/*tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				// TODO Auto-generated method stub
				System.out.println("Tab changed from "+oldValue.getText()+" to "+newValue.getText());
				if(newValue == customersTab){
					customerTabController.reloadData();
				}
				else if(newValue == lineInfoTab){
					lineInfoTabController.reloadData();
				}
				else if(newValue == hawkerTab){
					hawkerTabController.reloadData();
				}
				else if(newValue == pausedCustTab){
					pausedCustTabController.reloadData();
				}
				
			}
		});*/
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
			
			/*pausedCustTab = new Tab();
			FXMLLoader pausedCustTabLoader = new FXMLLoader(getClass().getResource("A-PausedCustomerTab.fxml"));
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
			});*/
			
			tabPane.getTabs().addAll(hawkerTab,customersTab,  lineInfoTab);
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
//		pausedCustTabController.reloadData();
	}
	

}

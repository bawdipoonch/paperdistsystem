package application;

import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HawkerHomeController implements Initializable {
	
	@FXML private Button logoutButton;
	@FXML private Button refreshButton;
	@FXML private Button changePwdButton;
	@FXML private Label hawkerName;
	@FXML private Label code;
	@FXML private Label agencyName;
	@FXML private Label mobileNum;
	Stage stage; 
    Parent root;
    @FXML private Tab customersTab;
    @FXML private Tab lineInfoTab;
    @FXML private Tab pausedCustTab;
    @FXML private Tab lineDistTab;
    @FXML private TabPane tabPane;
    
    private ACustomerInfoTabController customerTabController;
    private ALineDistributorTabController lineDistTabController;
    private ALineInfoTabController lineInfoTabController;
    private APausedCustomerTabController pausedCustTabController;
    


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
			hawkerName.setText(HawkerLoginController.loggedInHawker.getName());
			code.setText(HawkerLoginController.loggedInHawker.getHawkerCode());
			agencyName.setText(HawkerLoginController.loggedInHawker.getAgencyName());
			mobileNum.setText(HawkerLoginController.loggedInHawker.getMobileNum());
			loadTabs();
			if(!HawkerLoginController.loggedInHawker.getCustomerAccess().equals("Y"))
				tabPane.getTabs().remove(customersTab);
			if(!HawkerLoginController.loggedInHawker.getLineInfoAccess().equals("Y"))
				tabPane.getTabs().remove(lineInfoTab);
			if(!HawkerLoginController.loggedInHawker.getPausedCustAccess().equals("Y"))
				tabPane.getTabs().remove(pausedCustTab);
			if(!HawkerLoginController.loggedInHawker.getLineDistAccess().equals("Y"))
				tabPane.getTabs().remove(lineDistTab);
			
			/*tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

				@Override
				public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
					// TODO Auto-generated method stub
					System.out.println("Tab changed from "+oldValue.getText()+" to "+newValue.getText());
					if(newValue == customersTab){
						FXMLLoader customerTabLoader = new FXMLLoader(getClass().getResource("H-CustomerInfoTab.fxml"));
						root = (Parent)customerTabLoader.load();
						customerTabController = customerTabLoader.<HCustomerInfoTabController>getController();
						customerTabController.reloadData();
					}
					else if(newValue == lineInfoTab){
						FXMLLoader lineDistTabLoader = new FXMLLoader(getClass().getResource("H-LineDistributorTab.fxml"));
						root = (Parent)lineDistTabLoader.load();
						lineDistTabController = lineDistTabLoader.<HLineDistributorTabController>getController();
						lineInfoTabController.reloadData();
					}
					else if(newValue == lineDistTab){
						FXMLLoader lineInfoTabLoader = new FXMLLoader(getClass().getResource("H-LineInfoTab.fxml"));
						root = (Parent)lineInfoTabLoader.load();
						lineInfoTabController = lineInfoTabLoader.<HLineInfoTabController>getController();
						lineDistTabController.reloadData();
					}
					else if(newValue == pausedCustTab){
						FXMLLoader pausedCustTabLoader = new FXMLLoader(getClass().getResource("H-PausedCustomerTab.fxml"));
						root = (Parent)pausedCustTabLoader.load();
						pausedCustTabController = pausedCustTabLoader.<HPausedCustomerTabController>getController();
						pausedCustTabController.reloadData();
					}
					
				}
			});*/
		
	}
	
	@FXML
	private void logoutClicked(ActionEvent event) throws IOException {
		System.out.println("Logout clicked");
		HawkerLoginController.loggedInHawker = null;
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
			
			tabPane.getTabs().addAll(customersTab, lineInfoTab, lineDistTab );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@FXML private void refreshClicked(ActionEvent event){
//		ACustomerInfoTabController
		customerTabController.reloadData();
		lineDistTabController.reloadData();
		lineInfoTabController.reloadData();
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
//		     changeHawkerPwd(hawkerRow,result.get());
			 HawkerLoginController.loggedInHawker.setPassword(result.get());
			 HawkerLoginController.loggedInHawker.updateHawkerRecord();
			 Notifications.create().title("Password updated").text("Password was successfully updated").hideAfter(Duration.seconds(5)).showInformation();
		 }
	}
	

}

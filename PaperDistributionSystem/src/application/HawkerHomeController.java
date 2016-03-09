package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
import javafx.util.Duration;

public class HawkerHomeController implements Initializable {
	
	@FXML private Button logoutButton;
	@FXML private Button refreshButton;
	@FXML private Button changePwdButton;
	@FXML private Label hawkerName;
	@FXML private Label code;
	@FXML private Label agencyName;
	@FXML private Label mobileNum;
	@FXML private Label billCategory;
	@FXML private Label pointName;
//	Stage stage; 
    Parent root;
    @FXML private Tab customersTab;
    @FXML private Tab lineInfoTab;
    @FXML private Tab pausedCustTab;
    @FXML private Tab lineDistTab;
    @FXML private Tab productsTab;
    @FXML private TabPane tabPane;
    
    private ACustomerInfoTabController customerTabController;
    private ALineDistributorTabController lineDistTabController;
    private ALineInfoTabController lineInfoTabController;
    private APausedCustomerTabController pausedCustTabController;
    private AProductsTabController productsTabController;
    


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
			hawkerName.setText(HawkerLoginController.loggedInHawker.getName());
			code.setText(HawkerLoginController.loggedInHawker.getHawkerCode());
			agencyName.setText(HawkerLoginController.loggedInHawker.getAgencyName());
			mobileNum.setText(HawkerLoginController.loggedInHawker.getMobileNum());
			pointName.setText(HawkerLoginController.loggedInHawker.getPointName());
			populateBillCategory();
			loadTabs();
			if(!HawkerLoginController.loggedInHawker.getCustomerAccess().equals("Y"))
				tabPane.getTabs().remove(customersTab);
			if(!HawkerLoginController.loggedInHawker.getLineInfoAccess().equals("Y"))
				tabPane.getTabs().remove(lineInfoTab);
			if(!HawkerLoginController.loggedInHawker.getPausedCustAccess().equals("Y"))
				tabPane.getTabs().remove(pausedCustTab);
			if(!HawkerLoginController.loggedInHawker.getLineDistAccess().equals("Y"))
				tabPane.getTabs().remove(lineDistTab);
			if(!HawkerLoginController.loggedInHawker.getProductAccess().equals("Y"))
				tabPane.getTabs().remove(productsTab);
			
			tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {

				@Override
				public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
					if (oldValue != null) {
						if (oldValue == customersTab) {
							customerTabController.releaseVariables();
						}
						if (oldValue == lineInfoTab) {
							lineInfoTabController.releaseVariables();
						}
						if (oldValue == pausedCustTab) {
							pausedCustTabController.releaseVariables();
						}
//						if (oldValue == hawkerTab) {
//							hawkerTabController.releaseVariables();
//						}
						if (oldValue == lineDistTab) {
							lineDistTabController.releaseVariables();
						}
//						if (oldValue == additionalItemsTab) {
//							additionalItemsTabController.releaseVariables();
//						}
						if (oldValue == productsTab) {
							productsTabController.releaseVariables();
						}
						System.gc();
					}

					if (newValue != null) {
						if (newValue == customersTab) {
							customerTabController.reloadData();
						}
						if (newValue == lineInfoTab) {
							lineInfoTabController.reloadData();
						}
						if (newValue == pausedCustTab) {
							pausedCustTabController.reloadData();
						}
//						if (newValue == hawkerTab) {
//							hawkerTabController.reloadData();
//						}
						if (newValue == lineDistTab) {
							lineDistTabController.reloadData();
						}
//						if (newValue == additionalItemsTab) {
//							additionalItemsTabController.reloadData();
//						}
						if (newValue == productsTab) {
							productsTabController.reloadData();
						}

					}

				}
			});
			tabPane.getSelectionModel().selectFirst();
			customerTabController.reloadData();
			
	}
	
	private void populateBillCategory() {
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select bill_category from point_name where name =?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, HawkerLoginController.loggedInHawker.getPointName());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				billCategory.setText(rs.getString(1));
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
	}

	@FXML
	private void logoutClicked(ActionEvent event) throws IOException {
		System.out.println("Logout clicked");
		HawkerLoginController.loggedInHawker = null;
//		stage=(Stage) logoutButton.getScene().getWindow();
        //load up OTHER FXML document
		root = FXMLLoader.load(getClass().getResource("HawkerLogin.fxml"));
		
		Scene scene = new Scene(root);
		Main.primaryStage.setScene(scene);
		Main.primaryStage.setMaximized(true);
		Main.primaryStage.show();
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
			/*customersTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					
					customerTabController.reloadData();
				}
			});*/
			
			lineDistTab = new Tab();
			FXMLLoader lineDistTabLoader = new FXMLLoader(getClass().getResource("A-LineDistributorTab.fxml"));
			Parent linedistroot = (Parent)lineDistTabLoader.load();
			lineDistTabController = lineDistTabLoader.<ALineDistributorTabController>getController();
			lineDistTab.setText("Line Distribution Boy");
			lineDistTab.setContent(linedistroot);
			/*lineDistTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					
					lineDistTabController.reloadData();
				}
			});*/
			
			lineInfoTab = new Tab();
			FXMLLoader lineInfoTabLoader = new FXMLLoader(getClass().getResource("A-LineInfoTab.fxml"));
			Parent lineinforoot = (Parent)lineInfoTabLoader.load();
			lineInfoTabController = lineInfoTabLoader.<ALineInfoTabController>getController();
			lineInfoTab.setText("Line Information");
			lineInfoTab.setContent(lineinforoot);
			
			/*lineInfoTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					
					lineInfoTabController.reloadData();
				}
			});*/
			
			pausedCustTab = new Tab();
			FXMLLoader pausedCustTabLoader = new FXMLLoader(getClass().getResource("A-PausedCustomersTab.fxml"));
			Parent pausedcustroot = (Parent)pausedCustTabLoader.load();
			pausedCustTabController = pausedCustTabLoader.<APausedCustomerTabController>getController();
			pausedCustTab.setText("Stopped Customers");
			pausedCustTab.setContent(pausedcustroot);
			productsTab = new Tab();
			FXMLLoader productsTabLoader = new FXMLLoader(getClass().getResource("AProductsTab.fxml"));
			Parent productsRoot = (Parent)productsTabLoader.load();
			productsTabController = productsTabLoader.<AProductsTabController>getController();
			productsTab.setText("Products");
			productsTab.setContent(productsRoot);
			/*productsTab.setOnSelectionChanged(new EventHandler<Event>() {
				
				@Override
				public void handle(Event event) {
					
					productsTabController.reloadData();
				}
			});*/
			
			
			tabPane.getTabs().addAll(customersTab, lineInfoTab, lineDistTab,pausedCustTab ,productsTab);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	@FXML private void refreshClicked(ActionEvent event){
		Tab t = tabPane.getSelectionModel().getSelectedItem();
		if (t != null) {
			if (t == customersTab) {
				customerTabController.reloadData();
			}
			if (t == lineInfoTab) {
				lineInfoTabController.reloadData();
			}
			if (t == pausedCustTab) {
				pausedCustTabController.reloadData();
			}
//			if (t == hawkerTab) {
//				hawkerTabController.reloadData();
//			}
			if (t == lineDistTab) {
				lineDistTabController.reloadData();
			}
//			if (t == additionalItemsTab) {
//				additionalItemsTabController.reloadData();
//			}
			if (t == productsTab) {
				productsTabController.reloadData();
			}

		}
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

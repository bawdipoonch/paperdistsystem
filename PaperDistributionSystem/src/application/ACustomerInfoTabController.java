package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.controlsfx.control.Notifications;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Pair;

public class ACustomerInfoTabController implements Initializable {

	@FXML private TableView<Customer> ACustInfoTable;
	
	@FXML private TextField addCustName;
	@FXML private TextField addCustMobile;
	@FXML private ComboBox<String> addCustLineNum;
	@FXML private ComboBox<String> addCustHwkCode;
	@FXML private TextField addCustHouseSeq;
	@FXML private TextField addCustOldHouseNum;
	@FXML private TextField addCustNewHouseNum;
	@FXML private TextField addCustAddrLine1;
	@FXML private TextField addCustAddrLine2;
	@FXML private TextField addCustLocality;
	@FXML private TextField addCustCity;
	@FXML private ComboBox<String> addCustState;
	@FXML private TextField addCustProf1;
	@FXML private TextField addCustProf2;
	@FXML private TextField addCustProf3;
	
	//Columns
	@FXML private TableColumn<Customer, Long> customerCodeColumn;
	@FXML private TableColumn<Customer, String> customerNameColumn;
	@FXML private TableColumn<Customer, String> mobileNumColumn;
	@FXML private TableColumn<Customer, String> hawkerCodeColumn;
	@FXML private TableColumn<Customer, Long> lineNumColumn;
	@FXML private TableColumn<Customer, Long> houseSeqColumn;
	@FXML private TableColumn<Customer, String> oldHouseNumColumn;
	@FXML private TableColumn<Customer, String> newHouseNumColumn;
	@FXML private TableColumn<Customer, String> addrLine1Column;
	@FXML private TableColumn<Customer, String> addrLine2Column;
	@FXML private TableColumn<Customer, String> localityColumn;
	@FXML private TableColumn<Customer, String> cityColumn;
	@FXML private TableColumn<Customer, String> stateColumn;
	@FXML private TableColumn<Customer, String> profile1Column;
	@FXML private TableColumn<Customer, String> profile2Column;
	@FXML private TableColumn<Customer, String> profile3Column;
	
	private FilteredList<Customer> filteredData;
	private String searchText;


	private ObservableList<Customer> customerMasterData = FXCollections.observableArrayList();
	private ObservableList<String> hawkerCodeData = FXCollections.observableArrayList();
	private ObservableList<String> hawkerLineNumData = FXCollections.observableArrayList();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		System.out.println("Entered ACustomerInfoTabController");
		
		//Set cell value factories
		customerCodeColumn.setCellValueFactory(new PropertyValueFactory<Customer, Long>("customerCode"));
		customerNameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
		
		hawkerCodeColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("hawkerCode"));
		lineNumColumn.setCellValueFactory(new PropertyValueFactory<Customer, Long>("lineNum"));
		houseSeqColumn.setCellValueFactory(new PropertyValueFactory<Customer, Long>("houseSeq"));
		mobileNumColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("mobileNum"));
		newHouseNumColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("newHouseNum"));
		oldHouseNumColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("oldHouseNum"));
		addrLine1Column.setCellValueFactory(new PropertyValueFactory<Customer, String>("addrLine1"));
		addrLine2Column.setCellValueFactory(new PropertyValueFactory<Customer, String>("addrLine2"));
		localityColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("locality"));
		cityColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("city"));
		stateColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("state")); 
		profile1Column.setCellValueFactory(new PropertyValueFactory<Customer, String>("profile1")); 
		profile2Column.setCellValueFactory(new PropertyValueFactory<Customer, String>("profile2")); 
		profile3Column.setCellValueFactory(new PropertyValueFactory<Customer, String>("profile3")); 
		
		addCustLineNum.setDisable(true);
		addCustHouseSeq.setDisable(true);
		addCustLineNum.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				addCustHouseSeq.setDisable(false);
			}
		});
		
		addCustHwkCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				addCustLineNum.setDisable(false);
				addCustLineNum.getItems().clear();
				populateLineNumbersForHawkerCode(newValue);
				addCustLineNum.getItems().addAll(hawkerLineNumData);
			}

			
		});
		
		
		
		addCustState.getItems().addAll("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", 
				"Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha",
				"Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal");
		
		ACustInfoTable.setRowFactory(new Callback<TableView<Customer>, TableRow<Customer>>() {
			
			@Override
			public TableRow<Customer> call(TableView<Customer> param) {
				// TODO Auto-generated method stub
				final TableRow<Customer> row = new TableRow<>();
				MenuItem mnuDel = new MenuItem("Delete customer");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
				    @Override
				    public void handle(ActionEvent t) {
				        Customer custRow = customerMasterData.get(ACustInfoTable.getSelectionModel().getSelectedIndex());
				        if (custRow != null){ 
				        	deleteCustomer(custRow);
				        	ACustInfoTable.refresh();
				        }
				    }

				});
				
				MenuItem mnuEdit = new MenuItem("Edit customer");
				mnuEdit.setOnAction(new EventHandler<ActionEvent>() {
				    @Override
				    public void handle(ActionEvent t) {
				        Customer custRow = customerMasterData.get(ACustInfoTable.getSelectionModel().getSelectedIndex());
				        if (custRow != null){ 
				        	showEditCustomerDialog(custRow);
				        	ACustInfoTable.refresh();
				        }
				    }

				});
				ContextMenu menu = new ContextMenu();
				menu.getItems().addAll(mnuEdit,mnuDel);
				row.contextMenuProperty().bind(
					      Bindings.when(Bindings.isNotNull(row.itemProperty()))
					      .then(menu)
					      .otherwise((ContextMenu)null));
				return row;
			}
		});
//		ACustInfoTable.setContextMenu(menu);
		populateHawkerCodes();
		addCustHwkCode.getItems().addAll(hawkerCodeData);
		
		refreshCustomerTable();
	}
	
	private void deleteCustomer(Customer custRow) {
		Dialog<ButtonType> deleteWarning = new Dialog<ButtonType>();
		deleteWarning.setTitle("Warning");
		deleteWarning.setHeaderText("Are you sure you want to delete this record?");
		deleteWarning.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
		Optional<ButtonType> result = deleteWarning.showAndWait();
		 if (result.isPresent() && result.get() == ButtonType.YES) {
			 
				try {
					
					Connection con = Main.dbConnection;
					while(!con.isValid(0)){
						con = Main.reconnect();
					}
					String deleteString = "delete from customer where customer_id=?";
					PreparedStatement deleteStmt = con.prepareStatement(deleteString);
					deleteStmt.setLong(1, custRow.getCustomerId());
					
					deleteStmt.executeUpdate();
					con.commit();
					
					Notifications.create().title("Delete Successful").text("Deletion of customer was successful").showInformation();
					customerMasterData.remove(custRow);
					ACustInfoTable.refresh();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Notifications.create().title("Delete failed").text("Delete request of customer has failed").showError();
				}
		 }
		
	}
	
	private void showEditCustomerDialog(Customer custRow) {
		int selectedIndex = ACustInfoTable.getSelectionModel().selectedIndexProperty().get();
		try {
		
		Dialog<Customer> editCustomerDialog = new Dialog<Customer>();
		editCustomerDialog.setTitle("Edit customer data");
		editCustomerDialog.setHeaderText("Update the customer data below");

		// Set the button types.
		ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
		editCustomerDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
		
		FXMLLoader editCustomerLoader = new FXMLLoader(getClass().getResource("EditCustomer.fxml"));
		Parent editCustomerGrid = (Parent)editCustomerLoader.load();
		EditCustomerController editCustController = editCustomerLoader.<EditCustomerController>getController();
		
		editCustomerDialog.getDialogPane().setContent(editCustomerGrid);
		editCustController.setCustomerToEdit(custRow);
		editCustController.setupBindings();
		
		
		editCustomerDialog.setResultConverter(dialogButton -> {
		    if (dialogButton == saveButtonType) {
		    	Customer edittedCustomer = editCustController.returnUpdatedCustomer();
		    	return edittedCustomer;
		    }
		    return null;
		});
		
		Optional<Customer> updatedCustomer = editCustomerDialog.showAndWait();
//		refreshCustomerTable();
		
		updatedCustomer.ifPresent(new Consumer<Customer>() {

			@Override
			public void accept(Customer t) {
				// TODO Auto-generated method stub
				customerMasterData.add(selectedIndex, t);
				customerMasterData.remove(custRow);
				ACustInfoTable.getSelectionModel().select(t);
			}
		});
		
		
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void populateLineNumbersForHawkerCode(String hawkerCode) {
		// TODO Auto-generated method stub
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			hawkerLineNumData.clear();
			PreparedStatement stmt = con.prepareStatement("select distinct line_num from line_info where hawker_id = ?");
			stmt.setLong(1, hawkerIdForCode(hawkerCode));
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				hawkerLineNumData.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private long hawkerIdForCode(String hawkerCode) {
		// TODO Auto-generated method stub
		long hawkerId=-1;
		Connection con = Main.dbConnection;
		try {
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			PreparedStatement hawkerIdStatement = null;
			String hawkerIdQuery = "select hawker_id from hawker_info where hawker_code = ?";
			hawkerIdStatement = con.prepareStatement(hawkerIdQuery);
			hawkerIdStatement.setString(1, hawkerCode);
			ResultSet hawkerIdRs = hawkerIdStatement.executeQuery();
			
			if(hawkerIdRs.next()){
				hawkerId=hawkerIdRs.getLong(1);
			}
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hawkerId;
	}
	
	public void refreshCustomerTable(){
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			customerMasterData.clear();
			populateHawkerCodes();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select customer_id,customer_code, name,mobile_num,hawker_code, line_Num, house_Seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3 from customer");
			while(rs.next()){
				customerMasterData.add(new Customer(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4), rs.getString(5), 
						rs.getLong(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),rs.getString(13),rs.getString(14),
						rs.getString(15), rs.getString(16),rs.getString(17)));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!customerMasterData.isEmpty()){
			filteredData = new FilteredList<>(customerMasterData, p -> true);
			SortedList<Customer> sortedData = new SortedList<>(filteredData);
			sortedData.comparatorProperty().bind(ACustInfoTable.comparatorProperty());
			ACustInfoTable.setItems(sortedData);
		}
		ACustInfoTable.refresh();
	}
	
	private void populateHawkerCodes() {
		// TODO Auto-generated method stub
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			hawkerCodeData.clear();
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select distinct hawker_code from hawker_info");
			while(rs.next()){
				hawkerCodeData.add(rs.getString(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void addCustomerClicked(ActionEvent event){
		System.out.println("addCustomerClicked");
		PreparedStatement insertCustomer = null;
		String insertStatement = 
				"INSERT INTO CUSTOMER(name,mobile_num,hawker_code, line_num, house_seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3) " + 
						"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Connection con = Main.dbConnection;
		try {
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
				insertCustomer = con.prepareStatement(insertStatement);
				insertCustomer.setString(1, addCustName.getText());
				insertCustomer.setString(2, addCustMobile.getText());
				insertCustomer.setString(3, addCustHwkCode.getSelectionModel().getSelectedItem());
				if(!addCustLineNum.isDisabled())
					insertCustomer.setLong(4, Long.parseLong(addCustLineNum.getSelectionModel().getSelectedItem()));
				else
					insertCustomer.setString(4, null);
				if(!addCustHouseSeq.isDisabled())
					insertCustomer.setInt(5, Integer.parseInt(addCustHouseSeq.getText()));
				else
					insertCustomer.setString(5, null);
				insertCustomer.setString(6, addCustOldHouseNum.getText());
				insertCustomer.setString(7, addCustNewHouseNum.getText());
				insertCustomer.setString(8, addCustAddrLine1.getText());
				insertCustomer.setString(9, addCustAddrLine2.getText());
				insertCustomer.setString(10, addCustLocality.getText());
				insertCustomer.setString(11, addCustCity.getText());
				insertCustomer.setString(12, addCustState.getSelectionModel().getSelectedItem());
				insertCustomer.setString(13, addCustProf1.getText());
				insertCustomer.setString(14, addCustProf2.getText());
				insertCustomer.setString(15, addCustProf3.getText());
				
				insertCustomer.execute();
				refreshCustomerTable();
				con.commit();
//				con.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notifications.create().title("Error!").text("There has been some error during customer creation, please retry").showError();
			Main.reconnect();
		}
		resetClicked(event);
		
	}
	
	@FXML private void resetClicked(ActionEvent event){
		System.out.println("resetClicked");
		addCustName.clear();
		addCustMobile.clear();
		addCustHwkCode.setValue(null);
		addCustLineNum.setValue(null);
		addCustLineNum.setDisable(true);
		addCustHouseSeq.clear();
		addCustHouseSeq.setDisable(true);
		addCustOldHouseNum.clear();
		addCustNewHouseNum.clear();
		addCustAddrLine1.clear();
		addCustAddrLine2.clear();
		addCustLocality.clear();
		addCustCity.clear();
		addCustState.setValue("State");
		addCustProf1.clear();
		addCustProf2.clear();
		addCustProf3.clear();
	}

@FXML private void filterCustomersClicked(ActionEvent event){
		
		TextInputDialog customersFilterDialog = new TextInputDialog(searchText);
		customersFilterDialog.setTitle("Filter Hawkers" );
		customersFilterDialog.setHeaderText("Enter the filter text");
		Optional<String> returnValue = customersFilterDialog.showAndWait();
		if(returnValue.isPresent()){
			try {
				searchText = returnValue.get();
				filteredData.setPredicate(new Predicate<Customer>() {

					@Override
					public boolean test(Customer customer) {
						// TODO Auto-generated method stub
						if(searchText == null || searchText.isEmpty()) return true;
						else if(customer.getAddrLine1().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getAddrLine2().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getCity().toUpperCase().contains(searchText.toUpperCase())) return true;
//						else if(customer.getHawkerCode().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getLocality().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getMobileNum().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getName().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getNewHouseNum().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getOldHouseNum().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getState().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getProfile1().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getProfile2().toUpperCase().contains(searchText.toUpperCase())) return true;
						else if(customer.getProfile3().toUpperCase().contains(searchText.toUpperCase())) return true;
						return false;
					}
				});
				
				SortedList<Customer> sortedData = new SortedList<>(filteredData);
				sortedData.comparatorProperty().bind(ACustInfoTable.comparatorProperty());
				ACustInfoTable.setItems(sortedData);

				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Notifications.create().title("Invalid value entered").text("Please enter numeric value only").showError();
			}
		}
		
	}
	
	@FXML private void clearClicked(ActionEvent event){
		filteredData = new FilteredList<>(customerMasterData, p -> true);
		SortedList<Customer> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(ACustInfoTable.comparatorProperty());
		ACustInfoTable.setItems(sortedData);
		ACustInfoTable.getSelectionModel().clearSelection();
	}

}

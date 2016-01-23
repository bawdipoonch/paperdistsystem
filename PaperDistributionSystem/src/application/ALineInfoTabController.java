package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import com.amazonaws.util.NumberUtils;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;

public class ALineInfoTabController implements Initializable {

	@FXML
	private TableView<LineInfo> lineNumTable;
	@FXML
	private TextField addLineNumField;
	@FXML
	private TableView<Customer> lineNumCustomersTable;
	@FXML
	private ComboBox<String> hawkerComboBox;

	// Columns
	@FXML
	private TableColumn<LineInfo, Integer> lineNumColumn;
	@FXML
	private TableColumn<Customer, Long> customerIDColumn;
	@FXML
	private TableColumn<Customer, String> customerNameColumn;
	@FXML
	private TableColumn<Customer, String> mobileNumColumn;
	@FXML
	private TableColumn<Customer, Integer> houseSeqColumn;

	private ObservableList<String> hawkerCodeData = FXCollections.observableArrayList();
	private ObservableList<LineInfo> lineNumData = FXCollections.observableArrayList();
	private ObservableList<Customer> customerData = FXCollections.observableArrayList();
	private ArrayList<TextField> newHouseSeqTFArray;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		System.out.println("Entered HLineInfoTabController");
		/*
		 * EventHandler hawkerComboBoxEventHandler = new
		 * EventHandler<ActionEvent>() {
		 * 
		 * @Override public void handle(ActionEvent event) { // TODO
		 * Auto-generated method stub
		 * 
		 * } };
		 */
		lineNumColumn.setCellValueFactory(new PropertyValueFactory<LineInfo, Integer>("lineNum"));
		lineNumTable.setDisable(true);
		addLineNumField.setDisable(true);

		customerIDColumn.setCellValueFactory(new PropertyValueFactory<Customer, Long>("customerCode"));
		customerNameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
		mobileNumColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("mobileNum"));
		houseSeqColumn.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("houseSeq"));

		hawkerComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				// TODO Auto-generated method stub
				if (!newValue.equalsIgnoreCase("Hawker")) {
					lineNumTable.setDisable(false);
					addLineNumField.setDisable(false);
				}
				refreshLineNumTableForHawker(newValue);
			}

		});

		lineNumTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LineInfo>() {

			@Override
			public void changed(ObservableValue<? extends LineInfo> observable, LineInfo oldValue, LineInfo newValue) {
				// TODO Auto-generated method stub
				populateCustomersForLine();
			}

		});

		lineNumTable.setRowFactory(new Callback<TableView<LineInfo>, TableRow<LineInfo>>() {
			
			@Override
			public TableRow<LineInfo> call(TableView<LineInfo> param) {
				final TableRow<LineInfo> row = new TableRow<>();
				MenuItem mnuDel = new MenuItem("Delete line");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
				    @Override
				    public void handle(ActionEvent t) {
				    	LineInfo lineRow = lineNumData.get(lineNumTable.getSelectionModel().getSelectedIndex());
				        if (lineRow != null){ 
				        	deleteLine(lineRow);
				        }
				    }


				});
				
				MenuItem mnuEdit = new MenuItem("Edit line number");
				mnuEdit.setOnAction(new EventHandler<ActionEvent>() {
				    @Override
				    public void handle(ActionEvent t) {
				    	LineInfo lineRow = lineNumData.get(lineNumTable.getSelectionModel().getSelectedIndex());
				        if (lineRow != null){ 
				        	showEditLineDialog(lineRow);
				        	lineNumTable.refresh();
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

		populateHawkerCodes();
		

	}

	private void showEditLineDialog(LineInfo lineRow) {
		// TODO Auto-generated method stub
		
		TextInputDialog dialog = new TextInputDialog();
		
		dialog.setTitle("Edit line number");
		dialog.setHeaderText("Enter new line number below");
		
		ButtonType saveButton = new ButtonType("Save");
//		dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
		Button saveBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
		
		saveBtn.addEventFilter(ActionEvent.ACTION, event -> {
			try {
				Integer newLineNum = Integer.parseInt(dialog.getEditor().getText());
				
				if(checkExistingLineNum(newLineNum)){
					lineNumTable.getSelectionModel().getSelectedItem().setLineNum(newLineNum);
					lineNumTable.getSelectionModel().getSelectedItem().updateLineNumRecord();
					updateLineNumForCust(newLineNum,customerData);
					lineNumTable.refresh();
					Notifications.create().hideAfter(Duration.seconds(5)).title("Successful").text("Line number update successful.").showInformation();
				}
				else {
					Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid line num").text("This line number already exists.").showError();
				}
				
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid line num").text("Please enter numeric line number only").showError();
			}
		});
		dialog.showAndWait();
	}
	
	private void updateLineNumForCust(Integer newLineNum, ObservableList<Customer> custData) {
		// TODO Auto-generated method stub
		for(int i=0;i<custData.size();i++){
			custData.get(i).setLineNum(newLineNum);
			custData.get(i).updateCustomerRecord();
		}
		lineNumCustomersTable.refresh();
	}

	private boolean deleteLine(LineInfo lineRow) {
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
					
					String findString = "select count(*) from customer where hawker_code=? and line_num=?";
					PreparedStatement findStmt = con.prepareStatement(findString);
					findStmt.setString(1,hawkerComboBox.getSelectionModel().getSelectedItem());
					findStmt.setInt(2, lineNumTable.getSelectionModel().getSelectedItem().getLineNum());
					ResultSet rs = findStmt.executeQuery();
					if(rs.next() && rs.getInt(1)==0) {
						String deleteString = "delete from line_info where line_id=?";
						PreparedStatement deleteStmt = con.prepareStatement(deleteString);
						deleteStmt.setLong(1, lineRow.getLineId());
						deleteStmt.executeUpdate();
						con.commit();
						Notifications.create().hideAfter(Duration.seconds(5)).title("Delete Successful").text("Deletion of line was successful")
								.showInformation();
						refreshLineNumTableForHawker(hawkerComboBox.getSelectionModel().getSelectedItem());
					} else
					{
						Notifications.create().hideAfter(Duration.seconds(5)).title("Delete not allowed").text("This line has customers associated to it, hence cannot be deleted").showError();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed").text("Delete request of line has failed").showError();
				}
		 }
		return false;
	}
	private void populateCustomersForLine() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					customerData.clear();
					PreparedStatement stmt = con.prepareStatement(
							"select customer_id,customer_code, name,mobile_num,hawker_code, line_Num, house_Seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3 from customer where hawker_code = ? and line_num = ? ORDER BY HOUSE_SEQ");
					stmt.setString(1, hawkerComboBox.getSelectionModel().getSelectedItem());
					stmt.setInt(2, lineNumTable.getSelectionModel().selectedItemProperty().getValue().getLineNum());
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						customerData.add(new Customer(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4),
								rs.getString(5), rs.getLong(6), rs.getInt(7), rs.getString(8), rs.getString(9),
								rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14),
								rs.getString(15), rs.getString(16), rs.getString(17)));
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!customerData.isEmpty())
					lineNumCustomersTable.setItems(customerData);
				lineNumCustomersTable.refresh();
				return null;
			}
			
		};
		
		new Thread(task).start();
		
	}

	private void populateHawkerCodes() {
		
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					hawkerCodeData.clear();
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("select distinct hawker_code from hawker_info");
					while (rs.next()) {
						hawkerCodeData.add(rs.getString(1));
					}
					hawkerComboBox.getItems().clear();
					hawkerComboBox.getItems().addAll(hawkerCodeData);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		
	}

	private void refreshLineNumTableForHawker(String hawkerCode) {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				lineNumTable.getItems().clear();
				System.out.println("refreshLineNumTableForHawker : " + hawkerCode);
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					lineNumData.clear();
					PreparedStatement hawkerIdStatement = null;
					String hawkerIdQuery = "select hawker_id from hawker_info where hawker_code = ?";
					hawkerIdStatement = con.prepareStatement(hawkerIdQuery);
					hawkerIdStatement.setString(1, hawkerCode);
					ResultSet hawkerIdRs = hawkerIdStatement.executeQuery();
					long hawkerId = -1;
					if (hawkerIdRs.next()) {
						hawkerId = hawkerIdRs.getLong(1);
					}
					if (hawkerId >= 1) {
						PreparedStatement lineNumStatement = null;
						String lineNumQuery = "select line_id, line_num, hawker_id from line_info where hawker_id = ?";
						lineNumStatement = con.prepareStatement(lineNumQuery);
						lineNumStatement.setLong(1, hawkerId);
						// Statement stmt = con.createStatement();
						ResultSet rs = lineNumStatement.executeQuery();
						while (rs.next()) {
							lineNumData.add(new LineInfo(rs.getLong(1), rs.getInt(2), rs.getLong(3)));
						}
						System.out.println("LineNumData = " + lineNumData.toString());
						lineNumTable.getItems().addAll(lineNumData);
						lineNumTable.refresh();
					} else {
						lineNumData.clear();
						lineNumTable.getItems().clear();
						lineNumTable.refresh();
						Notifications.create().hideAfter(Duration.seconds(5)).title("No lines found").text("No lines found under the hawker").show();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		};
		new Thread(task).start();
	}

	@FXML
	private void addLineButtonClicked(ActionEvent event) {
		try {
			if (hawkerComboBox.getSelectionModel().selectedIndexProperty().get()!=-1) {
				Integer addLineNumValue = Integer.parseInt(addLineNumField.getText());
				if (checkExistingLineNum(addLineNumValue)) {
					Task<Void> task = new Task<Void> () {

						@Override
						protected Void call() throws Exception {
							PreparedStatement insertLineNum = null;
							String insertStatement = "INSERT INTO LINE_INFO(LINE_NUM,HAWKER_ID) " + "VALUES (?,?)";
							Connection con = Main.dbConnection;
							try {
								while (!con.isValid(0)) {
									con = Main.reconnect();
								}
								insertLineNum = con.prepareStatement(insertStatement);
								long hawkerId = hawkerIdForCode();
								if (hawkerId >= 1) {
									insertLineNum.setInt(1, Integer.parseInt(addLineNumField.getText()));
									insertLineNum.setLong(2, hawkerId);
									insertLineNum.execute();
									refreshLineNumTableForHawker(hawkerComboBox.getSelectionModel().getSelectedItem());
									addLineNumField.clear();
								}
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							return null;
						}
						
					};
					new Thread(task).start();
				} 
			}
			else
				Notifications.create().hideAfter(Duration.seconds(5)).title("Hawker not selected").text("Please select hawker before adding line number").showError();

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notifications.create().hideAfter(Duration.seconds(5)).title("Error").text("Please enter proper numeric value in Line Number field").showError();
		}

	}

	private long hawkerIdForCode() {
		
		long hawkerId = -1;
		Connection con = Main.dbConnection;
		try {
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			PreparedStatement hawkerIdStatement = null;
			String hawkerIdQuery = "select hawker_id from hawker_info where hawker_code = ?";
			hawkerIdStatement = con.prepareStatement(hawkerIdQuery);
			hawkerIdStatement.setString(1, hawkerComboBox.getSelectionModel().getSelectedItem());
			ResultSet hawkerIdRs = hawkerIdStatement.executeQuery();

			if (hawkerIdRs.next()) {
				hawkerId = hawkerIdRs.getLong(1);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hawkerId;
	}

	private boolean checkExistingLineNum(Integer lineNum) {
		// TODO Auto-generated method stub
		Connection con = Main.dbConnection;
		try {
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			PreparedStatement lineNumExists = null;
			String lineNumExistsQuery = "select line_num from line_info where line_num = ? and hawker_id = ?";
			lineNumExists = con.prepareStatement(lineNumExistsQuery);
			lineNumExists.setInt(1, lineNum);
			lineNumExists.setLong(2, hawkerIdForCode());
			ResultSet lineNumExistsRs = lineNumExists.executeQuery();
			if (lineNumExistsRs.next()) {
				Notifications.create().hideAfter(Duration.seconds(5)).title("Line number exists")
				.text("This line number already exists in the hawker selected").showError();
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}


	@FXML public void shuffleHouseSeqClicked(ActionEvent event){
		// Create the custom dialog.
		if(!customerData.isEmpty()){
//			Dialog<ArrayList<Pair<Long, Integer>>> dialog = new Dialog<>();
			Dialog<ArrayList<Integer>> dialog = new Dialog<>();
			dialog.setTitle("Shuffle house sequences!");
			dialog.setHeaderText("Update house numbers below");

			newHouseSeqTFArray = new ArrayList<TextField>();
			ArrayList<Integer> newHouseSeq = new ArrayList<Integer>(customerData.size());
			
			// Set the button types.
			ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
			ScrollPane scrollPane = new ScrollPane();
			Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
			saveButton.addEventFilter(ActionEvent.ACTION, btnevent -> {
				for(int i=0;i<customerData.size();i++){
					if (NumberUtils.tryParseInt(newHouseSeqTFArray.get(i).getText()) != null) {
						if (!newHouseSeq.contains(NumberUtils.tryParseInt(newHouseSeqTFArray.get(i).getText()))) {
							newHouseSeq.add(i,NumberUtils.tryParseInt(newHouseSeqTFArray.get(i).getText()));
						} else{
							Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid sequence").text("Duplicate house sequence found")
							.showError();
							newHouseSeq.clear();
							btnevent.consume();
						}
					} else{
						Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid sequence").text("House sequence should not be empty and must be NUMBERS only")
						.showError();
						newHouseSeq.clear();
						btnevent.consume();
					}
				}
			});

			// Create the username and password labels and fields.
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 150, 10, 10));
			//			Iterator<Customer> custIter = customerData.iterator();
			
			grid.add(new Label("Customer Code"), 0, 0);
			grid.add(new Label("Name"), 1, 0);
			grid.add(new Label("Mobile Number"), 2, 0);
			grid.add(new Label("Old House Seq"), 3, 0);
			grid.add(new Label("New House Seq"), 4, 0);
			for(int i=0;i<customerData.size();i++){
				Customer cust = customerData.get(i);
				grid.add(new Label(""+cust.getCustomerCode()), 0, i+1);
				grid.add(new Label(""+cust.getName()), 1, i+1);
				grid.add(new Label(""+cust.getMobileNum()), 2, i+1);
				grid.add(new Label(""+cust.getHouseSeq()), 3, i+1);
				TextField newHNumTF = new TextField();

				 /*newHNumTF.focusedProperty().addListener(new ChangeListener<Boolean>() {

					@Override
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
						// TODO Auto-generated method stub
						if (oldValue && !newValue) {
							if (NumberUtils.tryParseInt(newHNumTF.getText()) != null) {
								if (!newHouseSeq.contains(NumberUtils.tryParseInt(newHNumTF.getText()))) {
									newHouseSeq.add(newHouseSeqTFArray.indexOf(newHNumTF),NumberUtils.tryParseInt(newHNumTF.getText()));
								} else
									Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid sequence").text("House sequence already entered before")
									.showError();
							} else{
								newHouseSeq.remove(newHouseSeqTFArray.indexOf(newHNumTF));
								
							}
						}
					}
				});
*/
				newHouseSeqTFArray.add(i,newHNumTF);
				grid.add(newHNumTF, 4, i+1);
			}
			scrollPane.setContent(grid);

			dialog.getDialogPane().setContent(scrollPane);

			// Convert the result to a username-password-pair when the login button is clicked.
			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == saveButtonType) {
					return newHouseSeq;
				}
				return null;
			});

//			Optional<ArrayList<Pair<Long, Integer>>> result = dialog.showAndWait();
			Optional<ArrayList<Integer>> result = dialog.showAndWait();
			if(result.isPresent()){
				if(result != null){
					updateHouseSequences(result.get());
				}
			}
		}else {
			Notifications.create().hideAfter(Duration.seconds(5)).title("No customers").text("There are no customers in this line").showError();
		}
	}

	private void updateHouseSequences(ArrayList<Integer> houseSeqList) {
		// TODO Auto-generated method stub
		for(int i=0; i<houseSeqList.size();i++){
			customerData.get(i).setHouseSeq(houseSeqList.get(i));
			customerData.get(i).updateCustomerRecord();
		}
		lineNumCustomersTable.refresh();
	}

	public void reloadData(){
		System.out.println("Entered Line Info ReloadData");
		populateHawkerCodes();
		lineNumTable.getItems().clear();
		
	}
}

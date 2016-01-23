package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

import com.amazonaws.util.NumberUtils;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Duration;


public class HLineDistributorTabController implements Initializable {
	
	

	@FXML private TableView<LineDistributor> lineDistInfoTable;
	
	@FXML private TableColumn<LineDistributor, String> lineDistNameColumn;
	@FXML private TableColumn<LineDistributor, String> lineDistMobNumColumn;
	@FXML private TableColumn<LineDistributor, Integer> lineDistLineNumColumn;
	
	@FXML private TextField addNameField;
	@FXML private TextField addMobileNumField;
	@FXML private ComboBox<Integer> addLineNumField;
	
	private ObservableList<LineDistributor> lineDistData = FXCollections.observableArrayList();

	private ObservableList<Integer> lineNumData = FXCollections.observableArrayList();
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		System.out.println("Entered HLineDistributorTabController");
		lineDistData.clear();
		lineNumData.clear();
		lineDistNameColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("name"));
		lineDistMobNumColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("mobileNum"));
		lineDistLineNumColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, Integer>("lineNum"));
//		populateLineNumbersForHawkerCode();
//		refreshLineDistTable();
//		reloadData();
		
		lineDistInfoTable.setRowFactory(new Callback<TableView<LineDistributor>, TableRow<LineDistributor>>() {
			
			@Override
			public TableRow<LineDistributor> call(TableView<LineDistributor> param) {
				final TableRow<LineDistributor> row = new TableRow<>();
				MenuItem mnuDel = new MenuItem("Delete line");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
				    @Override
				    public void handle(ActionEvent t) {
				    	LineDistributor lineDistRow = lineDistData.get(lineDistInfoTable.getSelectionModel().getSelectedIndex());
				        if (lineDistRow != null){ 
				        	deleteLineDist(lineDistRow);
				        }
				    }


				});
				
				MenuItem mnuEdit = new MenuItem("Edit line number");
				mnuEdit.setOnAction(new EventHandler<ActionEvent>() {
				    @Override
				    public void handle(ActionEvent t) {
				    	LineDistributor lineDistRow = lineDistData.get(lineDistInfoTable.getSelectionModel().getSelectedIndex());
				        if (lineDistRow != null){ 
				        	showEditLineDialog(lineDistRow);
				        	lineDistInfoTable.refresh();
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

	}
	

	private void deleteLineDist(LineDistributor lineDistRow) {
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
					
					
						String deleteString = "delete from line_distributor where line_dist_id=?";
						PreparedStatement deleteStmt = con.prepareStatement(deleteString);
						deleteStmt.setLong(1, lineDistRow.getLineDistId());
						deleteStmt.executeUpdate();
						con.commit();
						Notifications.create().hideAfter(Duration.seconds(5)).title("Delete Successful").text("Deletion of line distributor was successful")
								.showInformation();
						lineDistData.remove(lineDistRow);
						lineDistInfoTable.refresh();
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed").text("Delete request of line distributor has failed").showError();
				}
		 }
	}

	private void showEditLineDialog(LineDistributor lineDistRow) {
		// TODO Auto-generated method stub
		Dialog<ArrayList<String>> dialog = new Dialog<ArrayList<String>>();
		dialog.setTitle("Edit Line Distributor");
		dialog.setHeaderText("Update line distributor details below");

		
		// Set the button types.
		ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
		ScrollPane scrollPane = new ScrollPane();
		

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));
		//			Iterator<Customer> custIter = customerData.iterator();
		
		grid.add(new Label("Name"), 0, 0);
		TextField nameTF = new TextField();
		nameTF.setText(lineDistRow.getName());
		grid.add(nameTF, 1, 0);
		
		grid.add(new Label("Mobile Number"), 0, 1);
		TextField mobileTF = new TextField();
		mobileTF.setText(lineDistRow.getMobileNum());
		grid.add(mobileTF, 1, 1);
		
		grid.add(new Label("Line Number"), 0, 2);
		ComboBox<Integer> lineNumTF = new ComboBox<Integer>();
		lineNumTF.getItems().addAll(lineNumData);
		lineNumTF.getSelectionModel().select(new Integer(lineDistRow.getLineNum()));
		grid.add(lineNumTF, 1,2);
		
		Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
		saveButton.addEventFilter(ActionEvent.ACTION, btnevent -> {
			if(nameTF.getText()==null || mobileTF.getText()==null || lineNumTF.getSelectionModel().getSelectedItem()==null) {
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid value").text("Name, Mobile Number and Line number must have appropriate values").showError();
				btnevent.consume();
			}
			else if(lineDistForLineExists(lineNumTF.getSelectionModel().getSelectedItem())){
				Notifications.create().hideAfter(Duration.seconds(5)).title("Error").text("Line Distributor already exists for this line number").showError();
				btnevent.consume();
			}
				
		});
		scrollPane.setContent(grid);

		dialog.getDialogPane().setContent(scrollPane);

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == saveButtonType) {
				 ArrayList<String> list = new ArrayList<>(); 
				 list.add(nameTF.getText());
				 list.add(mobileTF.getText());
				 list.add(lineNumTF.getSelectionModel().getSelectedItem()+"");
				return list;
			}
			return null;
		});

//		Optional<ArrayList<Pair<Long, Integer>>> result = dialog.showAndWait();
		Optional<ArrayList<String>> result = dialog.showAndWait();
		if(result.isPresent()){
			if(result != null){
				ArrayList<String> resultlist = result.get(); 
				lineDistRow.setName(resultlist.get(0));
				lineDistRow.setMobileNum(resultlist.get(1));
				lineDistRow.setLineNum(Integer.parseInt(resultlist.get(2)));
				lineDistRow.updateLineDistRecord();
				refreshLineDistTable();
			}
		}
	}
		
	@FXML
	private void addButtonClicked(ActionEvent event){
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					if (addNameField.getText()!=null && addMobileNumField.getText()!=null && addLineNumField.getSelectionModel().selectedItemProperty().isNotNull().get()) {
						
							if (!lineDistForLineExists(addLineNumField.getSelectionModel().selectedItemProperty().getValue())) {
								PreparedStatement insertLineNum = null;
								String insertStatement = "INSERT INTO LINE_DISTRIBUTOR(NAME, MOBILE_NUM, LINE_NUM,HAWKER_ID) "
										+ "VALUES (?,?,?,?)";
								Connection con = Main.dbConnection;
								try {
									while (!con.isValid(0)) {
										con = Main.reconnect();
									}
									insertLineNum = con.prepareStatement(insertStatement);
									long hawkerId = HawkerLoginController.loggedInHawker.getHawkerId();
									insertLineNum.setString(1, addNameField.getText());
									insertLineNum.setString(2, addMobileNumField.getText());
									insertLineNum.setInt(3, addLineNumField.getSelectionModel().selectedItemProperty().get());
									insertLineNum.setLong(4, hawkerId);
									insertLineNum.execute();
									
									refreshLineDistTable();
								} catch (SQLException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} 
							}
							else {
								Notifications.create().hideAfter(Duration.seconds(5)).title("Error").text("Line Distributor already exists for this line number").showError();
							}
						
					}else {
						Notifications.create().hideAfter(Duration.seconds(5)).title("Required fields").text("Please enter value in Name, Mobile Number and Line Number").showError();
					}
					
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Notifications.create().hideAfter(Duration.seconds(5)).title("Error").text("Please enter proper numeric value in Line Number field").showError();
				}
				return null;
			}
			
		};
		
		new Thread(task).start();
		
	}
	
	@FXML
	private void resetClicked(ActionEvent event){
		addNameField.clear();
		addMobileNumField.clear();
		addLineNumField.getSelectionModel().clearSelection();
	}
	
	private void populateLineNumbersForHawkerCode() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					
					Connection con = Main.dbConnection;
					while(!con.isValid(0)){
						con = Main.reconnect();
					}
					lineNumData.clear();
					PreparedStatement stmt = con.prepareStatement("select distinct line_num from line_info where hawker_id = ?");
					stmt.setLong(1, HawkerLoginController.loggedInHawker.getHawkerId());
					ResultSet rs = stmt.executeQuery();
					while(rs.next()){
						lineNumData.add(rs.getInt(1));
					}
					addLineNumField.getItems().clear();
					addLineNumField.getItems().addAll(lineNumData);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		};
		new Thread(task).start();
	}
	
	public void refreshLineDistTable(){
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					
					Connection con = Main.dbConnection;
					while(!con.isValid(0)){
						con = Main.reconnect();
					}
					lineDistData.clear();
					PreparedStatement stmt = con.prepareStatement("select line_dist_id, name, mobile_num, hawker_id, line_num from line_distributor where hawker_id = ?");
					stmt.setLong(1, HawkerLoginController.loggedInHawker.getHawkerId());
					ResultSet rs = stmt.executeQuery();
					while(rs.next()){
						lineDistData.add(new LineDistributor(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getLong(4), rs.getInt(5)));
					}
//					lineDistInfoTable.getItems().clear();
					lineDistInfoTable.setItems(lineDistData);
					lineDistInfoTable.refresh();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		};
		new Thread(task).start();
	}
	
	public boolean lineDistForLineExists(int line_num){
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			PreparedStatement stmt = con.prepareStatement("select count(*) from line_distributor where hawker_id = ? and line_num=?");
			stmt.setLong(1, HawkerLoginController.loggedInHawker.getHawkerId());
			stmt.setInt(2, line_num);
			ResultSet rs = stmt.executeQuery();
			if(rs.next() && rs.getInt(1)>0){
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void reloadData(){
		System.out.println("Line Distributor reloadData");
		refreshLineDistTable();
		populateLineNumbersForHawkerCode();
	}

}

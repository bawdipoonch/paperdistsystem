package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import javafx.util.Duration;

public class ALineDistributorTabController implements Initializable {

	@FXML
	private TableView<LineDistributor> lineDistInfoTable;

	@FXML
	private TableColumn<LineDistributor, String> lineDistNameColumn;
	@FXML
	private TableColumn<LineDistributor, String> lineDistMobNumColumn;

	@FXML
	private TableColumn<LineDistributor, String> lineDistHawkerCodeColumn;
	@FXML
	private TableColumn<LineDistributor, Integer> lineDistLineNumColumn;

	@FXML
	private TextField addNameField;
	@FXML
	private TextField addInitialsField;
	@FXML
	private TextField addMobileNumField;
	@FXML
	private ComboBox<String> addLineNumField;
	@FXML
	private ComboBox<String> addHwkCode;
	@FXML
	private TextField addOldHNum;
	@FXML
	private TextField addNewHNum;
	@FXML
	private TextField addAddrLine1;
	@FXML
	private TextField addAddrLine2;
	@FXML
	private TextField addLocality;
	@FXML
	private TextField addCity;
	@FXML
	private ComboBox<String> addState;
	@FXML
	private ComboBox<String> addProf1;
	@FXML
	private ComboBox<String> addProf2;
	@FXML
	private TextField addProf3;
	@FXML
	private ComboBox<String> addEmployment;
	@FXML
	private TextField addComments;
	@FXML
	private TextField addBuildingStreet;

	@FXML
	private ComboBox<String> addPointName;

	@FXML
	private TableColumn<LineDistributor, String> OldHouseNumColumn;
	@FXML
	private TableColumn<LineDistributor, String> NewHouseNumColumn;
	@FXML
	private TableColumn<LineDistributor, String> InitialsColumn;
	@FXML
	private TableColumn<LineDistributor, String> AddrLine1Column;
	@FXML
	private TableColumn<LineDistributor, String> AddrLine2Column;
	@FXML
	private TableColumn<LineDistributor, String> LocalityColumn;
	@FXML
	private TableColumn<LineDistributor, String> CityColumn;
	@FXML
	private TableColumn<LineDistributor, String> StateColumn;
	@FXML
	private TableColumn<LineDistributor, String> profile1Column;
	@FXML
	private TableColumn<LineDistributor, String> profile2Column;
	@FXML
	private TableColumn<LineDistributor, String> profile3Column;
	@FXML
	private TableColumn<LineDistributor, String> employmentColumn;
	@FXML
	private TableColumn<LineDistributor, String> commentsColumn;
	@FXML
	private TableColumn<LineDistributor, String> buildingStreetColumn;

	private ObservableList<LineDistributor> lineDistData = FXCollections.observableArrayList();

	private ObservableList<String> lineNumData = FXCollections.observableArrayList();
	private ObservableList<String> hawkerCodeData = FXCollections.observableArrayList();

	private ObservableList<String> employmentData = FXCollections.observableArrayList();
	private ObservableList<String> profileValues = FXCollections.observableArrayList();
	private ObservableList<String> pointNameValues = FXCollections.observableArrayList();

	@FXML
	private Button addLineDistExtraButton;

	@FXML private Button saveButton;
	@FXML private Button clearButton;
	@FXML private Button searchButton;
	@FXML private Button resetButton;

	private FilteredList<LineDistributor> filteredData;
	private String searchText;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		System.out.println("Entered HLineDistributorTabController");
		lineDistData.clear();
		lineNumData.clear();
		lineDistHawkerCodeColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("hawkerCode"));
		lineDistNameColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("name"));
		InitialsColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("initials"));
		lineDistMobNumColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("mobileNum"));
		lineDistLineNumColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, Integer>("lineNum"));
		NewHouseNumColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("newHouseNum"));
		OldHouseNumColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("oldHouseNum"));
		AddrLine1Column.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("addrLine1"));
		AddrLine2Column.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("addrLine2"));
		LocalityColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("locality"));
		CityColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("city"));
		StateColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("state"));
		profile1Column.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("profile1"));
		profile2Column.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("profile2"));
		profile3Column.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("profile3"));
		employmentColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("employment"));
		commentsColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("comments"));
		buildingStreetColumn.setCellValueFactory(new PropertyValueFactory<LineDistributor, String>("buildingStreet"));
		// populateLineNumbersForHawkerCode();
		// refreshLineDistTable();
		// reloadData();

		addState.getItems().addAll("Tamil Nadu", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
				"Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand",
				"Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
				"Odisha", "Punjab", "Rajasthan", "Sikkim", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand",
				"West Bengal");
		addLineNumField.getSelectionModel().clearSelection();
		addLineNumField.setDisable(true);
		
		addPointName.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				populateHawkerCodes();
				addHwkCode.getItems().clear();
				addHwkCode.getItems().addAll(hawkerCodeData);
			}
		});
		
		addHwkCode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				addLineNumField.setDisable(false);
				addLineNumField.getItems().clear();
				populateLineNumbersForHawkerCode(newValue);
				addLineNumField.getItems().addAll(lineNumData);
				refreshLineDistTable();
			}

		});
		
		
		lineDistInfoTable.setRowFactory(new Callback<TableView<LineDistributor>, TableRow<LineDistributor>>() {

			@Override
			public TableRow<LineDistributor> call(TableView<LineDistributor> param) {
				final TableRow<LineDistributor> row = new TableRow<>();
				MenuItem mnuDel = new MenuItem("Delete line distributor");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						LineDistributor lineDistRow = lineDistData
								.get(lineDistInfoTable.getSelectionModel().getSelectedIndex());
						if (lineDistRow != null) {
							deleteLineDist(lineDistRow);
						}
					}

				});

				MenuItem mnuEdit = new MenuItem("Edit line distributor");
				mnuEdit.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						LineDistributor lineDistRow = lineDistData
								.get(lineDistInfoTable.getSelectionModel().getSelectedIndex());
						if (lineDistRow != null) {
							showEditLineDialog(lineDistRow);
							lineDistInfoTable.refresh();
						}
					}

				});

				ContextMenu menu = new ContextMenu();
				menu.getItems().addAll(mnuEdit, mnuDel);
				row.contextMenuProperty().bind(
						Bindings.when(Bindings.isNotNull(row.itemProperty())).then(menu).otherwise((ContextMenu) null));
				return row;
			}
		});

		addLineDistExtraButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					addLineDistExtraScreenClicked(new ActionEvent());
				}
			}
		});

		addInitialsField.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				
				if (newValue.length() > 3)
					addInitialsField.setText(oldValue);
			}
		});
		
		saveButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					addButtonClicked(new ActionEvent());
				}

			}
		});
		
		resetButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					resetClicked(new ActionEvent());
				}

			}
		});
		
		clearButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					clearClicked(new ActionEvent());
				}

			}
		});
		
		searchButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					filterLineDistClicked(new ActionEvent());
				}

			}
		});
		
		addNameField.requestFocus();
	}

	private void populateLineNumbersForHawkerCode(String hawkerCode) {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					lineNumData.clear();
					PreparedStatement stmt = con.prepareStatement(
							"select li.line_id, li.line_num, li.hawker_id,li.LINE_NUM || ' ' || ld.NAME as line_num_dist from line_info li, line_distributor ld where li.HAWKER_ID=ld.HAWKER_ID(+) and li.line_num=ld.line_num(+) and li.hawker_id = ? order by li.line_num");
					stmt.setLong(1, hawkerIdForCode(hawkerCode));
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						lineNumData.add(rs.getString(4));
					}
					addLineNumField.getItems().clear();
					addLineNumField.getItems().addAll(lineNumData);
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				return null;
			}

		};
		new Thread(task).start();

	}

	private long hawkerIdForCode(String hawkerCode) {

		long hawkerId = -1;
		Connection con = Main.dbConnection;
		try {
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			PreparedStatement hawkerIdStatement = null;
			String hawkerIdQuery = "select hawker_id from hawker_info where hawker_code = ?";
			hawkerIdStatement = con.prepareStatement(hawkerIdQuery);
			hawkerIdStatement.setString(1, hawkerCode);
			ResultSet hawkerIdRs = hawkerIdStatement.executeQuery();

			if (hawkerIdRs.next()) {
				hawkerId = hawkerIdRs.getLong(1);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return hawkerId;
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
				while (!con.isValid(0)) {
					con = Main.reconnect();
				}

				String deleteString = "delete from line_distributor where line_dist_id=?";
				PreparedStatement deleteStmt = con.prepareStatement(deleteString);
				deleteStmt.setLong(1, lineDistRow.getLineDistId());
				deleteStmt.executeUpdate();
				con.commit();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete Successful")
						.text("Deletion of line distributor was successful").showInformation();
				lineDistData.remove(lineDistRow);
				lineDistInfoTable.refresh();

			} catch (SQLException e) {
				
				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed")
						.text("Delete request of line distributor has failed").showError();
			}
		}
	}

	private void showEditLineDialog(LineDistributor lineDistRow) {
		int selectedIndex = lineDistInfoTable.getSelectionModel().selectedIndexProperty().get();
		try {

			Dialog<LineDistributor> editLineDistributorDialog = new Dialog<LineDistributor>();
			editLineDistributorDialog.setTitle("Edit Line Distributor Boy data");
			editLineDistributorDialog.setHeaderText("Update the Line Distributor Boy data below");

			// Set the button types.
			ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
			editLineDistributorDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
			Button saveButton = (Button) editLineDistributorDialog.getDialogPane().lookupButton(saveButtonType);

			FXMLLoader editLineDistributorLoader = new FXMLLoader(getClass().getResource("EditLineDistributor.fxml"));
			Parent editLineDistributorGrid = (Parent) editLineDistributorLoader.load();
			EditLineDistributorController editLineDistributorController = editLineDistributorLoader
					.<EditLineDistributorController> getController();

			editLineDistributorDialog.getDialogPane().setContent(editLineDistributorGrid);
			editLineDistributorController.setLineDistToEdit(lineDistRow);
			editLineDistributorController.setupBindings();
			saveButton.addEventFilter(ActionEvent.ACTION, btnEvent -> {
				if(!editLineDistributorController.isLineDistValid()){
					Notifications.create().title("Invalid Line Distributor").text("Please provide appropriate values for line distributor").hideAfter(Duration.seconds(5)).showError();
					btnEvent.consume();
				}
			});

			editLineDistributorDialog.setResultConverter(dialogButton -> {
				if (dialogButton == saveButtonType) {
					LineDistributor edittedLineDistributor = editLineDistributorController
							.returnUpdatedLineDistributor();
					return edittedLineDistributor;
				}
				return null;
			});

			Optional<LineDistributor> updatedLineDistributor = editLineDistributorDialog.showAndWait();
			// refreshCustomerTable();

			updatedLineDistributor.ifPresent(new Consumer<LineDistributor>() {

				@Override
				public void accept(LineDistributor t) {
					
					lineDistData.add(selectedIndex, t);
					lineDistData.remove(lineDistRow);
					lineDistInfoTable.getSelectionModel().select(t);
					lineDistInfoTable.getSelectionModel().getSelectedItem().updateLineDistRecord();
					editLineDistributorController.releaseVariables();
				}
			});

		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	public void addLineDistExtraScreenClicked(ActionEvent event) {
		try {

			Dialog<String> addLineDistDialog = new Dialog<String>();
			addLineDistDialog.setTitle("Add new line distributor");
			addLineDistDialog.setHeaderText("Add new line distributor data below.");

			// Set the button types.
			ButtonType saveButtonType = new ButtonType("Save and add new", ButtonData.OK_DONE);
			addLineDistDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CLOSE);
			Button saveButton = (Button) addLineDistDialog.getDialogPane().lookupButton(saveButtonType);
			FXMLLoader addLineDistLoader = new FXMLLoader(getClass().getResource("AddLineDistExtraScreen.fxml"));
			Parent addLineDistGrid = (Parent) addLineDistLoader.load();
			AddLineDistExtraScreenController addLineDistController = addLineDistLoader
					.<AddLineDistExtraScreenController> getController();
			saveButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					if(addLineDistController.isValid()){
						addLineDistController.addLineDistributor();
						Notifications.create().hideAfter(Duration.seconds(5)).title("Line Distributor created")
								.text("Line Distributor created successfully.").showInformation();
						refreshLineDistTable();
					} else
					event.consume();
				}
			});
			addLineDistDialog.getDialogPane().setContent(addLineDistGrid);
			addLineDistController.setupBindings();

			addLineDistDialog.setResultConverter(dialogButton -> {
				if (dialogButton != saveButtonType) {
					return null;
				}
				return null;
			});

			Optional<String> updatedCustomer = addLineDistDialog.showAndWait();
			// refreshCustomerTable();

			updatedCustomer.ifPresent(new Consumer<String>() {

				@Override
				public void accept(String t) {
					
					addLineDistController.releaseVariables();
				}
			});

		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	@FXML
	private void addButtonClicked(ActionEvent event) {

		try {
			if (addNameField.getText() != null && addMobileNumField.getText() != null
					&& addLineNumField.getSelectionModel().selectedItemProperty().isNotNull().get()) {

				if (!lineDistForLineExists(
						addLineNumField.getSelectionModel().getSelectedItem().split(" ")[0].trim())) {
					PreparedStatement insertLineNum = null;
					String insertStatement = "INSERT INTO LINE_DISTRIBUTOR(NAME, MOBILE_NUM, LINE_NUM,HAWKER_ID,old_house_num, new_house_num, address_line1, address_line2, locality, city, state,profile1,profile2,profile3,initials, employment, comments, building_street) "
							+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					Connection con = Main.dbConnection;
					try {
						while (!con.isValid(0)) {
							con = Main.reconnect();
						}
						insertLineNum = con.prepareStatement(insertStatement);
						long hawkerId = HawkerLoginController.loggedInHawker!=null?HawkerLoginController.loggedInHawker.getHawkerId():hawkerIdForCode(addHwkCode.getSelectionModel().getSelectedItem());
						insertLineNum.setString(1, addNameField.getText());
						insertLineNum.setString(2, addMobileNumField.getText());
						insertLineNum.setString(3,
								addLineNumField.getSelectionModel().getSelectedItem().split(" ")[0].trim());
						insertLineNum.setLong(4, hawkerId);
						insertLineNum.setString(5, addOldHNum.getText());
						insertLineNum.setString(6, addNewHNum.getText());
						insertLineNum.setString(7, addAddrLine1.getText());
						insertLineNum.setString(8, addAddrLine2.getText());
						insertLineNum.setString(9, addLocality.getText());
						insertLineNum.setString(10, addCity.getText());
						insertLineNum.setString(11, addState.getSelectionModel().getSelectedItem());
						insertLineNum.setString(12, addProf1.getSelectionModel().getSelectedItem());
						insertLineNum.setString(13, addProf2.getSelectionModel().getSelectedItem());
						insertLineNum.setString(14, addProf3.getText());
						insertLineNum.setString(15, addInitialsField.getText());
						insertLineNum.setString(16, addEmployment.getSelectionModel().getSelectedItem());
						insertLineNum.setString(17, addComments.getText());
						insertLineNum.setString(18, addBuildingStreet.getText());
						insertLineNum.execute();
						resetClicked(event);
						addNameField.requestFocus();
						refreshLineDistTable();
					} catch (SQLException e) {
						
						e.printStackTrace();
					}
				} else {
					Notifications.create().hideAfter(Duration.seconds(5)).title("Error")
							.text("Line Distributor already exists for this line number").showError();
				}

			} else {
				Notifications.create().hideAfter(Duration.seconds(5)).title("Required fields")
						.text("Please enter value in Name, Mobile Number and Line Number").showError();
			}

		} catch (NumberFormatException e) {
			
			e.printStackTrace();
			Notifications.create().hideAfter(Duration.seconds(5)).title("Error")
					.text("Please enter proper numeric value in Line Number field").showError();
		}

	}

	@FXML
	private void resetClicked(ActionEvent event) {

		addNameField.clear();

		addInitialsField.clear();

		addMobileNumField.clear();

		addLineNumField.getSelectionModel().clearSelection();

		addOldHNum.clear();

		addNewHNum.clear();

		addAddrLine1.clear();

		addAddrLine2.clear();

		addLocality.clear();

		addCity.clear();

//		addState.getSelectionModel().clearSelection();

		addProf1.getSelectionModel().clearSelection();

		addProf2.getSelectionModel().clearSelection();

		addProf3.clear();
		addBuildingStreet.clear();
		addComments.clear();

		if (HawkerLoginController.loggedInHawker != null) {
			addHwkCode.getSelectionModel().select(HawkerLoginController.loggedInHawker.getHawkerCode());
			addHwkCode.setDisable(true);
		} else
			addHwkCode.getSelectionModel().clearSelection();
	}

	public void refreshLineDistTable() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					lineDistData.clear();
					PreparedStatement stmt;
					if (HawkerLoginController.loggedInHawker != null) {
						stmt = con.prepareStatement(
								"select ld.line_dist_id, ld.name, ld.mobile_num, ld.hawker_id, ld.line_num,ld.old_house_num, ld.new_house_num, ld.address_line1, ld.address_line2, ld.locality, ld.city, ld.state,ld.profile1,ld.profile2,ld.profile3,ld.initials, ld.employment, ld.comments, ld.building_street, hwk.HAWKER_CODE from line_distributor ld, hawker_info hwk where ld.hawker_id=hwk.hawker_id and ld.hawker_id = ? order by hwk.hawker_code, ld.line_num, ld.name");
						stmt.setLong(1, HawkerLoginController.loggedInHawker.getHawkerId());
					} else {
						stmt = con.prepareStatement(
								"select ld.line_dist_id, ld.name, ld.mobile_num, ld.hawker_id, ld.line_num,ld.old_house_num, ld.new_house_num, ld.address_line1, ld.address_line2, ld.locality, ld.city, ld.state,ld.profile1,ld.profile2,ld.profile3,ld.initials, ld.employment, ld.comments, ld.building_street, hwk.HAWKER_CODE from line_distributor ld, hawker_info hwk  where hwk.hawker_code=? and ld.hawker_id=hwk.hawker_id order by hwk.hawker_code, ld.line_num, ld.name");
						stmt.setString(1, addHwkCode.getSelectionModel().getSelectedItem());
					}

					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						lineDistData.add(new LineDistributor(rs.getLong(1), rs.getString(2), rs.getString(3),
								rs.getLong(4), rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8),
								rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13),
								rs.getString(14), rs.getString(15), rs.getString(16), rs.getString(17),
								rs.getString(18), rs.getString(19), rs.getString(20)));
					}
					// lineDistInfoTable.getItems().clear();
					if (!lineDistData.isEmpty()) {
						filteredData = new FilteredList<>(lineDistData, p -> true);
						SortedList<LineDistributor> sortedData = new SortedList<>(filteredData);
						sortedData.comparatorProperty().bind(lineDistInfoTable.comparatorProperty());
						lineDistInfoTable.setItems(sortedData);
						lineDistInfoTable.refresh();
					}
				} catch (SQLException e) {
					
					e.printStackTrace();
				}

				return null;
			}

		};
		new Thread(task).start();
	}

	public boolean lineDistForLineExists(String line_num) {
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			PreparedStatement stmt = con
					.prepareStatement("select count(*) from line_distributor where hawker_id = ? and line_num=?");
			stmt.setLong(1, HawkerLoginController.loggedInHawker!=null?HawkerLoginController.loggedInHawker.getHawkerId():hawkerIdForCode(addHwkCode.getSelectionModel().getSelectedItem()));
			stmt.setString(2, line_num);
			ResultSet rs = stmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return true;
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return false;
	}

	@FXML
	private void filterLineDistClicked(ActionEvent event) {

		TextInputDialog lineDistFilterDialog = new TextInputDialog(searchText);
		lineDistFilterDialog.setTitle("Filter Line Distribution Boys");
		lineDistFilterDialog.setHeaderText("Enter the filter text");
		Optional<String> returnValue = lineDistFilterDialog.showAndWait();
		if (returnValue.isPresent()) {
			try {
				searchText = returnValue.get();
				filteredData.setPredicate(new Predicate<LineDistributor>() {

					@Override
					public boolean test(LineDistributor lineDistributor) {
						
						if (searchText == null || searchText.isEmpty())
							return true;
						else if (lineDistributor.getAddrLine1() != null
								&& lineDistributor.getAddrLine1().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getAddrLine2() != null
								&& lineDistributor.getAddrLine2().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getCity() != null
								&& lineDistributor.getCity().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getHawkerCode().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getLocality() != null
								&& lineDistributor.getLocality().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getName() != null
								&& lineDistributor.getName().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getMobileNum() != null
								&& lineDistributor.getMobileNum().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getNewHouseNum() != null
								&& lineDistributor.getNewHouseNum().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getOldHouseNum() != null
								&& lineDistributor.getOldHouseNum().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getState() != null
								&& lineDistributor.getState().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getProfile1() != null
								&& lineDistributor.getProfile1().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getProfile2() != null
								&& lineDistributor.getProfile2().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getProfile3() != null
								&& lineDistributor.getProfile3().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (lineDistributor.getInitials() != null
								&& lineDistributor.getInitials().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if ((lineDistributor.getEmployment() + "").toUpperCase()
								.contains(searchText.toUpperCase()))
							return true;
						else if ((lineDistributor.getComments() + "").toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if ((lineDistributor.getBuildingStreet() + "").toUpperCase()
								.contains(searchText.toUpperCase()))
							return true;
						return false;

					}
				});

				SortedList<LineDistributor> sortedData = new SortedList<>(filteredData);
				sortedData.comparatorProperty().bind(lineDistInfoTable.comparatorProperty());
				lineDistInfoTable.setItems(sortedData);
				lineDistInfoTable.refresh();

			} catch (NumberFormatException e) {
				
				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid value entered")
						.text("Please enter numeric value only").showError();
			}
		}

	}

	@FXML
	private void clearClicked(ActionEvent event) {
		filteredData = new FilteredList<>(lineDistData, p -> true);
		SortedList<LineDistributor> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(lineDistInfoTable.comparatorProperty());
		lineDistInfoTable.setItems(sortedData);
		lineDistInfoTable.getSelectionModel().clearSelection();
	}
//	@Override
	public void reloadData() {
		System.out.println("Line Distributor reloadData");
		addProf1.getItems().clear();
		addProf2.getItems().clear();
		addEmployment.getItems().clear();
		addPointName.getItems().clear();
		addHwkCode.getItems().clear();
//		populateHawkerCodes();
		populateProfileValues();
		populateEmploymentValues();

		populatePointNames();
		addPointName.getItems().clear();
		addPointName.getItems().addAll(pointNameValues);

		if(HawkerLoginController.loggedInHawker!=null){
			addPointName.getSelectionModel().select(HawkerLoginController.loggedInHawker.getPointName());
			addPointName.setDisable(true);
			addHwkCode.getSelectionModel().select(HawkerLoginController.loggedInHawker.getHawkerCode());
			addHwkCode.setDisable(true);
		}
	}

	private void populateHawkerCodes() {

		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			hawkerCodeData.clear();
			PreparedStatement stmt = con.prepareStatement("select distinct hawker_code from hawker_info where point_name=?");
			stmt.setString(1, addPointName.getSelectionModel().getSelectedItem());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				hawkerCodeData.add(rs.getString(1));
			}
			addHwkCode.getItems().addAll(hawkerCodeData);
			if (HawkerLoginController.loggedInHawker != null) {
				addHwkCode.getSelectionModel().select(HawkerLoginController.loggedInHawker.getHawkerCode());
				addHwkCode.setDisable(true);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}

	private void populateProfileValues() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					profileValues.clear();
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("select value, code, seq, lov_lookup_id from lov_lookup where code='PROFILE_VALUES' order by seq");
					while (rs.next()) {
						profileValues.add(rs.getString(1));
					}
					addProf1.getItems().addAll(profileValues);
					addProf2.getItems().addAll(profileValues);
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				return null;
			}

		};
		new Thread(task).start();
	}

	private void populateEmploymentValues() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					employmentData.clear();
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("select value, code, seq, lov_lookup_id from lov_lookup where code='EMPLOYMENT_STATUS' order by seq");
					while (rs.next()) {
						employmentData.add(rs.getString(1));
					}

					addEmployment.getItems().addAll(employmentData);
				} catch (SQLException e) {
					
					e.printStackTrace();
				}
				return null;
			}

		};
		new Thread(task).start();
	}
	
	public void populatePointNames() {
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			pointNameValues.clear();
			PreparedStatement stmt = con.prepareStatement("select distinct name from point_name order by name");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				pointNameValues.add(rs.getString(1));
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}
	
//	@Override
	public void releaseVariables(){
		filteredData=null;
		searchText=null;
		hawkerCodeData = null;
		employmentData = null;
		profileValues = null;
		lineDistData = null;
		lineNumData = null;
		pointNameValues=null;
		hawkerCodeData = FXCollections.observableArrayList();
		employmentData = FXCollections.observableArrayList();
		profileValues = FXCollections.observableArrayList();
		lineNumData = FXCollections.observableArrayList();
		lineDistData = FXCollections.observableArrayList();
		pointNameValues = FXCollections.observableArrayList();
	}

}
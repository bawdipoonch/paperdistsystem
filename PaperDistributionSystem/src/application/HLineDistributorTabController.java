package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.controlsfx.control.Notifications;

import com.amazonaws.util.NumberUtils;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
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

	@FXML
	private TableView<LineDistributor> lineDistInfoTable;

	@FXML
	private TableColumn<LineDistributor, String> lineDistNameColumn;
	@FXML
	private TableColumn<LineDistributor, String> lineDistMobNumColumn;
	@FXML
	private TableColumn<LineDistributor, Integer> lineDistLineNumColumn;

	@FXML
	private TextField addNameField;
	@FXML
	private TextField addInitialsField;
	@FXML
	private TextField addMobileNumField;
	@FXML
	private ComboBox<Integer> addLineNumField;
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
	private TextField addProf1;
	@FXML
	private TextField addProf2;
	@FXML
	private TextField addProf3;
	

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

	private ObservableList<LineDistributor> lineDistData = FXCollections.observableArrayList();

	private ObservableList<Integer> lineNumData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		System.out.println("Entered HLineDistributorTabController");
		lineDistData.clear();
		lineNumData.clear();
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
		// populateLineNumbersForHawkerCode();
		// refreshLineDistTable();
		// reloadData();

		addState.getItems().addAll("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
				"Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala",
				"Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
				"Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand",
				"West Bengal");
		lineDistInfoTable.setRowFactory(new Callback<TableView<LineDistributor>, TableRow<LineDistributor>>() {

			@Override
			public TableRow<LineDistributor> call(TableView<LineDistributor> param) {
				final TableRow<LineDistributor> row = new TableRow<>();
				MenuItem mnuDel = new MenuItem("Delete line");
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
				// TODO Auto-generated catch block
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

			FXMLLoader editLineDistributorLoader = new FXMLLoader(getClass().getResource("EditLineDistributor.fxml"));
			Parent editLineDistributorGrid = (Parent) editLineDistributorLoader.load();
			EditLineDistributorController editLineDistributorController = editLineDistributorLoader.<EditLineDistributorController> getController();

			editLineDistributorDialog.getDialogPane().setContent(editLineDistributorGrid);
			editLineDistributorController.setLineDistToEdit(lineDistRow);
			editLineDistributorController.setupBindings();

			editLineDistributorDialog.setResultConverter(dialogButton -> {
				if (dialogButton == saveButtonType) {
					LineDistributor edittedLineDistributor = editLineDistributorController.returnUpdatedLineDistributor();
					return edittedLineDistributor;
				}
				return null;
			});

			Optional<LineDistributor> updatedLineDistributor = editLineDistributorDialog.showAndWait();
			// refreshCustomerTable();

			updatedLineDistributor.ifPresent(new Consumer<LineDistributor>() {

				@Override
				public void accept(LineDistributor t) {
					// TODO Auto-generated method stub
					lineDistData.add(selectedIndex, t);
					lineDistData.remove(lineDistRow);
					lineDistInfoTable.getSelectionModel().select(t);
					lineDistInfoTable.getSelectionModel().getSelectedItem().updateLineDistRecord();
				}
			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	private void addButtonClicked(ActionEvent event) {

		try {
			if (addNameField.getText() != null && addMobileNumField.getText() != null
					&& addLineNumField.getSelectionModel().selectedItemProperty().isNotNull().get()) {

				if (!lineDistForLineExists(addLineNumField.getSelectionModel().selectedItemProperty().getValue())) {
					PreparedStatement insertLineNum = null;
					String insertStatement = "INSERT INTO LINE_DISTRIBUTOR(NAME, MOBILE_NUM, LINE_NUM,HAWKER_ID,old_house_num, new_house_num, address_line1, address_line2, locality, city, state,profile1,profile2,profile3,initials) "
							+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
						insertLineNum.setString(5, addOldHNum.getText());
						insertLineNum.setString(6, addNewHNum.getText());
						insertLineNum.setString(7, addAddrLine1.getText());
						insertLineNum.setString(8, addAddrLine2.getText());
						insertLineNum.setString(9, addLocality.getText());
						insertLineNum.setString(10, addCity.getText());
						insertLineNum.setString(11, addState.getSelectionModel().getSelectedItem());
						insertLineNum.setString(12, addProf1.getText());
						insertLineNum.setString(13, addProf2.getText());
						insertLineNum.setString(14, addProf3.getText());
						insertLineNum.setString(15, addInitialsField.getText());
						insertLineNum.execute();
						resetClicked(event);
						refreshLineDistTable();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
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
		
		
		addState.getSelectionModel().clearSelection();
		
		addProf1.clear();
		
		addProf2.clear();
		
		addProf3.clear();
	}

	private void populateLineNumbersForHawkerCode() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					lineNumData.clear();
					PreparedStatement stmt = con
							.prepareStatement("select distinct line_num from line_info where hawker_id = ?");
					stmt.setLong(1, HawkerLoginController.loggedInHawker.getHawkerId());
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
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
					PreparedStatement stmt = con.prepareStatement(
							"select line_dist_id, name, mobile_num, hawker_id, line_num,old_house_num, new_house_num, address_line1, address_line2, locality, city, state,profile1,profile2,profile3,initials from line_distributor where hawker_id = ?");
					stmt.setLong(1, HawkerLoginController.loggedInHawker.getHawkerId());
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						lineDistData.add(new LineDistributor(rs.getLong(1), rs.getString(2), rs.getString(3),
								rs.getLong(4), rs.getInt(5), rs.getString(6),
								rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10), rs.getString(11),
								rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15),rs.getString(16)));
					}
					// lineDistInfoTable.getItems().clear();
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

	public boolean lineDistForLineExists(int line_num) {
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			PreparedStatement stmt = con
					.prepareStatement("select count(*) from line_distributor where hawker_id = ? and line_num=?");
			stmt.setLong(1, HawkerLoginController.loggedInHawker.getHawkerId());
			stmt.setInt(2, line_num);
			ResultSet rs = stmt.executeQuery();
			if (rs.next() && rs.getInt(1) > 0) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public void reloadData() {
		System.out.println("Line Distributor reloadData");
		refreshLineDistTable();
		populateLineNumbersForHawkerCode();
	}

}

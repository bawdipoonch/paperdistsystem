package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Calendar;
import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.controlsfx.control.Notifications;

import com.amazonaws.util.NumberUtils;

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
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.Pair;

public class AHawkerInfoTabController implements Initializable {

	@FXML
	private TableView<Hawker> hawkerTable;
	@FXML
	private TableView<HawkerBilling> hawkerBillingTable;

	// Add Hawker Fields
	@FXML
	private TextField addHwkName;
	@FXML
	private TextField addHwkCode;
	@FXML
	private TextField addHwkMob;
	@FXML
	private TextField addHwkAgencyName;
	@FXML
	private TextField addHwkFee;
	@FXML
	private TextField addHwkOldHNum;
	@FXML
	private TextField addHwkNewHNum;
	@FXML
	private TextField addHwkAddrLine1;
	@FXML
	private TextField addHwkAddrLine2;
	@FXML
	private TextField addHwkLocality;
	@FXML
	private TextField addHwkCity;
	@FXML
	private CheckBox addHwkActive;
	@FXML
	private ComboBox<String> addHwkState;

	// ScreenAccess Checkbox
	@FXML
	private CheckBox customerCheckBox;
	@FXML
	private CheckBox billingCheckBox;
	@FXML
	private CheckBox lineInfoCheckBox;
	@FXML
	private CheckBox lineDistCheckBox;
	@FXML
	private CheckBox pausedCustCheckBox;
	@FXML
	private CheckBox prodCatalogCheckBox;
	@FXML
	private CheckBox reportsCheckBox;

	// Hawker Columns
	@FXML
	private TableColumn<Hawker, String> hwkNameColumn;
	@FXML
	private TableColumn<Hawker, String> hwkCodeColumn;
	@FXML
	private TableColumn<Hawker, String> hwkMobileColumn;
	@FXML
	private TableColumn<Hawker, String> hwkAgencyColumn;
	@FXML
	private TableColumn<Hawker, Double> hwkFeeColumn;
	@FXML
	private TableColumn<Hawker, Boolean> hwkActiveColumn;
	@FXML
	private TableColumn<Hawker, Double> hwkTotalDueColumn;
	@FXML
	private TableColumn<Hawker, String> hwkOldHouseNumColumn;
	@FXML
	private TableColumn<Hawker, String> hwkNewHouseNumColumn;
	@FXML
	private TableColumn<Hawker, String> hwkAddrLine1Column;
	@FXML
	private TableColumn<Hawker, String> hwkAddrLine2Column;
	@FXML
	private TableColumn<Hawker, String> hwkLocalityColumn;
	@FXML
	private TableColumn<Hawker, String> hwkCityColumn;
	@FXML
	private TableColumn<Hawker, String> hwkStateColumn;

	// Billing columns
	@FXML
	private TableColumn<HawkerBilling, Date> hwkBillStartDateColumn;
	@FXML
	private TableColumn<HawkerBilling, Date> hwkBillEndDateColumn;
	@FXML
	private TableColumn<HawkerBilling, Double> hwkBillAmountColumn;
	@FXML
	private TableColumn<HawkerBilling, Double> hwkBillPaidColumn;
	@FXML
	private TableColumn<HawkerBilling, Double> hwkBillDueColumn;

	private String searchText;
	private FilteredList<Hawker> filteredData;

	@FXML
	private Button enableAllButton;
	@FXML
	private Button disableAllButton;

	private ObservableList<Hawker> hawkerMasterData = FXCollections.observableArrayList();
	private ObservableList<HawkerBilling> hawkerBillingMasterData = FXCollections.observableArrayList();

	public AHawkerInfoTabController() {
		super();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		// Set cell value factories

		hwkNameColumn.setCellValueFactory(new PropertyValueFactory<Hawker, String>("name"));

		hwkCodeColumn.setCellValueFactory(new PropertyValueFactory<Hawker, String>("hawkerCode"));

		hwkMobileColumn.setCellValueFactory(new PropertyValueFactory<Hawker, String>("mobileNum"));
		hwkAgencyColumn.setCellValueFactory(new PropertyValueFactory<Hawker, String>("agencyName"));
		hwkActiveColumn.setCellValueFactory(new PropertyValueFactory<Hawker, Boolean>("activeFlag"));
		/*
		 * hwkActiveColumn.setCellFactory(new
		 * Callback<TableColumn<Hawker,Boolean>, TableCell<Hawker, Boolean>>() {
		 * 
		 * @Override public TableCell<Hawker, Boolean> call(TableColumn<Hawker,
		 * Boolean> param) { // TODO Auto-generated method stub
		 * CheckBoxTableCell<Hawker, Boolean> cell = new
		 * CheckBoxTableCell<Hawker, Boolean>();
		 * cell.selectedStateCallbackProperty().set return null; } });
		 */
		hwkFeeColumn.setCellValueFactory(new PropertyValueFactory<Hawker, Double>("fee"));
		hwkTotalDueColumn.setCellValueFactory(new PropertyValueFactory<Hawker, Double>("totalDue"));
		hwkNewHouseNumColumn.setCellValueFactory(new PropertyValueFactory<Hawker, String>("newHouseNum"));
		hwkOldHouseNumColumn.setCellValueFactory(new PropertyValueFactory<Hawker, String>("oldHouseNum"));
		hwkAddrLine1Column.setCellValueFactory(new PropertyValueFactory<Hawker, String>("addrLine1"));
		hwkAddrLine2Column.setCellValueFactory(new PropertyValueFactory<Hawker, String>("addrLine2"));
		hwkLocalityColumn.setCellValueFactory(new PropertyValueFactory<Hawker, String>("locality"));
		hwkCityColumn.setCellValueFactory(new PropertyValueFactory<Hawker, String>("city"));
		hwkStateColumn.setCellValueFactory(new PropertyValueFactory<Hawker, String>("state"));

		hwkBillStartDateColumn.setCellValueFactory(new PropertyValueFactory<HawkerBilling, Date>("startDate"));
		hwkBillEndDateColumn.setCellValueFactory(new PropertyValueFactory<HawkerBilling, Date>("endDate"));
		hwkBillAmountColumn.setCellValueFactory(new PropertyValueFactory<HawkerBilling, Double>("billAmount"));
		hwkBillPaidColumn.setCellValueFactory(new PropertyValueFactory<HawkerBilling, Double>("paid"));
		hwkBillDueColumn.setCellValueFactory(new PropertyValueFactory<HawkerBilling, Double>("due"));

		addHwkState.getItems().addAll("Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar", "Chhattisgarh", "Goa",
				"Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand", "Karnataka", "Kerala",
				"Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha", "Punjab",
				"Rajasthan", "Sikkim", "Tamil Nadu", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand",
				"West Bengal");
		if (hawkerTable.getSelectionModel().getSelectedItem() == null) {
			disableAllChild();
		}
		hawkerTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Hawker>() {

			@Override
			public void changed(ObservableValue<? extends Hawker> observable, Hawker oldValue, Hawker newValue) {
				if (newValue != null) {
					if (oldValue != null) {
						// TODO Auto-generated method stub
						oldValue.updateHawkerRecord();
					}
					enableAllChild();
					refreshHawkerBilling(newValue.getHawkerId());
					customerCheckBox.setSelected(newValue.getCustomerAccess().equals("Y") ? true : false);
					billingCheckBox.setSelected(newValue.getBillingAccess().equals("Y") ? true : false);
					lineInfoCheckBox.setSelected(newValue.getLineInfoAccess().equals("Y") ? true : false);
					lineDistCheckBox.setSelected(newValue.getLineDistAccess().equals("Y") ? true : false);
					pausedCustCheckBox.setSelected(newValue.getPausedCustAccess().equals("Y") ? true : false);
					prodCatalogCheckBox.setSelected(newValue.getProductAccess().equals("Y") ? true : false);
					reportsCheckBox.setSelected(newValue.getReportsAccess().equals("Y") ? true : false);
				} else
					disableAllChild();
			}
		});

		hawkerBillingTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<HawkerBilling>() {

			@Override
			public void changed(ObservableValue<? extends HawkerBilling> observable, HawkerBilling oldValue,
					HawkerBilling newValue) {
				// TODO Auto-generated method stub

			}
		});
		customerCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				hawkerTable.getSelectionModel().getSelectedItem().setCustomerAccess(newValue ? "Y" : "N");
				hawkerTable.getSelectionModel().getSelectedItem().updateHawkerRecord();
			}
		});
		billingCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				hawkerTable.getSelectionModel().getSelectedItem().setBillingAccess(newValue ? "Y" : "N");
				hawkerTable.getSelectionModel().getSelectedItem().updateHawkerRecord();
			}
		});
		lineInfoCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				hawkerTable.getSelectionModel().getSelectedItem().setLineInfoAccess(newValue ? "Y" : "N");
				hawkerTable.getSelectionModel().getSelectedItem().updateHawkerRecord();
			}
		});
		lineDistCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				hawkerTable.getSelectionModel().getSelectedItem().setLineDistAccess(newValue ? "Y" : "N");
				hawkerTable.getSelectionModel().getSelectedItem().updateHawkerRecord();
			}
		});
		pausedCustCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				hawkerTable.getSelectionModel().getSelectedItem().setPausedCustAccess(newValue ? "Y" : "N");
				hawkerTable.getSelectionModel().getSelectedItem().updateHawkerRecord();
			}
		});
		prodCatalogCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				hawkerTable.getSelectionModel().getSelectedItem().setProductAccess(newValue ? "Y" : "N");
				hawkerTable.getSelectionModel().getSelectedItem().updateHawkerRecord();
			}
		});
		reportsCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				// TODO Auto-generated method stub
				hawkerTable.getSelectionModel().getSelectedItem().setReportsAccess(newValue ? "Y" : "N");
				hawkerTable.getSelectionModel().getSelectedItem().updateHawkerRecord();
			}
		});

		hawkerTable.setRowFactory(new Callback<TableView<Hawker>, TableRow<Hawker>>() {

			@Override
			public TableRow<Hawker> call(TableView<Hawker> param) {
				// TODO Auto-generated method stub
				final TableRow<Hawker> row = new TableRow<>();
				MenuItem mnuDel = new MenuItem("Delete hawker");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Hawker hawkerRow = hawkerMasterData.get(hawkerTable.getSelectionModel().getSelectedIndex());
						if (hawkerRow != null) {
							deleteHawker(hawkerRow);
							hawkerTable.refresh();
						}
					}

				});

				MenuItem mnuEdit = new MenuItem("Edit hawker");
				mnuEdit.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Hawker hawkerRow = hawkerMasterData.get(hawkerTable.getSelectionModel().getSelectedIndex());
						if (hawkerRow != null) {
							showEditHawkerDialog(hawkerRow);
							hawkerTable.refresh();
						}
					}

				});

				/*
				 * MenuItem mnuBill = new MenuItem("Generate bill");
				 * mnuBill.setOnAction(new EventHandler<ActionEvent>() {
				 * 
				 * @Override public void handle(ActionEvent t) { Hawker
				 * hawkerRow =
				 * hawkerMasterData.get(hawkerTable.getSelectionModel().
				 * getSelectedIndex()); if (hawkerRow != null){
				 * generateBill(hawkerRow); hawkerBillingTable.refresh(); } }
				 * 
				 * });
				 */
				ContextMenu menu = new ContextMenu();
				menu.getItems().addAll(mnuEdit, mnuDel);
				row.contextMenuProperty().bind(
						Bindings.when(Bindings.isNotNull(row.itemProperty())).then(menu).otherwise((ContextMenu) null));
				return row;
			}
		});

		hawkerBillingTable.setRowFactory(new Callback<TableView<HawkerBilling>, TableRow<HawkerBilling>>() {

			@Override
			public TableRow<HawkerBilling> call(TableView<HawkerBilling> arg0) {
				// TODO Auto-generated method stub
				final TableRow<HawkerBilling> row = new TableRow<HawkerBilling>();

				MenuItem mnuDel = new MenuItem("Delete Bill");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						HawkerBilling hwkBillRow = hawkerBillingTable.getSelectionModel().getSelectedItem();
						if (hwkBillRow != null) {
							deleteHawkerBill(hwkBillRow);
							Hawker hawkerRow = hawkerMasterData.get(hawkerTable.getSelectionModel().getSelectedIndex());
							hawkerRow.calculateTotalDue();
							hawkerBillingTable.refresh();
							hawkerTable.refresh();
							hawkerTable.refresh();
						}
					}

				});

				MenuItem updatePaid = new MenuItem("Update Payment Recieved");
				updatePaid.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						// TODO Auto-generated method stub
						HawkerBilling hwkBillRow = hawkerBillingTable.getSelectionModel().getSelectedItem();
						TextInputDialog payRcvdDialog = new TextInputDialog(
								"" + hawkerTable.getSelectionModel().getSelectedItem().getTotalDue());
						payRcvdDialog.setTitle("Payment recieved");
						payRcvdDialog.setHeaderText("Enter the payment recieved amount");
						Optional<String> returnValue = payRcvdDialog.showAndWait();
						if (returnValue.isPresent()) {
							try {
								Double rcvdValue = Double.parseDouble(returnValue.get());
								hwkBillRow.setPaid(rcvdValue.doubleValue());
								hwkBillRow.updateHawkerBillingRecord();
								Hawker hawkerRow = hawkerMasterData
										.get(hawkerTable.getSelectionModel().getSelectedIndex());
								hawkerRow.calculateTotalDue();
								hawkerBillingTable.refresh();
								hawkerTable.refresh();
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid value entered")
								.text("Please enter numeric value only").showError();
							}
						}
					}
				});
				ContextMenu menu = new ContextMenu();
				menu.getItems().addAll(mnuDel, updatePaid);
				row.contextMenuProperty().bind(
						Bindings.when(Bindings.isNotNull(row.itemProperty())).then(menu).otherwise((ContextMenu) null));
				return row;
			}
		});

		// refreshHawkerTable();

	}

	/*
	 * private void generateBill(Hawker hawkerRow) { // TODO Auto-generated
	 * method stub Date[] currentMonthDR = getDateRange(); PreparedStatement
	 * insertHawker = null; String insertStatement =
	 * "INSERT INTO HAWKER_BILLING(hawker_id, start_date, end_date, bill_amount, paid, due) "
	 * + "VALUES (?,?,?,?,?,?)"; Connection con = Main.dbConnection; try {
	 * while(!con.isValid(0)){ con = Main.reconnect(); } insertHawker =
	 * con.prepareStatement(insertStatement); insertHawker.setLong(1,
	 * hawkerRow.getHawkerId()); insertHawker.setDate(2, currentMonthDR[0]);
	 * insertHawker.setDate(3, currentMonthDR[1]); insertHawker.setDouble(4,
	 * 100); insertHawker.setDouble(5, 100); insertHawker.setDouble(6, 0);
	 * insertHawker.execute(); hawkerMasterData.add(new
	 * Hawker(addHwkName.getText(),addHwkCode.getText(),addHwkMob.getText(),
	 * addHwkAgencyName.getText(),addHwkActive.isSelected(), d.doubleValue(),
	 * addHwkOldHNum.getText(),addHwkNewHNum.getText(),addHwkAddrLine1.getText()
	 * ,addHwkAddrLine2.getText(),addHwkLocality.getText(),
	 * addHwkCity.getText(),addHwkState.getSelectionModel().getSelectedItem()));
	 * refreshHawkerBilling(hawkerRow.getHawkerId());
	 * hawkerRow.calculateTotalDue(); hawkerRow.updateHawkerRecord(); //
	 * hawkerTable.refresh(); con.commit();
	 * 
	 * } catch (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace();
	 * Notifications.create().hideAfter(Duration.seconds(5)).title("Error!").
	 * text(
	 * "There has been some error during hawker bill creation, please retry"
	 * ).showError(); Main.reconnect(); }
	 * 
	 * }
	 */

	private void deleteHawkerBill(HawkerBilling hwkBillRow) {
		// TODO Auto-generated method stub
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
				String deleteString = "delete from hawker_billing where hwk_bill_id=?";
				PreparedStatement deleteStmt = con.prepareStatement(deleteString);
				deleteStmt.setLong(1, hwkBillRow.getHwkBillId());

				deleteStmt.executeUpdate();
				con.commit();

				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete Successful")
				.text("Deletion of hawker bill was successful").showInformation();
				hawkerBillingMasterData.remove(hwkBillRow);
				hawkerBillingTable.refresh();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed")
				.text("Delete request of hawker bill has failed").showError();
			}
		}
	}

	private void deleteHawker(Hawker hawkerRow) {
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
				String deleteString = "delete from hawker_info where hawker_id=?";
				PreparedStatement deleteStmt = con.prepareStatement(deleteString);
				deleteStmt.setLong(1, hawkerRow.getHawkerId());

				deleteStmt.executeUpdate();
				con.commit();

				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete Successful")
				.text("Deletion of hawker was successful").showInformation();
				hawkerMasterData.remove(hawkerRow);
				hawkerTable.refresh();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed")
				.text("Delete request of hawker has failed").showError();
			}
		}

	}

	private void showEditHawkerDialog(Hawker hawkerRow) {
		int selectedIndex = hawkerTable.getSelectionModel().selectedIndexProperty().get();
		try {

			Dialog<Hawker> editHawkerDialog = new Dialog<Hawker>();
			editHawkerDialog.setTitle("Edit hawker data");
			editHawkerDialog.setHeaderText("Update the hawker data below");

			// Set the button types.
			ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
			editHawkerDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

			FXMLLoader editHawkerLoader = new FXMLLoader(getClass().getResource("EditHawker.fxml"));
			Parent editHawkerGrid = (Parent) editHawkerLoader.load();
			EditHawkerController editHawkerController = editHawkerLoader.<EditHawkerController> getController();

			editHawkerDialog.getDialogPane().setContent(editHawkerGrid);
			editHawkerController.setHawkerToEdit(hawkerRow);
			editHawkerController.setupBindings();

			editHawkerDialog.setResultConverter(dialogButton -> {
				if (dialogButton == saveButtonType) {
					Hawker edittedHawker = editHawkerController.returnUpdatedHawker();
					return edittedHawker;
				}
				return null;
			});

			Optional<Hawker> updatedHawker = editHawkerDialog.showAndWait();
			// refreshCustomerTable();

			updatedHawker.ifPresent(new Consumer<Hawker>() {

				@Override
				public void accept(Hawker t) {
					// TODO Auto-generated method stub
					hawkerMasterData.add(selectedIndex, t);
					hawkerMasterData.remove(hawkerRow);
					hawkerTable.getSelectionModel().select(t);
					hawkerTable.getSelectionModel().getSelectedItem().updateHawkerRecord();
				}
			});

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void refreshHawkerTable() {
		
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					hawkerMasterData.clear();
					Statement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery(
							"select hawker_id,name,hawker_code, mobile_num, agency_name, active_flag, fee, old_house_num, new_house_num, addr_line1, addr_line2, locality, city, state,customer_access, billing_access, line_info_access, line_dist_access, paused_cust_access, product_access, reports_access  from hawker_info");
					while (rs.next()) {
						Hawker hwkRow = new Hawker(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4),
								rs.getString(5), rs.getString(6).equalsIgnoreCase("Y"), rs.getDouble(7), rs.getString(8),
								rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13),
								rs.getString(14), rs.getString(15), rs.getString(16), rs.getString(17), rs.getString(18),
								rs.getString(19), rs.getString(20), rs.getString(21));
						hwkRow.calculateTotalDue();
						hawkerMasterData.add(hwkRow);
					}
					// con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (!hawkerMasterData.isEmpty()) {
					filteredData = new FilteredList<>(hawkerMasterData, p -> true);
					SortedList<Hawker> sortedData = new SortedList<>(filteredData);
					sortedData.comparatorProperty().bind(hawkerTable.comparatorProperty());
					hawkerTable.setItems(sortedData);
				}
				hawkerTable.refresh();
				return null;
			}
			
		};
		
		new Thread(task).start();
		
	}

	private void disableAllChild() {
		// TODO Auto-generated method stub
		customerCheckBox.setDisable(true);
		billingCheckBox.setDisable(true);
		lineInfoCheckBox.setDisable(true);
		lineDistCheckBox.setDisable(true);
		pausedCustCheckBox.setDisable(true);
		prodCatalogCheckBox.setDisable(true);
		reportsCheckBox.setDisable(true);
		enableAllButton.setDisable(true);
		disableAllButton.setDisable(true);
		hawkerBillingTable.setDisable(true);

		/*
		 * customerCheckBox.setSelected(false);
		 * billingCheckBox.setSelected(false);
		 * lineInfoCheckBox.setSelected(false);
		 * lineDistCheckBox.setSelected(false);
		 * pausedCustCheckBox.setSelected(false);
		 * prodCatalogCheckBox.setSelected(false);
		 * reportsCheckBox.setSelected(false);
		 */
	}

	private void enableAllChild() {
		// TODO Auto-generated method stub
		customerCheckBox.setDisable(false);
		billingCheckBox.setDisable(false);
		lineInfoCheckBox.setDisable(false);
		lineDistCheckBox.setDisable(false);
		pausedCustCheckBox.setDisable(false);
		prodCatalogCheckBox.setDisable(false);
		reportsCheckBox.setDisable(false);
		enableAllButton.setDisable(false);
		disableAllButton.setDisable(false);
		hawkerBillingTable.setDisable(false);
	}

	protected void refreshHawkerBilling(Long hawkerId) {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					String getHwkBillingInfo = "Select start_date,end_date,bill_amount,paid,due,hwk_bill_id, hawker_id from hawker_billing where hawker_id = ?";
					PreparedStatement hwkBillInfo = null;
					hwkBillInfo = con.prepareStatement(getHwkBillingInfo);
					hwkBillInfo.setLong(1, hawkerId);
					ResultSet rs = hwkBillInfo.executeQuery();
					hawkerBillingMasterData.clear();
					while (rs.next()) {
						hawkerBillingMasterData.add(new HawkerBilling(hawkerId, rs.getDate(1), rs.getDate(2), rs.getDouble(3),
								rs.getDouble(4), rs.getDouble(5), rs.getLong(6)));
					}
					hawkerBillingTable.setItems(hawkerBillingMasterData);
					hawkerBillingTable.refresh();
					// con.close();

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			
		};
		
	}

	@FXML
	private void addHawkerClicked(ActionEvent event) {
		System.out.println("addCustomerClicked");
		if (!validateHawkerCodeExists(addHwkCode.getText())) {
			Notifications.create().title("Hawker already exists")
			.text("Hawker with same Hawker Code alraedy exists. Please choose different hawker code")
			.hideAfter(Duration.seconds(5)).showError();
		} else {
			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					boolean validate = true;
					if (addHwkName.getText() == null) {
						validate = false;
						Notifications.create().hideAfter(Duration.seconds(5)).title("Hawker not selected")
						.text("Please select a hawker before adding the the customer").showError();
					}
					// if(addCustLineNum.getSelectionModel().selectedIndexProperty().get()!=-1)
					// validate=true;
					if (NumberUtils.tryParseInt(addHwkFee.getText()) == null) {
						validate = false;
						Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid fee")
						.text("Fee per subscription should not be empty and must be numeric only").showError();
					}

					if (validate) {
						PreparedStatement insertHawker = null;
						String insertStatement = "INSERT INTO HAWKER_INFO(NAME,HAWKER_CODE, MOBILE_NUM, AGENCY_NAME,FEE,ACTIVE_FLAG, OLD_HOUSE_NUM, NEW_HOUSE_NUM,ADDR_LINE1,ADDR_LINE2,LOCALITY,CITY,STATE,customer_access, billing_access, line_info_access, line_dist_access, paused_cust_access, product_access, reports_access) "
								+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
						Connection con = Main.dbConnection;
						try {
							while (!con.isValid(0)) {
								con = Main.reconnect();
							}
							insertHawker = con.prepareStatement(insertStatement);
							insertHawker.setString(1, addHwkName.getText());
							insertHawker.setString(2, addHwkCode.getText());
							insertHawker.setString(3, addHwkMob.getText());
							insertHawker.setString(4, addHwkAgencyName.getText());
							insertHawker.setDouble(5,
									Double.parseDouble(addHwkFee.getText() != null ? addHwkFee.getText() : "0"));
							insertHawker.setString(6, addHwkActive.isSelected() == true ? "Y" : "N");
							insertHawker.setString(7, addHwkOldHNum.getText());
							insertHawker.setString(8, addHwkNewHNum.getText());
							insertHawker.setString(9, addHwkAddrLine1.getText());
							insertHawker.setString(10, addHwkAddrLine2.getText());
							insertHawker.setString(11, addHwkLocality.getText());
							insertHawker.setString(12, addHwkCity.getText());
							insertHawker.setString(13, addHwkState.getSelectionModel().getSelectedItem());
							insertHawker.setString(14, "N");
							insertHawker.setString(15, "N");
							insertHawker.setString(16, "N");
							insertHawker.setString(17, "N");
							insertHawker.setString(18, "N");
							insertHawker.setString(19, "N");
							insertHawker.setString(20, "N");

							insertHawker.execute();
							Double d = Double.parseDouble(addHwkFee.getText());
							/*
							 * hawkerMasterData.add(new
							 * Hawker(addHwkName.getText(),addHwkCode.getText(),
							 * addHwkMob.getText(),addHwkAgencyName.getText(),
							 * addHwkActive.isSelected(), d.doubleValue(),
							 * addHwkOldHNum.getText(),addHwkNewHNum.getText(),
							 * addHwkAddrLine1.getText(),addHwkAddrLine2.getText(),
							 * addHwkLocality.getText(),
							 * addHwkCity.getText(),addHwkState.getSelectionModel().
							 * getSelectedItem()));
							 */
							refreshHawkerTable();
							// hawkerTable.refresh();
							con.commit();
							// con.close();

						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Notifications.create().hideAfter(Duration.seconds(5)).title("Error!")
							.text("There has been some error during hawker creation, please retry").showError();
							Main.reconnect();
						}
						resetClicked(event);
					}
					return null;
				}
				
			};
			
			new Thread(task).start();
			
		}

	}

	private boolean validateHawkerCodeExists(String text) {

		for (Hawker hwk : hawkerMasterData) {
			if (hwk.getHawkerCode().equalsIgnoreCase(text)) {
				return false;
			}
		}
		return true;
	}

	@FXML
	private void resetClicked(ActionEvent event) {
		System.out.println("resetClicked");
		addHwkName.clear();
		addHwkCode.clear();
		addHwkMob.clear();
		addHwkAgencyName.clear();
		addHwkFee.clear();
		addHwkOldHNum.clear();
		addHwkNewHNum.clear();
		addHwkAddrLine1.clear();
		addHwkAddrLine2.clear();
		addHwkLocality.clear();
		addHwkCity.clear();
		addHwkActive.setSelected(false);
		addHwkState.setValue("State");
	}

	@FXML
	private void enableAllClicked(ActionEvent event) {
		customerCheckBox.setSelected(true);
		billingCheckBox.setSelected(true);
		lineInfoCheckBox.setSelected(true);
		lineDistCheckBox.setSelected(true);
		pausedCustCheckBox.setSelected(true);
		prodCatalogCheckBox.setSelected(true);
		reportsCheckBox.setSelected(true);
	}

	@FXML
	private void disableAllClicked(ActionEvent event) {
		customerCheckBox.setSelected(false);
		billingCheckBox.setSelected(false);
		lineInfoCheckBox.setSelected(false);
		lineDistCheckBox.setSelected(false);
		pausedCustCheckBox.setSelected(false);
		prodCatalogCheckBox.setSelected(false);
		reportsCheckBox.setSelected(false);
	}

	@FXML
	private void filterHawkersClicked(ActionEvent event) {

		TextInputDialog hawkerFilterDialog = new TextInputDialog(searchText);
		hawkerFilterDialog.setTitle("Filter Hawkers");
		hawkerFilterDialog.setHeaderText("Enter the filter text");
		Optional<String> returnValue = hawkerFilterDialog.showAndWait();
		if (returnValue.isPresent()) {
			try {
				searchText = returnValue.get();
				filteredData.setPredicate(new Predicate<Hawker>() {

					@Override
					public boolean test(Hawker hawker) {
						// TODO Auto-generated method stub
						if (searchText == null || searchText.isEmpty())
							return true;
						else if (hawker.getAddrLine1().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getAddrLine2().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getAgencyName().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getCity().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getHawkerCode().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getLocality().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getMobileNum().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getName().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getNewHouseNum().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getOldHouseNum().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (hawker.getState().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						return false;
					}
				});

				SortedList<Hawker> sortedData = new SortedList<>(filteredData);
				sortedData.comparatorProperty().bind(hawkerTable.comparatorProperty());
				hawkerTable.setItems(sortedData);
				disableAllChild();

			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid value entered")
				.text("Please enter numeric value only").showError();
			}
		}

	}

	@FXML
	private void clearClicked(ActionEvent event) {
		filteredData = new FilteredList<>(hawkerMasterData, p -> true);
		SortedList<Hawker> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(hawkerTable.comparatorProperty());
		hawkerTable.setItems(sortedData);
		hawkerTable.getSelectionModel().clearSelection();
		disableAllChild();
	}

	public void reloadData() {
		// TODO Auto-generated method stub
		refreshHawkerTable();
	}

}

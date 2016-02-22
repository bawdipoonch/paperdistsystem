package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
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
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;

public class ACustomerInfoTabController implements Initializable {

	@FXML
	private TableView<Customer> ACustInfoTable;

	@FXML
	private TextField addCustName;
	@FXML
	private TextField addCustInitials;
	@FXML
	private TextField addCustMobile;
	@FXML
	private ComboBox<String> addCustLineNum;
	@FXML
	private ComboBox<String> addCustHwkCode;
	@FXML
	private TextField addCustHouseSeq;
	@FXML
	private TextField addCustOldHouseNum;
	@FXML
	private TextField addCustNewHouseNum;
	@FXML
	private TextField addCustAddrLine1;
	@FXML
	private TextField addCustAddrLine2;
	@FXML
	private TextField addCustLocality;
	@FXML
	private TextField addCustCity;
	@FXML
	private ComboBox<String> addCustState;
	@FXML
	private ComboBox<String> addCustProf1;
	@FXML
	private ComboBox<String> addCustProf2;
	@FXML
	private TextField addCustProf3;
	@FXML
	private ComboBox<String> addCustEmployment;
	@FXML
	private TextField addCustComments;
	@FXML
	private TextField addCustBuildingStreet;

	// Columns
	@FXML
	private TableColumn<Customer, Long> customerCodeColumn;
	@FXML
	private TableColumn<Customer, String> customerNameColumn;
	@FXML
	private TableColumn<Customer, String> customerInitialsColumn;
	@FXML
	private TableColumn<Customer, String> mobileNumColumn;
	@FXML
	private TableColumn<Customer, String> hawkerCodeColumn;
	@FXML
	private TableColumn<Customer, String> lineNumColumn;
	@FXML
	private TableColumn<Customer, Long> houseSeqColumn;
	@FXML
	private TableColumn<Customer, String> oldHouseNumColumn;
	@FXML
	private TableColumn<Customer, String> newHouseNumColumn;
	@FXML
	private TableColumn<Customer, String> addrLine1Column;
	@FXML
	private TableColumn<Customer, String> addrLine2Column;
	@FXML
	private TableColumn<Customer, String> localityColumn;
	@FXML
	private TableColumn<Customer, String> cityColumn;
	@FXML
	private TableColumn<Customer, String> stateColumn;
	@FXML
	private TableColumn<Customer, String> profile1Column;
	@FXML
	private TableColumn<Customer, String> profile2Column;
	@FXML
	private TableColumn<Customer, String> profile3Column;
	@FXML
	private TableColumn<Customer, String> employmentColumn;
	@FXML
	private TableColumn<Customer, String> commentsColumn;
	@FXML
	private TableColumn<Customer, String> buildingStreetColumn;

	@FXML
	private TableView<Subscription> subscriptionsTable;

	@FXML
	private TableColumn<Subscription, Long> subsIdColumn;
	@FXML
	private TableColumn<Subscription, String> subsProdNameColumn;

	@FXML
	private TableColumn<Subscription, String> subsProdTypeColumn;

	@FXML
	private TableColumn<Subscription, String> subsTypeColumn;

	@FXML
	private TableColumn<Subscription, String> subsPaymentTypeColumn;

	@FXML
	private TableColumn<Subscription, Double> subPriceColumn;

	@FXML
	private TableColumn<Subscription, Double> subsServiceChargeColumn;

	@FXML
	private TableColumn<Subscription, String> subsFreqColumn;

	@FXML
	private TableColumn<Subscription, String> subsDOWColumn;

	@FXML
	private TableColumn<Subscription, String> subsStatusColumn;

	@FXML
	private TableColumn<Subscription, LocalDate> subsStartDateColumn;

	@FXML
	private TableColumn<Subscription, LocalDate> subsPausedDateColumn;
	@FXML
	private TableColumn<Subscription, String> subsNumberColumn;

	@FXML
	private VBox billingVBOX;

	private FilteredList<Customer> filteredData;
	private String searchText;

	@FXML
	private Button addCustExtraButton;
	@FXML
	private Button saveCustomerButton;
	@FXML
	private Button resetButton;
	@FXML
	private Button searchButton;
	@FXML
	private Button clearButton;

	private ObservableList<Customer> customerMasterData = FXCollections.observableArrayList();
	private ObservableList<Subscription> subscriptionMasterData = FXCollections.observableArrayList();
	private ObservableList<String> hawkerCodeData = FXCollections.observableArrayList();
	private ObservableList<String> hawkerLineNumData = FXCollections.observableArrayList();
	private ObservableList<String> employmentData = FXCollections.observableArrayList();
	private ObservableList<String> profileValues = FXCollections.observableArrayList();

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		System.out.println("Entered ACustomerInfoTabController");

		// Set cell value factories
		customerCodeColumn.setCellValueFactory(new PropertyValueFactory<Customer, Long>("customerCode"));
		customerNameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
		customerInitialsColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("initials"));
		hawkerCodeColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("hawkerCode"));
		lineNumColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("lineNum"));
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
		employmentColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("employment"));
		commentsColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("comments"));
		buildingStreetColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("buildingStreet"));

		subsIdColumn.setCellValueFactory(new PropertyValueFactory<Subscription, Long>("subscriptionId"));
		subsProdNameColumn.setCellValueFactory(new PropertyValueFactory<Subscription, String>("productName"));
		subsProdTypeColumn.setCellValueFactory(new PropertyValueFactory<Subscription, String>("productType"));
		subsPaymentTypeColumn.setCellValueFactory(new PropertyValueFactory<Subscription, String>("paymentType"));
		subsTypeColumn.setCellValueFactory(new PropertyValueFactory<Subscription, String>("subscriptionType"));
		subPriceColumn.setCellValueFactory(new PropertyValueFactory<Subscription, Double>("cost"));
		subsServiceChargeColumn.setCellValueFactory(new PropertyValueFactory<Subscription, Double>("serviceCharge"));
		subsFreqColumn.setCellValueFactory(new PropertyValueFactory<Subscription, String>("frequency"));
		subsDOWColumn.setCellValueFactory(new PropertyValueFactory<Subscription, String>("dow"));
		subsStatusColumn.setCellValueFactory(new PropertyValueFactory<Subscription, String>("status"));
		subsStatusColumn.setCellValueFactory(new PropertyValueFactory<Subscription, String>("status"));
		subsStartDateColumn.setCellValueFactory(new PropertyValueFactory<Subscription, LocalDate>("startDate"));
		subsPausedDateColumn.setCellValueFactory(new PropertyValueFactory<Subscription, LocalDate>("pausedDate"));
		subsNumberColumn.setCellValueFactory(new PropertyValueFactory<Subscription, String>("subNumber"));
		subsPausedDateColumn.setCellFactory(
				new Callback<TableColumn<Subscription, LocalDate>, TableCell<Subscription, LocalDate>>() {

					@Override
					public TableCell<Subscription, LocalDate> call(TableColumn<Subscription, LocalDate> param) {
						TextFieldTableCell<Subscription, LocalDate> cell = new TextFieldTableCell<Subscription, LocalDate>();
						cell.setConverter(Main.dateConvertor);
						return cell;
					}
				});

		subsStartDateColumn.setCellFactory(
				new Callback<TableColumn<Subscription, LocalDate>, TableCell<Subscription, LocalDate>>() {

					@Override
					public TableCell<Subscription, LocalDate> call(TableColumn<Subscription, LocalDate> param) {
						TextFieldTableCell<Subscription, LocalDate> cell = new TextFieldTableCell<Subscription, LocalDate>();
						cell.setConverter(Main.dateConvertor);
						return cell;
					}
				});
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

		addCustState.getItems().addAll("Tamil Nadu", "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
				"Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh", "Jammu and Kashmir", "Jharkhand",
				"Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra", "Manipur", "Meghalaya", "Mizoram", "Nagaland",
				"Odisha", "Punjab", "Rajasthan", "Sikkim", "Telangana", "Tripura", "Uttar Pradesh", "Uttarakhand",
				"West Bengal");

		ACustInfoTable.setRowFactory(new Callback<TableView<Customer>, TableRow<Customer>>() {

			@Override
			public TableRow<Customer> call(TableView<Customer> param) {

				final TableRow<Customer> row = new TableRow<>();
				MenuItem mnuDel = new MenuItem("Delete customer");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Customer custRow = customerMasterData
								.get(ACustInfoTable.getSelectionModel().getSelectedIndex());
						if (custRow != null) {
							deleteCustomer(custRow);
							ACustInfoTable.refresh();
						}
					}

				});

				MenuItem mnuEdit = new MenuItem("Edit customer");
				mnuEdit.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Customer custRow = customerMasterData
								.get(ACustInfoTable.getSelectionModel().getSelectedIndex());
						if (custRow != null) {
							showEditCustomerDialog(custRow);
							ACustInfoTable.refresh();
						}
					}

				});
				MenuItem mnuSubs = new MenuItem("Add subscription");
				mnuSubs.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Customer custRow = customerMasterData
								.get(ACustInfoTable.getSelectionModel().getSelectedIndex());
						if (custRow != null) {
							addCustSubscription(custRow);
							refreshSubscriptions();
						}
					}

				});
				ContextMenu menu = new ContextMenu();
				menu.getItems().addAll(mnuEdit, mnuDel, mnuSubs);
				row.contextMenuProperty().bind(
						Bindings.when(Bindings.isNotNull(row.itemProperty())).then(menu).otherwise((ContextMenu) null));
				return row;
			}
		});

		addCustExtraButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					addCustomerExtraScreenClicked(new ActionEvent());
				}
			}
		});

		addCustInitials.textProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (newValue.length() > 3)
					addCustInitials.setText(oldValue);
			}
		});

		saveCustomerButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					addCustomerClicked(new ActionEvent());
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
		searchButton.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					filterCustomersClicked(new ActionEvent());
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

		subscriptionsTable.setRowFactory(new Callback<TableView<Subscription>, TableRow<Subscription>>() {

			@Override
			public TableRow<Subscription> call(TableView<Subscription> param) {
				final TableRow<Subscription> row = new TableRow<Subscription>();
				MenuItem mnuDel = new MenuItem("Delete Subscription");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Subscription subsRow = subscriptionsTable.getSelectionModel().getSelectedItem();
						if (subsRow != null) {
							deleteSubscription(subsRow);
							// refreshSubscriptions();
						}
					}

				});

				MenuItem mnuEdit = new MenuItem("Edit Subscription");
				mnuEdit.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Subscription subsRow = subscriptionsTable.getSelectionModel().getSelectedItem();
						if (subsRow != null) {
							showEditSubscriptionDialog(subsRow);
							// refreshSubscriptions();
						}
					}

				});

				MenuItem mnuPause = new MenuItem("Pause Subscription");
				mnuPause.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Subscription subsRow = subscriptionsTable.getSelectionModel().getSelectedItem();
						if (subsRow != null && subsRow.getStatus().equals("Active")) {
							Dialog<ButtonType> pauseWarning = new Dialog<ButtonType>();
							pauseWarning.setTitle("Warning");
							pauseWarning.setHeaderText("Are you sure you want to PAUSE this subscription?");
							pauseWarning.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
							GridPane grid = new GridPane();
							grid.setHgap(10);
							grid.setVgap(10);
							grid.setPadding(new Insets(20, 150, 10, 10));

							grid.add(new Label("Pause Date"), 0, 0);
							DatePicker dp = new DatePicker(LocalDate.now());
							dp.setConverter(Main.dateConvertor);

							grid.add(dp, 1, 0);
							pauseWarning.getDialogPane().setContent(grid);
							Button yesButton = (Button) pauseWarning.getDialogPane().lookupButton(ButtonType.YES);
							yesButton.addEventFilter(ActionEvent.ACTION, btnEvent -> {
								if (dp.getValue().isBefore(subsRow.getStartDate())) {
									Notifications.create().title("Invalid paused date")
											.text("Paused date should not be before Start date for subscription")
											.hideAfter(Duration.seconds(5)).showError();
									btnEvent.consume();
								}
							});
							Optional<ButtonType> result = pauseWarning.showAndWait();
							if (result.isPresent() && result.get() == ButtonType.YES) {
								subsRow.setStatus("Paused");
								subsRow.setPausedDate(dp.getValue());
								subsRow.updateSubscriptionRecord();
								refreshSubscriptions();
							}
						} else {
							Notifications.create().title("Invalid operation").text("Subscription is already PAUSED")
									.hideAfter(Duration.seconds(5)).showWarning();
						}

					}

				});

				MenuItem mnuResume = new MenuItem("Resume Subscription");
				mnuResume.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Subscription subsRow = subscriptionsTable.getSelectionModel().getSelectedItem();
						if (subsRow != null && subsRow.getStatus().equals("Paused")) {
							Dialog<ButtonType> deleteWarning = new Dialog<ButtonType>();
							deleteWarning.setTitle("Warning");
							deleteWarning.setHeaderText("Are you sure you want to RESUME this record?");
							deleteWarning.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
							Optional<ButtonType> result = deleteWarning.showAndWait();
							if (result.isPresent() && result.get() == ButtonType.YES) {
								subsRow.setStatus("Active");
								subsRow.setPausedDate(null);
								subsRow.updateSubscriptionRecord();
								refreshSubscriptions();
							}
						} else {
							Notifications.create().title("Invalid operation").text("Subscription is already ACTIVE")
									.hideAfter(Duration.seconds(5)).showWarning();
						}
					}

				});
				ContextMenu menu = new ContextMenu();
				/*
				 * switch(param.getSelectionModel().getSelectedItem().getStatus(
				 * )){ case "Active": menu.getItems().addAll(mnuEdit,
				 * mnuDel,mnuPause,mnuResume); break; case "Paused":
				 * menu.getItems().addAll(mnuEdit, mnuDel,mnuResume); break; }
				 */
				menu.getItems().addAll(mnuEdit, mnuDel, mnuPause, mnuResume);
				row.contextMenuProperty().bind(
						Bindings.when(Bindings.isNotNull(row.itemProperty())).then(menu).otherwise((ContextMenu) null));
				return row;
			}
		});

		ACustInfoTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Customer>() {

			@Override
			public void changed(ObservableValue<? extends Customer> observable, Customer oldValue, Customer newValue) {
				if (newValue != null) {
					refreshSubscriptions();
				}

			}
		});

		billingVBOX.setVisible(false);

		// ACustInfoTable.setContextMenu(menu);
		// populateHawkerCodes();
		// refreshCustomerTable();
	}

	private void deleteCustomer(Customer custRow) {
		Dialog<ButtonType> deleteWarning = new Dialog<ButtonType>();
		deleteWarning.setTitle("Warning");
		deleteWarning.setHeaderText("Are you sure you want to delete this record?");
		deleteWarning.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
		Optional<ButtonType> result = deleteWarning.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.YES) {

			try {
				ArrayList<Customer> custData = getCustomerDataToShift(custRow.getHawkerCode(),
						custRow.getLineNum().intValue());
				shiftHouseSeqForDelete(custData, custRow.getHouseSeq());
				Connection con = Main.dbConnection;
				while (!con.isValid(0)) {
					con = Main.reconnect();
				}
				String deleteString = "delete from customer where customer_id=?";
				PreparedStatement deleteStmt = con.prepareStatement(deleteString);
				deleteStmt.setLong(1, custRow.getCustomerId());

				deleteStmt.executeUpdate();
				con.commit();

				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete Successful")
						.text("Deletion of customer was successful").showInformation();
				refreshCustomerTable();
			} catch (SQLException e) {

				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed")
						.text("Delete request of customer has failed").showError();
			}
		}

	}

	private void showEditCustomerDialog(Customer custRow) {
		int selectedIndex = ACustInfoTable.getSelectionModel().selectedIndexProperty().get();
		int prevHouseSeq = custRow.getHouseSeq();
		try {

			Dialog<Customer> editCustomerDialog = new Dialog<Customer>();
			editCustomerDialog.setTitle("Edit customer data");
			editCustomerDialog.setHeaderText("Update the customer data below");

			// Set the button types.
			ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
			editCustomerDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

			FXMLLoader editCustomerLoader = new FXMLLoader(getClass().getResource("EditCustomer.fxml"));
			Parent editCustomerGrid = (Parent) editCustomerLoader.load();
			EditCustomerController editCustController = editCustomerLoader.<EditCustomerController> getController();

			editCustomerDialog.getDialogPane().setContent(editCustomerGrid);
			editCustController.setCustomerToEdit(custRow);
			editCustController.setupBindings();

			editCustomerDialog.setResultConverter(dialogButton -> {
				if (dialogButton == saveButtonType) {
					Customer edittedCustomer = editCustController.returnUpdatedCustomer();
					if (houseSequenceExistsInLine(edittedCustomer.getHawkerCode(), edittedCustomer.getHouseSeq(),
							edittedCustomer.getLineNum())) {
						ArrayList<Customer> custData = getCustomerDataToShift(custRow.getHawkerCode(),
								custRow.getLineNum().intValue());
						shiftHouseSeqFromToForCustId(custData, prevHouseSeq, edittedCustomer.getHouseSeq(),
								custRow.getCustomerId());

					}
					return edittedCustomer;
				}
				return null;
			});

			Optional<Customer> updatedCustomer = editCustomerDialog.showAndWait();
			// refreshCustomerTable();

			updatedCustomer.ifPresent(new Consumer<Customer>() {

				@Override
				public void accept(Customer t) {

					customerMasterData.add(selectedIndex, t);
					customerMasterData.remove(custRow);
					ACustInfoTable.getSelectionModel().select(t);
					reloadData();
				}
			});

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void deleteSubscription(Subscription subsRow) {
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
				String deleteString = "delete from subscription where subscription_id=?";
				PreparedStatement deleteStmt = con.prepareStatement(deleteString);
				deleteStmt.setLong(1, subsRow.getSubscriptionId());

				deleteStmt.executeUpdate();
				con.commit();

				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete Successful")
						.text("Deletion of subscription was successful").showInformation();
				refreshSubscriptions();
			} catch (SQLException e) {

				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed")
						.text("Delete request of subscription has failed").showError();
			}
		}
	}

	private void showEditSubscriptionDialog(Subscription subsRow) {
		try {

			Dialog<Subscription> editSubscriptionDialog = new Dialog<Subscription>();
			editSubscriptionDialog.setTitle("Edit subscription data");
			editSubscriptionDialog.setHeaderText("Update the subscription data below");

			// Set the button types.
			ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
			editSubscriptionDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

			FXMLLoader editSubscriptionLoader = new FXMLLoader(getClass().getResource("EditSubscription.fxml"));
			Parent editSubscriptionGrid = (Parent) editSubscriptionLoader.load();
			EditSubscriptionController editSubsController = editSubscriptionLoader
					.<EditSubscriptionController> getController();

			editSubscriptionDialog.getDialogPane().setContent(editSubscriptionGrid);
			editSubsController.setSubscriptionToEdit(subsRow);
			editSubsController.setupBindings();
			Button saveBtn = (Button) editSubscriptionDialog.getDialogPane().lookupButton(saveButtonType);
			saveBtn.addEventFilter(ActionEvent.ACTION, btnEvent -> {
				if (editSubsController.isValid()) {
					editSubsController.updateSubscriptionRecord();
					refreshSubscriptions();
					// btnEvent.consume();
				} else
					btnEvent.consume();
			});
			editSubscriptionDialog.setResultConverter(dialogButton -> {
				// if (dialogButton == saveButtonType) {
				// Subscription edittedSubscription =
				// editSubsController.returnUpdatedSubscription();
				// return null;
				// return edittedSubscription;
				// }
				return null;
			});

			Optional<Subscription> updatedSubscription = editSubscriptionDialog.showAndWait();
			// refreshCustomerTable();

			updatedSubscription.ifPresent(new Consumer<Subscription>() {

				@Override
				public void accept(Subscription t) {

					refreshSubscriptions();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addCustomerExtraScreenClicked(ActionEvent event) {
		try {

			Dialog<String> addCustomerDialog = new Dialog<String>();
			addCustomerDialog.setTitle("Add new customer");
			addCustomerDialog.setHeaderText("Add new Customer data below.");

			// Set the button types.
			ButtonType saveButtonType = new ButtonType("Save and add new", ButtonData.OK_DONE);
			addCustomerDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CLOSE);
			Button saveButton = (Button) addCustomerDialog.getDialogPane().lookupButton(saveButtonType);
			FXMLLoader addCustomerLoader = new FXMLLoader(getClass().getResource("AddCustomersExtraScreen.fxml"));
			Parent addCustomerGrid = (Parent) addCustomerLoader.load();
			AddCustomerExtraScreenController addCustController = addCustomerLoader
					.<AddCustomerExtraScreenController> getController();
			saveButton.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					if (addCustController.isValid()) {
						addCustController.addCustomer();
						Notifications.create().hideAfter(Duration.seconds(5)).title("Customer created")
								.text("Customer created successfully.").showInformation();
						refreshCustomerTable();
					} else {
						event.consume();
					}

					// event.consume();
				}
			});
			addCustomerDialog.getDialogPane().setContent(addCustomerGrid);
			addCustController.setupBindings();

			addCustomerDialog.setResultConverter(dialogButton -> {
				if (dialogButton != saveButtonType) {
					return null;
				}
				return null;
			});

			Optional<String> updatedCustomer = addCustomerDialog.showAndWait();
			// refreshCustomerTable();

			updatedCustomer.ifPresent(new Consumer<String>() {

				@Override
				public void accept(String t) {

					addCustomerDialog.showAndWait();
				}
			});

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void populateLineNumbersForHawkerCode(String hawkerCode) {

		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			hawkerLineNumData.clear();
			PreparedStatement stmt = con.prepareStatement(
					"select li.LINE_NUM || ' ' || ld.NAME as line_num_dist from line_info li, line_distributor ld where li.HAWKER_ID=ld.HAWKER_ID(+) and li.line_num=ld.line_num(+) and li.hawker_id = ? order by li.line_num");
			stmt.setLong(1, hawkerIdForCode(hawkerCode));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				hawkerLineNumData.add(rs.getString(1));
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

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

	public void refreshCustomerTable() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					customerMasterData.clear();
					populateHawkerCodes();
					String queryString;
					PreparedStatement stmt;
					if (HawkerLoginController.loggedInHawker != null) {
						queryString = "select customer_id,customer_code, name,mobile_num,hawker_code, line_Num, house_Seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3,initials, employment, comments, building_street from customer where hawker_code=? order by hawker_code,line_num,house_seq";
						stmt = con.prepareStatement(queryString);
						stmt.setString(1, HawkerLoginController.loggedInHawker.getHawkerCode());
					} else {
						queryString = "select customer_id,customer_code, name,mobile_num,hawker_code, line_Num, house_Seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3,initials, employment, comments, building_street from customer order by hawker_code,line_num,house_seq";
						stmt = con.prepareStatement(queryString);
					}

					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						customerMasterData.add(new Customer(rs.getLong(1), rs.getLong(2), rs.getString(3),
								rs.getString(4), rs.getString(5), rs.getLong(6), rs.getInt(7), rs.getString(8),
								rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13),
								rs.getString(14), rs.getString(15), rs.getString(16), rs.getString(17),
								rs.getString(18), rs.getString(19), rs.getString(20), rs.getString(21)));
					}
				} catch (SQLException e) {

					e.printStackTrace();
				}
				if (!customerMasterData.isEmpty()) {
					filteredData = new FilteredList<>(customerMasterData, p -> true);
					SortedList<Customer> sortedData = new SortedList<>(filteredData);
					sortedData.comparatorProperty().bind(ACustInfoTable.comparatorProperty());
					ACustInfoTable.setItems(sortedData);
				}
				ACustInfoTable.refresh();
				// ACustInfoTable.getSelectionModel().select(0);
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

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	@FXML
	private void addCustomerClicked(ActionEvent event) {
		System.out.println("addCustomerClicked");

		boolean validate = true;
		if (addCustHwkCode.getSelectionModel().getSelectedItem() == null) {
			validate = false;
			Notifications.create().hideAfter(Duration.seconds(5)).title("Hawker not selected")
					.text("Please select a hawker before adding the the customer").showError();
		}
		// if(addCustLineNum.getSelectionModel().selectedIndexProperty().get()!=-1)
		// validate=true;
		if (NumberUtils.tryParseInt(addCustHouseSeq.getText()) == null) {
			validate = false;
			Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid house number")
					.text("House sequence should not be empty and must be NUMBERS only").showError();
		}

		if (validate) {
			if (houseSequenceExistsInLine(addCustHwkCode.getSelectionModel().getSelectedItem(),
					Integer.parseInt(addCustHouseSeq.getText()),
					Long.parseLong(addCustLineNum.getSelectionModel().getSelectedItem().split(" ")[0].trim()))) {
				ArrayList<Customer> custData = getCustomerDataToShift(
						addCustHwkCode.getSelectionModel().getSelectedItem(),
						Integer.parseInt(addCustLineNum.getSelectionModel().getSelectedItem().split(" ")[0].trim()));
				shiftHouseSeqFrom(custData, Integer.parseInt(addCustHouseSeq.getText()));
			}

			PreparedStatement insertCustomer = null;
			String insertStatement = "INSERT INTO CUSTOMER(name,mobile_num,hawker_code, line_num, house_seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3,initials, employment, comments, building_street) "
					+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			Connection con = Main.dbConnection;
			try {
				while (!con.isValid(0)) {
					con = Main.reconnect();
				}
				insertCustomer = con.prepareStatement(insertStatement);
				insertCustomer.setString(1, addCustName.getText());
				insertCustomer.setString(2, addCustMobile.getText());
				insertCustomer.setString(3, addCustHwkCode.getSelectionModel().getSelectedItem());
				if (!addCustLineNum.isDisabled())
					insertCustomer.setLong(4,
							Long.parseLong(addCustLineNum.getSelectionModel().getSelectedItem().split(" ")[0].trim()));
				else
					insertCustomer.setString(4, null);
				if (!addCustHouseSeq.isDisabled())
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
				insertCustomer.setString(13, addCustProf1.getSelectionModel().getSelectedItem());
				insertCustomer.setString(14, addCustProf2.getSelectionModel().getSelectedItem());
				insertCustomer.setString(15, addCustProf3.getText());
				insertCustomer.setString(16, addCustInitials.getText());
				insertCustomer.setString(17, addCustEmployment.getSelectionModel().getSelectedItem());
				insertCustomer.setString(18, addCustComments.getText());
				insertCustomer.setString(19, addCustBuildingStreet.getText());

				insertCustomer.execute();
				refreshCustomerTable();
				con.commit();
				resetClicked(event);
				// con.close();
				addCustName.requestFocus();
			} catch (SQLException e) {

				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Error!")
						.text("There has been some error during customer creation, please retry").showError();
				Main.reconnect();
			}

		}

	}

	public ArrayList<Customer> getCustomerDataToShift(String hawkerCode, int lineNum) {
		ArrayList<Customer> custData = new ArrayList<Customer>();

		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select customer_id,customer_code, name,mobile_num,hawker_code, line_Num, house_Seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3,initials, employment, comments, building_street from customer where hawker_code=? and line_num=? order by house_seq";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setString(1, hawkerCode);
			stmt.setInt(2, lineNum);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				custData.add(new Customer(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4),
						rs.getString(5), rs.getLong(6), rs.getInt(7), rs.getString(8), rs.getString(9),
						rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14),
						rs.getString(15), rs.getString(16), rs.getString(17), rs.getString(18), rs.getString(19),
						rs.getString(20), rs.getString(21)));
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}

		return custData;
	}

	private void shiftHouseSeqFrom(ArrayList<Customer> custData, int seq) {

		for (int i = 0; i < custData.size(); i++) {
			Customer cust = custData.get(i);
			if (cust != null && cust.getHouseSeq() >= seq) {
				if (cust.getHouseSeq() == seq) {
					cust.setHouseSeq(cust.getHouseSeq() + 1);
					cust.updateCustomerRecord();
				} else if (cust.getHouseSeq() > seq) {
					if (i > 0 && custData.get(i - 1).getHouseSeq() == cust.getHouseSeq()) {
						cust.setHouseSeq(cust.getHouseSeq() + 1);
						cust.updateCustomerRecord();
					} else
						return;
				}

			}
		}
		// reloadData();
	}

	private void shiftHouseSeqForDelete(ArrayList<Customer> custData, int seq) {

		for (int i = 0; i < custData.size(); i++) {
			Customer cust = custData.get(i);
			if (cust != null && cust.getHouseSeq() >= seq) {
				cust.setHouseSeq(cust.getHouseSeq() - 1);
				cust.updateCustomerRecord();
			}
		}
		// reloadData();
	}

	private void shiftHouseSeqFromToForCustId(ArrayList<Customer> custData, int fromSeq, int toSeq, long custId) {
		if (fromSeq < toSeq) {
			for (int i = 0; i < custData.size(); i++) {
				Customer cust = custData.get(i);
				if (cust != null && cust.getHouseSeq() > fromSeq && cust.getHouseSeq() <= toSeq
						&& cust.getCustomerId() != custId) {
					cust.setHouseSeq(cust.getHouseSeq() - 1);
					cust.updateCustomerRecord();
				}
			}
		} else if (fromSeq > toSeq) {
			for (int i = 0; i < custData.size(); i++) {
				Customer cust = custData.get(i);
				if (cust != null && cust.getHouseSeq() < fromSeq && cust.getHouseSeq() >= toSeq
						&& cust.getCustomerId() != custId) {
					cust.setHouseSeq(cust.getHouseSeq() + 1);
					cust.updateCustomerRecord();
				}
			}
		}

		// reloadData();
	}

	@FXML
	private void resetClicked(ActionEvent event) {
		System.out.println("resetClicked");
		addCustName.clear();
		addCustMobile.clear();
		addCustLineNum.getSelectionModel().clearSelection();
		if (HawkerLoginController.loggedInHawker == null) {
			addCustHwkCode.getSelectionModel().clearSelection();
			addCustLineNum.setDisable(true);
		}

		addCustHouseSeq.clear();
		addCustHouseSeq.setDisable(true);
		addCustOldHouseNum.clear();
		addCustNewHouseNum.clear();
		addCustAddrLine1.clear();
		addCustAddrLine2.clear();
		addCustLocality.clear();
		addCustCity.clear();
		// addCustState.setValue("State");
		addCustProf1.getSelectionModel().clearSelection();
		addCustProf2.getSelectionModel().clearSelection();
		addCustProf3.clear();
		addCustInitials.clear();
		addCustEmployment.getSelectionModel().clearSelection();
		addCustComments.clear();
		addCustBuildingStreet.clear();
	}

	@FXML
	private void filterCustomersClicked(ActionEvent event) {

		TextInputDialog customersFilterDialog = new TextInputDialog(searchText);
		customersFilterDialog.setTitle("Filter Customers");
		customersFilterDialog.setHeaderText("Enter the filter text");
		Optional<String> returnValue = customersFilterDialog.showAndWait();
		if (returnValue.isPresent()) {
			try {
				searchText = returnValue.get();
				filteredData.setPredicate(new Predicate<Customer>() {

					@Override
					public boolean test(Customer customer) {

						if (searchText == null || searchText.isEmpty())
							return true;
						else if (customer.getAddrLine1() != null
								&& customer.getAddrLine1().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getAddrLine2() != null
								&& customer.getAddrLine2().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getCity() != null
								&& customer.getCity().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getHawkerCode().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getLocality() != null
								&& customer.getLocality().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getName() != null
								&& customer.getName().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getName() != null
								&& customer.getName().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getNewHouseNum() != null
								&& customer.getNewHouseNum().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getOldHouseNum() != null
								&& customer.getOldHouseNum().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getState() != null
								&& customer.getState().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getProfile1() != null
								&& customer.getProfile1().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getProfile2() != null
								&& customer.getProfile2().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getProfile3() != null
								&& customer.getProfile3().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if (customer.getInitials() != null
								&& customer.getInitials().toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if ((customer.getCustomerCode() + "").toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if ((customer.getEmployment() + "").toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if ((customer.getComments() + "").toUpperCase().contains(searchText.toUpperCase()))
							return true;
						else if ((customer.getBuildingStreet() + "").toUpperCase().contains(searchText.toUpperCase()))
							return true;
						return false;

					}
				});

				SortedList<Customer> sortedData = new SortedList<>(filteredData);
				sortedData.comparatorProperty().bind(ACustInfoTable.comparatorProperty());
				ACustInfoTable.setItems(sortedData);
				ACustInfoTable.refresh();

			} catch (NumberFormatException e) {

				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid value entered")
						.text("Please enter numeric value only").showError();
			}
		}

	}

	@FXML
	private void clearClicked(ActionEvent event) {
		filteredData = new FilteredList<>(customerMasterData, p -> true);
		SortedList<Customer> sortedData = new SortedList<>(filteredData);
		sortedData.comparatorProperty().bind(ACustInfoTable.comparatorProperty());
		ACustInfoTable.setItems(sortedData);
		ACustInfoTable.getSelectionModel().clearSelection();
	}

	public void reloadData() {
		populateHawkerCodes();
		addCustHwkCode.getItems().clear();
		addCustHwkCode.getItems().addAll(hawkerCodeData);
		if (HawkerLoginController.loggedInHawker != null) {
			addCustHwkCode.getSelectionModel().select(HawkerLoginController.loggedInHawker.getHawkerCode());
			addCustHwkCode.setDisable(true);
		}
		subscriptionsTable.getItems().clear();
		subscriptionMasterData.clear();
		addCustLineNum.getSelectionModel().clearSelection();
		populateProfileValues();
		populateEmploymentValues();
		addCustProf1.getItems().addAll(profileValues);
		addCustProf2.getItems().addAll(profileValues);
		addCustEmployment.getItems().addAll(employmentData);
		refreshCustomerTable();
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
					ResultSet rs = stmt.executeQuery(
							"select value, code, seq from lov_lookup where code='PROFILE_VALUES' order by seq");
					while (rs.next()) {
						profileValues.add(rs.getString(1));
					}

				} catch (SQLException e) {

					e.printStackTrace();
				}
				addCustProf1.getItems().clear();
				addCustProf2.getItems().clear();
				addCustProf1.getItems().addAll(profileValues);
				addCustProf2.getItems().addAll(profileValues);
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
					ResultSet rs = stmt.executeQuery(
							"select value, code, seq from lov_lookup where code='EMPLOYMENT_STATUS' order by seq");
					while (rs.next()) {
						employmentData.add(rs.getString(1));
					}

				} catch (SQLException e) {

					e.printStackTrace();
				}
				addCustEmployment.getItems().clear();
				addCustEmployment.getItems().addAll(employmentData);
				return null;
			}

		};

		new Thread(task).start();

	}

	public boolean houseSequenceExistsInLine(String hawkerCode, int seq, Long lineNum) {
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select customer_id,customer_code, name,mobile_num,hawker_code, line_Num, house_Seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3,initials from customer where house_seq=? and line_num=? and hawker_code=?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setInt(1, seq);
			stmt.setLong(2, lineNum);
			stmt.setString(3, hawkerCode);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return true;
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return false;
	}

	private void addCustSubscription(Customer custRow) {

		try {

			Dialog<String> addSubscriptionDialog = new Dialog<String>();
			addSubscriptionDialog.setTitle("Add subscription data");
			addSubscriptionDialog.setHeaderText("Add the subscription data below");

			// Set the button types.
			ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
			addSubscriptionDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

			FXMLLoader addSubscriptionLoader = new FXMLLoader(getClass().getResource("AddCustSubscription.fxml"));
			Parent addSubscriptionGrid = (Parent) addSubscriptionLoader.load();
			AddSubscriptionController addSubsController = addSubscriptionLoader
					.<AddSubscriptionController> getController();

			addSubscriptionDialog.getDialogPane().setContent(addSubscriptionGrid);
			addSubsController.setCustomer(ACustInfoTable.getSelectionModel().getSelectedItem());
			addSubsController.setupBindings();
			Button saveBtn = (Button) addSubscriptionDialog.getDialogPane().lookupButton(saveButtonType);

			saveBtn.addEventFilter(ActionEvent.ACTION, btnEvent -> {
				if (addSubsController.isValid()) {
					addSubsController.addSubscription();
				} else
					btnEvent.consume();
			});

			addSubscriptionDialog.setResultConverter(dialogButton -> {
				if (dialogButton == saveButtonType) {

					return null;
				}
				return null;
			});

			Optional<String> updatedSubscription = addSubscriptionDialog.showAndWait();
			// refreshCustomerTable();

			updatedSubscription.ifPresent(new Consumer<String>() {

				@Override
				public void accept(String t) {

					refreshSubscriptions();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void refreshSubscriptions() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					subscriptionsTable.getItems().clear();
					subscriptionMasterData.clear();
					String query = "select sub.SUBSCRIPTION_ID, sub.CUSTOMER_ID, sub.PRODUCT_ID, prod.name, prod.type, sub.PAYMENT_TYPE, sub.SUBSCRIPTION_COST, sub.SERVICE_CHARGE, sub.FREQUENCY, sub.TYPE, sub.DOW, sub.STATUS, sub.START_DATE, sub.PAUSED_DATE, prod.CODE, sub.STOP_DATE, sub.DURATION, sub.OFFER_MONTHS, sub.SUB_NUMBER from subscription sub, products prod where sub.PRODUCT_ID=prod.PRODUCT_ID and sub.customer_id =? order by prod.name";
					PreparedStatement stmt = con.prepareStatement(query);
					stmt.setLong(1, ACustInfoTable.getSelectionModel().getSelectedItem().getCustomerId());
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						subscriptionMasterData.add(new Subscription(rs.getLong(1), rs.getLong(2), rs.getLong(3),
								rs.getString(4), rs.getString(5), rs.getString(6), rs.getDouble(7), rs.getDouble(8),
								rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12),
								rs.getDate(13) == null ? null : rs.getDate(13).toLocalDate(),
								rs.getDate(14) == null ? null : rs.getDate(14).toLocalDate(), rs.getString(15),
								rs.getDate(16) == null ? null : rs.getDate(16).toLocalDate(), rs.getString(17),
								rs.getInt(18), rs.getString(19)));
					}

				} catch (SQLException e) {

					e.printStackTrace();
				}
				subscriptionsTable.getItems().addAll(subscriptionMasterData);
				subscriptionsTable.refresh();
				return null;
			}

		};

		new Thread(task).start();

	}

}

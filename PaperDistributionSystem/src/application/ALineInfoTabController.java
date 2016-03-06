package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;

public class ALineInfoTabController implements Initializable{

	@FXML
	private TableView<LineInfo> lineNumTable;
	@FXML
	private TextField addLineNumField;
	@FXML
	private TableView<Customer> lineNumCustomersTable;
	@FXML
	private ComboBox<String> hawkerComboBox;
	@FXML
	private ComboBox<String> addPointName;
	@FXML private Label days14Count;
	@FXML private Label weeklyCount;
	@FXML private Label dailyCount;

	@FXML
	private Button addCustExtraButton;
	// Columns
	@FXML
	private TableColumn<LineInfo, String> lineNumColumn;
	@FXML
	private TableColumn<Customer, Long> customerIDColumn;
	@FXML
	private TableColumn<Customer, String> customerNameColumn;
	@FXML
	private TableColumn<Customer, String> mobileNumColumn;
	@FXML
	private TableColumn<Customer, Integer> houseSeqColumn;
	@FXML
	private TableColumn<Customer, String> flatNameColumn;

	@FXML
	private TableColumn<Customer, String> line1Column;

	@FXML
	private TableColumn<Customer, String> line2Column;

	private ObservableList<String> hawkerCodeData = FXCollections.observableArrayList();
	private ObservableList<LineInfo> lineNumData = FXCollections.observableArrayList();
	private ObservableList<Customer> customerData = FXCollections.observableArrayList();
	private ObservableList<String> pointNameValues = FXCollections.observableArrayList();
	private ArrayList<TextField> newHouseSeqTFArray;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		System.out.println("Entered HLineInfoTabController");

		lineNumColumn.setCellValueFactory(new PropertyValueFactory<LineInfo, String>("lineNumDist"));
		lineNumTable.setDisable(true);
		addLineNumField.setDisable(true);

		customerIDColumn.setCellValueFactory(new PropertyValueFactory<Customer, Long>("customerCode"));
		customerNameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
		mobileNumColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("mobileNum"));
		flatNameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("buildingStreet"));
		line1Column.setCellValueFactory(new PropertyValueFactory<Customer, String>("addrLine1"));
		line2Column.setCellValueFactory(new PropertyValueFactory<Customer, String>("addrLine2"));
		houseSeqColumn.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("houseSeq"));
		
		addPointName.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				populateHawkerCodes();
				hawkerComboBox.getItems().clear();
				hawkerComboBox.getItems().addAll(hawkerCodeData);
			}
		});


		hawkerComboBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

				if (newValue != null) {
					lineNumTable.setDisable(false);
					addLineNumField.setDisable(false);
					refreshLineNumTableForHawker(newValue);
				}else {
					lineNumTable.getItems().clear();
					lineNumTable.refresh();
					lineNumData.clear();
				}
				dailyCount.setText("");
				weeklyCount.setText("");
				days14Count.setText("");

			}

		});

		lineNumTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LineInfo>() {

			@Override
			public void changed(ObservableValue<? extends LineInfo> observable, LineInfo oldValue, LineInfo newValue) {

				populateCustomersForLine();
				populateSubscriptionCount();
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

		lineNumTable.setRowFactory(new Callback<TableView<LineInfo>, TableRow<LineInfo>>() {

			@Override
			public TableRow<LineInfo> call(TableView<LineInfo> param) {
				final TableRow<LineInfo> row = new TableRow<>();
				
				MenuItem mnuDel = new MenuItem("Delete line");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						LineInfo lineRow = lineNumData.get(lineNumTable.getSelectionModel().getSelectedIndex());
						if (lineRow != null) {
							deleteLine(lineRow);
						}
					}

				});

				MenuItem mnuEdit = new MenuItem("Edit line number");
				mnuEdit.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						LineInfo lineRow = lineNumData.get(lineNumTable.getSelectionModel().getSelectedIndex());
						if (lineRow != null) {
							showEditLineDialog(lineRow);
							lineNumTable.refresh();
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

		addLineNumField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					addLineButtonClicked(new ActionEvent());
				}

			}
		});

		lineNumCustomersTable.setRowFactory(new Callback<TableView<Customer>, TableRow<Customer>>() {

			@Override
			public TableRow<Customer> call(TableView<Customer> param) {
				final TableRow<Customer> row = new TableRow<Customer>();
				MenuItem mnuDel = new MenuItem("Delete customer");
				mnuDel.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Customer custRow = lineNumCustomersTable.getSelectionModel().getSelectedItem();
						if (custRow != null) {
							deleteCustomer(custRow);
							lineNumCustomersTable.refresh();
						}
					}

				});
				MenuItem mnuEdit = new MenuItem("Edit customer");
				mnuEdit.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Customer custRow = lineNumCustomersTable.getSelectionModel().getSelectedItem();
						if (custRow != null) {
							showEditCustomerDialog(custRow);
							// populateCustomersForLine();
						}
					}

				});
				MenuItem mnuSubs = new MenuItem("Add subscription");
				mnuSubs.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Customer custRow = lineNumCustomersTable.getSelectionModel().getSelectedItem();
						if (custRow != null) {
							addCustSubscription(custRow);
//							refreshSubscriptions();
						}
					}

				});
				MenuItem mnuPause = new MenuItem("Pause Subscription");
				mnuPause.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Customer custRow = lineNumCustomersTable.getSelectionModel().getSelectedItem();

						Dialog<ButtonType> pauseWarning = new Dialog<ButtonType>();
						pauseWarning.setTitle("Pause Subscription");
						pauseWarning.setHeaderText("Please select the subscription you want to PAUSE");
						pauseWarning.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
						GridPane grid = new GridPane();
						grid.setHgap(10);
						grid.setVgap(10);
						grid.setPadding(new Insets(20, 150, 10, 10));
						ObservableList<Subscription> subsList = getActiveSubsListForCust(custRow);
						ComboBox<Subscription> subsBox = new ComboBox<Subscription>();
						subsBox.setConverter(new StringConverter<Subscription>() {

							@Override
							public String toString(Subscription object) {
								return object.getSubscriptionId() + "-" + object.getProductCode() + "-"
										+ object.getProductName();
							}

							@Override
							public Subscription fromString(String string) {
								for (int i = 0; i < subsList.size(); i++) {
									Subscription sub = subsList.get(i);
									if (sub.getSubscriptionId() == (Long.parseLong(string.split("-")[0])))
										return sub;
								}
								return null;
							}
						});
						subsBox.getItems().addAll(subsList);
						subsBox.getSelectionModel().selectFirst();
						grid.add(new Label("Subscription"), 0, 0);
						grid.add(new Label("Pause Date"), 0, 1);
						grid.add(new Label("Pause Date"), 0, 2);
						DatePicker dp = new DatePicker(LocalDate.now());
						dp.setConverter(Main.dateConvertor);

						grid.add(dp, 1, 1);
						grid.add(subsBox, 1, 0);
						DatePicker resumeDP = new DatePicker(LocalDate.now().plusDays(7));
						resumeDP.setConverter(Main.dateConvertor);
						grid.add(resumeDP, 1, 2);
						pauseWarning.getDialogPane().setContent(grid);
						Button yesButton = (Button) pauseWarning.getDialogPane().lookupButton(ButtonType.YES);
						yesButton.addEventFilter(ActionEvent.ACTION, btnEvent -> {
							if (dp.getValue().isBefore(subsBox.getSelectionModel().getSelectedItem().getStartDate())) {
								Notifications.create().title("Invalid paused date")
										.text("Paused date should not be before Start date for subscription")
										.hideAfter(Duration.seconds(5)).showError();
								btnEvent.consume();
							}
						});
						Optional<ButtonType> result = pauseWarning.showAndWait();
						if (result.isPresent() && result.get() == ButtonType.YES) {
							Subscription subsRow = subsBox.getSelectionModel().getSelectedItem();
							if (subsRow != null && subsRow.getStatus().equals("Active")) {

							} else {
								Notifications.create().title("Invalid operation").text("Subscription is already PAUSED")
										.hideAfter(Duration.seconds(5)).showWarning();
							}
							subsRow.setStatus("Paused");
							subsRow.setPausedDate(dp.getValue());
							subsRow.setResumeDate(resumeDP.getValue());
							subsRow.updateSubscriptionRecord();
							Notifications.create().title("Pause successful").text("Pause subscription successful")
									.hideAfter(Duration.seconds(5)).showInformation();

						}

					}

				});

				MenuItem mnuResume = new MenuItem("Resume Subscription");
				mnuResume.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						Customer custRow = lineNumCustomersTable.getSelectionModel().getSelectedItem();

						Dialog<ButtonType> deleteWarning = new Dialog<ButtonType>();
						deleteWarning.setTitle("Start Subscriptions");
						deleteWarning.setHeaderText("Are you sure you want to START this subscription?");
						deleteWarning.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);

						ObservableList<Subscription> subsList = getPausedSubsListForCust(custRow);
						ComboBox<Subscription> subsBox = new ComboBox<Subscription>();
						subsBox.setConverter(new StringConverter<Subscription>() {

							@Override
							public String toString(Subscription object) {
								return object.getSubscriptionId() + "-" + object.getProductCode() + "-"
										+ object.getProductName();
							}

							@Override
							public Subscription fromString(String string) {
								for (int i = 0; i < subsList.size(); i++) {
									Subscription sub = subsList.get(i);
									if (sub.getSubscriptionId() == (Long.parseLong(string.split("-")[0])))
										return sub;
								}
								return null;
							}
						});
						subsBox.getItems().addAll(subsList);

						GridPane grid = new GridPane();
						grid.setHgap(10);
						grid.setVgap(10);
						grid.setPadding(new Insets(20, 150, 10, 10));
						subsBox.getSelectionModel().selectFirst();
						grid.add(new Label("Subscription"), 0, 0);
						grid.add(subsBox, 1, 0);

						deleteWarning.getDialogPane().setContent(grid);
						Optional<ButtonType> result = deleteWarning.showAndWait();
						if (result.isPresent() && result.get() == ButtonType.YES) {
							Subscription subsRow = subsBox.getSelectionModel().getSelectedItem();
							if (subsRow != null && subsRow.getStatus().equals("Paused")) {
								subsRow.resumeSubscription();
								Notifications.create().title("Resume successful").text("Resume subscription successful")
										.hideAfter(Duration.seconds(5)).showInformation();
							} else {
								Notifications.create().title("Invalid operation").text("Subscription is already ACTIVE")
										.hideAfter(Duration.seconds(5)).showWarning();
							}
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
				menu.getItems().addAll(mnuEdit,mnuDel,mnuSubs, mnuPause, mnuResume);
				row.contextMenuProperty().bind(
						Bindings.when(Bindings.isNotNull(row.itemProperty())).then(menu).otherwise((ContextMenu) null));
				return row;
			}
		});

	}

	private ObservableList<Subscription> getActiveSubsListForCust(Customer custRow) {
		ObservableList<Subscription> subsList = FXCollections.observableArrayList();
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			subsList.clear();
			String query = "select sub.SUBSCRIPTION_ID, sub.CUSTOMER_ID, sub.PRODUCT_ID, prod.name, prod.type, sub.PAYMENT_TYPE, sub.SUBSCRIPTION_COST, sub.SERVICE_CHARGE, sub.FREQUENCY, sub.TYPE, sub.DOW, sub.STATUS, sub.START_DATE, sub.PAUSED_DATE, prod.code, sub.STOP_DATE, sub.DURATION, sub.OFFER_MONTHS, sub.SUB_NUMBER, sub.resume_date from subscription sub, products prod where sub.PRODUCT_ID=prod.PRODUCT_ID and sub.customer_id =? and sub.status='Active' order by sub.subscription_id";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(1, custRow.getCustomerId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				subsList.add(new Subscription(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getDouble(7), rs.getDouble(8), rs.getString(9),
						rs.getString(10), rs.getString(11), rs.getString(12),
						rs.getDate(13) == null ? null : rs.getDate(13).toLocalDate(),
						rs.getDate(14) == null ? null : rs.getDate(14).toLocalDate(), rs.getString(15),
						rs.getDate(16) == null ? null : rs.getDate(16).toLocalDate(), rs.getString(17), rs.getInt(18),
						rs.getString(19),rs.getDate(20)==null?null:rs.getDate(20).toLocalDate()));
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return subsList;
	}

	private ObservableList<Subscription> getPausedSubsListForCust(Customer custRow) {
		ObservableList<Subscription> subsList = FXCollections.observableArrayList();
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			subsList.clear();
			String query = "select sub.SUBSCRIPTION_ID, sub.CUSTOMER_ID, sub.PRODUCT_ID, prod.name, prod.type, sub.PAYMENT_TYPE, sub.SUBSCRIPTION_COST, sub.SERVICE_CHARGE, sub.FREQUENCY, sub.TYPE, sub.DOW, sub.STATUS, sub.START_DATE, sub.PAUSED_DATE, prod.code, sub.STOP_DATE, sub.DURATION, sub.OFFER_MONTHS, sub.SUB_NUMBER, sub.resume_date from subscription sub, products prod where sub.PRODUCT_ID=prod.PRODUCT_ID and sub.customer_id =? and sub.status='Paused' order by sub.subscription_id";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(1, custRow.getCustomerId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				subsList.add(new Subscription(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getDouble(7), rs.getDouble(8), rs.getString(9),
						rs.getString(10), rs.getString(11), rs.getString(12),
						rs.getDate(13) == null ? null : rs.getDate(13).toLocalDate(),
						rs.getDate(14) == null ? null : rs.getDate(14).toLocalDate(), rs.getString(15),
						rs.getDate(16) == null ? null : rs.getDate(16).toLocalDate(), rs.getString(17), rs.getInt(18),
						rs.getString(19),rs.getDate(20)==null?null:rs.getDate(20).toLocalDate()));
			}

		} catch (SQLException e) {

			e.printStackTrace();
		}
		return subsList;
	}

	private void showEditLineDialog(LineInfo lineRow) {

		TextInputDialog dialog = new TextInputDialog();

		dialog.setTitle("Edit line number");
		dialog.setHeaderText("Enter new line number below");

		// dialog.getDialogPane().getButtonTypes().addAll(saveButton,
		// ButtonType.CANCEL);
		Button saveBtn = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

		saveBtn.addEventFilter(ActionEvent.ACTION, event -> {
			try {
				Integer newLineNum = Integer.parseInt(dialog.getEditor().getText());

				if (checkExistingLineNum(newLineNum)) {
					lineRow.setLineNum(newLineNum);
					lineRow.updateLineNumRecord();
					updateLineNumForCust(newLineNum, customerData);
					updateLineNumForDist(lineRow);
					refreshLineNumTableForHawker(hawkerComboBox.getSelectionModel().getSelectedItem());
					Notifications.create().hideAfter(Duration.seconds(5)).title("Successful")
							.text("Line number update successful.").showInformation();
				} else {
					Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid line num")
							.text("This line number already exists.").showError();
				}

			} catch (NumberFormatException e) {

				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid line num")
						.text("Please enter numeric line number only").showError();
			}
		});
		dialog.showAndWait();
	}

	private void updateLineNumForDist(LineInfo lineRow) {
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String updateString = "update line_distributor set line_num=? where hawker_id=?, line_num=?";
			PreparedStatement stmt = con.prepareStatement(updateString);
			stmt.setLong(1, lineRow.getHawkerId());
			stmt.setInt(2, lineRow.getLineNum());
			stmt.executeUpdate();
			con.commit();
			Notifications.create().hideAfter(Duration.seconds(5)).title("Update successful")
					.text("Line number updation in line distribution boy successful").showInformation();
		} catch (SQLException e) {

			e.printStackTrace();
			Notifications.create().hideAfter(Duration.seconds(5)).title("Update failed")
					.text("Line number updation in line distribution boy failed").showError();
		}

	}

	private void updateLineNumForCust(Integer newLineNum, ObservableList<Customer> custData) {

		for (int i = 0; i < custData.size(); i++) {
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
				while (!con.isValid(0)) {
					con = Main.reconnect();
				}

				String findString = "select count(*) from customer where hawker_code=? and line_num=?";
				PreparedStatement findStmt = con.prepareStatement(findString);
				findStmt.setString(1, hawkerComboBox.getSelectionModel().getSelectedItem());
				findStmt.setInt(2, lineNumTable.getSelectionModel().getSelectedItem().getLineNum());
				ResultSet rs = findStmt.executeQuery();
				if (rs.next() && rs.getInt(1) == 0) {
					String deleteString = "delete from line_info where line_id=?";
					PreparedStatement deleteStmt = con.prepareStatement(deleteString);
					deleteStmt.setLong(1, lineRow.getLineId());
					deleteStmt.executeUpdate();
					con.commit();
					Notifications.create().hideAfter(Duration.seconds(5)).title("Delete Successful")
							.text("Deletion of line was successful").showInformation();
					refreshLineNumTableForHawker(hawkerComboBox.getSelectionModel().getSelectedItem());
				} else {
					Notifications.create().hideAfter(Duration.seconds(5)).title("Delete not allowed")
							.text("This line has customers associated to it, hence cannot be deleted").showError();
				}
			} catch (SQLException e) {

				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed")
						.text("Delete request of line has failed").showError();
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
					// lineNumCustomersTable.getItems().clear();
					PreparedStatement stmt = con.prepareStatement(
							"select customer_id,customer_code, name,mobile_num,hawker_code, line_Num, house_Seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3,initials, employment, comments, building_street from customer where hawker_code = ? and line_num = ? ORDER BY HOUSE_SEQ");
					stmt.setString(1, hawkerComboBox.getSelectionModel().getSelectedItem());
					stmt.setInt(2, lineNumTable.getSelectionModel().getSelectedItem().getLineNum());
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						customerData.add(new Customer(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4),
								rs.getString(5), rs.getLong(6), rs.getInt(7), rs.getString(8), rs.getString(9),
								rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13),
								rs.getString(14), rs.getString(15), rs.getString(16), rs.getString(17),
								rs.getString(18), rs.getString(19), rs.getString(20), rs.getString(21)));
					}
				} catch (SQLException e) {

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
			PreparedStatement stmt = con.prepareStatement("select distinct hawker_code from hawker_info where point_name=?");
			stmt.setString(1, addPointName.getSelectionModel().getSelectedItem());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				hawkerCodeData.add(rs.getString(1));
			}
			hawkerComboBox.getItems().addAll(hawkerCodeData);
			if (HawkerLoginController.loggedInHawker != null) {
				hawkerComboBox.getSelectionModel().select(HawkerLoginController.loggedInHawker.getHawkerCode());
				hawkerComboBox.setDisable(true);
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

	}

	private void refreshLineNumTableForHawker(String hawkerCode) {

		lineNumTable.getItems().clear();
		lineNumTable.refresh();
		System.out.println("refreshLineNumTableForHawker : " + hawkerCode);

		lineNumData.clear();
		
		long hawkerId = hawkerIdForCode(hawkerCode);

		if (hawkerId >= 1) {
			Task<Void> task = new Task<Void>() {

				@Override
				protected Void call() throws Exception {
					try {

						Connection con = Main.dbConnection;
						while (!con.isValid(0)) {
							con = Main.reconnect();
						}
						PreparedStatement lineNumStatement = null;
						String lineNumQuery = "select li.line_id, li.line_num, li.hawker_id,li.LINE_NUM || ' ' || ld.NAME as line_num_dist from line_info li, line_distributor ld where li.HAWKER_ID=ld.HAWKER_ID(+) and li.line_num=ld.line_num(+) and li.hawker_id = ? order by li.line_num";
						lineNumStatement = con.prepareStatement(lineNumQuery);
						lineNumStatement.setLong(1, hawkerId);
						// Statement stmt = con.createStatement();
						ResultSet rs = lineNumStatement.executeQuery();
						while (rs.next()) {
							lineNumData.add(new LineInfo(rs.getLong(1), rs.getInt(2), rs.getLong(3), rs.getString(4)));
						}
						System.out.println("LineNumData = " + lineNumData.toString());
						lineNumTable.getItems().addAll(lineNumData);
						lineNumTable.refresh();
					} catch (SQLException e) {

						e.printStackTrace();
					}
					return null;
				}

			};
			new Thread(task).start();

		} else {
			lineNumData.clear();
			lineNumTable.getItems().clear();
			lineNumTable.refresh();
			Notifications.create().hideAfter(Duration.seconds(5)).title("No lines found")
					.text("No lines found under the hawker").show();
		}

	}

	@FXML
	private void addLineButtonClicked(ActionEvent event) {
		try {
			if (hawkerComboBox.getSelectionModel().selectedIndexProperty().get() != -1) {
				Integer addLineNumValue = Integer.parseInt(addLineNumField.getText().trim());
				if (checkExistingLineNum(addLineNumValue)) {
					Task<Void> task = new Task<Void>() {

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
								long hawkerId = hawkerIdForCode(hawkerComboBox.getSelectionModel().getSelectedItem());
								if (hawkerId >= 1) {
									insertLineNum.setInt(1, Integer.parseInt(addLineNumField.getText()));
									insertLineNum.setLong(2, hawkerId);
									insertLineNum.execute();
									refreshLineNumTableForHawker(hawkerComboBox.getSelectionModel().getSelectedItem());
									addLineNumField.clear();
								}
							} catch (SQLException e) {

								e.printStackTrace();
							}
							return null;
						}

					};
					new Thread(task).start();
				}
			} else
				Notifications.create().hideAfter(Duration.seconds(5)).title("Hawker not selected")
						.text("Please select hawker before adding line number").showError();

		} catch (NumberFormatException e) {

			e.printStackTrace();
			Notifications.create().hideAfter(Duration.seconds(5)).title("Error")
					.text("Please enter proper numeric value in Line Number field").showError();
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

	private boolean checkExistingLineNum(Integer lineNum) {

		Connection con = Main.dbConnection;
		try {
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			PreparedStatement lineNumExists = null;
			String lineNumExistsQuery = "select line_num from line_info where line_num = ? and hawker_id = ?";
			lineNumExists = con.prepareStatement(lineNumExistsQuery);
			lineNumExists.setInt(1, lineNum);
			lineNumExists.setLong(2, hawkerIdForCode(hawkerComboBox.getSelectionModel().getSelectedItem()));
			ResultSet lineNumExistsRs = lineNumExists.executeQuery();
			if (lineNumExistsRs.next()) {
				Notifications.create().hideAfter(Duration.seconds(5)).title("Line number exists")
						.text("This line number already exists in the hawker selected").showError();
				return false;
			}
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return true;
	}

	@FXML
	public void shuffleHouseSeqClicked(ActionEvent event) {
		// Create the custom dialog.
		if (!customerData.isEmpty()) {
			// Dialog<ArrayList<Pair<Long, Integer>>> dialog = new Dialog<>();
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
				for (int i = 0; i < customerData.size(); i++) {
					if (NumberUtils.tryParseInt(newHouseSeqTFArray.get(i).getText()) != null) {
						if (!newHouseSeq.contains(NumberUtils.tryParseInt(newHouseSeqTFArray.get(i).getText()))) {
							newHouseSeq.add(i, NumberUtils.tryParseInt(newHouseSeqTFArray.get(i).getText()));
						} else {
							Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid sequence")
									.text("Duplicate house sequence found").showError();
							newHouseSeq.clear();
							btnevent.consume();
						}
					} else {
						Notifications.create().hideAfter(Duration.seconds(5)).title("Invalid sequence")
								.text("House sequence should not be empty and must be NUMBERS only").showError();
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
			// Iterator<Customer> custIter = customerData.iterator();

			grid.add(new Label("Customer Code"), 0, 0);
			grid.add(new Label("Name"), 1, 0);
			grid.add(new Label("Mobile Number"), 2, 0);
			grid.add(new Label("Flat/Street Name"), 3, 0);
			grid.add(new Label("Addr Line1"), 4, 0);
			grid.add(new Label("Addr Line2"), 5, 0);
			grid.add(new Label("Old House Seq"), 6, 0);
			grid.add(new Label("New House Seq"), 7, 0);
			for (int i = 0; i < customerData.size(); i++) {
				Customer cust = customerData.get(i);
				grid.add(new Label("" + cust.getCustomerCode()), 0, i + 1);
				grid.add(new Label(cust.getName()), 1, i + 1);
				grid.add(new Label(cust.getMobileNum()), 2, i + 1);
				grid.add(new Label(cust.getBuildingStreet()), 3, i + 1);
				grid.add(new Label(cust.getAddrLine1()), 4, i + 1);
				grid.add(new Label(cust.getAddrLine2()), 5, i + 1);
				grid.add(new Label("" + cust.getHouseSeq()), 6, i + 1);
				TextField newHNumTF = new TextField();

				/*
				 * newHNumTF.focusedProperty().addListener(new
				 * ChangeListener<Boolean>() {
				 * 
				 * @Override public void changed(ObservableValue<? extends
				 * Boolean> observable, Boolean oldValue, Boolean newValue) { //
				 * TODO Auto-generated method stub if (oldValue && !newValue) {
				 * if (NumberUtils.tryParseInt(newHNumTF.getText()) != null) {
				 * if (!newHouseSeq.contains(NumberUtils.tryParseInt(newHNumTF.
				 * getText()))) {
				 * newHouseSeq.add(newHouseSeqTFArray.indexOf(newHNumTF),
				 * NumberUtils.tryParseInt(newHNumTF.getText())); } else
				 * Notifications.create().hideAfter(Duration.seconds(5)).title(
				 * "Invalid sequence").text(
				 * "House sequence already entered before") .showError(); }
				 * else{
				 * newHouseSeq.remove(newHouseSeqTFArray.indexOf(newHNumTF));
				 * 
				 * } } } });
				 */
				newHouseSeqTFArray.add(i, newHNumTF);
				grid.add(newHNumTF, 7, i + 1);
			}
			scrollPane.setContent(grid);

			dialog.getDialogPane().setContent(scrollPane);

			// Convert the result to a username-password-pair when the login
			// button is clicked.
			dialog.setResultConverter(dialogButton -> {
				if (dialogButton == saveButtonType) {
					return newHouseSeq;
				}
				return null;
			});

			// Optional<ArrayList<Pair<Long, Integer>>> result =
			// dialog.showAndWait();
			Optional<ArrayList<Integer>> result = dialog.showAndWait();
			if (result.isPresent()) {
				if (result != null) {
					updateHouseSequences(result.get());
				}
			}
		} else {
			Notifications.create().hideAfter(Duration.seconds(5)).title("No customers")
					.text("There are no customers in this line").showError();
		}
	}

	private void updateHouseSequences(ArrayList<Integer> houseSeqList) {

		for (int i = 0; i < houseSeqList.size(); i++) {
			customerData.get(i).setHouseSeq(houseSeqList.get(i));
			customerData.get(i).updateCustomerRecord();
		}
		lineNumCustomersTable.refresh();
	}

	private void showEditCustomerDialog(Customer custRow) {
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
					/*
					 * if
					 * (houseSequenceExistsInLine(edittedCustomer.getHawkerCode(
					 * ), edittedCustomer.getHouseSeq(),
					 * edittedCustomer.getLineNum())) { ArrayList<Customer>
					 * custData =
					 * getCustomerDataToShift(custRow.getHawkerCode(),
					 * custRow.getLineNum().intValue());
					 * shiftHouseSeqFromToForCustId(custData, prevHouseSeq,
					 * edittedCustomer.getHouseSeq(), custRow.getCustomerId());
					 * 
					 * }
					 */
					return edittedCustomer;
				}
				return null;
			});

			Optional<Customer> updatedCustomer = editCustomerDialog.showAndWait();
			// refreshCustomerTable();

			updatedCustomer.ifPresent(new Consumer<Customer>() {

				@Override
				public void accept(Customer t) {

					populateCustomersForLine();
				}
			});

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void addCustomerExtraScreenClicked(ActionEvent event) {
		if (hawkerComboBox.getSelectionModel().getSelectedItem() == null
				|| lineNumTable.getSelectionModel().getSelectedItem() == null) {
			Notifications.create().title("Hawker and Line not selected").text("Please select hawker and line first").hideAfter(Duration.seconds(5)).showError();
		} else {
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
				saveButton.addEventFilter(ActionEvent.ACTION, btnEvent -> {
					if (addCustController.isValid()) {
						addCustController.addCustomer();
						Notifications.create().hideAfter(Duration.seconds(5)).title("Customer created")
								.text("Customer created successfully.").showInformation();
						populateCustomersForLine();
					} else {
						btnEvent.consume();
					}
				});
				addCustomerDialog.getDialogPane().setContent(addCustomerGrid);
				addCustController.setupBindings();
				addCustController.setupHawkerAndLine(hawkerComboBox.getSelectionModel().getSelectedItem(),
						lineNumTable.getSelectionModel().getSelectedItem().getLineNum() + "");
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

					}
				});

			} catch (IOException e) {

				e.printStackTrace();
			}
		}
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
				populateCustomersForLine();
			} catch (SQLException e) {

				e.printStackTrace();
				Notifications.create().hideAfter(Duration.seconds(5)).title("Delete failed")
						.text("Delete request of customer has failed").showError();
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
			addSubsController.setCustomer(lineNumCustomersTable.getSelectionModel().getSelectedItem());
			addSubsController.setupBindings();
			Button saveBtn = (Button) addSubscriptionDialog.getDialogPane().lookupButton(saveButtonType);

			saveBtn.addEventFilter(ActionEvent.ACTION, btnEvent -> {
				if (addSubsController.isValid()) {
					addSubsController.addSubscription();
					Notifications.create().title("New Subscription created").text("New subscription created successfully").hideAfter(Duration.seconds(5)).showInformation();
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

//					refreshSubscriptions();
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	public void populateSubscriptionCount() {
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
//			billCategoryValues.clear();
			PreparedStatement stmt = con.prepareStatement("SELECT sub.frequency, count(*) cnt FROM subscription sub, customer cust WHERE sub.customer_id=cust.customer_id AND sub.frequency IN ('Daily','14 Days','Weekly') AND cust.hawker_code =? AND cust.line_num =? group by sub.frequency");
			stmt.setString(1, hawkerComboBox.getSelectionModel().getSelectedItem());
			stmt.setInt(2, lineNumTable.getSelectionModel().getSelectedItem().getLineNum());
			ResultSet rs = stmt.executeQuery();

			dailyCount.setText("0");
			weeklyCount.setText("0");
			days14Count.setText("0");
			while (rs.next()) {
				switch(rs.getString(1)){
				case "Daily":
					dailyCount.setText(rs.getString(2));
					break;
				case "Weekly":
					weeklyCount.setText(rs.getString(2));
					break;
				case "14 Days":
					days14Count.setText(rs.getString(2));
					break;
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	

	
//	@Override
	public void reloadData() {
		System.out.println("Entered Line Info ReloadData");
		populatePointNames();
		addPointName.getItems().clear();
		addPointName.getItems().addAll(pointNameValues);
		lineNumCustomersTable.getItems().clear();

		if (HawkerLoginController.loggedInHawker != null) {
			addPointName.getSelectionModel().select(HawkerLoginController.loggedInHawker.getPointName());
			addPointName.setDisable(true);
			hawkerComboBox.getSelectionModel().select(HawkerLoginController.loggedInHawker.getHawkerCode());
			hawkerComboBox.setDisable(true);
			
		}
		else {
			lineNumTable.getItems().clear();
			lineNumTable.refresh();
		}
	}
//	@Override
	public void releaseVariables(){
		hawkerCodeData = null;
		lineNumData = null;
		customerData = null;
		hawkerCodeData = FXCollections.observableArrayList();
		lineNumData = FXCollections.observableArrayList();
		customerData = FXCollections.observableArrayList();
		pointNameValues = FXCollections.observableArrayList();
		newHouseSeqTFArray=null;
	}
}

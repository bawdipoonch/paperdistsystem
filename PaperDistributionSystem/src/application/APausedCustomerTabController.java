package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.Duration;

public class APausedCustomerTabController implements Initializable {
	

	@FXML
	private ComboBox<String> hawkerComboBox;
	@FXML
	private ComboBox<String> addPointName;
	
	@FXML
	private TableView<PausedSubscription> pausedCustTable;

	@FXML
	private TableColumn<PausedSubscription, String> custNameCol;

	@FXML
	private TableColumn<PausedSubscription, Long> custCodeCol;

	@FXML
	private TableColumn<PausedSubscription, String> custMobCol;

	@FXML
	private TableColumn<PausedSubscription, String> custHwkCodeCol;

	@FXML
	private TableColumn<PausedSubscription, String> custLineNumCol;

	@FXML
	private TableColumn<PausedSubscription, Integer> custHouseSeqCol;

	@FXML
	private TableColumn<PausedSubscription, String> prodNameCol;

	@FXML
	private TableColumn<PausedSubscription, String> prodTypeCol;

	@FXML
	private TableColumn<PausedSubscription, String> subTypeCol;

	@FXML
	private TableColumn<PausedSubscription, String> freqCol;

	@FXML
	private TableColumn<PausedSubscription, String> paymentTypeCol;

	@FXML
	private TableColumn<PausedSubscription, Double> subCostCol;

	@FXML
	private TableColumn<PausedSubscription, Double> srvChargeCol;

	@FXML
	private TableColumn<PausedSubscription, LocalDate> pausedDateCol;
	@FXML
	private TableColumn<PausedSubscription, LocalDate> resumeDateCol;

	private ObservableList<PausedSubscription> pausedSubsValues = FXCollections.observableArrayList();
	private ObservableList<LineInfo> lineNumData = FXCollections.observableArrayList();
	private ObservableList<String> pointNameValues = FXCollections.observableArrayList();
	private ObservableList<String> hawkerCodeData = FXCollections.observableArrayList();
	

	@FXML
	private TableColumn<LineInfo, String> lineNumColumn;
	@FXML
	private TableView<LineInfo> lineNumTable;

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		custNameCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, String>("customerName"));
		custCodeCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, Long>("customerCode"));
		custMobCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, String>("mobileNum"));
		custHwkCodeCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, String>("hawkerCode"));
		custLineNumCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, String>("lineNum"));
		custHouseSeqCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, Integer>("houseSeq"));
		prodNameCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, String>("productName"));
		prodTypeCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, String>("productType"));
		subTypeCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, String>("subscriptionType"));
		freqCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, String>("frequency"));
		paymentTypeCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, String>("paymentType"));
		subCostCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, Double>("subscriptionCost"));
		srvChargeCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, Double>("serviceCharge"));
		pausedDateCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, LocalDate>("pausedDate"));
		resumeDateCol.setCellValueFactory(new PropertyValueFactory<PausedSubscription, LocalDate>("resumeDate"));
		pausedDateCol.setCellFactory(
				new Callback<TableColumn<PausedSubscription, LocalDate>, TableCell<PausedSubscription, LocalDate>>() {

					@Override
					public TableCell<PausedSubscription, LocalDate> call(
							TableColumn<PausedSubscription, LocalDate> param) {
						TextFieldTableCell<PausedSubscription, LocalDate> cell = new TextFieldTableCell<PausedSubscription, LocalDate>();
						cell.setConverter(Main.dateConvertor);
						return cell;
					}
				});
		resumeDateCol.setCellFactory(
				new Callback<TableColumn<PausedSubscription, LocalDate>, TableCell<PausedSubscription, LocalDate>>() {

					@Override
					public TableCell<PausedSubscription, LocalDate> call(
							TableColumn<PausedSubscription, LocalDate> param) {
						TextFieldTableCell<PausedSubscription, LocalDate> cell = new TextFieldTableCell<PausedSubscription, LocalDate>();
						cell.setConverter(Main.dateConvertor);
						return cell;
					}
				});
		pausedCustTable.setRowFactory(new Callback<TableView<PausedSubscription>, TableRow<PausedSubscription>>() {

			@Override
			public TableRow<PausedSubscription> call(TableView<PausedSubscription> param) {
				final TableRow<PausedSubscription> row = new TableRow<>();
				MenuItem mnuRes = new MenuItem("Resume subscription");
				mnuRes.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent t) {
						PausedSubscription pausedSubsRow = pausedCustTable.getSelectionModel().getSelectedItem();
						if (pausedSubsRow != null) {
							Dialog<ButtonType> deleteWarning = new Dialog<ButtonType>();
							deleteWarning.setTitle("Warning");
							deleteWarning.setHeaderText("Are you sure you want to RESUME this record?");
							deleteWarning.getDialogPane().getButtonTypes().addAll(ButtonType.YES, ButtonType.CANCEL);
							Optional<ButtonType> result = deleteWarning.showAndWait();
							if (result.isPresent() && result.get() == ButtonType.YES) {
								pausedSubsRow.resumeSubscription();
								refreshPausedCustTable();
							}

						}
					}

				});
				ContextMenu menu = new ContextMenu();
				menu.getItems().addAll(mnuRes);
				row.contextMenuProperty().bind(
						Bindings.when(Bindings.isNotNull(row.itemProperty())).then(menu).otherwise((ContextMenu) null));
				return row;
			}
		});
		
		lineNumColumn.setCellValueFactory(new PropertyValueFactory<LineInfo, String>("lineNumDist"));
		lineNumTable.setDisable(true);
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
					refreshLineNumTableForHawker(newValue);
				}else {
					lineNumTable.getItems().clear();
					lineNumTable.refresh();
					lineNumData.clear();
					pausedCustTable.getItems().clear();
					pausedCustTable.refresh();
					pausedSubsValues.clear();
				}

			}

		});

		lineNumTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<LineInfo>() {

			@Override
			public void changed(ObservableValue<? extends LineInfo> observable, LineInfo oldValue, LineInfo newValue) {

				refreshPausedCustTable();
			}

		});

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

	public void refreshPausedCustTable() {
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {

					Connection con = Main.dbConnection;
					while (!con.isValid(0)) {
						con = Main.reconnect();
					}
					pausedSubsValues.clear();
					PreparedStatement stmt;
					String queryString = "select cust.CUSTOMER_ID, sub.SUBSCRIPTION_ID, prod.PRODUCT_ID, cust.NAME Customer_Name, cust.CUSTOMER_CODE, cust.MOBILE_NUM,cust.HAWKER_CODE, cust.LINE_NUM, cust.HOUSE_SEQ, prod.NAME Product_Name, prod.TYPE product_type, sub.TYPE subscription_type, sub.FREQUENCY, sub.PAYMENT_TYPE, sub.SUBSCRIPTION_COST, sub.SERVICE_CHARGE, sub.PAUSED_DATE, sub.resume_date from customer cust, subscription sub, products prod where cust.customer_id=sub.customer_id and sub.product_id=prod.product_id and sub.STATUS='Paused' and hawker_code=? and cust.LINE_NUM=? order by sub.resume_date desc, cust.HAWKER_CODE, cust.LINE_NUM, cust.HOUSE_SEQ, prod.name";
					
					if (HawkerLoginController.loggedInHawker != null) {
						stmt = con.prepareStatement(queryString);
						stmt.setString(1, HawkerLoginController.loggedInHawker.getHawkerCode());
					} else {
						stmt = con.prepareStatement(queryString);
						stmt.setString(1, hawkerComboBox.getSelectionModel().getSelectedItem());
					}

					stmt.setInt(2, lineNumTable.getSelectionModel().getSelectedItem().getLineNum());
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						pausedSubsValues.add(new PausedSubscription(rs.getLong(1), rs.getLong(2), rs.getLong(3),
								rs.getString(4), rs.getLong(5), rs.getString(6), rs.getString(7), rs.getString(8),
								rs.getInt(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13),
								rs.getString(14), rs.getDouble(15), rs.getDouble(16),
								rs.getDate(17) == null ? null : rs.getDate(17).toLocalDate(),
								rs.getDate(18) == null ? null : rs.getDate(18).toLocalDate()));
					}

					pausedCustTable.getItems().clear();
					pausedCustTable.getItems().addAll(pausedSubsValues);
					pausedCustTable.refresh();

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
	
//	@Override
	public void reloadData() {
		populatePointNames();
		addPointName.getItems().clear();
		addPointName.getItems().addAll(pointNameValues);
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
		pausedSubsValues = null;
		pausedSubsValues = FXCollections.observableArrayList();
		hawkerCodeData = FXCollections.observableArrayList();
		lineNumData = FXCollections.observableArrayList();
		pointNameValues = FXCollections.observableArrayList();
	}

}

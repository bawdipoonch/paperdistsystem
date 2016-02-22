package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
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

public class APausedCustomerTabController implements Initializable {
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
	

	private ObservableList<PausedSubscription> pausedSubsValues = FXCollections.observableArrayList();

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
		pausedDateCol.setCellFactory(new Callback<TableColumn<PausedSubscription,LocalDate>, TableCell<PausedSubscription,LocalDate>>() {
			
			@Override
			public TableCell<PausedSubscription, LocalDate> call(TableColumn<PausedSubscription, LocalDate> param) {
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

	}
	
	public void refreshPausedCustTable(){
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
					String queryString;
					if(HawkerLoginController.loggedInHawker!=null){
						queryString = 
								"select cust.CUSTOMER_ID, sub.SUBSCRIPTION_ID, prod.PRODUCT_ID, cust.NAME Customer_Name, cust.CUSTOMER_CODE, cust.MOBILE_NUM,cust.HAWKER_CODE, cust.LINE_NUM, cust.HOUSE_SEQ, prod.NAME Product_Name, prod.TYPE product_type, sub.TYPE subscription_type, sub.FREQUENCY, sub.PAYMENT_TYPE, sub.SUBSCRIPTION_COST, sub.SERVICE_CHARGE, sub.PAUSED_DATE from customer cust, subscription sub, products prod where cust.customer_id=sub.customer_id and sub.product_id=prod.product_id and sub.STATUS='Paused' and hawker_code=? order by cust.HAWKER_CODE, cust.LINE_NUM, cust.HOUSE_SEQ, prod.name";
						stmt = con.prepareStatement(queryString);
						stmt.setString(1, HawkerLoginController.loggedInHawker.getHawkerCode());
					} else {
						queryString = 
								"select cust.CUSTOMER_ID, sub.SUBSCRIPTION_ID, prod.PRODUCT_ID, cust.NAME Customer_Name, cust.CUSTOMER_CODE, cust.MOBILE_NUM,cust.HAWKER_CODE, cust.LINE_NUM, cust.HOUSE_SEQ, prod.NAME Product_Name, prod.TYPE product_type, sub.TYPE subscription_type, sub.FREQUENCY, sub.PAYMENT_TYPE, sub.SUBSCRIPTION_COST, sub.SERVICE_CHARGE, sub.PAUSED_DATE from customer cust, subscription sub, products prod where cust.customer_id=sub.customer_id and sub.product_id=prod.product_id and sub.STATUS='Paused' order by cust.HAWKER_CODE, cust.LINE_NUM, cust.HOUSE_SEQ, prod.name";
						stmt = con.prepareStatement(queryString);
					}
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						pausedSubsValues.add(new PausedSubscription(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getLong(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getInt(9), rs.getString(10), rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getDouble(15), rs.getDouble(16), rs.getDate(17).toLocalDate()));
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

	public void reloadData() {
		refreshPausedCustTable();
	}

}

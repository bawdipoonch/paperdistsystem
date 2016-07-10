package application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.controlsfx.control.Notifications;
import org.jpedal.examples.viewer.OpenViewerFX;

import com.qoppa.pdf.PDFException;
import com.qoppa.pdfViewerFX.PDFViewer;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.ExporterInput;
import net.sf.jasperreports.export.OutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class AReportsTabController implements Initializable {

	@FXML
	private ComboBox<String> addHawkerCodeLOV;
	@FXML
	private ComboBox<String> addLineNumLOV;

	@FXML
	private Button lineAllSubButton;

	private ObservableList<String> hawkerCodeData = FXCollections.observableArrayList();
	private ObservableList<String> hawkerLineNumData = FXCollections.observableArrayList();

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		addHawkerCodeLOV.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue != null) {
					addLineNumLOV.getItems().clear();
					populateLineNumbersForHawkerCode(newValue);
					addLineNumLOV.getSelectionModel().clearSelection();
				} else {
					hawkerLineNumData = FXCollections.observableArrayList();
					addLineNumLOV.setItems(hawkerLineNumData);
				}
			}
		});

	}


	private void populateLineNumbersForHawkerCode(String hawkerCode) {
		Main._logger.debug("Entered  populateLineNumbersForHawkerCode  method");

		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			hawkerLineNumData.clear();
			PreparedStatement stmt = con.prepareStatement(
					"select li.LINE_NUM || ' ' || ld.NAME as line_num_dist from line_info li, line_distributor ld where li.HAWKER_ID=ld.HAWKER_ID(+) and li.line_num=ld.line_num(+) and li.hawker_id = ? and li.line_num<>0 order by li.line_num");
			stmt.setLong(1, hawkerIdForCode(hawkerCode));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				hawkerLineNumData.add(rs.getString(1));
			}
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					addLineNumLOV.setItems(hawkerLineNumData);

					new AutoCompleteComboBoxListener<>(addLineNumLOV);
				}
			});
		} catch (SQLException e) {

			Main._logger.debug("Error :", e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :", e);
			e.printStackTrace();
		}
	}

	private long hawkerIdForCode(String hawkerCode) {
		Main._logger.debug("Entered  hawkerIdForCode  method");

		long hawkerId = -1;
		Connection con = Main.dbConnection;
		try {
			if (!con.isValid(0)) {
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

			Main._logger.debug("Error :", e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :", e);
			e.printStackTrace();
		}
		return hawkerId;
	}

	private void populateHawkerCodes() {

		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {

				synchronized (this) {
					try {

						Connection con = Main.dbConnection;
						if (!con.isValid(0)) {
							con = Main.reconnect();
						}
						if (HawkerLoginController.loggedInHawker != null) {
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									hawkerCodeData = FXCollections.observableArrayList();
									hawkerCodeData.add(HawkerLoginController.loggedInHawker.getHawkerCode());
									addHawkerCodeLOV.setItems(hawkerCodeData);
									addHawkerCodeLOV.getSelectionModel().selectFirst();
									addHawkerCodeLOV.setDisable(true);
								}
							});
						} else {
							hawkerCodeData = FXCollections.observableArrayList();
							PreparedStatement stmt = con.prepareStatement(
									"select distinct hawker_code from hawker_info order by hawker_code");
							ResultSet rs = stmt.executeQuery();
							while (rs.next()) {
								if (hawkerCodeData != null && !hawkerCodeData.contains(rs.getString(1)))
									hawkerCodeData.add(rs.getString(1));
							}
							Platform.runLater(new Runnable() {

								@Override
								public void run() {

									addHawkerCodeLOV.getItems().clear();
									addHawkerCodeLOV.setItems(hawkerCodeData);
									new AutoCompleteComboBoxListener<>(addHawkerCodeLOV);
								}
							});
							rs.close();
							stmt.close();
						}
					} catch (

					SQLException e) {

						Main._logger.debug("Error :", e);
						e.printStackTrace();
					} catch (Exception e) {

						Main._logger.debug("Error :", e);
						e.printStackTrace();

					}
				}
				return null;
			}

		};

		new Thread(task).start();

	}
	

	@FXML
	void lineAllSubButtonClicked(ActionEvent event) {
		try {
			String reportSrcFile = "HwkLineAllSubsList.jrxml";

			InputStream input = BillingUtilityClass.class.getResourceAsStream(reportSrcFile);
			JasperReport jasperReport = JasperCompileManager.compileReport(input);

			// Parameters for report
			Map<String, Object> parameters = new HashMap<String, Object>();
			String hawkerCode = addHawkerCodeLOV.getSelectionModel().getSelectedItem();
			String lineNum = addLineNumLOV.getSelectionModel().getSelectedItem().split(" ")[0];
			parameters.put("HAWKER_CODE", hawkerCode);
			parameters.put("LINE_NUM", lineNum);
			JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, Main.dbConnection);

			// Make sure the output directory exists.
			File outDir = new File("C:/pds");
			outDir.mkdirs();

			// PDF Exportor.
			JRPdfExporter exporter = new JRPdfExporter();

			ExporterInput exporterInput = new SimpleExporterInput(print);
			// ExporterInput
			exporter.setExporterInput(exporterInput);

			// ExporterOutput
			String filename = "C:/pds/" + hawkerCode + "-" + lineNum + "-"
					+ "SubscriptionList" + ".pdf";
			OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(filename);
			// Output
			exporter.setExporterOutput(exporterOutput);

			//
			SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
			exporter.setConfiguration(configuration);
			exporter.exportReport();
			File outFile = new File(filename);

			Notifications.create().title("Invocie PDF Created").text("Invoice PDF created at : " + filename)
					.hideAfter(Duration.seconds(15)).showInformation();
			
			BorderPane root;
			root = new BorderPane();
			Stage stage = new Stage();
			stage.setTitle("Invoice PDF");
			stage.setScene(new Scene(root, 1024, 800));
			OpenViewerFX fx = new OpenViewerFX(stage, null);
			fx.setupViewer();
			fx.openDefaultFile(outFile.getAbsolutePath());
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent event) {
					((Stage)event.getSource()).hide();
					
				}
			});
			stage.show();


		} catch (JRException e) {
			Main._logger.debug("Error during Bill PDF Generation: ", e);
		}
	}
	

	public BorderPane createAndLoad(File pdfFile) {
		long before = System.currentTimeMillis();
		PDFViewer notesBean = new PDFViewer();
		System.out.println("After: " + (System.currentTimeMillis() - before));
		new Thread(() -> {
			try {
				notesBean.loadPDF(new FileInputStream(pdfFile));
			} catch (PDFException | FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}).run();
		BorderPane borderPane = new BorderPane(notesBean);
		return borderPane;
	}

	public void reloadData() {
		hawkerLineNumData.clear();
		hawkerCodeData.clear();
		populateHawkerCodes();
	}

	public void releaseVariables() {

		hawkerCodeData = null;
		hawkerLineNumData = null;

		hawkerCodeData = FXCollections.observableArrayList();
		hawkerLineNumData = FXCollections.observableArrayList();
	}

}

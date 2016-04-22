package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.controlsfx.control.Notifications;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Main extends Application {
	public static Connection dbConnection = null;
	public static String dateFormat = "dd/MM/yyyy";
	public static StringConverter<LocalDate> dateConvertor = new StringConverter<LocalDate>() {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(Main.dateFormat);

		@Override
		public String toString(LocalDate date) {
			if (date != null) {
				return dateFormatter.format(date);
			} else {
				return "";
			}
		}

		@Override
		public LocalDate fromString(String string) {
			if (string != null && !string.isEmpty()) {
				return LocalDate.parse(string, dateFormatter);
			} else {
				return null;
			}
		}
	};
	final private static String ACCESS_KEY = "AKIAJHK6Z2KAU4WJSTGQ";
	final private static String SECRET = "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";
	final private static String dbConnectionString = "jdbc:oracle:thin:@lateefahmedpds.c3in7ocqfbfv.ap-southeast-1.rds.amazonaws.com:1521:ORCL";
	public static Stage primaryStage;
	
	public static final Logger _logger = LogManager.getLogger(Main.class.getName());
	
	@Override
	public void start(Stage primaryStage) {
		Main._logger.debug("Start method ");
		Main.primaryStage=primaryStage;
		try {
			Pane root = (Pane) FXMLLoader.load(getClass().getResource("HawkerLogin.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Main.primaryStage.setScene(scene);
			Main.primaryStage.setMaximized(true);
			Main.primaryStage.show();
		} catch (Exception e) {
			Main._logger.debug(e.getStackTrace());
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Main._logger.debug("Entered Main method");
		try {
			// step1 load the driver class
			Class.forName("oracle.jdbc.driver.OracleDriver");
			AWSCredentials credentials = null;
			credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET);
			AmazonRDSClient client = new AmazonRDSClient(credentials);
			client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_1));
			DescribeDBInstancesRequest req = new DescribeDBInstancesRequest();
			req.setDBInstanceIdentifier("lateefahmedpds");
			DescribeDBInstancesResult result = client.describeDBInstances();
			DBInstance dbInstance = (DBInstance) result.getDBInstances().toArray()[0];
			String address=dbInstance.getEndpoint().getAddress();
			
			// step2 create the connection object
			dbConnection = DriverManager.getConnection("jdbc:oracle:thin:@"+dbInstance.getEndpoint().getAddress()+":"+dbInstance.getEndpoint().getPort()+":ORCL", "admin", "LateefAhmedPDS");

			// step3 create the statement object
			// Statement stmt = dbConnection.createStatement();

			// step4 execute query
			// ResultSet rs = stmt.executeQuery("select * from CUSTOMER");
			// while (rs.next())
			// System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " +
			// rs.getString(3));

			// step5 close the connection object
			// con.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		launch(args);
	}

	@Override
	public void init() throws Exception {
		// TODO Auto-generated method stub
		super.init();
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		super.stop();
		dbConnection.close();
	}

	public static Connection reconnect() {
		try {
			Notifications.create().title("Checking connection!").text("Checking database connection.")
					.showInformation();
			if (!dbConnection.isValid(0)) {
				Notifications.create().title("Connection Error!").text("Database connection lost, reconnecting")
						.showError();
				// step1 load the driver class
				Class.forName("oracle.jdbc.driver.OracleDriver");

				// step2 create the connection object
				dbConnection = DriverManager.getConnection(dbConnectionString, "admin", "LateefAhmedPDS");

				Notifications.create().title("Connection Success!").text("Database reconnected").show();
			} else {
				Notifications.create().title("All good!").text("Dabatase connection seems fine, please try again.")
						.showInformation();
			}

		} catch (Exception e) {
			System.out.println(e);
			Notifications.create().title("Connection Error!")
					.text("Cannot connect to amazon database, contact administrator!").showError();
		}

		return dbConnection;
	}
}

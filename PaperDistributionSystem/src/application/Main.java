package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.controlsfx.control.Notifications;

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
	final private static String ACCESS_KEY = "AKIAILIR5CDWQ2B4KORQ";
	final private static String SECRET = "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";
	final private static String dbConnectionString = "jdbc:oracle:thin:@lateefahmedpds.c3in7ocqfbfv.ap-southeast-1.rds.amazonaws.com:1521:ORCL";

	@Override
	public void start(Stage primaryStage) {
		try {
			Pane root = (Pane) FXMLLoader.load(getClass().getResource("HawkerLogin.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		try {
			// step1 load the driver class
			Class.forName("oracle.jdbc.driver.OracleDriver");

			// step2 create the connection object
			dbConnection = DriverManager.getConnection(dbConnectionString, "admin", "LateefAhmedPDS");

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

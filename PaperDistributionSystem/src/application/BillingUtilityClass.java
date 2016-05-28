package application;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.controlsfx.control.Notifications;

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

public class BillingUtilityClass {

	public static void createBillingLines(Billing bill, Customer cust, LocalDate startDate, LocalDate endDate,
			boolean regenerate) {
		ArrayList<Subscription> subsList = subsListForCust(cust, startDate, endDate);
		HashMap<String, Double> prodValPair = new HashMap<String, Double>();
		HashMap<String, Double> prodTeaExpPair = new HashMap<String, Double>();
		ArrayList<String> prodList = new ArrayList<String>();
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select distinct prod.code from subscription sub, products prod where sub.PRODUCT_ID=prod.PRODUCT_ID and sub.customer_id =? and (stop_date is null or ? between start_date and stop_date-1) order by prod.code";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(1, cust.getCustomerId());
			stmt.setDate(2, Date.valueOf(endDate));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				// prodValPair.put(rs.getString(1), 0.0);
				prodList.add(rs.getString(1));
			}
			rs.close();
			for (Subscription sub : subsList) {
				double val = calculateBillForSubscription(sub, startDate, endDate, regenerate);
				if ((sub.getSubscriptionType().equals("Free Copy")
						|| sub.getSubscriptionType().equals("Coupon Copy/Adv Payment"))) {
					prodValPair.put(sub.getProductCode(), (prodValPair.get(sub.getProductCode()) == null ? 0.0
							: prodValPair.get(sub.getProductCode())) + val);
					prodTeaExpPair.put(sub.getProductCode(), (prodTeaExpPair.get(sub.getProductCode()) == null ? 0.0
							: prodTeaExpPair.get(sub.getProductCode())) + sub.getServiceCharge());
				}

				else if (val > 0) {
					prodValPair.put(sub.getProductCode(), (prodValPair.get(sub.getProductCode()) == null ? 0.0
							: prodValPair.get(sub.getProductCode())) + val);
					prodTeaExpPair.put(sub.getProductCode(), (prodTeaExpPair.get(sub.getProductCode()) == null ? 0.0
							: prodTeaExpPair.get(sub.getProductCode())) + sub.getServiceCharge());
				}

			}
			try {

				// Iterator<String> set = prodValPair.keySet().iterator();

				Object[] setArray = prodValPair.keySet().toArray();
				for (int i = 0; i < setArray.length; i++) {
					// for(String s : set.next()){

					try {
						String s = (String) setArray[i];
						String insertStmt = "INSERT INTO BILLING_LINES(BILL_INVOICE_NUM,LINE_NUM,PRODUCT,AMOUNT,TEA_EXPENSES) VALUES(?,?,?,?,?)";
						stmt = con.prepareStatement(insertStmt);
						stmt.setLong(1, bill.getBillInvoiceNum());
						stmt.setInt(2, i + 1);
						stmt.setString(3, s);
						stmt.setDouble(4, prodValPair.get(s));
						stmt.setDouble(5, prodTeaExpPair.get(s));
						stmt.executeUpdate();

					} catch (SQLException e) {

						Main._logger.debug("Error :",e);
						e.printStackTrace();
					} catch (Exception e) {

						Main._logger.debug("Error :",e);
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				Main._logger.debug("Error :",e);
				e.printStackTrace();
			}
			bill.setDue(bill.getDue() + cust.getTotalDue());
			bill.updateBillingRecord();
			cust.setTotalDue(0.0);
			cust.updateCustomerRecord();
			stmt.close();

		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} finally {

		}
	}

	public static double calculateBillForSubscription(Subscription subRow, LocalDate startDate, LocalDate endDate,
			boolean regenerate) {
		ArrayList<StopHistory> stopHistoryList = new ArrayList<StopHistory>();
		boolean free = (subRow.getSubscriptionType().equals("Coupon Copy/Adv Payment")
				|| subRow.getSubscriptionType().equals("Free Copy"));
		double val = 0.0;
		int c = 0;
		switch (subRow.getPaymentType()) {
		case "Month End":
			stopHistoryList = stopHistoryList(subRow.getSubscriptionId(), startDate, endDate);
			c = Period.between(startDate, endDate).getDays()
					- (free ? 0 : stopHistListDaysCountForSub(stopHistoryList, startDate, endDate));
			if (c > 0) {
				LocalDate sDate = subRow.getStartDate().isAfter(startDate) ? subRow.getStartDate() : startDate;
				double subBill = calculateEOMBillForSub(subRow, sDate, endDate);
				double stpBill = (subRow.getSubscriptionType().equals("Coupon Copy/Adv Payment") ? 0.0
						: calculateStopHistoryAmounts(stopHistoryList, sDate, endDate, regenerate));

				val = subBill - stpBill == subRow.getServiceCharge() ? 0.0 : subBill - stpBill + subRow.getAddToBill();
			}
			return val;
		case "Current Month":
			stopHistoryList = stopHistoryList(subRow.getSubscriptionId(), startDate, endDate);
			// c = Period.between(startDate, endDate).getDays()
			// - (free ? 0 : stopHistListDaysCountForSub(stopHistoryList,
			// startDate, endDate));
			// if (c > 0) {
			LocalDate sDate = subRow.getStartDate().isAfter(startDate) ? subRow.getStartDate() : startDate;
			double subBill = calculateEOMBillForSub(subRow, startDate.plusMonths(1),
					startDate.plusMonths(1).plusMonths(1).minusDays(1));
			double stpBill = (subRow.getSubscriptionType().equals("Coupon Copy/Adv Payment") ? 0.0
					: calculateStopHistoryAmounts(stopHistoryList, startDate, endDate, regenerate));
			boolean fail = (subRow.getSubscriptionType().equals("Free Copy")
					|| subRow.getSubscriptionType().equals("Coupon Copy/Adv Payment"));
			double prodDiff = !fail ? calculateProdDiff(subRow, sDate, endDate) : 0.0;
			val = prodDiff - stpBill + subBill + subRow.getAddToBill();
			// }
			return val;
		// calculateEOMBillForSub(subRow, startDate.minusMonths(1),
		// endDate.minusMonths(1))+
		}

		return 0;

	}

	public static void createCurrentMonthStartingDue(Subscription subRow, LocalDate startDate, LocalDate endDate) {
		double val = calculateActualBilling(subRow, startDate, endDate);
		Customer cust = custForCustId(subRow.getCustomerId());
		cust.setTotalDue(cust.getTotalDue() + val + subRow.getServiceCharge() + subRow.getAddToBill());
		cust.updateCustomerRecord();
	}

	public static void createBillingInvoice(long customerId, LocalDate startDate, LocalDate endDate,
			boolean regenerate) {

		Billing bill = findBillingInvoice(customerId, startDate.withDayOfMonth(1).plusMonths(1).minusDays(1));
		boolean eom = isEOM(customerId);
		String month = eom ? startDate.getMonth().toString()
				: startDate.withDayOfMonth(1).plusMonths(1).getMonth().toString();
		if (bill != null) {
			deleteBillingLines(bill);
		} else {
			try {

				Connection con = Main.dbConnection;
				if (!con.isValid(0)) {
					con = Main.reconnect();
				}
				String insertStmt = "INSERT INTO BILLING(INVOICE_DATE, CUSTOMER_ID, MONTH) VALUES(?,?,?)";
				PreparedStatement stmt = con.prepareStatement(insertStmt, new String[] { "BILL_INVOICE_NUM" });
				stmt.setLong(2, customerId);
				stmt.setDate(1, Date.valueOf(endDate));
				stmt.setString(3, month);
				if (stmt.executeUpdate() > 0) {
					// Notifications.create().title("Billing Invoice
					// created").text("Billing invoice for created")
					// .hideAfter(Duration.seconds(5)).showInformation();
				}

				bill = findBillingInvoice(customerId, startDate.withDayOfMonth(1).plusMonths(1).minusDays(1));
				stmt.close();
			} catch (SQLException e) {

				Main._logger.debug("Error :",e);
				e.printStackTrace();
			} catch (Exception e) {

				Main._logger.debug("Error :",e);
				e.printStackTrace();
			}
		}

		bill.setMonth(month);
		createBillingLines(bill, custForCustId(customerId), startDate, endDate, regenerate);

	}

	private static boolean isEOM(long customerId) {
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			// lineNumCustomersTable.getItems().clear();
			PreparedStatement stmt = con
					.prepareStatement("select distinct payment_type from subscription where customer_id=?");
			stmt.setLong(1, customerId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				if ("Month End".equals(rs.getString(1)))
					return true;

			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return false;
	}

	public static void createBillingInvoiceForHwkLine(String hawkerCode, int lineNum, LocalDate startDate,
			LocalDate endDate, boolean regenerate) {

		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			// lineNumCustomersTable.getItems().clear();
			PreparedStatement stmt = con.prepareStatement(
					"select customer_id from customer where hawker_code = ? and line_num = ? ORDER BY HOUSE_SEQ");
			stmt.setString(1, hawkerCode);
			stmt.setInt(2, lineNum);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				createBillingInvoice(rs.getInt(1), startDate, endDate, regenerate);

			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}

	}

	public static void createBillingInvoiceForHwk(String hawkerCode, LocalDate startDate, LocalDate endDate,
			boolean regenerate) {

		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			// lineNumCustomersTable.getItems().clear();
			PreparedStatement stmt = con
					.prepareStatement("select customer_id from customer where hawker_code = ? ORDER BY customer_id");
			stmt.setString(1, hawkerCode);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				createBillingInvoice(rs.getInt(1), startDate, endDate, regenerate);

			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}

	}
	
	public static void createHawkerBill(Hawker hawker, LocalDate endDate){
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			// lineNumCustomersTable.getItems().clear();
			PreparedStatement stmt = con
					.prepareStatement("SELECT hwk.hawker_code, COUNT(line.PRODUCT) CNT FROM hawker_info hwk, customer cust, billing bill, billing_lines line WHERE hwk.HAWKER_CODE =cust.HAWKER_CODE AND cust.CUSTOMER_ID =bill.CUSTOMER_ID AND bill.BILL_INVOICE_NUM=line.BILL_INVOICE_NUM and hwk.hawker_code=? and bill.INVOICE_DATE=? GROUP BY hwk.hawker_code ORDER BY hwk.hawker_code");
			stmt.setString(1, hawker.getHawkerCode());
			stmt.setDate(2, Date.valueOf(endDate));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
	}

	private static void deleteBillingLines(Billing bill) {

		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String insertStmt = "DELETE FROM BILLING_LINES WHERE BILL_INVOICE_NUM=?";
			PreparedStatement stmt = con.prepareStatement(insertStmt);
			stmt.setLong(1, bill.getBillInvoiceNum());
			if (stmt.executeUpdate() > 0) {
				// Notifications.create().title("Deleted").text("Billing lines
				// deleted").hideAfter(Duration.seconds(5))
				// .showInformation();
			}
			stmt.close();
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
	}

	public static Billing findBillingInvoice(long customerId, LocalDate invoiceDate) {
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String insertStmt = "select BILL_INVOICE_NUM, CUSTOMER_ID , INVOICE_DATE, PDF_URL, DUE, MONTH from BILLING where customer_id=? and invoice_date=?";
			PreparedStatement stmt = con.prepareStatement(insertStmt);
			stmt.setLong(1, customerId);
			stmt.setDate(2, Date.valueOf(invoiceDate));
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Billing(rs.getLong(1), rs.getLong(2), rs.getDate(3).toLocalDate(), rs.getString(4),
						rs.getDouble(5), rs.getString(6));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return null;
	}

	private static ArrayList<Subscription> subsListForCust(Customer cust, LocalDate startDate, LocalDate endDate) {
		ArrayList<Subscription> subsList = new ArrayList<Subscription>();
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select sub.SUBSCRIPTION_ID, sub.CUSTOMER_ID, sub.PRODUCT_ID, prod.name, prod.type, sub.PAYMENT_TYPE, sub.SUBSCRIPTION_COST, sub.SERVICE_CHARGE, sub.FREQUENCY, sub.TYPE, sub.DOW, sub.STATUS, sub.START_DATE, sub.PAUSED_DATE, prod.CODE, sub.STOP_DATE, sub.DURATION, sub.OFFER_MONTHS, sub.SUB_NUMBER, sub.resume_date, sub.ADD_TO_BILL from subscription sub, products prod where sub.PRODUCT_ID=prod.PRODUCT_ID and sub.customer_id =? and (stop_date is null or ? between start_date and stop_date-1)  order by prod.name";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(1, cust.getCustomerId());
			stmt.setDate(2, Date.valueOf(endDate));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				subsList.add(new Subscription(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getString(4),
						rs.getString(5), rs.getString(6), rs.getDouble(7), rs.getDouble(8), rs.getString(9),
						rs.getString(10), rs.getString(11), rs.getString(12),
						rs.getDate(13) == null ? null : rs.getDate(13).toLocalDate(),
						rs.getDate(14) == null ? null : rs.getDate(14).toLocalDate(), rs.getString(15),
						rs.getDate(16) == null ? null : rs.getDate(16).toLocalDate(), rs.getString(17), rs.getInt(18),
						rs.getString(19), rs.getDate(20) == null ? null : rs.getDate(20).toLocalDate(),
						rs.getDouble(21)));
			}
			rs.close();
			stmt.close();

		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return subsList;
	}

	private static double calculateEOMBillForSub(Subscription subRow, LocalDate startDate, LocalDate endDate) {

		double bill = 0.0;
		if (subRow.getSubscriptionType().equals("Actual Days Billing")) {
			bill = calculateActualBilling(subRow, startDate, endDate);
		} else if (subRow.getSubscriptionType().equals("Fixed Rate")) {
			if (subRow.getStartDate().isAfter(startDate.withDayOfMonth(1)))
				bill = calculateActualBilling(subRow, startDate, endDate);
			else
				bill = subRow.getCost() + calculateProdDiff(subRow, startDate, endDate);
		}
		return bill;
	}

	private static double calculateActualBilling(Subscription subRow, LocalDate startDate, LocalDate endDate) {
		boolean spcl = subRow.getPaymentType().equals("Month End");
		double bill = 0.0;
		Product prod = productForSub(subRow.getProductId());
		ArrayList<ProductSpecialPrice> prodSpclPriceList = prodSpclPriceList(prod.getProductId(), startDate, endDate);
		HashMap<LocalDate, Double> spclMap = new HashMap<LocalDate, Double>();
		for (ProductSpecialPrice psp : prodSpclPriceList) {
			spclMap.put(psp.getFullDate(), psp.getPrice());
		}
		if (subRow.getFrequency().equals("Daily")) {
			for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
				bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, date) : amountForDate(subRow, prod, date);

			}
			// bill += bill == 0.0 ? 0.0 : subRow.getServiceCharge();
		} else if (subRow.getFrequency().equals("Weekly")) {
			ArrayList<LocalDate> dateList = new ArrayList<LocalDate>();
			switch (subRow.getDow()) {
			case "Monday":
				dateList = weeksInPeriod(DayOfWeek.MONDAY, startDate, endDate);
				break;
			case "Tuesday":
				dateList = weeksInPeriod(DayOfWeek.TUESDAY, startDate, endDate);
				break;
			case "Wednesday":
				dateList = weeksInPeriod(DayOfWeek.WEDNESDAY, startDate, endDate);
				break;
			case "Thursday":
				dateList = weeksInPeriod(DayOfWeek.THURSDAY, startDate, endDate);
				break;
			case "Friday":
				dateList = weeksInPeriod(DayOfWeek.FRIDAY, startDate, endDate);
				break;
			case "Saturday":
				dateList = weeksInPeriod(DayOfWeek.SATURDAY, startDate, endDate);
				break;
			case "Sunday":
				dateList = weeksInPeriod(DayOfWeek.SUNDAY, startDate, endDate);
				break;
			}
			for (LocalDate date : dateList) {
				// bill += spclMap.containsKey(date) ? spclMap.get(date) :
				// prod.getPrice();
				// confirm whether to get price from DOW of SUB PRICE
				bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, date) : amountForDate(subRow, prod, date);

			}
			// bill += bill == 0.0 ? 0.0 : subRow.getServiceCharge();
		} else if (subRow.getFrequency().equals("14 Days")) {
			double first = Math.ceil(ChronoUnit.DAYS.between(prod.getFirstDeliveryDate(), startDate) / 14.0);
			LocalDate firstDate = prod.getFirstDeliveryDate().plusDays((int) (14 * first));
			LocalDate secondDate = !firstDate.plusDays(14).isAfter(endDate) ? firstDate.plusDays(14) : null;
			LocalDate thirdDate = secondDate == null ? null
					: !secondDate.plusDays(14).isAfter(endDate) ? secondDate.plusDays(14) : null;
			if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
				bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, firstDate)
						: amountForDate(subRow, prod, firstDate);

			}
			if (secondDate != null) {
				if (!(secondDate.isBefore(startDate)) && (!secondDate.isAfter(endDate))) {
					bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, secondDate)
							: amountForDate(subRow, prod, secondDate);

				}
			}
			if (thirdDate != null) {
				if (!(thirdDate.isBefore(startDate)) && (!thirdDate.isAfter(endDate))) {
					bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, thirdDate)
							: amountForDate(subRow, prod, thirdDate);

				}
			}
			// bill += bill == 0.0 ? 0.0 : subRow.getServiceCharge();
		} else if (subRow.getFrequency().equals("15 Days")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);
			LocalDate secondDate = firstDate.plusDays(15);
			if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
				bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, firstDate)
						: amountForDate(subRow, prod, firstDate);

			}
			if (secondDate != null) {
				if (!(secondDate.isBefore(startDate)) && (!secondDate.isAfter(endDate))) {
					bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, secondDate)
							: amountForDate(subRow, prod, secondDate);

				}
			}
			// bill += bill == 0.0 ? 0.0 : subRow.getServiceCharge();
		} else if (subRow.getFrequency().equals("Monthly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
				bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, firstDate)
						: amountForDate(subRow, prod, firstDate);

			}
			// bill += bill == 0.0 ? 0.0 : subRow.getServiceCharge();
		} else if (subRow.getFrequency().equals("Quarterly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (months % 3 == 0) {
				if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
					bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, firstDate)
							: amountForDate(subRow, prod, firstDate);

				}
			}
			// bill += bill == 0.0 ? 0.0 : subRow.getServiceCharge();
		} else if (subRow.getFrequency().equals("Half Yearly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (months % 6 == 0) {
				if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
					bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, firstDate)
							: amountForDate(subRow, prod, firstDate);

				}
			}
			// bill += bill == 0.0 ? 0.0 : subRow.getServiceCharge();
		} else if (subRow.getFrequency().equals("Yearly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (months % 12 == 0) {
				if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
					bill += spcl ? amountForDateSpcl(subRow, prod, spclMap, firstDate)
							: amountForDate(subRow, prod, firstDate);

				}
			}
			// bill += bill == 0.0 ? 0.0 : subRow.getServiceCharge();
		}

		return bill;
	}

	public static double calculateStopHistoryAmount(StopHistory stpRow) {
		Subscription subRow = subForSubId(stpRow.getSubscriptionId());
		Product prod = productForSub(subRow.getProductId());
		ArrayList<ProductSpecialPrice> prodSpclPriceList = prodSpclPriceList(prod.getProductId(), stpRow.getStopDate(),
				stpRow.getResumeDate().minusDays(1));
		HashMap<LocalDate, Double> spclMap = new HashMap<LocalDate, Double>();
		for (ProductSpecialPrice psp : prodSpclPriceList) {
			spclMap.put(psp.getFullDate(), psp.getPrice());
		}
		double bill = 0.0;
		if (subRow.getFrequency().equals("Daily")) {
			for (LocalDate date = stpRow.getStopDate(); date
					.isBefore(stpRow.getResumeDate()); date = date.plusDays(1)) {
				bill += amountForDateSpcl(subRow, prod, spclMap, date);

			}
		} else if (subRow.getFrequency().equals("Weekly")) {
			ArrayList<LocalDate> dateList = new ArrayList<LocalDate>();
			switch (subRow.getDow()) {
			case "Monday":
				dateList = weeksInPeriod(DayOfWeek.MONDAY, stpRow.getStopDate(), stpRow.getResumeDate().minusDays(1));
				break;
			case "Tuesday":
				dateList = weeksInPeriod(DayOfWeek.TUESDAY, stpRow.getStopDate(), stpRow.getResumeDate().minusDays(1));
				break;
			case "Wednesday":
				dateList = weeksInPeriod(DayOfWeek.WEDNESDAY, stpRow.getStopDate(),
						stpRow.getResumeDate().minusDays(1));
				break;
			case "Thursday":
				dateList = weeksInPeriod(DayOfWeek.THURSDAY, stpRow.getStopDate(), stpRow.getResumeDate().minusDays(1));
				break;
			case "Friday":
				dateList = weeksInPeriod(DayOfWeek.FRIDAY, stpRow.getStopDate(), stpRow.getResumeDate().minusDays(1));
				break;
			case "Saturday":
				dateList = weeksInPeriod(DayOfWeek.SATURDAY, stpRow.getStopDate(), stpRow.getResumeDate().minusDays(1));
				break;
			case "Sunday":
				dateList = weeksInPeriod(DayOfWeek.SUNDAY, stpRow.getStopDate(), stpRow.getResumeDate().minusDays(1));
				break;
			}
			for (LocalDate date : dateList) {
				// bill +=
				// prod.getPrice();
				// confirm whether to get price from DOW of SUB PRICE
				bill += amountForDateSpcl(subRow, prod, spclMap, date);

			}
		} else if (subRow.getFrequency().equals("14 Days")) {
			double first = Math.ceil(ChronoUnit.DAYS.between(prod.getFirstDeliveryDate(), stpRow.getStopDate()) / 14.0);
			LocalDate firstDate = prod.getFirstDeliveryDate().plusDays((int) (14 * first));
			LocalDate secondDate = firstDate.plusDays(14).isBefore(stpRow.getResumeDate()) ? firstDate.plusDays(14)
					: null;
			LocalDate thirdDate = secondDate == null ? null
					: secondDate.plusDays(14).isBefore(stpRow.getResumeDate()) ? secondDate.plusDays(14) : null;
			if (!(firstDate.isBefore(stpRow.getStopDate())) && (firstDate.isBefore(stpRow.getResumeDate()))) {
				bill += amountForDateSpcl(subRow, prod, spclMap, firstDate);

			}
			if (secondDate != null) {
				if (!(secondDate.isBefore(stpRow.getStopDate())) && (secondDate.isBefore(stpRow.getResumeDate()))) {
					bill += amountForDateSpcl(subRow, prod, spclMap, secondDate);

				}
			}
			if (thirdDate != null) {
				if (!(thirdDate.isBefore(stpRow.getStopDate())) && (thirdDate.isBefore(stpRow.getResumeDate()))) {
					bill += amountForDateSpcl(subRow, prod, spclMap, thirdDate);

				}
			}
		} else if (subRow.getFrequency().equals("15 Days")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), stpRow.getStopDate())
					.getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);
			LocalDate secondDate = firstDate.plusDays(15);
			if (!(firstDate.isBefore(stpRow.getStopDate())) && (firstDate.isBefore(stpRow.getResumeDate()))) {
				bill += amountForDateSpcl(subRow, prod, spclMap, firstDate);

			}
			if (secondDate != null) {
				if (!(secondDate.isBefore(stpRow.getStopDate())) && (secondDate.isBefore(stpRow.getResumeDate()))) {
					bill += amountForDateSpcl(subRow, prod, spclMap, secondDate);

				}
			}
		} else if (subRow.getFrequency().equals("Monthly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), stpRow.getStopDate())
					.getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (!(firstDate.isBefore(stpRow.getStopDate())) && (firstDate.isBefore(stpRow.getResumeDate()))) {
				bill += amountForDateSpcl(subRow, prod, spclMap, firstDate);

			}
		} else if (subRow.getFrequency().equals("Quarterly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), stpRow.getStopDate())
					.getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (months % 3 == 0) {
				if (!(firstDate.isBefore(stpRow.getStopDate())) && (firstDate.isBefore(stpRow.getResumeDate()))) {
					bill += amountForDateSpcl(subRow, prod, spclMap, firstDate);

				}
			}
		} else if (subRow.getFrequency().equals("Half Yearly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), stpRow.getStopDate())
					.getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (months % 6 == 0) {
				if (!(firstDate.isBefore(stpRow.getStopDate())) && (firstDate.isBefore(stpRow.getResumeDate()))) {
					bill += amountForDateSpcl(subRow, prod, spclMap, firstDate);

				}
			}
		} else if (subRow.getFrequency().equals("Yearly")) {
			int months = Period.between(prod.getFirstDeliveryDate(), stpRow.getStopDate()).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (months % 12 == 0) {
				if (!(firstDate.isBefore(stpRow.getStopDate())) && (firstDate.isBefore(stpRow.getResumeDate()))) {
					bill += amountForDateSpcl(subRow, prod, spclMap, firstDate);

				}
			}
		}
		return bill;

	}

	public static double calculateStopHistoryAmounts(ArrayList<StopHistory> stopHistoryList, LocalDate startDate,
			LocalDate endDate, boolean regenerate) {
		Subscription sub = null;
		double bill = 0.0;
		for (StopHistory stp : stopHistoryList) {
			sub = subForSubId(stp.getSubscriptionId());
			if (sub.getSubscriptionType().equals("Coupon Copy/Adv Payment")
					|| sub.getSubscriptionType().equals("Free Copy")) {

			} else {

				if (stp.getResumeDate() == null || stp.getResumeDate().minusDays(1).isAfter(endDate)) {
					StopHistory.splitOnDate(stp, endDate);
				}
				if ((stp.getAmount() == null || stp.getAmount() == 0.0) || regenerate) {
					stp.setAmount(calculateStopHistoryAmount(stp));
					stp.updateStopHistoryRecord();
				}
				bill += stp.getAmount();
			}
		}

		return bill;
	}

	public static ArrayList<StopHistory> stopHistoryList(long subId, LocalDate startDate, LocalDate endDate) {
		ArrayList<StopHistory> stopHistList = new ArrayList<StopHistory>();
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "SELECT STP.STOP_HISTORY_ID, CUST.NAME, CUST.CUSTOMER_CODE, CUST.MOBILE_NUM, CUST.HAWKER_CODE, CUST.LINE_NUM, SUB.SUBSCRIPTION_ID, CUST.HOUSE_SEQ, PROD.NAME, PROD.CODE, PROD.BILL_CATEGORY, STP.STOP_DATE, STP.RESUME_DATE, SUB.TYPE, SUB.FREQUENCY, SUB.DOW, STP.AMOUNT FROM STOP_HISTORY STP, CUSTOMER CUST, PRODUCTS PROD , SUBSCRIPTION SUB WHERE sub.SUBSCRIPTION_ID=? AND STP.SUB_ID =SUB.SUBSCRIPTION_ID AND SUB.CUSTOMER_ID =CUST.CUSTOMER_ID AND SUB.PRODUCT_ID =PROD.PRODUCT_ID  AND (stp.stop_date between ? and ?) ORDER BY SUB.PAUSED_DATE DESC";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(1, subId);
			stmt.setDate(2, Date.valueOf(startDate));
			stmt.setDate(3, Date.valueOf(endDate));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				stopHistList.add(new StopHistory(rs.getLong(1), rs.getString(2), rs.getLong(3), rs.getString(4),
						rs.getString(5), rs.getLong(6), rs.getLong(7), rs.getInt(8), rs.getString(9), rs.getString(10),
						rs.getString(11), rs.getDate(12) == null ? null : rs.getDate(12).toLocalDate(),
						rs.getDate(13) == null ? null : rs.getDate(13).toLocalDate(), rs.getString(14),
						rs.getString(15), rs.getString(16), rs.getDouble(17)));
			}
			rs.close();
			stmt.close();
			return stopHistList;
		} catch (SQLException e) {
			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return stopHistList;
	}

	public static ArrayList<StopHistory> stopHistoryListEOM(LocalDate endDate) {
		ArrayList<StopHistory> stopHistList = new ArrayList<StopHistory>();
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "SELECT STP.STOP_HISTORY_ID, CUST.NAME, CUST.CUSTOMER_CODE, CUST.MOBILE_NUM, CUST.HAWKER_CODE, CUST.LINE_NUM, SUB.SUBSCRIPTION_ID, CUST.HOUSE_SEQ, PROD.NAME, PROD.CODE, PROD.BILL_CATEGORY, STP.STOP_DATE, STP.RESUME_DATE, SUB.TYPE, SUB.FREQUENCY, SUB.DOW, STP.AMOUNT FROM STOP_HISTORY STP, CUSTOMER CUST, PRODUCTS PROD , SUBSCRIPTION SUB WHERE STP.SUB_ID =SUB.SUBSCRIPTION_ID AND SUB.CUSTOMER_ID =CUST.CUSTOMER_ID AND SUB.PRODUCT_ID =PROD.PRODUCT_ID  AND ((stp.resume_date is null and stp.stop_date between ? and ?) or ? between stp.stop_date and stp.resume_date-1) ORDER BY SUB.PAUSED_DATE DESC";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setDate(1, Date.valueOf(endDate.withDayOfMonth(1)));
			stmt.setDate(2, Date.valueOf(endDate.withDayOfMonth(1).plusMonths(1).minusDays(1)));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				StopHistory sh = new StopHistory(rs.getLong(1), rs.getString(2), rs.getLong(3), rs.getString(4),
						rs.getString(5), rs.getLong(6), rs.getLong(7), rs.getInt(8), rs.getString(9), rs.getString(10),
						rs.getString(11), rs.getDate(12) == null ? null : rs.getDate(12).toLocalDate(),
						rs.getDate(13) == null ? null : rs.getDate(13).toLocalDate(), rs.getString(14),
						rs.getString(15), rs.getString(16), rs.getDouble(17));
				StopHistory.splitOnDate(sh, endDate);
				stopHistList.add(sh);
			}
			rs.close();
			stmt.close();
			return stopHistList;
		} catch (SQLException e) {
			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return stopHistList;
	}

	public static ArrayList<ProductSpecialPrice> prodSpclPriceList(long productId, LocalDate startDate,
			LocalDate endDate) {
		ArrayList<ProductSpecialPrice> prodSpclPriceList = new ArrayList<ProductSpecialPrice>();
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			PreparedStatement stmt = con.prepareStatement(
					"select SPCL_PRICE_ID, PRODUCT_ID, FULL_DATE, PRICE from prod_spcl_price where product_id=? and FULL_DATE between ? and ? order by full_date desc");
			stmt.setLong(1, productId);
			stmt.setDate(2, Date.valueOf(startDate));
			stmt.setDate(3, Date.valueOf(endDate));
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				prodSpclPriceList.add(new ProductSpecialPrice(rs.getLong(1), rs.getLong(2), rs.getDate(3).toLocalDate(),
						rs.getDouble(4)));
			}
			rs.close();
			stmt.close();
			return prodSpclPriceList;
		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return prodSpclPriceList;
	}

	public static Product productForSub(long productId) {
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			PreparedStatement stmt;
			stmt = con.prepareStatement(
					"SELECT prod.PRODUCT_ID, prod.NAME, prod.TYPE, prod.SUPPORTED_FREQ, prod.MONDAY, prod.TUESDAY, prod.WEDNESDAY, prod.THURSDAY, prod.FRIDAY, prod.SATURDAY, prod.SUNDAY, prod.PRICE, prod.CODE, prod.DOW, prod.FIRST_DELIVERY_DATE, prod.ISSUE_DATE, prod.bill_Category FROM products prod where prod.product_id = ?");
			stmt.setLong(1, productId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Product(rs.getLong(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getDouble(5),
						rs.getDouble(6), rs.getDouble(7), rs.getDouble(8), rs.getDouble(9), rs.getDouble(10),
						rs.getDouble(11), rs.getDouble(12), rs.getString(13), rs.getString(14),
						rs.getDate(15) == null ? null : rs.getDate(15).toLocalDate(),
						rs.getDate(16) == null ? null : rs.getDate(16).toLocalDate(), rs.getString(17));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return null;
	}

	public static ArrayList<LocalDate> weeksInPeriod(DayOfWeek dow, LocalDate startDate, LocalDate endDate) {
		ArrayList<LocalDate> firstDaysOfWeeks = new ArrayList<LocalDate>();

		ChronoUnit.DAYS.between(startDate, endDate);
		LocalDate firstDOW = startDate.with(TemporalAdjusters.nextOrSame(dow));

		if (firstDOW.isAfter(endDate))
			firstDOW = null;
		if (firstDOW != null) {
			for (LocalDate date = firstDOW; !date.isAfter(endDate); date = date.plusWeeks(1)) {
				firstDaysOfWeeks.add(date);
			}
		}

		return firstDaysOfWeeks;
	}

	public static Subscription subForSubId(long subId) {
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select sub.SUBSCRIPTION_ID, sub.CUSTOMER_ID, sub.PRODUCT_ID, prod.name, prod.type, sub.PAYMENT_TYPE, sub.SUBSCRIPTION_COST, sub.SERVICE_CHARGE, sub.FREQUENCY, sub.TYPE, sub.DOW, sub.STATUS, sub.START_DATE, sub.PAUSED_DATE, prod.CODE, sub.STOP_DATE, sub.DURATION, sub.OFFER_MONTHS, sub.SUB_NUMBER, sub.resume_date, sub.ADD_TO_BILL from subscription sub, products prod where sub.PRODUCT_ID=prod.PRODUCT_ID and sub.subscription_id =? order by prod.name";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(1, subId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Subscription(rs.getLong(1), rs.getLong(2), rs.getLong(3), rs.getString(4), rs.getString(5),
						rs.getString(6), rs.getDouble(7), rs.getDouble(8), rs.getString(9), rs.getString(10),
						rs.getString(11), rs.getString(12),
						rs.getDate(13) == null ? null : rs.getDate(13).toLocalDate(),
						rs.getDate(14) == null ? null : rs.getDate(14).toLocalDate(), rs.getString(15),
						rs.getDate(16) == null ? null : rs.getDate(16).toLocalDate(), rs.getString(17), rs.getInt(18),
						rs.getString(19), rs.getDate(20) == null ? null : rs.getDate(20).toLocalDate(),
						rs.getDouble(21));
			}
			rs.close();
			stmt.close();

		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return null;
	}

	public static Customer custForCustId(long custId) {
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select customer_id,customer_code, name,mobile_num,hawker_code, line_Num, house_Seq, old_house_num, new_house_num, ADDRESS_LINE1, ADDRESS_LINE2, locality, city, state,profile1,profile2,profile3,initials, employment, comments, building_street, total_due from customer where customer_id = ? ";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(1, custId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return new Customer(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4), rs.getString(5),
						rs.getLong(6), rs.getInt(7), rs.getString(8), rs.getString(9), rs.getString(10),
						rs.getString(11), rs.getString(12), rs.getString(13), rs.getString(14), rs.getString(15),
						rs.getString(16), rs.getString(17), rs.getString(18), rs.getString(19), rs.getString(20),
						rs.getString(21), rs.getDouble(22));
			}
			rs.close();
			stmt.close();

		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return null;
	}

	public static int stopHistListDaysCountForSub(ArrayList<StopHistory> stopHistoryList, LocalDate startDate,
			LocalDate endDate) {
		int c = 0;
		// ArrayList<StopHistory> stpList =
		// stopHistoryList(stopHistoryList.getSubscriptionId(), startDate,
		// endDate);
		for (StopHistory stp : stopHistoryList) {
			if (stp.getResumeDate() == null || stp.getResumeDate().isAfter(endDate)) {
				StopHistory.splitOnDate(stp, endDate);
			}
			c += (Period.between(stp.getStopDate(), stp.getResumeDate().minusDays(1)).getDays());
		}
		return c;
	}

	public static double calculateTotalBillAmount(Billing bill) {
		try {

			Connection con = Main.dbConnection;
			if (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String query = "select sum(amount) + sum(tea_expenses) from billing_lines where bill_invoice_num = ?";
			PreparedStatement stmt = con.prepareStatement(query);
			stmt.setLong(1, bill.getBillInvoiceNum());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}
			rs.close();
			stmt.close();

		} catch (SQLException e) {

			Main._logger.debug("Error :",e);
			e.printStackTrace();
		}
		return 0.0;
	}

	public static double amountForDateSpcl(Subscription subRow, Product prod, HashMap<LocalDate, Double> spclMap,
			LocalDate date) {
		double bill = 0.0;
		switch (date.getDayOfWeek()) {
		case MONDAY:
			bill += spclMap.containsKey(date) ? spclMap.get(date)
					: subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getMonday();
			break;
		case TUESDAY:
			bill += spclMap.containsKey(date) ? spclMap.get(date)
					: subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getTuesday();
			break;
		case WEDNESDAY:
			bill += spclMap.containsKey(date) ? spclMap.get(date)
					: subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getWednesday();
			break;
		case THURSDAY:
			bill += spclMap.containsKey(date) ? spclMap.get(date)
					: subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getThursday();
			break;
		case FRIDAY:
			bill += spclMap.containsKey(date) ? spclMap.get(date)
					: subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getFriday();
			break;
		case SATURDAY:
			bill += spclMap.containsKey(date) ? spclMap.get(date)
					: subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getSaturday();
			break;
		case SUNDAY:
			bill += spclMap.containsKey(date) ? spclMap.get(date)
					: subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getSunday();
			break;
		}
		return bill;
	}

	public static double amountForDate(Subscription subRow, Product prod, LocalDate date) {
		double bill = 0.0;
		switch (date.getDayOfWeek()) {
		case MONDAY:
			bill += subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getMonday();
			break;
		case TUESDAY:
			bill += subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getTuesday();
			break;
		case WEDNESDAY:
			bill += subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getWednesday();
			break;
		case THURSDAY:
			bill += subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getThursday();
			break;
		case FRIDAY:
			bill += subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getFriday();
			break;
		case SATURDAY:
			bill += subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getSaturday();
			break;
		case SUNDAY:
			bill += subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getSunday();
			break;
		}
		return bill;
	}

	public static double calculateProdDiff(Subscription subRow, LocalDate startDate, LocalDate endDate) {
		double diff = 0.0;
		Product prod = productForSub(subRow.getProductId());
		ArrayList<ProductSpecialPrice> prodSpclPriceList = prodSpclPriceList(prod.getProductId(), startDate, endDate);
		HashMap<LocalDate, Double> spclMap = new HashMap<LocalDate, Double>();
		ArrayList<StopHistory> stopHistList = stopHistoryList(subRow.getSubscriptionId(), startDate, endDate);

		for (ProductSpecialPrice psp : prodSpclPriceList) {
			boolean b = true;
			for (StopHistory stp : stopHistList) {
				if ((stp.getStopDate().isBefore(psp.getFullDate()) || stp.getStopDate().isEqual(psp.getFullDate()))
						&& stp.getResumeDate() == null) {
					b = false;
				} else if ((stp.getStopDate().isBefore(psp.getFullDate())
						|| stp.getStopDate().isEqual(psp.getFullDate()))
						&& (stp.getResumeDate().isAfter(psp.getFullDate())
								|| stp.getResumeDate().isEqual(psp.getFullDate())))
					b = false;
			}
		}
		for (ProductSpecialPrice psp : prodSpclPriceList) {
			boolean b = true;
			for (StopHistory stp : stopHistList) {
				if ((stp.getStopDate().isBefore(psp.getFullDate()) || stp.getStopDate().isEqual(psp.getFullDate()))
						&& stp.getResumeDate() == null) {
					b = false;
				} else if ((stp.getStopDate().isBefore(psp.getFullDate())
						|| stp.getStopDate().isEqual(psp.getFullDate()))
						&& (stp.getResumeDate().isAfter(psp.getFullDate())
								|| stp.getResumeDate().isEqual(psp.getFullDate())))
					b = false;
			}
			if (b)
				spclMap.put(psp.getFullDate(), psp.getPrice());
		}
		if (subRow.getFrequency().equals("Daily")) {
			for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
				diff += diffForDateSpcl(subRow, prod, spclMap, date);

			}
		} else if (subRow.getFrequency().equals("Weekly")) {
			ArrayList<LocalDate> dateList = new ArrayList<LocalDate>();
			switch (subRow.getDow()) {
			case "Monday":
				dateList = weeksInPeriod(DayOfWeek.MONDAY, startDate, endDate);
				break;
			case "Tuesday":
				dateList = weeksInPeriod(DayOfWeek.TUESDAY, startDate, endDate);
				break;
			case "Wednesday":
				dateList = weeksInPeriod(DayOfWeek.WEDNESDAY, startDate, endDate);
				break;
			case "Thursday":
				dateList = weeksInPeriod(DayOfWeek.THURSDAY, startDate, endDate);
				break;
			case "Friday":
				dateList = weeksInPeriod(DayOfWeek.FRIDAY, startDate, endDate);
				break;
			case "Saturday":
				dateList = weeksInPeriod(DayOfWeek.SATURDAY, startDate, endDate);
				break;
			case "Sunday":
				dateList = weeksInPeriod(DayOfWeek.SUNDAY, startDate, endDate);
				break;
			}
			for (LocalDate date : dateList) {
				diff += diffForDateSpcl(subRow, prod, spclMap, date);

			}
		} else if (subRow.getFrequency().equals("14 Days")) {
			double first = Math.ceil(ChronoUnit.DAYS.between(prod.getFirstDeliveryDate(), startDate) / 14.0);
			LocalDate firstDate = prod.getFirstDeliveryDate().plusDays((int) (14 * first));
			LocalDate secondDate = !firstDate.plusDays(14).isAfter(endDate) ? firstDate.plusDays(14) : null;
			LocalDate thirdDate = secondDate == null ? null
					: !secondDate.plusDays(14).isAfter(endDate) ? secondDate.plusDays(14) : null;
			if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
				diff += diffForDateSpcl(subRow, prod, spclMap, firstDate);

			}
			if (secondDate != null) {
				if (!(secondDate.isBefore(startDate)) && (!secondDate.isAfter(endDate))) {
					diff += diffForDateSpcl(subRow, prod, spclMap, secondDate);

				}
			}
			if (thirdDate != null) {
				if (!(thirdDate.isBefore(startDate)) && (!thirdDate.isAfter(endDate))) {
					diff += diffForDateSpcl(subRow, prod, spclMap, thirdDate);

				}
			}
		} else if (subRow.getFrequency().equals("15 Days")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);
			LocalDate secondDate = firstDate.plusDays(15);
			if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
				diff += diffForDateSpcl(subRow, prod, spclMap, firstDate);

			}
			if (secondDate != null) {
				if (!(secondDate.isBefore(startDate)) && (!secondDate.isAfter(endDate))) {
					diff += diffForDateSpcl(subRow, prod, spclMap, secondDate);

				}
			}
		} else if (subRow.getFrequency().equals("Monthly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
				diff += diffForDateSpcl(subRow, prod, spclMap, firstDate);

			}
		} else if (subRow.getFrequency().equals("Quarterly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (months % 3 == 0) {
				if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
					diff += diffForDateSpcl(subRow, prod, spclMap, firstDate);

				}
			}
		} else if (subRow.getFrequency().equals("Half Yearly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (months % 6 == 0) {
				if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
					diff += diffForDateSpcl(subRow, prod, spclMap, firstDate);

				}
			}
		} else if (subRow.getFrequency().equals("Yearly")) {
			int months = Period.between(prod.getFirstDeliveryDate().withDayOfMonth(1), startDate).getMonths();
			LocalDate firstDate = prod.getFirstDeliveryDate().plusMonths(months);

			if (months % 12 == 0) {
				if (!(firstDate.isBefore(startDate)) && (!firstDate.isAfter(endDate))) {
					diff += diffForDateSpcl(subRow, prod, spclMap, firstDate);

				}
			}
		}

		return diff;

	}

	public static double diffForDateSpcl(Subscription subRow, Product prod, HashMap<LocalDate, Double> spclMap,
			LocalDate date) {
		double diff = 0.0;
		switch (date.getDayOfWeek()) {
		case MONDAY:
			diff += spclMap.containsKey(date) ? spclMap.get(date)
					- (subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getMonday()) : 0;
			break;
		case TUESDAY:
			diff += spclMap.containsKey(date) ? spclMap.get(date)
					- (subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getTuesday()) : 0;
			break;
		case WEDNESDAY:
			diff += spclMap.containsKey(date) ? spclMap.get(date)
					- (subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getWednesday()) : 0;
			break;
		case THURSDAY:
			diff += spclMap.containsKey(date) ? spclMap.get(date)
					- (subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getThursday()) : 0;
			break;
		case FRIDAY:
			diff += spclMap.containsKey(date) ? spclMap.get(date)
					- (subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getFriday()) : 0;
			break;
		case SATURDAY:
			diff += spclMap.containsKey(date) ? spclMap.get(date)
					- (subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getSaturday()) : 0;
			break;
		case SUNDAY:
			diff += spclMap.containsKey(date) ? spclMap.get(date)
					- (subRow.getProductType().equals("Magazine") ? prod.getPrice() : prod.getSunday()) : 0;
			break;
		}
		return diff;
	}

	public static void generateInvoicePDF(String hawkerCode, int lineNum, String invoiceDate) throws JRException {
//		Task<Void> task = new Task<Void>() {
//
//			@Override
//			protected Void call() throws Exception {
				try {
//					
//					Platform.runLater(new Runnable() {
//
//						@Override
//						public void run() {

							
//						}
//					});
					
					String reportSrcFile = "MasterInvoice.jrxml";
					String subReportPath = "BillSubreport.jrxml";
					
					InputStream input = BillingUtilityClass.class.getResourceAsStream(reportSrcFile);
					InputStream subreport = BillingUtilityClass.class.getResourceAsStream(subReportPath);
					// First, compile jrxml file.
//					JasperReport subReport = JasperCompileManager.compileReport(report);
					JasperReport jasperReport = JasperCompileManager.compileReport(input);
					JasperReport jasperSubReport = JasperCompileManager.compileReport(subreport);
//					JasperCompileManager.compileReportToFile(reportSrcFile);
					// Connection conn = ConnectionUtils.getConnection();

					// Parameters for report
					Map<String, Object> parameters = new HashMap<String, Object>();
					parameters.put("HWK_CODE", hawkerCode);
					parameters.put("LINE_NUM", lineNum);
					parameters.put("INVOICE_DATE", invoiceDate);
					parameters.put("SubReportParam", jasperSubReport);
					JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, Main.dbConnection);
//					JasperPrint print = JasperFillManager.fillReport("/application/MasterInvoice.jasper", parameters, Main.dbConnection);
					// Make sure the output directory exists.
					File outDir = new File("C:/pds");
					outDir.mkdirs();

					// PDF Exportor.
					JRPdfExporter exporter = new JRPdfExporter();

					ExporterInput exporterInput = new SimpleExporterInput(print);
					// ExporterInput
					exporter.setExporterInput(exporterInput);

					// ExporterOutput
					String filename = "C:/pds/" + hawkerCode + "-" + Integer.toString(lineNum) + "-"
							+ invoiceDate.replace('/', '-') + ".pdf";
					OutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(filename);
					// Output
					exporter.setExporterOutput(exporterOutput);

					//
					SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
					exporter.setConfiguration(configuration);
					exporter.exportReport();
//					Platform.runLater(new Runnable() {
//
//						@Override
//						public void run() {

							Notifications.create().title("Invocie PDF Created").text("Invoice PDF created at : " + filename)
									.hideAfter(Duration.seconds(15)).showInformation();
//						}
//					});
				} catch (JRException e) {
					Main._logger.debug("Error during Bill PDF Generation: ",e);
//					e.printStackTrace();
				}
//				return null;
//			}
//
//		};
//		new Thread(task).start();
	}

}

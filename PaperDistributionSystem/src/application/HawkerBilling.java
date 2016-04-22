package application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;

public class HawkerBilling {

	private final SimpleLongProperty hwkBillId = new SimpleLongProperty();
	private final SimpleLongProperty hawkerId = new SimpleLongProperty();
	private final SimpleObjectProperty<Date> startDate = new SimpleObjectProperty<>();
	private final SimpleObjectProperty<Date> endDate = new SimpleObjectProperty<>();
	private final SimpleDoubleProperty billAmount = new SimpleDoubleProperty();
	private final SimpleDoubleProperty paid = new SimpleDoubleProperty();
	private final SimpleDoubleProperty due = new SimpleDoubleProperty();

	public HawkerBilling(long hawkerId, Date startDate, Date endDate, double billAmount, double paid, double due,
			long hwkBillId) {
		// setHwkBillId(hwkBillId);
		setHawkerId(hawkerId);
		setStartDate(startDate);
		setEndDate(endDate);
		setBillAmount(billAmount);
		setPaid(paid);
		setDue(due);
		setHwkBillId(hwkBillId);
	}

	public Long getHwkBillId() {
		return hwkBillId.get();
	}

	public void setHwkBillId(long hwkBillId) {
		this.hwkBillId.set(hwkBillId);
	}

	public Long getHawkerId() {
		return hawkerId.get();
	}

	public void setHawkerId(long hawkerId) {
		this.hawkerId.set(hawkerId);
	}

	public Date getStartDate() {
		return startDate.get();
	}

	public void setStartDate(Date startDate) {
		this.startDate.set(startDate);
	}

	public Date getEndDate() {
		return endDate.get();
	}

	public void setEndDate(Date endDate) {
		this.endDate.set(endDate);
	}

	public double getBillAmount() {
		return this.billAmount.get();
	}

	public void setBillAmount(double billAmount) {
		this.billAmount.set(billAmount);
	}

	public double getPaid() {
		return this.paid.get();
	}

	public void setPaid(double paid) {
		this.paid.set(paid);
	}

	public double getDue() {
		return this.due.get();
	}

	public void setDue(double due) {
		this.due.set(due);
	}

	public void updateHawkerBillingRecord() {
		try {

			Connection con = Main.dbConnection;
			while (!con.isValid(0)) {
				con = Main.reconnect();
			}
			String updateString = "update hawker_billing set paid=? where hwk_bill_id=?";

			PreparedStatement updateStmt = con.prepareStatement(updateString);
			updateStmt.setDouble(1, getPaid());

			updateStmt.setLong(2, getHwkBillId());
			updateStmt.executeUpdate();
			con.commit();

			updateString = "update hawker_billing set due=bill_amount-paid where hwk_bill_id=?";
			updateStmt = con.prepareStatement(updateString);
			updateStmt.setDouble(1, getPaid());

			updateStmt.setLong(2, getHwkBillId());
			updateStmt.executeUpdate();
			con.commit();
			setDue(getBillAmount() - getPaid());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Main._logger.debug(e.getStackTrace());
			e.printStackTrace();
		} catch (Exception e) {

			Main._logger.debug(e.getStackTrace());
			e.printStackTrace();
		}
	}

}

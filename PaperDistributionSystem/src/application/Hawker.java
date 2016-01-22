package application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.controlsfx.control.Notifications;

import javafx.beans.property.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Hawker {
	
	private final SimpleLongProperty hawkerId = new SimpleLongProperty();
	private final SimpleStringProperty name = new SimpleStringProperty("");
	private final SimpleStringProperty hawkerCode = new SimpleStringProperty("");
	private final SimpleStringProperty mobileNum = new SimpleStringProperty("");
	private final SimpleStringProperty agencyName = new SimpleStringProperty("");
	private final SimpleBooleanProperty activeFlag = new SimpleBooleanProperty();
	private final SimpleDoubleProperty fee = new SimpleDoubleProperty();
	private final SimpleStringProperty oldHouseNum = new SimpleStringProperty("");
	private final SimpleStringProperty newHouseNum = new SimpleStringProperty("");
	private final SimpleStringProperty addrLine1 = new SimpleStringProperty("");
	private final SimpleStringProperty addrLine2 = new SimpleStringProperty("");
	private final SimpleStringProperty locality = new SimpleStringProperty("");
	private final SimpleStringProperty city = new SimpleStringProperty("");
	private final SimpleStringProperty state = new SimpleStringProperty("");
	private SimpleStringProperty customerAccess = new SimpleStringProperty("");
	private SimpleStringProperty billingAccess = new SimpleStringProperty("");
	private SimpleStringProperty lineInfoAccess = new SimpleStringProperty("");
	private SimpleStringProperty lineDistAccess = new SimpleStringProperty("");
	private SimpleStringProperty pausedCustAccess = new SimpleStringProperty("");
	private SimpleStringProperty productAccess = new SimpleStringProperty("");
	private SimpleStringProperty reportsAccess = new SimpleStringProperty("");
	private final SimpleDoubleProperty totalDue = new SimpleDoubleProperty();
	
	public Hawker(long hawkerId, String name, String hawkerCode,
			String moblieNum, String agencyName, boolean activeFlag,
			double fee, String oldHouseNum, String newHouseNum, String addrLine1, String addrLine2,
			String locality, String city, String state, String customerAccess, String billingAccess, 
			String lineInfoAccess, String lineDistAccess, String pausedCustAccess, String productAccess, String reportsAccess) {
		super();
		setHawkerId(hawkerId);
		setName(name);
		setHawkerCode(hawkerCode);
		setMobileNum(moblieNum);
		setAgencyName(agencyName);
		setActiveFlag(activeFlag);
		setFee(fee);
		setOldHouseNum(oldHouseNum);
		setNewHouseNum(newHouseNum);
		setAddrLine1(addrLine1);
		setAddrLine2(addrLine2);
		setLocality(locality);
		setCity(city);
		setState(state);
		setCustomerAccess(customerAccess);
		setBillingAccess(billingAccess);
		setLineInfoAccess(lineInfoAccess);
		setLineDistAccess(lineDistAccess);
		setPausedCustAccess(pausedCustAccess);
		setProductAccess(productAccess);
		setReportsAccess(reportsAccess);
	}

	public Hawker(Hawker hawkerRow) {
		// TODO Auto-generated constructor stub
		setHawkerId(hawkerRow.getHawkerId());
		setName(hawkerRow.getName());
		setMobileNum(hawkerRow.getMobileNum());
		setHawkerCode(hawkerRow.getHawkerCode());
		setOldHouseNum(hawkerRow.getOldHouseNum());
		setNewHouseNum(hawkerRow.getNewHouseNum());
		setAddrLine1(hawkerRow.getAddrLine1());
		setAddrLine2(hawkerRow.getAddrLine2());
		setLocality(hawkerRow.getLocality());
		setCity(hawkerRow.getCity());
		setState(hawkerRow.getState());
		setActiveFlag(hawkerRow.getActiveFlag());
		setFee(hawkerRow.getFee());
		setAgencyName(hawkerRow.getAgencyName());
		setCustomerAccess(hawkerRow.getCustomerAccess());
		setBillingAccess(hawkerRow.getBillingAccess());
		setLineInfoAccess(hawkerRow.getLineInfoAccess());
		setLineDistAccess(hawkerRow.getLineDistAccess());
		setPausedCustAccess(hawkerRow.getPausedCustAccess());
		setProductAccess(hawkerRow.getProductAccess());
		setReportsAccess(hawkerRow.getReportsAccess());
	}

	public Long getHawkerId() {
		return hawkerId.get();
	}
	

	public String getName() {
		return name.get();
	}


	public String getHawkerCode() {
		return hawkerCode.get();
	}


	public String getMobileNum() {
		return mobileNum.get();
	}


	public String getAgencyName() {
		return agencyName.get();
	}


	public boolean getActiveFlag() {
		return activeFlag.get();
	}


	public double getFee() {
		return fee.get();
	}

	
	public String getOldHouseNum() {
		return oldHouseNum.get();
	}
	
	
	public String getNewHouseNum() {
		return newHouseNum.get();
	}

	public String getAddrLine1() {
		return addrLine1.get();
	}


	public String getAddrLine2() {
		return addrLine2.get();
	}
	
	public String getLocality() {
		return locality.get();
	}

	public String getCity() {
		return city.get();
	}


	public String getState() {
		return state.get();
	}



	public void setHawkerId(long hawkerId) {
		this.hawkerId.set(hawkerId);
	}
	public void setName(String name) {
		this.name.set(name);
	}
	public void setHawkerCode(String hawkerCode) {
		this.hawkerCode.set(hawkerCode);
	}
	public void setMobileNum(String mobileNum) {
		this.mobileNum.set(mobileNum);
	}
	public void setAgencyName(String agencyName) {
		this.agencyName.set(agencyName);
	}
	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag.set(activeFlag);
	}
	public void setFee(double fee) {
		this.fee.set(fee);
	}
	public void setOldHouseNum(String oldHouseNum) {
		this.oldHouseNum.set(oldHouseNum);
	}
	public void setNewHouseNum(String newHouseNum) {
		this.newHouseNum.set(newHouseNum);
	}

	public void setAddrLine1(String addrLine1) {
		this.addrLine1.set(addrLine1);
	}
	public void setAddrLine2(String addrLine2) {
		this.addrLine2.set(addrLine2);
	}
	
	public void setLocality(String locality) {
		this.locality.set(locality);
	}

	public void setCity(String city) {
		this.city.set(city);
	}
	public void setState(String state) {
		this.state.set(state);
	}
	
	public String getCustomerAccess() {
		return customerAccess.get();		
	}

	public void setCustomerAccess(String customerAccess) {
		this.customerAccess.set(customerAccess);
	}

	public String getBillingAccess() {
		return billingAccess.get();
	}

	public void setBillingAccess(String billingAccess) {
		this.billingAccess.set(billingAccess);
	}

	public String getLineInfoAccess() {
		return lineInfoAccess.get();
	}

	public void setLineInfoAccess(String lineInfoAccess) {
		this.lineInfoAccess.set(lineInfoAccess);
	}

	public String getLineDistAccess() {
		return lineDistAccess.get();
	}

	public void setLineDistAccess(String lineDistAccess) {
		this.lineDistAccess.set(lineDistAccess);
	}

	public String getPausedCustAccess() {
		return pausedCustAccess.get();
	}

	public void setPausedCustAccess(String pausedCustAccess) {
		this.pausedCustAccess.set(pausedCustAccess);
	}

	public String getProductAccess() {
		return productAccess.get();
	}

	public void setProductAccess(String productAccess) {
		this.productAccess.set(productAccess);
	}

	public String getReportsAccess() {
		return reportsAccess.get();
	}

	public void setReportsAccess(String reportsAccess) {
		this.reportsAccess.set(reportsAccess);
	}
	
	public double getTotalDue() {
		return totalDue.get();
	}

	public void setTotalDue(double totalDue) {
		this.totalDue.set(totalDue);
	}

	public String toString(){
		return getHawkerCode();
	}
	
	public void calculateTotalDue(){
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			String sqlString = "select sum(due) from HAWKER_BILLING where HAWKER_ID=?";
			PreparedStatement stmt = con.prepareStatement(sqlString);
			stmt.setLong(1, getHawkerId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				setTotalDue(rs.getDouble(1));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateHawkerRecord(){
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			String updateString = "update hawker_info set name=?, hawker_code=?,  mobile_num=?,  agency_name=?,  active_flag=?,  fee=?,  old_house_num=?,  new_house_num=?,  addr_line1=?,  addr_line2=?,  locality=?,  city=?,  state=?, customer_access=?,  billing_access=?,  line_info_access=?,  line_dist_access=?,  paused_cust_access=?,  product_access=?,  reports_access=?, total_Due=? where hawker_id = ?";
			PreparedStatement updateStmt = con.prepareStatement(updateString);
			updateStmt.setString(1, getName());
			updateStmt.setString(2, getHawkerCode());
			updateStmt.setString(3, getMobileNum());
			updateStmt.setString(4, getAgencyName());
			updateStmt.setString(5, getActiveFlag()?"Y":"N");
			updateStmt.setDouble(6, getFee());
			updateStmt.setString(7, getOldHouseNum());
			updateStmt.setString(8, getNewHouseNum());
			updateStmt.setString(9, getAddrLine1());
			updateStmt.setString(10, getAddrLine2());
			updateStmt.setString(11, getLocality());
			updateStmt.setString(12, getCity());
			updateStmt.setString(13, getState());
			updateStmt.setString(14, getCustomerAccess());
			updateStmt.setString(15, getBillingAccess());
			updateStmt.setString(16, getLineInfoAccess());
			updateStmt.setString(17, getLineDistAccess());
			updateStmt.setString(18, getPausedCustAccess());
			updateStmt.setString(19, getProductAccess());
			updateStmt.setString(20, getReportsAccess());
			updateStmt.setLong(22, getHawkerId()); 
			updateStmt.setDouble(21, getTotalDue());
			updateStmt.executeUpdate();
			con.commit();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notifications.create().title("Failed").text("Hawker update failed").showError();
		}
	}

}

package application;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.controlsfx.control.Notifications;

import javafx.beans.property.*;

public class LineDistributor {
	private final SimpleLongProperty lineDistId = new SimpleLongProperty();
	private final SimpleStringProperty name = new SimpleStringProperty();
	private final SimpleStringProperty mobileNum = new SimpleStringProperty();
	private final SimpleLongProperty hawkerId = new SimpleLongProperty();
	private final SimpleIntegerProperty lineNum = new SimpleIntegerProperty();
	
	
	
	public LineDistributor(long lineDistId, String name, String mobileNum, long hawkerId, int lineNum){
		setLineDistId(lineDistId);
		setName(name);
		setMobileNum(mobileNum);
		setHawkerId(hawkerId);
		setLineNum(lineNum);
	}
	
	public Long getLineDistId() {
		return lineDistId.get();
	}
	
	public void setLineDistId(long lineDistId) {
		this.lineDistId.set(lineDistId);
	}
	
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public String getMobileNum() {
		return mobileNum.get();
	}

	public void setMobileNum(String mobileNum) {
		this.mobileNum.set(mobileNum);
	}
	
	public Long getHawkerId() {
		return hawkerId.get();
	}
	
	public void setHawkerId(long hawkerId) {
		this.hawkerId.set(hawkerId);
	}
	
	public int getLineNum() {
		return lineNum.get();
	}
	
	public void setLineNum(int lineNum) {
		this.lineNum.set(lineNum);
	}
	
	public void updateLineDistRecord(){
		try {
			
			Connection con = Main.dbConnection;
			while(!con.isValid(0)){
				con = Main.reconnect();
			}
			String updateString = "update line_distributor set name=?, mobile_num=?, line_num=? where line_dist_id=?";
			PreparedStatement updateStmt = con.prepareStatement(updateString);
			updateStmt.setString(1, getName());
			updateStmt.setString(2, getMobileNum());
			updateStmt.setInt(3, getLineNum());
			updateStmt.setLong(4, getLineDistId());
			updateStmt.executeUpdate();
			con.commit();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Notifications.create().title("Failed").text("Line distributor update failed").showError();
		}
	}


}

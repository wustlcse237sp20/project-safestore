package tables;

import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "DebitCards")
public class DebitCard extends BaseDaoEnabled<DebitCard, String>{
	
	@DatabaseField(columnName = "user_username", canBeNull = false, foreign = true, foreignAutoRefresh = true)
	private User safeStoreUser;
	
	@DatabaseField(columnName = "nickname")
	private String nickname;
	
	// reason for String is that a card number is usally in the form:
	//	#### #### #### #### which is easiest to store as a string in 
	// 	a database for our uses
	@DatabaseField(columnName = "number", id = true)
	private String number;
	
	@DatabaseField(columnName = "expiration_date", canBeNull = false, dataType = DataType.DATE_STRING)
	private Date expirationDate;
	
	@DatabaseField(columnName = "cvv", canBeNull = false)
	private int cvv;
	
	@DatabaseField(columnName = "pin", canBeNull = false)
	private int pin;
	
	@DatabaseField(columnName = "address_id", canBeNull = false, foreign = true, foreignAutoRefresh = true)
	private Address billingAddress;
	
	// ORMLite needs a no-argument constructor
	public DebitCard() {}
	
	public DebitCard(User safeStoreUser, String nickname, String number,
			Date expirationDate, int cvv, int pin, Address billingAddress) {
		this.safeStoreUser = safeStoreUser;
		this.nickname = nickname;
		this.number = number;
		this.expirationDate = expirationDate;
		this.cvv = cvv;
		this.billingAddress = billingAddress;
		this.pin = pin;
	}
	
	public User getSafeStoreUser() {
		return safeStoreUser;
	}
	
	public void setSafeStoreUser(User safeStoreUser) {
		this.safeStoreUser = safeStoreUser;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public int getCvv() {
		return cvv;
	}
	
	public void setCvv(int cvv) {
		this.cvv = cvv;
	}
	
	public int getPin() {
		return pin;
	}
	
	public void setPin(int pin) {
		this.pin = pin;
	}
	
	public Address getBillingAddress() {
		return billingAddress;
	}
	
	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}
	
}

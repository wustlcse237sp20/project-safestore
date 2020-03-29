package tables;

import java.util.Date; 

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "CreditCards")
public class CreditCard extends BaseDaoEnabled<CreditCard, String> {
	
	// reason for String is that a card number is usally in the form:
	//	#### #### #### #### which is easiest to store as a string in 
	// 	a database for our uses
	@DatabaseField(columnName = "credit_card_number", id = true, canBeNull = false)
	private String creditCardNumber;
	
	@DatabaseField(columnName = "safe_store_username", canBeNull = false, 
			foreign = true, foreignColumnName = "username", foreignAutoRefresh = true,
			foreignAutoCreate = true)
	private User safeStoreUser;
	
	@DatabaseField(columnName = "nickname")
	private String nickname;
	
	//, dataType = DataType.DATE_STRING
	@DatabaseField(columnName = "expiration_date", canBeNull = false)
	private String expirationDate;
	
	@DatabaseField(columnName = "cvv", canBeNull = false)
	private String cvv;
	
	@DatabaseField(columnName = "address_id", canBeNull = false, 
			foreign = true, foreignColumnName = "id", foreignAutoRefresh = true,
			foreignAutoCreate = true)
	private Address billingAddress;
	
	// ORMLite needs a no-argument constructor
	public CreditCard() {}
	
	public CreditCard(User safeStoreUser, String nickname,
			String creditCardNumber, String expirationDate, String cvv, Address billingAddress) {
		this.safeStoreUser = safeStoreUser;
		this.nickname = nickname;
		this.creditCardNumber = creditCardNumber;
		this.expirationDate = expirationDate;
		this.cvv = cvv;
		this.billingAddress = billingAddress;
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
	
	public String getCreditCardNumber() {
		return creditCardNumber;
	}
	
	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}
	
	public String getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public String getCvv() {
		return cvv;
	}
	
	public void setCvv(String cvv) {
		this.cvv = cvv;
	}
	
	public Address getBillingAddress() {
		return billingAddress;
	}
	
	public void setBillingAddress(Address billingAddress) {
		this.billingAddress = billingAddress;
	}
	
	
}

package tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "CreditCards")
public class CreditCardEntity extends BaseDaoEnabled<CreditCardEntity, String> {
	
	// reason for String is that a card number is usally in the form:
	//	#### #### #### #### which is easiest to store as a string in 
	// 	a database for our uses
	@DatabaseField(columnName = "credit_card_number", id = true, canBeNull = false)
	private String creditCardNumber;
	
	@DatabaseField(columnName = "safe_store_username", canBeNull = false, 
			foreign = true, foreignColumnName = "username", foreignAutoRefresh = true,
			foreignAutoCreate = true)
	private UserEntity safeStoreUser;
	
	@DatabaseField(columnName = "nickname")
	private String nickname;
	
	@DatabaseField(columnName = "expiration_date", canBeNull = false)
	private String expirationDate;
	
	@DatabaseField(columnName = "cvv", canBeNull = false)
	private String cvv;
	
	@DatabaseField(columnName = "address_id", canBeNull = false, 
			foreign = true, foreignColumnName = "id", foreignAutoRefresh = true,
			foreignAutoCreate = true)
	private AddressEntity billingAddress;
	
	// ORMLite needs a no-argument constructor
	public CreditCardEntity() {}
	
	public CreditCardEntity(UserEntity safeStoreUser, String nickname,
			String creditCardNumber, String expirationDate, String cvv, AddressEntity billingAddress) {
		this.safeStoreUser = safeStoreUser;
		this.nickname = nickname;
		this.creditCardNumber = creditCardNumber;
		this.expirationDate = expirationDate;
		this.cvv = cvv;
		this.billingAddress = billingAddress;
	}
	
	public UserEntity getSafeStoreUser() {
		return safeStoreUser;
	}
	
	public void setSafeStoreUser(UserEntity safeStoreUser) {
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
	
	public AddressEntity getBillingAddress() {
		return billingAddress;
	}
	
	public void setBillingAddress(AddressEntity billingAddress) {
		this.billingAddress = billingAddress;
	}
	
	
}

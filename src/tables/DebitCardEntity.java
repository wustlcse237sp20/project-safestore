package tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "DebitCards")
public class DebitCardEntity extends BaseDaoEnabled<DebitCardEntity, String> {
	
	@DatabaseField(columnName = "safe_store_username", canBeNull = false, 
			foreign = true, foreignColumnName = "username", foreignAutoCreate = true)
	private UserEntity safeStoreUser;
	
	@DatabaseField(columnName = "nickname")
	private String nickname;
	
	// card number stored in String as card numbers are usually in the form:
	//	#### #### #### #### 
	@DatabaseField(columnName = "debit_card_number", id = true, canBeNull = false)
	private String debitCardNumber;
	
	@DatabaseField(columnName = "expiration_date", canBeNull = false)
	private String expirationDate;
	
	@DatabaseField(columnName = "cvv", canBeNull = false)
	private String cvv;
	
	@DatabaseField(columnName = "pin", canBeNull = false)
	private String pin;
	
	@DatabaseField(columnName = "address_id", canBeNull = false, 
			foreign = true, foreignAutoRefresh = true, 
			foreignAutoCreate = true, foreignColumnName = "id")
	private AddressEntity billingAddress;
	
	// ORMLite needs a no-argument constructor
	public DebitCardEntity() {}
	
	public DebitCardEntity(UserEntity safeStoreUser, String nickname, String debitCardNumber,
			String expirationDate, String cvv, String pin, AddressEntity billingAddress) {
		this.safeStoreUser = safeStoreUser;
		this.nickname = nickname;
		this.debitCardNumber = debitCardNumber;
		this.expirationDate = expirationDate;
		this.cvv = cvv;
		this.billingAddress = billingAddress;
		this.pin = pin;
	}
	
	public UserEntity getSafeStoreUser() {
		return safeStoreUser;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public String getDebitCardNumber() {
		return debitCardNumber;
	}
	
	public String getExpirationDate() {
		return expirationDate;
	}
	
	public String getCvv() {
		return cvv;
	}
	
	public String getPin() {
		return pin;
	}
	
	public AddressEntity getBillingAddress() {
		return billingAddress;
	}
	
	public void setSafeStoreUser(UserEntity safeStoreUser) {
		this.safeStoreUser = safeStoreUser;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	} 
	
	public void setDebitCardNumber(String debitCardNumber) {
		this.debitCardNumber = debitCardNumber;
	}
	
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public void setCvv(String cvv) {
		this.cvv = cvv;
	}
	
	public void setPin(String pin) {
		this.pin = pin;
	}
	
	public void setBillingAddress(AddressEntity billingAddress) {
		this.billingAddress = billingAddress;
	}
	
}

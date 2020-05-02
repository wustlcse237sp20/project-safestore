package tables;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Users")
public class UserEntity extends BaseDaoEnabled<UserEntity, String> {
	
	@DatabaseField(columnName = "username", id = true, canBeNull = false)
	private String username;
	 
	@DatabaseField(columnName = "password_hashed", canBeNull = false)
	private String passwordHashed;
	
	@DatabaseField(columnName = "salt", canBeNull = false, dataType=DataType.BYTE_ARRAY)
	private byte[] salt;
	
	// these three are used to store the accounts and cards for the user
	// 	they are NOT database columns
	@ForeignCollectionField(eager = true, orderColumnName = "nickname")
	private ForeignCollection<WebsiteAccountEntity> websiteAccounts;
	
	@ForeignCollectionField(eager = true, orderColumnName = "nickname")
	private ForeignCollection<CreditCardEntity> creditCards;
	
	@ForeignCollectionField(eager = true, orderColumnName = "nickname")
	private ForeignCollection<DebitCardEntity> debitCards;
	
	// ORMLite needs a no-argument constructor
	public UserEntity() {}
	
	
	public UserEntity(String username, String password, byte[] salt) {
		this.username = username;
		this.passwordHashed = password;
		this.salt = salt;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassordHashed() {
		return passwordHashed;
	}
	
	public byte[] getSalt() {
		return salt;
	}
	
	// only getters for ForeignCollections because the queries will automatically set them
	public ForeignCollection<WebsiteAccountEntity> getWebsiteAccounts() {
		return websiteAccounts;
	}
	
	public ForeignCollection<CreditCardEntity> getCreditCards() {
		return creditCards;
	}
	
	public ForeignCollection<DebitCardEntity> getDebitCards() {
		return debitCards;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPasswordHashed(String passwordHashed) {
		this.passwordHashed = passwordHashed;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

}

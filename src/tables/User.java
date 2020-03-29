package tables;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Users")
public class User extends BaseDaoEnabled<User, String> {
	
	@DatabaseField(columnName = "username", id = true, canBeNull = false)
	private String username;
	 
	@DatabaseField(columnName = "password_hashed", canBeNull = false)
	private String passwordHashed;
	
	@DatabaseField(columnName = "salt", canBeNull = false)
	private String salt;
	
	// these three are used to store the accounts and cards for the user
	// 	they are NOT database columns, they are just used to store 
	//	the objects to make life easier
	@ForeignCollectionField(eager = true, orderColumnName = "nickname")
	private ForeignCollection<WebsiteAccount> websiteAccounts;
	
	@ForeignCollectionField(eager = true, orderColumnName = "nickname")
	private ForeignCollection<CreditCard> creditCards;
	
	@ForeignCollectionField(eager = true, orderColumnName = "nickname")
	private ForeignCollection<DebitCard> debitCards;
	
	// ORMLite needs a no-argument constructor
	public User() {}
	
	public User(String username, String password, String salt) {
		this.username = username;
		this.passwordHashed = password;
		this.salt = salt;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassordHashed() {
		return passwordHashed;
	}
	
	public void setPasswordHashed(String passwordHashed) {
		this.passwordHashed = passwordHashed;
	}
	
	public String getSalt() {
		return salt;
	}
	
	public void setSalt(String salt) {
		this.salt = salt;
	}
	
	// no setters here because the queries will do that automatically
	public ForeignCollection<WebsiteAccount> getWebsiteAccounts() {
		return websiteAccounts;
	}
	
	public ForeignCollection<CreditCard> getCreditCards() {
		return creditCards;
	}
	
	public ForeignCollection<DebitCard> getDebitCards() {
		return debitCards;
	}

}

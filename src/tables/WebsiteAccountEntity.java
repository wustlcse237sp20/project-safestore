package tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

import encryption.Encryption;

@DatabaseTable(tableName = "WebsiteAccounts")
public class WebsiteAccountEntity extends BaseDaoEnabled<WebsiteAccountEntity, Integer> {

	@DatabaseField(columnName = "id", canBeNull = false, generatedId = true)
	private int id;
	  
	@DatabaseField(columnName = "safe_store_username", canBeNull = false, 
			foreign = true, foreignColumnName = "username", foreignAutoRefresh = true,
			foreignAutoCreate = true)
	private UserEntity safeStoreUser;
	
	@DatabaseField(columnName = "nickname", canBeNull = false)
	private String nickname;
	
	@DatabaseField(columnName = "website_login", canBeNull = false)
	private String websiteLogin;
	
	@DatabaseField(columnName = "website_password", canBeNull = false)
	private String websitePassword;
	
	// ORMLite needs a no-argument constructor
	public WebsiteAccountEntity() {}
	
	public WebsiteAccountEntity(UserEntity safeStoreUser, String nickname, 
			String websiteLogin, String websitePassword) {
		this.safeStoreUser = safeStoreUser;
		this.nickname = nickname;
		this.websiteLogin = websiteLogin;
		this.websitePassword = websitePassword;
	}
	
	public String toString() {
		String decryptedNickname = Encryption.decrypt(this.nickname);
		String decryptedLogin = Encryption.decrypt(this.websiteLogin);
		String decryptedPassword = Encryption.decrypt(this.websitePassword);
		return decryptedNickname + " - Login: " + decryptedLogin + ", Password: " + decryptedPassword;
	}
	
	// no setter for id because it is auto-generated and shouldn't be changed once made
	public int getId() {
		return id;
	}
	
	public UserEntity getSafeStoreUser() {
		return safeStoreUser;
	}
	
	public String getNickname() {
		return nickname;
	}
	
	public String getWebsiteLogin() {
		return websiteLogin;
	}
	
	public String getWebsitePassword() {
		return websitePassword;
	}
	
	public void setSafeStoreUser(UserEntity safeStoreUser) {
		this.safeStoreUser = safeStoreUser;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public void setWebsiteLogin(String websiteLogin) {
		this.websiteLogin = websiteLogin;
	}

	public void setWebsitePassword(String websitePassword) {
		this.websitePassword = websitePassword;
	}
}

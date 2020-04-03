package tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

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
	
	public WebsiteAccountEntity(UserEntity safeStoreUser, String nickname, String websiteLogin, String websitePassword) {
		this.safeStoreUser = safeStoreUser;
		this.nickname = nickname;
		this.websiteLogin = websiteLogin;
		this.websitePassword = websitePassword;
	}
	
	public String toString() {
		return this.nickname + " " + this.websiteLogin + " " + this.websitePassword;
	}
	
	public int getId() {
		return id;
	}
	
	// didn't make an setter for id because it is unique, auto generated, and shouldn't 
	//	be adjusted once created and put in the database
	
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
	
	public String getWebsiteLogin() {
		return websiteLogin;
	}
	
	public void setWebsiteLogin(String websiteLogin) {
		this.websiteLogin = websiteLogin;
	}
	
	public String getWebsitePassword() {
		return websitePassword;
	}
	
	public void setWebsitePassword(String websitePassword) {
		this.websitePassword = websitePassword;
	}
	
	
}

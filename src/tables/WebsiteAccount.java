package tables;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "WebsiteAccounts")
public class WebsiteAccount extends BaseDaoEnabled<WebsiteAccount, Integer> {

	@DatabaseField(columnName = "id", generatedId = true)
	private int id;
	
	@DatabaseField(columnName = "user_username", canBeNull = false, foreign = true, foreignAutoRefresh = true)
	private User safeStoreUser;
	
	@DatabaseField(columnName = "nickname", canBeNull = false)
	private String nickname;
	
	@DatabaseField(columnName = "login", canBeNull = false)
	private String login;
	
	@DatabaseField(columnName = "password", canBeNull = false)
	private String password;
	
	// ORMLite needs a no-argument constructor
	public WebsiteAccount() {}
	
	public WebsiteAccount(User safeStoreUser, String nickname, String login, String password) {
		this.safeStoreUser = safeStoreUser;
		this.nickname = nickname;
		this.login = login;
		this.password = password;
	}
	
	public int getId() {
		return id;
	}
	
	// didn't make an setter for id because it is unique, auto generated, and shouldn't 
	//	be adjusted once created and put in the database
	
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
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}

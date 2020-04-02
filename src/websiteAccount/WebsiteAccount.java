package websiteAccount;

import java.sql.SQLException;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.WebsiteAccountEntity;
import user.User;

public class WebsiteAccount {
	
	private WebsiteAccountEntity websiteAccountEntity;
	
	public WebsiteAccount(User safeStoreUser, String nickname, String websiteLogin, String websitePassword) {
		String encryptedNickname = Encryption.encrypt(nickname);
		String encryptedLogin = Encryption.encrypt(websiteLogin);
		String encryptedPassword = Encryption.encrypt(websitePassword);
		websiteAccountEntity = new WebsiteAccountEntity(
				safeStoreUser.getUserEntity(), encryptedNickname, encryptedLogin, encryptedPassword);
	}
	
	public WebsiteAccount(WebsiteAccountEntity websiteAccountEntity) {
		this.websiteAccountEntity = websiteAccountEntity;
	}
	
	public int getId() {
		return this.websiteAccountEntity.getId();
	}
	
	public String getNickname() {
		return Encryption.decrypt(this.websiteAccountEntity.getNickname());
	}
	
	public void setNickname(String nickname) {
		this.websiteAccountEntity.setNickname(Encryption.encrypt(nickname));
	}
	
	public String getWebsiteLogin() {
		return Encryption.decrypt(this.websiteAccountEntity.getWebsiteLogin());
	}
	
	public void setWebsiteLogin(String websiteLogin) {
		this.websiteAccountEntity.setNickname(Encryption.encrypt(websiteLogin));
	}
	
	public String getWebsitePassword() {
		return Encryption.decrypt(this.websiteAccountEntity.getWebsitePassword());
	}
	
	public void setWebsitePassword(String websitePassword) {
		this.websiteAccountEntity.setNickname(Encryption.encrypt(websitePassword));
	}
	
	/**
	 * Adds the WebsiteAccount to the database
	 * @param databaseConnection - the ConnectionSource object to the database where the 
	 * 								the web account will be stored
	 * @throws SQLException
	 */
	public boolean addWebsiteAccount(ConnectionSource databaseConnection) {
		try {
			Dao<WebsiteAccountEntity, Integer> websiteAccountDao = 
					DaoManager.createDao(databaseConnection, WebsiteAccountEntity.class);
			websiteAccountDao.create(websiteAccountEntity);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * Prompts the user to add info for a website account 
	 * @param databaseConnection - the ConnectionSource object to the database where the 
	 * 								the web account will be stored
	 * @param keyboard - the input stream for entering in info which will be the keyboard
	 */
	public static boolean addWebsiteAccountPrompts(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		// Setting up website account variables
		String nickname = "";
		String login = "";
		String password = "";
		
		System.out.println("Please provide a name for this account: ");
		nickname = keyboard.nextLine();
		System.out.println("Please provide the login (username or email adddress) for this account: ");
		login = keyboard.nextLine();
		System.out.println("Please provide the password for this account: ");
		password = keyboard.nextLine();
		
		WebsiteAccount websiteAccount = new WebsiteAccount(safeStoreUser, nickname, login, password);
		return websiteAccount.addWebsiteAccount(databaseConnection);
	}
	
}

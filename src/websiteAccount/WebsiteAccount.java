package websiteAccount;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.UserEntity;
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
			
			QueryBuilder<WebsiteAccountEntity, Integer> queryBuilder = websiteAccountDao.queryBuilder();
			Where<WebsiteAccountEntity, Integer> where = queryBuilder.where();
			where.eq("nickname", Encryption.encrypt(this.getNickname()));
			where.and();
			where.eq("safe_store_username", this.websiteAccountEntity.getSafeStoreUser().getUsername());
			PreparedQuery<WebsiteAccountEntity> preparedQuery = queryBuilder.prepare();
			List<WebsiteAccountEntity> accountsWithMatchingNicknames = websiteAccountDao.query(preparedQuery);
			
			// there is an account with this existing nickname
			if (accountsWithMatchingNicknames.size() != 0) {
				return false;
			}
			else {
				websiteAccountDao.create(websiteAccountEntity);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}	
	
	/**
	 * Prompts the user to add info for a website account 
	 * @param databaseConnection - the ConnectionSource object to the database where the 
	 * 								the web account will be stored
	 * @param keyboard - the input stream for entering in info which will be the keyboard
	 */
	public static boolean addWebsiteAccountPrompts(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		System.out.println("Please provide a name for this account: ");
		String nickname = keyboard.nextLine().trim();
		while (nickname.isEmpty()) {
			System.out.println("Name cannot be blank, please try again: ");
			nickname = keyboard.nextLine().trim();
		}
		System.out.println("Please provide the login (username or email adddress) for this account: ");
		String login = keyboard.nextLine().trim();
		while (login.isEmpty()) {
			System.out.println("Login cannot be blank, please try again: ");
			login = keyboard.nextLine().trim();
		}
		System.out.println("Please provide the password for this account: ");
		String password = keyboard.nextLine().trim();
		while (password.isEmpty()) {
			System.out.println("Password cannot be blank, please try again: ");
			password = keyboard.nextLine().trim();
		}
		
		WebsiteAccount websiteAccount = new WebsiteAccount(safeStoreUser, nickname, login, password);
		return websiteAccount.addWebsiteAccount(databaseConnection);
	}
	
	public static String viewWebsiteAccountLogin(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		try {
			// get user object from db to view accounts
			Dao<UserEntity, String> userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			UserEntity returnedFromDb = userDao.queryForSameId(safeStoreUser.getUserEntity());
			ForeignCollection<WebsiteAccountEntity> websiteAccounts = returnedFromDb.getWebsiteAccounts();
			
			// print out all the accounts for the user
			if (websiteAccounts.isEmpty()) {
				System.out.println("No accounts");
				return "No accounts";
			}
			for (WebsiteAccountEntity account : websiteAccounts) {
				System.out.println(Encryption.decrypt(account.getNickname()));
			}
			
			// get the user to type in which one they want to view, and keep asking if the account
			//	doesn't exist of they type in a blank string
			String login = "";
			System.out.println("Type the account that you want to see the username for:");
			while (login == "") {
				String nickname = keyboard.nextLine().trim();
				for (WebsiteAccountEntity account : websiteAccounts) {
					if (nickname.equals(Encryption.decrypt(account.getNickname()))) {
						login = Encryption.decrypt(account.getWebsiteLogin());
						System.out.println("Login: " + Encryption.decrypt(account.getWebsiteLogin()));
					}
				}
				// if the account they typed in doesn't exist, login will never be updated and continue
				//	to be an empty string, so this writes a little prompt telling the user to enter a 
				//	re enter the account
				if (login == "") {
					System.out.println("Invalid account name. Type the name exactly how it printed above.");
				}
			}
			return login;
		} catch (SQLException e) {
			e.printStackTrace();
			return "SQL Error Occured";
		}
	}
	
}

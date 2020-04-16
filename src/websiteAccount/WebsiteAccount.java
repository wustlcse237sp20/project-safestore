package websiteAccount;

import java.sql.SQLException;
import java.util.Arrays;
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

	public boolean setNickname(String nickname) {
		this.websiteAccountEntity.setNickname(Encryption.encrypt(nickname));
		try {
			int successfulUpdate = this.websiteAccountEntity.update();
			return successfulUpdate > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getWebsiteLogin() {
		return Encryption.decrypt(this.websiteAccountEntity.getWebsiteLogin());
	}

	public boolean setWebsiteLogin(String websiteLogin) {
		this.websiteAccountEntity.setWebsiteLogin(Encryption.encrypt(websiteLogin));
		try {
			int successfulUpdate = this.websiteAccountEntity.update();
			return successfulUpdate > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public String getWebsitePassword() {
		return Encryption.decrypt(this.websiteAccountEntity.getWebsitePassword());
	}

	public boolean setWebsitePassword(String websitePassword) {
		this.websiteAccountEntity.setWebsitePassword(Encryption.encrypt(websitePassword));
		try {
			int successfulUpdate = this.websiteAccountEntity.update();
			return successfulUpdate > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public WebsiteAccountEntity getWebsiteAccountEntity() {
		return this.websiteAccountEntity;
	}

	public String toString() {
		return this.getNickname() + " - Login: " + this.getWebsiteLogin() + ", Password: " + this.getWebsitePassword();
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

	/**
	 * gets the foreign collection of all website account rows for that user
	 * @param databaseConnection
	 * @param safeStoreUser of type User
	 * @return a ForeignCollection<WebsiteAccountEntity> that holds all the db rows of accounts
	 * 			for the safeStoreUser
	 */
	public static ForeignCollection<WebsiteAccountEntity> getAllWebsiteAccounts(ConnectionSource databaseConnection, User safeStoreUser) {
		Dao<UserEntity, String> userDao;
		try {
			userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			UserEntity userEntity = userDao.queryForSameId(safeStoreUser.getUserEntity());
			return userEntity.getWebsiteAccounts();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Prints a list of all website accounts for the User
	 * @param databaseConnection
	 * @param safeStoreUser of type User
	 */
	public static void printAllWebsiteAccounts(ConnectionSource databaseConnection, User safeStoreUser) {
		ForeignCollection<WebsiteAccountEntity> websiteAccounts = getAllWebsiteAccounts(databaseConnection, safeStoreUser);
		if (websiteAccounts.isEmpty() || websiteAccounts == null) {
			System.out.println("No accounts");
		}
		for (WebsiteAccountEntity account : websiteAccounts) {
			System.out.println(Encryption.decrypt(account.getNickname()));
		}
	}

	/**
	 * Prompts the user to pick the website account they want to view the login and password info for and prints it out
	 * @param databaseConnection
	 * @param keyboard
	 * @param safeStoreUser
	 * @return - what was printed for that specific website account...mainly used for testing
	 */
	public static String viewWebsiteAccountInfo(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		printAllWebsiteAccounts(databaseConnection, safeStoreUser);
		ForeignCollection<WebsiteAccountEntity> websiteAccounts = getAllWebsiteAccounts(databaseConnection, safeStoreUser);

		String returnAccountInfo = "No accounts";
		if (websiteAccounts.isEmpty()) {
			return returnAccountInfo;
		}

		Boolean accountExists = false;
		System.out.println("Type the account that you want to see the login info for:");
		while (!accountExists) {
			String nickname = keyboard.nextLine().trim();
			for (WebsiteAccountEntity account : websiteAccounts) {
				if (nickname.equals(Encryption.decrypt(account.getNickname()))) {
					accountExists = true;
					returnAccountInfo = account.toString();
					break;
				}
			}
			if (!accountExists) {
				System.out.println("Invalid account name. Type the name exactly how it is printed above.");
			}
		}
		System.out.println(returnAccountInfo);
		return returnAccountInfo;
	}

	/**
	 * Gets a WebsiteAccount based on a User and a nickname. If it doesn't exist, returns an 
	 * exception
	 * @param databaseConnection
	 * @param nickname
	 * @param safeStoreUser
	 * @return - the WebsiteAccount associated with the nickname and user inputted
	 * @throws Exception
	 */
	public static WebsiteAccount getWebsiteAccountFromNickname(ConnectionSource databaseConnection, String nickname, User safeStoreUser) throws Exception {
		try {
			Dao<WebsiteAccountEntity, Integer> websiteAccountDao = DaoManager.createDao(databaseConnection, WebsiteAccountEntity.class);
			QueryBuilder<WebsiteAccountEntity, Integer> queryBuilder = websiteAccountDao.queryBuilder();
			queryBuilder.where()
			.eq("nickname", Encryption.encrypt(nickname))
			.and()
			.eq("safe_store_username", safeStoreUser.getUserEntity().getUsername());
			List<WebsiteAccountEntity> accountsWithMatchingNicknames = websiteAccountDao.query(queryBuilder.prepare());
			if (accountsWithMatchingNicknames.size() == 0) {
				throw new Exception("No accounts exist with that nickname");
			}
			return new WebsiteAccount(accountsWithMatchingNicknames.get(0));
		} catch (SQLException e) {
			e.printStackTrace();
			throw(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw(e);
		}
	}

	/**
	 * Prompts the user to update information for their website account and updates it in database
	 * @param databaseConnection
	 * @param keyboard
	 * @param safeStoreUser
	 * @return - true if update was successful, false otherwise
	 */
	public static boolean updateWebsiteAccount(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		System.out.println("Enter the nickname of the account you want to modify: ");
		String nickname = keyboard.nextLine().trim();
		try {
			WebsiteAccount accountToModify = WebsiteAccount.getWebsiteAccountFromNickname(databaseConnection, nickname, safeStoreUser);
			System.out.println(accountToModify.toString());
			System.out.println("Which part of account do you want to modify?");
			String[] acceptableInput = {"Nickname", "Login", "Password"};
			String whichFieldToModify = keyboard.nextLine().trim();
			while (!Arrays.asList(acceptableInput).contains(whichFieldToModify)) {
				System.out.println("Invalid input. Please type: Nickname, Login, or Password for which you want to change: ");
				whichFieldToModify = keyboard.nextLine().trim();
			}
			if (whichFieldToModify.equals("Nickname")) {
				System.out.println("Please enter the new nickname for the account: ");
				String newNickname = keyboard.nextLine().trim();
				return accountToModify.setNickname(newNickname);
			}
			if (whichFieldToModify.equals("Login")) {
				System.out.println("Please enter the new login for the account: ");
				String newLogin = keyboard.nextLine().trim();
				return accountToModify.setWebsiteLogin(newLogin);
			}
			if (whichFieldToModify.equals("Password")) {
				System.out.println("Please enter the new password for the account: ");
				String newPassword = keyboard.nextLine().trim();
				return accountToModify.setWebsitePassword(newPassword);
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	public static boolean updateWebsiteAccount(ConnectionSource databaseConnection, String currentAccountUsername, User safeStoreUser, String[] fieldsToModify,String[] newInputs) {
		try {
			WebsiteAccount accountToModify = WebsiteAccount.getWebsiteAccountFromNickname(databaseConnection,currentAccountUsername, safeStoreUser);

			for(int i = 0; i < fieldsToModify.length; i++) {

				if (fieldsToModify[i].equals("Nickname")) {
					String newNickname = newInputs[0].trim();
					accountToModify.setNickname(newNickname);
					System.out.println("GETS HERE");
				}
				if (fieldsToModify[i].equals("Login")) {
					String newLogin = newInputs[1].trim();
					accountToModify.setWebsiteLogin(newLogin);
				}
				if (fieldsToModify[i].equals("Password")) {
					String newPassword = newInputs[2].trim();
					accountToModify.setWebsitePassword(newPassword);
				}
			}
			return true;
		} catch (SQLException e) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}

}

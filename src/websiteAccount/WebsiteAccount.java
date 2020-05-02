package websiteAccount;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.QueryBuilder;
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

	public String getWebsiteLogin() {
		return Encryption.decrypt(this.websiteAccountEntity.getWebsiteLogin());
	}

	public String getWebsitePassword() {
		return Encryption.decrypt(this.websiteAccountEntity.getWebsitePassword());
	}

	public WebsiteAccountEntity getWebsiteAccountEntity() {
		return this.websiteAccountEntity;
	}

	/**
	 * 
	 * @param nickname new nickname
	 * @return true if successful database update 
	 */
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

	/**
	 * 
	 * @param websiteLogin new website account login 
	 * @return true if successful database update
	 */
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

	/**
	 * 
	 * @param websitePassword new website account password
	 * @return true if successful database update
	 */
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

	public String toString() {
		return this.getNickname() + " - Login: " + this.getWebsiteLogin() + ", Password: " + this.getWebsitePassword();
	}
	
	/**
	 * Checks to see if a website account with the supplied nickname already exists
	 * @param databaseConnection
	 * @param safeStoreUser
	 * @param nickname
	 * @return true if the nickname given doens't exist in db for that user, false otherwise
	 */
	public static boolean accountNicknameIsUnique(ConnectionSource databaseConnection, 
			UserEntity safeStoreUser, String nickname) {
		try {
			Dao<WebsiteAccountEntity, String> websiteAccountDao = DaoManager.createDao(databaseConnection, WebsiteAccountEntity.class);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("nickname", Encryption.encrypt(nickname));
			queryParams.put("safe_store_username", safeStoreUser);
			List<WebsiteAccountEntity> returnedWebsiteAccounts = websiteAccountDao.queryForFieldValues(queryParams);
			if (returnedWebsiteAccounts.isEmpty()) {
				System.out.println("empty");
				return true;
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
		
	}
	/**
	 * Adds the WebsiteAccount to the database
	 * @param databaseConnection - the ConnectionSource object to the database where the 
	 * web account will be stored
	 * @return true if successful database update
	 */
	public boolean addWebsiteAccount(ConnectionSource databaseConnection) {
		try {
			Dao<WebsiteAccountEntity, Integer> websiteAccountDao = 
					DaoManager.createDao(databaseConnection, WebsiteAccountEntity.class);

			// nicknames must be unique 
			if (accountNicknameIsUnique(databaseConnection, this.websiteAccountEntity.getSafeStoreUser(), this.getNickname())) {
				websiteAccountDao.create(websiteAccountEntity);
				return true;
			}else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}	

	/**
	 * gets the foreign collection of all website account rows for that user
	 * @param databaseConnection
	 * @param safeStoreUser of type User
	 * @return a ForeignCollection<WebsiteAccountEntity> that holds all the db rows of web accounts
	 * 	for the safeStoreUser
	 */
	public static ForeignCollection<WebsiteAccountEntity> getAllWebsiteAccounts(ConnectionSource databaseConnection, 
			User safeStoreUser) {
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
	 * Gets a WebsiteAccount based on a User and a nickname. If it doesn't exist, throws an 
	 * exception
	 * @param databaseConnection
	 * @param nickname
	 * @param safeStoreUser
	 * @return - the WebsiteAccount associated with the nickname and inputed user 
	 * @throws Exception
	 */
	public static WebsiteAccount getWebsiteAccountFromNickname(ConnectionSource databaseConnection, 
			String nickname, User safeStoreUser) throws Exception {
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
	 * 
	 * @param databaseConnection
	 * @param currentAccountNickname
	 * @param safeStoreUser
	 * @param newInputs user inputs to modify web acct info. User only fills in input fields they wish to change.
	 * User has the option to modify: nickname in newInputs[0], web login in newInputs[1], 
	 * and web password in newInputs[2]
	 * 
	 * @return true if successful database update 
	 */
	public static boolean updateWebsiteAccount(ConnectionSource databaseConnection, 
			String currentAccountNickname, User safeStoreUser, String[] newInputs) {
		try {
			WebsiteAccount accountToModify = 
					WebsiteAccount.getWebsiteAccountFromNickname(databaseConnection, currentAccountNickname, safeStoreUser);

			if (!newInputs[0].isEmpty()) {
				if(accountNicknameIsUnique(databaseConnection, safeStoreUser.getUserEntity(), newInputs[0])) {
				accountToModify.setNickname(newInputs[0]);
				}else {
					return false;
				}
			}
			if (!newInputs[1].isEmpty()) {
				accountToModify.setWebsiteLogin(newInputs[1]);
			}
			if (!newInputs[2].isEmpty()) {
				accountToModify.setWebsitePassword(newInputs[2]);
			}

			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}

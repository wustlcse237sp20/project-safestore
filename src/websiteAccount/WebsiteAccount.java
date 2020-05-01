package websiteAccount;

import java.sql.SQLException;
import java.util.List;

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
	 * Adds the WebsiteAccount to the database
	 * @param databaseConnection - the ConnectionSource object to the database where the 
	 * web account will be stored
	 * @return true if successful database update
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

			// nicknames must be unique 
			if (accountsWithMatchingNicknames.size() == 0) {
				websiteAccountDao.create(websiteAccountEntity);
				return true;
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
				accountToModify.setNickname(newInputs[0]);
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

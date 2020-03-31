package websiteAccount;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.UserEntity;
import tables.WebsiteAccountEntity;

public class WebsiteAccount {
	
	private WebsiteAccountEntity websiteAccount;
	private Dao<WebsiteAccountEntity, Integer> websiteAccountDao;
	
	//TODO: change UserEntity to User once Tali finished
	public WebsiteAccount(UserEntity safeStoreUser, String nickname, String websiteLogin, 
			String websitePassword, ConnectionSource connectionSource) throws SQLException {
		try {
			websiteAccount = new WebsiteAccountEntity(
					safeStoreUser, Encryption.encrypt(nickname), Encryption.encrypt(websiteLogin), Encryption.encrypt(websitePassword));
			websiteAccountDao = DaoManager.createDao(connectionSource, WebsiteAccountEntity.class);
		} catch (SQLException e) { // if the connection to database failed
			e.printStackTrace();
		}
	}
	
	public WebsiteAccountEntity getWebsiteAccount() {
		return websiteAccount;
	}

	public void setWebsiteAccount(WebsiteAccountEntity websiteAccount) {
		this.websiteAccount = websiteAccount;
	}

	public Dao<WebsiteAccountEntity, Integer> getWebsiteAccountDao() {
		return websiteAccountDao;
	}

	public void setWebsiteAccountDao(Dao<WebsiteAccountEntity, Integer> websiteAccountDao) {
		this.websiteAccountDao = websiteAccountDao;
	}
	
	public void insertIntoDatabase() throws SQLException {
		try {
			websiteAccountDao.create(websiteAccount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		
		
	}

}

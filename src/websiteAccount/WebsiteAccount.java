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
	
	//TODO: change UserEntity to User once Tali finished
	public WebsiteAccount(UserEntity safeStoreUser, String nickname, String websiteLogin, 
			String websitePassword) throws SQLException {
			websiteAccount = new WebsiteAccountEntity(safeStoreUser, Encryption.encrypt(nickname), 
					Encryption.encrypt(websiteLogin), Encryption.encrypt(websitePassword));
	}
	
	public WebsiteAccountEntity getWebsiteAccount() {
		return websiteAccount;
	}

	public void setWebsiteAccount(WebsiteAccountEntity websiteAccount) {
		this.websiteAccount = websiteAccount;
	}
	
	public void addWebsiteAccount(ConnectionSource connectionSource) throws SQLException {
		try {
			Dao<WebsiteAccountEntity, Integer> websiteAccountDao = 
					DaoManager.createDao(connectionSource, WebsiteAccountEntity.class);
			websiteAccountDao.create(websiteAccount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

	}

}

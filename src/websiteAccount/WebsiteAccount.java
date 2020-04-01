package websiteAccount;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.WebsiteAccountEntity;
import user.User;

public class WebsiteAccount {
	
	private WebsiteAccountEntity websiteAccountEntity;
	
	public WebsiteAccount(User safeStoreUser, String nickname, String websiteLogin, String websitePassword) {
			websiteAccountEntity = new WebsiteAccountEntity(safeStoreUser.getUserEntity(), Encryption.encrypt(nickname), 
					Encryption.encrypt(websiteLogin), Encryption.encrypt(websitePassword));
	}
	
	public WebsiteAccount(WebsiteAccountEntity websiteAccountEntity) {
		this.websiteAccountEntity = websiteAccountEntity;
	}
	
	public WebsiteAccountEntity getWebsiteAccountEntity() {
		return this.websiteAccountEntity;
	}

	public void setWebsiteAccountEntity(WebsiteAccountEntity websiteAccount) {
		this.websiteAccountEntity = websiteAccount;
	}
	
	public void addWebsiteAccount(ConnectionSource databaseConnection) throws SQLException {
		try {
			Dao<WebsiteAccountEntity, Integer> websiteAccountDao = 
					DaoManager.createDao(databaseConnection, WebsiteAccountEntity.class);
			websiteAccountDao.create(websiteAccountEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

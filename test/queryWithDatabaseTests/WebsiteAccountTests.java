package queryWithDatabaseTests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.UserEntity;
import tables.WebsiteAccountEntity;
import user.User;
import websiteAccount.WebsiteAccount;

class WebsiteAccountTests {
	
	ConnectionSource databaseConnection;
	String testUserUsername = "username";
	String testUserPassword = "password";
	String testUserSalt = "salt";
	UserEntity testUserEntity = new UserEntity(testUserUsername, testUserPassword, testUserSalt);
	String testAccountNickname = "nickname";
	String testAccountLogin = "login@login";
	String testAccountPassword = "pA$$woRd";
	WebsiteAccountEntity testWebsiteAccountEntity = 
			new WebsiteAccountEntity(testUserEntity, testAccountNickname, testAccountLogin, testAccountPassword);
	Dao<UserEntity, String> userDao;
	Dao<WebsiteAccountEntity, Integer> websiteAccountDao;

	@BeforeEach
	void setUp() throws Exception {
		try {
			this.databaseConnection = new JdbcConnectionSource("jdbc:sqlite:src/database/app.db");
			assertNotNull(databaseConnection, "Connection is null. Failed to Connect.");
			websiteAccountDao = DaoManager.createDao(databaseConnection, WebsiteAccountEntity.class);
			userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
		} catch (SQLException e) {
			fail("Failed to connect to database and set up DAO.");
			e.printStackTrace();
		}
	}

	@Test
	void testInsertWebsiteAccountToDatabase() {
		try {
			User testUser = new User(testUserUsername, testUserPassword);
			userDao.create(testUserEntity);
			WebsiteAccount account = new WebsiteAccount(testUser, testAccountNickname, testAccountLogin, testAccountPassword);
			account.addWebsiteAccount(databaseConnection);
			WebsiteAccountEntity accountPulledFromDb = websiteAccountDao.queryForId(account.getId());
			assertEquals("Usernames foreign keys not equal", accountPulledFromDb.getSafeStoreUser().getUsername(), testUserUsername);
			assertEquals("Nicknames not equal", testAccountNickname, Encryption.decrypt(accountPulledFromDb.getNickname()));
			assertEquals("Logins not equal", testAccountLogin, Encryption.decrypt(accountPulledFromDb.getWebsiteLogin()));
			assertEquals("Passwords not equal", testAccountPassword, Encryption.decrypt(accountPulledFromDb.getWebsitePassword()));
			
			// delete rows from database once completed test
			websiteAccountDao.deleteById(account.getId()); 
			userDao.delete(testUserEntity);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
	}

}

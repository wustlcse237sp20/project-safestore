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
		} catch (SQLException e) {
			fail("Failed to connect to database and set up DAO.");
			e.printStackTrace();
		}
	}

	@Test
	void testInsertWebsiteAccountToDatabase() {
		try {
			// insert both into database both ways
			User testUser = new User(testUserEntity);
			testUser.createSafeStoreAccount(databaseConnection);
			websiteAccountDao.create(testWebsiteAccountEntity);
			
			WebsiteAccount websiteAccountQuery = // insert query object into database
					new WebsiteAccount(testUser, testAccountNickname, testAccountLogin, testAccountPassword);
			websiteAccountQuery.addWebsiteAccount(databaseConnection);
			
			// pull from database both objects and make sure they are equal
			WebsiteAccountEntity testEntityPulledFromDb = websiteAccountDao.queryForId(testWebsiteAccountEntity.getId());
			WebsiteAccountEntity queryEntityPulledFromDb = websiteAccountDao.queryForId(websiteAccountQuery.getWebsiteAccountEntity().getId());
			System.out.println(testEntityPulledFromDb.getSafeStoreUser());
			assertEquals("Usernames foreign keys not equal", 
					testEntityPulledFromDb.getSafeStoreUser().getUsername(), queryEntityPulledFromDb.getSafeStoreUser().getUsername());
			assertEquals("Nicknames not equal", testEntityPulledFromDb.getNickname(), Encryption.decrypt(queryEntityPulledFromDb.getNickname()));
			assertEquals("Logins not equal", testEntityPulledFromDb.getWebsiteLogin(), Encryption.decrypt(queryEntityPulledFromDb.getWebsiteLogin()));
			assertEquals("Passwords not equal", testEntityPulledFromDb.getWebsitePassword(), Encryption.decrypt(queryEntityPulledFromDb.getWebsitePassword()));
			
			// delete rows from database once completed test
			websiteAccountDao.delete(testEntityPulledFromDb); 
			websiteAccountDao.delete(queryEntityPulledFromDb);
			
			// Need to delete user manually because delete not working for some reason
			//userDao.delete(testUser.getUserEntity());
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
	}

}

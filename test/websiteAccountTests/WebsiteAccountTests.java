package websiteAccountTests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
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
	
	@Test
	void testStaticAddWebsiteAccount() {
		File file = new File("test/websiteAccountTests/addWebAccountInput.txt");
		try {
			Scanner keyboard = new Scanner(file);
			User testUser = new User(testUserUsername, testUserPassword);
			userDao.create(testUserEntity);
			assertTrue(WebsiteAccount.addWebsiteAccountPrompts(databaseConnection, keyboard, testUser), "Insert account failed");
			
			QueryBuilder<WebsiteAccountEntity, Integer> queryBuilder = websiteAccountDao.queryBuilder();
			queryBuilder.where().eq("safe_store_username", testUserUsername);
			PreparedQuery<WebsiteAccountEntity> preparedQuery = queryBuilder.prepare();
			List<WebsiteAccountEntity> accountList = websiteAccountDao.query(preparedQuery);
			WebsiteAccountEntity accountPulledFromDb = accountList.get(0);
			assertTrue(accountList.size() == 1, "Should have inserted exactly 1 account into db but was not the case");
			
			assertEquals("Nicknames not equal", "niiicckknnaammmee", Encryption.decrypt(accountPulledFromDb.getNickname()));
			assertEquals("Logins not equal", "uusseernna3948me", Encryption.decrypt(accountPulledFromDb.getWebsiteLogin()));
			assertEquals("Passwords not equal", "pas938wo4045rd", Encryption.decrypt(accountPulledFromDb.getWebsitePassword()));
			
			websiteAccountDao.delete(accountPulledFromDb); 
			userDao.delete(testUserEntity);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	

	@Test
	void testInsertWebsiteAccountToDatabaseWithSameNickname() {
		try {
			User testUser = new User(testUserUsername, testUserPassword);
			userDao.create(testUserEntity);
			WebsiteAccount account = new WebsiteAccount(testUser, testAccountNickname, testAccountLogin, testAccountPassword);
			assertTrue(account.addWebsiteAccount(databaseConnection), "Wasn't able to add the first account");
			assertFalse(account.addWebsiteAccount(databaseConnection), "Was able to add same nickname");
			
			// delete rows from database once completed test
			websiteAccountDao.deleteById(account.getId()); 
			userDao.delete(testUserEntity);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	@Test
	void testViewWebsiteAccountLogin() {
		File file = new File("test/websiteAccountTests/viewLoginForWebAccount.txt");
		try {
			// add user and account to db
			User testUser = new User(testUserUsername, testUserPassword);
			testUser.createSafeStoreAccount(databaseConnection);
			WebsiteAccount account = new WebsiteAccount(testUser, testAccountNickname, testAccountLogin, testAccountPassword);
			account.addWebsiteAccount(databaseConnection);
			
			// try to view the website account login
			Scanner keyboard = new Scanner(file);
			String loginPrinted = WebsiteAccount.viewWebsiteAccountLogin(databaseConnection, keyboard, testUser);
			assertEquals(testAccountLogin, loginPrinted);
			
			// delete rows from database once completed test
			websiteAccountDao.deleteById(account.getId()); 
			userDao.delete(testUser.getUserEntity());
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}

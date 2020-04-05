package userTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Scanner;

import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import tables.UserEntity;
import user.User;


class UserTest {
	ConnectionSource databaseConnection;

	@BeforeEach
	void setUp() throws Exception {
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			databaseConnection = new JdbcConnectionSource(databaseUrl);
			assertNotNull(databaseConnection, "Connection is null. Failed to Connect.");
		}
		catch (SQLException e) {
			fail("failed to connect to database.");
			e.printStackTrace();
		}
	}

	@Test
	void testCreateAccountDatabaseEnd() {
		String username = "testNewUser";
		String password = "testNewPassword";	
		User newUser = new User(username, password);
		assertTrue(newUser.createSafeStoreAccountThroughDatabase(databaseConnection), "should be successful creation");
		try {
			Dao<UserEntity, String> userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			UserEntity matchedUser = userDao.queryForId(newUser.getUserEntity().extractId());
			assertNotNull(matchedUser, "query for new user returned null.");
			assertEquals(matchedUser.extractId(), username, "username doesn't match");
			userDao.deleteById("testNewUser");
		} catch (SQLException e) {
			fail("failed to create user dao");
			e.printStackTrace();
		}
	}
	

	@Test
	void testCreateAccountThroughTerminal() throws IOException {
		File createAccountUserInput = new File("test/userTest/terminalCreateAccount");
		
		// output stream code from https://limzhenghong.wordpress.com/2015/03/18/junit-with-system-out-println/“
		//Prepare to redirect output
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(os);
		System.setOut(ps);
		
		try {
			Scanner terminalCreateAcounnt = new Scanner(createAccountUserInput);
			
			//case: empty line before unique username. password  entered normally.
			assertEquals("test user", User.createSafeStoreAccountTerminal(databaseConnection, terminalCreateAcounnt));
			assertEquals("Type your username:" + System.getProperty("line.separator")
					+ "Your username cannot be empty and it must be unique. Try another." 
					+ System.getProperty("line.separator") + "Type your password:"
					+ System.getProperty("line.separator"), os.toString());
			//Restore normal output
			System.setOut(System.out);
			os.reset();
			
			//case: unique username entered normally. empty line entered before password
			assertEquals("test user2", User.createSafeStoreAccountTerminal(databaseConnection, terminalCreateAcounnt));
			assertEquals("Type your username:" + System.getProperty("line.separator")
				 + "Type your password:" + System.getProperty("line.separator")
				 + "Your password cannot be empty."+ System.getProperty("line.separator")
				 , os.toString());
			System.setOut(System.out);
			os.reset();
		
			//case: unique username and password entered normally
			assertEquals("test user3", User.createSafeStoreAccountTerminal(databaseConnection, terminalCreateAcounnt));
			assertEquals("Type your username:" + System.getProperty("line.separator")
			 	+ "Type your password:"
				+ System.getProperty("line.separator"), os.toString());
			System.setOut(System.out);
			os.reset();
			
			//case: non-unique username entered first. Unique username entered
			//after with password entered normally
			assertEquals("test user4", User.createSafeStoreAccountTerminal(databaseConnection, terminalCreateAcounnt));
			assertEquals("Type your username:" + System.getProperty("line.separator")
			+ "Your username cannot be empty and it must be unique. Try another." 
			+ System.getProperty("line.separator") + "Type your password:"
			+ System.getProperty("line.separator"), os.toString());
			System.setOut(System.out);
			os.reset();
			
			os.close();
			ps.close();
			try {
				Dao<UserEntity, String> userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
				userDao.deleteById("test user");
				userDao.deleteById("test user2");
				userDao.deleteById("test user3");
				userDao.deleteById("test user4");
			} catch (SQLException e) {
				fail("failed to create user dao");
				e.printStackTrace();
			}
			terminalCreateAcounnt.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	
	@Test
	void testUniqueUsername() {
		String username = "testNewUser";
		String password = "testNewPassword";	
		User newUser = new User(username, password);
		newUser.createSafeStoreAccountThroughDatabase(databaseConnection);
		assertFalse(User.isUniqueUsername(databaseConnection, username), "not a unique username. should return false.");
		assertTrue(User.isUniqueUsername(databaseConnection, "this should be unique"), "unique username. should return true.");
		try {
			Dao<UserEntity, String> userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			userDao.deleteById("testNewUser");
		} catch (SQLException e) {
			fail("failed to create user dao");
			e.printStackTrace();
		}
	}
	
	@Test
	void testLoginForRegisteredUser() {
		String username = "testNewUser";
		String password = "testNewPassword";	
		User newUser = new User(username, password);
		newUser.createSafeStoreAccountThroughDatabase(databaseConnection);
		assertFalse(User.isUniqueUsername(databaseConnection, username), "User is inputed in database.");
		assertTrue(User.loginThroughDatabase(databaseConnection, username, password), "User should be able to login");
		try {
			Dao<UserEntity, String> userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			userDao.deleteById("testNewUser");
		} catch (SQLException e) {
			fail("failed to create user dao");
			e.printStackTrace();
		}
	}
	
	@Test
	void testLoginForNonRegisteredUser() {
		String username = "testUser";
		String password = "testPassword";	
		assertFalse(User.loginThroughDatabase(databaseConnection, username, password), "User should not be able to login. They are not a registered user.");
	}
	
	@Test
	void testPasswordHashing() {
		String username = "testNewUser";
		String password = "testNewPassword";	
		User newUser = new User(username, password);
		newUser.createSafeStoreAccountThroughDatabase(databaseConnection);
		try {
			Dao<UserEntity, String> userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			UserEntity matchedUser = userDao.queryForId(username);
			byte[] salt = matchedUser.getSalt();
			String hashedPassword = matchedUser.getPassordHashed();
			assertEquals(hashedPassword, User.getSecurePassword(password, salt));
			userDao.deleteById("testNewUser");
		} catch (SQLException e) {
			fail("failed to create user dao");
			e.printStackTrace();
		}
	}
	
	@Test
	void testLoginViaTerminal() {
		String username = "testNewUser";
		String password = "testNewPassword";	
		User newUser = new User(username, password);
		assertTrue(newUser.createSafeStoreAccountThroughDatabase(databaseConnection), "should be successful account creation.");
		try {
			File loginUserInput = new File("test/userTest/terminalLoginTest");
			Scanner terminalLogin = new Scanner(loginUserInput);
			
			//normal case: correct username and password
			assertEquals(username, User.terminalLogin(databaseConnection, terminalLogin), 
					"Should return username for a successful login.");
			
			//registered username but incorrect password
			assertNull(User.terminalLogin(databaseConnection, terminalLogin), "Should return null"
					+ "for an unsuccessful login");
			
			//not a registered username 
			assertNull(User.terminalLogin(databaseConnection, terminalLogin), "Should return null"
					+ "for an unsuccessful login");
			
			terminalLogin.close();
			try {
				Dao<UserEntity, String> userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
				userDao.deleteById("testNewUser");
			} catch (SQLException e) {
				fail("failed to create user dao");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			fail("failed to open terminal login file");
			e.printStackTrace();
		}
	}
	
	@AfterEach 
	void cleanUp() {
		if(databaseConnection != null) {
			try {
				databaseConnection.close();
			} catch (IOException e) {
				fail("couldn't close connection");
				e.printStackTrace();
			}
		}
	}
}

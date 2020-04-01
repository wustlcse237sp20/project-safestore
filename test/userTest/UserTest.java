package userTest;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;

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
	void testCreateAccount() {
		String username = "testNewUser";
		String password = "testNewPassword";	
		User newUser = new User(username, password);
		newUser.createSafeStoreAccount(databaseConnection);
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
	void testUniqueUsername() {
		String username = "testNewUser";
		String password = "testNewPassword";	
		User newUser = new User(username, password);
		newUser.createSafeStoreAccount(databaseConnection);
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
		newUser.createSafeStoreAccount(databaseConnection);
		assertFalse(User.isUniqueUsername(databaseConnection, username), "User is inputed in database.");
		assertTrue(User.login(databaseConnection, username, password), "User should be able to login");
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
		String username = "testNewUser";
		String password = "testNewPassword";	
		assertFalse(User.login(databaseConnection, username, password), "User should not be able to login. They are not a registered user.");
	}
	
	@Test
	void testPasswordHashing() {
		String username = "testNewUser";
		String password = "testNewPassword";	
		User newUser = new User(username, password);
		newUser.createSafeStoreAccount(databaseConnection);
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

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
	ConnectionSource connectionSource;

	@BeforeEach
	void setUp() throws Exception {
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			connectionSource = new JdbcConnectionSource(databaseUrl);
			assertNotNull(connectionSource, "Connection is null. Failed to Connect.");
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
		newUser.createSafeStoreAccount(connectionSource);
		try {
			Dao<UserEntity, String> userDao = DaoManager.createDao(connectionSource, UserEntity.class);
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
		newUser.createSafeStoreAccount(connectionSource);
		assertFalse(User.isUniqueUsername(connectionSource, username), "not a unique username. should return false.");
		assertTrue(User.isUniqueUsername(connectionSource, "this should be unique"), "unique username. should return true.");
		try {
			Dao<UserEntity, String> userDao = DaoManager.createDao(connectionSource, UserEntity.class);
			userDao.deleteById("testNewUser");
		} catch (SQLException e) {
			fail("failed to create user dao");
			e.printStackTrace();
		}
	}
	
	@AfterEach 
	void cleanUp() {
		if(connectionSource != null) {
			try {
				connectionSource.close();
			} catch (IOException e) {
				fail("couldn't close connection");
				e.printStackTrace();
			}
		}
	}
}

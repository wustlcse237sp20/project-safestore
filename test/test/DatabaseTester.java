package test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;

import database.Connect;
import database.InsertRecords;
import tables.Address;
import tables.CreditCard;
import tables.DebitCard;
import tables.User;
import websiteAccount.WebsiteAccount;

/**
 * Things to test:
 * connect to database, disconnect (done)
 * database tables/columns exist and are correct (done)
 * can add data to tables
 * can query data from tables
 *
 */

/** 
 * !!! sql col types are found here 
 * https://www.tutorialspoint.com/java-resultsetmetadata-getcolumntype-method-with-example
 */ 

public class DatabaseTester {
	ConnectionSource connectionSource;
	
	//table Daos
	Dao<User, String> userDao;
	Dao<CreditCard, String> creditCardDao;
	Dao<DebitCard, String> debitCardDao;
	Dao<WebsiteAccount, String> websiteDao;
	Dao<Address, String> addressDao;
	
	//generic record objects
	User testUser = new User();
	WebsiteAccount testWebAcct = new WebsiteAccount();
	Address testAddress = new Address();
	CreditCard testCreditCard = new CreditCard();
	DebitCard testDebitCard = new DebitCard();
	
	/**
	 * Setup connection to database.
	 * Tests connecting to db doesn't throw SQLExceptions 
	 * If no exception thrown, tests connection isn't null
	 */
	@Before
    public void setUp() throws Exception {
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			connectionSource = new JdbcConnectionSource(databaseUrl);
			assertNotNull(connectionSource, "Connection is null. Failed to Connect.");
			
			userDao = DaoManager.createDao(connectionSource, User.class);
			creditCardDao = DaoManager.createDao(connectionSource, CreditCard.class);
			debitCardDao = DaoManager.createDao(connectionSource, DebitCard.class);
			websiteDao = DaoManager.createDao(connectionSource, WebsiteAccount.class);
			addressDao = DaoManager.createDao(connectionSource, Address.class);
			
			testUser.setUsername("test user");
			testUser.setPasswordHashed("not a hashed password");
			testUser.setSalt("actually kind of a salt");
			
			testWebAcct.setNickname("test website");
			testWebAcct.setSafeStoreUser(testUser);
			testWebAcct.setWebsiteLogin("test login");
			testWebAcct.setWebsitePassword("test website pswd");
			
			testAddress.setCity("test city");
			testAddress.setState("test state");
			testAddress.setStreetAddress("test street address");
			testAddress.setZipCode("test zip code");
			
			testCreditCard.setBillingAddress(testAddress);
			testCreditCard.setCreditCardNumber("test credit card number");
			testCreditCard.setCvv("test cvv");
			testCreditCard.setNickname("test nickname");
			testCreditCard.setSafeStoreUser(testUser);
			testCreditCard.setExpirationDate("test date");
			
			testDebitCard.setBillingAddress(testAddress);
			testDebitCard.setCvv("test cvv");
			testDebitCard.setNickname("test nickname");
			testDebitCard.setDebitCardNumber("test debit card number");
			testDebitCard.setExpirationDate("test date");
			testDebitCard.setPin("test pin");
			testDebitCard.setSafeStoreUser(testUser);
		}
		catch (SQLException e) {
			fail("failed to connect to database.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
	/**
	 * test connection to each table
	 */
	@Test
	public void testConnectUserTable() {
		try {
			assertTrue(userDao.isTableExists(), "User table can't be found");
		} catch (SQLException e) {
			fail("SQL exception in determining user table existence");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(connectionSource.isOpen("User"), "Failed to connect to User Table");
	}
	
	@Test
	public void testConnectWebsiteTable() {
		try {
			assertTrue(websiteDao.isTableExists(), "Website table can't be found");
		} catch (SQLException e) {
			fail("SQL exception in determining website table existence");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(connectionSource.isOpen("WebsiteAccount"), "Failed to connect to WebsiteAccount Table");
	}
	
	@Test
	public void testConnectCreditCardTable() {
		try {
			assertTrue(creditCardDao.isTableExists(), "Credit Card table can't be found");
		} catch (SQLException e) {
			fail("SQL exception in determining credit card table existence");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(connectionSource.isOpen("CreditCard"), "Failed to connect to CreditCard Table");
	}
	
	@Test
	public void testConnectDebitCardTable() {
		try {
			assertTrue(debitCardDao.isTableExists(), "Debit card table can't be found");
		} catch (SQLException e) {
			fail("SQL exception in determining debit card table existence");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(connectionSource.isOpen("DebitCard"), "Failed to connect to DebitCard Table");
	}
	
	@Test
	public void testConnectAddressTable() {
		try {
			assertTrue(addressDao.isTableExists(), "Address table can't be found");
		} catch (SQLException e) {
			fail("SQL exception in determining address table existence");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue(connectionSource.isOpen("Address"), "Failed to connect to Address Table");
	}
	
	@After
	@Test
	public void testDisconnect() {
		if(connectionSource != null) {
			try {
				connectionSource.close();
			} catch (IOException e) {
				fail("couldn't close connection");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	

}

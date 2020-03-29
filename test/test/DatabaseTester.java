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
import tables.WebsiteAccount;

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
	
	//test generic records for column tests 
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
	
	/**
	 * Number of and correct columns in user table 
	 */
	@Test
	public void testUserTableColumns() {
		try {
			InsertRecords.insertSafeStoreUser(testUser);
			GenericRawResults<String[]> rawTestUserQuery = userDao.queryRaw(testUser.getUsername());
			String[] queryColNames = rawTestUserQuery.getColumnNames();
			int queryColNum = rawTestUserQuery.getNumberColumns();
			assertEquals(queryColNum, 3, "Incorrect number of columns. User Table should have 3.");
			String[] correctColNames = {"safe_store_username", "safe_store_pswd", "salt"};
			for(int i=0;i<correctColNames.length;i++) {
				assertEquals(queryColNames[i], correctColNames[i], "Wrong column name.");
			}
			userDao.delete(testUser);
		} catch (SQLException e) {
			fail("could not query/update user table.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testInsertUser() {
		try {
			InsertRecords.insertSafeStoreUser(testUser);
			User insertedUser = userDao.queryForSameId(testUser);
			
		} catch (SQLException e) {
			fail("could not insert into user table");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testWebsiteAccountsTableColumns() {
		
		
		User testUser = new User();
		testUser.setUsername("test user");
		testUser.setPasswordHashed("not a hashed password");
		testUser.setSalt("actually kind of a salt i guess");
		try {
			userDao.createIfNotExists(testUser);
			GenericRawResults<String[]> rawTestUserQuery = userDao.queryRaw(testUser.getUsername());
			String[] queryColNames = rawTestUserQuery.getColumnNames();
			int queryColNum = rawTestUserQuery.getNumberColumns();
			assertEquals(queryColNum, 3, "Incorrect number of columns. User Table should have 3.");
			String[] correctColNames = {"safe_store_username", "safe_store_pswd", "salt"};
			for(int i=0;i<correctColNames.length;i++) {
				assertEquals(queryColNames[i], correctColNames[i], "Wrong column name.");
			}
			userDao.delete(testUser);
		} catch (SQLException e) {
			fail("could not query/update user table.");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] webAcctsColNames = {"safe_store_username", "website_name", "website_login", "website_pswd"};
		// all 12 column types are varchar, further discussion needed on this 
		Integer[] webAcctsColTypes = {12, 12, 12, 12};
		
		 
	}
	
	@Test
	public void testInsertWebsite() {
//		String safe_store_username = "testUser";
//		String website_name = "test website";
//		String website_login = "test login";
//		String website_pswd = "test password";
//		InsertRecords.insertWebAccount(safe_store_username, website_name, website_login, website_pswd);
//		String sqlStmt = "SELECT * FROM WebsiteAccounts WHERE safe_store_username=" + safe_store_username
//				+ "AND website_name=" + website_name;
//		try {  
//            Statement stmt = conn.createStatement();  
//            ResultSet website = stmt.executeQuery(sqlStmt);
//            assertNotNull(website);
//            assertTrue(website.isLast(), "should only be one entry safe_store_username & website_name "
//            		+ "are joined prim key");
//            assertEquals(website.getString("website_login"), "test login");
//            assertEquals(website.getString("website_pswd"), "test password");
//        } catch (SQLException e) {  
//            System.out.println(e.getMessage());  
//        }  
	}
	
	@Test
	public void testCreditCardTableColumns() {
//		String[] creditCardColNames = {"safe_store_username", "cc_nickname", "cc_num", "expire_date", "cvv", "address_id"};
//		// all 12 column types are varchar, further discussion needed on this 
//		// all 4 column types are int 
//		Integer[] creditCardColTypes = {12, 12, 12, 12, 4, 4};
//		
//		String sqlStmt = "SELECT * FROM CreditCards";
//		try {
//			Statement stmt = conn.createStatement();
//			stmt.execute(sqlStmt);
//			ResultSetMetaData creditCardCol = stmt.getResultSet().getMetaData();
//			assertEquals(creditCardCol.getColumnCount(), 6, "Credit Card Table should have 6 columns");
//			for(int i=0;i<creditCardColNames.length;i++) {
//				assertEquals(creditCardCol.getColumnName(i), creditCardColNames[i]);
//				assertEquals(creditCardCol.getColumnType(i), creditCardColTypes);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
	}
	
	@Test
	public void testInsertCreditCardWithNickname() {
//		String safe_store_username = "testUser";
//		String cc_nickname = "test nickname";
//		String cc_num = "1234123412341234";
//		String expire_date = "test expiration date";
//		int cvv = -1;
//		int address_id = -10;
//		InsertRecords.insertCreditCard(safe_store_username, cc_nickname, cc_num, expire_date, cvv, address_id);
//		String sqlStmt = "SELECT * FROM CreditCards WHERE cc_num=" + cc_num;
//		try {  
//            Statement stmt = conn.createStatement();  
//            ResultSet cc = stmt.executeQuery(sqlStmt);
//            assertNotNull(cc);
//            assertTrue(cc.isLast(), "should only be one entry for each credit card");
//            assertEquals(cc.getString("safe_store_username"), safe_store_username);
//            assertEquals(cc.getString("cc_nickname"), cc_nickname);
//            assertEquals(cc.getString("expire_date"), expire_date);
//            assertEquals(cc.getInt("cvv"), cvv);
//            assertEquals(cc.getInt("address_id"), address_id);
//        } catch (SQLException e) {  
//            System.out.println(e.getMessage());  
//        }  
	}
	
	@Test
	public void testInsertCreditCardWithoutNickname() {
//		String safe_store_username = "testUser";
//		String cc_num = "1234123412341234";
//		String expire_date = "test expiration date";
//		int cvv = -1;
//		int address_id = -10;
//		InsertRecords.insertCreditCard(safe_store_username, cc_num, expire_date, cvv, address_id);
//		String sqlStmt = "SELECT * FROM CreditCards WHERE cc_num=" + cc_num;
//		try {  
//            Statement stmt = conn.createStatement();  
//            ResultSet cc = stmt.executeQuery(sqlStmt);
//            assertNotNull(cc);
//            assertTrue(cc.isLast(), "should only be one entry for each credit card");
//            assertEquals(cc.getString("safe_store_username"), safe_store_username);
//            //when no nickname inputed, nickname is set to last 4 digits of the card
//            assertEquals(cc.getString("cc_nickname"), "1234");
//            assertEquals(cc.getString("expire_date"), expire_date);
//            assertEquals(cc.getInt("cvv"), cvv);
//            assertEquals(cc.getInt("address_id"), address_id);
//        } catch (SQLException e) {  
//            System.out.println(e.getMessage());  
//        }  
	}
	
	@Test
	public void testDebitCardTableColumns() {
//		String[] debitCardColNames = {"safe_store_username", "dc_nickname", "dc_num", "expire_date", "cvv", "pin", "address_id"};
//		// all 12 column types are varchar, further discussion needed on this 
//		// all 4 column types are int 
//		Integer[] debitCardColTypes = {12, 12, 12, 12, 4, 4, 4};
//		
//		String sqlStmt = "SELECT * FROM DebitCards";
//		try {
//			Statement stmt = conn.createStatement();
//			stmt.execute(sqlStmt);
//			ResultSetMetaData debitCardCol = stmt.getResultSet().getMetaData();
//			assertEquals(debitCardCol.getColumnCount(), 7, "Debit Card Table should have 7 columns");
//			for(int i=0;i<debitCardColNames.length;i++) {
//				assertEquals(debitCardCol.getColumnName(i), debitCardColNames[i]);
//				assertEquals(debitCardCol.getColumnType(i), debitCardColTypes);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
	}
	
	@Test
	public void testInsertDebitCardWithNickname() {
//		String safe_store_username = "testUser";
//		String dc_nickname = "test nickname";
//		String dc_num = "1234123412341234";
//		String expire_date = "test expiration date";
//		int cvv = -1;
//		int pin = 1111;
//		int address_id = -10;
//		InsertRecords.insertDebitCard(safe_store_username, dc_nickname, dc_num, expire_date, cvv, pin, address_id);
//		String sqlStmt = "SELECT * FROM DebitCards WHERE dc_num=" + dc_num;
//		try {  
//            Statement stmt = conn.createStatement();  
//            ResultSet dc = stmt.executeQuery(sqlStmt);
//            assertNotNull(dc);
//            assertTrue(dc.isLast(), "should only be one entry for each debit card");
//            assertEquals(dc.getString("safe_store_username"), safe_store_username);
//            assertEquals(dc.getString("dc_nickname"), dc_nickname);
//            assertEquals(dc.getString("expire_date"), expire_date);
//            assertEquals(dc.getInt("cvv"), cvv);
//            assertEquals(dc.getInt("pin"), pin);
//            assertEquals(dc.getInt("address_id"), address_id);
//        } catch (SQLException e) {  
//            System.out.println(e.getMessage());  
//        }  
	}
	
	@Test
	public void testInsertDebitCardWithoutNickname() {
//		String safe_store_username = "testUser";
//		String dc_num = "1234123412341234";
//		String expire_date = "test expiration date";
//		int cvv = -1;
//		int pin = 1111;
//		int address_id = -10;
//		InsertRecords.insertDebitCard(safe_store_username, dc_num, expire_date, cvv, pin, address_id);
//		String sqlStmt = "SELECT * FROM DebitCards WHERE cc_num=" + dc_num;
//		try {  
//            Statement stmt = conn.createStatement();  
//            ResultSet dc = stmt.executeQuery(sqlStmt);
//            assertNotNull(dc);
//            assertTrue(dc.isLast(), "should only be one entry for each debit card");
//            assertEquals(dc.getString("safe_store_username"), safe_store_username);
//            assertEquals(dc.getString("dc_nickname"), "1234");
//            assertEquals(dc.getString("expire_date"), expire_date);
//            assertEquals(dc.getInt("cvv"), cvv);
//            assertEquals(dc.getInt("pin"), pin);
//            assertEquals(dc.getInt("address_id"), address_id);
//        } catch (SQLException e) {  
//            System.out.println(e.getMessage());  
//        }  
	}
	
	@Test
	public void testAddressTableColumns() {
//		String[] addressColNames = {"address_id", "street_address", "city", "state", "zip"};
//		// all 12 column types are varchar, further discussion needed on this 
//		// all 4 column types are int 
//		Integer[] addressColTypes = {4, 12, 12, 12, 4};
//		
//		String sqlStmt = "SELECT * FROM Address";
//		try {
//			Statement stmt = conn.createStatement();
//			stmt.execute(sqlStmt);
//			ResultSetMetaData addressCol = stmt.getResultSet().getMetaData();
//			assertEquals(addressCol.getColumnCount(), 5, "Address Table should have 5 columns");
//			for(int i=0;i<addressColNames.length;i++) {
//				assertEquals(addressCol.getColumnName(i), addressColNames[i]);
//				assertEquals(addressCol.getColumnType(i), addressColTypes);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}  
	}
	
	@Test
	public void testInsertAddress() {
//		int address_id = -10;
//		String street_address = "test address";
//		String city = "test city";
//		String state = "test state";
//		int zip = 11111;
//		InsertRecords.insertAddress(street_address, city, state, zip);
//		String sqlStmt = "SELECT * FROM Address WHERE address_id=" + address_id;
//		try {  
//            Statement stmt = conn.createStatement();  
//            ResultSet address = stmt.executeQuery(sqlStmt);
//            assertNotNull(address);
//            assertTrue(address.isLast(), "should only be one entry for each address_id");
//            assertEquals(address.getString("street_address"), street_address);
//            assertEquals(address.getString("city"), city);
//            assertEquals(address.getString("state"), state);
//            assertEquals(address.getInt("zip"), zip);
//        } catch (SQLException e) {  
//            System.out.println(e.getMessage());  
//        }  
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

package cardTests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import card.Address;
import card.CreditCard;
import encryption.Encryption;
import tables.AddressEntity;
import tables.CreditCardEntity;
import tables.UserEntity;
import user.User;

class creditCardTests {
	
	ConnectionSource databaseConnection;
	
	Address testAddress;
	
	UserEntity testUserEntity;
	User testUser;
	User testUserTwo;
	UserEntity testUserEntityTwo;
	
	Dao<AddressEntity, String> addressDao;
	Dao<UserEntity, String> userDao;
	
	@BeforeEach
    public void setUp() {
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			databaseConnection = new JdbcConnectionSource(databaseUrl);
			assertNotNull(databaseConnection, "Connection is null. Failed to Connect.");
			
			addressDao = DaoManager.createDao(databaseConnection, AddressEntity.class);
			
			testAddress = new Address("6843 kingsbury blvd", "STL", "MO", "63130");
			testAddress.addAddress(databaseConnection);
			
			testUserEntity = new UserEntity("username", "password", "salt");
			testUserEntityTwo = new UserEntity("usernametwo", "password", "salt");
			
			userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			userDao.create(testUserEntity);
			
			testUser = new User(testUserEntity);
			
			userDao.create(testUserEntityTwo);
			
			testUserTwo = new User(testUserEntityTwo);
			
		}
		catch (SQLException e) {
			fail("failed to connect to database.");
			e.printStackTrace();
		}
	}
	
	@AfterEach
	public void tearDown() {
		testAddress.deleteAddress(databaseConnection);
		try {
			userDao.delete(testUserEntity);
			userDao.delete(testUserEntityTwo);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testDefaultNickname() {
		System.out.println("RUNNING TEST: testDefaultNickname");
		CreditCard testCreditCard = new CreditCard(testUser, "4323 3249 2013 3232", "04/34", "123", testAddress);
		assertTrue(testCreditCard.getNickname().equals("3232"), "Default nickname was not the last four digits");
	}

	@Test
	void testAddCreditCardExistingAddressDefaultNickname() {
		System.out.println("RUNNING TEST: testAddCreditCardExistingAddressDefaultNickname");
		String encryptedCreditCardNumber = Encryption.encrypt("1234567890");
		CreditCard testCreditCard = new CreditCard(testUser, "1234567890", "04/23", "123", testAddress);
		try {
			UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
			ForeignCollection<CreditCardEntity> usersAssociatedCreditCards = returnedUser.getCreditCards();
			int initialNumCards = usersAssociatedCreditCards.size();
			
			boolean result = testCreditCard.addCard(databaseConnection);
			assertTrue(result, "Should return true if adding was successful");
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			
			Map<String, Object> addressQueryParams = new HashMap<String, Object>();
			addressQueryParams.put("street_address", Encryption.encrypt(testAddress.getStreetAddress()));
			addressQueryParams.put("city", Encryption.encrypt(testAddress.getCity()));
			addressQueryParams.put("state", Encryption.encrypt(testAddress.getState()));
			addressQueryParams.put("zip_code", Encryption.encrypt(testAddress.getZipCode()));
			
			try {
				List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(addressQueryParams);
				String errMsg = "Only one address should exist in the table with this particular street address, city, state, and zip code";
				assertTrue(returnedAddresses.size() == 1, errMsg);
				
				AddressEntity returnedAddressEntity = returnedAddresses.get(0);
				ForeignCollection<CreditCardEntity> associatedCreditCards = returnedAddressEntity.getCreditCards();
				assertTrue(associatedCreditCards.size() == 1, "Address should only have one card associated with it");
				
				returnedUser = userDao.queryForSameId(testUserEntity);
				usersAssociatedCreditCards = returnedUser.getCreditCards();
				int newNumCards = usersAssociatedCreditCards.size();
				assertTrue(initialNumCards + 1 == newNumCards, "User should have a credit card added to their collection");
				
				Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
				CreditCardEntity queriedCard = creditCardDao.queryForId(encryptedCreditCardNumber);
				CreditCard queriedCreditCard = new CreditCard(queriedCard);
				assertTrue(queriedCreditCard.getNickname().equals("7890"), "Nickname doesn't match");
				assertTrue(queriedCreditCard.getCardNumber().equals("1234567890"), "Credit card number doesn't match");
				assertTrue(queriedCreditCard.getExpirationDate().equals("04/23"), "Expiration date doesn't match");
				assertTrue(queriedCreditCard.getCvv().equals("123"), "CVV doesn't match");
				
				creditCardDao.delete(testCreditCardEntity);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Test
	void testAddCreditCardNewAddress() {
		System.out.println("RUNNING TEST: testAddCreditCardNewAddress");
		
		String encryptedCreditCardNumber = Encryption.encrypt("0000000000");
		Address newAddress = new Address("123 oak st", "STL", "MO", "63130");
		CreditCard testCreditCard = new CreditCard(testUser, "0000000000", "04/23", "123", newAddress);
		try {
			UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
			ForeignCollection<CreditCardEntity> usersAssociatedCreditCards = returnedUser.getCreditCards();
			int initialNumCards = usersAssociatedCreditCards.size();
			
			boolean result = testCreditCard.addCard(databaseConnection);
			assertTrue(result, "Should return true if add was successful");
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			
			Map<String, Object> addressQueryParams = new HashMap<String, Object>();
			addressQueryParams.put("street_address", Encryption.encrypt(newAddress.getStreetAddress()));
			addressQueryParams.put("city", Encryption.encrypt(newAddress.getCity()));
			addressQueryParams.put("state", Encryption.encrypt(newAddress.getState()));
			addressQueryParams.put("zip_code", Encryption.encrypt(newAddress.getZipCode()));
			try {
				List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(addressQueryParams);
				String errMsg = "Only one address should exist in the table with this particular street address, city, state, and zip code";
				assertTrue(returnedAddresses.size() == 1, errMsg);
				
				AddressEntity returnedAddressEntity = returnedAddresses.get(0);
				ForeignCollection<CreditCardEntity> associatedCreditCards = returnedAddressEntity.getCreditCards();
				assertTrue(associatedCreditCards.size() == 1, "Address should only have one card associated with it");
				
				returnedUser = userDao.queryForSameId(testUserEntity);
				usersAssociatedCreditCards = returnedUser.getCreditCards();
				int newNumCards = usersAssociatedCreditCards.size();
				assertTrue(initialNumCards + 1 == newNumCards, "User should have an added credit card to their collection");
				
				Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
				CreditCardEntity queriedCard = creditCardDao.queryForId(encryptedCreditCardNumber);
				CreditCard queriedCreditCard = new CreditCard(queriedCard);
				assertTrue(queriedCreditCard.getNickname().equals("0000"), "Nickname doesn't match");
				assertTrue(queriedCreditCard.getCardNumber().equals("0000000000"), "Credit card number doesn't match");
				assertTrue(queriedCreditCard.getExpirationDate().equals("04/23"), "Expiration date doesn't match");
				assertTrue(queriedCreditCard.getCvv().equals("123"), "CVV doesn't match");
				
				creditCardDao.delete(testCreditCardEntity);
				newAddress.deleteAddress(databaseConnection);
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	@Test
	void testAddCreditCardExistingNickname() {
		System.out.println("RUNNING TEST: testAddCreditCardExistingNickname");
		
		CreditCard testCreditCard = new CreditCard(testUser, "testNickname", "3333333333", "04/23", "123", testAddress);
		try {
			boolean firstAddResult = testCreditCard.addCard(databaseConnection);
			assertTrue(firstAddResult, "first time adding should be successful");
			CreditCardEntity firstCreditCard = testCreditCard.getCreditCardEntity();
			
			String encryptedCreditCardNumberTwo = Encryption.encrypt("0987654321");
			CreditCard testCreditCardTwo = new CreditCard(testUser, "testNickname", "0987654321", "04/23", "123", testAddress);
			boolean secondAddResult = testCreditCardTwo.addCard(databaseConnection);
			assertFalse(secondAddResult, "Second add should return false due to same nickname");
			
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			CreditCardEntity returnedCreditCard = creditCardDao.queryForId(encryptedCreditCardNumberTwo);
			assertTrue(returnedCreditCard == null, "The second credit card should not be in the database");
			
			creditCardDao.delete(firstCreditCard);
			if(returnedCreditCard != null) {
				creditCardDao.delete(returnedCreditCard);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testAddCreditCardExistingNicknameDifferentUser() {
		System.out.println("RUNNING TEST: testAddCreditCardExistingNicknameDifferentUser");
		CreditCard testCreditCard = new CreditCard(testUser, "testNickname", "5555555555", "04/23", "123", testAddress);
		try {
			boolean firstAddResult = testCreditCard.addCard(databaseConnection);
			assertTrue(firstAddResult, "first time adding should be successful");
			CreditCardEntity firstCreditCard = testCreditCard.getCreditCardEntity();
			
			String encryptedCreditCardNumberTwo = Encryption.encrypt("8888888888");
			CreditCard testCreditCardTwo = new CreditCard(testUserTwo, "testNickname", "8888888888", "04/23", "123", testAddress);
			boolean secondAddResult = testCreditCardTwo.addCard(databaseConnection);
			assertTrue(secondAddResult, "Second add should return true due to different user");
			
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			CreditCardEntity returnedCreditCard = creditCardDao.queryForId(encryptedCreditCardNumberTwo);
			assertFalse(returnedCreditCard == null, "The second credit card should be in the database");
			creditCardDao.delete(firstCreditCard);
			creditCardDao.delete(returnedCreditCard);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testStaticAddCreditCardDefaultNickname() {
		System.out.println("RUNNING TEST: testStaticAddCreditCardDefaultNickname");

		String encryptedCreditCardNumber = Encryption.encrypt("1234567890");
		String expectedNickname = "7890";
		String expectedExpDate = "04/23";
		String expectedCvv = "321";
		String expectedStreetAddress = "456 lazy rd";
		String expectedCity = "STL";
		String expectedState = "MO";
		String expectedZipCode = "54321";
		File file = new File("test/cardTests/addCardDefaultNicknameInput.txt");
		try {
			UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
			ForeignCollection<CreditCardEntity> usersAssociatedCreditCards = returnedUser.getCreditCards();
			int initialNumCards = usersAssociatedCreditCards.size();
			
			Scanner keyboard = new Scanner(file);
			boolean result = CreditCard.addCreditCard(databaseConnection, keyboard, testUser);
			
			assertTrue(result, "Should return true if credit card is added");
			
			Map<String, Object> addressQueryParams = new HashMap<String, Object>();
			addressQueryParams.put("street_address", Encryption.encrypt(expectedStreetAddress));
			addressQueryParams.put("city", Encryption.encrypt(expectedCity));
			addressQueryParams.put("state", Encryption.encrypt(expectedState));
			addressQueryParams.put("zip_code", Encryption.encrypt(expectedZipCode));
			
			try {
				List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(addressQueryParams);
				String errMsg = "Only one address should exist in the table with this particular street address, city, state, and zip code";
				assertTrue(returnedAddresses.size() == 1, errMsg);
				
				AddressEntity returnedAddressEntity = returnedAddresses.get(0);
				ForeignCollection<CreditCardEntity> associatedCreditCards = returnedAddressEntity.getCreditCards();
				assertTrue(associatedCreditCards.size() == 1, "Address should only have one card associated with it");
				
				returnedUser = userDao.queryForSameId(testUserEntity);
				usersAssociatedCreditCards = returnedUser.getCreditCards();
				int newNumCards = usersAssociatedCreditCards.size();
				assertTrue(initialNumCards + 1 == newNumCards, "User should have a credit card added to their collection");
				
				Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
				CreditCardEntity queriedCard = creditCardDao.queryForId(encryptedCreditCardNumber);
				CreditCard queriedCreditCard = new CreditCard(queriedCard);
				assertTrue(queriedCreditCard.getNickname().equals(expectedNickname), "Nickname doesn't match");
				assertTrue(queriedCreditCard.getCardNumber().equals("1234567890"), "Credit card number doesn't match");
				assertTrue(queriedCreditCard.getExpirationDate().equals(expectedExpDate), "Expiration date doesn't match");
				assertTrue(queriedCreditCard.getCvv().equals(expectedCvv), "CVV doesn't match");
				
				addressDao.delete(returnedAddressEntity);
				creditCardDao.delete(queriedCard);
			} catch (SQLException e) {
				e.printStackTrace();
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testStaticAddCreditCardWithNickname() {
		System.out.println("RUNNING TEST: testStaticAddCreditCardWithNickname");

		String encryptedCreditCardNumber = Encryption.encrypt("1234567890");
		String expectedExpDate = "03/24";
		String expectedCvv = "123";
		String expectedNickname = "CSR";
		String expectedStreetAddress = "123 cherry ln";
		String expectedCity = "STL";
		String expectedState = "MO";
		String expectedZipCode = "12345";
		File file = new File("test/cardTests/addCardWithNicknameInput.txt");
		try {
			UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
			ForeignCollection<CreditCardEntity> usersAssociatedCreditCards = returnedUser.getCreditCards();
			int initialNumCards = usersAssociatedCreditCards.size();
			
			Scanner keyboard = new Scanner(file);
			boolean result = CreditCard.addCreditCard(databaseConnection, keyboard, testUser);
			
			assertTrue(result, "Should return true if credit card is added");
			
			Map<String, Object> addressQueryParams = new HashMap<String, Object>();
			addressQueryParams.put("street_address", Encryption.encrypt(expectedStreetAddress));
			addressQueryParams.put("city", Encryption.encrypt(expectedCity));
			addressQueryParams.put("state", Encryption.encrypt(expectedState));
			addressQueryParams.put("zip_code", Encryption.encrypt(expectedZipCode));
			
			try {
				List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(addressQueryParams);
				String errMsg = "Only one address should exist in the table with this particular street address, city, state, and zip code";
				assertTrue(returnedAddresses.size() == 1, errMsg);
				
				AddressEntity returnedAddressEntity = returnedAddresses.get(0);
				ForeignCollection<CreditCardEntity> associatedCreditCards = returnedAddressEntity.getCreditCards();
				assertTrue(associatedCreditCards.size() == 1, "Address should only have one card associated with it");
				
				
				returnedUser = userDao.queryForSameId(testUserEntity);
				usersAssociatedCreditCards = returnedUser.getCreditCards();
				int newNumCards = usersAssociatedCreditCards.size();
				assertTrue(initialNumCards + 1 == newNumCards, "User should have a credit card added to their collection");
				
				Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
				CreditCardEntity queriedCard = creditCardDao.queryForId(encryptedCreditCardNumber);
				CreditCard queriedCreditCard = new CreditCard(queriedCard);
				assertTrue(queriedCreditCard.getNickname().equals(expectedNickname), "Nickname doesn't match");
				assertTrue(queriedCreditCard.getCardNumber().equals("1234567890"), "Credit card number doesn't match");
				assertTrue(queriedCreditCard.getExpirationDate().equals(expectedExpDate), "Expiration date doesn't match");
				assertTrue(queriedCreditCard.getCvv().equals(expectedCvv), "CVV doesn't match");
				
				addressDao.delete(returnedAddressEntity);
				creditCardDao.delete(queriedCard);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	void testGetCreditCardInformationAll() {
		String nickname = "testNickname";
		String cardNumber = "99998888877776666";
		String expirationDate = "04/23";
		String cvv = "987";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			String expectedOutput = testCreditCard.toString();
			try {
				File userInput = new File("test/cardTests/getAllInput.txt");
				Scanner keyboard = new Scanner(userInput);
				String output = CreditCard.getCreditCardInformation(databaseConnection, keyboard, testUser);
				assertTrue(output.equals(expectedOutput), "Expected: " + expectedOutput + " but got: " + output);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("Error with user input text file");
			}
			
			creditCardDao.delete(testCreditCardEntity);			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test 
	void testGetCreditCardInformaitonBillingAddress() {
		String nickname = "testNickname";
		String cardNumber = "99998888877776666";
		String expirationDate = "04/23";
		String cvv = "987";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			String expectedOutput = testCreditCard.getBillingAddress();
			try {
				File userInput = new File("test/cardTests/getBillingAddressInput.txt");
				Scanner keyboard = new Scanner(userInput);
				String output = CreditCard.getCreditCardInformation(databaseConnection, keyboard, testUser);
				assertTrue(output.equals(expectedOutput), "Expected: " + expectedOutput + " but got: " + output);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("Error with user input text file");
			}
			
			creditCardDao.delete(testCreditCardEntity);			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test
	void testGetCreditCardInformationCardNumber() {
		String nickname = "testNickname";
		String cardNumber = "99998888877776666";
		String expirationDate = "04/23";
		String cvv = "987";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			String expectedOutput = testCreditCard.getCardNumber();
			try {
				File userInput = new File("test/cardTests/getCardNumberInput.txt");
				Scanner keyboard = new Scanner(userInput);
				String output = CreditCard.getCreditCardInformation(databaseConnection, keyboard, testUser);
				assertTrue(output.equals(expectedOutput), "Expected: " + expectedOutput + " but got: " + output);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("Error with user input text file");
			}
			
			creditCardDao.delete(testCreditCardEntity);			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test 
	void testGetCreditCardInformationCvv() {
		String nickname = "testNickname";
		String cardNumber = "99998888877776666";
		String expirationDate = "04/23";
		String cvv = "987";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			String expectedOutput = testCreditCard.getCvv();
			try {
				File userInput = new File("test/cardTests/getCvvInput.txt");
				Scanner keyboard = new Scanner(userInput);
				String output = CreditCard.getCreditCardInformation(databaseConnection, keyboard, testUser);
				assertTrue(output.equals(expectedOutput), "Expected: " + expectedOutput + " but got: " + output);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("Error with user input text file");
			}
			
			creditCardDao.delete(testCreditCardEntity);			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test
	void testGetCreditCardInformationExpDate() {
		String nickname = "testNickname";
		String cardNumber = "99998888877776666";
		String expirationDate = "04/23";
		String cvv = "987";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			String expectedOutput = testCreditCard.getExpirationDate();
			try {
				File userInput = new File("test/cardTests/getExpDateInput.txt");
				Scanner keyboard = new Scanner(userInput);
				String output = CreditCard.getCreditCardInformation(databaseConnection, keyboard, testUser);
				assertTrue(output.equals(expectedOutput), "Expected: " + expectedOutput + " but got: " + output);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("Error with user input text file");
			}
			
			creditCardDao.delete(testCreditCardEntity);			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test
	void testGetCreditCardInformationZipCode() {
		String nickname = "testNickname";
		String cardNumber = "99998888877776666";
		String expirationDate = "04/23";
		String cvv = "987";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			String expectedOutput = testCreditCard.getZipCode();
			try {
				File userInput = new File("test/cardTests/getZipCodeInput.txt");
				Scanner keyboard = new Scanner(userInput);
				String output = CreditCard.getCreditCardInformation(databaseConnection, keyboard, testUser);
				assertTrue(output.equals(expectedOutput), "Expected: " + expectedOutput + " but got: " + output);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				fail("Error with user input text file");
			}
			
			creditCardDao.delete(testCreditCardEntity);			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}

}

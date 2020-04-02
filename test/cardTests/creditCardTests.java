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
			
			userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			userDao.create(testUserEntity);
			
			testUser = new User(testUserEntity);
			
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void testAddCreditCardExistingAddressDefaultNickname() {
		String encryptedCreditCardNumber = Encryption.encrypt("1234567890");
		CreditCard testCreditCard = new CreditCard(testUser, "1234567890", "04/23", "123", testAddress);
		try {
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
				
				UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
				ForeignCollection<CreditCardEntity> usersAssociatedCreditCards = returnedUser.getCreditCards();
				assertTrue(usersAssociatedCreditCards.size() == 1, "User should only have one card associated with it");
				
				Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
				CreditCardEntity queriedCard = creditCardDao.queryForId(encryptedCreditCardNumber);
				CreditCard queriedCreditCard = new CreditCard(queriedCard);
				assertTrue(queriedCreditCard.getCreditCardNumber().equals("1234567890"), "Credit card number doesn't match");
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
		String encryptedCreditCardNumber = Encryption.encrypt("1234567890");
		Address newAddress = new Address("123 oak st", "STL", "MO", "63130");
		CreditCard testCreditCard = new CreditCard(testUser, "1234567890", "04/23", "123", newAddress);
		try {
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
				
				UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
				ForeignCollection<CreditCardEntity> usersAssociatedCreditCards = returnedUser.getCreditCards();
				assertTrue(usersAssociatedCreditCards.size() == 1, "User should only have one card associated with it");
				
				Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
				CreditCardEntity queriedCard = creditCardDao.queryForId(encryptedCreditCardNumber);
				CreditCard queriedCreditCard = new CreditCard(queriedCard);
				assertTrue(queriedCreditCard.getCreditCardNumber().equals("1234567890"), "Credit card number doesn't match");
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
		String encryptedCreditCardNumber = Encryption.encrypt("1234567890");
		CreditCard testCreditCard = new CreditCard(testUser, "testNickname", "1234567890", "04/23", "123", testAddress);
		try {
			boolean firstAddResult = testCreditCard.addCard(databaseConnection);
			assertTrue(firstAddResult, "first time adding should be successful");
			CreditCardEntity firstCreditCard = testCreditCard.getCreditCardEntity();
			
			String encryptedCreditCardNumberTwo = Encryption.encrypt("0987654321");
			CreditCard testCreditCardTwo = new CreditCard(testUser, "testNickname", "0987654321", "04/23", "123", testAddress);
			boolean secondAddResult = testCreditCard.addCard(databaseConnection);
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
	void testStaticAddCreditCardDefaultNickname() {
		String encryptedCreditCardNumber = Encryption.encrypt("1234567890");
		String expectedExpDate = "04/23";
		String expectedCvv = "321";
		String expectedStreetAddress = "456 lazy rd";
		String expectedCity = "STL";
		String expectedState = "MO";
		String expectedZipCode = "54321";
		File file = new File("test/cardTests/addCardDefaultNicknameInput.txt");
		try {
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
				
				UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
				ForeignCollection<CreditCardEntity> usersAssociatedCreditCards = returnedUser.getCreditCards();
				assertTrue(usersAssociatedCreditCards.size() == 1, "User should only have one card associated with it");
				
				Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
				CreditCardEntity queriedCard = creditCardDao.queryForId(encryptedCreditCardNumber);
				CreditCard queriedCreditCard = new CreditCard(queriedCard);
				assertTrue(queriedCreditCard.getCreditCardNumber().equals("1234567890"), "Credit card number doesn't match");
				assertTrue(queriedCreditCard.getExpirationDate().equals(expectedExpDate), "Expiration date doesn't match");
				assertTrue(queriedCreditCard.getCvv().equals(expectedCvv), "CVV doesn't match");
				
				addressDao.delete(returnedAddressEntity);
				creditCardDao.delete(queriedCard);
			} catch (SQLException e) {
				e.printStackTrace();
			}	
			
		} catch (FileNotFoundException e) {
			System.out.println("Fiel not found");
			e.printStackTrace();
		}
	}
	
	@Test
	void testStaticAddCreditCardWithNickname() {
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
				
				UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
				ForeignCollection<CreditCardEntity> usersAssociatedCreditCards = returnedUser.getCreditCards();
				assertTrue(usersAssociatedCreditCards.size() == 1, "User should only have one card associated with it");
				
				Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
				CreditCardEntity queriedCard = creditCardDao.queryForId(encryptedCreditCardNumber);
				CreditCard queriedCreditCard = new CreditCard(queriedCard);
				assertTrue(queriedCreditCard.getCreditCardNumber().equals("1234567890"), "Credit card number doesn't match");
				assertTrue(queriedCreditCard.getExpirationDate().equals(expectedExpDate), "Expiration date doesn't match");
				assertTrue(queriedCreditCard.getCvv().equals(expectedCvv), "CVV doesn't match");
				
				addressDao.delete(returnedAddressEntity);
				creditCardDao.delete(queriedCard);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("FIle not found");
			e.printStackTrace();
		}
		
		
	}

}

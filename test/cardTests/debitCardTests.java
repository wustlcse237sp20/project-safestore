package cardTests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
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
import card.DebitCard;
import encryption.Encryption;
import tables.AddressEntity;
import tables.CreditCardEntity;
import tables.DebitCardEntity;
import tables.UserEntity;
import user.User;


class debitCardTests {

	ConnectionSource databaseConnection;

	Address testAddress;

	UserEntity testUserEntity;
	User testUser;
	User testUserTwo;
	UserEntity testUserEntityTwo;

	Dao<AddressEntity, String> addressDao;
	Dao<UserEntity, String> userDao;

	@BeforeEach
	void setUp() throws Exception {
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			databaseConnection = new JdbcConnectionSource(databaseUrl);
			assertNotNull(databaseConnection, "Connection is null. Failed to Connect.");

			addressDao = DaoManager.createDao(databaseConnection, AddressEntity.class);

			testAddress = new Address("6843 kingsbury blvd", "STL", "MO", "63130");
			testAddress.addAddress(databaseConnection);

			byte[] salt = new byte[5];
			testUserEntity = new UserEntity("username", "password", salt);
			testUserEntityTwo = new UserEntity("usernametwo", "password", salt);

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
		DebitCard testDebitCard = new DebitCard(testUser, "4323 3249 2013 3232", "04/34", "123", "1234", testAddress);
		assertTrue(testDebitCard.getNickname().equals("3232"), "Default nickname was not the last four digits");
	}
	@Test
	void testAddCreditCardExistingAddressDefaultNickname() {
		System.out.println("RUNNING TEST: testAddDebitCardExistingAddressDefaultNickname");
		String encryptedDebitCardNumber = Encryption.encrypt("1234567890");
		DebitCard testDebitCard = new DebitCard(testUser, "1234567890", "04/23", "123", "1234", testAddress);
		try {
			UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
			ForeignCollection<DebitCardEntity> usersAssociatedDebitCards = returnedUser.getDebitCards();
			int initialNumCards = usersAssociatedDebitCards.size();
			
			boolean result = testDebitCard.addCard(databaseConnection);
			assertTrue(result, "Should return true if adding was successful");
			DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();
			
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
				usersAssociatedDebitCards = returnedUser.getDebitCards();
				int newNumCards = usersAssociatedDebitCards.size();
				assertTrue(initialNumCards + 1 == newNumCards, "User should have a debit card added to their collection");
				
				Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
				DebitCardEntity queriedCard = debitCardDao.queryForId(encryptedDebitCardNumber);
				DebitCard queriedDebitCard = new DebitCard(queriedCard);
				assertTrue(queriedDebitCard.getNickname().equals("7890"), "Nickname doesn't match");
				assertTrue(queriedDebitCard.getCardNumber().equals("1234567890"), "Credit card number doesn't match");
				assertTrue(queriedDebitCard.getExpirationDate().equals("04/23"), "Expiration date doesn't match");
				assertTrue(queriedDebitCard.getCvv().equals("123"), "CVV doesn't match");
				assertTrue(queriedDebitCard.getPin().equals("1234"), "Pin doesn't match");
				debitCardDao.delete(testDebitCardEntity);
				
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
	void testStaticAddDebitCardDefaultNickname() {
		System.out.println("RUNNING TEST: testStaticAddDebitCardDefaultNickname");

		String encryptedDebitCardNumber = Encryption.encrypt("1234567890");
		String expectedNickname = "7890";
		String expectedExpDate = "04/23";
		String expectedCvv = "321";
		String expectedStreetAddress = "456 lazy rd";
		String expectedCity = "STL";
		String expectedState = "MO";
		String expectedZipCode = "54321";
		String expectedPin = "4321";
		
		File file = new File("test/cardTests/addDebitCardDefaultNicknameInput.txt");
		try {
			UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
			ForeignCollection<DebitCardEntity> usersAssociatedDebitCards = returnedUser.getDebitCards();
			int initialNumCards = usersAssociatedDebitCards.size();
			
			Scanner keyboard = new Scanner(file);
			boolean result = DebitCard.addDebitCard(databaseConnection, keyboard, testUser);
			
			assertTrue(result, "Should return true if debit card is added");
			
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
				ForeignCollection<DebitCardEntity> associatedDebitCards = returnedAddressEntity.getDebitCards();
				assertTrue(associatedDebitCards.size() == 1, "Address should only have one card associated with it");
				
				returnedUser = userDao.queryForSameId(testUserEntity);
				usersAssociatedDebitCards = returnedUser.getDebitCards();
				int newNumCards = usersAssociatedDebitCards.size();
				assertTrue(initialNumCards + 1 == newNumCards, "User should have a debit card added to their collection");
				
				Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
				DebitCardEntity queriedCard = debitCardDao.queryForId(encryptedDebitCardNumber);
				DebitCard queriedDebitCard = new DebitCard(queriedCard);
				assertTrue(queriedDebitCard.getNickname().equals(expectedNickname), "Nickname doesn't match");
				assertTrue(queriedDebitCard.getCardNumber().equals("1234567890"), "Credit card number doesn't match");
				assertTrue(queriedDebitCard.getExpirationDate().equals(expectedExpDate), "Expiration date doesn't match");
				assertTrue(queriedDebitCard.getCvv().equals(expectedCvv), "CVV doesn't match");
				assertTrue(queriedDebitCard.getPin().equals(expectedPin), "Pin doesn't match");
				
				addressDao.delete(returnedAddressEntity);
				debitCardDao.delete(queriedCard);
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

		String encryptedDebitCardNumber = Encryption.encrypt("1234567890");
		String expectedExpDate = "03/24";
		String expectedCvv = "123";
		String expectedNickname = "CSR";
		String expectedStreetAddress = "123 cherry ln";
		String expectedCity = "STL";
		String expectedState = "MO";
		String expectedZipCode = "12345";
		String expectedPin = "1234";
		
		File file = new File("test/cardTests/addDebitCardWithNicknameInput.txt");
		try {
			UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
			ForeignCollection<DebitCardEntity> usersAssociatedDebitCards = returnedUser.getDebitCards();
			int initialNumCards = usersAssociatedDebitCards.size();
			
			Scanner keyboard = new Scanner(file);
			boolean result = DebitCard.addDebitCard(databaseConnection, keyboard, testUser);
			
			assertTrue(result, "Should return true if debit card is added");
			
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
				ForeignCollection<DebitCardEntity> associatedDebitCards = returnedAddressEntity.getDebitCards();
				assertTrue(associatedDebitCards.size() == 1, "Address should only have one card associated with it");
				
				
				returnedUser = userDao.queryForSameId(testUserEntity);
				usersAssociatedDebitCards = returnedUser.getDebitCards();
				int newNumCards = usersAssociatedDebitCards.size();
				assertTrue(initialNumCards + 1 == newNumCards, "User should have a debit card added to their collection");
				
				Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
				DebitCardEntity queriedCard = debitCardDao.queryForId(encryptedDebitCardNumber);
				DebitCard queriedDebitCard = new DebitCard(queriedCard);
				assertTrue(queriedDebitCard.getNickname().equals(expectedNickname), "Nickname doesn't match");
				assertTrue(queriedDebitCard.getCardNumber().equals("1234567890"), "Credit card number doesn't match");
				assertTrue(queriedDebitCard.getExpirationDate().equals(expectedExpDate), "Expiration date doesn't match");
				assertTrue(queriedDebitCard.getCvv().equals(expectedCvv), "CVV doesn't match");
				assertTrue(queriedDebitCard.getPin().equals(expectedPin), "Pin doesn't match");
				
				addressDao.delete(returnedAddressEntity);
				debitCardDao.delete(queriedCard);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

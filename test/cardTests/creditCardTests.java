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
import card.DebitCard;
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
		CreditCard testCreditCard = new CreditCard(testUser, "blah", "5555555555", "04/23", "123", testAddress);
		try {
			boolean firstAddResult = testCreditCard.addCard(databaseConnection);
			assertTrue(firstAddResult, "first time adding should be successful");
			CreditCardEntity firstCreditCard = testCreditCard.getCreditCardEntity();
			
			String encryptedCreditCardNumberTwo = Encryption.encrypt("8888888888");
			CreditCard testCreditCardTwo = new CreditCard(testUserTwo, "blah", "8888888888", "04/23", "123", testAddress);
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
	void testGetCreditCardFromNickname() {
		String nickname = "nickyname";
		String cardNumber = "99998888877776666";
		String expirationDate = "04/23";
		String cvv = "987";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			String expectedOutput = testCreditCard.toString();
			
			CreditCard gottenCard;
			try {
				gottenCard = CreditCard.getCreditCardFromNickname(nickname, testUser, databaseConnection);
				String output = gottenCard.toString();
				assertTrue(output.equals(expectedOutput), "Expected: " + expectedOutput + " but got: " + output);
				
			} catch (Exception e) {
				e.printStackTrace();
				creditCardDao.delete(testCreditCardEntity);
				fail(e);
			}
				
			creditCardDao.delete(testCreditCardEntity);			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	
	@Test
	void testSetNickname() {
		String nickname = "oldNickname";
		String cardNumber = "20394203923482";
		String expirationDate = "02/24";
		String cvv = "666";
		CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
		CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			
			String newNickname = "newNickname";
			boolean result = testCreditCard.setNickname(newNickname, databaseConnection);
			assertTrue(result, "setting nickname should have returned true if successful");
		
			CreditCardEntity queriedCardEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");
			
			CreditCard queriedCard = new CreditCard(queriedCardEntity);
			String queriedCardNickname = queriedCard.getNickname();
			assertTrue(queriedCardNickname.equals(newNickname), "Nickname was not updated. Expected: " + newNickname + " but got: " + queriedCardNickname);
			
			creditCardDao.delete(testCreditCardEntity);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e);
		}

	}
	
	@Test
	void testSetCardNumber() {
		String nickname = "name";
		String cardNumber = "20394203923482";
		String expirationDate = "02/24";
		String cvv = "666";
		CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
		CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			
			String newCardNumber = "666666666666";
			boolean result = testCreditCard.setCardNumber(newCardNumber, databaseConnection);
			assertTrue(result, "setting card number should have returned true if successful");
		
			CreditCardEntity queriedCardEntity = creditCardDao.queryForId(Encryption.encrypt(newCardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database, number not updated");
			
			CreditCard queriedCard = new CreditCard(queriedCardEntity);
			String queriedCardNumber = queriedCard.getCardNumber();
			assertTrue(queriedCardNumber.equals(newCardNumber), "Card Number was not updated. Expected: " + newCardNumber + " but got: " + queriedCardNumber);
			
			CreditCardEntity queriedOldNum = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertTrue(queriedOldNum==null, "No card should exist with old number");
			
			creditCardDao.delete(queriedCardEntity);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e);
		}
	}
	
	@Test
	void testSetExpirationDate() {
		String nickname = "testNickname";
		String cardNumber = "23948029384029";
		String expirationDate = "02/24";
		String cvv = "666";
		CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
		CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			
			String newExpirationDate = "02/30";
			boolean result = testCreditCard.setExpirationDate(newExpirationDate, databaseConnection);
			assertTrue(result, "setting expiration date should have returned true if successful");
		
			CreditCardEntity queriedCardEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");
			
			CreditCard queriedCard = new CreditCard(queriedCardEntity);
			String queriedExpirationDate = queriedCard.getExpirationDate();
			assertTrue(queriedExpirationDate.equals(newExpirationDate), "Expiration was not updated. Expected: " + newExpirationDate + " but got: " + queriedExpirationDate);
			
			creditCardDao.delete(testCreditCardEntity);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	void testSetCvv() {
		String nickname = "testNickname";
		String cardNumber = "4350203980239857";
		String expirationDate = "02/24";
		String cvv = "666";
		CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
		CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			
			String newCvv = "888";
			boolean result = testCreditCard.setCvv(newCvv, databaseConnection);
			assertTrue(result, "setting CVV should have returned true if successful");
		
			CreditCardEntity queriedCardEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");
			
			CreditCard queriedCard = new CreditCard(queriedCardEntity);
			String queriedCvv = queriedCard.getCvv();
			assertTrue(queriedCvv.equals(newCvv), "CVV was not updated. Expected: " + newCvv + " but got: " + queriedCvv);
			
			creditCardDao.delete(testCreditCardEntity);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	void testSetBillingAddressOneCardAssociatedNewAddress() {
		String nickname = "ocana";
		String cardNumber = "679283928342085";
		String expirationDate = "02/24";
		String cvv = "666";
		Address oldBillingAddress = new Address("216 W 92nd st", "NYC", "NY", "10025");
		CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, oldBillingAddress);
		CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			
			Address newBillingAddress = new Address("6666 wash ave", "STL", "MO", "63130");
			String newBillingAddressString = newBillingAddress.getFullAddress();
			boolean result = testCreditCard.setBillingAddress(newBillingAddress, databaseConnection);
			assertTrue(result, "setting billing address should have returned true if successful");
		
			CreditCardEntity queriedCardEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");
			
			CreditCard queriedCard = new CreditCard(queriedCardEntity);
			String queriedBillingAddress = queriedCard.getBillingAddress();
			assertTrue(queriedBillingAddress.equals(newBillingAddressString), "Billing Address was not updated. Expected: " + newBillingAddressString + " but got: " + queriedBillingAddress);
			
			Map<String, Object> addressQueryParams = new HashMap<String, Object>();
			addressQueryParams.put("street_address", Encryption.encrypt("216 W 92nd st"));
			addressQueryParams.put("city", Encryption.encrypt("NYC"));
			addressQueryParams.put("state", Encryption.encrypt("NY"));
			addressQueryParams.put("zip_code", Encryption.encrypt("10025"));
			
			List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(addressQueryParams);
			assertTrue(returnedAddresses.size() == 0, "Should have deleted the old address");
			
			Map<String, Object> addressQueryParamsTwo = new HashMap<String, Object>();
			addressQueryParamsTwo.put("street_address", Encryption.encrypt("6666 wash ave"));
			addressQueryParamsTwo.put("city", Encryption.encrypt("STL"));
			addressQueryParamsTwo.put("state", Encryption.encrypt("MO"));
			addressQueryParamsTwo.put("zip_code", Encryption.encrypt("63130"));
			
			List<AddressEntity> returnedAddressesTwo = addressDao.queryForFieldValues(addressQueryParamsTwo);
			assertTrue(returnedAddressesTwo.size() == 1, "Should have added another row for new address");
			
			creditCardDao.delete(testCreditCardEntity);
			newBillingAddress.deleteAddress(databaseConnection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e);
		}
	}
	
	@Test
	void testSetBillingAddressOneCardAssociatedExistingAddress() {
		String nickname = "ocaea";
		String cardNumber = "84357934857220";
		String expirationDate = "02/24";
		String cvv = "666";
		Address oldBillingAddress = new Address("246 W 99nd st", "NYC", "NY", "10025");
		CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, oldBillingAddress);
		CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			
			String newBillingAddressString = testAddress.getFullAddress();
			boolean result = testCreditCard.setBillingAddress(testAddress, databaseConnection);
			assertTrue(result, "setting billing address should have returned true if successful");
		
			CreditCardEntity queriedCardEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");
			
			CreditCard queriedCard = new CreditCard(queriedCardEntity);
			String queriedBillingAddress = queriedCard.getBillingAddress();
			assertTrue(queriedBillingAddress.equals(newBillingAddressString), "Billing was not updated. Expected: " + newBillingAddressString + " but got: " + queriedBillingAddress);
			
			Map<String, Object> addressQueryParams = new HashMap<String, Object>();
			addressQueryParams.put("street_address", Encryption.encrypt("246 W 99nd st"));
			addressQueryParams.put("city", Encryption.encrypt("NYC"));
			addressQueryParams.put("state", Encryption.encrypt("NY"));
			addressQueryParams.put("zip_code", Encryption.encrypt("10025"));
			
			List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(addressQueryParams);
			assertTrue(returnedAddresses.size() == 0, "Should have deleted the old address");
			
			Map<String, Object> addressQueryParamsTwo = new HashMap<String, Object>();
			addressQueryParamsTwo.put("street_address", Encryption.encrypt(testAddress.getStreetAddress()));
			addressQueryParamsTwo.put("city", Encryption.encrypt(testAddress.getCity()));
			addressQueryParamsTwo.put("state", Encryption.encrypt(testAddress.getState()));
			addressQueryParamsTwo.put("zip_code", Encryption.encrypt(testAddress.getZipCode()));
			
			List<AddressEntity> returnedAddressesTwo = addressDao.queryForFieldValues(addressQueryParamsTwo);
			assertTrue(returnedAddressesTwo.size() == 1, "Should not have added another row for existing address");
			
			creditCardDao.delete(testCreditCardEntity);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e);
		}
	}
	
	@Test
	void testSetBillingAddressMultipleCardsAssociated() {
		String nickname = "mca";
		String cardNumber = "4238420394855993";
		String expirationDate = "02/24";
		String cvv = "666";
		Address oldBillingAddress = new Address("444 E 82nd st", "NYC", "NY", "10025");
		CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, oldBillingAddress);
		CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
		
		CreditCard testCreditCardTwo = new CreditCard(testUser, "nick", "8888777733331111", "04/32", "349", oldBillingAddress);
		CreditCardEntity testCreditCardEntityTwo = testCreditCardTwo.getCreditCardEntity();
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);
			creditCardDao.create(testCreditCardEntityTwo);
			
			Address newBillingAddress = new Address("6645 wash ave", "STL", "MO", "63130");
			String newBillingAddressString = newBillingAddress.getFullAddress();
			boolean result = testCreditCard.setBillingAddress(newBillingAddress, databaseConnection);
			assertTrue(result, "setting billing address should have returned true if successful");
		
			CreditCardEntity queriedCardEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");
			
			CreditCard queriedCard = new CreditCard(queriedCardEntity);
			String queriedBillingAddress = queriedCard.getBillingAddress();
			assertTrue(queriedBillingAddress.equals(newBillingAddressString), "Building Address was not updated. Expected: " + newBillingAddressString + " but got: " + queriedBillingAddress);
			
			Map<String, Object> addressQueryParams = new HashMap<String, Object>();
			addressQueryParams.put("street_address", Encryption.encrypt("444 E 82nd st"));
			addressQueryParams.put("city", Encryption.encrypt("NYC"));
			addressQueryParams.put("state", Encryption.encrypt("NY"));
			addressQueryParams.put("zip_code", Encryption.encrypt("10025"));
			
			List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(addressQueryParams);
			assertTrue(returnedAddresses.size() == 1, "Should not have deleted the old address since it is associated with another card");
			
			Map<String, Object> addressQueryParamsTwo = new HashMap<String, Object>();
			addressQueryParamsTwo.put("street_address", Encryption.encrypt("6645 wash ave"));
			addressQueryParamsTwo.put("city", Encryption.encrypt("STL"));
			addressQueryParamsTwo.put("state", Encryption.encrypt("MO"));
			addressQueryParamsTwo.put("zip_code", Encryption.encrypt("63130"));
			
			List<AddressEntity> returnedAddressesTwo = addressDao.queryForFieldValues(addressQueryParamsTwo);
			assertTrue(returnedAddressesTwo.size() == 1, "Should have added another row for new address");
			
			creditCardDao.delete(testCreditCardEntity);
			creditCardDao.delete(testCreditCardEntityTwo);
			newBillingAddress.deleteAddress(databaseConnection);
			oldBillingAddress.deleteAddress(databaseConnection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e);
		}
	}
	
	@Test
	void testUpdateCreditCardInformationUpdateNickname() {
		String nickname = "nick";
		String cardNumber = "92347109485193857";
		String expirationDate = "04/23";
		String cvv = "988";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);

			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: st address, 5: city, 6: state, 7: zip 
			String[] newInputs = {"name", "", "", "", "", "", "", ""};
			boolean result = CreditCard.updateCreditCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated credit card");
			
			CreditCardEntity queriedEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedEntity==null, "Should have found credit card in the database still");
			
			CreditCard queriedCreditCard = new CreditCard(queriedEntity);
			String gottenNickname = queriedCreditCard.getNickname();
			assertTrue(gottenNickname.equals("name"), "Nickname was not updated. Expected 'name' but got: " + gottenNickname);
			assertTrue(queriedCreditCard.getCardNumber().equals(cardNumber), "Card number did not remain the same. Expected '92347109485193857' but got: " + queriedCreditCard.getCardNumber());
			assertTrue(queriedCreditCard.getExpirationDate().equals(expirationDate), "Exp Date did not remain the same. Expected '04/23' but got: " + queriedCreditCard.getExpirationDate());
			assertTrue(queriedCreditCard.getCvv().equals(cvv), "Cvv did not remain the same. Expected '988' but got: " + queriedCreditCard.getCvv());
			
			creditCardDao.delete(testCreditCardEntity);			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}

	@Test
	void testUpdateCreditCardInformationUpdateCardNum() {
		String nickname = "card";
		String cardNumber = "1111111111111111";
		String expirationDate = "04/23";
		String cvv = "988";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);

			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: st address, 5: city, 6: state, 7: zip 
			String[] newInputs = {"", "8888888888888888", "", "", "", "", "", ""};
			boolean result = CreditCard.updateCreditCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated credit card");
			
			CreditCardEntity queriedEntity = creditCardDao.queryForId(Encryption.encrypt("8888888888888888"));
			assertFalse(queriedEntity==null, "card number not updated");
			
			CreditCard queriedCard = new CreditCard(queriedEntity);
			String queriedCardNumber = queriedCard.getCardNumber();
			assertTrue(queriedCardNumber.equals("8888888888888888"), "Card Number was not updated. Expected: 8888888888888888 but got: " + queriedCardNumber);
			assertTrue(queriedCard.getNickname().equals(nickname), "Nickname did not remain the same. Expected 'card' but got: " + queriedCard.getNickname());
			assertTrue(queriedCard.getExpirationDate().equals(expirationDate), "Card expiration date did not remain the same. Expected '04/23' but got: " + queriedCard.getExpirationDate());
			assertTrue(queriedCard.getCvv().equals(cvv), "Cvv did not remain the same. Expected '988' but got: " + queriedCard.getCvv());
			
			
			CreditCardEntity queriedOldNum = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertTrue(queriedOldNum==null, "No card should exist with old number");
			
			creditCardDao.delete(queriedEntity);
		
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test
	void testUpdateCreditCardInformationUpdateExpDate() {
		String nickname = "C1";
		String cardNumber = "48537459283001394";
		String expirationDate = "04/23";
		String cvv = "908";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);

			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: st address, 5: city, 6: state, 7: zip 
			String[] newInputs = {"", "", "04/30", "", "", "", "", ""};
			boolean result = CreditCard.updateCreditCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated credit card");
			
			CreditCardEntity queriedEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedEntity==null, "card not found in database");
			
			CreditCard queriedCard = new CreditCard(queriedEntity);
			String queriedExpDate= queriedCard.getExpirationDate();
			assertTrue(queriedExpDate.equals("04/30"), "Card Number was not updated. Expected: 04/30 but got: " + queriedExpDate);
			assertTrue(queriedCard.getCardNumber().equals(cardNumber), "Card number did not remain the same. Expected '48537459283001394' but got: " + queriedCard.getCardNumber());
			assertTrue(queriedCard.getNickname().equals(nickname), "Nickname did not remain the same. Expected 'C1' but got: " + queriedCard.getNickname());
			assertTrue(queriedCard.getCvv().equals(cvv), "Card CVV did not remain the same. Expected '908' but got: " + queriedCard.getCvv());
			
			creditCardDao.delete(queriedEntity);

			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test
	void testUpdateCreditCardInformationUpdateCvv() {
		String nickname = "7up";
		String cardNumber = "48537459283001394";
		String expirationDate = "04/23";
		String cvv = "908";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);

			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: st address, 5: city, 6: state, 7: zip 
			String[] newInputs = {"", "", "", "669", "", "", "", ""};
			boolean result = CreditCard.updateCreditCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated credit card");
			
			CreditCardEntity queriedEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedEntity==null, "card not found in database");
			
			CreditCard queriedCard = new CreditCard(queriedEntity);
			String queriedCvv= queriedCard.getCvv();
			assertTrue(queriedCvv.equals("669"), "Card Number was not updated. Expected: 669 but got: " + queriedCvv);
			assertTrue(queriedCard.getExpirationDate().equals(expirationDate), "Card Expiration date did not remain the same. Expected: 04/23 but got: " + queriedCard.getExpirationDate());
			assertTrue(queriedCard.getCardNumber().equals(cardNumber), "Card number did not remain the same. Expected '48537459283001394' but got: " + queriedCard.getCardNumber());
			assertTrue(queriedCard.getNickname().equals(nickname), "Nickname did not remain the same. Expected '7up' but got: " + queriedCard.getNickname());
			
			creditCardDao.delete(queriedEntity);
			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test
	void testUpdateCreditCardInformationUpdateBillingAddress() {
		String nickname = "ba";
		String cardNumber = "90230293875549203";
		String expirationDate = "04/23";
		String cvv = "908";
		try {
			CreditCard testCreditCard = new CreditCard(testUser, nickname, cardNumber, expirationDate, cvv, testAddress);
			CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(testCreditCardEntity);

			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: st address, 5: city, 6: state, 7: zip 
			String[] newInputs = {"", "", "", "", "999 9th st", "Miami", "FL", "23423"};
			boolean result = CreditCard.updateCreditCardInformation(nickname, databaseConnection, testUser,  newInputs);
			assertTrue(result, "Should have returned true if updated credit card");
			
			CreditCardEntity queriedEntity = creditCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedEntity==null, "card not found in database");
			
			CreditCard queriedCard = new CreditCard(queriedEntity);
			String queriedBillingAddress = queriedCard.getBillingAddress();
			Address expectedAddress = new Address("999 9th st", "Miami", "FL", "23423");
			String expectedAddressString = expectedAddress.getFullAddress();
			assertTrue(queriedBillingAddress.equals(expectedAddressString), "Card Number was not updated. Expected: " + expectedAddressString + "but got: " + queriedBillingAddress);
			assertTrue(queriedCard.getExpirationDate().equals(expirationDate), "Card Expiration date did not remain the same. Expected: 04/23 but got: " + queriedCard.getExpirationDate());
			assertTrue(queriedCard.getCardNumber().equals(cardNumber), "Card number did not remain the same. Expected '48537459283001394' but got: " + queriedCard.getCardNumber());
			assertTrue(queriedCard.getNickname().equals(nickname), "Nickname did not remain the same. Expected '7up' but got: " + queriedCard.getNickname());
			assertTrue(queriedCard.getCvv().equals(cvv), "Card CVV did not remain the same. Expected '908' but got: " + queriedCard.getCvv());
			
			
			expectedAddress.updateToExistingAddress(databaseConnection);
			expectedAddress.deleteAddress(databaseConnection);
			
			creditCardDao.delete(queriedEntity);
			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test
	void testCardNicknameIsUnique() {
		System.out.println("RUNNING TEST: testCardNicknameIsUnique");
		CreditCard testCreditCard = new CreditCard(testUser, "testNickname", "3333333333", "04/23", "123", testAddress);
		try {
			boolean firstAddResult = testCreditCard.addCard(databaseConnection);
			assertTrue(firstAddResult, "first time adding should be successful");
			CreditCardEntity firstCreditCard = testCreditCard.getCreditCardEntity();
			
			boolean checkSameNickname = CreditCard.cardNicknameIsUnique(databaseConnection, testUser.getUserEntity(), "testNickname");
			assertFalse(checkSameNickname, "The nicknames are the same so it should return false");
			
			boolean checkDifferentNickname = CreditCard.cardNicknameIsUnique(databaseConnection, testUser.getUserEntity(), "testNickname1");
			assertTrue(checkDifferentNickname, "The nicknames are different so it should return true");
			
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.delete(firstCreditCard);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}

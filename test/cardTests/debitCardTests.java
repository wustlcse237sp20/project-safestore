package cardTests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import card.Address;
import card.DebitCard;
import encryption.Encryption;
import tables.AddressEntity;
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
	void testAddDebitCardExistingAddressDefaultNickname() {
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
				ForeignCollection<DebitCardEntity> associatedDebitCards = returnedAddressEntity.getDebitCards();
				assertTrue(associatedDebitCards.size() == 1, "Address should only have one card associated with it");

				returnedUser = userDao.queryForSameId(testUserEntity);
				usersAssociatedDebitCards = returnedUser.getDebitCards();
				int newNumCards = usersAssociatedDebitCards.size();
				assertTrue(initialNumCards + 1 == newNumCards, "User should have a debit card added to their collection");

				Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
				DebitCardEntity queriedCard = debitCardDao.queryForId(encryptedDebitCardNumber);
				DebitCard queriedDebitCard = new DebitCard(queriedCard);
				assertTrue(queriedDebitCard.getNickname().equals("7890"), "Nickname doesn't match");
				assertTrue(queriedDebitCard.getCardNumber().equals("1234567890"), "Debit card number doesn't match");
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
	void testAddDebitCardExistingNicknameDifferentUser() {
		System.out.println("RUNNING TEST: testAddDebitCardExistingNicknameDifferentUser");
		DebitCard testDebitCard = new DebitCard(testUser, "blah", "5555555555", "04/23", "123", "321", testAddress);
		try {
			boolean firstAddResult = testDebitCard.addCard(databaseConnection);
			assertTrue(firstAddResult, "first time adding should be successful");
			DebitCardEntity firstDebititCard = testDebitCard.getDebitCardEntity();
			
			String encryptedDebitCardNumberTwo = Encryption.encrypt("8888888888");
			DebitCard testDebitCardTwo = new DebitCard(testUserTwo, "blah", "8888888888", "04/23", "123", "321", testAddress);
			boolean secondAddResult = testDebitCardTwo.addCard(databaseConnection);
			assertTrue(secondAddResult, "Second add should return true due to different user");
			
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			DebitCardEntity returnedDebitCard = debitCardDao.queryForId(encryptedDebitCardNumberTwo);
			assertFalse(returnedDebitCard == null, "The second credit card should be in the database");
			debitCardDao.delete(firstDebititCard);
			debitCardDao.delete(returnedDebitCard);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testAddDebitCardNewAddress() {
		System.out.println("RUNNING TEST: testAddDebitCardNewAddress");
		
		String encryptedDebitCardNumber = Encryption.encrypt("0000000000");
		Address newAddress = new Address("123 oak st", "STL", "MO", "63130");
		DebitCard testDebitCard = new DebitCard(testUser, "0000000000", "04/23", "123", "321", newAddress);
		try {
			UserEntity returnedUser = userDao.queryForSameId(testUserEntity);
			ForeignCollection<DebitCardEntity> usersAssociatedDebitCards = returnedUser.getDebitCards();
			int initialNumCards = usersAssociatedDebitCards.size();
			
			boolean result = testDebitCard.addCard(databaseConnection);
			assertTrue(result, "Should return true if add was successful");
			DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();
			
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
				ForeignCollection<DebitCardEntity> associatedDebitCards = returnedAddressEntity.getDebitCards();
				assertTrue(associatedDebitCards.size() == 1, "Address should only have one card associated with it");
				
				returnedUser = userDao.queryForSameId(testUserEntity);
				usersAssociatedDebitCards = returnedUser.getDebitCards();
				int newNumCards = usersAssociatedDebitCards.size();
				assertTrue(initialNumCards + 1 == newNumCards, "User should have an added debit card to their collection");
				
				Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
				DebitCardEntity queriedCard = debitCardDao.queryForId(encryptedDebitCardNumber);
				DebitCard queriedDebitCard = new DebitCard(queriedCard);
				assertTrue(queriedDebitCard.getNickname().equals("0000"), "Nickname doesn't match");
				assertTrue(queriedDebitCard.getCardNumber().equals("0000000000"), "Credit card number doesn't match");
				assertTrue(queriedDebitCard.getExpirationDate().equals("04/23"), "Expiration date doesn't match");
				assertTrue(queriedDebitCard.getCvv().equals("123"), "CVV doesn't match");
				
				debitCardDao.delete(testDebitCardEntity);
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
	void testGetDebitCardFromNickname() {
		String nickname = "nickyname";
		String cardNumber = "99998888877776666";
		String expirationDate = "04/23";
		String cvv = "987";
		String pin = "6789";
		try {
			DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
			DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);
			String expectedOutput = testDebitCard.toString();

			DebitCard gottenCard;
			try {
				gottenCard = DebitCard.getDebitCardFromNickname(nickname, testUser, databaseConnection);
				String output = gottenCard.toString();
				assertTrue(output.equals(expectedOutput), "Expected: " + expectedOutput + " but got: " + output);

			} catch (Exception e) {
				e.printStackTrace();
				debitCardDao.delete(testDebitCardEntity);
				fail(e);
			}

			debitCardDao.delete(testDebitCardEntity);
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
		String pin = "6789";
		DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv,pin, testAddress);
		DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();

		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);

			String newNickname = "newNickname";
			boolean result = testDebitCard.setNickname(newNickname, databaseConnection);
			assertTrue(result, "setting nickname should have returned true if successful");

			DebitCardEntity queriedCardEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");

			DebitCard queriedCard = new DebitCard(queriedCardEntity);
			String queriedCardNickname = queriedCard.getNickname();
			assertTrue(queriedCardNickname.equals(newNickname), "Nickname was not updated. Expected: " + newNickname + " but got: " + queriedCardNickname);

			debitCardDao.delete(testDebitCardEntity);
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
		String pin = "6789";
		DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
		DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();

		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);

			String newCardNumber = "666666666666";
			boolean result = testDebitCard.setCardNumber(newCardNumber, databaseConnection);
			assertTrue(result, "setting card number should have returned true if successful");

			DebitCardEntity queriedCardEntity = debitCardDao.queryForId(Encryption.encrypt(newCardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database, number not updated");

			DebitCard queriedCard = new DebitCard(queriedCardEntity);
			String queriedCardNumber = queriedCard.getCardNumber();
			assertTrue(queriedCardNumber.equals(newCardNumber), "Card Number was not updated. Expected: " + newCardNumber + " but got: " + queriedCardNumber);

			DebitCardEntity queriedOldNum = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertTrue(queriedOldNum==null, "No card should exist with old number");

			debitCardDao.delete(queriedCardEntity);
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
		String pin = "6789";
		DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
		DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();

		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);

			String newExpirationDate = "02/30";
			boolean result = testDebitCard.setExpirationDate(newExpirationDate, databaseConnection);
			assertTrue(result, "setting expiration date should have returned true if successful");

			DebitCardEntity queriedCardEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");

			DebitCard queriedCard = new DebitCard(queriedCardEntity);
			String queriedExpirationDate = queriedCard.getExpirationDate();
			assertTrue(queriedExpirationDate.equals(newExpirationDate), "Expiration was not updated. Expected: " + newExpirationDate + " but got: " + queriedExpirationDate);

			debitCardDao.delete(testDebitCardEntity);
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
		String pin = "6789";
		DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
		DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();

		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);

			String newCvv = "888";
			boolean result = testDebitCard.setCvv(newCvv, databaseConnection);
			assertTrue(result, "setting CVV should have returned true if successful");

			DebitCardEntity queriedCardEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");

			DebitCard queriedCard = new DebitCard(queriedCardEntity);
			String queriedCvv = queriedCard.getCvv();
			assertTrue(queriedCvv.equals(newCvv), "CVV was not updated. Expected: " + newCvv + " but got: " + queriedCvv);

			debitCardDao.delete(testDebitCardEntity);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	void testSetPin() {
		String nickname = "testNickname";
		String cardNumber = "4350203980239857";
		String expirationDate = "02/24";
		String cvv = "666";
		String pin = "6789";
		
		DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
		DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();

		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);

			String newPin = "8888";
			boolean result = testDebitCard.setPin(newPin, databaseConnection);
			assertTrue(result, "setting Pin should have returned true if successful");

			DebitCardEntity queriedCardEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");

			DebitCard queriedCard = new DebitCard(queriedCardEntity);
			String queriedPin = queriedCard.getPin();
			assertTrue(queriedPin.equals(newPin), "Pin was not updated. Expected: " + newPin + " but got: " + queriedPin);

			debitCardDao.delete(testDebitCardEntity);
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
		String pin = "6789";
		
		Address oldBillingAddress = new Address("216 W 92nd st", "NYC", "NY", "10025");
		DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, oldBillingAddress);
		DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();

		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);

			Address newBillingAddress = new Address("6666 wash ave", "STL", "MO", "63130");
			String newBillingAddressString = newBillingAddress.getFullAddress();
			boolean result = testDebitCard.setBillingAddress(newBillingAddress, databaseConnection);
			assertTrue(result, "setting billing address should have returned true if successful");

			DebitCardEntity queriedCardEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");

			DebitCard queriedCard = new DebitCard(queriedCardEntity);
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

			debitCardDao.delete(testDebitCardEntity);
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
		String pin ="6789";
		Address oldBillingAddress = new Address("246 W 99nd st", "NYC", "NY", "10025");
		DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, oldBillingAddress);
		DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();

		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);

			String newBillingAddressString = testAddress.getFullAddress();
			boolean result = testDebitCard.setBillingAddress(testAddress, databaseConnection);
			assertTrue(result, "setting billing address should have returned true if successful");

			DebitCardEntity queriedCardEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");

			DebitCard queriedCard = new DebitCard(queriedCardEntity);
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

			debitCardDao.delete(testDebitCardEntity);
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
		String pin = "6789";
		Address oldBillingAddress = new Address("444 E 82nd st", "NYC", "NY", "10025");
		DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, oldBillingAddress);
		DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();

		DebitCard testDebitCardTwo = new DebitCard(testUser, "nick", "8888777733331111", "04/32", "349", oldBillingAddress);
		DebitCardEntity testDebitCardEntityTwo = testDebitCardTwo.getDebitCardEntity();

		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);
			debitCardDao.create(testDebitCardEntityTwo);

			Address newBillingAddress = new Address("6645 wash ave", "STL", "MO", "63130");
			String newBillingAddressString = newBillingAddress.getFullAddress();
			boolean result = testDebitCard.setBillingAddress(newBillingAddress, databaseConnection);
			assertTrue(result, "setting billing address should have returned true if successful");

			DebitCardEntity queriedCardEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedCardEntity == null, "card should be found in database");

			DebitCard queriedCard = new DebitCard(queriedCardEntity);
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

			debitCardDao.delete(testDebitCardEntity);
			debitCardDao.delete(testDebitCardEntityTwo);
			newBillingAddress.deleteAddress(databaseConnection);
			oldBillingAddress.deleteAddress(databaseConnection);
		} catch (SQLException e) {
			e.printStackTrace();
			fail(e);
		}
	}

	@Test
	void testUpdateDebitCardInformationUpdateNickname() {
		String nickname = "nick";
		String cardNumber = "92347109485193857";
		String expirationDate = "04/23";
		String cvv = "988";
		String pin ="6789";
		try {
			DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv,pin, testAddress);
			DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);
				
			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: pin, 5: st address, 6: city, 7: state, 8: zip 
			String[] newInputs = {"name", "", "","", "", "","", "", ""};
			boolean result = DebitCard.updateDebitCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated debit card");

			DebitCardEntity queriedEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedEntity==null, "Should have found debit card in the database still");

			DebitCard queriedDebitCard = new DebitCard(queriedEntity);
			String gottenNickname = queriedDebitCard.getNickname();
			assertTrue(gottenNickname.equals("name"), "Nickname was not updated. Expected 'name' but got: " + gottenNickname);
			assertTrue(queriedDebitCard.getCardNumber().equals(cardNumber), "Card number did not remain the same. Expected '92347109485193857' but got: " + queriedDebitCard.getCardNumber());
			assertTrue(queriedDebitCard.getExpirationDate().equals(expirationDate), "Exp Date did not remain the same. Expected '04/23' but got: " + queriedDebitCard.getExpirationDate());
			assertTrue(queriedDebitCard.getCvv().equals(cvv), "Cvv did not remain the same. Expected '988' but got: " + queriedDebitCard.getCvv());
			assertTrue(queriedDebitCard.getPin().equals(pin), "Pin did not remain the same. Expected '6789' but got: " + queriedDebitCard.getPin());

			debitCardDao.delete(testDebitCardEntity);			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}

	@Test
	void testUpdateDebitCardInformationUpdateCardNum() {
		String nickname = "card";
		String cardNumber = "1111111111111111";
		String expirationDate = "04/23";
		String cvv = "988";
		String pin ="6789";
		
		try {
			DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
			DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);
			
			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: pin, 5: st address, 6: city, 7: state, 8: zip 
			String[] newInputs = {"", "8888888888888888", "", "", "", "", "", "", ""};
			boolean result = DebitCard.updateDebitCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated debit card");

			DebitCardEntity queriedEntity = debitCardDao.queryForId(Encryption.encrypt("8888888888888888"));
			assertFalse(queriedEntity==null, "card number not updated");

			DebitCard queriedCard = new DebitCard(queriedEntity);
			String queriedCardNumber = queriedCard.getCardNumber();
			assertTrue(queriedCardNumber.equals("8888888888888888"), "Card Number was not updated. Expected: 8888888888888888 but got: " + queriedCardNumber);
			assertTrue(queriedCard.getNickname().equals(nickname), "Nickname did not remain the same. Expected 'card' but got: " + queriedCard.getNickname());
			assertTrue(queriedCard.getExpirationDate().equals(expirationDate), "Card expiration date did not remain the same. Expected '04/23' but got: " + queriedCard.getExpirationDate());
			assertTrue(queriedCard.getCvv().equals(cvv), "Cvv did not remain the same. Expected '988' but got: " + queriedCard.getCvv());
			assertTrue(queriedCard.getPin().equals(pin), "Pin did not remain the same. Expected '6789' but got: " + queriedCard.getPin());
			
			DebitCardEntity queriedOldNum = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertTrue(queriedOldNum==null, "No card should exist with old number");

			debitCardDao.delete(queriedEntity);

		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}

	@Test
	void testUpdateDebitCardInformationUpdateExpDate() {
		String nickname = "C1";
		String cardNumber = "48537459283001394";
		String expirationDate = "04/23";
		String cvv = "908";
		String pin = "6789";
		try {
			DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
			DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);
			
			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: pin, 5: st address, 6: city, 7: state, 8: zip 
			String[] newInputs = {"", "", "04/30", "", "", "", "", "", ""};
			boolean result = DebitCard.updateDebitCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated debit card");

			DebitCardEntity queriedEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedEntity==null, "card not found in database");

			DebitCard queriedCard = new DebitCard(queriedEntity);
			String queriedExpDate= queriedCard.getExpirationDate();
			assertTrue(queriedExpDate.equals("04/30"), "Expiration date was not updated. Expected: 04/30 but got: " + queriedExpDate);
			assertTrue(queriedCard.getCardNumber().equals(cardNumber), "Card number did not remain the same. Expected '48537459283001394' but got: " + queriedCard.getCardNumber());
			assertTrue(queriedCard.getNickname().equals(nickname), "Nickname did not remain the same. Expected 'C1' but got: " + queriedCard.getNickname());
			assertTrue(queriedCard.getCvv().equals(cvv), "Card CVV did not remain the same. Expected '908' but got: " + queriedCard.getCvv());
			assertTrue(queriedCard.getPin().equals(pin), "Card pin did not remain the same. Expected '6789' but got: " + queriedCard.getPin());

			debitCardDao.delete(queriedEntity);

		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}

	@Test
	void testUpdateDebitCardInformationUpdateCvv() {
		String nickname = "7up";
		String cardNumber = "48537459283001394";
		String expirationDate = "04/23";
		String cvv = "908";
		String pin = "6789";
		try {

			DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
			DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);

			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: pin, 5: st address, 6: city, 7: state, 8: zip 
			String[] newInputs = {"", "", "", "669", "", "", "", "", ""};
			boolean result = DebitCard.updateDebitCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated debit card");

			DebitCardEntity queriedEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedEntity==null, "card not found in database");

			DebitCard queriedCard = new DebitCard(queriedEntity);
			String queriedCvv= queriedCard.getCvv();
			assertTrue(queriedCvv.equals("669"), "CVV was not updated. Expected: 669 but got: " + queriedCvv);
			assertTrue(queriedCard.getExpirationDate().equals(expirationDate), "Card Expiration date did not remain the same. Expected: 04/23 but got: " + queriedCard.getExpirationDate());
			assertTrue(queriedCard.getCardNumber().equals(cardNumber), "Card number did not remain the same. Expected '48537459283001394' but got: " + queriedCard.getCardNumber());
			assertTrue(queriedCard.getNickname().equals(nickname), "Nickname did not remain the same. Expected '7up' but got: " + queriedCard.getNickname());
			assertTrue(queriedCard.getPin().equals(pin), "Card pin did not remain the same. Expected '6789' but got: " + queriedCard.getPin());

			

			debitCardDao.delete(queriedEntity);

		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}

	@Test
	void testUpdateDebitCardInformationUpdatePin() {
		String nickname = "8up";
		String cardNumber = "48537459283001394";
		String expirationDate = "04/23";
		String cvv = "908";
		String pin = "6789";
		try {

			DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
			DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);
	
			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: pin, 5: st address, 6: city, 7: state, 8: zip 
			String[] newInputs = {"", "", "", "", "1111", "", "", "", ""};
			boolean result = DebitCard.updateDebitCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated debit card");

			DebitCardEntity queriedEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedEntity==null, "card not found in database");

			DebitCard queriedCard = new DebitCard(queriedEntity);
			String queriedPin= queriedCard.getPin();
			assertTrue(queriedPin.equals("1111"), "Pin was not updated. Expected: 6699 but got: " + queriedPin);
			assertTrue(queriedCard.getExpirationDate().equals(expirationDate), "Card Expiration date did not remain the same. Expected: 04/23 but got: " + queriedCard.getExpirationDate());
			assertTrue(queriedCard.getCardNumber().equals(cardNumber), "Card number did not remain the same. Expected '48537459283001394' but got: " + queriedCard.getCardNumber());
			assertTrue(queriedCard.getNickname().equals(nickname), "Nickname did not remain the same. Expected '7up' but got: " + queriedCard.getNickname());
			assertTrue(queriedCard.getCvv().equals(cvv), "Card CVV did not remain the same. Expected '908' but got: " + queriedCard.getCvv());

			debitCardDao.delete(queriedEntity);

		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	@Test
	void testUpdateDebitCardInformationUpdateBillingAddress() {
		String nickname = "ba";
		String cardNumber = "90230293875549203";
		String expirationDate = "04/23";
		String cvv = "908";
		String pin = "6789";

		try {
			DebitCard testDebitCard = new DebitCard(testUser, nickname, cardNumber, expirationDate, cvv, pin, testAddress);
			DebitCardEntity testDebitCardEntity = testDebitCard.getDebitCardEntity();
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.create(testDebitCardEntity);

			String stAddress = "6820 Redwood Ave";
			String city = "Redwood City";
			String state = "CA";
			String zip = "11111";
			
			//0: nickname, 1: card number, 2: expiration date, 3: cvv, 4: pin, 5: st address, 6: city, 7: state, 8: zip 
			String[] newInputs = {"", "", "", "", "", stAddress, city, state, zip};
			boolean result = DebitCard.updateDebitCardInformation(nickname, databaseConnection, testUser, newInputs);
			assertTrue(result, "Should have returned true if updated debit card");

			DebitCardEntity queriedEntity = debitCardDao.queryForId(Encryption.encrypt(cardNumber));
			assertFalse(queriedEntity==null, "card not found in database");

			DebitCard queriedCard = new DebitCard(queriedEntity);
			String queriedBillingAddress = queriedCard.getBillingAddress();
			Address expectedAddress = new Address(stAddress, city, state, zip);
			String expectedAddressString = expectedAddress.getFullAddress();
			assertTrue(queriedBillingAddress.equals(expectedAddressString), "Card Address was not updated. Expected: " + expectedAddressString + "but got: " + queriedBillingAddress);
			assertTrue(queriedCard.getExpirationDate().equals(expirationDate), "Card Expiration date did not remain the same. Expected: 04/23 but got: " + queriedCard.getExpirationDate());
			assertTrue(queriedCard.getCardNumber().equals(cardNumber), "Card number did not remain the same. Expected '48537459283001394' but got: " + queriedCard.getCardNumber());
			assertTrue(queriedCard.getNickname().equals(nickname), "Nickname did not remain the same. Expected '7up' but got: " + queriedCard.getNickname());
			assertTrue(queriedCard.getCvv().equals(cvv), "Card CVV did not remain the same. Expected '908' but got: " + queriedCard.getCvv());
			assertTrue(queriedCard.getPin().equals(pin), "Card pin did not remain the same. Expected '6789' but got: " + queriedCard.getPin());
			
			expectedAddress.updateToExistingAddress(databaseConnection);
			expectedAddress.deleteAddress(databaseConnection);

			debitCardDao.delete(queriedEntity);
			
		} catch (SQLException e) {			
			e.printStackTrace();
			fail("Error inserting test card (Database failure)");
		}
	}
	
	@Test
	void testAddDebitCardExistingNickname() {
		System.out.println("RUNNING TEST: testAddDebitCardExistingNickname");
		
		DebitCard testDebitCard = new DebitCard(testUser, "testNickname", "3333333333", "04/23", "123", "1234",testAddress);
		try {
			boolean firstAddResult = testDebitCard.addCard(databaseConnection);
			assertTrue(firstAddResult, "first time adding should be successful");
			DebitCardEntity firstDebitCard = testDebitCard.getDebitCardEntity();
			
			String encryptedDebitCardNumberTwo = Encryption.encrypt("0987654321");
			DebitCard testDebitCardTwo = new DebitCard(testUser, "testNickname", "0987654321", "04/23", "123", "0123", testAddress);
			boolean secondAddResult = testDebitCardTwo.addCard(databaseConnection);
			assertFalse(secondAddResult, "Second add should return false due to same nickname");
			
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			DebitCardEntity returnedDebitCard = debitCardDao.queryForId(encryptedDebitCardNumberTwo);
			assertTrue(returnedDebitCard == null, "The second credit card should not be in the database");
			
			debitCardDao.delete(firstDebitCard);
			if(returnedDebitCard != null) {
				debitCardDao.delete(returnedDebitCard);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	void testCardNicknameIsUnique() {
		System.out.println("RUNNING TEST: testCardNicknameIsUnique");
		DebitCard testDebitCard = new DebitCard(testUser, "testNickname", "3333333333", "04/23", "123", "1234", testAddress);
		try {
			boolean firstAddResult = testDebitCard.addCard(databaseConnection);
			assertTrue(firstAddResult, "first time adding should be successful");
			DebitCardEntity firstDebitCard = testDebitCard.getDebitCardEntity();
			
			boolean checkSameNickname = DebitCard.cardNicknameIsUnique(databaseConnection, testUser.getUserEntity(), "testNickname");
			assertFalse(checkSameNickname, "The nicknames are the same so it should return false");
			
			boolean checkDifferentNickname = DebitCard.cardNicknameIsUnique(databaseConnection, testUser.getUserEntity(), "testNickname1");
			assertTrue(checkDifferentNickname, "The nicknames are different so it should return true");
			
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			debitCardDao.delete(firstDebitCard);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}



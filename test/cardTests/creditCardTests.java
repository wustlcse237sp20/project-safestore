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
		testCreditCard.addCard(databaseConnection);
		CreditCardEntity testCreditCardEntity = testCreditCard.getCreditCardEntity();
		
		Map<String, Object> addressQueryParams = new HashMap<String, Object>();
		addressQueryParams.put("street_address", Encryption.encrypt(testAddress.getStreetAddress()));
		addressQueryParams.put("city", Encryption.encrypt(testAddress.getCity()));
		addressQueryParams.put("state", Encryption.encrypt(testAddress.getState()));
		addressQueryParams.put("zip_code", Encryption.encrypt(testAddress.getZipCode()));
		
		try {
			List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(addressQueryParams);
			System.out.println("LOOK BOI: " + returnedAddresses.size());
			assertTrue(returnedAddresses.size() == 1, "Should not have added another address");
			
			AddressEntity returnedAddressEntity = returnedAddresses.get(0);
			ForeignCollection<CreditCardEntity> associatedCreditCards = returnedAddressEntity.getCreditCards();
			assertTrue(associatedCreditCards.size() == 1, "Address should only have one card associated with it");
			
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
		
	}
	
	@Test
	void testAddCreditCardNewAddress() {
		
	}

}

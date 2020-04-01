package cardTests;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import card.Address;
import tables.AddressEntity;

class addressTests {
	
	ConnectionSource databaseConnection;
	AddressEntity testAddressEntity;
	Dao<AddressEntity, String> addressDao;
	
	@BeforeEach
    public void setUp() {
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			databaseConnection = new JdbcConnectionSource(databaseUrl);
			assertNotNull(databaseConnection, "Connection is null. Failed to Connect.");
			
			addressDao = DaoManager.createDao(databaseConnection, AddressEntity.class);
			
			testAddressEntity = new AddressEntity();
			
			testAddressEntity.setCity("test city");
			testAddressEntity.setState("test state");
			testAddressEntity.setStreetAddress("test street address");
			testAddressEntity.setZipCode("test zip code");
			
			addressDao.create(testAddressEntity);		
		}
		catch (SQLException e) {
			fail("failed to connect to database.");
			e.printStackTrace();
		}
	}
	
	@AfterEach
	public void tearDown() {
		try {
			addressDao.delete(testAddressEntity);
			
			databaseConnection.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testAddressExistsExistingAddresses() {
		Address testAddress = new Address(testAddressEntity);
		boolean result = testAddress.addressExists(databaseConnection);
		assertTrue(result, "Address didn't exist, but it should");
	}
	
	@Test
	void testAddressExistsNonExistingAddresses() {
		Address testAddress = new Address("fake", "fake", "fake", "fake");
		boolean result = testAddress.addressExists(databaseConnection);
		assertFalse(result, "Address exists, but it shouldn't");
	}
	
	@Test
	void testUpdateToExistingAddress() {
		Address testAddress = new Address(testAddressEntity);
		boolean result = testAddress.updateToExistingAddress(databaseConnection);
		assertTrue(result, "Returned false, but should return true if updated to existing address");
		assertTrue(testAddress.getId() == testAddressEntity.getId(), "Did not actually update address");
	}
	
	@Test 
	void testUpdateToExistingAddressNoAddress() {
		Address testAddress = new Address("fake", "fake", "fake", "fake");
		boolean result = testAddress.updateToExistingAddress(databaseConnection);
		assertFalse(result, "Returned true, but should return false if updated to existing address");
		assertTrue(testAddress.getId() == 0, "Updated address, but should not have");
	}
	
	@Test
	void testAddAddress() {
		AddressEntity newEntity = new AddressEntity("123 oak st", "STL", "MO", "20394");
		Address testAddress = new Address(newEntity);
		boolean result = testAddress.addAddress(databaseConnection);
		assertTrue(result, "Should return true if added");
		try {
			List<AddressEntity> queryResults = addressDao.queryForMatching(newEntity);
			assertFalse(queryResults.isEmpty(), "Should get results when finding the address added");	
			assertTrue(queryResults.get(0).getStreetAddress().equals(newEntity.getStreetAddress()), "Street addresses should be equal");
			assertTrue(queryResults.get(0).getCity().equals(newEntity.getCity()), "cities should be equal");
			assertTrue(queryResults.get(0).getState().equals(newEntity.getState()), "states should be equal");
			assertTrue(queryResults.get(0).getZipCode().equals(newEntity.getZipCode()), "zip code should be equal");
			assertTrue(queryResults.get(0).getId() == newEntity.getId(), "ids should be equal");
			addressDao.delete(newEntity);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	void testDeleteAddress() {
		AddressEntity toDeleteEntity = new AddressEntity("4 cherry ln", "STL", "MO", "0934");
		Address toDelete = new Address(toDeleteEntity);
		toDelete.deleteAddress(databaseConnection);
		try {
			List<AddressEntity> queryResults = addressDao.queryForMatching(toDeleteEntity);
			assertTrue(queryResults.isEmpty(), "Should get no results when finding the address added");	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

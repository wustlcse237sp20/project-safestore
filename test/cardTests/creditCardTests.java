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

class creditCardTests {
	
	ConnectionSource connectionSource;
	Address testAddress;
	Dao<AddressEntity, String> addressDao;
	
	@BeforeEach
    public void setUp() {
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			connectionSource = new JdbcConnectionSource(databaseUrl);
			assertNotNull(connectionSource, "Connection is null. Failed to Connect.");
			
			addressDao = DaoManager.createDao(connectionSource, AddressEntity.class);
			
			testAddress = new Address("6843 kingsbury blvd", "STL", "MO", "63130");
			testAddress.addAddress(connectionSource);
		}
		catch (SQLException e) {
			fail("failed to connect to database.");
			e.printStackTrace();
		}
	}
	
	@AfterEach
	public void tearDown() {
		testAddress.deleteAddress(connectionSource);
	}

	@Test
	void testAddCreditCardExistingAddress() {
		CreditCard testCreditCard = new CreditCard()
	}
	
	@Test
	void testAddCreditCardNewAddress() {
		
	}

}

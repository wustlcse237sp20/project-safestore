package card;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.AddressEntity;

public class Address {
	
	private AddressEntity addressEntity;
	
	public Address(String streetAddress, String city, String state, String zipCode) {
		String encryptedStreetAddress = Encryption.encrypt(streetAddress);
		String encryptedCity = Encryption.encrypt(city);
		String encryptedState = Encryption.encrypt(state);
		String encryptedZipCode = Encryption.encrypt(zipCode);
		this.addressEntity = new AddressEntity(encryptedStreetAddress, encryptedCity, encryptedState, encryptedZipCode);
	}
	
	public Address(AddressEntity addressEntity) {
		this.addressEntity = addressEntity;
	}
	
	public String getStreetAddress() {
		return Encryption.decrypt(this.addressEntity.getStreetAddress());
	}
	
	public String getCity() {
		return Encryption.decrypt(this.addressEntity.getCity());
	}
	
	public String getState() {
		return Encryption.decrypt(this.addressEntity.getState());
	}
	
	public String getZipCode() {
		return Encryption.decrypt(this.addressEntity.getZipCode());
	}
	
	public int getId() {
		return this.addressEntity.getId();
	}
	
	/**
	 * 
	 * @return address in format of:
	 * street address 
	 * city, state 
	 * zipcode
	 */
	public String getFullAddress() {
		return this.getStreetAddress() + "\n " + this.getCity() + ", " + this.getState() + "\n" + this.getZipCode();
	}
	
	public void setStreetAddress(String streetAddress) {
		this.addressEntity.setStreetAddress(Encryption.encrypt(streetAddress));
	}
	
	public void setCity(String city) {
		this.addressEntity.setCity(Encryption.encrypt(city));
	}
	
	public void setState(String state) {
		this.addressEntity.setState(Encryption.encrypt(state));
	}
	
	public void setZipCode(String zipCode) {
		this.addressEntity.setZipCode(Encryption.encrypt(zipCode));
	}
	
	protected AddressEntity getAddressEntity() {
		return this.addressEntity;
	}
	
	protected int getNumAssociatedCards() {
		return this.addressEntity.getCreditCards().size() + this.addressEntity.getDebitCards().size();
	}
	
	/**
	 * Checks if an address with the same street address, city, state, and zip code
	 * already exists in the database 
	 * 
	 * @param databaseConnection
	 * @return true if the address exists already in the database, false otherwise
	 */
	public boolean addressExists(ConnectionSource databaseConnection){
		//checks if an address with this streetAddress, city, state, zipCode exists 
		try {
			Dao<AddressEntity, String> addressDao = DaoManager.createDao(databaseConnection, AddressEntity.class);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("street_address", this.addressEntity.getStreetAddress());
			queryParams.put("city", this.addressEntity.getCity());
			queryParams.put("state", this.addressEntity.getState());
			queryParams.put("zip_code", this.addressEntity.getZipCode());
			
			List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(queryParams);
			if(returnedAddresses.isEmpty()) {
				return false;
			}
			else {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false; 
	}
	
	/**
	 * Gets the entity from the database based on the street address, city, state, and zip code
	 * @param databaseConnection
	 * @return the AddressEntity
	 */
	private AddressEntity getExistingEntity(ConnectionSource databaseConnection) {
		try {
			Dao<AddressEntity, String> addressDao = DaoManager.createDao(databaseConnection, AddressEntity.class);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("street_address", this.addressEntity.getStreetAddress());
			queryParams.put("city", this.addressEntity.getCity());
			queryParams.put("state", this.addressEntity.getState());
			queryParams.put("zip_code", this.addressEntity.getZipCode());
			
			List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(queryParams);
			if (!returnedAddresses.isEmpty()) {
				return returnedAddresses.get(0);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Tries to update the Address to have the entity for the existing address.
	 * 
	 * @param databaseConnection connection to the database
	 * @return true if it updated to an existing address, false if there was not an existing address to update to
	 */
	public boolean updateToExistingAddress(ConnectionSource databaseConnection) {
		if (this.addressExists(databaseConnection)) {
			AddressEntity updatedEntity = this.getExistingEntity(databaseConnection);
			this.addressEntity = updatedEntity;
			return true;
		}
		return false;
	}
	
	/**
	 * Adds an address to the database
	 * @param databaseConnection connection to the database
	 * @return true if the address was added, false if not
	 */
	public boolean addAddress(ConnectionSource databaseConnection) {
		try {
			Dao<AddressEntity, String> addressDao = DaoManager.createDao(databaseConnection, AddressEntity.class);
			int result = addressDao.create(this.addressEntity);
			return result == 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false; 
		}
	}
	
	/**
	 * Deletes an address from the database
	 * @param databaseConnection connection to the database
	 */
	public void deleteAddress(ConnectionSource databaseConnection) {
		if (this.addressExists(databaseConnection)) {
			this.updateToExistingAddress(databaseConnection);
			try {
				Dao<AddressEntity, String> addressDao = DaoManager.createDao(databaseConnection, AddressEntity.class);
				addressDao.delete(this.addressEntity);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

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
	
	public String getFullAddress() {
		return this.getStreetAddress() + ", " + this.getCity() + ", " + this.getState() + " " + this.getZipCode();
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
	
	public boolean addressExists(ConnectionSource connectionSource){
		//checks if an address with this streetAddress, city, state, zipCode exists 
		try {
			Dao<AddressEntity, String> addressDao = DaoManager.createDao(connectionSource, AddressEntity.class);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("street_address", this.getStreetAddress());
			queryParams.put("city", this.getCity());
			queryParams.put("state", this.getState());
			queryParams.put("zip_code", this.getZipCode());
			
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
	
	private AddressEntity getExistingEntity(ConnectionSource connectionSource) {
		try {
			Dao<AddressEntity, String> addressDao = DaoManager.createDao(connectionSource, AddressEntity.class);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("street_address", this.getStreetAddress());
			queryParams.put("city", this.getCity());
			queryParams.put("state", this.getState());
			queryParams.put("zip_code", this.getZipCode());
			
			List<AddressEntity> returnedAddresses = addressDao.queryForFieldValues(queryParams);
			if (returnedAddresses.isEmpty()) {
				return null;
			}
			return returnedAddresses.get(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param connectionSource connection to the database
	 * @return true if it updated to an existing address, false if there was not an existing address to update to
	 */
	public boolean updateToExistingAddress(ConnectionSource connectionSource) {
		if (this.addressExists(connectionSource)) {
			AddressEntity updatedEntity = this.getExistingEntity(connectionSource);
			this.addressEntity = updatedEntity;
			return true;
		}
		return false;
	}
	
	public boolean addAddress(ConnectionSource connectionSource) {
		//question: what does the connectionSource represent? Could it be named better? 
		try {
			Dao<AddressEntity, String> addressDao = DaoManager.createDao(connectionSource, AddressEntity.class);
			int result = addressDao.create(this.addressEntity);
			return result == 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false; 
		}
	}
	
	/**
	 * Method only used for cleaning up after tests
	 * @param connectionSource
	 */
	public void deleteAddress(ConnectionSource connectionSource) {
		if (this.addressExists(connectionSource)) {
			this.updateToExistingAddress(connectionSource);
			try {
				Dao<AddressEntity, String> addressDao = DaoManager.createDao(connectionSource, AddressEntity.class);
				addressDao.delete(this.addressEntity);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

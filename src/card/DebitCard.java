package card;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.DebitCardEntity;
import tables.UserEntity;
import tables.WebsiteAccountEntity;
import user.User;

public class DebitCard implements Card {

	private DebitCardEntity debitCardEntity;
	private Address billingAddress;
	
	public DebitCard(User safeStoreUser, String debitCardNumber, String expirationDate, 
			String cvv, String pin, Address billingAddress) {
		//last 4 digits of card number is default card nickname if no nickname provided 
		String defaultNickname = debitCardNumber.substring(debitCardNumber.length() - 4, debitCardNumber.length());
		defaultNickname = Encryption.encrypt(defaultNickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedDCNumber = Encryption.encrypt(debitCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		String encryptedPin = Encryption.encrypt(pin);
		this.debitCardEntity = new DebitCardEntity(safeStoreUserEntity, defaultNickname, 
				encryptedDCNumber, encryptedExpDate, encryptedCvv, encryptedPin, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
	}
	
	public DebitCard(User safeStoreUser, String nickname, String debitCardNumber, String expirationDate, 
			String cvv, String pin, Address billingAddress) {
		String encryptedNickname = Encryption.encrypt(nickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedDCNumber = Encryption.encrypt(debitCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		String encryptedPin = Encryption.encrypt(pin);
		this.debitCardEntity = new DebitCardEntity(safeStoreUserEntity, encryptedNickname, 
				encryptedDCNumber, encryptedExpDate, encryptedCvv, encryptedPin, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
	}
	
	public DebitCard(DebitCardEntity debitCardEntity) {
		this.debitCardEntity = debitCardEntity;
		this.billingAddress = new Address(debitCardEntity.getBillingAddress());
	}
	
	public DebitCardEntity getDebitCardEntity() {
		return this.debitCardEntity;
	}
	
	public String getNickname() {	
		return Encryption.decrypt(this.debitCardEntity.getNickname());
	}

	public String getCardNumber() {	
		return Encryption.decrypt(this.debitCardEntity.getDebitCardNumber());
	}

	public String getExpirationDate() {		
		return Encryption.decrypt(this.debitCardEntity.getExpirationDate());
	}
	
	public String getCvv() {	
		return Encryption.decrypt(this.debitCardEntity.getCvv());
	}
	
	public String getPin() {
		return Encryption.decrypt(this.debitCardEntity.getPin());
	}
	
	public String getZipCode() {		
		return this.billingAddress.getZipCode();
	}
	
	public String getBillingAddress() {
		return this.billingAddress.getFullAddress();
	}
	
	public String toString() {
		return "Card Number: " + this.getCardNumber() + "\n Expiration Date: " + this.getExpirationDate() + 
				"\n CVV: " + this.getCvv() + "\n pin: " + this.getPin() + "\n Billing Address: " + this.getBillingAddress();
	}
	
	/**
	 * sets the debit cards nickname in the database 
	 * @return true if successful, false if not
	 */
	public boolean setNickname(String nickname, ConnectionSource databaseConnection) {
		this.debitCardEntity.setNickname(Encryption.encrypt(nickname));
		try {
			int result = this.debitCardEntity.update();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	/**
	 * sets the debit card number in the database 
	 * @return true if successful, false if not
	 */
	public boolean setCardNumber(String cardNumber, ConnectionSource databaseConnection) {
		try {
			int result = this.debitCardEntity.updateId(Encryption.encrypt(cardNumber));
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * sets the debit cards expiration date in the database 
	 * @return true if successful, false if not
	 */
	public boolean setExpirationDate(String expirationDate, ConnectionSource databaseConnection) {
		this.debitCardEntity.setExpirationDate(Encryption.encrypt(expirationDate));
		try {
			int result = this.debitCardEntity.update();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * sets the debit cards CVV in the database 
	 * @return true if successful, false if not
	 */
	public boolean setCvv(String cvv, ConnectionSource databaseConnection) {
		this.debitCardEntity.setCvv(Encryption.encrypt(cvv));
		try {
			int result = this.debitCardEntity.update();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * Sets the debit cards Pin in the database
	 * @param pin
	 * @param databaseConnection
	 * @return true if successful, false if not
	 */
	public boolean setPin(String pin, ConnectionSource databaseConnection) {
		this.debitCardEntity.setPin(Encryption.encrypt(pin));
		try {
			int result = this.debitCardEntity.update();
			return result > 0;
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		}

	}
	/**
	 * Sets the billing address to be a new address 
	 * If old address only had one card associated with it, then it deletes the old address
	 * @return true if successful, false if not
	 */
	public boolean setBillingAddress(Address newBillingAddress, ConnectionSource databaseConnection) {		
		Address oldAddress = this.billingAddress;
		this.billingAddress = newBillingAddress;
		
		if (this.billingAddress.addressExists(databaseConnection)) {
			boolean updateSuccessful = newBillingAddress.updateToExistingAddress(databaseConnection);
			this.debitCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
			if (!updateSuccessful) {
				this.billingAddress = oldAddress;
				return false;
			}
		}
		else {
			boolean addAddressSuccessful = this.billingAddress.addAddress(databaseConnection);
			this.debitCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
			if(!addAddressSuccessful) {
				this.billingAddress = oldAddress;
				return false;
			}
		}

		try {
			int result = debitCardEntity.update();
			oldAddress.updateToExistingAddress(databaseConnection);
			if (oldAddress.getNumAssociatedCards() == 0) {
				oldAddress.deleteAddress(databaseConnection);
			}
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Checks to see if a debit card with the supplied nickname already exists
	 * @param databaseConnection
	 * @param safeStoreUser
	 * @param nickname
	 * @return true if the nickname given doens't exist in db for that user, false otherwise
	 */
	public static boolean cardNicknameIsUnique(ConnectionSource databaseConnection, 
			UserEntity safeStoreUser, String nickname) {
		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("nickname", Encryption.encrypt(nickname));
			queryParams.put("safe_store_username", safeStoreUser);
			List<DebitCardEntity> returnedDebitCards = debitCardDao.queryForFieldValues(queryParams);
			if (returnedDebitCards.isEmpty()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Checks if the address associated already exists, if it does then just adds the 
	 * debit card with a the foreign key to the appropriate existing address. If it can't
	 * update to the existing address, an exception is thrown.
	 * If address doesn't exist, adds the address to the database, and then the debit
	 * card with the appropriate foreign key. If address isn't added, an exception is thrown.
	 * 
	 * @param databaseConnection
	 * @return true if the debit card was successfully added, false if nickname already in use
	 * @throws Exception if the database fails to connect or if the address is not added 
	 */
	public boolean addCard(ConnectionSource databaseConnection) throws Exception {
		if (this.billingAddress.addressExists(databaseConnection)) {
			boolean updateSuccessful = this.billingAddress.updateToExistingAddress(databaseConnection);
			this.debitCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
			if (!updateSuccessful) {
				throw(new Exception("Exisitng address not gotten properly, abort add card"));
			}
		}
		else {
			boolean addAddressSuccessful = this.billingAddress.addAddress(databaseConnection);
			this.debitCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
			if(!addAddressSuccessful) {
				throw(new Exception("Address not added properly, abort add card"));
			}
		}		
		
		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			if (cardNicknameIsUnique(databaseConnection, this.debitCardEntity.getSafeStoreUser(), this.getNickname())) {
				debitCardDao.create(this.debitCardEntity);
				return true;
			}
			else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw(new Exception("Failed to connect to database"));
		}

	}
	/**
	 * gets the foreign collection of all debit card rows for that user
	 * @param databaseConnection
	 * @param safeStoreUser of type User
	 * @return a ForeignCollection<WebsiteAccountEntity> that holds all the db rows of debit cards
	 * 			for the safeStoreUser
	 */
	public static ForeignCollection<DebitCardEntity> getAllDebitCards(ConnectionSource databaseConnection, 
			User safeStoreUser) {
		Dao<UserEntity, String> userDao;
		try {
			userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			UserEntity userEntity = userDao.queryForSameId(safeStoreUser.getUserEntity());
			return userEntity.getDebitCards();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * Gets a Debit card from the database based off the nickname and SafeStore user
	 * 
	 * @param nickname
	 * @param safeStoreUser
	 * @param databaseConnection
	 * @return the Debit Card with that nickname and user
	 * @throws Exception if no credit card is found, or if there is a database error
	 */
	public static DebitCard getDebitCardFromNickname(String nickname, User safeStoreUser,
			ConnectionSource databaseConnection) throws Exception {
		nickname = Encryption.encrypt(nickname);
		try {
			Dao<DebitCardEntity, String> debitCardDao = DaoManager.createDao(databaseConnection, DebitCardEntity.class);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("nickname", nickname);
			queryParams.put("safe_store_username", safeStoreUser.getUserEntity());
			List<DebitCardEntity> returnedDebitCards = debitCardDao.queryForFieldValues(queryParams);
			if (returnedDebitCards.size() == 0) {
				throw new Exception("No Debit Card Found");
			}
			DebitCardEntity returnedCard = returnedDebitCards.get(0);
			return new DebitCard(returnedCard);

		} catch (SQLException e) {
			e.printStackTrace();
			throw(e);
		}
		
	}

	/**
	 * 
	 * @param currentNickname
	 * @param databaseConnection
	 * @param safeStoreUser
	 * @param newInputs user inputs to modify credit card info. User only fills in input fields they wish to change. 
	 * 
	 * User has the option to modify: nickname in newInputs[0], card number in newInputs[1], 
	 * expiration date in newInputs[2], cvv in newInputs[3], pin in newInputs[4],
	 * street address in newInputs[5], city in newInputs[6], state in newInputs[7], 
	 * and zip code in newInputs[8]
	 * 
	 * @return true if successful update 
	 */
	public static boolean updateDebitCardInformation(String currentNickname, 
			ConnectionSource databaseConnection, User safeStoreUser, String[] newInputs) {
		try {
			DebitCard requestedDebitCard = DebitCard.getDebitCardFromNickname(currentNickname, safeStoreUser, databaseConnection);

			if(!newInputs[0].isEmpty()) {
				if (cardNicknameIsUnique(databaseConnection, safeStoreUser.getUserEntity(), newInputs[0])) {
					requestedDebitCard.setNickname(newInputs[0], databaseConnection);
				}else {
					return false;
				}
			}
			if(!newInputs[1].isEmpty()) {
				requestedDebitCard.setCardNumber(newInputs[1], databaseConnection);
			}
			if(!newInputs[2].isEmpty()) {
				requestedDebitCard.setExpirationDate(newInputs[2], databaseConnection);
			}
			if(!newInputs[3].isEmpty()) {
				requestedDebitCard.setCvv(newInputs[3], databaseConnection);
			}
			if(!newInputs[4].isEmpty()) {
				requestedDebitCard.setPin(newInputs[4], databaseConnection);
			}

			Address oldBillingAddress = requestedDebitCard.billingAddress;
			String streetAddress = oldBillingAddress.getStreetAddress();
			String city = oldBillingAddress.getCity();
			String state = oldBillingAddress.getState();
			String zipCode = oldBillingAddress.getZipCode();
			
			if(!newInputs[5].isEmpty()) {
				streetAddress = newInputs[5];
			}
			if(!newInputs[6].isEmpty()) {
				city = newInputs[6];
			}
			if(!newInputs[7].isEmpty()) {
				state = newInputs[7];
			}
			if(!newInputs[8].isEmpty()) {
				zipCode = newInputs[8];
			}
			
			Address newBillingAddress = new Address(streetAddress, city, state, zipCode);	
			requestedDebitCard.setBillingAddress(newBillingAddress, databaseConnection);

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

}

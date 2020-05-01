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
import tables.CreditCardEntity;
import tables.UserEntity;
import user.User;

public class CreditCard implements Card{

	private CreditCardEntity creditCardEntity;
	private Address billingAddress;

	public CreditCard(User safeStoreUser, String creditCardNumber, 
			String expirationDate, String cvv, Address billingAddress) {
		//last 4 digits of card number is default card nickname if no nickname provided 
		String defaultNickname = creditCardNumber.substring(creditCardNumber.length() - 4, creditCardNumber.length());
		defaultNickname = Encryption.encrypt(defaultNickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedCCNumber = Encryption.encrypt(creditCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		this.creditCardEntity = new CreditCardEntity(safeStoreUserEntity, defaultNickname, 
				encryptedCCNumber, encryptedExpDate, encryptedCvv, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
	}

	public CreditCard(User safeStoreUser, String nickname, String creditCardNumber, 
			String expirationDate, String cvv, Address billingAddress) {
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedNickname = Encryption.encrypt(nickname);
		String encryptedCCNumber = Encryption.encrypt(creditCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		this.creditCardEntity = new CreditCardEntity(safeStoreUserEntity, encryptedNickname, 
				encryptedCCNumber, encryptedExpDate, encryptedCvv, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
	}

	public CreditCard(CreditCardEntity creditCardEntity) {
		this.creditCardEntity = creditCardEntity;
		this.billingAddress = new Address(creditCardEntity.getBillingAddress());
	}

	public CreditCardEntity getCreditCardEntity() {
		return this.creditCardEntity;
	}

	public String getNickname() {
		return Encryption.decrypt(this.creditCardEntity.getNickname());
	}

	public String getCardNumber() {
		return Encryption.decrypt(this.creditCardEntity.getCreditCardNumber());
	}

	public String getExpirationDate() {
		return Encryption.decrypt(this.creditCardEntity.getExpirationDate());
	}

	public String getCvv() {
		return Encryption.decrypt(this.creditCardEntity.getCvv());
	}

	public String getZipCode() {
		return this.billingAddress.getZipCode();
	}

	public String getBillingAddress() {
		return this.billingAddress.getFullAddress();
	}

	public String toString() {
		return "Card Number: " + this.getCardNumber() + "\n Expiration Date: " + this.getExpirationDate() + 
				"\n CVV: " + this.getCvv() + "\n Billing Address: " + this.getBillingAddress();
	}

	/**
	 * sets the credit cards nickname in the database 
	 * @return true if successful, false if not
	 */
	public boolean setNickname(String nickname, ConnectionSource databaseConnection) {
		this.creditCardEntity.setNickname(Encryption.encrypt(nickname));
		try {
			int result = this.creditCardEntity.update();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * sets the credit card number in the database 
	 * @return true if successful, false if not
	 */
	public boolean setCardNumber(String cardNumber, ConnectionSource databaseConnection) {
		try {
			int result = this.creditCardEntity.updateId(Encryption.encrypt(cardNumber));
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * sets the credit cards expiration date in the database 
	 * @return true if successful, false if not
	 */
	public boolean setExpirationDate(String expirationDate, ConnectionSource databaseConnection) {
		this.creditCardEntity.setExpirationDate(Encryption.encrypt(expirationDate));
		try {
			int result = this.creditCardEntity.update();
			return result > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * sets the credit cards CVV in the database 
	 * @return true if successful, false if not
	 */
	public boolean setCvv(String cvv, ConnectionSource databaseConnection) {
		this.creditCardEntity.setCvv(Encryption.encrypt(cvv));
		try {
			int result = this.creditCardEntity.update();
			return result > 0;
		} catch (SQLException e) {
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
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
			if (!updateSuccessful) {
				this.billingAddress = oldAddress;
				return false;
			}
		}
		else {
			boolean addAddressSuccessful = this.billingAddress.addAddress(databaseConnection);
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
			if(!addAddressSuccessful) {
				this.billingAddress = oldAddress;
				return false;
			}
		}

		try {
			int result = creditCardEntity.update();
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
	 * Checks to see if a credit card with the supplied nickname already exists
	 * @param databaseConnection
	 * @param safeStoreUser
	 * @param nickname
	 * @return true is the nickname given doens't exist in db for that user, false otherwise
	 */
	public static boolean cardNicknameIsUnique(ConnectionSource databaseConnection, 
			UserEntity safeStoreUser, String nickname) {
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("nickname", Encryption.encrypt(nickname));
			queryParams.put("safe_store_username", safeStoreUser.getUsername());
			List<CreditCardEntity> returnedCreditCards = creditCardDao.queryForFieldValues(queryParams);
			System.out.println(returnedCreditCards);
			if (returnedCreditCards.isEmpty()) {
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
	 * credit card with a the foreign key to the appropriate existing address. If it can't
	 * update to the existing address, an exception is thrown.
	 * If address doesn't exist, adds the address to the database, and then the credit
	 * card with the appropriate foreign key. If address isn't added, an exception is thrown.
	 * 
	 * @param databaseConnection
	 * @return true if the credit card was successfully added, false if nickname already in use
	 * @throws Exception if the 
	 */
	public boolean addCard(ConnectionSource databaseConnection) throws Exception {
		if (this.billingAddress.addressExists(databaseConnection)) {
			boolean updateSuccessful = this.billingAddress.updateToExistingAddress(databaseConnection);
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
			if (!updateSuccessful) {
				throw(new Exception("Exisitng address not gotten properly, abort add card"));
			}
		}
		else {
			boolean addAddressSuccessful = this.billingAddress.addAddress(databaseConnection);
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
			if(!addAddressSuccessful) {
				throw(new Exception("Address not added properly, abort add card"));
			}
		}

		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			if (cardNicknameIsUnique(databaseConnection, this.creditCardEntity.getSafeStoreUser(), this.getNickname())) {
				creditCardDao.create(this.creditCardEntity);
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw(new Exception("Failed to connect to database"));
		}

	}

	/**
	 * gets the foreign collection of all credit card rows for that user
	 * @param databaseConnection
	 * @param safeStoreUser of type User
	 * @return a ForeignCollection<WebsiteAccountEntity> that holds all the db rows of credit cards
	 * 			for the safeStoreUser
	 */
	public static ForeignCollection<CreditCardEntity> getAllCreditCards(ConnectionSource databaseConnection, 
			User safeStoreUser) {
		Dao<UserEntity, String> userDao;
		try {
			userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			UserEntity userEntity = userDao.queryForSameId(safeStoreUser.getUserEntity());
			return userEntity.getCreditCards();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Gets a Credit card from the database based off the nickname and SafeStore user
	 * 
	 * @param nickname
	 * @param safeStoreUser
	 * @param databaseConnection
	 * @return the Credit Card with that nickname and user
	 * @throws Exception if no credit card is found, or if there is a database error
	 */
	public static CreditCard getCreditCardFromNickname(String nickname, User safeStoreUser, 
			ConnectionSource databaseConnection) throws Exception{
		nickname = Encryption.encrypt(nickname);
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("nickname", nickname);
			queryParams.put("safe_store_username", safeStoreUser.getUserEntity());
			List<CreditCardEntity> returnedCreditCards = creditCardDao.queryForFieldValues(queryParams);
			if (returnedCreditCards.size() == 0) {
				throw new Exception("No Credit Card Found");
			}
			CreditCardEntity returnedCard = returnedCreditCards.get(0);
			return new CreditCard(returnedCard);

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
	 * expiration date in newInputs[2], cvv in newInputs[3], street address in newInputs[4],
	 * city in newInputs[5], state in newInputs[6], and zip code in newInputs[7]
	 * 
	 * @return true if successful update 
	 */
	public static boolean updateCreditCardInformation(String currentNickname, ConnectionSource databaseConnection, 
			User safeStoreUser, String[] newInputs) {

		try {
			CreditCard requestedCreditCard = CreditCard.getCreditCardFromNickname(currentNickname, safeStoreUser, databaseConnection);

			if(!newInputs[0].isEmpty()) {
				if (cardNicknameIsUnique(databaseConnection, safeStoreUser.getUserEntity(), newInputs[0])) {
					requestedCreditCard.setNickname(newInputs[0], databaseConnection);
				}
			}
			if(!newInputs[1].isEmpty()) {
				requestedCreditCard.setCardNumber(newInputs[1], databaseConnection);
			}
			if(!newInputs[2].isEmpty()) {
				requestedCreditCard.setExpirationDate(newInputs[2], databaseConnection);
			}
			if(!newInputs[3].isEmpty()) {
				requestedCreditCard.setCvv(newInputs[3], databaseConnection);
			}

			Address oldBillingAddress = requestedCreditCard.billingAddress;
			String streetAddress = oldBillingAddress.getStreetAddress();
			String city = oldBillingAddress.getCity();
			String state = oldBillingAddress.getState();
			String zipCode = oldBillingAddress.getZipCode();
			
			if(!newInputs[4].isEmpty()) {
				streetAddress = newInputs[4];
			}
			if(!newInputs[5].isEmpty()) {
				city = newInputs[5];
			}
			if(!newInputs[6].isEmpty()) {
				state = newInputs[6];
			}
			if(!newInputs[7].isEmpty()) {
				zipCode = newInputs[7];
			}
			Address newBillingAddress = new Address(streetAddress, city, state, zipCode);	
			requestedCreditCard.setBillingAddress(newBillingAddress, databaseConnection);

			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

}

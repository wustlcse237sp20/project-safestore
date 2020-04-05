package card;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.DebitCardEntity;
import tables.UserEntity;
import user.User;

public class DebitCard implements Card {

	private DebitCardEntity debitCardEntity;
	private Address billingAddress;
	
	public DebitCard(User safeStoreUser, String debitCardNumber, String expirationDate, String cvv, String pin, Address billingAddress) {
		String defaultNickname = debitCardNumber.substring(debitCardNumber.length() - 4, debitCardNumber.length());
		defaultNickname = Encryption.encrypt(defaultNickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedCCNumber = Encryption.encrypt(debitCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		String encryptedPin = Encryption.encrypt(pin);
		this.debitCardEntity = new DebitCardEntity(safeStoreUserEntity, defaultNickname, encryptedCCNumber, encryptedExpDate, encryptedCvv, encryptedPin, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
		
	}
	public DebitCard(User safeStoreUser, String nickname, String debitCardNumber, String expirationDate, String cvv, String pin, Address billingAddress) {
		
		String encryptedNickname = Encryption.encrypt(nickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedCCNumber = Encryption.encrypt(debitCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		String encryptedPin = Encryption.encrypt(pin);
		this.debitCardEntity = new DebitCardEntity(safeStoreUserEntity, encryptedNickname, encryptedCCNumber, encryptedExpDate, encryptedCvv, encryptedPin, billingAddress.getAddressEntity());
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
	@Override
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
	 * Checks if the address associated already exists, if it does then just adds the 
	 * debit card with a the foreign key to the appropriate existing address. If it can't
	 * update to the existing address, an exception is thrown.
	 * If address doesn't exist, adds the address to the database, and then the debit
	 * card with the appropriate foreign key. If address isn't added, an exception is thrown.
	 * 
	 * @param databaseConnection
	 * @return true if the debit card was successfully added, false if nickname already in use
	 * @throws Exception if the 
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
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("nickname", Encryption.encrypt(this.getNickname()));
			queryParams.put("safe_store_username", this.debitCardEntity.getSafeStoreUser());
			List<DebitCardEntity> returnedDebitCards = debitCardDao.queryForFieldValues(queryParams);
			if (returnedDebitCards.size() > 0) {
				return false;
			}

			debitCardDao.create(this.debitCardEntity);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw(new Exception("Failed to connect to database"));
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	/**
	 * A static method to prompt user for necessary information to make a debit card 
	 * and add it to the database
	 * 
	 * @param databaseConnection 
	 * @param keyboard
	 * @param safeStoreUser
	 * @return true if the debit card was successfully added, false if not
	 */
	public static boolean addDebitCard(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		String userInput = "";

		//Setting up Debit Card Variables
		String debitCardNumber = "";
		String expirationDate = "";
		String cvv = "";
		String pin = "";
		boolean hasNickname = false;
		String nickname = "";

		//Setting up Address Variables
		String streetAddress = "";
		String city = "";
		String state = "";
		String zipCode = "";

		System.out.println("What is the Debit Card Number? ");
		debitCardNumber = keyboard.nextLine();
		System.out.println("What is the Expiration Date?");
		expirationDate = keyboard.nextLine();
		System.out.println("What is the CVV?");
		cvv = keyboard.nextLine();
		System.out.print("What is the pin?");
		pin = keyboard.nextLine();
		System.out.println("Do you want a nickname for this debit card? Y/N (default is the last 4 digits)");
		userInput = keyboard.nextLine();
		while (!userInput.equals("Y") && !userInput.equals("N")) {
			System.out.println("Enter 'Y' or 'N'");
			userInput = keyboard.nextLine();
		}
		if (userInput.equals("Y")) {
			hasNickname = true;
		}
		else {
			hasNickname = false;
		}
		if(hasNickname) {
			System.out.println("What is the nickname?");
			nickname = keyboard.nextLine();
		}

		System.out.println("What is the street address?");
		streetAddress = keyboard.nextLine();
		System.out.println("What is the city?");
		city = keyboard.nextLine();
		System.out.println("What is the state?");
		state = keyboard.nextLine();
		System.out.println("What is the zip code?");
		zipCode = keyboard.nextLine();

		Address billingAddress = new Address(streetAddress, city, state, zipCode);	        
		DebitCard debitCard;
		if(hasNickname) {
			debitCard = new DebitCard(safeStoreUser, nickname, debitCardNumber, expirationDate, cvv, pin, billingAddress);
		}
		else {
			debitCard = new DebitCard(safeStoreUser, debitCardNumber, expirationDate, cvv, pin, billingAddress);
		}
		try {
			return debitCard.addCard(databaseConnection);
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}
	
	public static boolean updateDebitCardInformation(ConnectionSource databaseConnection, Scanner keyboard,
			User testUser) {
		// TODO Auto-generated method stub
		return false;
	}
	public static DebitCard getDebitCardFromNickname(String nickname, User testUser,
			ConnectionSource databaseConnection) {
		// TODO Auto-generated method stub
		return null;
	}
	public static String getDebitCardInformation(ConnectionSource databaseConnection, Scanner keyboard, User testUser) {
		// TODO Auto-generated method stub
		return null;
	}


	

}

package card;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.CreditCardEntity;
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
		String encryptedDCNumber = Encryption.encrypt(debitCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		String encryptedPin = Encryption.encrypt(pin);
		this.debitCardEntity = new DebitCardEntity(safeStoreUserEntity, defaultNickname, encryptedDCNumber, encryptedExpDate, encryptedCvv, encryptedPin, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
		
	}
	public DebitCard(User safeStoreUser, String nickname, String debitCardNumber, String expirationDate, String cvv, String pin, Address billingAddress) {
		
		String encryptedNickname = Encryption.encrypt(nickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedDCNumber = Encryption.encrypt(debitCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		String encryptedPin = Encryption.encrypt(pin);
		this.debitCardEntity = new DebitCardEntity(safeStoreUserEntity, encryptedNickname, encryptedDCNumber, encryptedExpDate, encryptedCvv, encryptedPin, billingAddress.getAddressEntity());
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
	 * @return true is the nickname given doens't exist in db for that user, false otherwise
	 */
	public static boolean cardNicknameIsUnique(ConnectionSource databaseConnection, UserEntity safeStoreUser, String nickname) {
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
		System.out.println("What is the pin?");
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
	/**
	 * Updates the debit card in the database based off user input 
	 * @param databaseConnection
	 * @param keyboard
	 * @param safeStoreUser
	 * @return true if the information is updated, false if not
	 */
	public static boolean updateDebitCardInformation(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		String userPrompt = "What is the card nickname you'd like to update info for (default is the last four digits of the card number)";
		System.out.println(userPrompt);
		String nickname = keyboard.nextLine();
		
		try {
			DebitCard requestedDebitCard = DebitCard.getDebitCardFromNickname(nickname, safeStoreUser, databaseConnection);
			userPrompt = "What information would you like to update? Type: 'Nickname', 'Card Number', 'Expiration Date', 'CVV', 'Pin', or 'Billing Address'";
			System.out.println(userPrompt);
			String userInput = keyboard.nextLine();
			
			String[] acceptableInput = {"Nickname", "Card Number", "Expiration Date", "CVV", "Pin", "Billing Address"};
			while(!Arrays.asList(acceptableInput).contains(userInput)) {
				userPrompt = "Enter: 'Nickname', 'Card Number', 'Expiration Date', 'CVV', 'Pin' or 'Billing Address'";
				System.out.println(userPrompt);
				userInput = keyboard.nextLine();
			}
			
			if(userInput.equals("Nickname")) {
				userPrompt = "What is the new nickname you'd like to use?";
				System.out.println(userPrompt);
				String newNickname = keyboard.nextLine();
				return requestedDebitCard.setNickname(newNickname, databaseConnection);
			}
			if(userInput.equals("Card Number")) {
				userPrompt = "What is the new card number?";
				System.out.println(userPrompt);
				String newCardNum = keyboard.nextLine();
				return requestedDebitCard.setCardNumber(newCardNum, databaseConnection);
			}
			if(userInput.equals("Expiration Date")) {
				userPrompt = "What is the new expiration date?";
				System.out.println(userPrompt);
				String newExpDate = keyboard.nextLine();
				return requestedDebitCard.setExpirationDate(newExpDate, databaseConnection);
			}
			if(userInput.equals("CVV")) {
				userPrompt = "What is the new CVV?";
				System.out.println(userPrompt);
				String newCvv = keyboard.nextLine();
				return requestedDebitCard.setCvv(newCvv, databaseConnection);
			}
			if(userInput.equals("Pin")) {
				userPrompt = "What is the new Pin?";
				System.out.println(userPrompt);
				String newPin = keyboard.nextLine();
				return requestedDebitCard.setPin(newPin, databaseConnection);
			}
			if(userInput.equals("Billing Address")) {
				System.out.println("What is the street address?");
				String streetAddress = keyboard.nextLine();
				System.out.println("What is the city?");
				String city = keyboard.nextLine();
				System.out.println("What is the state?");
				String state = keyboard.nextLine();
				System.out.println("What is the zip code?");
				String zipCode = keyboard.nextLine();

				Address newBillingAddress = new Address(streetAddress, city, state, zipCode);	
				return requestedDebitCard.setBillingAddress(newBillingAddress, databaseConnection);
			}
			return false;
		} catch (Exception e) {
			System.out.println(e);
			return false;
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
	public static DebitCard getDebitCardFromNickname(String nickname, User safeStoreUser,ConnectionSource databaseConnection) throws Exception {
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
	 * Returns and prints the requested information about a debit card
	 * @param databaseConnection
	 * @param keyboard
	 * @param safeStoreUser
	 * @return requested debit card info 
	 */
	public static String getDebitCardInformation(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		String userPrompt = "What is the card nickname you'd like to retrieve info for (default is the last four digits of the card number)";
		System.out.println(userPrompt);
		String nickname = keyboard.nextLine();

		try {
			DebitCard requestedDebitCard = DebitCard.getDebitCardFromNickname(nickname, safeStoreUser, databaseConnection);

			userPrompt = "What information would you like to view? Type: 'Card Number', 'Expiration Date', 'CVV', 'Pin, 'Zip Code', 'Billing Address', or 'All'";
			System.out.println(userPrompt);

			String userInput = keyboard.nextLine();
			String[] acceptableInput = {"Card Number", "Expiration Date", "CVV", "Pin", "Zip Code", "Billing Address", "All"};
			while(!Arrays.asList(acceptableInput).contains(userInput)) {
				userPrompt = "Enter: 'Card Number', 'Expiration Date', 'CVV', 'Pin', 'Zip Code', 'Billing Address', or 'All'";
				System.out.println(userPrompt);
				userInput = keyboard.nextLine();
			}

			String requestedInformation = "An Error Occurred";
			if(userInput.equals("Card Number")) {
				requestedInformation = requestedDebitCard.getCardNumber();
			}
			if(userInput.equals("Expiration Date")) {
				requestedInformation = requestedDebitCard.getExpirationDate();
			}
			if(userInput.equals("CVV")) {
				requestedInformation = requestedDebitCard.getCvv();
			}
			if(userInput.equals("Pin")) {
				requestedInformation = requestedDebitCard.getPin();
			}
			if(userInput.equals("Zip Code")) {
				requestedInformation = requestedDebitCard.getZipCode();
			}
			if(userInput.equals("Billing Address")) {
				requestedInformation = requestedDebitCard.getBillingAddress();
			}
			if(userInput.equals("All")) {
				requestedInformation = requestedDebitCard.toString();
			}
			System.out.println(requestedInformation);
			return requestedInformation;
		} catch (Exception e) {
			System.out.println(e);
			return(e.toString());
		}
	}


	

}

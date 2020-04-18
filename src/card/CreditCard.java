package card;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.CreditCardEntity;
import tables.UserEntity;
import user.User;

public class CreditCard implements Card{

	private CreditCardEntity creditCardEntity;
	private Address billingAddress;

	public CreditCard(User safeStoreUser, String creditCardNumber, String expirationDate, String cvv, Address billingAddress) {
		String defaultNickname = creditCardNumber.substring(creditCardNumber.length() - 4, creditCardNumber.length());
		defaultNickname = Encryption.encrypt(defaultNickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedCCNumber = Encryption.encrypt(creditCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		this.creditCardEntity = new CreditCardEntity(safeStoreUserEntity, defaultNickname, encryptedCCNumber, encryptedExpDate, encryptedCvv, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
	}

	public CreditCard(User safeStoreUser, String nickname, String creditCardNumber, String expirationDate, String cvv, Address billingAddress) {
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedNickname = Encryption.encrypt(nickname);
		String encryptedCCNumber = Encryption.encrypt(creditCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		this.creditCardEntity = new CreditCardEntity(safeStoreUserEntity, encryptedNickname, encryptedCCNumber, encryptedExpDate, encryptedCvv, billingAddress.getAddressEntity());
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
			Map<String, Object> queryParams = new HashMap<String, Object>();
			queryParams.put("nickname", Encryption.encrypt(this.getNickname()));
			queryParams.put("safe_store_username", this.creditCardEntity.getSafeStoreUser());
			List<CreditCardEntity> returnedCreditCards = creditCardDao.queryForFieldValues(queryParams);
			if (returnedCreditCards.size() > 0) {
				return false;
			}

			creditCardDao.create(this.creditCardEntity);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			throw(new Exception("Failed to connect to database"));
		}

	}

	/**
	 * A static method to prompt user for necessary information to make a credit card 
	 * and add it to the database
	 * 
	 * @param databaseConnection 
	 * @param keyboard
	 * @param safeStoreUser
	 * @return true if the credit card was successfully added, false if not
	 */
	public static boolean addCreditCard(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		String userInput = "";

		//Setting up Credit Card Variables
		String creditCardNumber = "";
		String expirationDate = "";
		String cvv = "";
		boolean hasNickname = false;
		String nickname = "";

		//Setting up Address Variables
		String streetAddress = "";
		String city = "";
		String state = "";
		String zipCode = "";

		System.out.println("What is the Credit Card Number? ");
		creditCardNumber = keyboard.nextLine();
		System.out.println("What is the Expiration Date?");
		expirationDate = keyboard.nextLine();
		System.out.println("What is the CVV?");
		cvv = keyboard.nextLine();
		System.out.println("Do you want a nickname for this credit card? Y/N (default is the last 4 digits)");
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
		CreditCard creditCard;
		if(hasNickname) {
			creditCard = new CreditCard(safeStoreUser, nickname, creditCardNumber, expirationDate, cvv, billingAddress);
		}
		else {
			creditCard = new CreditCard(safeStoreUser, creditCardNumber, expirationDate, cvv, billingAddress);
		}
		try {
			return creditCard.addCard(databaseConnection);
		} catch (Exception e) {
			System.out.println(e);
			return false;
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
	public static CreditCard getCreditCardFromNickname(String nickname, User safeStoreUser, ConnectionSource databaseConnection) throws Exception{
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
	 * Returns and prints the requested information about a credit card
	 * @param databaseConnection
	 * @param keyboard
	 * @param safeStoreUser
	 * @return requested credit card info
	 */
	public static String getCreditCardInformation(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		String userPrompt = "What is the card nickname you'd like to retrieve info for (default is the last four digits of the card number)";
		System.out.println(userPrompt);
		String nickname = keyboard.nextLine();

		try {
			CreditCard requestedCreditCard = CreditCard.getCreditCardFromNickname(nickname, safeStoreUser, databaseConnection);

			userPrompt = "What information would you like to view? Type: 'Card Number', 'Expiration Date', 'CVV', 'Zip Code', 'Billing Address', or 'All'";
			System.out.println(userPrompt);

			String userInput = keyboard.nextLine();
			String[] acceptableInput = {"Card Number", "Expiration Date", "CVV", "Zip Code", "Billing Address", "All"};
			while(!Arrays.asList(acceptableInput).contains(userInput)) {
				userPrompt = "Enter: 'Card Number', 'Expiration Date', 'CVV', 'Zip Code', 'Billing Address', or 'All'";
				System.out.println(userPrompt);
				userInput = keyboard.nextLine();
			}

			String requestedInformation = "An Error Occurred";
			if(userInput.equals("Card Number")) {
				requestedInformation = requestedCreditCard.getCardNumber();
			}
			if(userInput.equals("Expiration Date")) {
				requestedInformation = requestedCreditCard.getExpirationDate();
			}
			if(userInput.equals("CVV")) {
				requestedInformation = requestedCreditCard.getCvv();
			}
			if(userInput.equals("Zip Code")) {
				requestedInformation = requestedCreditCard.getZipCode();
			}
			if(userInput.equals("Billing Address")) {
				requestedInformation = requestedCreditCard.getBillingAddress();
			}
			if(userInput.equals("All")) {
				requestedInformation = requestedCreditCard.toString();
			}
			System.out.println(requestedInformation);
			return requestedInformation;
		} catch (Exception e) {
			System.out.println(e);
			return(e.toString());
		}
	}

	/**
	 * Updates the credit card in the database based off user input 
	 * @param databaseConnection
	 * @param keyboard
	 * @param safeStoreUser
	 * @return true if the information is updated, false if not
	 */
	public static boolean updateCreditCardInformation(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
		String userPrompt = "What is the card nickname you'd like to update info for (default is the last four digits of the card number)";
		System.out.println(userPrompt);
		String nickname = keyboard.nextLine();

		try {
			CreditCard requestedCreditCard = CreditCard.getCreditCardFromNickname(nickname, safeStoreUser, databaseConnection);
			userPrompt = "What information would you like to update? Type: 'Nickname', 'Card Number', 'Expiration Date', 'CVV', or 'Billing Address'";
			System.out.println(userPrompt);
			String userInput = keyboard.nextLine();

			String[] acceptableInput = {"Nickname", "Card Number", "Expiration Date", "CVV", "Billing Address"};
			while(!Arrays.asList(acceptableInput).contains(userInput)) {
				userPrompt = "Enter: 'Nickname', 'Card Number', 'Expiration Date', 'CVV', or 'Billing Address'";
				System.out.println(userPrompt);
				userInput = keyboard.nextLine();
			}

			if(userInput.equals("Nickname")) {
				userPrompt = "What is the new nickname you'd like to use?";
				System.out.println(userPrompt);
				String newNickname = keyboard.nextLine();
				return requestedCreditCard.setNickname(newNickname, databaseConnection);
			}
			if(userInput.equals("Card Number")) {
				userPrompt = "What is the new card number?";
				System.out.println(userPrompt);
				String newCardNum = keyboard.nextLine();
				return requestedCreditCard.setCardNumber(newCardNum, databaseConnection);
			}
			if(userInput.equals("Expiration Date")) {
				userPrompt = "What is the new expiration date?";
				System.out.println(userPrompt);
				String newExpDate = keyboard.nextLine();
				return requestedCreditCard.setExpirationDate(newExpDate, databaseConnection);
			}
			if(userInput.equals("CVV")) {
				userPrompt = "What is the new CVV?";
				System.out.println(userPrompt);
				String newCvv = keyboard.nextLine();
				return requestedCreditCard.setCvv(newCvv, databaseConnection);
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
				return requestedCreditCard.setBillingAddress(newBillingAddress, databaseConnection);
			}
			return false;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	/**
	 * Main here is just to test out adding a card through the console, would be deleted when functionality
	 * added to SafeStore.java
	 */
	public static void main(String[] args) {
		ConnectionSource databaseConnection;
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			databaseConnection = new JdbcConnectionSource(databaseUrl);
			@SuppressWarnings("resource")
			Scanner keyboard = new Scanner(System.in);
			//			System.out.println("Would you like to add a Website Account, Credit Card, or Debit Card?");
			//			String userInput = keyboard.nextLine();
			//			while (!userInput.trim().equals("WebsiteAccount") && !userInput.trim().equals("Credit Card") && !userInput.trim().equals("Debit Card")) {
			//				System.out.println("Enter 'Website Account', 'Credit Card', or 'Debit Card'");
			//		        userInput = keyboard.nextLine();
			//		    }
			//			if(userInput.equals("Credit Card")) {
			//				User safeStoreUser = new User("testUser", "testPassword");
			//				CreditCard.addCreditCard(databaseConnection, keyboard, safeStoreUser);
			//			}
			User safeStoreUser = new User("testUser", "testPassword");
			safeStoreUser.createSafeStoreAccountThroughDatabase(databaseConnection);
			CreditCard.addCreditCard(databaseConnection, keyboard, safeStoreUser);
			CreditCard.updateCreditCardInformation(databaseConnection, keyboard, safeStoreUser);
			CreditCard.getCreditCardInformation(databaseConnection, keyboard, safeStoreUser);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static boolean updateCreditCardInformation(String currentNickname, ConnectionSource databaseConnection, User safeStoreUser,String[] fieldsToModify, String[] newInputs) {

		try {
			CreditCard requestedCreditCard = CreditCard.getCreditCardFromNickname(currentNickname, safeStoreUser, databaseConnection);


			if(fieldsToModify[0].equals("Nickname")) {
				
				requestedCreditCard.setNickname(newInputs[0], databaseConnection);
			}
			if(fieldsToModify[1].equals("Card Number")) {

				requestedCreditCard.setCardNumber(newInputs[1], databaseConnection);
			}
			if(fieldsToModify[2].equals("Expiration Date")) {
				requestedCreditCard.setExpirationDate(newInputs[2], databaseConnection);
			}
			if(fieldsToModify[3].equals("CVV")) {

				requestedCreditCard.setCvv(newInputs[3], databaseConnection);
			}
			if(fieldsToModify[4].equals("Billing Address")) {
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
			}
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

}

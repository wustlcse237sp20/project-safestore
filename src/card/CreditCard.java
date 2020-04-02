package card;

import java.sql.SQLException;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.AddressEntity;
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
		String encryptedCCNumber = Encryption.encrypt(creditCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		this.creditCardEntity = new CreditCardEntity(safeStoreUserEntity, nickname, encryptedCCNumber, encryptedExpDate, encryptedCvv, billingAddress.getAddressEntity());
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
		return this.creditCardEntity.getNickname();
	}
	
	public String getCreditCardNumber() {
		return Encryption.decrypt(this.creditCardEntity.getCreditCardNumber());
	}
	
	public String getExpirationDate() {
		return Encryption.decrypt(this.creditCardEntity.getExpirationDate());
	}
	
	public String getCvv() {
		return Encryption.decrypt(this.creditCardEntity.getCvv());
	}
	
	
	public void addCard(ConnectionSource databaseConnection) {
		if (this.billingAddress.addressExists(databaseConnection)) {
			this.billingAddress.updateToExistingAddress(databaseConnection);
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
		}
		else {
			this.billingAddress.addAddress(databaseConnection);
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
		}
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(this.creditCardEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void addCreditCard(ConnectionSource databaseConnection, Scanner keyboard, User safeStoreUser) {
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
	        creditCard.addCard(databaseConnection);
	        
	}
	
	public static void main(String[] args) {
		ConnectionSource databaseConnection;
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			databaseConnection = new JdbcConnectionSource(databaseUrl);
			@SuppressWarnings("resource")
			Scanner keyboard = new Scanner(System.in);
			System.out.println("Would you like to add a Website Account, Credit Card, or Debit Card?");
			String userInput = keyboard.nextLine();
			while (!userInput.trim().equals("WebsiteAccount") && !userInput.trim().equals("Credit Card") && !userInput.trim().equals("Debit Card")) {
				System.out.println("Enter 'Website Account', 'Credit Card', or 'Debit Card'");
		        userInput = keyboard.nextLine();
		    }
			if(userInput.equals("Credit Card")) {
				User safeStoreUser = new User("testUser", "testPassword");
				CreditCard.addCreditCard(databaseConnection, keyboard, safeStoreUser);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

package application;

import java.sql.SQLException;
import java.util.Scanner;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;

import card.CreditCard;
import card.DebitCard;
import tables.UserEntity;
import user.User;
import websiteAccount.WebsiteAccount;

//Simple program demonstrating how to give and receive command line info 
//To run program from terminal: 
//1. cd into src/application
//2. 'javac SafeStore.java'
//3. 'cd ..' back into src folder
//4. 'java application.SafeStore'  to run program
//When the program is inside a package, I couldn't find a better way. I know there's ways to run it with 
//a -cp flag but this seemed simpler, not sure if there's a setting that could be changed in another file 

public class SafeStore {
	
	public static void main(String[] args) {
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY, "ERROR");
		ConnectionSource databaseConnection;
		String databaseUrl = "jdbc:sqlite:database/app.db";
		User safeStoreUser;
		boolean userWantsToQuit = false;
		try {
			databaseConnection = new JdbcConnectionSource(databaseUrl);
			@SuppressWarnings("resource")
			Scanner keyboard = new Scanner(System.in);
			System.out.println("Type 'create' to create an account. Type 'login' to log into existing account.");
			String userInput = keyboard.nextLine();
			while (!userInput.trim().equals("create") && !userInput.trim().equals("login") && !userInput.trim().equals("quit")) {
		        System.out.println("Enter 'create' or 'login' ");
		        userInput = keyboard.nextLine();
		    }
			Scanner scanner = new Scanner(System.in);
			String username = "";
			
			//creating new account
			if(userInput.equals("create")) {
				username = User.createSafeStoreAccountTerminal(databaseConnection, scanner);
				if(username != null) {
					System.out.println("Successfully created account. Welcome, " + username);
				}
				else {
					System.out.println("Could not create an account.");
				}
			}
			else if (userInput.equals("quit")) {
				userWantsToQuit = true;
			}
			//logging in 
			else {
				username = User.terminalLogin(databaseConnection, scanner);
				if(username != null) {
					System.out.println("Welcome, " + username);
				}
				else {
					System.out.println("Username and password don't match our records.");
				}
			}
			Dao<UserEntity, String> userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			UserEntity userEntity = userDao.queryForId(username);
			safeStoreUser = new User(userEntity);
			while (!userWantsToQuit) {
				System.out.println("Type 'add' to create a new debit card, credit card, or website account");
				System.out.println("Type 'view' to create a new debit card, credit card, or website account");
				System.out.println("Type 'quit' to leave application");
				userInput = keyboard.nextLine();
				if (userInput.equals("add")) {
					System.out.println("Type which you want to create: 'debit card', 'credit card', 'web account': ");
					userInput = keyboard.nextLine();
					if (userInput.equals("debit card")) {
						DebitCard.addDebitCard(databaseConnection, keyboard, safeStoreUser);
					}
					else if (userInput.equals("credit card")) {
						CreditCard.addCreditCard(databaseConnection, keyboard, safeStoreUser);
					}
					else if (userInput.equals("web account")) {
						WebsiteAccount.addWebsiteAccountPrompts(databaseConnection, keyboard, safeStoreUser);
					}
					else if (userInput.equals("quit")) {
						userWantsToQuit = true;
					}
					else {
						continue;
					}
				}
				else if (userInput.equals("view")) {
					System.out.println("Type which you want to view: 'debit card', 'credit card', 'web account': ");
					userInput = keyboard.nextLine();
					if (userInput.equals("debit card")) {
						DebitCard.getDebitCardInformation(databaseConnection, keyboard, safeStoreUser);
					}
					else if (userInput.equals("credit card")) {
						CreditCard.getCreditCardInformation(databaseConnection, keyboard, safeStoreUser);
					}
					else if (userInput.equals("web account")) { 
						WebsiteAccount.viewWebsiteAccountInfo(databaseConnection, keyboard, safeStoreUser);
					}
					else if (userInput.equals("quit")) {
						userWantsToQuit = true;
					}
					else {
						continue;
					}
				}
				else if (userInput.equals("quit")) {
					break;
				}
				else {
					continue;
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
package application;

import java.sql.SQLException;
import java.util.Scanner;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import user.User;

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
		ConnectionSource databaseConnection;
		String databaseUrl = "jdbc:sqlite:database/app.db";
		try {
			databaseConnection = new JdbcConnectionSource(databaseUrl);
			@SuppressWarnings("resource")
			Scanner keyboard = new Scanner(System.in);
			System.out.println("Type 'create' to create an account. Type 'login' to log into existing account.");
			String userInput = keyboard.nextLine();
			while (!userInput.trim().equals("create") && !userInput.trim().equals("login")) {
		        System.out.println("Enter 'create' or 'login' ");
		        userInput = keyboard.nextLine();
		    }
			Scanner scanner = new Scanner(System.in);
			//creating new account
			if(userInput.equals("create")) {
				String username = User.createSafeStoreAccountTerminal(databaseConnection, scanner);
				if(username != null) {
					System.out.println("Successfully created account. Welcome, " + username);
				}
				else {
					System.out.println("Could not create an account.");
				}
			}
			//logging in 
			else {
				String username = User.terminalLogin(databaseConnection, scanner);
				if(username != null) {
					System.out.println("Welcome, " + username);
				}
				else {
					System.out.println("Username and password don't match our records.");
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
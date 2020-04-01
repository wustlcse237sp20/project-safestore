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
		ConnectionSource connectionSource;
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		try {
			connectionSource = new JdbcConnectionSource(databaseUrl);
			@SuppressWarnings("resource")
			Scanner keyboard = new Scanner(System.in);
			System.out.println("Type 'create' to create an account. Type 'login' to log into existing account.");
			String userInput = keyboard.nextLine();
			while (!userInput.trim().equals("create") && !userInput.trim().equals("login")) {
		        System.out.println("Enter 'view' or 'edit' ");
		        userInput = keyboard.nextLine();
		    }
			if(userInput.equals("create")) {
				System.out.println("Type your usernmae:");
				String username = keyboard.nextLine();
				while(username.trim() == "" || !User.isUniqueUsername(connectionSource, username)) {
					System.out.println("Your username cannot be empty and it must be unique. Try another.");
					username = keyboard.nextLine();
				}
				System.out.println("Type your password:");
				String password = keyboard.nextLine();
				while(password.trim() == "") {
					System.out.println("Your password cannot be empty.");
					password = keyboard.nextLine();
				}
				User newUser = new User(username, password);
				boolean createdNewUser = newUser.createSafeStoreAccount(connectionSource);
				if(createdNewUser) {
					System.out.println("Successfully created account. Welcome, " + username);
				}
				else {
					System.out.println("Could not create an account.");
				}
			}
			//logging in 
			else {
				
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
}
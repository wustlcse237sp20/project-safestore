package terminalInteraction;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

// a lot of code taken from https://stackoverflow.com/questions/6415728/junit-testing-with-simulated-user-input

// Simple program demonstrating how to give and receive command line info 
// To run program from terminal: 
// 1. cd into src/terminalInteraction
// 2. 'javac terminalInteraction.java'
// 3. 'cd ..' back into src folder
// 4. 'java terminalInteraction.terminalInteraction'  to run program
// When the program is inside a package, I couldn't find a better way. I know there's ways to run it with 
// a -cp flag but this seemed simpler, not sure if there's a setting that could be changed in another file 

public class terminalInteraction {

	public static void main(String[] args) {
		
		if(viewOrEditInfo(System.in, System.out).equals("view")) {
			
			viewInfo(System.in, System.out);
			return;
		}else {
			
			System.out.println("Ask more edit questions here.... \n");
			return;
		}
		
	}
	
	public static String viewOrEditInfo(InputStream in,PrintStream out) {
	    @SuppressWarnings("resource")
		Scanner keyboard = new Scanner(in);
	    out.println("Would you like to view information or edit information?");
	    String inputString = keyboard.nextLine();
	    while (!inputString.trim().equals("view") && !inputString.trim().equals("edit")) {
	        out.println("Enter 'view' or 'edit' ");
	        inputString = keyboard.nextLine();
	    }

	    return inputString;
	}
	public static void viewInfo(InputStream in, PrintStream out) {
		 @SuppressWarnings("resource")
			Scanner keyboard = new Scanner(in);
		    out.println("What info do you want to view (address or credit card)?");
		    String inputString = keyboard.nextLine();
		    while (!inputString.trim().equals("address") && !inputString.trim().equals("credit card")) {
		    	
		        out.println("Enter 'address' or 'credit card' ");
		        inputString = keyboard.nextLine();
		    }
		    if(inputString.equals("address")) {
		    	out.print("Address info here..... \n");
		    }else {
		    	out.print("Credit card info here..... \n");
		    }
	}
}
package terminalInteraction;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Scanner;

public class terminalInteraction {

	public static void main(String[] args) {
		
		testUserInput(System.in, System.out);
		
	}
	
	public static String testUserInput(InputStream in,PrintStream out) {
	    @SuppressWarnings("resource")
		Scanner keyboard = new Scanner(in);
	    out.println("Give a number between 1 and 10");
	    //int input = keyboard.nextInt();
	    String inputString = keyboard.next();
	    while (inputString != "view" || inputString != "edit") {
	        out.println("Enter 'view' or 'edit' ");
	        inputString = keyboard.next();
	    }

	    return inputString;
	}
}
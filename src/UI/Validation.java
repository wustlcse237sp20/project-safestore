package UI;

import java.util.regex.Pattern;

public class Validation {
	
	/**
	 * Simple regex to test for a valid card number that is between 13-16 digits
	 * This doesn't test for validation by card companies (like all Mastercards start
	 * with 51-55). It just tests to make sure it is the correct number of digits
	 * @param cardNumber
	 * @return true if the card number is all digits and 13-16 in length
	 */
	public static boolean validateCardNumber(String cardNumber) {
		return Pattern.matches("^\\d{13,16}$", cardNumber);
	}
	
	/**
	 * Simple regex to test for a valid expiration in the formats M/YY, MM/YY, M/YYYY, MM/YYYY
	 * It just checks for digits, it does NOT check to make sure the card is not expired or the year
	 * is whacky such as 1245
	 * @param expirationDate
	 * @return
	 */
	public static boolean validateExpirationDate(String expirationDate) {
		// format MM/YY
		boolean formatOne = Pattern.matches("^[0]\\d/\\d{2}$", expirationDate) || Pattern.matches("^[1][012]/\\d{2}$", expirationDate);
		
		// format MM/YYYY
		boolean formatTwo = Pattern.matches("^[0]\\d/\\d{4}$", expirationDate) || Pattern.matches("^[1][012]/\\d{4}$", expirationDate);
		
		// format M/YY
		boolean formatThree = Pattern.matches("^\\d/\\d{2}", expirationDate);
		
		// format M/YYYY
		boolean formatFour = Pattern.matches("^\\d/\\d{4}", expirationDate);

		return (formatOne || formatTwo || formatThree || formatFour);
	}
	
	/**
	 * Simple regex to check for a 3 or 4 digit CVV
	 * @param cvv
	 * @return
	 */
	public static boolean validateCvv(String cvv) {
		return Pattern.matches("^\\d{3,4}$", cvv);
	}
	
	/**
	 * Simple regex to check for a 4 digit pin number
	 * @param pin
	 * @return
	 */
	public static boolean validatePin(String pin) {
		return Pattern.matches("^\\d{4}$", pin);
	}
	
}

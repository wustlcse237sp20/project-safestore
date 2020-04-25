package validation;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import UI.Validation;
import card.DebitCard;
import tables.AddressEntity;
import tables.CreditCardEntity;
import tables.DebitCardEntity;
import tables.UserEntity;
import tables.WebsiteAccountEntity;

public class ValidationTests {
	ConnectionSource connectionSource;
	
	@Test
	void testValidateCardNumber() {
		System.out.println("RUNNING TEST: testValidateCardNumber");
		String testCases[] = {"1234567890123456", "1234-5678-9012-3456", " 2222333344445555", "2222333344445555 ", 
							  "123456789a123456", "bbbbeeeekkkkllll", "123456789012345", "1234567890123", "12345678901234",
							  "                ", "anotherbatdoneya"};
		boolean expectedResults[] = {true, false, false, false, false, false, true, true, true, false, false};
		for (int i = 0; i < testCases.length; ++i) {
			assertEquals(expectedResults[i], Validation.validateCardNumber(testCases[i]));
		}
		
	}
	
	@Test
	void testValidateCardExpDate() {
		System.out.println("RUNNING TEST: testValidateCardExpDate");
		String testCases[] = {"04/20", "4/20", "004/20", "13/20", "20/20", "4/1999", "15/2020", "12/2020", "4/20 ", " 5/20"};
		boolean expectedResults[] = {true, true, false, false, false, true, false, true, false, false};
		for (int i = 0; i < testCases.length; ++i) {
			assertEquals(expectedResults[i], Validation.validateExpirationDate(testCases[i]));
		}
	}
	
	@Test
	void testValidateCvv() {
		System.out.println("RUNNING TEST: testValidateCvv");
		String testCases[] = {"111", "2222", "2a2", "b22b", " 333", " 333", "44444", "11", "1"};
		boolean expectedResults[] = {true, true, false, false, false, false, false, false, false};
		for (int i = 0; i < testCases.length; ++i) {
			assertEquals(expectedResults[i], Validation.validateCvv(testCases[i]));
		}
	}
	
	@Test
	void testValidatePin() {
		System.out.println("RUNNING TEST: testValidatePin");
		String testCases[] = {"111", "2222", "2a2", "b22b", " 333", " 333", "44444", "11", "1"};
		boolean expectedResults[] = {false, true, false, false, false, false, false, false, false};
		for (int i = 0; i < testCases.length; ++i) {
			assertEquals(expectedResults[i], Validation.validatePin(testCases[i]));
		}
	}
	
}

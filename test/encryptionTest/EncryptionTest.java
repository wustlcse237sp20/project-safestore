package encryptionTest;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import encryption.Encryption;

class EncryptionTest {

	@Test
	void testEncryptAndDecrypt() {
		String [] originalStrings = {"testing", "234", "de23s", "!@#kdf", "20394705029384"};
		for (String stringToEncrypt : originalStrings) {
			String encryptedString = Encryption.encrypt(stringToEncrypt);
			String decryptedString = Encryption.decrypt(encryptedString);
			String failureMessage = "Original String: " + stringToEncrypt + " does not match Decrypted String: " + decryptedString;
			Assert.assertEquals(failureMessage, stringToEncrypt, decryptedString);
		}
		
	}

}

package encryptionTest;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import encryption.Encryption;

class EncryptionTest {
	
	private String [] plainTexts = {"cse327", "Franklin", "638294720", "m!@b3NdY"};
	private String [] cipherTexts = {"VsbQfiTkMTmYGXrXWLHitQ==", "Swo0Wun1jW3diBjSiQzPtg==", "bdTCLUY2d38dQzlWdO72Tw==" , "Z/tQKBXMwbmwQv8jSz2X7A=="};

	@Test
	void testEncrypt() {
		for (int i = 0; i < plainTexts.length; i++) {
			String encryptedString = Encryption.encrypt(plainTexts[i]);
			String failureMessage = "Expected encrypted string: " + cipherTexts[i] + ". But got: " + encryptedString;
			Assert.assertEquals(failureMessage, encryptedString, cipherTexts[i]);
		}
	}
	
	@Test
	void testDecrypt() {
		for (int i = 0; i < cipherTexts.length; i++) {
			String decryptedString = Encryption.decrypt(cipherTexts[i]);
			String failureMessage = "Expected decrypted string: " + plainTexts[i] + ". But got: " + decryptedString;
			Assert.assertEquals(failureMessage, decryptedString, plainTexts[i]);
		}
	}
	
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

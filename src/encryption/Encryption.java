package encryption;

import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.*;

public class Encryption {
	
	/* Code mostly gotten from: https://howtodoinjava.com/security/java-aes-encryption-example/
	 * Other files used to understand the code: 
	 * 	- https://docs.oracle.com/javase/7/docs/technotes/guides/security/StandardNames.html#SecretKeyFactory
	 *  - https://docs.oracle.com/javase/7/docs/api/javax/crypto/package-summary.html
	 *  - http://tutorials.jenkov.com/java-cryptography/index.html
	 */
	
	private static SecretKeySpec secretKey = generateKey();
	
	/**
	 * Generates the secret key so that it is not stored in plaintext in the code 
	 * @return the secret key to use for encryption
	 */
	private static SecretKeySpec generateKey() {
		String baseKey = "coronavirus";
		String salt = "d0ntb3s@lty";
		
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			KeySpec keySpecifications = new PBEKeySpec(baseKey.toCharArray(), salt.getBytes(), 65536 ,128);
	        SecretKey tmp = keyFactory.generateSecret(keySpecifications);
	        SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
	        return secretKey;
		} catch (Exception e) {
			System.out.println("Error while generating key: " + e.toString());
			e.printStackTrace();	
		}
        return null; 
	}
	
	/**
	 * Encrypts a string using AES-128
	 * @param stringToEncrypt plaintext string
	 * @return the encrypted string
	 */
	public static String encrypt(String stringToEncrypt) {
		try {
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	        IvParameterSpec ivspec = new IvParameterSpec(iv);
	         
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
	        return Base64.getEncoder().encodeToString(cipher.doFinal(stringToEncrypt.getBytes("UTF-8")));
		} catch (Exception e) {
			System.out.println("Error while encrypting: " + e.toString());
			e.printStackTrace();		
		}
		return null;
		
	}
	
	
	/**
	 * Decrypts a string that was encrypted using AES-128
	 * @param stringToDecrypt 
	 * @return the decrypted string into plaintext string 
	 */
	public static String decrypt(String stringToDecrypt) {
		try {	
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	        IvParameterSpec ivspec = new IvParameterSpec(iv);
	         
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
	        return new String(cipher.doFinal(Base64.getDecoder().decode(stringToDecrypt)));
	    } 
	    catch (Exception e) {
	        System.out.println("Error while decrypting: " + e.toString());
			e.printStackTrace();
	    }
	    return null;
	}

}

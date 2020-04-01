package user;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;

import tables.UserEntity;

public class User {
	private UserEntity userEntity;
	
	/**
	 * Constructor for logging into existing SafeStore User account
	 * @param userEntity record for SafeStore User account from UserEntity table
	 */
	public User(UserEntity userEntity) {
		this.userEntity = userEntity;
	}
	
	/**
	 * Constructor for when creating a new SafeStore User account 
	 * @param username
	 * @param password
	 */
	public User(String username, String password) {
		byte[] salt = getSalt();
		if(salt != null) {
			String hashPassword = getSecurePassword(password, salt);
			userEntity = new UserEntity();
			userEntity.setUsername(username);
			userEntity.setPasswordHashed(hashPassword);
			userEntity.setSalt(salt.toString());
		}
	}
	
	public UserEntity getUserEntity() {
		return userEntity;
	}
	
	/**
	 * 
	 * @param databaseSource to connect to UserEntity table 
	 * @param username to check against usernames in UserEntity table
	 * @return true if unique username is UserEntity table
	 */
	public static boolean isUniqueUsername(ConnectionSource databaseSource, String username) {
		try {
			Dao<UserEntity, String> userEntityDao = DaoManager.createDao(databaseSource, UserEntity.class);
			UserEntity matchedUser = userEntityDao.queryForId(username);
			if(matchedUser == null) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 *  method from: 
	 *  https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	 * @return random md5 salt
	 * @throws NoSuchAlgorithmException
	 */
	private static byte[] getSalt()
	{
	    //Always use a SecureRandom generator
	    SecureRandom sr;
		try {
			sr = SecureRandom.getInstance("SHA1PRNG");
			byte[] salt = new byte[16];
		    //Get a random salt
		    sr.nextBytes(salt);
		    return salt;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * method from: 
	 * https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/
	 * @param passwordToHash user plaintext password
	 * @param salt randomly generated from getSalt()
	 * @return
	 */
	private static String getSecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            // Create MessageDigest instance for MD5
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            //This bytes[] has bytes in decimal format;
            //Convert it to hexadecimal format
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            //Get complete hashed password in hex format
            generatedPassword = sb.toString();
        } 
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return generatedPassword;
    }
	
	/**
	 * Tries to input new SafeStore user account info into database
	 * @param databaseSource to connect to UserEntity table 
	 * @return true if safe store account successfully created 
	 */
	public boolean createSafeStoreAccount(ConnectionSource databaseSource) {
		try {
			Dao<UserEntity, String> userEntityDao = DaoManager.createDao(databaseSource, UserEntity.class);
			//returns number of records user entity DAO created
			//Can only be 1 or 0 here as username is id
			int numRecordsCreated = userEntityDao.create(userEntity);
			if(numRecordsCreated == 1) {
				return true;
			}
			else {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

}
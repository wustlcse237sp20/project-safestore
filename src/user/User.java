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
	
	public User(UserEntity userEntity) {
		this.userEntity = userEntity;
	}
	
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
			// TODO Auto-generated catch block
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
            //Add password bytes to digest
            md.update(salt);
            //Get the hash's bytes 
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
	 * 
	 * @param connectionSource to connect to UserEntity table 
	 * @return true if safe store account successfully created 
	 */
	public boolean createSafeStoreAccount(ConnectionSource connectionSource) {
		try {
			Dao<UserEntity, String> userEntityDao = DaoManager.createDao(connectionSource, UserEntity.class);
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

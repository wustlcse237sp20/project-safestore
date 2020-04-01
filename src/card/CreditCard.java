package card;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.AddressEntity;
import tables.CreditCardEntity;
import tables.UserEntity;
import user.User;

public class CreditCard implements Card{
	
	private CreditCardEntity creditCardEntity;
	private Address billingAddress;
	
	public CreditCard(User safeStoreUser, String creditCardNumber, String expirationDate, String cvv, Address billingAddress) {
		String defaultNickname = creditCardNumber.substring(creditCardNumber.length() - 4, creditCardNumber.length());
		defaultNickname = Encryption.encrypt(defaultNickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedCCNumber = Encryption.encrypt(creditCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		this.creditCardEntity = new CreditCardEntity(safeStoreUserEntity, defaultNickname, encryptedCCNumber, encryptedExpDate, encryptedCvv, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
	}
	
	public CreditCard(UserEntity safeStoreUser, String nickname, String creditCardNumber, String expirationDate, String cvv, Address billingAddress) {
		this.creditCardEntity = new CreditCardEntity(safeStoreUser, nickname, creditCardNumber, expirationDate, cvv, billingAddress.getAddressEntity());
	}
	
	public CreditCard(CreditCardEntity creditCardEntity) {
		this.creditCardEntity = creditCardEntity;
		this.billingAddress = new Address(creditCardEntity.getBillingAddress());
	}
	
	public CreditCardEntity getCreditCardEntity() {
		return this.creditCardEntity;
	}
	
	public String getNickname() {
		return this.creditCardEntity.getNickname();
	}
	
	public String getCreditCardNumber() {
		return Encryption.decrypt(this.creditCardEntity.getCreditCardNumber());
	}
	
	public String getExpirationDate() {
		return Encryption.decrypt(this.creditCardEntity.getExpirationDate());
	}
	
	public String getCvv() {
		return Encryption.decrypt(this.creditCardEntity.getCvv());
	}
	
	
	public void addCard(ConnectionSource databaseConnection) {
		if (this.billingAddress.addressExists(databaseConnection)) {
			this.billingAddress.updateToExistingAddress(databaseConnection);
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
		}
		else {
			this.billingAddress.addAddress(databaseConnection);
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
		}
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(databaseConnection, CreditCardEntity.class);
			creditCardDao.create(this.creditCardEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}

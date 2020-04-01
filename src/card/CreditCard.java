package card;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import tables.AddressEntity;
import tables.CreditCardEntity;
import tables.UserEntity;

public class CreditCard implements Card{
	
	private CreditCardEntity creditCardEntity;
	private Address billingAddress;
	
	public CreditCard(UserEntity safeStoreUser, String creditCardNumber, String expirationDate, String cvv, Address billingAddress) {
		String defaultNickname = creditCardNumber.substring(creditCardNumber.length() - 4, creditCardNumber.length());
		this.creditCardEntity = new CreditCardEntity(safeStoreUser, defaultNickname, creditCardNumber, expirationDate, cvv, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
	}
	
	public CreditCard(UserEntity safeStoreUser, String nickname, String creditCardNumber, String expirationDate, String cvv, Address billingAddress) {
		this.creditCardEntity = new CreditCardEntity(safeStoreUser, nickname, creditCardNumber, expirationDate, cvv, billingAddress.getAddressEntity());
	}
	
	public void addCard(ConnectionSource connectionSource) {
		if (this.billingAddress.addressExists(connectionSource)) {
			this.billingAddress.updateToExistingAddress(connectionSource);
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
		}
		else {
			this.billingAddress.addAddress(connectionSource);
			this.creditCardEntity.setBillingAddress(this.billingAddress.getAddressEntity());
		}
		
		try {
			Dao<CreditCardEntity, String> creditCardDao = DaoManager.createDao(connectionSource, CreditCardEntity.class);
			int result = creditCardDao.create(this.creditCardEntity);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

}

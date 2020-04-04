package card;

import com.j256.ormlite.support.ConnectionSource;

import encryption.Encryption;
import tables.CreditCardEntity;
import tables.DebitCardEntity;
import tables.UserEntity;
import user.User;

public class DebitCard implements Card {

	private DebitCardEntity debitCardEntity;
	private Address billingAddress;
	
	public DebitCard(User safeStoreUser, String debitCardNumber, String expirationDate, String cvv, String pin, Address billingAddress) {
		String defaultNickname = debitCardNumber.substring(debitCardNumber.length() - 4, debitCardNumber.length());
		defaultNickname = Encryption.encrypt(defaultNickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedCCNumber = Encryption.encrypt(debitCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		String encryptedPin = Encryption.encrypt(pin);
		this.debitCardEntity = new DebitCardEntity(safeStoreUserEntity, defaultNickname, encryptedCCNumber, encryptedExpDate, encryptedCvv, encryptedPin, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
		
	}
	public DebitCard(User safeStoreUser, String nickname, String debitCardNumber, String expirationDate, String cvv, String pin, Address billingAddress) {
		
		String encryptedNickname = Encryption.encrypt(nickname);
		UserEntity safeStoreUserEntity = safeStoreUser.getUserEntity();
		String encryptedCCNumber = Encryption.encrypt(debitCardNumber);
		String encryptedExpDate = Encryption.encrypt(expirationDate);
		String encryptedCvv = Encryption.encrypt(cvv);
		String encryptedPin = Encryption.encrypt(pin);
		this.debitCardEntity = new DebitCardEntity(safeStoreUserEntity, encryptedNickname, encryptedCCNumber, encryptedExpDate, encryptedCvv, encryptedPin, billingAddress.getAddressEntity());
		this.billingAddress = billingAddress;
		
	}
	public DebitCard(DebitCardEntity debitCardEntity) {
		// TODO Auto-generated constructor stub
	}
	@Override
	public String getNickname() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCardNumber() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getExpirationDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCvv() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getZipCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBillingAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setNickname(String nickname, ConnectionSource databaseConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setCardNumber(String cardNumber, ConnectionSource databaseConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setExpirationDate(String expirationDate, ConnectionSource databaseConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setCvv(String cvv, ConnectionSource databaseConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setBillingAddress(Address billingAddress, ConnectionSource databaseConnection) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addCard(ConnectionSource databaseConnection) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public DebitCardEntity getDebitCardEntity() {
		// TODO Auto-generated method stub
		return null;
	}

}

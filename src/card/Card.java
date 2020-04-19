package card;

import com.j256.ormlite.support.ConnectionSource;


public interface Card {
	
	public String getNickname();
	
	public String getCardNumber();
	
	public String getExpirationDate();
	
	public String getCvv();
	
	public String getZipCode();
	
	public String getBillingAddress();
	
	public String toString();
	
	public boolean setNickname(String nickname, ConnectionSource databaseConnection);
	
	public boolean setCardNumber(String cardNumber, ConnectionSource databaseConnection);
	
	public boolean setExpirationDate(String expirationDate, ConnectionSource databaseConnection);
	
	public boolean setCvv(String cvv, ConnectionSource databaseConnection);
	
	public boolean setBillingAddress(Address billingAddress, ConnectionSource databaseConnection);
	
	public boolean addCard(ConnectionSource databaseConnection) throws Exception;
	
	
}

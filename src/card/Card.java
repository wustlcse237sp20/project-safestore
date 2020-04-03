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
	
	public boolean addCard(ConnectionSource databaseConnection) throws Exception;
	
}

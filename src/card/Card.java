package card;

import com.j256.ormlite.support.ConnectionSource;

public interface Card {
	
	public void addCard(ConnectionSource databaseConnection);
}

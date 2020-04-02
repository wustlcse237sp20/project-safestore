package card;

import com.j256.ormlite.support.ConnectionSource;

public interface Card {
	
	public boolean addCard(ConnectionSource databaseConnection);
}

package UI;

import java.sql.SQLException;
import java.util.LinkedList;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;

import card.Address;
import card.CreditCard;
import card.DebitCard;
import encryption.Encryption;
import tables.CreditCardEntity;
import tables.DebitCardEntity;
import tables.UserEntity;
import tables.WebsiteAccountEntity;
import user.User;
import websiteAccount.WebsiteAccount;

public class SafeStore {
	static ConnectionSource databaseConnection;
	static String databaseUrl;
	static User safeStoreUser;
	
	public static void main(String[] args) {
		// TODO Auto-generated constructor stub
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY,"ERROR");
		databaseUrl = "jdbc:sqlite:database/app.db";
		try {
			databaseConnection = new JdbcConnectionSource(databaseUrl);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		//TODO: Maybe have a method within UserSignInWindow to check if logged in, if logged in then launch the FrontEnd and close the UserSignInWindow? 
		UserSignInWindow.launchWindow();
		//FrontEnd.launchWindow();
	}

	public static boolean createUser(String username, String password) {
		User newUser = new User(username, password);
		return newUser.createSafeStoreAccountThroughDatabase(databaseConnection);
	}
	
	public static boolean loginUser(String username, String password) {
		return User.loginThroughDatabase(databaseConnection, username, password);		
	}
	
	public static void setUserForSession(String username) {
		Dao<UserEntity, String> userDao;
		try {
			userDao = DaoManager.createDao(databaseConnection, UserEntity.class);
			UserEntity userEntity = userDao.queryForId(username);
			safeStoreUser = new User(userEntity);
			UserSignInWindow.closeWindow();
			FrontEnd.launchWindow();
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void logout() {
		FrontEnd.closeWindow();
		safeStoreUser = null;
		UserSignInWindow.launchWindow();
	}
	
	public static String[] getUsersWebsites() {
		ForeignCollection<WebsiteAccountEntity> websiteEntities = WebsiteAccount.getAllWebsiteAccounts(databaseConnection, safeStoreUser);
		LinkedList<String> websiteNicknamesList = new LinkedList<String>();
		for(WebsiteAccountEntity websiteEntity :  websiteEntities) {
			websiteNicknamesList.add(Encryption.decrypt(websiteEntity.getNickname()));
		}
		String[] websiteNicknamesArray = websiteNicknamesList.toArray(new String[0]);
		
		return websiteNicknamesArray;
	}
	
	public static WebsiteAccount getWebsiteAccountInfo(String nickname) {
		WebsiteAccount website = null;
		try {
			website = WebsiteAccount.getWebsiteAccountFromNickname(databaseConnection, nickname, safeStoreUser);
		} catch (Exception e) {

		}
		return website;
	}
	
	public static boolean addWebsiteAccount(String nickname, String username, String password) {
		WebsiteAccount websiteAccount = new WebsiteAccount(safeStoreUser, nickname, username, password);
		return websiteAccount.addWebsiteAccount(databaseConnection);
	}

	public static boolean modifyWebsiteAccount(String currentNickname, String newNickname, String newLogin, String newPassword) {
		String[] newInputs = {newNickname,newLogin, newPassword};
		return WebsiteAccount.updateWebsiteAccount(databaseConnection, currentNickname, safeStoreUser, newInputs);
	}

	public static CreditCard getCreditCardInfo(String nickname) {
		CreditCard creditCard = null;
		try {
			creditCard = CreditCard.getCreditCardFromNickname(nickname, safeStoreUser, databaseConnection);
		} catch (Exception e) {

		}
		return creditCard;

	}
	public static String[] getUsersCreditCards() {
		ForeignCollection<CreditCardEntity> creditCardEntities = CreditCard.getAllCreditCards(databaseConnection, safeStoreUser);
		LinkedList<String> creditCardNicknamesList = new LinkedList<String>();
		for(CreditCardEntity creditCardEntity :  creditCardEntities) {
			creditCardNicknamesList.add(Encryption.decrypt(creditCardEntity.getNickname()));
		}
		String[] creditCardNicknamesArray = creditCardNicknamesList.toArray(new String[0]);
		
		return creditCardNicknamesArray;
		
	}
	
	public static boolean addCreditCard(String cardNumber, String nickname,String expDate, String cvv, String streetAddress, String city, String state, String zip) {
		Address billingAddress = new Address(streetAddress, city, state, zip);
		CreditCard creditCard;
		if(!nickname.isEmpty()) {
			creditCard = new CreditCard(safeStoreUser, nickname, cardNumber, expDate, cvv, billingAddress);
		}
		else {
			creditCard = new CreditCard(safeStoreUser, cardNumber, expDate, cvv, billingAddress);
		}
		try {
			return creditCard.addCard(databaseConnection);
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean modifyCreditCard(String currentNickname, String nickname, String cardNumber,String expDate, String cvv, String streetAddress, String city, String state, String zip) {
		String[] newInputs = {nickname,cardNumber,expDate,cvv,streetAddress,city,state,zip};
		return CreditCard.updateCreditCardInformation(currentNickname, databaseConnection, safeStoreUser, newInputs);
	}
	
	public static DebitCard getDebitCardInfo(String nickname) {
		DebitCard debitCard = null;
		try {
			debitCard = DebitCard.getDebitCardFromNickname(nickname, safeStoreUser, databaseConnection);
		} catch (Exception e) {

		}
		return debitCard;

	}
	
	public static boolean addDebitCard(String cardNumber, String nickname, String expDate, String cvv, String pin, String streetAddress, String city, String state, String zip) {
		Address billingAddress = new Address(streetAddress, city, state, zip);
		DebitCard debitCard;
		if(!nickname.isEmpty()) {
			debitCard = new DebitCard(safeStoreUser, nickname, cardNumber, expDate, cvv, pin, billingAddress);
		}
		else {
			debitCard = new DebitCard(safeStoreUser, cardNumber, expDate, cvv, pin, billingAddress);
		}
		try {
			return debitCard.addCard(databaseConnection);
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean modifyDebitCard(String currentNickname, String nickname, String cardNumber,String expDate, String cvv, String pin, String streetAddress, String city, String state, String zip) {
		String[] newInputs = {nickname,cardNumber,expDate,cvv,pin,streetAddress,city,state,zip};
		return DebitCard.updateDebitCardInformation(currentNickname, databaseConnection, safeStoreUser, newInputs);
	}

	public static String[] getUsersDebitCards() {
		ForeignCollection<DebitCardEntity> debitCardEntities = DebitCard.getAllDebitCards(databaseConnection, safeStoreUser);
		LinkedList<String> debitCardNicknamesList = new LinkedList<String>();
		for(DebitCardEntity debitCardEntity :  debitCardEntities) {
			debitCardNicknamesList.add(Encryption.decrypt(debitCardEntity.getNickname()));
		}
		String[] debitCardNicknamesArray = debitCardNicknamesList.toArray(new String[0]);
		
		return debitCardNicknamesArray;
		
	}

}

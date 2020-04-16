package UI;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;

import tables.UserEntity;
import user.User;
import websiteAccount.WebsiteAccount;

public class UIController {
	static ConnectionSource databaseConnection;
	static String databaseUrl;
	static User safeStoreUser;
	public static void main(String[] args) {
		// TODO Auto-generated constructor stub
		System.setProperty(LocalLog.LOCAL_LOG_LEVEL_PROPERTY,"ERROR");
		databaseUrl = "jdbc:sqlite:src/database/app.db";
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
	public static WebsiteAccount getWebsiteAccountInfo(String nickname) {
			WebsiteAccount website = null;
			try {
				 website = WebsiteAccount.getWebsiteAccountFromNickname(databaseConnection, nickname, safeStoreUser);
			} catch (Exception e) {
				
			}
			return website;
	}

}

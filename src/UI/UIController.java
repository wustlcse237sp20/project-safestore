package UI;

import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;

import user.User;

public class UIController {
	static ConnectionSource databaseConnection;
	static String databaseUrl;
	
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
		UserSignInWindow.launchWindow();
	}
	
	public static boolean createUser(String username, String password) {
		User newUser = new User(username, password);
		return newUser.createSafeStoreAccountThroughDatabase(databaseConnection);
		
	}
	public static boolean loginUser(String username, String password) {
		return User.loginThroughDatabase(databaseConnection, username, password);		
	}

}

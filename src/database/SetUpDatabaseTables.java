package database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import tables.Address;
import tables.CreditCard;
import tables.DebitCard;
import tables.User;
import tables.WebsiteAccount;

public class SetUpDatabaseTables {
	
	// this will print out the create table query statements without the foreign key constraints
	//	due to lack of functionality. You need to add the foreign keys as necessary if you want
	// 	them in the database for SQLite
	public static void main(String[] args) {
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		ConnectionSource connectionSource;
		try {
			connectionSource = new JdbcConnectionSource(databaseUrl);
			List<String> userTableClassCreation = TableUtils.getCreateTableStatements(connectionSource, User.class);
			List<String> addressTableClassCreation = TableUtils.getCreateTableStatements(connectionSource, Address.class);
			List<String> websiteAccountTableClassCreation = TableUtils.getCreateTableStatements(connectionSource, WebsiteAccount.class);
			List<String> creditCardTableClassCreation = TableUtils.getCreateTableStatements(connectionSource, CreditCard.class);
			List<String> debitCardTableClassCreation = TableUtils.getCreateTableStatements(connectionSource, DebitCard.class);
			System.out.println(userTableClassCreation);
			System.out.println(addressTableClassCreation);
			System.out.println(websiteAccountTableClassCreation);
			System.out.println(creditCardTableClassCreation);
			System.out.println(debitCardTableClassCreation);
			connectionSource.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

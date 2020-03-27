package database;

import java.io.IOException;
import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import tables.Address;
import tables.CreditCard;
import tables.DebitCard;
import tables.User;
import tables.WebsiteAccount;

public class SetUpDatabaseTables {

	public static void main(String[] args) {
		String databaseUrl = "jdbc:sqlite:src/database/app.db";
		ConnectionSource connectionSource;
		try {
			connectionSource = new JdbcConnectionSource(databaseUrl);
			TableUtils.createTable(connectionSource, User.class);
			TableUtils.createTable(connectionSource, Address.class);
			TableUtils.createTable(connectionSource, WebsiteAccount.class);
			TableUtils.createTable(connectionSource, CreditCard.class);
			TableUtils.createTable(connectionSource, DebitCard.class);
			connectionSource.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InsertRecords {
	
	/**
	 * 
	 * @param safe_store_username
	 * @param safe_store_pswd
	 */
	public static void insertSafeStoreUser(String safe_store_username, String safe_store_pswd) {
		String sql = "INSERT INTO employees(safe_store_username, safe_store_pswd, salt) VALUES(?,?,?)";  
		String salt = "temporarily unsafe";
        try{  
            Connection conn = Connect.connect();  
            PreparedStatement pstmt = conn.prepareStatement(sql);  
            pstmt.setString(1, safe_store_username);  
            pstmt.setString(2, safe_store_pswd);  
            pstmt.setString(3, salt);
            pstmt.executeUpdate();  
            conn.close();
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }  
	}
	
	public static void insertWebAccount(String safe_store_username, String website_name, String website_login, String website_pswd) {
		
	}
	
	/**
	 * Insert credit card with a stated nickname 
	 * @param safe_store_username
	 * @param cc_nickname
	 * @param cc_num
	 * @param expire_date
	 * @param cvv
	 * @param address_id
	 */
	public static void insertCreditCard(String safe_store_username, String cc_nickname, String cc_num, String expire_date, int cvv, int address_id) {
			
	}
	
	/**
	 * Insert credit card with no stated nickname
	 * @param safe_store_username
	 * @param cc_num
	 * @param expire_date
	 * @param cvv
	 * @param address_id
	 */
	public static void insertCreditCard(String safe_store_username, String cc_num, String expire_date, int cvv, int address_id) {
		
	}
	/**
	 * insert debit card with a stated nickname
	 * @param safe_store_username
	 * @param dc_nickname
	 * @param dc_num
	 * @param expire_date
	 * @param cvv
	 * @param pin
	 * @param address_id
	 */
	public static void insertDebitCard(String safe_store_username, String dc_nickname, String dc_num, String expire_date, int cvv, int pin, int address_id) {
		
	}
	/**
	 * insert debit card with no stated nickname
	 * @param safe_store_username
	 * @param dc_num
	 * @param expire_date
	 * @param cvv
	 * @param pin
	 * @param address_id
	 */
	public static void insertDebitCard(String safe_store_username, String dc_num, String expire_date, int cvv, int pin, int address_id) {
		
	}
	
	/**
	 * 
	 * @param street_address
	 * @param city
	 * @param state
	 * @param zip
	 */
	public static void insertAddress(String street_address, String city, String state, int zip) {
		
	}
}

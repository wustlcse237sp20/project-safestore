package UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.SpringLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Color;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcDatabaseConnection;
import com.j256.ormlite.logger.LocalLog;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.support.DatabaseConnection;

import user.User;

import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPasswordField;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.ComponentOrientation;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.awt.event.ActionEvent;

public class UserSignInWindow {

	private JFrame frame;
	private JTextField usernameField;
	private JPasswordField passwordField;
	
	JTextField newUsername = new JTextField(10);
	JPasswordField newPassword = new JPasswordField(10);
	/**
	 * Launch the application.
	 */
	public static void launchWindow() {

	
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					UserSignInWindow window = new UserSignInWindow();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public UserSignInWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);

		JLabel lblSafestore = new JLabel("SAFESTORE");
		springLayout.putConstraint(SpringLayout.WEST, lblSafestore, 120, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblSafestore, -120, SpringLayout.EAST, frame.getContentPane());
		lblSafestore.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		lblSafestore.setFont(new Font("Marker Felt", Font.PLAIN, 35));
		springLayout.putConstraint(SpringLayout.NORTH, lblSafestore, 39, SpringLayout.NORTH, frame.getContentPane());
		lblSafestore.setHorizontalAlignment(SwingConstants.CENTER);
		lblSafestore.setForeground(Color.RED);
		frame.getContentPane().add(lblSafestore);

		JLabel lblLogin = new JLabel("Login");
		springLayout.putConstraint(SpringLayout.NORTH, lblLogin, 15, SpringLayout.SOUTH, lblSafestore);
		springLayout.putConstraint(SpringLayout.WEST, lblLogin, 50, SpringLayout.WEST, lblSafestore);
		springLayout.putConstraint(SpringLayout.EAST, lblLogin, -50, SpringLayout.EAST, lblSafestore);
		lblLogin.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblLogin.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(lblLogin);

		usernameField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, usernameField, 5, SpringLayout.SOUTH, lblLogin);
		springLayout.putConstraint(SpringLayout.WEST, usernameField, 0, SpringLayout.WEST, lblLogin);
		springLayout.putConstraint(SpringLayout.EAST, usernameField, 0, SpringLayout.EAST, lblLogin);
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);

		passwordField = new JPasswordField();
		springLayout.putConstraint(SpringLayout.NORTH, passwordField, 5, SpringLayout.SOUTH, usernameField);
		springLayout.putConstraint(SpringLayout.WEST, passwordField, 0, SpringLayout.WEST, usernameField);
		springLayout.putConstraint(SpringLayout.EAST, passwordField, 0, SpringLayout.EAST, usernameField);
		frame.getContentPane().add(passwordField);
		passwordField.setColumns(10);

		JLabel lblUsername = new JLabel("Username");
		springLayout.putConstraint(SpringLayout.NORTH, lblUsername, 0, SpringLayout.NORTH, usernameField);
		springLayout.putConstraint(SpringLayout.SOUTH, lblUsername, 0, SpringLayout.SOUTH, usernameField);
		springLayout.putConstraint(SpringLayout.EAST, lblUsername, 0, SpringLayout.WEST, usernameField);
		frame.getContentPane().add(lblUsername);

		JLabel lblPassword = new JLabel("Password");
		springLayout.putConstraint(SpringLayout.NORTH, lblPassword, 5, SpringLayout.SOUTH, lblUsername);
		springLayout.putConstraint(SpringLayout.SOUTH, lblPassword, 0, SpringLayout.SOUTH, passwordField);
		springLayout.putConstraint(SpringLayout.EAST, lblPassword, 0, SpringLayout.WEST, passwordField);
		frame.getContentPane().add(lblPassword);

		JButton btnEnter = new JButton("Enter");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				char[] password = passwordField.getPassword();
				String passwordString = String.valueOf(password);
				
				if(!passwordString.isEmpty() && !username.isEmpty()) {
					boolean login = UIController.loginUser(username, passwordString); 
					if(login) {
						// NEXT WINDOW
						System.out.println("success");
						
					}
					else {
						JOptionPane.showMessageDialog(frame, "Username or password incorrect");
					}
				}


			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnEnter, 5, SpringLayout.SOUTH, passwordField);
		springLayout.putConstraint(SpringLayout.WEST, btnEnter, 20, SpringLayout.WEST, usernameField);
		springLayout.putConstraint(SpringLayout.EAST, btnEnter, -20, SpringLayout.EAST, usernameField);
		frame.getContentPane().add(btnEnter);

		JButton btnCreateAccount = new JButton("Create Account");
		btnCreateAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// popup window stuff: https://stackoverflow.com/questions/12810460/joptionpane-input-dialog-box-program
				
				JPanel basePanel = new JPanel();
				
				basePanel.setOpaque(true);
				basePanel.setBackground(Color.RED);

				JPanel centerPanel = new JPanel();
				centerPanel.setLayout(new GridLayout(3, 2, 5, 5));
				centerPanel.setBorder(
						BorderFactory.createEmptyBorder(5, 5, 5, 5));
				centerPanel.setOpaque(true);
				centerPanel.setBackground(Color.WHITE);



				centerPanel.add(new JLabel("Username "));
				centerPanel.add(newUsername);
				centerPanel.add(new JLabel("Password "));
				centerPanel.add(newPassword);

				basePanel.add(centerPanel);
				boolean creatingUser = true;
				while(creatingUser){
					int selection = JOptionPane.showConfirmDialog(frame, basePanel, "New Account: ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

					if(selection == JOptionPane.OK_OPTION) {

						if(newUsername.getText().isEmpty() || String.valueOf(newPassword.getPassword()).isEmpty()) {
							JOptionPane.showMessageDialog(frame, "Username and password must contain values");
						}else {
							
							
							boolean createdNewUser = UIController.createUser(newUsername.getText(),String.valueOf(newPassword.getPassword())); 
							if(createdNewUser) {
								creatingUser = false;
								//Go to next window
							}else {
								JOptionPane.showMessageDialog(frame, "Username taken, choose another");
							}
						}
						
					}else if(selection == JOptionPane.CANCEL_OPTION){
						creatingUser = false;
					}

				}
			}
		});
		btnCreateAccount.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		springLayout.putConstraint(SpringLayout.SOUTH, btnCreateAccount, -6, SpringLayout.NORTH, lblSafestore);
		springLayout.putConstraint(SpringLayout.EAST, btnCreateAccount, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(btnCreateAccount);

	}
}
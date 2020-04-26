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

import javax.swing.JTextField;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import java.awt.ComponentOrientation;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class UserSignInWindow {

	private static JFrame frame;
	private JTextField usernameField;
	private JPasswordField passwordField;	
	JTextField newUsername = new JTextField(10);
	JPasswordField newPassword = new JPasswordField(10);
	JPasswordField repeatPassword = new JPasswordField(10);

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

		JLabel safestoreLabel = new JLabel("SAFESTORE");
		springLayout.putConstraint(SpringLayout.WEST, safestoreLabel, 120, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, safestoreLabel, -120, SpringLayout.EAST, frame.getContentPane());
		safestoreLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		safestoreLabel.setFont(new Font("Marker Felt", Font.PLAIN, 35));
		springLayout.putConstraint(SpringLayout.NORTH, safestoreLabel, 39, SpringLayout.NORTH, frame.getContentPane());
		safestoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		safestoreLabel.setForeground(Color.RED);
		frame.getContentPane().add(safestoreLabel);

		JLabel loginLabel = new JLabel("Login");
		springLayout.putConstraint(SpringLayout.NORTH, loginLabel, 15, SpringLayout.SOUTH, safestoreLabel);
		springLayout.putConstraint(SpringLayout.WEST, loginLabel, 50, SpringLayout.WEST, safestoreLabel);
		springLayout.putConstraint(SpringLayout.EAST, loginLabel, -50, SpringLayout.EAST, safestoreLabel);
		loginLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(loginLabel);

		usernameField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, usernameField, 5, SpringLayout.SOUTH, loginLabel);
		springLayout.putConstraint(SpringLayout.WEST, usernameField, 0, SpringLayout.WEST, loginLabel);
		springLayout.putConstraint(SpringLayout.EAST, usernameField, 0, SpringLayout.EAST, loginLabel);
		frame.getContentPane().add(usernameField);
		usernameField.setColumns(10);

		passwordField = new JPasswordField();
		springLayout.putConstraint(SpringLayout.NORTH, passwordField, 5, SpringLayout.SOUTH, usernameField);
		springLayout.putConstraint(SpringLayout.WEST, passwordField, 0, SpringLayout.WEST, usernameField);
		springLayout.putConstraint(SpringLayout.EAST, passwordField, 0, SpringLayout.EAST, usernameField);
		frame.getContentPane().add(passwordField);
		passwordField.setColumns(10);

		JLabel usernameLabel = new JLabel("Username");
		springLayout.putConstraint(SpringLayout.NORTH, usernameLabel, 0, SpringLayout.NORTH, usernameField);
		springLayout.putConstraint(SpringLayout.SOUTH, usernameLabel, 0, SpringLayout.SOUTH, usernameField);
		springLayout.putConstraint(SpringLayout.EAST, usernameLabel, 0, SpringLayout.WEST, usernameField);
		frame.getContentPane().add(usernameLabel);

		JLabel passwordLabel = new JLabel("Password");
		springLayout.putConstraint(SpringLayout.NORTH, passwordLabel, 5, SpringLayout.SOUTH, usernameLabel);
		springLayout.putConstraint(SpringLayout.SOUTH, passwordLabel, 0, SpringLayout.SOUTH, passwordField);
		springLayout.putConstraint(SpringLayout.EAST, passwordLabel, 0, SpringLayout.WEST, passwordField);
		frame.getContentPane().add(passwordLabel);

		JButton enterButton = new JButton("Enter");
		enterButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				char[] password = passwordField.getPassword();
				String passwordString = String.valueOf(password);

				if(!passwordString.isEmpty() && !username.isEmpty()) {
					boolean isLoggedIn = SafeStore.loginUser(username, passwordString); 
					if(isLoggedIn) {
						// NEXT WINDOW
						SafeStore.setUserForSession(username);
					}
					else {
						JOptionPane.showMessageDialog(frame, "Username or password incorrect");
					}
				}


			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, enterButton, 5, SpringLayout.SOUTH, passwordField);
		springLayout.putConstraint(SpringLayout.WEST, enterButton, 20, SpringLayout.WEST, usernameField);
		springLayout.putConstraint(SpringLayout.EAST, enterButton, -20, SpringLayout.EAST, usernameField);
		frame.getContentPane().add(enterButton);

		JButton createAccountButton = new JButton("Create Account");
		createAccountButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// popup window stuff: https://stackoverflow.com/questions/12810460/joptionpane-input-dialog-box-program
				boolean creatingUser = true;
				while (creatingUser) {
					int userSelection = JOptionPane.showConfirmDialog(frame, createBasePanel(), "New Account: ", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
					if (userSelection == JOptionPane.OK_OPTION) {
						String username = newUsername.getText();
						String password = String.valueOf(newPassword.getPassword());
						String confirmPassword = String.valueOf(repeatPassword.getPassword());
						
						if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
							JOptionPane.showMessageDialog(frame, "Username and both password fields must contain values");
						} else if (!password.equals(confirmPassword)) {
							JOptionPane.showMessageDialog(frame, "Passwords do not match");
						} else {
							boolean createdNewUser = SafeStore.createUser(username, password); 
							if (createdNewUser) {
								creatingUser = false;
								SafeStore.setUserForSession(username); // go to main account window
							} else {
								JOptionPane.showMessageDialog(frame, "Username taken, choose another");
							}
						}
					} else if (userSelection == JOptionPane.CANCEL_OPTION){
						creatingUser = false;
					}
				}
			}
		});
		createAccountButton.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		springLayout.putConstraint(SpringLayout.SOUTH, createAccountButton, -6, SpringLayout.NORTH, safestoreLabel);
		springLayout.putConstraint(SpringLayout.EAST, createAccountButton, -10, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(createAccountButton);

	}
	private JPanel createBasePanel() {
		JPanel basePanel = new JPanel();

		basePanel.setOpaque(true);
		basePanel.setBackground(Color.RED);

		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(3, 3, 5, 5));
		centerPanel.setBorder(
				BorderFactory.createEmptyBorder(5, 5, 5, 5));
		centerPanel.setOpaque(true);
		centerPanel.setBackground(Color.WHITE);
		centerPanel.add(new JLabel("Username "));
		centerPanel.add(newUsername);
		centerPanel.add(new JLabel("Password "));
		centerPanel.add(newPassword);
		centerPanel.add(new JLabel("Confirm Password "));
		centerPanel.add(repeatPassword);

		basePanel.add(centerPanel);

		return basePanel;
	}
	
	public static void closeWindow() {
		frame.setVisible(false);
		frame.dispose();
	}

}
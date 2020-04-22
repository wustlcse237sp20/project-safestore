package UI;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import websiteAccount.WebsiteAccount;

public class WebsiteAccountTab {

	private JFrame frame;
	private JTextField websiteAccountSearchNicknameInput;
	private JTextField websiteAccountAddNicknameInput;
	private JTextField websiteAccountAddUsernameInput;
	private JPasswordField websiteAccountAddPasswordInput;
	private JTextField websiteAccountModifyCurrNicknameInput;
	private JTextField websiteAccountModifyNicknameInput;
	private JTextField websiteAccountModifyUsernameInput;
	private JPasswordField websiteAccountModifyPasswordInput;
	private JLabel websiteAccountViewUsernameResult;
	private JLabel websiteAccountViewPasswordResult;
	private JButton websiteAccountSearchButton;


	public WebsiteAccountTab(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * Initializes the view section of the website account tab.
	 */
	private void initializeWebAccountViewSection(JPanel websiteAccounts) {
		JLabel websiteAccountSearchHeader = new JLabel("Search Account Information");
		websiteAccountSearchHeader.setBounds(89, 43, 175, 16);
		websiteAccounts.add(websiteAccountSearchHeader);

		websiteAccountSearchNicknameInput = new JTextField();
		websiteAccountSearchNicknameInput.setBounds(112, 71, 130, 26);
		websiteAccounts.add(websiteAccountSearchNicknameInput);
		websiteAccountSearchNicknameInput.setColumns(10);

		websiteAccountSearchButton = new JButton("Search");

		websiteAccountSearchButton.setBounds(248, 71, 73, 29);
		websiteAccounts.add(websiteAccountSearchButton);

		JLabel websiteAccountSearchLabel = new JLabel("Nickname:");
		websiteAccountSearchLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountSearchLabel.setBounds(47, 76, 67, 16);
		websiteAccounts.add(websiteAccountSearchLabel);

		JTextArea websiteAccountNicknames = new JTextArea();
		websiteAccountNicknames.setText("will hold account \nnicknames in \nnext iteration");
		websiteAccountNicknames.setBounds(47, 109, 130, 257);
		websiteAccounts.add(websiteAccountNicknames);

		JLabel websiteAccountViewUsernameLabel = new JLabel("Username:");
		websiteAccountViewUsernameLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountViewUsernameLabel.setBounds(189, 181, 75, 16);
		websiteAccounts.add(websiteAccountViewUsernameLabel);

		JLabel websiteAccountViewPasswordLabel = new JLabel("Password:");
		websiteAccountViewPasswordLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountViewPasswordLabel.setBounds(189, 209, 61, 16);
		websiteAccounts.add(websiteAccountViewPasswordLabel);

		//Label for displaying the username when it is gotten
		websiteAccountViewUsernameResult = new JLabel("");
		websiteAccountViewUsernameResult.setBounds(259, 181, 155, 16);
		websiteAccounts.add(websiteAccountViewUsernameResult);

		//Label for displaying the password when it is gotten
		websiteAccountViewPasswordResult = new JLabel("");
		websiteAccountViewPasswordResult.setBounds(260, 209, 154, 16);
		websiteAccounts.add(websiteAccountViewPasswordResult);

		websiteAccountSearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WebsiteAccount website = SafeStore.getWebsiteAccountInfo(websiteAccountSearchNicknameInput.getText());
				if(website != null) {
					websiteAccountViewUsernameResult.setText(website.getWebsiteLogin());
					websiteAccountViewPasswordResult.setText(website.getWebsitePassword());
					websiteAccountSearchNicknameInput.setText(website.getNickname());
				}else {
					JOptionPane.showMessageDialog(frame, "You have no website stored under " + websiteAccountSearchNicknameInput.getText());
					websiteAccountViewUsernameResult.setText("");
					websiteAccountViewPasswordResult.setText("");
					websiteAccountSearchNicknameInput.setText("");

				}
			}
		});
	}

	private void resetAddWebsiteFields(){
		websiteAccountAddNicknameInput.setText("");
		websiteAccountAddPasswordInput.setText("");
		websiteAccountAddUsernameInput.setText("");
	}
	
	private void setSearchWebsiteFieldAfterModifyingOrAddingSite(String siteName) {
		websiteAccountSearchNicknameInput.setText(siteName);
		websiteAccountSearchButton.doClick();
	}

	/**
	 * 
	 * Initializes the add section of the website account tab.
	 */
	private void intializeWebAccountAddSection(JPanel websiteAccounts) {
		JLabel websiteAccountAddHeader = new JLabel("Add Account Information");
		websiteAccountAddHeader.setBounds(493, 43, 209, 16);
		websiteAccounts.add(websiteAccountAddHeader);



		JLabel websiteAccountAddNicknameLabel = new JLabel("Nickname*:");
		websiteAccountAddNicknameLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountAddNicknameLabel.setBounds(468, 79, 75, 16);
		websiteAccounts.add(websiteAccountAddNicknameLabel);

		websiteAccountAddNicknameInput = new JTextField();
		websiteAccountAddNicknameInput.setColumns(10);
		websiteAccountAddNicknameInput.setBounds(541, 74, 130, 26);
		websiteAccounts.add(websiteAccountAddNicknameInput);

		JLabel websiteAccountAddUsernameLabel = new JLabel("Username*:");
		websiteAccountAddUsernameLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountAddUsernameLabel.setBounds(468, 111, 75, 16);
		websiteAccounts.add(websiteAccountAddUsernameLabel);

		websiteAccountAddUsernameInput = new JTextField();
		websiteAccountAddUsernameInput.setColumns(10);
		websiteAccountAddUsernameInput.setBounds(541, 106, 130, 26);
		websiteAccounts.add(websiteAccountAddUsernameInput);

		JLabel websiteAccountAddPasswordLabel = new JLabel("Password*:");
		websiteAccountAddPasswordLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountAddPasswordLabel.setBounds(468, 144, 75, 16);
		websiteAccounts.add(websiteAccountAddPasswordLabel);

		websiteAccountAddPasswordInput = new JPasswordField();
		websiteAccountAddPasswordInput.setColumns(10);
		websiteAccountAddPasswordInput.setBounds(541, 139, 130, 26);
		websiteAccounts.add(websiteAccountAddPasswordInput);

		JButton websiteAccountAddButton = new JButton("Add");
		websiteAccountAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(websiteAccountAddNicknameInput.getText().isEmpty() || String.valueOf(websiteAccountAddPasswordInput.getPassword()).isEmpty()|| websiteAccountAddUsernameInput.getText().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Username, password, and nickname must contain values");
				}else {
					if(SafeStore.addWebsiteAccount(websiteAccountAddNicknameInput.getText(), websiteAccountAddUsernameInput.getText(),String.valueOf(websiteAccountAddPasswordInput.getPassword()))) {
						JOptionPane.showMessageDialog(frame, "Website Added Succesfully!");
						setSearchWebsiteFieldAfterModifyingOrAddingSite(websiteAccountAddNicknameInput.getText());
						resetAddWebsiteFields();
					}else {
						JOptionPane.showMessageDialog(frame, "Could not add website - Website with nickname already exists");
						resetAddWebsiteFields();
					}
				}
			}
		});
		websiteAccountAddButton.setBounds(541, 176, 73, 29);
		websiteAccounts.add(websiteAccountAddButton);

		JLabel wesbiteAccountrequiredFieldLabelAdd = new JLabel("* required field");
		wesbiteAccountrequiredFieldLabelAdd.setForeground(Color.RED);
		wesbiteAccountrequiredFieldLabelAdd.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
		wesbiteAccountrequiredFieldLabelAdd.setBounds(553, 59, 61, 16);
		websiteAccounts.add(wesbiteAccountrequiredFieldLabelAdd);
	}
	
	private void resetModifyWebsiteFields() {
		websiteAccountModifyCurrNicknameInput.setText("");
		websiteAccountModifyUsernameInput.setText("");
		websiteAccountModifyNicknameInput.setText("");
		websiteAccountModifyPasswordInput.setText("");
	}

	/**
	 * 
	 * initializes the modify section of the website account tab.
	 */
	private void initializeWebAccountModifySection(JPanel websiteAccounts) {
		JLabel websiteAccountModifyLabel = new JLabel("Modify Account Information");
		websiteAccountModifyLabel.setBounds(493, 220, 209, 16);
		websiteAccounts.add(websiteAccountModifyLabel);

		JLabel websiteAccountModifyCurrNicknameLabel = new JLabel("Current Nickname*:");
		websiteAccountModifyCurrNicknameLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountModifyCurrNicknameLabel.setBounds(423, 256, 120, 16);
		websiteAccounts.add(websiteAccountModifyCurrNicknameLabel);

		websiteAccountModifyCurrNicknameInput = new JTextField();
		websiteAccountModifyCurrNicknameInput.setColumns(10);
		websiteAccountModifyCurrNicknameInput.setBounds(541, 251, 130, 26);
		websiteAccounts.add(websiteAccountModifyCurrNicknameInput);

		JLabel websiteAccountModifyNicknameLabel = new JLabel("New Nickname:");
		websiteAccountModifyNicknameLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountModifyNicknameLabel.setBounds(445, 288, 98, 16);
		websiteAccounts.add(websiteAccountModifyNicknameLabel);

		websiteAccountModifyNicknameInput = new JTextField();
		websiteAccountModifyNicknameInput.setColumns(10);
		websiteAccountModifyNicknameInput.setBounds(541, 283, 130, 26);
		websiteAccounts.add(websiteAccountModifyNicknameInput);

		JLabel websiteAccountModifyUsernameLabel = new JLabel("New Username:");
		websiteAccountModifyUsernameLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountModifyUsernameLabel.setBounds(445, 321, 98, 16);
		websiteAccounts.add(websiteAccountModifyUsernameLabel);

		websiteAccountModifyUsernameInput = new JTextField();
		websiteAccountModifyUsernameInput.setColumns(10);
		websiteAccountModifyUsernameInput.setBounds(541, 316, 130, 26);
		websiteAccounts.add(websiteAccountModifyUsernameInput);

		JButton websiteAccountModifyButton = new JButton("Modify");
		websiteAccountModifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(websiteAccountModifyCurrNicknameInput.getText().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Please enter the nickname of the account you want to modify");
				}else {

					if(SafeStore.modifyWebsiteAccount(websiteAccountModifyCurrNicknameInput.getText(),websiteAccountModifyNicknameInput.getText(),websiteAccountModifyUsernameInput.getText(),String.valueOf(websiteAccountModifyPasswordInput.getPassword()))) {
						JOptionPane.showMessageDialog(frame, "Website modified ");

						String siteNickname = "";
						if(websiteAccountModifyNicknameInput.getText().isEmpty()) {
							siteNickname = websiteAccountModifyCurrNicknameInput.getText(); 
						}else {
							siteNickname = websiteAccountModifyNicknameInput.getText();
						}
						setSearchWebsiteFieldAfterModifyingOrAddingSite(siteNickname);
						resetModifyWebsiteFields();
					}else {
						JOptionPane.showMessageDialog(frame, "Couldn't Modify website");
						resetModifyWebsiteFields();
					}
				}
			}
		});
		websiteAccountModifyButton.setBounds(541, 381, 73, 29);
		websiteAccounts.add(websiteAccountModifyButton);

		JLabel websiteAccountModifyPasswordLabel = new JLabel("New Password:");
		websiteAccountModifyPasswordLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		websiteAccountModifyPasswordLabel.setBounds(445, 355, 98, 16);
		websiteAccounts.add(websiteAccountModifyPasswordLabel);

		websiteAccountModifyPasswordInput = new JPasswordField();
		websiteAccountModifyPasswordInput.setColumns(10);
		websiteAccountModifyPasswordInput.setBounds(541, 350, 130, 26);
		websiteAccounts.add(websiteAccountModifyPasswordInput);

		JLabel wesbiteAccountrequiredFieldLabel = new JLabel("* required field");
		wesbiteAccountrequiredFieldLabel.setForeground(Color.RED);
		wesbiteAccountrequiredFieldLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
		wesbiteAccountrequiredFieldLabel.setBounds(553, 236, 61, 16);
		websiteAccounts.add(wesbiteAccountrequiredFieldLabel);
	}

	/**
	 * Initialize the contents of the website account tab.
	 */
	public void initializeWebAccountTab(JTabbedPane safeStore) {
		JPanel websiteAccounts = new JPanel();
		safeStore.addTab("Website Accounts", null, websiteAccounts, null);
		websiteAccounts.setLayout(null);

		JLabel websiteAccountsLabel = new JLabel("Website Accounts");
		websiteAccountsLabel.setFont(new Font("Lucida Grande", Font.BOLD, 20));
		websiteAccountsLabel.setBounds(315, 6, 189, 25);
		websiteAccounts.add(websiteAccountsLabel);

		JLabel logoWebsiteAccountPane = new JLabel("SAFESTORE");
		logoWebsiteAccountPane.setHorizontalAlignment(SwingConstants.CENTER);
		logoWebsiteAccountPane.setForeground(Color.RED);
		logoWebsiteAccountPane.setFont(new Font("Marker Felt", Font.PLAIN, 16));
		logoWebsiteAccountPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		logoWebsiteAccountPane.setBounds(0, 399, 114, 33);
		websiteAccounts.add(logoWebsiteAccountPane);

		initializeWebAccountViewSection(websiteAccounts);
		intializeWebAccountAddSection(websiteAccounts);
		initializeWebAccountModifySection(websiteAccounts);	
	}

}

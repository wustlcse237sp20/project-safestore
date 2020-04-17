package UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.Color;
import javax.swing.SwingConstants;

import websiteAccount.WebsiteAccount;

import javax.swing.JButton;
import javax.swing.JScrollBar;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class FrontEnd {

	private JFrame frame;
	private JTextField websiteAccountSearchNicknameInput;
	private JTextField websiteAccountAddNicknameInput;
	private JTextField websiteAccountAddUsernameInput;
	private JPasswordField websiteAccountAddPasswordInput;
	private JTextField websiteAccountModifyCurrNicknameInput;
	private JTextField websiteAccountModifyNicknameInput;
	private JTextField websiteAccountModifyUsernameInput;
	private JPasswordField websiteAccountModifyPasswordInput;
	private JTextField creditCardSearchNicknameInput;
	private JTextField creditCardAddNicknameInput;
	private JTextField creditCardAddNumberInput;
	private JTextField creditCardAddExpDateInput;
	private JTextField creditCardAddCVVInput;
	private JTextField creditCardAddStreetAdressInput;
	private JTextField creditCardAddCityInput;
	private JTextField creditCardAddStateInput;
	private JTextField creditCardAddZipInput;
	private JTextField modifyCreditCardNumInput;
	private JTextField modifyCreditCardNewNicknameInput;
	private JTextField modifyCreditCardExpDateInput;
	private JTextField modifyCreditCardCVVInput;
	private JTextField modifyCreditCardStreetAddressInput;
	private JTextField modifyCreditCardCityInput;
	private JTextField modifyCreditCardStateInput;
	private JTextField modifyCreditCardZipInput;
	private JTextField modifyCreditCardCurNicknameInput;
	private JTextField debitCardSearchInput;
	private JTextField debitCardAddNicknameInput;
	private JTextField debitCardAddNumberInput;
	private JTextField debitCardAddExpDateInput;
	private JTextField debitCardAddCvvInput;
	private JTextField debitCardAddStAdressInput;
	private JTextField debitCardAddCityInput;
	private JTextField debitCardAddStateInput;
	private JTextField debitCardAddZipInput;
	private JTextField debitCardAddPinInput;
	private JTextField debitCardModifyNumInput;
	private JTextField debitCardModifyNewNicknameInput;
	private JTextField debitCardModifyExpDateInput;
	private JTextField debitCardModifyCvvInput;
	private JTextField debitCardModifyPinInput;
	private JTextField debitCardModifyStAddressInput;
	private JTextField debitCardModifyCityInput;
	private JTextField debitCardModifyStateInput;
	private JTextField debitCardModifyZipInput;
	private JTextField debitCardModifyCurrNicknameInput;
	private JLabel websiteAccountViewUsernameResult;
	private JLabel websiteAccountViewPasswordResult;
	private JButton websiteAccountSearchButton;
	/**
	 * Launch the application.
	 */
	public static void launchWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FrontEnd window = new FrontEnd();
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
	public FrontEnd() {
		initialize();
	}

	/**
	 * 
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

		//label for displaying the password when it is gotten
		websiteAccountViewPasswordResult = new JLabel("");
		websiteAccountViewPasswordResult.setBounds(260, 209, 154, 16);
		websiteAccounts.add(websiteAccountViewPasswordResult);

		websiteAccountSearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WebsiteAccount website = UIController.getWebsiteAccountInfo(websiteAccountSearchNicknameInput.getText());
				if(website != null) {
					websiteAccountViewUsernameResult.setText(website.getWebsiteLogin());
					websiteAccountViewPasswordResult.setText(website.getWebsitePassword());
				}else {
					websiteAccountViewUsernameResult.setText("Site doesn't exist");
					websiteAccountViewPasswordResult.setText(" ");
				}
			}
		});
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
					if(UIController.addWebsiteAccount(websiteAccountAddNicknameInput.getText(), websiteAccountAddUsernameInput.getText(),String.valueOf(websiteAccountAddPasswordInput.getPassword()))) {
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

					if(UIController.modifyWebsiteAccount(websiteAccountModifyCurrNicknameInput.getText(),websiteAccountModifyNicknameInput.getText(),websiteAccountModifyUsernameInput.getText(),String.valueOf(websiteAccountModifyPasswordInput.getPassword()))) {
						JOptionPane.showMessageDialog(frame, "Website modified ");
						
						String siteNickname = "f";
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
	private void initializeWebAccountTab(JTabbedPane safeStore) {
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

	/**
	 * 
	 * Initializes the credit card view tab for the credit card tab.
	 */
	private void initializeCreditCardViewTab(JTabbedPane creditCardTabbedPane) {
		JPanel creditCardViewTab = new JPanel();
		creditCardTabbedPane.addTab("View", null, creditCardViewTab, null);
		creditCardViewTab.setLayout(null);

		JLabel creditCardSearchLabel = new JLabel("Nickname*:");
		creditCardSearchLabel.setBounds(235, 13, 73, 15);
		creditCardSearchLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		creditCardViewTab.add(creditCardSearchLabel);

		creditCardSearchNicknameInput = new JTextField();
		creditCardSearchNicknameInput.setBounds(313, 8, 130, 26);
		creditCardSearchNicknameInput.setColumns(10);
		creditCardViewTab.add(creditCardSearchNicknameInput);

		JButton creditCardSearchButton = new JButton("Search");
		creditCardSearchButton.setBounds(448, 6, 85, 29);
		creditCardViewTab.add(creditCardSearchButton);

		JTextArea creditCardDisplayNicknamesView = new JTextArea();
		creditCardDisplayNicknamesView.setBounds(195, 64, 113, 248);
		creditCardDisplayNicknamesView.setText("will hold account \nnicknames in \nnext iteration");
		creditCardViewTab.add(creditCardDisplayNicknamesView);

		JLabel defaultNicknameDisclaimer = new JLabel("*default nickname is last four digits of card number");
		defaultNicknameDisclaimer.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		defaultNicknameDisclaimer.setBounds(501, 315, 251, 16);
		creditCardViewTab.add(defaultNicknameDisclaimer);

		JTextArea creditCardViewDisplay = new JTextArea();
		creditCardViewDisplay.setText("call the toString method on \nCreditCard to display all \ncard information here");
		creditCardViewDisplay.setBounds(415, 90, 230, 185);
		creditCardViewTab.add(creditCardViewDisplay);
	}

	/**
	 * initializes the credit card add tab for the credit card tab.
	 * 
	 */
	private void initializeCreditCardAddTab(JTabbedPane creditCardTabbedPane) {
		JPanel creditCardAddTab = new JPanel();
		creditCardTabbedPane.addTab("Add", null, creditCardAddTab, null);
		creditCardAddTab.setLayout(null);

		JLabel creditCardAddNumberLabel = new JLabel("Card Number*:");
		creditCardAddNumberLabel.setBounds(384, 45, 98, 16);
		creditCardAddTab.add(creditCardAddNumberLabel);

		JLabel creditCardAddNicknameLabel = new JLabel("Nickname:");
		creditCardAddNicknameLabel.setBounds(384, 73, 77, 16);
		creditCardAddTab.add(creditCardAddNicknameLabel);

		JLabel creditCardAddExpirationDateLabel = new JLabel("Expiration Date*:");
		creditCardAddExpirationDateLabel.setBounds(384, 101, 112, 16);
		creditCardAddTab.add(creditCardAddExpirationDateLabel);

		JLabel creditCardAddCvvLabel = new JLabel("CVV*:");
		creditCardAddCvvLabel.setBounds(384, 129, 40, 16);
		creditCardAddTab.add(creditCardAddCvvLabel);

		JLabel creditCardAddStAddressLabel = new JLabel("Street Address*:");
		creditCardAddStAddressLabel.setBounds(384, 157, 106, 16);
		creditCardAddTab.add(creditCardAddStAddressLabel);

		JLabel creditCardAddCityLabel = new JLabel("City*:");
		creditCardAddCityLabel.setBounds(384, 185, 40, 16);
		creditCardAddTab.add(creditCardAddCityLabel);

		JLabel creditCardAddStateLabel = new JLabel("State*:");
		creditCardAddStateLabel.setBounds(384, 213, 52, 16);
		creditCardAddTab.add(creditCardAddStateLabel);

		JLabel creditCardAddZipLabel = new JLabel("Zip Code*:");
		creditCardAddZipLabel.setBounds(384, 241, 77, 16);
		creditCardAddTab.add(creditCardAddZipLabel);

		JTextArea creditCardDisplayNicknamesAdd = new JTextArea();
		creditCardDisplayNicknamesAdd.setText("will display\nthe list of \nnicknames");
		creditCardDisplayNicknamesAdd.setBounds(95, 45, 168, 212);
		creditCardAddTab.add(creditCardDisplayNicknamesAdd);

		creditCardAddNicknameInput = new JTextField();
		creditCardAddNicknameInput.setBounds(458, 68, 222, 26);
		creditCardAddTab.add(creditCardAddNicknameInput);
		creditCardAddNicknameInput.setColumns(10);

		creditCardAddNumberInput = new JTextField();
		creditCardAddNumberInput.setColumns(10);
		creditCardAddNumberInput.setBounds(482, 40, 198, 26);
		creditCardAddTab.add(creditCardAddNumberInput);

		creditCardAddExpDateInput = new JTextField();
		creditCardAddExpDateInput.setColumns(10);
		creditCardAddExpDateInput.setBounds(495, 96, 185, 26);
		creditCardAddTab.add(creditCardAddExpDateInput);

		creditCardAddCVVInput = new JTextField();
		creditCardAddCVVInput.setColumns(10);
		creditCardAddCVVInput.setBounds(424, 124, 256, 26);
		creditCardAddTab.add(creditCardAddCVVInput);

		creditCardAddStreetAdressInput = new JTextField();
		creditCardAddStreetAdressInput.setColumns(10);
		creditCardAddStreetAdressInput.setBounds(494, 152, 185, 26);
		creditCardAddTab.add(creditCardAddStreetAdressInput);

		creditCardAddCityInput = new JTextField();
		creditCardAddCityInput.setColumns(10);
		creditCardAddCityInput.setBounds(425, 180, 255, 26);
		creditCardAddTab.add(creditCardAddCityInput);

		creditCardAddStateInput = new JTextField();
		creditCardAddStateInput.setColumns(10);
		creditCardAddStateInput.setBounds(435, 208, 245, 26);
		creditCardAddTab.add(creditCardAddStateInput);

		creditCardAddZipInput = new JTextField();
		creditCardAddZipInput.setColumns(10);
		creditCardAddZipInput.setBounds(458, 236, 222, 26);
		creditCardAddTab.add(creditCardAddZipInput);

		JButton creditCardAddButton = new JButton("Add");
		creditCardAddButton.setBounds(495, 274, 117, 29);
		creditCardAddTab.add(creditCardAddButton);

		JLabel creditCardAddRequiredFieldLabel = new JLabel("* required field");
		creditCardAddRequiredFieldLabel.setForeground(Color.RED);
		creditCardAddRequiredFieldLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
		creditCardAddRequiredFieldLabel.setBounds(619, 19, 61, 16);
		creditCardAddTab.add(creditCardAddRequiredFieldLabel);
	}

	/**
	 * initializes the credit card modify tab for the credit card tab.
	 * 
	 */
	private void initializeCreditCardModifyTab(JTabbedPane creditCardTabbedPane) {
		JPanel creditCardModifyTab = new JPanel();
		creditCardTabbedPane.addTab("Modify", null, creditCardModifyTab, null);
		creditCardModifyTab.setLayout(null);

		JTextArea creditCardDisplayNicknamesAdd_1 = new JTextArea();
		creditCardDisplayNicknamesAdd_1.setText("will display\nthe list of \nnicknames");
		creditCardDisplayNicknamesAdd_1.setBounds(83, 27, 168, 244);
		creditCardModifyTab.add(creditCardDisplayNicknamesAdd_1);

		JLabel modifyCreditCardNumLabel = new JLabel("Card Number:");
		modifyCreditCardNumLabel.setBounds(371, 58, 98, 16);
		creditCardModifyTab.add(modifyCreditCardNumLabel);

		modifyCreditCardNumInput = new JTextField();
		modifyCreditCardNumInput.setColumns(10);
		modifyCreditCardNumInput.setBounds(462, 53, 205, 26);
		creditCardModifyTab.add(modifyCreditCardNumInput);

		modifyCreditCardNewNicknameInput = new JTextField();
		modifyCreditCardNewNicknameInput.setColumns(10);
		modifyCreditCardNewNicknameInput.setBounds(469, 81, 198, 26);
		creditCardModifyTab.add(modifyCreditCardNewNicknameInput);

		JLabel modifyCreditCardNewNicknameLabel = new JLabel("New Nickname:");
		modifyCreditCardNewNicknameLabel.setBounds(371, 86, 98, 16);
		creditCardModifyTab.add(modifyCreditCardNewNicknameLabel);

		JLabel modifyCreditCardExpDateLabel = new JLabel("Expiration Date:");
		modifyCreditCardExpDateLabel.setBounds(371, 114, 112, 16);
		creditCardModifyTab.add(modifyCreditCardExpDateLabel);

		modifyCreditCardExpDateInput = new JTextField();
		modifyCreditCardExpDateInput.setColumns(10);
		modifyCreditCardExpDateInput.setBounds(479, 109, 188, 26);
		creditCardModifyTab.add(modifyCreditCardExpDateInput);

		modifyCreditCardCVVInput = new JTextField();
		modifyCreditCardCVVInput.setColumns(10);
		modifyCreditCardCVVInput.setBounds(401, 137, 266, 26);
		creditCardModifyTab.add(modifyCreditCardCVVInput);

		JLabel modifyCreditCardCVVLabel = new JLabel("CVV:");
		modifyCreditCardCVVLabel.setBounds(371, 142, 40, 16);
		creditCardModifyTab.add(modifyCreditCardCVVLabel);

		modifyCreditCardStreetAddressInput = new JTextField();
		modifyCreditCardStreetAddressInput.setColumns(10);
		modifyCreditCardStreetAddressInput.setBounds(469, 165, 197, 26);
		creditCardModifyTab.add(modifyCreditCardStreetAddressInput);

		JLabel modifyCreditCardStreetAddressLabel = new JLabel("Street Address:");
		modifyCreditCardStreetAddressLabel.setBounds(371, 170, 106, 16);
		creditCardModifyTab.add(modifyCreditCardStreetAddressLabel);

		JLabel modifyCreditCardCityLabel = new JLabel("City:");
		modifyCreditCardCityLabel.setBounds(371, 198, 40, 16);
		creditCardModifyTab.add(modifyCreditCardCityLabel);

		modifyCreditCardCityInput = new JTextField();
		modifyCreditCardCityInput.setColumns(10);
		modifyCreditCardCityInput.setBounds(401, 193, 266, 26);
		creditCardModifyTab.add(modifyCreditCardCityInput);

		modifyCreditCardStateInput = new JTextField();
		modifyCreditCardStateInput.setColumns(10);
		modifyCreditCardStateInput.setBounds(411, 221, 256, 26);
		creditCardModifyTab.add(modifyCreditCardStateInput);

		JLabel modifyCreditCardStateLabel = new JLabel("State:");
		modifyCreditCardStateLabel.setBounds(371, 226, 52, 16);
		creditCardModifyTab.add(modifyCreditCardStateLabel);

		JLabel modifyCreditCardZipLabel = new JLabel("Zip Code:");
		modifyCreditCardZipLabel.setBounds(371, 254, 77, 16);
		creditCardModifyTab.add(modifyCreditCardZipLabel);

		modifyCreditCardZipInput = new JTextField();
		modifyCreditCardZipInput.setColumns(10);
		modifyCreditCardZipInput.setBounds(432, 249, 235, 26);
		creditCardModifyTab.add(modifyCreditCardZipInput);

		JButton modifyCreditCardButton = new JButton("Modify");
		modifyCreditCardButton.setBounds(482, 287, 117, 29);
		creditCardModifyTab.add(modifyCreditCardButton);

		JLabel modifyCreditCardCurNicknameLabel = new JLabel("Current Nickname*:");
		modifyCreditCardCurNicknameLabel.setBounds(371, 27, 131, 16);
		creditCardModifyTab.add(modifyCreditCardCurNicknameLabel);

		modifyCreditCardCurNicknameInput = new JTextField();
		modifyCreditCardCurNicknameInput.setColumns(10);
		modifyCreditCardCurNicknameInput.setBounds(499, 22, 168, 26);
		creditCardModifyTab.add(modifyCreditCardCurNicknameInput);

		JLabel creditCardModifyRequiredFieldLabel = new JLabel("* required field");
		creditCardModifyRequiredFieldLabel.setForeground(Color.RED);
		creditCardModifyRequiredFieldLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
		creditCardModifyRequiredFieldLabel.setBounds(606, 6, 61, 16);
		creditCardModifyTab.add(creditCardModifyRequiredFieldLabel);
	}

	/**
	 * Initialize the contents of the credit card tab.
	 */
	private void initializeCreditCardTab(JTabbedPane safeStore) {
		JPanel creditCards = new JPanel();
		safeStore.addTab("Credit Cards", null, creditCards, null);
		creditCards.setLayout(null);

		JLabel creditCardLabel = new JLabel("Credit Cards");
		creditCardLabel.setFont(new Font("Lucida Grande", Font.BOLD, 20));
		creditCardLabel.setBounds(323, 6, 139, 16);
		creditCards.add(creditCardLabel);

		JLabel logoCreditCardPane = new JLabel("SAFESTORE");
		logoCreditCardPane.setHorizontalAlignment(SwingConstants.CENTER);
		logoCreditCardPane.setForeground(Color.RED);
		logoCreditCardPane.setFont(new Font("Marker Felt", Font.PLAIN, 16));
		logoCreditCardPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		logoCreditCardPane.setBounds(0, 399, 114, 33);
		creditCards.add(logoCreditCardPane);

		JTabbedPane creditCardTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		creditCardTabbedPane.setBounds(6, 29, 773, 383);
		creditCards.add(creditCardTabbedPane);

		initializeCreditCardViewTab(creditCardTabbedPane);
		initializeCreditCardAddTab(creditCardTabbedPane);
		initializeCreditCardModifyTab(creditCardTabbedPane);

	}

	/**
	 * initializes the debit card view tab for the debit card tab.
	 * 
	 */
	private void initializeDebitCardViewTab(JTabbedPane debitCardTabbedPane) {
		JPanel debitCardViewTab = new JPanel();
		debitCardTabbedPane.addTab("View", null, debitCardViewTab, null);
		debitCardViewTab.setLayout(null);

		JLabel debitCardSearchLabel = new JLabel("Nickname*:");
		debitCardSearchLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		debitCardSearchLabel.setBounds(211, 13, 73, 15);
		debitCardViewTab.add(debitCardSearchLabel);

		debitCardSearchInput = new JTextField();
		debitCardSearchInput.setColumns(10);
		debitCardSearchInput.setBounds(289, 8, 130, 26);
		debitCardViewTab.add(debitCardSearchInput);

		JButton debitCardSearchButton = new JButton("Search");
		debitCardSearchButton.setBounds(424, 6, 85, 29);
		debitCardViewTab.add(debitCardSearchButton);

		JTextArea debitCardDisplayNicknamesView = new JTextArea();
		debitCardDisplayNicknamesView.setText("will hold account \nnicknames in \nnext iteration");
		debitCardDisplayNicknamesView.setBounds(171, 64, 113, 248);
		debitCardViewTab.add(debitCardDisplayNicknamesView);

		JTextArea debitCardViewDisplay = new JTextArea();
		debitCardViewDisplay.setText("call the toString method on \nDebitCard to display all \ncard information here");
		debitCardViewDisplay.setBounds(391, 90, 230, 185);
		debitCardViewTab.add(debitCardViewDisplay);

		JLabel debitCardDefaultNicknameDisclaimer = new JLabel("*default nickname is last four digits of card number");
		debitCardDefaultNicknameDisclaimer.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		debitCardDefaultNicknameDisclaimer.setBounds(477, 315, 251, 16);
		debitCardViewTab.add(debitCardDefaultNicknameDisclaimer);
	}

	/**
	 * initializes the debit card add tab for the debit card tab.
	 * 
	 */
	private void initializeDebitCardAddTab(JTabbedPane debitCardTabbedPane) {
		JPanel debitCardAddTab = new JPanel();
		debitCardTabbedPane.addTab("Add", null, debitCardAddTab, null);
		debitCardAddTab.setLayout(null);


		JLabel debitCardAddNumberLabel = new JLabel("Card Number*:");
		debitCardAddNumberLabel.setBounds(384, 45, 98, 16);
		debitCardAddTab.add(debitCardAddNumberLabel);

		JLabel debitCardAddNicknameLabel = new JLabel("Nickname:");
		debitCardAddNicknameLabel.setBounds(384, 73, 77, 16);
		debitCardAddTab.add(debitCardAddNicknameLabel);

		JLabel debitCardAddExpirationDateLabel = new JLabel("Expiration Date*:");
		debitCardAddExpirationDateLabel.setBounds(384, 101, 112, 16);
		debitCardAddTab.add(debitCardAddExpirationDateLabel);

		JLabel debitCardAddCvvLabel = new JLabel("CVV*:");
		debitCardAddCvvLabel.setBounds(384, 129, 40, 16);
		debitCardAddTab.add(debitCardAddCvvLabel);

		JLabel debitCardAddStAddressLabel = new JLabel("Street Address*:");
		debitCardAddStAddressLabel.setBounds(384, 185, 106, 16);
		debitCardAddTab.add(debitCardAddStAddressLabel);

		JLabel debitCardAddCityLabel = new JLabel("City*:");
		debitCardAddCityLabel.setBounds(384, 213, 40, 16);
		debitCardAddTab.add(debitCardAddCityLabel);

		JLabel debitCardAddStateLabel = new JLabel("State*:");
		debitCardAddStateLabel.setBounds(384, 241, 52, 16);
		debitCardAddTab.add(debitCardAddStateLabel);

		JLabel debitCardAddZipLabel = new JLabel("Zip Code*:");
		debitCardAddZipLabel.setBounds(384, 269, 77, 16);
		debitCardAddTab.add(debitCardAddZipLabel);

		JTextArea debitCardAddDisplayNickname = new JTextArea();
		debitCardAddDisplayNickname.setText("will display\nthe list of \nnicknames");
		debitCardAddDisplayNickname.setBounds(95, 45, 168, 212);
		debitCardAddTab.add(debitCardAddDisplayNickname);

		debitCardAddNicknameInput = new JTextField();
		debitCardAddNicknameInput.setColumns(10);
		debitCardAddNicknameInput.setBounds(458, 68, 222, 26);
		debitCardAddTab.add(debitCardAddNicknameInput);

		debitCardAddNumberInput = new JTextField();
		debitCardAddNumberInput.setColumns(10);
		debitCardAddNumberInput.setBounds(482, 40, 198, 26);
		debitCardAddTab.add(debitCardAddNumberInput);

		debitCardAddExpDateInput = new JTextField();
		debitCardAddExpDateInput.setColumns(10);
		debitCardAddExpDateInput.setBounds(495, 96, 185, 26);
		debitCardAddTab.add(debitCardAddExpDateInput);

		debitCardAddCvvInput = new JTextField();
		debitCardAddCvvInput.setColumns(10);
		debitCardAddCvvInput.setBounds(424, 124, 256, 26);
		debitCardAddTab.add(debitCardAddCvvInput);

		debitCardAddStAdressInput = new JTextField();
		debitCardAddStAdressInput.setColumns(10);
		debitCardAddStAdressInput.setBounds(494, 180, 185, 26);
		debitCardAddTab.add(debitCardAddStAdressInput);

		debitCardAddCityInput = new JTextField();
		debitCardAddCityInput.setColumns(10);
		debitCardAddCityInput.setBounds(425, 208, 255, 26);
		debitCardAddTab.add(debitCardAddCityInput);

		debitCardAddStateInput = new JTextField();
		debitCardAddStateInput.setColumns(10);
		debitCardAddStateInput.setBounds(435, 236, 245, 26);
		debitCardAddTab.add(debitCardAddStateInput);

		debitCardAddZipInput = new JTextField();
		debitCardAddZipInput.setColumns(10);
		debitCardAddZipInput.setBounds(458, 264, 222, 26);
		debitCardAddTab.add(debitCardAddZipInput);

		JButton debitCardAddButton = new JButton("Add");
		debitCardAddButton.setBounds(495, 302, 117, 29);
		debitCardAddTab.add(debitCardAddButton);

		JLabel debitCardAddRequiredFieldLabel = new JLabel("* required field");
		debitCardAddRequiredFieldLabel.setForeground(Color.RED);
		debitCardAddRequiredFieldLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
		debitCardAddRequiredFieldLabel.setBounds(619, 19, 61, 16);
		debitCardAddTab.add(debitCardAddRequiredFieldLabel);

		JLabel debitCardAddPinLabel = new JLabel("Pin*:");
		debitCardAddPinLabel.setBounds(384, 157, 40, 16);
		debitCardAddTab.add(debitCardAddPinLabel);

		debitCardAddPinInput = new JTextField();
		debitCardAddPinInput.setColumns(10);
		debitCardAddPinInput.setBounds(413, 152, 267, 26);
		debitCardAddTab.add(debitCardAddPinInput);
	}

	/**
	 * initializes the debit card modify tab for the debit card tab.
	 * 
	 */
	private void initializeDebitCardModifyTab(JTabbedPane debitCardTabbedPane) {
		JPanel debitCardModifyTab = new JPanel();
		debitCardTabbedPane.addTab("Modify", null, debitCardModifyTab, null);
		debitCardModifyTab.setLayout(null);

		JTextArea debitCardModifyDisplayNickname = new JTextArea();
		debitCardModifyDisplayNickname.setText("will display\nthe list of \nnicknames");
		debitCardModifyDisplayNickname.setBounds(109, 50, 168, 212);
		debitCardModifyTab.add(debitCardModifyDisplayNickname);

		JLabel debitCardModifyRequiredFieldLabel = new JLabel("* required field");
		debitCardModifyRequiredFieldLabel.setForeground(Color.RED);
		debitCardModifyRequiredFieldLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
		debitCardModifyRequiredFieldLabel.setBounds(629, 6, 61, 16);
		debitCardModifyTab.add(debitCardModifyRequiredFieldLabel);

		JLabel debitCardModifyNumLabel = new JLabel("Card Number:");
		debitCardModifyNumLabel.setBounds(398, 50, 98, 16);
		debitCardModifyTab.add(debitCardModifyNumLabel);

		debitCardModifyNumInput = new JTextField();
		debitCardModifyNumInput.setColumns(10);
		debitCardModifyNumInput.setBounds(485, 45, 209, 26);
		debitCardModifyTab.add(debitCardModifyNumInput);

		JLabel debitCardModifyNewNicknameLabel = new JLabel("New Nickname:");
		debitCardModifyNewNicknameLabel.setBounds(398, 78, 98, 16);
		debitCardModifyTab.add(debitCardModifyNewNicknameLabel);

		debitCardModifyNewNicknameInput = new JTextField();
		debitCardModifyNewNicknameInput.setColumns(10);
		debitCardModifyNewNicknameInput.setBounds(495, 73, 199, 26);
		debitCardModifyTab.add(debitCardModifyNewNicknameInput);

		JLabel debitCardModifyExpDateLabel = new JLabel("Expiration Date:");
		debitCardModifyExpDateLabel.setBounds(398, 106, 112, 16);
		debitCardModifyTab.add(debitCardModifyExpDateLabel);

		debitCardModifyExpDateInput = new JTextField();
		debitCardModifyExpDateInput.setColumns(10);
		debitCardModifyExpDateInput.setBounds(500, 101, 194, 26);
		debitCardModifyTab.add(debitCardModifyExpDateInput);

		JLabel debitCardModifyCvvLabel = new JLabel("CVV:");
		debitCardModifyCvvLabel.setBounds(398, 134, 40, 16);
		debitCardModifyTab.add(debitCardModifyCvvLabel);

		debitCardModifyCvvInput = new JTextField();
		debitCardModifyCvvInput.setColumns(10);
		debitCardModifyCvvInput.setBounds(427, 129, 267, 26);
		debitCardModifyTab.add(debitCardModifyCvvInput);

		JLabel debitCardModifyPinLabel = new JLabel("Pin:");
		debitCardModifyPinLabel.setBounds(398, 162, 40, 16);
		debitCardModifyTab.add(debitCardModifyPinLabel);

		debitCardModifyPinInput = new JTextField();
		debitCardModifyPinInput.setColumns(10);
		debitCardModifyPinInput.setBounds(421, 157, 273, 26);
		debitCardModifyTab.add(debitCardModifyPinInput);

		JLabel debitCardModifyStAddressLabel = new JLabel("Street Address:");
		debitCardModifyStAddressLabel.setBounds(398, 190, 106, 16);
		debitCardModifyTab.add(debitCardModifyStAddressLabel);

		debitCardModifyStAddressInput = new JTextField();
		debitCardModifyStAddressInput.setColumns(10);
		debitCardModifyStAddressInput.setBounds(490, 185, 203, 26);
		debitCardModifyTab.add(debitCardModifyStAddressInput);

		JLabel debitCardModifyCityLabel = new JLabel("City:");
		debitCardModifyCityLabel.setBounds(398, 218, 40, 16);
		debitCardModifyTab.add(debitCardModifyCityLabel);

		debitCardModifyCityInput = new JTextField();
		debitCardModifyCityInput.setColumns(10);
		debitCardModifyCityInput.setBounds(427, 213, 267, 26);
		debitCardModifyTab.add(debitCardModifyCityInput);

		JLabel debitCardModifyStateLabel = new JLabel("State:");
		debitCardModifyStateLabel.setBounds(398, 246, 52, 16);
		debitCardModifyTab.add(debitCardModifyStateLabel);

		debitCardModifyStateInput = new JTextField();
		debitCardModifyStateInput.setColumns(10);
		debitCardModifyStateInput.setBounds(437, 241, 257, 26);
		debitCardModifyTab.add(debitCardModifyStateInput);

		JLabel debitCardModifyZipLabel = new JLabel("Zip Code:");
		debitCardModifyZipLabel.setBounds(398, 274, 77, 16);
		debitCardModifyTab.add(debitCardModifyZipLabel);

		debitCardModifyZipInput = new JTextField();
		debitCardModifyZipInput.setColumns(10);
		debitCardModifyZipInput.setBounds(461, 269, 233, 26);
		debitCardModifyTab.add(debitCardModifyZipInput);

		JButton debitCardModifyButton = new JButton("Modify");
		debitCardModifyButton.setBounds(509, 307, 117, 29);
		debitCardModifyTab.add(debitCardModifyButton);

		JLabel debitCardModifyCurNicknameLabel = new JLabel("Current Nickname*:");
		debitCardModifyCurNicknameLabel.setBounds(398, 25, 124, 16);
		debitCardModifyTab.add(debitCardModifyCurNicknameLabel);

		debitCardModifyCurrNicknameInput = new JTextField();
		debitCardModifyCurrNicknameInput.setColumns(10);
		debitCardModifyCurrNicknameInput.setBounds(520, 20, 174, 26);
		debitCardModifyTab.add(debitCardModifyCurrNicknameInput);
	}

	/**
	 * Initialize the contents of the debit card tab.
	 */
	private void initializeDebitCardTab(JTabbedPane safeStore) {
		JPanel debitCards = new JPanel();
		safeStore.addTab("Debit Cards", null, debitCards, null);
		debitCards.setLayout(null);

		JLabel debitCardLabel = new JLabel("Debit Cards");
		debitCardLabel.setFont(new Font("Lucida Grande", Font.BOLD, 20));
		debitCardLabel.setBounds(317, 0, 135, 33);
		debitCards.add(debitCardLabel);

		JLabel logoDebitCardPane = new JLabel("SAFESTORE");
		logoDebitCardPane.setHorizontalAlignment(SwingConstants.CENTER);
		logoDebitCardPane.setForeground(Color.RED);
		logoDebitCardPane.setFont(new Font("Marker Felt", Font.PLAIN, 16));
		logoDebitCardPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		logoDebitCardPane.setBounds(0, 399, 114, 33);
		debitCards.add(logoDebitCardPane);

		JTabbedPane debitCardTabbedPane = new JTabbedPane(JTabbedPane.TOP);
		debitCardTabbedPane.setBounds(0, 27, 773, 388);
		debitCards.add(debitCardTabbedPane);

		initializeDebitCardViewTab(debitCardTabbedPane);
		initializeDebitCardAddTab(debitCardTabbedPane);
		initializeDebitCardModifyTab(debitCardTabbedPane);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		JTabbedPane safeStore = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(safeStore, BorderLayout.CENTER);

		initializeWebAccountTab(safeStore);
		initializeCreditCardTab(safeStore);
		initializeDebitCardTab(safeStore);

	}
	
	// Display the new website updates automatically - implement better later - also do this for adding a website maybe
	private void setSearchWebsiteFieldAfterModifyingOrAddingSite(String siteName) {
		websiteAccountSearchNicknameInput.setText(siteName);
		websiteAccountSearchButton.doClick();
	}
	private void resetAddWebsiteFields(){
		websiteAccountAddNicknameInput.setText("");
		websiteAccountAddPasswordInput.setText("");
		websiteAccountAddUsernameInput.setText("");
	}
	private void resetModifyWebsiteFields() {
		websiteAccountModifyCurrNicknameInput.setText("");
		websiteAccountModifyUsernameInput.setText("");
		websiteAccountModifyNicknameInput.setText("");
		websiteAccountModifyPasswordInput.setText("");
	}
}

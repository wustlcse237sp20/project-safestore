package UI;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import card.CreditCard;

public class CreditCardTab {

	private JFrame frame;
	private static JTextField creditCardSearchNicknameInput;
	private static JButton creditCardSearchButton;
	private JTextField creditCardAddNicknameInput;
	private JTextField creditCardAddNumberInput;
	private JTextField creditCardAddExpDateInput;
	private JTextField creditCardAddCVVInput;
	private JTextField creditCardAddStreetAddressInput;
	private JTextField creditCardAddCityInput;
	private JTextField creditCardAddStateInput;
	private JTextField creditCardAddZipInput;
	private JTextField creditCardModifyNumberInput;
	private JTextField creditCardModifyNewNicknameInput;
	private JTextField creditCardModifyExpDateInput;
	private JTextField creditCardModifyCVVInput;
	private JTextField creditCardModifyStreetAddressInput;
	private JTextField creditCardModifyCityInput;
	private JTextField creditCardModifyStateInput;
	private JTextField creditCardModifyZipInput;
	private JTextField creditCardModifyCurNicknameInput;
	private DefaultListModel<String> creditCardModel = new DefaultListModel<String>();
	private JPanel creditCardViewTab;
	private JPanel creditCardAddTab;
	private JPanel creditCardModifyTab;
	
	public CreditCardTab(JFrame frame) {
		this.frame = frame;
	}
	
	/**
	 * 
	 * Initializes the credit card view tab for the credit card tab.
	 */
	private void initializeCreditCardViewTab(JTabbedPane creditCardTabbedPane) {
		creditCardViewTab = new JPanel();
		creditCardTabbedPane.addTab("View", null, creditCardViewTab, null);
		creditCardViewTab.setLayout(null);

		JLabel creditCardSearchLabel = new JLabel("Nickname*:");
		creditCardSearchLabel.setBounds(235, 13, 73, 15);
		creditCardSearchLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		creditCardViewTab.add(creditCardSearchLabel);

		JTextArea creditCardViewDisplay = new JTextArea();
		creditCardViewDisplay.setBounds(370, 64, 305, 248);
		creditCardViewTab.add(creditCardViewDisplay);

		creditCardSearchNicknameInput = new JTextField();
		creditCardSearchNicknameInput.setBounds(313, 8, 130, 26);
		creditCardSearchNicknameInput.setColumns(10);
		creditCardViewTab.add(creditCardSearchNicknameInput);

		creditCardSearchButton = new JButton("Search");
		creditCardSearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(creditCardSearchNicknameInput.getText().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Enter the card's nickname (default is last 4 digits)");
				}else {
					CreditCard creditCard = SafeStore.getCreditCardInfo(creditCardSearchNicknameInput.getText());
					if(creditCard != null) {
						creditCardViewDisplay.setText(creditCard.toString());
						creditCardSearchNicknameInput.setText("");
					}else {
						JOptionPane.showMessageDialog(frame, "You have no credit card stored under " + creditCardSearchNicknameInput.getText());
						creditCardSearchNicknameInput.setText("");
						creditCardViewDisplay.setText("");

					}

				}
			}
		});
		creditCardSearchButton.setBounds(448, 6, 85, 29);
		creditCardViewTab.add(creditCardSearchButton);

		updateCreditCardList(creditCardViewTab, creditCardModel); 
			
		JLabel defaultNicknameDisclaimer = new JLabel("*default nickname is last four digits of card number");
		defaultNicknameDisclaimer.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		defaultNicknameDisclaimer.setBounds(501, 315, 251, 16);
		creditCardViewTab.add(defaultNicknameDisclaimer);

	}
	
	/**
	 * 
	 * @param creditCardTab - credit card tab to add the list to
	 * @param creditCardModel - model that holds the credit cards
	 */

	public static void updateCreditCardList(JPanel creditCardTab, DefaultListModel<String> creditCardModel) {
		creditCardModel.clear();
		String[] creditCards = SafeStore.getUsersCreditCards();
		Arrays.sort(creditCards,String.CASE_INSENSITIVE_ORDER);
		for(String card : creditCards) {
			creditCardModel.addElement(card);
		}
		JList<String> creditCardList = new JList<String>(creditCardModel);
		JScrollPane creditCardScrollPane = new JScrollPane(creditCardList);
		creditCardScrollPane.setBounds(195, 64, 113, 248);
		creditCardTab.add(creditCardScrollPane);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				creditCardSearchNicknameInput.setText(creditCardModel.get(creditCardList.getSelectedIndex()));
				creditCardSearchButton.doClick();
			}
		};
		creditCardList.addMouseListener(mouseListener);
	}
	
	private void resetAddCreditCard() {
		creditCardAddNumberInput.setText("");
		creditCardAddNicknameInput.setText("");
		creditCardAddExpDateInput.setText("");
		creditCardAddStateInput.setText("");
		creditCardAddCityInput.setText("");
		creditCardAddStreetAddressInput.setText("");
		creditCardAddZipInput.setText("");
		creditCardAddCVVInput.setText("");
	}

	/**
	 * initializes the credit card add tab for the credit card tab.
	 * 
	 */
	private void initializeCreditCardAddTab(JTabbedPane creditCardTabbedPane) {
		creditCardAddTab = new JPanel();
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


		updateCreditCardList(creditCardAddTab, creditCardModel); 
		
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

		creditCardAddStreetAddressInput = new JTextField();
		creditCardAddStreetAddressInput.setColumns(10);
		creditCardAddStreetAddressInput.setBounds(494, 152, 185, 26);
		creditCardAddTab.add(creditCardAddStreetAddressInput);

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
		creditCardAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cardNickname = creditCardAddNicknameInput.getText().trim();
				String cardNumber = creditCardAddNumberInput.getText().trim();
				String cardExpDate =  creditCardAddExpDateInput.getText().trim();
				String cardCVV = creditCardAddCVVInput.getText().trim();
				String cardStAddress = creditCardAddStreetAddressInput.getText().trim();
				
				String cardCity = creditCardAddCityInput.getText().trim();
				String cardState = creditCardAddStateInput.getText().trim();
				String cardZip = creditCardAddZipInput.getText().trim();
				
				String errors = Validation.validateCreditCardParams(cardNumber, cardExpDate,cardCVV);
				if(cardNumber.isEmpty() || cardExpDate.isEmpty() || cardCVV.isEmpty() || cardStAddress.isEmpty() || cardCity.isEmpty() || cardState.isEmpty() || cardZip.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "All fields marked with * must have a value");
				} else if (!errors.equals("")) {
					JOptionPane.showMessageDialog(frame, errors);
				}
			    else {
					if(SafeStore.addCreditCard(cardNumber,cardNickname,cardExpDate,cardCVV,cardStAddress,cardCity,cardState,cardZip)) {
						JOptionPane.showMessageDialog(frame, "Credit Card Added");
						resetAddCreditCard();
						updateCreditCardList(creditCardAddTab, creditCardModel);
					}else {
						JOptionPane.showMessageDialog(frame, "Credit card already added with number (and/or nickname): " + cardNumber);
						resetAddCreditCard();
					}
				}
			}
		});
		creditCardAddButton.setBounds(495, 274, 117, 29);
		creditCardAddTab.add(creditCardAddButton);

		JLabel creditCardAddRequiredFieldLabel = new JLabel("* required field");
		creditCardAddRequiredFieldLabel.setForeground(Color.RED);
		creditCardAddRequiredFieldLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
		creditCardAddRequiredFieldLabel.setBounds(619, 19, 61, 16);
		creditCardAddTab.add(creditCardAddRequiredFieldLabel);
	}
	
	private void resetModifyCreditCard() {
		creditCardModifyNumberInput.setText("");
		creditCardModifyCurNicknameInput.setText("");
		creditCardModifyNewNicknameInput.setText("");
		creditCardModifyExpDateInput.setText("");
		creditCardModifyStateInput.setText("");
		creditCardModifyCityInput.setText("");
		creditCardModifyStreetAddressInput.setText("");
		creditCardModifyZipInput.setText("");
		creditCardModifyCVVInput.setText("");
	}

	/**
	 * initializes the credit card modify tab for the credit card tab.
	 * 
	 */
	private void initializeCreditCardModifyTab(JTabbedPane creditCardTabbedPane) {
		creditCardModifyTab = new JPanel();
		creditCardTabbedPane.addTab("Modify", null, creditCardModifyTab, null);
		creditCardModifyTab.setLayout(null);

		updateCreditCardList(creditCardModifyTab, creditCardModel); 
		
		JLabel modifyCreditCardNumLabel = new JLabel("Card Number:");
		modifyCreditCardNumLabel.setBounds(371, 58, 98, 16);
		creditCardModifyTab.add(modifyCreditCardNumLabel);

		creditCardModifyNumberInput = new JTextField();
		creditCardModifyNumberInput.setColumns(10);
		creditCardModifyNumberInput.setBounds(462, 53, 205, 26);
		creditCardModifyTab.add(creditCardModifyNumberInput);

		creditCardModifyNewNicknameInput = new JTextField();
		creditCardModifyNewNicknameInput.setColumns(10);
		creditCardModifyNewNicknameInput.setBounds(469, 81, 198, 26);
		creditCardModifyTab.add(creditCardModifyNewNicknameInput);

		JLabel modifyCreditCardNewNicknameLabel = new JLabel("New Nickname:");
		modifyCreditCardNewNicknameLabel.setBounds(371, 86, 98, 16);
		creditCardModifyTab.add(modifyCreditCardNewNicknameLabel);

		JLabel modifyCreditCardExpDateLabel = new JLabel("Expiration Date:");
		modifyCreditCardExpDateLabel.setBounds(371, 114, 112, 16);
		creditCardModifyTab.add(modifyCreditCardExpDateLabel);

		creditCardModifyExpDateInput = new JTextField();
		creditCardModifyExpDateInput.setColumns(10);
		creditCardModifyExpDateInput.setBounds(479, 109, 188, 26);
		creditCardModifyTab.add(creditCardModifyExpDateInput);

		creditCardModifyCVVInput = new JTextField();
		creditCardModifyCVVInput.setColumns(10);
		creditCardModifyCVVInput.setBounds(401, 137, 266, 26);
		creditCardModifyTab.add(creditCardModifyCVVInput);

		JLabel modifyCreditCardCVVLabel = new JLabel("CVV:");
		modifyCreditCardCVVLabel.setBounds(371, 142, 40, 16);
		creditCardModifyTab.add(modifyCreditCardCVVLabel);

		creditCardModifyStreetAddressInput = new JTextField();
		creditCardModifyStreetAddressInput.setColumns(10);
		creditCardModifyStreetAddressInput.setBounds(469, 165, 197, 26);
		creditCardModifyTab.add(creditCardModifyStreetAddressInput);

		JLabel modifyCreditCardStreetAddressLabel = new JLabel("Street Address:");
		modifyCreditCardStreetAddressLabel.setBounds(371, 170, 106, 16);
		creditCardModifyTab.add(modifyCreditCardStreetAddressLabel);

		JLabel modifyCreditCardCityLabel = new JLabel("City:");
		modifyCreditCardCityLabel.setBounds(371, 198, 40, 16);
		creditCardModifyTab.add(modifyCreditCardCityLabel);

		creditCardModifyCityInput = new JTextField();
		creditCardModifyCityInput.setColumns(10);
		creditCardModifyCityInput.setBounds(401, 193, 266, 26);
		creditCardModifyTab.add(creditCardModifyCityInput);

		creditCardModifyStateInput = new JTextField();
		creditCardModifyStateInput.setColumns(10);
		creditCardModifyStateInput.setBounds(411, 221, 256, 26);
		creditCardModifyTab.add(creditCardModifyStateInput);

		JLabel modifyCreditCardStateLabel = new JLabel("State:");
		modifyCreditCardStateLabel.setBounds(371, 226, 52, 16);
		creditCardModifyTab.add(modifyCreditCardStateLabel);

		JLabel modifyCreditCardZipLabel = new JLabel("Zip Code:");
		modifyCreditCardZipLabel.setBounds(371, 254, 77, 16);
		creditCardModifyTab.add(modifyCreditCardZipLabel);

		creditCardModifyZipInput = new JTextField();
		creditCardModifyZipInput.setColumns(10);
		creditCardModifyZipInput.setBounds(432, 249, 235, 26);
		creditCardModifyTab.add(creditCardModifyZipInput);

		JButton modifyCreditCardButton = new JButton("Modify");
		modifyCreditCardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cardCurNickname = creditCardModifyCurNicknameInput.getText().trim();
				String cardNewNickname = creditCardModifyNewNicknameInput.getText().trim();
				String cardNumber = creditCardModifyNumberInput.getText().trim();
				String cardExpDate =  creditCardModifyExpDateInput.getText().trim();
				String cardCVV = creditCardModifyCVVInput.getText().trim();
				String cardStAddress = creditCardModifyStreetAddressInput.getText().trim();
				String cardCity = creditCardModifyCityInput.getText().trim();
				String cardState = creditCardModifyStateInput.getText().trim();
				String cardZip = creditCardModifyZipInput.getText().trim();
				String errors = Validation.validateCreditCardParams(cardNumber, cardExpDate, cardCVV);
				if(creditCardModifyCurNicknameInput.getText().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Enter a credit card to modify");
				} else if (!errors.equals("")) {
					JOptionPane.showMessageDialog(frame, errors);
				} else {
					if(SafeStore.modifyCreditCard(cardCurNickname,cardNewNickname,cardNumber,cardExpDate,cardCVV,cardStAddress,cardCity,cardState,cardZip)) {
						JOptionPane.showMessageDialog(frame, "Credit Card Updated");
						resetModifyCreditCard();
						updateCreditCardList(creditCardModifyTab, creditCardModel);
					}else {
						JOptionPane.showMessageDialog(frame, "Couldn't update credit card named" + cardCurNickname);
						resetModifyCreditCard();
					}
				}
			}
		});
		modifyCreditCardButton.setBounds(482, 287, 117, 29);
		creditCardModifyTab.add(modifyCreditCardButton);

		JLabel modifyCreditCardCurNicknameLabel = new JLabel("Current Nickname*:");
		modifyCreditCardCurNicknameLabel.setBounds(371, 27, 131, 16);
		creditCardModifyTab.add(modifyCreditCardCurNicknameLabel);

		creditCardModifyCurNicknameInput = new JTextField();
		creditCardModifyCurNicknameInput.setColumns(10);
		creditCardModifyCurNicknameInput.setBounds(499, 22, 168, 26);
		creditCardModifyTab.add(creditCardModifyCurNicknameInput);

		JLabel creditCardModifyRequiredFieldLabel = new JLabel("* required field");
		creditCardModifyRequiredFieldLabel.setForeground(Color.RED);
		creditCardModifyRequiredFieldLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 8));
		creditCardModifyRequiredFieldLabel.setBounds(606, 6, 61, 16);
		creditCardModifyTab.add(creditCardModifyRequiredFieldLabel);
	}

	/**
	 * Initialize the contents of the credit card tab.
	 */
	public void initializeCreditCardTab(JTabbedPane safeStore) {
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
		
		JPanel[] panels = {creditCardViewTab, creditCardAddTab, creditCardModifyTab};

		creditCardTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateCreditCardList(panels[creditCardTabbedPane.getSelectedIndex()],creditCardModel);
				
			}


		});

	}

}

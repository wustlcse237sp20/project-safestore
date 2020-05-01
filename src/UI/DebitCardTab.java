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

import card.DebitCard;

public class DebitCardTab {

	private JFrame frame;
	private static JTextField debitCardSearchInput;
	private static JButton debitCardSearchButton;
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
	private DefaultListModel<String> debitCardModel = new DefaultListModel<String>();
	private JPanel debitCardViewTab;
	private JPanel debitCardAddTab;
	private JPanel debitCardModifyTab;

	public DebitCardTab(JFrame frame) {
		this.frame = frame;
	}

	/**
	 * initializes the debit card view tab for the debit card tab.
	 * 
	 */
	private void initializeDebitCardViewTab(JTabbedPane debitCardTabbedPane) {
		debitCardViewTab = new JPanel();
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

		updateDebitCardList(debitCardViewTab, debitCardModel);

		JTextArea debitCardViewDisplay = new JTextArea();
		debitCardViewDisplay.setText("");
		debitCardViewDisplay.setBounds(372, 64, 293, 248);
		debitCardViewTab.add(debitCardViewDisplay);

		JLabel debitCardDefaultNicknameDisclaimer = new JLabel("*default nickname is last four digits of card number");
		debitCardDefaultNicknameDisclaimer.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		debitCardDefaultNicknameDisclaimer.setBounds(477, 315, 251, 16);
		debitCardViewTab.add(debitCardDefaultNicknameDisclaimer);

		debitCardSearchButton = new JButton("Search");
		debitCardSearchButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(debitCardSearchInput.getText().isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Enter the card's nickname (default is last 4 digits)");
				} else {
					DebitCard debitCard = SafeStore.getDebitCardInfo(debitCardSearchInput.getText());
					if(debitCard != null) {
						debitCardViewDisplay.setText(debitCard.toString());
						debitCardSearchInput.setText("");
					} else {
						JOptionPane.showMessageDialog(frame, "You have no debit card stored under " + debitCardSearchInput.getText());
						debitCardSearchInput.setText("");
						debitCardViewDisplay.setText("");
					}

				}
			}
		});
		debitCardSearchButton.setBounds(424, 6, 85, 29);
		debitCardViewTab.add(debitCardSearchButton);
	}
	/**
	 * 
	 * @param debitCardTab - debit card tab to add the list to
	 * @param debitCardModel - model that holds the debit cards
	 */

	public static void updateDebitCardList(JPanel debitCardTab, DefaultListModel<String> debitCardModel) {
		debitCardModel.clear();
		String[] debitCards = SafeStore.getUsersDebitCards();
		Arrays.sort(debitCards,String.CASE_INSENSITIVE_ORDER);
		for(String card : debitCards) {
			debitCardModel.addElement(card);
		}
		JList<String> debitCardList = new JList<String>(debitCardModel);
		JScrollPane debitCardScrollPane = new JScrollPane(debitCardList);
		debitCardScrollPane.setBounds(171, 64, 113, 248);
		debitCardTab.add(debitCardScrollPane);

		MouseListener mouseListener = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				debitCardSearchInput.setText(debitCardModel.get(debitCardList.getSelectedIndex()));
				debitCardSearchButton.doClick();
			}
		};
		debitCardList.addMouseListener(mouseListener);
	}

	private void resetAddDebitCard() {
		debitCardAddNumberInput.setText("");
		debitCardAddNicknameInput.setText("");
		debitCardAddExpDateInput.setText("");
		debitCardAddStateInput.setText("");
		debitCardAddCityInput.setText("");
		debitCardAddStAdressInput.setText("");
		debitCardAddZipInput.setText("");
		debitCardAddCvvInput.setText("");
		debitCardAddPinInput.setText("");
	}

	/**
	 * initializes the debit card add tab for the debit card tab.
	 * 
	 */
	private void initializeDebitCardAddTab(JTabbedPane debitCardTabbedPane) {
		debitCardAddTab = new JPanel();
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

		updateDebitCardList(debitCardAddTab, debitCardModel);

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

		JButton debitCardAddButton = new JButton("Add");
		debitCardAddButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cardNickname = debitCardAddNicknameInput.getText().trim();
				String cardNumber = debitCardAddNumberInput.getText().trim();
				String cardExpDate =  debitCardAddExpDateInput.getText().trim();
				String cardCVV = debitCardAddCvvInput.getText().trim();
				String cardStAddress = debitCardAddStAdressInput.getText().trim();
				String cardCity = debitCardAddCityInput.getText().trim();
				String cardState = debitCardAddStateInput.getText().trim();
				String cardZip = debitCardAddZipInput.getText().trim();
				String cardPin = debitCardAddPinInput.getText().trim();
				String errors = Validation.validateDebitCardParams(cardNumber, cardExpDate, cardCVV, cardPin);

				if (cardNumber.isEmpty() || cardExpDate.isEmpty() || cardCVV.isEmpty() || cardPin.isEmpty() || cardStAddress.isEmpty() || cardCity.isEmpty() || cardState.isEmpty() || cardZip.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "All fields marked with * must have a value");
				} else if (!errors.equals("")) {
					JOptionPane.showMessageDialog(frame, errors);
				} else {
					if (SafeStore.addDebitCard(cardNumber,cardNickname,cardExpDate,cardCVV,cardPin,cardStAddress,cardCity,cardState,cardZip)) {
						JOptionPane.showMessageDialog(frame, "Debit Card Added");
						resetAddDebitCard();
						updateDebitCardList(debitCardAddTab, debitCardModel);
					
					} else {
						JOptionPane.showMessageDialog(frame, "Debit card already added with number (and/or nickname): " + cardNumber);
						resetAddDebitCard();
					}
				}
			}
		});
		debitCardAddButton.setBounds(495, 302, 117, 29);
		debitCardAddTab.add(debitCardAddButton);
	}

	private void resetModifyDebitCard() {
		debitCardModifyCurrNicknameInput.setText("");
		debitCardModifyNewNicknameInput.setText("");
		debitCardModifyNumInput.setText("");
		debitCardModifyExpDateInput.setText("");
		debitCardModifyCvvInput.setText("");
		debitCardModifyPinInput.setText("");
		debitCardModifyStAddressInput.setText("");
		debitCardModifyCityInput.setText("");
		debitCardModifyStateInput.setText("");
		debitCardModifyZipInput.setText("");	
	}

	/**
	 * initializes the debit card modify tab for the debit card tab.
	 * 
	 */
	private void initializeDebitCardModifyTab(JTabbedPane debitCardTabbedPane) {
		debitCardModifyTab = new JPanel();
		debitCardTabbedPane.addTab("Modify", null, debitCardModifyTab, null);
		debitCardModifyTab.setLayout(null);
	
		updateDebitCardList(debitCardModifyTab, debitCardModel);

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

		JLabel debitCardModifyCurNicknameLabel = new JLabel("Current Nickname*:");
		debitCardModifyCurNicknameLabel.setBounds(398, 25, 124, 16);
		debitCardModifyTab.add(debitCardModifyCurNicknameLabel);

		debitCardModifyCurrNicknameInput = new JTextField();
		debitCardModifyCurrNicknameInput.setColumns(10);
		debitCardModifyCurrNicknameInput.setBounds(520, 20, 174, 26);
		debitCardModifyTab.add(debitCardModifyCurrNicknameInput);

		JButton debitCardModifyButton = new JButton("Modify");
		debitCardModifyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cardCurNickname = debitCardModifyCurrNicknameInput.getText().trim();
				String cardNewNickname = debitCardModifyNewNicknameInput.getText().trim();
				String cardNumber = debitCardModifyNumInput.getText().trim();
				String cardExpDate =  debitCardModifyExpDateInput.getText().trim();
				String cardCVV = debitCardModifyCvvInput.getText().trim();
				String cardStAddress = debitCardModifyStAddressInput.getText().trim();
				String cardCity = debitCardModifyCityInput.getText().trim();
				String cardState = debitCardModifyStateInput.getText().trim();
				String cardZip = debitCardModifyZipInput.getText().trim();
				String cardPin = debitCardModifyPinInput.getText().trim();
				String errors = Validation.validateDebitCardParams(cardNumber, cardExpDate,  cardCVV, cardPin);
				if (cardCurNickname.isEmpty()) {
					JOptionPane.showMessageDialog(frame, "Enter a debit card to modify");
				} else if (!errors.equals("")) {
					JOptionPane.showMessageDialog(frame, errors);
				} else {
					if(SafeStore.modifyDebitCard(cardCurNickname,cardNewNickname,cardNumber,cardExpDate,cardCVV,cardPin,cardStAddress,cardCity,cardState,cardZip)) {
						JOptionPane.showMessageDialog(frame, "Debit Card Updated");
						resetModifyDebitCard();
						updateDebitCardList(debitCardModifyTab, debitCardModel);
						
					}else {
						JOptionPane.showMessageDialog(frame, "Couldn't update debit card named: " + cardCurNickname + " \nMake sure new nickname does not already exist");
						resetModifyDebitCard();
					}
				}
			}
		});
		debitCardModifyButton.setBounds(509, 307, 117, 29);
		debitCardModifyTab.add(debitCardModifyButton);
	}

	/**
	 * Initialize the contents of the debit card tab.
	 */
	public void initializeDebitCardTab(JTabbedPane safeStore) {
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

		JButton debitCardLogoutButton = new JButton("Logout");
		debitCardLogoutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SafeStore.logout();
			}
		});
		debitCardLogoutButton.setBounds(656, 6, 117, 29);
		debitCards.add(debitCardLogoutButton);

		JPanel[] panels = {debitCardViewTab, debitCardAddTab, debitCardModifyTab};

		debitCardTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateDebitCardList(panels[debitCardTabbedPane.getSelectedIndex()],debitCardModel);
			}


		});
	}
}

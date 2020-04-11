package UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

public class FrontEnd {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
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
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane safeStore = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(safeStore, BorderLayout.CENTER);
		
		JPanel websiteAccounts = new JPanel();
		safeStore.addTab("Website Accounts", null, websiteAccounts, null);
		websiteAccounts.setLayout(null);
		
		JLabel websiteAccountsLabel = new JLabel("Website Accounts");
		websiteAccountsLabel.setBounds(333, 5, 112, 16);
		websiteAccounts.add(websiteAccountsLabel);
		
		JPanel creditCards = new JPanel();
		safeStore.addTab("Credit Cards", null, creditCards, null);
		creditCards.setLayout(null);
		
		JLabel creditCardLabel = new JLabel("Credit Cards");
		creditCardLabel.setBounds(350, 5, 78, 16);
		creditCards.add(creditCardLabel);
		
		JPanel debitCards = new JPanel();
		safeStore.addTab("Debit Cards", null, debitCards, null);
		debitCards.setLayout(null);
		
		JLabel debitCardLabel = new JLabel("Debit Cards");
		debitCardLabel.setBounds(352, 5, 74, 16);
		debitCards.add(debitCardLabel);
	}

}

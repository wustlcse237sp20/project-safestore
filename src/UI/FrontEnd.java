package UI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;

public class FrontEnd {

	private static JFrame frame;
	
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
	 * Close the window
	 */
	public static void closeWindow() {
		frame.setVisible(false);
		frame.dispose();
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
		frame.setResizable(false);

		JTabbedPane safeStore = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(safeStore, BorderLayout.CENTER);
		
		WebsiteAccountTab websiteAccountTab = new WebsiteAccountTab(this.frame);
		websiteAccountTab.initializeWebAccountTab(safeStore);
		CreditCardTab creditCardTab = new CreditCardTab(this.frame);
		creditCardTab.initializeCreditCardTab(safeStore);
		DebitCardTab debitCardTab = new DebitCardTab(this.frame);
		debitCardTab.initializeDebitCardTab(safeStore);

	}

	
}

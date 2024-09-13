package librarysystem;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import business.ControllerInterface;
import business.SystemController;
import dataaccess.Auth;


public class LibrarySystem extends JFrame implements LibWindow {
	ControllerInterface ci = new SystemController();
	public final static LibrarySystem INSTANCE =new LibrarySystem();
	JPanel mainPanel;
	JMenuBar menuBar;
    JMenu optionsMenu;
	JMenu librarianMenu;
	JMenu adminMenu;

	JMenuItem login, checkoutBookM, admBooks, adminMembers;
    String pathToImage;
	String userName ;
	Auth userRole;
	String title;
    private boolean isInitialized = false;
    
    private static LibWindow[] allWindows = { 
    	LibrarySystem.INSTANCE,
		LoginWindow.INSTANCE,
		MembersWindow.INSTANCE,
		BooksWindow.INSTANCE,
		CheckoutWindow.INSTANCE
	};

	public static void hideAllWindows() {		
		for(LibWindow frame: allWindows) {
			frame.setVisible(false);			
		}
	}
     
    private LibrarySystem() {}
    
    public void init() {
    	formatContentPane();
    	setPathToImage();
    	insertSplashImage();

		title = "Library Application";
		userName = "";
		userRole = Auth.NONE;
		createMenus();
		updateUI();

		setSize(660,500);
		isInitialized = true;
    }

	public  void updateUI() {
		setTitle(title + " user: " + userName);
		optionsMenu.setEnabled(true);
		adminMenu.setEnabled(false);
		librarianMenu.setEnabled(false);
		switch (userRole) {
			case Auth.LIBRARIAN:
				librarianMenu.setEnabled(true);
				adminMenu.setEnabled(false);
				break;
			case Auth.ADMIN:
				adminMenu.setEnabled(true);
				librarianMenu.setEnabled(false);
				break;
			case Auth.BOTH:
				librarianMenu.setEnabled(true);
				adminMenu.setEnabled(true);
					break;
			case Auth.NONE:
				librarianMenu.setEnabled(false);
				adminMenu.setEnabled(false);
				break;
		}
	}

	private void formatContentPane() {
		mainPanel = new JPanel();
		mainPanel.setLayout(new GridLayout(1,1));
		getContentPane().add(mainPanel);	
	}
    
    private void setPathToImage() {
    	String currDirectory = System.getProperty("user.dir");
    	pathToImage = currDirectory+"\\src\\librarysystem\\library.jpg";
    }
    
    private void insertSplashImage() {
        ImageIcon image = new ImageIcon(pathToImage);
		mainPanel.add(new JLabel(image));	
    }
    private void createMenus() {
    	menuBar = new JMenuBar();
		menuBar.setBorder(BorderFactory.createRaisedBevelBorder());
		addMenuItems();
		setJMenuBar(menuBar);		
    }
    
    private void addMenuItems() {
		optionsMenu = new JMenu("Login");
		librarianMenu = new JMenu("Librarian");
		adminMenu = new JMenu("Admin");
		menuBar.add(optionsMenu);
		menuBar.add(librarianMenu);
		menuBar.add(adminMenu);
		login = new JMenuItem("Change User");
		login.addActionListener(new LoginListener());
		checkoutBookM = new JMenuItem("Checkout Book");
		checkoutBookM.addActionListener(new CheckoutBookListener());
		admBooks = new JMenuItem("Admin Books");
		admBooks.addActionListener(new AdminBookListener());
		adminMembers = new JMenuItem("Admin Members");
		adminMembers.addActionListener(new AdminMembersListener());
		optionsMenu.add(login);
		adminMenu.add(adminMembers);
		adminMenu.add(admBooks);
		librarianMenu.add(checkoutBookM);
    }
    
    class LoginListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			LibrarySystem.hideAllWindows();
			LoginWindow.INSTANCE.init();
			Util.centerFrameOnDesktop(LoginWindow.INSTANCE);
			LoginWindow.INSTANCE.setVisible(true);
			
		}
    	
    }

	class CheckoutBookListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			LibrarySystem.hideAllWindows();
			CheckoutWindow.INSTANCE.init();
			Util.centerFrameOnDesktop(CheckoutWindow.INSTANCE);
			CheckoutWindow.INSTANCE.setVisible(true);

		}

	}
    class AdminBookListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			LibrarySystem.hideAllWindows();
			BooksWindow.INSTANCE.init();
			Util.centerFrameOnDesktop(BooksWindow.INSTANCE);
			BooksWindow.INSTANCE.setVisible(true);
			
		}
    	
    }
    
    class AdminMembersListener implements ActionListener {

    	@Override
		public void actionPerformed(ActionEvent e) {
			LibrarySystem.hideAllWindows();
			MembersWindow.INSTANCE.init();
			MembersWindow.INSTANCE.pack();
			Util.centerFrameOnDesktop(MembersWindow.INSTANCE);
			MembersWindow.INSTANCE.setVisible(true);
		}
    	
    }

	@Override
	public boolean isInitialized() {
		return isInitialized;
	}


	@Override
	public void isInitialized(boolean val) {
		isInitialized =val;
		
	}
    
}

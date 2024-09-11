package librarysystem;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

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
    public final static LibrarySystem INSTANCE = new LibrarySystem();
    JPanel mainPanel;
    JMenuBar menuBar;
    JMenu optionsMenu;
    JMenu librarianMenu;
    JMenu adminMenu;

    JMenuItem login, chechoutBoook, allBookIds, allMemberIds, oneBook;
    String pathToImage;
    String userName;
    Auth userRole;
    String title;
    private boolean isInitialized = false;

    private static LibWindow[] allWindows = {
            LibrarySystem.INSTANCE,
            LoginWindow.INSTANCE,
            AllMemberIdsWindow.INSTANCE,
            AllBookIdsWindow.INSTANCE
    };

    public static void hideAllWindows() {
        for (LibWindow frame : allWindows) {
            frame.setVisible(false);
        }
    }

    private LibrarySystem() {
    }

    public void init() {
        formatContentPane();
        setPathToImage();
        insertSplashImage();

        title = "Library Application";
        userName = "";
        //@todo disable menus by defaul
        userRole = Auth.BOTH;
        createMenus();
        updateUI();

        setSize(660, 500);
        isInitialized = true;
    }

    private void updateUI() {
        setTitle(title + " userid = " + userName);
        optionsMenu.setEnabled(true);
        librarianMenu.setEnabled(false);
        adminMenu.setEnabled(false);

        switch (userRole) {
            case Auth.LIBRARIAN:
                librarianMenu.setEnabled(true);
                break;
            case Auth.ADMIN:
                adminMenu.setEnabled(true);
                break;
            case Auth.BOTH:
                librarianMenu.setEnabled(true);
                adminMenu.setEnabled(true);
                break;
        }
    }

    private void formatContentPane() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(1, 1));
        getContentPane().add(mainPanel);
    }

    private void setPathToImage() {
        String currDirectory = System.getProperty("user.dir");
        pathToImage = currDirectory + "\\src\\librarysystem\\library.jpg";
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
        chechoutBoook = new JMenuItem("Checkout Book");
        allBookIds = new JMenuItem("Admin Boooks");
        allBookIds.addActionListener(new AllBookIdsListener());
        oneBook = new JMenuItem("Add copies");
        allMemberIds = new JMenuItem("Admin Members");
        allMemberIds.addActionListener(new AllMemberIdsListener());
        optionsMenu.add(login);
        adminMenu.add(allMemberIds);
        adminMenu.add(allBookIds);
        adminMenu.add(oneBook);
        librarianMenu.add(chechoutBoook);
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

    class AllBookIdsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            LibrarySystem.hideAllWindows();
            AllBookIdsWindow.INSTANCE.init();

            List<String> ids = ci.allBookIds();
            Collections.sort(ids);
            StringBuilder sb = new StringBuilder();
            for (String s : ids) {
                sb.append(s + "\n");
            }
            System.out.println(sb.toString());
            AllBookIdsWindow.INSTANCE.setData(sb.toString());
            AllBookIdsWindow.INSTANCE.pack();
            //AllBookIdsWindow.INSTANCE.setSize(660,500);
            Util.centerFrameOnDesktop(AllBookIdsWindow.INSTANCE);
            AllBookIdsWindow.INSTANCE.setVisible(true);

        }

    }

    class AllMemberIdsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            LibrarySystem.hideAllWindows();
            AllMemberIdsWindow.INSTANCE.init();
            AllMemberIdsWindow.INSTANCE.pack();
            AllMemberIdsWindow.INSTANCE.setVisible(true);


            LibrarySystem.hideAllWindows();
            AllBookIdsWindow.INSTANCE.init();

            List<String> ids = ci.allMemberIds();
            Collections.sort(ids);
            StringBuilder sb = new StringBuilder();
            for (String s : ids) {
                sb.append(s + "\n");
            }
            System.out.println(sb.toString());
            AllMemberIdsWindow.INSTANCE.setData(sb.toString());
            AllMemberIdsWindow.INSTANCE.pack();
            //AllMemberIdsWindow.INSTANCE.setSize(660,500);
            Util.centerFrameOnDesktop(AllMemberIdsWindow.INSTANCE);
            AllMemberIdsWindow.INSTANCE.setVisible(true);


        }

    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }


    @Override
    public void isInitialized(boolean val) {
        isInitialized = val;

    }

}

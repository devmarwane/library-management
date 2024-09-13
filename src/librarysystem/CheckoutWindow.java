package librarysystem;

import business.CheckoutEntry;
import business.ControllerInterface;
import business.SystemController;

import javax.swing.*;
import java.awt.*;

public class CheckoutWindow extends JFrame implements LibWindow{
    public static final CheckoutWindow INSTANCE = new CheckoutWindow();
    private static final ControllerInterface controller = new SystemController();
    private JTextField memberID;
    private JTextField isbn;
    private JButton checkoutButton;
    private JButton backToMainButton;
    private JPanel mainPanel;
    private boolean isInitialized = false;



    @Override
    public void init() {
        setContentPane(mainPanel);
        setTitle("Checkout a Book");
        addBackButtonListener(backToMainButton);
        addCheckoutButtonListener(checkoutButton);
        pack();
        setLocationRelativeTo(null);
        isInitialized(true);
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public void isInitialized(boolean val) {
        isInitialized = val;
    }

    private void addBackButtonListener(JButton butn) {
        butn.addActionListener(evt -> {
            LibrarySystem.hideAllWindows();
            LibrarySystem.INSTANCE.setVisible(true);
        });
    }

    private void addCheckoutButtonListener(JButton butn) {
        butn.addActionListener(evt -> {
            checkoutBook();

        });
    }

    private void checkoutBook() {
        String newMember = this.memberID.getText();
        if (newMember.isEmpty()){
            JOptionPane.showMessageDialog(this, "member ID is required",
                    "Failed Checkout",JOptionPane.ERROR_MESSAGE);
            return;
        }
        String newisbn	= this.isbn.getText();
        if (newisbn.isEmpty()){
            JOptionPane.showMessageDialog(this, "ISBN is required",
                    "Failed login",JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            CheckoutEntry entry = controller.checkoutBook(newMember,newisbn);
            String message = "You have checked out a book!" + "\n" +
                    "Title: \t" + entry.getBookcopy().getBook().getTitle() + "\n" +
                    "Chekout Date: \t" + entry.getChekoutDate() + "\n" +
                    "Due data: \t" + entry.getDueDate();

            JOptionPane.showMessageDialog(this, message, "Successful checkout", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                CheckoutWindow mf = CheckoutWindow.INSTANCE;
                mf.init();
                mf.setVisible(true);
            }
        });
    }
}

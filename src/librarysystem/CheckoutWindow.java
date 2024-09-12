package librarysystem;

import javax.swing.*;

public class CheckoutWindow extends JFrame implements LibWindow{
    public static final CheckoutWindow INSTANCE = new CheckoutWindow();
    private JTextField textField1;
    private JTextField textField2;
    private JButton checkoutButton;
    private JButton backToMainButton;
    private JPanel mainPanel;
    private boolean isInitialized = false;



    @Override
    public void init() {
        setContentPane(mainPanel);
        setTitle("Checkout a Book");
        addBackButtonListener(backToMainButton);
        addBackButtonListener(checkoutButton);
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
        // Validate parameters if fail show messsage and return
        //
        try {
            checkoutBook();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

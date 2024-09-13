package librarysystem;

import business.*;

import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;



public class CheckoutWindow extends JFrame implements LibWindow{
    public static final CheckoutWindow INSTANCE = new CheckoutWindow();
    private static final ControllerInterface controller = new SystemController();
    private JTextField memberID;
    private JTextField isbn;
    private JButton checkoutButton;
    private JButton backToMainButton;
    private JPanel mainPanel;
    private JTable checkoutHistoryTable;
    private DefaultTableModel checkoutHistoryTableModel;
    private boolean isInitialized = false;
    LibraryMember member;


    @Override

    public void init() {
        setContentPane(mainPanel);
        checkoutHistoryTableModel= new DefaultTableModel(new String[]{"Member ID", "Member Name","ISBN",
                "title","Checkout Date", "Due Date"},1);
        checkoutHistoryTable.setModel(checkoutHistoryTableModel);
        checkoutHistoryTable.setVisible(true);


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
        checkoutRecordToTable();

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
            member  = controller.checkoutBook(newMember,newisbn);
            CheckoutRecord record = member.getCheckoutRecord();
            CheckoutEntry entry = record.getLastEntry();
            String message = member.getFullName() + " has checked out a book!" + "\n" +
                    "Title: \t\t" + entry.getBookcopy().getBook().getTitle() + "\n" +
                    "Chekout Date: \t" + entry.getCheckoutDate() + "\n" +
                    "Due date: \t\t" + entry.getDueDate();

            JOptionPane.showMessageDialog(this, message, "Successful checkout", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }
    private void checkoutRecordToTable() {
        checkoutHistoryTableModel.setColumnIdentifiers(new String[]{"ISBN",
                "title","Checkout Date", "Due Date"});

        checkoutHistoryTableModel.setRowCount(0);
        if (member != null){
            List<CheckoutEntry> entries =  member.getCheckoutRecord().getEntries();
            for(CheckoutEntry e : entries){
                checkoutHistoryTableModel.addRow(new Object[]{
                        e.getBookcopy().getBook().getIsbn(),
                        e.getBookcopy().getBook().getTitle(),
                                e.getCheckoutDate(), e.getDueDate()});
            }

        }
        checkoutHistoryTableModel.addRow(new String[]{"uno","dos","tres","cuatro"});

    }

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                CheckoutWindow mf = CheckoutWindow.INSTANCE;
                mf.init();
                mf.setVisible(true);
                mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        });
    }


    private void createUIComponents() {
        checkoutHistoryTableModel = new DefaultTableModel(new String[]{"Member ID", "Member Name","ISBN",
                "title","Checkout Date", "Due Date"},1);
        checkoutHistoryTable.setModel(checkoutHistoryTableModel);
        checkoutHistoryTable.setVisible(true);
    }
}

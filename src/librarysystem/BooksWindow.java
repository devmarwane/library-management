package librarysystem;

import business.*;
import dataaccess.DataAccess;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class BooksWindow extends JFrame{
    private JTextField txtIsbn;
    private JTextField txtTitle;
    private JRadioButton rdBtn7days;
    private JRadioButton rdBtn21days;
    private JList lstAuthors;
    private JTable tblBooks;
    private JPanel mainPnl;
    private JScrollPane tblBooksScroll;
    private JButton clearFormButton;
    private JButton editBookButton;
    private JButton deleteBookButton;
    private JButton newAuthorButton;


    //Services
    private DataAccess dataAccess;
    private ControllerInterface controller;

    private DefaultTableModel bookTableModel;
    private DefaultListModel<Author> authorListModel;  // Model for JList to display authors

    public BooksWindow() {
        // Default visibility is false. You have enabled visibility true
        setVisible(true);
        // Terminates the Application when the frame is closed.
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPnl);
        setTitle("Books management");
        // Provide the frame width and height
        setSize(800, 400);
        // Make your screen center
        setLocationRelativeTo(null);
        setResizable(false);// If you wish
        //Data
        controller = new SystemController();
        // Table to display the books
        bookTableModel = new DefaultTableModel(new String[]{"ISBN", "Title", "Authors", "Copies"}, 0);
        tblBooks = new JTable(bookTableModel){
            @Override
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };
        tblBooks.setCellSelectionEnabled(false);
        tblBooks.setRowSelectionAllowed(true);
        //Load Books
        loadBooks();
        //Load authors
        loadAuthors();

        tblBooksScroll.setViewportView(tblBooks);
        tblBooksScroll.setSize(600, 400);

        clearFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtTitle.setText("");
                txtIsbn.setText("");
                lstAuthors.clearSelection();
                tblBooks.clearSelection();
                //showAddAuthorDialog(new Author("Marwan","Bidamou","000000",null,"bingo"));
                showAddAuthorDialog(null);
            }
        });
    }

    // Load books from the SystemController (allBooks method) and populate the table
    private void loadBooks() {
        List<Book> books = controller.allBooks();  // Use the allBooks method from SystemController
        for (Book book : books) {
            System.out.println("Iteration book :" +book.getIsbn());
            String authors = String.join(", ", book.getAuthors().stream().map(Author::getFullName).toArray(String[]::new));
            System.out.println("Authors :" +authors);

            bookTableModel.addRow(new Object[]{book.getIsbn(), book.getTitle(), authors, book.getNumCopies()});
        }
    }

    private void loadAuthors() {
        authorListModel = new DefaultListModel<>();  // Initialize the model for the authors list
        lstAuthors.setModel(authorListModel);  // Attach the model to the JList

        List<Author> authors = controller.allAuthors();  // Assuming you have this method in SystemController
        for (Author author : authors) {
            authorListModel.addElement(author);  // Add each author to the list model
        }
    }

    private void showAddAuthorDialog(Author author) {
        // Create a new JDialog
        JDialog dialog = new JDialog(this, author!=null?"Edit Author":"Add Author", true);  // Modal dialog
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(9, 2, 5, 25));  // 9 rows and 2 columns for labels and input fields

        // Input fields for Author details
        JLabel lblFirstName = new JLabel("First Name:");
        JTextField txtFirstName = new JTextField(author!=null?author.getFirstName():"");

        JLabel lblLastName = new JLabel("Last Name:");
        JTextField txtLastName = new JTextField(author!=null?author.getLastName():"");

        JLabel lblTelephone = new JLabel("Telephone:");
        JTextField txtTelephone = new JTextField(author!=null?author.getTelephone():"");

        // Address details
        JLabel lblStreet = new JLabel("Street:");
        JTextField txtStreet = new JTextField(author!=null?author.getAddress().getStreet():"");

        JLabel lblCity = new JLabel("City:");
        JTextField txtCity = new JTextField(author!=null?author.getAddress().getCity():"");

        JLabel lblState = new JLabel("State:");
        JTextField txtState = new JTextField(author!=null?author.getAddress().getState():"");

        JLabel lblZip = new JLabel("ZIP Code:");
        JTextField txtZip = new JTextField(author!=null?author.getAddress().getZip():"");

        JLabel lblBio = new JLabel("Short Bio:");
        JTextArea txtBio = new JTextArea(author!=null?author.getBio():"",2, 20);  // Use a JTextArea for bio

        // Add components to the dialog
        dialog.add(lblFirstName);
        dialog.add(txtFirstName);
        dialog.add(lblLastName);
        dialog.add(txtLastName);
        dialog.add(lblTelephone);
        dialog.add(txtTelephone);

        // Address fields
        dialog.add(lblStreet);
        dialog.add(txtStreet);
        dialog.add(lblCity);
        dialog.add(txtCity);
        dialog.add(lblState);
        dialog.add(txtState);
        dialog.add(lblZip);
        dialog.add(txtZip);

        // Bio field
        dialog.add(lblBio);
        dialog.add(txtBio);

        // Buttons
        JButton btnSubmit = new JButton("Submit");
        JButton btnCancel = new JButton("Cancel");
        dialog.add(btnSubmit);
        dialog.add(btnCancel);

        // Add functionality to the buttons
        btnSubmit.addActionListener(e -> {
            // Capture input data
            String firstName = txtFirstName.getText();
            String lastName = txtLastName.getText();
            String telephone = txtTelephone.getText();
            String street = txtStreet.getText();
            String city = txtCity.getText();
            String state = txtState.getText();
            String zip = txtZip.getText();
            String bio = txtBio.getText();

            if (!firstName.isEmpty() && !lastName.isEmpty() && !telephone.isEmpty() && !street.isEmpty()
                    && !city.isEmpty() && !state.isEmpty() && !zip.isEmpty() && !bio.isEmpty()) {

                // Create a new Address and Author object
                Address address = new Address(street, city, state, zip);
                Author newAuthor = new Author(firstName, lastName, telephone, address, bio);

                // Add the new author to the JList (assuming you are using authorListModel for the JList)
                authorListModel.addElement(newAuthor);  // Add the author to the list

                dialog.dispose();  // Close the dialog
            } else {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields!");
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());  // Close the dialog

        // Show the dialog
        dialog.setLocationRelativeTo(this);  // Center the dialog on the main window
        dialog.setVisible(true);
    }


    public static void main(String[] args) {
    /*
    While it is not mandatory to use EventQueue.invokeLater,
    it is a best practice for all Swing applications to ensure
    thread safety and avoid potential concurrency issues.
    */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                BooksWindow mf = new BooksWindow();

            }
        });
    }
}

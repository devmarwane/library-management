package librarysystem;

import business.*;
import dataaccess.DataAccess;
import dataaccess.DataAccessFacade;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import static librarysystem.Util.setPanelEnabled;

public class BooksWindow extends JFrame implements LibWindow{
    public static final BooksWindow INSTANCE = new BooksWindow();
    private boolean isInitialized = false;
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
    private JPanel frmBook;
    private JButton newBookButton;
    private JButton saveButton;
    private JButton btnCopiesMng;
    private JButton backButton;

    private void createUIComponents()   {
        // TODO: place custom component creation code here
    }



    @Override
    public void init() {
        if(isInitialized){
            books = controller.allBooks();
            loadBooks();
            clearForm();
            itemIndex = -1;
            setPanelEnabled(frmBook,false);
            formState = formStateEnum.Idle;
            return;
        }
        setBooksWindow();
        pack();
        /*setContentPane(mainPnl);
        setTitle("Books management");
        // Provide the frame width and height
        setSize(800, 400);
        // Make your screen center
        setLocationRelativeTo(null);
        setResizable(false);*/ // If you wish


        isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }
    public void isInitialized(boolean val) {
        isInitialized = val;
    }

    enum formStateEnum{
        Idle,
        New,
        Editing,
        Viewing
    }

    //Services
    private ControllerInterface controller = new SystemController();
    private DataAccess dataAccess = new DataAccessFacade();

    private int itemIndex = -1;
    private formStateEnum formState = formStateEnum.Idle;
    private formStateEnum authorFormState;
    private List<Book> books;
    private Book currentBook;
    private List<Author> authors = new ArrayList<>();
    private DefaultTableModel bookTableModel;
    private DefaultListModel<Author> authorListModel;  // Model for JList to display authors

    public void setBooksWindow() {
        // Terminates the Application when the frame is closed.

        setContentPane(mainPnl);
        setTitle("Books management");
        //Load books
        books = controller.allBooks();

        // Table to display the books
        bookTableModel = new DefaultTableModel(new String[]{"ISBN", "Title", "Authors","Max Checkout len.", "Available Copies"}, 0);
        tblBooks = new JTable(bookTableModel){
            @Override
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };
        tblBooks.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {

                if (event.getValueIsAdjusting() && tblBooks.getSelectedRow()>=0) {
                    if (formState == formStateEnum.Idle || formState == formStateEnum.Viewing){
                        itemIndex = tblBooks.getSelectedRow();
                        formState = formStateEnum.Viewing;
                        Book book = books.get(itemIndex);
                        try {
                            currentBook = (Book) book.clone();
                            authors = new ArrayList<>(currentBook.getAuthors());
                        } catch (CloneNotSupportedException e) {
                            throw new RuntimeException(e);
                        }
                        showBook(currentBook);
                        setPanelEnabled(frmBook,false);
                        //tblBooks.di
                    } else {
                        JOptionPane.showMessageDialog(null, "Please complete or cancel current operation first!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });
        tblBooks.setCellSelectionEnabled(false);
        tblBooks.setRowSelectionAllowed(true);
        //Load Books
        loadBooks();
        //Load authors
        loadAuthors(new ArrayList<>());
        lstAuthors.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) {
                    Integer index = lstAuthors.getSelectedIndex();
                    if (index >= 0) {
                        Author _author = authors.get(index);
                        showEditAuthorDialog(_author, index);
                    }
                }
            }
        });
        tblBooksScroll.setViewportView(tblBooks);
        tblBooksScroll.setSize(600, 400);

        setPanelEnabled(frmBook,false);

        clearFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
                itemIndex = -1;
                setPanelEnabled(frmBook,false);
                formState = formStateEnum.Idle;
            }
        });
        newAuthorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditAuthorDialog(null, -1);
            }
        });
        editBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (itemIndex==-1) {
                    JOptionPane.showMessageDialog(null, "Please choose a book from the list first!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (formState != formStateEnum.Viewing){
                    JOptionPane.showMessageDialog(null, "Please cancel or complete the current operation first!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    formState = formStateEnum.Editing;
                    setPanelEnabled(frmBook,true);
                    txtIsbn.setEnabled(false);
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //System.out.println(formState);
                if (formState == formStateEnum.Idle || formState == formStateEnum.Viewing){
                    JOptionPane.showMessageDialog(null, "Choose an operation (Add/Edit) first!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(!validateForm())
                    return;

                if (formState == formStateEnum.New){
                    int maxCheckout = rdBtn7days.isSelected()?7:21;
                    Book book = new Book(txtIsbn.getText(),txtTitle.getText(),maxCheckout,authors);
                    books.add(book);
                    loadBooks();
                    formState = formStateEnum.Viewing;
                    itemIndex = (int) books.stream().count()-1;
                    setPanelEnabled(frmBook, false);
                    dataAccess.saveNewBook(book);
                    JOptionPane.showMessageDialog(null, "Book added successfully.");
                }else if (formState == formStateEnum.Editing){
                    int maxCheckout = rdBtn7days.isSelected()?7:21;
                    Book _updatedBook = new Book(txtIsbn.getText(),txtTitle.getText(),maxCheckout,authors, currentBook.getCopies());
                    books.set(itemIndex,_updatedBook);
                    loadBooks();
                    dataAccess.updateBook(_updatedBook);
                    formState = formStateEnum.Viewing;
                    setPanelEnabled(frmBook, false);
                    JOptionPane.showMessageDialog(null, "Book updated successfully.");
                }

            }
        });
        newBookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (formState == formStateEnum.Editing || formState==formStateEnum.New){
                    JOptionPane.showMessageDialog(null, "Please cancel or complete the current operation first!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    clearForm();
                    setPanelEnabled(frmBook,true);
                    formState = formStateEnum.New;
                    itemIndex = -1;
                }
            }
        });
        btnCopiesMng.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (itemIndex==-1) {
                    JOptionPane.showMessageDialog(null, "Please choose a book from the list first!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else if (formState != formStateEnum.Viewing){
                    JOptionPane.showMessageDialog(null, "Please cancel or complete the current operation first!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    showCopyManagementDialog(books.get(itemIndex));
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });

        // Set default close operation to DO_NOTHING_ON_CLOSE so it doesn't close automatically
        INSTANCE.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        INSTANCE.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

    }

    private void closeWindow() {
        LibrarySystem.INSTANCE.updateUI();
        this.repaint();
        LibrarySystem.hideAllWindows();
        LibrarySystem.INSTANCE.setVisible(true);
    }

    // Load books from the SystemController (allBooks method) and populate the table
    private void loadBooks() {
        // Use the allBooks method from SystemController
        bookTableModel.setRowCount(0);
        for (Book book : books) {
            String authors = String.join(", ", book.getAuthors().stream().map(Author::getFullName).toArray(String[]::new));
            bookTableModel.addRow(new Object[]{book.getIsbn(), book.getTitle(), authors,book.getMaxCheckoutLength(), book.getAvailableCopies()+" / "+ book.getNumCopies()});
        }
    }

    private void loadAuthors(List<Author> authors) {
        authorListModel = new DefaultListModel<>();  // Initialize the model for the authors list
        lstAuthors.setModel(authorListModel);  // Attach the model to the JList
        this.authors = new ArrayList<>(authors);
        for (Author author : authors) {
            authorListModel.addElement(author);  // Add each author to the list model
        }
    }

    private void showBook(Book book){
        txtTitle.setText(book.getTitle());
        txtIsbn.setText(book.getIsbn());
        if (book.getMaxCheckoutLength()==7){
            rdBtn7days.setSelected(true);
        }else {
            rdBtn21days.setSelected(true);
        }
        loadAuthors(new ArrayList<>(book.getAuthors()));
    }

    private void clearForm(){
        txtTitle.setText("");
        txtIsbn.setText("");
        loadAuthors(new ArrayList<>());
        lstAuthors.clearSelection();
        tblBooks.clearSelection();
    }

    private void showEditAuthorDialog(Author author, Integer _index) {
        authorFormState = author == null ? formStateEnum.New : formStateEnum.Editing;
        // Create a new JDialog
        JDialog dialog = new JDialog(this, author != null ? "Edit Author" : "Add Author", true);  // Modal dialog
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(10, 2, 5, 25));  // 9 rows and 2 columns for labels and input fields

        // Input fields for Author details
        JLabel lblFirstName = new JLabel("First Name:");
        JTextField txtFirstName = new JTextField(author != null ? author.getFirstName() : "");

        JLabel lblLastName = new JLabel("Last Name:");
        JTextField txtLastName = new JTextField(author != null ? author.getLastName() : "");

        JLabel lblTelephone = new JLabel("Telephone:");
        JTextField txtTelephone = new JTextField(author != null ? author.getTelephone() : "");

        // Address details
        JLabel lblStreet = new JLabel("Street:");
        JTextField txtStreet = new JTextField(author != null ? author.getAddress().getStreet() : "");

        JLabel lblCity = new JLabel("City:");
        JTextField txtCity = new JTextField(author != null ? author.getAddress().getCity() : "");

        JLabel lblState = new JLabel("State:");
        JTextField txtState = new JTextField(author != null ? author.getAddress().getState() : "");

        JLabel lblZip = new JLabel("ZIP Code:");
        JTextField txtZip = new JTextField(author != null ? author.getAddress().getZip() : "");

        JLabel lblBio = new JLabel("Short Bio:");
        JTextArea txtBio = new JTextArea(author != null ? author.getBio() : "", 2, 20);  // Use a JTextArea for bio

        JLabel lblCredentials = new JLabel("Credentials:");
        JCheckBox chlCredentials = new JCheckBox("", author != null ? author.getCredentials() : false);  // Use a JCheckBox for credentials

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

        // Credentials field
        dialog.add(lblCredentials);
        dialog.add(chlCredentials);

        // Buttons
        JButton btnSubmit = new JButton("Submit");
        JButton btnCancel = new JButton("Cancel");
        dialog.add(btnSubmit);
        dialog.add(btnCancel);

        // Add functionality to the buttons
        btnSubmit.addActionListener(e -> {
            // Capture input data
            String firstName = txtFirstName.getText().trim();
            String lastName = txtLastName.getText().trim();
            String telephone = txtTelephone.getText().trim();
            String street = txtStreet.getText().trim();
            String city = txtCity.getText().trim();
            String state = txtState.getText().trim();
            String zip = txtZip.getText().trim();
            String bio = txtBio.getText().trim();

            // Accumulate validation errors in a single message
            String errorMessage = "Please fix the following(s) errors:";
            boolean isValid = true;

            if (firstName.isEmpty()) {
                errorMessage += "\n- First name cannot be empty!";
                isValid = false;
            }

            if (lastName.isEmpty()) {
                errorMessage += "\n- Last name cannot be empty!";
                isValid = false;
            }

            if (!telephone.matches("^(\\+1\\s?)?(\\(\\d{3}\\)|\\d{3})[-.\\s]?\\d{3}[-.\\s]?\\d{4}$")) {  // Assuming a valid telephone number should be 10 digits
                errorMessage += "\n- Telephone must be 10 digits!";
                isValid = false;
            }

            if (street.isEmpty()) {
                errorMessage += "\n- Street cannot be empty!";
                isValid = false;
            }

            if (city.isEmpty()) {
                errorMessage += "\n- City cannot be empty!";
                isValid = false;
            }

            if (state.isEmpty()) {
                errorMessage += "\n- State cannot be empty!";
                isValid = false;
            }

            if (!zip.matches("\\d{5}")) {  // Assuming ZIP code should be 5 digits
                errorMessage += "\n- ZIP code must be 5 digits!";
                isValid = false;
            }

            if (bio.isEmpty()) {
                errorMessage += "\n- Bio cannot be empty!";
                isValid = false;
            }

            // If validation fails, show all errors in one message
            if (!isValid) {
                JOptionPane.showMessageDialog(dialog, errorMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;  // Stop further processing
            }

            Address address = new Address(street, city, state, zip);
            if (authorFormState == formStateEnum.New) {
                Author newAuthor = new Author(firstName, lastName, telephone, address, bio, chlCredentials.isSelected());
                authors.add(newAuthor);
                authorListModel.addElement(newAuthor);  // Add the author to the list
            } else {
                Author _author = new Author(firstName, lastName, telephone, address, bio, chlCredentials.isSelected());
                authors.set(_index, _author);
                loadAuthors(authors);  // Update the list
            }

            dialog.dispose();  // Close the dialog
            lstAuthors.clearSelection();
        });

        btnCancel.addActionListener(e -> {
            dialog.dispose();
            lstAuthors.clearSelection();
        });

        // Show the dialog
        dialog.setLocationRelativeTo(this);  // Center the dialog on the main window
        dialog.setVisible(true);
    }


    private void showCopyManagementDialog(Book book) {
        JDialog dialog = new JDialog(this, "Manage Copies", true); // Modal dialog
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());

        // Table model for copies
        DefaultTableModel copyTableModel = new DefaultTableModel(new String[]{"Copy Number", "Available"}, 0);
        JTable tblCopies = new JTable(copyTableModel);

        // Load copies into table
        for (BookCopy copy : book.getCopies()) {
            copyTableModel.addRow(new Object[]{copy.getCopyNum(), copy.isAvailable() ? "Yes" : "No"});
        }

        JScrollPane scrollPane = new JScrollPane(tblCopies);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Buttons for Add, Toggle Availability, and Delete
        JPanel buttonPanel = new JPanel();
        JButton btnAdd = new JButton("Add Copy");
        JButton btnDelete = new JButton("Delete Copy");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add functionality to buttons

        // Add a new copy
        btnAdd.addActionListener(e -> {
            BookCopy newCopy = book.addCopy();
            copyTableModel.addRow(new Object[]{newCopy.getCopyNum(), "Yes"});
            dataAccess.updateBook(book);
            JOptionPane.showMessageDialog(null, "The book copy no " + newCopy.getCopyNum() + " has been added successfully to the book " + book.getTitle() + ".");
        });

        // Delete a copy if it's available
        btnDelete.addActionListener(e -> {
            int selectedRow = tblCopies.getSelectedRow();
            if (selectedRow != -1) {
                BookCopy selectedCopy = book.getCopies()[selectedRow];
                if (selectedCopy.isAvailable()) {
                    book.removeCopy(selectedCopy);
                    copyTableModel.removeRow(selectedRow);
                    dataAccess.updateBook(book);
                    JOptionPane.showMessageDialog(null, "The book copy no " + selectedCopy.getCopyNum() + " has been deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(dialog, "Only available copies can be deleted.");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a copy to delete.");
            }
        });

        // Add a WindowListener to handle actions when the dialog is closing
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                books = controller.allBooks();
                loadBooks();
            }
        });

        // Show the dialog
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    // Function to validate form inputs, including ISBN uniqueness
    private boolean validateForm() {
        String isbn = txtIsbn.getText().trim();
        String title = txtTitle.getText().trim();
        boolean isValid = true;

        String errorMessage="Please fix the following(s) errors :";
        // Validate ISBN
        if (isbn.isEmpty()) {
            errorMessage+="\n- Isbn cannot be empty!";
            isValid = false;
        } else if (!isbn.matches("^\\d{2}-\\d{5}$")) {
            errorMessage+="\n- Isbn must be in the following format 00-00000";
            isValid = false;
        } else if (isIsbnDuplicate(isbn) && formState==formStateEnum.New) { // Check ISBN uniqueness only for new books
            errorMessage+="\n- Isbn already exists! Please enter a unique ISBN.";
            isValid = false;
        }

        // Validate title
        if (title.isEmpty()) {
            errorMessage+="\n- Title cannot be empty!";
            isValid = false;
        }

        // Validate authors
        if (authors.isEmpty()) {
            errorMessage+="\n- At least one author must be added!";
            isValid = false;
        }

        // Validate max checkout duration (radio buttons)
        if (!rdBtn7days.isSelected() && !rdBtn21days.isSelected()) {
            errorMessage+="\n- Please select a checkout duration (7 or 21 days)!";
            isValid = false;
        }

        if (!isValid){
            JOptionPane.showMessageDialog(this, errorMessage, "Validation Error", JOptionPane.ERROR_MESSAGE);
        }

        return isValid;
    }

    // Function to check if the entered ISBN is already in the system
    private boolean isIsbnDuplicate(String isbn) {
        // Assuming controller.allIsbn() returns a list of all existing ISBNs
        List<String> allIsbn = controller.allBookIds();
        return allIsbn.contains(isbn);
    }


    public static void main(String[] args) {
    /*
    While it is not mandatory to use EventQueue.invokeLater,
    it is a best practice for all Swing applications to ensure
    thread safety and avoid potential concurrency issues.
    */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                BooksWindow mf = BooksWindow.INSTANCE;
                mf.init();
                mf.setVisible(true);
            }
        });
    }
}

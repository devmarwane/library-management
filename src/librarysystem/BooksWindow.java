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

    private void createUIComponents()   {
        // TODO: place custom component creation code here
    }

    @Override
    public void init() {
        if(isInitialized) return;
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
    private ControllerInterface controller= new SystemController();
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
                itemIndex = -1;
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
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(formState);
                if (formState == formStateEnum.Idle || formState == formStateEnum.Viewing){
                    JOptionPane.showMessageDialog(null, "Choose an operation (Add/Edit) first!", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (formState == formStateEnum.New){
                    //TODO: validate form
                    int maxCheckout = rdBtn7days.isSelected()?7:21;
                    Book book = new Book(txtIsbn.getText(),txtTitle.getText(),maxCheckout,authors);
                    books.add(book);
                    loadBooks();
                    formState = formStateEnum.Viewing;
                    itemIndex = (int) books.stream().count()-1;
                    setPanelEnabled(frmBook, false);
                    dataAccess.saveNewBook(book);
                }else if (formState == formStateEnum.Editing){

                    //TODO: validate form
                    int maxCheckout = rdBtn7days.isSelected()?7:21;
                    Book _updatedBook = new Book(txtIsbn.getText(),txtTitle.getText(),maxCheckout,authors);
                    books.set(itemIndex,_updatedBook);
                    loadBooks();
                    dataAccess.updateBook(_updatedBook);
                    formState = formStateEnum.Viewing;
                    setPanelEnabled(frmBook, false);
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
    }

    // Load books from the SystemController (allBooks method) and populate the table
    private void loadBooks() {
        // Use the allBooks method from SystemController
        bookTableModel.setRowCount(0);
        for (Book book : books) {
            String authors = String.join(", ", book.getAuthors().stream().map(Author::getFullName).toArray(String[]::new));
            bookTableModel.addRow(new Object[]{book.getIsbn(), book.getTitle(), authors,book.getMaxCheckoutLength(), book.getAvailableCopies()+"/"+ book.getNumCopies()});
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
        authorFormState = author==null?formStateEnum.New : formStateEnum.Editing;
        // Create a new JDialog
        JDialog dialog = new JDialog(this, author!=null?"Edit Author":"Add Author", true);  // Modal dialog
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(10, 2, 5, 25));  // 9 rows and 2 columns for labels and input fields

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

        JLabel lblCredentials = new JLabel("Credentials:");
        JCheckBox chlCredentials = new JCheckBox("",author!=null?author.getCredentials(): false);  // Use a JTextArea for bio

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

                if(authorFormState==formStateEnum.New) {
                    // Create a new Address and Author object
                    Address address = new Address(street, city, state, zip);
                    Author newAuthor = new Author(firstName, lastName, telephone, address, bio, chlCredentials.isSelected());

                    // Add the new author to the JList (assuming you are using authorListModel for the JList)
                    authors.add(newAuthor);
                    authorListModel.addElement(newAuthor);  // Add the author to the list
                } else {
                    Address address = new Address(street, city, state, zip);
                    Author _author = new Author(firstName, lastName, telephone, address, bio, chlCredentials.isSelected());
                    System.out.println("+++++++++++++++"+_index);
                    authors.set(_index, _author);
                    loadAuthors(authors);
                }


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
        });

        // Delete a copy if it is not available
        btnDelete.addActionListener(e -> {
            int selectedRow = tblCopies.getSelectedRow();
            if (selectedRow != -1) {
                BookCopy selectedCopy = book.getCopies()[selectedRow];
                if (selectedCopy.isAvailable()) {
                    book.removeCopy(selectedCopy);
                    copyTableModel.removeRow(selectedRow);
                    dataAccess.updateBook(book);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Only available copies can be deleted.");
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a copy to delete.");
            }
        });

        // Show the dialog
        dialog.setLocationRelativeTo(this);

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

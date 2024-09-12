package librarysystem;

import business.*;
import dataaccess.DataAccess;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MembersWindow extends JFrame {
    private JTextField txtMemberId;
    private JTextField txtFirstName;
    private JTextField txtLastName;
    private JTextField txtPhone;
    private JTextField txtStreet;
    private JTextField txtCity;
    private JTextField txtState;
    private JTextField txtZip;
    private JTable tblMembers;
    private JPanel mainPnl;
    private JScrollPane tblMembersScroll;
    private JButton clearFormButton;
    private JButton editMemberButton;
    private JButton deleteMemberButton;
    private JButton addMemberButton;
    private JButton saveButton;

    // Services
    private DataAccess dataAccess;
    private ControllerInterface controller;

    private DefaultTableModel memberTableModel;

    public MembersWindow() {
        // Default visibility is false. You have enabled visibility true
        setVisible(true);
        // Terminates the Application when the frame is closed.
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(mainPnl);
        setTitle("Members Management");
        // Provide the frame width and height
        setSize(800, 400);
        // Make your screen center
        setLocationRelativeTo(null);
        setResizable(false); // If you wish
        // Data
        controller = new SystemController();
        // Table to display the members
        memberTableModel = new DefaultTableModel(new String[]{"Member ID", "First Name", "Last Name", "Phone", "Address"}, 0);
        tblMembers = new JTable(memberTableModel) {
            @Override
            public boolean editCellAt(int row, int column, java.util.EventObject e) {
                return false;
            }
        };
        tblMembers.setCellSelectionEnabled(false);
        tblMembers.setRowSelectionAllowed(true);
        // Load Members
        loadMembers();

        tblMembersScroll.setViewportView(tblMembers);
        tblMembersScroll.setSize(600, 400);

        clearFormButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });

        addMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMember(null);
            }
        });

        editMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblMembers.getSelectedRow();
                if (selectedRow != -1) {
                    LibraryMember selectedMember = getSelectedMember(selectedRow);
                    populateForm(selectedMember);
                } else {
                    JOptionPane.showMessageDialog(MembersWindow.this, "Please select a member to edit.");
                }
            }
        });

        deleteMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblMembers.getSelectedRow();
                if (selectedRow != -1) {
                    memberTableModel.removeRow(selectedRow);
                } else {
                    JOptionPane.showMessageDialog(MembersWindow.this, "Please select a member to delete.");
                }
            }
        });
    }

    // Load members from the SystemController (allMembers method) and populate the table
    private void loadMembers() {
        List<LibraryMember> members = controller.allMembers(); // Use the allMembers method from SystemController
        for (LibraryMember member : members) {
            memberTableModel.addRow(new Object[]{
                    member.getMemberId(),
                    member.getFirstName(),
                    member.getLastName(),
                    member.getTelephone(),
                    member.getAddress().toString()
            });
        }
    }

    // Save or update member data based on the current form inputs
    private void saveMember(LibraryMember existingMember) {
        String memberId = txtMemberId.getText();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String phone = txtPhone.getText();
        String street = txtStreet.getText();
        String city = txtCity.getText();
        String state = txtState.getText();
        String zip = txtZip.getText();

        // Validation check
        if (!memberId.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !phone.isEmpty() &&
                !street.isEmpty() && !city.isEmpty() && !state.isEmpty() && !zip.isEmpty()) {

            Address address = new Address(street, city, state, zip);
            LibraryMember newMember = new LibraryMember(memberId, firstName, lastName, phone, address);

            if (existingMember != null) { // Edit existing member
                updateMemberInTable(newMember);
            } else { // Add new member
                memberTableModel.addRow(new Object[]{newMember.getMemberId(), newMember.getFirstName(),
                        newMember.getLastName(), newMember.getTelephone(), newMember.getAddress().toString()});
            }
            clearForm(); // Clear the form after saving
        } else {
            JOptionPane.showMessageDialog(this, "Please fill all fields!");
        }
    }

    // Populate the form fields with the selected member's data
    private void populateForm(LibraryMember member) {
        txtMemberId.setText(member.getMemberId());
        txtFirstName.setText(member.getFirstName());
        txtLastName.setText(member.getLastName());
        txtPhone.setText(member.getTelephone());
        txtStreet.setText(member.getAddress().getStreet());
        txtCity.setText(member.getAddress().getCity());
        txtState.setText(member.getAddress().getState());
        txtZip.setText(member.getAddress().getZip());
    }

    // Clear all form fields
    private void clearForm() {
        txtMemberId.setText("");
        txtFirstName.setText("");
        txtLastName.setText("");
        txtPhone.setText("");
        txtStreet.setText("");
        txtCity.setText("");
        txtState.setText("");
        txtZip.setText("");
        tblMembers.clearSelection();
    }

    private LibraryMember getSelectedMember(int selectedRow) {
        String memberId = (String) memberTableModel.getValueAt(selectedRow, 0);
        String firstName = (String) memberTableModel.getValueAt(selectedRow, 1);
        String lastName = (String) memberTableModel.getValueAt(selectedRow, 2);
        String phone = (String) memberTableModel.getValueAt(selectedRow, 3);
        String addressString = (String) memberTableModel.getValueAt(selectedRow, 4);

        // Address parsing based on your Address.toString() format
        Address address = new Address("Street", "City", "State", "Zip"); // Replace with correct parsing logic

        return new LibraryMember(memberId, firstName, lastName, phone, address);
    }

    private void updateMemberInTable(LibraryMember member) {
        for (int i = 0; i < memberTableModel.getRowCount(); i++) {
            if (memberTableModel.getValueAt(i, 0).equals(member.getMemberId())) {
                memberTableModel.setValueAt(member.getFirstName(), i, 1);
                memberTableModel.setValueAt(member.getLastName(), i, 2);
                memberTableModel.setValueAt(member.getTelephone(), i, 3);
                memberTableModel.setValueAt(member.getAddress().toString(), i, 4);
                break;
            }
        }
    }

    public static void main(String[] args) {
        /*
        While it is not mandatory to use EventQueue.invokeLater,
        it is a best practice for all Swing applications to ensure
        thread safety and avoid potential concurrency issues.
        */
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MembersWindow mf = new MembersWindow();
            }
        });
    }
}

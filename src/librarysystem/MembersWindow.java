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
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import static librarysystem.Util.setPanelEnabled;

public class MembersWindow extends JFrame implements LibWindow {
    public static final MembersWindow INSTANCE = new MembersWindow();
    private boolean isInitialized = false;
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
    private JButton backButton;
    private JPanel frmMember;
    private DefaultTableModel memberTableModel;

    private ControllerInterface controller = new SystemController();
    private DataAccess dataAccess = new DataAccessFacade();

    private enum formStateEnum {Idle, Viewing, New, Editing}

    private formStateEnum formState = formStateEnum.Idle;
    private List<LibraryMember> members = controller.allMembers();

    private int itemIndex = -1;
    private int memberIdCounter = 1;

    @Override
    public void init() {
        if (isInitialized) {
            loadMembers();
            clearForm();
            itemIndex = -1;
            setPanelEnabled(frmMember, false);
            formState = formStateEnum.Idle;
            return;
        }
        setMembersWindow();
        pack();
        isInitialized = true;
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void isInitialized(boolean val) {
        isInitialized = val;
    }

    public void setMembersWindow() {
        INSTANCE.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setContentPane(mainPnl);
        setTitle("Members Management");
        setSize(800, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        memberTableModel = new DefaultTableModel(new String[]{"Member ID", "First Name", "Last Name", "Phone", "Address"}, 0);
        tblMembers = new JTable(memberTableModel) {
            @Override
            public boolean editCellAt(int row, int column, EventObject e) {
                return false;
            }
        };
        tblMembers.setCellSelectionEnabled(false);
        tblMembers.setRowSelectionAllowed(true);
        loadMembers();

        tblMembersScroll.setViewportView(tblMembers);
        tblMembersScroll.setSize(600, 400);

        formState = formStateEnum.Viewing;
        setPanelEnabled(frmMember, false);

        tblMembers.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {

                if (event.getValueIsAdjusting() && tblMembers.getSelectedRow() >= 0) {
                    if (formState == MembersWindow.formStateEnum.Idle || formState == MembersWindow.formStateEnum.Viewing) {
                        itemIndex = tblMembers.getSelectedRow();
                        formState = MembersWindow.formStateEnum.Viewing;
                        LibraryMember currentMember = members.get(itemIndex);
                        populateForm(currentMember);
                        setPanelEnabled(frmMember, false);
                    } else {
                        JOptionPane.showMessageDialog(null, "Please complete or cancel current operation first!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }

                }
            }
        });

        INSTANCE.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeWindow();
            }
        });

        clearFormButton.addActionListener(e -> {
            clearForm();
            itemIndex = -1;
            setPanelEnabled(frmMember, false);
            formState = formStateEnum.Idle;
        });

        addMemberButton.addActionListener(e -> {
            clearForm();
            txtMemberId.setText(String.valueOf(memberIdCounter++));
            setPanelEnabled(frmMember, true);
            formState = formStateEnum.New;
            txtMemberId.setEnabled(false);
            itemIndex = -1;
        });

        editMemberButton.addActionListener(e -> {
            int selectedRow = tblMembers.getSelectedRow();
            if (selectedRow != -1) {
                formState = formStateEnum.Editing;
                itemIndex = selectedRow;
                setPanelEnabled(frmMember, true);
                txtMemberId.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(MembersWindow.this, "Please select a member to edit.");
            }
        });

        deleteMemberButton.addActionListener(e -> {
            int selectedRow = tblMembers.getSelectedRow();
            if (selectedRow != -1) {
                LibraryMember memberToDelete = getSelectedMember(selectedRow);
                memberTableModel.removeRow(selectedRow);
                dataAccess.updateMember(memberToDelete);
                JOptionPane.showMessageDialog(MembersWindow.this, "Member deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(MembersWindow.this, "Please select a member to delete.");
            }
        });

        saveButton.addActionListener(e -> {
            if (formState == formStateEnum.Idle || formState == formStateEnum.Viewing) {
                JOptionPane.showMessageDialog(null, "Choose an operation (Add/Edit) first!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!validateForm())
                return;

            if (formState == formStateEnum.New) {
                LibraryMember newMember = createMemberFromForm();
                members.add(newMember);
                itemIndex = members.size() - 1;
                dataAccess.saveNewMember(newMember);
//                memberTableModel.addRow(new Object[]{
//                        newMember.getMemberId(),
//                        newMember.getFirstName(),
//                        newMember.getLastName(),
//                        newMember.getTelephone(),
//                        newMember.getAddress().toString()
//                });
                JOptionPane.showMessageDialog(null, "Member added successfully.");
            } else if (formState == formStateEnum.Editing) {
                LibraryMember updatedMember = createMemberFromForm();
                updateMemberInTable(updatedMember);
                dataAccess.updateMember(updatedMember);
                JOptionPane.showMessageDialog(null, "Member updated successfully.");
            }

            loadMembers();
            formState = formStateEnum.Viewing;
            setPanelEnabled(frmMember, false);
        });

        backButton.addActionListener(e -> closeWindow());
    }

    private void closeWindow() {
        LibrarySystem.INSTANCE.updateUI();
        this.repaint();
        LibrarySystem.hideAllWindows();
        LibrarySystem.INSTANCE.setVisible(true);
    }

    private void loadMembers() {
        memberTableModel.setRowCount(0);
        for (LibraryMember member : members) {
            memberTableModel.addRow(new Object[]{
                    member.getMemberId(),
                    member.getFirstName(),
                    member.getLastName(),
                    member.getTelephone(),
                    member.getAddress().toString()
            });
            memberIdCounter = Math.max(memberIdCounter, Integer.parseInt(member.getMemberId()) + 1);
        }
    }

    private boolean validateForm() {
        String phonePattern = "^(\\+1\\s?)?(\\(\\d{3}\\)|\\d{3})[-.\\s]?\\d{3}[-.\\s]?\\d{4}$";
        String zipPattern = "\\d{5}";

        if (txtFirstName.getText().isEmpty() || txtLastName.getText().isEmpty() ||
                txtPhone.getText().isEmpty() || txtStreet.getText().isEmpty() ||
                txtCity.getText().isEmpty() || txtState.getText().isEmpty() ||
                txtZip.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled.");
            return false;
        }

        if (!Pattern.matches(phonePattern, txtPhone.getText())) {
            JOptionPane.showMessageDialog(this, "Invalid phone number. Please enter a valid 10-digit US phone number.");
            return false;
        }

        if (!Pattern.matches(zipPattern, txtZip.getText())) {
            JOptionPane.showMessageDialog(this, "ZIP code must be a 5-digit number.");
            return false;
        }

        return true;
    }

    private LibraryMember createMemberFromForm() {
        String memberId = txtMemberId.getText();
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String phone = txtPhone.getText();
        Address address = new Address(txtStreet.getText(), txtCity.getText(), txtState.getText(), txtZip.getText());
        return new LibraryMember(memberId, firstName, lastName, phone, address);
    }

    private void updateMemberInTable(LibraryMember member) {
        memberTableModel.setValueAt(member.getFirstName(), itemIndex, 1);
        memberTableModel.setValueAt(member.getLastName(), itemIndex, 2);
        memberTableModel.setValueAt(member.getTelephone(), itemIndex, 3);
        memberTableModel.setValueAt(member.getAddress().toString(), itemIndex, 4);
    }

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
        HashMap<String, LibraryMember> mems = dataAccess.readMemberMap();
        String memberId = (String) memberTableModel.getValueAt(selectedRow, 0);

        return mems.get(memberId);
    }


    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                MembersWindow mf = MembersWindow.INSTANCE;
                mf.init();
                mf.setVisible(true);
            }
        });
    }
}

package app;

import model.LibraryResource;
import model.Loan;
import model.Reservation;
import model.Student;
import service.LibraryService;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/** A small Swing interface for the library service. */
public class LibraryWindow extends JFrame {
    private static final Color NAVY = new Color(34, 55, 82);
    private static final Color BLUE = new Color(53, 105, 158);
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color BORDER = new Color(220, 226, 233);

    private final LibraryService library;

    private final JTextField searchField = new JTextField(22);
    private final JComboBox<String> searchType = new JComboBox<>(
            new String[]{"All", "Book", "E-Book", "Journal", "Project Resource"});
    private final JTable resourcesTable = createTable(new String[]{
            "ID", "Type", "Title", "Author / Creator", "Available", "Total"});

    private final JComboBox<String> studentChoice = new JComboBox<>();
    private final JComboBox<String> resourceChoice = new JComboBox<>();
    private final JTable loansTable = createTable(new String[]{
            "Loan ID", "Resource ID", "Title", "Issue Date", "Due Date", "Status"});
    private final JTable reservationsTable = createTable(new String[]{
            "Reservation ID", "Resource ID", "Title", "Status"});
    private final JLabel studentMessage = new JLabel("Choose a student and a resource to begin.");

    private final JComboBox<String> newResourceType = new JComboBox<>(
            new String[]{"Book", "E-Book", "Journal", "Project Resource"});
    private final JTextField newResourceId = new JTextField();
    private final JTextField newResourceTitle = new JTextField();
    private final JTextField newResourceCreator = new JTextField();
    private final JTextField newResourceCopies = new JTextField("1");
    private final JComboBox<String> inventoryResourceChoice = new JComboBox<>();
    private final JTextField extraCopiesField = new JTextField("1", 4);

    private final JTextField newStudentId = new JTextField();
    private final JTextField newStudentName = new JTextField();
    private final JComboBox<String> removeStudentChoice = new JComboBox<>();
    private final JTable membersTable = createTable(new String[]{"Student ID", "Name"});

    public LibraryWindow(LibraryService library) {
        this.library = library;
        setTitle("Smart Library & Resource Sharing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setSize(1120, 740);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(0, 0));

        add(createHeader(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);

        refreshAllViews();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(BorderFactory.createEmptyBorder(18, 24, 18, 24));

        JLabel title = new JLabel("Smart Library & Resource Sharing System");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 23));

        JLabel subtitle = new JLabel("Manage books, e-books, journals, project resources and student borrowing");
        subtitle.setForeground(new Color(220, 230, 240));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(title);
        text.add(Box.createVerticalStrut(5));
        text.add(subtitle);
        header.add(text, BorderLayout.CENTER);
        return header;
    }

    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.addTab("Resources", createResourcesTab());
        tabs.addTab("Student Services", createStudentTab());
        tabs.addTab("Librarian", createLibrarianTab());
        return tabs;
    }

    private JPanel createResourcesTab() {
        JPanel panel = basePanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel searchPanel = new JPanel(new BorderLayout(8, 8));
        searchPanel.setOpaque(false);
        JPanel searchControls = new JPanel();
        searchControls.setOpaque(false);
        searchControls.add(new JLabel("Search"));
        searchControls.add(searchField);
        searchControls.add(new JLabel("Type"));
        searchControls.add(searchType);

        JButton searchButton = button("Search");
        JButton showAllButton = button("Show All");
        searchControls.add(searchButton);
        searchControls.add(showAllButton);
        searchPanel.add(searchControls, BorderLayout.CENTER);

        JLabel hint = new JLabel("Search by resource ID, title, author, publisher or creator.");
        hint.setForeground(new Color(95, 105, 118));
        hint.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
        searchPanel.add(hint, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> applyResourceSearch());
        searchField.addActionListener(e -> applyResourceSearch());
        showAllButton.addActionListener(e -> {
            searchField.setText("");
            searchType.setSelectedItem("All");
            applyResourceSearch();
        });

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(tablePanel("Library inventory", resourcesTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStudentTab() {
        JPanel panel = basePanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

        JPanel selectionRow = new JPanel();
        selectionRow.setOpaque(false);
        selectionRow.add(new JLabel("Student"));
        studentChoice.setPreferredSize(new Dimension(220, 28));
        selectionRow.add(studentChoice);
        selectionRow.add(Box.createHorizontalStrut(14));
        selectionRow.add(new JLabel("Resource"));
        resourceChoice.setPreferredSize(new Dimension(300, 28));
        selectionRow.add(resourceChoice);
        top.add(selectionRow);

        // JPanel actions = new JPanel();
        // actions.setOpaque(false);
        // JButton issueButton = button("Issue");
        // JButton returnButton = button("Return");
        // JButton renewButton = button("Renew");
        // JButton reserveButton = button("Reserve");
        // actions.add(issueButton);
        // actions.add(returnButton);
        // actions.add(renewButton);
        // actions.add(reserveButton);
        // top.add(Box.createVerticalStrut(8));
        // top.add(actions);

        JPanel actions = new JPanel();
actions.setOpaque(false);

JButton issueButton = button("Issue");
JButton returnButton = button("Return");
JButton renewButton = button("Renew");
JButton reserveButton = button("Reserve");

// Set button text color to black
issueButton.setForeground(Color.BLACK);
returnButton.setForeground(Color.BLACK);
renewButton.setForeground(Color.BLACK);
reserveButton.setForeground(Color.BLACK);

actions.add(issueButton);
actions.add(returnButton);
actions.add(renewButton);
actions.add(reserveButton);

top.add(Box.createVerticalStrut(8));
top.add(actions);

        studentMessage.setForeground(NAVY);
        studentMessage.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 4));
        top.add(studentMessage);

        issueButton.addActionListener(e -> performStudentAction("issue"));
        returnButton.addActionListener(e -> performStudentAction("return"));
        renewButton.addActionListener(e -> performStudentAction("renew"));
        reserveButton.addActionListener(e -> performStudentAction("reserve"));
        studentChoice.addActionListener(e -> refreshStudentActivity());

        JPanel activity = new JPanel(new GridLayout(1, 2, 12, 0));
        activity.setOpaque(false);
        activity.add(tablePanel("Loan history", loansTable));
        activity.add(tablePanel("Active reservations", reservationsTable));

        panel.add(top, BorderLayout.NORTH);
        panel.add(activity, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLibrarianTab() {
        JPanel panel = basePanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 18, 18, 18));

        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setOpaque(false);
        inventoryPanel.setLayout(new BoxLayout(inventoryPanel, BoxLayout.Y_AXIS));
        inventoryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        inventoryPanel.add(sectionTitle("Manage resources"));
        inventoryPanel.add(Box.createVerticalStrut(8));

        JPanel resourceFields = new JPanel(new GridLayout(2, 5, 8, 5));
        resourceFields.setOpaque(false);
        resourceFields.add(new JLabel("Type"));
        resourceFields.add(new JLabel("Resource ID"));
        resourceFields.add(new JLabel("Title"));
        resourceFields.add(new JLabel("Author / Creator"));
        resourceFields.add(new JLabel("Copies"));
        resourceFields.add(newResourceType);
        resourceFields.add(newResourceId);
        resourceFields.add(newResourceTitle);
        resourceFields.add(newResourceCreator);
        resourceFields.add(newResourceCopies);
        inventoryPanel.add(resourceFields);

        JPanel addResourceRow = new JPanel();
        addResourceRow.setOpaque(false);
        JButton addResourceButton = button("Add Resource");
        addResourceRow.add(addResourceButton);
        inventoryPanel.add(Box.createVerticalStrut(8));
        inventoryPanel.add(addResourceRow);

        JPanel inventoryActions = new JPanel();
        inventoryActions.setOpaque(false);
        inventoryActions.add(new JLabel("Selected resource"));
        inventoryResourceChoice.setPreferredSize(new Dimension(300, 28));
        inventoryActions.add(inventoryResourceChoice);
        inventoryActions.add(new JLabel("Extra copies"));
        inventoryActions.add(extraCopiesField);
        JButton addCopiesButton = button("Add Copies");
        JButton removeResourceButton = button("Remove Resource");
        inventoryActions.add(addCopiesButton);
        inventoryActions.add(removeResourceButton);
        inventoryPanel.add(Box.createVerticalStrut(8));
        inventoryPanel.add(inventoryActions);

        addResourceButton.addActionListener(e -> addResourceFromForm());
        addCopiesButton.addActionListener(e -> addCopiesFromForm());
        removeResourceButton.addActionListener(e -> removeResourceFromForm());

        JPanel memberPanel = new JPanel();
        memberPanel.setOpaque(false);
        memberPanel.setLayout(new BoxLayout(memberPanel, BoxLayout.Y_AXIS));
        memberPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(12, 12, 12, 12)));
        memberPanel.add(sectionTitle("Manage student members"));
        memberPanel.add(Box.createVerticalStrut(8));

        JPanel memberFields = new JPanel(new GridLayout(2, 2, 8, 5));
        memberFields.setOpaque(false);
        memberFields.add(new JLabel("Student ID"));
        memberFields.add(new JLabel("Student name"));
        memberFields.add(newStudentId);
        memberFields.add(newStudentName);
        memberPanel.add(memberFields);

        JPanel memberActions = new JPanel();
        memberActions.setOpaque(false);
        JButton addStudentButton = button("Add Student");
        memberActions.add(addStudentButton);
        memberActions.add(Box.createHorizontalStrut(18));
        memberActions.add(new JLabel("Remove member"));
        removeStudentChoice.setPreferredSize(new Dimension(220, 28));
        memberActions.add(removeStudentChoice);
        JButton removeStudentButton = button("Remove Student");
        memberActions.add(removeStudentButton);
        memberPanel.add(Box.createVerticalStrut(8));
        memberPanel.add(memberActions);

        addStudentButton.addActionListener(e -> addStudentFromForm());
        removeStudentButton.addActionListener(e -> removeStudentFromForm());

        JPanel lower = new JPanel(new BorderLayout(0, 8));
        lower.setOpaque(false);
        lower.add(memberPanel, BorderLayout.NORTH);
        lower.add(tablePanel("Registered students", membersTable), BorderLayout.CENTER);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, inventoryPanel, lower);
        mainSplit.setResizeWeight(0.52);
        mainSplit.setBorder(null);
        mainSplit.setOpaque(false);
        panel.add(mainSplit, BorderLayout.CENTER);
        return panel;
    }

    private void performStudentAction(String action) {
        String studentId = selectedId(studentChoice);
        String resourceId = selectedId(resourceChoice);
        if (studentId == null || resourceId == null) {
            showError("Please select both a student and a resource.");
            return;
        }

        try {
            String message;
            if ("issue".equals(action)) {
                Loan loan = library.issueResource(studentId, resourceId);
                message = "Resource issued. Due date: " + loan.getDueDate();
            } else if ("return".equals(action)) {
                library.returnResource(studentId, resourceId);
                message = "Resource returned successfully.";
            } else if ("renew".equals(action)) {
                Loan loan = library.renewResource(studentId, resourceId);
                message = "Renewal successful. New due date: " + loan.getDueDate();
            } else {
                Reservation reservation = library.reserveResource(studentId, resourceId);
                message = "Reservation created: " + reservation.getId() + " (" + reservation.getStatus() + ").";
            }
            studentMessage.setText(message);
            refreshAllViews();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            studentMessage.setText("Unable to complete the operation: " + ex.getMessage());
            showError(ex.getMessage());
        }
    }

    private void addResourceFromForm() {
        try {
            int copies = parsePositiveNumber(newResourceCopies.getText(), "Copies");
            library.addResource((String) newResourceType.getSelectedItem(), newResourceId.getText().trim(),
                    newResourceTitle.getText().trim(), newResourceCreator.getText().trim(), copies);
            newResourceId.setText("");
            newResourceTitle.setText("");
            newResourceCreator.setText("");
            newResourceCopies.setText("1");
            refreshAllViews();
            showInfo("Resource added successfully.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void addCopiesFromForm() {
        String resourceId = selectedId(inventoryResourceChoice);
        if (resourceId == null) {
            showError("Please select a resource first.");
            return;
        }
        try {
            int copies = parsePositiveNumber(extraCopiesField.getText(), "Extra copies");
            library.addCopies(resourceId, copies);
            refreshAllViews();
            showInfo("Copies added successfully.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void removeResourceFromForm() {
        String resourceId = selectedId(inventoryResourceChoice);
        if (resourceId == null) {
            showError("Please select a resource first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Remove resource " + resourceId + " from the inventory?", "Confirm removal",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            library.removeResource(resourceId);
            refreshAllViews();
            showInfo("Resource removed.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void addStudentFromForm() {
        try {
            library.addStudent(new Student(newStudentId.getText().trim(), newStudentName.getText().trim()));
            newStudentId.setText("");
            newStudentName.setText("");
            refreshAllViews();
            showInfo("Student added successfully.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void removeStudentFromForm() {
        String studentId = selectedId(removeStudentChoice);
        if (studentId == null) {
            showError("Please select a student first.");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Remove student " + studentId + "?", "Confirm removal",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (answer != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            library.removeStudent(studentId);
            refreshAllViews();
            showInfo("Student removed.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            showError(ex.getMessage());
        }
    }

    private void applyResourceSearch() {
        String type = (String) searchType.getSelectedItem();
        List<LibraryResource> matches = library.searchResources(searchField.getText(), type);
        fillResourceTable(matches);
    }

    private void refreshAllViews() {
        applyResourceSearch();
        refreshCombo(studentChoice, studentOptions());
        refreshCombo(resourceChoice, resourceOptions());
        refreshCombo(inventoryResourceChoice, resourceOptions());
        refreshCombo(removeStudentChoice, studentOptions());
        fillMembersTable();
        refreshStudentActivity();
    }

    private void refreshStudentActivity() {
        String studentId = selectedId(studentChoice);
        DefaultTableModel loansModel = (DefaultTableModel) loansTable.getModel();
        loansModel.setRowCount(0);
        DefaultTableModel reservationsModel = (DefaultTableModel) reservationsTable.getModel();
        reservationsModel.setRowCount(0);

        if (studentId == null) {
            return;
        }
        for (Loan loan : library.getLoansForStudent(studentId)) {
            loansModel.addRow(new Object[]{loan.getId(), loan.getResourceId(), loan.getResourceTitle(),
                    loan.getIssueDate(), loan.getDueDate(), loan.isReturned() ? "Returned" : "Active"});
        }
        for (Reservation reservation : library.getReservationsForStudent(studentId)) {
            String title = library.getResource(reservation.getResourceId()).getTitle();
            reservationsModel.addRow(new Object[]{reservation.getId(), reservation.getResourceId(),
                    title, reservation.getStatus()});
        }
    }

    private void fillResourceTable(List<LibraryResource> resources) {
        DefaultTableModel model = (DefaultTableModel) resourcesTable.getModel();
        model.setRowCount(0);
        for (LibraryResource resource : resources) {
            model.addRow(new Object[]{resource.getId(), resource.getType(), resource.getTitle(),
                    resource.getCreator(), resource.getAvailableCopies(), resource.getTotalCopies()});
        }
    }

    private void fillMembersTable() {
        DefaultTableModel model = (DefaultTableModel) membersTable.getModel();
        model.setRowCount(0);
        for (Student student : library.getAllStudents()) {
            model.addRow(new Object[]{student.getId(), student.getName()});
        }
    }

    private String[] resourceOptions() {
        List<LibraryResource> resources = library.getAllResources();
        String[] options = new String[resources.size()];
        for (int i = 0; i < resources.size(); i++) {
            LibraryResource resource = resources.get(i);
            options[i] = resource.getId() + " | " + resource.getTitle();
        }
        return options;
    }

    private String[] studentOptions() {
        List<Student> students = library.getAllStudents();
        String[] options = new String[students.size()];
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            options[i] = student.getId() + " | " + student.getName();
        }
        return options;
    }

    private void refreshCombo(JComboBox<String> combo, String[] options) {
        String previousId = selectedId(combo);
        combo.removeAllItems();
        for (String option : options) {
            combo.addItem(option);
        }
        if (previousId != null) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (previousId.equals(selectedIdFromText(combo.getItemAt(i)))) {
                    combo.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private String selectedId(JComboBox<String> combo) {
        Object item = combo.getSelectedItem();
        return item == null ? null : selectedIdFromText(item.toString());
    }

    private String selectedIdFromText(String text) {
        int separator = text.indexOf(" | ");
        return separator < 0 ? text : text.substring(0, separator).trim();
    }

    private int parsePositiveNumber(String value, String fieldName) {
        try {
            int number = Integer.parseInt(value.trim());
            if (number < 1) {
                throw new NumberFormatException();
            }
            return number;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a whole number greater than zero.");
        }
    }

    private JPanel basePanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(BACKGROUND);
        return panel;
    }

    private JPanel tablePanel(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        panel.add(sectionTitle(title), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 15));
        label.setForeground(NAVY);
        return label;
    }

    private JButton button(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(BLUE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        return button;
    }

    private static JTable createTable(String[] columns) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(235, 240, 246));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setGridColor(new Color(232, 236, 241));
        return table;
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Smart Library", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Please check", JOptionPane.ERROR_MESSAGE);
    }
}

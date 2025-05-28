package fr.rapizz.view.panels;

import fr.rapizz.util.AlternatingRowRenderer;
import fr.rapizz.view.theme.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public abstract class AbstractManagementPanel<T> extends JPanel {
    // UI Components
    protected JTable entityTable;
    protected DefaultTableModel tableModel;
    protected JTextField searchField;
    protected JButton addButton;
    protected JButton updateButton;
    protected JButton deleteButton;
    protected JButton clearButton;

    // State
    protected T selectedEntity;

    /**
     * Creates a new management panel.
     */
    protected AbstractManagementPanel() {
        setupPanel();
        createComponents();
        layoutComponents();
        addEventListeners();
    }

    /**
     * Sets up the panel's basic properties.
     */
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(AppTheme.BACKGROUND);
    }

    /**
     * Creates all UI components.
     */
    private void createComponents() {
        createTable();
        createFormFields();
        createButtons();
    }

    /**
     * Creates the table and its model.
     */
    private void createTable() {
        tableModel = createTableModel();
        entityTable = new JTable(tableModel);
        entityTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        entityTable.getTableHeader().setReorderingAllowed(false);
        entityTable.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
    }

    /**
     * Creates the form fields.
     */
    protected abstract void createFormFields();

    /**
     * Creates the buttons.
     */
    private void createButtons() {
        addButton = new JButton("Ajouter");
        updateButton = new JButton("Modifier");
        deleteButton = new JButton("Supprimer");
        clearButton = new JButton("Effacer");

        // Initially disable update and delete
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    /**
     * Lays out all components.
     */
    private void layoutComponents() {
        JPanel mainPanel = createMainPanel();
        JPanel titlePanel = createTitlePanel();
        JPanel tableCard = createTablePanel();
        JPanel formCard = createFormPanel();

        // Assemble components
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(tableCard);
        contentPanel.add(formCard);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    /**
     * Creates the main panel.
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(AppTheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return mainPanel;
    }

    /**
     * Creates the title panel with search field.
     */
    protected JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        titlePanel.setBackground(AppTheme.BACKGROUND);

        JLabel titleLabel = new JLabel(getPanelTitle());
        titleLabel.setFont(AppTheme.TITLE);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titlePanel.add(titleLabel, BorderLayout.WEST);

        // Add search bar
        searchField = new JTextField(15);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.setBackground(AppTheme.BACKGROUND);
        searchPanel.add(searchField);
        titlePanel.add(searchPanel, BorderLayout.EAST);

        return titlePanel;
    }

    /**
     * Creates the table panel.
     */
    private JPanel createTablePanel() {
        JPanel tableCard = createCard();
        tableCard.setLayout(new BorderLayout());

        styleTable();

        JScrollPane scrollPane = new JScrollPane(entityTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
    }

    /**
     * Applies styling to the table.
     */
    private void styleTable() {
        entityTable.setRowHeight(35);
        entityTable.setShowGrid(false);
        entityTable.setIntercellSpacing(new Dimension(0, 0));
        entityTable.setFillsViewportHeight(true);

        JTableHeader header = entityTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 40));
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(240, 240, 240));
        header.setForeground(AppTheme.TEXT_PRIMARY);
    }

    /**
     * Creates the form panel.
     */
    protected JPanel createFormPanel() {
        JPanel formCard = createCard();
        formCard.setLayout(new BorderLayout(0, 10));

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel(getFormTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titlePanel.add(titleLabel);

        // Form
        JPanel formContentPanel = new JPanel(new GridBagLayout());
        formContentPanel.setOpaque(false);

        formCard.add(titlePanel, BorderLayout.NORTH);
        formCard.add(formContentPanel, BorderLayout.CENTER);

        addFormFields(formContentPanel);
        addButtonsPanel(formContentPanel);

        return formCard;
    }

    /**
     * Adds form fields to the form panel.
     */
    protected abstract void addFormFields(JPanel formCard);

    /**
     * Adds the buttons panel to the form panel.
     */
    protected void addButtonsPanel(JPanel formCard) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = getButtonsPanelGridY();
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(25, 15, 10, 15);

        JPanel buttonsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.add(addButton);
        buttonsPanel.add(updateButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(clearButton);
        formCard.add(buttonsPanel, gbc);

        styleButtons();
    }

    /**
     * Creates a styled panel.
     */
    protected JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return card;
    }

    /**
     * Styles the buttons.
     */
    private void styleButtons() {
        AppTheme.styleButton(addButton, "Ajouter", AppTheme.SUCCESS_COLOR);
        AppTheme.styleButton(updateButton, "Modifier", AppTheme.INFO_COLOR);
        AppTheme.styleButton(deleteButton, "Supprimer", AppTheme.ERROR_COLOR);
        AppTheme.styleButton(clearButton, "Effacer", AppTheme.NEUTRAL_COLOR);
    }

    /**
     * Adds event listeners to components.
     */
    private void addEventListeners() {
        // Table selection listener
        entityTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = entityTable.getSelectedRow();
                if (selectedRow >= 0) {
                    Object entityId = entityTable.getValueAt(selectedRow, 0);
                    onRowSelected(entityId);
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> handleAddEntity());
        updateButton.addActionListener(e -> handleUpdateEntity());
        deleteButton.addActionListener(e -> handleDeleteEntity());
        clearButton.addActionListener(e -> clearForm());

        // Search field listener
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterEntities(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterEntities(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterEntities(); }
        });
    }

    /**
     * Called when a row is selected in the table.
     * @param entityId The ID of the selected entity
     */
    protected abstract void onRowSelected(Object entityId);

    /**
     * Handles adding a new entity.
     */
    protected abstract void handleAddEntity();

    /**
     * Handles updating an existing entity.
     */
    protected abstract void handleUpdateEntity();

    /**
     * Handles deleting an entity.
     */
    protected abstract void handleDeleteEntity();

    /**
     * Filters entities based on the search field text.
     */
    protected abstract void filterEntities();

    /**
     * Loads entities from the controller into the table.
     */
    protected abstract void loadEntities();

    /**
     * Populates form fields with entity data.
     * @param entity The entity to display in the form
     */
    protected abstract void populateFormFields(T entity);

    /**
     * Clears the form and selection.
     */
    protected void clearForm() {
        selectedEntity = null;
        clearFormFields();

        // Disable buttons
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Clear table selection
        entityTable.clearSelection();
    }

    /**
     * Clears all form fields.
     */
    protected abstract void clearFormFields();

    /**
     * Helper method to add a labeled form field.
     */
    protected void addFormField(JPanel panel, String labelText, JTextField field, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = 0.0;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(label, gbc);

        gbc.gridx = x + 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    /**
     * Shows a success message dialog.
     */
    protected void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Shows an error message dialog.
     */
    protected void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a validation error dialog.
     */
    protected void showValidationError(List<String> errors) {
        JOptionPane.showMessageDialog(this,
                String.join("\n", errors),
                "Erreur de Validation",
                JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Shows a confirmation dialog.
     * @return true if confirmed, false otherwise
     */
    protected boolean showConfirmDialog(String message, String title) {
        int result = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);
        return result == JOptionPane.YES_OPTION;
    }

    // Abstract methods that must be implemented by subclasses

    /**
     * Creates the table model for the entities list.
     */
    protected abstract DefaultTableModel createTableModel();

    /**
     * Gets the grid Y position for the buttons panel.
     */
    protected abstract int getButtonsPanelGridY();

    /**
     * Gets the title for the panel.
     */
    protected abstract String getPanelTitle();


    /**
     * Gets the title for the form section.
     */
    protected abstract String getFormTitle();
}

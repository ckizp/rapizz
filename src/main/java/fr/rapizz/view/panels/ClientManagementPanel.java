package fr.rapizz.view.panels;

import fr.rapizz.controller.ClientController;
import fr.rapizz.model.Client;
import fr.rapizz.util.Result;
import fr.rapizz.view.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Panel for managing clients
 */
public class ClientManagementPanel extends AbstractManagementPanel<Client> {
    // Controller reference
    private final ClientController controller;

    // Form fields specific to Client
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField amountField;
    private JTextField loyaltyField;

    /**
     * Creates a new client management panel
     * @param controller The client controller
     */
    public ClientManagementPanel(ClientController controller) {
        super(); // Initialize the abstract panel
        this.controller = controller;
        loadEntities();
    }

    @Override
    protected void createFormFields() {
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        addressField = new JTextField(20);
        phoneField = new JTextField(20);
        amountField = new JTextField(20);
        loyaltyField = new JTextField(20);

        // Make loyalty counter and amount non-editable for new clients
        amountField.setEditable(false);
        loyaltyField.setEditable(false);

        AppTheme.styleTextField(firstNameField, "Prénom du client");
        AppTheme.styleTextField(lastNameField, "Nom du client");
        AppTheme.styleTextField(addressField, "Adresse du client");
        AppTheme.styleTextField(phoneField, "Numéro de téléphone");
        AppTheme.styleTextField(amountField, "Solde du client");
        AppTheme.styleTextField(loyaltyField, "Points de fidélité");
    }

    @Override
    protected void addFormFields(JPanel formCard) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;

        addFormField(formCard, "Prénom:", firstNameField, gbc, 0, 0);
        addFormField(formCard, "Nom:", lastNameField, gbc, 0, 1);
        addFormField(formCard, "Adresse:", addressField, gbc, 0, 2);
        addFormField(formCard, "Téléphone:", phoneField, gbc, 0, 3);
        addFormField(formCard, "Solde:", amountField, gbc, 0, 4);
        addFormField(formCard, "Points Fidélité:", loyaltyField, gbc, 0, 5);
    }

    @Override
    protected DefaultTableModel createTableModel() {
        String[] columns = {"ID", "Prénom", "Nom", "Adresse", "Téléphone", "Solde", "Points Fidélité"};
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    @Override
    protected void onRowSelected(Object entityId) {
        controller.getClientById(Integer.parseInt(entityId.toString()))
                .ifPresent(this::populateFormFields);
    }

    @Override
    protected void handleAddEntity() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();

        Result<Client> result = controller.createClient(firstName, lastName, address, phone);

        if (result.isSuccess()) {
            clearForm();
            loadEntities();
            showSuccessMessage("Client ajouté avec succès!");
        } else {
            showValidationError(result.getErrors());
        }
    }

    @Override
    protected void handleUpdateEntity() {
        if (selectedEntity != null) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();

            BigDecimal amount;
            Integer loyaltyPoints;

            try {
                amount = new BigDecimal(amountField.getText().trim());
            } catch (NumberFormatException e) {
                showErrorMessage("Le solde doit être un nombre valide");
                return;
            }

            try {
                loyaltyPoints = Integer.parseInt(loyaltyField.getText().trim());
            } catch (NumberFormatException e) {
                showErrorMessage("Les points de fidélité doivent être un nombre entier");
                return;
            }

            Result<Client> result = controller.updateClient(
                    selectedEntity.getClientId(), firstName, lastName,
                    address, phone, amount, loyaltyPoints);

            if (result.isSuccess()) {
                clearForm();
                loadEntities();
                showSuccessMessage("Client mis à jour avec succès!");
            } else {
                showValidationError(result.getErrors());
            }
        }
    }

    @Override
    protected void handleDeleteEntity() {
        if (selectedEntity != null) {
            if (showConfirmDialog("Êtes-vous sûr de vouloir supprimer ce client ?", "Confirmation de suppression")) {
                Result<Void> result = controller.deleteClient(selectedEntity.getClientId());

                if (result.isSuccess()) {
                    clearForm();
                    loadEntities();
                    showSuccessMessage("Client supprimé avec succès!");
                } else {
                    showErrorMessage(String.join("\n", result.getErrors()));
                }
            }
        }
    }

    @Override
    protected void filterEntities() {
        String searchText = searchField.getText().toLowerCase();
        List<Client> filteredClients = controller.searchClients(searchText);

        tableModel.setRowCount(0);
        filteredClients.forEach(client -> tableModel.addRow(createTableRow(client)));
    }

    @Override
    protected void loadEntities() {
        tableModel.setRowCount(0);
        List<Client> clients = controller.getAllClients();
        clients.forEach(client -> tableModel.addRow(createTableRow(client)));
    }

    /**
     * Creates a table row for a client
     */
    private Object[] createTableRow(Client client) {
        return new Object[] {
                client.getClientId(),
                client.getFirstName(),
                client.getLastName(),
                client.getClientAddress(),
                client.getPhoneNumber(),
                client.getAmount(),
                client.getLoyaltyCounter()
        };
    }

    @Override
    protected void populateFormFields(Client client) {
        selectedEntity = client;

        firstNameField.setText(client.getFirstName());
        lastNameField.setText(client.getLastName());
        addressField.setText(client.getClientAddress());
        phoneField.setText(client.getPhoneNumber());
        amountField.setText(client.getAmount().toString());
        loyaltyField.setText(client.getLoyaltyCounter().toString());

        // Enable update and delete buttons
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);

        // For existing clients, we can edit amount and loyalty
        amountField.setEditable(true);
        loyaltyField.setEditable(true);
    }

    @Override
    protected void clearFormFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        addressField.setText("");
        phoneField.setText("");
        amountField.setText("0.00");
        loyaltyField.setText("0");

        // Make loyalty counter and amount non-editable for new clients
        amountField.setEditable(false);
        loyaltyField.setEditable(false);
    }

    @Override
    protected int getButtonsPanelGridY() {
        return 6;
    }

    @Override
    protected String getPanelTitle() {
        return "Gestion des Clients";
    }

    @Override
    protected String getFormTitle() {
        return "Informations Client";
    }
}

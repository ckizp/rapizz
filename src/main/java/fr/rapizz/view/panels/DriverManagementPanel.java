package fr.rapizz.view.panels;

import fr.rapizz.controller.DriverController;
import fr.rapizz.model.DeliveryDriver;
import fr.rapizz.util.Result;
import fr.rapizz.view.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing delivery drivers
 */
public class DriverManagementPanel extends AbstractManagementPanel<DeliveryDriver> {
    // Controller reference
    private final DriverController controller;

    // Form fields specific to DeliveryDriver
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField phoneField;

    /**
     * Creates a new driver management panel
     * @param controller The driver controller
     */
    public DriverManagementPanel(DriverController controller) {
        super(); // Initialize the abstract panel
        this.controller = controller;
        loadEntities();
    }

    @Override
    protected void createFormFields() {
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        phoneField = new JTextField(15);

        AppTheme.styleTextField(firstNameField, "Prénom du livreur");
        AppTheme.styleTextField(lastNameField, "Nom du livreur");
        AppTheme.styleTextField(phoneField, "Numéro de téléphone");
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
        addFormField(formCard, "Téléphone:", phoneField, gbc, 0, 2);
    }

    @Override
    protected DefaultTableModel createTableModel() {
        String[] columns = {"ID", "Prénom", "Nom", "Téléphone"};
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    @Override
    protected void onRowSelected(Object entityId) {
        controller.getDriverById(Integer.parseInt(entityId.toString()))
                .ifPresent(this::populateFormFields);
    }

    @Override
    protected void handleAddEntity() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneField.getText().trim();

        Result<DeliveryDriver> result = controller.createDriver(firstName, lastName, phone);

        if (result.isSuccess()) {
            clearForm();
            loadEntities();
            showSuccessMessage("Livreur ajouté avec succès!");
        } else {
            showValidationError(result.getErrors());
        }
    }

    @Override
    protected void handleUpdateEntity() {
        if (selectedEntity != null) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String phone = phoneField.getText().trim();

            Result<DeliveryDriver> result = controller.updateDriver(
                    selectedEntity.getDriverId(), firstName, lastName, phone);

            if (result.isSuccess()) {
                clearForm();
                loadEntities();
                showSuccessMessage("Livreur mis à jour avec succès!");
            } else {
                showValidationError(result.getErrors());
            }
        }
    }

    @Override
    protected void handleDeleteEntity() {
        if (selectedEntity != null) {
            if (showConfirmDialog("Êtes-vous sûr de vouloir supprimer ce livreur ?", "Confirmation de suppression")) {
                Result<Void> result = controller.deleteDriver(selectedEntity.getDriverId());

                if (result.isSuccess()) {
                    clearForm();
                    loadEntities();
                    showSuccessMessage("Livreur supprimé avec succès!");
                } else {
                    showErrorMessage(String.join("\n", result.getErrors()));
                }
            }
        }
    }

    @Override
    protected void filterEntities() {
        String searchText = searchField.getText().toLowerCase();
        List<DeliveryDriver> filteredDrivers = controller.searchDrivers(searchText);

        tableModel.setRowCount(0);
        filteredDrivers.forEach(driver -> tableModel.addRow(createTableRow(driver)));
    }

    @Override
    protected void loadEntities() {
        tableModel.setRowCount(0);
        List<DeliveryDriver> drivers = controller.getAllDrivers();
        drivers.forEach(driver -> tableModel.addRow(createTableRow(driver)));
    }

    /**
     * Creates a table row for a driver
     */
    private Object[] createTableRow(DeliveryDriver driver) {
        return new Object[] {
                driver.getDriverId(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getPhoneNumber()
        };
    }

    @Override
    protected void populateFormFields(DeliveryDriver driver) {
        selectedEntity = driver;

        firstNameField.setText(driver.getFirstName());
        lastNameField.setText(driver.getLastName());
        phoneField.setText(driver.getPhoneNumber());

        // Enable update and delete buttons
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

    @Override
    protected void clearFormFields() {
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
    }

    @Override
    protected int getButtonsPanelGridY() {
        return 3;
    }

    @Override
    protected String getPanelTitle() {
        return "Gestion des Livreurs";
    }

    @Override
    protected String getFormTitle() {
        return "Informations livreur";
    }
}

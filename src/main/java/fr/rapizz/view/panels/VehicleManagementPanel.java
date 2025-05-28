package fr.rapizz.view.panels;

import fr.rapizz.controller.VehicleController;
import fr.rapizz.model.Vehicle;
import fr.rapizz.model.VehicleType;
import fr.rapizz.util.Result;
import fr.rapizz.view.theme.AppTheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing delivery vehicles
 */
public class VehicleManagementPanel extends AbstractManagementPanel<Vehicle>{
    // Controller reference
    private final VehicleController controller;

    // Form fields specific to Vehicle
    private JComboBox<VehicleType> vehicleTypeComboBox;
    private JTextField licensePlateField;

    /**
     * Creates a new driver management panel
     * @param controller The vehicle controller
     */
    public VehicleManagementPanel(VehicleController controller) {
        super(); // Initialize the abstract panel
        this.controller = controller;
        loadEntities();
    }

    @Override
    protected void createFormFields() {
        vehicleTypeComboBox = new JComboBox<>(VehicleType.values());
        licensePlateField = new JTextField(15);

        // Custom renderer for the combo box to display the French names
        vehicleTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof VehicleType) {
                    setText(((VehicleType) value).getDisplayName());
                }
                return this;
            }
        });

        AppTheme.styleTextField(licensePlateField, "Immatriculation");
    }

    @Override
    protected void addFormFields(JPanel formCard) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formCard.add(typeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formCard.add(vehicleTypeComboBox, gbc);

        addFormField(formCard, "Immatriculation:", licensePlateField, gbc, 0, 1);
    }

    @Override
    protected DefaultTableModel createTableModel() {
        String[] columns = {"ID", "Type", "Immatriculation"};
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    @Override
    protected void onRowSelected(Object entityId) {
        controller.getVehicleById(Integer.parseInt(entityId.toString()))
                .ifPresent(this::populateFormFields);
    }

    @Override
    protected void handleAddEntity() {
        VehicleType type = (VehicleType) vehicleTypeComboBox.getSelectedItem();
        String licensePlate = licensePlateField.getText().trim();

        Result<Vehicle> result = controller.createVehicle(type, licensePlate);

        if (result.isSuccess()) {
            clearForm();
            loadEntities();
            showSuccessMessage("Véhicule ajouté avec succès!");
        } else {
            showValidationError(result.getErrors());
        }
    }

    @Override
    protected void handleUpdateEntity() {
        if (selectedEntity != null) {
            VehicleType type = (VehicleType) vehicleTypeComboBox.getSelectedItem();
            String licensePlate = licensePlateField.getText().trim();

            Result<Vehicle> result = controller.updateVehicle(
                    selectedEntity.getVehicleId(), type, licensePlate);

            if (result.isSuccess()) {
                clearForm();
                loadEntities();
                showSuccessMessage("Véhicule mis à jour avec succès!");
            } else {
                showValidationError(result.getErrors());
            }
        }
    }

    @Override
    protected void handleDeleteEntity() {
        if (selectedEntity != null) {
            if (showConfirmDialog("Êtes-vous sûr de vouloir supprimer ce véhicule ?", "Confirmation de suppression")) {
                Result<Void> result = controller.deleteVehicle(selectedEntity.getVehicleId());

                if (result.isSuccess()) {
                    clearForm();
                    loadEntities();
                    showSuccessMessage("Véhicule supprimé avec succès!");
                } else {
                    showErrorMessage(String.join("\n", result.getErrors()));
                }
            }
        }
    }

    @Override
    protected void filterEntities() {
        String searchText = searchField.getText().toLowerCase();
        List<Vehicle> filteredVehicles = controller.searchVehicles(searchText);

        tableModel.setRowCount(0);
        filteredVehicles.forEach(vehicle -> tableModel.addRow(createTableRow(vehicle)));
    }

    @Override
    protected void loadEntities() {
        tableModel.setRowCount(0);
        List<Vehicle> vehicles = controller.getAllVehicles();
        vehicles.forEach(vehicle -> tableModel.addRow(createTableRow(vehicle)));
    }

    /**
     * Creates a table row for a vehicle
     */
    private Object[] createTableRow(Vehicle vehicle) {
        return new Object[] {
                vehicle.getVehicleId(),
                vehicle.getVehicleType().getDisplayName(),
                vehicle.getLicensePlate()
        };
    }

    @Override
    protected void populateFormFields(Vehicle vehicle) {
        selectedEntity = vehicle;

        vehicleTypeComboBox.setSelectedItem(vehicle.getVehicleType());
        licensePlateField.setText(vehicle.getLicensePlate());

        // Enable update and delete buttons
        updateButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

    @Override
    protected void clearFormFields() {
        vehicleTypeComboBox.setSelectedIndex(0);
        licensePlateField.setText("");
    }

    @Override
    protected int getButtonsPanelGridY() {
        return 2;
    }

    @Override
    protected String getPanelTitle() {
        return "Gestion des Véhicules";
    }

    @Override
    protected String getFormTitle() {
        return "Informations Véhicule";
    }
}
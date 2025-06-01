package fr.rapizz.util;

import fr.rapizz.controller.*;
import fr.rapizz.service.*;
import fr.rapizz.view.panels.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewFactory {
    private final ClientController clientController;
    private final DriverController driverController;
    private final VehicleController vehicleController;
    private final DeliveryController deliveryController;
    private final MenuController menuController;
    private final StatisticsController statisticsController;

    private final Map<String, Supplier<JPanel>> viewCreators = new HashMap<>();

    @PostConstruct
    public void initializeViewCreators() {
        log.info("Initializing view creators");

        viewCreators.put("MENU_PIZZAS", () -> new MenuPanel(menuController));
        viewCreators.put("STATISTICS", () -> new StatisticsPanel(statisticsController));
        viewCreators.put("DELIVERY", () -> new DeliveryPanel(deliveryController, clientController));
        viewCreators.put("DRIVER_MANAGEMENT", () -> new DriverManagementPanel(driverController));
        viewCreators.put("VEHICLE_MANAGEMENT", () -> new VehicleManagementPanel(vehicleController));
        viewCreators.put("CLIENT_MANAGEMENT", () -> new ClientManagementPanel(clientController));

        log.info("View creators initialized: {}", viewCreators.keySet());
    }

    public JPanel createView(String viewName) {
        log.debug("Creating view: {}", viewName);

        if (viewName == null || viewName.trim().isEmpty()) {
            log.warn("Null or empty view name provided");
            return createErrorPanel("Nom de vue invalide");
        }

        Supplier<JPanel> creator = viewCreators.get(viewName);
        if (creator == null) {
            log.warn("Unknown view requested: {}", viewName);
            return createErrorPanel("Vue inconnue: " + viewName);
        }

        try {
            JPanel panel = creator.get();

            if (panel == null) {
                log.error("Creator returned null for view: {}", viewName);
                return createErrorPanel("Erreur de création de la vue: " + viewName);
            }

            log.info("View created successfully: {}", viewName);

            // Ensure visibility
            SwingUtilities.invokeLater(() -> {
                panel.setVisible(true);
                panel.revalidate();
                panel.repaint();
            });

            return panel;
        } catch (Exception e) {
            log.error("Error creating view {}: {}", viewName, e.getMessage(), e);
            return createErrorPanel("Erreur lors de la création de la vue: " + viewName +
                    "\nErreur: " + e.getMessage());
        }
    }

    private JPanel createErrorPanel(String message) {
        log.debug("Creating error panel: {}", message);

        JPanel errorPanel = new JPanel();
        errorPanel.setBackground(java.awt.Color.WHITE);
        errorPanel.setLayout(new java.awt.BorderLayout());
        errorPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel errorLabel = new JLabel("<html><div style='text-align: center;'>" +
                message.replace("\n", "<br>") + "</div></html>");
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setForeground(java.awt.Color.RED);
        errorLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));

        errorPanel.add(errorLabel, java.awt.BorderLayout.CENTER);

        // Retry button (optional)
        JButton retryButton = new JButton("Retry");
        retryButton.addActionListener(e -> {
            log.info("Reload attempt requested");
        });

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout());
        buttonPanel.setBackground(java.awt.Color.WHITE);
        buttonPanel.add(retryButton);
        errorPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        return errorPanel;
    }

    public boolean isViewSupported(String viewName) {
        boolean supported = viewCreators.containsKey(viewName);
        log.debug("View {} supported: {}", viewName, supported);
        return supported;
    }

    public Set<String> getSupportedViews() {
        return viewCreators.keySet();
    }
}
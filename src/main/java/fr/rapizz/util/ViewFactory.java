package fr.rapizz.util;

import fr.rapizz.controller.ClientController;
import fr.rapizz.controller.DriverController;
import fr.rapizz.controller.VehicleController;
import fr.rapizz.service.*;
import fr.rapizz.view.panels.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class ViewFactory {
    private final ClientController clientController;
    private final DriverController driverController;
    private final VehicleController vehicleController;
    private final PizzaService pizzaService;
    private final StatisticsService statisticsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private VehicleService vehicleService;
    @Autowired
    private DeliveryDriverService driverService;
    @Autowired
    private ClientService clientService;

    private final Map<String, Supplier<JPanel>> viewCreators = new HashMap<>();

    @PostConstruct
    public void initializeViewCreators() {
        log.info("Initialisation des créateurs de vues");

        viewCreators.put("MENU_PIZZAS", () -> new MenuPanel(pizzaService));
        viewCreators.put("STATISTICS", () -> new StatisticsPanel(statisticsService));
        viewCreators.put("DELIVERY", () -> {
            log.info("Création du DeliveryPanel avec OrderService={}, DeliveryDriverService={}, VehicleService={}, ClientController={}, PizzaService={}, ClientService={}", 
                orderService != null ? "OK" : "NULL",
                driverService != null ? "OK" : "NULL",
                vehicleService != null ? "OK" : "NULL",
                clientController != null ? "OK" : "NULL",
                pizzaService != null ? "OK" : "NULL",
                clientService != null ? "OK" : "NULL");
            return new DeliveryPanel(orderService, driverService, vehicleService, clientController, pizzaService, clientService);
        });
        viewCreators.put("DRIVER_MANAGEMENT", () -> new DriverManagementPanel(driverController));
        viewCreators.put("VEHICLE_MANAGEMENT", () -> new VehicleManagementPanel(vehicleController));
        viewCreators.put("CLIENT_MANAGEMENT", () -> new ClientManagementPanel(clientController));

        log.info("Créateurs de vues initialisés: {}", viewCreators.keySet());
    }

    public JPanel createView(String viewName) {
        log.info("Demande de création de vue: {}", viewName);

        if (viewName == null || viewName.trim().isEmpty()) {
            log.warn("Nom de vue null ou vide");
            return createErrorPanel("Nom de vue invalide");
        }

        Supplier<JPanel> creator = viewCreators.get(viewName);
        if (creator == null) {
            log.warn("Vue inconnue demandée: {}", viewName);
            return createErrorPanel("Vue inconnue: " + viewName);
        }

        try {
            JPanel panel = creator.get();

            if (panel == null) {
                log.error("Le créateur a retourné null pour la vue: {}", viewName);
                return createErrorPanel("Erreur de création de la vue: " + viewName);
            }

            log.info("Vue créée avec succès: {}", viewName);

            // Assurer la visibilité
            SwingUtilities.invokeLater(() -> {
                panel.setVisible(true);
                panel.revalidate();
                panel.repaint();
            });

            return panel;
        } catch (Exception e) {
            log.error("Erreur lors de la création de la vue {}: {}", viewName, e.getMessage(), e);
            return createErrorPanel("Erreur lors de la création de la vue: " + viewName +
                    "\nErreur: " + e.getMessage());
        }
    }

    private JPanel createErrorPanel(String message) {
        log.debug("Création d'un panel d'erreur: {}", message);

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

        // Bouton pour recharger (optionnel)
        JButton retryButton = new JButton("Réessayer");
        retryButton.addActionListener(e -> {
            log.info("Tentative de rechargement demandée");
            // Vous pouvez ajouter ici une logique de rechargement si nécessaire
        });

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout());
        buttonPanel.setBackground(java.awt.Color.WHITE);
        buttonPanel.add(retryButton);
        errorPanel.add(buttonPanel, java.awt.BorderLayout.SOUTH);

        return errorPanel;
    }

    public boolean isViewSupported(String viewName) {
        boolean supported = viewCreators.containsKey(viewName);
        log.debug("Vue {} supportée: {}", viewName, supported);
        return supported;
    }

    public java.util.Set<String> getSupportedViews() {
        return viewCreators.keySet();
    }
}
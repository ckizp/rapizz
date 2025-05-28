package fr.rapizz.util;

import fr.rapizz.controller.ClientController;
import fr.rapizz.controller.DriverController;
import fr.rapizz.controller.VehicleController;
import fr.rapizz.service.PizzaService;
import fr.rapizz.service.StatisticsService;
import fr.rapizz.view.panels.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    private final Map<String, Supplier<JPanel>> viewCreators = new HashMap<>();

    @PostConstruct
    private void initializeViewCreators() {
        viewCreators.put("MENU_PIZZAS", () -> new MenuPanel(pizzaService));
        viewCreators.put("DELIVERY", DeliveryPanel::new);
        viewCreators.put("STATISTICS", () -> new StatisticsPanel(statisticsService));
        viewCreators.put("CLIENT_MANAGEMENT", () -> new ClientManagementPanel(clientController));
        viewCreators.put("DRIVER_MANAGEMENT", () -> new DriverManagementPanel(driverController));
        viewCreators.put("VEHICLE_MANAGEMENT", () -> new VehicleManagementPanel(vehicleController));
    }

    public JPanel createView(String viewName) {
        log.debug("Creating view: {}", viewName);

        Supplier<JPanel> creator = viewCreators.get(viewName);
        if (creator == null) {
            log.warn("Unknown view requested: {}", viewName);
            return createErrorPanel("Vue inconnue: " + viewName);
        }

        try {
            JPanel panel = creator.get();
            log.info("Successfully created view: {}", viewName);
            return panel;
        } catch (Exception e) {
            log.error("Error creating view {}: {}", viewName, e.getMessage(), e);
            return createErrorPanel("Erreur lors de la création de la vue: " + viewName);
        }
    }

    private JPanel createErrorPanel(String message) {
        JPanel errorPanel = new JPanel();
        errorPanel.add(new JLabel(message));
        return errorPanel;
    }

    public boolean isViewSupported(String viewName) {
        return viewCreators.containsKey(viewName);
    }
}

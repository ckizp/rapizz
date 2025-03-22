package fr.rapizz.view;

import fr.rapizz.controller.NavigationController;
import fr.rapizz.view.panels.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Main application window
 */
public class MainFrame extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(MainFrame.class);
    private static final String DEFAULT_VIEW = "MENU_PIZZAS";

    private final Map<String, JPanel> viewMap = new HashMap<>();
    
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        logger.info("Initializing MainFrame...");

        setTitle("RaPizz - Gestion des Pizzas");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set application icon
        try {
            ImageIcon appIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/rapizz.png")));
            setIconImage(appIcon.getImage());
        } catch (Exception e) {
            logger.warn("Failed to load application icon", e);
        }

        initComponents();
        setVisible(true);
        logger.info("MainFrame is now visible.");
    }

    private void initComponents() {
        // Create navbar
        NavigationController navController = new NavigationController(this);
        JPanel navbarPanel = new NavbarPanel(navController);

        // Initialize main content panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create footer
        JPanel footerPanel = new FooterPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(navbarPanel, BorderLayout.NORTH);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        getContentPane().add(footerPanel, BorderLayout.SOUTH);

        // Initialize default view
        JPanel defaultView = addView(DEFAULT_VIEW);
        if (defaultView != null) {
            cardLayout.show(contentPanel, DEFAULT_VIEW);
            logger.info("Default view initialized: {}", DEFAULT_VIEW);
        } else {
            logger.error("Failed to initialize default view");
        }
    }

    /**
     * Shows the specified view, creating it first if necessary
     */
    public void showView(String viewName) {
        logger.info("Request to show view: {}", viewName);

        if (!viewMap.containsKey(viewName)) {
            addView(viewName);
        }
        
        if (viewMap.containsKey(viewName)) {
            logger.debug("Switching to view: {}", viewName);
            cardLayout.show(contentPanel, viewName);
            contentPanel.revalidate();
            contentPanel.repaint();
        } else {
            logger.error("Failed to show view: {}", viewName);
        }
    }

    /**
     * Creates a new view, adds it to the content panel and view map
     * @return The created view panel or null if creation failed
     */
    private JPanel addView(String viewName) {
        JPanel panel = null;
        
        try {
            panel = switch (viewName) {
                case "MENU_PIZZAS" -> new MenuPanel();
                case "DELIVERY" -> new DeliveryPanel();
                case "STATISTICS" -> new StatistiquesPanel();
                case "CLIENT_MANAGEMENT" -> new ClientManagementPanel();
                case "DRIVER_MANAGEMENT" -> new DriverManagementPanel();
                case "VEHICLE_MANAGEMENT" -> new VehicleManagementPanel();
                default -> {
                    logger.warn("Unknown view requested: {}", viewName);
                    yield null;
                }
            };
            
            if (panel != null) {
                contentPanel.add(panel, viewName);
                viewMap.put(viewName, panel);
                logger.info("Successfully created view: {}", viewName);
            }
        } catch (Exception e) {
            logger.error("Error creating view {}: {}", viewName, e.getMessage(), e);
        }
        
        return panel;
    }
}

package fr.rapizz.view;

import fr.rapizz.controller.NavigationController;
import fr.rapizz.util.ViewFactory;
import fr.rapizz.view.panels.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainFrame extends JFrame {
    private static final String DEFAULT_VIEW = "MENU_PIZZAS";

    private final ViewFactory viewFactory;
    private final NavigationController navigationController;

    private CardLayout cardLayout;
    private JPanel contentPanel;
    private final Map<String, JPanel> viewCache = new HashMap<>();

    @PostConstruct
    public void initialize() {
        log.info("Initializing MainFrame...");

        setupWindow();
        initializeComponents();
        showDefaultView();

        setVisible(true);
        log.info("MainFrame is now visible");
    }

    private void setupWindow() {
        setTitle("RaPizz - Gestion des Pizzas");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        loadApplicationIcon();
    }

    private void loadApplicationIcon() {
        try {
            ImageIcon appIcon = new ImageIcon(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("images/rapizz.png")));
            setIconImage(appIcon.getImage());
        } catch (Exception e) {
            log.warn("Failed to load application icon", e);
        }
    }

    private void initializeComponents() {
        // Configure navigation controller
        navigationController.setMainFrame(this);

        // Create UI components
        JPanel navbarPanel = new NavbarPanel(navigationController);
        JPanel footerPanel = new FooterPanel();

        // Initialize content panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Main layout
        setLayout(new BorderLayout());
        add(navbarPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private void showDefaultView() {
        showView(DEFAULT_VIEW);
    }

    public void showView(String viewName) {
        log.info("Request to show view: {}", viewName);

        // Check if view is supported
        if (!viewFactory.isViewSupported(viewName)) {
            log.error("Unsupported view requested: {}", viewName);
            showErrorView("Vue non supportée: " + viewName);
            return;
        }

        // Create view if not cached
        if (!viewCache.containsKey(viewName)) {
            JPanel panel = viewFactory.createView(viewName);
            if (panel != null) {
                contentPanel.add(panel, viewName);
                viewCache.put(viewName, panel);
                log.debug("View added to cache: {}", viewName);
            } else {
                log.error("Failed to create view: {}", viewName);
                showErrorView("Erreur lors de la création de la vue: " + viewName);
                return;
            }
        }

        // Display the view
        cardLayout.show(contentPanel, viewName);
        contentPanel.revalidate();
        contentPanel.repaint();
        log.debug("Switched to view: {}", viewName);
    }

    private void showErrorView(String errorMessage) {
        JPanel errorPanel = new JPanel();
        errorPanel.add(new JLabel(errorMessage));

        String errorViewName = "ERROR_" + System.currentTimeMillis();
        contentPanel.add(errorPanel, errorViewName);
        cardLayout.show(contentPanel, errorViewName);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}

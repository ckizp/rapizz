package fr.rapizz.view.panels;

import fr.rapizz.controller.NavigationController;
import fr.rapizz.util.MousePressListener;
import fr.rapizz.view.theme.AppTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class NavbarPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(NavbarPanel.class);

    private final NavigationController controller;

    private JPopupMenu mgtMenu;

    public NavbarPanel(NavigationController controller) {
        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBackground(AppTheme.PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Add the application logo to the navbar
        try {
            ImageIcon logoIcon = new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("images/rapizz-nobg.png")));
            Image scaledImage = logoIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
            add(logoLabel);
        } catch (Exception e) {
            logger.warn("Failed to load application logo", e);
        }

        // Create navbar items
        JLabel menuLabel = createNavItem("Menu");
        JLabel deliveryLabel = createNavItem("Fiche de Livraison");
        JLabel statsLabel = createNavItem("Statistiques");
        JLabel mgtLabel = createNavItem("Gestion ▼");

        // Add labels to the layout with spacing
        add(menuLabel);
        add(Box.createHorizontalStrut(20));
        add(deliveryLabel);
        add(Box.createHorizontalStrut(20));
        add(statsLabel);
        add(Box.createHorizontalStrut(20));
        add(mgtLabel);

        add(Box.createHorizontalGlue());

        // Add mouse click event listeners
        menuLabel.addMouseListener((MousePressListener) e -> controller.navigateTo("MENU_PIZZAS"));
        deliveryLabel.addMouseListener((MousePressListener) e -> controller.navigateTo("DELIVERY"));
        statsLabel.addMouseListener((MousePressListener) e -> controller.navigateTo("STATISTICS"));

        createManagementMenu();
        mgtLabel.addMouseListener(
            (MousePressListener) e -> mgtMenu.show(mgtLabel, 0, mgtLabel.getHeight())
        );
    }

    private void createManagementMenu() {
        mgtMenu = new JPopupMenu();

        mgtMenu.setBorder(BorderFactory.createLineBorder(AppTheme.PRIMARY, 1));

        JMenuItem clientsItem = new JMenuItem("Clients");
        JMenuItem driversItem = new JMenuItem("Livreurs");
        JMenuItem vehiclesItem = new JMenuItem("Véhicules");

        Dimension itemSize = new Dimension(150, 30);

        for (JMenuItem item : new JMenuItem[]{clientsItem, driversItem, vehiclesItem}) {
            item.setFont(AppTheme.MENU_ITEM);
            item.setPreferredSize(itemSize);
            mgtMenu.add(item);
        }

        // Listeners
        clientsItem.addActionListener(e -> controller.navigateTo("CLIENT_MANAGEMENT"));
        driversItem.addActionListener(e -> controller.navigateTo("DRIVER_MANAGEMENT"));
        vehiclesItem.addActionListener(e -> controller.navigateTo("VEHICLE_MANAGEMENT"));
    }

    private JLabel createNavItem(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.WHITE);
        label.setFont(AppTheme.NAV_ITEM);
        label.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                label.setBackground(AppTheme.PRIMARY_DARK);
                label.setOpaque(true);
                label.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setOpaque(false);
                label.repaint();
            }
        });

        return label;
    }
}

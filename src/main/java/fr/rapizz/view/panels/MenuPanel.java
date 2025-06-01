package fr.rapizz.view.panels;

import fr.rapizz.controller.MenuController;
import fr.rapizz.model.Pizza;
import fr.rapizz.service.PizzaService;
import fr.rapizz.view.theme.AppTheme;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.List;

@Component
public class MenuPanel extends JPanel {
    private final MenuController menuController;
    private List<Pizza> pizzas;

    private JPanel cardsContainer;
    private JScrollPane scrollPane;
    private JLabel titleLabel;

    public MenuPanel(MenuController menuController) {
        this.menuController = menuController;

        setupPanel();
        createComponents();
        layoutComponents();
        loadPizzas();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 248, 248));
    }

    private void createComponents() {
        titleLabel = new JLabel("Menu");
        titleLabel.setFont(AppTheme.TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Panel to contain all pizza cards with a grid layout
        cardsContainer = new JPanel();
        cardsContainer.setLayout(new GridBagLayout());
        cardsContainer.setBackground(AppTheme.BACKGROUND);

        // Scroll pane in case there are many pizzas
        scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
    }

    private void layoutComponents() {
        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void loadPizzas() {
        // Clear existing cards
        cardsContainer.removeAll();

        // Load pizzas from controller
        pizzas = menuController.getAllPizzas();

        if (pizzas == null || pizzas.isEmpty()) {
            JLabel emptyLabel = new JLabel("Aucune pizza disponible", SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 16));
            add(emptyLabel, BorderLayout.CENTER);
            return;
        }

        // Create constraints for GridBagLayout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between cards
        gbc.weightx = 1.0;

        int count = 0;
        int columns = determineColumnCount();

        for (Pizza pizza : pizzas) {
            PizzaCardPanel card = new PizzaCardPanel(pizza, menuController);

            // Set grid position
            gbc.gridx = count % columns;
            gbc.gridy = count / columns;

            // Add to container
            cardsContainer.add(card, gbc);
            count++;
        }

        revalidate();
        repaint();
    }

    // Determine how many columns we should have based on the window width
    private int determineColumnCount() {
        int width = getWidth();

        // Adjust column count based on panel width
        if (width < 600) return 1;
        else if (width < 900) return 2;
        else if (width < 1200) return 3;
        else return 4;
    }

    // Update layout when panel is resized
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        // If we have pizzas loaded, refresh the layout
        if (pizzas != null && !pizzas.isEmpty()) {
            loadPizzas();
        }
    }
}

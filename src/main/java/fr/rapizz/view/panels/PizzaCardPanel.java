package fr.rapizz.view.panels;

import fr.rapizz.controller.MenuController;
import fr.rapizz.model.Ingredient;
import fr.rapizz.model.Pizza;
import fr.rapizz.model.PizzaSize;
import fr.rapizz.service.PizzaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Set;

public class PizzaCardPanel extends JPanel {
    private final Pizza pizza;
    private final MenuController menuController;

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color TITLE_COLOR = new Color(33, 33, 33);
    private static final Color INGREDIENT_BG_COLOR = new Color(240, 240, 240);
    private static final Color PRICE_BG_COLOR = new Color(245, 245, 245);

    public PizzaCardPanel(Pizza pizza, MenuController menuController) {
        this.pizza = pizza;
        this.menuController = menuController;

        // Configuration
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(350, 250));

        // Shadow
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Add components
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createContentPanel(), BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(pizza.getPizzaName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TITLE_COLOR);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JPanel ingredientsPanel = createIngredientsPanel();
        contentPanel.add(ingredientsPanel);
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel pricesPanel = createPricesPanel();
        contentPanel.add(pricesPanel);

        return contentPanel;
    }

    private JPanel createIngredientsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Ingrédients:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));

        JPanel chipsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        chipsPanel.setOpaque(false);
        chipsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        Set<Ingredient> ingredients = pizza.getIngredients();

        if (!ingredients.isEmpty()) {
            for (Ingredient ingredient : ingredients) {
                chipsPanel.add(createIngredientChip(ingredient.getIngredientName()));
            }
        } else {
            JLabel noIngredientsLabel = new JLabel("Aucun ingrédient disponible");
            noIngredientsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            chipsPanel.add(noIngredientsLabel);
        }

        panel.add(chipsPanel);
        return panel;
    }

    private JPanel createPricesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel titleLabel = new JLabel("Prix:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));

        JPanel sizesPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        sizesPanel.setOpaque(false);
        sizesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sizesPanel.add(createSizePrice("Naine", menuController.calculatePrice(pizza, PizzaSize.NAINE)));
        sizesPanel.add(createSizePrice("Humaine", menuController.calculatePrice(pizza, PizzaSize.HUMAINE)));
        sizesPanel.add(createSizePrice("Ogresse", menuController.calculatePrice(pizza, PizzaSize.OGRESSE)));

        panel.add(sizesPanel);
        return panel;
    }

    private JPanel createIngredientChip(String name) {
        JPanel chip = new JPanel();
        chip.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 2));
        chip.setBackground(INGREDIENT_BG_COLOR);

        JLabel label = new JLabel(name);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        chip.add(label);

        chip.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        return chip;
    }

    private JPanel createSizePrice(String size, BigDecimal price) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PRICE_BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));

        JLabel sizeLabel = new JLabel(size);
        sizeLabel.setFont(new Font("Arial", Font.BOLD, 12));
        sizeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("%.2f €", price));
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(sizeLabel);
        panel.add(Box.createVerticalStrut(3));
        panel.add(priceLabel);

        return panel;
    }
}

package fr.rapizz.view.panels;

import fr.rapizz.controller.DeliveryController;
import fr.rapizz.controller.ClientController;
import fr.rapizz.model.*;
import fr.rapizz.util.Result;
import fr.rapizz.view.theme.AppTheme;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
public class DeliveryPanel extends JPanel {
    private final DeliveryController deliveryController;
    private final ClientController clientController;

    // UI Components
    private JPanel ordersPanel;
    private JComboBox<DeliveryDriver> driverCombo;
    private JComboBox<Vehicle> vehicleCombo;
    private JComboBox<Client> clientCombo;
    private JPanel pizzaSelectionPanel;
    private final List<OrderPizza> selectedPizzas;
    private final List<JCheckBox> freePizzaCheckboxes;
    private JLabel clientInfoLabel;
    private JComboBox<String> orderFilterCombo;
    private JLabel orderSummaryLabel;

    private Client selectedClient;
    private JLabel loyaltyInfoLabel;

    public DeliveryPanel(DeliveryController deliveryController, ClientController clientController) {
        this.deliveryController = deliveryController;
        this.clientController = clientController;
        this.selectedPizzas = new ArrayList<>();
        this.freePizzaCheckboxes = new ArrayList<>();

        log.info("Creating DeliveryPanel with controllers: DeliveryController={}, ClientController={}",
                deliveryController != null ? "OK" : "NULL",
                clientController != null ? "OK" : "NULL");

        initializeComponents();
    }

    private void initializeComponents() {
        try {
            log.info("Initializing DeliveryPanel components");

            setLayout(new BorderLayout());
            setBackground(Color.WHITE);
            setOpaque(true);
            setBorder(new EmptyBorder(20, 20, 20, 20));
            setPreferredSize(new Dimension(1200, 800));
            setMinimumSize(new Dimension(800, 600));

            // Main title
            JLabel mainTitle = new JLabel("Gestion des Livraisons");
            try {
                mainTitle.setFont(AppTheme.TITLE);
            } catch (Exception e) {
                mainTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
            }
            mainTitle.setForeground(Color.BLACK);
            mainTitle.setBorder(new EmptyBorder(20, 20, 10, 20));
            mainTitle.setHorizontalAlignment(SwingConstants.CENTER);
            add(mainTitle, BorderLayout.NORTH);

            // Two-column layout
            JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
            contentPanel.setOpaque(false);
            contentPanel.setPreferredSize(new Dimension(1160, 700));

            // Left: New delivery form
            JPanel formPanel = createFormPanel();

            // Right: Active orders list
            JPanel ordersContainer = createOrdersContainer();

            contentPanel.add(formPanel);
            contentPanel.add(ordersContainer);

            add(contentPanel, BorderLayout.CENTER);

            // Load initial data
            loadOrders();

            log.info("DeliveryPanel initialization completed successfully");
        } catch (Exception e) {
            log.error("Error initializing DeliveryPanel", e);
            showErrorPanel(e.getMessage());
        }
    }

    private JPanel createFormPanel() {
        JPanel formPanel = createCardPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(570, 700));

        JLabel titleForm = createTitle("Nouvelle Livraison");
        titleForm.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleForm);
        formPanel.add(Box.createVerticalStrut(15));

        // Driver selection
        List<DeliveryDriver> allDrivers = deliveryController.getAllDrivers();
        List<DeliveryDriver> availableDrivers = deliveryController.getAvailableDrivers();

        driverCombo = new JComboBox<>(allDrivers.toArray(new DeliveryDriver[0]));
        driverCombo.setRenderer(new DriverComboRenderer(availableDrivers));
        formPanel.add(createLabeledCombo("Livreur", driverCombo));

        // Vehicle selection
        List<Vehicle> allVehicles = deliveryController.getAllVehicles();
        List<Vehicle> availableVehicles = deliveryController.getAvailableVehicles();

        vehicleCombo = new JComboBox<>(allVehicles.toArray(new Vehicle[0]));
        vehicleCombo.setRenderer(new VehicleComboRenderer(availableVehicles));
        formPanel.add(createLabeledCombo("Véhicule", vehicleCombo));

        // Client selection
        List<Client> clients = clientController.getAllClients();
        clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
        clientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Client client) {
                    setText(client.getFirstName() + " " + client.getLastName());
                }
                return this;
            }
        });
        clientCombo.addActionListener(e -> updateClientInfo());
        formPanel.add(createLabeledCombo("Client", clientCombo));

        // Client info display
        JPanel clientInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        clientInfoPanel.setOpaque(false);
        clientInfoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        clientInfoLabel = new JLabel(" ");
        clientInfoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        clientInfoLabel.setForeground(new Color(100, 100, 100));

        loyaltyInfoLabel = new JLabel(" ");
        loyaltyInfoLabel.setFont(new Font("Arial", Font.BOLD, 12));
        loyaltyInfoLabel.setForeground(new Color(0, 120, 0));

        clientInfoPanel.add(clientInfoLabel);
        clientInfoPanel.add(loyaltyInfoLabel);

        formPanel.add(clientInfoPanel);;
        formPanel.add(Box.createVerticalStrut(10));

        // Pizza selection panel
        pizzaSelectionPanel = createPizzaSelectionPanel();
        formPanel.add(pizzaSelectionPanel);

        // Order summary
        JPanel summaryPanel = createOrderSummaryPanel();
        formPanel.add(summaryPanel);

        if (clientCombo.getItemCount() > 0) {
            selectedClient = (Client) clientCombo.getItemAt(0);
            clientCombo.setSelectedIndex(0);
            updateClientInfo();
        }

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        buttonsPanel.setPreferredSize(new Dimension(530, 45));

        JButton addPizzaButton = new JButton();
        AppTheme.styleButton(addPizzaButton, "Ajouter une Pizza", AppTheme.INFO_COLOR);
        addPizzaButton.addActionListener(e -> showAddPizzaDialog());
        buttonsPanel.add(addPizzaButton);

        JButton createButton = new JButton();
        AppTheme.styleButton(createButton, "Créer la Livraison", AppTheme.SUCCESS_COLOR);
        createButton.addActionListener(e -> {
            try {
                createDelivery();
            } catch (Exception ex) {
                log.error("Error creating delivery", ex);
                showErrorMessage("Erreur lors de la création de la livraison: " + ex.getMessage());
            }
        });
        buttonsPanel.add(createButton);

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(buttonsPanel);

        return formPanel;
    }

    private void updateClientInfo() {
        selectedClient = (Client) clientCombo.getSelectedItem();
        if (selectedClient != null) {
            String info = String.format("Solde: %.2f€ — ", selectedClient.getAmount().doubleValue());
            clientInfoLabel.setText(info);

            int loyaltyPoints = selectedClient.getLoyaltyCounter();
            int availableFreePizzas = loyaltyPoints / 10;

            if (availableFreePizzas > 0) {
                loyaltyInfoLabel.setText(String.format("Points fidélité: %d (%d pizza%s gratuite%s disponible%s)",
                        loyaltyPoints,
                        availableFreePizzas,
                        availableFreePizzas > 1 ? "s" : "",
                        availableFreePizzas > 1 ? "s" : "",
                        availableFreePizzas > 1 ? "s" : ""));
            } else {
                loyaltyInfoLabel.setText(String.format("Points fidélité: %d (aucune pizza gratuite disponible)", loyaltyPoints));
            }

            // Refresh les pizzas pour mettre à jour les checkboxes
            updateSelectedPizzasPanel();
            updateOrderSummary();
        } else {
            clientInfoLabel.setText(" ");
            if (loyaltyInfoLabel != null) loyaltyInfoLabel.setText(" ");
        }
    }

    private JPanel createPizzaSelectionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Pizzas Sélectionnées"));

        JPanel selectedPizzasPanel = new JPanel();
        selectedPizzasPanel.setLayout(new BoxLayout(selectedPizzasPanel, BoxLayout.Y_AXIS));
        selectedPizzasPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(selectedPizzasPanel);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setMinimumSize(new Dimension(500, 100));
        panel.add(scrollPane);

        return panel;
    }

    private JPanel createOrderSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createTitledBorder("Résumé de la Commande"));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        orderSummaryLabel = new JLabel("Aucune pizza sélectionnée");
        orderSummaryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        orderSummaryLabel.setForeground(new Color(50, 50, 50));
        panel.add(orderSummaryLabel);

        return panel;
    }

    private void showAddPizzaDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter une Pizza", true);
        dialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Pizza selection
        List<Pizza> pizzas = deliveryController.getAllPizzas();
        JComboBox<Pizza> pizzaCombo = new JComboBox<>(pizzas.toArray(new Pizza[0]));
        pizzaCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pizza pizza) {
                    setText(pizza.getPizzaName() + " (€" + pizza.getBasePrice() + ")");
                }
                return this;
            }
        });
        contentPanel.add(createLabeledCombo("Pizza", pizzaCombo));

        // Size selection
        JComboBox<PizzaSize> sizeCombo = new JComboBox<>(PizzaSize.values());
        sizeCombo.setSelectedItem(PizzaSize.HUMAINE);
        sizeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PizzaSize size) {
                    setText(size.getDisplayName());
                }
                return this;
            }
        });
        contentPanel.add(createLabeledCombo("Taille", sizeCombo));

        // Quantity selection
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);
        contentPanel.add(createLabeledSpinner("Quantité", quantitySpinner));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton();
        AppTheme.styleButton(cancelButton, "Annuler", AppTheme.NEUTRAL_COLOR);

        JButton addButton = new JButton();
        AppTheme.styleButton(addButton, "Ajouter", AppTheme.SUCCESS_COLOR);

        cancelButton.addActionListener(e -> dialog.dispose());
        addButton.addActionListener(e -> {
            Pizza selectedPizza = (Pizza) pizzaCombo.getSelectedItem();
            PizzaSize selectedSize = (PizzaSize) sizeCombo.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();

            OrderPizza orderPizza = new OrderPizza();
            orderPizza.setPizza(selectedPizza);
            orderPizza.setPizzaSize(selectedSize);
            orderPizza.setQuantity(quantity);
            orderPizza.setPizzaPrice(deliveryController.calculatePizzaPrice(selectedPizza, selectedSize));

            selectedPizzas.add(orderPizza);
            updateSelectedPizzasPanel();
            dialog.dispose();
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void updateSelectedPizzasPanel() {
        JPanel selectedPizzasPanel = (JPanel) ((JScrollPane) pizzaSelectionPanel.getComponent(0)).getViewport().getView();
        selectedPizzasPanel.removeAll();

        List<Boolean> previousStates = new ArrayList<>();
        for (JCheckBox checkbox : freePizzaCheckboxes) {
            previousStates.add(checkbox.isSelected());
        }

        freePizzaCheckboxes.clear();

        if (selectedClient == null) {
            selectedPizzasPanel.revalidate();
            selectedPizzasPanel.repaint();
            updateOrderSummary();
            return;
        }

        int availableFreePizzas = selectedClient.getLoyaltyCounter() / 10;

        for (int i = 0; i < selectedPizzas.size(); i++) {
            OrderPizza orderPizza = selectedPizzas.get(i);

            JPanel pizzaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            pizzaPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            pizzaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            pizzaPanel.setPreferredSize(new Dimension(480, 40));
            pizzaPanel.setOpaque(false);

            String pizzaText = String.format("%dx %s (%s) - %.2f€",
                    orderPizza.getQuantity(),
                    orderPizza.getPizza().getPizzaName(),
                    orderPizza.getPizzaSize().getDisplayName(),
                    orderPizza.getPizzaPrice().multiply(new BigDecimal(orderPizza.getQuantity())));

            JLabel pizzaLabel = new JLabel(pizzaText);
            pizzaLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            pizzaLabel.setPreferredSize(new Dimension(250, 25));
            pizzaLabel.setVerticalAlignment(SwingConstants.CENTER);
            pizzaPanel.add(pizzaLabel);

            pizzaPanel.add(Box.createHorizontalStrut(10));

            JCheckBox loyaltyCheckbox = new JCheckBox("Fidélité (gratuite)");
            loyaltyCheckbox.setFont(new Font("Arial", Font.PLAIN, 11));
            loyaltyCheckbox.setOpaque(false);
            loyaltyCheckbox.setPreferredSize(new Dimension(120, 25));

            if (i < previousStates.size()) {
                loyaltyCheckbox.setSelected(previousStates.get(i));
            }

            freePizzaCheckboxes.add(loyaltyCheckbox);

            int usedFreePizzas = 0;
            for (JCheckBox cb : freePizzaCheckboxes) {
                if (cb.isSelected()) {
                    usedFreePizzas++;
                }
            }

            boolean canUseLoyalty = usedFreePizzas < availableFreePizzas || loyaltyCheckbox.isSelected();
            loyaltyCheckbox.setEnabled(canUseLoyalty);

            if (!loyaltyCheckbox.isEnabled()) {
                loyaltyCheckbox.setToolTipText("Points de fidélité insuffisants");
            } else {
                loyaltyCheckbox.setToolTipText("Utiliser 10 points de fidélité pour cette pizza");
            }

            loyaltyCheckbox.addActionListener(e -> {
                updateCheckboxAvailability();
                updateOrderSummary();
            });

            pizzaPanel.add(loyaltyCheckbox);

            JButton removeButton = new JButton("X");
            removeButton.setPreferredSize(new Dimension(25, 25));
            removeButton.setFont(new Font("Arial", Font.BOLD, 12));
            removeButton.setForeground(Color.RED);
            removeButton.setBackground(Color.WHITE);
            removeButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            removeButton.setFocusPainted(false);
            removeButton.setToolTipText("Supprimer cette pizza");

            removeButton.addActionListener(e -> {
                selectedPizzas.remove(orderPizza);
                updateSelectedPizzasPanel();
                updateOrderSummary();
            });

            pizzaPanel.add(removeButton);
            selectedPizzasPanel.add(pizzaPanel);
        }

        selectedPizzasPanel.revalidate();
        selectedPizzasPanel.repaint();
        updateOrderSummary();
    }

    private void updateCheckboxAvailability() {
        if (selectedClient == null) return;

        int availableFreePizzas = selectedClient.getLoyaltyCounter() / 10;
        int usedFreePizzas = 0;

        for (JCheckBox checkbox : freePizzaCheckboxes) {
            if (checkbox.isSelected()) {
                usedFreePizzas++;
            }
        }

        for (JCheckBox checkbox : freePizzaCheckboxes) {
            if (checkbox.isSelected()) {
                checkbox.setEnabled(true);
                checkbox.setToolTipText("Utiliser 10 points de fidélité pour cette pizza");
            } else {
                boolean canSelect = usedFreePizzas < availableFreePizzas;
                checkbox.setEnabled(canSelect);
                if (canSelect) {
                    checkbox.setToolTipText("Utiliser 10 points de fidélité pour cette pizza");
                } else {
                    checkbox.setToolTipText("Points de fidélité insuffisants");
                }
            }
        }
    }

    private void updateOrderSummary() {
        if (selectedPizzas.isEmpty()) {
            orderSummaryLabel.setText("Aucune pizza sélectionnée");
            return;
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        BigDecimal loyaltyDiscount = BigDecimal.ZERO;
        int freePizzasUsed = 0;

        for (int i = 0; i < selectedPizzas.size(); i++) {
            OrderPizza pizza = selectedPizzas.get(i);
            BigDecimal unitPrice = pizza.getPizzaPrice();
            int quantity = pizza.getQuantity();

            if (i < freePizzaCheckboxes.size() && freePizzaCheckboxes.get(i).isSelected()) {
                loyaltyDiscount = loyaltyDiscount.add(unitPrice);
                freePizzasUsed++;

                if (quantity > 1) {
                    BigDecimal remainingTotal = unitPrice.multiply(new BigDecimal(quantity - 1));
                    totalPrice = totalPrice.add(remainingTotal);
                }
            } else {
                BigDecimal pizzaTotal = unitPrice.multiply(new BigDecimal(quantity));
                totalPrice = totalPrice.add(pizzaTotal);
            }
        }

        StringBuilder summary = new StringBuilder();
        summary.append("<html>");

        if (loyaltyDiscount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal originalTotal = totalPrice.add(loyaltyDiscount);
            summary.append(String.format("Sous-total: %.2f€<br>", originalTotal.doubleValue()));
            summary.append(String.format("Remise fidélité (%d pizza%s): -%.2f€<br>",
                    freePizzasUsed,
                    freePizzasUsed > 1 ? "s" : "",
                    loyaltyDiscount.doubleValue()));
            summary.append(String.format("<b>Total à payer: %.2f€</b>", totalPrice.doubleValue()));
        } else {
            summary.append(String.format("<b>Total: %.2f€</b>", totalPrice.doubleValue()));
        }

        summary.append("</html>");
        orderSummaryLabel.setText(summary.toString());
    }

    private void createDelivery() {
        // Validation
        if (driverCombo.getSelectedItem() == null) {
            throw new IllegalArgumentException("Veuillez sélectionner un livreur");
        }
        if (vehicleCombo.getSelectedItem() == null) {
            throw new IllegalArgumentException("Veuillez sélectionner un véhicule");
        }
        if (selectedClient == null) {
            throw new IllegalArgumentException("Veuillez sélectionner un client valide");
        }
        if (selectedPizzas.isEmpty()) {
            throw new IllegalArgumentException("Veuillez ajouter au moins une pizza");
        }

        // Free pizzas
        List<Integer> freePizzaIndices = new ArrayList<>();
        for (int i = 0; i < freePizzaCheckboxes.size(); i++) {
            if (freePizzaCheckboxes.get(i).isSelected()) {
                freePizzaIndices.add(i);
            }
        }

        BigDecimal totalToPay = BigDecimal.ZERO;
        for (int i = 0; i < selectedPizzas.size(); i++) {
            OrderPizza pizza = selectedPizzas.get(i);
            BigDecimal unitPrice = pizza.getPizzaPrice();
            int quantity = pizza.getQuantity();

            if (freePizzaIndices.contains(i)) {
                if (quantity > 1) {
                    BigDecimal remainingTotal = unitPrice.multiply(new BigDecimal(quantity - 1));
                    totalToPay = totalToPay.add(remainingTotal);
                }
            } else {
                BigDecimal pizzaTotal = unitPrice.multiply(new BigDecimal(quantity));
                totalToPay = totalToPay.add(pizzaTotal);
            }
        }

        if (selectedClient.getAmount().compareTo(totalToPay) < 0) {
            showErrorMessage(String.format("Solde insuffisant.\nSolde disponible: %.2f€\nTotal à payer: %.2f€",
                    selectedClient.getAmount().doubleValue(), totalToPay.doubleValue()));
            return;
        }

        log.info("Creating order with {} pizzas, {} free pizzas (loyalty), total: {}€",
                selectedPizzas.size(), freePizzaIndices.size(), totalToPay.doubleValue());

        Result<Order> result = deliveryController.createDelivery(
                (DeliveryDriver) driverCombo.getSelectedItem(),
                (Vehicle) vehicleCombo.getSelectedItem(),
                selectedClient,
                selectedPizzas,
                "BALANCE",
                freePizzaIndices
        );

        if (result.isSuccess()) {
            refreshAllClientData();
            resetForm();
            loadOrders();
            showSuccessMessage("Livraison créée avec succès !");
        } else {
            showErrorMessage(String.join("\n", result.getErrors()));
        }
    }

    private void refreshAvailabilityIndicators() {
        List<DeliveryDriver> availableDrivers = deliveryController.getAvailableDrivers();
        driverCombo.setRenderer(new DriverComboRenderer(availableDrivers));

        List<Vehicle> availableVehicles = deliveryController.getAvailableVehicles();
        vehicleCombo.setRenderer(new VehicleComboRenderer(availableVehicles));

        driverCombo.repaint();
        vehicleCombo.repaint();
    }

    private void resetForm() {
        if (driverCombo.getItemCount() > 0) driverCombo.setSelectedIndex(0);
        if (vehicleCombo.getItemCount() > 0) vehicleCombo.setSelectedIndex(0);
        if (clientCombo.getItemCount() > 0) clientCombo.setSelectedIndex(0);
        selectedPizzas.clear();
        freePizzaCheckboxes.clear();
        updateSelectedPizzasPanel();
        updateClientInfo();
        refreshAvailabilityIndicators();
    }

    private JPanel createOrdersContainer() {
        JPanel ordersContainer = createCardPanel();
        ordersContainer.setLayout(new BorderLayout());
        ordersContainer.setPreferredSize(new Dimension(570, 700));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setOpaque(true);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel titleOrders = createTitle("Commandes");
        headerPanel.add(titleOrders, BorderLayout.WEST);

        JPanel filterPanel = createOrderFilterPanel();
        headerPanel.add(filterPanel, BorderLayout.EAST);

        ordersContainer.add(headerPanel, BorderLayout.NORTH);

        ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        ordersPanel.setOpaque(true);
        ordersPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        ordersContainer.add(scrollPane, BorderLayout.CENTER);
        return ordersContainer;
    }

    private JPanel createOrderFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setOpaque(true);

        JLabel filterLabel = new JLabel("Afficher: ");
        filterLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        filterPanel.add(filterLabel);

        String[] filterOptions = {
                "Commandes actives",
                "En attente",
                "En Cours de livraison",
                "Livrées",
                "Toutes"
        };

        orderFilterCombo = new JComboBox<>(filterOptions);
        orderFilterCombo.setSelectedItem("Commandes Actives"); // Par défaut
        orderFilterCombo.setPreferredSize(new Dimension(180, 30));
        orderFilterCombo.addActionListener(e -> loadOrders());
        filterPanel.add(orderFilterCombo);

        return filterPanel;
    }

    public void loadOrders() {
        ordersPanel.removeAll();

        try {
            String selectedFilter = (String) orderFilterCombo.getSelectedItem();
            log.info("Loading orders with filter: {}", selectedFilter);

            List<Order> orders = getOrdersByFilter(selectedFilter);
            log.info("Found {} orders for filter {}", orders.size(), selectedFilter);

            if (orders.isEmpty()) {
                showNoOrdersMessage("Aucune commande trouvée pour ce filtre");
            } else {
                for (Order order : orders) {
                    JPanel orderCard = createOrderCard(order);
                    ordersPanel.add(orderCard);
                    ordersPanel.add(Box.createVerticalStrut(10));
                }
            }

            ordersPanel.revalidate();
            ordersPanel.repaint();

            refreshAvailabilityIndicators();

            SwingUtilities.invokeLater(() -> {
                revalidate();
                repaint();
            });

            log.info("Orders loading completed successfully for filter: {}", selectedFilter);
        } catch (Exception e) {
            log.error("Error loading orders with filter", e);
            showNoOrdersMessage("Erreur de chargement: " + e.getMessage());
        }
    }

    private List<Order> getOrdersByFilter(String filter) {
        return switch (filter) {
            case "Commandes actives" -> deliveryController.getActiveOrders();
            case "En attente" -> deliveryController.getOrdersByStatus(OrderStatus.PENDING);
            case "En cours de livraison" -> deliveryController.getOrdersByStatus(OrderStatus.IN_PROGRESS);
            case "Livrées" -> deliveryController.getOrdersByStatus(OrderStatus.DELIVERED);
            case "Toutes" -> deliveryController.getAllOrders();
            default -> deliveryController.getActiveOrders();
        };
    }

    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(480, Integer.MAX_VALUE));
        card.setMinimumSize(new Dimension(400, 150));
        card.setBackground(Color.WHITE);
        card.setOpaque(true);

        // Order header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setOpaque(true);
        headerPanel.setPreferredSize(new Dimension(480, 45));

        JPanel leftHeader = new JPanel();
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
        leftHeader.setBackground(Color.WHITE);
        leftHeader.setOpaque(true);

        JLabel orderIdLabel = new JLabel("Commande #" + order.getOrderId());
        orderIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        leftHeader.add(orderIdLabel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        JLabel dateLabel = new JLabel("Créée le " + order.getOrderDate().format(formatter));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(Color.GRAY);
        leftHeader.add(dateLabel);

        headerPanel.add(leftHeader, BorderLayout.WEST);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightHeader.setBackground(Color.WHITE);
        rightHeader.setOpaque(true);

        JLabel statusLabel = new JLabel(order.getOrderStatus().getDisplayName());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(order.getOrderStatus() == OrderStatus.PENDING ? Color.ORANGE : Color.BLUE);
        rightHeader.add(statusLabel);

        headerPanel.add(rightHeader, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

        // Order details
        JPanel detailsPanel = createOrderDetailsPanel(order);
        card.add(detailsPanel, BorderLayout.CENTER);

        // Action buttons (bas droite)
        JPanel buttonContainer = new JPanel(new BorderLayout());
        buttonContainer.setBackground(Color.WHITE);
        buttonContainer.setOpaque(true);

        JPanel buttonPanel = createOrderButtonsPanel(order);
        buttonContainer.add(buttonPanel, BorderLayout.EAST);

        card.add(buttonContainer, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createOrderDetailsPanel(Order order) {
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setOpaque(true);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Delivery time (if delivered)
        if (order.getDeliveredAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            JLabel deliveredLabel = new JLabel("Livrée: " + order.getDeliveredAt().format(formatter));
            deliveredLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            detailsPanel.add(deliveredLabel);
        }

        // Client info
        if (order.getClient() != null) {
            String clientName = (order.getClient().getFirstName() != null ? order.getClient().getFirstName() : "") +
                    " " + (order.getClient().getLastName() != null ? order.getClient().getLastName() : "");
            JLabel clientLabel = new JLabel("Client: " + clientName.trim());
            clientLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            detailsPanel.add(clientLabel);

            if (order.getClient().getClientAddress() != null) {
                JLabel addressLabel = new JLabel("Adresse: " + order.getClient().getClientAddress());
                addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                detailsPanel.add(addressLabel);
            }
        }

        // Driver info
        if (order.getDriver() != null) {
            JLabel driverLabel = new JLabel("Livreur: " + order.getDriver().getFirstName() + " " + order.getDriver().getLastName());
            driverLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            detailsPanel.add(driverLabel);
        }

        // Pizza details
        try {
            for (OrderPizza orderPizza : order.getOrderItems()) {
                String pizzaText;

                if (orderPizza.isFree()) {
                    pizzaText = String.format("%dx %s (%s) GRATUITE - %s",
                            orderPizza.getQuantity(),
                            orderPizza.getPizza().getPizzaName(),
                            orderPizza.getPizzaSize().getDisplayName(),
                            orderPizza.getFreeReason().getDisplayName());

                    JLabel pizzaLabel = new JLabel(pizzaText);
                    pizzaLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    pizzaLabel.setForeground(new Color(0, 128, 0)); // Vert foncé
                    detailsPanel.add(pizzaLabel);
                } else {
                    pizzaText = String.format("%dx %s (%s) - %.2f€",
                            orderPizza.getQuantity(),
                            orderPizza.getPizza().getPizzaName(),
                            orderPizza.getPizzaSize().getDisplayName(),
                            orderPizza.getPizzaPrice().multiply(new BigDecimal(orderPizza.getQuantity())));

                    JLabel pizzaLabel = new JLabel(pizzaText);
                    pizzaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    detailsPanel.add(pizzaLabel);
                }
            }
        } catch (Exception e) {
            log.warn("Error formatting order details for order #{}", order.getOrderId(), e);
            JLabel pizzasLabel = new JLabel("Pizzas: Détails indisponibles");
            pizzasLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            detailsPanel.add(pizzasLabel);
        }

        // Total
        try {
            double total = deliveryController.calculateOrderTotal(order);
            JLabel totalLabel = new JLabel(String.format("Total: %.2f €", total));
            totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
            detailsPanel.add(totalLabel);
        } catch (Exception e) {
            log.warn("Error calculating total for order #{}", order.getOrderId(), e);
        }

        // Late indicator
        try {
            if (deliveryController.isLateDelivery(order)) {
                JLabel lateLabel = new JLabel("Commande en retard");
                lateLabel.setFont(new Font("Arial", Font.BOLD, 14));
                lateLabel.setForeground(Color.RED);
                detailsPanel.add(lateLabel);
            }
        } catch (Exception e) {
            log.warn("Error checking delivery delay for order #{}", order.getOrderId(), e);
        }

        return detailsPanel;
    }

    private JPanel createOrderButtonsPanel(Order order) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setOpaque(true);

        if (order.getOrderStatus() == OrderStatus.PENDING) {
            JButton startButton = new JButton();
            AppTheme.styleButton(startButton, "Démarrer Livraison", AppTheme.INFO_COLOR);
            startButton.setPreferredSize(new Dimension(160, 35));
            startButton.addActionListener(e -> updateOrderStatus(order.getOrderId(), OrderStatus.IN_PROGRESS));
            buttonPanel.add(startButton);
        } else if (order.getOrderStatus() == OrderStatus.IN_PROGRESS) {
            JButton completeButton = new JButton();
            AppTheme.styleButton(completeButton, "Terminer Livraison", AppTheme.SUCCESS_COLOR);
            completeButton.setPreferredSize(new Dimension(160, 35));
            completeButton.addActionListener(e -> updateOrderStatus(order.getOrderId(), OrderStatus.DELIVERED));
            buttonPanel.add(completeButton);
        }

        return buttonPanel;
    }

    private void updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Result<Order> result = deliveryController.updateOrderStatus(orderId, newStatus);

        if (result.isSuccess()) {
            loadOrders();
        } else {
            showErrorMessage("Erreur lors de la mise à jour du statut: " + String.join("\n", result.getErrors()));
        }
    }

    private void refreshAllClientData() {
        try {
            Integer selectedClientId = selectedClient != null ? selectedClient.getClientId() : null;

            List<Client> updatedClients = clientController.getAllClients();

            clientCombo.removeAllItems();
            Client newSelectedClient = null;

            for (Client client : updatedClients) {
                clientCombo.addItem(client);

                if (client.getClientId().equals(selectedClientId)) {
                    newSelectedClient = client;
                }
            }

            if (newSelectedClient != null) {
                selectedClient = newSelectedClient;
                clientCombo.setSelectedItem(newSelectedClient);
            } else if (clientCombo.getItemCount() > 0) {
                selectedClient = clientCombo.getItemAt(0);
                clientCombo.setSelectedIndex(0);
            } else {
                selectedClient = null;
            }

            log.info("All client data refreshed - {} clients loaded", updatedClients.size());
            if (selectedClient != null) {
                log.info("Selected client updated: balance={}€, loyalty={} points",
                        selectedClient.getAmount(), selectedClient.getLoyaltyCounter());
            }

        } catch (Exception e) {
            log.error("Error refreshing all client data", e);
        }
    }

    // Utility methods
    private void showNoOrdersMessage(String message) {
        ordersPanel.removeAll();
        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerPanel.setOpaque(false);
        JLabel noOrdersLabel = new JLabel(message);
        noOrdersLabel.setFont(new Font("Arial", Font.ITALIC, 16));
        noOrdersLabel.setForeground(Color.GRAY);
        centerPanel.add(noOrdersLabel);
        ordersPanel.add(centerPanel);
        ordersPanel.revalidate();
        ordersPanel.repaint();
    }

    private void showErrorPanel(String errorMessage) {
        removeAll();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        JLabel errorLabel = new JLabel("Erreur: " + errorMessage);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        add(errorLabel, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);
        panel.setBorder(new CompoundBorder(
                new LineBorder(new Color(230, 230, 230), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    private JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        return label;
    }

    private JPanel createLabeledCombo(String labelText, JComboBox<?> combo) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(200, 30));
        panel.add(label, BorderLayout.NORTH);
        panel.add(combo, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return panel;
    }

    private JPanel createLabeledSpinner(String labelText, JSpinner spinner) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        spinner.setPreferredSize(new Dimension(200, 30));
        panel.add(label, BorderLayout.NORTH);
        panel.add(spinner, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return panel;
    }

    private static class DriverComboRenderer extends DefaultListCellRenderer {
        private final List<DeliveryDriver> availableDrivers;

        public DriverComboRenderer(List<DeliveryDriver> availableDrivers) {
            this.availableDrivers = availableDrivers;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof DeliveryDriver driver) {
                boolean isAvailable = availableDrivers.stream()
                        .anyMatch(d -> d.getDriverId().equals(driver.getDriverId()));

                String driverName = driver.getFirstName() + " " + driver.getLastName();

                if (isAvailable) {
                    setText(driverName + " (Disponible)");
                    setForeground(isSelected ? Color.WHITE : new Color(0, 120, 0)); // Vert foncé
                } else {
                    setText(driverName + " (En livraison)");
                    setForeground(isSelected ? Color.WHITE : new Color(255, 140, 0)); // Orange
                    setFont(getFont().deriveFont(Font.ITALIC));
                }
            }

            return this;
        }
    }

    private static class VehicleComboRenderer extends DefaultListCellRenderer {
        private final List<Vehicle> availableVehicles;

        public VehicleComboRenderer(List<Vehicle> availableVehicles) {
            this.availableVehicles = availableVehicles;
        }

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Vehicle vehicle) {
                boolean isAvailable = availableVehicles.stream()
                        .anyMatch(v -> v.getVehicleId().equals(vehicle.getVehicleId()));

                String vehicleInfo = vehicle.getVehicleType().getDisplayName() + " (" + vehicle.getLicensePlate() + ")";

                if (isAvailable) {
                    setText(vehicleInfo + " (Disponible)");
                    setForeground(isSelected ? Color.WHITE : new Color(0, 120, 0)); // Vert foncé
                } else {
                    setText(vehicleInfo + " (En cours d'utilisation)");
                    setForeground(isSelected ? Color.WHITE : new Color(255, 140, 0)); // Orange
                    setFont(getFont().deriveFont(Font.ITALIC)); // Italique
                }
            }

            return this;
        }
    }
}
package fr.rapizz.view.panels;

import fr.rapizz.model.Order;
import fr.rapizz.model.OrderStatus;
import fr.rapizz.model.DeliveryDriver;
import fr.rapizz.model.Vehicle;
import fr.rapizz.model.Client;
import fr.rapizz.model.Pizza;
import fr.rapizz.model.PizzaSize;
import fr.rapizz.model.OrderPizza;
import fr.rapizz.model.FreeReason;
import fr.rapizz.service.OrderService;
import fr.rapizz.service.DeliveryDriverService;
import fr.rapizz.service.VehicleService;
import fr.rapizz.service.PizzaService;
import fr.rapizz.controller.ClientController;
import fr.rapizz.service.ClientService;
import fr.rapizz.view.theme.AppTheme;
import fr.rapizz.util.Result;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Slf4j
public class DeliveryPanel extends JPanel {
    private final OrderService orderService;
    private final DeliveryDriverService driverService;
    private final VehicleService vehicleService;
    private final ClientController clientController;
    private final PizzaService pizzaService;
    private final ClientService clientService;
    private JPanel ordersPanel;
    private JComboBox<DeliveryDriver> driverCombo;
    private JComboBox<Vehicle> vehicleCombo;
    private JComboBox<Client> clientCombo;
    private JPanel pizzaSelectionPanel;
    private List<OrderPizza> selectedPizzas;

    public DeliveryPanel(OrderService orderService, DeliveryDriverService driverService, VehicleService vehicleService, ClientController clientController, PizzaService pizzaService, ClientService clientService) {
        this.orderService = orderService;
        this.driverService = driverService;
        this.vehicleService = vehicleService;
        this.clientController = clientController;
        this.pizzaService = pizzaService;
        this.clientService = clientService;
        this.selectedPizzas = new ArrayList<>();
        log.info("Création du DeliveryPanel avec les services: OrderService={}, DriverService={}, VehicleService={}, ClientController={}, PizzaService={}, ClientService={}", 
            orderService != null ? "OK" : "NULL",
            driverService != null ? "OK" : "NULL",
            vehicleService != null ? "OK" : "NULL",
            clientController != null ? "OK" : "NULL",
            pizzaService != null ? "OK" : "NULL",
            clientService != null ? "OK" : "NULL");
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

            // ===== Main Title =====
            JLabel mainTitle = new JLabel("Fiche de livraison");
            try {
                mainTitle.setFont(AppTheme.TITLE);
            } catch (Exception e) {
                mainTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
            }
            mainTitle.setForeground(Color.WHITE);
            mainTitle.setBorder(new EmptyBorder(20, 20, 10, 20));
            mainTitle.setHorizontalAlignment(SwingConstants.CENTER);
            add(mainTitle, BorderLayout.NORTH);

            // Central panel containing the two columns
            JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
            contentPanel.setOpaque(false);
            contentPanel.setPreferredSize(new Dimension(1160, 700));

            // Left side: New Delivery
            JPanel formPanel = createFormPanel();
            
            // Right side: Orders List
            JPanel ordersContainer = createOrdersContainer();

            // Add panels to contentPanel
            contentPanel.add(formPanel);
            contentPanel.add(ordersContainer);

            // Add contentPanel to center
            add(contentPanel, BorderLayout.CENTER);

            // Load orders
            loadOrders();

            log.info("DeliveryPanel initialization completed successfully");
        } catch (Exception e) {
            log.error("Error during DeliveryPanel initialization", e);
            showErrorPanel(e.getMessage());
        }
    }

    private JPanel createFormPanel() {
        JPanel formPanel = createCardPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setPreferredSize(new Dimension(570, 700));

        JLabel titleForm = createTitle("Nouvelle Livraison");
        formPanel.add(titleForm);
        formPanel.add(Box.createVerticalStrut(15));

        // Liste des livreurs
        List<DeliveryDriver> drivers = driverService.findAll();
        driverCombo = new JComboBox<>(drivers.toArray(new DeliveryDriver[0]));
        driverCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DeliveryDriver) {
                    DeliveryDriver driver = (DeliveryDriver) value;
                    setText(driver.getFirstName() + " " + driver.getLastName());
                }
                return this;
            }
        });
        formPanel.add(createLabeledCombo("Livreur", driverCombo));

        // Liste des véhicules
        List<Vehicle> vehicles = vehicleService.findAll();
        vehicleCombo = new JComboBox<>(vehicles.toArray(new Vehicle[0]));
        vehicleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Vehicle) {
                    Vehicle vehicle = (Vehicle) value;
                    setText(vehicle.getVehicleType().getDisplayName() + " (" + vehicle.getLicensePlate() + ")");
                }
                return this;
            }
        });
        formPanel.add(createLabeledCombo("Véhicule", vehicleCombo));

        // Liste des clients
        List<Client> clients = clientController.getAllClients();
        clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
        clientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Client) {
                    Client client = (Client) value;
                    setText(client.getFirstName() + " " + client.getLastName());
                }
                return this;
            }
        });

        formPanel.add(createLabeledCombo("Client", clientCombo));

        // Panel pour la sélection des pizzas
        pizzaSelectionPanel = new JPanel();
        pizzaSelectionPanel.setLayout(new BoxLayout(pizzaSelectionPanel, BoxLayout.Y_AXIS));
        pizzaSelectionPanel.setOpaque(false);
        pizzaSelectionPanel.setBorder(BorderFactory.createTitledBorder("Pizzas"));

        // Bouton pour ajouter une pizza
        JButton addPizzaButton = new JButton("Ajouter une pizza");
        addPizzaButton.addActionListener(e -> showAddPizzaDialog());
        pizzaSelectionPanel.add(addPizzaButton);
        pizzaSelectionPanel.add(Box.createVerticalStrut(10));

        // Panel pour afficher les pizzas sélectionnées
        JPanel selectedPizzasPanel = new JPanel();
        selectedPizzasPanel.setLayout(new BoxLayout(selectedPizzasPanel, BoxLayout.Y_AXIS));
        selectedPizzasPanel.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(selectedPizzasPanel);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        pizzaSelectionPanel.add(scrollPane);

        formPanel.add(pizzaSelectionPanel);

        JButton btn = new JButton("Créer la livraison");
        btn.setBackground(Color.BLACK);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 14));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        btn.addActionListener(e -> {
            try {
                createDelivery();
            } catch (Exception ex) {
                log.error("Erreur lors de la création de la livraison", ex);
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la création de la livraison: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(btn);

        return formPanel;
    }

    private void showAddPizzaDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter une pizza", true);
        dialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Sélection de la pizza
        List<Pizza> pizzas = pizzaService.findAll();
        JComboBox<Pizza> pizzaCombo = new JComboBox<>(pizzas.toArray(new Pizza[0]));
        pizzaCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Pizza) {
                    Pizza pizza = (Pizza) value;
                    setText(pizza.getPizzaName() + " (€" + pizza.getBasePrice() + ")");
                }
                return this;
            }
        });
        contentPanel.add(createLabeledCombo("Pizza", pizzaCombo));

        // Sélection de la taille
        JComboBox<PizzaSize> sizeCombo = new JComboBox<>(PizzaSize.values());
        sizeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PizzaSize) {
                    PizzaSize size = (PizzaSize) value;
                    setText(size.getDisplayName());
                }
                return this;
            }
        });
        contentPanel.add(createLabeledCombo("Taille", sizeCombo));

        // Sélection de la quantité
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        JSpinner quantitySpinner = new JSpinner(spinnerModel);
        contentPanel.add(createLabeledSpinner("Quantité", quantitySpinner));

        // Boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Annuler");
        JButton addButton = new JButton("Ajouter");

        cancelButton.addActionListener(e -> dialog.dispose());
        addButton.addActionListener(e -> {
            Pizza selectedPizza = (Pizza) pizzaCombo.getSelectedItem();
            PizzaSize selectedSize = (PizzaSize) sizeCombo.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();

            OrderPizza orderPizza = new OrderPizza();
            orderPizza.setPizza(selectedPizza);
            orderPizza.setPizzaSize(selectedSize);
            orderPizza.setQuantity(quantity);
            orderPizza.setPizzaPrice(pizzaService.calculatePrice(selectedPizza, selectedSize));

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
        JPanel selectedPizzasPanel = (JPanel) ((JScrollPane) pizzaSelectionPanel.getComponent(2)).getViewport().getView();
        selectedPizzasPanel.removeAll();
        selectedPizzasPanel.setLayout(new BoxLayout(selectedPizzasPanel, BoxLayout.Y_AXIS));

        // Agréger les pizzas identiques (sauf si gratuites)
        Map<String, OrderPizza> aggregatedPizzas = new HashMap<>();
        List<OrderPizza> freePizzas = new ArrayList<>();

        for (OrderPizza orderPizza : selectedPizzas) {
            if (orderPizza.getIsFree()) {
                freePizzas.add(orderPizza);
            } else {
                String key = orderPizza.getPizza().getPizzaName() + "_" + orderPizza.getPizzaSize();
                if (aggregatedPizzas.containsKey(key)) {
                    OrderPizza existing = aggregatedPizzas.get(key);
                    existing.setQuantity(existing.getQuantity() + orderPizza.getQuantity());
                } else {
                    aggregatedPizzas.put(key, orderPizza);
                }
            }
        }

        // Afficher d'abord les pizzas payantes
        for (OrderPizza orderPizza : aggregatedPizzas.values()) {
            JPanel pizzaPanel = new JPanel(new BorderLayout());
            pizzaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            pizzaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            String pizzaText = String.format("x%d %s (%s) - %.2f€",
                orderPizza.getQuantity(),
                orderPizza.getPizza().getPizzaName(),
                orderPizza.getPizzaSize().getDisplayName(),
                orderPizza.getPizzaPrice().multiply(new BigDecimal(orderPizza.getQuantity())));

            JLabel pizzaLabel = new JLabel(pizzaText);
            pizzaPanel.add(pizzaLabel, BorderLayout.CENTER);

            JButton removeButton = new JButton("X");
            removeButton.setMaximumSize(new Dimension(30, 25));
            removeButton.addActionListener(e -> {
                selectedPizzas.remove(orderPizza);
                updateSelectedPizzasPanel();
            });
            pizzaPanel.add(removeButton, BorderLayout.EAST);

            selectedPizzasPanel.add(pizzaPanel);
        }

        // Afficher ensuite les pizzas gratuites
        for (OrderPizza orderPizza : freePizzas) {
            JPanel pizzaPanel = new JPanel(new BorderLayout());
            pizzaPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            pizzaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            String pizzaText = String.format("x%d %s (%s) OFFERTE 🎁",
                orderPizza.getQuantity(),
                orderPizza.getPizza().getPizzaName(),
                orderPizza.getPizzaSize().getDisplayName());
            
            JLabel pizzaLabel = new JLabel(pizzaText);
            pizzaLabel.setFont(new Font("Arial", Font.BOLD, 14));
            pizzaLabel.setForeground(new Color(0, 128, 0)); // Vert foncé
            pizzaPanel.add(pizzaLabel, BorderLayout.CENTER);

            JButton removeButton = new JButton("X");
            removeButton.setMaximumSize(new Dimension(30, 25));
            removeButton.addActionListener(e -> {
                selectedPizzas.remove(orderPizza);
                updateSelectedPizzasPanel();
            });
            pizzaPanel.add(removeButton, BorderLayout.EAST);

            selectedPizzasPanel.add(pizzaPanel);
        }

        selectedPizzasPanel.revalidate();
        selectedPizzasPanel.repaint();
    }

    private String formatPhoneNumber(String phone) {
        // Si le numéro commence déjà par +, le retourner tel quel
        if (phone.startsWith("+")) {
            return phone;
        }
        
        // Supprimer tous les caractères non numériques
        String digitsOnly = phone.replaceAll("[^0-9]", "");
        
        // Si le numéro commence par 0, le remplacer par 33
        if (digitsOnly.startsWith("0")) {
            digitsOnly = "33" + digitsOnly.substring(1);
        }
        
        // Si le numéro ne commence pas par 33, l'ajouter
        if (!digitsOnly.startsWith("33")) {
            digitsOnly = "33" + digitsOnly;
        }
        
        // S'assurer que le numéro commence par un chiffre entre 1 et 9
        if (digitsOnly.charAt(0) == '0') {
            digitsOnly = "1" + digitsOnly.substring(1);
        }
        
        // Ajouter le + au début
        return "+" + digitsOnly;
    }

    private void createDelivery() {
        try {
            // Field validation
            if (driverCombo.getSelectedItem() == null) {
                throw new IllegalArgumentException("Please select a driver");
            }
            if (vehicleCombo.getSelectedItem() == null) {
                throw new IllegalArgumentException("Please select a vehicle");
            }
            Object selectedClient = clientCombo.getSelectedItem();
            if (selectedClient == null || !(selectedClient instanceof Client)) {
                throw new IllegalArgumentException("Please select a valid client");
            }
            if (selectedPizzas.isEmpty()) {
                throw new IllegalArgumentException("Please add at least one pizza");
            }

            Client client = (Client) selectedClient;
            
            // Calculer le total de la commande
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (OrderPizza orderPizza : selectedPizzas) {
                if (!orderPizza.getIsFree()) {
                    totalAmount = totalAmount.add(orderPizza.getPizzaPrice().multiply(new BigDecimal(orderPizza.getQuantity())));
                }
            }

            // Vérifier si le client a assez d'argent
            if (client.getAmount().compareTo(totalAmount) < 0) {
                String message = String.format(
                    "Solde insuffisant. Solde actuel: %.2f€, Montant de la commande: %.2f€",
                    client.getAmount().doubleValue(),
                    totalAmount.doubleValue()
                );
                log.warn("Tentative de commande avec solde insuffisant pour le client {}: {}", 
                    client.getClientId(), message);
                JOptionPane.showMessageDialog(this,
                    message,
                    "Solde insuffisant",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            log.info("Starting order creation with {} pizzas", selectedPizzas.size());

            // Order creation
            Order order = new Order();
            order.setDriver((DeliveryDriver) driverCombo.getSelectedItem());
            order.setVehicle((Vehicle) vehicleCombo.getSelectedItem());
            order.setClient(client);
            order.setOrderStatus(OrderStatus.PENDING);

            // Adding pizzas to the order
            log.info("Adding {} pizzas to the order...", selectedPizzas.size());
            for (OrderPizza orderPizza : selectedPizzas) {
                log.info("Creating new OrderPizza for pizza: {} (size: {}, quantity: {})", 
                    orderPizza.getPizza().getPizzaName(),
                    orderPizza.getPizzaSize(),
                    orderPizza.getQuantity());

                OrderPizza newOrderPizza = new OrderPizza();
                newOrderPizza.setPizza(orderPizza.getPizza());
                newOrderPizza.setPizzaSize(orderPizza.getPizzaSize());
                newOrderPizza.setQuantity(orderPizza.getQuantity());
                newOrderPizza.setPizzaPrice(orderPizza.getPizzaPrice());
                newOrderPizza.setIsFree(orderPizza.getIsFree());
                newOrderPizza.setOrder(order);
                order.addOrderItem(newOrderPizza);
            }

            // Save order with all pizzas
            log.info("Saving order with pizzas...");
            Order savedOrder = orderService.save(order);
            log.info("Order saved with ID: {} and {} pizzas", 
                savedOrder.getOrderId(), savedOrder.getOrderItems().size());

            // Débiter le compte du client
            try {
                // Calculer le nouveau solde avec 2 décimales
                BigDecimal newAmount = client.getAmount().subtract(totalAmount).setScale(2, BigDecimal.ROUND_HALF_UP);
                
                // Mettre à jour uniquement le solde via le service
                clientService.updateAmount(client.getClientId(), newAmount);

                log.info("Client {} account debited of {}€. New balance: {}€", 
                    client.getClientId(), 
                    totalAmount.doubleValue(),
                    newAmount.doubleValue());
            } catch (Exception e) {
                log.error("Error while debiting client account", e);
                JOptionPane.showMessageDialog(this,
                    "Erreur lors du débit du compte client. Veuillez contacter l'administrateur.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Final verification
            int expectedTotalPizzas = selectedPizzas.stream()
                .mapToInt(OrderPizza::getQuantity)
                .sum();
            int actualTotalPizzas = savedOrder.getOrderItems().stream()
                .mapToInt(OrderPizza::getQuantity)
                .sum();

            if (actualTotalPizzas != expectedTotalPizzas) {
                log.error("Incorrect number of pizzas in saved order. Expected: {}, Got: {}", 
                    expectedTotalPizzas, actualTotalPizzas);
                log.error("Expected pizzas details:");
                for (OrderPizza pizza : selectedPizzas) {
                    log.error("- {} (size: {}, quantity: {})", 
                        pizza.getPizza().getPizzaName(),
                        pizza.getPizzaSize(),
                        pizza.getQuantity());
                }
                log.error("Saved pizzas details:");
                for (OrderPizza pizza : savedOrder.getOrderItems()) {
                    log.error("- {} (size: {}, quantity: {}, isFree: {})", 
                        pizza.getPizza().getPizzaName(),
                        pizza.getPizzaSize(),
                        pizza.getQuantity(),
                        pizza.getIsFree());
                }
                throw new RuntimeException("Error while saving pizzas");
            }

            // Refresh orders list
            loadOrders();

            // Reset form
            driverCombo.setSelectedIndex(0);
            vehicleCombo.setSelectedIndex(0);
            clientCombo.setSelectedIndex(0);
            selectedPizzas.clear();
            updateSelectedPizzasPanel();

            JOptionPane.showMessageDialog(this,
                String.format("Commande créée avec succès ! Montant débité : %.2f€", totalAmount.doubleValue()),
                "Succès",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            log.error("Error during delivery creation", e);
            JOptionPane.showMessageDialog(this,
                "Erreur lors de la création de la livraison: " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createOrdersContainer() {
        JPanel ordersContainer = createCardPanel();
        ordersContainer.setLayout(new BorderLayout());
        ordersContainer.setPreferredSize(new Dimension(570, 700));

        JLabel titleOrders = createTitle("Commandes en cours");
        ordersContainer.add(titleOrders, BorderLayout.NORTH);

        // Panel pour la liste des commandes avec scroll
        ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        ordersPanel.setOpaque(true);
        ordersPanel.setBackground(Color.WHITE);

        // Ajout du scroll
        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        ordersContainer.add(scrollPane, BorderLayout.CENTER);
        return ordersContainer;
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

    public void loadOrders() {
        if (orderService == null) {
            log.error("OrderService is null, cannot load orders");
            showNoOrdersMessage("Service unavailable");
            return;
        }
        
        if (ordersPanel == null) {
            log.error("OrdersPanel is null, cannot load orders");
            return;
        }
        
        try {
            log.info("Loading orders...");
            ordersPanel.removeAll();
            
            List<Order> orders = orderService.findActiveOrders();
            log.info("Number of orders found: {}", orders.size());

            if (orders.isEmpty()) {
                showNoOrdersMessage("No active orders");
            } else {
                for (Order order : orders) {
                    JPanel orderCard = createOrderCard(order);
                    JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    wrapper.setOpaque(false);
                    wrapper.add(orderCard);
                    ordersPanel.add(wrapper);
                    ordersPanel.add(Box.createVerticalStrut(10));
                }
            }

            ordersPanel.revalidate();
            ordersPanel.repaint();
            
            // Force refresh
            SwingUtilities.invokeLater(() -> {
                revalidate();
                repaint();
            });
            
            log.info("Orders loading completed");
        } catch (Exception e) {
            log.error("Error while loading orders", e);
            showNoOrdersMessage("Loading error: " + e.getMessage());
        }
    }

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

    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
        card.setPreferredSize(new Dimension(500, 200));
        card.setBackground(Color.WHITE);
        card.setOpaque(true);

        // En-tête de la commande
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setOpaque(true);
        
        JPanel leftHeader = new JPanel();
        leftHeader.setLayout(new BoxLayout(leftHeader, BoxLayout.Y_AXIS));
        leftHeader.setBackground(Color.WHITE);
        leftHeader.setOpaque(true);
        
        JLabel orderIdLabel = new JLabel("Commande #" + order.getOrderId());
        orderIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        leftHeader.add(orderIdLabel);
        
        // Ajout de la date de création
        if (order.getOrderDate() != null) {
            JLabel dateLabel = new JLabel("Créée le " + order.getOrderDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
            dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            dateLabel.setForeground(Color.GRAY);
            leftHeader.add(dateLabel);
        }
        
        headerPanel.add(leftHeader, BorderLayout.WEST);

        JLabel statusLabel = new JLabel(order.getOrderStatus().toString());
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(order.getOrderStatus() == OrderStatus.PENDING ? Color.ORANGE : Color.BLUE);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        card.add(headerPanel);
        card.add(Box.createVerticalStrut(10));

        // Détails de la commande
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setOpaque(true);

        // Client (avec vérification null)
        if (order.getClient() != null) {
            String clientName = (order.getClient().getFirstName() != null ? order.getClient().getFirstName() : "") + 
                              " " + (order.getClient().getLastName() != null ? order.getClient().getLastName() : "");
            JLabel clientLabel = new JLabel("Client: " + clientName.trim());
            clientLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            detailsPanel.add(clientLabel);

            // Adresse
            if (order.getClient().getClientAddress() != null) {
                JLabel addressLabel = new JLabel("Adresse: " + order.getClient().getClientAddress());
                addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                detailsPanel.add(addressLabel);
            }
        }

        // Détails des pizzas
        try {
            // Agréger les pizzas identiques
            Map<String, OrderPizza> aggregatedPizzas = new HashMap<>();
            List<OrderPizza> freePizzas = new ArrayList<>();

            for (OrderPizza orderPizza : order.getOrderItems()) {
                if (orderPizza.getIsFree()) {
                    freePizzas.add(orderPizza);
                } else {
                    String key = orderPizza.getPizza().getPizzaName() + "_" + orderPizza.getPizzaSize();
                    if (aggregatedPizzas.containsKey(key)) {
                        OrderPizza existing = aggregatedPizzas.get(key);
                        existing.setQuantity(existing.getQuantity() + orderPizza.getQuantity());
                    } else {
                        aggregatedPizzas.put(key, orderPizza);
                    }
                }
            }

            // Afficher les pizzas payantes
            for (OrderPizza orderPizza : aggregatedPizzas.values()) {
                String pizzaText = String.format("x%d %s (%s) - %.2f€",
                    orderPizza.getQuantity(),
                    orderPizza.getPizza().getPizzaName(),
                    orderPizza.getPizzaSize().getDisplayName(),
                    orderPizza.getPizzaPrice().multiply(new BigDecimal(orderPizza.getQuantity())));
                
                JLabel pizzaLabel = new JLabel(pizzaText);
                pizzaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                detailsPanel.add(pizzaLabel);
            }

            // Afficher les pizzas gratuites
            for (OrderPizza orderPizza : freePizzas) {
                String pizzaText = String.format("x%d %s (%s) OFFERTE 🎁",
                    orderPizza.getQuantity(),
                    orderPizza.getPizza().getPizzaName(),
                    orderPizza.getPizzaSize().getDisplayName());
                
                JLabel pizzaLabel = new JLabel(pizzaText);
                pizzaLabel.setFont(new Font("Arial", Font.BOLD, 14));
                pizzaLabel.setForeground(new Color(0, 128, 0)); // Vert foncé
                detailsPanel.add(pizzaLabel);
            }

        } catch (Exception e) {
            log.warn("Erreur lors du formatage des détails de commande", e);
            JLabel pizzasLabel = new JLabel("Pizzas: Détails non disponibles");
            pizzasLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            detailsPanel.add(pizzasLabel);
        }

        // Total
        try {
            double total = orderService.calculateOrderTotal(order);
            JLabel totalLabel = new JLabel(String.format("Total: %.2f €", total));
            totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
            detailsPanel.add(totalLabel);

            // Afficher le remboursement si la commande est en retard
            if (order.getFreeReason() == FreeReason.LATE_DELIVERY) {
                JLabel refundLabel = new JLabel("Commande remboursée (Retard de livraison)");
                refundLabel.setFont(new Font("Arial", Font.BOLD, 14));
                refundLabel.setForeground(new Color(0, 128, 0)); // Vert foncé
                detailsPanel.add(refundLabel);
            }
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du total", e);
            JLabel totalLabel = new JLabel("Total: Non calculable");
            totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
            detailsPanel.add(totalLabel);
        }

        // Temps de livraison
        try {
            String deliveryTime = orderService.calculateDeliveryTime(order);
            JLabel deliveryTimeLabel = new JLabel("Temps estimé: " + deliveryTime);
            deliveryTimeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            detailsPanel.add(deliveryTimeLabel);
        } catch (Exception e) {
            log.warn("Erreur lors du calcul du temps de livraison", e);
        }

        // Indicateur de retard
        try {
            if (orderService.isLateDelivery(order)) {
                JLabel lateLabel = new JLabel("Commande en retard");
                lateLabel.setFont(new Font("Arial", Font.BOLD, 14));
                lateLabel.setForeground(Color.RED);
                detailsPanel.add(lateLabel);
            }
        } catch (Exception e) {
            log.warn("Erreur lors de la vérification du retard", e);
        }

        card.add(detailsPanel);

        // Boutons d'action
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setOpaque(true);

        if (order.getOrderStatus() == OrderStatus.PENDING) {
            JButton startButton = new JButton("Démarrer la livraison");
            startButton.addActionListener(e -> {
                try {
                    orderService.updateStatus(order.getOrderId(), OrderStatus.IN_PROGRESS);
                    loadOrders();
                } catch (Exception ex) {
                    log.error("Erreur lors de la mise à jour du statut", ex);
                }
            });
            buttonPanel.add(startButton);
        } else if (order.getOrderStatus() == OrderStatus.IN_PROGRESS) {
            JButton completeButton = new JButton("Terminer la livraison");
            completeButton.addActionListener(e -> {
                try {
                    orderService.updateStatus(order.getOrderId(), OrderStatus.DELIVERED);
                    loadOrders();
                } catch (Exception ex) {
                    log.error("Erreur lors de la mise à jour du statut", ex);
                }
            });
            buttonPanel.add(completeButton);
        }

        card.add(Box.createVerticalStrut(10));
        card.add(buttonPanel);

        return card;
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

    private JPanel createLabeledTextField(String labelText, JTextField field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(5, 5));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(200, 30));
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        return panel;
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
}
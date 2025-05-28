package fr.rapizz.view.panels;

import fr.rapizz.model.OrderStatus;
import fr.rapizz.service.StatisticsService;
import fr.rapizz.view.theme.AppTheme;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

/**
 * Panel that displays business statistics.
 */
@Slf4j
public class StatisticsPanel extends JPanel {
    private final StatisticsService statisticsService;
    
    // Filter components
    private JComboBox<String> periodComboBox;
    private JButton refreshButton;
    
    // KPI components
    private JLabel totalRevenueLabel;
    private JLabel totalOrdersLabel;
    private JLabel avgOrderValueLabel;
    private JLabel topDriverLabel;
    
    // Chart panels
    private ChartPanel pizzaPopularityChart;
    private ChartPanel orderStatusChart;
    private ChartPanel revenueTimeChart;
    
    /**
     * Creates a new statistics panel
     * @param statisticsService The statistics service
     */
    public StatisticsPanel(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
        
        setupPanel();
        createComponents();
        layoutComponents();
        loadStatistics();
    }
    
    /**
     * Sets up the panel's basic properties.
     */
    private void setupPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(AppTheme.BACKGROUND);
    }
    
    /**
     * Creates all UI components.
     */
    private void createComponents() {
        createFilterComponents();
        createKpiComponents();
        createChartComponents();
    }
    
    /**
     * Creates filter control components.
     */
    private void createFilterComponents() {
        String[] periods = {"Aujourd'hui", "Cette semaine", "Ce mois", "Cette année", "Tout"};
        periodComboBox = new JComboBox<>(periods);
        periodComboBox.setPreferredSize(new Dimension(150, 35));
        periodComboBox.setFont(AppTheme.MENU_ITEM);
        
        refreshButton = new JButton("Actualiser");
        AppTheme.styleButton(refreshButton, "Actualiser", AppTheme.INFO_COLOR);
        refreshButton.addActionListener(e -> loadStatistics());
    }
    
    /**
     * Creates KPI display components.
     */
    private void createKpiComponents() {
        totalRevenueLabel = new JLabel("0.00 €");
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalRevenueLabel.setForeground(AppTheme.SUCCESS_COLOR);
        
        totalOrdersLabel = new JLabel("0");
        totalOrdersLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalOrdersLabel.setForeground(AppTheme.INFO_COLOR);
        
        avgOrderValueLabel = new JLabel("0.00 €");
        avgOrderValueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        avgOrderValueLabel.setForeground(AppTheme.PRIMARY);
        
        topDriverLabel = new JLabel("Aucun");
        topDriverLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topDriverLabel.setForeground(AppTheme.TEXT_PRIMARY);
    }
    
    /**
     * Creates chart components with placeholder data.
     */
    private void createChartComponents() {
        createPizzaPopularityChart(new DefaultPieDataset<>());
        createOrderStatusChart(new DefaultPieDataset<>());
        createRevenueTimeChart(new DefaultCategoryDataset());
    }
    
    /**
     * Lays out all components.
     */
    private void layoutComponents() {
        // Header fixe en haut (comme titleLabel dans MenuPanel)
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);

        // Content scrollable (comme MenuPanel)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(AppTheme.BACKGROUND);

        // Créer les sections (sans le titlePanel qui est maintenant fixe)
        JPanel kpiPanel = createKpiPanel();
        JPanel chartsPanel = createChartsPanel();

        // Ajouter les sections avec espacement
        contentPanel.add(kpiPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(chartsPanel);

        // ScrollPane (comme MenuPanel)
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(AppTheme.BACKGROUND);

        // Ajouter au CENTER (comme MenuPanel)
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the main panel.
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(AppTheme.BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        return mainPanel;
    }
    
    /**
     * Creates the title panel with search field.
     */
    protected JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setBorder(new EmptyBorder(10, 0, 5, 0));
        
        // Title
        JLabel titleLabel = new JLabel("Statistiques de l'Activité");
        titleLabel.setFont(AppTheme.TITLE);
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        filterPanel.setOpaque(false);
        
        JLabel periodLabel = new JLabel("Période:");
        periodLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        periodLabel.setForeground(AppTheme.TEXT_PRIMARY);
        
        filterPanel.add(periodLabel);
        filterPanel.add(periodComboBox);
        filterPanel.add(refreshButton);
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(filterPanel, BorderLayout.CENTER);
        
        return titlePanel;
    }
    
    /**
     * Creates the KPI metrics panel.
     */
    private JPanel createKpiPanel() {
        JPanel kpiContainer = new JPanel(new GridLayout(1, 3, 20, 0));
        kpiContainer.setOpaque(false);
        kpiContainer.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        kpiContainer.add(createKpiCard("Chiffre d'Affaires", "💰", totalRevenueLabel, AppTheme.SUCCESS_COLOR));
        kpiContainer.add(createKpiCard("Commandes", "📋", totalOrdersLabel, AppTheme.INFO_COLOR));
        kpiContainer.add(createKpiCard("Valeur Moyenne", "📊", avgOrderValueLabel, AppTheme.PRIMARY));
        
        return kpiContainer;
    }
    
    /**
     * Creates individual KPI card.
     */
    private JPanel createKpiCard(String title, String icon, JLabel valueLabel, Color accentColor) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(10, 10));
        
        // Header with icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        headerPanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);
        
        // Value panel
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setOpaque(false);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valuePanel.add(valueLabel);
        
        // Accent border (like styled buttons in management panels)
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        ));
        
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates the charts panel.
     */
    private JPanel createChartsPanel() {
        JPanel chartsContainer = new JPanel(new GridLayout(2, 2, 15, 15));
        chartsContainer.setOpaque(false);
        
        // Pizza popularity chart
        JPanel pizzaPanel = createChartCard("Pizzas les Plus Populaires", pizzaPopularityChart);
        chartsContainer.add(pizzaPanel);
        
        // Order status chart
        JPanel statusPanel = createChartCard("Statut des Commandes", orderStatusChart);
        chartsContainer.add(statusPanel);
        
        // Revenue time chart
        JPanel revenuePanel = createChartCard("Évolution du Chiffre d'Affaires", revenueTimeChart);
        chartsContainer.add(revenuePanel);
        
        // Top driver panel
        JPanel driverPanel = createTopDriverCard();
        chartsContainer.add(driverPanel);
        
        return chartsContainer;
    }
    
    /**
     * Creates individual chart card.
     */
    private JPanel createChartCard(String title, ChartPanel chartPanel) {
        JPanel card = createCard();
        card.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(chartPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates the top driver card.
     */
    private JPanel createTopDriverCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Meilleur Livreur");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(AppTheme.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel tropheeLabel = new JLabel();
        tropheeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            ImageIcon tropheeIcon = new ImageIcon(Objects.requireNonNull(
                    getClass().getClassLoader().getResource("images/trophee.png")));
            Image scaledImage = tropheeIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            tropheeLabel.setIcon(new ImageIcon(scaledImage));
        } catch (Exception e) {
            log.warn("Failed to load medal icon", e);
            tropheeLabel.setText("🏆");
            tropheeLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        }
        
        JLabel subtitleLabel = new JLabel("Livreur du mois");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLabel.setForeground(AppTheme.TEXT_SECONDARY);
        
        topDriverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(Box.createVerticalGlue());
        contentPanel.add(tropheeLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(subtitleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(topDriverLabel);
        contentPanel.add(Box.createVerticalGlue());
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    /**
     * Creates a styled panel with title.
     */
    protected JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return card;
    }
    
    /**
     * Creates the pizza popularity chart.
     */
    private void createPizzaPopularityChart(DefaultPieDataset<String> dataset) {
        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);
        
        pizzaPopularityChart = new ChartPanel(chart);
        pizzaPopularityChart.setBackground(Color.WHITE);
        pizzaPopularityChart.setPreferredSize(new Dimension(280, 200));
    }
    
    /**
     * Creates the order status chart.
     */
    private void createOrderStatusChart(DefaultPieDataset<String> dataset) {
        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);
        
        orderStatusChart = new ChartPanel(chart);
        orderStatusChart.setBackground(Color.WHITE);
        orderStatusChart.setPreferredSize(new Dimension(280, 200));
    }
    
    /**
     * Creates the revenue time chart.
     */
    private void createRevenueTimeChart(DefaultCategoryDataset dataset) {
        JFreeChart chart = ChartFactory.createLineChart(
                null, "Période", "CA (€)", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        
        revenueTimeChart = new ChartPanel(chart);
        revenueTimeChart.setBackground(Color.WHITE);
        revenueTimeChart.setPreferredSize(new Dimension(280, 200));
    }
    
    /**
     * Loads entities from the controller into the table.
     */
    protected void loadStatistics() {
        try {
            String selectedPeriod = Objects.requireNonNullElse(
                    (String) periodComboBox.getSelectedItem(), "Tout");
            LocalDate startDate = calculateStartDateFromPeriod(selectedPeriod);
            
            // Load KPI data
            BigDecimal totalRevenue = statisticsService.calculateTotalRevenue(startDate);
            int totalOrders = statisticsService.countOrders(startDate);
            BigDecimal avgOrderValue = totalOrders > 0 ?
                    totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP) :
                    BigDecimal.ZERO;
            String topDriver = statisticsService.findTopDriver(startDate);
            
            // Update KPI labels
            totalRevenueLabel.setText(totalRevenue + " €");
            totalOrdersLabel.setText(String.valueOf(totalOrders));
            avgOrderValueLabel.setText(avgOrderValue + " €");
            topDriverLabel.setText(topDriver != null ? topDriver : "Aucun");
            
            // Update charts
            updatePizzaPopularityChart(startDate);
            updateOrderStatusChart(startDate);
            updateRevenueTimeChart(startDate);
            
        } catch (Exception e) {
            showErrorMessage("Erreur lors du chargement des statistiques: " + e.getMessage());
        }
    }
    
    /**
     * Updates pizza popularity chart.
     */
    private void updatePizzaPopularityChart(LocalDate startDate) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Integer> pizzaCounts = statisticsService.getMostPopularPizzas(startDate, 5);
        
        for (Map.Entry<String, Integer> entry : pizzaCounts.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);
        
        pizzaPopularityChart.setChart(chart);
    }
    
    /**
     * Updates order status chart.
     */
    private void updateOrderStatusChart(LocalDate startDate) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<OrderStatus, Integer> statusCounts = statisticsService.getOrderStatusCounts(startDate);
        
        for (Map.Entry<OrderStatus, Integer> entry : statusCounts.entrySet()) {
            dataset.setValue(entry.getKey().getDisplayName(), entry.getValue());
        }
        
        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);
        
        orderStatusChart.setChart(chart);
    }
    
    /**
     * Updates revenue time chart.
     */
    private void updateRevenueTimeChart(LocalDate startDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        Map<String, BigDecimal> revenueByPeriod = statisticsService.getRevenueByTimePeriod(startDate);
        
        for (Map.Entry<String, BigDecimal> entry : revenueByPeriod.entrySet()) {
            dataset.addValue(entry.getValue(), "Chiffre d'affaires", entry.getKey());
        }
        
        JFreeChart chart = ChartFactory.createLineChart(
                null, "Période", "Chiffre d'affaires (€)", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        
        chart.setBackgroundPaint(Color.WHITE);
        chart.setBorderVisible(false);
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        
        revenueTimeChart.setChart(chart);
    }
    
    /**
     * Calculates start date from selected period.
     */
    private LocalDate calculateStartDateFromPeriod(String period) {
        LocalDate now = LocalDate.now();
        
        return switch (period) {
            case "Aujourd'hui" -> now;
            case "Cette semaine" -> now.minusDays(now.getDayOfWeek().getValue() - 1);
            case "Ce mois" -> now.withDayOfMonth(1);
            case "Cette année" -> now.withDayOfYear(1);
            default -> LocalDate.of(2000, 1, 1); // "Tout"
        };
    }
    
    /**
     * Shows an error message dialog.
     */
    protected void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
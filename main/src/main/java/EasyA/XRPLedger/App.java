package EasyA.XRPLedger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App extends JFrame {
    
    public App() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("ACME Asset Manager");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Main layout
        setLayout(new BorderLayout());
        
        // Header
        add(createHeader(), BorderLayout.NORTH);
        
        // Main content
        add(createMainContent(), BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(44, 90, 160)); // #2c5aa0
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Title
        JLabel titleLabel = new JLabel("ACME Asset Manager");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Right side buttons
        JPanel rightPanel = new JPanel(new FlowLayout());
        rightPanel.setOpaque(false);
        
        JButton settingsBtn = createHeaderButton("⚙");
        JButton dollarBtn = createHeaderButton("$");
        JButton helpBtn = createHeaderButton("?");
        
        rightPanel.add(settingsBtn);
        rightPanel.add(dollarBtn);
        rightPanel.add(helpBtn);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        
        // Navigation tabs
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        String[] tabs = {"Home", "Portfolio", "Trading", "Assets", "Reports"};
        for (int i = 0; i < tabs.length; i++) {
            JButton tab = createNavTab(tabs[i], i == 0);
            navPanel.add(tab);
        }
        
        JPanel headerContainer = new JPanel(new BorderLayout());
        headerContainer.add(headerPanel, BorderLayout.CENTER);
        headerContainer.add(navPanel, BorderLayout.SOUTH);
        
        return headerContainer;
    }
    
    private JButton createHeaderButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setBackground(new Color(74, 144, 226)); // #4a90e2
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setFocusPainted(false);
        return btn;
    }
    
    private JButton createNavTab(String text, boolean active) {
        JButton tab = new JButton(text);
        tab.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        tab.setFocusPainted(false);
        
        if (active) {
            tab.setBackground(new Color(74, 144, 226)); // #4a90e2
            tab.setForeground(Color.WHITE);
        } else {
            tab.setBackground(Color.WHITE);
            tab.setForeground(new Color(102, 102, 102));
        }
        
        return tab;
    }
    
    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245)); // #f5f5f5
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Market Overview
        contentPanel.add(createMarketOverview());
        contentPanel.add(Box.createVerticalStrut(30));
        
        // Portfolio Section
        contentPanel.add(createPortfolioSection());
        
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        return mainPanel;
    }
    
    private JPanel createMarketOverview() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        
        // Section title
        JLabel title = new JLabel("Market Overview");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(74, 144, 226)));
        
        // Market cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsPanel.setOpaque(false);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        cardsPanel.add(createMarketCard("S&P 500", "4,005.44", "+0.45%", true));
        cardsPanel.add(createMarketCard("DOW Jones", "12,003.33", "-0.45%", false));
        cardsPanel.add(createMarketCard("NASDAQ", "101.25", "+0.45%", true));
        cardsPanel.add(createMarketCard("BITCOIN", "102,322.51", "-0.45%", false));
        
        section.add(title, BorderLayout.NORTH);
        section.add(cardsPanel, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createMarketCard(String name, String value, String change, boolean positive) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        nameLabel.setForeground(Color.GRAY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        changeLabel.setForeground(positive ? new Color(0, 150, 0) : Color.RED);
        changeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(changeLabel);
        
        return card;
    }
    
    private JPanel createPortfolioSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);
        
        // Section title
        JLabel title = new JLabel("Your Portfolio");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(74, 144, 226)));
        
        // Portfolio content
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Left side - BTC card and chart
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(350, 300));
        
        // BTC Card
        JPanel btcCard = createBTCCard();
        leftPanel.add(btcCard);
        leftPanel.add(Box.createVerticalStrut(15));
        
        // Simple chart representation
        JPanel chartPanel = createSimpleChart();
        leftPanel.add(chartPanel);
        
        // Right side - Portfolio summary and table
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        
        // Portfolio summary
        JPanel summaryPanel = createPortfolioSummary();
        rightPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Holdings table
        JScrollPane tableScrollPane = createHoldingsTable();
        rightPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(Box.createHorizontalStrut(30), BorderLayout.CENTER);
        contentPanel.add(rightPanel, BorderLayout.EAST);
        
        section.add(title, BorderLayout.NORTH);
        section.add(contentPanel, BorderLayout.CENTER);
        
        return section;
    }
    
    private JPanel createBTCCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(44, 62, 80)); // #2c3e50
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(350, 80));
        
        JPanel leftInfo = new JPanel();
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        leftInfo.setOpaque(false);
        
        JLabel symbolLabel = new JLabel("BTC/USD");
        symbolLabel.setForeground(Color.WHITE);
        symbolLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel valueLabel = new JLabel("$42,856.30");
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel changeLabel = new JLabel("+$1,184.52");
        changeLabel.setForeground(new Color(0, 200, 0));
        changeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        leftInfo.add(symbolLabel);
        leftInfo.add(valueLabel);
        leftInfo.add(changeLabel);
        
        JLabel percentLabel = new JLabel("+3.24%");
        percentLabel.setForeground(new Color(0, 200, 0));
        percentLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        card.add(leftInfo, BorderLayout.WEST);
        card.add(percentLabel, BorderLayout.EAST);
        
        return card;
    }
    
    private JPanel createSimpleChart() {
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int barWidth = width / 12;
                
                // Sample bar heights
                int[] heights = {60, 90, 80, 70, 100, 120, 95, 75, 130, 125};
                
                for (int i = 0; i < heights.length; i++) {
                    int barHeight = heights[i];
                    int x = i * (barWidth + 5) + 10;
                    int y = height - barHeight - 20;
                    
                    // Alternate colors (green/red)
                    g2d.setColor(i % 3 == 0 ? Color.RED : new Color(0, 150, 0));
                    g2d.fillRect(x, y, barWidth, barHeight);
                }
            }
        };
        
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        chartPanel.setPreferredSize(new Dimension(350, 180));
        chartPanel.setMaximumSize(new Dimension(350, 180));
        
        return chartPanel;
    }
    
    private JPanel createPortfolioSummary() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 2, 50, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        JPanel totalValue = new JPanel();
        totalValue.setLayout(new BoxLayout(totalValue, BoxLayout.Y_AXIS));
        totalValue.setOpaque(false);
        
        JLabel totalLabel = new JLabel("Total Value");
        totalLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        totalLabel.setForeground(Color.GRAY);
        
        JLabel totalAmount = new JLabel("12,003.33");
        totalAmount.setFont(new Font("Arial", Font.BOLD, 18));
        
        totalValue.add(totalLabel);
        totalValue.add(totalAmount);
        
        JPanel todayChange = new JPanel();
        todayChange.setLayout(new BoxLayout(todayChange, BoxLayout.Y_AXIS));
        todayChange.setOpaque(false);
        
        JLabel changeLabel = new JLabel("Today's Change");
        changeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        changeLabel.setForeground(Color.GRAY);
        
        JLabel changeAmount = new JLabel("12,003.33");
        changeAmount.setFont(new Font("Arial", Font.BOLD, 18));
        
        todayChange.add(changeLabel);
        todayChange.add(changeAmount);
        
        summaryPanel.add(totalValue);
        summaryPanel.add(todayChange);
        
        return summaryPanel;
    }
    
    private JScrollPane createHoldingsTable() {
        String[] columnNames = {"Symbol", "Shares", "Price", "Value", "Change"};
        Object[][] data = {
            {"AAPL", "50", "$185.20", "$9,260.00", "+2.1%"},
            {"MSFT", "25", "$380.45", "$9,511.25", "+1.8%"},
            {"VOO", "60", "$410.20", "$24,612.00", "+0.5%"}
        };
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        
        // Custom renderer for the Change column to show colors
        table.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setOpaque(true);
                
                String changeValue = value.toString();
                if (changeValue.startsWith("+")) {
                    label.setForeground(new Color(0, 150, 0));
                } else if (changeValue.startsWith("-")) {
                    label.setForeground(Color.RED);
                }
                
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                } else {
                    label.setBackground(Color.WHITE);
                }
                
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        return scrollPane;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new App();
            }
        });
    }
}
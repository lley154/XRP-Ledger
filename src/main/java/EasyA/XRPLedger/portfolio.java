package EasyA.XRPLedger;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.*;

public class portfolio extends JFrame {
    // Color scheme
    private static final Color DARK_BG = new Color(28, 33, 53);
    private static final Color DARKER_BG = new Color(20, 24, 38);
    private static final Color CARD_BG = new Color(35, 41, 70);
    private static final Color ACCENT_BLUE = new Color(100, 149, 237);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color DIVIDER_COLOR = new Color(55, 65, 100);
    
    // Allocation colors
    private static final Color[] PIE_COLORS = {
        new Color(255, 99, 132),  // Red
        new Color(54, 162, 235),  // Blue
        new Color(255, 206, 86),  // Yellow
        new Color(75, 192, 192)   // Teal
    };
    
    // Add the createTable() helper method HERE:
    private JTable createTable(String[] headers, Object[][] data) {
        JTable table = new JTable(data, headers) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setForeground(TEXT_PRIMARY);
        table.setBackground(CARD_BG);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(DARKER_BG);
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER_COLOR));
        
        // Cell styling
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setForeground(TEXT_PRIMARY);
        centerRenderer.setBackground(CARD_BG);
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setForeground(TEXT_PRIMARY);
        leftRenderer.setBackground(CARD_BG);
        
        table.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        return table;
    }

    public portfolio() {
        setTitle("ACME Inc. - Portfolio Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        // Main container with sidebar and content
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(DARK_BG);
        
        // Add sidebar with sub-headings
        container.add(createSidebar(), BorderLayout.WEST);
        
        // Main content area
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(DARK_BG);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Create scrollable content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Top section with performance and allocations
        JPanel topSection = new JPanel(new GridLayout(1, 2, 30, 0));
        topSection.setBackground(DARK_BG);
        topSection.add(createPerformanceSection());
        topSection.add(createAdditionsSection());
        contentPanel.add(topSection);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        
        // Holdings and Transfers sections
        contentPanel.add(createHoldingsAndTransferSection());
        
        container.add(scrollPane, BorderLayout.CENTER);
        add(container);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBackground(DARKER_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 25, 30, 25));
        
        // Logo
        JLabel logo = new JLabel("ACME Inc.");
        logo.setFont(new Font("Arial", Font.BOLD, 26));
        logo.setForeground(TEXT_PRIMARY);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 50)));
        
        // Main sections with sub-headings
        sidebar.add(createMenuHeading("DASHBOARD"));
        String[] dashboardItems = {"Home", "Portfolio", "Performance"};
        for (String item : dashboardItems) {
            sidebar.add(createMenuItem(item, item.equals("Portfolio")));
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(createMenuHeading("ASSETS"));
        String[] assetItems = {"Allocations", "Holdings"};
        for (String item : assetItems) {
            sidebar.add(createMenuItem(item, false));
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        sidebar.add(createMenuHeading("ACTIONS"));
        String[] actionItems = {"Transfer", "Trading"};
        for (String item : actionItems) {
            sidebar.add(createMenuItem(item, false));
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        // Settings section
        sidebar.add(createMenuHeading("SETTINGS"));
        String[] settingsItems = {"Reports", "Settings"};
        for (String item : settingsItems) {
            sidebar.add(createMenuItem(item, false));
            sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        
        return sidebar;
    }
    
    private JPanel createMenuHeading(String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.setBackground(DARKER_BG);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(TEXT_SECONDARY);
        
        panel.add(label);
        return panel;
    }
    
    private JPanel createMenuItem(String text, boolean active) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        panel.setBackground(active ? CARD_BG : DARKER_BG);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (active) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_BLUE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else {
            panel.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        }
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        textLabel.setForeground(active ? TEXT_PRIMARY : TEXT_SECONDARY);
        
        panel.add(textLabel);
        return panel;
    }

    private JPanel createPerformanceSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER_COLOR, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Portfolio Performance (SUSD)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        // Value panel
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        valuePanel.setBackground(CARD_BG);
        valuePanel.setBorder(new EmptyBorder(15, 0, 30, 0));
        
        JLabel valueLabel = new JLabel("Value $ 1,347.50");
        valueLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        valueLabel.setForeground(TEXT_PRIMARY);
        valuePanel.add(valueLabel);
        
        // Line graph with updated gradient
        JPanel graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Updated gradient background (more blue)
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 50, 100), 
                    getWidth(), getHeight(), new Color(10, 20, 50)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // White line graph
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(3));
                
                int[] xPoints = {50, 150, 250, 350, 450, 550, 650};
                int[] yPoints = {150, 120, 130, 110, 125, 115, 120};
                
                // Draw line
                for (int i = 0; i < xPoints.length - 1; i++) {
                    g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                }
                
                // Draw points
                g2d.setColor(Color.WHITE);
                for (int i = 0; i < xPoints.length; i++) {
                    g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
                }
                
                // Date labels
                g2d.setColor(TEXT_SECONDARY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                String[] dates = {"06/01", "06/02", "06/03", "06/04", "06/05"};
                for (int i = 0; i < dates.length; i++) {
                    g2d.drawString(dates[i], xPoints[i] - 15, 180);
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(700, 200);
            }
        };
        
        panel.add(titleLabel);
        panel.add(valuePanel);
        panel.add(graphPanel);
        
        return panel;
    }

    private JPanel createAdditionsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER_COLOR, 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Title
        JLabel titleLabel = new JLabel("Additions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_BG);
        
        // Allocation title
        JLabel allocationLabel = new JLabel("Allocation");
        allocationLabel.setFont(new Font("Arial", Font.BOLD, 16));
        allocationLabel.setForeground(ACCENT_BLUE);
        
        // Transparent pie chart
        JPanel pieChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int diameter = Math.min(getWidth(), getHeight()) - 40;
                int x = (getWidth() - diameter) / 2;
                int y = (getHeight() - diameter) / 2;
                
                // Pie chart segments (GOLD 20%, SILVER 15%, etc.)
                double[] values = {20, 15, 40, 25}; // Example values
                double total = 100;
                int startAngle = 0;
                
                for (int i = 0; i < values.length; i++) {
                    int arcAngle = (int) Math.round(values[i] / total * 360);
                    g2d.setColor(PIE_COLORS[i]);
                    g2d.fillArc(x, y, diameter, diameter, startAngle, arcAngle);
                    startAngle += arcAngle;
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(200, 200);
            }
        };
        pieChartPanel.setOpaque(false); // Make pie chart background transparent
        
        // Allocation list
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(CARD_BG);
        listPanel.setBorder(new EmptyBorder(20, 40, 0, 0));
        
        String[] allocations = {
            "GOLD    20.0%",
            "SILVER    15.0%",
            "RLUSD    40.0%",
            "USTSYR    25.0%"
        };
        
        for (int i = 0; i < allocations.length; i++) {
            JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            itemPanel.setBackground(CARD_BG);
            
            // Color indicator
            JLabel colorLabel = new JLabel("■");
            colorLabel.setFont(new Font("Arial", Font.BOLD, 20));
            colorLabel.setForeground(PIE_COLORS[i]);
            
            // Allocation text
            JLabel textLabel = new JLabel(allocations[i]);
            textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            textLabel.setForeground(TEXT_PRIMARY);
            
            itemPanel.add(colorLabel);
            itemPanel.add(textLabel);
            listPanel.add(itemPanel);
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(CARD_BG);
        chartPanel.add(allocationLabel, BorderLayout.NORTH);
        chartPanel.add(pieChartPanel, BorderLayout.CENTER);
        
        contentPanel.add(chartPanel, BorderLayout.WEST);
        contentPanel.add(listPanel, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

 private JPanel createHoldingsAndTransferSection() {
    // Main panel to hold both sections horizontally
    JPanel mainPanel = new JPanel(new GridLayout(1, 2, 30, 0));
    mainPanel.setBackground(DARK_BG);
    mainPanel.setBorder(new EmptyBorder(0, 0, 40, 0));

    // Holdings panel
    JPanel holdingsPanel = createTableSection("Holdings", 
        new Object[][]{
            {"nHBxELyOppyde...", "RLUSD", "1000.0"},
            {"nHBPvhwv9lvrUCr...", "UST3YR", "0.05"},
            {"nHBJBGBfyrWzqo...", "GOLD", "2.5"},
            {"nHBJBGBfyrWzqo...", "SILVER", "50.0"}
        });

    // Transfer panel
    JPanel transferPanel = new JPanel(new BorderLayout());
    transferPanel.setBackground(CARD_BG);
    transferPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(DIVIDER_COLOR, 1),
        BorderFactory.createEmptyBorder(25, 25, 25, 25)
    ));

    JLabel transferTitle = new JLabel("Transfer");
    transferTitle.setFont(new Font("Arial", Font.BOLD, 20));
    transferTitle.setForeground(TEXT_PRIMARY);
    transferTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
    transferPanel.add(transferTitle, BorderLayout.NORTH);

    // Transfer content
    JPanel transferContent = new JPanel(new BorderLayout());
    transferContent.setBackground(CARD_BG);

    // Transfer table
    JPanel transferTablePanel = createTableSection("", 
        new Object[][]{
            {"nHBxELyOppyde...", "RLUSD", "1000.0"},
            {"nHBxELyOppyde...", "RLUSD", "1000.0"}
        });
    transferContent.add(transferTablePanel, BorderLayout.NORTH);

    // Transfer form
    JPanel formPanel = createTransferForm();
    transferContent.add(formPanel, BorderLayout.CENTER);
    transferPanel.add(transferContent, BorderLayout.CENTER);

    mainPanel.add(holdingsPanel);
    mainPanel.add(transferPanel);
    return mainPanel;
}

// Helper method to create table sections
private JPanel createTableSection(String title, Object[][] data) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBackground(CARD_BG);
    panel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(DIVIDER_COLOR, 1),
        BorderFactory.createEmptyBorder(25, 25, 25, 25)
    ));

    if (!title.isEmpty()) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
    }

    JTable table = createTable(new String[]{"Issuer", "Symbol", "Quantity"}, data);
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(null);
    panel.add(scrollPane, BorderLayout.CENTER);
    return panel;
}

// Helper method to create transfer form
private JPanel createTransferForm() {
    JPanel formPanel = new JPanel();
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
    formPanel.setBackground(CARD_BG);
    formPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

    String[] fields = {"From", "To", "Quantity"};
    String[] values = {"", "", "1000.0"};
    
    for (int i = 0; i < fields.length; i++) {
        JLabel label = new JLabel(fields[i]);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setBorder(new EmptyBorder(10, 0, 5, 0));

        JTextField textField = new JTextField(values[i]);
        textField.setMaximumSize(new Dimension(300, 30));
        textField.setBackground(DARKER_BG);
        textField.setForeground(TEXT_PRIMARY);
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DIVIDER_COLOR, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        formPanel.add(label);
        formPanel.add(textField);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));
    }
    return formPanel;
}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            portfolio dashboard = new portfolio();
            dashboard.setVisible(true);
        });
    }
}
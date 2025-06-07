package EasyA.XRPLedger;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class home extends JFrame {
    // Color scheme
    private static final Color DARK_BG = new Color(28, 33, 53);
    private static final Color DARKER_BG = new Color(20, 24, 38);
    private static final Color CARD_BG = new Color(35, 41, 70);
    private static final Color ACCENT_BLUE = new Color(100, 149, 237);
    private static final Color GREEN = new Color(34, 197, 94);
    private static final Color RED = new Color(239, 68, 68);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);
    private static final Color PURPLE = new Color(147, 51, 234);
    
    private JPanel sidePanel;
    private JPanel mainPanel;
    private JPanel topBar;
    
    public home() {
        setTitle("ACME Inc. - Financial Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1600, 900);
        setLocationRelativeTo(null);
        
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Main container
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(DARK_BG);
        
        // Create components
        createSidePanel();
        createMainPanel();
        
        // Add to container
        container.add(sidePanel, BorderLayout.WEST);
        container.add(mainPanel, BorderLayout.CENTER);
        
        add(container);
    }
    
    private ImageIcon loadIcon(String path, int width, int height) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            Image scaled = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (IOException e) {
            System.err.println("Could not load icon: " + path);
            return null;
        }
    }
    
    private void createSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(280, 0));
        sidePanel.setBackground(DARKER_BG);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 25));
        
        // Logo
        JLabel logo = new JLabel("ACME Inc.");
        logo.setFont(new Font("Arial", Font.BOLD, 26));
        logo.setForeground(TEXT_PRIMARY);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidePanel.add(logo);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 50)));
        
        // Menu items
        String[] menuItems = {"Home", "Portfolio", "Trading", "Assets", "Reports", "Settings"};
        String[] iconPaths = {
            "src/main/resources/images/icons/home.png",
            "src/main/resources/images/icons/portfolio.png",
            "src/main/resources/images/icons/trading.png",
            "src/main/resources/images/icons/assets.png",
            "src/main/resources/images/icons/reports.png",
            "src/main/resources/images/icons/settings.png"
        };
        
        for (int i = 0; i < menuItems.length; i++) {
            JPanel menuItem = createMenuItem(iconPaths[i], menuItems[i], i == 0);
            sidePanel.add(menuItem);
            sidePanel.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        
        sidePanel.add(Box.createVerticalGlue());
        
        // Logout button
        JPanel logoutPanel = createMenuItem("src/main/resources/images/icons/logout.png", "Logout", false);
        sidePanel.add(logoutPanel);
    }
    
    private JPanel createMenuItem(String iconPath, String text, boolean isActive) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
        panel.setBackground(isActive ? CARD_BG : DARKER_BG);
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (isActive) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_BLUE, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
        } else {
            panel.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        }
        
        ImageIcon icon = loadIcon(iconPath, 24, 24);
        JLabel iconLabel = icon != null ? new JLabel(icon) : new JLabel("•");
        iconLabel.setForeground(isActive ? ACCENT_BLUE : TEXT_SECONDARY);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        textLabel.setForeground(isActive ? TEXT_PRIMARY : TEXT_SECONDARY);
        
        panel.add(iconLabel);
        panel.add(textLabel);
        
        // Hover effect
        panel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    panel.setBackground(CARD_BG);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    panel.setBackground(DARKER_BG);
                }
            }
        });
        
        return panel;
    }
    
    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        
        // Top bar
        createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);
        
        // Content area
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(DARK_BG);
        contentArea.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Overview section
        JPanel overviewSection = createOverviewSection();
        contentArea.add(overviewSection, BorderLayout.NORTH);
        
        // Bottom section with transactions and wallet
        JPanel bottomSection = new JPanel(new BorderLayout(30, 0));
        bottomSection.setOpaque(false);
        bottomSection.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Transactions panel (left/center)
        JPanel transactionsPanel = createTransactionsPanel();
        
        // Wallet panel (right sidebar)
        JPanel walletPanel = createWalletPanel();
        
        bottomSection.add(transactionsPanel, BorderLayout.CENTER);
        bottomSection.add(walletPanel, BorderLayout.EAST);
        
        contentArea.add(bottomSection, BorderLayout.CENTER);
        
        mainPanel.add(contentArea, BorderLayout.CENTER);
    }
    
    private void createTopBar() {
        topBar = new JPanel(new BorderLayout());
        topBar.setBackground(DARK_BG);
        topBar.setPreferredSize(new Dimension(0, 100));
        topBar.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 30));
        
        // Title
        JLabel titleLabel = new JLabel("Home v2.0");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        // User profile
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        userPanel.setOpaque(false);
        
        JLabel userName = new JLabel("Amara Boyle");
        userName.setFont(new Font("Arial", Font.BOLD, 16));
        userName.setForeground(TEXT_PRIMARY);
        
        // User avatar
        ImageIcon avatarIcon = loadIcon("src/main/resources/images/avatars/amara.png", 50, 50);
        JLabel avatarLabel;
        
        if (avatarIcon != null) {
            avatarLabel = new JLabel(avatarIcon);
        } else {
            // Fallback gradient avatar
            JPanel avatarPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    GradientPaint gradient = new GradientPaint(0, 0, PURPLE, getWidth(), getHeight(), 
                        new Color(167, 139, 250));
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
            };
            avatarPanel.setPreferredSize(new Dimension(50, 50));
            avatarPanel.setLayout(new BorderLayout());
            
            JLabel avatarText = new JLabel("A", SwingConstants.CENTER);
            avatarText.setFont(new Font("Arial", Font.BOLD, 20));
            avatarText.setForeground(Color.WHITE);
            avatarPanel.add(avatarText, BorderLayout.CENTER);
            
            avatarLabel = new JLabel();
            avatarLabel.setLayout(new BorderLayout());
            avatarLabel.add(avatarPanel);
        }
        
        userPanel.add(userName);
        userPanel.add(avatarLabel);
        
        topBar.add(userPanel, BorderLayout.EAST);
    }
    
    private JPanel createOverviewSection() {
        JPanel panel = new JPanel(new GridLayout(1, 3, 30, 0));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 220));
        
        // Income card
        JPanel incomeCard = createStatCard("Income", "21.4 US$", null, ACCENT_BLUE);
        
        // Outcome card with chart
        JPanel outcomeCard = createStatCard("Outcome", "7.15 US$", createMiniChart(), PURPLE);
        
        // Watchlist card
        JPanel watchlistCard = createWatchlistCard();
        
        panel.add(incomeCard);
        panel.add(outcomeCard);
        panel.add(watchlistCard);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, JComponent chart, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 65, 100), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(TEXT_PRIMARY);
        
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        topSection.add(valueLabel, BorderLayout.CENTER);
        
        card.add(topSection, BorderLayout.NORTH);
        
        if (chart != null) {
            chart.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            card.add(chart, BorderLayout.CENTER);
        }
        
        return card;
    }
    
    private JPanel createMiniChart() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Transparent background
                setOpaque(false);
                
                // Simple line chart
                g2d.setColor(ACCENT_BLUE);
                g2d.setStroke(new BasicStroke(3));
                
                int[] xPoints = {20, 60, 100, 140, 180, 220, 260};
                int[] yPoints = {70, 50, 60, 40, 55, 45, 50};
                
                // Draw line
                for (int i = 0; i < xPoints.length - 1; i++) {
                    g2d.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
                }
                
                // Draw points
                g2d.setColor(ACCENT_BLUE);
                for (int i = 0; i < xPoints.length; i++) {
                    g2d.fillOval(xPoints[i] - 4, yPoints[i] - 4, 8, 8);
                }
                
                // Month labels
                g2d.setColor(TEXT_SECONDARY);
                g2d.setFont(new Font("Arial", Font.PLAIN, 10));
                String[] months = {"Mar", "Apr", "May", "Jun", "Jul", "Aug"};
                for (int i = 0; i < months.length && i < xPoints.length - 1; i++) {
                    g2d.drawString(months[i], xPoints[i] - 10, 95);
                }
            }
            
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(300, 100);
            }
            
            @Override
            public boolean isOpaque() {
                return false;
            }
        };
    }
    
    private JPanel createWatchlistCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 65, 100), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel titleLabel = new JLabel("Watchlist");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(TEXT_SECONDARY);
        
        JPanel watchlistItems = new JPanel();
        watchlistItems.setLayout(new BoxLayout(watchlistItems, BoxLayout.Y_AXIS));
        watchlistItems.setOpaque(false);
        watchlistItems.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // BTC
        JPanel btcItem = createWatchlistItem("BTC", "$67,000", "+1.2%", true);
        JPanel ethItem = createWatchlistItem("ETH", "$3,000", "-0.8%", false);
        
        watchlistItems.add(btcItem);
        watchlistItems.add(Box.createRigidArea(new Dimension(0, 15)));
        watchlistItems.add(ethItem);
        watchlistItems.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Performance section
        JPanel perfPanel = new JPanel(new BorderLayout());
        perfPanel.setOpaque(false);
        
        JLabel perfLabel = new JLabel("Net Profit");
        perfLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        perfLabel.setForeground(TEXT_SECONDARY);
        
        JLabel profitLabel = new JLabel("+$1,200");
        profitLabel.setFont(new Font("Arial", Font.BOLD, 28));
        profitLabel.setForeground(GREEN);
        
        perfPanel.add(perfLabel, BorderLayout.NORTH);
        perfPanel.add(profitLabel, BorderLayout.CENTER);
        
        watchlistItems.add(perfPanel);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(watchlistItems, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createWatchlistItem(String symbol, String price, String change, boolean isPositive) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        
        JLabel symbolLabel = new JLabel(symbol);
        symbolLabel.setFont(new Font("Arial", Font.BOLD, 16));
        symbolLabel.setForeground(TEXT_PRIMARY);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel priceLabel = new JLabel(price);
        priceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        priceLabel.setForeground(TEXT_PRIMARY);
        
        JLabel changeLabel = new JLabel(change);
        changeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        changeLabel.setForeground(isPositive ? GREEN : RED);
        
        rightPanel.add(priceLabel);
        rightPanel.add(changeLabel);
        
        item.add(symbolLabel, BorderLayout.WEST);
        item.add(rightPanel, BorderLayout.EAST);
        
        return item;
    }
    
    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 65, 100), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Transactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        headerRight.setOpaque(false);
        
        // Tabs
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        tabPanel.setOpaque(false);
        
        JLabel historyTab = new JLabel("History");
        historyTab.setFont(new Font("Arial", Font.PLAIN, 14));
        historyTab.setForeground(ACCENT_BLUE);
        historyTab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        historyTab.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));
        
        JLabel upcomingTab = new JLabel("Upcoming");
        upcomingTab.setFont(new Font("Arial", Font.PLAIN, 14));
        upcomingTab.setForeground(TEXT_SECONDARY);
        upcomingTab.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        tabPanel.add(historyTab);
        tabPanel.add(upcomingTab);
        
        JLabel dateRange = new JLabel("06 Jun 2020 - 4 Oct 2020");
        dateRange.setFont(new Font("Arial", Font.PLAIN, 12));
        dateRange.setForeground(TEXT_SECONDARY);
        
        headerRight.add(tabPanel);
        headerRight.add(Box.createRigidArea(new Dimension(30, 0)));
        headerRight.add(dateRange);
        
        header.add(titleLabel, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);
        
        // Transaction list
        JPanel transactionsList = new JPanel();
        transactionsList.setLayout(new BoxLayout(transactionsList, BoxLayout.Y_AXIS));
        transactionsList.setOpaque(false);
        
        // Add transactions
        addTransaction(transactionsList, "13 Jun 2020", "Transfer to Lucas", "Personal", "25.90 US$", PURPLE);
        addTransaction(transactionsList, "27 Jun 2020", "Burger King", "Food", "25.90 US$", Color.ORANGE);
        addTransaction(transactionsList, "27 Jun 2020", "Investment to BGA", "Investment", "25.90 US$", GREEN);
        addTransaction(transactionsList, "05 July 2020", "Withdrawal", "Personal", "25.90 US$", PURPLE);
        
        JScrollPane scrollPane = new JScrollPane(transactionsList);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void addTransaction(JPanel parent, String date, String title, String category, String amount, Color categoryColor) {
        JPanel transaction = new JPanel(new BorderLayout());
        transaction.setOpaque(false);
        transaction.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        transaction.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        // Left section
        JPanel leftSection = new JPanel();
        leftSection.setLayout(new BoxLayout(leftSection, BoxLayout.Y_AXIS));
        leftSection.setOpaque(false);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(TEXT_SECONDARY);
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        categoryPanel.setOpaque(false);
        categoryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel categoryDot = new JLabel("●");
        categoryDot.setForeground(categoryColor);
        categoryDot.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel categoryLabel = new JLabel(category);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryLabel.setForeground(TEXT_SECONDARY);
        
        categoryPanel.add(categoryDot);
        categoryPanel.add(categoryLabel);
        
        leftSection.add(dateLabel);
        leftSection.add(Box.createRigidArea(new Dimension(0, 5)));
        leftSection.add(titleLabel);
        leftSection.add(Box.createRigidArea(new Dimension(0, 5)));
        leftSection.add(categoryPanel);
        
        // Right section
        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        rightSection.setOpaque(false);
        
        JLabel amountLabel = new JLabel(amount);
        amountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        amountLabel.setForeground(TEXT_PRIMARY);
        
        JButton invoiceBtn = new JButton("See Invoice");
        invoiceBtn.setBackground(new Color(55, 65, 100));
        invoiceBtn.setForeground(TEXT_PRIMARY);
        invoiceBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        invoiceBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        invoiceBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        invoiceBtn.setFocusPainted(false);
        
        rightSection.add(amountLabel);
        rightSection.add(invoiceBtn);
        
        transaction.add(leftSection, BorderLayout.WEST);
        transaction.add(rightSection, BorderLayout.EAST);
        
        parent.add(transaction);
        
        // Add separator
        if (parent.getComponentCount() < 8) { // Don't add separator after last item
            JSeparator separator = new JSeparator();
            separator.setForeground(new Color(55, 65, 100));
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
            parent.add(separator);
        }
    }
    
    private JPanel createWalletPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 65, 100), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        panel.setPreferredSize(new Dimension(400, 0));
        
        // Main content with scroll
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        
        // Wallet header
        JLabel walletLabel = new JLabel("Wallet");
        walletLabel.setFont(new Font("Arial", Font.BOLD, 20));
        walletLabel.setForeground(TEXT_PRIMARY);
        walletLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Credit card visual
        JPanel cardVisual = createCreditCardVisual();
        cardVisual.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Quick transfer section
        JPanel quickTransfer = createQuickTransferSection();
        quickTransfer.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Recent contacts
        JPanel recentContacts = createRecentContactsSection();
        recentContacts.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        mainContent.add(walletLabel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));
        mainContent.add(cardVisual);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));
        mainContent.add(quickTransfer);
        mainContent.add(Box.createRigidArea(new Dimension(0, 25)));
        mainContent.add(recentContacts);
        mainContent.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCreditCardVisual() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background with rounded corners
                GradientPaint gradient = new GradientPaint(0, 0, new Color(100, 149, 237), 
                    getWidth(), getHeight(), new Color(167, 139, 250));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            }
            
            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        card.setPreferredSize(new Dimension(320, 180));
        card.setMaximumSize(new Dimension(320, 180));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Card content
        JPanel cardContent = new JPanel(new BorderLayout());
        cardContent.setOpaque(false);
        
        // Top section
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setOpaque(false);
        
        JLabel visaLabel = new JLabel("VISA");
        visaLabel.setFont(new Font("Arial", Font.BOLD, 28));
        visaLabel.setForeground(Color.WHITE);
        
        // Chip icon
        ImageIcon chipIcon = loadIcon("src/main/resources/images/chips/card-chip.png", 40, 30);
        JLabel chipLabel = chipIcon != null ? new JLabel(chipIcon) : new JLabel();
        
        if (chipIcon == null) {
            // Fallback chip drawing
            JPanel chipPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2d.setColor(new Color(255, 255, 255, 200));
                    g2d.fillRoundRect(0, 0, 40, 30, 5, 5);
                    g2d.setColor(new Color(200, 200, 200));
                    g2d.drawRoundRect(0, 0, 40, 30, 5, 5);
                }
                
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(40, 30);
                }
            };
            chipLabel = new JLabel();
            chipLabel.setLayout(new BorderLayout());
            chipLabel.add(chipPanel);
        }
        
        topSection.add(visaLabel, BorderLayout.WEST);
        topSection.add(chipLabel, BorderLayout.EAST);
        
        // Middle section
        JPanel middleSection = new JPanel();
        middleSection.setLayout(new BoxLayout(middleSection, BoxLayout.Y_AXIS));
        middleSection.setOpaque(false);
        middleSection.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        JLabel balanceLabel = new JLabel("Active Balance");
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        balanceLabel.setForeground(new Color(255, 255, 255, 200));
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel amountLabel = new JLabel("$ 45,720");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 32));
        amountLabel.setForeground(Color.WHITE);
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        middleSection.add(balanceLabel);
        middleSection.add(Box.createRigidArea(new Dimension(0, 5)));
        middleSection.add(amountLabel);
        
        // Bottom section
        JPanel bottomSection = new JPanel(new BorderLayout());
        bottomSection.setOpaque(false);
        
        JLabel nameLabel = new JLabel("Amara Boyle");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel expiryLabel = new JLabel("06/25");
        expiryLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        expiryLabel.setForeground(Color.WHITE);
        
        bottomSection.add(nameLabel, BorderLayout.WEST);
        bottomSection.add(expiryLabel, BorderLayout.EAST);
        
        cardContent.add(topSection, BorderLayout.NORTH);
        cardContent.add(middleSection, BorderLayout.CENTER);
        cardContent.add(bottomSection, BorderLayout.SOUTH);
        
        card.add(cardContent);
        
        return card;
    }
    
    private JPanel createQuickTransferSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel titleLabel = new JLabel("Quick Transfer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel transferPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        transferPanel.setOpaque(false);
        transferPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        transferPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        transferPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        
        JTextField accountField = new JTextField("Account number");
        accountField.setPreferredSize(new Dimension(220, 40));
        accountField.setMaximumSize(new Dimension(220, 40));
        accountField.setBackground(DARKER_BG);
        accountField.setForeground(TEXT_SECONDARY);
        accountField.setCaretColor(TEXT_PRIMARY);
        accountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(55, 65, 100), 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        accountField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        accountField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (accountField.getText().equals("Account number")) {
                    accountField.setText("");
                    accountField.setForeground(TEXT_PRIMARY);
                }
            }
            public void focusLost(FocusEvent e) {
                if (accountField.getText().isEmpty()) {
                    accountField.setText("Account number");
                    accountField.setForeground(TEXT_SECONDARY);
                }
            }
        });
        
        JButton sendBtn = new JButton("➤");
        sendBtn.setPreferredSize(new Dimension(45, 40));
        sendBtn.setBackground(PURPLE);
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFont(new Font("Arial", Font.BOLD, 18));
        sendBtn.setBorder(null);
        sendBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sendBtn.setFocusPainted(false);
        
        transferPanel.add(accountField);
        transferPanel.add(sendBtn);
        
        section.add(titleLabel);
        section.add(transferPanel);
        
        return section;
    }
    
    private JPanel createRecentContactsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        
        JLabel titleLabel = new JLabel("Recent");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel contactsPanel = new JPanel();
        contactsPanel.setLayout(new BoxLayout(contactsPanel, BoxLayout.Y_AXIS));
        contactsPanel.setOpaque(false);
        contactsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        contactsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        addRecentContact(contactsPanel, "Maria Lim", "VISA •4099 2287 •••••", 
            "src/main/resources/images/avatars/maria.png");
        addRecentContact(contactsPanel, "David Miller", "VISA •2654 1934 •••••", 
            "src/main/resources/images/avatars/david.png");
        
        section.add(titleLabel);
        section.add(contactsPanel);
        
        return section;
    }
    
    private void addRecentContact(JPanel parent, String name, String cardInfo, String avatarPath) {
        JPanel contact = new JPanel(new BorderLayout());
        contact.setOpaque(false);
        contact.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        contact.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftSection.setOpaque(false);
        
        // Avatar
        ImageIcon avatarIcon = loadIcon(avatarPath, 45, 45);
        JLabel avatarLabel;
        
        if (avatarIcon != null) {
            avatarLabel = new JLabel(avatarIcon);
        } else {
            // Fallback avatar
            JPanel avatarPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2d.setColor(new Color(55, 65, 100));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                }
            };
            avatarPanel.setPreferredSize(new Dimension(45, 45));
            avatarPanel.setLayout(new BorderLayout());
            
            JLabel avatarText = new JLabel(name.substring(0, 1), SwingConstants.CENTER);
            avatarText.setFont(new Font("Arial", Font.PLAIN, 20));
            avatarText.setForeground(TEXT_SECONDARY);
            avatarPanel.add(avatarText, BorderLayout.CENTER);
            
            avatarLabel = new JLabel();
            avatarLabel.setLayout(new BorderLayout());
            avatarLabel.add(avatarPanel);
        }
        
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel cardLabel = new JLabel(cardInfo);
        cardLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        cardLabel.setForeground(TEXT_SECONDARY);
        cardLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 3)));
        infoPanel.add(cardLabel);
        
        leftSection.add(avatarLabel);
        leftSection.add(infoPanel);
        
        contact.add(leftSection, BorderLayout.WEST);
        
        parent.add(contact);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            home dashboard = new home();
            dashboard.setVisible(true);
        });
    }
}
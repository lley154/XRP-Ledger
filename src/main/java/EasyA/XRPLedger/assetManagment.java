package EasyA.XRPLedger;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class assetManagment extends JFrame {
    // Color scheme
    private static final Color DARK_BG = new Color(28, 33, 53);
    private static final Color DARKER_BG = new Color(20, 24, 38);
    private static final Color CARD_BG = new Color(35, 41, 70);
    private static final Color ACCENT_BLUE = new Color(100, 149, 237);
    private static final Color GREEN = new Color(34, 197, 94);
    private static final Color RED = new Color(239, 68, 68);
    private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
    private static final Color TEXT_SECONDARY = new Color(148, 163, 184);

    private JPanel sidePanel;
    private JPanel mainPanel;

    public assetManagment() {
        setTitle("ACME Inc. - Asset Management");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(DARK_BG);

        createSidePanel();
        createMainPanel();

        container.add(sidePanel, BorderLayout.WEST);
        container.add(mainPanel, BorderLayout.CENTER);

        add(container);
    }

    private void createSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(250, 0));
        sidePanel.setBackground(DARKER_BG);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Asset Management");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidePanel.add(title);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Company name
        JLabel company = new JLabel("ACME Inc.");
        company.setFont(new Font("Arial", Font.PLAIN, 14));
        company.setForeground(TEXT_SECONDARY);
        company.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidePanel.add(company);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Main menu items
        String[] mainItems = {"Home", "Portfolio", "Trading", "Assets"};
        for (String item : mainItems) {
            sidePanel.add(createMenuItem(item, false));
            sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        // Submenu for Assets
        String[] subItems = {"Asset Price", "Order Book", "Asset Details", "Manage Asset", "Reports"};
        JPanel subMenuPanel = new JPanel();
        subMenuPanel.setLayout(new BoxLayout(subMenuPanel, BoxLayout.Y_AXIS));
        subMenuPanel.setBackground(DARKER_BG);
        subMenuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subMenuPanel.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));

        for (String subItem : subItems) {
            JLabel subLabel = new JLabel(subItem);
            subLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            subLabel.setForeground(TEXT_SECONDARY);
            subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            subMenuPanel.add(subLabel);
            subMenuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        sidePanel.add(subMenuPanel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // Logout
        JLabel logout = new JLabel("Logout");
        logout.setFont(new Font("Arial", Font.PLAIN, 14));
        logout.setForeground(TEXT_SECONDARY);
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidePanel.add(logout);
    }

    private JPanel createMenuItem(String text, boolean active) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        p.setBackground(active ? CARD_BG : DARKER_BG);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN, 14));
        l.setForeground(active ? TEXT_PRIMARY : TEXT_SECONDARY);
        p.add(l);
        return p;
    }

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel title = new JLabel("Asset Management");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(TEXT_PRIMARY);
        mainPanel.add(title, BorderLayout.NORTH);
        
        // Content
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        contentPanel.setOpaque(false);
        
        // Left column
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setOpaque(false);
        
        // Liquidity Pool section
        JPanel liquidityPoolPanel = createSectionPanel("Liquidity Pool");
        JPanel liquidityPoolContent = new JPanel();
        liquidityPoolContent.setLayout(new BoxLayout(liquidityPoolContent, BoxLayout.Y_AXIS));
        liquidityPoolContent.setOpaque(false);
        liquidityPoolContent.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        JLabel xrpLabel = new JLabel("• XRP (40000.2)");
        xrpLabel.setForeground(TEXT_PRIMARY);
        xrpLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel rusdLabel = new JLabel("• RUSD (60000.4)");
        rusdLabel.setForeground(TEXT_PRIMARY);
        rusdLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        liquidityPoolContent.add(xrpLabel);
        liquidityPoolContent.add(Box.createRigidArea(new Dimension(0, 5)));
        liquidityPoolContent.add(rusdLabel);
        liquidityPoolPanel.add(liquidityPoolContent);
        
        // Assets Details section
        JPanel assetsDetailsPanel = createSectionPanel("Assets Details");
        String[][] assetDetails = {
            {"Asset Name", "Acme Inc. (ACME)"},
            {"Asset Type", "Equity"},
            {"Current Price", "$189.50"},
            {"Quantity Owned", "75"},
            {"Total Market Value", "$14212.50"},
            {"Unrealized Gain Lost", "+$1875.00"},
            {"Market Capitalization", "$801.58"}
        };
        assetsDetailsPanel.add(createDetailsTable(assetDetails));
        
        leftColumn.add(liquidityPoolPanel);
        leftColumn.add(Box.createRigidArea(new Dimension(0, 20)));
        leftColumn.add(assetsDetailsPanel);
        
        // Right column
        JPanel rightColumn = new JPanel();
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setOpaque(false);
        
        // Liquidity Pool Management section
        JPanel liquidityPoolMgmtPanel = createSectionPanel("Liquidity Pool Management");
        String[][] poolMgmtDetails = {
            {"Liquidity Pool TVL (USD)", "250,000"},
            {"Pool Ratio (XRP / RLUSD)", "1:1.5"},
            {"LP Token Balance", "100"}
        };
        liquidityPoolMgmtPanel.add(createDetailsTable(poolMgmtDetails));
        
        // Manager Assets section
        JPanel manageAssetsPanel = createSectionPanel("Manager Assets");
        String[][] manageAssetsDetails = {
            {"Asset Symbol", "XYZ"},
            {"Asset Name", "XYZ Gold Corp"},
            {"Total Asset Supply", "250,000"},
            {"Asset Balance", "15,000"}
        };
        manageAssetsPanel.add(createDetailsTable(manageAssetsDetails));
        
        // Listing section
        JPanel listingPanel = createSectionPanel("Listing");
        JPanel listingContent = new JPanel(new GridLayout(1, 4, 5, 5));
        listingContent.setOpaque(false);
        listingContent.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        JButton issueButton = createActionButton("Issue");
        JButton redeemButton = createActionButton("Redeem");
        JButton freezeButton = createActionButton("Freeze");
        JButton seizeButton = createActionButton("Seize");
        
        listingContent.add(issueButton);
        listingContent.add(redeemButton);
        listingContent.add(freezeButton);
        listingContent.add(seizeButton);
        
        listingPanel.add(listingContent);
        
        rightColumn.add(liquidityPoolMgmtPanel);
        rightColumn.add(Box.createRigidArea(new Dimension(0, 20)));
        rightColumn.add(manageAssetsPanel);
        rightColumn.add(Box.createRigidArea(new Dimension(0, 20)));
        rightColumn.add(listingPanel);
        
        contentPanel.add(leftColumn);
        contentPanel.add(rightColumn);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 15, 15, 15),
            BorderFactory.createMatteBorder(0, 0, 1, 0, TEXT_SECONDARY)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        return panel;
    }

    private JPanel createDetailsTable(String[][] data) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        for (String[] row : data) {
            JPanel rowPanel = new JPanel(new BorderLayout());
            rowPanel.setOpaque(false);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            
            JLabel property = new JLabel(row[0]);
            property.setForeground(TEXT_SECONDARY);
            property.setFont(new Font("Arial", Font.PLAIN, 14));
            
            JLabel value = new JLabel(row[1]);
            value.setForeground(TEXT_PRIMARY);
            value.setFont(new Font("Arial", Font.PLAIN, 14));
            value.setHorizontalAlignment(SwingConstants.RIGHT);
            
            rowPanel.add(property, BorderLayout.WEST);
            rowPanel.add(value, BorderLayout.EAST);
            panel.add(rowPanel);
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        return panel;
    }

    private JButton createActionButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(TEXT_PRIMARY);
        button.setBackground(DARKER_BG);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 5, 8, 5));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new assetManagment().setVisible(true));
    }
}
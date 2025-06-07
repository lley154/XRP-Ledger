package EasyA.XRPLedger;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class trading extends JFrame {
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

    public trading() {
        setTitle("ACME Inc. - Trading Dashboard");
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

    private ImageIcon loadIcon(String path, int w, int h) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            return new ImageIcon(img.getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (IOException e) {
            return null;
        }
    }

    private void createSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(250, 0));
        sidePanel.setBackground(DARKER_BG);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JLabel logo = new JLabel("ACME Inc.");
        logo.setFont(new Font("Arial", Font.BOLD, 24));
        logo.setForeground(TEXT_PRIMARY);
        sidePanel.add(logo);
        sidePanel.add(Box.createRigidArea(new Dimension(0,30)));

        String[] main = {"Home","Portfolio","Trading","Assets","Reports","Settings"};
        for (String item: main) {
            JPanel m = createMenuItem(item, item.equals("Trading"));
            sidePanel.add(m);
            sidePanel.add(Box.createRigidArea(new Dimension(0,5)));
            if (item.equals("Trading")) {
                String[] sub = {"Market Price","Order Book","Positions","Place Order"};
                for (String s: sub) {
                    JPanel sm = createSubMenuItem(s);
                    sidePanel.add(sm);
                    sidePanel.add(Box.createRigidArea(new Dimension(0,5)));
                }
            }
        }
    }

    private JPanel createMenuItem(String text, boolean active) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,15,12));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,45));
        p.setBackground(active?CARD_BG:DARKER_BG);
        if (active) p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_BLUE,2),
            BorderFactory.createEmptyBorder(5,10,5,10)));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN,16));
        l.setForeground(active?TEXT_PRIMARY:TEXT_SECONDARY);
        p.add(l);
        return p;
    }
    private JPanel createSubMenuItem(String text) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,15,8));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        p.setBackground(DARKER_BG);
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.PLAIN,14));
        l.setForeground(TEXT_SECONDARY);
        p.add(Box.createRigidArea(new Dimension(20,0)));
        p.add(l);
        return p;
    }

    private void createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(DARK_BG);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        mainPanel.add(createMarketBanner(), BorderLayout.NORTH);
        mainPanel.add(createGrid(), BorderLayout.CENTER);
    }

    private JPanel createMarketBanner() {
        JPanel b = new JPanel(new BorderLayout()); b.setOpaque(false);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT,10,5)); left.setOpaque(false);
        String[] syms = {"S&P 500","DOW JONES","DXY","VIX","BTC","XRP"};
        String[] prc = {"4,005.44","12,003.33","101.25","22.15","1,032.51","2.51"};
        String[] chg = {"-0.45%","+0.45%","-0.37%","-0.56%","+0.73%","+0.45%"};
        for (int i=0;i<syms.length;i++)
            left.add(createTicker(syms[i],prc[i],chg[i],chg[i].startsWith("+")?GREEN:RED));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,5)); right.setOpaque(false);
        right.add(new JLabel(loadIcon("src/main/resources/images/icons/bell.png",24,24)));
        right.add(new JLabel(loadIcon("src/main/resources/images/avatars/amara.png",36,36)));
        b.add(left, BorderLayout.WEST); b.add(right, BorderLayout.EAST);
        return b;
    }
    private JPanel createTicker(String s,String p,String c,Color col) {
        JPanel x = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0)); x.setOpaque(false);
        JLabel ls = new JLabel(s); ls.setForeground(TEXT_PRIMARY); ls.setFont(new Font("Arial",Font.PLAIN,14));
        JLabel lp = new JLabel(p); lp.setForeground(TEXT_PRIMARY); lp.setFont(new Font("Arial",Font.PLAIN,14));
        JLabel lc = new JLabel(c); lc.setForeground(col); lc.setFont(new Font("Arial",Font.PLAIN,12));
        x.add(ls); x.add(lp); x.add(lc);
        return x;
    }

    private JPanel createGrid() {
        JPanel g = new JPanel(new GridLayout(2,2,20,20)); g.setOpaque(false);
        g.add(chartPanel()); g.add(orderBookPanel());
        g.add(orderHistoryPanel()); g.add(placeOrderPanel());
        return g;
    }

    private JPanel chartPanel() {
        return new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CARD_BG);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                int left=50, right=getWidth()-30, top=40, bottom=getHeight()-50;
                g2.setColor(TEXT_SECONDARY);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(left,top,left,bottom);
                g2.drawLine(left,bottom,right,bottom);
                // y-labels
                for(int i=0;i<=9;i++){
                    int y=bottom - i*(bottom-top)/9;
                    g2.drawString("$"+i*10,10,y+5);
                }
                // x labels
                g2.drawString("March",left+20,bottom+20);
                g2.drawString("April",left+200,bottom+20);
                // candles
                int n=9;
                double[] o={40,60,50,70,60,80,75,90,85};
                double[] c={50,55,45,65,70,85,80,95,88};
                double[] h={55,70,60,75,75,90,85,100,95};
                double[] l={35,50,40,60,55,75,70,85,80};
                for(int i=0;i<n;i++){
                    int x=left + i*(right-left)/(n-1);
                    int yo=(int)(bottom - (o[i]-10)*(bottom-top)/80);
                    int yc=(int)(bottom - (c[i]-10)*(bottom-top)/80);
                    int yh=(int)(bottom - (h[i]-10)*(bottom-top)/80);
                    int yl=(int)(bottom - (l[i]-10)*(bottom-top)/80);
                    g2.setColor(c[i]>o[i]?GREEN:RED);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(x,yh,x,yl);
                    g2.fillRect(x-6,Math.min(yo,yc),12,Math.max(2,Math.abs(yo-yc)));
                }
            }
            public Dimension getPreferredSize(){return new Dimension(400,260);}        
        };
    }

    private JPanel orderBookPanel() {
        JPanel p=new JPanel(new BorderLayout()); p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JLabel t=new JLabel("Order Book"); t.setFont(new Font("Arial",Font.BOLD,18)); t.setForeground(TEXT_PRIMARY);
        p.add(t,BorderLayout.NORTH);
        // table
        String[] cols={"Bid Size","Bid Price","Ask Price","Ask Size"};
        Object[][] data={{0.1051,146716.0,148000.0,0.06},{0.1101,146500.0,148250.0,0.0449},
                        {0.1151,146800.0,148900.0,0.0599},{0.1288,146000.0,149000.0,0.1511},
                        {0.1856,146800.0,149500.0,0.1942}};
        JTable tb=new JTable(data,cols);
        tb.setBackground(CARD_BG); tb.setForeground(TEXT_PRIMARY);
        tb.setShowGrid(false); tb.setRowHeight(24);
        ((DefaultTableCellRenderer)tb.getDefaultRenderer(Object.class)).setBackground(CARD_BG);
        JScrollPane sp=new JScrollPane(tb); sp.setPreferredSize(new Dimension(0,140));
        p.add(sp,BorderLayout.CENTER);
        // depth chart
        p.add(new JPanel(){protected void paintComponent(Graphics g){
            super.paintComponent(g); Graphics2D g2=(Graphics2D)g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight();
            int[] bx={20,20,80,80}, by={h, h-100, h-80, h};
            g2.setColor(new Color(34,197,94,150)); g2.fillPolygon(bx,by,bx.length);
            int[] sx={w-20,w-20,w-80,w-80}, sy={h, h-120, h-100, h};
            g2.setColor(new Color(239,68,68,150)); g2.fillPolygon(sx,sy,sx.length);
            g2.setColor(TEXT_SECONDARY); g2.setFont(new Font("Arial",Font.PLAIN,12));
            g2.drawString("Price (BTC/ETH)",w/2-40,20);
        } public Dimension getPreferredSize(){return new Dimension(0,120);} },BorderLayout.SOUTH);
        return p;
    }

    private JPanel orderHistoryPanel(){
        String[] cols={"Size","Price (USD)","Time Stamp"};
        Object[][] d={{0.03,148714.87,"05-20 15:26:42"},{0.00005,148000.00,"05-20 15:12:49"},
                     {0.0017,148000.00,"05-20 14:28:59"},{0.005,148000.00,"05-20 13:56:05"},
                     {0.0035,147500.00,"05-20 13:46:15"},{0.002383,146775.00,"05-20 13:06:49"},
                     {0.036,146500.00,"05-20 13:06:45"}};
        JTable tb=new JTable(d,cols);
        tb.setBackground(CARD_BG); tb.setForeground(TEXT_PRIMARY);
        tb.setShowGrid(false); tb.setRowHeight(24);
        JScrollPane sp=new JScrollPane(tb);
        JPanel p=new JPanel(new BorderLayout()); p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        JLabel t=new JLabel("Order History"); t.setFont(new Font("Arial",Font.BOLD,18)); t.setForeground(TEXT_PRIMARY);
        p.add(t,BorderLayout.NORTH); p.add(sp,BorderLayout.CENTER);
        return p;
    }

    private JPanel placeOrderPanel(){
        JPanel p=new JPanel(); p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));
        JLabel t=new JLabel("Place Order"); t.setFont(new Font("Arial",Font.BOLD,18)); t.setForeground(TEXT_PRIMARY);
        p.add(t); p.add(Box.createRigidArea(new Dimension(0,10)));
        JPanel bal=new JPanel(new GridLayout(2,2)); bal.setBackground(ACCENT_BLUE);
        bal.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        bal.add(new JLabel("Account Balance (USD)")); bal.add(new JLabel("1003.33"));
        bal.add(new JLabel("Asset Balance")); bal.add(new JLabel("400.534"));
        for(Component c:bal.getComponents()){c.setForeground(TEXT_PRIMARY);((JLabel)c).setFont(new Font("Arial",Font.PLAIN,12));}
        p.add(bal); p.add(Box.createRigidArea(new Dimension(0,15)));
        JLabel lp=new JLabel("Price"); lp.setForeground(TEXT_PRIMARY); p.add(lp);
        JTextField price=new JTextField(); price.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        price.setBackground(DARKER_BG); price.setForeground(TEXT_PRIMARY); price.setBorder(BorderFactory.createLineBorder(TEXT_SECONDARY));
        p.add(price); p.add(Box.createRigidArea(new Dimension(0,10)));
        JLabel lq=new JLabel("Quantity"); lq.setForeground(TEXT_PRIMARY); p.add(lq);
        JTextField qty=new JTextField(); qty.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        qty.setBackground(DARKER_BG); qty.setForeground(TEXT_PRIMARY); qty.setBorder(BorderFactory.createLineBorder(TEXT_SECONDARY));
        p.add(qty); p.add(Box.createRigidArea(new Dimension(0,15)));
        JButton go=new JButton("Go  "); go.setBackground(new Color(147,51,234)); go.setForeground(TEXT_PRIMARY);
        go.setFont(new Font("Arial",Font.BOLD,14)); go.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(go);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new trading().setVisible(true));
    }
}

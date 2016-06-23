package graphics;

import org.jibble.pircbot.User;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;

public class acebotsthree extends JFrame {
    public acebotsthree() {
        try
        {
            UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
        }
        catch(Exception e){
        }
        initComponents();
    }

    public static final Color TIMECOLOR = new Color(69, 69, 69);

    private void loadAccountActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void openBotFolderActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void channelListBoxActionPerformed(ActionEvent e) {
    }

    private void inputFieldActionPerformedTEST(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Bob Joe
        dListModel = new DefaultListModel<String>();
        channelUserList = new JList<String>(dListModel);
        menuBar = new JMenuBar();
        botMenu = new JMenu();
        loadAccountItem = new JMenuItem();
        openBotFoldeItem = new JMenuItem();
        someExtraLabel = new JLabel();
        channelListBox = new JComboBox();
        splitPaneMain = new JSplitPane();
        allChatLeftPane = new JTabbedPane();
        scrollPane2 = new JScrollPane();
        allChatLeftBox = new JTextPane();
        allChatRightPane = new JTabbedPane();
        scrollPane3 = new JScrollPane();
        allChatRightBox = new JTextPane();
        scrollPane1 = new JScrollPane();
        inputTab = new JTabbedPane();
        inputField = new JTextField();
        accountListBox = new JComboBox();

        //======== this ========
        setForeground(Color.magenta);
        setBackground(Color.green);
        setTitle("Acebots III :|: Disconnected");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        setVisible(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {863,/* 130,*/ 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {0, 452, 50, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {1.0,/* 0.0,*/ 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

        //======== menuBar ========
        {
            menuBar.setBackground(new Color(51, 51, 51));

            //======== botMenu ========
            {
                botMenu.setText("Bot");

                //---- loadAccountItem ----
                loadAccountItem.setText("Load Account");
                loadAccountItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        loadAccountActionPerformed(e);
                    }
                });
                botMenu.add(loadAccountItem);

                //---- openBotFoldeItem ----
                openBotFoldeItem.setText("Open Bot Folder");
                openBotFoldeItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        openBotFolderActionPerformed(e);
                    }
                });
                botMenu.add(openBotFoldeItem);
            }
            menuBar.add(botMenu);
        }
        setJMenuBar(menuBar);

        //---- someExtraLabel ----
        someExtraLabel.setText("Moderating for 0 viewers.  Loading...");
        someExtraLabel.setBackground(Color.black);
        someExtraLabel.setOpaque(true);
        someExtraLabel.setForeground(Color.white);
        contentPane.add(someExtraLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 2), 0, 0));

        /*//---- channelListBox ----
        channelListBox.setMaximumRowCount(20);
        channelListBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                channelListBoxActionPerformed(e);
            }
        });
        contentPane.add(channelListBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 0), 0, 0));
*/

        //======== splitPaneMain ========
        {
            splitPaneMain.setDividerLocation(525);
            splitPaneMain.setDividerSize(2);

            //======== allChatLeftPane ========
            {
                allChatLeftPane.setBackground(Color.black);
                allChatLeftPane.setForeground(new Color(128, 128, 128));

                //======== scrollPane2 ========
                {

                    //---- allChatLeftBox ----
                    allChatLeftBox.setBackground(new Color(25, 25, 25));
                    allChatLeftBox.setEditable(false);
                    allChatLeftBox.setDoubleBuffered(false);
                    scrollPane2.setViewportView(allChatLeftBox);
                }
                allChatLeftPane.addTab("All Chat", scrollPane2);
                allChatLeftPane.setForegroundAt(0, new Color(0, 230, 230));
            }
            splitPaneMain.setLeftComponent(allChatLeftPane);

            //======== allChatRightPane ========
            {
                allChatRightPane.setBackground(Color.black);
                allChatRightPane.setForeground(new Color(128, 128, 128));

                //======== scrollPane3 ========
                {

                    //---- allChatRightBox ----
                    allChatRightBox.setBackground(new Color(25, 25, 25));
                    allChatRightBox.setEditable(false);
                    allChatRightBox.setDoubleBuffered(true);
                    scrollPane3.setViewportView(allChatRightBox);
                }
                allChatRightPane.addTab("All Chat", scrollPane3);
                allChatRightPane.setForegroundAt(0, new Color(0, 230, 230));
            }
            splitPaneMain.setRightComponent(allChatRightPane);
        }
        contentPane.add(splitPaneMain, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 2), 0, 0));

        /*//======== scrollPane1 ========
        {

            //---- channelUserList ----
            channelUserList.setPreferredSize(new Dimension(20, 48));
            channelUserList.setBackground(Color.black);
            channelUserList.setForeground(Color.white);
            channelUserList.setFixedCellWidth(60);
            channelUserList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            scrollPane1.setViewportView(channelUserList);
        }
        contentPane.add(scrollPane1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 0), 0, 0));*/

        //======== inputTab ========
        {
            inputTab.setTabPlacement(SwingConstants.BOTTOM);
            inputTab.setBackground(Color.black);
            inputTab.setForeground(new Color(128, 128, 128));
            inputTab.setBorder(null);
            inputTab.setOpaque(true);
            inputTab.setMaximumSize(new Dimension(1200, 36));

            //---- inputField ----
            inputField.setMaximumSize(new Dimension(1200, 6));
            inputField.setPreferredSize(new Dimension(1200, 6));
            inputField.setSelectionColor(new Color(255, 153, 0));
            inputField.setSelectedTextColor(new Color(0, 200, 200));
            inputField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    inputFieldActionPerformedTEST(e);
                }
            });
            //inputTab.addTab("text", inputField);
        }
        contentPane.add(inputTab, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 2), 0, 0));

        /*//---- accountListBox ----
        accountListBox.setPreferredSize(new Dimension(20, 20));
        accountListBox.setEnabled(false);
        contentPane.add(accountListBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));*/
        setSize(1065, 600);
        setLocationRelativeTo(getOwner());

        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public void setJList(DefaultListModel<String> uList)
    {
        //System.out.println("asdf");
        //dListModel = uList;
    }

    private DefaultListModel<String> dListModel;
    public JMenuBar menuBar;
    public JMenu botMenu;
    public JMenuItem loadAccountItem;
    public JMenuItem openBotFoldeItem;
    public JLabel someExtraLabel;
    public JComboBox channelListBox;
    public JSplitPane splitPaneMain;
    public JTabbedPane allChatLeftPane;
    public JScrollPane scrollPane2;
    public JTextPane allChatLeftBox;
    public JTabbedPane allChatRightPane;
    public JScrollPane scrollPane3;
    public JTextPane allChatRightBox;
    public JScrollPane scrollPane1;
    public JList<String> channelUserList;
    public JTabbedPane inputTab;
    public JTextField inputField;
    public JComboBox accountListBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

package Bot;

import graphics.acebotsthree;
import org.jibble.pircbot.*;
import org.jibble.pircbot.User;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static u.u.*;

public class Channel {

    private BotCore acebotCore;
    private String[] previousMessages = new String[50]; //circular array
    private String[] previousSenders = new String[50];
    private int[] messageTimestamps = new int[50];
    private int[] loopTimeArray = new int[10];
    private int arrayIndex = 0;
    private int joinTime = (int)new Date().getTime();
    private String channelName;
    private int messagesLoopTime;
    private int[] viewerCounts = new int[10];
    private JTextPane leftChatBox;
    private JTextPane rightChatBox;
    private acebotsthree acebotsGUI;
    private JTextField inputBox;

    /* Put all new stuff here */
    private DefaultListModel<String> userList = new DefaultListModel<String>();
    //private ArrayList<String> userList = new ArrayList<String>();
    private String streamTitle;
    private String streamGame;
    private int viewerCount;
    private boolean isLive;
    private int liveTime;
    private int gameTime;
    private static int totalViewerCount;
    private InputTabListener inputTabChangeListener;

    private String lastMessage = "notAL3g1tmessage";
    public static int duplicateDelay = 20000;
    public Timer messageDelay;

    public int lookupDelay = 5 * 60 * 1000;
    public Timer channelLookup;

    public Channel() {}

    public Channel(String name, BotCore core)
    {
        //New Stuff, move soon
        streamTitle = "not a game";
        streamGame = "not a game";
        viewerCount = 0;
        isLive = false;
        liveTime = (int)new Date().getTime();
        gameTime = (int)new Date().getTime();
        //liveTime = 0;
        //gameTime = 0;

        //Create GUI components - Left Box, Right Box, Sender
        //Implement sender box
        acebotCore = core;
        acebotCore.joinChannel(name);
        channelName = name.substring(1);
        core.subscribe("onMessage", new MessageActionListener());
        //core.subscribe("onUserJoin", new JoinActionListener());
        for (int i = 0; i < messageTimestamps.length; i++)
            messageTimestamps[i] = 0;
        messageDelay = new Timer(duplicateDelay, resetMessageTimer);
        messageDelay.stop();

        channelLookup = new Timer(lookupDelay, channelLookupTimer);
        channelLookup.setInitialDelay(0);
        channelLookup.start();

        leftChatBox = new JTextPane();
        rightChatBox = new JTextPane();

        acebotsGUI = acebotCore.getGUI();

        JScrollPane leftChatScrollBar = new JScrollPane();
        JScrollPane rightChatScrollBar = new JScrollPane();

        leftChatBox.setBackground(new Color(25, 25, 25));
        leftChatBox.setEditable(false);

        rightChatBox.setBackground(new Color(25, 25, 25));
        rightChatBox.setEditable(false);

        leftChatScrollBar.setViewportView(leftChatBox);
        rightChatScrollBar.setViewportView(rightChatBox);

        acebotsGUI.allChatLeftPane.addTab(channelName, leftChatScrollBar);
        acebotsGUI.allChatRightPane.addTab(channelName, rightChatScrollBar);
        acebotsGUI.channelListBox.addItem(channelName);


        acebotsGUI.inputTab.addChangeListener(new InputTabListener());

        inputBox = new JTextField();
        inputBox.setPreferredSize(new Dimension(1200, 6));
        inputBox.setMaximumSize(new Dimension(1200, 6));
        inputBox.setMinimumSize(new Dimension(1200, 6));
        acebotsGUI.inputTab.addTab(channelName, inputBox);
        acebotsGUI.inputTab.setForeground(new Color(128, 128, 128));

        totalViewerCount = 0;
        inputTabChangeListener  = new InputTabListener();

        acebotsGUI.accountListBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) acebotsGUI.accountListBox.getSelectedItem();
                if (channelName.equalsIgnoreCase(selected))
                    acebotsGUI.setJList(userList);
            }
        });

        inputBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputBoxActionPerformed(e);
            }

            private void inputBoxActionPerformed(ActionEvent e) {
                String msg = inputBox.getText();
                if (msg.length() > 0)
                {
                    if (msg.length() == 1 && msg.equals("/"))
                        return;
                    if (msg.subSequence(0, 1).toString().equals("/"))
                    {
                        if (msg.startsWith("//"))
                        {
                            acebotCore.fire("onCommand", new String[]{addHash(channelName), acebotCore.getNick(), "1", msg});
                        }
                        else
                        {
                            acebotCore.fire("onCommand", new String[]{addHash(channelName), acebotCore.getNick(), "0", msg});
                        }
                    }
                    else
                    {
                        acebotCore.addToQueue(addHash(channelName), msg, 1);
                    }
                    inputBox.setText("");
                }
            }
        });
    }

    private class InputTabListener implements ChangeListener {
        public void stateChanged(ChangeEvent e) {
            String tabName = acebotsGUI.inputTab.getTitleAt(acebotsGUI.inputTab.getSelectedIndex());
            if (tabName.endsWith(channelName))
            {
                if (isLive)
                    acebotsGUI.someExtraLabel.setText("Moderating for " + totalViewerCount + " viewers.  " + channelName.substring(0,1).toUpperCase() + channelName.substring(1) + " - " + streamTitle + "  [" + streamGame + "]." );
                else
                    acebotsGUI.someExtraLabel.setText("Moderating for " + totalViewerCount + " viewers.  " + channelName.substring(0,1).toUpperCase() + channelName.substring(1) + " is offline.");
            }
            //System.out.println()
        }
    }

    private class MessageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];
            if (channel.substring(1).equalsIgnoreCase(channelName))
            {
                previousMessages[arrayIndex] = message;
                previousSenders[arrayIndex] = sender;
                messageTimestamps[arrayIndex] = ((int)new Date().getTime() - joinTime) / 1000;
                messagesLoopTime = messageTimestamps[arrayIndex] - messageTimestamps[(arrayIndex + 1) % messageTimestamps.length];
                if (messageTimestamps[messageTimestamps.length - 1] != 0)
                    loopTimeArray[arrayIndex % 10] = messagesLoopTime;
                //System.out.println(arrayIndex + ": Loop time: " + messagesLoopTime);
                arrayIndex = (arrayIndex + 1) % messageTimestamps.length;
            }
            //add to gui, display msg
        }
    }

    /*private class JoinActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            onJoin();
        }
    }

    private void onJoin()
    {

    } */

    public boolean leave()
    {
        return false;
    }

    public String[] getLastMessages()
    {
        return previousMessages;
    }

    public int[] getMessageTimestamps()
    {
        return messageTimestamps;
    }

    public int[] getLoopTimes()
    {
        return loopTimeArray;
    }

    public int[] getViewerCounts()
    {
        return viewerCounts;
    }

    public String[] getLastSenders()
    {
        return previousSenders;
    }

    public String getLastMessage()
    {
        return lastMessage;
    }

    ActionListener resetMessageTimer = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            messageDelay.stop();
            lastMessage = "notaxrealxmessagexasdf";
        }
    };

    ActionListener channelLookupTimer = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            /*
            Get Viewer count from API
            Get Title
            Update title??
            Update viewer list
             */

            //Get Live Status
            //If live, then do all of this, otherwise 0 it out

            //Do a was live check
                                 String blah = "";
            HashMap<String, String> streamInfo = new HashMap<String, String>();
            try {
                URL url = new URL("https://api.twitch.tv/kraken/streams/" + channelName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                blah = reader.readLine();
                if(!(blah.endsWith("null}"))){
                    if (!isLive)
                    {
                        acebotCore.fire("onStreamGoesOnline", new String[]{channelName});
                        isLive = true;
                        liveTime = (int)new Date().getTime();
                        for (int i = 0; i < acebotsGUI.allChatLeftPane.getTabCount(); i++)
                        {
                            if (acebotsGUI.allChatLeftPane.getTitleAt(i).equals(channelName))
                            {
                                acebotsGUI.allChatLeftPane.setTitleAt(i, "•" + channelName);
                                acebotsGUI.allChatLeftPane.setForegroundAt(i, new Color(255, 50, 50));
                                acebotsGUI.allChatRightPane.setTitleAt(i, "•" + channelName);
                                acebotsGUI.allChatRightPane.setForegroundAt(i, new Color(255, 50, 50));
                                acebotsGUI.inputTab.setTitleAt(i - 1, "•" + channelName);
                                acebotsGUI.inputTab.setForegroundAt(i - 1, new Color(255, 50, 50));
                            }
                        }
                    }

                    String[] data = blah.split(",\"");
                    for (int i = 0; i < data.length; i++)
                    {
                        String[] keyValue = data[i].split("\":");
                        streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                    }
                    lookupDelay = 1 * 60 * 1000;
                    String updatedStreamTitle = streamInfo.get("status");
                    String updatedStreamGame = streamInfo.get("game");
                    totalViewerCount -= viewerCount;
                    viewerCount = Integer.parseInt(streamInfo.get("viewers"));
                    totalViewerCount += viewerCount;

                    acebotsGUI.someExtraLabel.setText("Moderating for " + totalViewerCount + " viewers.  " + acebotsGUI.someExtraLabel.getText().split(" viewers.  ")[1]);

                    if (!streamGame.equals(updatedStreamGame))
                    {
                        System.out.println(channelName + " has changed their game to " + updatedStreamGame);
                        gameTime = (int)new Date().getTime();
                        streamGame = updatedStreamGame;
                        acebotCore.fire("onGameChange", new String[]{channelName, updatedStreamGame, gameTime + ""});
                    }

                    if (streamTitle == null)
                    {
                        System.out.println("nullfix");
                        streamTitle = updatedStreamTitle;
                    }

                    if (updatedStreamTitle == null)
                    {
                        System.out.println("Pulled out nothing from UST.  "+ blah);
                        return;
                    }
                    if (!streamTitle.equals(updatedStreamTitle))
                    {
                        System.out.println(channelName + " has changed their title to " + updatedStreamTitle);
                        streamTitle = updatedStreamTitle;
                        acebotsGUI.someExtraLabel.setText(acebotsGUI.someExtraLabel.getText().split("  ")[0] + "  " + channelName.substring(0,1).toUpperCase() + channelName.substring(1) + " - " + streamTitle + "  [" + streamGame + "].");
                        acebotCore.fire("onTitleChange", new String[]{channelName, gameTime + "", updatedStreamGame});
                    }

                    streamTitle = updatedStreamTitle;
                    streamGame = updatedStreamGame;
                }
                else
                {
                    lookupDelay = 5 * 60  * 1000;
                    if (isLive)
                    {
                        acebotCore.fire("onStreamGoesOffline", new String[]{channelName});
                        liveTime = (int)new Date().getTime();
                        isLive = false;
                        for (int i = 0; i < acebotsGUI.allChatLeftPane.getTabCount(); i++)
                        {
                            if (acebotsGUI.allChatLeftPane.getTitleAt(i).endsWith(channelName))
                            {
                                acebotsGUI.allChatLeftPane.setTitleAt(i, "" + channelName );
                                acebotsGUI.allChatLeftPane.setForegroundAt(i, new Color(128, 128, 128));
                                acebotsGUI.allChatRightPane.setTitleAt(i, "" + channelName );
                                acebotsGUI.allChatRightPane.setForegroundAt(i, new Color(128, 128, 128));
                                acebotsGUI.inputTab.setTitleAt(i - 1, "" + channelName);
                                acebotsGUI.inputTab.setForegroundAt(i - 1, new Color(128, 128, 128
                                ));
                            }
                        }
                    }
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
                return;
            } catch (SocketException e1) {
                e1.printStackTrace();
                System.out.println("Failed to retrieve data for " + channelName + ".");
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            } catch (NullPointerException e1) {
                e1.printStackTrace();
                System.out.println("Failed to retrieve data for " + channelName + ". " + blah);
                return;
            }
            User[] ulist = acebotCore.getUsers("#" + channelName);

            userList.clear();
            for (User u:ulist)
                userList.addElement(u.getNick());
            userList.trimToSize();

            if (((String)acebotsGUI.channelListBox.getSelectedItem()).equalsIgnoreCase(channelName))
                acebotsGUI.setJList(userList);

            channelLookup.setDelay(lookupDelay);
        }
    };

    public void startTimer()
    {
        if (messageDelay.isRunning())
            messageDelay.restart();
        else
            messageDelay.start();
    }

    public void setLastMessage(String msg)
    {
        lastMessage = msg;
    }

    public JTextPane getLeftChatBox()
    {
        return leftChatBox;
    }

    public JTextPane getRightChatBox()
    {
        return rightChatBox;
    }

    public String getStreamGame()
    {
        return streamGame;
    }

    public String getStreamTitle()
    {
        return streamTitle;
    }

    public int getStreamStartTime()
    {
        return liveTime;
    }

    public int getGameStartTime()
    {
        return gameTime;
    }

    public int getViewerCount()
    {
        return viewerCount;
    }

    public boolean getLiveStatus()
    {
        return isLive;
    }
    //use this space to loop around the viewer count thingy every once in a while
}

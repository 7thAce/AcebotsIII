package Bot;

import graphics.acebotsthree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
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

    private ArrayList<String> userList = new ArrayList<String>();
    private String streamTitle;
    private String streamGame;
    private int viewerCount;

    private String lastMessage = "notAL3g1tmessage";
    public static int duplicateDelay = 20000;
    public Timer messageDelay;

    public static int lookupDelay = 5 * 60 * 1000;
    public Timer channelLookup;

    public Channel() {}

    public Channel(String name, BotCore core)
    {
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

        acebotsGUI.allChatLeftPane.addTab(name, leftChatScrollBar);
        acebotsGUI.allChatRightPane.addTab(name, rightChatScrollBar);
        acebotsGUI.channelListBox.addItem(channelName);

        inputBox = new JTextField();
        inputBox.setPreferredSize(new Dimension(1200, 6));
        inputBox.setMaximumSize(new Dimension(1200, 6));
        inputBox.setMinimumSize(new Dimension(1200, 6));
        acebotsGUI.inputTab.addTab(name, inputBox);
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
            HashMap<String, String> streamInfo = new HashMap<String, String>();
            try {
                URL url = new URL("https://api.twitch.tv/kraken/channels/" + channelName);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String blah = reader.readLine();
                if(!(blah.equals("[]"))){
                    String[] data = blah.split(",\"");
                    for (int i = 0; i < data.length; i++)
                    {
                        String[] keyValue = data[i].split("\":");
                        streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                    }
                    return;
                }
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            streamTitle = streamInfo.get("status");
            streamGame = streamInfo.get("game");
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
    //use this space to loop around the viewer count thingy every once in a while
}

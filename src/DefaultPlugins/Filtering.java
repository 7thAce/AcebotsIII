package DefaultPlugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static u.u.*;

public class Filtering {

    private BotCore acebotCore;
    private ArrayList<String> messageFilterList = new ArrayList<String>();
    private ArrayList<String> userFilterList = new ArrayList<String>();
    private HashMap<String, Integer> userPunishMap = new HashMap<String, Integer>();
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String,Integer>();


    public Filtering() { }

    public Filtering(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onMessage", new messageActionListener());
        acebotCore.subscribe("onMe", new emoteActionListener());
        acebotCore.subscribe("onCommand", new commandActionListener());

        String[] cmdInfo = core.getCommandInfo("join");

        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        for (int i = 4; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));

        loadFilters();
    }

    private void loadFilters()
    {
        messageFilterList.clear();
        userFilterList.clear();
        String line = "";

        FileReader fr = null;
        try {
            fr = new FileReader(BotCore.FILTERSFILEPATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(fr);

        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        do
        {
            try {
                if (!line.contains(" "))
                    userFilterList.add(line.toLowerCase());
                else
                    messageFilterList.add(line.toLowerCase());
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!isBlank(line));
        try {
            reader.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class messageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            if (acebotCore.isMod(args[0], args[1]))
                return;
            	if (!usernameFilter(args[0], args[1]))
            		if (!basicMessageFilter(args[0], args[1], args[2]))
                        if (!superSimpleCleanup(args[0], args[1], args[2]))
                            ;
        }
    }

    private class emoteActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            if (acebotCore.isMod(args[0], args[1]))
                return;
            if (!usernameFilter(args[0], args[1]))
                if (!basicMessageFilter(args[0], args[1], args[2]))
                    if (!superSimpleCleanup(args[0], args[1], args[2]));
        }
    }

    private class commandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];
            if (isCommand("filter", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap));
                {
                    String filterPhrase = message.substring(8);
                    int punishLevel = 1;
                    if (isInteger(args[0]))
                    {
                        filterPhrase = filterPhrase.substring(2);
                        punishLevel = Integer.parseInt(args[0]);
                    }

                    if (messageFilterList.contains("1 " + filterPhrase) || messageFilterList.contains("2 " + filterPhrase) || messageFilterList.contains("3 " + filterPhrase))
                    {
                        acebotCore.addToQueue(channel, "That filter already exists!", Integer.parseInt(source));
                        return;
                    }

                    messageFilterList.add(filterPhrase.toLowerCase());
                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(new BufferedWriter(new FileWriter(BotCore.FILTERSFILEPATH, true)));
                        out.println(punishLevel + " " + filterPhrase);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    if (out != null)
                    {
                        out.close();
                        loadFilters();
                        acebotCore.addToQueue(channel, "Filter added with punish level " + punishLevel + ".", Integer.parseInt(source));
                        return;
                    }
                    acebotCore.addToQueue(channel, "Filter failed to add.", Integer.parseInt(source));
                }
            }
        }
    }

    private boolean basicMessageFilter(String channel, String sender, String message)
    {
        if (message.toLowerCase().startsWith(BotCore.TRIGGER.toLowerCase() + "delfilter "))
            return false;

        for(String filterPhrase:messageFilterList)
        {
            if (message.toLowerCase().contains(filterPhrase.substring(2)))
            {
                int offenseCount;
                if (userPunishMap.containsKey(sender.toLowerCase()))
                {
                    offenseCount = userPunishMap.get(sender.toLowerCase()) + Integer.parseInt(filterPhrase.substring(0, 1));
                }
                else
                {
                    offenseCount = Integer.parseInt(filterPhrase.substring(0, 1));
                }

                if (channel.toLowerCase().equalsIgnoreCase("#azorae") && offenseCount == 1)
                {
                    return false;
                }

                if (offenseCount == 1)
                {
                    acebotCore.addToQueue(channel, "/timeout " + sender + " 1", 1 /*1*/);
                    acebotCore.addToQueue(channel, "[Warning] Purging " + sender + ".", 1);
                }
                if (offenseCount == 2)
                {
                    acebotCore.addToQueue(channel, "/timeout " + sender + " 1", 1 /*1*/);
                    acebotCore.addToQueue(channel, "[WARNING] T/O 2m " + sender + ".", 1);
                }
                if (offenseCount >= 3)
                {
                    acebotCore.addToQueue(channel, "[KAPOW] " + sender + " - Contact a mod if this ban is in error.", 1);
                    acebotCore.addToQueue(channel, "/ban " + sender, 1 /*1*/);
                }
                userPunishMap.put(sender.toLowerCase(), offenseCount);
                return true;
            }
        }
        return false;
    }

    private boolean usernameFilter(String channel, String sender)
    {
        if (userFilterList.contains(sender.toLowerCase()))
        {
            acebotCore.addToQueue(channel, "/ban " + sender, 1 /*1*/);
            acebotCore.addToQueue(channel, "[KAPOW] " + sender + " - Be gone foul user!", 1);
            return true;
        }
        return false;
    }
    
    private boolean superSimpleCleanup(String channel, String sender, String message)
    {
        if (channel.equalsIgnoreCase("#azorae"))
            return false;
    	if (!message.contains(" ") && message.length() >= 150)
    	{
    		acebotCore.addToQueue(channel, "/timeout " + sender + " 60", 1 /*1*/);
    		acebotCore.addToQueue(channel, "[LongMessage] T/O 1m " + sender + ".", 1);
    		return true;
    	}
    	if (message.equals(message.toUpperCase()) && message.length() >= 250)
    	{
    		acebotCore.addToQueue(channel, "/timeout " + sender + " 120", 1 /*1*/);
    		acebotCore.addToQueue(channel, "[MassiveCaps] T/O 2m " + sender + ".", 1);
    		return true;
    	}
    	return false;
    }
}


package DefaultPlugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static u.u.*;

public class Filtering {

    private BotCore acebotsCore;
    private ArrayList<String> messageFilterList = new ArrayList<String>();
    private ArrayList<String> userFilterList = new ArrayList<String>();
    private  HashMap<String, Integer> userPunishMap = new HashMap<String, Integer>();

    public Filtering() { }

    public Filtering(BotCore core)
    {
        acebotsCore = core;
        acebotsCore.subscribe("onMessage", new messageActionListener());
        acebotsCore.subscribe("onMe", new emoteActionListener());
        acebotsCore.subscribe("onCommand", new commandActionListener());

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
            if (acebotsCore.isMod(args[0], args[1]))
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
        }
    }

    private class commandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            //copypasta cmd
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
                    acebotsCore.addToQueue(channel, "/timeout " + sender + " 1", 1 /*1*/);
                    acebotsCore.addToQueue(channel, "[Warning] Purging " + sender + ".", 1);
                }
                if (offenseCount == 2)
                {
                    acebotsCore.addToQueue(channel, "/timeout " + sender + " 1", 1 /*1*/);
                    acebotsCore.addToQueue(channel, "[WARNING] T/O 2m " + sender + ".", 1);
                }
                if (offenseCount >= 3)
                {
                    acebotsCore.addToQueue(channel, "[KAPOW] " + sender + " - Contact a mod if this ban is in error.", 1);
                    acebotsCore.addToQueue(channel, "/ban " + sender, 1 /*1*/);
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
            acebotsCore.addToQueue(channel, "/ban " + sender, 1 /*1*/);
            acebotsCore.addToQueue(channel, "[KAPOW] " + sender + " - Be gone foul user!", 1);
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
    		acebotsCore.addToQueue(channel, "/timeout " + sender + " 60", 1 /*1*/);
    		acebotsCore.addToQueue(channel, "[LongMessage] T/O 1m " + sender + ".", 1);
    		return true;
    	}
    	if (message.equals(message.toUpperCase()) && message.length() >= 250)
    	{
    		acebotsCore.addToQueue(channel, "/timeout " + sender + " 120", 1 /*1*/);
    		acebotsCore.addToQueue(channel, "[MassiveCaps] T/O 2m " + sender + ".", 1);
    		return true;
    	}
    	return false;
    }
}


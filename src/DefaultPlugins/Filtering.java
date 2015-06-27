package DefaultPlugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private final String FILTERLOG = "filterlog.txt";
    private final double CCVALUE = 0.80; //From 0 to 1, default 0.80
    /* TODO - Config file this value */



    public Filtering() { }

    public Filtering(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onMessage", new messageActionListener());
        acebotCore.subscribe("onMe", new emoteActionListener());
        acebotCore.subscribe("onCommand", new commandActionListener());

        String[] cmdInfo = core.getCommandInfo("filter");

        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        for (int i = 3; i < cmdInfo.length; i++)
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
            if (args[1].equals("jtv") || args[1].equals("nightbot"))
                return;
            if (acebotCore.isMod(args[0], args[1]))
                return;
            	if (!usernameFilter(args[0], args[1]))
            		if (!basicMessageFilter(args[0], args[1], args[2]))
                        if (!superSimpleCleanup(args[0], args[1], args[2]));
                            //if (!messageCC(args[0], args[1], args[2]));
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
                        //if (!messageCC(args[0], args[1], args[2]));
        }
    }

    private boolean messageCC(String channel, String sender, String message)
    {
        if (!channel.equals("#cirno_tv"))
            return false;

        int duplicateWordCap = 15;
        String[] words =  message.split(" ");
        if (words.length < duplicateWordCap)
            return false;

        //<3 Encryptio
        HashMap<String, Integer> wordsOccurrenceMap = new HashMap<String, Integer>();
        for (int i = 0; i < words.length; i++)
        {
            if (wordsOccurrenceMap.containsKey(words[i].toLowerCase()))
                wordsOccurrenceMap.put(words[i].toLowerCase(), wordsOccurrenceMap.get(words[i].toLowerCase()) + 1);
            else
                wordsOccurrenceMap.put(words[i].toLowerCase(), 1);
        }
        System.out.println("[" + (double)wordsOccurrenceMap.size() / words.length + "] is the message CC for " + sender + "'s message [" + message + "].");
        if ((double)wordsOccurrenceMap.size() / words.length < (1.0 / duplicateWordCap))


        //messageCC = (double)containsCount / (words.length - 1.0); //Currently only checking one thing.
        //if (messageCC >= CCVALUE)
        {
            int offenseCount = 1;
            if (userPunishMap.containsKey(sender.toLowerCase()))
            {
                offenseCount = userPunishMap.get(sender.toLowerCase()) + 1;
            }
            if (offenseCount == 1)
            {
                logFilter(channel, sender, message, "CC Purge 2s");
                acebotCore.addToQueue(channel, "/timeout " + sender + " 2", 1 /*1*/);
                //acebotCore.addToQueue(channel, "[Warning] Purging " + sender + ".", 1);
            }
            if (offenseCount == 2)
            {
                logFilter(channel, sender, message, "CC Timeout 2m");
                acebotCore.addToQueue(channel, "/timeout " + sender + " 120", 1 /*1*/);
                acebotCore.addToQueue(channel, "[WARNING] T/O 2m " + sender + ".", 1);
            }
            if (offenseCount >= 3)
            {
                logFilter(channel, sender, message, "CC Permaban");
                acebotCore.addToQueue(channel, "[KAPOW] " + sender + " - Contact a mod if this ban is in error.", 1);
                acebotCore.addToQueue(channel, "/ban " + sender, 1 /*1*/);
            }
            userPunishMap.put(sender.toLowerCase(), offenseCount);
            return true;
        }
        return false;
        //Later, we can classfiy (emotes) into one group and make it detect emote spam!
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
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    String filterPhrase = message.substring(8);
                    String[] filterSplit = filterPhrase.split(" ");
                    int punishLevel = 1;
                    if (isInteger(filterSplit[0]))
                    {
                        filterPhrase = filterPhrase.substring(2);
                        punishLevel = Integer.parseInt(filterSplit[0]);
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
                        logFilter(channel, sender, message, "ADDED FILTER");
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
        if (message.toLowerCase().startsWith(BotCore.TRIGGER.toLowerCase() + "delfilter ") || message.toLowerCase().startsWith(BotCore.TRIGGER.toLowerCase() + "filter "))
            return false;

        for(String filterPhrase:messageFilterList)
        {
            if (message.toLowerCase().contains(filterPhrase.substring(2).toLowerCase()))
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
                if (offenseCount == 1)
                {
                    if (channel.equalsIgnoreCase("#azorae"))
                        return false;
                    logFilter(channel, sender, message, "Purge 2s");
                    acebotCore.addToQueue(channel, "/timeout " + sender + " 2", 1 /*1*/);
                    //acebotCore.addToQueue(channel, "[Warning] Purging " + sender + ".", 1);
                }
                if (offenseCount == 2)
                {
                    if (channel.equalsIgnoreCase("#azorae"))
                        return false;
                    logFilter(channel, sender, message, "Timeout 2m");
                    acebotCore.addToQueue(channel, "/timeout " + sender + " 120", 1 /*1*/);
                    acebotCore.addToQueue(channel, "[WARNING] T/O 2m " + sender + ".", 1);
                }
                if (offenseCount >= 3)
                {
                    logFilter(channel, sender, message, "Permaban");
                    if (Integer.parseInt(filterPhrase.substring(0, 1)) != 3)
                        acebotCore.addToQueue(channel, "[KAPOW] " + sender + " - Contact a mod if this ban is in error.", 1);
                    else
                        /*try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } */
                        acebotCore.addToQueue(channel, "", 1 /*1*/);
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
            logFilter(channel, sender, "null", "User filter");
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
            if (message.startsWith("http") && message.length() < 300)
                return false;
            logFilter(channel, sender, message, "Long message 150");
            acebotCore.addToQueue(channel, "/timeout " + sender + " 60", 1 /*1*/);
    		acebotCore.addToQueue(channel, "[LongMessage] T/O 1m " + sender + ".", 1);
    		return true;
    	}
    	if (message.equals(message.toUpperCase()) && message.length() >= 250)
    	{
            logFilter(channel, sender, message, "Caps 250");
            acebotCore.addToQueue(channel, "/timeout " + sender + " 120", 1 /*1*/);
    		acebotCore.addToQueue(channel, "[MassiveCaps] T/O 2m " + sender + ".", 1);
    		return true;
        }
    	return false;
    }

    private void logFilter(String channel, String sender, String message, String type)
    {
        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(FILTERLOG, true)));
            SimpleDateFormat logsdf = new SimpleDateFormat("MM/dd/yy h:mm:ss aa");
            out.println(logsdf.format(new Date()) + " " + channel + " " + sender + " " + type + " " + message);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
}


package Plugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static u.u.*;

public class Quote {

    private BotCore acebotCore;
    private ArrayList<String> quotesList = new ArrayList<String>();
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

    public Quote() { }
    public Quote(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        acebotCore.subscribe("onLoad", new LoadActionListener());

        String[] cmdInfo = acebotCore.getCommandInfo("quote");
        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        //accessExceptionMap = fillAccessExceptionMap(info);

        for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            /*tring[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];
            String qParam;

            if (isCommand("quote", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap));
                {
                    if (message.length() <= 6)
                        qParam = "";
                    else
                        qParam = message.substring(7);

                    ArrayList<String> matchingQuotes = new ArrayList<String>();

                    for (String quote:quotesList)
                        if (quote.toLowerCase().contains(qParam.toLowerCase()))
                            matchingQuotes.add(quote);

                    if (matchingQuotes.size() == 0)
                        return;
                    String quote = matchingQuotes.get((int) (Math.random() * (matchingQuotes.size())));
                    String quoter = quote.substring(quote.lastIndexOf("-"));
                    acebotCore.addToQueue(channel, quote.substring(0, quote.lastIndexOf("-")) + quoter, Integer.parseInt(source));
                }
            }              */
        }
    }

    private class LoadActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            quotesList.clear();
            FileReader fr = null;
            try {
                fr = new FileReader(BotCore.QUOTESFILEPATH);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(fr);
            String line = "asdf";

            while (!isBlank(line))
            {
                try {
                    line = reader.readLine();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (!isBlank(line))
                {
                    quotesList.add(line);
                }
            }

            try {
                reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}

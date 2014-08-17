package Plugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static u.u.*;

public class Deathgame {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();
    private boolean isTakingBets;
    private HashMap<String, Integer> userDeathMap = new HashMap<String, Integer>();

    public Deathgame() { }
    public Deathgame(BotCore core) {
        acebotCore = core;
        acebotCore.subscribe("onMessage", new MessageActionListener());
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("deathgame");
        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        //accessExceptionMap = fillAccessExceptionMap(info);

        for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];
            if (isCommand("startdeath", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap));
                {
                    if (!isTakingBets)
                    {
                        isTakingBets = true;
                        acebotCore.addToQueue(channel, "Starting death contest, type a number in chat to place a bet on the death total!", Integer.parseInt(source));
                    }
                }
            }
            if (isCommand("stopdeath", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap));
                {
                    if (isTakingBets)
                    {
                        if (!isInteger(message.split(" ")[1]))
                            return;
                        isTakingBets = false;
                        int actualDeaths = Integer.parseInt(message.split(" ")[1]);
                        ArrayList<String> userList = new ArrayList<String>(userDeathMap.keySet());
                        ArrayList<Integer> deathList = new ArrayList<Integer>();
                        ArrayList<Integer> absoluteDeathList = new ArrayList<Integer>();

                        for (String user:userList)
                        {
                            int deaths = userDeathMap.get(user);
                            deathList.add(deaths);
                            absoluteDeathList.add(Math.abs(deaths - actualDeaths));
                        }

                        StringBuilder returnMessage = new StringBuilder("Results! ");
                        String[] ordinalArray = {"1st", "2nd", "3rd", "4th", "5th"};

                        for (int i = 0; i < 5; i++)
                        {
                            int minIndex = absoluteDeathList.indexOf(Collections.min(absoluteDeathList));
                            returnMessage.append(ordinalArray[i] + ": " + userList.get(minIndex) + " (" + deathList.get(minIndex) + "),  ");
                            userList.remove(minIndex);
                            deathList.remove(minIndex);
                            absoluteDeathList.remove(minIndex);
                        }
                        returnMessage.append("Players: " + userDeathMap.size());
                        userDeathMap.clear();
                        acebotCore.addToQueue(channel, returnMessage.toString(), Integer.parseInt(source));
                    }
                }
            }
        }
    }

    private class MessageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];
            if (isTakingBets)
                if (isInteger(message))
                {
                    userDeathMap.put(sender, Integer.parseInt(message));
                }

        }
    }
}

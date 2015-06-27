package DefaultPlugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import static u.u.*;

public class Whoami {

    private int whoamiUserAccess;
    private int whoamiChannelAccess;
    private int whoisUserAccess;
    private int whoisChannelAccess;
    private HashMap<String, Integer> whoamiAccessExceptionMap = new HashMap<String, Integer>();
    private HashMap<String, Integer> whoisAccessExceptionMap = new HashMap<String, Integer>();
    private BotCore acebotCore;

    public Whoami() { }

    public Whoami(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        acebotCore.subscribe("onLoad", new LoadActionListener());

        String[] cmdInfo = acebotCore.getCommandInfo("whoami");
        whoamiUserAccess = Integer.parseInt(cmdInfo[1]);
        whoamiChannelAccess = Integer.parseInt(cmdInfo[2]);

        //accessExceptionMap = fillAccessExceptionMap(info);

        for (int i = 3; i < cmdInfo.length; i++)
            whoamiAccessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));

        cmdInfo = acebotCore.getCommandInfo("whois");
        whoisUserAccess = Integer.parseInt(cmdInfo[1]);
        whoisChannelAccess = Integer.parseInt(cmdInfo[2]);

        //accessExceptionMap = fillAccessExceptionMap(info);

        for (int i = 3; i < cmdInfo.length; i++)
            whoisAccessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }

    private class LoadActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            acebotCore.createCommand("whoami", 2, 1);
            acebotCore.createCommand("whois", 2, 1);
        }
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];

            if (isCommand("whoami", message))
            {
                if (acebotCore.hasAccess(channel, sender, whoamiChannelAccess, whoamiUserAccess, whoamiAccessExceptionMap))
                {
                    int userAccess;

                    if (acebotCore.userAccessMap.containsKey(sender.toLowerCase()))
                        userAccess = acebotCore.userAccessMap.get(sender.toLowerCase());
                    else
                        userAccess = 1;

                    //if (isSubscriber(channel, sender))
                    //    userAccess = Math.max(2, userAccess);

                    if (acebotCore.isMod(channel, sender)) //Default Moderator Access
                        userAccess = Math.max(3, userAccess);

                    if (channel.substring(1).equalsIgnoreCase(sender))
                        userAccess = Math.max(4, userAccess);

                    if (sender.equalsIgnoreCase(acebotCore.getNick()))
                        userAccess = 6;

                    acebotCore.addToQueue(channel, "You, " + sender + " have access level " + userAccess + " (" + BotCore.RANKARRAY[userAccess] + ").", Integer.parseInt(source));
                }
            }

            if (isCommand("whois", message))
            {
                if (acebotCore.hasAccess(channel, sender, whoamiChannelAccess, whoamiUserAccess, whoamiAccessExceptionMap))
                {
                    String target;
                    if (!message.contains(" "))
                        target = sender;
                    else
                        target = message.split(" ")[1];
                    
                    int userAccess;

                    if (acebotCore.channelAccessMap.containsKey(target.toLowerCase()))
                    {
                        acebotCore.addToQueue(channel, target + " has access level " + acebotCore.channelAccessMap.get(target.toLowerCase()) + ".", Integer.parseInt(source));
                        return;
                    }

                    if (acebotCore.userAccessMap.containsKey(target.toLowerCase()))
                        userAccess = acebotCore.userAccessMap.get(target.toLowerCase());
                    else
                        userAccess = 1;

                    //if (isSubscriber(channel, target))
                    //    userAccess = Math.max(2, userAccess);

                    if (acebotCore.isMod(channel, target)) //Default Moderator Access
                        userAccess = Math.max(3, userAccess);

                    if (channel.substring(1).equalsIgnoreCase(target))
                        userAccess = Math.max(4, userAccess);

                    if (target.equalsIgnoreCase(acebotCore.getNick()))
                        userAccess = 6;

                    acebotCore.addToQueue(channel, target + " has access level " + userAccess + " (" + BotCore.RANKARRAY[userAccess] + ").", Integer.parseInt(source));
                }
            }
        }
    }
}

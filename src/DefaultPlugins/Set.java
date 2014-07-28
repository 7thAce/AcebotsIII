package DefaultPlugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.HashMap;

import static u.u.*;

public class Set {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

	public Set() {}
    public Set(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        acebotCore.subscribe("onLoad", new LoadActionListener());

        String[] cmdInfo = acebotCore.getCommandInfo("set");
        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        //accessExceptionMap = fillAccessExceptionMap(info);

        for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }

    private class CommandActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];

            if (isCommand("set", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    args = message.split(" ");

                    if (args.length != 3)
                        return;
                    if (!isInteger(args[2]))
                        return;
                    int requestedSetAccess = Integer.parseInt(args[2]);
                    int senderAccess;
                    try {
                    senderAccess = acebotCore.getUserAccessMap().get(sender.toLowerCase());
                    } catch (NullPointerException e1) {
                        senderAccess = 1;
                    }

                    int requestedUserAccess;
                    if  (acebotCore.getUserAccessMap().containsKey(args[1].toLowerCase()))
                        requestedUserAccess = acebotCore.getUserAccessMap().get(args[1].toLowerCase());
                    else
                        requestedUserAccess = 1;

                    if (acebotCore.getNick().equalsIgnoreCase(sender))
                        senderAccess = 6;

                    if (requestedSetAccess >= senderAccess)
                    {
                        acebotCore.addToQueue(channel, "You cannot set a user to an access higher than your own.", 1);
                        return;
                    }
                    if (requestedUserAccess >= senderAccess)
                    {
                        acebotCore.addToQueue(channel, "You modify a user with equal or higher access than your own.", 1);
                        return;
                    }

                    if (args[1].startsWith("#"))
                    {
                        acebotCore.getChannelAccessMap().put(args[1].toLowerCase(), Integer.parseInt(args[2]));
                        acebotCore.addToQueue(channel, "Added channel " + args[1] + " with access " + args[2] + ".", Integer.parseInt(source));
                    }
                    else
                    {
                        acebotCore.getUserAccessMap().put(args[1].toLowerCase(), Integer.parseInt(args[2]));
                        acebotCore.addToQueue(channel, "Added user " + args[1] + " with access " + args[2] + ".", Integer.parseInt(source));
                    }

                    PrintWriter writer = null;
                    try {
                        writer = new PrintWriter(BotCore.USERSFILEPATH);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    assert writer != null;
                    writer.close();
                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(new BufferedWriter(new FileWriter(BotCore.USERSFILEPATH, true)));
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    Object[] keys = acebotCore.getUserAccessMap().keySet().toArray();
                    Object[] values = acebotCore.getUserAccessMap().values().toArray();
                    Object[] keys2 = acebotCore.getChannelAccessMap().keySet().toArray();
                    Object[] values2 = acebotCore.getChannelAccessMap().values().toArray();
                    for (int i = 0; i < keys.length; i++) {
                        assert out != null;
                        out.println(keys[i] + " " + values[i]);
                    }
                    for (int i = 0; i < keys2.length; i++) {
                        assert out != null;
                        out.println(keys2[i] + " " + values2[i]);
                    }
                    assert out != null;
                    out.close();
                }
            }
        }
    }

    private class LoadActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            acebotCore.createCommand("set", 3, 1);
        }
    }
}

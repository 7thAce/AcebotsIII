package DefaultPlugins;

import static u.u.addHash;
import static u.u.getArgs;
import static u.u.isCommand;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import Bot.BotCore;
import Bot.Channel;

public class Joinleave {

	private BotCore acebotCore;
	private int userAccess;
	private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String,Integer>();
	
	public Joinleave() { }
	
	public Joinleave(BotCore core)
	{
		acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = core.getCommandInfo("join");

        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        for (int i = 4; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];

            boolean allSuccessful = true;

            if (isCommand("join", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    String[] channelArgs = message.split(" ");
                    for (int i = 1; i < channelArgs.length; i++)
                        if (!acebotCore.botJoinChannel(addHash(channelArgs[i])))
                            allSuccessful = false;
                    if (allSuccessful)
                        acebotCore.addToQueue(channel, "Joined channel(s) " + message.substring(6) + ".", Integer.parseInt(source));
                    else
                        acebotCore.addToQueue(channel, "Already in some channel(s):  " + message.substring(6) + ".", Integer.parseInt(source));
                }
            }

            if (isCommand("leave", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    if (acebotCore.getChannels().length == Math.max(message.split(" ").length - 1, 1))
                        return;

                    String[] channelArgs = message.split(" ");

                    if (channelArgs.length == 1)
                    {
                        acebotCore.botLeaveChannel(addHash(channel));
                        acebotCore.printlnAll("Left channel " + channel + ".", new Color(230, 230, 230));
                    }
                    else
                    {
                        for (int i = 1; i < channelArgs.length; i++)
                            if(!acebotCore.botLeaveChannel(addHash(channelArgs[i])))
                                allSuccessful = false;
                    //if (message.toLowerCase().endsWith("leave"))
                    //    return;

                    if (allSuccessful)
                        acebotCore.addToQueue(channel, "Left channel(s) " + message.substring(7) + ".", Integer.parseInt(source));
                    else
                        acebotCore.addToQueue(channel, "Not in some channel(s): " + message.substring(7) + ".", Integer.parseInt(source));
                    }
                }
            }
        }
    }
}

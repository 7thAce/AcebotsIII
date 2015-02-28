package DefaultPlugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import Bot.BotCore;

import javax.swing.*;

import static u.u.*;

public class Copypasta {

	private BotCore acebotCore;
	private int userAccess;
	private int channelAccess;
	private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();
	private HashMap<String, Boolean> channelEnabledMap = new HashMap<String, Boolean>();
	private ArrayList<String> pastaMenu = new ArrayList<String>();
    private int characterLevel = 200;
    private HashMap<String, Timer> channelTimerMap = new HashMap<String, Timer>();
    private ArrayList<String> subHypeQueue = new ArrayList<String>();

	public Copypasta() {
	}
	
	public Copypasta(BotCore core)
	{
		acebotCore = core;
		acebotCore.subscribe("onCommand", new CommandActionListener());
		acebotCore.subscribe("onMessage", new MessageActionListener());
        acebotCore.subscribe("onBotJoin", new BotJoinActionListener());
        acebotCore.subscribe("onSubscribe", new SubscribeActionListener());
		//acebotCore.subscribe("onMe", new EmoteActionListener());
		String[] cmdInfo = acebotCore.getCommandInfo("allowpasta");
		userAccess = Integer.parseInt(cmdInfo[1]);
		channelAccess = Integer.parseInt(cmdInfo[2]);
		for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
	}

    ActionListener resetTimer = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            String channel = subHypeQueue.get(0);
            subHypeQueue.remove(0);
            channelTimerMap.get(channel).stop();
            channelEnabledMap.put(channel, true);
            System.out.println("disallowing sub hype for prev channel");
        }
    };

	private class CommandActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            int source = Integer.parseInt(args[2]);
            String message = args[3];
            if (message.split(" ")[0].toLowerCase().endsWith("allowpasta")) //Consider making this a method in core (isCommand("thing"))
            {
                if (!acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                    return;
                channelEnabledMap.put(channel, false);
                acebotCore.addToQueue(channel, "Allowing Copy/Pasta.", source);
            }
            if (message.split(" ")[0].toLowerCase().endsWith("denypasta")) //Consider making this a method in core (isCommand("thing"))
            {
                if (!acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                    return;
                channelEnabledMap.put(channel, true);
                for (String prevMessage:acebotCore.getChannel(channel).getLastMessages())
                {
                    if (prevMessage != null)
                	    if (prevMessage.length() >= characterLevel)
                		    pastaMenu.add(prevMessage.toLowerCase());
                }
                acebotCore.addToQueue(channel, "Preventing Copy/Pasta.", source);
            }
            if (message.split(" ")[0].toLowerCase().endsWith("setpasta")) //Consider making this a method in core (isCommand("thing"))
            {
                if (!acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                    return;
                pastaMenu.add(message.split(" ", 2)[1].toLowerCase());
                acebotCore.addToQueue(channel, "Added to copy/pasta list.", source);
            }
		}
	}
	
	private class MessageActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];

            if (acebotCore.isMod(channel, sender))
                return;

			if (channelEnabledMap.get(channel))
			{
				for (String pasta:pastaMenu)
				{
					if (message.toLowerCase().contains(pasta))
					{
                        if (sender.equalsIgnoreCase("jtv"))
                            return;
                        if (acebotCore.isMod(channel, sender))
                            return;
						acebotCore.addToQueue(channel, "/timeout " + sender + " 300", 1);
						System.out.println("Pasta match: " + sender);
					}
				}
			}
            if (message.length() >= characterLevel && channelEnabledMap.get(channel))
                pastaMenu.add(message.toLowerCase());

		}	
	}

    private class BotJoinActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String chan = args[0];
            channelEnabledMap.put(args[0], false);
            channelEnabledMap.put(chan, false);
            Timer timerThing = new Timer(60000, resetTimer);
            channelTimerMap.put(chan, timerThing);
        }
    }

    private class SubscribeActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            if (channelEnabledMap.get(args[0]))
            {
                channelTimerMap.get(args[0]).start();
                channelEnabledMap.put(args[0], false);
                System.out.println("allowing sub hype for " + args[0]);
                subHypeQueue.add(args[0]);
            }
        }
    }
}

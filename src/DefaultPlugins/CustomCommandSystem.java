package DefaultPlugins;

import Bot.BotCore;
import data.CustomCommand;

import static u.u.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Date;
import java.util.HashMap;

public class CustomCommandSystem {

	private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String,Integer>();
    private HashMap<String, CustomCommand> commandMap = new HashMap<String, CustomCommand>(); //edit import
	
	public CustomCommandSystem() { }
	public CustomCommandSystem(BotCore core)
	{
		//NOTE TO SELF: Move .addToQueue to the core instead of .getQueue();
		acebotCore = core;
        core.subscribe("onCommand", new CommandActionListener());
        core.subscribe("onMessage", new MessageActionListener());
        core.subscribe("onLoad", new LoadActionListener());
        String[] cmdInfo = core.getCommandInfo("addcommand");

        userAccess = Integer.parseInt(cmdInfo[1]); 
        channelAccess = Integer.parseInt(cmdInfo[2]);

        for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
	}

    private class LoadActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            String line = "";
            FileReader fr = null;
            try {
                fr = new FileReader(BotCore.CUSTOMCMDSFILEPATH);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(fr);
            try {
                line = reader.readLine();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            do
            {
                try {
                    String[] args = line.split(" ", 4);
                    commandMap.put(args[0].toLowerCase(), new CustomCommand(args[0].split("#")[0], Integer.parseInt(args[1]), Integer.parseInt(args[2]), args[3], args[0].split("#")[1]));
                    line = reader.readLine();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } while (!isBlank(line));
        }
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

			if (isCommand("addcommand", message) || isCommand("createcommand", message) || isCommand("editcommand", message))
			{
                if (!acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                    return;
				args = message.split(" ");
				if (args.length <= 2)
                {
					acebotCore.addToQueue(channel, "Failed to add new command, please follow syntax.", Integer.parseInt(source));
                }
				else
				{
                    int initSize = commandMap.size();
					if (isInteger(args[2]) && isInteger(args[3]))
                    {
                        if (args[1].contains("#"))
                            commandMap.put(args[1].toLowerCase(), new CustomCommand(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), message.split(" ", 5)[4], args[1].split("#")[1]));
                        else
						    commandMap.put(args[1].toLowerCase() + channel, new CustomCommand(args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), message.split(" ", 5)[4], channel));
                    }
					else
                    {
                        if (args[1].contains("#"))
						    commandMap.put(args[1].toLowerCase(), new CustomCommand(args[1], message.split(" ", 3)[2], args[1].split("#")[1]));
                        else
                            commandMap.put(args[1].toLowerCase() + channel, new CustomCommand(args[1], message.split(" ", 3)[2], channel));
                    }

                    PrintWriter writer = null;

                    try {
                        writer = new PrintWriter(BotCore.CUSTOMCMDSFILEPATH);
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }

                    if (initSize == commandMap.size()) //Case edit
                    {
                        if (writer != null) {
                            writer.print("");
                            writer.close();
                        }
                    }

                    try {
                        writer = new PrintWriter(BotCore.CUSTOMCMDSFILEPATH);
                        for (CustomCommand cc:commandMap.values())
                        {
                            writer.println(cc.getCmd() + addHash(cc.getChannel()) + " " + cc.getUserAccess() + " " + cc.getChannelAccess() + " " + cc.getResponse());
                        }
                        writer.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }

                    acebotCore.addToQueue(channel, "Added/edited command " + args[1] + ".", Integer.parseInt(source));
				}
			}
			if (isCommand("delcommand", message))
			{
                if (!acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                    return;
				args = message.split(" ");
				if (commandMap.containsKey(args[1].toLowerCase() + channel))
				{
					commandMap.remove(args[1].toLowerCase() + channel);
					acebotCore.addToQueue(channel, "Deleted command " + args[1] + ".", Integer.parseInt(source));
                    PrintWriter writer;

                    try {
                        writer = new PrintWriter(BotCore.CUSTOMCMDSFILEPATH);
                        for (CustomCommand cc:commandMap.values())
                        {
                            writer.println(cc.getCmd() + addHash(cc.getChannel()) + " " + cc.getUserAccess() + " " + cc.getChannelAccess() + " " + cc.getResponse());
                        }
                        writer.close();
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
				}
				else
					acebotCore.addToQueue(channel, "Unable to delete Command " + args[1] + ".", Integer.parseInt(source));
			}

            if (commandMap.containsKey((message.toLowerCase().replace("/", "").replace("!",  "")).split(" ")[0] + channel)  && (message.startsWith("/") || message.startsWith("!")))
            {
                CustomCommand customCmd = commandMap.get(message.toLowerCase().replace("/", "").replace("!",  "").split(" ")[0] + channel);
                if (acebotCore.hasAccess(channel, sender, customCmd.getChannelAccess(), customCmd.getUserAccess(), null))
                {
                    String[] fromArray = CustomCommand.fromArray;
                    String[] toArray = {sender, sender, channel, channel.replace("#", ""), BotCore.sdf.format(new Date()), message.replace("/", "").replace("!",  "").split(" ")[0], acebotCore.getNick(), "", "", "", "", "", ""};

                    String resp = customCmd.getResponse();

                    for (int i = 0; i < fromArray.length; i++)
                        resp = resp.replace(fromArray[i], toArray[i]);
                    int i; //i is set to be local outside the for loop so we can use it for determining %+ since it's all of the rest of the args
                    String prevMessage = message;
                    for (i = 0; i < message.split(" ").length; i++)
                    {
                        resp = resp.replace("%" + i, message.split(" ")[i]);
                        if (prevMessage.equals(resp))
                            break;
                        else
                            prevMessage = resp;
                    }
/*					%+ will be the rest of the message that wasn't used in the arguments prior (+ for all other numbers)
 * 					So, you can have set arguments for a few with a "text box" style input at the end where the bot will repeat the end of the message
 * 					Example: !addcommand shoutout %b recommends you follow twitch.tv/%1! %+
 * 					Usage:   !shoutout 7thAce Kappa b
 * 					Output:  Acebots recommends you follow twitch.tv/7thace! Kappa b
 * 					The !say command can be recreated with this functionality.
 * 					Example: !addcommand 2 1 say %+
 * 					Would recreate the entire functionality of the say command.
*/
                    if (resp.contains("%+"))
                    {
                        try {
                            resp = resp.replace("%+", message.split(" ", i + 1)[i]);
                        } catch (IndexOutOfBoundsException e1) {
                            resp = resp.replace("%+", "");
                        }
                    }
                    acebotCore.addToQueue(channel, resp, Integer.parseInt(source));
                }
            }
		}
	}
	
	private class MessageActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{
			//System.out.println("not quite yet");
		}
	}
}

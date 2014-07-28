package DefaultPlugins;

import Bot.BotCore;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import static u.u.*;

public class Say {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String,Integer>();

    public Say() { }

    public Say(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = core.getCommandInfo("say");

        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            sayCommand(args[0], args[1], args[2], args[3]);
        }
    }

    private void sayCommand(String channel, String sender, String source, String message)
    {
        if (isCommand("say", message))
        {
            if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
            {
            	if (message.split(" ", 2)[1].startsWith("/") || message.split(" ", 2)[1].startsWith("."))
                    return;
                acebotCore.addToQueue(channel, message.split(" ", 2)[1], Integer.parseInt(source));
            }
        }
    }
}

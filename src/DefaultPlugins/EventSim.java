package DefaultPlugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static u.u.*;
public class EventSim {
    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;

    public EventSim() {
    }

    public EventSim(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        //acebotCore.subscribe("onMe", new EmoteActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("fire");
        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            int source = Integer.parseInt(args[2]);
            String message = args[3];
            if (isCommand("fire", message))
            {
                String[] words = message.split(" ", 4);
                if (words.length < 3)
                    return;
                int maxArgs = Integer.parseInt(words[1]);
                String event = words[2];
                acebotCore.fire(event, words[3].split(" ", maxArgs)); //not perfect but this might be edited
                //gotta use `` for now.
            }
        }
    }
}

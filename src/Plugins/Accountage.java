package Plugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static u.u.*;

public class Accountage {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

    public Accountage() { }
    public Accountage(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        acebotCore.createCommand("accountage", 3, 1);

        String[] cmdInfo = acebotCore.getCommandInfo("accountage");
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
            if (isCommand("accountage", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    BufferedReader reader;
                    String target;
                    if (!message.contains(" "))
                        target = sender;
                    else
                        target = message.split(" ")[1];
                    try {
                        URL url = new URL("https://api.twitch.tv/kraken/users/" + target);
                        reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        String blah = reader.readLine();
                        if(!(blah.startsWith("{\"error\""))){
                            blah = blah.split("\"created_at\":\"")[1];
                            String time = blah.split("\"")[0].replace("T", " at ");
                            acebotCore.addToQueue(channel, target + " created the account at " + time, Integer.parseInt(source));
                        } else {
                            acebotCore.addToQueue(channel, "That user does not exist.", Integer.parseInt(source));
                        }
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}

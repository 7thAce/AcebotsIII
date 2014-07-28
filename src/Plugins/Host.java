package Plugins;

import Bot.BotCore;
import Bot.Channel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import static u.u.*;

public class Host {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String,Integer>();
    HashMap<String, Timer> channelTimerMap = new HashMap<String, Timer>();

    public Host() {}
    public Host(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        acebotCore.subscribe("onLoad", new LoadActionListener());
        acebotCore.subscribe("onBotJoin", new BJoinActionListener());
        acebotCore.createEvent("onHost");
        acebotCore.createEvent("onUnhost");
        String[] cmdInfo = core.getCommandInfo("host");

        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

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

            if (isCommand("unhost", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    acebotCore.addToQueue(channel, "/unhost", Integer.parseInt(source));
                }
            }

            if (isCommand("host", message))
            {
                HashMap<String, String> streamInfo = new HashMap<String, String>();
                try {
                    URL url = new URL("http://api.justin.tv/api/stream/list.json?jsonp=&channel=" + channel.substring(1));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String blah = reader.readLine();
                    if(!(blah.equals("[]"))){
                        String[] data = blah.split(",\"");
                        for (int i = 0; i < data.length; i++)
                        {
                            String[] keyValue = data[i].split("\":");
                            streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                        }
                        return;
                    }
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    String hostingTarget;
                    String[] messageArgs = message.split(" ");
                    hostingTarget = messageArgs[1];
                    if (messageArgs.length == 3)
                    {
                        if (isInteger(messageArgs[2]))
                        {
                            channelTimerMap.get(channel).setInitialDelay(60000 * Integer.parseInt(messageArgs[2]));
                            channelTimerMap.get(channel).start();
                        }
                        else
                        {
                            //nothing yet
                        }
                    }
                    //We need to check the 2 case: Game (in the future)

                    streamInfo = new HashMap<String, String>();
                    try {
                        URL url = new URL("http://api.justin.tv/api/stream/list.json?jsonp=&channel=" + hostingTarget);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        String blah = reader.readLine();
                        if(!(blah.equals("[]"))){
                            String[] data = blah.split(",\"");
                            for (int i = 0; i < data.length; i++)
                            {
                                String[] keyValue = data[i].split("\":");
                                streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                            }
                            acebotCore.addToQueue(channel, hostingTarget + " is playing " + streamInfo.get("meta_game") + " : " + streamInfo.get("status") + ".", Integer.parseInt(source));
                        } else {
                            acebotCore.addToQueue(channel, hostingTarget + " is currently offline.", Integer.parseInt(source));
                        }
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    acebotCore.addToQueue(channel, "/host " + hostingTarget, 1);
                }
            }
        }
    }

    private class LoadActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            for (final String chan:acebotCore.getChannels())
            {
                ActionListener endHostingEvent = new ActionListener() {
                    public void actionPerformed(ActionEvent e1) {
                        acebotCore.addToQueue(chan, "/unhost", 1);
                        acebotCore.addToQueue(chan, "Hosting ended.", 1);
                    }
                };
                Timer tempTimer = new Timer(0, endHostingEvent);
                tempTimer.setRepeats(false);
                channelTimerMap.put(chan, tempTimer);
            }
        }
    }

    private class BJoinActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            final String chan = e.getActionCommand();
            if (!channelTimerMap.containsKey(chan))
            {
                ActionListener endHostingEvent = new ActionListener() {
                    public void actionPerformed(ActionEvent e1) {
                        acebotCore.addToQueue(chan, "/unhost", 1);
                        acebotCore.addToQueue(chan, "Hosting ended.", 1);
                    }
                };
                Timer tempTimer = new Timer(0, endHostingEvent);
                tempTimer.setRepeats(false);
                channelTimerMap.put(chan, tempTimer);
            }
        }
    }
}

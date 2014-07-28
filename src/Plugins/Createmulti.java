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

public class Createmulti {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

    public Createmulti() { }
    public Createmulti(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());

        String[] cmdInfo = acebotCore.getCommandInfo("createmulti");
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
            if (isCommand("createmulti", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap));
                {
                    String multiLink = "";
                    if (message.equalsIgnoreCase("!createmulti"))
                    {
                        BufferedReader reader;
                        HashMap<String, String> streamInfo = new HashMap<String, String>();
                        try {
                            URL url = new URL("http://api.justin.tv/api/stream/list.json?jsonp=&channel=" + channel.substring(1));
                            reader = new BufferedReader(new InputStreamReader(url.openStream()));
                            String blah = reader.readLine();
                            if(!(blah.equals("[]"))){
                                String[] data = blah.split(",\"");
                                for (int i = 0; i < data.length; i++)
                                {
                                    String[] keyValue = data[i].split("\":");
                                    streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                                }
                                String streamTitle = streamInfo.get("status");
                                if (streamTitle.contains("http://"))
                                {
                                    for (int i = 0; i < streamTitle.split(" ").length; i++)
                                        if (streamTitle.split(" ")[i].contains("http://"))
                                            multiLink = streamTitle.split(" ")[i];
                                }
                                if (!isBlank(multiLink))
                                {
                                    String[] multiNames = multiLink.split("/");
                                    int streamersStartAt;
                                    if (multiLink.contains("kadgar"))
                                        streamersStartAt = 4;
                                    else if (multiLink.contains("multitwitch"))
                                        streamersStartAt = 3;
                                    else if (multiLink.contains("multitwitch"))
                                        streamersStartAt = 1;
                                    else
                                        streamersStartAt = 3;
                                    for (int i = streamersStartAt; i < multiNames.length; i++)
                                    {
                                        System.out.println(multiNames[i] + "---------/addcommand multi " + multiLink);
                                        acebotCore.fire("onCommand", "#" + multiNames[i] + "``acebots``" + "0" + "``/addcommand multi " + multiLink);
                                    }
                                    acebotCore.addToQueue(channel, "Created multi link: " + multiLink, Integer.parseInt(source));
                                }
                            }
                        } catch (MalformedURLException e1) {
                            e1.printStackTrace();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    else
                    {
                        args = message.split(" ");
                        StringBuilder multi = new StringBuilder("http://kadgar.net/live/");
                        for (int i = 1; i < args.length; i++)
                            multi.append(args[i] + "/");

                        multiLink = multi.toString();
                        for (int i = 1; i < args.length; i++)
                        {
                            System.out.println("--------/addcommand multi " + multiLink);
                            acebotCore.fire("onCommand", "#" + args[i] + "``acebots``" + "0" + "``/addcommand multi " + multiLink);
                        }
                        acebotCore.addToQueue(channel, "Created multi link: " + multiLink, Integer.parseInt(source));
                    }
                }
            }
        }
    }

}

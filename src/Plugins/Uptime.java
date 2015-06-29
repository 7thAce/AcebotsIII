package Plugins;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import data.StreamStatus;

import Bot.BotCore;
import Bot.Channel;

import static u.u.*;

public class Uptime {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();
    private HashMap<String, StreamStatus> channelStreamMap = new HashMap<String, StreamStatus>();
    //Chosen to do this way over making a tiny data class for this.  If more info is added though, a data class may be helpful.

    public Uptime() { }
    public Uptime(BotCore core) {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        acebotCore.subscribe("onGameChange", new GameChangeActionListener());
        acebotCore.subscribe("onStreamGoesOnline", new StreamOnlineActionListener());
        acebotCore.subscribe("onStreamGoesOffline", new StreamOfflineActionListener());
        acebotCore.subscribe("onBotJoin", new BotJoinActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("uptime");
        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        for (int i = 3; i < cmdInfo.length; i++)
        {
            System.out.println("Added exception for " + cmdInfo[i].substring(1).toLowerCase() + " at access " + Integer.parseInt(cmdInfo[i].substring(0,1)));
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
        }
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];
            if (isCommand("uptime", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    boolean isOnline = acebotCore.getChannel(channel).getLiveStatus();
                    if (isOnline)
                    {
                        System.out.println(channel + "oni chan");
                        Calendar gameStartTime = channelStreamMap.get(channel.substring(1)).getGameStart();
                        long gameTimeDiff = (Calendar.getInstance().getTimeInMillis() - gameStartTime.getTimeInMillis()) / 1000;
                        System.out.println("gamediff " + gameTimeDiff);
                        long gameTimeDiffHours = (gameTimeDiff / 3600);
                        gameTimeDiff -= gameTimeDiffHours * 3600;
                        long gameTimeDiffMins = (gameTimeDiff / 60);

                        Calendar streamStartTime = channelStreamMap.get(channel).getStreamStart();
                        long streamTimeDiff = (Calendar.getInstance().getTimeInMillis() - streamStartTime.getTimeInMillis()) / 1000 + 3600 * 5;
                        System.out.println("streamdiff " + streamTimeDiff);
                        long streamTimeDiffHours = (streamTimeDiff / 3600);
                        streamTimeDiff -= streamTimeDiffHours * 3600;
                        long streamTimeDiffMins = (streamTimeDiff / 60);

                        if (Math.abs(streamTimeDiff) < 300000) //game time matches start time within 5 minutes                                                                                               /
                        {
                            acebotCore.addToQueue(channel, channel.substring(1) + " has been live for " + streamTimeDiffHours + " hours, " + streamTimeDiffMins + " minutes" +
                                    " and has been playing " + channelStreamMap.get(channel.substring(1)).getPreviousGameName() + " for the entire time." , Integer.parseInt(source));
                        }
                        else
                        {
                            acebotCore.addToQueue(channel, channel.substring(1) + " has been live for " + streamTimeDiffHours + ":" + streamTimeDiffMins +
                                    " and has been playing " + channelStreamMap.get(channel.substring(1)).getPreviousGameName() + " for " + gameTimeDiffHours + " hours, " + gameTimeDiffMins + "minutes." , Integer.parseInt(source));
                        }
                    }
                    else
                    {
                        if (null == channelStreamMap.get(channel.substring(1)))
                        {
                            acebotCore.addToQueue(channel, channel.substring(1) + " has been offline for as long as I can remember!", Integer.parseInt(source));
                        }
                        else
                        {
                            //long currentTime = Calendar.getInstance().getTimeInMillis();
                            long streamTimeDiff = Calendar.getInstance().compareTo(channelStreamMap.get(channel).getGameStart()) / 1000 + 3600 * 5;
                            long streamTimeDiffHours = (streamTimeDiff / 3600);
                            streamTimeDiff -= streamTimeDiffHours * 3600;
                            long streamTimeDiffMins = (streamTimeDiff / 60);

                            acebotCore.addToQueue(channel, channel.substring(1) + " has been offline for " + streamTimeDiffHours + " hours, " + streamTimeDiffMins + " minutes.", Integer.parseInt(source));
                        }
                    }
                }
            }
        }
    }

    private class GameChangeActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String newGame = args[1];
            if (!channelStreamMap.containsKey(channel))
                channelStreamMap.put(channel, new StreamStatus());
            //Detect if the stream has just gone online and use that time?
            channelStreamMap.get(channel).setNewGameName(newGame);
            System.out.println("[Offline Debug] Set game in uptime for " + channel + " to " + newGame);
        }
    }

    private class StreamOnlineActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = addHash(args[0]);
            if (!channelStreamMap.containsKey(channel))
                channelStreamMap.put(channel, new StreamStatus());
            //https://api.twitch.tv/kraken/streams/cirno_tv
            String blah = "";
            HashMap<String, String> streamInfo = new HashMap<String, String>();
            try {
                URL url = new URL("https://api.twitch.tv/kraken/streams/" + channel.substring(1));
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                blah = reader.readLine();
                if(!(blah.endsWith("exist\"}")))
                {
                    String[] data = blah.split(",\"");
                    for (int i = 0; i < data.length; i++)
                    {
                        String[] keyValue = data[i].split("\":");
                        streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                        if (keyValue[0].equals("created_at"))
                            break;
                        //System.out.println(keyValue[0] + " --> " + keyValue[1]);
                    }
                }

                String[] timeParts = streamInfo.get("created_at").split("T")[1].split(":");
                String[] dateParts = streamInfo.get("created_at").split("T")[0].split("-");
                Calendar onlineDateTime = Calendar.getInstance();

                //Did you know that java months are 0 indexed?  Cool.
                onlineDateTime.set(Integer.parseInt(dateParts[0]), Integer.parseInt(dateParts[1]) - 1, Integer.parseInt(dateParts[2]), Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]), 0);
                channelStreamMap.get(channel).setStreamStart(onlineDateTime);
                System.out.println("Set time for " + channel);

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    private class StreamOfflineActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            if (!channelStreamMap.containsKey(channel))
                channelStreamMap.put(channel, new StreamStatus());
            //channelStreamMap.get(channel).setStreamStart(new Date());
            channelStreamMap.get(channel).setNewGameName("Offline");
        }
    }

    private class BotJoinActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            //channelStreamMap.put(getArgs(e)[0], new StreamStatus());
        }
    }
}

/*
    SimpleDateFormat sdf = new SimpleDateFormat("y M d H m s");
    String date = sdf.format(new Date());
                    end.set(Integer.parseInt(date.split(" ")[0]), Integer.parseInt(date.split(" ")[1]), Integer.parseInt(date.split(" ")[2]), Integer.parseInt(date.split(" ")[3]), Integer.parseInt(date.split(" ")[4]), Integer.parseInt(date.split(" ")[5]));

    /*
    long milliseconds2 = end.getTimeInMillis();
    long diff = milliseconds1 - milliseconds2;
    long diffDays = (int)(diff / (1000 * 60 * 60 * 24));
    Date startDate = new Date();
 */

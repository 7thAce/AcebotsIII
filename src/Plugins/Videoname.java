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

public class Videoname {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

    public Videoname() { }
    public Videoname(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onMessage", new MessageActionListener());

        String[] cmdInfo = acebotCore.getCommandInfo("videolookup");
        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        //accessExceptionMap = fillAccessExceptionMap(info);

        for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }

    private class MessageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];

            if (message.contains("youtube.com/watch"))
                if (!acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap) && !(sender.equalsIgnoreCase("idolmasterbot") || sender.equalsIgnoreCase("nightbot")))
                    acebotCore.addToQueue(channel, getYoutubeInfo(message), BotCore.OUTPUT_CHANNEL);

            if (message.contains("twitch.tv/") && (message.contains("/c/") || message.contains("/b/")))
                if (!acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap) && !(sender.equalsIgnoreCase("idolmasterbot") || sender.equalsIgnoreCase("nightbot")))
                    acebotCore.addToQueue(channel, getTwitchInfo(message), BotCore.OUTPUT_CHANNEL);
        }
    }

    public String getYoutubeInfo(String message)
    {
        URL url;
        StringBuilder allWords = new StringBuilder();
        String[] parts = message.split(" ");
        for (String word:parts)
        {
            if (word.contains("youtube.com/watch"))
            {
                if (word.contains("http"))
                    word = word.split("://")[1];
                try {
                    url = new URL("https://" + word);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;
                    while ((str = in.readLine()) != null) {
                        allWords.append(str);
                    }
                    in.close();
                } catch (MalformedURLException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }        //allWords.toString().split("</script><title>")[1].split(" - YouTube")[0]
        try{
        String title = "\"" + allWords.toString().split("<title>")[1].split(" - YouTube")[0] + "\"";
            String uploader =  allWords.toString().replace("https://", "http://").split("<link itemprop=\"url\" href=\"http://www.youtube.com/user/")[1].split("\">")[0];
            //BotCore.screenPrint("Linked YouTube video: " + title + " by " + uploader);
            return "Linked YouTube video: " + title + " by " + uploader;
        } catch (Exception e)
        {
            System.out.println("AW: " + allWords.toString());
            System.out.println("Msg: " + message);
            e.printStackTrace();
        }
        return "";
    }

    public String getTwitchInfo(String message)
    {
        URL url;
        StringBuilder allWords = new StringBuilder();
        String[] parts = message.split(" ");
        for (String word:parts)
        {
            if (word.contains("twitch.tv/") && (word.contains("/c/") || word.contains("/b/")))
            {
                word = word.substring(word.indexOf("http"));
                if (word.toLowerCase().startsWith("https"))
                    word = "http" + word.substring(5);
                try {
                    url = new URL(word);
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str;
                    while ((str = in.readLine()) != null) {
                        allWords.append(str);
                    }
                    in.close();
                } catch (MalformedURLException e) {

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String temp = allWords.toString().split("<title>")[1].split("</title>")[0];
        String title = "\"" + temp.split(" - ")[1] + "\"";
        String uploader =  temp.split(" - ")[0];
        return "Linked Twitch VOD - " + title + " by " + uploader;
    }
}

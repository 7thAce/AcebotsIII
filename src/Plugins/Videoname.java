package Plugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
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

            if (message.contains("youtube.com/watch") || message.contains("youtu.be/"))
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap) && !(sender.equalsIgnoreCase("idolmasterbot") || sender.equalsIgnoreCase("nightbot")))
                    acebotCore.addToQueue(channel, getYoutubeInfo(message), BotCore.OUTPUT_CHANNEL);

            //if (message.contains("twitch.tv/") && (message.contains("/c/") || message.contains("/b/")))
            //    if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap) && !(sender.equalsIgnoreCase("idolmasterbot") || sender.equalsIgnoreCase("nightbot")))
            //        acebotCore.addToQueue(channel, getTwitchInfo(message), BotCore.OUTPUT_CHANNEL);
        }
    }

    public String getYoutubeInfo(String message)
    {
        URL url;
        StringBuilder allWords = new StringBuilder();
        String[] parts = message.split(" ");
        for (String word:parts)
        {
            if (word.contains("youtu.be/"))
            {
                if (word.contains("?list="))
                {
                    return "";
                }
                else
                {
                    word = word.replace("youtu.be/", "www.youtube.com/watch?v=");
                }
            }

            if (word.contains("youtube.com/watch"))
            {
                String videoID = word.split("youtube.com/watch\\?v=")[1];
                if (videoID.contains("?"))
                    videoID = videoID.split("\\?")[0];
                try {                                                                                 //YOU NEED A GOOGLE API KEY INSTRUCTIONS EVENTUALLY TM
                    url = new URL("https://www.googleapis.com/youtube/v3/videos?id=" + videoID + "&key=AIzaSyBD-quFoBeVxEMdgZAcIDUQfokqwBepyOE&part=snippet,contentDetails&fields=items(snippet/title,snippet/channelTitle,contentDetails/duration)");
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

        //This is pretty miserable but also fast.

        String wordsString = allWords.toString().replace("\\\"", "\"");
        String title = "";
        title = title.replace("youtu.be/", "www.youtube.com/watch?v=");

        try {
            title = wordsString.split("\"title\": \"")[1].split("\",  ")[0];
        } catch (java.lang.ArrayIndexOutOfBoundsException e1) {
            System.out.println("Videoname failed: " + message + " :: " + title);
            e1.printStackTrace();
        }

        String uploader = wordsString.split("\"channelTitle\": \"")[1].split("\"", 2)[0];
        String duration = wordsString.split("\"duration\": \"")[1].split("\"", 2)[0].substring(2);

        String minutes = "0";
        String seconds = "";
        String hours = "";

        //Thanks to Roflcopter
        if (duration.contains("H")) {
            hours = duration.split("H")[0];
            duration = duration.substring(duration.indexOf("H") + 1);
        }
        if (duration.contains("M")) {
            minutes = duration.split("M")[0];
            duration = duration.substring(duration.indexOf("M") + 1);
        }
        if (duration.contains("S")) {
            seconds = duration.split("S")[0];
        }
        if (!hours.equals(""))
        {
            if (minutes.equals(""))
                minutes = "00";
            if (minutes.length() == 1)
                minutes = "0" + minutes;
        }
            if (seconds.equals(""))
                seconds = "00";
            if (seconds.length() == 1)
                seconds = "0" + seconds;
        if (hours.equals(""))
            return "Linked YouTube video: \"" + title + "\" by " + uploader + ". [" + minutes + ":" + seconds + "]";
        else
            return "Linked YouTube video: \"" + title + "\" by " + uploader + ". [" + hours + ":" + minutes + ":" + seconds + "]";

        //allWords.toString().split("</script><title>")[1].split(" - YouTube")[0]
        /*try{
        String title = "\"" + allWords.toString().split("<title>")[1].split(" - YouTube")[0] + "\"";
            String uploader =  allWords.toString().split("\"author\": ")[1].split("\",")[0].substring(1);
            //BotCore.screenPrint("Linked YouTube video: " + title + " by " + uploader);
        } catch (Exception e)
        {
            //System.out.println("AW: " + allWords.toString());
            //System.out.println("Msg: " + message);
            e.printStackTrace();
        }      */
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

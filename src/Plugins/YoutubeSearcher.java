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

import static u.u.getArgs;
import static u.u.isCommand;

public class YoutubeSearcher {
    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

    public YoutubeSearcher() { }
    public YoutubeSearcher(BotCore core) {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("youtube");
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
            if (isCommand("yt", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    String keywords = message.split(" ", 2)[1];
                    StringBuilder allWords = new StringBuilder();
                    try {
                        URL url = new URL("https://www.googleapis.com/youtube/v3/search?part=snippet&q=" + keywords.replace(" ", "+") + "&type=video&key=AIzaSyBD-quFoBeVxEMdgZAcIDUQfokqwBepyOE");
                        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                        String str;
                        while ((str = in.readLine()) != null) {
                            allWords.append(str + "\n");
                        }
                        in.close();
                    } catch (MalformedURLException e1) {

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    String title = "";
                    String videoID = "";
                    String uploader = "";
                    for (String line:allWords.toString().split("\n")) {
                        if (line.contains("\"videoId\": \"") && videoID.equals(""))
                            videoID = line.split("\"videoId\": \"")[1].split("\"")[0];
                        if (line.contains("\"title\": \"") && title.equals("")) {
                            String temp = line.split("\"title\": \"")[1];
                            title = temp.substring(0, temp.length() - 2);
                        }
                        if (line.contains("\"channelTitle\": \"") && uploader.equals(""))
                            uploader = line.split("\"channelTitle\": \"")[1].split("\"")[0];
                    }
                    acebotCore.addToQueue(channel, "https://www.youtube.com/watch?v=" + videoID + " - " + title + " by " + uploader + ".", Integer.parseInt(source));
                }
            }
        }
    }
}

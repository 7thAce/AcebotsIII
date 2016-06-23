package DefaultPlugins;

import Bot.BotCore;

import javax.xml.ws.Response;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

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
            if (isCommand("sim", message)) {
                if (sender.equalsIgnoreCase(acebotCore.getNick())) {
                    acebotCore.onUnknown(message.substring(5));
                }
            }

            if (isCommand("fire", message))
            {
                if (sender.equalsIgnoreCase(acebotCore.getNick()))
                {
                    String[] words = message.split(" ", 4);
                    if (words.length < 3)
                        return;
                    System.out.println("Simulating " + message);
                    int maxArgs = Integer.parseInt(words[1]);
                    String event = words[2];
                    acebotCore.fire(event, words[3].split(" ", maxArgs)); //not perfect but this might be edited
                    //gotta use `` for now.
                }
            }
            if (isCommand("titletest", message))
            {
                String url = "https://api.twitch.tv/kraken/channels/7thace?oauth_token=xz1u8mr7wnp7ogy88gzc9jwadl7d1y";
                try
                {
                    URL obj = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

                    conn.setRequestProperty("Accept", "application/vnd.twitchtv.v2+json");
                    conn.setRequestMethod("PUT");
                    conn.setDoOutput(true);

                    String data = "channel[status]=Acebots testing&channel[game]=Rocksmith 2014";
                    OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                    out.write(data);
                    out.flush();

                    for (Map.Entry<String, List<String>> header : conn.getHeaderFields().entrySet()) {
                        System.out.println(header.getKey() + "=" + header.getValue());
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}

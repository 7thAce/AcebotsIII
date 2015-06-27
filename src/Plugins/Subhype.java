package Plugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static u.u.getArgs;

public class Subhype {

    private BotCore acebotCore;

    public Subhype() { }
    public Subhype(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onMessage", new SubListener());
        //why don't I just use onsubscribe?
    }

    private class SubListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];
            if (sender.equalsIgnoreCase("twitchnotify") && message.endsWith("subscribed!"))
            {
                System.out.println(message + "/" + sender + "/" + channel + "/ SUB HYPE");
                if (channel.equalsIgnoreCase("#cirno_tv"))
                    acebotCore.addToQueue("#cirno_tv", "Get Honked on " + message.split(" ")[0] + "!  Welcome to the Baka Brigade!", BotCore.OUTPUT_CHANNEL);
                else if (channel.equalsIgnoreCase("#professorbroman"))
                    acebotCore.addToQueue("#professorbroman", message.split(" ")[0] + " is now Legendary!  Welcome to the Broforce!",BotCore.OUTPUT_CHANNEL);
                else if (channel.equalsIgnoreCase("#azorae"))
                    acebotCore.addToQueue("#azorae", "Take a peek at the new sub " + message.split(" ")[0] + "!",BotCore.OUTPUT_CHANNEL);
                else if (channel.equalsIgnoreCase("#admiral_bahroo"))
                    acebotCore.addToQueue("#admiral_bahroo", "Welcome to the Rescue Force " + message.split(" ")[0] + "!",BotCore.OUTPUT_CHANNEL);
                else if (channel.equalsIgnoreCase("#noobest"))
                    acebotCore.addToQueue("#noobest", "Raise your Suns for the new sub " + message.split(" ")[0] + "!",BotCore.OUTPUT_CHANNEL);
                //else if (channel.equalsIgnoreCase("#geoff"))
                    //acebotCore.addToQueue("#geoff", "Welcome to the Geoof Troop " + message.split(" ")[0] + "!  You are the cutest goof in the troop!", BotCore.OUTPUT_CHANNEL);
            }
            else if (sender.equalsIgnoreCase("twitchnotify") && message.contains("subscribed for"))
            {
                if (channel.equalsIgnoreCase("#admiral_bahroo"))
                    acebotCore.addToQueue("#admiral_bahroo", "Thank you for your continued support to the Rescue Force " + message.split(" ")[0] + "!",BotCore.OUTPUT_CHANNEL);
                if (channel.equalsIgnoreCase("#cirno_tv"))
                    acebotCore.addToQueue("#cirno_tv", "Thank you for your continued support to the Baka Brigade " + message.split(" ")[0] + "!",BotCore.OUTPUT_CHANNEL);
            }
        }
    }
}

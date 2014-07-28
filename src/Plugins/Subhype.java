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
    }

    private class SubListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];
            if (sender.equalsIgnoreCase("twitchnotify"))
            {
                System.out.println(message + "/" + sender + "/" + channel + "/ SUB HYPE");
                if (channel.equalsIgnoreCase("#cirno_tv"))
                    acebotCore.addToQueue("#cirno_tv", "Raise your Fairies for the new sub " + message.split(" ")[0] + "!", BotCore.OUTPUT_CHANNEL);
                if (channel.equalsIgnoreCase("#professorbroman"))
                    acebotCore.addToQueue("#professorbroman", message.split(" ")[0] + " is now the sexiest human being!  Raise your waifus!",BotCore.OUTPUT_CHANNEL);
                if (channel.equalsIgnoreCase("#azorae"))
                    acebotCore.addToQueue("#azorae", "Take a peek at the new sub " + message.split(" ")[0] + "!",BotCore.OUTPUT_CHANNEL);
                if (channel.equalsIgnoreCase("#witwix"))
                    System.out.println("witwix hype message here");
                //if (channel.equalsIgnoreCase("#ubergoos"))
                   // acebotCore.addToQueue("#ubergoos", "Raise your HONKs for the new sub " + message.split(" ")[0] + "!",BotCore.OUTPUT_CHANNEL);
                if (channel.equalsIgnoreCase("#noobest"))
                    acebotCore.addToQueue("#noobest", "Raise your Bears for the new sub " + message.split(" ")[0] + "!",BotCore.OUTPUT_CHANNEL);
            }
        }
    }
}

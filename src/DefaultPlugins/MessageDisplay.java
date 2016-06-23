package DefaultPlugins;


import Bot.BotCore;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import static u.u.*;

public class MessageDisplay {

    private BotCore acebotCore;
    /* private String[] infoVariables = {"Prefix (Sub, Turbo, Admin, Staff)", "Color", "Something"}; Array in case they want to add things */

    public MessageDisplay() { }

    public MessageDisplay(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onMessage", new MessageActionListener());
        acebotCore.subscribe("onMe", new EmoteActionListener());
        acebotCore.subscribe("onPrivateMessage", new PMActionListener());
        acebotCore.subscribe("onTimeout", new TimeoutActionListener());
        acebotCore.subscribe("onBan", new BanActionListener());
        //acebotCore.subscribe("onPrivateMessage", new PrivateActionListener());
    }

    private class TimeoutActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String bannedUser = args[1];
            String duration = args[2];

            if (args.length > 3) {
                String reason = args[3];

                acebotCore.printChannel(channel, "[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR); //Time stamp
                acebotCore.printlnChannel(channel, bannedUser + " has been timed out for " + duration + " seconds. (" + reason + ")", new Color(67, 202, 247));

                acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printlnAll(channel + ": " + bannedUser + " has been timed out for " + duration + " seconds. (" + reason + ")", new Color(67, 202, 247));
            }
            else {
                acebotCore.printChannel(channel, "[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR); //Time stamp
                acebotCore.printlnChannel(channel, bannedUser + " has been timed out for " + duration + " seconds.", new Color(67, 202, 247));

                acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printlnAll(channel + ": " + bannedUser + " has been timed out for " + duration + " seconds.", new Color(67, 202, 247));
            }
        }
    }

    private class BanActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String bannedUser = args[1];
            String reason;
            if (args.length > 2) {
                reason = args[2];
                acebotCore.printChannel(channel, "[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR); //Time stamp
                acebotCore.printlnChannel(channel, bannedUser + " has been banned. (" + reason + ")", new Color(67, 202, 247));

                acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printlnAll(channel + ": " + bannedUser + " has been banned. (" + reason + ")", new Color(67, 202, 247));
            }
            else
            {
                acebotCore.printChannel(channel, "[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR); //Time stamp
                acebotCore.printlnChannel(channel, bannedUser + " has been banned.", new Color(67, 202, 247));

                acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printlnAll(channel + ": " + bannedUser + " has been banned.", new Color(67, 202, 247));
            }
        }
    }

    private class PMActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String sender = args[0];
            String message = args[1];

            /*acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
            acebotCore.printAll(messageInfo[0] + args[1] + args[0].substring(0,4) + ": ", new Color(r, g, b));
            acebotCore.printlnAll(args[2], new Color(230, 230, 230));
            System.out.println("WHISPER: [" + sender + "] " + message);           */
        }
    }

    private class MessageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];
            String userColor = args[3];
            boolean isSubscriber = args[4].equals("1");
            boolean isTurbo = args[5].equals("1");
            String userType = args[6];
            StringBuilder prefix = new StringBuilder();
            if (!args[1].equals("jtv"))
            {
                int r=0,g=0,b=0;
                try {
                    if (userColor.equals("")) {
                        r = g = b = 100;
                    } else {
                        r = Integer.valueOf(userColor.substring(1, 3), 16);
                        g = Integer.valueOf(userColor.substring(3, 5), 16);
                        b = Integer.valueOf(userColor.substring(5, 7), 16);
                    }
                } catch (Exception e1)
                {
                    System.out.println("Color error: " + userColor + " :|: " + sender + " :|: " + message);
                    e1.printStackTrace();
                }

                if (userType.equals("mod")) {
                    if (sender.equalsIgnoreCase(channel.substring(1)))
                        prefix.append("B");
                    else
                        prefix.append("M");
                }
                else if (userType.equals("global_mod")) {
                    prefix.append("G"); } else
                if (userType.equals("admin")) {
                    prefix.append("A"); }else
                if (userType.equals("staff")) {
                    prefix.append("F"); }

                if (isSubscriber)
                    prefix.append("S");
                if (isTurbo)
                    prefix.append("T");

                String stringPrefix;
                if (prefix.length() > 0)
                    stringPrefix = "[" + prefix.toString() + "]";
                else
                    stringPrefix = "";

                acebotCore.printChannel(args[0], "[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR); //Time stamp
                acebotCore.printChannel(args[0], stringPrefix + sender + ": ", new Color(r, g, b));
                acebotCore.printlnChannel(args[0], args[2], new Color(230, 230, 230));

                acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printAll(stringPrefix + sender + channel.substring(0,4) + ": ", new Color(r, g, b));
                acebotCore.printlnAll(args[2], new Color(230, 230, 230));
            }
            else
            {
                //old jtv stuff, shouldn't be used
                /*System.out.println(message);
                String[] messageArgs = message.split(" ");
                if (messageArgs.length < 3)
                    return;
                if (messageArgs[2].equalsIgnoreCase(acebotCore.getNick()))
                    return;
                if (messageArgs[0].equals("SPECIALUSER")) //Options: Subscriber, staff, admin, turbo, ?
                {
                    if (acebotCore.isMod(channel, sender))
                    {
                        System.out.println("A mod");
                        if (sender.equalsIgnoreCase(channel.substring(1)))
                            messageInfo[0] += "B";
                        else
                            messageInfo[0] += "M";
                    }

                    if (messageArgs[2].equals("subscriber"))
                        messageInfo[0] += "S";
                    else if (messageArgs[2].equals("turbo"))
                        messageInfo[0] += "T";
                    else if (messageArgs[2].equals("admin"))
                        messageInfo[0] += "A";
                    else if (messageArgs[2].equals("staff"))
                        messageInfo[0] += "F";

                }
                else if (messageArgs[0].equals("USERCOLOR"))
                    messageInfo[1] = messageArgs[2];

                //else if (messageArgs[0].equals("EMOTESET"))
                //        System.out.println(message.split(" ")[1] + " is subbed to " + (message.split(",").length - 1));*/
            }
            //if (args[1].equalsIgnoreCase("twitchnotify"))
            //    System.out.println(args[2]);
        }
    }

    private class EmoteActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];
            String userColor = args[3];
            boolean isSubscriber = args[4].equals("1");
            boolean isTurbo = args[5].equals("1");
            String userType = args[6];

            StringBuilder prefix = new StringBuilder();

            int r=0,g=0,b=0;
            try {
                if (userColor.equals("")) {
                    r = g = b = 100;
                } else {
                    r = Integer.valueOf(userColor.substring(1, 3), 16);
                    g = Integer.valueOf(userColor.substring(3, 5), 16);
                    b = Integer.valueOf(userColor.substring(5, 7), 16);
                }
            } catch (Exception e1)
            {
                System.out.println("Color error: " + userColor + " :|: " + sender + " :|: " + message);
                e1.printStackTrace();
            }

            if (userType.equals("mod")) {
                if (sender.equalsIgnoreCase(channel.substring(1)))
                    prefix.append("B");
                else
                    prefix.append("M");
            }
            else if (userType.equals("global_mod")) {
                prefix.append("G"); } else
            if (userType.equals("admin")) {
                prefix.append("A"); }else
            if (userType.equals("staff")) {
                prefix.append("F"); }

            if (isSubscriber)
                prefix.append("S");
            if (isTurbo)
                prefix.append("T");

            String stringPrefix;
            if (prefix.length() > 0)
                stringPrefix = "[" + prefix.toString() + "]";
            else
                stringPrefix = "";

            //                acebotCore.printChannel(args[0], stringPrefix + sender + ": ", new Color(r, g, b));


            acebotCore.printChannel(channel,  "[" + BotCore.sdf.format(new Date())+ "] ", graphics.acebotsthree.TIMECOLOR);
            acebotCore.printlnChannel(channel, stringPrefix + sender + " " + message, new Color(r,g,b));

            acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
            acebotCore.printlnAll(stringPrefix + sender + " " + message.substring(0,4) + " " + message, new Color(r,g,b));
        }
    }
    /*
    private class PrivateActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            System.out.println(args[0] + ": " + args[1]);
        }
    }   */
}

package DefaultPlugins;


import Bot.BotCore;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import static u.u.*;

public class MessageDisplay {

    private BotCore acebotCore;
    private String[] messageInfo = {"",""};
    /* private String[] infoVariables = {"Prefix (Sub, Turbo, Admin, Staff)", "Color", "Something"}; Array in case they want to add things */

    public MessageDisplay() { }

    public MessageDisplay(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onMessage", new MessageActionListener());
        acebotCore.subscribe("onMe", new EmoteActionListener());
    }

    private class MessageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];

            if (!args[1].equals("jtv"))
            {
                int r,g,b;
                if (messageInfo[1].equals("")) {
                    r = g = b = 100;
                } else {
                    r = Integer.valueOf(messageInfo[1].substring(1, 3), 16);
                    g = Integer.valueOf(messageInfo[1].substring(3, 5), 16);
                    b = Integer.valueOf(messageInfo[1].substring(5, 7), 16);
                }

                if (!messageInfo[0].equals(""))
                    messageInfo[0] = "[" + messageInfo[0] + "]";

                acebotCore.printChannel(args[0], "[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printChannel(args[0], messageInfo[0] + args[1] + ": ", new Color(r, g, b));
                acebotCore.printlnChannel(args[0], args[2], new Color(230, 230, 230));

                acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printAll(messageInfo[0] + args[1] + args[0].substring(0,4) + ": ", new Color(r, g, b));
                acebotCore.printlnAll(args[2], new Color(230, 230, 230));

                messageInfo[0] = "";
                messageInfo[1] = "";
            }
            else
            {
                String[] messageArgs = message.split(" ");
                if (messageArgs.length < 3)
                    return;
                if (messageArgs[2].equalsIgnoreCase(acebotCore.getNick()))
                    return;
                if (messageArgs[0].equals("SPECIALUSER")) //Options: Subscriber, staff, admin, turbo, ?
                {
                    System.out.println("SU Line: " + messageArgs[0]);
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
            }
            if (args[1].equalsIgnoreCase("twitchnotify"))
                System.out.println(args[2]);
        }
    }

    private class EmoteActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);

            acebotCore.printChannel(args[0],  "[" + BotCore.sdf.format(new Date())+ "] ", graphics.acebotsthree.TIMECOLOR);
            acebotCore.printlnChannel(args[0], args[1] + " " + args[2], new Color(180,0,0));

            acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
            acebotCore.printlnAll(args[1] + args[0].substring(0,4) + " " + args[2], new Color(180,0,0));
        }
    }
}

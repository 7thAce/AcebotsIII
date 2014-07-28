package DefaultPlugins;


import Bot.BotCore;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import static u.u.*;

public class MessageDisplay {

    private BotCore acebotCore;

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
            if (!args[1].equals("jtv"))
            {
                acebotCore.printChannel(args[0], "[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printChannel(args[0], args[1] + ": ", new Color(180,0,0));
                acebotCore.printlnChannel(args[0], args[2], new Color(230, 230, 230));

                acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printAll(args[1] + args[0].substring(0,4) + " ", new Color(180,0,0));
                acebotCore.printlnAll(args[2], new Color(230, 230, 230));
            }
        }
    }

    private class EmoteActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String[] args = getArgs(e);

            acebotCore.printChannel(args[0],  "[" + BotCore.sdf.format(new Date())+ "] ", graphics.acebotsthree.TIMECOLOR);
            acebotCore.printlnChannel(args[0], args[1] + ": " + args[2], new Color(180,0,0));

            acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
            acebotCore.printlnAll(args[1] + args[0].substring(0,4) + ": " + args[2], new Color(180,0,0));
        }
    }
}

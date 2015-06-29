package Plugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static u.u.getArgs;
import static u.u.isCommand;

public class Binpdf {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

    public Binpdf() { }
    public Binpdf(BotCore core) {

        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("binpdf");
        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        //accessExceptionMap = fillAccessExceptionMap(info);

        for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];
            if (isCommand("binpdf", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    String[] binArgs = message.split(" ");
                    int accuracy = Integer.parseInt(binArgs[1]);
                    int hits = Integer.parseInt(binArgs[2]);
                    int trials = Integer.parseInt(binArgs[3]);
                    double probability = Math.pow(accuracy / 100.0, hits) * Math.pow((100.0 - accuracy) / 100.0, (trials - hits)) * (factorial(trials) * 1.0 / (1.0 * (factorial(hits) * factorial(trials - hits))));
                    acebotCore.addToQueue(channel, "The probability of " + hits + " successes in " + trials + " trials for " + accuracy + "% success has a " + Math.round(probability * 10000.0) / 100.0 + "% chance of happening.", Integer.parseInt(source));
                }
            }
        }

        private int factorial(int number)
        {
            int returnValue = 1;
            for (int i = 2; i <= number; i++)
                returnValue = returnValue * i;
            return returnValue;
        }
    }
}

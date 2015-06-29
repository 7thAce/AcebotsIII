package data;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static u.u.*;

public class Temperature {

    private BotCore acebotCore;

    public Temperature() { }
    public Temperature(BotCore core)
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
            if (message.contains("F") || message.contains("C"))
            {
                for (String word:message.split(" "))
                {
                    if (word.endsWith("F"))
                    {
                        try {
                            String tempInF = word.substring(0, word.length() - 1);
                            double tempInC;
                            if (isDouble(tempInF))
                            {
                                tempInC = (Double.parseDouble(tempInF) - 32) / 1.8;
                                acebotCore.addToQueue(channel, "Temperature detected: " + word + " is " + Math.round(10 * tempInC) / 10.0 + "C.", 1);
                                return;
                            }
                        } catch (Exception e1)
                        {
                            return;
                        }
                    }
                    if (word.endsWith("C"))
                    {
                        try
                        {
                            String tempInC = word.substring(0, word.length() - 1);
                            double tempInF;
                            if (isDouble(tempInC))
                            {
                                tempInF = Double.parseDouble(tempInC) * 1.8 + 32;
                                acebotCore.addToQueue(channel, "Temperature detected: " + word + " is " + Math.round(10 * tempInF) / 10.0 + "F.", 1);
                            }
                        } catch (Exception e1)
                        {
                            return;
                        }
                    }
                }
            }
        }
    }
}

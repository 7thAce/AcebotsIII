package Bot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.Timer;

import static u.u.addHash;

public class Queue implements ActionListener {

    private BotCore acebotCore;
    private int activeDelay;
    private Timer messageDelay;
    private ArrayList<String> messagesQueue = new ArrayList<String>();
    private int[] priorityPosition = {0, 0, 0, 0, 0};

    public Queue() { }

    public Queue(BotCore core)
    {
        acebotCore = core;
        activeDelay = 1;
        messageDelay = new Timer(activeDelay, this);
        messageDelay.setInitialDelay(0);
        messageDelay.start();
    }

    public int addToQueue(String channel, String message, int source)
    {
        if (source == 0)
        {
            acebotCore.printChannel(addHash(channel), "[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
            acebotCore.printlnChannel(addHash(channel), message, new Color(230, 230, 230));

            acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
            acebotCore.printlnAll(message, new Color(230, 230, 230));
            System.out.println(message);
            return -1;
        }
        else
        {
            messagesQueue.add(channel + " " + message);
            messageDelay.start();
            return ++priorityPosition[4];
        }
    }

    /*public int addToQueue(String channel, String message, String account, int source)
    {
        messagesQueue.add(channel + " " + message);
        System.out.println("Account " + account + " send failed, not implemented (soon");
        return ++priorityPosition[4];
    }    */

    public int addToQueueAutoPriority(String channel, String message, int source)
    {
        return addToQueue(channel, message, source);     //soon
    }

    public int addToQueuePriority(String channel, String message, int source, int priority)
    {
        if (source == 0)
        {
            System.out.println(message);
            return -1;
        }
        else
        {
            priority = Math.max(Math.min(priority, 5), 1) - 1; //Chose 5,1 - 1 because it is closer to the syntax I want (1 most, 5 least)
            messagesQueue.add(priorityPosition[Math.max(priority, 0)], channel + " " + message);
            for (int i = priority; i <= 4; i++)
                priorityPosition[i]++;
            messageDelay.start();
            return priorityPosition[priority];
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (messagesQueue.size() == 0)
        {
            messageDelay.stop();
            activeDelay = 1;
        }
        else
        {
            String channel = addHash(messagesQueue.get(0).split(" ", 2)[0]);
            if (!acebotCore.getChannel(channel).getLastMessage().equalsIgnoreCase(messagesQueue.get(0).split(" ", 2)[1]))
            {
                acebotCore.sendMessage(channel, messagesQueue.get(0).split(" ", 2)[1]);
                //GLOBAL'D -
                acebotCore.printChannel(channel, "[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printChannel(channel, acebotCore.getNick() + ": ", acebotCore.getBotColor());
                acebotCore.printlnChannel(channel, messagesQueue.get(0).split(" ", 2)[1], new Color(230, 230, 230));

                acebotCore.printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
                acebotCore.printAll(acebotCore.getNick() + channel.substring(0,4) + ": ", acebotCore.getBotColor());
                acebotCore.printlnAll(messagesQueue.get(0).split(" ", 2)[1], new Color(230, 230, 230));
                      
                activeDelay = calculateNewDelay();
                messageDelay.setDelay(activeDelay);
                acebotCore.getChannel(channel).startTimer();
                acebotCore.getChannel(channel).setLastMessage(messagesQueue.get(0).split(" ", 2)[1]);
            }
            else
            {
                System.out.println("Denied message " + messagesQueue.get(0).split(" ", 2)[1]);
            }
            for (int i = 0; i <= 4; i++)
                priorityPosition[i]--;
            messagesQueue.remove(0);
        }
    }

    private int calculateNewDelay()
    {
        //core.setMessageDelay((long)(500/(1+500*Math.pow(2, messagesQueue.size() * 2))));
        return (int)(500/(1+500*Math.pow(2, messagesQueue.size() * 2)));
    }
}

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

public class GDQ {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();
    private boolean isTakingBets;
    private HashMap<String, Integer> userDeathMap = new HashMap<String, Integer>();

    public GDQ() { }
    public GDQ(BotCore core) {

        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("gdq");
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
            if (isCommand("sgdq", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    if (message.substring(1).equals("sgdq"))
                    {
                        Calendar start = Calendar.getInstance();
                        Calendar end = Calendar.getInstance();
                        start.set(2015, 7, 25, 12, 0, 0);
                        SimpleDateFormat sdf = new SimpleDateFormat("y M d H m s");
                        String date = sdf.format(new Date());
                        System.out.println(date);
                        end.set(Integer.parseInt(date.split(" ")[0]), Integer.parseInt(date.split(" ")[1]), Integer.parseInt(date.split(" ")[2]), Integer.parseInt(date.split(" ")[3]), Integer.parseInt(date.split(" ")[4]), Integer.parseInt(date.split(" ")[5]));
                        long milliseconds1 = start.getTimeInMillis();
                        long milliseconds2 = end.getTimeInMillis();
                        long diff = milliseconds1 - milliseconds2;
                        long diffDays = (int)(diff / (1000 * 60 * 60 * 24));
                        //String timeStr = diffHrs + " hours, " + diffMins + " minutes";


                    /*start.add(Calendar.DAY_OF_MONTH, (int)diffDays);
                    while (start.before(end)) {
                        start.add(Calendar.DAY_OF_MONTH, 1);
                        diffDays++;
                    }
                    while (start.after(end)) {
                        start.add(Calendar.DAY_OF_MONTH, -1);
                        diffDays--;
                    }    */
                        if (diffDays >= 0)
                            acebotCore.addToQueue(channel, "SGDQ is in " + diffDays + " days (Jul 26 - Aug 1 @ Minneapolis, MN).  Schedule at https://gamesdonequick.com/schedule", Integer.parseInt(source));
                    }
                }
            }
        }
    }
}

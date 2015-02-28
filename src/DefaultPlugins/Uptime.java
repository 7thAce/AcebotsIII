package DefaultPlugins;

import static u.u.getArgs;
import static u.u.isCommand;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;

import Bot.BotCore;
import Bot.Channel;

public class Uptime {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

    public Uptime() { }
    public Uptime(BotCore core) {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("uptime");
        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];
            if (isCommand("uptime", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap));
                {
                    /*boolean isOnline = acebotCore.getChannel(channel).getLiveStatus();
                    Channel currentChannel = acebotCore.getChannel(channel);

                    int streamStartTime = currentChannel.getStreamStartTime();
                    int currentTime = (int)new Date().getTime();
                    int streamTimeDiff = (currentTime - streamStartTime) / 1000;
                    int streamTimeDiffHours = (streamTimeDiff / 3600);
                    streamTimeDiff -= streamTimeDiffHours * 3600;
                    int streamTimeDiffMins = (streamTimeDiff / 60);
                    String streamTimeDiffMinsStr;

                    if (streamTimeDiffMins <= 9)
                        streamTimeDiffMinsStr = "0" + streamTimeDiffMins;
                    else
                        streamTimeDiffMinsStr = "" + streamTimeDiffMins;

                    if (isOnline)
                    {
                        int gameStartTime = currentChannel.getGameStartTime();
                        int gameTimeDiff = (currentTime - gameStartTime)/1000;
                        int gameTimeDiffHours = (gameTimeDiff / 3600);
                        gameTimeDiff -= gameTimeDiffHours * 3600;
                        int gameTimeDiffMins = (gameTimeDiff / 60);

                        String gameTimeDiffMinsStr;
                        if (gameTimeDiffMins <= 9)
                            gameTimeDiffMinsStr = "0" + gameTimeDiffMins;
                        else
                            gameTimeDiffMinsStr = "" + gameTimeDiffMins;

                        if (streamTimeDiff == gameTimeDiff)
                            acebotCore.addToQueue(channel, channel.substring(1) + " has been live for " + streamTimeDiffHours + ":" + streamTimeDiffMinsStr +
                                " and has been playing " + currentChannel.getStreamGame() + " for the entire time.", Integer.parseInt(source));
                        else
                            acebotCore.addToQueue(channel, channel.substring(1) + " has been live for " + streamTimeDiffHours + ":" + streamTimeDiffMinsStr +
                                " and has been playing " + currentChannel.getStreamGame() + " for " + gameTimeDiffHours + ":" + gameTimeDiffMinsStr + "." , Integer.parseInt(source));
                    }
                    else
                    {
                        acebotCore.addToQueue(channel, channel.substring(1) + " has been offline for " + streamTimeDiffHours + ":" + streamTimeDiffMinsStr + ".", Integer.parseInt(source));
                    }                     */
                }
            }
        }
    }
}

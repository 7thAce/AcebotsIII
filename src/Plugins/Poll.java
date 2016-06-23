package Plugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static u.u.*;

public class Poll {


    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();
    private boolean isAcceptingResponses;
    private HashMap<String, Integer> userResponseMap = new HashMap<String, Integer>();
    private int optionCount;
    private String[] pollOptions;

    public Poll() { }
    public Poll(BotCore core) {
        acebotCore = core;
        acebotCore.subscribe("onMessage", new MessageActionListener());
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("polling");
        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        accessExceptionMap = fillAccessExceptionMap(cmdInfo);
    }

    private class CommandActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];
            if (isCommand("startpoll", message)) {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap)) {
                    if (!isAcceptingResponses) {
                        String optionsString = message.substring(11);
                        if (optionsString.contains(",")) {
                            pollOptions = optionsString.replace(", ", ",").split(",");
                        } else {
                            pollOptions = optionsString.split(" ");
                        }
                        if (pollOptions.length < 2) {
                            acebotCore.addToQueue(channel, "The poll needs more than one option!  Separate options with \",\" or one word for each option.", Integer.parseInt(source));
                        }
                        if (pollOptions.length > 5) {
                            acebotCore.addToQueue(channel, "Acebots polls have a max of 5 options.  Use a strawpoll.me for larger polls.", Integer.parseInt(source));
                            return;
                        }
                        isAcceptingResponses = true;
                        optionCount = pollOptions.length;
                        acebotCore.addToQueue(channel, sender + "'s poll -  Type the number or option to vote!  Poll options:", Integer.parseInt(source));
                        for (int i = 1; i <= pollOptions.length; i++) //1 index for ease of output
                        {
                            acebotCore.addToQueue(channel, i + ". " + pollOptions[i - 1], Integer.parseInt(source));
                        }
                    } else {
                        acebotCore.addToQueue(channel, "A poll is already in progress!", Integer.parseInt(source));
                    }
                }
            }


            if (isCommand("stoppoll", message)) {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap)) {
                    if (isAcceptingResponses) {
                        isAcceptingResponses = false;

                        int[] optionTally = new int[optionCount];
                        int totalVotes = userResponseMap.values().size();

                        if (totalVotes == 0)
                        {
                            acebotCore.addToQueue(channel, "There were no voters!", Integer.parseInt(source));
                            userResponseMap.clear();
                            optionCount = 0;
                            pollOptions = new String[0];
                            return;
                        }
                        for(int vote:userResponseMap.values())
                        {
                            System.out.println(vote + "");
                            optionTally[vote - 1] = ++optionTally[vote - 1];
                            System.out.println("vote assigned for " + vote + " and has value " + optionTally[vote - 1]);
                        }

                        int maxVotes = -1;
                        int maxIndex = -1;
                        //int secondVotes = -2;
                        //int secondIndex = -2;

                        for (int i = 0; i < optionCount; i++)
                        {
                            if (optionTally[i] > maxVotes)
                            {
                                //secondIndex = maxIndex;
                                //secondVotes = maxVotes;
                                maxIndex = i;
                                maxVotes = optionTally[i];
                            }
                        }

                        userResponseMap.clear();
                        optionCount = 0;
                        acebotCore.addToQueue(channel, "Poll ended! " + pollOptions[maxIndex] + " won with " + maxVotes + " votes (" + Math.round(maxVotes * 100.0  / totalVotes) + "%). There were " + totalVotes + " voters.", Integer.parseInt(source));
                        pollOptions = new String[0];
                    }
                }
            }
        }
    }

    private class MessageActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];
            if (isAcceptingResponses)
            {
                if (isInteger(message))
                {
                    if (Integer.parseInt(message) <= optionCount && Integer.parseInt(message) >= 1)
                        userResponseMap.put(sender, Integer.parseInt(message));
                    else
                        System.out.println("Denied poll input from " + sender + ": " + message);
                }
                else
                {
                    for (int i = 0; i < optionCount; i++)
                    {
                        if (pollOptions[i].equalsIgnoreCase(message)) {
                            userResponseMap.put(sender, i + 1);
                            System.out.println("matched the option");
                        }
                    }
                }
            }

        }
    }
}

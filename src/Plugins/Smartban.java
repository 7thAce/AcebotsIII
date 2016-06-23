package Plugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import static u.u.getArgs;
import static u.u.isCommand;

/**
 * Created by Nicholas on 1/19/2016.
 */
public class Smartban {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

    public Smartban() { }
    public Smartban(BotCore core) {

        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("smartban");
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
            if (isCommand("sban", message) || isCommand("sb", message) || isCommand("sbtest", message)) {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap)) {
                    String[] prevMessages = acebotCore.getChannel(channel).getLastMessages();
                    int maxIndex = -1;
                    double maxScore = -1;
                    for (int line = 0; line < prevMessages.length; line++)
                    {
                        /* For each person, assign a score
                        Score:  1pt for lowercase char
                                2pt for uppercase char
                                4pt for a symbol
                                10pt for a URL
                        Max Score gets the punishment.
                                */

                        //Caps and Symbol
                        double currentScore = (double)prevMessages[line].length();
                        for (int charPos = 0; charPos < prevMessages[line].length(); charPos++)
                        {
                            String currentChar = prevMessages[line].substring(charPos, charPos + 1);
                            //System.out.println(currentChar + " vs. " + currentChar.toUpperCase() + " = " + currentChar.equals(currentChar.toUpperCase()));
                            if (!currentChar.equals(" ")) {
                                if (currentChar.equals(currentChar.toUpperCase())) {
                                    if (currentChar.equals(currentChar.toLowerCase())) {
                                        currentScore += 3;
                                        //System.out.println("Applied 4 points to " + currentChar);
                                    } else
                                        currentScore++;
                                }
                            }
                        }

                        //URL
                        String[] splitLine = prevMessages[line].split(" ");
                        for (int i = 0; i < splitLine.length; i++)
                        {
                            if (splitLine[i].contains(".") && splitLine[i].contains("/") && !splitLine[i].endsWith("."))
                            {
                                currentScore += 20; //Arbitrary added point value.
                            }
                        }


                        HashMap<String, Integer> wordsOccurrenceMap = new HashMap<String, Integer>();
                        for (int i = 0; i < splitLine.length; i++)
                        {
                            if (wordsOccurrenceMap.containsKey(splitLine[i].toLowerCase()))
                                wordsOccurrenceMap.put(splitLine[i].toLowerCase(), wordsOccurrenceMap.get(splitLine[i].toLowerCase()) + 1);
                            else
                                wordsOccurrenceMap.put(splitLine[i].toLowerCase(), 1);
                        }
                        double cc = (double)wordsOccurrenceMap.size() / splitLine.length - 1.0 / splitLine.length;

                        //System.out.println("CC = " + cc + " @ " + prevMessages[line]);

                        currentScore = ((1 - cc) / 2 + .5) * currentScore; //Scale CC to a .5 to 1 scale instead of 0 to 1 to reduce weight.

                        System.out.println(currentScore + " @ " + prevMessages[line]);

                        if (currentScore > maxScore) {
                            maxScore = currentScore;
                            maxIndex = line;
                            System.out.println("New max score is " + maxScore + "(" + prevMessages[line] + ").");
                        }
                        //System.out.println("[" + (double)wordsOccurrenceMap.size() / splitLine.length + "] is the message CC for " + sender + "'s message [" + message + "].");
                    }


                    if (maxScore >= 40) //Minimum score to purge
                    {
                        if(message.contains("sbtest")) {
                            acebotCore.addToQueue(channel, "The max score reported was " + maxScore + " with message " + acebotCore.getChannel(channel).getLastSenders()[maxIndex] + ": " + prevMessages[maxIndex], Integer.parseInt(source));
                            System.out.println("[Test] The max score reported was " + maxScore + " with message " + acebotCore.getChannel(channel).getLastSenders()[maxIndex] + ": " + prevMessages[maxIndex]);
                        }
                        else
                        {
                            if (message.endsWith("sb"))
                            {
                                acebotCore.addToQueue(channel, "/ban " + acebotCore.getChannel(channel).getLastSenders()[maxIndex], Integer.parseInt(source));
                                acebotCore.addToQueue(channel, "[KAPOW] Smartbanned " + acebotCore.getChannel(channel).getLastSenders()[maxIndex] + ".", Integer.parseInt(source));
                            }
                            else
                            {
                                acebotCore.addToQueue(channel, "/timeout " + acebotCore.getChannel(channel).getLastSenders()[maxIndex] + " " + message.split(" ")[1], Integer.parseInt(source));
                                acebotCore.addToQueue(channel, "Smartbanned " + acebotCore.getChannel(channel).getLastSenders()[maxIndex] + " for " + message.split(" ")[1] + " seconds.", Integer.parseInt(source));
                            }
                        }
                    }
                    else
                    {
                        acebotCore.addToQueue(channel, "No appropriate smartban target found.", Integer.parseInt(source));
                    }
                    System.out.println("The max score reported was " + maxScore + " with message " + acebotCore.getChannel(channel).getLastSenders()[maxIndex] + ": " + prevMessages[maxIndex]);
                    //System.out.println("The max score reported was " + maxScore + " with message " + acebotCore.getChannel(channel).getLastSenders()[maxIndex] + ": " + prevMessages[maxIndex]);
                }
            }

            if (isCommand("spurge", message) || isCommand("sp", message)) {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap)) {
                    actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, e.getActionCommand().replace("sp", "sb 1"), 0));
                }
            }
        }
    }
}

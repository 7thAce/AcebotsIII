package Plugins;

import Bot.BotCore;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static u.u.getArgs;
import static u.u.isBlank;
import static u.u.isCommand;

public class Pokedex {
    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();

    public Pokedex() { }
    public Pokedex(BotCore core) {

        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = acebotCore.getCommandInfo("pokedex");
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
            if (isCommand("pokedex", message) || isCommand("dex", message))
            {
                if (acebotCore.hasAccess(channel, sender, channelAccess, userAccess, accessExceptionMap))
                {
                    if (message.equalsIgnoreCase("!dex"))
                        return;
                    String pokemon = message.split(" ")[1];
                    String line = "";
                    FileReader fr = null;
                    try {
                        fr = new FileReader("C:\\Users\\Nicholas\\Desktop\\Gen 6 List w Types.txt");
                    } catch (FileNotFoundException e1) {
                        e1.printStackTrace();
                    }
                    BufferedReader reader = new BufferedReader(fr);
                    String name;
                    String type1;
                    String type2 = "";
                    try {
                        line = reader.readLine();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    do
                    {
                        try {
                            String[] temp = line.split("_");
                            name = temp[0];
                            type1 = temp[1];
                            type2 = "";
                            if (temp.length == 3)
                                type2 = temp[2];
                            if (name.equalsIgnoreCase(pokemon))
                            {
                                if (type2.equals(""))
                                    acebotCore.addToQueue(channel, name + " is pure " + type1 + " type.", Integer.parseInt(source));
                                else
                                    acebotCore.addToQueue(channel, name + " is dual " + type1 + "/" + type2 + " type.", Integer.parseInt(source));
                                reader.close();
                                fr.close();
                                return;
                            }
                            line = reader.readLine();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    } while (!
                            isBlank(line));
                }
            }
        }
    }
}

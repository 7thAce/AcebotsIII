package DefaultPlugins;

import Bot.BotCore;
import data.WorldRecord;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import static u.u.isBlank;

public class Wrlink {

    private BotCore acebotCore;
    private int userAccess;
    private int channelAccess;
    private HashMap<String, Integer> accessExceptionMap = new HashMap<String,Integer>();
    private HashSet<WorldRecord> wrLinkSet = new HashSet<WorldRecord>();

    public Wrlink() { }

    public Wrlink(BotCore core)
    {
        acebotCore = core;
        acebotCore.subscribe("onCommand", new CommandActionListener());
        String[] cmdInfo = core.getCommandInfo("editwr");

        userAccess = Integer.parseInt(cmdInfo[1]);
        channelAccess = Integer.parseInt(cmdInfo[2]);

        for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }

    private class CommandActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    private void loadWRLinkList()
    {
        wrLinkSet.clear();
        FileReader fr = null;
        try {
            fr = new FileReader(BotCore.WRLINKSFILEPATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(fr);
        String line = "asdf";

        while (!isBlank(line))
        {
            try {
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (!isBlank(line))
            {
                String[] temp = line.split("//");
                boolean addedAlready = false;
                for (WorldRecord wr:wrLinkSet)
                {
                    //(String game, String holder, String time, String categoriesString)
                    if (wr.getGame().equalsIgnoreCase(temp[0]) && wr.getCategories()[0].equalsIgnoreCase(temp[2]))
                    {
                        addedAlready = true;
                    }
                }
                if (!addedAlready)
                {
                    WorldRecord wr = new WorldRecord();
                    wr.setGame(temp[0]);
                    wr.setCategories(temp[2]);
                    wrLinkSet.add(new WorldRecord());
                }
            }
        }
        //quotes = quotesTemp.toArray(new String[quotesTemp.size()]);
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (fr != null) {
                fr.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

package DefaultPlugins;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

import static u.u.*;
import data.WorldRecord;

import Bot.BotCore;

public class Wr {

	private int wrUserAccess;
	private int wrChannelAccess;
	private int wrEditorUserAccess;
	private int wrEditorChannelAccess;
    private HashMap<String, Integer> wrAccessExceptionMap = new HashMap<String,Integer>();
    private HashMap<String, Integer> wrEditorAccessExceptionMap = new HashMap<String,Integer>();
	private BotCore acebotCore;
	private HashSet<WorldRecord> wrSet = new HashSet<WorldRecord>();
	
    public Wr() { }
    public Wr(BotCore core)
    {
    	acebotCore = core;
    	acebotCore.subscribe("onCommand", new CommandActionListener());
    	acebotCore.subscribe("onMessage", new MessageActionListener());
    	acebotCore.subscribe("onLoad", new LoadWRs());
    	
        String[] cmdInfo = core.getCommandInfo("wr");

        wrUserAccess = Integer.parseInt(cmdInfo[1]);
        wrChannelAccess = Integer.parseInt(cmdInfo[2]);

        for (int i = 4; i < cmdInfo.length; i++)
            wrAccessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
        
        cmdInfo = core.getCommandInfo("editwr");

        wrEditorUserAccess = Integer.parseInt(cmdInfo[1]);
        wrEditorChannelAccess = Integer.parseInt(cmdInfo[2]);

        for (int i = 4; i < cmdInfo.length; i++)
            wrEditorAccessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
    }
    
    private class CommandActionListener implements ActionListener
    {
		public void actionPerformed(ActionEvent e1) {
    		String[] args = getArgs(e1);
            String channel = args[0];
            String sender = args[1];
            String source = args[2];
            String message = args[3];

	        if (isCommand("wr", message))
	        {
                String streamGame = "";
                String streamTitle = "";
                BufferedReader reader;

                if (message.replace("!", "/").replace("/", "").toLowerCase().startsWith("wr")) //Case they type !wr
                {
                    if (message.toLowerCase().endsWith("wr")) //Case they type !wr
                    {
                        HashMap<String, String> streamInfo = new HashMap<String, String>();
                        try {
                            System.out.println(channel);
                            URL url = new URL("http://api.justin.tv/api/stream/list.json?jsonp=&channel=" + channel.substring(1));
                            reader = new BufferedReader(new InputStreamReader(url.openStream()));
                            String blah = reader.readLine();
                            if(!(blah.equals("[]"))){
                                String[] data = blah.split(",\"");
                                for (int i = 0; i < data.length; i++)
                                {
                                    String[] keyValue = data[i].split("\":");
                                    streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                                }
                                streamGame = streamInfo.get("meta_game");
                                streamTitle = streamInfo.get("status");
                                if (streamTitle.contains("[nosrl]"))
                                    return;
                            } else {
                                acebotCore.addToQueue(channel, "No game detected on this stream.  Please narrow your search with !wr <game> [category]", Integer.parseInt(source));
                                return;
                            }
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        for (WorldRecord wr:wrSet) //Get each WR from the DBSet
                        {
                            for (String wrCat:wr.getCategories()) //Get each Category from the WR
                            {
                                if (streamTitle.toLowerCase().contains(wrCat.replace("%", "").toLowerCase()))
                                {
                                    if (wr.getGame().equalsIgnoreCase(streamGame))
                                    {
                                        acebotCore.addToQueue(channel, "World Record for " + streamGame + " (" + wrCat + ") is " + wr.getTime() + " by " + wr.getWRholder() + ".", Integer.parseInt(source));
                                        return;
                                    }
                                }
                            }
                        }

                        for (WorldRecord wr:wrSet) //Get each WR from the DBSet
                        {
                            for (String wrCat:wr.getCategories()) //Get each Category from the WR
                            {
                                if (wrCat.replace("%", "").toLowerCase().contains("any"))
                                {
                                    if (wr.getGame().equalsIgnoreCase(streamGame))
                                    {
                                        acebotCore.addToQueue(channel, "World Record for " + streamGame + " (" + wrCat + ") is " + wr.getTime() + " by " + wr.getWRholder() + ".", Integer.parseInt(source));
                                        return;
                                    }
                                }
                            }
                        }
                        acebotCore.addToQueue(channel, "No world record found for " + streamGame + ".", Integer.parseInt(source));
                        return;
                    }
                    else
                    {
                    /* The idea here is that we need to try each different combination of the arguments as a game name and category
                    * Eg. [(StreamGame)]|[Dark Souls All Bosses], [Dark]|[Souls All Bosses], [Dark Souls]|[All Bosses], [Dark Souls All]|[Bosses], [Dark Souls All Bosses]|](Any%)]
                    * When it finds that BOTH match (given implied game StreamGame and category Any% when none is given), it returns.
                    * Else, no return.
                    */
                        for (int i = 0; i <= args.length; i++) //the iteration through the arguments.  Check each possibility
                        {
                            args = message.split(" ", 2)[1].split(" ");
                            StringBuilder gameName = new StringBuilder();
                            StringBuilder category = new StringBuilder();
                            for (int j = 0; j < args.length; j++) //builder loop, i is the splitter, j for junk (waste var)
                            {
                                if (j < i)
                                {
                                    gameName.append(" " + args[j]);
                                }
                                else
                                {
                                    category.append(" " + args[j]);
                                }
                            }

                            if (category.length() == 0)
                            {
                                category.append(" Any%");
                            }
                            if (gameName.length() == 0 && i == 0)
                            {
                                HashMap<String, String> streamInfo = new HashMap<String, String>();
                                try {
                                    URL url = new URL("http://api.justin.tv/api/stream/list.json?jsonp=&channel=" + channel.substring(1));
                                    reader = new BufferedReader(new InputStreamReader(url.openStream()));
                                    String blah = reader.readLine();
                                    if(!(blah.equals("[]"))){
                                        String[] data = blah.split(",\"");
                                        for (int k = 0; k < data.length; k++)
                                        {
                                            String[] keyValue = data[k].split("\":");
                                            streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                                        }
                                        gameName.append(" " + streamInfo.get("meta_game"));
                                    } else {
                                        gameName.append(" |%^#");
                                    }
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            String gn = trim(gameName.toString());
                            String cat = trim(category.toString());

                            for (WorldRecord wr:wrSet) //Get each WR from the DBSet
                            {
                                for (String wrCatList:wr.getCategories())
                                {
                                    for (String wrCat:wrCatList.split(","))
                                    {
                                        if (wr.getGame().equalsIgnoreCase(gn) && wrCat.replace("%",  "").equalsIgnoreCase(cat.replace("%", "")))
                                        {
                                            acebotCore.addToQueue(channel, "World Record for " + wr.getGame() + " (" + cat + ") is " + wr.getTime() + " by " + wr.getWRholder() + ".", Integer.parseInt(source));
                                            return;
                                        }
                                        /* if ((wr.getGame().toLowerCase().startsWith(gn.toLowerCase()) || wr.getGame().toLowerCase().endsWith(gn.toLowerCase()))  && wrCat.replace("%",  "").equalsIgnoreCase(cat.replace("%", "")))
                                        {
                                            acebotCore.addToQueue(channel, "World Record for " + wr.getGame() + " (" + cat + ") is " + wr.getTime() + " by " + wr.getWRholder() + ".", Integer.parseInt(source));
                                            return;
                                        } */
                                    }
                                }
                            }
                        }
                    }

                    int access;
                    if (acebotCore.getUserAccessMap().keySet().contains(sender.toLowerCase()))
                    {
                        access = acebotCore.getUserAccessMap().get(sender.toLowerCase());
                    }
                    else
                    {
                        access = 1;
                    }


                    if (access > 1)
                    {
                        acebotCore.addToQueue(channel, "Unable to determine World Record for " + message.substring(4) + " (it may not be recorded yet).", Integer.parseInt(source));
                        return;
                    }
                    else
                    {
                        return;
                    }
                }
            }

                    /*HashMap<String, String> streamInfo = new HashMap<String, String>();
                    try {
                        URL url = new URL("http://api.justin.tv/api/stream/list.json?jsonp=&channel=" + channel.substring(1));
                        reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        String blah = reader.readLine();
                        if(!(blah.equals("[]"))){
                            String[] data = blah.split(",\"");
                            for (int i = 0; i < data.length; i++)
                            {
                                String[] keyValue = data[i].split("\":");
                                streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                            }
                            streamGame = streamInfo.get("meta_game");
                            streamTitle = streamInfo.get("status");
                            if (streamTitle.contains("[nosrl]"))
                                return;
                        } else {
                            acebotCore.addToQueue(channel, "No game detected on this stream.  Please narrow your search with !wr <game> [category].", Integer.parseInt(source));
                            return;
                        }
                    } catch (MalformedURLException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                    for (WorldRecord wr:wrSet) //Get each WR from the DBSet
                    {
                        for (String wrCat:wr.getCategories()) //Get each Category from the WR
                        {
                            if (streamTitle.toLowerCase().contains(wrCat.replace("%", "").toLowerCase()))
                            {
                                if (wr.getGame().equalsIgnoreCase(streamGame))
                                {
                                    acebotCore.addToQueue(channel, "World Record for " + streamGame + " (" + wrCat + ") is " + wr.getTime() + " by " + wr.getWRholder() + ".", Integer.parseInt(source));
                                    return;
                                }
                            }
                        }
                    }

                    for (WorldRecord wr:wrSet) //Get each WR from the DBSet
                    {
                        for (String wrCat:wr.getCategories()) //Get each Category from the WR
                        {
                            if (wrCat.replace("%", "").toLowerCase().contains("any"))
                            {
                                if (wr.getGame().equalsIgnoreCase(streamGame))
                                {
                                    acebotCore.addToQueue(channel, "World Record for " + streamGame + " (" + wrCat + ") is " + wr.getTime() + " by " + wr.getWRholder() + ".", Integer.parseInt(source));
                                    return;
                                }
                            }
                        }
                    }
                    acebotCore.addToQueue(channel, "No world record found for " + streamGame + ".", Integer.parseInt(source));
                    return;
                }
                else
                {
                   args = message.split(" ", 2)[1].split(" ");
                /* The idea here is that we need to try each different combination of the arguments as a game name and category
                 * Eg. [(StreamGame)]|[Dark Souls All Bosses], [Dark]|[Souls All Bosses], [Dark Souls]|[All Bosses], [Dark Souls All]|[Bosses], [Dark Souls All Bosses]|](Any%)]
                 * When it finds that BOTH match (given implied game StreamGame and category Any% when none is given), it returns.
                 * Else, no return.
                */
                   /* for (int i = 0; i <= args.length; i++) //the iteration through the arguments.  Check each possibility
                    {
                        StringBuilder gameName = new StringBuilder();
                        StringBuilder category = new StringBuilder();
                        for (int j = 0; j < args.length; j++) //builder loop, i is the splitter, j for junk (waste var)
                        {
                            if (j < i)
                            {
                                gameName.append(" " + args[j]);
                            }
                            else
                            {
                                category.append(" " + args[j]);
                            }
                        }

                        if (category.length() == 0)
                        {
                            category.append(" Any%");
                        }
                        if (gameName.length() == 0 && i == 0)
                        {
                            HashMap<String, String> streamInfo = new HashMap<String, String>();
                            try {
                                URL url = new URL("http://api.justin.tv/api/stream/list.json?jsonp=&channel=" + channel.substring(1));
                                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                                String blah = reader.readLine();
                                if(!(blah.equals("[]"))){
                                    String[] data = blah.split(",\"");
                                    for (int k = 0; k < data.length; k++)
                                    {
                                        String[] keyValue = data[k].split("\":");
                                        streamInfo.put(keyValue[0].toLowerCase(), stripQuotes(keyValue[1]));
                                    }
                                    gameName.append(" " + streamInfo.get("meta_game"));
                                } else {
                                    gameName.append(" |%^#");
                                }
                            } catch (MalformedURLException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                        String gn = trim(gameName.toString());
                        String cat = trim(category.toString());

                        for (WorldRecord wr:wrSet) //Get each WR from the DBSet
                        {
                            for (String wrCatList:wr.getCategories())
                            {
                                for (String wrCat:wrCatList.split(","))
                                {
                                    System.out.println(wr.getGame() + " : " + wrCat + " vs. " + gn + " : " + cat);
                                    if (wr.getGame().equalsIgnoreCase(gn) && wrCat.replace("%",  "").equalsIgnoreCase(cat.replace("%", "")))
                                    {
                                        acebotCore.addToQueue(channel, "World Record for " + wr.getGame() + " (" + cat + ") is " + wr.getTime() + " by " + wr.getWRholder() + ".", Integer.parseInt(source));
                                        return;
                                    }
                                    if ((wr.getGame().toLowerCase().startsWith(gn.toLowerCase()) || wr.getGame().toLowerCase().endsWith(gn.toLowerCase()))  && wrCat.replace("%",  "").equalsIgnoreCase(cat.replace("%", "")))
                                    {
                                        acebotCore.addToQueue(channel, "World Record for " + wr.getGame() + " (" + cat + ") is " + wr.getTime() + " by " + wr.getWRholder() + ".", Integer.parseInt(source));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                int access = acebotCore.getUserAccessMap().get(sender.toLowerCase());
                if (access == 0)
                    access = 1;
                if (access > 1)
                    acebotCore.addToQueue(channel, "Unable to determine World Record for " + message.substring(4) + " (it may not be recorded yet).", Integer.parseInt(source));
	        }                             */
	        
                    
	        if (isCommand("editwr", message))
	        {
            	if (acebotCore.hasAccess(channel, sender, wrEditorChannelAccess, wrEditorUserAccess, wrEditorAccessExceptionMap))
            	{
                    if (args.length < 4)
                    {
                        acebotCore.addToQueue(channel, "Invalid syntax, use !editwr <game> <time> <runner> <categories>", Integer.parseInt(source));
                        return;
                    }
                    String gameName = "asdf";
                    String time = "asdf";
                    String runner = "asdf";
                    String categories = "asdf";
                    for (int i = 0; i < args.length; i++)
                    {
                        if (isValidTime(args[i]))
                        {
                            try
                            {
                                time = args[i];
                                gameName = message.substring(8).split(" " + time)[0];
                                runner = args[i + 1];
                                categories = message.split(runner + " ")[1];
                            }
                            catch (ArrayIndexOutOfBoundsException e)
                            {
                                acebotCore.addToQueue(channel, "Invalid syntax, use !editwr <game> <time> <runner> <categories>", Integer.parseInt(source));
                                return;
                            }
                        }
                    }
                    if (gameName.equals("asdf") || time.equals("asdf") || runner.equals("asdf") || categories.equals("asdf"))
                    {
                        acebotCore.addToQueue(channel, "Invalid syntax, use !editwr <game> <time> <runner> <categories>", Integer.parseInt(source));
                        return;
                    }
                    for (WorldRecord wr:wrSet)
                    {
                        if (wr.getGame().equalsIgnoreCase(gameName))
                        {
                            if(categories.contains(",")) //case multiple categories set (me)
                            {
                                for (String cat:wr.getCategories())
                                {
                                    for (String category:categories.split(","))
                                    {
                                        if (category.replace("%", "").equalsIgnoreCase(cat.replace("%", "")))
                                        {
                                            wr.setTime(time);
                                            wr.setWRholder(runner);
                                            if (wr.getCategories().length < categories.split(",").length)
                                                wr.setCategories(categories);
                                            try {
                                                PrintWriter writer = new PrintWriter("WRs.txt");
                                                for(WorldRecord record:wrSet)
                                                {
                                                    StringBuilder catString = new StringBuilder();
                                                    for (String s:record.getCategories())
                                                        catString.append("," + s);
                                                    writer.println(record.getGame() + "//" + record.getTime() + " by " + record.getWRholder() + "//" + catString.substring(1));
                                                }
                                                writer.close();
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                            acebotCore.fire("onLoad", "");
                                            acebotCore.addToQueue(channel, "World record for " + gameName + " (" + categories + ") edited to " + time + " by " + runner, Integer.parseInt(source));
                                            return;
                                        }
                                    }
                                }
                            }
                            else
                            { //case everyone else, one category
                                for (String cat:wr.getCategories())
                                {
                                    if (cat.replace("%", "").equalsIgnoreCase(categories.replace("%", "")))
                                    {
                                        wr.setTime(time);
                                        wr.setWRholder(runner);
                                        try {
                                            PrintWriter writer = new PrintWriter("WRs.txt");
                                            for(WorldRecord record:wrSet)
                                            {
                                                StringBuilder catString = new StringBuilder();
                                                for (String s:record.getCategories())
                                                    catString.append("," + s);
                                                writer.println(record.getGame() + "//" + record.getTime() + " by " + record.getWRholder() + "//" + catString.substring(1));
                                            }
                                            writer.close();
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                        acebotCore.fire("onLoad", "");
                                        acebotCore.addToQueue(channel, "World record for " + gameName + " (" + categories + ") edited to " + time + " by " + runner, Integer.parseInt(source));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    PrintWriter out = null;
                    try {
                        out = new PrintWriter(new BufferedWriter(new FileWriter("WRs.txt", true)));
                        out.println(gameName + "//" + time + " by " + runner + "//" + categories);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (out != null) {
                            out.close();
                            acebotCore.fire("onLoad", "");
                            acebotCore.addToQueue(channel, "Added new world record for " + gameName + " (" + categories + ") - " + time + " by " + runner, Integer.parseInt(source));
                            return;
                        }
                    }
                    acebotCore.fire("onLoad", "");
                    acebotCore.addToQueue(channel, "World record for " + gameName + " (" + categories + ") edited to " + time + " by " + runner, Integer.parseInt(source));
                    return;
            	}
	        }
	        
	        if (isCommand("addalias", message))
	        {
            	if (acebotCore.hasAccess(channel, sender, wrEditorChannelAccess, wrEditorUserAccess, wrEditorAccessExceptionMap))
            	{
            		//check if exists, add for ALL game/cats, reload
            	}
	        }
		}	
    }
    
    private class MessageActionListener implements ActionListener
    {
    	public void actionPerformed(ActionEvent e)
    	{
    		String[] args = getArgs(e);
            String channel = args[0];
            String sender = args[1];
            String message = args[2];
            if (((message.toLowerCase().contains("what ") && message.toLowerCase().contains(" is ")) || (message.toLowerCase().contains("what's") || message.toLowerCase().contains("whats"))) && (message.toLowerCase().contains("wr") || message.toLowerCase().contains("record")))
            {
            	if (message.contains("for "))
            		acebotCore.fire("onCommand", channel + "``" + sender + "``1``" + "!wr " + message.split("for ")[1].replace("?", ""));
            	else
            		acebotCore.fire("onCommand", channel + "``" + sender + "``1``" + "!wr");
            }
            if (message.toLowerCase().contains("who ") && (message.toLowerCase().contains("has") || message.toLowerCase().contains("holds")) && (message.toLowerCase().contains("wr") || message.toLowerCase().contains("record")))
            {
            	if (message.contains("for "))
            		acebotCore.fire("onCommand", channel + "``" + sender + "``1``" + "!wr " + message.split("for ")[1].replace("?", ""));
            	else
            		acebotCore.fire("onCommand", channel + "``" + sender + "``1``" + "!wr");
            }
        }
    }
    
    private class LoadWRs implements ActionListener
    {
    	public void actionPerformed(ActionEvent e1) {
    		/*FileReader fr = null;
            try {
                fr = new FileReader(BotCore.WRSFILEPATH);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            BufferedReader reader = new BufferedReader(fr);
            String line = "asdf";

            while (!isBlank(line))
            {
                try {
                    line = reader.readLine();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (!isBlank(line))
                {
                	wrSet.add(new WorldRecord(line));
                }
            }
            try {
                reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }   */

            wrSet.clear();
            FileReader fr = null;
            try {
                fr = new FileReader(BotCore.WRSFILEPATH);
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
                    for (WorldRecord wr:wrSet)
                    {
                        //(String game, String holder, String time, String categoriesString)
                        if (wr.getGame().equalsIgnoreCase(temp[0]) && wr.getWRholder().equalsIgnoreCase(temp[1].split(" by ")[1]) && wr.getCategories()[0].equalsIgnoreCase(temp[2]) && wr.getTime().equalsIgnoreCase(temp[1].split(" by ")[0]))
                        {
                            addedAlready = true;
                        }
                    }
                    if (!addedAlready)
                        wrSet.add(new WorldRecord(temp[0], temp[1].split(" by ")[1], temp[1].split(" by ")[0], temp[2]));
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
}

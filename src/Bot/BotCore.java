package Bot;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.PircBot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import graphics.acebotsthree;

import javax.swing.*;

import static u.u.appendTextPane;
import static u.u.addHash;
import static u.u.isBlank;

public class BotCore extends PircBot {

    public static HashMap<String, Object> objectMap = new HashMap<String, Object>();
    public static HashMap<String, Channel> channelMap = new HashMap<String, Channel>();
    public HashMap<String, Integer> userAccessMap = new HashMap<String, Integer>();
    public HashMap<String, Integer> channelAccessMap = new HashMap<String, Integer>();
    public ArrayList<String> modList = new ArrayList<String>();
    private Queue messageQueue;
    public static HashMap<String, ArrayList<ActionListener>> alMap = new HashMap<String, ArrayList<ActionListener>>();
    public final static int OUTPUT_INTERNAL = 0;
    public final static int OUTPUT_CHANNEL = 1;
    private acebotsthree acebotsGUI;

    public BotCore() { }

    public BotCore(String username, String password, String server, String initChannel)
    {
        //JOptionPane.showMessageDialog(null, "I raq.");
        alMap.put("onLoad", new ArrayList<ActionListener>());
        alMap.put("onMessage", new ArrayList<ActionListener>());
        alMap.put("onCommand", new ArrayList<ActionListener>());
        alMap.put("onBotJoin", new ArrayList<ActionListener>());
        alMap.put("onBotLeave", new ArrayList<ActionListener>());
        alMap.put("onUserJoin", new ArrayList<ActionListener>());
        alMap.put("onUserLeave", new ArrayList<ActionListener>());
        alMap.put("onFiltered", new ArrayList<ActionListener>());
        alMap.put("onReload", new ArrayList<ActionListener>());
        alMap.put("onLogin", new ArrayList<ActionListener>());
        alMap.put("onInternalMessage", new ArrayList<ActionListener>());
        alMap.put("onChannelMode", new ArrayList<ActionListener>());
        alMap.put("onUserMode", new ArrayList<ActionListener>());
        alMap.put("onPrivateMessage", new ArrayList<ActionListener>());
        alMap.put("onNotice", new ArrayList<ActionListener>());
        alMap.put("onVoice", new ArrayList<ActionListener>());
        alMap.put("onMe", new ArrayList<ActionListener>());
        alMap.put("onPing", new ArrayList<ActionListener>());
        alMap.put("onConnect", new ArrayList<ActionListener>());
        alMap.put("onDisconnect", new ArrayList<ActionListener>());
        alMap.put("onSubscribe", new ArrayList<ActionListener>());

        messageQueue = new Queue(this);
        setLogin(username);
        setName(username);

        loadUsers();
        bootPluginSystem();

        int port = 80;

        acebotsGUI = new acebotsthree();
        //acebotsGUI = new Maingui(server, port, username)

        try {
            connect(server, port, password);
            fire("onConnect", new String[]{server, port + "", username});

            //acebotsGUI.changeTitle("Acebots II :|: Connected to " + server);

        } catch (IOException e) {
            System.out.println("Unable to connect to IRC");
            e.printStackTrace();
        } catch (IrcException e) {
            System.out.println("Unable to connect to " + server);
            e.printStackTrace();
        }

        String[] initChannels = initChannel.split(",");
        for (String chan:initChannels)
            botJoinChannel(addHash(chan));

        sendRawLine("TWITCHCLIENT 3");
        fire("onLoad", new String[]{});
        printTitleLine("Acebots III Loaded!  Updates will be frequent so check back often.", new Color(255, 128, 0));
        acebotsGUI.setTitle("(0) Acebots III :|: Connected to " + server + ":" + port);
        //sendMessage(initChannel, "I am connected as Acebots III!");
    }

    /* private HashMap<String, Channel> channelMap = new HashMap<String, Channel>();
    private HashMap<String, SubBotCore> subCoreMap = new HashMap<String, SubBotCore>();
    private ArrayList<String> quotesList = new ArrayList<String>();
    private HashMap<String, String> userAccessMap = new HashMap<String, String>();
    private HashMap<String, String> channelAccessMap = new HashMap<String, String>();
    private HashMap<String, String> commandUserAccessMap = new HashMap<String, String>();
    private HashMap<String, String> commandChannelAccessMap = new HashMap<String, String>();
    private HashSet<WorldRecord> wrSet = new HashSet<WorldRecord>();
    private HashMap<String, CustomCommand> customCommandMap = new HashMap<String, CustomCommand>();
    private ArrayList<String> filterList = new ArrayList<String>();
    private HashMap<String, Integer> userPunishMap = new HashMap<String, Integer>(); */

    public final static String TRIGGER = "!";
    public final static String QUOTESFILEPATH = "quotes.txt";
    public final static String USERSFILEPATH = "users.txt";
    public final static String CMDSFILEPATH = "commands.txt";
    public final static String CUSTOMCMDSFILEPATH = "customcommands.txt";
    public final static String WRSFILEPATH = "WRs.txt";
    public final static String CONFIGFILEPATH = "config.txt";
    public final static String FILTERSFILEPATH = "filters.txt";
    public final static String[] RANKARRAY = {"Restricted", "User", "Power User", "Moderator", "Admin", "Owner", "Bot Console"};
    public final static SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss aa");


    //Plugin Components
    public void subscribe(String event, ActionListener al)
    {
        try {
            alMap.get(event).add(al);
        } catch (java.lang.NullPointerException e) {
            System.out.println("[API] Subscribing: " + event + " doesn't exist");
            e.printStackTrace();
        }
    }

    /* public void fire(String event, String arguments)
    {
        //System.out.println("Firing " + event);
        try {
            for (ActionListener al:alMap.get(event))
                al.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, arguments));
        } catch (Exception e) {
            System.out.println("Error in event: " + event);
            e.printStackTrace();
        }
    }  */

    public void fire(String event, String[] argumentsArray)
    {
        //System.out.println("Firing " + event);
        try {
            StringBuilder arguments = new StringBuilder();
            for (String arg:argumentsArray)
                arguments.append(arg + "``");
            System.out.println(arguments.toString() + event);
            for (ActionListener al:alMap.get(event))
                al.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, arguments.toString().substring(0, Math.max(arguments.length() - 2, 0))));
        } catch (Exception e) {
            System.out.println("Error in event: " + event);
            e.printStackTrace();
        }
    }

    public void createEvent(String name)
    {
        if (!alMap.containsKey(name))
            alMap.put(name, new ArrayList<ActionListener>());
        else
            System.out.println("[Plugins] Could not create event " + name + ".");
    }

    //Begin PircBot event handlers and fire our own
    public void onMessage(String channel, String sender, String login, String hostname, String message)
    {
        if (sender.equalsIgnoreCase(channel.substring(1)) && message.toLowerCase().equalsIgnoreCase("!kill"))
            System.exit(-5);
        fire("onMessage", new String[]{channel, sender, message});
        if (message.substring(0, TRIGGER.length()).equals(TRIGGER))
            fire("onCommand", new String[]{channel, sender, "1", message});
        if (message.toLowerCase().startsWith(getNick().toLowerCase() + ", "))
            fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 2)[1]});
        if (message.toLowerCase().startsWith("acebots iii, "))
            fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 3)[2]});

        if (message.contains("just subscribed") && sender.equalsIgnoreCase("twitchnotify"))
            fire("onSubscribe", new String[]{channel, message.split(" ")[0] +  "``"});
        //sendMessage(channel, "We have received a message");
    }

    protected void onJoin(String channel, String sender, String login, String hostname)
    {
        fire("onUserJoin", new String[]{channel, sender});
    }

    protected void onPart(String channel, String sender, String login, String hostname)
    {
        fire("onUserLeave", new String[]{channel, sender});
    }

    protected void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode)
    {
        fire("onChannelMode", new String[]{channel, sourceNick, mode});
    }

    protected void onUserMode(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String mode)
    {
        if (mode.contains("+o"))
        {
            modList.add(mode.split(" ")[2].toLowerCase() + mode.split(" ")[0]);
        }
        fire("onUserMode", new String[]{targetNick, sourceNick, mode});
    }

    protected void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue)
    {
        fire("onPing", new String[]{sourceNick, target, pingValue});
    }

    protected void onAction(String sender, String login, String hostname, String target, String action)
    {
        fire("onMe", new String[]{target, sender, action});
    }

    protected void onPrivateMessage(String sender, String login, String hostname, String message)
    {
        fire("onPrivateMessage", new String[]{sender, message});
    }

    /*alMap.put("onFiltered", new ArrayList<ActionListener>());
    alMap.put("onInternalMessage", new ArrayList<ActionListener>());
    alMap.put("onChannelMode", new ArrayList<ActionListener>());
    alMap.put("onUserMode", new ArrayList<ActionListener>());
    alMap.put("onPrivateMessage", new ArrayList<ActionListener>());
    alMap.put("onNotice", new ArrayList<ActionListener>());
    alMap.put("onVoice", new ArrayList<ActionListener>());
    alMap.put("onPing", new ArrayList<ActionListener>()); */

    //@SuppressWarnings("ConstantConditions")
    public void bootPluginSystem()
    {
        Class[] defaultPlugins = loadPlugins("DefaultPlugins");
		Class[] extraPlugins = loadPlugins("Plugins");
        for (int i = 0; i < defaultPlugins.length; i++)
        {
            try {
                objectMap.put(defaultPlugins[i].getName().substring(15).toLowerCase(), BotCore.class.getClassLoader().loadClass(defaultPlugins[i].getName()).getConstructor(BotCore.class).newInstance(this));
            } catch (InstantiationException e) {
                System.out.println("Instantiation Exception: Failed to load default plugin " + defaultPlugins[i].getName());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.out.println("Illegal Access Exception: Failed to load default plugin " + defaultPlugins[i].getName());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Class Not Found Exception: Failed to load default plugin " + defaultPlugins[i].getName() + "\nMake sure that the class exists in the right package.");
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                System.out.println("Failed to load default plugin " + defaultPlugins[i].getName() + "\nMake sure the plugin has a BotCore parameter constructor!\nEg. public YourClass(Botcore core){}");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                System.out.println("Invocation Target Exception: Failed to load default plugin " + defaultPlugins[i].getName());
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Generic error: " + e.getMessage());
                e.printStackTrace();
            }
        }

        for (int i = 0; i < extraPlugins.length; i++)
        {
            try {
                objectMap.put(extraPlugins[i].getName().substring(8).toLowerCase(), BotCore.class.getClassLoader().loadClass(extraPlugins[i].getName()).getConstructor(BotCore.class).newInstance(this));
                //System.out.println("Mapped extra: " + extraPlugins[i].getName().substring(8).toLowerCase());
            } catch (InstantiationException e) {
                System.out.println("Instantiation Exception: Failed to load extra plugin " + extraPlugins[i].getName());
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                System.out.println("Illegal Access Exception: Failed to load extra plugin " + extraPlugins[i].getName());
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Class Not Found Exception: Failed to load extra plugin " + extraPlugins[i].getName() + "\nMake sure that the class exists in the right package.");
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                System.out.println("Skipping extra plugin " + extraPlugins[i].getName() + "\nMake sure the plugin has a BotCore parameter constructor!\nEg. public YourClass(Botcore core){}");
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                System.out.println("Invocation Target Exception: Failed to load extra plugin " + extraPlugins[i].getName());
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Generic Error: Failed to load extra plugin " + extraPlugins[i].getName());
                e.printStackTrace();
            }
        }
        fire("onReload", new String[]{});
    }

    private static Class[] loadPlugins(String packageName)
    {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            try {
                classes.addAll(findClasses(directory, packageName));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException
    {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
        	System.out.println("No thing");
            return classes;
            }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                //classes.addAll(findClasses(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    if (!file.getName().contains("$"))
                    {
                        classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                    }
                }
            }
        return classes;
    }

    public boolean hasAccess(String channel, String user, int cReqAccess, int uReqAccess, HashMap<String, Integer> exceptionMap)
    {
        int userAccess;
        int channelAccess;


        if (user.equalsIgnoreCase(getNick()))
            return true;

        if (userAccessMap.containsKey(user.toLowerCase()))
            userAccess = userAccessMap.get(user.toLowerCase());
        else
            userAccess = 1;

        //if (isMod, isBroadcaster, give access)
        //bool for subscribers get 2 access

        if (channelAccessMap.containsKey(channel.toLowerCase()))
            channelAccess = channelAccessMap.get(channel.toLowerCase());
        else
            channelAccess = 1;

        if (exceptionMap != null)
            if (exceptionMap.containsKey(channel.toLowerCase()))
                cReqAccess = exceptionMap.get(channel.toLowerCase());

      //if (isSubscriber(channel, user))
      //    userAccess = Math.max(2, userAccess);

        if (isMod(channel, user)) //Default Moderator Access
            userAccess = Math.max(3, userAccess);

        if (channel.substring(1).equalsIgnoreCase(user))
            userAccess = Math.max(4, userAccess);

        if (userAccess >= uReqAccess && channelAccess >= cReqAccess)
            return true;
        else
            return false;
    }

    public void loadUsers()
    {
        userAccessMap.clear();
        String line = "";
        FileReader fr = null;
        try {
            fr = new FileReader(BotCore.USERSFILEPATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(fr);
        String name;
        String access;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        do
        {
            try {
                String[] temp = line.split(" ");
                name = temp[0];
                access = temp[1];
                if (name.startsWith("#"))
                    channelAccessMap.put(name.toLowerCase(), Integer.parseInt(access));
                else
                    userAccessMap.put(name.toLowerCase(), Integer.parseInt(access));
                line = reader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!
                isBlank(line));

        //channelAccessMap.put(homeChan, "3"); //You cannot set the bot name lower.
        userAccessMap.put(getNick().toLowerCase(), 5);

        System.out.println("[LOADED] Users and Channels");
    }
    //Acebots raid origin detection

    public String[] getCommandInfo(String commandName)
    {
        FileReader fr = null;
        try {
            fr = new FileReader(BotCore.CMDSFILEPATH);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buffReader = new BufferedReader(fr);
        String line = "asdf";
        try {
            line = buffReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        do
        {
            try {
                if (line.split(" ")[0].equalsIgnoreCase(commandName))
                    return line.split(" ");
                line = buffReader.readLine(); //command 1 1 ea#chan1 ea#chan2
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!isBlank(line));
        try {
			buffReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
    }

    public int addToQueue(String channel, String message, int source)
    {
        return messageQueue.addToQueue(channel, message, source);
    }

    public int addToQueuePriority(String channel, String message, int source, int priority)
    {
        return messageQueue.addToQueuePriority(channel, message, source, priority);
    }

    public boolean isMod(String channel, String user)
    {
        if(modList.contains(user.toLowerCase() + channel))
            return true;
        else
            return false;
    }

	public Channel getChannel(String channel) {
		return channelMap.get(channel);
	}

    public boolean botJoinChannel(String name)
    {
        if (channelMap.containsKey(name.toLowerCase()))
            return false;
        else
        {
            channelMap.put(name, new Channel(name, this));
            fire("onBotJoin", new String[]{name});
            return true;
        }
    }

    public boolean botLeaveChannel(String name)
    {
        if (channelMap.containsKey(name.toLowerCase()))
        {
            try {
            partChannel(name);
            channelMap.remove(name);
            acebotsGUI.allChatLeftPane.removeTabAt(acebotsGUI.allChatLeftPane.indexOfTab(addHash(name.toLowerCase())));
            acebotsGUI.allChatRightPane.removeTabAt(acebotsGUI.allChatRightPane.indexOfTab(addHash(name.toLowerCase())));
            acebotsGUI.inputTab.removeTabAt(acebotsGUI.inputTab.indexOfTab(addHash(name)));
            acebotsGUI.channelListBox.removeItem(name);
            fire("onBotLeave", new String[]{name});
            return true;
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
                return false;
            }
        }
        else
            return false;
    }

    public void createCommand(String name, int userAccess, int channelAccess)
    {
        //Coming soon Kappa//
    }

    public HashMap<String, Integer> getUserAccessMap()
    {
        return userAccessMap;
    }

    public HashMap<String, Integer> getChannelAccessMap()
    {
        return channelAccessMap;
    }

    public void printlnChannel(String channel, String message, Color messageColor)
    {
        try{
        appendTextPane(messageColor, message + "\n", getChannel(channel).getLeftChatBox());
        appendTextPane(messageColor, message + "\n", getChannel(channel).getRightChatBox());
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Error: " + channel);
        System.out.println("Message: " + message);
    }
    }

    public void printlnAll(String message, Color messageColor)
    {
        try{
        appendTextPane(messageColor, message + "\n", acebotsGUI.allChatLeftBox);
        appendTextPane(messageColor, message + "\n", acebotsGUI.allChatRightBox);
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Message: " + message);
    }
    }


    public void printChannel(String channel, String message, Color messageColor)
    {
        try{
            appendTextPane(messageColor, message, getChannel(channel).getLeftChatBox());
            appendTextPane(messageColor, message, getChannel(channel).getRightChatBox());
        } catch (Exception e) {
             e.printStackTrace();
            System.out.println("Error: " + channel);
            System.out.println("Message: " + message);
        }
    }

    public void printAll(String message, Color messageColor)
    {
        appendTextPane(messageColor, message, acebotsGUI.allChatLeftBox);
        appendTextPane(messageColor, message, acebotsGUI.allChatRightBox);
    }

    public void printTitleLine(String message, Color messageColor)
    {
        acebotsGUI.someExtraLabel.setText(message);
        acebotsGUI.someExtraLabel.setForeground(messageColor);
    }

    public acebotsthree getGUI()
    {
        return acebotsGUI;
    }
}

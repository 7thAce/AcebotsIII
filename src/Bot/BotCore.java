package Bot;

import pircbot.IrcException;
import pircbot.PircBot;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import graphics.acebotsthree;

import javax.swing.*;
import javax.swing.Timer;

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
    private boolean isConnected;
    private Color botColor;

    public BotCore() { }

    public BotCore(String username, String password, String server, int port, String initChannel)
    {
        //setVerbose(true);

        /**
         * Create a whole bunch of events that can be subscribed to.
         * Events are called by the fire(event) and subscribed to by subscribe(event)
         * Not all events are called, so if one is needed and isn't called properly, raise and issue
         */
        alMap.put("onLoad", new ArrayList<ActionListener>());
        alMap.put("onMessage", new ArrayList<ActionListener>());
        alMap.put("onCommand", new ArrayList<ActionListener>());
        alMap.put("onBotJoin", new ArrayList<ActionListener>());
        alMap.put("onBotLeave", new ArrayList<ActionListener>());
        alMap.put("onUserJoin", new ArrayList<ActionListener>());
        alMap.put("onUserLeave", new ArrayList<ActionListener>());
        alMap.put("onTimeout", new ArrayList<ActionListener>());
        alMap.put("onBan", new ArrayList<ActionListener>());
        alMap.put("onReload", new ArrayList<ActionListener>());
        alMap.put("onLogin", new ArrayList<ActionListener>());
        alMap.put("onInternalMessage", new ArrayList<ActionListener>());
        alMap.put("onChannelMode", new ArrayList<ActionListener>());
        alMap.put("onUserMode", new ArrayList<ActionListener>());
        alMap.put("onPrivateMessage", new ArrayList<ActionListener>());
        alMap.put("onNotice", new ArrayList<ActionListener>());
        alMap.put("onVoice", new ArrayList<ActionListener>());
        alMap.put("onMe", new ArrayList<ActionListener>());
        alMap.put("onConnect", new ArrayList<ActionListener>());
        alMap.put("onDisconnect", new ArrayList<ActionListener>());
        alMap.put("onSubscribe", new ArrayList<ActionListener>());
        alMap.put("onResubscribe", new ArrayList<ActionListener>());
        alMap.put("onGameChange", new ArrayList<ActionListener>());
        alMap.put("onTitleChange", new ArrayList<ActionListener>());
        alMap.put("onStreamGoesOnline", new ArrayList<ActionListener>());
        alMap.put("onStreamGoesOffline", new ArrayList<ActionListener>());
        alMap.put("onServerPing", new ArrayList<ActionListener>());
        alMap.put("onMessageDenied", new ArrayList<ActionListener>());

        messageQueue = new Queue(this);

        loadUsers();
        bootPluginSystem();

        port = 6667;

        acebotsGUI = new acebotsthree();
        //acebotsGUI = new Maingui(server, port, username)
        acebotsConnect(username, password, server, port, initChannel);


        fire("onLoad", new String[]{});
        printTitleLine("Moderating for 0 viewers.  Acebots III loaded!", new Color(255, 128, 0));
        acebotsGUI.setTitle("Acebots III :|: Connected to " + server + ":" + port);
        //sendMessage(initChannel, "I am connected as Acebots III!");
    }

    /**
     * Connects the bot to twitch.tv with given parameters
     * Implements the pircbot connect() method.
     * @param username Username to connect with.
     * @param password Password to conect with.  In safety, never stored in memory.
     * @param server Server to connect to.
     * @param port  Port to connect to the server with.
     * @param initChannel Initial channels to join, multiples separated by commas.  # not required.
     */
    private void acebotsConnect(String username, String password, String server, int port, String initChannel)
    {
        setLogin(username);
        setName(username);
        //setUser(username);
        try {
            //connect(server, port, password);
            connect("irc.chat.twitch.tv", port, password);
            fire("onConnect", new String[]{server, port + "", username});

            //acebotsGUI.changeTitle("Acebots II :|: Connected to " + server);

        } catch (IOException e) {
            System.out.println("Unable to connect to IRC");
            e.printStackTrace();
        } catch (IrcException e) {
            System.out.println("Unable to connect to " + server);
            e.printStackTrace();
        }

        sendRawLine("CAP REQ :twitch.tv/membership");
        sendRawLine("CAP REQ :twitch.tv/commands");
        sendRawLine("CAP REQ :twitch.tv/tags");

        String[] initChannels = initChannel.split(",");
        for (String chan:initChannels)
            botJoinChannel(addHash(chan));

        //sendRawLine("TWITCHCLIENT 3");
        fire("onConnect", new String[]{});
        isConnected = true;
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
    public final static String WRLINKSFILEPATH = "wrlinks.txt";
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

    public void fire(String event, String[] argumentsArray)
    {
        //System.out.println("Firing " + event);
        try {
            StringBuilder arguments = new StringBuilder();
            for (String arg:argumentsArray)
                arguments.append(arg + "``");
            for (ActionListener al:alMap.get(event))
                al.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, arguments.toString().substring(0, Math.max(arguments.length() - 2, 0))));
        } catch (Exception e) {
            System.out.println("Error in event: " + event);
            e.printStackTrace();

            String channelError = "";
            for (String arg:argumentsArray)
            {
                if (arg.startsWith("#"))
                {
                    channelError = arg.toLowerCase();
                    break;
                }
            }

            printAll("[" + BotCore.sdf.format(new Date()) + "] ", acebotsthree.TIMECOLOR);
            printlnAll("Error in event " + event + ".", new Color(255, 96, 93));
            printAll("[" + BotCore.sdf.format(new Date()) + "] ", acebotsthree.TIMECOLOR);
            printlnAll(e.toString(), new Color(255, 107, 104));
            printAll("[" + BotCore.sdf.format(new Date()) + "] ", acebotsthree.TIMECOLOR);
            printlnAll(e.getStackTrace()[0].toString(), new Color(255, 107, 104));

            if (!channelError.equals(""))
            {
                printChannel(channelError, "[" + BotCore.sdf.format(new Date()) + "] ", acebotsthree.TIMECOLOR);
                printlnChannel(channelError, "Error in event " + event + ".", new Color(255, 107, 104));
                printChannel(channelError, "[" + BotCore.sdf.format(new Date()) + "] ", acebotsthree.TIMECOLOR);
                printlnChannel(channelError, e.toString(), new Color(255, 107, 104));
                printChannel(channelError, "[" + BotCore.sdf.format(new Date()) + "] ", acebotsthree.TIMECOLOR);
                printlnChannel(channelError, e.getStackTrace()[0].toString(), new Color(255, 107, 104));
            }
        }
    }

    public void createEvent(String name)
    {
        if (!alMap.containsKey(name))
            alMap.put(name, new ArrayList<ActionListener>());
        else
            System.out.println("[Plugins] Could not create event " + name + ".");
    }

    public void onDisconnect()
    {
        fire("onDisconnect", new String[]{});
        super.disconnect();
        isConnected = false;
        System.out.println("I HAVE DISCONNECTED");
        acebotsGUI.setTitle("Acebots III :|: Disconnected");/*
        Timer reconnectTimer = new Timer(15000, reconnectAL);
        if (isConnected)
        {
            reconnectTimer.stop();
            return;
        }
        reconnectTimer.setInitialDelay(10000);
        reconnectTimer.start();*/
        //new AcebotsIII();
        //System.exit(1);
    }

    ActionListener reconnectAL = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            printAll("[" + BotCore.sdf.format(new Date()) + "] ", graphics.acebotsthree.TIMECOLOR);
            printlnAll("[Acebots] Attempting to Reconnect", new Color(255,0,0));
            try {
                String info[] = new String[4];
                FileReader fr = new FileReader("config.txt");
                BufferedReader reader = new BufferedReader(fr);
                for (int i = 0; i < 4; i++)
                {
                    try {
                        info[i] = reader.readLine().split("=", 2)[1];
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }

                acebotsConnect(info[0], info[1], info[2], 80, info[3]);
            } catch (Exception e1) {
                System.out.println("asdf error in rc");
            }
        }
    };

    //Begin PircBot event handlers and fire our own
    public void onMessage(String channel, String sender, String login, String hostname, String message)
    {
        if (sender.equalsIgnoreCase(channel.substring(1)) && message.toLowerCase().equalsIgnoreCase("!kill"))
            System.exit(-5);
        //This only gets fired for twitchnotify and other non-user messages.
        fire("onMessage", new String[]{channel, sender, message, "#808080", "0", "0", "user"});
        if (message.substring(0, TRIGGER.length()).equals(TRIGGER))
            fire("onCommand", new String[]{channel, sender, "1", message});
        if (message.toLowerCase().startsWith(getNick().toLowerCase() + ", "))
            fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 2)[1]});
        if (message.toLowerCase().startsWith("acebots iii, "))
            fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 3)[2]});

        if ((message.contains("subscribed!") || message.contains("subscribed for")) && sender.equalsIgnoreCase("twitchnotify"))
            fire("onSubscribe", new String[]{channel, message.split(" ")[0]});
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
            //System.out.println(mode.split(" ")[2].toLowerCase() + mode.split(" ")[0]);
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

    protected void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice)
    {
        fire("onNotice", new String[]{});
    }

    protected void onTopic(String channel, String topic, String setBy, long date, boolean changed)
    {
        fire("onTopic", new String[]{});
    }

    protected void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient)
    {
        fire("onOP", new String[]{});
    }

    protected void onServerPing(String response) {
        fire("onServerPing", new String[]{response});
        sendRawLine("PONG " + response);
    }

    public void onUnknown(String line) {

        String origin = "";
        String[] messageInfo = new String[3];
        String lineRep = line;
        while (!origin.contains("tmi.twitch.tv")) {
            messageInfo = lineRep.split(":", 3);
            origin = messageInfo[1];
            lineRep = lineRep.replaceFirst(":", ",");
        }

        String tags = messageInfo[0];
        String message = "";
        if (messageInfo.length >= 3) {
            message = messageInfo[2];
        }

        String twitchMessageType = origin.split(" ")[1];

        String channel = origin.split(" ")[2];
        HashMap<String, String> parameterValueMap = new HashMap<String, String>();

        /*if (origin.split(" ")[0].equals("twitchnotify!twitchnotify@twitchnotify.tmi.twitch.tv"))
        {
            if (message.endsWith("just subscribed!")) {
                String sender = message.split(" ")[0];
                fire("onSubscribe", new String[]{channel, sender});
                return;
            }
        }*/

        if (tags.length() > 0) {
            String[] tempArray = tags.substring(1).split(";");
            for (String paramValue:tempArray)
            {
                String[] paramValueArray = paramValue.split("=");
                if (paramValueArray.length == 1) {
                    parameterValueMap.put(paramValueArray[0], "");
                } else {
                    parameterValueMap.put(paramValueArray[0], paramValueArray[1]);
                }
            }
        }


        if (twitchMessageType.equals("PRIVMSG") || twitchMessageType.equals("ACTION")) {
            String sender,userColor, isSubscriber, isTurbo, userType;
            sender = parameterValueMap.get("display-name");
            if (sender.equals(""))
                sender = origin.split("!")[0];

            try {
                userColor = parameterValueMap.get("color");
            } catch (ArrayIndexOutOfBoundsException e1) {
                userColor = "#646464"; //default no specified color
            }
            if (sender.equalsIgnoreCase(this.getNick())) {
                int r, g, b;
                r = Integer.valueOf(userColor.substring(1, 3), 16);
                g = Integer.valueOf(userColor.substring(3, 5), 16);
                b = Integer.valueOf(userColor.substring(5, 7), 16);
                botColor = new Color(r, g, b);
            }

            isSubscriber = parameterValueMap.get("subscriber");
            isTurbo = parameterValueMap.get("turbo");

            try {
                userType = parameterValueMap.get("user-type");
            } catch (ArrayIndexOutOfBoundsException e1) {
                userType = "user"; //default no specified color
            }

            if (message.length() > 7) {
                if (message.substring(1, 7).equals("ACTION")) {
                    fire("onMe", new String[]{channel, sender, message.substring(8, message.length() - 1), userColor, isSubscriber, isTurbo, userType});
                    return;
                }
            }

            fire("onMessage", new String[]{channel, sender, message, userColor, isSubscriber, isTurbo, userType});
            if (message.substring(0, TRIGGER.length()).equals(TRIGGER))
                fire("onCommand", new String[]{channel, sender, "1", message});
            if (message.toLowerCase().startsWith(getNick().toLowerCase() + ", "))
                fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 2)[1]});
            if (message.toLowerCase().startsWith("acebots iii, "))
                fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 3)[2]});

        } else if (twitchMessageType.equals("USERNOTICE")) {
            String sender, messageID, messageParam;

            sender = parameterValueMap.get("display-name");
            try {
                messageID = parameterValueMap.get("msg-id");
            } catch (ArrayIndexOutOfBoundsException e1) {
                messageID = "";
            }

            try {
                messageParam = parameterValueMap.get("msg-param-months");
            } catch (ArrayIndexOutOfBoundsException e1) {
                messageParam = "";
            }

            if (messageID.equals("resub")) {
                fire("onResubscribe", new String[]{channel, sender, messageParam});
            }
        } else if (twitchMessageType.equals("USERSTATE")) {
            int r, g, b;
            String userColor = parameterValueMap.get("color");
            r = Integer.valueOf(userColor.substring(1, 3), 16);
            g = Integer.valueOf(userColor.substring(3, 5), 16);
            b = Integer.valueOf(userColor.substring(5, 7), 16);
            botColor = new Color(r, g, b);
        }

        /* else if (twitchMessageType.equals("ACTION")) {
            System.out.println("asdf");
        }*/

        /*                if (message.contains("ACTION")) {
                    if (message.substring(1, 7).equals("ACTION"))
                        fire("onMe", new String[]{channel, sender, message.substring(8, message.length() - 1), userColor, isSubscriber, isTurbo, userType});
                }
                else {
                    fire("onMessage", new String[]{channel, sender, message, userColor, isSubscriber, isTurbo, userType});
                }
                if (message.substring(0, TRIGGER.length()).equals(TRIGGER))
                    fire("onCommand", new String[]{channel, sender, "1", message});
                if (message.toLowerCase().startsWith(getNick().toLowerCase() + ", "))
                    fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 2)[1]});
                if (message.toLowerCase().startsWith("acebots iii, "))
                    fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 3)[2]});*/
    }

    protected void onUnknownerer(String line) {
        String[] messageInfo = line.split(" ", 5);
        String[] newTwitchInfo;
        if (line.startsWith("@")) {
            //There is also @broadcaster
            if (line.startsWith("@ban-reason")) //ban message
            {
                System.out.println(1);
                String reason = messageInfo[0].split("=", 2)[1].replace("\\s", " ");
                String channel = messageInfo[3];
                String bannedUser = messageInfo[4].substring(1);
                fire("onBan", new String[]{channel, bannedUser, reason});
                return;
            }
            if (line.startsWith("@ban-duration")) //timeout message
            {
                String duration = messageInfo[0].split(";")[0].split("=")[1];
                String reason = messageInfo[0].split("=", 3)[2].replace("\\s", " ");;
                String channel = messageInfo[3];
                String bannedUser = messageInfo[4].substring(1);
                fire("onTimeout", new String[]{channel, bannedUser, duration, reason});
                return;
            }
            if (line.startsWith("@color")) {
                newTwitchInfo = ("@badges=;" + messageInfo[0].substring(1)).split(";");
            }
            else {
                newTwitchInfo = messageInfo[0].split(";");
            }
            /* Twitchinfo array
            0 - Badges
            1 - Color
            2 - Display Name
            3 - Emotes (#)
            4 - Mod (0/1)
            5 - Room ID (#)
            6 - Subscriber (0/1)
            7 - Turbo (0/1)
            8 - User ID (#)
            9 - User Type (Mod, Admin, etc.)
            */

            String badges = newTwitchInfo[0];
            String userColor;
            try {
                userColor = newTwitchInfo[1].split("=")[1];
            } catch (ArrayIndexOutOfBoundsException e1) {
                userColor = "#646464"; //default no specified color
            }

            String sender;
            try {
                sender = newTwitchInfo[2].split("=")[1];
            } catch (ArrayIndexOutOfBoundsException e1) {
                sender = messageInfo[2].split("!")[0].substring(1);
            }

            if (sender.equalsIgnoreCase(this.getNick())) {
                int r, g, b;
                r = Integer.valueOf(userColor.substring(1, 3), 16);
                g = Integer.valueOf(userColor.substring(3, 5), 16);
                b = Integer.valueOf(userColor.substring(5, 7), 16);
                botColor = new Color(r, g, b);
            } else {
                //  String userEmotes = newTwitchInfo[2].split("=")[1];
                String isSubscriber = newTwitchInfo[6].split("=")[1];
                String isTurbo = newTwitchInfo[7].split("=")[1];

                String userType;
                try {
                    userType = newTwitchInfo[9].split("=")[1];
                } catch (ArrayIndexOutOfBoundsException e1) {
                    userType = "user";
                }

                String channel = messageInfo[3];
                String message = messageInfo[4].substring(1);

                /*if (userType.equals("mod")) {
                    if (!modList.contains(sender.toLowerCase() + addHash(channel))) {
                        System.out.println("Added mod " + sender + addHash(channel));
                        modList.add(sender.toLowerCase() + addHash(channel));
                        //Super shitty temporary fix thing.
                    }
                }*/

                if (message.contains("ACTION")) {
                    if (message.substring(1, 7).equals("ACTION"))
                        fire("onMe", new String[]{channel, sender, message.substring(8, message.length() - 1), userColor, isSubscriber, isTurbo, userType});
                }
                else {
                    fire("onMessage", new String[]{channel, sender, message, userColor, isSubscriber, isTurbo, userType});
                }
                if (message.substring(0, TRIGGER.length()).equals(TRIGGER))
                    fire("onCommand", new String[]{channel, sender, "1", message});
                if (message.toLowerCase().startsWith(getNick().toLowerCase() + ", "))
                    fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 2)[1]});
                if (message.toLowerCase().startsWith("acebots iii, "))
                    fire("onCommand", new String[]{channel, sender, "1", TRIGGER + message.split(" ", 3)[2]});
            }
        }
        if (line.startsWith("@msg-id=msg_duplicate"))
        {
            newTwitchInfo = line.split(" ");
            String channel = newTwitchInfo[3];
            printlnChannel(channel, "[" + BotCore.sdf.format(new Date()) + "] Message failed to send to Twitch (Identical message within 30 seconds).", graphics.acebotsthree.TIMECOLOR);
            printlnAll("[" + BotCore.sdf.format(new Date()) + "] Message failed to send to Twitch (Identical message within 30 seconds).", graphics.acebotsthree.TIMECOLOR);
        }
        if (line.startsWith("@msg-id=msg_ratelimit"))
        {
            newTwitchInfo = line.split(" ");
            String channel = newTwitchInfo[3];
            printlnChannel(channel, "[" + BotCore.sdf.format(new Date()) + "] Message failed to send to Twitch (Sending messages too fast).", graphics.acebotsthree.TIMECOLOR);
            printlnAll("[" + BotCore.sdf.format(new Date()) + "] Message failed to send to Twitch (Sending messages too fast).", graphics.acebotsthree.TIMECOLOR);
        }

        /*String str = line;
        String findStr = "cirLewd";
        int lastIndex = 0;
        while(lastIndex != -1){

            lastIndex = str.indexOf(findStr,lastIndex);

            if(lastIndex != -1){
                lewdCount ++;
                lastIndex += findStr.length();
            }
        }
        System.out.println("cirLewd count: " + lewdCount);*/
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
        System.out.println(defaultPlugins.length + " is the default len");

        for (int i = 0; i < defaultPlugins.length; i++)
        {
            System.out.println(defaultPlugins[i].getName() + " attempting load");
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

/*    private static Class[] loadPlugins(String packageName) {
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
        System.out.println(dirs.size());
        for (File directory : dirs) {
            try {
                if(BotCore.class.getClassLoader().getResource("DefaultPlugins").getPath().contains(".jar")) {
                    classes.addAll(findJarClasses(directory, packageName));
                } else {
                    classes.addAll(findClasses(directory, packageName));
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class> findJarClasses(File directory, String packageName) throws ClassNotFoundException {
        System.out.println(new File(BotCore.class.getResource("").getPath()).getParent());
        String s = new File(BotCore.class.getResource("").getPath()).getParent();
        System.out.println("AceID: " + s);
        s = s.replaceAll("(!|file:\\\\)", "");
        if (s.startsWith("file")) { // Linux paths fix
            s = s.substring(5);
        }
//        System.out.println("AceID2: " + s);
        ArrayList<File> files = new ArrayList<File>();
        try {
            System.out.println("Trying to find the jar");
//            System.out.println("path: \"" + s + "\"");
            JarFile jar = new JarFile(s);
//            System.out.println("Found jar");
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if (je.getName().startsWith(packageName) && !je.getName().contains("$") && je.getName().contains(".class")) {
//                    System.out.println(je.getName());
                    String filePath = packageName + "/" + je.getName().substring(je.getName().indexOf("/") + 1, je.getName().length());
                    System.out.println("AceID3: " + filePath);
                    File f = new File(filePath);
                        /*f.deleteOnExit();
                        FileOutputStream resourceOS = new FileOutputStream(f);
                        byte[] byteArray = new byte[1024];
                        int i;
                        InputStream classIS = BotCore.class.getClassLoader().getResourceAsStream(filePath);
                        while ((i = classIS.read(byteArray)) > 0) {
                            resourceOS.write(byteArray, 0, i);
                        }
                        classIS.close();
                        resourceOS.close(); / () /
                    files.add(f);
                }
            }
            jar.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        List<Class> classes = new ArrayList<Class>();
        System.out.println("directory: " + directory.getPath());
//        if (!directory.exists()) {
//            System.out.println("No thing");
//            return classes;
//        }
        File[] files2 = directory.listFiles();
        System.out.println(files2.length);
        for (File file : files2) {
            System.out.println("AceID5: " + file.getParentFile() + " & " + file.getName());

            //if (file.isDirectory()) {
                //assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
           // }
//            else if (file.getName().endsWith(".class")) {
            if (!file.getName().contains("$")) {
                	/*
	                	System.out.println("AceID4: " + Class.forName(packageName + '.' + file.getName()));
	                    classes.add(Class.forName(packageName + '.' + file.getName() ));//.substring(0, file.getName().indexOf(".class"))));
	//                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	                    System.out.println("Class initiated"); /()/
                List<String> classNames = new ArrayList<String>();
                ZipInputStream zip;
                try {
                    zip = new ZipInputStream(new FileInputStream(s));

                    for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                        if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                            // This ZipEntry represents a class. Now, what class does it represent?
                            String className = file.getName().replace('/', '.'); // including ".class"
                            classNames.add(className.substring(0, className.length() - ".class".length()));
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
//            }
        }
        return classes;
    }

    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        System.out.println("FC 1");
        System.out.println("directory2: " + directory.getPath());
        if (!directory.exists()) {
            System.out.println("No thing");
            return classes;
        }
        System.out.println("FC 2");
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                //assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                System.out.println("AceID1:" + file.getName());
                if (!file.getName().contains("$")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        System.out.println("Found classes length = " + classes.size());
        return classes;
    }*/

    private static Class[] loadPlugins(String packageName) {
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
        System.out.println(dirs.size());
        for (File directory : dirs) {
            try {
                if(BotCore.class.getClassLoader().getResource("DefaultPlugins").getPath().contains(".jar")) {
                    classes.addAll(findJarClasses(directory, packageName));
                } else {
                    classes.addAll(findClasses(directory, packageName));
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return classes.toArray(new Class[classes.size()]);
    }

    private static List<Class> findJarClasses(File directory, String packageName) throws ClassNotFoundException {
        System.out.println(new File(BotCore.class.getResource("").getPath()).getParent());
        String s = new File(BotCore.class.getResource("").getPath()).getParent();
        s = s.replaceAll("(!|file:\\\\)", "");
        if (s.startsWith("file")) { // Linux paths fix
            s = s.substring(5);
        }
        ArrayList<File> files = new ArrayList<File>();
        try {
            System.out.println("Trying to find the jar");
//            System.out.println("path: \"" + s + "\"");
            JarFile jar = new JarFile(s);
//            System.out.println("Found jar");
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry je = entries.nextElement();
                if (je.getName().startsWith(packageName) && !je.getName().contains("$") && je.getName().contains(".class")) {
//                    System.out.println(je.getName());
                    try {
                        String filePath = packageName + "/" + je.getName().substring(je.getName().indexOf("/") + 1, je.getName().length());
//                        System.out.println(filePath);
                        File f = File.createTempFile(filePath, null);
                        FileOutputStream resourceOS = new FileOutputStream(f);
                        byte[] byteArray = new byte[1024];
                        int i;
                        InputStream classIS = BotCore.class.getClassLoader().getResourceAsStream(filePath);
                        while ((i = classIS.read(byteArray)) > 0) {
                            resourceOS.write(byteArray, 0, i);
                        }
                        classIS.close();
                        resourceOS.close();
                        files.add(f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        List<Class> classes = new ArrayList<Class>();
        System.out.println("directory: " + directory.getPath());
//        if (!directory.exists()) {
//            System.out.println("No thing");
//            return classes;
//        }
//        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                //classes.addAll(findClasses(file, packageName + "." + file.getName()));
            }
//            else if (file.getName().endsWith(".class")) {
            if (!file.getName().contains("$")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().indexOf(".class"))));
//                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                System.out.println("Class initiated");
            }
//            }
        }
        return classes;
    }

    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        System.out.println("directory: " + directory.getPath());
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
                if (!file.getName().contains("$")) {
                    classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
                }
            }
        }
        return classes;
    }

    //@Deprecated
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

        if (channelAccessMap.containsKey(channel.toLowerCase()))
            channelAccess = channelAccessMap.get(channel.toLowerCase());
        else
            channelAccess = 1;

        if (exceptionMap != null)
        {
            for (String chan:exceptionMap.keySet())
            {
                if (chan.equalsIgnoreCase(channel))
                {
                    cReqAccess = exceptionMap.get(chan);
                }
            }
        }

        if (isMod(channel, user))
            userAccess = Math.max(3, userAccess);

        if (channel.substring(1).equalsIgnoreCase(user))
            userAccess = Math.max(4, userAccess);

        if (userAccess >= uReqAccess && channelAccess >= cReqAccess)
        {
            return true;
        }
        else
            return false;
    }

    public boolean hasAccess(String channel, String user, int cReqAccess, int uReqAccess, HashMap<String, Integer> exceptionMap, boolean isSubscriber)
    {
        int userAccess;
        int channelAccess;

        if (user.equalsIgnoreCase(getNick()))
            return true;

        if (userAccessMap.containsKey(user.toLowerCase()))
            userAccess = userAccessMap.get(user.toLowerCase());
        else
            userAccess = 1;

        if (channelAccessMap.containsKey(channel.toLowerCase()))
            channelAccess = channelAccessMap.get(channel.toLowerCase());
        else
            channelAccess = 1;

        if (exceptionMap != null)
        {
            for (String chan:exceptionMap.keySet())
            {
                if (chan.equalsIgnoreCase(channel))
                {
                    cReqAccess = exceptionMap.get(chan);
                }
            }
        }

        if (isSubscriber)
            userAccess = Math.max(2, userAccess);

        if (isMod(channel, user))
            userAccess = Math.max(3, userAccess);

        if (channel.substring(1).equalsIgnoreCase(user))
            userAccess = Math.max(4, userAccess);

        if (userAccess >= uReqAccess && channelAccess >= cReqAccess)
        {
            return true;
        }
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
                for (int i = 0; i < acebotsGUI.inputTab.getTabCount(); i++)
                {
                    if (acebotsGUI.inputTab.getTitleAt(i).endsWith(name.replace("#", "")))
                    {
                        acebotsGUI.allChatLeftPane.removeTabAt(i + 1);
                        acebotsGUI.allChatRightPane.removeTabAt(i + 1);
                        acebotsGUI.inputTab.removeTabAt(i);
                        acebotsGUI.channelListBox.removeItem(name);
                        fire("onBotLeave", new String[]{name});
                        return true;
                    }
                }
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
                return false;
            }
        }
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
        try {
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

    public Color getBotColor()
    {
        return botColor;
    }
}

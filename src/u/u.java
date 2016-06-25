package u;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;

public class u {

    public synchronized static void appendTextPane(Color color, String message, JTextPane box)
    {
        StyledDocument doc = box.getStyledDocument();
        Style style = box.addStyle("style", null);
        /*if (color == null) {
            color = new Color(255, 255, 255);
        }*/
        StyleConstants.setForeground(style, color);
        box.setCaretPosition(doc.getLength());
        try {
            doc.insertString(box.getCaretPosition(), message, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        box.setCaretPosition(doc.getLength());
        try {
            cleanUpChat(box);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public synchronized static void timeAppendTextPane(Color color, String message, JTextPane box)
    {
        //Autodo it and build it like normal
        StyledDocument doc = box.getStyledDocument();
        Style style = box.addStyle("style", null);
        StyleConstants.setForeground(style, color);
        box.setCaretPosition(doc.getLength());
        try {
            doc.insertString(box.getCaretPosition(), message, style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        box.setCaretPosition(doc.getLength());
        try {
            cleanUpChat(box);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private static void cleanUpChat(JTextPane box) throws BadLocationException {
        StyledDocument doc =  box.getStyledDocument();
        String[] lines = box.getStyledDocument().getText(0,  box.getStyledDocument().getLength()).split("\n");
        if (lines.length <= 100)
            return;
        String firstLine = lines[0];
        doc.remove(0, firstLine.length() + 1);
    }

    public static boolean isInteger(String input)
    {
        try
        {
            Integer.parseInt(input);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public static boolean isDouble(String input)
    {
        try
        {
            Double.parseDouble(input);
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    public static String addHash(String channel)
    {
        if (!channel.substring(0,1).equals("#"))
            return "#" + channel;
        return channel;
    }

    public static boolean isBlank(String message)
    {
        if (message != null && !message.isEmpty())
            return false;
        else
            return true;
    }

    public static boolean isValidTime(String time)
    {
        String[] times = time.split(":");
        for (int i = 0; i < times.length - 1; i++)
            if (!isInteger(times[i]))
                return false;
            if (!isDouble(times[times.length - 1]))
                return false;
        return true;
    }

    public static String stripQuotes(String msg)
    {
        if (msg.startsWith("\""))
            msg = msg.substring(1);
        if (msg.substring(msg.length() - 1).equals("\""))
            msg = msg.substring(0, msg.length() - 1);
        return msg;
    }

    public static String trim(String message)
    {
        while (message.startsWith(" ")) {
            message = message.substring(1);
        }
        while (message.endsWith(" ")) {
            message = message.substring(0, message.length() - 1);
        }
        return message;
    }

    public static HashMap<String, String> readJSON(String line)
    {
        return null;
    }

    public static String[] getArgs(ActionEvent e)
    {
        return e.getActionCommand().split("``");
    }
    
    public static boolean isCommand(String command, String message)
    {
        String cmd = message.split(" ")[0];

        //System.out.println("Len: " + cmd.replace("!", "").replace("/", "").length() + " vs. " + command.length());


        if (cmd.replace("!", "").replace("/", "").length() != command.length())
            return false;

        //System.out.println("Ends: " + cmd.substring(cmd.replace("!", "/").lastIndexOf("/") + 1, command.length() + cmd.replace("!", "/").lastIndexOf("/") + 1));

        if (cmd.toLowerCase().endsWith(command.toLowerCase()) && //Check End
    	    cmd.substring(cmd.replace("!", "/").lastIndexOf("/") + 1, command.length() + cmd.replace("!", "/").lastIndexOf("/") + 1).equalsIgnoreCase(command)) //Check right after trigger
    		return true;
    	else
    		return false;
    }

    public static HashMap<String, Integer> fillAccessExceptionMap(String[] cmdInfo)
    {
        HashMap<String, Integer> accessExceptionMap = new HashMap<String, Integer>();
        for (int i = 3; i < cmdInfo.length; i++)
            accessExceptionMap.put(cmdInfo[i].substring(1).toLowerCase(), Integer.parseInt(cmdInfo[i].substring(0,1)));
        return accessExceptionMap;
    }
}

package data;

import java.util.Date;

public class CustomCommand {

	private String name;
	private String response;
	private int userAccess;
	private int channelAccess;
    private String channel;

    public static String[] fromArray = {"%n", "%c", "%s", "%t", "%m", "%b", "/timeout", "/ban"};

	public CustomCommand() { }
	public CustomCommand(String cmdName, String cmdResponse, String cmdChannel)
	{
        if (cmdName.contains("#"))
        {
            name = cmdName.split("#")[0];
            channel = cmdName.split("#")[1];
        }
        else
        {
            name = cmdName;
            channel = cmdChannel;
        }

		response = cmdResponse;
		userAccess = 1;
		channelAccess = 1;
	}
	
	public CustomCommand(String cmdName, int uAccess, int cAccess, String cmdResponse, String cmdChannel)
	{
        if (cmdName.contains("#"))
        {
            name = cmdName.split("#")[0];
            channel = cmdName.split("#")[1];
        }
        else
        {
            name = cmdName;
            channel = cmdChannel;
        }
		response = cmdResponse;
		userAccess = uAccess;
		channelAccess = cAccess;
	}

	public String getCmd()
	{
		return name;
	}
	
	public String getResponse()
	{
		return response;
	}
	
	public int getUserAccess()
	{
		return userAccess;
	}
	
	public int getChannelAccess()
	{
		return channelAccess;
	}

    public String getChannel()
    {
        return channel;
    }
}

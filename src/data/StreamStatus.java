package data;

import java.util.Date;

public class StreamStatus {

    private Date streamStart;
    private Date gameStart;
    private String previousGameName;

    public StreamStatus() { }

    public StreamStatus(Date streamStartTime, Date gameStartTime, String newGameName)
    {
        streamStart = streamStartTime;
        gameStart = gameStartTime;
        previousGameName = newGameName;
    }

    public StreamStatus(Date gameStartTime, String newGameName)
    {
        gameStart = gameStartTime;
        previousGameName = newGameName;
        //Twitch provides us the ability to pull start time from API so we can look it up here if we want.
    }

    public Date getStreamStart()
    {
        return streamStart;
    }

    public void setStreamStart(Date newStreamStart)
    {
        streamStart = newStreamStart;
    }

    public Date getGameStart()
    {
        return gameStart;
    }

    private void setGameStart(Date newGameStart)
    {
        gameStart = newGameStart;
    }

    public String getPreviousGameName()
    {
        return previousGameName;
    }

    public void setNewGameName(String newGameName)
    {
        previousGameName = newGameName;
        if (previousGameName.equals("Offline"))
            setGameStart(null);
        else
            setGameStart(new Date());
    }
}

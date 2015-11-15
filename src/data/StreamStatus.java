package data;

import java.util.Calendar;
import java.util.TimeZone;

public class StreamStatus {

    private Calendar streamStart;
    private Calendar gameStart;
    private String previousGameName;

    public StreamStatus() { }

    public StreamStatus(Calendar streamStartTime, Calendar gameStartTime, String newGameName)
    {
        streamStart = streamStartTime;
        gameStart = gameStartTime;
        previousGameName = newGameName;
    }

    public StreamStatus(Calendar gameStartTime, String newGameName)
    {
        gameStart = gameStartTime;
        previousGameName = newGameName;
        //Twitch provides us the ability to pull start time from API so we can look it up here if we want.
    }

    public Calendar getStreamStart()
    {
        return streamStart;
    }

    public void setStreamStart(Calendar newStreamStart)
    {
        streamStart = newStreamStart;
    }

    public Calendar getGameStart()
    {
        return gameStart;
    }

    private void setGameStart(Calendar newGameStart)
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
            setGameStart(Calendar.getInstance(TimeZone.getTimeZone("GMT")));
    }
}

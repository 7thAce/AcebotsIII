package data;

public class WorldRecordNew {

    private String game;
    private String WRholder;
    private String time;
    private String[] categories;
    private String[] gameAliases;

    public WorldRecordNew() { }

    public WorldRecordNew(String gameString, String wrTime, String holder, String categoriesString)
    {
        gameAliases = gameString.split(",");
        game = gameAliases[0];
        WRholder = holder;
        time = wrTime;
        categories = categoriesString.replace(",", "").split(",");
    }

    public WorldRecordNew(String line)
    {
        String[] splitLine = line.split("//");
        gameAliases = splitLine[0].split(",");

        String longest = "";
       // for (String gameN:gameAliases)
         //   if (gameN.length() > )
        game = gameAliases[gameAliases.length - 1];
        time = splitLine[1];
        WRholder = splitLine[2];
        categories = splitLine[3].split(",");
    }

    public WorldRecordNew(String[] splitLine)
    {
        gameAliases = splitLine[0].split(",");
        game = gameAliases[0];
        time = splitLine[1];
        WRholder = splitLine[2];
        categories = splitLine[3].replace(",", "").split(",");
    }

    public String getWRholder() {
        return WRholder;
    }

    public String getGame() {
        return game;
    }

    public String getTime() {
        return time;
    }

    public String[] getCategories() {
        return categories;
    }

    public void setWRholder(String newWRholder) {
        WRholder = newWRholder;
    }

    public void setGame(String newGame) {
        game = newGame;
    }

    public void setTime(String newTime) {
        time = newTime;
    }

    public void setCategories(String newCategories) {
        categories = newCategories.split(",");
    }

    public void setAliases(String aliasString)
    {
        gameAliases = aliasString.split(",,");
    }

    public String toString() {
        StringBuilder categoriesString = new StringBuilder();
        for (int i = 0; i < categories.length; i++)
            categoriesString.append("," + categories[i]);
        return game + "//" + time + "//" + WRholder + "//" + categoriesString.substring(1);
    }
}
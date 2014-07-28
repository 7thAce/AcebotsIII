package data;

public class WorldRecord {

    private String game;
    private String WRholder;
    private String time;
    private String[] categories;

    public WorldRecord() { }

    public WorldRecord(String game, String holder, String time, String categoriesString)
    {
        this.game = game;
        setWRholder(holder);
        this.time = time;
        categories = categoriesString.split(",");
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

    public String toString() {
        StringBuilder categoriesString = new StringBuilder();
        for (int i = 0; i < categories.length; i++)
            categoriesString.append("," + categories[i]);
        return game + "//" + time + " by " + WRholder + "//" + categoriesString.substring(1);
    }
}

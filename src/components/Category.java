package components;

public class Category {

    private String main;
    private String sub;

    public Category(String main, String sub) {
        this.main = main;
        this.sub = sub;
    }

    //region Getters

    public String getMain() {
        return main;
    }

    public String getSub() {
        return sub;
    }

    //endregion

    //region Setters

    public void setMain(String main) {
        this.main = main;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    //endregion

}
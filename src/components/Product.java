package components;

import use.Date;

public class Product {

    private String iconURL;        // Icon url - ../images/icon.png
    private String name;           // Product name
    private float price;           // Product price - 10.99
    private int discount;          // Product discount - 10%
    private Date dateAdded;        // When the product is added
    private Date dateLastChange;   // When is the last change of the product
    private String categoryMain;   // Main category of the product - <category></category>
    private String categorySub;    // Sub category of the product - _category
    private String userFrom;       // User that added the product
    private String info;           // Short description

    // Create product - default
    public Product(String iconURL, String name, float price, int discount, Date dateAdded, Date dateLastChange, String categoryMain, String categorySub, String userFrom, String info) {
        this.iconURL = iconURL;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.dateAdded = dateAdded;
        this.dateLastChange = dateLastChange;
        this.categoryMain = categoryMain;
        this.categorySub = categorySub;
        this.userFrom = userFrom;
        this.info = info;
    }

    //region Getters

    public String getIconURL() {
        return iconURL;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getDiscount() {
        return discount;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public Date getDateLastChange() {
        return dateLastChange;
    }

    public String getCategoryMain() {
        return categoryMain;
    }

    public String getCategorySub() {
        return categorySub;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public String getInfo() {
        return info;
    }

    //endregion

    //region Setters

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setDateLastChange(Date dateLastChange) {
        this.dateLastChange = dateLastChange;
    }

    public void setCategoryMain(String categoryMain) {
        this.categoryMain = categoryMain;
    }

    public void setCategorySub(String categorySub) {
        this.categorySub = categorySub;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    //endregion

}

package dev.twd.take_a_walk_duo.vos.shop;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;

public class ProductVo extends ArticleEntity {
    private int quantity;
    private int cost;
    private int discount;
    private int price;
    private String categoryText;

    private String text;

    private String categoryName;

    private Boolean isAdmin;

    public int getQuantity() {
        return quantity;
    }

    public ProductVo setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public int getCost() {
        return cost;
    }

    public ProductVo setCost(int cost) {
        this.cost = cost;
        return this;
    }

    public int getDiscount() {
        return discount;
    }

    public ProductVo setDiscount(int discount) {
        this.discount = discount;
        return this;
    }

    public int getPrice() {
        return price;
    }

    public ProductVo setPrice(int price) {
        this.price = price;
        return this;
    }

    public String getCategoryText() {
        return categoryText;
    }

    public void setCategoryText(String categoryText) {
        this.categoryText = categoryText;
    }

    public String getText() {
        return text;
    }

    public ProductVo setText(String text) {
        this.text = text;
        return this;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public ProductVo setCategoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public ProductVo setAdmin(Boolean admin) {
        isAdmin = admin;
        return this;
    }
}





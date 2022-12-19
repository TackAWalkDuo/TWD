package dev.twd.take_a_walk_duo.vos.shop;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;

public class ProductVo extends ArticleEntity {
    private int quantity;
    private int price;
    private String categoryText;

    public int getQuantity() {
        return quantity;
    }

    public ProductVo setQuantity(int quantity) {
        this.quantity = quantity;
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
}

package dev.test.take_a_walk_duo.vos.shop;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;

import java.util.Objects;

public class ProductVo extends ArticleEntity {
    private int quantity;
    private int price;
    private int categoryIndex;

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

    public int getCategoryIndex() {
        return categoryIndex;
    }

    public ProductVo setCategoryIndex(int categoryIndex) {
        this.categoryIndex = categoryIndex;
        return this;
    }
}

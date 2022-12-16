package dev.test.take_a_walk_duo.entities.shop;

import java.util.Objects;

public class SaleProductEntity {
    private int index;
    private int articleIndex;
    private int quantity;       // 수량
    private int cost;           //원가
    private int discount;       // 할인율(0~100)
    private int price;          // 판매가
    private int profit;         // 이익
    private String categoryText;  //상품 분류.
    private int deliveryFee;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public void setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public String getCategoryText() {
        return categoryText;
    }

    public SaleProductEntity setCategoryText(String categoryText) {
        this.categoryText = categoryText;
        return this;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public SaleProductEntity setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SaleProductEntity that = (SaleProductEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}

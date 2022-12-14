package dev.test.take_a_walk_duo.entities.shop;

import java.util.Date;
import java.util.Objects;

public class ShoppingCartEntity {
    private int index;
    private String userEmail;
    private int productIndex;
    private int salePrice;
    private int quantity;
    private Date registrationOn;

    public int getIndex() {
        return index;
    }

    public ShoppingCartEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public ShoppingCartEntity setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public int getProductIndex() {
        return productIndex;
    }

    public ShoppingCartEntity setProductIndex(int productIndex) {
        this.productIndex = productIndex;
        return this;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public ShoppingCartEntity setSalePrice(int salePrice) {
        this.salePrice = salePrice;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public ShoppingCartEntity setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public Date getRegistrationOn() {
        return registrationOn;
    }

    public ShoppingCartEntity setRegistrationOn(Date registrationOn) {
        this.registrationOn = registrationOn;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCartEntity that = (ShoppingCartEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}

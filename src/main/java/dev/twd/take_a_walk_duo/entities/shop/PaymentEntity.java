package dev.twd.take_a_walk_duo.entities.shop;

import java.util.Date;
import java.util.Objects;

public class PaymentEntity {
    private int index;
    private String userEmail;
    private int productIndex;
    private int salePrice;
    private int quantity;
    private String address;
    private int deliveryFee;
    private Date registrationOn;
    private int deliveryStatus;

    public int getIndex() {
        return index;
    }

    public PaymentEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public PaymentEntity setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        return this;
    }

    public int getProductIndex() {
        return productIndex;
    }

    public PaymentEntity setProductIndex(int productIndex) {
        this.productIndex = productIndex;
        return this;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public PaymentEntity setSalePrice(int salePrice) {
        this.salePrice = salePrice;
        return this;
    }

    public int getQuantity() {
        return quantity;
    }

    public PaymentEntity setQuantity(int quantity) {
        this.quantity = quantity;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public PaymentEntity setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
        return this;
    }

    public Date getRegistrationOn() {
        return registrationOn;
    }

    public PaymentEntity setRegistrationOn(Date registrationOn) {
        this.registrationOn = registrationOn;
        return this;
    }

    public int getDeliveryStatus() {
        return deliveryStatus;
    }

    public PaymentEntity setDeliveryStatus(int deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentEntity that = (PaymentEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}

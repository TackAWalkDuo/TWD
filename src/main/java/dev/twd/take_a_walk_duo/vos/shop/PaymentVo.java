package dev.twd.take_a_walk_duo.vos.shop;

import dev.twd.take_a_walk_duo.entities.shop.PaymentEntity;

public class PaymentVo extends PaymentEntity {
    private String title;

    public String getTitle() {
        return title;
    }

    public PaymentVo setTitle(String title) {
        this.title = title;
        return this;
    }
}

package dev.twd.take_a_walk_duo.vos.shop;


import dev.twd.take_a_walk_duo.entities.shop.ShoppingCartEntity;

import java.util.Date;

public class CartVo extends ShoppingCartEntity {
    private String title;

    private String boardId;
    private int deliveryFee;

    public String getTitle() {
        return title;
    }

    public CartVo setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getBoardId() {
        return boardId;
    }

    public CartVo setBoardId(String boardId) {
        this.boardId = boardId;
        return this;
    }

    public int getDeliveryFee() {
        return deliveryFee;
    }

    public CartVo setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
        return this;
    }
}


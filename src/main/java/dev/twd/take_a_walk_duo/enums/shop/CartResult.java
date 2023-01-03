package dev.twd.take_a_walk_duo.enums.shop;

import dev.twd.take_a_walk_duo.interfaces.IResult;

public enum CartResult implements IResult {
    CART_DUPLICATED, // 장바구니 중복시
    CART_NOT_ALLOWED, // boardId shop이 아닐시,
    CART_NOT_SIGNED // 로그인 되어 있지 않을시
}

package dev.test.take_a_walk_duo.enums.bbs;

import dev.test.take_a_walk_duo.interfaces.IResult;

public enum WriteResult implements IResult {
    NOT_ALLOWED, //작성자 != 로그인한사람
    NO_SUCH_BOARD //게시글을 찾을수 없음
}

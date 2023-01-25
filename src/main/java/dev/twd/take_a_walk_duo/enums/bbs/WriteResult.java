package dev.twd.take_a_walk_duo.enums.bbs;

import dev.twd.take_a_walk_duo.interfaces.IResult;

public enum WriteResult implements IResult {
    NOT_ALLOWED, //작성자 != 로그인한사람
    NO_SUCH_BOARD, //게시글을 찾을수 없음
    NOT_SAME    // 작성자와 로그인 사용자가 다를경우.
}

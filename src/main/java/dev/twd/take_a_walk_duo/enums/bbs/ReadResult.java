package dev.twd.take_a_walk_duo.enums.bbs;

import dev.twd.take_a_walk_duo.interfaces.IResult;

public enum ReadResult implements IResult {
    NO_SUCH_ARTICLE, //존재하지 않는 게시글
    NOT_ALLOWED,     //게시글 작성자 !=로그인한 사람
    NOT_SIGNED,       //로그인하지 않음
    NO_SUCH_COMMENT //  존재하지 않는 댓글
}

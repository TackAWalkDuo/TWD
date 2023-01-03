package dev.twd.take_a_walk_duo.enums.member;

import dev.twd.take_a_walk_duo.interfaces.IResult;

public enum ModifyUserResult implements IResult {
    NO_SUCH_USER,   // 유저가 없음
    NOT_ALLOWED     // 권한이 없음
}

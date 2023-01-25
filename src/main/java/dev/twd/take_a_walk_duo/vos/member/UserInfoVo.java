package dev.twd.take_a_walk_duo.vos.member;

import dev.twd.take_a_walk_duo.entities.member.UserEntity;

public class UserInfoVo extends UserEntity {
    private String userNickname;
    private String userHaveDog;

    public String getUserNickname() {
        return userNickname;
    }

    public UserInfoVo setUserNickname(String userNickname) {
        this.userNickname = userNickname;
        return this;
    }

    public String getUserHaveDog() {
        return userHaveDog;
    }

    public UserInfoVo setUserHaveDog(String userHaveDog) {
        this.userHaveDog = userHaveDog;
        return this;
    }
}

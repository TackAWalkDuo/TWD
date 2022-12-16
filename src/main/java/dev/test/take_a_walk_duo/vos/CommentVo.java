package dev.test.take_a_walk_duo.vos;

import dev.test.take_a_walk_duo.entities.bbs.CommentEntity;

public class CommentVo extends CommentEntity {
    private String userNickname;

    private int[] imageIndexes;

    public String getUserNickname() {
        return userNickname;
    }

    public CommentVo setUserNickname(String userNickname) {
        this.userNickname = userNickname;
        return this;
    }

    public int[] getImageIndexes() {
        return imageIndexes;
    }

    public CommentVo setImageIndexes(int[] imageIndexes) {
        this.imageIndexes = imageIndexes;
        return this;
    }
}

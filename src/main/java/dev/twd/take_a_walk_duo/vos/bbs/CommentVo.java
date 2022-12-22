package dev.twd.take_a_walk_duo.vos.bbs;

import dev.twd.take_a_walk_duo.entities.bbs.CommentEntity;

public class CommentVo extends CommentEntity {
    private String nickname;

    private int[] imageIndexes;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int[] getImageIndexes() {
        return imageIndexes;
    }

    public void setImageIndexes(int[] imageIndexes) {
        this.imageIndexes = imageIndexes;
    }
}

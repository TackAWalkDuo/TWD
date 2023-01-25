package dev.twd.take_a_walk_duo.vos.bbs;

import dev.twd.take_a_walk_duo.entities.bbs.CommentEntity;

public class CommentVo extends CommentEntity {
    private String nickname;

    private int[] imageIndexes;

    private boolean isSigned;

    private boolean isMine;

    private boolean isLiked;

    private int likeCommentCount;

    public int getLikeCommentCount() {
        return likeCommentCount;
    }

    public void setLikeCommentCount(int likeCommentCount) {
        this.likeCommentCount = likeCommentCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public CommentVo setSigned(boolean signed) {
        isSigned = signed;
        return this;
    }

    public boolean isMine() {
        return isMine;
    }

    public CommentVo setMine(boolean mine) {
        isMine = mine;
        return this;
    }

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

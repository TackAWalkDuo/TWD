package dev.test.take_a_walk_duo.entities.bbs;

import java.util.Objects;

public class CommentImageEntity {
    private int index;
    private int commentIndex;
    private byte[] data;
    private String type;

    public int getIndex() {
        return index;
    }

    public CommentImageEntity setIndex(int index) {
        this.index = index;
        return this;
    }

    public int getCommentIndex() {
        return commentIndex;
    }

    public CommentImageEntity setCommentIndex(int commentIndex) {
        this.commentIndex = commentIndex;
        return this;
    }

    public byte[] getData() {
        return data;
    }

    public CommentImageEntity setData(byte[] data) {
        this.data = data;
        return this;
    }

    public String getType() {
        return type;
    }

    public CommentImageEntity setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentImageEntity that = (CommentImageEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}

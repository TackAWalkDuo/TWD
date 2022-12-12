package dev.test.take_a_walk_duo.entities.bbs;

import javax.xml.crypto.Data;
import java.util.Objects;

public class CommentEntity {
    private int index;
    private Integer commentIndex;
    private String userEmail;
    private String content;
    private int articleIndex;
    private Data writtenOn;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Integer getCommentIndex() {
        return commentIndex;
    }

    public void setCommentIndex(Integer commentIndex) {
        this.commentIndex = commentIndex;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getArticleIndex() {
        return articleIndex;
    }

    public void setArticleIndex(int articleIndex) {
        this.articleIndex = articleIndex;
    }

    public Data getWrittenOn() {
        return writtenOn;
    }

    public void setWrittenOn(Data writtenOn) {
        this.writtenOn = writtenOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentEntity that = (CommentEntity) o;
        return index == that.index;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index);
    }
}

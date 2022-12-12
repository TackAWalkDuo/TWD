package dev.test.take_a_walk_duo.entities.bbs;

import java.util.Date;
import java.util.Objects;

public class CommentLikeEntity {
    private String userEmail;
    private int commentIndex;
    private Date createdOn;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getCommentIndex() {
        return commentIndex;
    }

    public void setCommentIndex(int commentIndex) {
        this.commentIndex = commentIndex;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentLikeEntity that = (CommentLikeEntity) o;
        return commentIndex == that.commentIndex && Objects.equals(userEmail, that.userEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userEmail, commentIndex);
    }
}

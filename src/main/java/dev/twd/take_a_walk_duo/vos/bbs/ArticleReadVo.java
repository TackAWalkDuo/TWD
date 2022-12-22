package dev.twd.take_a_walk_duo.vos.bbs;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;

public class ArticleReadVo extends ArticleEntity {
    private String userNickname;
    private String userSpecies;
    private boolean articleLiked;
    private int articleLikedCount;

    public String getUserNickname() {
        return userNickname;
    }

    public ArticleReadVo setUserNickname(String userNickname) {
        this.userNickname = userNickname;
        return this;
    }

    public String getUserSpecies() {
        return userSpecies;
    }

    public ArticleReadVo setUserSpecies(String userSpecies) {
        this.userSpecies = userSpecies;
        return this;
    }

    public boolean isArticleLiked() {
        return articleLiked;
    }

    public ArticleReadVo setArticleLiked(boolean articleLiked) {
        this.articleLiked = articleLiked;
        return this;
    }

    public int getArticleLikedCount() {
        return articleLikedCount;
    }

    public ArticleReadVo setArticleLikedCount(int articleLikedCount) {
        this.articleLikedCount = articleLikedCount;
        return this;
    }
}

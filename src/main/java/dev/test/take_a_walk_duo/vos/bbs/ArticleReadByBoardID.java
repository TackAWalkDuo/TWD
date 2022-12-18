package dev.test.take_a_walk_duo.vos.bbs;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;

public class ArticleReadByBoardID extends ArticleEntity {
    private String ArticleByBoardId;

    public String getArticleByBoardId() {
        return ArticleByBoardId;
    }

    public void setArticleByBoardId(String articleByBoardId) {
        ArticleByBoardId = articleByBoardId;
    }

}

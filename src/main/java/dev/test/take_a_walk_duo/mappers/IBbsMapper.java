package dev.test.take_a_walk_duo.mappers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.test.take_a_walk_duo.entities.bbs.CommentEntity;
import dev.test.take_a_walk_duo.entities.bbs.CommentImageEntity;
import dev.test.take_a_walk_duo.vos.bbs.ArticleReadVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IBbsMapper {

    BoardEntity selectBoardById(@Param(value = "bid") String id);

    //Mr.g
    //    글쓰기
    int insertArticle(ArticleEntity articleEntity);

    ArticleEntity selectThumbnailByIndex(@Param(value = "index") int index);

    //Mr.m
    //ArticleIndex 로 ArticleEntity 불러오기
    ArticleReadVo selectArticleByIndex(@Param(value = "index")int index,
                                       @Param(value = "email") String email);

    //Mr.m
    //ArticleEntity(수정)조회수 만들기
    int updateArticle(ArticleEntity articleEntity);

    int insertComment(CommentEntity comment);
    int insertCommentImage(CommentImageEntity commentImage);

}

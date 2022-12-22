package dev.twd.take_a_walk_duo.mappers;

import dev.twd.take_a_walk_duo.entities.bbs.*;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import dev.twd.take_a_walk_duo.vos.bbs.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IBbsMapper {

    BoardEntity selectBoardById(@Param(value = "bid") String id);

    //Mr.g
    //    글쓰기
    int insertArticle(ArticleEntity articleEntity);

    int insertArticleLike(ArticleLikeEntity articleLikeEntity);

    int deleteByArticleLiked(@Param(value = "articleIndex") int index);

    ArticleEntity selectThumbnailByIndex(@Param(value = "index") int index);

    //Mr.m
    //ArticleIndex 로 ArticleEntity 불러오기 + 수정
    default ArticleReadVo selectArticleByIndex(@Param(value = "index") int index) {
        return this.selectArticleByIndex(index, null);
    }

    ArticleReadVo selectArticleByIndex(@Param(value = "index") int index,
                                       @Param(value = "email") String email);

    ArticleLikeEntity selectArticleLikeByIndex(@Param(value = "articleIndex") int index,
                                               @Param(value = "userEmail") String email);

    //Mr.m
    //ArticleEntity(수정)조회수 만들기
    int updateArticle(ArticleEntity articleEntity);

    int insertComment(CommentEntity comment);

    int insertCommentImage(CommentImageEntity commentImage);

    int deleteArticle(int index);


    //Mr.m
    //게시판 만들기
    ArticleReadVo[] selectArticlesByBoardId(@Param(value = "boardId") String boardId,
                                            @Param(value = "limit") int limit,
                                            @Param(value = "offset") int offSet);

    int selectArticleCountByBoardId(@Param(value = "boardId") String boardId,
                                    @Param(value = "criterion") String criterion,
                                    @Param(value = "keyword") String keyword);


    //Mr.m
    //이미지추가 맵퍼
    int insertImage(ImageEntity image);

    ImageEntity selectImageByIndex(@Param(value = "index") int index);

    // 게시글 댓글
    CommentVo[] selectCommentByIndex(@Param(value = "index") int index);
    //댓글의 이미지 가져오기(map 에서 사용)
    CommentImageEntity[] selectCommentImagesByCommentIndexExceptData(@Param(value = "commentIndex") int commentIndex);

    CommentImageEntity selectCommentImageByIndex(@Param(value = "index") int index);
    BoardEntity[] selectBoardByBoardId(@Param(value = "bid") String bid);

}

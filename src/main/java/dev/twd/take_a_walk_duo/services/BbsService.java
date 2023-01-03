package dev.twd.take_a_walk_duo.services;

import dev.twd.take_a_walk_duo.entities.bbs.*;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.enums.bbs.ModifyArticleResult;
import dev.twd.take_a_walk_duo.enums.bbs.ReadResult;
import dev.twd.take_a_walk_duo.enums.bbs.WriteResult;
import dev.twd.take_a_walk_duo.interfaces.IResult;
import dev.twd.take_a_walk_duo.mappers.IBbsMapper;
import dev.twd.take_a_walk_duo.mappers.IShopMapper;
import dev.twd.take_a_walk_duo.models.PagingModel;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import dev.twd.take_a_walk_duo.vos.bbs.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@Service(value = "dev.twd.take_a_walk_duo.services.BbsService")
public class BbsService {

    private final IBbsMapper bbsMapper;

    private final IShopMapper shopMapper;

    @Autowired
    public BbsService(IBbsMapper bbsMapper, IShopMapper shopMapper) {
        this.bbsMapper = bbsMapper;
        this.shopMapper = shopMapper;
    }

    //1.write boardId 값 끌고오기
    //mr.s
    public BoardEntity getBoard(String id) {
        return this.bbsMapper.selectBoardById(id);
    }
    public BoardEntity getNoticeBoard(){
        return this.bbsMapper.selectNoticeBoardById();
    }


    //1.write boardId 값 끌고오기
    //mr.s
    public Enum<? extends IResult> RegisterArticle(ArticleEntity article, MultipartFile[] images) throws IOException {
        BoardEntity board = this.bbsMapper.selectBoardById(article.getBoardId());
        if (board == null) {
            return WriteResult.NO_SUCH_BOARD;
        }
        System.out.println(board.getId() + "이게문제인가?");

        if (images != null) {
            for (MultipartFile image : images) {
                article.setThumbnail(image.getBytes());
                article.setThumbnailType(image.getContentType());
            }
        } else {
            byte[] imageInByte;
            File defaultImage = new File("src/main/resources/static/resources/images/TAWD_logo.png");
            defaultImage.setReadable(true, false);

            System.out.println("file 권한  : " + defaultImage.canRead());
            System.out.println("file exit  : " + defaultImage.exists());

            BufferedImage originalImage = ImageIO.read(defaultImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "png", baos);
            baos.flush();

            imageInByte = baos.toByteArray();

            article.setThumbnail(imageInByte);
            article.setThumbnailType("image/png");

            baos.close();
        }
        return this.bbsMapper.insertArticle(article) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    //        Mr.m
//        게시글 읽기 구현 (게시글 aid값으로 불러오기) + 조회수 구현
    public ArticleReadVo readArticle(int index, UserEntity user) {
        ArticleReadVo existingArticleReadVo = this.bbsMapper.selectArticleByIndex(index, user == null ? null : user.getEmail());
        if (existingArticleReadVo != null) {
            existingArticleReadVo.setView(existingArticleReadVo.getView() + 1);
            this.bbsMapper.updateArticle(existingArticleReadVo);
        }
        return existingArticleReadVo;
    }

    // Mr.m
    // 게시글 읽기 (메뉴) 구성 (게시글 article.getBoarId()) 값으로 끌고오기 (소형 타이틀구현)
    public BoardEntity[] chartBoardId(String bid) {
        return this.bbsMapper.selectBoardByBoardId(bid);
    }

    //        Mr.m (2022 12 19 일 결합인인터셉터 구현 하는방법 공부하기)
//    게시글 title구현(인터셉터용)
    public BoardEntity[] getBoardEntities() {
        return this.shopMapper.selectBoards();
    }

    //    Mr.m
    //    게시글 좋아요 구현
    public Enum<? extends IResult> likedArticle(ArticleLikeEntity articleLikeEntity, UserEntity user) {
        ArticleReadVo existingArticleLiked = this.bbsMapper.selectArticleByIndex(articleLikeEntity.getArticleIndex());
        if (existingArticleLiked == null) {
            return CommonResult.FAILURE;
        } else if (this.bbsMapper.selectArticleLikeByIndex(articleLikeEntity.getArticleIndex(), user.getEmail()) != null) {
            return this.bbsMapper.deleteByArticleLiked(articleLikeEntity.getArticleIndex()) > 0
                    ? CommonResult.SUCCESS
                    : CommonResult.FAILURE;
        }
        articleLikeEntity.setUserEmail(user.getEmail());
        articleLikeEntity.setCreatedOn(new Date());
        return this.bbsMapper.insertArticleLike(articleLikeEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public ArticleEntity getThumbnail(int index) {
        return this.bbsMapper.selectThumbnailByIndex(index);
    }

    public Enum<? extends IResult> addComment(UserEntity user,
                                              CommentEntity comment,
                                              MultipartFile[] images) throws IOException {
        if (user == null) {
            return CommonResult.NOT_SIGNED;
        }

        comment.setUserEmail(user.getEmail());
        comment.setWrittenOn(new Date());
        if (this.bbsMapper.insertComment(comment) == 0) {
            return CommonResult.FAILURE;
        }
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                CommentImageEntity commentImage = new CommentImageEntity();
                commentImage.setCommentIndex(comment.getIndex());
                commentImage.setData(image.getBytes());
                commentImage.setType(image.getContentType());
                if (this.bbsMapper.insertCommentImage(commentImage) == 0) {
                    return CommonResult.FAILURE;
                }
            }
        }

        return CommonResult.SUCCESS;


    }

    //이미지추가 서비스
    public Enum<? extends IResult> addImage(ImageEntity image) {
        return this.bbsMapper.insertImage(image) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public ImageEntity getImage(int index) {
        return this.bbsMapper.selectImageByIndex(index);
    }

    //Mr.m
    //게시물 수정하기 (get)서비스
    public ArticleReadVo getModifyArticles(int articleIndex, UserEntity user) {
        return this.bbsMapper.selectArticleByIndex(articleIndex);
    }

    public Enum<? extends IResult> modifyArticle(int articleIndex, UserEntity user, ArticleEntity articleEntity) {
        ArticleEntity existingArticle = this.bbsMapper.selectArticleByIndex(articleIndex);
        if (existingArticle == null) {
            return CommonResult.FAILURE;
        }
        if (user == null || !user.getEmail().equals(existingArticle.getUserEmail())) {
            return ModifyArticleResult.NOT_ALLOWED;
        }
        System.out.println(articleEntity.getIndex());
        System.out.println(articleEntity.getTitle());
        existingArticle.setIndex(articleIndex);
        existingArticle.setTitle(articleEntity.getTitle());
        existingArticle.setContent(articleEntity.getContent());
        existingArticle.setModifiedOn(new Date());
        if (this.bbsMapper.updateArticle(existingArticle) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    //댓글 불러오기 and 이미지 index 가져오기

    public CommentVo[] getComment(int index,UserEntity user) {
        CommentVo[] comments = this.bbsMapper.selectCommentsByIndex(index, user == null ? null : user.getEmail());
        for(CommentVo comment : comments){
            CommentImageEntity[] commentImage = this.bbsMapper.selectCommentImagesByCommentIndexExceptData(comment.getIndex());
            int[] reviewImageIndexes = Arrays.stream(commentImage).mapToInt(CommentImageEntity::getIndex).toArray();

            comment.setImageIndexes(reviewImageIndexes);
        }

        return comments;
    }

    public CommentImageEntity getCommentImage(int index) {
        return this.bbsMapper.selectCommentImageByIndex(index);
    }


    @Transactional
    public Enum<? extends IResult> modifyComment(UserEntity user, CommentEntity comment,
                                                 MultipartFile[] images, Boolean modifyFlag) throws IOException {
        CommentVo existingComment = this.bbsMapper.selectCommentByIndex(comment.getIndex());
        //로그인 안했을 경우.
        if(user == null) return CommonResult.NOT_SIGNED;
        //로그인 사용자와 댓글 작성자가 다를 경우.
        if(!user.getEmail().equals(comment.getUserEmail()))
            return WriteResult.NOT_SAME;

        //존재하지 않는 댓글일 경우.
        if(this.bbsMapper.selectCommentsByIndex(comment.getIndex(), user.getEmail()) == null)
            return ReadResult.NO_SUCH_COMMENT;

        existingComment.setContent(comment.getContent());
        existingComment.setWrittenOn(new Date()); // 날짜를 현재 날짜로 변경.
        //update 시작.
        if(this.bbsMapper.updateComment(existingComment) < 0 )
            return CommonResult.FAILURE;

        if(modifyFlag){
            //변경되었다면 기존의 이미지는 전부 삭제.
            this.bbsMapper.deleteCommentImage(existingComment.getIndex());

            if (images != null && images.length > 0) {
                for (MultipartFile image : images) {
                    CommentImageEntity commentImage = new CommentImageEntity();
                    commentImage.setCommentIndex(existingComment.getIndex());
                    commentImage.setData(image.getBytes());
                    commentImage.setType(image.getContentType());
                    if (this.bbsMapper.insertCommentImage(commentImage) == 0) {
                        return CommonResult.FAILURE;
                    }
                }
            }

        }

        return CommonResult.SUCCESS;
    }


    public Enum<? extends IResult> likedComment(CommentLikeEntity commentLikeEntity, UserEntity user) {
        CommentVo existingComment = this.bbsMapper.selectCommentByIndex(commentLikeEntity.getCommentIndex());
        if (existingComment == null) {
            return CommonResult.FAILURE;
        }
        if (this.bbsMapper.selectCommentLikeByIndex(commentLikeEntity.getCommentIndex(), user.getEmail()) != null) {
            return this.bbsMapper.deleteByCommentLiked(commentLikeEntity.getCommentIndex()) > 0
                    ? CommonResult.SUCCESS
                    : CommonResult.FAILURE;
        }
        commentLikeEntity.setUserEmail(user.getEmail());
        commentLikeEntity.setCreatedOn(new Date());
        return this.bbsMapper.insertCommentLike(commentLikeEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
    public Enum<? extends IResult> deleteComment(UserEntity user, CommentEntity comment) {
        if(user == null) return CommonResult.NOT_SIGNED;
        if(!user.getEmail().equals(comment.getUserEmail())) return WriteResult.NOT_SAME;
        return this.bbsMapper.deleteComment(comment.getIndex()) > 0 ?
                CommonResult.SUCCESS : CommonResult.FAILURE;
    }


    //Mr.m
    //게시글 삭제구문
    public Enum<? extends IResult> deleteArticle(ArticleEntity article, UserEntity user) {
        ArticleEntity existingArticle = this.bbsMapper.selectArticleByIndex(article.getIndex());
        if (existingArticle == null) {
            return ReadResult.NO_SUCH_ARTICLE;
        }
        if (user == null || !user.getEmail().equals(existingArticle.getUserEmail())) {
            return ReadResult.NOT_ALLOWED;
        }
        article.setBoardId(existingArticle.getBoardId());

        return this.bbsMapper.deleteArticle(article.getIndex()) > 0 ? CommonResult.SUCCESS : CommonResult.FAILURE;
    }

    public ArticleReadVo[] getArticles(BoardEntity board,PagingModel paging)  {
        return this.bbsMapper.selectArticlesByBoardId(
                board.getId(),paging.countPerPage,
                (paging.requestPage - 1) * paging.countPerPage);
    }

    public ArticleReadVo[] getHotArticle(BoardEntity board){
        return this.bbsMapper.selectHotArticlesByBoardId(board.getId());
    }
    public ArticleReadVo getNoticeArticle(BoardEntity board){
        return this.bbsMapper.selectNoticeArticleByBoardId(board.getId());
    }
    public int getArticleCount(BoardEntity board, String criterion, String keyword) {
        return this.bbsMapper.selectArticleCountByBoardId(board.getId(), criterion, keyword);
    }
}

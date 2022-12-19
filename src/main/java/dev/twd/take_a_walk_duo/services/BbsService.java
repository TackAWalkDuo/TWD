package dev.test.take_a_walk_duo.services;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ArticleLikeEntity;
import dev.twd.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.twd.take_a_walk_duo.entities.bbs.*;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.enums.bbs.ModifyArticleResult;
import dev.twd.take_a_walk_duo.enums.bbs.WriteResult;
import dev.twd.take_a_walk_duo.interfaces.IResult;
import dev.twd.take_a_walk_duo.mappers.IBbsMapper;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import dev.twd.take_a_walk_duo.vos.bbs.CommentVo;
import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ArticleLikeEntity;
import dev.twd.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.twd.take_a_walk_duo.entities.bbs.*;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.enums.bbs.ModifyArticleResult;
import dev.twd.take_a_walk_duo.enums.bbs.WriteResult;
import dev.twd.take_a_walk_duo.interfaces.IResult;
import dev.twd.take_a_walk_duo.mappers.IBbsMapper;
import dev.twd.take_a_walk_duo.mappers.IShopMapper;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

@Service(value = "dev.test.take_a_walk_duo.services.BbsService")
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
        BoardEntity boardEntity = this.bbsMapper.selectBoardById(id);
        System.out.println(boardEntity.getId());
        return boardEntity;
    }


    //1.write boardId 값 끌고오기
    //mr.s
    public Enum<? extends IResult> RegisterArticle(ArticleEntity article, MultipartFile[] images) throws IOException {
        BoardEntity board = this.bbsMapper.selectBoardById(article.getBoardId());
        if (board == null) {
            return WriteResult.NO_SUCH_BOARD;
        }

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
//            for (MultipartFile image : images) {
//                existingArticleReadVo.setThumbnail(image.getBytes());
//                existingArticleReadVo.setThumbnailType(image.getContentType());
//            }
            existingArticleReadVo.setView(existingArticleReadVo.getView() + 1);
            this.bbsMapper.updateArticle(existingArticleReadVo);
        } else {
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

    public CommentVo[] getComment(int index) {
        CommentVo[] comments = this.bbsMapper.selectCommentByIndex(index);
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


}

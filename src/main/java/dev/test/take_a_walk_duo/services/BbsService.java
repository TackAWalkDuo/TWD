package dev.test.take_a_walk_duo.services;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
import dev.test.take_a_walk_duo.enums.bbs.WriteResult;
import dev.test.take_a_walk_duo.interfaces.IResult;
import dev.test.take_a_walk_duo.mappers.IBbsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service(value = "dev.test.take_a_walk_duo.services.BbsService")
public class BbsService {

    private final IBbsMapper bbsMapper;

    @Autowired
    public BbsService(IBbsMapper bbsMapper) {
        this.bbsMapper = bbsMapper;
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
            System.out.println("existingArticleReadVo check1");
        }else {
            System.out.println("existingArticleReadVo check2");
        }
        return existingArticleReadVo;
    }

    //    Mr.m
    //    게시글 좋아요 구현
//    public Enum<? extends IResult> likedArticle(ArticleLikeEntity articleLikeEntity, UserEntity user) {
//        ArticleReadVo existingArticleLiked = this.bbsMapper.selectArticleByIndex(articleLikeEntity.getArticleIndex());
//        if(existingArticleLiked == null)
//            return CommonResult.FAILURE;
//        articleLikeEntity.setUserEmail(user.getEmail());
//        articleLikeEntity.setCreatedOn(new Date());
//        return this.bbsMapper.insertArticleLike(articleLikeEntity) > 0
//                ? CommonResult.SUCCESS
//                : CommonResult.FAILURE;
//    }

    public ArticleEntity getThumbnail(int index){
        return this.bbsMapper.selectThumbnailByIndex(index);
    }
}

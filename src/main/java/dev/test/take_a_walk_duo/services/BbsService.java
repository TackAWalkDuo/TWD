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

    public ArticleEntity getThumbnail(int index){
        return this.bbsMapper.selectThumbnailByIndex(index);
    }
}

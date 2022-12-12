package dev.test.take_a_walk_duo.services;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.map.LocationEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
import dev.test.take_a_walk_duo.interfaces.IResult;
import dev.test.take_a_walk_duo.mappers.IMapMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Service(value = "dev.test.take_a_walk_duo.services.MapService")
public class MapService {
    private final IMapMapper mapMapper;

    @Autowired
    public MapService(IMapMapper mapMapper) {
        this.mapMapper = mapMapper;
    }

    //TODO user 업데이트 후 제약조건 및 데이터 set 변경.
    public Enum<? extends IResult> addWalkArticle(ArticleEntity article,
                                                  LocationEntity location,
                                                  MultipartFile[] images) throws IOException {
        // article 내용 DB에 저장
        article.setUserEmail("been1@twd.com");      // TODO user.setEmail 로 변경.
        article.setBoardId("walk");                 // TODO bord 확정 후 변경.

        if (images != null) {
            for (MultipartFile image : images) {
                article.setThumbnail(image.getBytes());
                article.setThumbnailType(image.getContentType());
            }
        } else {
            byte[] imageInByte;
            File defaultImage = new File("src/main/resources/static/resources/images/TAWD_logo.png");
            defaultImage.setReadable(true, false);

            System.out.println("file 권한  : " + defaultImage.canRead() );
            System.out.println("file exit  : " + defaultImage.exists() );

            BufferedImage originalImage = ImageIO.read(defaultImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "png", baos);
            baos.flush();

            imageInByte = baos.toByteArray();

            article.setThumbnail(imageInByte);
            article.setThumbnailType("image/png");

            baos.close();
        }

        if (this.mapMapper.insertWalkArticle(article) == 0) return CommonResult.FAILURE;

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // article 에 저장 한 뒤 해당 index 로 Location 에 남은 내용 저장.
        location.setArticleIndex(article.getIndex());


        if (this.mapMapper.insertLocation(location) == 0) return CommonResult.FAILURE;

        return CommonResult.SUCCESS;
    }


    public ArticleEntity getArticle(int index){
        return this.mapMapper.selectArticleByIndex(index);
    }
}

package dev.twd.take_a_walk_duo.services;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.map.LocationEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.interfaces.IResult;
import dev.twd.take_a_walk_duo.mappers.IBbsMapper;
import dev.twd.take_a_walk_duo.mappers.IMapMapper;
import dev.twd.take_a_walk_duo.vos.map.PlaceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service(value = "dev.test.take_a_walk_duo.services.MapService")
public class MapService {
    private final IMapMapper mapMapper;
    private final IBbsMapper bbsMapper;

    @Autowired
    public MapService(IMapMapper mapMapper, IBbsMapper bbsMapper) {
        this.mapMapper = mapMapper;
        this.bbsMapper = bbsMapper;
    }

    //TODO user 업데이트 후 제약조건 및 데이터 set 변경.
    @Transactional
    public Enum<? extends IResult> addWalkArticle(ArticleEntity article,
                                                  LocationEntity location,
                                                  MultipartFile[] images,
                                                  UserEntity user) throws IOException {
        if(user == null) {
            return CommonResult.NOT_SIGNED;
        }

        // article 내용 DB에 저장
        article.setUserEmail(user.getEmail());
        article.setBoardId("walk");

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

        if (this.mapMapper.insertWalkArticle(article) == 0) return CommonResult.FAILURE;

        // article 에 저장 한 뒤 해당 index 로 Location 에 남은 내용 저장.
        location.setArticleIndex(article.getIndex());


        if (this.mapMapper.insertLocation(location) == 0) return CommonResult.FAILURE;

        return CommonResult.SUCCESS;
    }


    public PlaceVo[] getPlaces(double minLat, double minLng, double maxLat, double maxLng,
                               UserEntity user) {
        return this.mapMapper.selectPlacesExceptImage(minLat, minLng, maxLat, maxLng, user==null ? null: user.getEmail());
    }

    public ArticleEntity updateView(int index) {
        ArticleEntity article = this.bbsMapper.selectArticleByIndex(index, null);
        if (article == null) return null;

        article.setView(article.getView() + 1);
        return this.bbsMapper.updateArticle(article) > 0 ?
                article : null;
    }

}

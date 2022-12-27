package dev.twd.take_a_walk_duo.services;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.map.LocationEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.enums.bbs.ModifyArticleResult;
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
import java.util.Date;

@Service(value = "dev.test.take_a_walk_duo.services.MapService")
public class MapService {
    private final IMapMapper mapMapper;
    private final IBbsMapper bbsMapper;

    @Autowired
    public MapService(IMapMapper mapMapper, IBbsMapper bbsMapper) {
        this.mapMapper = mapMapper;
        this.bbsMapper = bbsMapper;
    }

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


    public PlaceVo getPlace(int index, UserEntity user) {
        if(user == null) return null;
        return this.mapMapper.selectPlace(index);
    }

    @Transactional
    public Enum<? extends IResult> modifyWalkArticle(ArticleEntity article, LocationEntity location,
                                                     UserEntity user, MultipartFile[] images,
                                                     Boolean modifyFlag) throws IOException {
        if(user == null) return ModifyArticleResult.NOT_SIGNED;       // 로그인 하지 않았을 경우

        if(!user.getEmail().equals(article.getUserEmail()))
            return ModifyArticleResult.NOT_ALLOWED;         // 작성자와 로그인 사용자가 다를 경우.

        PlaceVo place = this.mapMapper.selectPlace(article.getIndex());
        if(place == null)  //게시글이 없을 경우
            return ModifyArticleResult.NO_SUCH_ARTICLE;

        // article 에 부족한 값 setting
        article.setBoardId("walk");
        article.setView(place.getView());
        article.setWrittenOn(place.getWrittenOn());
        article.setModifiedOn(new Date());

        LocationEntity existingLocation = this.mapMapper.selectLocationByArticleIndex(article.getIndex());
        // 위에 place 로 확인했지만 Location index 가 필요하여 검색하였으니 한번더 확인.
        if(existingLocation == null) return ModifyArticleResult.NO_SUCH_ARTICLE;

        // location 에 부족한 값 setting
        location.setArticleIndex(article.getIndex());
        location.setIndex(existingLocation.getIndex());

        //이미지 수정이 있었다면.
        if(modifyFlag) {
            if (images != null) {
                for (MultipartFile image : images) {
                    article.setThumbnail(image.getBytes());
                    article.setThumbnailType(image.getContentType());
                }
            } else {
                byte[] imageInByte;
                File defaultImage = new File("src/main/resources/static/resources/images/mococo.jpg");
                BufferedImage originalImage = ImageIO.read(defaultImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(originalImage, "jpeg", baos);
                baos.flush();

                imageInByte = baos.toByteArray();

                article.setThumbnail(imageInByte);
                article.setThumbnailType("image/jpeg");
                baos.close();
            }
        }else { // 이미지 수정이 없었다면.
            article.setThumbnail(place.getThumbnail());
            article.setThumbnailType(place.getThumbnailType());
        }

        // 먼저 article update
        if(this.bbsMapper.updateArticle(article) < 0) return CommonResult.FAILURE;

        // location update
        if(this.mapMapper.updateLocation(location) < 0 ) return CommonResult.FAILURE;


        return CommonResult.SUCCESS;
    }

}

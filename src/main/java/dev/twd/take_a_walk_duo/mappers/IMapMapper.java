package dev.twd.take_a_walk_duo.mappers;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.map.LocationEntity;
import dev.twd.take_a_walk_duo.vos.map.PlaceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IMapMapper {

    int insertWalkArticle(ArticleEntity article);

    int insertLocation(LocationEntity location);

    PlaceVo[] selectPlacesExceptImage(@Param(value = "minLat") double minLat,
                                      @Param(value = "minLng") double minLng,
                                      @Param(value = "maxLat") double maxLat,
                                      @Param(value = "maxLng") double maxLng,
                                      @Param(value = "email") String email);


    int updateArticleView(ArticleEntity article);


}

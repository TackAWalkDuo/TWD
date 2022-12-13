package dev.test.take_a_walk_duo.mappers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.map.LocationEntity;
import dev.test.take_a_walk_duo.vos.PlaceVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IMapMapper {

    int insertWalkArticle(ArticleEntity article);

    int insertLocation(LocationEntity location);

    PlaceVo[] selectPlaces(@Param(value = "minLat") double minLat,
                           @Param(value = "minLng") double minLng,
                           @Param(value = "maxLat") double maxLat,
                           @Param(value = "maxLng") double maxLng);
}

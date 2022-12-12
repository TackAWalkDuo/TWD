package dev.test.take_a_walk_duo.mappers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.map.LocationEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IMapMapper {

    int insertWalkArticle(ArticleEntity article);

    int insertLocation(LocationEntity location);

    ArticleEntity selectArticleByIndex(@Param(value = "index") int index);
}

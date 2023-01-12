package dev.twd.take_a_walk_duo.mappers;

import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IHomeMapper {

    ArticleReadVo[] selectArticlesByBoardId(@Param(value = "boardId") String boardId);
}

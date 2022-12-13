package dev.test.take_a_walk_duo.mappers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.sale.SaleProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IShopMapper {
    int selectArticleCountByBoardId(@Param(value = "boardId") String boardId,
                                    @Param(value = "keyword") String keyword,
                                    @Param(value = "criterion")String criterion);

    int insertProduct(SaleProductEntity product);

    int insertShopArticle(ArticleEntity article);
}

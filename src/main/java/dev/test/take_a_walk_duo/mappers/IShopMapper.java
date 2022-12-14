package dev.test.take_a_walk_duo.mappers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.test.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.test.take_a_walk_duo.entities.shop.SaleProductEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IShopMapper {
    int selectArticleCountByBoardId(@Param(value = "boardId") String boardId,
                                    @Param(value = "keyword") String keyword,
                                    @Param(value = "criterion")String criterion);

    BoardEntity selectBoardById(@Param(value = "id") String id);

    // SaleProductEntity 등록용
    int insertProduct(SaleProductEntity product);

    // ArticleEntity 등록용
    int insertShopArticle(ArticleEntity article);

    // 이미지 호출
    ImageEntity selectImageByIndex(@Param(value = "index")int index);

    // 이미지 등록
    int insertImage(ImageEntity image);
}

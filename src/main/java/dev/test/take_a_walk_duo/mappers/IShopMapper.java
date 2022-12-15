package dev.test.take_a_walk_duo.mappers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.test.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.test.take_a_walk_duo.entities.shop.SaleProductEntity;
import dev.test.take_a_walk_duo.vos.bbs.ArticleReadVo;
import dev.test.take_a_walk_duo.vos.shop.ProductVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface IShopMapper {
    int selectArticleCountByBoardId(@Param(value = "boardId") String boardId,
                                    @Param(value = "keyword") String keyword,
                                    @Param(value = "criterion")String criterion);

    ProductVo[] selectArticlesByBoardId(@Param(value = "boardText") String boardText,
                                        @Param(value = "limit") int limit,
                                        @Param(value = "offset") int offset,
                                        @Param(value = "criterion") String criterion,
                                        @Param(value = "keyword") String keyword);

    BoardEntity selectBoardById(@Param(value = "id") String id);

    BoardEntity[] selectBoards();

    // SaleProductEntity 등록용
    int insertProduct(SaleProductEntity product);

    // ArticleEntity 등록용
    int insertShopArticle(ArticleEntity article);

    // 이미지 호출
    ImageEntity selectImageByIndex(@Param(value = "index")int index);

    // 이미지 등록
    int insertImage(ImageEntity image);
}

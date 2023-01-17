package dev.twd.take_a_walk_duo.mappers;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.twd.take_a_walk_duo.entities.shop.PaymentEntity;
import dev.twd.take_a_walk_duo.entities.shop.SaleProductEntity;
import dev.twd.take_a_walk_duo.entities.shop.ShoppingCartEntity;
import dev.twd.take_a_walk_duo.vos.shop.CartVo;
import dev.twd.take_a_walk_duo.vos.shop.PaymentVo;
import dev.twd.take_a_walk_duo.vos.shop.ProductVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface IShopMapper {
    ArticleEntity selectArticleByIndex(@Param(value = "aid") int aid);

    SaleProductEntity selectProductByArticleIndex(@Param(value = "index") int index);

    ShoppingCartEntity selectCartByIndex(@Param(value = "index") int index, @Param(value = "userEmail") String userEmail);

    ShoppingCartEntity selectCartByCartIndex(@Param(value = "index") int index);

    int selectArticleCountByBoardId(@Param(value = "boardId") String boardId,
                                    @Param(value = "criterion") String criterion,
                                    @Param(value = "keyword") String keyword);

    PaymentEntity selectPaymentByRegistration(@Param(value = "date")Date date);

    ProductVo[] selectArticlesByBoardId(@Param(value = "boardId") String boardId,
                                        @Param(value = "limit") int limit,
                                        @Param(value = "offset") int offset,
                                        @Param(value = "criterion") String criterion,
                                        @Param(value = "keyword") String keyword);

    // detail page
    ProductVo selectArticleByArticleIndex(@Param(value = "aid") int aid);

    ProductVo[] selectAllArticles();
    ProductVo[] selectDiscountProducts();

    ProductVo[] selectConditionArticles( @Param(value = "categoryText") String categoryText);

    BoardEntity selectBoardById(@Param(value = "bid") String id);

    BoardEntity[] selectBoards();

    // get write
    ProductVo selectArticle();

    PaymentEntity[] selectPaymentByGroupIndex(@Param(value = "index")int index);

    PaymentEntity selectPaymentByIndex(@Param(value = "index")int index);

    CartVo[] selectCartsByUserEmail(@Param(value = "userEmail")String userEmail);

    PaymentVo[] selectPaymentsByUserEmail(@Param(value = "userEmail")String userEmail);

    ShoppingCartEntity selectArticleByArticleIndexUserEmail(@Param(value = "aid") int aid, @Param(value = "userEmail") String userEmail);

    // 상품 수정
    int updateProduct(SaleProductEntity product);

    int updateArticle(ArticleEntity article);

    int updateCart(ShoppingCartEntity cart);

    // SaleProductEntity 등록용
    int insertProduct(SaleProductEntity product);

    // ArticleEntity 등록용
    int insertShopArticle(ArticleEntity article);

    // 이미지 호출
    ImageEntity selectImageByIndex(@Param(value = "index") int index);

    // 이미지 등록
    int insertImage(ImageEntity image);

    int insertCart(ShoppingCartEntity cart);

    int deleteCartByIndex(ShoppingCartEntity cart);

    int insertPayment(PaymentEntity payment);

    int deletePayment(@Param(value = "groupIndex")int groupIndex);
}

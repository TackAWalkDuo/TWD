package dev.test.take_a_walk_duo.services;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.test.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.test.take_a_walk_duo.entities.shop.SaleProductEntity;
import dev.test.take_a_walk_duo.entities.member.UserEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
import dev.test.take_a_walk_duo.interfaces.IResult;
import dev.test.take_a_walk_duo.mappers.IMemberMapper;
import dev.test.take_a_walk_duo.mappers.IShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service(value = "dev.test.take_a_walk_duo.services.ShopService")
public class ShopService {
    private final IShopMapper shopMapper;
    private final IMemberMapper memberMapper;

    // memberMapper 서비스도 의존성 주입함.
    @Autowired
    public ShopService(IShopMapper shopMapper, IMemberMapper memberMapper) {
        this.shopMapper = shopMapper;
        this.memberMapper = memberMapper;
    }

//    public int getArticleCount(BoardEntity board, String criterion, String keyword) {
//        return this.shopMapper.selectArticleCountByBoardId(board.getId(), criterion, keyword);
//    }

    // list
    public BoardEntity getBoard(String id)
    // id 는 게시판의 id임 notice 등
    {
        return this.shopMapper.selectBoardById(id);
    }

    // 상품 등록
    public Enum<? extends IResult> write(ArticleEntity article,
                                         SaleProductEntity product,
                                         @RequestParam(value = "images", required = false) MultipartFile[] images,
                                         @SessionAttribute(value = "user", required = false) UserEntity user) throws IOException {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail()); // 로그인한 userEmail과 DB에 있는 userEmail 대조(memberMapper의 쿼리 사용)
        if (!existingUser.getAdmin()) { // 관리자 계정이 아니라면 등록 실패
            return CommonResult.FAILURE;
        }
        article.setUserEmail(user.getEmail());
        article.setBoardId("shop"); // BoardId shop 하나라서 고정.

        for (MultipartFile image : images) {
            article.setThumbnail(image.getBytes());
            article.setThumbnailType(image.getContentType());
        }

        if (this.shopMapper.insertShopArticle(article) == 0) {
            return CommonResult.FAILURE;
        }

        product.setArticleIndex(article.getIndex());
        product.setCost(0);
        product.setProfit(0);
        product.setDiscount(0);

        if (this.shopMapper.insertProduct(product) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }


    public Enum<? extends IResult> addImage(ImageEntity image) {
        return this.shopMapper.insertImage(image) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public ImageEntity getImage(int index){
        return this.shopMapper.selectImageByIndex(index);
    }

}


package dev.test.take_a_walk_duo.services;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.sale.SaleProductEntity;
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


    @Autowired
    public ShopService(IShopMapper shopMapper, IMemberMapper memberMapper) {
        this.shopMapper = shopMapper;
        this.memberMapper = memberMapper;
    }

//    public int getArticleCount(BoardEntity board, String criterion, String keyword) {
//        return this.shopMapper.selectArticleCountByBoardId(board.getId(), criterion, keyword);
//    }

    public Enum<? extends IResult> write(ArticleEntity article,
                                         SaleProductEntity product,
                                         @RequestParam(value = "images", required = false) MultipartFile[] images,
                                         @SessionAttribute(value = "user", required = false) UserEntity user) throws IOException {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());
        if (!existingUser.getAdmin()) {
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

}


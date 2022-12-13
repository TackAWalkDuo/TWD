package dev.test.take_a_walk_duo.services;

import dev.test.take_a_walk_duo.entities.bbs.sale.SaleProductEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
import dev.test.take_a_walk_duo.interfaces.IResult;
import dev.test.take_a_walk_duo.mappers.IShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dev.test.take_a_walk_duo.services.ShopService")
public class ShopService {
    private final IShopMapper shopMapper;
    @Autowired
    public ShopService(IShopMapper shopMapper){this.shopMapper = shopMapper;}

//    public int getArticleCount(BoardEntity board, String criterion, String keyword) {
//        return this.shopMapper.selectArticleCountByBoardId(board.getId(), criterion, keyword);
//    }

    public Enum<? extends IResult> write(SaleProductEntity product) {

        return this.shopMapper.insertProduct(product) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}

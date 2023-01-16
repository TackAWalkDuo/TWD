package dev.twd.take_a_walk_duo.services;

import dev.twd.take_a_walk_duo.mappers.IHomeMapper;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import dev.twd.take_a_walk_duo.vos.shop.ProductVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "dev.twd.take_a_walk_duo.services.HomeService")
public class HomeService {
    private final IHomeMapper homeMapper;

    @Autowired
    public HomeService(IHomeMapper homeMapper) {
        this.homeMapper = homeMapper;
    }

    public ArticleReadVo[] getRecentArticles(String bid) {
        return this.homeMapper.selectArticlesByBoardId(bid);
    }
    public ProductVo[] getArticles(String bid){
        return this.homeMapper.selectShopArticlesByBoardId(bid);
    }
}

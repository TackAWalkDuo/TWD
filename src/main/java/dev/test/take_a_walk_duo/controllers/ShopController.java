package dev.test.take_a_walk_duo.controllers;

import dev.test.take_a_walk_duo.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "dev.test.take_a_walk_duo.controllers.ShopController")
@RequestMapping(value = "/shop")
public class ShopController {
    private final ShopService shopService;

    @Autowired
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @RequestMapping(value = "/main",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getShop() {
        ModelAndView modelAndView = new ModelAndView("shop/main_backup");
        return modelAndView;
    }

    @RequestMapping(value = "/list",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
        page = Math.max(1, page);
        ModelAndView modelAndView = new ModelAndView("shop/list_backup");
//        int totalCount = this.shopService.getboard, criterion, keyword);
//        PagingModel paging = new PagingModel(totalCount, page);
//        modelAndView.addObject("paging", paging);
        return modelAndView;
    }

    //    @RequestParam(value = "aid", required = false) int aid
    @GetMapping(value = "detail", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getDetail(
    ) {
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("shop/detail_backup");
//        ArticleReadVO article = this.bbsService.readArticle(aid);
//        modelAndView.addObject("article", article);
//        if (article != null) {
//            BoardEntity board = this.bbsService.getBoard(article.getBoardId());
//            modelAndView.addObject("board", board);
//        }
        return modelAndView;
    }

    @RequestMapping(value = "write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite() {
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("shop/write");
        return modelAndView;
    }

}

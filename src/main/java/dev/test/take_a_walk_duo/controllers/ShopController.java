package dev.test.take_a_walk_duo.controllers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.sale.SaleProductEntity;
import dev.test.take_a_walk_duo.entities.member.UserEntity;
import dev.test.take_a_walk_duo.services.MemberService;
import dev.test.take_a_walk_duo.services.ShopService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller(value = "dev.test.take_a_walk_duo.controllers.ShopController")
@RequestMapping(value = "/shop")
public class ShopController {
    private final ShopService shopService;
    private final MemberService memberService;


    @Autowired
    public ShopController(ShopService shopService, MemberService memberService) {
        this.shopService = shopService;
        this.memberService = memberService;
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
        modelAndView = new ModelAndView("shop/write_backup");
        return modelAndView;
    }

    @PostMapping(value = "write",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(ArticleEntity article,
                            SaleProductEntity product,
                            @RequestParam(value = "images", required = false) MultipartFile[] images,
                            @SessionAttribute(value = "user", required = false) UserEntity user)throws IOException{
        Enum<?> result;
        JSONObject responseObject = new JSONObject();
        result = this.shopService.write(article, product, images, user);
        responseObject.put("result",result.name().toLowerCase());

        return responseObject.toString();
    }

}

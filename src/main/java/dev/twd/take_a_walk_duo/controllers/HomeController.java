package dev.twd.take_a_walk_duo.controllers;

import dev.twd.take_a_walk_duo.services.HomeService;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "dev.twd.pet_walk.controllers.HomeController")
@RequestMapping(value = "/")
public class HomeController extends GeneralController {

    private final HomeService homeService;

    @Autowired
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHome() {
        ModelAndView modelAndView = new ModelAndView("home/index");
        ArticleReadVo[] recentArticle = this.homeService.getRecentArticles("free");
        modelAndView.addObject("articles", recentArticle);
        return modelAndView;
    }
}

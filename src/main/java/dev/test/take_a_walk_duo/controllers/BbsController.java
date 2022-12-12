package dev.test.take_a_walk_duo.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "dev.test.take_a_walk_duo.controllers.bbsController")
@RequestMapping(value = "/bbs")
public class BbsController {

    //Mr.s
    @RequestMapping(value = "list",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList() {
        ModelAndView modelAndView = new ModelAndView("bbs/list");
        return modelAndView;
    }

    //Mr.s
    @RequestMapping(value = "write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite() {
        ModelAndView modelAndView = new ModelAndView("bbs/write");
        return modelAndView;
    }

    //Mr.s
    @RequestMapping(value = "read",
    method = RequestMethod.GET,
    produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRead(){
        ModelAndView modelAndView = new ModelAndView("bbs/read");
        return modelAndView;
    }





}

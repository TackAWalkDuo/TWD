package dev.twd.take_a_walk_duo.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "dev.twd.pet_walk.controllers.HomeController")
@RequestMapping(value = "/")
public class HomeController extends GeneralController {

    @RequestMapping(value = "/", method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getHome(){
        ModelAndView modelAndView = new ModelAndView("home/index");
        return modelAndView;
    }
}

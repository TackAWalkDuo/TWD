package dev.twd.take_a_walk_duo.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller(value = "dev.twd.take_a_walk_duo.controllers.ErrorController")
@RequestMapping(value = "error")
public class ErrorController {

    @RequestMapping(value = "error",
    method = RequestMethod.GET,
    produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getError() {
        ModelAndView modelAndView = new ModelAndView("error/error");
        return modelAndView;
    }
}

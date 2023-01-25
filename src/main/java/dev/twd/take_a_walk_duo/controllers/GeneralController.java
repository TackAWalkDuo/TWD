package dev.twd.take_a_walk_duo.controllers;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

public class GeneralController {

   @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception ex) {

        return new ModelAndView("error");
    }
}

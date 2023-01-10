package dev.twd.take_a_walk_duo.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

public class GeneralController {

   @ExceptionHandler(value = Exception.class)
    public ModelAndView handleException(Exception ex) {
       System.out.println("=".repeat(50));
       System.out.println(ex.getMessage());
       System.out.println("=".repeat(50));
       
        return new ModelAndView("error");
    }
}

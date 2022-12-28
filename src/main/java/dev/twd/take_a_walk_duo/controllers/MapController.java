package dev.twd.take_a_walk_duo.controllers;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.map.LocationEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.services.MapService;
import dev.twd.take_a_walk_duo.vos.map.PlaceVo;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller(value = "dev.twd.take_a_walk_duo.controllers.MapController")
@RequestMapping(value = "map")
public class MapController {
    private final MapService mapService;

    @Autowired
    public MapController(MapService bbsService) {
        this.mapService = bbsService;
    }

    @GetMapping(value = "write", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWalkWrite() {
        return new ModelAndView("map/walkWrite");
    }

    @PostMapping(value = "write", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWalkWrite(ArticleEntity article,
                                LocationEntity location,
                                @RequestParam(value = "images", required = false, defaultValue = "false") MultipartFile[] images,
                                @SessionAttribute(value = "user", required = false) UserEntity user)
            throws IOException {
        Enum<?> result = this.mapService.addWalkArticle(article, location, images, user);

        System.out.println("check mpa writesdfsdfsdfsdfsdf");
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }

    @GetMapping(value = "read", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWalkRead() {
        return new ModelAndView("map/walkRead");
    }

    @GetMapping(value = "place", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public PlaceVo[] getPlace(@RequestParam(value = "minLat") double minLat,
                              @RequestParam(value = "minLng") double minLng,
                              @RequestParam(value = "maxLat") double maxLat,
                              @RequestParam(value = "maxLng") double maxLng,
                              @SessionAttribute(value = "user", required = false) UserEntity user) {
        return this.mapService.getPlaces(minLat, minLng, maxLat, maxLng, user);
    }

    @PostMapping(value = "view", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postView(@RequestParam(value = "index") int index) {
        JSONObject responseObject = new JSONObject();

        ArticleEntity article = this.mapService.updateView(index);
        if (article == null)
            responseObject.put("result", CommonResult.FAILURE.name().toLowerCase());
        else {
            responseObject.put("result", CommonResult.SUCCESS.name().toLowerCase());
            responseObject.put("view", article.getView());
        }
        return responseObject.toString();
    }

    @GetMapping(value = "modify", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify(@RequestParam(value = "index") int index,
                                  @SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView("map/walkModify");

        System.out.println("modify index check = " + index);
        modelAndView.addObject("place", this.mapService.getPlace(index, user));

        return modelAndView;
    }

    @PostMapping(value = "modify", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postModify(ArticleEntity article, LocationEntity location,
                             @SessionAttribute(value = "user", required = false) UserEntity user,
                             @RequestParam(value = "images", required = false) MultipartFile[] images,
                             Boolean modifyFlag) throws IOException {

        Enum<?> result = this.mapService.modifyWalkArticle(article, location, user, images, modifyFlag);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }

}

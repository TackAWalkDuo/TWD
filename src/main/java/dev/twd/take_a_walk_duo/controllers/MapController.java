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

@Controller(value = "dev.test.take_a_walk_duo.controllers.MapController")
@RequestMapping(value = "map")
public class MapController {
    private final MapService mapService;

    @Autowired
    public MapController(MapService bbsService) {
        this.mapService = bbsService;
    }

    @GetMapping(value = "walk-write", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWalkWrite() {
        return new ModelAndView("map/walkWrite");
    }


    //TODO user 업데이트 이후에 session 추가
    @PostMapping(value = "walk-write", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWalkWrite(ArticleEntity article,
                                LocationEntity location,
                                @RequestParam(value = "images", required = false) MultipartFile[] images,
                                @SessionAttribute(value = "user", required = false) UserEntity user)
            throws IOException {

        System.out.println(article.getTitle());
        System.out.println(article.getContent());
        System.out.println(location.getLatitude());
        System.out.println(location.getLongitude());

        Enum<?> result = this.mapService.addWalkArticle(article, location, images, user);

        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }

    @GetMapping(value = "walk-read", produces = MediaType.TEXT_HTML_VALUE)
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
        System.out.println("login check"+user);
        PlaceVo[] temp = this.mapService.getPlaces(minLat, minLng, maxLat, maxLng, user);
        for (int i = 0; i < temp.length; i++) {
            System.out.println("isSigned " + temp[i].isSigned());
        }
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


}

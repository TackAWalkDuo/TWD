package dev.test.take_a_walk_duo.controllers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.map.LocationEntity;
import dev.test.take_a_walk_duo.models.PagingModel;
import dev.test.take_a_walk_duo.services.MapService;
import dev.test.take_a_walk_duo.vos.PlaceVo;
import org.json.JSONArray;
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
                                @RequestParam(value = "images", required = false) MultipartFile[] images)
            throws IOException {

        System.out.println(article.getTitle());
        System.out.println(article.getContent());
        System.out.println(location.getLatitude());
        System.out.println(location.getLongitude());

        Enum<?> result = this.mapService.addWalkArticle(article, location, images);

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
                           @RequestParam(value = "maxLng") double maxLng) {
        return this.mapService.getPlaces(minLat, minLng, maxLat, maxLng);
    }

}

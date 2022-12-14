package dev.test.take_a_walk_duo.controllers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.test.take_a_walk_duo.entities.shop.SaleProductEntity;
import dev.test.take_a_walk_duo.entities.member.UserEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
import dev.test.take_a_walk_duo.services.MemberService;
import dev.test.take_a_walk_duo.services.ShopService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    // 쇼핑 메인 페이지 호출
    @RequestMapping(value = "/main",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getShop() {
        ModelAndView modelAndView = new ModelAndView("shop/main_backup");
        return modelAndView;
    }

    // 쇼핑 리스트 페이지 호출
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
    // 쇼핑 상품 상세보기 페이지 호출
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

    // 상품 등록 페이지 호출
    @RequestMapping(value = "write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite() {
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("shop/write_backup");
        return modelAndView;
    }

    // 상품 등록
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

    // 이미지 다운로드(화면에 보이게하는 매핑)
    @GetMapping(value = "image")
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "index") int index) {
        ResponseEntity<byte[]> responseEntity;
        ImageEntity image = this.shopService.getImage(index);
        if (image == null) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(image.getFileMime()));
            headers.setContentLength(image.getData().length);
            responseEntity = new ResponseEntity<>(image.getData(), HttpStatus.OK);
        }
        return responseEntity;
    }

    // 이미지 업로드(ckEditor에 등록하는 매핑)
    @PostMapping(value = "image",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    //upload는 ckeiditor에서 정해놓으거라서 따라해야함
    // upload용 매핑임
    public String postImage(@RequestParam(value = "upload") MultipartFile file) throws IOException {
        ImageEntity image = new ImageEntity();
        image.setFileName(file.getOriginalFilename());
        image.setFileMime(file.getContentType());
        image.setData(file.getBytes());

        Enum<?> result = this.shopService.addImage(image);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("url", "http://localhost:8080/shop/image?index=" + image.getIndex());
        }
        return responseObject.toString();
    }
}

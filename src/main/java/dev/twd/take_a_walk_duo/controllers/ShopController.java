package dev.twd.take_a_walk_duo.controllers;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.twd.take_a_walk_duo.entities.shop.SaleProductEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.models.PagingModel;
import dev.twd.take_a_walk_duo.services.BbsService;
import dev.twd.take_a_walk_duo.services.MemberService;
import dev.twd.take_a_walk_duo.services.ShopService;
import dev.twd.take_a_walk_duo.vos.shop.ProductVo;
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

    private final BbsService bbsService;
    private final MemberService memberService;


    @Autowired
    public ShopController(ShopService shopService, BbsService bbsService, MemberService memberService) {
        this.shopService = shopService;
        this.bbsService = bbsService;
        this.memberService = memberService;
    }

    // 쇼핑 메인 페이지 호출
    @RequestMapping(value = "/main",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getShop() {
        ModelAndView modelAndView = new ModelAndView("shop/main_backup");
        ProductVo[] products = this.shopService.getAllArticles();
        modelAndView.addObject("products",products);

        return modelAndView;
    }

//     쇼핑 리스트 페이지 호출
//    @RequestMapping(value = "/list",
//            method = RequestMethod.GET,
//            produces = MediaType.TEXT_HTML_VALUE)
//    public ModelAndView getList(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page) {
//        page = Math.max(1, page);
//        ModelAndView modelAndView = new ModelAndView("shop/list_backup");
////        int totalCount = this.shopService.getboard, criterion, keyword);
////        PagingModel paging = new PagingModel(totalCount, page);
////        modelAndView.addObject("paging", paging);
//        return modelAndView;
//    }

    // 키워드, 크리테리온 null이면 목록, 아니면 검색 결과 뜸
    @GetMapping(value = "/list",
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList(@RequestParam(value = "bid", required = false) String bid,
                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "criterion", required = false) String criterion,
                                @RequestParam(value = "keyword", required = false) String keyword) {

        page = Math.max(1, page); // 1과 page값 중 더 큰 값 반환. 마이너스 값을 방지하기 위해 if문 대신 이렇게 작성했음
        ModelAndView modelAndView = new ModelAndView("shop/list_backup");
        BoardEntity board = this.shopService.getBoard(bid);
        modelAndView.addObject("board", board);
        if (board != null) {
            int totalCount = this.shopService.getArticleCount(board, criterion, keyword);

            PagingModel paging = new PagingModel(16 ,totalCount, page);
            modelAndView.addObject("paging", paging);

            ProductVo[] articles = this.shopService.getArticles(board, paging, criterion, keyword);
            modelAndView.addObject("articles", articles);
            System.out.println("test articles" + articles.length);
            System.out.printf("이동 가능한 최소 페이지 : %d\n", paging.minPage);
            System.out.printf("이동 가능한 최대 페이지 : %d\n", paging.maxPage);
            System.out.printf("표시 시작 페이지 : %d\n", paging.startPage);
            System.out.printf("표시 끝 페이지 : %d\n", paging.endPage);


            for (int i = 0; i < articles.length; i++) {
                System.out.println("확인이다 이자식아" + articles[i].getCategoryText());
            }
        }
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
    @PostMapping(value = "write", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(ArticleEntity article,
                            SaleProductEntity product,
                            @RequestParam(value = "images", required = false) MultipartFile[] images,
                            @SessionAttribute(value = "user", required = false) UserEntity user) throws IOException {
        Enum<?> result;
        JSONObject responseObject = new JSONObject();
        result = this.shopService.write(article, product, images, user);
        responseObject.put("result", result.name().toLowerCase());

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

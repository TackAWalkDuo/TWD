package dev.twd.take_a_walk_duo.controllers;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.twd.take_a_walk_duo.entities.shop.ShoppingCartEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.models.PagingModel;
import dev.twd.take_a_walk_duo.services.MemberService;
import dev.twd.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.twd.take_a_walk_duo.entities.shop.SaleProductEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.services.BbsService;
import dev.twd.take_a_walk_duo.services.ShopService;
import dev.twd.take_a_walk_duo.vos.shop.CartVo;
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

@Controller(value = "dev.twd.take_a_walk_duo.controllers.ShopController")
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
    public ModelAndView getShop(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView("shop/main");
        ProductVo[] products = this.shopService.getAllArticles();
        modelAndView.addObject("products", products);
        if (user != null) {
            modelAndView.addObject("user", user);
        }
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
        ModelAndView modelAndView = new ModelAndView("shop/list");
        BoardEntity board = this.shopService.getBoard(bid);
        modelAndView.addObject("board", board);
        if (board != null) {
            int totalCount = this.shopService.getArticleCount(board, criterion, keyword);

            PagingModel paging = new PagingModel(totalCount, page);
            modelAndView.addObject("paging", paging);

            ProductVo[] articles = this.shopService.getArticles(board, paging, criterion, keyword);

            modelAndView.addObject("articles", articles);
            System.out.println("토탈 카운트? " + totalCount);
            System.out.println("twd articles" + articles.length);
            System.out.printf("이동 가능한 최소 페이지 : %d\n", paging.minPage);
            System.out.printf("이동 가능한 최대 페이지 : %d\n", paging.maxPage);
            System.out.printf("표시 시작 페이지 : %d\n", paging.startPage);
            System.out.printf("표시 끝 페이지 : %d\n", paging.endPage);
        }
        return modelAndView;
    }


    //    @RequestParam(value = "aid", required = false) int aid
    // 쇼핑 상품 상세보기 페이지 호출
    @GetMapping(value = "detail", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getDetail(@SessionAttribute(value = "user", required = false) UserEntity user, @RequestParam(value = "aid", required = false) int aid) {
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("shop/detail");
        ProductVo product = this.shopService.detailArticle(aid);
        modelAndView.addObject("product", product);
//        if (product != null) {
//            BoardEntity board = this.bbsService.getBoard(product.getBoardId());
//            modelAndView.addObject("board", board);
//        } else
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    // 상품 수정 호출
    @GetMapping(value = "modify",
            produces = MediaType.TEXT_HTML_VALUE)
    // modelandview 쓸때는 responsebody 어노테이션 쓰는거 아님.
    public ModelAndView getModify(@SessionAttribute(value = "user", required = false) UserEntity user,
                                  @RequestParam(value = "aid", required = false) int aid
    ) {
        ProductVo product = new ProductVo();
        product.setIndex(aid);
        Enum<?> result = this.shopService.prepareModifyArticle(product, user);
        ModelAndView modelAndView;

        modelAndView = new ModelAndView("shop/modify");
//        ProductVo product = this.shopService.getArticle();
        modelAndView.addObject("product", product);
        if (user != null) {
            modelAndView.addObject("user", user);
        }
//        modelAndView.addObject("result", result.name());
//        if (result == CommonResult.SUCCESS) {
//            modelAndView.addObject("board", this.bbsService.getBoard(article.getBoardId()));
//        }
        return modelAndView;
    }

    // 상품 수정 등록
    @PatchMapping(value = "modify",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody // xhr로 결과를 받기위해 사용
    public String patchModify(ArticleEntity article,
                              SaleProductEntity product,
                              @SessionAttribute(value = "user", required = false) UserEntity user, @RequestParam(value = "images", required = false) MultipartFile[] images,
                              @RequestParam(value = "aid", required = false) int aid) throws IOException {
        article.setIndex(aid);
//        Enum<?> result = this.shopService.modifyArticle(article, product, user);
        Enum<?> result = this.shopService.modifyArticle(article, product, images);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("aid", aid); // 성공시 js로 aid값 전달
        }
        return responseObject.toString();
    }

    // 상품 삭제
    @DeleteMapping(value = "detail", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteDetail(@SessionAttribute(value = "user", required = false) UserEntity user,
                               @RequestParam(value = "aid", required = false) int aid) {
        ProductVo product = new ProductVo();
        product.setIndex(aid);

        Enum<?> result = this.shopService.deleteProduct(product, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("bid", product.getCategoryText());
            System.out.println("카테" + product.getCategoryText());
        }
        return responseObject.toString();
    }

    // 상품 등록 페이지 호출
    @RequestMapping(value = "write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView;

        modelAndView = new ModelAndView("shop/write");
        ProductVo product = this.shopService.getArticle();
        modelAndView.addObject("product", product);
        if (user != null) {
            modelAndView.addObject("user", user);
        }
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

//    // 장바구니 호출
//    @GetMapping(value = "cart",
//            produces = MediaType.TEXT_HTML_VALUE)
//    public ModelAndView getCart(@SessionAttribute(value = "user", required = false) UserEntity user) {
//        ModelAndView modelAndView = new ModelAndView("shop/cart");
////        user.setEmail(userEmail);
////        System.out.println("유저 이메일은?"+user.getEmail());
//        CartVo[] products = this.shopService.getArticles(user);
//        modelAndView.addObject("products", products);
//
//        if (user != null) {
//            modelAndView.addObject("user", user);
//        }
//        return modelAndView;
//    }

    // 장바구니 호출(2트)
    @GetMapping(value = "cart",
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCart(@SessionAttribute(value = "user", required = false) UserEntity user, @RequestParam(value = "userEmail", required = false) String userEmail) {
        ModelAndView modelAndView = new ModelAndView("shop/cart");
        BoardEntity board = this.shopService.getBoard("shop");
        if (user == null) {
            modelAndView = new ModelAndView("redirect:/member/login");
        }
        modelAndView.addObject("userEmail", userEmail);
        modelAndView.addObject("user", user);
        CartVo[] carts = this.shopService.getArticles(userEmail);
        modelAndView.addObject("carts", carts);
        modelAndView.addObject("board", board);
        System.out.println("보드?"+board.getId());

        return modelAndView;
    }

    //장바구니 등록
    @PostMapping(value = "detail",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postCart(@SessionAttribute(value = "user", required = false) UserEntity user,
                           @RequestParam(value = "aid", required = false) int aid,
                           ShoppingCartEntity cart, ArticleEntity article) {
        //물품 수량 필요
//        CartVo cart = new CartVo();
//        cart.setIndex(aid);

        // 매개변수 user , aid , quantity
        // user 는 로그인 확인
        // aid 상품 확인
        // user, aid cart 에 동일한 상품을 등록하였는가?
        // quantity cart 에 담아서 insert
        Enum<?> result = this.shopService.addCart(article, cart, user, aid);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        System.out.println("장바구니 번호는?" + cart.getIndex());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("aid", aid);
        }
        return responseObject.toString();
    }

}

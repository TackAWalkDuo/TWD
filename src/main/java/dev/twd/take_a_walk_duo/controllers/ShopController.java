package dev.twd.take_a_walk_duo.controllers;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.twd.take_a_walk_duo.entities.shop.PaymentEntity;
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
import dev.twd.take_a_walk_duo.vos.shop.PaymentVo;
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
public class ShopController extends GeneralController {
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

    // 키워드, 크리테리온 null이면 목록, 아니면 검색 결과 뜸
    @GetMapping(value = "/list",
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList(@RequestParam(value = "bid", required = false) String bid,
                                @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                                @RequestParam(value = "criterion", required = false) String criterion,
                                @RequestParam(value = "keyword", required = false) String keyword) {

        page = Math.max(1, page); // 1과 page값 중 더 큰 값 반환. 마이너스 값을 방지하기 위해 해당 메서드 사용
        ModelAndView modelAndView = new ModelAndView("shop/list");
        BoardEntity board = this.shopService.getBoard(bid);
        modelAndView.addObject("board", board);
        if (board != null) {
            int totalCount = this.shopService.getArticleCount(board, criterion, keyword);

            PagingModel paging = new PagingModel(totalCount, page);
            modelAndView.addObject("paging", paging);

            ProductVo[] articles = this.shopService.getArticles(board, paging, criterion, keyword);

            modelAndView.addObject("articles", articles);
        }
        return modelAndView;
    }

    // 쇼핑 상품 상세보기 페이지 호출
    @GetMapping(value = "detail", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getDetail(@SessionAttribute(value = "user", required = false) UserEntity user, @RequestParam(value = "aid", required = false) int aid) {
        ModelAndView modelAndView;
        modelAndView = new ModelAndView("shop/detail");
        ProductVo product = this.shopService.detailArticle(aid);
        modelAndView.addObject("product", product);
        modelAndView.addObject("user", user);
        return modelAndView;
    }

    // 상품 수정 호출
    @GetMapping(value = "modify",
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify(@SessionAttribute(value = "user", required = false) UserEntity user,
                                  @RequestParam(value = "aid", required = false) int aid
    ) {
        ModelAndView modelAndView;
        ProductVo product = new ProductVo();
        product.setIndex(aid);

        this.shopService.prepareModifyArticle(product, user);

        modelAndView = new ModelAndView("shop/modify");
        modelAndView.addObject("product", product);
        if (user != null) {
            modelAndView.addObject("user", user);
        }
        return modelAndView;
    }

    // 상품 수정 등록
    @PatchMapping(value = "modify",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchModify(ArticleEntity article,
                              SaleProductEntity product,
                              @RequestParam(value = "images", required = false) MultipartFile[] images,
                              @RequestParam(value = "aid", required = false) int aid) throws IOException {
        article.setIndex(aid);
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
        if (result == CommonResult.SUCCESS) {
            responseObject.put("aid", product.getArticleIndex());
        }
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
    //value upload는 ckeiditor에서 정해놓은 것
    // upload용 매핑
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

    // 장바구니 호출
    @GetMapping(value = "cart",
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCart(@SessionAttribute(value = "user", required = false) UserEntity user) {
        ModelAndView modelAndView = new ModelAndView("shop/cart");
        BoardEntity board = this.shopService.getBoard("shop");
        modelAndView.addObject("user", user);
        modelAndView.addObject("board", board);
        if (user != null) {
            CartVo[] carts = this.shopService.getArticles(user.getEmail());
            modelAndView.addObject("carts", carts);
            modelAndView.addObject("isCart",carts.length);
        }
        return modelAndView;
    }

    //장바구니 등록
    @PostMapping(value = "detail",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postDetail(@SessionAttribute(value = "user", required = false) UserEntity user,
                           @RequestParam(value = "aid", required = false) int aid,
                           ShoppingCartEntity cart, ArticleEntity article) {
        Enum<?> result = this.shopService.addCart(article, cart, user, aid);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("aid", aid);
        }
        return responseObject.toString();
    }

    @PostMapping(value = "payment",
    produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postPayment(@SessionAttribute(value = "user", required = false) UserEntity user, PaymentEntity payment, int index, ShoppingCartEntity cart){
        Enum<?> result = this.shopService.easeAddPayment(user, payment, index, cart);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @PatchMapping(value = "cart",
    produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchCart(@SessionAttribute(value = "user", required = false) UserEntity user, ShoppingCartEntity cart) {
        Enum<?> result = this.shopService.modifyCart(cart, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @DeleteMapping(value = "cart",
    produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteCart(int[] index, @SessionAttribute(value = "user", required = false)UserEntity user){
        Enum<?> result = this.shopService.deleteCarts(index,user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @PostMapping(value = "cart",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postCart(@SessionAttribute(value = "user", required = false) UserEntity user,
                           int[] cartIndex) {
        Enum<?> result = this.shopService.addPayment(user, cartIndex);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @GetMapping(value = "payment",
    produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPayment(@SessionAttribute(value = "user", required = false)UserEntity user){
        ModelAndView modelAndView = new ModelAndView("shop/payment");
        BoardEntity board = this.shopService.getBoard("shop");
        modelAndView.addObject("user", user);
        modelAndView.addObject("board", board);
        if (user != null) {
            PaymentVo[] payments = this.shopService.getPayments(user.getEmail());
            modelAndView.addObject("payments", payments);
            modelAndView.addObject("isPayment",payments.length);
        }
        return modelAndView;
    }

    @DeleteMapping(value = "payment",
    produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deletePayment(PaymentEntity payment, @SessionAttribute(value = "user", required = false)UserEntity user){
        System.out.println("payment controller 결과후 " + payment.getGroupIndex());
        Enum<?> result = this.shopService.deletePayment(payment,user);
        System.out.println("payment service 결과후 " + payment.getGroupIndex());
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }
}

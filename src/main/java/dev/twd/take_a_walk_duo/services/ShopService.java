package dev.twd.take_a_walk_duo.services;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.twd.take_a_walk_duo.entities.shop.PaymentEntity;
import dev.twd.take_a_walk_duo.entities.shop.ShoppingCartEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.enums.shop.CartResult;
import dev.twd.take_a_walk_duo.interfaces.IResult;
import dev.twd.take_a_walk_duo.mappers.IBbsMapper;
import dev.twd.take_a_walk_duo.mappers.IMemberMapper;
import dev.twd.take_a_walk_duo.models.PagingModel;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import dev.twd.take_a_walk_duo.vos.shop.CartVo;
import dev.twd.take_a_walk_duo.vos.shop.PaymentVo;
import dev.twd.take_a_walk_duo.vos.shop.ProductVo;
import dev.twd.take_a_walk_duo.entities.shop.SaleProductEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.mappers.IShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service(value = "dev.twd.take_a_walk_duo.services.ShopService")
public class ShopService {
    private final IShopMapper shopMapper;
    private final IMemberMapper memberMapper;

    private final IBbsMapper bbsMapper;

    // memberMapper 서비스도 의존성 주입함.
    @Autowired
    public ShopService(IShopMapper shopMapper, IMemberMapper memberMapper, IBbsMapper bbsMapper) {
        this.shopMapper = shopMapper;
        this.memberMapper = memberMapper;
        this.bbsMapper = bbsMapper;
    }


    // list page
    public BoardEntity getBoard(String id)
    // id 는 게시판의 id임 notice 등
    {
        BoardEntity boardEntity = this.shopMapper.selectBoardById(id);
        System.out.println("보드 아이디는" + boardEntity.getId());
        return boardEntity;
//        return this.shopMapper.selectBoardById(id);
    }

    // 인터셉터용
    public BoardEntity[] getBoards() {
        return this.shopMapper.selectBoards();
    }

    public int getArticleCount(BoardEntity board, String criterion, String keyword) {
        System.out.println("criterion??" + criterion);
        return this.shopMapper.selectArticleCountByBoardId(board.getId(), criterion, keyword);
    }

    public ProductVo[] getArticles(BoardEntity board, PagingModel paging, String criterion, String keyword) {
        System.out.println("서비스 보드 테크스트ㅡ " + board.getText());
        return this.shopMapper.selectArticlesByBoardId(
                board.getId(),
                paging.countPerPage,
                (paging.requestPage - 1) * paging.countPerPage,
                criterion,
                keyword);
    }

    // 모든 상품 보기(메인 페이지)
    public ProductVo[] getAllArticles() {
        return this.shopMapper.selectAllArticles();
    }

    // 상품 가져오기(cart)
    public CartVo[] getArticles(String userEmail) {
        return this.shopMapper.selectCartsByUserEmail(userEmail);
    }

    public PaymentVo[] getPayments(String userEmail) {
        return this.shopMapper.selectPaymentsByUserEmail(userEmail);
    }

    // 상품 가져오기 (cart) 2트
//    public Enum<? extends  IResult> getCart(ArticleEntity article, UserEntity user, ShoppingCartEntity cart){
//
//    }

    // detail page
    public ProductVo detailArticle(int index) {
        return this.shopMapper.selectArticleByArticleIndex(index);
    }

    // 상품 수정
    public Enum<? extends IResult> prepareModifyArticle(ProductVo product, UserEntity user) {
        if (user == null) {
            return CommonResult.FAILURE;
        }
        ProductVo existingProduct = this.shopMapper.selectArticleByArticleIndex(product.getIndex());
        if (existingProduct == null) {
            return CommonResult.FAILURE;
        }
        product.setIndex(existingProduct.getIndex());
        product.setUserEmail(existingProduct.getUserEmail());
        product.setCategoryText(existingProduct.getCategoryText());
        product.setAdmin(existingProduct.getAdmin());
        product.setCost(existingProduct.getCost());
        product.setBoardId(existingProduct.getBoardId());
        product.setPrice(existingProduct.getPrice());
        product.setDiscount(existingProduct.getDiscount());
        product.setQuantity(existingProduct.getQuantity());
        product.setText(existingProduct.getText());
        product.setTitle(existingProduct.getTitle());
        product.setContent(existingProduct.getContent());
        product.setCategoryName(existingProduct.getCategoryName());
        product.setWrittenOn(existingProduct.getWrittenOn());
        product.setModifiedOn(existingProduct.getModifiedOn());
        return CommonResult.SUCCESS;
    }

    // 상품 수정 2트
    public Enum<? extends IResult> modifyArticle(ArticleEntity article, SaleProductEntity product, @RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {
        SaleProductEntity existingProduct = this.shopMapper.selectProductByArticleIndex(article.getIndex());
        System.out.println("프로덕트 어디니" + existingProduct.getArticleIndex());
        System.out.println("프로덕트 뭐 가지고 있니" + product.getText());

        ArticleEntity existingArticle = this.shopMapper.selectArticleByIndex(article.getIndex());
        System.out.println("아티클 어디니?" + article.getIndex());

        System.out.println("아티클 타이틀 수정 전 " + existingArticle.getTitle());
        existingArticle.setTitle(article.getTitle());
        existingArticle.setContent(article.getContent());
        System.out.println("아티클 타이틀 수정 후 " + existingArticle.getTitle());

        existingProduct.setCategoryText(product.getCategoryText());
        existingProduct.setCost(product.getCost());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscount(product.getDiscount());

        System.out.println("프로덕트 수량 수정 전 " + existingProduct.getQuantity());
        existingProduct.setQuantity(product.getQuantity());
        System.out.println("프로덕트 수량 수정 후 " + existingProduct.getQuantity());

        existingProduct.setText(product.getText());

        System.out.println("아티클 이미지 수정 전 " + existingArticle.getThumbnail());

//        existingArticle.setThumbnail(existingArticle.getThumbnail());
        System.out.println("아티클 이미지 수정 전 중간 " + existingArticle.getThumbnail());
        if (images != null) {
            for (MultipartFile image : images) {
                existingArticle.setThumbnail(image.getBytes());
                existingArticle.setThumbnailType(image.getContentType());
            }
        }
////            if (images == null){
//            byte[] imageInByte;
//            File defaultImage = new File("src/main/resources/static/resources/images/ingImage.jpeg");
//            defaultImage.setReadable(true, false);
//
//            BufferedImage originalImage = ImageIO.read(defaultImage);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(originalImage, "jpeg", baos);
//            baos.flush();
//
//            imageInByte = baos.toByteArray();
//
//            existingArticle.setThumbnail(imageInByte);
//            existingArticle.setThumbnailType("image/jpeg");
//            baos.close();
//        }
        System.out.println("아티클 이미지 수정 후" + existingArticle.getThumbnail());

        if (this.shopMapper.updateArticle(existingArticle) == 0) {
            return CommonResult.FAILURE;
        }

        if (this.shopMapper.updateProduct(existingProduct) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }

//    public Enum<? extends IResult> modifyArticle(ArticleEntity article, SaleProductEntity product, @SessionAttribute(value = "user", required = false) UserEntity user) {
//        ArticleEntity existingArticle =
//                this.shopMapper.selectArticleByIndex(
//                        article.getIndex());
//
//        SaleProductEntity existingProduct = this.shopMapper.selectProductByArticleIndex(product.getArticleIndex());
//
//        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());
//
//        if (!existingUser.getAdmin()) { // 관리자 계정이 아니라면 등록 실패
//            return CommonResult.FAILURE;
//        }
//
//
////        product.setCategoryText(existingProduct.getCategoryText());
////        product.setCost(existingProduct.getCost());
////        product.setPrice(existingProduct.getPrice());
////        product.setDiscount(existingProduct.getDiscount());
////        product.setQuantity(existingProduct.getQuantity());
////        product.setText(existingProduct.getText());
//
////        user.setAdmin(existingUser.getAdmin());
////
////        article.setIndex(existingArticle.getIndex());
////        article.setUserEmail(existingArticle.getUserEmail());
////        article.setBoardId(existingArticle.getBoardId());
////        article.setTitle(existingArticle.getTitle());
////        article.setContent(existingArticle.getContent());
////        product.setCategoryName(existingProduct.getCategoryName());
////        article.setWrittenOn(existingArticle.getWrittenOn());
////        article.setModifiedOn(new Date());
//        if (this.shopMapper.updateArticle(existingProduct) == 0) {
//            return CommonResult.FAILURE;
//        }
//        return CommonResult.SUCCESS;
//    }

    // 상품 삭제
    public Enum<? extends IResult> deleteProduct(ProductVo product, UserEntity user) {
        ProductVo existingProduct = this.shopMapper.selectArticleByArticleIndex(product.getIndex());
        if (existingProduct == null) {
            return CommonResult.FAILURE;
        }
        if (user == null || !user.getEmail().equals(existingProduct.getUserEmail())) {
            return CommonResult.FAILURE;
        }
        product.setCategoryText(existingProduct.getCategoryText());
        product.setBoardId(existingProduct.getBoardId()); // db에서 가져온 boardID를 전달받은 article의 boardID로 세팅 > 컨트롤러와 연계
        return this.bbsMapper.deleteArticle(product.getIndex()) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // write get
    public ProductVo getArticle() {
        return this.shopMapper.selectArticle();
    }

    // 상품 등록
    @Transactional
    public Enum<? extends IResult> write(ArticleEntity article,
                                         SaleProductEntity product,
                                         @RequestParam(value = "images", required = false) MultipartFile[] images,
                                         @SessionAttribute(value = "user", required = false) UserEntity user) throws IOException {
        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail()); // 로그인한 userEmail과 DB에 있는 userEmail 대조(memberMapper의 쿼리 사용)
        if (!existingUser.getAdmin()) { // 관리자 계정이 아니라면 등록 실패
            return CommonResult.FAILURE;
        }
        article.setUserEmail(user.getEmail());
        article.setBoardId("shop"); // BoardId shop 하나라서 고정.
        if (images != null) {
            for (MultipartFile image : images) {
                article.setThumbnail(image.getBytes());
                article.setThumbnailType(image.getContentType());
            }
        } else {
            byte[] imageInByte;
            File defaultImage = new File("src/main/resources/static/resources/images/ingImage.jpeg");
            defaultImage.setReadable(true, false);

            BufferedImage originalImage = ImageIO.read(defaultImage);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "jpeg", baos);
            baos.flush();

            imageInByte = baos.toByteArray();

            article.setThumbnail(imageInByte);
            article.setThumbnailType("image/jpeg");
            baos.close();
        }

        if (this.shopMapper.insertShopArticle(article) == 0) {
            return CommonResult.FAILURE;
        }

        product.setArticleIndex(article.getIndex());
//        product.setCost(0);
        product.setProfit(0);
//        product.setDiscount(0);

        if (this.shopMapper.insertProduct(product) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }

    // 이미지 업로드
    public Enum<? extends IResult> addImage(ImageEntity image) {
        return this.shopMapper.insertImage(image) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 이미지 다운로드
    public ImageEntity getImage(int index) {
        return this.shopMapper.selectImageByIndex(index);
    }

    // 장바구니 담기
    public Enum<? extends IResult> addCart(ArticleEntity article, ShoppingCartEntity cart, UserEntity user, int aid) {
        //email, aid 를 통해서 장바구니 검색. 있다면 "동일한 상품이 장바구니에 있습니다."
        //없다면 입력된 aid 값의 boarID shop 관련인지 확인
        //로그인 확인
        // 담으려는 상품 갯수가 0이하 인지 확인 재고확인.
//        ShoppingCartEntity existingCart = this.shopMapper.selectArticleByCartIndex(cart.getIndex());
//        System.out.println(existingCart.getIndex());
//        UserEntity existingUser = this.memberMapper.selectUserByEmail(user.getEmail());
//        existingCart.setUserEmail(user.getEmail());

//        if (existingCart != null){
//            return CommonResult.FAILURE;
//        }
//
//        if (user == null || !user.getEmail().equals(existingCart.getUserEmail())) {
//            return CommonResult.FAILURE;
//        }

//        ShoppingCartEntity existingCart = this.shopMapper.selectArticleByArticleIndexUserEmail(aid, cart.getUserEmail());
//        if (existingCart != null) {
//            return CommonResult.FAILURE;
//        }
//        System.out.println("머임?" + existingCart);
//        System.out.println("aid는?" + aid);
//        System.out.println("카트의 유저 이메일?" + cart.getUserEmail());
//        System.out.println("이메일은?" + user.getEmail());
//
//        existingCart.setUserEmail(user.getEmail());
//        System.out.println("ex이메일은?" + existingCart.getUserEmail());

        // 로그인 되어 있지 않으면
        if (user == null) {
            return CartResult.CART_NOT_SIGNED;
        }

        // boardId가 shop이 아니면
        ArticleEntity existingArticle = this.shopMapper.selectArticleByIndex(aid);
        article.setBoardId(existingArticle.getBoardId());
        article.setTitle(existingArticle.getTitle());
        System.out.println("어데 게시판입니꺼?" + article.getBoardId());
        System.out.println("제목은?" + article.getTitle());
        if (!article.getBoardId().equals("shop")) {
            return CartResult.CART_NOT_ALLOWED;
        }

        ShoppingCartEntity existingCart = this.shopMapper.selectArticleByArticleIndexUserEmail(aid, user.getEmail());

        // 이미 장바구니에 등록 되어 있으면
        if (existingCart != null) {
            return CartResult.CART_DUPLICATED;
        }
        System.out.println("머임?" + existingCart);
        System.out.println("aid는?" + aid);
        System.out.println("이메일은?" + user.getEmail());
        cart.setProductIndex(aid);
        System.out.println("카트의 aid는?" + cart.getProductIndex());
        cart.setUserEmail(user.getEmail());
        System.out.println("카트의 유저 이메일?" + cart.getUserEmail());


        //insert ..
//        cart.setProductIndex(aid);
//        cart.setUserEmail(existingCart.getUserEmail());
//        cart.setDeliveryFee(3000);
//        cart.setRegistrationOn(new Date());
//        existingCart.setProductIndex(aid);
//        existingCart.setUserEmail(cart.getUserEmail());
        cart.setDeliveryFee(3000);
        cart.setRegistrationOn(new Date());
//        System.out.println("인덱스는?" + existingCart.getIndex());
//        System.out.println("제목은?" + article.getTitle());
        return this.shopMapper.insertCart(cart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public Enum<? extends IResult> modifyCart(ShoppingCartEntity cart, UserEntity user) {
        ShoppingCartEntity existingCart = this.shopMapper.selectCartByIndex(cart.getIndex(), user.getEmail());
        existingCart.setQuantity(cart.getQuantity());

        return this.shopMapper.updateCart(existingCart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    @Transactional
    public Enum<? extends IResult> addPayment(UserEntity user, int[] cartIndex) {
        if (user == null) {
            return CommonResult.FAILURE;
        }

        for (int i = 0; i < cartIndex.length; i++) {
            ShoppingCartEntity existingCart = this.shopMapper.selectCartByCartIndex(cartIndex[i]);

            if (existingCart == null) {
                return CommonResult.FAILURE;
            }

            PaymentEntity payment = new PaymentEntity();
            payment.setDeliveryFee(i == 0 ? 3000 : 0);
            payment.setDeliveryStatus(0);
            payment.setProductIndex(existingCart.getProductIndex());
            payment.setSalePrice(existingCart.getSalePrice());
            payment.setUserEmail(user.getEmail());
            payment.setQuantity(existingCart.getQuantity());
            payment.setAddressPostal(user.getAddressPostal());
            payment.setAddressPrimary(user.getAddressPrimary());
            payment.setAddressSecondary(user.getAddressSecondary());
            payment.setRegistrationOn(new Date());

            if (this.shopMapper.insertPayment(payment) == 0) {
                return CommonResult.FAILURE;
            }

            SaleProductEntity existingProduct = this.shopMapper.selectProductByArticleIndex(existingCart.getProductIndex());
            existingProduct.setQuantity(existingProduct.getQuantity() - existingCart.getQuantity());
            if (this.shopMapper.updateProduct(existingProduct) == 0) {
                return CommonResult.FAILURE;
            }

            // sale_product 검색
            // sale_product.setQu~~(getQu~~ - ~~);
            //update()

            existingCart.setIndex(cartIndex[i]);
            if (this.shopMapper.deleteCartByIndex(existingCart) == 0) {
                return CommonResult.FAILURE;
            }
        }

        return CommonResult.SUCCESS;
    }

    // 2트
    @Transactional
    public Enum<? extends IResult> easeAddPayment(UserEntity user, PaymentEntity payment) {
        if (user == null) {
            return CommonResult.FAILURE;
        }

        SaleProductEntity existingProduct = this.shopMapper.selectProductByArticleIndex(payment.getProductIndex());

        if (existingProduct == null) {
            return CommonResult.FAILURE;
        }

        payment.setDeliveryFee(3000);
        payment.setDeliveryStatus(0);
        payment.setProductIndex(existingProduct.getArticleIndex());
        payment.setSalePrice(existingProduct.getPrice());
        payment.setUserEmail(user.getEmail());
        payment.setQuantity(existingProduct.getQuantity());
        payment.setAddressPostal(user.getAddressPostal());
        payment.setAddressPrimary(user.getAddressPrimary());
        payment.setAddressSecondary(user.getAddressSecondary());
        payment.setRegistrationOn(new Date());

        if (this.shopMapper.insertPayment(payment) == 0) {
            return CommonResult.FAILURE;
        }

        existingProduct.setQuantity(existingProduct.getQuantity() - payment.getQuantity());
        if (this.shopMapper.updateProduct(existingProduct) == 0) {
            return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    //1트
//    @Transactional
//    public Enum<? extends IResult> easeAddPayment(UserEntity user, int aid) {
//        if (user == null) {
//            return CommonResult.FAILURE;
//        }
//        SaleProductEntity existingProduct = this.shopMapper.selectProductByArticleIndex(aid);
//
//        if (existingProduct == null) {
//            return CommonResult.FAILURE;
//        }
//
//        PaymentEntity payment = new PaymentEntity();
//        payment.setDeliveryFee(3000);
//        payment.setDeliveryStatus(0);
//        payment.setProductIndex(existingProduct.getArticleIndex());
//        payment.setSalePrice(existingProduct.getPrice());
//        payment.setUserEmail(user.getEmail());
//        payment.setQuantity(existingProduct.getQuantity());
//        payment.setAddressPostal(user.getAddressPostal());
//        payment.setAddressPrimary(user.getAddressPrimary());
//        payment.setAddressSecondary(user.getAddressSecondary());
//        payment.setRegistrationOn(new Date());
//
//        if (this.shopMapper.insertPayment(payment) == 0) {
//            return CommonResult.FAILURE;
//        }
//
//        existingProduct.setQuantity(existingProduct.getQuantity());
//        if (this.shopMapper.updateProduct(existingProduct) == 0) {
//            return CommonResult.FAILURE;
//        }
//        return CommonResult.SUCCESS;
//    }
//    @Transactional
//    public Enum<? extends IResult> addPayment(UserEntity user, ShoppingCartEntity cart) {
//        if (user == null) {
//            return CommonResult.FAILURE;
//        }
//        ShoppingCartEntity existingCart = this.shopMapper.selectCartByCartIndex(cart.getIndex());
//        existingCart.setIndex(cart.getIndex());
//        System.out.println("ex카트 인덱스" + existingCart.getIndex());
//
//        if (this.shopMapper.deleteCartByIndex(existingCart) == 0) {
//            return CommonResult.FAILURE;
//        }
//
//        // 현재 주문하려고하는 상품의 재고가 주문 갯수보다 작을경우 return false;
//
////        PaymentEntity existingPayment = this.shopMapper.selectPaymentByRegistration();
//        PaymentEntity pay = new PaymentEntity();
//
//        pay.setDeliveryStatus(0);
//        pay.setProductIndex(existingCart.getProductIndex());
//        pay.setSalePrice(existingCart.getSalePrice());
//        pay.setUserEmail(user.getEmail());
//        pay.setQuantity(existingCart.getQuantity());
//        pay.setDeliveryFee(3000);
//        pay.setAddressPostal(user.getAddressPostal());
//        pay.setAddressPrimary(user.getAddressPrimary());
//        pay.setAddressSecondary(user.getAddressSecondary());
//        pay.setRegistrationOn(new Date());
//
//
//        System.out.println("카트 프로덕트 인덱스" + cart.getProductIndex());
//        System.out.println("ex 카트 푸로덕트 인덱스" + existingCart.getProductIndex());
//        System.out.println("주소?" + user.getAddressPrimary());
//
//        if (this.shopMapper.insertPayment(pay) == 0) {
//            return CommonResult.FAILURE;
//        }
//        return CommonResult.SUCCESS;
//    }

    public Enum<? extends IResult> deleteCarts(ShoppingCartEntity cart, UserEntity user) {
        ShoppingCartEntity existingCart = this.shopMapper.selectCartByCartIndex(cart.getIndex());
        if (existingCart == null) {
            return CommonResult.FAILURE;
        }
        if (!user.getEmail().equals(existingCart.getUserEmail())) {
            return CommonResult.FAILURE;
        }
        existingCart.setIndex(cart.getIndex());
        return this.shopMapper.deleteCartByIndex(existingCart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    @Transactional
    public Enum<? extends IResult> deletePayment(PaymentEntity payment, UserEntity user) {
        PaymentEntity existingPayment = this.shopMapper.selectPaymentByIndex(payment.getIndex());
        if (existingPayment == null) {
            return CommonResult.FAILURE;
        }
        if (!user.getEmail().equals(existingPayment.getUserEmail())) {
            return CommonResult.FAILURE;
        }

        SaleProductEntity existingProduct = this.shopMapper.selectProductByArticleIndex(existingPayment.getProductIndex());
        existingProduct.setQuantity(existingProduct.getQuantity() + existingPayment.getQuantity());

        if (this.shopMapper.updateProduct(existingProduct) == 0) {
            return CommonResult.FAILURE;
        }

        existingPayment.setIndex(payment.getIndex());
        return this.shopMapper.deletePayment(existingPayment) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}


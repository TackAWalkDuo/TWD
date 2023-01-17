package dev.twd.take_a_walk_duo.services;

import dev.twd.take_a_walk_duo.entities.bbs.*;
import dev.twd.take_a_walk_duo.entities.shop.PaymentEntity;
import dev.twd.take_a_walk_duo.entities.shop.ShoppingCartEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.enums.shop.CartResult;
import dev.twd.take_a_walk_duo.interfaces.IResult;
import dev.twd.take_a_walk_duo.mappers.IBbsMapper;
import dev.twd.take_a_walk_duo.mappers.IMemberMapper;
import dev.twd.take_a_walk_duo.models.PagingModel;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import dev.twd.take_a_walk_duo.vos.bbs.CommentVo;
import dev.twd.take_a_walk_duo.vos.shop.CartVo;
import dev.twd.take_a_walk_duo.vos.shop.PaymentVo;
import dev.twd.take_a_walk_duo.vos.shop.ProductVo;
import dev.twd.take_a_walk_duo.entities.shop.SaleProductEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.mappers.IShopMapper;
import org.apache.ibatis.exceptions.TooManyResultsException;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service(value = "dev.twd.take_a_walk_duo.services.ShopService")
public class ShopService {
    private final IShopMapper shopMapper;
    private final IMemberMapper memberMapper;

    private final IBbsMapper bbsMapper;

    @Autowired
    public ShopService(IShopMapper shopMapper, IMemberMapper memberMapper, IBbsMapper bbsMapper) {
        this.shopMapper = shopMapper;
        this.memberMapper = memberMapper;
        this.bbsMapper = bbsMapper;
    }

    // 리스트 호출(board)
    public BoardEntity getBoard(String id) {
        BoardEntity boardEntity = this.shopMapper.selectBoardById(id);
        return boardEntity;
    }

    // 인터셉터용
    public BoardEntity[] getBoards() {
        return this.shopMapper.selectBoards();
    }

    // 게시글 갯수 확인
    public int getArticleCount(BoardEntity board, String criterion, String keyword) {
        return this.shopMapper.selectArticleCountByBoardId(board.getId(), criterion, keyword);
    }

    // 리스트 호출
    public ProductVo[] getArticles(BoardEntity board, PagingModel paging, String criterion, String keyword) {
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

    public ProductVo[] getConditionArticles(String categoryText) {
        return this.shopMapper.selectConditionArticles(categoryText);
    }

    // 장바구니 호출
    public CartVo[] getArticles(String userEmail) {
        return this.shopMapper.selectCartsByUserEmail(userEmail);
    }

    // 주문내역 호출
    public PaymentVo[] getPayments(String userEmail) {
        return this.shopMapper.selectPaymentsByUserEmail(userEmail);
    }

    // 상세보기 호출
    public ProductVo detailArticle(int index) {
        return this.shopMapper.selectArticleByArticleIndex(index);
    }

    // 상품 수정(준비)
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

    // 상품 수정(실행)
    public Enum<? extends IResult> modifyArticle(ArticleEntity article, SaleProductEntity product, @RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {
        SaleProductEntity existingProduct = this.shopMapper.selectProductByArticleIndex(article.getIndex());

        ArticleEntity existingArticle = this.shopMapper.selectArticleByIndex(article.getIndex());
        existingArticle.setTitle(article.getTitle());
        existingArticle.setContent(article.getContent());
        existingProduct.setCategoryText(product.getCategoryText());
        existingProduct.setCost(product.getCost());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setText(product.getText());

        if (images != null) {
            for (MultipartFile image : images) {
                existingArticle.setThumbnail(image.getBytes());
                existingArticle.setThumbnailType(image.getContentType());
            }
        }

        if (this.shopMapper.updateArticle(existingArticle) == 0) {
            return CommonResult.FAILURE;
        }

        if (this.shopMapper.updateProduct(existingProduct) == 0) {
            return CommonResult.FAILURE;
        }

        return CommonResult.SUCCESS;
    }

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

    // 상품등록 호출
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

        // 관리자 계정이 아니라면 등록 실패
        if (!existingUser.getAdmin()) {
            return CommonResult.FAILURE;
        }
        article.setUserEmail(user.getEmail());
        // BoardId shop으로 고정.
        article.setBoardId("shop");
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
        product.setProfit(0);

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

        // 로그인 되어 있지 않으면
        if (user == null) {
            return CartResult.CART_NOT_SIGNED;
        }

        ArticleEntity existingArticle = this.shopMapper.selectArticleByIndex(aid);
        article.setBoardId(existingArticle.getBoardId());
        article.setTitle(existingArticle.getTitle());
        // boardId가 shop이 아니면
        if (!article.getBoardId().equals("shop")) {
            return CartResult.CART_NOT_ALLOWED;
        }

        ShoppingCartEntity existingCart = this.shopMapper.selectArticleByArticleIndexUserEmail(aid, user.getEmail());

        // 이미 장바구니에 등록 되어 있으면
        if (existingCart != null) {
            return CartResult.CART_DUPLICATED;
        }

        cart.setProductIndex(aid);
        cart.setUserEmail(user.getEmail());
        cart.setDeliveryFee(3000);
        cart.setRegistrationOn(new Date());
        return this.shopMapper.insertCart(cart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 장바구니 수정
    public Enum<? extends IResult> modifyCart(ShoppingCartEntity cart, UserEntity user) {
        ShoppingCartEntity existingCart = this.shopMapper.selectCartByIndex(cart.getIndex(), user.getEmail());
        existingCart.setQuantity(cart.getQuantity());

        return this.shopMapper.updateCart(existingCart) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 장바구니에서 구매
    @Transactional
    public Enum<? extends IResult> addPayment(UserEntity user, int[] cartIndex) {
        if (user == null) {
            return CommonResult.FAILURE;
        }

        int groupIndex = 0;
        for (int i = 0; i < cartIndex.length; i++) {

            ShoppingCartEntity existingCart = this.shopMapper.selectCartByCartIndex(cartIndex[i]);

            if (existingCart == null) {
                return CommonResult.FAILURE;
            }

            PaymentEntity payment = new PaymentEntity();

            if (i == 0) groupIndex = cartIndex[0];

            payment.setGroupIndex(groupIndex);
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

            existingCart.setIndex(cartIndex[i]);
            if (this.shopMapper.deleteCartByIndex(existingCart) == 0) {
                return CommonResult.FAILURE;
            }
        }
        return CommonResult.SUCCESS;
    }

    // 상세보기에서 바로 구매
    @Transactional
    public Enum<? extends IResult> easeAddPayment(UserEntity user, PaymentEntity payment, int index, ShoppingCartEntity cart) {
        if (user == null) {
            return CommonResult.FAILURE;
        }
        SaleProductEntity existingProduct = this.shopMapper.selectProductByArticleIndex(index);
        if (existingProduct == null) {
            return CommonResult.FAILURE;
        }

        cart.setProductIndex(index);
        cart.setUserEmail(user.getEmail());
        cart.setDeliveryFee(existingProduct.getDeliveryFee());
        cart.setRegistrationOn(new Date());

        if (this.shopMapper.insertCart(cart) == 0) {
            return CommonResult.FAILURE;
        }

        payment.setGroupIndex(cart.getIndex());
        payment.setQuantity(cart.getQuantity());

        if (this.shopMapper.deleteCartByIndex(cart) == 0) {
            return CommonResult.FAILURE;
        }

        payment.setDeliveryFee(3000);
        payment.setDeliveryStatus(0);
        payment.setProductIndex(existingProduct.getArticleIndex());
        payment.setSalePrice(existingProduct.getPrice());
        payment.setUserEmail(user.getEmail());
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

    // 장바구니 삭제
    public Enum<? extends IResult> deleteCarts(int[] index, UserEntity user) {
        for (int i = 0; i < index.length; i++) {
            ShoppingCartEntity existingCart = this.shopMapper.selectCartByCartIndex(index[i]);
            if (existingCart == null) {
                return CommonResult.FAILURE;
            }
            if (!user.getEmail().equals(existingCart.getUserEmail())) {
                return CommonResult.FAILURE;
            }
            existingCart.setIndex(index[i]);
            if (this.shopMapper.deleteCartByIndex(existingCart) <= 0)
                return CommonResult.FAILURE;
        }
        return CommonResult.SUCCESS;
    }

    // 구매내역 삭제
    @Transactional
    public Enum<? extends IResult> deletePayment(PaymentEntity payment, UserEntity user) {
        PaymentEntity[] existingPayment = this.shopMapper.selectPaymentByGroupIndex(payment.getGroupIndex());

        if (existingPayment == null) {
            return CommonResult.FAILURE;
        }

        for (int i = 0; i < existingPayment.length; i++) {
            if (!user.getEmail().equals(existingPayment[i].getUserEmail())) {
                return CommonResult.FAILURE;
            }

            SaleProductEntity existingProduct = this.shopMapper.selectProductByArticleIndex(existingPayment[i].getProductIndex());
            existingProduct.setQuantity(existingProduct.getQuantity() + existingPayment[i].getQuantity());
            if (this.shopMapper.updateProduct(existingProduct) == 0) {
                return CommonResult.FAILURE;
            }
        }

        return this.shopMapper.deletePayment(payment.getGroupIndex()) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    //todo comment 서비스
    public CommentVo[] getComment(int index, UserEntity user) {
        CommentVo[] comments = this.bbsMapper.selectCommentsByIndex(index, user == null ? null : user.getEmail());
        for (CommentVo comment : comments) {
            CommentImageEntity[] commentImage = this.bbsMapper.selectCommentImagesByCommentIndexExceptData(comment.getIndex());
            int[] reviewImageIndexes = Arrays.stream(commentImage).mapToInt(CommentImageEntity::getIndex).toArray();

            comment.setImageIndexes(reviewImageIndexes);
        }
        return comments;
    }


    @Transactional
    public Enum<? extends IResult> addComment(UserEntity user, CommentEntity comment,
                                              MultipartFile[] images, int aid) throws IOException {
        if (user == null) {
            return CommonResult.NOT_SIGNED;
        }

        comment.setUserEmail(user.getEmail());
        comment.setWrittenOn(new Date());
        comment.setArticleIndex(aid);
        if (this.bbsMapper.insertComment(comment) == 0) {
            return CommonResult.FAILURE;
        }
        if (images != null && images.length > 0) {
            for (MultipartFile image : images) {
                CommentImageEntity commentImage = new CommentImageEntity();
                commentImage.setCommentIndex(comment.getIndex());
                commentImage.setData(image.getBytes());
                commentImage.setType(image.getContentType());
                if (this.bbsMapper.insertCommentImage(commentImage) == 0) {
                    return CommonResult.FAILURE;
                }
            }
        }

        return CommonResult.SUCCESS;


    }

    public CommentImageEntity getCommentImage(int index) {
        return this.bbsMapper.selectCommentImageByIndex(index);
    }

    public Enum<? extends IResult> likedComment(CommentLikeEntity commentLikeEntity, UserEntity user) {
        CommentVo existingComment = this.bbsMapper.selectCommentByIndex(commentLikeEntity.getCommentIndex());
        if (existingComment == null) {
            return CommonResult.FAILURE;
        }
        if (this.bbsMapper.selectCommentLikeByIndex(commentLikeEntity.getCommentIndex(), user.getEmail()) != null) {
            return this.bbsMapper.deleteByCommentLiked(commentLikeEntity.getCommentIndex()) > 0
                    ? CommonResult.SUCCESS
                    : CommonResult.FAILURE;
        }
        commentLikeEntity.setUserEmail(user.getEmail());
        commentLikeEntity.setCreatedOn(new Date());
        return this.bbsMapper.insertCommentLike(commentLikeEntity) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    public SaleProductEntity getSaleProduct(int index) {
        PaymentEntity payment = this.shopMapper.selectPaymentByIndex(index);
        return this.shopMapper.selectProductByArticleIndex(payment.getProductIndex());
    }

    public ArticleEntity getArticle(int index) {
        return this.shopMapper.selectArticleByArticleIndex(index);
    }
}


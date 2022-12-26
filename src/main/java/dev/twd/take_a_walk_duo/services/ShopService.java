package dev.twd.take_a_walk_duo.services;

import dev.twd.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.twd.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.twd.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.interfaces.IResult;
import dev.twd.take_a_walk_duo.mappers.IBbsMapper;
import dev.twd.take_a_walk_duo.mappers.IMemberMapper;
import dev.twd.take_a_walk_duo.models.PagingModel;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import dev.twd.take_a_walk_duo.vos.shop.ProductVo;
import dev.twd.take_a_walk_duo.entities.shop.SaleProductEntity;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.mappers.IShopMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service(value = "dev.test.take_a_walk_duo.services.ShopService")
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
        return this.shopMapper.selectBoardById(id);
    }

    // 인터셉터용
    public BoardEntity[] getBoards() {
        return this.shopMapper.selectBoards();
    }

    public int getArticleCount(BoardEntity board, String criterion, String keyword) {
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
    public ProductVo[] getAllArticles(){
        return this.shopMapper.selectAllArticles();
    }

    // detail page
    public ProductVo detailArticle(int index){
        return this.shopMapper.selectArticleByArticleIndex(index);
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

    // 상품 수정


    // write get
    public ProductVo getArticle(){
        return this.shopMapper.selectArticle();
    }

    // 상품 등록
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
        }else {
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
    public ImageEntity getImage(int index){
        return this.shopMapper.selectImageByIndex(index);
    }
}


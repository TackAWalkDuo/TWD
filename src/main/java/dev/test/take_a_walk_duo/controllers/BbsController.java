package dev.test.take_a_walk_duo.controllers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.ArticleLikeEntity;
import dev.test.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.test.take_a_walk_duo.entities.bbs.ImageEntity;
import dev.test.take_a_walk_duo.entities.member.UserEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
import dev.test.take_a_walk_duo.enums.bbs.ModifyArticleResult;
import dev.test.take_a_walk_duo.enums.bbs.WriteResult;
import dev.test.take_a_walk_duo.services.BbsService;
import dev.test.take_a_walk_duo.vos.bbs.ArticleReadVo;
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

@Controller(value = "dev.test.take_a_walk_duo.controllers.bbsController")
@RequestMapping(value = "/bbs")
public class BbsController {
    private final BbsService bbsService;

    @Autowired
    public BbsController(BbsService bbsService) {
        this.bbsService = bbsService;
    }

    //Mr.m
    //글쓰기 Get(창띄우기)
    @RequestMapping(value = "write",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getWrite(@SessionAttribute(value = "user", required = false) UserEntity user,
                                 @RequestParam(value = "bid", required = false) String bid) {
        ModelAndView modelAndView;
        if (user == null) {
            modelAndView = new ModelAndView("redirect:/member/login");
        } else {
            System.out.println("췍");
            BoardEntity board = bid == null ? null : this.bbsService.getBoard(bid);
            System.out.println("췍2");

            modelAndView = new ModelAndView("bbs/write");
            modelAndView.addObject("board", board);
        }
        return modelAndView;
    }

    //Mr.m
    //글쓰기 post(글작업하기)
    @RequestMapping(value = "write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWrite(@SessionAttribute(value = "user", required = false) UserEntity user,
                            ArticleEntity article,
                            @RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {
        Enum<?> result;
        JSONObject responseObject = new JSONObject();
        if (user == null) {
            result = WriteResult.NOT_ALLOWED;
        } else if (article.getBoardId() == null) {
            result = WriteResult.NO_SUCH_BOARD;
        } else {
            article.setUserEmail(user.getEmail());
            result = this.bbsService.RegisterArticle(article, images);
        }
        if (result == CommonResult.SUCCESS) {
            responseObject.put("aid", article.getIndex());
        }
        responseObject.put("result", result.name().toLowerCase());
        System.out.println("controller check" + article.getBoardId());
        return responseObject.toString();
    }

    //Mr.m
    //게시글 읽기 구현
    @RequestMapping(value = "read",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRead(@SessionAttribute(value = "user", required = false) UserEntity user,
                                @RequestParam(value = "aid", required = false) int aid) {
        ModelAndView modelAndView = new ModelAndView("bbs/read");
        ArticleReadVo article = this.bbsService.readArticle(aid,user);
        modelAndView.addObject("article", article);
        if (article != null) {
            BoardEntity board = this.bbsService.getBoard(article.getBoardId());
            modelAndView.addObject("board", board);
            modelAndView.addObject("liked", article.isArticleLiked());
        }
        return modelAndView;
    }

//    Mr.m
//    게시글 수정하기 구현

        @RequestMapping(value = "modify", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify(@SessionAttribute(value = "user", required = false) UserEntity user, @RequestParam(value = "aid") int articleIndex) {
        ModelAndView modelAndView;
        ArticleReadVo article = this.bbsService.getModifyArticles(articleIndex, user);
        if (user == null) {
            //↑로그인 확인 조건
            modelAndView = new ModelAndView("redirect:/member/login");}
        else if (!user.getEmail().equals(article.getUserEmail())) {
            modelAndView = new ModelAndView("redirect:./read?aid=" + articleIndex);
        } else {
            modelAndView = new ModelAndView("bbs/modify");
            modelAndView.addObject("article", article);
            BoardEntity board = this.bbsService.getBoard(article.getBoardId());
            modelAndView.addObject("board", board);
            }
        return modelAndView;
    }

//    게시물 수정하기(patch)
    @RequestMapping(value = "modify", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchModify(@SessionAttribute(value = "user", required = false) UserEntity user,
                              @RequestParam(value = "aid") int articleIndex, ArticleEntity articleEntity) {
        Enum<?> result;//= this.bbsService.RegisterBoard(user,bid,articleEntity);
        JSONObject responseObject = new JSONObject();
        if (user == null) {
            result = ModifyArticleResult.NOT_ALLOWED;
        } else if (articleIndex == 0) {
            result = ModifyArticleResult.NO_SUCH_ARTICLE;
        }else {
            result = this.bbsService.modifyArticle(articleIndex, user, articleEntity);

            if (result == CommonResult.SUCCESS) {
                responseObject.put("aid", articleIndex);
            }
        }
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    //Mr.m
    @RequestMapping(value = "list",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList() {
        ModelAndView modelAndView = new ModelAndView("bbs/list");
        return modelAndView;
    }

    @GetMapping(value = "thumbnail")
    public ResponseEntity<byte[]> getReviewImage(@RequestParam(value = "index")int index) {
        ResponseEntity<byte[]> responseEntity;
        ArticleEntity articleThumbnail = this.bbsService.getThumbnail(index);
        if( articleThumbnail == null ) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(articleThumbnail.getThumbnailType()));
            headers.setContentLength(articleThumbnail.getThumbnail().length);
            responseEntity = new ResponseEntity<>(articleThumbnail.getThumbnail(), headers, HttpStatus.OK);
        }
        System.out.println("check thumbnail" + responseEntity);
        return responseEntity;
    }

    // 이미지 업로드(ckEditor에 등록하는 매핑)
    @GetMapping(value = "image")
    public ResponseEntity<byte[]> getImage(@RequestParam(value = "index") int index) {
        ResponseEntity<byte[]> responseEntity;
        ImageEntity image = this.bbsService.getImage(index);
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

    // 이미지 다운로드(ckEditor에 등록하는 매핑)
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

        Enum<?> result = this.bbsService.addImage(image);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("url", "http://localhost:8080/shop/image?index=" + image.getIndex());
        }
        return responseObject.toString();
    }

    @RequestMapping(value = "article-liked", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postArticleLike(@SessionAttribute(value = "user", required = false) UserEntity user, ArticleLikeEntity articleLikeEntity) {
        Enum<?> result;
        if (user == null) {
            result = WriteResult.NOT_ALLOWED;
        } else if (articleLikeEntity.getArticleIndex() == 0) {
            result = WriteResult.NO_SUCH_BOARD;
        } else {
            result = this.bbsService.likedArticle(articleLikeEntity, user);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

}

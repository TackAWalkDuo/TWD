package dev.test.take_a_walk_duo.controllers;

import dev.test.take_a_walk_duo.entities.bbs.ArticleEntity;
import dev.test.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.test.take_a_walk_duo.entities.bbs.CommentEntity;
import dev.test.take_a_walk_duo.entities.member.UserEntity;
import dev.test.take_a_walk_duo.enums.CommonResult;
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
        ArticleReadVo article = this.bbsService.readArticle(aid, user);
        modelAndView.addObject("article", article);
        System.out.println(article.getIndex());
        if (article != null) {
            BoardEntity board = this.bbsService.getBoard(article.getBoardId());
            modelAndView.addObject("board", board);
            modelAndView.addObject("liked", article.isArticleLiked());
        }
        return modelAndView;
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
    public ResponseEntity<byte[]> getReviewImage(@RequestParam(value = "index") int index) {
        ResponseEntity<byte[]> responseEntity;
        ArticleEntity articleThumbnail = this.bbsService.getThumbnail(index);
        if (articleThumbnail == null) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(articleThumbnail.getThumbnailType()));
            headers.setContentLength(articleThumbnail.getThumbnail().length);
            responseEntity = new ResponseEntity<>(articleThumbnail.getThumbnail(), headers, HttpStatus.OK);
        }
        System.out.println("check thumbnail" + responseEntity);
        return responseEntity;
    }

    @PostMapping(value = "comment", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postComment(@SessionAttribute(value = "user", required = false) UserEntity user,
                             @RequestParam(value = "images", required = false) MultipartFile[] images,
                             CommentEntity comment) throws IOException {
        JSONObject responseObject = new JSONObject();

        System.out.println("comment check");
        Enum<?> result =  this.bbsService.addComment(user, comment, images);

        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
    }


}

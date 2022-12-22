package dev.twd.take_a_walk_duo.controllers;

import dev.twd.take_a_walk_duo.entities.bbs.*;
import dev.twd.take_a_walk_duo.entities.member.UserEntity;
import dev.twd.take_a_walk_duo.enums.CommonResult;
import dev.twd.take_a_walk_duo.enums.bbs.ModifyArticleResult;
import dev.twd.take_a_walk_duo.enums.bbs.WriteResult;
import dev.twd.take_a_walk_duo.models.PagingModel;
import dev.twd.take_a_walk_duo.services.BbsService;
import dev.twd.take_a_walk_duo.vos.bbs.ArticleReadVo;
import dev.twd.take_a_walk_duo.vos.bbs.CommentVo;
import org.apache.ibatis.annotations.Param;
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
            BoardEntity[] boardList = this.bbsService.chartBoardId(board.getBoardId());
            BoardEntity[] boardTitle = this.bbsService.getBoardEntities();
            modelAndView.addObject("board", board);
            modelAndView.addObject("liked", article.isArticleLiked());
            modelAndView.addObject("boardList", boardList);
            modelAndView.addObject("boardTitles", boardTitle);


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
            modelAndView = new ModelAndView("redirect:/member/login");
        } else if (!user.getEmail().equals(article.getUserEmail())) {
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
        } else {
            result = this.bbsService.modifyArticle(articleIndex, user, articleEntity);

            if (result == CommonResult.SUCCESS) {
                responseObject.put("aid", articleIndex);
            }
        }
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    //    Mr.m
    //게시글 삭제
    @RequestMapping(value = "read", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteArticle(@SessionAttribute(value = "user", required = false) UserEntity user, @RequestParam(value = "aid", required = false) int aid) {
        ArticleEntity article = new ArticleEntity();
        article.setIndex(aid);
        Enum<?> result = this.bbsService.deleteArticle(article, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("bid", article.getBoardId());
        }
        return responseObject.toString();
    }


    //Mr.m
    //리스트 구현
    @RequestMapping(value = "list",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getList(@RequestParam(value = "bid", required = false) String bid,
                                @RequestParam(value = "page", required = false, defaultValue = "1")
                                Integer page,
                                @RequestParam(value = "criterion", required = false)
                                String criterion,
                                @RequestParam(value = "keyword", required = false)
                                String keyword) {
        page = Math.max(1, page);
        ModelAndView modelAndView = new ModelAndView("bbs/list");
        BoardEntity board = bid == null ? null : this.bbsService.getBoard(bid);
        modelAndView.addObject("board", board);
        if (board != null) {
            int totalCount = this.bbsService.getArticleCount(board, criterion, keyword);
            PagingModel paging = new PagingModel(totalCount, page);
            modelAndView.addObject("paging", paging);

            ArticleReadVo[] articles = this.bbsService.getArticles(board,paging);
            modelAndView.addObject("articles", articles);

            System.out.println(articles.length);
            BoardEntity[] boardList = this.bbsService.chartBoardId(board.getBoardId() == null ? board.getId() : board.getBoardId());
            BoardEntity[] boardTitle = this.bbsService.getBoardEntities();
            modelAndView.addObject("boardList", boardList);
            modelAndView.addObject("boardTitles", boardTitle);

            System.out.printf("bbs 이동 가능한 최소 페이지 : %d\n", paging.minPage);
            System.out.printf("bbs 이동 가능한 최대 페이지 : %d\n", paging.maxPage);
            System.out.printf("bbs 표시 시작 페이지 : %d\n", paging.startPage);
            System.out.printf("bbs 표시 끝 페이지 : %d\n", paging.endPage);
            System.out.println("가지고 있는 borad의 갯수" + boardList.length);
            System.out.println("test :::" + bid);

        }
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
        Enum<?> result = this.bbsService.addComment(user, comment, images);

        responseObject.put("result", result.name().toLowerCase());

        return responseObject.toString();
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
    public String postArticleLike(@SessionAttribute(value = "user", required = false) UserEntity user,
                                  ArticleLikeEntity articleLikeEntity) {
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


    //댓글 불러오기
    @GetMapping(value = "comment", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public CommentVo[] getComment(@Param(value = "index") int index){
        return this.bbsService.getComment(index);
    }

    //댓글 이미지 불러오기 // shop 내부가 조금 달라서 새로 만듬
    @GetMapping(value = "commentImage")
    public ResponseEntity<byte[]> getCommentImage(@RequestParam(value = "index")int index) {
        ResponseEntity<byte[]> responseEntity;
        CommentImageEntity commentImage = this.bbsService.getCommentImage(index);
        if( commentImage == null ) {
            responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.valueOf(commentImage.getType()));
            headers.setContentLength(commentImage.getData().length);
            responseEntity = new ResponseEntity<>(commentImage.getData(), headers, HttpStatus.OK);
        }

        return responseEntity;
    }

}

package dev.test.take_a_walk_duo.interceptors;

import dev.test.take_a_walk_duo.entities.bbs.BoardEntity;
import dev.test.take_a_walk_duo.services.ShopService;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommonInterceptor implements HandlerInterceptor {
    @Resource
    private ShopService shopService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        BoardEntity[] boards = this.shopService.getBoards();
        request.setAttribute("boards",boards);
        System.out.println("게시판" + boards.length + "개 있음");
        return true;
    }
}

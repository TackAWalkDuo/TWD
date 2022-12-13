package dev.test.take_a_walk_duo.models;

public class PagingModel {
    public final int countPerPage;  // 페이지당 표시할 게시글의 개수
    public final int totalCount;    // 전체 게시글 개수    DB가 가짐
    public final int requestPage;   // 요청한 페이지 번호  사용자가 요청.
    public final int maxPage;       // 이동 가능한 최대 페이지   ( t / c ) + ( ( t % c ) == 0 ? 0 : 1 )
    public final int minPage;       // 이동 가능한 최소 페이지   1
    public final int startPage;     // 표시 시작 페이지   ( ( p - 1 ) / 10 )*10 + 1
    public final int endPage;       // 표시 끝 페이지    startPage + 9 > x ? x : (startPage + 9)

    public PagingModel(int totalCount, int requestPage) {
        this(10, totalCount, requestPage);
    }

    public PagingModel(int countPerPage, int totalCount, int requestPage) {
        this.countPerPage = countPerPage;
        this.totalCount = totalCount;
        this.requestPage = requestPage;     //기본값은 1
        this.minPage = 1;
        this.maxPage = (totalCount / countPerPage) + ((totalCount % countPerPage) == 0 ? 0 : 1);
        this.startPage = ((requestPage - 1) / countPerPage) * countPerPage + 1;
        this.endPage = Math.min((this.startPage + countPerPage - 1), this.maxPage);
    }
}

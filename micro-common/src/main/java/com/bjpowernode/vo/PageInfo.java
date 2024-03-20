package com.bjpowernode.vo;

import com.bjpowernode.consts.YLBConsts;

//保存分页的数据
public class PageInfo {
    private Integer pageNo;   //页号
    private Integer pageSize; //每页大小
    private Integer totalPage;//总页数
    private Integer totalRecord;//总记录数量

    public PageInfo() {
        this.pageNo  = 1;
        this.pageSize = YLBConsts.DEFAULT_PAGE_SIZE;
        this.totalPage = 1;
        this.totalRecord = 0;
    }

    public PageInfo(Integer pageNo, Integer pageSize, Integer totalRecord) {
        this.pageNo = pageNo;
        this.pageSize = pageSize;
        this.totalRecord = totalRecord;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPage() {
        //计算总页数
        pageSize = (pageSize == null || pageSize < 1 ) ?
                    YLBConsts.DEFAULT_PAGE_SIZE: pageSize;
        if( totalRecord % pageSize == 0 ){
            totalPage = totalRecord / pageSize;
        } else {
            totalPage  = totalRecord/ pageSize + 1;
        }
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public Integer getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }
}

package cn.xzb.mybatis.gen.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageReq implements Serializable {
    private static final long serialVersionUID = -2312312431388542L;
    private Integer pageNumber = 1;
    private Integer pageSize;

    public PageReq() {
    }

    public Integer getPageNumber() {
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        return pageNumber;
    }

    public Integer getPageSize() {
        if (pageSize <= 0) {
            pageSize = 10;
        }
        return pageSize;
    }

    public String covertLimit() {
        return "limit " +(getPageNumber() - 1) * getPageSize() + "," + getPageSize();
    }
}

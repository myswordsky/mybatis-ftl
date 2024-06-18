package cn.xzb.mybatis.gen.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageRsp<T> implements Serializable {
    private static final long serialVersionUID = -2312312431388543L;
    private Integer code = 0;
    private String msg = "";
    /**
     * 数据总数
     */
    public long total;
    public long pageNumber;
    public List<T> data;

    public PageRsp() {
    }

    public PageRsp(long total, List<T> data) {
        this.total = total;
        this.data = data;
    }

    public PageRsp(long total, long pageNumber, List<T> data) {
        this.total = total;
        this.pageNumber = pageNumber;
        this.data = data;
    }

    public PageRsp(Integer retCode, String retMsg) {
        this.code = retCode;
        this.msg = retMsg;
    }
}
